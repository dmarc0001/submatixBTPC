package de.dmarcini.submatix.pclogger.utils;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectDatabaseUtil
{
  public Connection createNewDatabase() throws SQLException, ClassNotFoundException;

  public Connection createNewDatabase( String dbFileName ) throws SQLException, ClassNotFoundException;

  public Connection createConnection();

  public String[][] getAliasData();

  public String getAliasForName( final String name );

  public String getNameForAlias( final String alias );

  public boolean addAliasForName( final String dev, final String alias );

  public boolean setPinForDevice( final String dev, final String pin );

  public String getPinForDevice( final String deviceName );

  public boolean updateDeviceAlias( final String devName, final String devAlias );

  public boolean isOpenDB();

  public void closeDB();

  /**
   * 
   * Lese aus der Datenbank alle vorhandenen Geräte heraus
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.07.2012
   * @return Liste der Geräte
   */
  public String[] readDevicesFromDatabase();
}
