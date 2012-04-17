package de.dmarcini.submatix.pclogger.comm;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.bluetooth.*;

/**
 * 
 * Klasse zum Erforschen der BT-Funkumgebung
 *
 * Project: SubmatixBTConfigPC
 * Package: de.dmarcini.submatix.pclogger.comm
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 * Stand: 08.01.2012
 * TODO
 */
public class RemoteDeviceDiscovery
{
  //@formatter:off
  static Logger                                       LOGGER = null;
  private static boolean                                log = false;
  public static final Vector<RemoteDevice> devicesDiscovered = new Vector<RemoteDevice>();
 
  //@formatter:on
  
  /**
   * 
   * geschützter Konstruktor, soll nicht benutzt werden
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 08.01.2012
   */
  protected RemoteDeviceDiscovery()
  {
    LOGGER = null;
    log = false;
  }
  /**
   * 
   * Konstruktor für den BT-Discovery prozess
   * 
   * Project: BlueThoothTest Package:
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.01.2012
   * @param lg 
   */
  public RemoteDeviceDiscovery( Logger lg )
  {
    LOGGER = lg;
    if( lg != null )
      log = true;
    else
      log = false;   
  };

  /**
   * 
   * Bekannte Geräte lesen
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 22.01.2012
   * @throws BluetoothStateException
   * TODO
   */
  public static void readCached() throws BluetoothStateException
  {
    RemoteDevice[] rd = LocalDevice.getLocalDevice().getDiscoveryAgent().retrieveDevices( DiscoveryAgent.PREKNOWN );
    
    devicesDiscovered.clear();
    
    for( RemoteDevice btDevice : rd )
    {
      devicesDiscovered.addElement( btDevice );
    }
  }
  
  
  /**
   * 
   * Komplett Umgebug neu durchsuchen
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 22.01.2012
   * @throws IOException
   * @throws InterruptedException
   * TODO
   */
  public static void doDiscover() throws IOException, InterruptedException
  {
    // Objekt zum Empfang der Nachrichten
    final Object inquiryCompletedEvent = new Object();
    // Vector leeren
    devicesDiscovered.clear();
    // Horche auf Meldungen, 
    // lokale Klasse erzeugen
    DiscoveryListener listener = new DiscoveryListener() 
    {
      /**
       * Konstruktor lokale Klasse
       */
      public void deviceDiscovered( RemoteDevice btDevice, DeviceClass cod )
      {
        if( log ) LOGGER.log( Level.FINEST, "Device " + btDevice.getBluetoothAddress() + " found" );
        devicesDiscovered.addElement( btDevice );
        try
        {
          if( log ) LOGGER.log( Level.FINEST, "     name " + btDevice.getFriendlyName( false ) );
        }
        catch( IOException cantGetDeviceName )
        {
          if( log ) LOGGER.log( Level.FINEST, "     no name found...." );
        }
      }

      /**
       * alternativer Konstruktor
       */
      public void servicesDiscovered( int transID, ServiceRecord[] servRecord )
      {}

      /**
       * Ausforschen fertig!
       */
      public void inquiryCompleted( int discType )
      {
        if( log ) LOGGER.log( Level.FINEST, "Device Inquiry completed!" );
        synchronized( inquiryCompletedEvent )
        {
          inquiryCompletedEvent.notifyAll();
        }
      }

      /** 
       * Service suchen auf dem Ger�t fertig
       */
      public void serviceSearchCompleted( int transID, int respCode )
      {}

    };
    synchronized( inquiryCompletedEvent )
    {
      boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry( DiscoveryAgent.GIAC, listener );
      if( started )
      {
        if( log ) LOGGER.log( Level.FINEST, "wait for device inquiry to complete..." );
        inquiryCompletedEvent.wait();
        if( log ) LOGGER.log( Level.FINEST,  devicesDiscovered.size() + " device(s) found" );
      }
    }
  }
}
