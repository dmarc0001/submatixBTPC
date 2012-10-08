package de.dmarcini.submatix.pclogger.comm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.bluetooth.RemoteDevice;

import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;

public class DeviceCache
{
  private class deviceData
  {
    String       connection;
    String       pin;
    String       alias;
    String       type;
    RemoteDevice remoteDevice;
    boolean      isInDb       = false;
    boolean      isConsistent = false;
  }

  private final HashMap<String, deviceData> deviceHash = new HashMap<String, deviceData>();
  private LogDerbyDatabaseUtil              dbUtil;

  /**
   * 
   * Privater (gesperrter) Konstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   */
  @SuppressWarnings( "unused" )
  private DeviceCache()
  {}

  /**
   * 
   * Der Konstruktor...
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param dbUtil
   */
  public DeviceCache( LogDerbyDatabaseUtil dbUtil )
  {
    this.dbUtil = dbUtil;
  };

  /**
   * 
   * Einfaches zufügen des Gerätes
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @param type
   */
  public void addDevice( final String deviceName, final String type )
  {
    if( !deviceHash.containsKey( deviceName ) )
    {
      deviceData dev = new deviceData();
      //
      dev.connection = null;
      dev.pin = "0000";
      dev.alias = "AD-" + deviceName;
      dev.type = type;
      dev.remoteDevice = null;
      dev.isInDb = false;
      dev.isConsistent = false;
      //
      deviceHash.put( deviceName, dev );
    }
  }

  /**
   * 
   * Gerät zufügen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 11.09.2012
   * @param deviceName
   * @param connectionString
   * @param pin
   * @param alias
   * @param remoteDevice
   * @param type
   */
  public void addDevice( final String deviceName, final String connectionString, final String pin, final String alias, final RemoteDevice remoteDevice, final String type )
  {
    if( !deviceHash.containsKey( deviceName ) )
    {
      deviceData dev = new deviceData();
      //
      dev.connection = connectionString;
      dev.pin = pin;
      dev.alias = alias;
      dev.remoteDevice = remoteDevice;
      dev.isInDb = false;
      dev.isConsistent = false;
      dev.type = type;
      //
      deviceHash.put( deviceName, dev );
    }
  }

  /**
   * 
   * Den Cache leeren
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   */
  public void clear()
  {
    deviceHash.clear();
  }

  /**
   * 
   * Alias für ein Device zurückgeben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @return Alias
   */
  public String getAlias( final String deviceName )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      return( deviceHash.get( deviceName ).alias );
    }
    return( null );
  }

  /**
   * 
   * Gib den Verbindungsstring des Gerätes zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @return Verbindungs URL
   */
  public String getConnectionString( final String deviceName )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      return( deviceHash.get( deviceName ).connection );
    }
    return( null );
  }

  /**
   * 
   * Gib einien Iterator über alle Devices zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @return Iterator über Devices
   */
  public Iterator<String> getDevicesIterator()
  {
    return( deviceHash.keySet().iterator() );
  }

  /**
   * 
   * PIN für ein Gerät zurück geben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @return PIN oder null
   */
  public String getPin( final String deviceName )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      return( deviceHash.get( deviceName ).pin );
    }
    return( null );
  }

  /**
   * 
   * Das Remote Device von einem Gerät abfragen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @return REmote Device oder null
   */
  public RemoteDevice getRemoteDevice( final String deviceName )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      return( deviceHash.get( deviceName ).remoteDevice );
    }
    return( null );
  }

  /**
   * 
   * Gib den Typ des Gerätes zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.10.2012
   * @param deviceName
   * @return typ des Devices
   * 
   */
  public String getType( final String deviceName )
  {
    return( deviceHash.get( deviceName ).type );
  }

  /**
   * 
   * Cache von DB füllen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @return Vector mit Stringarrays
   */
  public Vector<String[]> initFromDb()
  {
    Vector<String[]> alData = dbUtil.getAliasDataConn();
    if( alData != null )
    {
      for( String[] fields : alData )
      {
        deviceData dev = new deviceData();
        //
        dev.connection = null;
        dev.pin = fields[3];
        dev.alias = fields[1];
        dev.remoteDevice = null;
        dev.isInDb = true;
        dev.isConsistent = true;
        dev.type = fields[4];
        //
        deviceHash.put( fields[0], dev );
      }
    }
    return( alData );
  }

  /**
   * 
   * Ist das Device in der Datenbank vorhanden?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @return Vorhanden?
   */
  public boolean isDeviceInDb( final String deviceName )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      return( deviceHash.get( deviceName ).isInDb );
    }
    return( false );
  }

  /**
   * 
   * Wurde Datensatz verändert?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @return Syncron mit datenbank?
   */
  public boolean isDeviceSyncWithDb( final String deviceName )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      return( deviceHash.get( deviceName ).isConsistent );
    }
    return( false );
  }

  /**
   * 
   * Gibt es den Datensatz im Cache?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @return ISt vorhanden oder nicht
   */
  public boolean isDeviceThere( final String deviceName )
  {
    return( deviceHash.containsKey( deviceName ) );
  }

  /**
   * 
   * Gibt zurück, ob das Objekt leer ist
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @return Ist anzahl der Einträge 0
   */
  public boolean isEmpty()
  {
    return( deviceHash.isEmpty() );
  }

  /**
   * 
   * Cache von Datenbank auffrischen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   */
  public void refreshFromDb()
  {
    Vector<String[]> alData = dbUtil.getAliasDataConn();
    if( alData != null )
    {
      for( String[] fields : alData )
      {
        if( deviceHash.containsKey( fields[0] ) )
        {
          deviceData dev = deviceHash.get( fields[0] );
          dev.alias = fields[1];
          dev.pin = fields[3];
          dev.type = fields[4];
          dev.isInDb = true;
        }
        else
        {
          deviceData dev = new deviceData();
          //
          dev.connection = null;
          dev.pin = fields[3];
          dev.alias = fields[1];
          dev.type = fields[4];
          dev.remoteDevice = null;
          dev.isInDb = true;
          dev.isConsistent = true;
          //
          deviceHash.put( fields[0], dev );
        }
      }
    }
  }

  /**
   * 
   * Einen Aliasnamen für ein Gerät vergeben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @param Alias
   */
  public void setAlias( final String deviceName, final String Alias )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      deviceHash.get( deviceName ).alias = Alias;
      deviceHash.get( deviceName ).isConsistent = false;
    }
  }

  /**
   * 
   * Setze den Verbindunggstring zu einem Gerät
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @param val
   */
  public void setConnectionString( final String deviceName, final String val )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      deviceHash.get( deviceName ).connection = val;
      deviceHash.get( deviceName ).isConsistent = false;
    }
  }

  /**
   * 
   * Setze Datensatz als in Datenbank vorhanden
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @param is
   */
  public void setDeviceInDb( final String deviceName, final boolean is )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      deviceHash.get( deviceName ).isInDb = is;
    }
  }

  /**
   * 
   * Setze den Datensatz als Konsistent mit DB
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @param is
   */
  public void setDeviceSyncWithDb( final String deviceName, final boolean is )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      deviceHash.get( deviceName ).isConsistent = is;
    }
  }

  /**
   * 
   * PIN für ein Gerät setzen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @param val
   */
  public void setPin( final String deviceName, final String val )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      deviceHash.get( deviceName ).pin = val;
      deviceHash.get( deviceName ).isConsistent = false;
    }
  }

  /**
   * 
   * DAs Remote device von einem Gerät erfragen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @param deviceName
   * @param val
   */
  public void setRemoteDevice( final String deviceName, final RemoteDevice val )
  {
    if( deviceHash.containsKey( deviceName ) )
    {
      deviceHash.get( deviceName ).remoteDevice = val;
      deviceHash.get( deviceName ).isConsistent = false;
    }
  }

  /**
   * 
   * Gib dien Anzahl der Einträge zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.09.2012
   * @return Anzahl der Einträge
   */
  public int size()
  {
    return( deviceHash.size() );
  }
}
