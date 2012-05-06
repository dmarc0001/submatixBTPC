package de.dmarcini.submatix.pclogger.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

/**
 * 
 * Helferlein für Logdaten in der Datenbank. Alle zeitangaben werden in der DB in UTC abgelegt und werden je nach Zeitzohne des PC wieder in Localzeit umgerechnet.
 * 
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 06.05.2012 TODO
 */
public class LogForDeviceDatabaseUtil implements ILogForDeviceDatabaseUtil
{
  private Logger     LOGGER = null;
  private File       dbFile = null;
  private Connection conn   = null;

  /**
   * 
   * Privater / gesperrter Konstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 06.05.2012
   */
  @SuppressWarnings( "unused" )
  private LogForDeviceDatabaseUtil()
  {};

  /**
   * 
   * Konstruktor für dieses Objekt
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 06.05.2012
   * @param lg
   * @param device
   * @param logdataDir
   */
  public LogForDeviceDatabaseUtil( Logger lg, String device, String logdataDir )
  {
    LOGGER = lg;
    dbFile = new File( String.format( "%s%sdivelog_%s.db", logdataDir, File.separator, device ) );
    conn = null;
  }

  @Override
  public Connection createConnection()
  {
    int version = 0;
    //
    try
    {
      if( conn != null )
      {
        if( !conn.isClosed() )
        {
          return( conn );
        }
      }
      // erzeuge eine Verbindung zur DB-Engine
      conn = null;
      Class.forName( "org.sqlite.JDBC" );
      conn = DriverManager.getConnection( "jdbc:sqlite:" + dbFile.getAbsoluteFile() );
      conn.setAutoCommit( false );
      // Datenbank öffnen, wenn File vorhanden
      LOGGER.log( Level.FINE, "database <" + dbFile.getAbsoluteFile() + "> opened..." );
      version = readDatabaseVersion();
      if( version != ProjectConst.DB_VERSION )
      {
        // ACHTUNG, da hat sich was geändert! Oder die DB war nicht vorhanden
        // ich muß mir was einfallen lassen
        _updateDatabaseVersion( version );
      }
    }
    catch( ClassNotFoundException ex )
    {
      LOGGER.log( Level.SEVERE, "ClassNotFoundException <" + ex.getLocalizedMessage() + ">" );
      return( null );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't open/recreate Database <" + dbFile.getName() + "> (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
    return( conn );
  }

  /**
   * 
   * Lese dei Version der Datenbank, wenn möglich
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 06.05.2012
   * @return
   */
  private int readDatabaseVersion()
  {
    String sql;
    Statement stat;
    ResultSet rs;
    int version = 0;
    //
    LOGGER.log( Level.FINE, "read database version..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( 0 );
    }
    //@formatter:off
    sql = String.format( 
            "select max( %s ) from %s;",
            ProjectConst.V_VERSION,
            ProjectConst.V_DBVERSION
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        version = rs.getInt( 1 );
        LOGGER.log( Level.FINE, String.format( "database read version:%d", version ) );
        rs.close();
        return( version );
      }
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't read dbversion <" + dbFile.getName() + "> (" + ex.getLocalizedMessage() + ")" );
      return( 0 );
    }
    return( 0 );
  }

  @Override
  public Connection createNewDatabase() throws SQLException, ClassNotFoundException
  {
    return( _createNewDatabase( dbFile ) );
  }

  @Override
  public Connection createNewDatabase( String dbFileName ) throws SQLException, ClassNotFoundException
  {
    dbFile = new File( dbFileName );
    return( _createNewDatabase( dbFile ) );
  }

  private Connection _createNewDatabase( File dbFl ) throws SQLException, ClassNotFoundException
  {
    String sql;
    Statement stat;
    //
    LOGGER.log( Level.INFO, String.format( "create new database version:%d", ProjectConst.DB_VERSION ) );
    dbFile = dbFl;
    // DB schliessen, wenn offen
    if( conn != null )
    {
      if( conn.isClosed() )
      {
        conn = null;
      }
      else
      {
        conn.close();
        conn = null;
      }
    }
    // Datendatei verschwinden lassen
    dbFile.delete();
    Class.forName( "org.sqlite.JDBC" );
    conn = DriverManager.getConnection( "jdbc:sqlite:" + dbFile.getAbsolutePath() );
    conn.setAutoCommit( false );
    stat = conn.createStatement();
    // ////////////////////////////////////////////////////////////////////////
    // Datentabellen erzeugen
    //
    // ////////////////////////////////////////////////////////////////////////
    // Die Versionstabelle
    //@formatter:off
    sql = String.format( 
            "create table %s ( %s numeric );",
            ProjectConst.V_DBVERSION,
            ProjectConst.V_VERSION
           );
    LOGGER.log( Level.FINE, String.format( "create table: %s", ProjectConst.V_DBVERSION ) );
    stat.execute(sql);
    // Versionsnummer reinschreiben
    sql = String.format( 
            "insert into %s ( %s ) values ( '%d' );",
            ProjectConst.V_DBVERSION,
            ProjectConst.V_VERSION,
            ProjectConst.DB_VERSION
           );
    LOGGER.log( Level.FINE, String.format( "write database version:%d", ProjectConst.DB_VERSION ) );
    stat.execute(sql);
    conn.commit();
    //@formatter:on
    //
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für die Kopfdaten des Tauchganges
    //@formatter:off
    sql = String.format( 
            "create table %s \n" + 
            " ( \n" + 
            "   %s integer primary key autoincrement,\n" +
            "   %s text not null,\n" +
            "   %s text not null,\n" +
            "   %s text not null,\n" +
            "   %s integer,\n" +
            "   %s real,\n" +
            "   %s real,\n" +
            "   %s integer,\n" +
            "   %s integer" +
            " );",
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVEID,
            ProjectConst.H_FILEONSPX,
            ProjectConst.H_DEVICEID,
            ProjectConst.H_STARTTIME,
            ProjectConst.H_HADSEND,
            ProjectConst.H_FIRSTTEMP,
            ProjectConst.H_LOWTEMP,
            ProjectConst.H_MAXDEPTH,
            ProjectConst.H_SAMPLES
            );
    //@formatter:on
    LOGGER.log( Level.FINE, String.format( "create table: %s", ProjectConst.H_TABLE_DIVELOGS ) );
    stat.execute( sql );
    conn.commit();
    // Index fuer die Tabelle erzeugen
    //@formatter:off
    sql = String.format(
            "create index idx_%s_%s on %s ( %s ASC);",
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_STARTTIME,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_STARTTIME );
    //@formatter:off     
    LOGGER.log( Level.FINE, String.format( "create index on  table: %s", ProjectConst.H_TABLE_DIVELOGS ) );
    stat.execute( sql );
    conn.commit();
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für die Logdaten
    //@formatter:off
    sql = String.format( 
            "create table %s\n" +
            " (\n" +
            "  %s integer not null,\n" +
            "  %s integer,\n" +
            "  %s integer,\n" +
            "  %s real,\n" +
            "  %s real,\n" +
            "  %s real,\n" +
            "  %s real,\n" +
            "  %s integer,\n" + 
            "  %s integer,\n" +
            "  %s integer,\n" +
            "  %s integer,\n" +
            "  %s integer\n" +
            " );",  
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DIVEID,
            ProjectConst.D_DEPTH,
            ProjectConst.D_TEMPERATURE,
            ProjectConst.D_PPO,
            ProjectConst.D_PPO_1,
            ProjectConst.D_PPO_2,
            ProjectConst.D_PPO_3,
            ProjectConst.D_SETPOINT,
            ProjectConst.D_N2,
            ProjectConst.D_HE,
            ProjectConst.D_NULLTIME,
            ProjectConst.D_DELTATIME );
    //@formatter:off     
    LOGGER.log( Level.FINE, String.format( "create table: %s", ProjectConst.D_TABLE_DIVEDETAIL ) );
    stat.execute( sql );
    conn.commit();
    // Index fuer die Tabelle erzeugen
    //@formatter:off
    sql = String.format(
            "create index idx_%s_%s on %s ( %s ASC);",
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DIVEID,
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DIVEID );
    //@formatter:off     
    LOGGER.log( Level.FINE, String.format( "create index on  table: %s", ProjectConst.D_TABLE_DIVEDETAIL ) );
    stat.execute( sql );
    conn.commit();


    // TODO: weitere Tabellen :-)
    stat.close();
    return( conn );
  }

  /**
   * Aus der DB alle Tabellen löschen... Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 24.04.2012
   * @throws SQLException
   * @throws SQLiteException
   */
  private void _dropTablesFromDatabase() throws SQLException
  {
    String sql;
    Statement stat;
    stat = conn.createStatement();
    //@formatter:off
    sql = String.format( 
            "drop table if exists %s;",
            ProjectConst.V_DBVERSION
           );
    LOGGER.log( Level.FINE, String.format( "drop table: %s", ProjectConst.V_DBVERSION ) );
    stat.execute(sql);
    //@formatter:on
    //
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für Kopfdaten löschen
    //@formatter:off
    sql = String.format( 
            "drop table if exists %s;",
            ProjectConst.H_TABLE_DIVELOGS
            );
    //@formatter:on
    LOGGER.log( Level.FINE, String.format( "drop table: %s", ProjectConst.H_TABLE_DIVELOGS ) );
    stat.execute( sql );
    //
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für Logdetails löschen
    //@formatter:off
    sql = String.format( 
            "drop table if exists %s;",
            ProjectConst.D_TABLE_DIVEDETAIL
            );
    //@formatter:on
    LOGGER.log( Level.FINE, String.format( "drop table: %s", ProjectConst.D_TABLE_DIVEDETAIL ) );
    //
    stat.close();
    conn.commit();
  }

  /**
   * Wenn sich die Versionsnummer der DB verändert hat, Datenbankinhalt/Struktur anpassen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 24.04.2012
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws SQLiteException
   */
  private void _updateDatabaseVersion( int oldVersion ) throws SQLException, ClassNotFoundException
  {
    // erst mal vor dem Release: stumpf Tabellen löschen und neu anlegen
    LOGGER.log( Level.INFO, String.format( "create new database version:%d", ProjectConst.DB_VERSION ) );
    _dropTablesFromDatabase();
    _createNewDatabase( dbFile );
  }

  @Override
  public void closeDB()
  {
    LOGGER.log( Level.FINE, "try close database..." );
    if( conn != null )
    {
      try
      {
        if( !conn.isClosed() )
        {
          conn.commit();
          LOGGER.log( Level.FINE, "close database..." );
          conn.close();
          conn = null;
        }
      }
      catch( SQLException ex )
      {
        LOGGER.log( Level.SEVERE, "Can't close Database <" + dbFile.getName() + "> (" + ex.getLocalizedMessage() + ")" );
        return;
      }
    }
    LOGGER.log( Level.FINE, "close database...OK" );
  }

  @Override
  public boolean isOpenDB()
  {
    if( conn == null )
    {
      return( false );
    }
    try
    {
      return( !conn.isClosed() );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to check database ist opened (%s))", ex.getLocalizedMessage() ) );
    }
    return( false );
  }

  @Override
  public boolean isLogSaved( String filename )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    //
    LOGGER.log( Level.FINE, "was log <" + filename + "> always saved?" );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( false );
    }
    //@formatter:off
    sql = String.format( 
            "select %s from %s where %s like '%s';",
            ProjectConst.H_DIVEID,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_FILEONSPX,
            filename
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        LOGGER.log( Level.FINE, String.format( "file <%s> was saved.", filename ) );
        rs.close();
        stat.close();
        return( true );
      }
      LOGGER.log( Level.FINE, "log <" + filename + "> was not saved." );
      rs.close();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't select from database! (" + ex.getLocalizedMessage() + ")" );
      return( false );
    }
    return( false );
  }
}
