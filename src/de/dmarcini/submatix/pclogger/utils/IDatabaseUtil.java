package de.dmarcini.submatix.pclogger.utils;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;

public interface IDatabaseUtil
{
  public SQLiteConnection createNewDatabase() throws SQLiteException;

  public SQLiteConnection createNewDatabase( String dbFileName ) throws SQLiteException;

  public SQLiteConnection createConnection();

  public String[][] getAliasData();

  public String getAliasForName( final String name );

  public String getNameForAlias( final String alias );

  public boolean addAliasForName( final String dev, final String alias );

  public boolean setPinForDevice( final String dev, final String pin );

  public String getPinForDevice( final String deviceName );

  public boolean updateDeviceAlias( final String devName, final String devAlias );

  public void closeDB();
}
