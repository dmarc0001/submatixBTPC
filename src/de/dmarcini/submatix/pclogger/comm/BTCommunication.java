package de.dmarcini.submatix.pclogger.comm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
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
import de.dmarcini.submatix.pclogger.utils.DatabaseUtil;
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
  private final HashMap<String,String>           connectHash = new HashMap<String,String>();
  private final HashMap<String,RemoteDevice>      deviceHash = new HashMap<String,RemoteDevice>();
  private final HashMap<String,String>         devicePinHash = new HashMap<String,String>();
  private final HashMap<String,String>       deviceAliasHash = new HashMap<String,String>();
  static Logger                                       LOGGER = null;
  private DatabaseUtil                                dbUtil = null;
  private boolean                                        log = false;
  private boolean                                isConnected = false;
  private ActionListener                           aListener = null;
  private static volatile boolean          discoverInProcess = false;
  StreamConnection                                      conn = null;
  private WriterRunnable                              writer = null;
  private ReaderRunnable                              reader = null;
  private RemoteDevice                       connectedDevice = null;
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
      if( log ) LOGGER.log( Level.FINE, "start writer thread..." );
      writeList.clear();
      this.running = true;
      while( this.running == true )
      {
        // syncronisiete Methode aufrufen, damit wait und notify machbar sind
        wtSync();
      }
      if( log ) LOGGER.log( Level.FINE, "stop writer thread..." );
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
          // also den String Eintrag in den Outstream...
          outStream.write( ( writeList.remove( 0 ) ).getBytes() );
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
      if( log ) LOGGER.log( Level.FINE, "start reader thread..." );
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
            if( log ) LOGGER.log( Level.SEVERE, "reader connection lost..." );
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
          if( log ) LOGGER.log( Level.SEVERE, "reader connection lost (" + ex.getLocalizedMessage() + ")..." );
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
          if( log ) LOGGER.log( Level.SEVERE, "INPUT BUFFER OVERFLOW!" );
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
      if( log ) LOGGER.log( Level.FINE, "stop reader thread..." );
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
      if( log ) LOGGER.log( Level.FINE, "execLogentryCmd..." );
      lstart = mInStrBuffer.indexOf( ProjectConst.STX );
      lend = mInStrBuffer.indexOf( ProjectConst.ETX );
      if( lstart > -1 && lend > lstart )
      {
        // ups, hier ist ein "normales" Kommando verpackt
        if( log ) LOGGER.log( Level.FINE, "oops, normalCmd found.... change to execNormalCmd..." );
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
      if( log ) LOGGER.log( Level.FINE, "Logline Recived <" + readMessage + ">" );
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
      if( log ) LOGGER.log( Level.FINE, "execNormalCmd..." );
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
      if( log ) LOGGER.log( Level.FINE, "normal Message Recived <" + readMessage + ">" );
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
        if( log ) LOGGER.log( Level.FINE, "Logentry list final recived." );
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
        LOGGER.log( Level.SEVERE, "Convert String to Int (" + ex.getLocalizedMessage() + ")" );
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
          if( log ) LOGGER.log( Level.FINE, "SPX Devicename recived! <" + fields[1] + ">" );
          break;
        case ProjectConst.SPX_ALIVE:
          // Ackuspannung übertragen
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SPXALIVE, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.log( Level.FINE, "SPX is Alive, Acku value recived." );
          break;
        case ProjectConst.SPX_APPLICATION_ID:
          // Sende Nachricht Firmwareversion empfangen!
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_FWVERSION_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.log( Level.FINE, "Application ID (Firmware Version)  recived! <" + fields[1] + ">" );
          break;
        case ProjectConst.SPX_SERIAL_NUMBER:
          // Sende Nachricht Seriennummer empfangen!
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SERIAL_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.log( Level.FINE, "Serial Number recived! <" + fields[1] + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_DEKO:
          // Quittung für Setze DECO
          if( log ) LOGGER.log( Level.FINE, "Response for set deco <" + readMessage + "> was recived." );
          //
          // TODO: readDecoPrefs();
          //
          break;
        case ProjectConst.SPX_SET_SETUP_SETPOINT:
          // Quittung für Setzen der Auto-Setpointeinstelungen
          if( log ) LOGGER.log( Level.FINE, "SPX_SET_SETUP_SETPOINT Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_DISPLAYSETTINGS:
          // Quittung für Setzen der Displayeinstellungen
          if( log ) LOGGER.log( Level.FINE, "SET_SETUP_DISPLAYSETTINGS Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_UNITS:
          // Quittung für Setzen der Einheiten
          if( log ) LOGGER.log( Level.FINE, "SPX_SET_SETUP_UNITS Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_INDIVIDUAL:
          // Quittung für Individualeinstellungen
          if( log ) LOGGER.log( Level.FINE, "SPX_SET_SETUP_INDIVIDUAL Acknoweledge recived <" + readMessage + ">" );
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
          if( log ) LOGGER.log( Level.FINE, "DECO_EINST recived <" + readMessage + ">" );
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
          if( log ) LOGGER.log( Level.FINE, "GET_SETUP_SETPOINT recived <" + readMessage + ">" );
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
          if( log ) LOGGER.log( Level.FINE, "GET_SETUP_DISPLAYSETTINGS recived <" + readMessage + ">" );
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
          if( log ) LOGGER.log( Level.FINE, "GET_SETUP_UNITS recived <" + readMessage + ">" );
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
          if( log ) LOGGER.log( Level.FINE, "GET_SETUP_INDIVIDUAL recived <" + readMessage + ">" );
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
          if( log ) LOGGER.log( Level.FINE, "GET_SETUP_GASLIST recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_GASLIST:
          // Besaetigung fuer Gas setzen bekommen
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_GAS_WRITTEN, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.log( Level.FINE, "SET_SETUP_GASLIST recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_GET_LOG_INDEX:
          // Ein Logbuch Verzeichniseintrag gefunden
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_DIRENTRY_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.log( Level.FINE, "SPX_GET_LOG_INDEX recived!" );
          break;
        case ProjectConst.SPX_GET_LOG_NUMBER_SE:
          if( 0 == fields[1].indexOf( "1" ) )
          {
            // Übertragung Logfile gestartet
            if( aListener != null )
            {
              ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LOGENTRY_START, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
              aListener.actionPerformed( ex );
            }
            if( log ) LOGGER.log( Level.FINE, "Logfile transmission started..." );
            isLogentryMode = true;
          }
          else if( 0 == fields[1].indexOf( "0" ) )
          {
            {
              // Übertragung beendet
              if( aListener != null )
              {
                ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LOGENTRY_STOP, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
                aListener.actionPerformed( ex );
              }
              if( log ) LOGGER.log( Level.FINE, "Logfile transmission finished." );
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
          if( log ) LOGGER.log( Level.FINE, "SPX42 switch syncmode OFF! Connection will failure!" );
          break;
        case ProjectConst.SPX_LICENSE_STATE:
          // LICENSE_STATE gefunden
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LICENSE_STATE_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          if( log ) LOGGER.log( Level.FINE, "LICENSE_STATE recived <" + readMessage + ">" );
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
  public BTCommunication( Logger lg, final DatabaseUtil dbUtil )
  {
    LOGGER = lg;
    this.dbUtil = dbUtil;
    if( lg == null )
      log = false;
    else
      log = true;
    // besorg mir die Gerätenamen aus der Datenbank
    if( !this.dbUtil.isOpenDB() )
    {
      this.dbUtil.createConnection();
    }
    String[][] alData = dbUtil.getAliasData();
    // gibt es welche: eintragen
    if( alData != null )
    {
      for( String[] pair : alData )
      {
        deviceAliasHash.put( pair[0], pair[1] );
      }
    }
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
        if( log ) LOGGER.log( Level.FINE, "start discover für Bluethooth devices..." );
        try
        {
          if( cached )
          {
            if( log ) LOGGER.log( Level.FINE, "read cached..." );
            if( !RemoteDeviceDiscovery.readCached() )
            {
              if( log ) LOGGER.log( Level.FINE, "read cached failed, try normal discovering..." );
              RemoteDeviceDiscovery.doDiscover();
            }
          }
          else
          {
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
        // Serviceliste leeren
        connectHash.clear();
        deviceHash.clear();
        // suche nach Serial devices
        UUID serviceUUID = UUID_SERIAL_DEVICE;
        // Event Objetk TODO:
        final Object serviceSearchCompletedEvent = new Object();
        // Inline Klasse
        DiscoveryListener listener = new DiscoveryListener() {
          @Override
          public void deviceDiscovered( RemoteDevice btDevice, DeviceClass cod )
          {}

          @Override
          public void inquiryCompleted( int discType )
          {}

          @Override
          public void servicesDiscovered( int transID, ServiceRecord[] servRecord )
          {
            if( log ) LOGGER.log( Level.FINE, "Services Discovered..." );
            for( int i = 0; i < servRecord.length; i++ )
            {
              String url = servRecord[i].getConnectionURL( ServiceRecord.AUTHENTICATE_NOENCRYPT, false );
              if( url == null )
              {
                continue;
              }
              DataElement serviceName = servRecord[i].getAttributeValue( 0x0100 );
              String devName;
              try
              {
                devName = servRecord[i].getHostDevice().getFriendlyName( false );
              }
              catch( IOException ex )
              {
                devName = "unknown";
              }
              if( serviceName != null )
              {
                String sName = ( ( String )serviceName.getValue() );
                sName = sName.replaceAll( "[^A-Z0-9a-z]{2,}", "" );
                if( log ) LOGGER.log( Level.FINE, "Device <" + devName + "> Service <" + sName + "> found <" + url + ">" );
                // URL für die Anzeige speichern
                connectHash.put( devName, url );
                // das Gerät für die Anzeige speichern
                deviceHash.put( devName, servRecord[i].getHostDevice() );
              }
              else
              {
                if( log ) LOGGER.log( Level.FINE, "Service found, URL: <" + url + "> IGNORE!" );
              }
            }
          }

          @Override
          public void serviceSearchCompleted( int transID, int respCode )
          {
            if( log ) LOGGER.log( Level.FINE, "service search completed!" );
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
              if( log ) LOGGER.log( Level.FINE, "search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName( false ) );
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
        }
        if( log ) LOGGER.log( Level.FINE, "Bluethooth Discovering OK" );
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
  public String[] getNameArray( boolean alFromDb )
  {
    ArrayList<String> nList = new ArrayList<String>();
    String alias = null;
    //
    // wenn aliase verändert wurden, neu einlesen
    //
    if( alFromDb )
    {
      String[][] alData = dbUtil.getAliasData();
      deviceAliasHash.clear();
      // gibt es welche: eintragen
      if( alData != null )
      {
        for( String[] pair : alData )
        {
          deviceAliasHash.put( pair[0], pair[1] );
        }
      }
    }
    //
    // jetzt das eigentliche Geschäft
    //
    if( log ) LOGGER.log( Level.FINE, "make stringarray of Services..." );
    for( String dev : deviceHash.keySet() )
    {
      // Jetzt bringe (falls vorhanden) den Alias in Erfahrung...
      if( deviceAliasHash.containsKey( dev ) )
      {
        alias = deviceAliasHash.get( dev );
      }
      else
      {
        alias = dev;
      }
      // Aliase in die Liste
      nList.add( alias );
    }
    int size = nList.size();
    String[] list = new String[size];
    return( nList.toArray( list ) );
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
    String deviceName, deviceAlias = null, devicePin = null;
    //
    deviceName = devName;
    // suche die URL für die Verbindung
    // hab ich hier direkt den Devicenamen erwischt?
    if( connectHash.containsKey( deviceName ) && deviceHash.containsKey( deviceName ) )
    {
      // Ich hab den Gerätenamen gefunden, kann verbinden
      url = connectHash.get( deviceName );
      connectedDevice = deviceHash.get( deviceName );
      if( aListener != null )
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTING, null );
        aListener.actionPerformed( ex );
      }
    }
    else
    {
      // nicht gefunden. Es könnte ein Alias sein, versuche das.
      LOGGER.log( Level.FINE, "device name not found in list. try found as alias..." );
      deviceAlias = deviceName;
      deviceName = dbUtil.getNameForAlias( deviceAlias );
      if( deviceName != null && connectHash.containsKey( deviceName ) && deviceHash.containsKey( deviceName ) )
      {
        LOGGER.log( Level.FINE, "ok, it was an alias. device name is " + deviceName + "..." );
        // Der Alias stand für einen Gerätenamen, und für den gibt es eine url
        url = connectHash.get( deviceName );
        connectedDevice = deviceHash.get( deviceName );
        if( aListener != null )
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTING, null );
          aListener.actionPerformed( ex );
        }
      }
      else
      {
        // das geht nicht! Kann nicht herausfinden, mit wem ich verbinden soll!
        LOGGER.log( Level.SEVERE, "device" + deviceName + "is not in list and not an alias. give up!" );
        if( aListener != null )
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_BTNODEVCONN, null );
          aListener.actionPerformed( ex );
        }
        return;
      }
    }
    // So, das Verbinden halt...
    try
    {
      if( log ) LOGGER.log( Level.FINE, "Connect to Device <" + deviceName + ">" );
      // Verbinden....
      if( connectedDevice.isAuthenticated() )
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
        if( devicePinHash.containsKey( deviceName ) )
        {
          devicePin = devicePinHash.get( deviceName );
        }
        else
        {
          devicePin = dbUtil.getPinForDevice( deviceName );
        }
        if( devicePin != null )
        {
          // ich habe eine PIN in meinem Speicher
          if( log ) LOGGER.log( Level.INFO, "try autentificating whith pin <" + devicePin + ">" );
          RemoteDeviceHelper.authenticate( connectedDevice, devicePinHash.get( deviceName ) );
          conn = ( StreamConnection )Connector.open( url );
          // Die Verbindung nun in die Alias Datenbank eintragen, wenn nicht schon vorhanden
          deviceAlias = dbUtil.getAliasForName( deviceName );
          if( deviceAlias == null )
          {
            // gibts noch nicht => Eintragen
            dbUtil.addAliasForName( deviceName, "A-" + deviceName );
            // wenn ich schon dabei bin, die PIN mit eintragen
            dbUtil.setPinForDevice( deviceName, devicePin );
          }
        }
        else
        {
          // Benutzer anquengeln
          // und PIN eigeben lassen
          if( log ) LOGGER.log( Level.INFO, "Device is NOT Authentificated" );
          if( aListener != null )
          {
            ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ex1 );
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_BTAUTHREQEST, deviceName );
            aListener.actionPerformed( ex );
          }
          // isConnected = false;
          connectedDevice = null;
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
      connectedDevice = null;
      if( log ) LOGGER.log( Level.SEVERE, "BTConnectionException <" + ex.getLocalizedMessage() + ">" );
      if( aListener != null )
      {
        ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
        aListener.actionPerformed( ex1 );
      }
      int status = ex.getStatus();
      switch ( status )
      {
        case BluetoothConnectionException.UNKNOWN_PSM:
          if( log ) LOGGER.log( Level.SEVERE, "BTConnectionException <the connection to the server failed because no service for the given PSM was registered>" );
          break;
        case BluetoothConnectionException.SECURITY_BLOCK:
          if( log )
            LOGGER.log( Level.SEVERE,
                    "BTConnectionException <the connection failed because the security settings on the local device or the remote device were incompatible with the request>" );
          break;
        case BluetoothConnectionException.NO_RESOURCES:
          if( log ) LOGGER.log( Level.SEVERE, "BTConnectionException <the connection failed due to a lack of resources either on the local device or on the remote device>" );
          break;
        case BluetoothConnectionException.FAILED_NOINFO:
          if( log ) LOGGER.log( Level.SEVERE, "BTConnectionException <the connection to the server failed due to unknown reasons.>" );
          break;
        case BluetoothConnectionException.TIMEOUT:
          if( log ) LOGGER.log( Level.SEVERE, "BTConnectionException <the connection to the server failed due to a timeout>" );
          break;
        case BluetoothConnectionException.UNACCEPTABLE_PARAMS:
          if( log )
            LOGGER.log( Level.SEVERE,
                    "BTConnectionException <the connection failed because the configuration parameters provided were not acceptable to either the remote device or the local device>" );
          break;
        default:
          if( log ) LOGGER.log( Level.SEVERE, "BTConnectionException <unknown>" );
      }
    }
    catch( Exception ex )
    {
      isConnected = false;
      connectedDevice = null;
      if( log ) LOGGER.log( Level.SEVERE, "Exception <" + ex.getLocalizedMessage() + ">" );
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
      LOGGER.log( Level.FINE, "readConfigFromSPX()...send <" + kdoString + ">" );
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
    // private HashMap<String,String> connectHash = new HashMap<String,String>();
    // private HashMap<String,RemoteDevice> deviceHash = new HashMap<String,RemoteDevice>();
    // private HashMap<String,String> devicePinHash = new HashMap<String,String>();
    connectHash.toString();
    deviceHash.toString();
    devicePinHash.toString();
    return null;
  }

  @Override
  public void putDeviceInfos( String infos ) throws Exception
  {}

  @Override
  public void setPinForDevice( String dev, String pin )
  {
    devicePinHash.put( dev, pin );
    // wenn da noch was anderes stehen sollte...
    // ansonsten passiert nix
    dbUtil.setPinForDevice( dev, pin );
  }

  @Override
  public String getPinForDevice( String dev )
  {
    if( devicePinHash.containsKey( dev ) )
    {
      return( devicePinHash.get( dev ) );
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
      if( log ) LOGGER.log( Level.SEVERE, "config was not initialized! CANCEL!" );
      return;
    }
    if( ProjectConst.BUGGY_FIRMWARE_01.equals( config.getFirmwareVersion() ) )
    {
      // Schreibe für die leicht Fehlerhafte Version
      // Führe als eigenen Thread aus, damit die Swing-Oberfläche
      // Gelegenheit bekommt, sich zu zeichnen
      configWriteThread = new Thread() {
        ActionEvent ae;

        @Override
        public void run()
        {
          String command;
          //
          // Kommando SPX_SET_SETUP_DEKO
          // ~29:GH:GL:LS:DY:DS
          // GH = Gradient HIGH
          // GL = Gradient LOW
          // LS = Last Stop 0=>6m 1=>3m
          // DY = Dynamische gradienten 0->off 1->on
          // DS = Deepstops 0=> enabled, 1=>disabled
          // Deco-Einstellungen setzen
          if( log ) LOGGER.log( Level.INFO, "write deco propertys" );
          command = String.format( "~%x:%x:%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_DEKO, config.getDecoGfHigh(), config.getDecoGfLow(), config.getLastStop(),
                  config.getDynGradientsEnable(), config.getDeepStopEnable() );
          if( log ) LOGGER.log( Level.FINE, "Send <" + command + ">" );
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
          if( log ) LOGGER.log( Level.FINE, "Send <" + command + ">" );
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
          if( log ) LOGGER.log( Level.FINE, "Send <" + command + ">" );
          writeSPXMsgToDevice( command );
          // gib Bescheid
          if( aListener != null )
          {
            ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
            aListener.actionPerformed( ae );
          }
          //
          // Kommando SPX_SET_SETUP_SETPOINT
          // ~30:P:A
          // P = Partialdruck (0..4) 1.0 .. 1.4
          // A = Setpoint bei (0,1,2,3,4) = (0,5,15,20,25)
          if( log ) LOGGER.log( Level.INFO, "write setpoint propertys" );
          command = String.format( "~%x:%x:%x", ProjectConst.SPX_SET_SETUP_SETPOINT, config.getMaxSetpoint(), config.getAutoSetpoint() );
          if( log ) LOGGER.log( Level.FINE, "Send <" + command + ">" );
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
            if( log ) LOGGER.log( Level.FINE, "Send <" + command + ">" );
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
      if( log ) LOGGER.log( Level.SEVERE, "write for this firmware version not confirmed! CANCEL!" );
    }
  }

  @Override
  public void readGaslistFromSPX42()
  {
    String kdoString;
    kdoString = String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_GET_SETUP_GASLIST, ProjectConst.ETX );
    if( log )
    {
      LOGGER.log( Level.FINE, "readGaslistFromSPX42()...send <" + kdoString + ">" );
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
      if( log ) LOGGER.log( Level.SEVERE, "config was not initialized! CANCEL!" );
      return;
    }
    if( spxVersion.equals( "V2.6.7.7_V" ) )
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
          //
          // Alle Gase des Computers durchexerzieren
          //
          for( gasNr = 0; gasNr < gasCount; gasNr++ )
          {
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
            //
            if( log ) LOGGER.log( Level.INFO, String.format( "write gas number %d to SPX...", gasNr ) );
            command = String.format( "~%x:%x:%x:%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_GASLIST, gasNr, gList.getHEFromGas( gasNr ), gList.getN2FromGas( gasNr ),
                    gList.getBailout( gasNr ), diluent, gList.getCurrGas( gasNr ) );
            if( log ) LOGGER.log( Level.FINE, "Send <" + command + ">" );
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
      if( log ) LOGGER.log( Level.SEVERE, "write for this firmware version not confirmed! CANCEL!" );
    }
  }

  @Override
  public void readLogDirectoryFromSPX()
  {
    String kdoString;
    kdoString = String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_GET_LOG_INDEX, ProjectConst.ETX );
    if( log )
    {
      LOGGER.log( Level.FINE, "readLogDirectoryFromSPX()...send <" + kdoString + ">" );
    }
    this.writeToDevice( kdoString );
  }
}
