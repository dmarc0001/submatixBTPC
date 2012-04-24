package de.dmarcini.submatix.pclogger.utils;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;

public interface IDatabaseUtil
{
  public SQLiteConnection createNewDatabase() throws SQLiteException;

  public SQLiteConnection createNewDatabase( String dbFileName ) throws SQLiteException;

  public SQLiteConnection createConnection();

  public void closeDB();
}
