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
}
