package de.dmarcini.submatix.pclogger.comm;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.intel.bluetooth.RemoteDeviceHelper;

import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.SPX42Config;
import de.dmarcini.submatix.pclogger.utils.SPX42GasList;

/**
 * 
 * Klasse zur direkten Kommunikation mit dem BT-Device
 * 
 * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 08.01.2012
 */
//@formatter:off
public class BTCommunication implements IBTCommunication
{
  static final UUID                       UUID_SERIAL_DEVICE = new UUID( 0x1101 );
  public static final int             CONFIG_WRITE_KDO_COUNT = 4;
  public static final int              CONFIG_READ_KDO_COUNT = 7;
  // übersichtlicher machen mit Objekt für alle
  private DeviceCache                            deviceCache = null;
  static Logger                                       LOGGER = null;
  private LogDerbyDatabaseUtil                        dbUtil = null;
  private boolean                                        log = false;
  private volatile boolean                       isConnected = false;
  private ActionListener                           aListener = null;
  private static volatile boolean          discoverInProcess = false;
  StreamConnection                                      conn = null;
  private WriterRunnable                              writer = null;
  private ReaderRunnable                              reader = null;
  private AliveTask                                    alive = null;
  private RemoteDevice                       connectedDevice = null;
  private String                      connectedVirtualDevice = null;
  private String                                  deviceName = null;
  private SerialPort                              serialPort = null; 
  private int                                  writeWatchDog = -1;
  @SuppressWarnings( "unused" )
  private static final Pattern              fieldPattern0x09 = Pattern.compile( ProjectConst.LOGSELECTOR );
  private static final Pattern                fieldPatternDp = Pattern.compile(  ":" );
  //@formatter:on
  /**
   * 
   * Lokale Klasse, Thread zum Schreiben auf Device
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 11.01.2012
   */
  //@formatter:off
  public class WriterRunnable implements Runnable
  {
    private OutputStream                           outStream = null;
    private boolean                                  running = false;
    private final ArrayList<String>                writeList = new ArrayList<String>();
    //@formatter:on
    public WriterRunnable( OutputStream outStr )
    {
      outStream = outStr;
    }

    @Override
    public void run()
    {
      // solange was auszugeben ist, mach ich das...
      if( log ) LOGGER.fine( "start writer thread..." );
      writeList.clear();
      this.running = true;
      while( this.running == true )
      {
        // syncronisiete Methode aufrufen, damit wait und notify machbar sind
        wtSync();
      }
      if( log ) LOGGER.fine( "stop writer thread..." );
      isConnected = false;
      try
      {
        outStream.close();
      }
      catch( IOException ex )
      {}
      if( aListener != null )
      {
        ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
        aListener.actionPerformed( ev );
      }
    }

    /**
     * 
     * Zeilenweise an SPX schreiben, wenn nix zu tun ist schlafen legen
     * 
     * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     *         Stand: 11.01.2012
     */
    private synchronized void wtSync()
    {
      if( writeList.isEmpty() || outStream == null )
      {
        // ist die Liste leer, mach nix, einfach relaxen
        try
        {
          Thread.yield();
          wait( 20 );
        }
        catch( InterruptedException ex )
        {}
      }
      else
      {
        // ich gebe einen Eintrag aus...
        try
        {
          // Watchdog für Schreiben aktivieren
          writeWatchDog = ProjectConst.WATCHDOG_FOR_WRITEOPS;
          // also den String Eintrag in den Outstream...
          outStream.write( ( writeList.remove( 0 ) ).getBytes() );
          // kommt das an, den Watchog wieder AUS
          writeWatchDog = -1;
          // zwischen den Kommandos etwas warten, der SPX braucht etwas bis er wieder zuhört...
          // das gibt dem Swing-Thread etwas Gelegenheit zum Zeichnen oder irgendwas anderem
          for( int factor = 0; factor < 5; factor++ )
          {
            Thread.yield();
            Thread.sleep( 80 );
          }
        }
        catch( IndexOutOfBoundsException ex )
        {
          isConnected = false;
          if( aListener != null )
          {
            ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ev );
          }
          running = false;
          return;
        }
        catch( IOException ex )
        {
          isConnected = false;
          if( aListener != null )
          {
            ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ev );
          }
          running = false;
          return;
        }
        catch( InterruptedException ex )
        {}
      }
    }

    /**
     * 
     * Terminieren lassen geht hier
     * 
     * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     *         Stand: 11.01.2012
     */
    public synchronized void doTeminate()
    {
      notifyAll();
      this.running = false;
    }

    /**
     * 
     * Schreibe zum SPX Daten
     * 
     * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     *         Stand: 11.01.2012
     * @param msg
     */
    public synchronized void writeToDevice( String msg )
    {
      writeList.add( msg );
      notifyAll();
    }
  }

  /**
   * 
   * Lokale Klasse zum lesen vom SPX42
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 11.01.2012
   */
  //@formatter:off
  public class ReaderRunnable implements Runnable
  {
    private final byte[]                                    buffer = new byte[1024];
    private InputStream                             inStream = null;
    private boolean                                  running = false;
    private volatile boolean                    isLogentryMode = false;
    private final StringBuffer                          mInStrBuffer = new StringBuffer( 1024 );  
    //@formatter:on
    public ReaderRunnable( InputStream inStr )
    {
      inStream = inStr;
    }

    @Override
    public void run()
    {
      int bytes = 0;
      String readMessage = "";
      int start, end, lstart, lend;
      boolean logCmd, normalCmd;
      // solange was auszugeben ist, mach ich das...
      if( log ) LOGGER.fine( "start reader thread..." );
      this.running = true;
      while( this.running == true )
      {
        try
        {
          bytes = inStream.read( buffer );
          if( bytes == -1 )
          {
            // Verbindung beendet/verloren
            isConnected = false;
            if( log ) LOGGER.severe( "reader connection lost..." );
            if( aListener != null )
            {
              ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
              aListener.actionPerformed( ev );
            }
            running = false;
            return;
          }
          readMessage = new String( buffer, 0, bytes );
        }
        catch( IOException ex )
        {
          // IO Fehler
          isConnected = false;
          if( log ) LOGGER.severe( "reader connection lost (" + ex.getLocalizedMessage() + ")..." );
          if( aListener != null )
          {
            ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ev );
          }
          running = false;
          return;
        }
        //
        // was mach ich jetzt mit dem empfangenen Zeuch?
        //
        // Puffer auffüllen, wenn noch Platz ist
        if( ( mInStrBuffer.capacity() + readMessage.length() ) > ProjectConst.MAXINBUFFER )
        {
          isConnected = false;
          if( log ) LOGGER.severe( "INPUT BUFFER OVERFLOW!" );
          if( aListener != null )
          {
            ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ev );
          }
          running = false;
          return;
        }
        mInStrBuffer.append( readMessage );
        readMessage = mInStrBuffer.toString();
        //
        // die Nachricht abarbeitern, solange komplette MSG da sind
        //
        start = mInStrBuffer.indexOf( ProjectConst.STX );
        end = mInStrBuffer.indexOf( ProjectConst.ETX );
        if( isLogentryMode )
        {
          // Logeinträge werden abgearbeitet
          lstart = mInStrBuffer.indexOf( ProjectConst.FILLER );
          lend = mInStrBuffer.indexOf( ProjectConst.FILLER, start + ProjectConst.FILLER.length() );
        }
        else
        {
          // der "normalmode"
          lstart = -1;
          lend = -1;
        }
        // solange etwas gefunden wird
        while( ( ( start > -1 ) && ( end > start ) ) || ( ( lstart > -1 ) && ( lend > lstart ) ) )
        {
          if( ( start > -1 ) && ( end > start ) )
            normalCmd = true;
          else
            normalCmd = false;
          if( ( lstart > -1 ) && ( lend > lstart ) )
            logCmd = true;
          else
            logCmd = false;
          // womit anfangen?
          // sind beide zu finden?
          if( normalCmd == true && logCmd == true )
          {
            // entscheidung, wer zuerst
            if( start < lstart )
            {
              execNormalCmd( start, end, mInStrBuffer );
            }
            else
            {
              execLogentryCmd( lstart, lend, mInStrBuffer );
            }
          }
          else
          {
            // nein, nur ein Typ. Welcher?
            if( normalCmd == true )
            {
              execNormalCmd( start, end, mInStrBuffer );
            }
            else if( logCmd == true )
            {
              execLogentryCmd( lstart, lend, mInStrBuffer );
            }
          }
          start = mInStrBuffer.indexOf( ProjectConst.STX );
          end = mInStrBuffer.indexOf( ProjectConst.ETX );
          if( isLogentryMode )
          {
            lstart = mInStrBuffer.indexOf( ProjectConst.FILLER );
            lend = mInStrBuffer.indexOf( ProjectConst.FILLER, start + ProjectConst.FILLER.length() );
          }
          else
          {
            lstart = -1;
            lend = -1;
          }
        }
      }
      if( inStream != null )
      {
        try
        {
          inStream.close();
        }
        catch( IOException ex )
        {}
      }
      isConnected = false;
      if( log ) LOGGER.fine( "stop reader thread..." );
    }

    /**
     * 
     * Bearbeite einen Logeintrag
     * 
     * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     *         Stand: 11.01.2012
     * @param start
     * @param end
     * @param mInStrBuffer
     */
    private void execLogentryCmd( int start, int end, StringBuffer mInStrBuffer )
    {
      String readMessage;
      int lstart, lend;
      if( log ) LOGGER.fine( "execLogentryCmd..." );
      lstart = mInStrBuffer.indexOf( ProjectConst.STX );
      lend = mInStrBuffer.indexOf( ProjectConst.ETX );
      if( lstart > -1 && lend > lstart )
      {
        // ups, hier ist ein "normales" Kommando verpackt
        if( log ) LOGGER.fine( "oops, normalCmd found.... change to execNormalCmd..." );
        isLogentryMode = false;
        execNormalCmd( lstart, lend, mInStrBuffer );
        return;
      }
      // muss der anfang weg?
      if( start > 0 )
      {
        // das davor kann dann weg...
        mInStrBuffer = mInStrBuffer.delete( 0, start );
        readMessage = mInStrBuffer.toString();
        // Indizies korrigieren
        end = mInStrBuffer.indexOf( ProjectConst.FILLER, start + ProjectConst.FILLER.length() );
        start = 0;
      }
      // lese das Ding ohne den Schmandzius der Füller
      readMessage = mInStrBuffer.substring( ProjectConst.FILLER.length(), end );
      // lösche das schon mal raus...
      mInStrBuffer = mInStrBuffer.delete( 0, end );
      readMessage = readMessage.replaceAll( ProjectConst.FILLERCHAR, "" );
      // Sende an aufrufende Activity
      if( log ) LOGGER.finer( "Logline Recived <" + readMessage.substring( 10 ).replaceAll( "\t", " " ) + "...>" );
      if( aListener != null )
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LOGENTRY_LINE, readMessage, System.currentTimeMillis() / 100, 0 );
        aListener.actionPerformed( ex );
      }
    }

    /**
     * 
     * Bearbeite eine Meldung vom SPX42
     * 
     * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     *         Stand: 24.12.2011
     * @param start
     * @param end
     * @param mInStrBuffer
     */
    private void execNormalCmd( int start, int end, StringBuffer mInStrBuffer )
    {
      String readMessage;
      String[] fields;
      int command;
      if( log ) LOGGER.fine( "execNormalCmd..." );
      // muss der anfang weg?
      if( start > 0 )
      {
        // das davor kann dann weg...
        mInStrBuffer = mInStrBuffer.delete( 0, start );
        readMessage = mInStrBuffer.toString();
        // Indizies korrigieren
        end = mInStrBuffer.indexOf( ProjectConst.ETX );
        start = 0;
      }
      // jetz beginnt der String immer bei 0, lese das Ding
      readMessage = mInStrBuffer.substring( 1, end );
      // lösche das schon mal raus...
      mInStrBuffer = mInStrBuffer.delete( 0, end + 1 );
      if( log ) LOGGER.fine( "normal Message Recived <" + readMessage + ">" );
      // Trenne die Parameter voneinander, fields[0] ist dann das Kommando
      fields = fieldPatternDp.split( readMessage );
      //
      //
      //
      if( 0 == readMessage.indexOf( ProjectConst.IS_END_LOGLISTENTRY ) )
      {
        // Logbucheinträge fertig gelesen
        if( aListener != null )
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_DIRENTRY_END, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( log ) LOGGER.fine( "Logentry list final recived." );
        return;
      }
      //
      //
      //
      fields[0] = fields[0].replaceFirst( "~", "" );
      try
      {
        command = Integer.parseInt( fields[0], 16 );
      }
      catch( NumberFormatException ex )
      {
        LOGGER.severe( "Convert String to Int (" + ex.getLocalizedMessage() + ")" );
        return;
      }
      // bekomme heraus, welcher Art die ankommende Message ist
      switch ( command )
      {
        case ProjectConst.SPX_MANUFACTURERS:
          // Sende Nachricht Gerätename empfangen!
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_MANUFACTURER_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "SPX Devicename recived! <" + fields[1] + ">" );
          break;
        case ProjectConst.SPX_ALIVE:
          // Ackuspannung übertragen
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SPXALIVE, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "SPX is Alive, Acku value recived." );
          break;
        case ProjectConst.SPX_APPLICATION_ID:
          // Sende Nachricht Firmwareversion empfangen!
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_FWVERSION_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "Application ID (Firmware Version)  recived! <" + fields[1] + ">" );
          break;
        case ProjectConst.SPX_SERIAL_NUMBER:
          // Sende Nachricht Seriennummer empfangen!
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SERIAL_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "Serial Number recived! <" + fields[1] + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_DEKO:
          // Quittung für Setze DECO
          if( log ) LOGGER.fine( "Response for set deco <" + readMessage + "> was recived." );
          //
          // TODO: readDecoPrefs();
          //
          break;
        case ProjectConst.SPX_SET_SETUP_SETPOINT:
          // Quittung für Setzen der Auto-Setpointeinstelungen
          if( log ) LOGGER.fine( "SPX_SET_SETUP_SETPOINT Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_DISPLAYSETTINGS:
          // Quittung für Setzen der Displayeinstellungen
          if( log ) LOGGER.fine( "SET_SETUP_DISPLAYSETTINGS Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_UNITS:
          // Quittung für Setzen der Einheiten
          if( log ) LOGGER.fine( "SPX_SET_SETUP_UNITS Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_INDIVIDUAL:
          // Quittung für Individualeinstellungen
          if( log ) LOGGER.fine( "SPX_SET_SETUP_INDIVIDUAL Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_GET_SETUP_DEKO:
          // Kommando DEC liefert zurück:
          // ~34:LL:HH:D:Y:C
          // LL=GF-Low, HH=GF-High,
          // D=Deepstops (0/1)
          // Y=Dynamische Gradienten (0/1)
          // C=Last Decostop (0=3 Meter/1=6 Meter)
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_DECO_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "DECO_EINST recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_GET_SETUP_SETPOINT:
          // Kommando GET_SETUP_SETPOINT liefert
          // ~35:A:P
          // A = Setpoint bei (0,1,2,3) = (0,5,15,20)
          // P = Partialdruck (0..4) 1.0 .. 1.4
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SETPOINT_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "GET_SETUP_SETPOINT recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_GET_SETUP_DISPLAYSETTINGS:
          // Kommando GET_SETUP_DISPLAYSETTINGS liefert
          // ~36:D:A
          // D= 0->10&, 1->50%, 2->100%
          // A= 0->Landscape 1->180Grad
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_DISPLAY_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "GET_SETUP_DISPLAYSETTINGS recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_GET_SETUP_UNITS:
          // Kommando GET_SETUP_UNITS
          // ~37:UD:UL:UW
          // UD= Fahrenheit/Celsius => immer 0 in der aktuellen Firmware 2.6.7.7_U
          // UL= 0=metrisch 1=imperial
          // UW= 0->Salzwasser 1->Süßwasser
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_UNITS_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "GET_SETUP_UNITS recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_GET_SETUP_INDIVIDUAL:
          // Kommando GET_SETUP_INDIVIDUAL liefert
          // ~38:SE:PS:SC:SN:LI
          // SE: Sensors 0->ON 1->OFF
          // PS: PSCRMODE 0->OFF 1->ON
          // SC: SensorCount
          // SN: Sound 0->OFF 1->ON
          // LI: Loginterval 0->10sec 1->30Sec 2->60 Sec
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_INDIVID_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "GET_SETUP_INDIVIDUAL recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_GET_SETUP_GASLIST:
          // Kommando GET_SETUP_GASLIST
          // ~39:NR:ST:HE:BA:AA:CG
          // NR: Numer des Gases
          // ST Stickstoff in Prozent (hex)
          // HELIUM
          // Bailout
          // AA Diluent 1 oder 2 oder keins
          // CG curent Gas
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_GAS_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "GET_SETUP_GASLIST recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_GASLIST:
          // Besaetigung fuer Gas setzen bekommen
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_GAS_WRITTEN, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "SET_SETUP_GASLIST recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_GET_LOG_INDEX:
          // Ein Logbuch Verzeichniseintrag gefunden
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_DIRENTRY_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "SPX_GET_LOG_INDEX recived!" );
          break;
        case ProjectConst.SPX_GET_LOG_NUMBER_SE:
          if( 0 == fields[1].indexOf( "1" ) )
          {
            // Übertragung Logfile gestartet
            if( aListener != null )
            {
              ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LOGENTRY_START, new String( fields[2] ), System.currentTimeMillis() / 100, 0 );
              aListener.actionPerformed( ex );
            }
            if( log ) LOGGER.fine( "Logfile transmission started..." );
            isLogentryMode = true;
          }
          else if( 0 == fields[1].indexOf( "0" ) )
          {
            {
              // Übertragung beendet
              if( aListener != null )
              {
                ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LOGENTRY_STOP, new String( fields[2] ), System.currentTimeMillis() / 100, 0 );
                aListener.actionPerformed( ex );
              }
              if( log ) LOGGER.fine( "Logfile transmission finished." );
              isLogentryMode = false;
            }
          }
          break;
        case ProjectConst.SPX_GET_DEVICE_OFF:
          // SPX meldet, er geht aus dem Sync-Mode
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SYCSTAT_OFF, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "SPX42 switch syncmode OFF! Connection will failure!" );
          break;
        case ProjectConst.SPX_LICENSE_STATE:
          // LICENSE_STATE gefunden
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LICENSE_STATE_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.fine( "LICENSE_STATE recived <" + readMessage + ">" );
          break;
        default:
          if( log ) LOGGER.log( Level.WARNING, "unknown Messagetype recived <" + readMessage + ">" );
      }
    }

    /**
     * 
     * Terminieren lassen geht hier
     * 
     * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     *         Stand: 11.01.2012
     */
    public synchronized void doTeminate()
    {
      this.running = false;
      // einfach den Stream wegputzen!
      try
      {
        inStream.close();
      }
      catch( IOException ex )
      {}
    }
  }

  /**
   * 
   * Task soll einfach von Zeit zu Zeit gucken, ob alles noch läuft
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 06.05.2012
   */
  private class AliveTask implements Runnable
  {
    private boolean running = false;

    @Override
    public void run()
    {
      this.running = true;
      int counter = 0;
      while( this.running )
      {
        try
        {
          // 1 Sekunden schlafen gehen
          Thread.sleep( 1000 );
          counter++;
        }
        catch( InterruptedException ex )
        {
          LOGGER.severe( "Exception while ticker sleeps: <" + ex.getMessage() + ">" );
        }
        // aller 90 sekunden
        if( isConnected && counter > 90 )
        {
          askForSPXAlive();
          counter = 0;
        }
        // den Watchdog testen
        if( isConnected && writeWatchDog > -1 )
        {
          if( writeWatchDog == 0 )
          {
            if( aListener != null )
            {
              ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_COMMTIMEOUT, "timeout" );
              aListener.actionPerformed( ev );
            }
          }
          // runterzählen, bei -1 ist eh schluss
          writeWatchDog--;
        }
        // regelmaessig bescheid geben
        if( aListener != null && ( counter % 10 == 0 ) )
        {
          ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_TICK, "tick" );
          aListener.actionPerformed( ev );
        }
      }
    }

    /**
     * 
     * Soll es möglich machen, den Task abzubrechen
     * 
     * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     *         Stand: 06.05.2012
     */
    @SuppressWarnings( "unused" )
    public synchronized void doTeminate()
    {
      this.running = false;
    }
  }

  @SuppressWarnings( "unused" )
  private BTCommunication()
  {};

  /**
   * 
   * Konstruktor der Kommunikation
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 11.01.2012
   * @param lg
   * @param dbUtil
   */
  public BTCommunication( final Logger lg, final LogDerbyDatabaseUtil dbUtil )
  {
    LOGGER = lg;
    LOGGER.fine( "bluethooth communication object create..." );
    this.dbUtil = dbUtil;
    deviceCache = new DeviceCache( dbUtil );
    if( lg == null )
      log = false;
    else
      log = true;
    // besorg mir die Gerätenamen aus der Datenbank
    if( !this.dbUtil.isOpenDB() )
    {
      try
      {
        this.dbUtil.createConnection();
      }
      catch( SQLException ex )
      {
        LOGGER.severe( "error while construct bluethooth object: <" + ex.getLocalizedMessage() + ">" );
        ex.printStackTrace();
      }
      catch( ClassNotFoundException ex )
      {
        LOGGER.severe( "error while construct bluethooth object: <" + ex.getLocalizedMessage() + ">" );
        ex.printStackTrace();
      }
    }
    //
    // Den Cache aus der Datenbank erstbefüllen
    deviceCache.initFromDb();
    //
    // jetzt noch Tick starten und dabei ALIVE abfragen...
    //
    LOGGER.fine( "bluethooth communication object create...start ticker..." );
    alive = new AliveTask();
    Thread al = new Thread( alive );
    al.setName( "bt_alive_task" );
    al.setPriority( Thread.NORM_PRIORITY - 2 );
    al.start();
    LOGGER.fine( "bluethooth communication object create...OK" );
  }

  @Override
  public boolean isConnected()
  {
    return this.isConnected;
  }

  @Override
  public boolean discoverDevices( final boolean cached )
  {
    if( discoverInProcess )
    {
      if( log ) LOGGER.log( Level.WARNING, "Discovering always in process..." );
      return false;
    }
    discoverInProcess = true;
    Thread sb = new Thread() {
      @Override
      public void run()
      {
        // Warteschleife mit Nachrichten
        while( discoverInProcess )
        {
          try
          {
            Thread.sleep( 50 );
          }
          catch( InterruptedException ex )
          {}
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_BTWAITFOR, null );
            aListener.actionPerformed( ex );
          }
        }
      }
    };
    Thread th = new Thread() {
      @Override
      public void run()
      {
        // Discovery starten
        if( log ) LOGGER.fine( "start discover für Bluethooth devices..." );
        try
        {
          if( cached )
          {
            if( log ) LOGGER.fine( "read cached..." );
            if( !RemoteDeviceDiscovery.readCached() )
            {
              if( log ) LOGGER.fine( "read cached failed, try normal discovering..." );
              RemoteDeviceDiscovery.doDiscover();
            }
          }
          else
          {
            if( log ) LOGGER.fine( "none read cached, try normal discovering..." );
            RemoteDeviceDiscovery.doDiscover();
          }
        }
        catch( IOException ex )
        {
          if( aListener != null )
          {
            ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_BTRECOVERERR, ex.getLocalizedMessage() );
            aListener.actionPerformed( ev );
          }
          discoverInProcess = false;
          return;
        }
        catch( InterruptedException ex )
        {
          if( aListener != null )
          {
            ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_BTRECOVERERR, ex.getLocalizedMessage() );
            aListener.actionPerformed( ev );
          }
          discoverInProcess = false;
          return;
        }
        // suche nach Serial devices
        UUID serviceUUID = UUID_SERIAL_DEVICE;
        // Event Objet
        final Object serviceSearchCompletedEvent = new Object();
        // Inline Klasse
        DiscoveryListener listener = new DiscoveryListener() {
          @Override
          public void deviceDiscovered( RemoteDevice btDevice, DeviceClass cod )
          {
            LOGGER.fine( "device discovered: <" + btDevice.getBluetoothAddress() + ">" );
          }

          @Override
          public void inquiryCompleted( int discType )
          {}

          @Override
          public void servicesDiscovered( int transID, ServiceRecord[] servRecord )
          {
            if( log ) LOGGER.fine( "Services Discovered..." );
            //
            // Alle gefundenen Devices durchgehen
            //
            for( int i = 0; i < servRecord.length; i++ )
            {
              String url = servRecord[i].getConnectionURL( ServiceRecord.AUTHENTICATE_NOENCRYPT, false );
              if( url == null )
              {
                // ich will nur mit gültiger Verbindung
                continue;
              }
              DataElement serviceName = servRecord[i].getAttributeValue( 0x0100 );
              String devName;
              devName = servRecord[i].getHostDevice().getBluetoothAddress();
              if( serviceName != null )
              {
                String sName = ( ( String )serviceName.getValue() );
                sName = sName.replaceAll( "[^A-Z0-9a-z]{2,}", "" );
                if( log ) LOGGER.fine( "Device <" + devName + "> Service <" + sName + "> found at <" + url + ">" );
                // URL speichern
                if( deviceCache.isDeviceThere( devName ) )
                {
                  if( log ) LOGGER.fine( "Device was in cache. update data..." );
                  // gerät ist schon im Cache
                  deviceCache.setConnectionString( devName, url );
                  deviceCache.setRemoteDevice( devName, servRecord[i].getHostDevice() );
                }
                else
                {
                  if( log ) LOGGER.fine( "Device was not in cache. insert data..." );
                  // Gerät ist noch nicht im Cache
                  deviceCache.addDevice( devName, url, "0000", "A-" + devName, servRecord[i].getHostDevice(), "nativ" );
                }
                if( aListener != null )
                {
                  ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_BTMESSAGE, devName );
                  aListener.actionPerformed( ev );
                }
              }
              else
              {
                if( log ) LOGGER.fine( "Service found, URL: <" + url + "> IGNORE!" );
              }
            }
          }

          @Override
          public void serviceSearchCompleted( int transID, int respCode )
          {
            if( log ) LOGGER.fine( "service search completed!" );
            synchronized( serviceSearchCompletedEvent )
            {
              serviceSearchCompletedEvent.notifyAll();
            }
          }
        };
        // nach welchen UUID soll gesucht werden? (natürlich nur Serial Comm)
        UUID[] searchUuidSet = new UUID[]
        { serviceUUID };
        // nach welchem Attr-Ids soll gesucht werden?
        int[] attrIDs = new int[]
        { 0x0100 };// Service name
        // jetzt alle gefundenen Devices nach dem Gesuchten untersuchen
        for( Enumeration<RemoteDevice> en = RemoteDeviceDiscovery.devicesDiscovered.elements(); en.hasMoreElements(); )
        {
          RemoteDevice btDevice = en.nextElement();
          synchronized( serviceSearchCompletedEvent )
          {
            try
            {
              if( log ) LOGGER.fine( "search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName( false ) );
              LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices( attrIDs, searchUuidSet, btDevice, listener );
              serviceSearchCompletedEvent.wait();
            }
            catch( BluetoothStateException ex )
            {
              if( aListener != null )
              {
                ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_BTRECOVERERR, ex.getLocalizedMessage() );
                aListener.actionPerformed( ev );
              }
              discoverInProcess = false;
              return;
            }
            catch( IOException ex )
            {
              if( aListener != null )
              {
                ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_BTRECOVERERR, ex.getLocalizedMessage() );
                aListener.actionPerformed( ev );
              }
              discoverInProcess = false;
              return;
            }
            catch( InterruptedException ex )
            {
              if( aListener != null )
              {
                ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_BTRECOVERERR, ex.getLocalizedMessage() );
                aListener.actionPerformed( ev );
              }
              discoverInProcess = false;
              return;
            }
          }
        }
        if( aListener != null )
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_BTRECOVEROK, new String( "discover_ok" ) );
          aListener.actionPerformed( ex );
          ex = new ActionEvent( this, ProjectConst.MESSAGE_BTMESSAGE, "OK" );
          aListener.actionPerformed( ex );
        }
        if( log ) LOGGER.fine( "Bluethooth Discovering OK" );
        discoverInProcess = false;
      }
    };
    th.setName( "btDiscoverThread" );
    sb.setName( "btStatusThread" );
    sb.start();
    th.start();
    return true;
  }

  @Override
  public void addActionListener( ActionListener al )
  {
    aListener = al;
  }

  @Override
  public Vector<String[]> getNameArray()
  {
    Vector<String[]> alData = null;
    //
    // den Vector erzeugen
    //
    alData = new Vector<String[]>();
    if( deviceCache.isEmpty() )
    {
      return( alData );
    }
    //
    // Vector füllen
    //
    Iterator<String> it = deviceCache.getDevicesIterator();
    while( it.hasNext() )
    {
      // den Vector mit den Stringarrays für die Anzeige in der Combobox füllen
      String dev = it.next();
      String[] e = new String[4];
      if( deviceCache.getType( dev ) == null ) continue;
      if( deviceCache.getType( dev ).equals( "virtual" ) ) continue;
      e[0] = dev;
      e[1] = deviceCache.getAlias( dev );
      if( deviceCache.getConnectionString( dev ) != null )
      {
        e[2] = "*";
      }
      else
      {
        e[2] = "";
      }
      e[3] = deviceCache.getPin( dev );
      alData.add( e );
    }
    return( alData );
  }

  @Override
  public void removeActionListener()
  {
    aListener = null;
  }

  @Override
  public void connectDevice( String devName ) throws Exception
  {
    String url = null;
    String devicePin = null;
    //
    LOGGER.fine( "try to connect device <" + devName + ">..." );
    this.deviceName = devName;
    this.connectedDevice = null;
    this.connectedVirtualDevice = null;
    this.serialPort = null;
    // suche die URL für die Verbindung
    if( deviceCache.isDeviceThere( devName ) )
    {
      LOGGER.fine( "device name found in list. can try to connect device..." );
      // Ich hab den Gerätenamen gefunden, kann verbinden
      url = deviceCache.getConnectionString( devName );
      if( url == null )
      {
        // keine url, kann man nix verbinden!
        if( aListener != null )
        {
          ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
          aListener.actionPerformed( ex1 );
        }
        return;
      }
      this.connectedDevice = deviceCache.getRemoteDevice( devName );
      if( aListener != null )
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTING, null );
        aListener.actionPerformed( ex );
      }
    }
    else
    {
      // das geht nicht! Kann nicht herausfinden, mit wem ich verbinden soll!
      LOGGER.severe( "device <" + devName + "> is not in list. give up!" );
      this.deviceName = null;
      this.connectedDevice = null;
      if( aListener != null )
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_BTNODEVCONN, null );
        aListener.actionPerformed( ex );
      }
      return;
    }
    // So, das Verbinden halt...
    try
    {
      if( log ) LOGGER.fine( "Connect to Device <" + devName + ">" );
      // Verbinden....
      if( this.connectedDevice.isAuthenticated() )
      {
        // Wenn device authentifiziert ist, alles OK
        if( log ) LOGGER.log( Level.INFO, "Device is Authentificated" );
        conn = ( StreamConnection )Connector.open( url );
      }
      else
      {
        // Device ist nicht authentifiziert.
        // Versuch das mal zu machen
        if( log ) LOGGER.log( Level.INFO, "try autentificating..." );
        // versuche die PIN zu bekommen
        devicePin = deviceCache.getPin( devName );
        if( devicePin != null )
        {
          // ich habe eine PIN in meinem Speicher
          if( log ) LOGGER.log( Level.INFO, "try autentificating whith pin <" + devicePin + ">" );
          RemoteDeviceHelper.authenticate( this.connectedDevice, devicePin );
          conn = ( StreamConnection )Connector.open( url );
          // Die Verbindung nun in die Alias Datenbank eintragen, wenn nicht schon vorhanden
          if( !deviceCache.isDeviceInDb( devName ) || !deviceCache.isDeviceSyncWithDb( devName ) )
          {
            if( !deviceCache.isDeviceInDb( devName ) )
            {
              dbUtil.addAliasForNameConn( devName, deviceCache.getAlias( devName ), "nativ" );
            }
            else
            {
              dbUtil.updateDeviceAliasConn( devName, deviceCache.getAlias( devName ) );
            }
            dbUtil.setPinForDeviceConn( devName, devicePin );
            deviceCache.setDeviceInDb( devName, true );
            deviceCache.setDeviceSyncWithDb( devName, true );
          }
        }
        else
        {
          // Benutzer anquengeln
          // und PIN eigeben lassen
          if( log ) LOGGER.log( Level.INFO, "Device is NOT Authentificated" );
          this.connectedDevice = null;
          this.deviceName = null;
          if( aListener != null )
          {
            ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ex1 );
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_BTAUTHREQEST, devName );
            aListener.actionPerformed( ex );
          }
          // isConnected = false;
          return;
        }
      }
      //
      // Wollen wir mal unser Glück versuchen?
      //
      // Eingabe erzeugen
      InputStream din = new DataInputStream( conn.openInputStream() );
      reader = new ReaderRunnable( din );
      Thread rt = new Thread( reader );
      rt.setName( "bt_reader_thread" );
      rt.setPriority( Thread.NORM_PRIORITY - 1 );
      rt.start();
      //
      // Ausgabe erzeugen
      OutputStream dout = new DataOutputStream( conn.openOutputStream() );// Get the output stream
      writer = new WriterRunnable( dout );
      Thread wt = new Thread( writer );
      wt.setName( "bt_writer_thread" );
      wt.setPriority( Thread.NORM_PRIORITY - 2 );
      wt.start();
      isConnected = true;
      if( aListener != null )
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTED, null );
        aListener.actionPerformed( ex );
      }
    }
    catch( BluetoothConnectionException ex )
    {
      isConnected = false;
      this.connectedDevice = null;
      if( log ) LOGGER.severe( "BTConnectionException <" + ex.getLocalizedMessage() + ">" );
      if( aListener != null )
      {
        ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
        aListener.actionPerformed( ex1 );
      }
      int status = ex.getStatus();
      switch ( status )
      {
        case BluetoothConnectionException.UNKNOWN_PSM:
          if( log ) LOGGER.severe( "BTConnectionException <the connection to the server failed because no service for the given PSM was registered>" );
          break;
        case BluetoothConnectionException.SECURITY_BLOCK:
          if( log )
            LOGGER.severe( "BTConnectionException <the connection failed because the security settings on the local device or the remote device were incompatible with the request>" );
          break;
        case BluetoothConnectionException.NO_RESOURCES:
          if( log ) LOGGER.severe( "BTConnectionException <the connection failed due to a lack of resources either on the local device or on the remote device>" );
          break;
        case BluetoothConnectionException.FAILED_NOINFO:
          if( log ) LOGGER.severe( "BTConnectionException <the connection to the server failed due to unknown reasons.>" );
          break;
        case BluetoothConnectionException.TIMEOUT:
          if( log ) LOGGER.severe( "BTConnectionException <the connection to the server failed due to a timeout>" );
          break;
        case BluetoothConnectionException.UNACCEPTABLE_PARAMS:
          if( log )
            LOGGER.severe( "BTConnectionException <the connection failed because the configuration parameters provided were not acceptable to either the remote device or the local device>" );
          break;
        default:
          if( log ) LOGGER.severe( "BTConnectionException <unknown>" );
      }
    }
    catch( Exception ex )
    {
      isConnected = false;
      this.connectedDevice = null;
      if( log ) LOGGER.severe( "Exception <" + ex.getLocalizedMessage() + ">" );
      if( aListener != null )
      {
        ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
        aListener.actionPerformed( ex1 );
      }
    }
    finally
    {
      // was immer ausgeführt werden muss
    }
  }

  @Override
  public void connectVirtDevice( String devName ) throws PortInUseException, Exception
  {
    //
    LOGGER.fine( "try to connect virtual device <" + devName + ">..." );
    this.deviceName = devName;
    this.connectedDevice = null;
    this.connectedVirtualDevice = null;
    this.serialPort = null;
    isConnected = false;
    // Port ID holen
    CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier( devName );
    // versuch den Port zu öffnen, wirft PortInUseException
    CommPort commPort = portIdentifier.open( this.getClass().getName(), 10000 );
    if( commPort instanceof SerialPort )
    {
      LOGGER.fine( "device <" + devName + "> is serial port..." );
      this.serialPort = ( SerialPort )commPort;
      this.connectedVirtualDevice = "virtual";
      // serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
      // Eingabe erzeugen
      InputStream din = serialPort.getInputStream();
      reader = new ReaderRunnable( din );
      Thread rt = new Thread( reader );
      rt.setName( "bt_reader_thread" );
      rt.setPriority( Thread.NORM_PRIORITY - 1 );
      rt.start();
      //
      // Ausgabe erzeugen
      OutputStream dout = serialPort.getOutputStream();
      writer = new WriterRunnable( dout );
      Thread wt = new Thread( writer );
      wt.setName( "bt_writer_thread" );
      wt.setPriority( Thread.NORM_PRIORITY - 2 );
      wt.start();
      isConnected = true;
      if( aListener != null )
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTED, null );
        aListener.actionPerformed( ex );
      }
    }
    else
    {
      LOGGER.severe( "device <" + devName + "> is NOT serial port ABORT!" );
      if( aListener != null )
      {
        ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
        aListener.actionPerformed( ex1 );
      }
    }
  }

  @Override
  public void disconnectDevice()
  {
    if( writer != null )
    {
      writer.doTeminate();
    }
    if( reader != null )
    {
      reader.doTeminate();
    }
    try
    {
      Thread.sleep( 500 );
    }
    catch( InterruptedException ex )
    {}
    if( conn != null )
    {
      try
      {
        conn.close();
      }
      catch( IOException ex )
      {
        ex.printStackTrace();
      }
    }
    writer = null;
    reader = null;
    conn = null;
    if( this.serialPort != null )
    {
      this.serialPort.close();
    }
    this.serialPort = null;
    this.connectedVirtualDevice = null;
    this.connectedDevice = null;
  }

  @Override
  public synchronized void writeToDevice( String msg )
  {
    if( isConnected && ( writer != null ) )
    {
      writer.writeToDevice( msg );
    }
  }

  @Override
  public void writeSPXMsgToDevice( String msg )
  {
    if( isConnected && ( writer != null ) )
    {
      writer.writeToDevice( ProjectConst.STX + msg + ProjectConst.ETX );
    }
  }

  @Override
  public void askForSerialNumber()
  {
    this.writeToDevice( String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_SERIAL_NUMBER, ProjectConst.ETX ) );
  }

  @Override
  public void askForSPXAlive()
  {
    this.writeToDevice( String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_ALIVE, ProjectConst.ETX ) );
  }

  @Override
  public void readConfigFromSPX42()
  {
    String kdoString;
    kdoString = String.format( "%s~%x~%x~%x~%x~%x~%x~%x%s", ProjectConst.STX, ProjectConst.SPX_GET_SETUP_DEKO, ProjectConst.SPX_GET_SETUP_SETPOINT,
            ProjectConst.SPX_GET_SETUP_DISPLAYSETTINGS, ProjectConst.SPX_GET_SETUP_UNITS, ProjectConst.SPX_GET_SETUP_INDIVIDUAL, ProjectConst.SPX_LICENSE_STATE,
            ProjectConst.SPX_ALIVE, ProjectConst.ETX );
    if( log )
    {
      LOGGER.fine( "readConfigFromSPX()...send <" + kdoString + ">" );
    }
    this.writeToDevice( kdoString );
  }

  @Override
  public void askForDeviceName()
  {
    this.writeToDevice( String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_MANUFACTURERS, ProjectConst.ETX ) );
  }

  @Override
  public void askForFirmwareVersion()
  {
    this.writeToDevice( String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_APPLICATION_ID, ProjectConst.ETX ) );
  }

  @Override
  public void askForLicenseFromSPX()
  {
    this.writeToDevice( String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_LICENSE_STATE, ProjectConst.ETX ) );
  }

  @Override
  public String getDeviceInfos()
  {
    // Mach aus den HashMaps einen String zum Wiedereinlesen
    return null;
  }

  @Override
  public void putDeviceInfos( String infos ) throws Exception
  {}

  @Override
  public void setPinForDevice( String devName, String pin )
  {
    deviceCache.setPin( devName, pin );
    //
    // ist das Device in der Datenbank aufgezeichnet?
    //
    if( deviceCache.isDeviceInDb( devName ) )
    {
      // Ja, in der Datenbank vorhanden, setze noch die PIN
      dbUtil.setPinForDeviceConn( devName, pin );
    }
  }

  @Override
  public String getPinForDevice( String dev )
  {
    if( deviceCache.isDeviceThere( dev ) )
    {
      return( deviceCache.getPin( dev ) );
    }
    return( "0000" );
  }

  @Override
  public void writeConfigToSPX( final SPX42Config config )
  {
    Thread configWriteThread = null;
    //
    if( !config.isInitialized() )
    {
      if( log ) LOGGER.severe( "config was not initialized! CANCEL!" );
      return;
    }
    if( ProjectConst.FIRMWARE_2_6_7_7V.equals( config.getFirmwareVersion() ) || ProjectConst.FIRMWARE_2_7V.equals( config.getFirmwareVersion() ) )
    {
      // Führe als eigenen Thread aus, damit die Swing-Oberfläche
      // Gelegenheit bekommt, sich zu zeichnen
      configWriteThread = new Thread() {
        ActionEvent ae;

        @Override
        public void run()
        {
          String command = null;
          int firmware = 0;
          if( ProjectConst.FIRMWARE_2_6_7_7V.equals( config.getFirmwareVersion() ) )
          {
            firmware = ProjectConst.FW_2_6_7_7V;
          }
          else if( ProjectConst.FIRMWARE_2_7V.equals( config.getFirmwareVersion() ) )
          {
            firmware = ProjectConst.FW_2_7V;
          }
          else
          {
            if( log ) LOGGER.severe( "Firmware not supportet! CANCEL!" );
            return;
          }
          //
          // Kommando SPX_SET_SETUP_DEKO
          // Deco-Einstellungen setzen
          if( log ) LOGGER.log( Level.INFO, "write deco propertys" );
          switch ( firmware )
          {
            case ProjectConst.FW_2_6_7_7V:
              // ~29:GH:GL:LS:DY:DS
              // GH = Gradient HIGH
              // GL = Gradient LOW
              // LS = Last Stop 0=>6m 1=>3m
              // DY = Dynamische gradienten 0->off 1->on
              // DS = Deepstops 0=> enabled, 1=>disabled
              command = String.format( "~%x:%x:%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_DEKO, config.getDecoGfHigh(), config.getDecoGfLow(), config.getLastStop(),
                      config.getDynGradientsEnable(), config.getDeepStopEnable() );
              break;
            default:
            case ProjectConst.FW_2_7V:
              // Kommando SPX_SET_SETUP_DEKO
              // ~29:GL:GH:DS:DY:LS
              // GL=GF-Low, GH=GF-High,
              // DS=Deepstops (0/1)
              // DY=Dynamische Gradienten (0/1)
              // LS=Last Decostop (0=3 Meter/1=6 Meter)
              command = String.format( "~%x:%x:%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_DEKO, config.getDecoGfLow(), config.getDecoGfHigh(), config.getDeepStopEnable(),
                      config.getDynGradientsEnable(), config.getLastStop() );
              break;
          }
          if( log ) LOGGER.fine( "Send <" + command + ">" );
          writeSPXMsgToDevice( command );
          // gib Bescheid
          if( aListener != null )
          {
            ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
            aListener.actionPerformed( ae );
          }
          //
          // Kommando SPX_SET_SETUP_DISPLAYSETTINGS
          // ~31:D:A
          // D= 0->10&, 1->50%, 2->100%
          // A= 0->Landscape 1->180Grad
          // Display setzen
          if( log ) LOGGER.log( Level.INFO, "write display propertys" );
          command = String.format( "~%x:%x:%x", ProjectConst.SPX_SET_SETUP_DISPLAYSETTINGS, config.getDisplayBrightness(), config.getDisplayOrientation() );
          if( log ) LOGGER.fine( "Send <" + command + ">" );
          writeSPXMsgToDevice( command );
          // gib Bescheid
          if( aListener != null )
          {
            ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
            aListener.actionPerformed( ae );
          }
          //
          // Kommando SPX_SET_SETUP_UNITS
          // ~32:UD:UL:UW
          // UD= Fahrenheit/Celsius => immer 0 in der aktuellen Firmware 2.6.7.7_U
          // UL= 0=>metrisch 1=>imperial
          // UW= 0->Salzwasser 1->Süßwasser
          if( log ) LOGGER.log( Level.INFO, "write units propertys" );
          command = String.format( "~%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_UNITS, config.getUnitTemperature(), config.getUnitDepth(), config.getUnitSalnity() );
          if( log ) LOGGER.fine( "Send <" + command + ">" );
          writeSPXMsgToDevice( command );
          // gib Bescheid
          if( aListener != null )
          {
            ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
            aListener.actionPerformed( ae );
          }
          if( log ) LOGGER.log( Level.INFO, "write setpoint propertys" );
          switch ( firmware )
          {
            case ProjectConst.FW_2_6_7_7V:
              //
              // Kommando SPX_SET_SETUP_SETPOINT
              // ~30:P:A
              // P = Partialdruck (0..4) 1.0 .. 1.4
              // A = Setpoint bei (0,1,2,3,4) = (0,5,15,20,25)
              command = String.format( "~%x:%x:%x", ProjectConst.SPX_SET_SETUP_SETPOINT, config.getMaxSetpoint(), config.getAutoSetpoint() );
              break;
            default:
            case ProjectConst.FW_2_7V:
              // ~30:A:P
              // A = Setpoint bei (0,1,2,3,4) = (0,5,15,20,25)
              // P = Partialdruck (0..4) 1.0 .. 1.4
              command = String.format( "~%x:%x:%x", ProjectConst.SPX_SET_SETUP_SETPOINT, config.getAutoSetpoint(), config.getMaxSetpoint() );
              break;
          }
          if( log ) LOGGER.fine( "Send <" + command + ">" );
          writeSPXMsgToDevice( command );
          // gib Bescheid
          if( aListener != null )
          {
            ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
            aListener.actionPerformed( ae );
          }
          //
          if( config.getCustomEnabled() == 1 )
          {
            // Kommando SPX_SET_SETUP_INDIVIDUAL
            // ~33:SM:PS:SC:AC:LT
            // SM = 0-> Sensoren ON, 1-> No Sensor
            // PS = PSCR Mode 0->off; 1->ON (sollte eigentlich immer off (0 ) sein)
            // SC = SensorsCount 0->1 Sensor, 1->2 sensoren, 2->3 Sensoren
            // AC = acoustic 0->off, 1->on
            // LT = Logbook Timeinterval 0->10s, 1->30s, 2->60s
            if( log ) LOGGER.log( Level.INFO, "write individual propertys" );
            command = String.format( "~%x:%x:%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_INDIVIDUAL, config.getSensorsOn(), config.getPscrModeOn(), config.getSensorsCount(),
                    config.getSoundOn(), config.getLogInterval() );
            if( log ) LOGGER.fine( "Send <" + command + ">" );
            writeSPXMsgToDevice( command );
            // gib Bescheid
            if( aListener != null )
            {
              ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
              aListener.actionPerformed( ae );
            }
          }
          // gib Bescheid vorgang zuende
          if( log ) LOGGER.log( Level.INFO, "write config endet." );
          if( aListener != null )
          {
            ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_END, "config_write" );
            aListener.actionPerformed( ae );
          }
        }
      };
      configWriteThread.setName( "write_config_to_spx" );
      configWriteThread.start();
    }
    else
    {
      if( log ) LOGGER.severe( "write for this firmware version not confirmed! CANCEL!" );
    }
  }

  @Override
  public void readGaslistFromSPX42()
  {
    String kdoString;
    kdoString = String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_GET_SETUP_GASLIST, ProjectConst.ETX );
    if( log )
    {
      LOGGER.fine( "readGaslistFromSPX42()...send <" + kdoString + ">" );
    }
    this.writeToDevice( kdoString );
  }

  @Override
  public void writeGaslistToSPX42( final SPX42GasList gList, final String spxVersion )
  {
    Thread gasListWriteThread = null;
    //
    if( !gList.isInitialized() )
    {
      if( log ) LOGGER.severe( "config was not initialized! CANCEL!" );
      return;
    }
    if( ProjectConst.FIRMWARE_2_6_7_7V.equals( spxVersion ) || ProjectConst.FIRMWARE_2_7V.equals( spxVersion ) )
    {
      // Schreibe für die leicht Fehlerhafte Version
      // Führe als eigenen Thread aus, damit die Swing-Oberfläche
      // Gelegenheit bekommt, sich zu zeichnen
      gasListWriteThread = new Thread() {
        ActionEvent ae;

        @Override
        public void run()
        {
          String command;
          int gasCount = gList.getGasCount();
          int gasNr;
          int diluent;
          int firmware = 0;
          if( ProjectConst.FIRMWARE_2_6_7_7V.equals( spxVersion ) )
          {
            firmware = ProjectConst.FW_2_6_7_7V;
          }
          else if( ProjectConst.FIRMWARE_2_7V.equals( spxVersion ) )
          {
            firmware = ProjectConst.FW_2_7V;
          }
          else
          {
            if( log ) LOGGER.severe( "Firmware not supportet! CANCEL!" );
            return;
          }
          //
          // Alle Gase des Computers durchexerzieren
          //
          for( gasNr = 0; gasNr < gasCount; gasNr++ )
          {
            if( log ) LOGGER.log( Level.INFO, String.format( "write gas number %d to SPX...", gasNr ) );
            switch ( firmware )
            {
              case ProjectConst.FW_2_6_7_7V:
                // Kommando SPX_SET_SETUP_GASLIST
                // ~40:NR:HE:N2:BO:DI:CU
                // NR -> Gas Nummer
                // HE -> Heliumanteil
                // N2 -> Stickstoffanteil
                // BO -> Bailoutgas? (3?)
                // DI -> Diluent ( 0, 1 oder 2 )
                // CU Current Gas (0 oder 1)
                if( gList.getDiulent1() == gasNr )
                {
                  diluent = 1;
                }
                else if( gList.getDiluent2() == gasNr )
                {
                  diluent = 2;
                }
                else
                {
                  diluent = 0;
                }
                command = String.format( "~%x:%x:%x:%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_GASLIST, gasNr, gList.getHEFromGas( gasNr ), gList.getN2FromGas( gasNr ),
                        gList.getBailout( gasNr ), diluent, gList.getCurrGas( gasNr ) );
                break;
              default:
              case ProjectConst.FW_2_7V:
                // Kommando SPX_SET_SETUP_GASLIST
                // ~40:NR:N2:HE:BO:DI:CU
                // NR: Nummer des Gases 0..7
                // N2: Sticksoff in %
                // HE: Heluim in %
                // BO: Bailout (Werte 0,1 und 3 gefunden, 0 kein BO, 3 BO Wert 1 unbekannt?)
                // DI: Diluent 1 oder 2
                // CU: Current Gas
                if( gList.getDiulent1() == gasNr )
                {
                  diluent = 1;
                }
                else if( gList.getDiluent2() == gasNr )
                {
                  diluent = 2;
                }
                else
                {
                  diluent = 0;
                }
                command = String.format( "~%x:%x:%x:%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_GASLIST, gasNr, gList.getN2FromGas( gasNr ), gList.getHEFromGas( gasNr ),
                        gList.getBailout( gasNr ), diluent, gList.getCurrGas( gasNr ) );
                break;
            }
            //
            if( log ) LOGGER.fine( "Send <" + command + ">" );
            writeSPXMsgToDevice( command );
            // gib Bescheid
            if( aListener != null )
            {
              ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
              aListener.actionPerformed( ae );
            }
          }
          // gib Bescheid Vorgang zuende
          if( log ) LOGGER.log( Level.INFO, "write gaslist success." );
          if( aListener != null )
          {
            ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_END, null );
            aListener.actionPerformed( ae );
          }
        }
      };
      gasListWriteThread.setName( "write_gaslist_to_spx" );
      gasListWriteThread.start();
    }
    else
    {
      if( log ) LOGGER.severe( "write for this firmware version not confirmed! CANCEL!" );
    }
  }

  @Override
  public void readLogDirectoryFromSPX()
  {
    String kdoString;
    kdoString = String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_GET_LOG_INDEX, ProjectConst.ETX );
    if( log )
    {
      LOGGER.fine( "readLogDirectoryFromSPX()...send <" + kdoString + ">" );
    }
    this.writeToDevice( kdoString );
  }

  @Override
  public String getConnectedDevice()
  {
    if( isConnected )
    {
      // ist es ein virtuelles Gerät?
      if( this.connectedVirtualDevice != null )
      {
        // ja, dann guck ich mal weiter
        return( this.connectedVirtualDevice );
      }
      // ich muss mal sehen, ob da ein Eintrag ist oder besorgt werden kann
      if( this.connectedDevice == null )
      {
        LOGGER.log( Level.WARNING, "connected Device is NULL!" );
        // Kein Eintrag da...
        if( deviceName == null )
        {
          // Kein Gerätename, mit dem ich verbunden bin
          LOGGER.log( Level.WARNING, "deviceName is NULL!" );
          return( null );
        }
        this.connectedDevice = deviceCache.getRemoteDevice( deviceName );
      }
      // ich muss mal sehen, ob da ein Eintrag nun da ist
      if( this.connectedDevice != null )
      {
        return( this.connectedDevice.getBluetoothAddress() );
      }
      LOGGER.log( Level.WARNING, "connected Device is again NULL!" );
    }
    return null;
  }

  @Override
  public void readLogDetailFromSPX( int logNumber )
  {
    if( isConnected )
    {
      String kdoString = String.format( "%s~%x:%x%s", ProjectConst.STX, ProjectConst.SPX_GET_LOG_NUMBER, logNumber, ProjectConst.ETX );
      if( log )
      {
        LOGGER.fine( "readLogDetailFromSPX()...send <" + kdoString + ">" );
      }
      this.writeToDevice( kdoString );
    }
  }

  /**
   * 
   * Den Cache von der DB ergänzen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   */
  public void refreshNameArray()
  {
    deviceCache.refreshFromDb();
  }

  @Override
  public void setNameForVirtualDevice( String serialNumber )
  {
    this.connectedVirtualDevice = serialNumber;
  }
}
