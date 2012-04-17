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

import de.dmarcini.submatix.pclogger.res.ProjectConst;

/**
 * 
 * Klasse zur direkten Kommunikation mit dem BT-Device
 *
 * Project: SubmatixBTConfigPC
 * Package: de.dmarcini.submatix.pclogger.comm
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 * Stand: 08.01.2012
 */
public class BTCommunication implements IBTCommunication
{
  //@formatter:off
  static final UUID                       UUID_SERIAL_DEVICE = new UUID( 0x1101 );
  private HashMap<String,String>                 connectHash = new HashMap<String,String>();
  static Logger                                       LOGGER = null;
  private boolean                                        log = false;
  private boolean                                isConnected = false;
  private ActionListener                           aListener = null;
  private static volatile boolean          discoverInProcess = false;
  StreamConnection                                      conn = null;
  private WriterRunnable                              writer = null;
  private ReaderRunnable                              reader = null;
  @SuppressWarnings( "unused" )
  private static final Pattern              fieldPattern0x09 = Pattern.compile( ProjectConst.LOGSELECTOR );
  private static final Pattern                fieldPatternDp = Pattern.compile(  ":" );
  //@formatter:on
  
  /**
   * 
   * Lokale Klasse, Thread zum Schreiben auf Device
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 11.01.2012
   */
  public class WriterRunnable implements Runnable
  {
    //@formatter:off
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
      if( LOGGER != null ) LOGGER.log( Level.FINEST, "start writer thread..." );
      this.running = true;
      while( this.running == true )
      {
        // syncronisiete Methode aufrufen, damit wait und notify machbar sind
        wtSync();
      }
      if( LOGGER != null ) LOGGER.log( Level.FINEST, "stop writer thread..." );
      isConnected = false;
      if( aListener != null ) 
      {
        ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED,  null );
        aListener.actionPerformed( ev );
      }
    }
    
    /**
     * 
     * Zeilenweise an SPX schreiben, wenn nix zu tun ist schlafen legen
     *
     * Project: SubmatixBTConfigPC
     * Package: de.dmarcini.submatix.pclogger.comm
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     * Stand: 11.01.2012
     * TODO
     */
    private synchronized void wtSync()
    {
      if( writeList.isEmpty() || outStream == null )
      {
        // ist die Liste leer, mach nix, einfach relaxen
        try
        {
          wait();
        }
        catch( InterruptedException ex ){}
      }
      else
      {
        // ich gebe einen Eintrag aus...
        try
        {
          // also den String Eintrag in den Outstream...
          outStream.write( (writeList.remove( 0 )).getBytes() );
        }
        catch( IOException ex )
        {
          isConnected = false;
          if( aListener != null ) 
          {
            ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTED, null );
            aListener.actionPerformed( ev );
          }
          running=false;
          return;
        }
      }
    }
    
    /**
     * 
     * Terminieren lassen geht hier
     *
     * Project: SubmatixBTConfigPC
     * Package: de.dmarcini.submatix.pclogger.comm
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     * Stand: 11.01.2012
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
     * Project: SubmatixBTConfigPC
     * Package: de.dmarcini.submatix.pclogger.comm
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     * Stand: 11.01.2012
     * @param msg
     */
    public synchronized void writeToDevice( String msg )
    {
      notifyAll();
      writeList.add( msg );
    } 
  }

  /**
   * 
   * Lokale Klasse zum lesen vom SPX42
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 11.01.2012
   */
  public class ReaderRunnable implements Runnable
  {
    //@formatter:off
    private byte[]                                    buffer = new byte[1024];
    private InputStream                             inStream = null;
    private boolean                                  running = false;
    private volatile boolean                    isLogentryMode = false;
    private final ArrayList<String>                    dirList = new ArrayList<String>();
    private StringBuffer                          mInStrBuffer = new StringBuffer( 1024 );  
    //@formatter:on
    
    public ReaderRunnable( InputStream inStr )
    {
      inStream = inStr;
    }
    
    @Override
    public void run()
    {
      int bytes=0;
      String readMessage = "";
      int start, end, lstart, lend;
      boolean logCmd, normalCmd;
      
      // solange was auszugeben ist, mach ich das...
      if( LOGGER != null ) LOGGER.log( Level.FINEST, "start reader thread..." );
      this.running = true;
      while( this.running == true )
      {
        try
        {
          bytes = inStream.read( buffer );
          readMessage = new String( buffer, 0, bytes );
          if( bytes == -1 )
          {
            // Verbindung beendet/verloren
            isConnected = false;
            if( LOGGER != null ) LOGGER.log( Level.SEVERE, "reader connection lost..." );
            if( aListener != null ) 
            {
              ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_BTLOSTCONNECT,  null );
              aListener.actionPerformed( ev );
            }
            running = false;
            return;
          }
        }
        catch( IOException ex )
        {
          // IO Fehler
          isConnected = false;
          if( LOGGER != null ) LOGGER.log( Level.SEVERE, "reader connection lost (" + ex.getLocalizedMessage() + ")..." );
          if( aListener != null ) 
          {
            ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_BTLOSTCONNECT,  null );
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
          if( LOGGER != null ) LOGGER.log( Level.SEVERE, "INPUT BUFFER OVERFLOW!" );
          if( aListener != null ) 
          {
            ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_BTLOSTCONNECT,  null );
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
      isConnected = false;
      if( LOGGER != null ) LOGGER.log( Level.FINEST, "stop reader thread..." );
    }
    
    /**
     * 
     * Bearbeite einen Logeintrag
     *
     * Project: SubmatixBTConfigPC
     * Package: de.dmarcini.submatix.pclogger.comm
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     * Stand: 11.01.2012
     * @param start
     * @param end
     * @param mInStrBuffer
     */
    private void execLogentryCmd( int start, int end, StringBuffer mInStrBuffer )
    {
      String readMessage;
      int lstart, lend;
      if( LOGGER != null ) LOGGER.log(  Level.FINEST, "execLogentryCmd..." );
      lstart = mInStrBuffer.indexOf( ProjectConst.STX );
      lend = mInStrBuffer.indexOf( ProjectConst.ETX );
      if( lstart > -1 && lend > lstart )
      {
        // ups, hier ist ein "normales" Kommando verpackt
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "oops, normalCmd found.... change to execNormalCmd..." );
        isLogentryMode = false;
        // den anfang wegputzen
        // mInStrBuffer = mInStrBuffer.delete( 0, lstart );
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
      if( LOGGER != null ) LOGGER.log( Level.FINEST, "Logline Recived <" + readMessage + ">" );
      if( aListener != null ) 
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LOGENTRY_LINE,  readMessage, System.currentTimeMillis() / 100, 0 );
        aListener.actionPerformed( ex );
      }
    }
    
    /**
     * 
     * Bearbeite eine Meldung vom SPX42
     *
     * Project: SubmatixBTConfigPC
     * Package: de.dmarcini.submatix.pclogger.comm
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     * Stand: 24.12.2011
     * @param start
     * @param end
     * @param mInStrBuffer
     */
    private void execNormalCmd( int start, int end, StringBuffer mInStrBuffer )
    {
      String readMessage;
      String[] fields;
      
      if( LOGGER != null ) LOGGER.log(Level.FINEST, "execNormalCmd..." );
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
      if( LOGGER != null ) LOGGER.log( Level.FINEST, "normal Message Recived <" + readMessage + ">" );
      
      // bekomme heraus, welcher Art die ankommende Message ist
      if( 0 == readMessage.indexOf( ProjectConst.IS_DEVNAME ) )
      {
        fields = fieldPatternDp.split( readMessage );
        // Sende Nachricht Gerätename empfangen!
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_TCNAME_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "SPX Devicename recived! <" + fields[1] + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.IS_VERSION ) )
      {
        fields = fieldPatternDp.split( readMessage );
        // Sende Nachricht Firmwareversion empfangen!
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_FWVERSION_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "Serial Number recived! <" + fields[1] + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.ISSERIAL ) )
      {
        fields = fieldPatternDp.split( readMessage );
        // Sende Nachricht Seriennummer empfangen!
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SERIAL_READ, new String( fields[1] ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "Serial Number recived! <" + fields[1] + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.ISKDO_DECO ) )
      {
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
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "DECO_EINST recived <" + readMessage + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.QKDOSETDECO ) )
      {
        // Quittung für Setze DECO
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "Response for set deco <" + readMessage + "> was recived." );
        //
        //TODO: readDecoPrefs();
        //
      }
      else if( 0 == readMessage.indexOf( ProjectConst.ISKDO_SETPOINT ) )
      {
        // Kommando SETPOINT liefert
        // ~35:A:P
        // A = Setpoint bei (0,1,2,3) = (0,5,15,20)
        // P = Partialdruck (0..4) 1.0 .. 1.4
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_SETPOINT_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "KDO_SETPOINT recived <" + readMessage + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.ISKDO_DISPLAY ) )
      {
        // Kommando DISPLAY liefert
        // ~36:D:A
        // D= 0->10&, 1->50%, 2->100%
        // A= 0->Landscape 1->180Grad
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_DISPLAY_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "KDO_DISPLAY recived <" + readMessage + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.ISKDO_UNITS ) )
      {
        // Kommando UNITS
        // ~37:UD:UL:UW
        // UD= Fahrenheit/Celsius => immer 0 in der aktuellen Firmware 2.6.7.7_U
        // UL= 0=metrisch 1=imperial
        // UW= 0->Salzwasser 1->Süßwasser
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_UNITS_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "KDO_UNITS recived <" + readMessage + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.IS_INDIVIDUAL ) )
      {
        // Kommando INDIVIDUAL liefert
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
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "KDO_INDIVIDUAL recived <" + readMessage + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.ISKDO_GAS ) )
      {
        // Kommando GAS
        // ~39:NR:ST:HE:0:AA:0
        // ST Stickstoff in Prozent (hex)
        // HELIUM
        // 0
        // AA 0=Bailout, 1=Diluent 1, 2= Diluent 2
        // Baulout, Diluent 1, Diulent 2
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_GAS_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "ISKDO_GAS recived <" + readMessage + ">" );
      }
      else if( readMessage.equals( ProjectConst.KDOSETGAS  ))
      {
        // Besaetigung fuer Gas setzen bekommen
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_GAS_WRITTEN, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "GAS WRITTEN recived <" + readMessage + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.ISKDO45 ) )
      {
        // KDO 45 gefunden
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_KDO45_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "KDO045 recived <" + readMessage + ">" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.IS_LOGLISTENTRY ) )
      {
        // Ein Logbuch Verzeichniseintrag gefunden
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_DIRENTRY_READ, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        dirList.add( readMessage );
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "Logentry recived! Stored in Cache" );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.IS_END_LOGLISTENTRY ) )
      {
        // Logbucheinträge fertig gelesen
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_DIRENTRY_END, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "Logentry list final recived." );
      }
      else if( 0 == readMessage.indexOf( ProjectConst.ISKDO_LOGENTRY_START ) )
      {
        // Übertragung Logfile gestartet
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LOGENTRY_START, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "Logfile transmission started..." );
        isLogentryMode = true;
      }
      else if( 0 == readMessage.indexOf( ProjectConst.ISKDO_LOGENTRY_STOP ) )
      {
        // Übertragung beendet
        if( aListener != null ) 
        {
          ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_LOGENTRY_STOP, new String( readMessage ), System.currentTimeMillis() / 100, 0 );
          aListener.actionPerformed( ex );
        }
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "Logfile transmission finished." );
        isLogentryMode = false;
      }
    }
    
    /**
     * 
     * Terminieren lassen geht hier
     *
     * Project: SubmatixBTConfigPC
     * Package: de.dmarcini.submatix.pclogger.comm
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     * Stand: 11.01.2012
     */
    public synchronized void doTeminate()
    {
      this.running = false;
    }
    
  }
  
  @SuppressWarnings( "unused" )
  private BTCommunication() {};
 
  /**
   * 
   * Konstruktor der Kommunikation
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 11.01.2012
   * @param lg
   */
  public BTCommunication( Logger lg )
  {
    LOGGER = lg;
    if( lg == null )
      log = false;
    else
      log = true;
  }
  
 
  @Override
  public boolean isConnected()
  {
    return isConnected;
  }
  
  @Override
  public boolean discoverDevices()
  {
    if( discoverInProcess )
    {
      if( log ) LOGGER.log( Level.WARNING, "Discovering always in process..." );
      return false;
    }
    discoverInProcess = true;
    Thread sb = new Thread()
    {
      public void run()
      {
        // Warteschleife mit Nachrichten
        while( discoverInProcess )
        {
          try
          {
            Thread.sleep(  50  );
          }
          catch( InterruptedException ex ) {}
          if( aListener != null ) 
          {
            ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_BTWAITFOR, null );
            aListener.actionPerformed( ex );
          }
          
        }
        
      }
    };
    
    Thread th = new Thread()
    {
      public void run()
      {
        // Discovery starten
        if( log ) LOGGER.log( Level.FINEST, "start discover für Bluethooth devices..." );
        try
        {
          RemoteDeviceDiscovery.doDiscover();
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
        // suche nach Serial devices
        UUID serviceUUID = UUID_SERIAL_DEVICE;
        
        // Event Objetk TODO: 
        final Object serviceSearchCompletedEvent = new Object();
        
        // Inline Klasse 
        DiscoveryListener listener = new DiscoveryListener() 
        {
          public void deviceDiscovered( RemoteDevice btDevice, DeviceClass cod )
          {}
    
          public void inquiryCompleted( int discType )
          {}
    
          public void servicesDiscovered( int transID, ServiceRecord[] servRecord )
          {
            if( log ) LOGGER.log(  Level.FINEST, "Services Discovered..." );
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
                String sName = (( String )serviceName.getValue());
                sName = sName.replaceAll( "[^A-Z0-9a-z]{2,}", "" );
                if( log ) LOGGER.log(  Level.FINEST, "Device <" + devName  + "> Service <" + sName + "> found <" + url + ">" );
                connectHash.put( devName, url );
              }
              else
              {
                if( log ) LOGGER.log(  Level.FINEST, "Service found, URL: <" + url + "> IGNORE!" );
              }
            }
          }
    
          public void serviceSearchCompleted( int transID, int respCode )
          {
            if( log ) LOGGER.log(  Level.FINEST, "service search completed!" );
            synchronized( serviceSearchCompletedEvent )
            {
              serviceSearchCompletedEvent.notifyAll();
            }
          }
        };
        // nach welchen UUID soll gesucht werden? (natürlich nur Serial Comm)
        UUID[] searchUuidSet = new UUID[] { serviceUUID };
        // nach welchem Attr-Ids soll gesucht werden?
        int[] attrIDs = new int[] { 0x0100 };// Service name
        
        // jetzt alle gefundenen Devices nach dem Gesuchten untersuchen
        for( Enumeration<?> en = RemoteDeviceDiscovery.devicesDiscovered.elements(); en.hasMoreElements(); )
        {
          RemoteDevice btDevice = ( RemoteDevice )en.nextElement();
          synchronized( serviceSearchCompletedEvent )
          {
            try
            {
              if( log )LOGGER.log(  Level.FINEST, "search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName( false ) );
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
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "Bluethooth Discovering OK" );
        discoverInProcess = false;
      }
    };
    th.setName( "btDiscoverThread" );
    sb.setName(  "btStatusThread" );
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
  public String[] getNameArray()
  {
    ArrayList<String> nList = new ArrayList<String>();
    
    if( LOGGER != null ) LOGGER.log( Level.FINEST, "make stringarray of Services..." );
    for( String dev : connectHash.keySet() )
    {
      // Die KEYS (also DeviceNames) in die Liste
      nList.add( dev );
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
  public void connectDevice( String deviceName ) throws Exception
  {
    String url;
    
    // suche die URL für die Verbindung
    if( connectHash.containsKey( deviceName ))
    {
      url = connectHash.get( deviceName );
      if( aListener != null ) 
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTING, null );
        aListener.actionPerformed( ex );
      }
    }
    else
    {
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
      if( LOGGER != null ) LOGGER.log( Level.FINEST, "Connect to Device..." );
      // Verbinden....
      conn = (StreamConnection) Connector.open(url);
      //
      // Eingabe erzeugen
      InputStream din = new DataInputStream( conn.openInputStream() ); 
      reader = new ReaderRunnable( din );
      Thread rt = new Thread( reader );
      rt.setName( "bt_reader_thread" );
      rt.start();
      //
      // ausgabe erzeugen
      OutputStream dout = new DataOutputStream(conn.openOutputStream());//Get the output stream
      writer = new WriterRunnable( dout );
      Thread wt = new Thread( writer );
      wt.setName( "bt_writer_thread" );
      wt.start();
      isConnected = true;
      System.out.println( "Connect to Device...OK" );
      if( aListener != null ) 
      {
        ActionEvent ex = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTED, null );
        aListener.actionPerformed( ex );
      }
    } 
    catch ( Exception ex) 
    {
      isConnected = false;
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
    catch( InterruptedException ex ){}
    if( conn != null )
    {
      try
      {
        conn.close();
      }
      catch( IOException ex )
      {
        // TODO Auto-generated catch block
      }
    }
    isConnected = false;
    writer = null;
    reader = null;
    conn = null;
  }

  @Override
  public synchronized void writeToDevice( String msg )
  {
    if( isConnected && (writer != null ))
    {
      writer.writeToDevice( msg );
    }
  }

  @Override
  public void writeSPXMsgToDevice( String msg )
  {
    if( isConnected && ( writer != null ))
    {
      writer.writeToDevice( ProjectConst.STX + msg + ProjectConst.ETX );
    }
  }

  @Override
  public void askForSerialNumber()
  {
    writeSPXMsgToDevice( ProjectConst.GETSERIAL );
  }

  @Override
  public void readConfigFromSPX42()
  {
    if( LOGGER != null )
    {
      LOGGER.log( Level.FINEST, "readConfigFromSPX()...send <" + ProjectConst.GETSERIAL + ProjectConst.KDO_DECO + ProjectConst.KDO_SETPOINT + ProjectConst.KDO_DISPLAY + ProjectConst.KDO_UNIS
            + ProjectConst.KDO_INDIVIDUAL + ProjectConst.KDO45 + ">" );
    }
    writeSPXMsgToDevice( ProjectConst.GETSERIAL + ProjectConst.KDO_DECO + ProjectConst.KDO_SETPOINT + ProjectConst.KDO_DISPLAY + ProjectConst.KDO_UNIS + ProjectConst.KDO_INDIVIDUAL
            + ProjectConst.KDO45 );
  }

  @Override
  public void askForDeviceName()
  {
    writeSPXMsgToDevice( ProjectConst.KDO_DEVNAME );
  }

  @Override
  public void askForFirmwareVersion()
  {
    writeSPXMsgToDevice( ProjectConst.KDO_VERSION );
  }
}
