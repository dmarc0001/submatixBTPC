package de.dmarcini.submatix.pclogger.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

//@formatter:off
public class DatabaseUtil implements IDatabaseUtil
{
  private Logger                             LOGGER = null;
  private File                               dbFile = null; 
  private SQLiteConnection                       db = null; 

  
//@formatter:on
  @SuppressWarnings( "unused" )
  private DatabaseUtil()
  {};

  /**
   * 
   * Konstruktor der Datenbank-Utilitys
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 23.04.2012
   * @param LOGGER
   * @param dbFileName
   */
  public DatabaseUtil( Logger LOGGER, String dbFileName )
  {
    this.LOGGER = LOGGER;
    dbFile = new File( dbFileName );
  }

  @Override
  public SQLiteConnection createConnection()
  {
    String sql;
    SQLiteStatement st;
    int version = 0;
    // ist die Verbindung schon offen, einfach das Teil zurückgeben
    if( db != null )
    {
      if( db.isOpen() )
      {
        return( db );
      }
    }
    // erzeuge eine Verbindung zur DB-Engine
    db = new SQLiteConnection( dbFile );
    try
    {
      // Datenbank öffnen, wenn File vorhanden
      db.open( false );
      LOGGER.log( Level.FINE, "database opened, read version..." );
      //@formatter:off
      sql = String.format( 
              "select max( %s ) from %s;",
              ProjectConst.V_VERSION,
              ProjectConst.V_DBVERSION
             );
      //@formatter:on
      st = db.prepare( sql );
      // st.bind( 0, version );
      // gibt es ein Ergebnis
      if( st.step() )
      {
        version = st.columnInt( 0 );
        LOGGER.log( Level.FINE, String.format( "database read version:%d", version ) );
      }
      if( version != ProjectConst.DB_VERSION )
      {
        // ACHTUNG, da hat sich was geändert!
        // ich muß mir was einfallen lassen
        _updateDatabaseVersion( version );
      }
    }
    catch( SQLiteException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't open/recreate Database <" + dbFile.getName() + "> (" + ex.getLocalizedMessage() + ")" );
      try
      {
        return( _createNewDatabase( dbFile ) );
      }
      catch( SQLiteException ex1 )
      {
        LOGGER.log( Level.SEVERE, "Can't create/open Database <" + dbFile.getName() + "> (" + ex1.getLocalizedMessage() + ")" );
        return( null );
      }
    }
    return( db );
  }

  @Override
  public SQLiteConnection createNewDatabase() throws SQLiteException
  {
    return( _createNewDatabase( dbFile ) );
  }

  @Override
  public SQLiteConnection createNewDatabase( String dbFileName ) throws SQLiteException
  {
    dbFile = new File( dbFileName );
    return( _createNewDatabase( dbFile ) );
  }

  /**
   * 
   * Interne Funktion zum erzeugen einer nagelneuen Datenbank
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 23.04.2012
   * @param dbFl
   *          Fileobjekt
   * @throws SQLiteException
   */
  private SQLiteConnection _createNewDatabase( File dbFl ) throws SQLiteException
  {
    String sql;
    //
    LOGGER.log( Level.INFO, String.format( "create new database version:%d", ProjectConst.DB_VERSION ) );
    dbFile = dbFl;
    // DB schliessen, wenn offen
    if( db != null )
    {
      if( db.isOpen() )
      {
        db.dispose();
      }
      db = null;
    }
    // Datendatei verschwinden lassen
    dbFile.delete();
    db = new SQLiteConnection( dbFile );
    try
    {
      // //////////////////////////////////////////////////////////////////////
      // Datenbank öffnen / erzeugen
      db.open( true );
    }
    catch( SQLiteException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't create Database <" + dbFile.getName() + "> (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
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
    db.exec( sql );
    // Versionsnummer reinschreiben
    sql = String.format( 
            "insert into %s ( %s ) values ( '%d' );",
            ProjectConst.V_DBVERSION,
            ProjectConst.V_VERSION,
            ProjectConst.DB_VERSION
           );
    LOGGER.log( Level.FINE, String.format( "write database version:%d", ProjectConst.DB_VERSION ) );
    db.exec( sql );
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
    db.exec( sql );
    // TODO: weitere Tabellen :-)
    return( db );
  }

  /**
   * 
   * Aus der DB alle Tabellen löschen...
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 24.04.2012
   * @throws SQLiteException
   */
  private void _dropTablesFromDatabase() throws SQLiteException
  {
    String sql;
    //@formatter:off
    sql = String.format( 
            "drop table %s;",
            ProjectConst.V_DBVERSION
           );
    LOGGER.log( Level.FINE, String.format( "drop table: %s", ProjectConst.V_DBVERSION ) );
    db.exec( sql );
    //@formatter:on
    //
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für Geräte (Tauchcompis mit aliasnamen und PIN)
    //@formatter:off
    sql = String.format( 
            "drop table %s;",
            ProjectConst.A_DBALIAS
            );
    //@formatter:on
    LOGGER.log( Level.FINE, String.format( "drop table: %s", ProjectConst.A_DBALIAS ) );
    db.exec( sql );
  }

  /**
   * 
   * Wenn sich die Versionsnummer der DB verändert hat, Datenbankinhalt/Struktur anpassen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 24.04.2012
   * @throws SQLiteException
   */
  private void _updateDatabaseVersion( int oldVersion ) throws SQLiteException
  {
    // erst mal vor dem Release: stumpf Tabellen löschen und neu anlegen
    LOGGER.log( Level.INFO, String.format( "create new database version:%d", ProjectConst.DB_VERSION ) );
    _dropTablesFromDatabase();
    _createNewDatabase( dbFile );
  }

  @Override
  public void closeDB()
  {
    LOGGER.log( Level.FINE, "close database..." );
    if( db != null )
    {
      db.dispose();
      db = null;
    }
    LOGGER.log( Level.FINE, "close database...OK" );
  }

  @Override
  public boolean updateDeviceAlias( final String devName, final String devAlias )
  {
    String sql;
    //
    LOGGER.log( Level.FINE, "try to update alias..." );
    if( db == null )
    {
      LOGGER.log( Level.WARNING, "try to update alias even if database is not created! ABORT!" );
      return( false );
    }
    if( !db.isOpen() )
    {
      LOGGER.log( Level.WARNING, "try to update alias even if database is closed! ABORT!" );
      return( false );
    }
    // Ok, Datenbank da und geöffnet!
    sql = String.format( "update %s set %s='%s' where %s like '%s';", ProjectConst.A_DBALIAS, ProjectConst.A_ALIAS, devAlias, ProjectConst.A_DEVNAME, devName );
    LOGGER.log( Level.FINE, String.format( "update device alias <%s> to <%s>", devName, devAlias ) );
    try
    {
      db.exec( sql );
    }
    catch( SQLiteException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to update device alias for device <%s> (%s)", devName, ex.getLocalizedMessage() ) );
      return( false );
    }
    return( true );
  }

  @Override
  public String[][] getAliasData()
  {
    String sql;
    String[][] aliasData;
    SQLiteStatement st;
    String devName, aliasName;
    int rows = 0, cnt = 0;
    //
    try
    {
      LOGGER.log( Level.FINE, "try to read aliases..." );
      //
      // Wie viele Einträge
      //
      sql = String.format( "select count(*) from %s", ProjectConst.A_DBALIAS );
      st = db.prepare( sql );
      if( st.step() )
      {
        rows = st.columnInt( 0 );
        LOGGER.log( Level.FINE, String.format( "Aliases in database: %d", rows ) );
      }
      st.dispose();
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
      st = db.prepare( sql );
      cnt = 0;
      while( st.step() )
      {
        devName = st.columnString( 0 );
        aliasName = st.columnString( 1 );
        aliasData[cnt][0] = devName;
        aliasData[cnt][1] = aliasName;
        cnt++;
        LOGGER.log( Level.FINE, String.format( "Read:%s::%s", devName, aliasName ) );
      }
      st.dispose();
      return( aliasData );
    }
    catch( SQLiteException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to read device alias for devices (%s)", ex.getLocalizedMessage() ) );
    }
    return( null );
  }

  @Override
  public String getAliasForName( final String devName )
  {
    String sql;
    SQLiteStatement st;
    String aliasName = null;
    //
    LOGGER.log( Level.FINE, "try to read aliases..." );
    sql = String.format( "select %s from %s where %s like '%s'", ProjectConst.A_ALIAS, ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME, devName );
    try
    {
      st = db.prepare( sql );
      if( st.step() )
      {
        aliasName = st.columnString( 0 );
        LOGGER.log( Level.FINE, String.format( "Alias for device %s : %s", devName, aliasName ) );
      }
      st.dispose();
    }
    catch( SQLiteException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to read device alias for device %s (%s)", devName, ex.getLocalizedMessage() ) );
      LOGGER.log( Level.SEVERE, sql );
    }
    return( aliasName );
  }

  @Override
  public boolean addAliasForName( final String dev, final String alias )
  {
    String sql;
    //
    LOGGER.log( Level.FINE, "try to add alias..." );
    sql = String.format( "insert into %s (%s, %s) values ('%s', '%s');", ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME, ProjectConst.A_ALIAS, dev, alias );
    try
    {
      db.exec( sql );
    }
    catch( SQLiteException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to insert device alias for device <%s> (%s)", dev, ex.getLocalizedMessage() ) );
      return( false );
    }
    return( true );
  }

  @Override
  public String getNameForAlias( final String aliasName )
  {
    String sql;
    SQLiteStatement st;
    String deviceName = null;
    //
    //
    LOGGER.log( Level.FINE, "try to read device name for alias..." );
    sql = String.format( "select %s from %s where %s like '%s';", ProjectConst.A_DEVNAME, ProjectConst.A_DBALIAS, ProjectConst.A_ALIAS, aliasName );
    try
    {
      st = db.prepare( sql );
      if( st.step() )
      {
        deviceName = st.columnString( 0 );
        LOGGER.log( Level.FINE, String.format( "device name for alias %s : %s", aliasName, deviceName ) );
      }
      st.dispose();
    }
    catch( SQLiteException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to read device name for alias %s (%s)", aliasName, ex.getLocalizedMessage() ) );
    }
    return( deviceName );
  }

  @Override
  public boolean setPinForDevice( final String dev, final String pin )
  {
    String sql;
    //
    LOGGER.log( Level.FINE, "try to set pin for device..." );
    sql = String.format( "update %s set %s='%s' where %s like '%s'", ProjectConst.A_DBALIAS, ProjectConst.A_PIN, pin, ProjectConst.A_DEVNAME, dev );
    try
    {
      db.exec( sql );
    }
    catch( SQLiteException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to update pin for device <%s> (%s)", dev, ex.getLocalizedMessage() ) );
      return( false );
    }
    return( true );
  }

  @Override
  public String getPinForDevice( final String deviceName )
  {
    String sql;
    SQLiteStatement st;
    String pin = null;
    //
    //
    LOGGER.log( Level.FINE, "try to read pin for device..." );
    sql = String.format( "select %s from %s where %s like '%s';", ProjectConst.A_PIN, ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME, deviceName );
    try
    {
      st = db.prepare( sql );
      if( st.step() )
      {
        pin = st.columnString( 0 );
        LOGGER.log( Level.FINE, String.format( "pin for device %s : %s", deviceName, pin ) );
      }
      st.dispose();
    }
    catch( SQLiteException ex )
    {
      LOGGER.log( Level.SEVERE, String.format( "fail to read pin for device %s (%s)", deviceName, ex.getLocalizedMessage() ) );
    }
    return( pin );
  }
}
