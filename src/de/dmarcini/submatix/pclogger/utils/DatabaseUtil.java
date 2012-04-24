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
}
