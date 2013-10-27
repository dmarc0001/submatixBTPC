package de.dmarcini.submatix.pclogger.comm;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.SPX42Config;
import de.dmarcini.submatix.pclogger.utils.SPX42GasList;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

/**
 * Klasse zur direkten Kommunikation mit dem BT-Device Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
 */
//@formatter:off
public class BTCommunication implements IBTCommunication
{
  public static final int             CONFIG_WRITE_KDO_COUNT = 4;
  public static final int              CONFIG_READ_KDO_COUNT = 7;
  // übersichtlicher machen mit Objekt für alle
  static Logger                                       lg = null;
  private LogDerbyDatabaseUtil                        dbUtil = null;
  private volatile boolean                       isConnected = false;
  private ActionListener                           aListener = null;
  private WriterRunnable                              writer = null;
  private ReaderRunnable                              reader = null;
  private AliveTask                                    alive = null;
  private String                      connectedVirtualDevice = null;
  private SerialPort                              serialPort = null; 
  private int                                  writeWatchDog = -1;
  protected String url;
  @SuppressWarnings( "unused" )
  private static final Pattern              fieldPattern0x09 = Pattern.compile( ProjectConst.LOGSELECTOR );
  private static final Pattern                fieldPatternDp = Pattern.compile(  ":" );
  //@formatter:on
  /**
   * Lokale Klasse, Thread zum Schreiben auf Device Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.01.2012
   */
  //@formatter:off
  public class WriterRunnable implements Runnable
  {
    private OutputStream                           outStream = null;
    private boolean                                  running = false;
    private final ArrayList<String>                writeList = new ArrayList<String>();
    //@formatter:on
    /**
     * Konstruktor des Schreibthreads erstellt: 22.08.2013
     * 
     * @param outStr
     */
    public WriterRunnable( OutputStream outStr )
    {
      outStream = outStr;
    }

    @Override
    public void run()
    {
      // solange was auszugeben ist, mach ich das...
      lg.debug( "start writer thread..." );
      writeList.clear();
      this.running = true;
      while( this.running == true )
      {
        // syncronisiete Methode aufrufen, damit wait und notify machbar sind
        wtSync();
      }
      lg.debug( "stop writer thread..." );
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
     * Zeilenweise an SPX schreiben, wenn nix zu tun ist schlafen legen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.01.2012
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
     * Terminieren lassen geht hier Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.01.2012
     */
    public synchronized void doTeminate()
    {
      notifyAll();
      this.running = false;
      try
      {
        Thread.sleep( 300 );
        outStream.close();
      }
      catch( InterruptedException ex )
      {
        lg.error( "exception while Thread.sleep (" + ex.getLocalizedMessage() + ")" );
      }
      catch( IOException ex )
      {
        lg.error( "exception while closing outstream (" + ex.getLocalizedMessage() + ")" );
      }
    }

    /**
     * Schreibe zum SPX Daten Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.01.2012
     * @param msg
     */
    public synchronized void writeToDevice( String msg )
    {
      writeList.add( msg );
      notifyAll();
    }
  }

  /**
   * Lokale Klasse zum lesen vom SPX42 Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.01.2012
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
    /**
     * Konstruktor des Lesethreads erstellt: 22.08.2013
     * 
     * @param inStr
     */
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
      lg.debug( "start reader thread..." );
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
            lg.error( "reader connection lost..." );
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
          lg.error( "reader connection lost (" + ex.getLocalizedMessage() + ")..." );
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
          lg.error( "INPUT BUFFER OVERFLOW!" );
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
      lg.debug( "stop reader thread..." );
    }

    /**
     * Bearbeite einen Logeintrag Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.01.2012
     * @param start
     * @param end
     * @param mInStrBuffer
     */
    private void execLogentryCmd( int start, int end, StringBuffer mInStrBuffer )
    {
      String readMessage;
      int lstart, lend;
      lg.debug( "execLogentryCmd..." );
      lstart = mInStrBuffer.indexOf( ProjectConst.STX );
      lend = mInStrBuffer.indexOf( ProjectConst.ETX );
      if( lstart > -1 && lend > lstart )
      {
        // ups, hier ist ein "normales" Kommando verpackt
        lg.debug( "oops, normalCmd found.... change to execNormalCmd..." );
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
      lg.debug( "Logline Recived <" + readMessage.substring( 10 ).replaceAll( "\t", " " ) + "...>" );
      if( aListener != null )
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LOGENTRY_LINE, readMessage, System.currentTimeMillis() / 100, 0 );
        aListener.actionPerformed( ex );
      }
    }

    /**
     * Bearbeite eine Meldung vom SPX42 Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 24.12.2011
     * @param start
     * @param end
     * @param mInStrBuffer
     */
    private void execNormalCmd( int start, int end, StringBuffer mInStrBuffer )
    {
      String readMessage;
      String[] fields;
      int command;
      lg.debug( "execNormalCmd..." );
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
      lg.debug( "normal Message Recived <" + readMessage + ">" );
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
        lg.debug( "Logentry list final recived." );
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
        lg.error( "Convert String to Int (" + ex.getLocalizedMessage() + ")" );
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
          lg.debug( "SPX Devicename recived! <" + fields[1] + ">" );
          break;
        case ProjectConst.SPX_ALIVE:
          // Ackuspannung übertragen
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SPXALIVE, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          lg.debug( "SPX is Alive, Acku value recived." );
          break;
        case ProjectConst.SPX_APPLICATION_ID:
          // Sende Nachricht Firmwareversion empfangen!
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_FWVERSION_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          lg.debug( "Application ID (Firmware Version)  recived! <" + fields[1] + ">" );
          break;
        case ProjectConst.SPX_SERIAL_NUMBER:
          // Sende Nachricht Seriennummer empfangen!
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SERIAL_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          lg.debug( "Serial Number recived! <" + fields[1] + ">" );
          break;
        case ProjectConst.SPX_DATETIME:
          // Quittung für Setzen des Datums
          lg.debug( "SPX_DATETIME Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_DEKO:
          // Quittung für Setze DECO
          lg.debug( "Response for set deco <" + readMessage + "> was recived." );
          //
          // TODO: readDecoPrefs();
          //
          break;
        case ProjectConst.SPX_SET_SETUP_SETPOINT:
          // Quittung für Setzen der Auto-Setpointeinstelungen
          lg.debug( "SPX_SET_SETUP_SETPOINT Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_DISPLAYSETTINGS:
          // Quittung für Setzen der Displayeinstellungen
          lg.debug( "SET_SETUP_DISPLAYSETTINGS Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_UNITS:
          // Quittung für Setzen der Einheiten
          lg.debug( "SPX_SET_SETUP_UNITS Acknoweledge recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_INDIVIDUAL:
          // Quittung für Individualeinstellungen
          lg.debug( "SPX_SET_SETUP_INDIVIDUAL Acknoweledge recived <" + readMessage + ">" );
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
          lg.debug( "DECO_EINST recived <" + readMessage + ">" );
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
          lg.debug( "GET_SETUP_SETPOINT recived <" + readMessage + ">" );
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
          lg.debug( "GET_SETUP_DISPLAYSETTINGS recived <" + readMessage + ">" );
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
          lg.debug( "GET_SETUP_UNITS recived <" + readMessage + ">" );
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
          lg.debug( "GET_SETUP_INDIVIDUAL recived <" + readMessage + ">" );
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
          lg.debug( "GET_SETUP_GASLIST recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_SET_SETUP_GASLIST:
          // Besaetigung fuer Gas setzen bekommen
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_GAS_WRITTEN, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          lg.debug( "SET_SETUP_GASLIST recived <" + readMessage + ">" );
          break;
        case ProjectConst.SPX_GET_LOG_INDEX:
          // Ein Logbuch Verzeichniseintrag gefunden
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_DIRENTRY_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          lg.debug( "SPX_GET_LOG_INDEX recived!" );
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
            lg.debug( "Logfile transmission started..." );
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
              lg.debug( "Logfile transmission finished." );
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
          lg.debug( "SPX42 switch syncmode OFF! Connection will failure!" );
          break;
        case ProjectConst.SPX_LICENSE_STATE:
          // LICENSE_STATE gefunden
          if( aListener != null )
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LICENSE_STATE_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
            aListener.actionPerformed( ex );
          }
          lg.debug( "LICENSE_STATE recived <" + readMessage + ">" );
          break;
        default:
          lg.warn( "unknown Messagetype recived <" + readMessage + ">" );
      }
    }

    /**
     * Terminieren lassen geht hier Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.01.2012
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
   * Task soll einfach von Zeit zu Zeit gucken, ob alles noch läuft Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 06.05.2012
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
          lg.error( "Exception while ticker sleeps: <" + ex.getMessage() + ">" );
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
     * Soll es möglich machen, den Task abzubrechen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 06.05.2012
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
   * Konstruktor der Kommunikation Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.01.2012
   * @param dbUtil
   */
  public BTCommunication( final LogDerbyDatabaseUtil dbUtil )
  {
    lg = SpxPcloggerProgramConfig.LOGGER;
    lg.debug( "bluethooth communication object create..." );
    this.dbUtil = dbUtil;
    // besorg mir die Gerätenamen aus der Datenbank
    if( !this.dbUtil.isOpenDB() )
    {
      try
      {
        this.dbUtil.createConnection();
      }
      catch( SQLException ex )
      {
        lg.error( "error while construct bluethooth object: <" + ex.getLocalizedMessage() + ">" );
        ex.printStackTrace();
      }
      catch( ClassNotFoundException ex )
      {
        lg.error( "error while construct bluethooth object: <" + ex.getLocalizedMessage() + ">" );
        ex.printStackTrace();
      }
    }
    //
    // jetzt noch Tick starten und dabei ALIVE abfragen...
    //
    lg.debug( "bluethooth communication object create...start ticker..." );
    alive = new AliveTask();
    Thread al = new Thread( alive );
    al.setName( "bt_alive_task" );
    al.setPriority( Thread.NORM_PRIORITY - 2 );
    al.start();
    lg.debug( "bluethooth communication object create...OK" );
  }

  @Override
  public boolean isConnected()
  {
    return this.isConnected;
  }

  @Override
  public void addActionListener( ActionListener al )
  {
    aListener = al;
  }

  @Override
  public void removeActionListener()
  {
    aListener = null;
  }

  @Override
  public void connectVirtDevice( final String devName )
  {
    //
    lg.debug( "try to connect virtual device <" + devName + ">..." );
    this.connectedVirtualDevice = null;
    this.serialPort = null;
    isConnected = false;
    //
    // Da das länger dauern kann, wieder einen Thread eröffnen,
    // damit Swing eine Change hat die Grafik zu erneutern
    //
    Thread ct = new Thread() {
      @Override
      public void run()
      {
        // Port ID holen
        CommPortIdentifier portIdentifier;
        CommPort commPort = null;
        try
        {
          portIdentifier = CommPortIdentifier.getPortIdentifier( devName );
          // versuch den Port zu öffnen, wirft PortInUseException
          commPort = portIdentifier.open( this.getClass().getName(), 10000 );
          if( commPort instanceof SerialPort )
          {
            lg.debug( "device <" + devName + "> is serial port..." );
            serialPort = ( SerialPort )commPort;
            connectedVirtualDevice = "virtual";
            // Ist ein virtueller Port, da brauch ioch keine Parameter einstellen....
            // serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            // Eingabe erzeugen
            InputStream din;
            din = serialPort.getInputStream();
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
            lg.error( "device <" + devName + "> is NOT serial port ABORT!" );
            if( aListener != null )
            {
              ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
              aListener.actionPerformed( ex1 );
            }
          }
        }
        catch( NoSuchPortException ex )
        {
          lg.error( "device <" + devName + "> is NOT serial port ABORT!" );
          if( aListener != null )
          {
            ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ex1 );
          }
        }
        catch( PortInUseException ex )
        {
          lg.error( "device <" + devName + "> is in use. ABORT!" );
          if( aListener != null )
          {
            ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ex1 );
          }
        }
        catch( IOException ex )
        {
          lg.error( "device <" + devName + "> had an Exception : <" + ex.getLocalizedMessage() + ">" );
          if( aListener != null )
          {
            ActionEvent ex1 = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ex1 );
          }
        }
      }
    };
    ct.setName( "virt_device_connect" );
    ct.start();
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
    writer = null;
    reader = null;
    if( this.serialPort != null )
    {
      this.serialPort.close();
    }
    this.serialPort = null;
    this.connectedVirtualDevice = null;
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
    {
      lg.debug( "readConfigFromSPX()...send <" + kdoString + ">" );
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
  public void writeConfigToSPX( final SPX42Config config )
  {
    Thread configWriteThread = null;
    //
    if( !config.isInitialized() )
    {
      lg.error( "config was not initialized! CANCEL!" );
      return;
    }
    if( ProjectConst.FIRMWARE_2_6_7_7V.equals( config.getFirmwareVersion() ) || config.getFirmwareVersion().startsWith( ProjectConst.FIRMWARE_2_7Vx )
            || config.getFirmwareVersion().startsWith( ProjectConst.FIRMWARE_2_7Hx ) )
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
          else if( config.getFirmwareVersion().startsWith( ProjectConst.FIRMWARE_2_7Vx ) )
          {
            firmware = ProjectConst.FW_2_7V;
          }
          else if( config.getFirmwareVersion().startsWith( ProjectConst.FIRMWARE_2_7Hx ) )
          {
            firmware = ProjectConst.FW_2_7H;
          }
          else
          {
            lg.error( "Firmware not supportet! CANCEL!" );
            if( aListener != null )
            {
              lg.error( "SEND MESSAGE!" );
              ae = new ActionEvent( this, ProjectConst.MESSAGE_FWNOTSUPPORTED, config.getFirmwareVersion() );
              aListener.actionPerformed( ae );
            }
            return;
          }
          //
          // Kommando SPX_SET_SETUP_DEKO
          // Deco-Einstellungen setzen
          lg.info( "write deco propertys" );
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
            case ProjectConst.FW_2_7H:
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
          lg.debug( "Send <" + command + ">" );
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
          lg.info( "write display propertys" );
          command = String.format( "~%x:%x:%x", ProjectConst.SPX_SET_SETUP_DISPLAYSETTINGS, config.getDisplayBrightness(), config.getDisplayOrientation() );
          lg.debug( "Send <" + command + ">" );
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
          lg.info( "write units propertys" );
          command = String.format( "~%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_UNITS, config.getUnitTemperature(), config.getUnitDepth(), config.getUnitSalnity() );
          lg.debug( "Send <" + command + ">" );
          writeSPXMsgToDevice( command );
          // gib Bescheid
          if( aListener != null )
          {
            ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
            aListener.actionPerformed( ae );
          }
          lg.info( "write setpoint propertys" );
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
            case ProjectConst.FW_2_7H:
              // ~30:A:P
              // A = Setpoint bei (0,1,2,3,4) = (0,5,15,20,25)
              // P = Partialdruck (0..4) 1.0 .. 1.4
              command = String.format( "~%x:%x:%x", ProjectConst.SPX_SET_SETUP_SETPOINT, config.getAutoSetpoint(), config.getMaxSetpoint() );
              break;
          }
          lg.debug( "Send <" + command + ">" );
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
            lg.info( "write individual propertys" );
            command = String.format( "~%x:%x:%x:%x:%x:%x", ProjectConst.SPX_SET_SETUP_INDIVIDUAL, config.getSensorsOn(), config.getPscrModeOn(), config.getSensorsCount(),
                    config.getSoundOn(), config.getLogInterval() );
            lg.debug( "Send <" + command + ">" );
            writeSPXMsgToDevice( command );
            // gib Bescheid
            if( aListener != null )
            {
              ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
              aListener.actionPerformed( ae );
            }
          }
          // gib Bescheid vorgang zuende
          lg.info( "write config endet." );
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
      lg.error( "write for this firmware version not confirmed! CANCEL!" );
      if( aListener != null )
      {
        lg.error( "SEND MESSAGE!" );
        ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_FWNOTSUPPORTED, config.getFirmwareVersion() );
        aListener.actionPerformed( ev );
      }
    }
  }

  @Override
  public void readGaslistFromSPX42()
  {
    String kdoString;
    kdoString = String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_GET_SETUP_GASLIST, ProjectConst.ETX );
    {
      lg.debug( "readGaslistFromSPX42()...send <" + kdoString + ">" );
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
      lg.error( "config was not initialized! CANCEL!" );
      return;
    }
    if( ProjectConst.FIRMWARE_2_6_7_7V.equals( spxVersion ) || spxVersion.startsWith( ProjectConst.FIRMWARE_2_7Vx ) || spxVersion.startsWith( ProjectConst.FIRMWARE_2_7Hx ) )
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
          else if( spxVersion.startsWith( ProjectConst.FIRMWARE_2_7Vx ) )
          {
            firmware = ProjectConst.FW_2_7V;
          }
          else if( spxVersion.startsWith( ProjectConst.FIRMWARE_2_7Hx ) )
          {
            firmware = ProjectConst.FW_2_7H;
          }
          else
          {
            lg.error( "Firmware not supportet! CANCEL!" );
            return;
          }
          //
          // Alle Gase des Computers durchexerzieren
          //
          for( gasNr = 0; gasNr < gasCount; gasNr++ )
          {
            lg.info( String.format( "write gas number %d to SPX...", gasNr ) );
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
              case ProjectConst.FW_2_7H:
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
            lg.debug( "Send <" + command + ">" );
            writeSPXMsgToDevice( command );
            // gib Bescheid
            if( aListener != null )
            {
              ae = new ActionEvent( this, ProjectConst.MESSAGE_PROCESS_NEXT, null );
              aListener.actionPerformed( ae );
            }
          }
          // gib Bescheid Vorgang zuende
          lg.info( "write gaslist success." );
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
      lg.error( "write for this firmware version not confirmed! CANCEL!" );
    }
  }

  @Override
  public void readLogDirectoryFromSPX()
  {
    String kdoString;
    kdoString = String.format( "%s~%x%s", ProjectConst.STX, ProjectConst.SPX_GET_LOG_INDEX, ProjectConst.ETX );
    {
      lg.debug( "readLogDirectoryFromSPX()...send <" + kdoString + ">" );
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
      lg.warn( "connected Device is again NULL!" );
    }
    return null;
  }

  @Override
  public void readLogDetailFromSPX( int logNumber )
  {
    if( isConnected )
    {
      String kdoString = String.format( "%s~%x:%x%s", ProjectConst.STX, ProjectConst.SPX_GET_LOG_NUMBER, logNumber, ProjectConst.ETX );
      {
        lg.debug( "readLogDetailFromSPX()...send <" + kdoString + ">" );
      }
      this.writeToDevice( kdoString );
    }
  }

  @Override
  public void setNameForVirtualDevice( String serialNumber )
  {
    this.connectedVirtualDevice = serialNumber;
  }

  @Override
  public void writeDateTimeToDevice( DateTime dTime )
  {
    String kdoString;
    //
    if( isConnected )
    {
      //
      // Setze das Zeit und Datum als Kommandostring zusammen
      //
      kdoString = String.format( "%s~%x:%02x:%02x:%02x:%02x:%02x%s", ProjectConst.STX, ProjectConst.SPX_DATETIME, dTime.getHourOfDay(), dTime.getMinuteOfHour(),
              dTime.getDayOfMonth(), dTime.getMonthOfYear(), dTime.getYearOfCentury(), ProjectConst.ETX );
      {
        lg.debug( "writeDateTimeToDevice()...send <" + kdoString + "> (DATETIME)" );
      }
      this.writeToDevice( kdoString );
    }
  }
}
