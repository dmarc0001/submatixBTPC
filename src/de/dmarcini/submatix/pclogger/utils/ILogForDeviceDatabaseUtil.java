package de.dmarcini.submatix.pclogger.utils;

import java.sql.Connection;
import java.sql.SQLException;

public interface ILogForDeviceDatabaseUtil
{
  public Connection createConnection();

  public Connection createNewDatabase() throws SQLException, ClassNotFoundException;

  public Connection createNewDatabase( String dbFileName ) throws SQLException, ClassNotFoundException;

  public void closeDB();

  public boolean isOpenDB();

  public boolean isLogSaved( String filename );

  public int writeNewDive( String deviceId, String fileOnSPX, String timeZone, long startTime );

  public int appendLogToCache( int diveId, LogLineDataObject logLineObj );

  public int writeLogToDatabase( int diveId );
}
