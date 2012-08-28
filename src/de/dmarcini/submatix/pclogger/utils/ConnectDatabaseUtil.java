package de.dmarcini.submatix.pclogger.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

//@formatter:off
/**
 * @author dmarc
 *
 */
public class ConnectDatabaseUtil implements IConnectDatabaseUtil
{
  private Logger                             LOGGER = null;
  private File                               dbFile = null; 
  private Connection                         conn   = null;
  
  
//@formatter:on
  @SuppressWarnings( "unused" )
  private ConnectDatabaseUtil()
  {};

  /**
   * Konstruktor der Datenbank-Utilitys Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 23.04.2012
   * @param LOGGER
   * @param dbFileName
   */
  public ConnectDatabaseUtil( Logger LOGGER, String dbFileName )
  {
    this.LOGGER = LOGGER;
    dbFile = new File( dbFileName );
    conn = null;
  }

  /**
   * Interne Funktion zum erzeugen einer nagelneuen Datenbank Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 23.04.2012
   * @param dbFl
   *          Fileobjekt
   * @throws ClassNotFoundException
   */
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
    stat.close();
    //@formatter:on
    //
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für Geräte (Tauchcompis mit aliasnamen und PIN)
    //@formatter:off
    sql = String.format( 
            "create table %s ( %s text not null, %s text not null, %s text );",
            ProjectConst.A_DBALIAS,
            ProjectConst.A_DEVNAME,
            ProjectConst.A_ALIAS,
            ProjectConst.A_PIN
            );
    //@formatter:on
    LOGGER.log( Level.FINE, String.format( "create table: %s", ProjectConst.V_DBVERSION ) );
    stat.execute( sql );
    stat.close();
    conn.commit();
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
    stat.close();
    //@formatter:on
    //
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für Geräte (Tauchcompis mit aliasnamen und PIN)
    //@formatter:off
    sql = String.format( 
            "drop table if exists %s;",
            ProjectConst.A_DBALIAS
            );
    //@formatter:on
    LOGGER.log( Level.FINE, String.format( "drop table: %s", ProjectConst.A_DBALIAS ) );
    stat.execute( sql );
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
    // so, mal sehen ob sich was machen läßt
    if( oldVersion > ProjectConst.DB_VERSION )
    {
      // das kann eigentlich nicht passieren
      LOGGER.log( Level.SEVERE, String.format( "found db-version is GREATER than this Version? found: %d, this version: %d", oldVersion, ProjectConst.DB_VERSION ) );
      return;
    }
    switch ( oldVersion )
    {
      case 1:
      case 2:
        _updateTableToVer3();
      case 3:
        _updateTableToVer4();
        break;
      default:
        // Tja, das gibt ja wohl nicht
        LOGGER.log( Level.INFO, String.format( "create new database version:%d", ProjectConst.DB_VERSION ) );
        _dropTablesFromDatabase();
        _createNewDatabase( dbFile );
    }
  }

  @Override
  public boolean addAliasForName( final String dev, final String alias )
  {
    String sql;
    Statement stat;
    //
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( false );
    }
    LOGGER.log( Level.FINE, "try to add alias..." );
    sql = String.format( "insert into %s (%s, %s) values ('%s', '%s')", ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME, ProjectConst.A_ALIAS, dev, alias );
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      conn.commit();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to insert device alias for device <%s> (%s)", dev, ex.getLocalizedMessage() ) );
      return( false );
    }
    return( true );
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

  @Override
  public String[][] getAliasData()
  {
    String sql;
    String[][] aliasData;
    Statement stat;
    ResultSet rs;
    String devName, aliasName;
    int rows = 0, cnt = 0;
    //
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    try
    {
      LOGGER.log( Level.FINE, "try to read aliases..." );
      stat = conn.createStatement();
      //
      // Wie viele Einträge
      //
      sql = String.format( "select count(*) from %s", ProjectConst.A_DBALIAS );
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        rows = rs.getInt( 1 );
        LOGGER.log( Level.FINE, String.format( "Aliases in database: %d", rows ) );
      }
      rs.close();
      if( rows == 0 )
      {
        return( null );
      }
      // Erzeuge das Array für die Tabelle
      aliasData = new String[rows][2];
      //
      // Gib her die Einträge, wenn welche vorhanden sind
      //
      sql = String.format( "select %s,%s from %s order by %s;", ProjectConst.A_DEVNAME, ProjectConst.A_ALIAS, ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME );
      rs = stat.executeQuery( sql );
      cnt = 0;
      while( rs.next() )
      {
        devName = rs.getString( 1 );
        aliasName = rs.getString( 2 );
        aliasData[cnt][0] = devName;
        aliasData[cnt][1] = aliasName;
        cnt++;
        LOGGER.log( Level.FINE, String.format( "Read:%s::%s", devName, aliasName ) );
      }
      rs.close();
      stat.close();
      return( aliasData );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to read device alias for devices (%s)", ex.getLocalizedMessage() ) );
    }
    return( null );
  }

  @Override
  public String getAliasForName( final String devName )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String aliasName = null;
    //
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    LOGGER.log( Level.FINE, "try to read aliases..." );
    sql = String.format( "select %s from %s where %s like '%s'", ProjectConst.A_ALIAS, ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME, devName );
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        aliasName = rs.getString( 1 );
        LOGGER.log( Level.FINE, String.format( "Alias for device %s : %s", devName, aliasName ) );
      }
      rs.close();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to read device alias for device %s (%s)", devName, ex.getLocalizedMessage() ) );
      LOGGER.log( Level.SEVERE, sql );
    }
    return( aliasName );
  }

  @Override
  public String getNameForAlias( final String aliasName )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String deviceName = null;
    //
    //
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    LOGGER.log( Level.FINE, "try to read device name for alias..." );
    sql = String.format( "select %s from %s where %s like '%s';", ProjectConst.A_DEVNAME, ProjectConst.A_DBALIAS, ProjectConst.A_ALIAS, aliasName );
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        deviceName = rs.getString( 1 );
        LOGGER.log( Level.FINE, String.format( "device name for alias %s : %s", aliasName, deviceName ) );
      }
      rs.close();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to read device name for alias %s (%s)", aliasName, ex.getLocalizedMessage() ) );
    }
    return( deviceName );
  }

  @Override
  public String getPinForDevice( final String deviceName )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String pin = null;
    //
    //
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    LOGGER.log( Level.FINE, "try to read pin for device..." );
    sql = String.format( "select %s from %s where %s like '%s';", ProjectConst.A_PIN, ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME, deviceName );
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        pin = rs.getString( 1 );
        LOGGER.log( Level.FINE, String.format( "pin for device %s : %s", deviceName, pin ) );
      }
      rs.close();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to read pin for device %s (%s)", deviceName, ex.getLocalizedMessage() ) );
    }
    return( pin );
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

  /**
   * Version der Datenbank lesen
   * 
   * @author Dirk Marciniak 02.05.2012 void
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
  public String[] readDevicesFromDatabase()
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String[] results;
    Vector<String> sammel = new Vector<String>();
    //
    LOGGER.log( Level.FINE, "read devices from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select distinct %s from %s;",
            ProjectConst.A_ALIAS,
            ProjectConst.A_DBALIAS
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      while( rs.next() )
      {
        sammel.add( rs.getString( 1 ) );
        LOGGER.log( Level.FINE, String.format( "database read device <%s>", rs.getString( 1 ) ) );
      }
      rs.close();
      // stelle die Liste der Geräte zusammen!
      results = new String[sammel.size()];
      results = sammel.toArray( results );
      return( results );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't read device list from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  @Override
  public boolean setPinForDevice( final String dev, final String pin )
  {
    String sql;
    Statement stat;
    //
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( false );
    }
    LOGGER.log( Level.FINE, "try to set pin for device..." );
    // jetzt kann ich die PIN einbauen, wenn datensatz schon vorhanden
    sql = String.format( "update %s set %s='%s' where %s like '%s'", ProjectConst.A_DBALIAS, ProjectConst.A_PIN, pin, ProjectConst.A_DEVNAME, dev );
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      stat.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to update pin for device <%s> (%s)", dev, ex.getLocalizedMessage() ) );
      return( false );
    }
    return( true );
  }

  @Override
  public boolean updateDeviceAlias( final String devName, final String devAlias )
  {
    String sql;
    Statement stat;
    //
    LOGGER.log( Level.FINE, "try to update alias..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "try to update alias even if database is not created! ABORT!" );
      return( false );
    }
    try
    {
      if( conn.isClosed() )
      {
        LOGGER.log( Level.WARNING, "try to update alias even if database is closed! ABORT!" );
        return( false );
      }
      // Ok, Datenbank da und geöffnet!
      stat = conn.createStatement();
      sql = String.format( "update %s set %s='%s' where %s like '%s';", ProjectConst.A_DBALIAS, ProjectConst.A_ALIAS, devAlias, ProjectConst.A_DEVNAME, devName );
      LOGGER.log( Level.FINE, String.format( "update device alias <%s> to <%s>", devName, devAlias ) );
      stat.execute( sql );
      stat.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to update device alias for device <%s> (%s)", devName, ex.getLocalizedMessage() ) );
      return( false );
    }
    return( true );
  }

  /**
   * 
   * Passe die Datenbank an von Version kleiner 3 auf 3
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.08.2012
   */
  private void _updateTableToVer3()
  {
    String sql;
    Statement stat;
    boolean rs;
    //
    LOGGER.log( Level.FINE, "update database version..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return;
    }
    //@formatter:off
    sql = String.format( 
            "insert into %s (%s) values ( '%d' );",
            ProjectConst.V_DBVERSION,
            ProjectConst.V_VERSION,
            3
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.execute( sql );
      if( rs )
      {
        LOGGER.log( Level.INFO, "Database updated." );
      }
      conn.commit();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't update dbversion <" + dbFile.getName() + "> (" + ex.getLocalizedMessage() + ")" );
      return;
    }
    return;
  }

  /**
   * 
   * Update zu Datenbankversion 4
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.08.2012
   */
  private void _updateTableToVer4()
  {
    String sql;
    Statement stat;
    boolean rs;
    //
    LOGGER.log( Level.FINE, "update database version..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return;
    }
    //@formatter:off
    sql = String.format( 
            "insert into %s (%s) values ( '%d' );",
            ProjectConst.V_DBVERSION,
            ProjectConst.V_VERSION,
            4
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.execute( sql );
      if( rs )
      {
        LOGGER.log( Level.INFO, "Database updated." );
      }
      conn.commit();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't update dbversion <" + dbFile.getName() + "> (" + ex.getLocalizedMessage() + ")" );
      return;
    }
    return;
  }
}
