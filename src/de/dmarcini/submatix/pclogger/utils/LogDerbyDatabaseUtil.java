package de.dmarcini.submatix.pclogger.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

public class LogDerbyDatabaseUtil
{
  public final static int           DELTATIME   = 0;
  public final static int           DEPTH       = 1;
  public final static int           TEMPERATURE = 2;
  public final static int           PPO2        = 3;
  public final static int           PPO2_01     = 4;
  public final static int           PPO2_02     = 5;
  public final static int           PPO2_03     = 6;
  public final static int           SETPOINT    = 7;
  public final static int           N2PERCENT   = 8;
  public final static int           HEPERCENT   = 9;
  public final static int           NULLTIME    = 10;
  public final static int           UNITSYSTEM  = 11;
  public final static int           PRESURE     = 12;
  public final static int           ACKU        = 13;
  private Logger                    LOGGER      = null;
  private File                      dbFolder    = null;
  private Connection                conn        = null;
  private final File                programDir  = new File( System.getProperty( "user.dir" ) );
  private final File                dataBase    = new File( programDir.getAbsolutePath() + File.separator + "spx42Log" );
  private final String              driver      = "org.apache.derby.jdbc.EmbeddedDriver";
  private ActionListener            aListener   = null;
  private Vector<LogLineDataObject> logDataList = null;
  private int                       currentDiveId;

  /**
   * 
   * Alternativer Konstruktor mit ActionListener
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param LOGGER
   * @param dbFolder
   * @param al
   */
  public LogDerbyDatabaseUtil( Logger LOGGER, File dbFolder, ActionListener al )
  {
    this.LOGGER = LOGGER;
    this.dbFolder = dbFolder;
    conn = null;
    aListener = al;
  }

  /**
   * 
   * Erzeuge alle Tabellen in der Datenbank
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbFl
   * @return
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  private Connection _createNewDatabase() throws SQLException, ClassNotFoundException
  {
    String sql;
    Statement stat;
    //
    LOGGER.log( Level.INFO, String.format( "create new database version:%d", ProjectConst.DB_VERSION ) );
    // ////////////////////////////////////////////////////////////////////////
    // Datentabellen erzeugen
    //
    stat = conn.createStatement();
    // ////////////////////////////////////////////////////////////////////////
    // Die Versionstabelle
    //@formatter:off
    sql = String.format( 
            "create table %s ( %s integer )",
            ProjectConst.V_DBVERSION,
            ProjectConst.V_VERSION
           );
    LOGGER.fine( String.format( "create table: %s", ProjectConst.V_DBVERSION ) );
    stat.execute(sql);
    // Versionsnummer reinschreiben
    sql = String.format( 
            "insert into %s ( %s ) values ( %d )",
            ProjectConst.V_DBVERSION,
            ProjectConst.V_VERSION,
            ProjectConst.DB_VERSION
           );
    LOGGER.fine( String.format( "write database version:%d", ProjectConst.DB_VERSION ) );
    stat.execute(sql);
    conn.commit();
    //@formatter:on
    //
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für Geräte (Tauchcompis mit aliasnamen und PIN)
    //@formatter:off
    sql = String.format( 
            "create table %s ( %s varchar(64) not null, %s varchar(64) not null, %s char(6) )",
            ProjectConst.A_DBALIAS,
            ProjectConst.A_DEVNAME,
            ProjectConst.A_ALIAS,
            ProjectConst.A_PIN
            );
    //@formatter:on
    LOGGER.fine( String.format( "create table: %s", ProjectConst.V_DBVERSION ) );
    stat.execute( sql );
    conn.commit();
    //
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für die Kopfdaten des Tauchganges
    //@formatter:off
    sql = String.format( 
            "create table %s \n" + 
            " ( \n" + 
            "   %s integer not null generated always as identity,\n" +
            "   %s integer not null,\n" +
            "   %s varchar(64) not null,\n" +
            "   %s varchar(64) not null,\n" +
            "   %s integer not null,\n" +
            "   %s integer,\n" +
            "   %s real,\n" +
            "   %s real,\n" +
            "   %s integer,\n" +
            "   %s integer,\n" +
            "   %s integer, \n" +
            "   %s integer, \n" +
            "   %s varchar(128) \n" + 
            " )",
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVEID,
            ProjectConst.H_DIVENUMBERONSPX,
            ProjectConst.H_FILEONSPX,
            ProjectConst.H_DEVICEID,
            ProjectConst.H_STARTTIME,
            ProjectConst.H_HADSEND,
            ProjectConst.H_FIRSTTEMP,
            ProjectConst.H_LOWTEMP,
            ProjectConst.H_MAXDEPTH,
            ProjectConst.H_SAMPLES,
            ProjectConst.H_DIVELENGTH,
            ProjectConst.H_UNITS,
            ProjectConst.H_NOTES
            );
    //@formatter:on
    LOGGER.fine( String.format( "create table: %s", ProjectConst.H_TABLE_DIVELOGS ) );
    stat.execute( sql );
    conn.commit();
    // Indize fuer die Tabelle erzeugen
    // index für Tauchnummer auf SPX
    //@formatter:off
    sql = String.format(
            "create index idx_%s_%s on %s ( %s ASC)",
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVENUMBERONSPX,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVENUMBERONSPX );
    //@formatter:off     
    LOGGER.fine( String.format( "create index on  table: %s", ProjectConst.H_TABLE_DIVELOGS ) );
    stat.execute( sql );
    conn.commit();
    //
    // index für Startzeit
    //@formatter:off
    sql = String.format(
            "create index idx_%s_%s on %s ( %s ASC)",
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_STARTTIME,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_STARTTIME );
    //@formatter:off     
    LOGGER.fine( String.format( "create index on  table: %s", ProjectConst.H_TABLE_DIVELOGS ) );
    stat.execute( sql );
    conn.commit();
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für die Logdaten
    //@formatter:off
    sql = String.format( 
            "create table %s\n" +
            " (\n" +
            "  %s integer not null generated always as identity, \n" + 
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
            "  %s integer,\n" +
            "  %s integer,\n" +
            "  %s real\n" +
            " )",  
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DBID,
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
            ProjectConst.D_DELTATIME,
            ProjectConst.D_PRESURE,
            ProjectConst.D_ACKU );
    //@formatter:off     
    LOGGER.fine( String.format( "create table: %s", ProjectConst.D_TABLE_DIVEDETAIL ) );
    stat.execute( sql );
    conn.commit();
    // Index fuer die Tabelle erzeugen
    //@formatter:off
    sql = String.format(
            "create index idx_%s_%s on %s ( %s ASC)",
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DIVEID,
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DIVEID );
    //@formatter:off     
    LOGGER.fine( String.format( "create index on  table: %s", ProjectConst.D_TABLE_DIVEDETAIL ) );
    stat.execute( sql );
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für die Gaspresets
    //@formatter:off
    sql = String.format( 
            "create table %s\n" +
            " (\n" +
            "  %s integer not null generated always as identity, \n" + 
            "  %s varchar(64) not null\n" +
            " )", 
            ProjectConst.P_TABLE_PRESETS,
            ProjectConst.P_DBID,
            ProjectConst.P_SETNAME
            );
    //@formatter:off     
    LOGGER.fine( String.format( "create table: %s", ProjectConst.P_TABLE_PRESETS ) );
    stat.execute( sql );
    conn.commit();
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für die Gaspreset Details
    //@formatter:off
    sql = String.format( 
            "create table %s\n" +
            " (\n" +
            "  %s integer not null generated always as identity, \n" + 
            "  %s integer not null, \n" + 
            "  %s integer not null, \n" + 
            "  %s integer not null, \n" + 
            "  %s integer not null, \n" + 
            "  %s boolean, \n" + 
            "  %s boolean, \n" + 
            "  %s boolean \n" + 
            " )", 
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_DBID,
            ProjectConst.PD_SETID,
            ProjectConst.PD_GASNR,
            ProjectConst.PD_O2,
            ProjectConst.PD_HE,
            ProjectConst.PD_DILUENT1,
            ProjectConst.PD_DILUENT2,
            ProjectConst.PD_BAILOUT
            );
    //@formatter:off     
    LOGGER.fine( String.format( "create table: %s", ProjectConst.PD_TABLE_PRESETDETAIL ) );
    stat.execute( sql );
    // Index fuer die Tabelle erzeugen
    //@formatter:off
    sql = String.format(
            "create index idx_%s_%s on %s ( %s ASC)",
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_SETID,
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_SETID );
    //@formatter:off     
    LOGGER.fine( String.format( "create index on  table: %s", ProjectConst.PD_TABLE_PRESETDETAIL ) );
    stat.execute( sql );
    conn.commit();
    //
    // eventuell noch mehr Tabellen
    //
    stat.close();
    conn.commit();
    return( conn );
  }

  /**
   * 
   * eine einzelne Tabelle entfernen, wenn vorhanden
   *
   * Project: SubmatixBTForPC
   * Package: de.dmarcini.submatix.pclogger.utils
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 05.09.2012
   * @param table
   */
  private void _dropTable( String table )
  {
    String sql;
    Statement stat;
    try
    {
      stat = conn.createStatement();
      //@formatter:off
      sql = String.format("drop table %s", table );
      LOGGER.fine( sql );
      stat.execute(sql);
      stat.close();
      //@formatter:on
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "can't create statement <" + ex.getLocalizedMessage() + ">" );
      return;
    }
  }

  /**
   * 
   * Tabellen von der Datenbank entfernen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @throws SQLException
   */
  private void _dropTablesFromDatabase() throws SQLException
  {
    if( checkForTable( ProjectConst.V_DBVERSION ) ) _dropTable( ProjectConst.V_DBVERSION );
    if( checkForTable( ProjectConst.A_DBALIAS ) ) _dropTable( ProjectConst.A_DBALIAS );
    if( checkForTable( ProjectConst.H_TABLE_DIVELOGS ) ) _dropTable( ProjectConst.H_TABLE_DIVELOGS );
    if( checkForTable( ProjectConst.D_TABLE_DIVEDETAIL ) ) _dropTable( ProjectConst.D_TABLE_DIVEDETAIL );
  }

  private void _updateDatabaseToVersion5() throws SQLException
  {
    String sql;
    Statement stat;
    //
    LOGGER.log( Level.INFO, String.format( "create new database version:%d", ProjectConst.DB_VERSION ) );
    // ////////////////////////////////////////////////////////////////////////
    // Datentabellen ergänzen
    //
    stat = conn.createStatement();
    // ////////////////////////////////////////////////////////////////////////
    // Die Versionstabelle updaten
    // Versionsnummer reinschreiben
    sql = String.format( "insert into %s ( %s ) values ( %d )", ProjectConst.V_DBVERSION, ProjectConst.V_VERSION, ProjectConst.DB_VERSION );
    LOGGER.fine( String.format( "write database version:%d", ProjectConst.DB_VERSION ) );
    stat.execute( sql );
    conn.commit();
    // @formatter:on
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für die Gaspresets
    //@formatter:off
    sql = String.format( 
            "create table %s\n" +
            " (\n" +
            "  %s integer not null generated always as identity, \n" + 
            "  %s varchar(64) not null\n" +
            " )", 
            ProjectConst.P_TABLE_PRESETS,
            ProjectConst.P_DBID,
            ProjectConst.P_SETNAME
            );
    //@formatter:off     
    LOGGER.fine( String.format( "create table: %s", ProjectConst.P_TABLE_PRESETS ) );
    stat.execute( sql );
    conn.commit();
    // ////////////////////////////////////////////////////////////////////////
    // Die Tabelle für die Gaspreset Details
    //@formatter:off
    sql = String.format( 
            "create table %s\n" +
            " (\n" +
            "  %s integer not null generated always as identity, \n" + 
            "  %s integer not null, \n" + 
            "  %s integer not null, \n" + 
            "  %s integer not null, \n" + 
            "  %s integer not null, \n" + 
            "  %s boolean, \n" + 
            "  %s boolean, \n" + 
            "  %s boolean \n" + 
            " )", 
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_DBID,
            ProjectConst.PD_SETID,
            ProjectConst.PD_GASNR,
            ProjectConst.PD_O2,
            ProjectConst.PD_HE,
            ProjectConst.PD_DILUENT1,
            ProjectConst.PD_DILUENT2,
            ProjectConst.PD_BAILOUT
            );
    //@formatter:off     
    LOGGER.fine( String.format( "create table: %s", ProjectConst.PD_TABLE_PRESETDETAIL ) );
    stat.execute( sql );
    // Index fuer die Tabelle erzeugen
    //@formatter:off
    sql = String.format(
            "create index idx_%s_%s on %s ( %s ASC)",
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_SETID,
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_SETID );
    //@formatter:off     
    LOGGER.fine( String.format( "create index on  table: %s", ProjectConst.PD_TABLE_PRESETDETAIL ) );
    stat.execute( sql );
    conn.commit();
    //
    // eventuell noch mehr Tabellen
    //
    stat.close();
    conn.commit();
  }

  /**
   * 
   * Einen neuen Alias in die DB aufnehmen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dev
   * @param alias
   * @return hat geklappt?
   */
  public boolean addAliasForNameConn( final String dev, final String alias )
  {
    String sql;
    Statement stat;
    //
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( false );
    }
    LOGGER.fine( "try to add alias..." );
    sql = String.format( "insert into %s (%s, %s) values ('%s', '%s')", ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME, ProjectConst.A_ALIAS, dev, alias );
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      stat.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( String.format( "fail to insert device alias for device <%s> (%s)", dev, ex.getLocalizedMessage() ) );
      return( false );
    }
    return( true );
  }

  /**
   * 
   * Cache für Logzeilen allocieren
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param diveId
   * @return ok oder nicht
   */
  public int allocateCacheLog( int diveId )
  {
    // immer eine neue anlegen, löscht durch garbage collector auch eventuell vorhandene alte Liste
    LOGGER.fine( "allocate new cache for update dive <" + diveId + ">..." );
    logDataList = new Vector<LogLineDataObject>();
    // aktuelle Id setzen
    currentDiveId = diveId;
    if( logDataList != null )
    {
      return( 1 );
    }
    LOGGER.fine( "allocate new cache for update dive <" + diveId + ">...OK" );
    return( 0 );
  }

  /**
   * 
   * Füge eine Logzeile an den Cache an
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param diveId
   * @param logLineObj
   * @return ok oder nicht
   */
  public int appendLogToCacheLog( int diveId, LogLineDataObject logLineObj )
  {
    if( logDataList == null )
    {
      LOGGER.severe( "no logDataList for caching allocated! ABORT" );
      return( -1 );
    }
    if( currentDiveId == -1 || currentDiveId != diveId )
    {
      LOGGER.severe( "diveid for this logline is not correct in this situation! ABORT" );
      return( -1 );
    }
    logDataList.add( logLineObj );
    LOGGER.fine( "line dataset cached..." );
    return( 1 );
  }

  /**
   * 
   * Teste, ob die Versionstabelle vorhanden ist
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param cn
   * @param table
   * @return tabelle existiert?
   * @throws SQLException
   */
  private boolean checkForTable( String table ) throws SQLException
  {
    Statement s = null;
    try
    {
      s = conn.createStatement();
      // es wird IMMER eine Exception ausgelöst ;-)
      String sql = String.format( "select * from %s where 1=2", table );
      s.execute( sql );
    }
    catch( SQLException sqle )
    {
      String theError = ( sqle ).getSQLState();
      /** If table exists will get - WARNING 02000: No row was found **/
      if( theError.equals( "42X05" ) ) // Table does not exist
      {
        LOGGER.fine( "table <" + table + "> was not found." );
        return false;
      }
      else if( theError.equals( "42X14" ) || theError.equals( "42821" ) )
      {
        LOGGER.severe( "incorect table definition. create new tables!" );
        throw sqle;
      }
      else
      {
        LOGGER.severe( "unhandled exception < " + sqle.getLocalizedMessage() + "> !" );
        throw sqle;
      }
    }
    finally
    {
      if( s != null ) s.close();
    }
    LOGGER.fine( "Table <" + table + "> exists! " );
    return true;
  }

  /**
   * 
   * Datenbank schliessen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   */
  public void closeDB()
  {
    LOGGER.fine( "try close database..." );
    if( conn != null )
    {
      try
      {
        if( !conn.isClosed() )
        {
          conn.commit();
          LOGGER.fine( "close database..." );
          conn.close();
          conn = null;
        }
      }
      catch( SQLException ex )
      {
        LOGGER.severe( "Can't close Database (" + ex.getLocalizedMessage() + ")" );
        return;
      }
    }
    LOGGER.fine( "close database...OK" );
  }

  /**
   * 
   * Versuche eine Verbindung zur (embedded) Datenbank
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @return Connection-Objekt
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public Connection createConnection() throws SQLException, ClassNotFoundException
  {
    String connectionURL = null;
    int dbVersion = 0;
    //
    if( dbFolder == null )
    {
      LOGGER.severe( "no database folder selected!" );
      return( null );
    }
    if( !dbFolder.isDirectory() )
    {
      LOGGER.severe( dbFolder.getName() + " is not a folder! (" + dbFolder.getAbsolutePath() + ")" );
      return( null );
    }
    //
    // versuche den Datenbanktreiber zu laden
    //
    try
    {
      Class.forName( driver );
    }
    catch( java.lang.ClassNotFoundException ex )
    {
      LOGGER.severe( "can't locaize database driver! Inform programmer!" );
      return( null );
    }
    //
    // Verbindungsbeschreibung
    //
    connectionURL = "jdbc:derby:" + dbFolder.getAbsolutePath() + File.separator + dataBase.getName() + ";create=true";
    //
    // versuche eine Verbindung zur Datenbank (embedded)
    //
    try
    {
      conn = DriverManager.getConnection( connectionURL );
      conn.setAutoCommit( false );
    }
    catch( Throwable ex )
    {
      LOGGER.severe( "no connection to database <" + ex.getLocalizedMessage() + ">" );
      conn = null;
      return( null );
    }
    //
    // finde heraus, welche Datenbankversion vorliegt
    // 0 == keine Tabellen vorhanden
    //
    dbVersion = readDatabaseVersion();
    switch ( dbVersion )
    {
      case 0:
        // Datenbank nagelneu initialisieren
        _createNewDatabase();
        return( conn );
      case 1:
      case 2:
      case 3:
        _dropTablesFromDatabase();
        // Datenbank nagelneu initialisieren
        _createNewDatabase();
        return( conn );
      case 4:
        // Da muss ich was machen
        _updateDatabaseToVersion5();
        return( conn );
      case 5:
        // das ist momentan aktuell
        return( conn );
      default:
        LOGGER.severe( "database version found was to high for this version!" );
        conn.close();
        conn = null;
        return( null );
    }
  }

  /**
   * 
   * Alle Daten für eine Ids löschen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbIds
   */
  public void deleteAllSetsForIdsLog( int[] dbIds )
  {
    String sql;
    Statement stat;
    StringBuilder out = new StringBuilder();
    //
    if( dbIds.length == 0 || conn == null )
    {
      return;
    }
    // die Datenbankids zusammenflicken
    // erste ID rein
    out.append( String.format( "%d", dbIds[0] ) );
    // restliche Ids, wenn vorhanden
    for( int x = 1; x < dbIds.length; x++ )
    {
      out.append( String.format( ", %d", dbIds[x] ) );
    }
    LOGGER.fine( "delete dbIds: " + out.toString() + " from database..." );
    //
    // zuerst die logdaten entfernen
    //
    //@formatter:off
    sql = String.format( 
            "delete from %s\n" +
            " where %s in (%s)"
            ,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVEID,
            out.toString()
            );
    //@formatter:on 
    //
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      stat.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "fatal error in delete dataset: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
    }
    //
    // jetzt die Kopfdaten entfernen
    //
    //@formatter:off
    sql = String.format( 
            "delete from %s\n" +
            " where %s in (%s)"
            ,
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DIVEID,
            out.toString()
            );
    //@formatter:on 
    //
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      stat.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "fatal error in delete dataset: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
    }
  }

  /**
   * 
   * aktuell bearbeitetes Log entfernen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @return gelöscht oder nicht
   */
  public int deleteLogFromDatabeaseLog()
  {
    String sql;
    Statement stat;
    //
    if( currentDiveId == -1 )
    {
      // das war nix...
      return( 0 );
    }
    //
    // entferne Headerdaten
    //
    //@formatter:off
    sql = String.format( 
            "delete from %s\n" +
            " where %s=%d"
            ,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVEID,
            currentDiveId
            );
    //@formatter:on 
    //
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "fatal error in delete dataset: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
    }
    currentDiveId = -1;
    if( logDataList != null )
    {
      logDataList.clear();
      logDataList = null;
    }
    return( 1 );
  }

  /**
   * 
   * Alias-Daten für Geräte zurückgeben Name/Alias
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @return Array mit Aliaseinträgen
   */
  public Vector<String[]> getAliasDataConn()
  {
    String sql;
    Vector<String[]> aliasData;
    Statement stat;
    ResultSet rs;
    //
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    try
    {
      LOGGER.fine( "try to read aliases..." );
      stat = conn.createStatement();
      // Erzeuge das Array für die Tabelle
      aliasData = new Vector<String[]>();
      //
      // Gib her die Einträge, wenn welche vorhanden sind
      //
      sql = String.format( "select %s,%s,%s from %s order by %s", ProjectConst.A_DEVNAME, ProjectConst.A_ALIAS, ProjectConst.A_PIN, ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME );
      rs = stat.executeQuery( sql );
      while( rs.next() )
      {
        String[] entr = new String[4];
        entr[0] = rs.getString( 1 ); // Device-ID
        entr[1] = rs.getString( 2 ); // Device Alias
        entr[2] = "";
        entr[3] = rs.getString( 3 ); // PIN
        aliasData.add( entr );
        LOGGER.fine( String.format( "Read:%s::%s::%s::%s", entr[0], entr[1], entr[2], entr[3] ) );
      }
      rs.close();
      stat.close();
      return( aliasData );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( String.format( "fail to read device alias for devices (%s)", ex.getLocalizedMessage() ) );
    }
    return( null );
  }

  /**
   * 
   * Suche einen Alias für einen Namen raus
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param devName
   * @return Alias
   */
  public String getAliasForNameConn( final String devName )
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
    LOGGER.fine( "try to read aliases..." );
    sql = String.format( "select %s from %s where %s like '%s'", ProjectConst.A_ALIAS, ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME, devName );
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        aliasName = rs.getString( 1 );
        LOGGER.fine( String.format( "Alias for device %s : %s", devName, aliasName ) );
      }
      rs.close();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( String.format( "fail to read device alias for device %s (%s)", devName, ex.getLocalizedMessage() ) );
      LOGGER.severe( sql );
    }
    return( aliasName );
  }

  /**
   * 
   * Lese die Id des Devices aus für einen Tauchgang
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbId
   * @return Geräteid
   */
  public String getDeviceIdLog( int dbId )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String dbIdString = null;
    //
    // baue einen String mit dbId für die Datenbank
    //
    LOGGER.fine( "read device id from db for <" + dbId + ">" );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s from %s where %s=%s",
            ProjectConst.H_DEVICEID,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVEID,
            dbId);
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        dbIdString = rs.getString( 1 );
      }
      rs.close();
      return( dbIdString );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read deviceId from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Erfrage Daten von einem Tauchgang mit der DBID
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbId
   * @return Array mit Logdaten
   */
  public Vector<Integer[]> getDiveDataFromIdLog( int dbId )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    Vector<Integer[]> diveData = new Vector<Integer[]>();
    //
    diveData.clear();
    LOGGER.fine( "read logdata for dbId <" + dbId + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s where %s=%d",
            ProjectConst.D_DELTATIME,
            ProjectConst.D_DEPTH,
            ProjectConst.D_TEMPERATURE,
            ProjectConst.D_PPO,
            ProjectConst.D_PPO_1,
            ProjectConst.D_PPO_2,
            ProjectConst.D_PPO_3,
            ProjectConst.D_SETPOINT,
            ProjectConst.D_HE,
            ProjectConst.D_N2,
            ProjectConst.D_NULLTIME,
            ProjectConst.D_PRESURE,
            ProjectConst.D_ACKU,
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DIVEID,
            dbId
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      while( rs.next() )
      {
        // Daten kosolidieren
        Integer[] resultSet = new Integer[14];
        resultSet[DELTATIME] = rs.getInt( 1 );
        resultSet[DEPTH] = rs.getInt( 2 );
        resultSet[TEMPERATURE] = rs.getInt( 3 );
        resultSet[PPO2] = ( int )( rs.getDouble( 4 ) * 1000.0 );
        resultSet[PPO2_01] = ( int )( rs.getDouble( 5 ) * 1000.0 );
        resultSet[PPO2_02] = ( int )( rs.getDouble( 6 ) * 1000.0 );
        resultSet[PPO2_03] = ( int )( rs.getDouble( 7 ) * 1000.0 );
        resultSet[SETPOINT] = rs.getInt( 8 );
        resultSet[HEPERCENT] = rs.getInt( 9 );
        resultSet[N2PERCENT] = rs.getInt( 10 );
        resultSet[NULLTIME] = rs.getInt( 11 );
        resultSet[PRESURE] = rs.getInt( 12 );
        resultSet[ACKU] = ( int )( rs.getDouble( 13 ) * 10 );
        // ab in den vector
        diveData.add( resultSet );
      }
      rs.close();
      LOGGER.fine( "read logdata for dbId <" + dbId + "> from DB...OK" );
      return( diveData );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read dive data from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Gib eine Liste von Tauchgängen für ein Gerät zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param device
   * @return array mit der Liste der Logs für ein Gerät
   */
  public Vector<String[]> getDiveListForDeviceLog( String device )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    Vector<String[]> results = new Vector<String[]>();
    //
    LOGGER.fine( "read divelist for device <" + device + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s,%s,%s from %s where %s like '%s' order by %s desc",
            ProjectConst.H_DIVEID,
            ProjectConst.H_DIVENUMBERONSPX,
            ProjectConst.H_STARTTIME,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DEVICEID,
            device,
            ProjectConst.H_DIVENUMBERONSPX
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      while( rs.next() )
      {
        // Daten kosolidieren
        String[] resultSet = new String[3];
        resultSet[0] = rs.getString( 1 ); // diveID
        resultSet[1] = rs.getString( 2 ); // Nummer auf dem SPX
        resultSet[2] = rs.getString( 3 ); // Anfangszeit
        // ab in den vector
        results.add( resultSet );
        LOGGER.fine( String.format( "database read dive nr <%s>", rs.getString( 1 ) ) );
      }
      rs.close();
      return( results );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read device list from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Gib eine Gasliste für einen Tauchgang zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbId
   * @return Array mit Gasliste für Tauchgang
   */
  public ArrayList<String> getGaslistForDiveLog( int dbId )
  {
    int[] dbIds = new int[1];
    dbIds[0] = dbId;
    return( getGaslistForDiveLog( dbIds ) );
  }

  /**
   * 
   * Gib eine gasliste für eine Anzahl Tauchgänge zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbIds
   * @return Array mit Gasliste für Tauchgänge
   */
  public ArrayList<String> getGaslistForDiveLog( int[] dbIds )
  {
    ArrayList<String> resultSet = new ArrayList<String>();
    String sql;
    Statement stat;
    ResultSet rs;
    String dbIdString = "";
    double n2, he, o2;
    boolean isFirst = true;
    //
    // baue einen String mit dbId für die Datenbank
    //
    for( int dbId : dbIds )
    {
      if( isFirst )
      {
        dbIdString += String.format( " %d", dbId );
        isFirst = false;
      }
      else
      {
        dbIdString += String.format( ", %d", dbId );
      }
    }
    LOGGER.fine( "read gaslist for dive(s) <" + dbIdString + ">" );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select distinct %s,%s from %s where %s in ( %s )",
            ProjectConst.D_N2,
            ProjectConst.D_HE,
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DIVEID,
            dbIdString );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      while( rs.next() )
      {
        // Daten kosolidieren
        // Stickstoff
        if( rs.getDouble( 1 ) == 0.0 )
        {
          n2 = 0.0;
        }
        else
        {
          n2 = rs.getDouble( 1 ) / 100.0;
        }
        // Helium
        if( rs.getDouble( 2 ) == 0.0 )
        {
          he = 0.0;
        }
        else
        {
          he = rs.getDouble( 2 ) / 100.0;
        }
        // Sauerstoff
        o2 = 1.0 - ( n2 + he );
        // Der vollständige Gasname
        String entry = String.format( Locale.ENGLISH, "%.3f:%.3f:%.3f:%.3f:%.3f", o2, n2, he, 0.0, 0.0 );
        resultSet.add( entry );
      }
      rs.close();
      return( resultSet );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read gaslist list from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Kopfdaten als Strings für eine Id zurückgeben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbId
   * @return Array von Strings
   */
  public String[] getHeadDiveDataFromIdAsSTringLog( int dbId )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String[] diveHeadData = null;
    //
    LOGGER.fine( "read head data for spx dive number <" + dbId + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format(
            "select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s where %s=%d",
            ProjectConst.H_DIVEID,
            ProjectConst.H_DIVENUMBERONSPX,
            ProjectConst.H_FILEONSPX,
            ProjectConst.H_DEVICEID,
            ProjectConst.H_STARTTIME,
            ProjectConst.H_HADSEND,
            ProjectConst.H_FIRSTTEMP,
            ProjectConst.H_LOWTEMP,
            ProjectConst.H_MAXDEPTH,
            ProjectConst.H_SAMPLES,
            ProjectConst.H_DIVELENGTH,
            ProjectConst.H_UNITS,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVEID,
            dbId
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        diveHeadData = new String[12];
        // Daten kosolidieren
        diveHeadData[0] = rs.getString( 1 );
        diveHeadData[1] = rs.getString( 2 );
        diveHeadData[2] = rs.getString( 3 );
        diveHeadData[3] = rs.getString( 4 );
        diveHeadData[4] = rs.getString( 5 );
        diveHeadData[5] = rs.getString( 6 );
        diveHeadData[6] = rs.getString( 7 );
        diveHeadData[7] = rs.getString( 8 );
        diveHeadData[8] = String.format( "%-3.1f", ( rs.getDouble( 9 ) / 10.0 ) ); // Tiefe
        diveHeadData[9] = rs.getString( 10 );
        // Minuten/Sekunden ausrechnen
        int minutes = rs.getInt( 11 ) / 60;
        int secounds = rs.getInt( 11 ) % 60;
        diveHeadData[10] = String.format( "%d:%02d", minutes, secounds );
        if( rs.getInt( 12 ) == ProjectConst.UNITS_IMPERIAL )
        {
          diveHeadData[11] = "IMPERIAL"; // Einheiten
        }
        else
        {
          diveHeadData[11] = "METRIC"; // Einheiten
        }
      }
      rs.close();
      LOGGER.fine( "read head data for spx dive number <" + dbId + "> from DB...OK" );
      return( diveHeadData );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read dive head data from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Gib Kopfdaten für einen Tauchgang zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbId
   * @return Kopfdaten für einenn Tauchgang
   */
  public int[] getHeadDiveDataFromIdLog( int dbId )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    int[] diveHeadData;
    //
    LOGGER.fine( "read head data for database id <" + dbId + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s,%s,%s,%s,%s,%s,%s from %s where %s=%d",
            ProjectConst.H_STARTTIME,
            ProjectConst.H_FIRSTTEMP,
            ProjectConst.H_LOWTEMP,
            ProjectConst.H_MAXDEPTH,
            ProjectConst.H_SAMPLES,
            ProjectConst.H_DIVELENGTH,
            ProjectConst.H_UNITS,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVEID,
            dbId
           );
    //@formatter:on
    try
    {
      diveHeadData = new int[7];
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        // Daten kosolidieren
        diveHeadData[0] = rs.getInt( 1 ); // starttime
        diveHeadData[1] = ( int )( rs.getDouble( 2 ) * 10.0 ); // firsttemp
        diveHeadData[2] = ( int )( rs.getDouble( 3 ) * 10.0 ); // lowtemp
        diveHeadData[3] = rs.getInt( 4 ); // maxdepth
        diveHeadData[4] = rs.getInt( 5 ); // samples
        diveHeadData[5] = rs.getInt( 6 ); // length
        diveHeadData[6] = rs.getInt( 7 ); // units
      }
      rs.close();
      LOGGER.fine( "read head data for database id <" + dbId + "> from DB...OK" );
      return( diveHeadData );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read dive head data from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Gib Bemerkungen für eine ID zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbId
   * @return Bemerkungen
   */
  public String getNotesForIdLog( int dbId )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String notesForDive = null;
    //
    LOGGER.fine( "read notes for dive <" + dbId + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s from %s where %s=%d",
            ProjectConst.H_NOTES,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVEID,
            dbId
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        // Daten kosolidieren
        notesForDive = rs.getString( 1 ); // die Bemerkungen
      }
      rs.close();
      return( notesForDive );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read notes from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Gib die PIN für ein Gerät zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param deviceName
   * @return PIN oder null
   */
  public String getPinForDeviceConn( final String deviceName )
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
    LOGGER.fine( "try to read pin for device..." );
    sql = String.format( "select %s from %s where %s like '%s'", ProjectConst.A_PIN, ProjectConst.A_DBALIAS, ProjectConst.A_DEVNAME, deviceName );
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        pin = rs.getString( 1 );
        LOGGER.fine( String.format( "pin for device %s : %s", deviceName, pin ) );
      }
      rs.close();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( String.format( "fail to read pin for device %s (%s)", deviceName, ex.getLocalizedMessage() ) );
    }
    return( pin );
  }

  /**
   * 
   * Gib einen Vector mit Presets aus der Datenbank zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 10.09.2012
   * @return Menge von DAten
   */
  public Vector<GasPresetComboObject> getPresets()
  {
    String sql;
    Statement stat;
    ResultSet rs;
    Vector<GasPresetComboObject> presets = new Vector<GasPresetComboObject>();
    GasPresetComboObject gasset = null;
    //
    LOGGER.fine( "read gas presets from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format(
            "select %s,%s from %s order by %s",
            ProjectConst.P_DBID,
            ProjectConst.P_SETNAME,
            ProjectConst.P_TABLE_PRESETS,
            ProjectConst.P_DBID
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      while( rs.next() )
      {
        gasset = new GasPresetComboObject( rs.getString( 2 ), rs.getInt( 1 ) );
        presets.add( gasset );
      }
      rs.close();
      LOGGER.fine( "read gas presets from DB...OK" );
      return( presets );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read dive head data from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Ist der Tauchgang mitr dem Namen (vom SPX) gesichert?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param filename
   *          Dateiname auf Gerät
   * @param device
   *          Gerät
   * @return Log schon gespeichert?
   */
  public int isLogSavedLog( String filename, String device )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    int dbId = -1;
    //
    LOGGER.fine( "was log <" + filename + "> always saved?" );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( -1 );
    }
    //@formatter:off
    sql = String.format( 
            "select %s from %s where %s like '%s' and %s like '%s'",
            ProjectConst.H_DIVEID,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_FILEONSPX,
            filename,
            ProjectConst.H_DEVICEID,
            device
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        LOGGER.fine( String.format( "file <%s> was saved.", filename ) );
        dbId = rs.getInt( 1 );
        rs.close();
        stat.close();
        return( dbId );
      }
      LOGGER.fine( "log <" + filename + "> was not saved." );
      rs.close();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't select from database! (" + ex.getLocalizedMessage() + ")" );
      return( -1 );
    }
    return( -1 );
  }

  /**
   * 
   * Ist die Datenbank offen?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @return ist DB offen?
   */
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
      LOGGER.severe( String.format( "fail to check database ist opened (%s))", ex.getLocalizedMessage() ) );
    }
    return( false );
  }

  /**
   * 
   * Datenbankversion erfragen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @return 0 == nicht vorhanden, ansonstren DB-Version
   */
  private int readDatabaseVersion()
  {
    String sql;
    Statement stat;
    ResultSet rs;
    int version = 0;
    //
    LOGGER.fine( "read database version..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( 0 );
    }
    //
    // gibts die Tabelle überhaupt?
    //
    try
    {
      if( !checkForTable( ProjectConst.V_DBVERSION ) )
      {
        // Tabelle nicht da, erzeuge Datenbank neu
        return( 0 );
      }
    }
    catch( SQLException ex )
    {
      LOGGER.severe( ex.getLocalizedMessage() );
      return( 0 );
    }
    //
    // jetzt lies mal, welche Versionsnummer da ist
    //
    //@formatter:off
    sql = String.format( 
            "select max( %s ) from %s",
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
        LOGGER.fine( String.format( "database read version:%d", version ) );
        rs.close();
        return( version );
      }
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read dbversion (" + ex.getLocalizedMessage() + ")" );
      return( 0 );
    }
    return( 0 );
  }

  /**
   * 
   * Lese die geräte aus der Datenbank aus
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @return Geräteliste lesen
   */
  public String[] readDevicesFromDatabaseConn()
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String[] results;
    Vector<String> sammel = new Vector<String>();
    //
    LOGGER.fine( "read devices from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select distinct %s from %s",
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
        LOGGER.fine( String.format( "database read device <%s>", rs.getString( 1 ) ) );
      }
      rs.close();
      // stelle die Liste der Geräte zusammen!
      results = new String[sammel.size()];
      results = sammel.toArray( results );
      return( results );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read device list from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Entsorge Logdaten für einen Tauchgang
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param diveId
   * @return Sämtliche Logdaten für einen Tauchgang löschen
   */
  public int removeLogdataForIdLog( int diveId )
  {
    String sql;
    Statement stat;
    //
    if( diveId == -1 )
    {
      // das war nix...
      return( 0 );
    }
    LOGGER.fine( "remove logdatedata for dive (update) <" + diveId + ">..." );
    //
    // entferne Logdatenfür ID
    //
    //@formatter:off
    sql = String.format( 
            "delete from %s\n" +
            " where %s=%d"
            ,
            ProjectConst.D_TABLE_DIVEDETAIL,
            ProjectConst.D_DIVEID,
            diveId
            );
    //@formatter:on 
    //
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      stat.close();
      LOGGER.fine( "remove logdatedata for dive (update) <" + diveId + ">...OK" );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "fatal error in delete dataset: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
      return 0;
    }
    return 1;
  }

  /**
   * 
   * Neuen Datensatz in die DB schreiben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 10.09.2012
   * @param newPresetName
   * @param currGasList
   */
  public void saveNewPresetData( String newPresetName, SPX42GasList currGasList )
  {
    String sql;
    Statement stat = null;
    PreparedStatement prep = null;
    ResultSet rs;
    int setId = -1;
    //
    LOGGER.fine( "insert new gas preset in DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return;
    }
    //@formatter:off
    sql = String.format(
            "insert into %s ( %s ) values ( '%s' )",
            ProjectConst.P_TABLE_PRESETS,
            ProjectConst.P_SETNAME,
            newPresetName
           );
    //@formatter:on
    try
    {
      //
      // datensatz anlegen, Datenbankid holen
      stat = conn.createStatement();
      stat.execute( sql, Statement.RETURN_GENERATED_KEYS );
      rs = stat.getGeneratedKeys();
      if( rs.next() )
      {
        setId = rs.getInt( 1 );
      }
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "fatal error while insert: " + ex.getLocalizedMessage() );
      try
      {
        conn.rollback();
      }
      catch( SQLException ex1 )
      {
        // Doppelfehler
        ex1.printStackTrace();
        System.exit( -1 );
      }
      return;
    }
    //
    // Jetzt Gasliste in die DB schreiben
    //
    //@formatter:off
    sql = String.format(
            "insert into %s ( %s,%s,%s,%s,%s,%s,%s ) values ( ?,?,?,?,?,?,? )",
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_O2,
            ProjectConst.PD_HE,
            ProjectConst.PD_DILUENT1,
            ProjectConst.PD_DILUENT2,
            ProjectConst.PD_BAILOUT,
            ProjectConst.PD_SETID,
            ProjectConst.PD_GASNR
           );
    //@formatter:on
    try
    {
      prep = conn.prepareStatement( sql );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't insert gas preset dataset! (" + ex.getLocalizedMessage() + ")" );
      return;
    }
    // jetzt alle Sets in die DB
    for( int idx = 0; idx < currGasList.getGasCount(); idx++ )
    {
      try
      {
        prep.setInt( 1, currGasList.getO2FromGas( idx ) );
        prep.setInt( 2, currGasList.getHEFromGas( idx ) );
        prep.setBoolean( 3, ( currGasList.diluent1 == idx ) );
        prep.setBoolean( 4, ( currGasList.diluent2 == idx ) );
        prep.setBoolean( 5, ( currGasList.bailout[idx] > 0 ) );
        prep.setInt( 6, setId );
        prep.setInt( 7, idx );
        prep.addBatch();
      }
      catch( SQLException ex )
      {
        LOGGER.severe( "fatal error in sql prepare: " + ex.getLocalizedMessage() );
        ex.printStackTrace();
        if( aListener != null )
        {
          // die "das ging schief" Nachricht
          ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DB_FAIL, "addBatch" );
          aListener.actionPerformed( ev );
        }
        return;
      }
    }
    //
    // jetzt in die DB schreiben
    //
    try
    {
      prep.executeBatch();
      prep.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      try
      {
        conn.rollback();
      }
      catch( SQLException ex1 )
      {
        // Doppelfehler...LogForDeviceDatabaseUtil Programm hart beenden!
        LOGGER.severe( "fatal double error in batch execute: " + ex1.getLocalizedMessage() );
        LOGGER.severe( "ABORT PROGRAM!!!!!!!!!" );
        ex1.printStackTrace();
        System.exit( -1 );
      }
      LOGGER.severe( "fatal error in batch execute: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
      if( aListener != null )
      {
        // die "das ging schief" Nachricht
        ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DB_FAIL, "executeBatch" );
        aListener.actionPerformed( ev );
      }
      return;
    }
  }

  /**
   * 
   * Sichere Bemerkungen für einen Tauchgang
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dbId
   * @param notes
   * @return Gesichert?
   */
  public int saveNoteForIdLog( int dbId, String notes )
  {
    String sql;
    Statement stat;
    boolean rs;
    //
    LOGGER.fine( "update notes for dive dbid: " + dbId + "..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( -1 );
    }
    try
    {
      //@formatter:off
      sql = String.format( 
              "update %s set %s='%s' where %s=%d",
              ProjectConst.H_TABLE_DIVELOGS,
              ProjectConst.H_NOTES,
              notes,
              ProjectConst.H_DIVEID,
              dbId
             );
      //@formatter:on
      stat = conn.createStatement();
      rs = stat.execute( sql );
      if( rs )
      {
        LOGGER.log( Level.INFO, "Notes updated." );
      }
      conn.commit();
      stat.close();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't update dbversion (" + ex.getLocalizedMessage() + ")" );
      return( -1 );
    }
    return( dbId );
  }

  /**
   * 
   * Actionlisteneer setzen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param al
   */
  public void setActionListener( ActionListener al )
  {
    aListener = al;
  }

  /**
   * 
   * PIN für Gerät in DB eintragen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param dev
   * @param pin
   * @return Pin setzen erfolgreich?
   */
  public boolean setPinForDeviceConn( final String dev, final String pin )
  {
    String sql;
    Statement stat;
    //
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( false );
    }
    LOGGER.fine( "try to set pin for device..." );
    if( null == getAliasForNameConn( dev ) )
    {
      LOGGER.log( Level.WARNING, "no Aliasname for Device..." );
      return( false );
    }
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
      LOGGER.severe( String.format( "fail to update pin for device <%s> (%s)", dev, ex.getLocalizedMessage() ) );
      return( false );
    }
    return( true );
  }

  /**
   * 
   * Alias für ein Gerät eintragen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param devName
   * @param devAlias
   * @return Alias aktualisieren
   */
  public boolean updateDeviceAliasConn( final String devName, final String devAlias )
  {
    String sql;
    Statement stat;
    //
    LOGGER.fine( "try to update alias..." );
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
      sql = String.format( "update %s set %s='%s' where %s like '%s'", ProjectConst.A_DBALIAS, ProjectConst.A_ALIAS, devAlias, ProjectConst.A_DEVNAME, devName );
      LOGGER.fine( String.format( "update device alias <%s> to <%s>", devName, devAlias ) );
      stat.execute( sql );
      stat.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( String.format( "fail to update device alias for device <%s> (%s)", devName, ex.getLocalizedMessage() ) );
      return( false );
    }
    return( true );
  }

  /**
   * 
   * Daten für ein vorhandenes Preset von Gasen sichern
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 10.09.2012
   * @param dbId
   * @param currGasList
   */
  public void updatePresetData( int dbId, SPX42GasList currGasList )
  {
    String sql;
    PreparedStatement prep = null;
    //
    LOGGER.fine( "update gas presets in DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return;
    }
    //@formatter:off
    sql = String.format(
            "update %s set %s=?,%s=?,%s=?,%s=?,%s=? where %s=? and %s=?",
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_O2,
            ProjectConst.PD_HE,
            ProjectConst.PD_DILUENT1,
            ProjectConst.PD_DILUENT2,
            ProjectConst.PD_BAILOUT,
            ProjectConst.PD_SETID,
            ProjectConst.PD_GASNR
           );
    //@formatter:on
    try
    {
      prep = conn.prepareStatement( sql );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't update gas preset dataset! (" + ex.getLocalizedMessage() + ")" );
      return;
    }
    // jetzt alle Sets in die DB
    for( int idx = 0; idx < currGasList.getGasCount(); idx++ )
    {
      try
      {
        prep.setInt( 1, currGasList.getO2FromGas( idx ) );
        prep.setInt( 2, currGasList.getHEFromGas( idx ) );
        prep.setBoolean( 3, ( currGasList.diluent1 == idx ) );
        prep.setBoolean( 4, ( currGasList.diluent2 == idx ) );
        prep.setBoolean( 5, ( currGasList.bailout[idx] > 0 ) );
        prep.setInt( 6, dbId );
        prep.setInt( 7, idx );
        prep.addBatch();
      }
      catch( SQLException ex )
      {
        LOGGER.severe( "fatal error in sql prepare: " + ex.getLocalizedMessage() );
        ex.printStackTrace();
        if( aListener != null )
        {
          // die "das ging schief" Nachricht
          ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DB_FAIL, "addBatch" );
          aListener.actionPerformed( ev );
        }
        return;
      }
    }
    // jetzt in die DB schreiben
    try
    {
      prep.executeBatch();
      prep.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      try
      {
        conn.rollback();
      }
      catch( SQLException ex1 )
      {
        // Doppelfehler...LogForDeviceDatabaseUtil Programm hart beenden!
        LOGGER.severe( "fatal double error in batch execute: " + ex1.getLocalizedMessage() );
        LOGGER.severe( "ABORT PROGRAM!!!!!!!!!" );
        ex1.printStackTrace();
        System.exit( -1 );
      }
      LOGGER.severe( "fatal error in batch execute: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
      if( aListener != null )
      {
        // die "das ging schief" Nachricht
        ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DB_FAIL, "executeBatch" );
        aListener.actionPerformed( ev );
      }
      return;
    }
    LOGGER.fine( "update gas presets in DB..." );
  }

  /**
   * 
   * Daten mit neuem Namen und neuen Daten sichern
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 10.09.2012
   * @param dbId
   * @param newPresetName
   * @param currGasList
   */
  public void updatePresetData( int dbId, String newPresetName, SPX42GasList currGasList )
  {
    String sql;
    Statement stat = null;
    //
    LOGGER.fine( "update gas presets in DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return;
    }
    //@formatter:off
    sql = String.format(
            "update %s set %s=? where %s=?",
            ProjectConst.P_TABLE_PRESETS,
            ProjectConst.P_SETNAME,
            newPresetName,
            ProjectConst.P_DBID,
            dbId
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      stat.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "fatal error in execute: " + ex.getLocalizedMessage() );
      return;
    }
    updatePresetData( dbId, currGasList );
  }

  /**
   * 
   * Schreibe Daten vom Tauchgang in die Datenbank
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param diveId
   * @return Erfolgreich?
   */
  public int writeLogToDatabaseLog( final int diveId )
  {
    Thread writeDb;
    //
    if( logDataList == null )
    {
      LOGGER.severe( "no logDataList for write to databasde allocated! ABORT" );
      return( -1 );
    }
    if( currentDiveId == -1 || currentDiveId != diveId )
    {
      LOGGER.severe( "diveid for this chache is not correct in this situation! ABORT" );
      return( -1 );
    }
    // Thread dafür aufbauen
    writeDb = new Thread( "cache_to_db" ) {
      @Override
      public void run()
      {
        PreparedStatement prep = null;
        Statement stat = null;
        String sql;
        LogLineDataObject logLineObj;
        ResultSet rs = null;
        double markAirtemp = -999.99; // merke mir die Lufttemperatur (erster Wert der Temp => Luft...)
        double markLowestTemp = 100.0; // Merke mir die tiefste Temperatur
        long markMaxDepth = 0; // merke mir die Maximaltiefe
        long markSamples = 0;
        long markDiveLength = 0;
        //
        LOGGER.fine( "thread to write data in database is running..." );
        //
        //@formatter:off
         sql = String.format( 
                 "insert into %s\n" +
                 " (\n" + 
                 "  %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n" + 
                 " )\n" + 
                 " values\n" +
                 " (\n" +
                 "  ?,?,?,?,?,?,?,?,?,?,?,?,?,?\n" + 
                 " )" 
                 ,                    
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
                 ProjectConst.D_DELTATIME,
                 ProjectConst.D_PRESURE,
                 ProjectConst.D_ACKU);
         //@formatter:off 
         try
         {
           prep = conn.prepareStatement( sql );
         }
         catch( SQLException ex )
         {
           LOGGER.severe( "fatal error : " + ex.getLocalizedMessage() );
           ex.printStackTrace();
           if( aListener != null )
           {
             // die "das ging schief" Nachricht
             ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DB_FAIL, "prepareStatement" );
             aListener.actionPerformed( ev );
           }
           logDataList.clear();
           logDataList = null;
           return;
         }
         // 
         // Alle Zeilen sichern
         for (Enumeration<LogLineDataObject> enu=logDataList.elements(); enu.hasMoreElements(); ) 
         {
           logLineObj = enu.nextElement();
           try
           {
             prep.setInt( 1, diveId );
             prep.setInt( 2, logLineObj.depth );
             prep.setInt( 3, logLineObj.temperature );
             prep.setDouble( 4, logLineObj.ppo2 );
             prep.setDouble( 5, logLineObj.ppo2_1 );
             prep.setDouble( 6, logLineObj.ppo2_2 );
             prep.setDouble( 7, logLineObj.ppo2_3 );
             prep.setInt( 8, logLineObj.setpoint );
             prep.setInt( 9, logLineObj.n2 );
             prep.setInt( 10, logLineObj.he );
             prep.setInt( 11, logLineObj.zeroTime );
             prep.setInt( 12, logLineObj.nextStep );
             prep.addBatch();
             prep.executeBatch();
           }
           catch( SQLException ex )
           {
             LOGGER.severe( "fatal error in sql prepare: " + ex.getLocalizedMessage() );
             ex.printStackTrace();
             if( aListener != null )
             {
               // die "das ging schief" Nachricht
               ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DB_FAIL, "addBatch" );
               aListener.actionPerformed( ev );
             }
             logDataList.clear();
             logDataList = null;
             return;
           }
           //
           // Statistiken mitführen
           //
           markSamples++; // Anzahl der Einträge mitzählen
           markDiveLength += logLineObj.nextStep; // Länge des Tauchganges mitrechnen
           if( markAirtemp == -999.99 )
           {
             // Der erste Wert ist mal die Lufttemperatur (geschätzt)
             markAirtemp = logLineObj.temperature;
           }
           
//           // Tiefste Temperatur
//           if( markLowestTemp > logLineObj.temperature )
//           {
//             // ja, die Temperatur war tiefer
//             markLowestTemp = logLineObj.temperature;
//           }
//           // Maximale Tiefe
//           if( markMaxDepth < logLineObj.depth )
//           {
//             // setze die größere Tiefe
//             markMaxDepth = logLineObj.depth;
//           }
         }
         try
         {
           prep.close();
           conn.commit();
         }
         catch( SQLException ex )
         {
            try
            {
              conn.rollback();
            }
            catch( SQLException ex1 )
            {
              //Doppelfehler...LogForDeviceDatabaseUtil Programm hart beenden!
              LOGGER.severe( "fatal double error in batch execute: " + ex1.getLocalizedMessage() );
              LOGGER.severe( "ABORT PROGRAM!!!!!!!!!" );
              ex1.printStackTrace();
              System.exit( -1 );
            }
           LOGGER.severe( "fatal error in batch execute: " + ex.getLocalizedMessage() );
           ex.printStackTrace();
           if( aListener != null )
           {
             // die "das ging schief" Nachricht
             ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DB_FAIL, "executeBatch" );
             aListener.actionPerformed( ev );
           }
           logDataList.clear();
           logDataList = null;
           return;
         }
         LOGGER.fine( "cache was writing to database." );
         // aufräumen!
         logDataList.clear();
         logDataList = null;
         // 
         // Statistische Daten in der DB updaten
         //
         LOGGER.fine( "make statistics..." );
         //@formatter:off
         sql = String.format( 
                 Locale.ENGLISH,
                 "select max(%s),min(%s)\n" + 
                 " from %s where %s=%d",
                 ProjectConst.D_DEPTH,
                 ProjectConst.D_TEMPERATURE,
                 ProjectConst.D_TABLE_DIVEDETAIL,
                 ProjectConst.D_DIVEID,
                 diveId
                  );
         //@formatter:on
        try
        {
          stat = conn.createStatement();
          rs = stat.executeQuery( sql );
          if( rs.next() )
          {
            // Daten kosolidieren
            markMaxDepth = rs.getLong( 1 );
            markLowestTemp = rs.getDouble( 2 );
          }
          rs.close();
        }
        catch( SQLException ex )
        {
          LOGGER.severe( "Can't make dive statistic from db! (" + ex.getLocalizedMessage() + ")" );
          return;
        }
        //
        // Statistik in die Datenbank eintragen
        LOGGER.fine( "update statistics in database..." );
        //@formatter:off
         sql = String.format( 
                 Locale.ENGLISH,
                 "update %s \n" +
                 " set %s=%-3.1f, \n" + 
                 "     %s=%-3.2f, \n" + 
                 "     %s=%d, \n" + 
                 "     %s=%d, \n" + 
                 "     %s=%d \n" + 
                 " where %s=%d",                    
                 ProjectConst.H_TABLE_DIVELOGS,
                 ProjectConst.H_FIRSTTEMP,markAirtemp,
                 ProjectConst.H_LOWTEMP,markLowestTemp,
                 ProjectConst.H_MAXDEPTH,markMaxDepth,
                 ProjectConst.H_SAMPLES,markSamples,
                 ProjectConst.H_DIVELENGTH, markDiveLength,
                 ProjectConst.H_DIVEID,
                 diveId
                  );
         //@formatter:off 
         try
         {
           stat = conn.createStatement();
           stat.execute( sql );
           stat.close();
           conn.commit();
         }
         catch( SQLException ex )
         {
           LOGGER.severe( "fatal error in data update: " + ex.getLocalizedMessage() );
           LOGGER.fine( "SQL:" + sql );
           ex.printStackTrace();
           if( aListener != null )
           {
             // die "das ging schief" Nachricht
             ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DB_FAIL, "dataUpdate" );
             aListener.actionPerformed( ev );
           }
           logDataList.clear();
           logDataList = null;
           return;
         }
         if( aListener != null )
         {
           // die "das ging schief" Nachricht
           ActionEvent ev = new ActionEvent( this, ProjectConst.MESSAGE_DB_SUCCESS, null );
           aListener.actionPerformed( ev );
         }
         LOGGER.fine(  "thread to write data in database is OK: ending..." );
         return;
       };
    };
    LOGGER.fine(  "start thread to write data in database..." );
    writeDb.start();
    return 0;
  }

  /**
   * 
   * Schreibe einen neuen Tachgang in die Datenbank (Kopfdaten)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param deviceId
   * @param fileOnSPX
   * @param units
   * @param numberOnSPX
   * @param startTime
   * @return erfolgreich?
   */
  public int writeNewDiveLog( String deviceId, String fileOnSPX, int units, long numberOnSPX, long startTime )
  {
    Statement stat;
    String sql;
    ResultSet rs;
    int generatedKey;
    //
    LOGGER.fine( "create new diving entry..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( -1 );
    }
    // immer eine neue anlegen (einen Cache), löscht durch garbage collector auch eventuell vorhandene alte Liste
    logDataList = new Vector<LogLineDataObject>();
    //
    try
    {
      stat = conn.createStatement();
      LOGGER.fine( "insert new dataset into database..." );
      //@formatter:off
      sql = String.format( 
              "insert into %s ( %s,%s,%s,%s,%s ) values ( '%s','%s', %d, %d, %d )",
              ProjectConst.H_TABLE_DIVELOGS,
              ProjectConst.H_DEVICEID,
              ProjectConst.H_FILEONSPX,
              ProjectConst.H_UNITS,
              ProjectConst.H_DIVENUMBERONSPX,
              ProjectConst.H_STARTTIME,
              deviceId, 
              fileOnSPX,
              units,
              numberOnSPX,
              startTime
             );
      //@formatter:on
      LOGGER.fine( "write database... " );
      stat.execute( sql );
      conn.commit();
      //@formatter:off
      sql = String.format( 
              "select max(%s) from %s",
              ProjectConst.H_DIVEID,
              ProjectConst.H_TABLE_DIVELOGS
             );
      //@formatter:on
      LOGGER.fine( "read generated key... " );
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        generatedKey = rs.getInt( 1 );
        LOGGER.log( Level.INFO, String.format( "inserted dataset has diveId: <%d>...", generatedKey ) );
        rs.close();
        stat.close();
        currentDiveId = generatedKey;
        return( generatedKey );
      }
      rs.close();
      stat.close();
      return( -1 );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't insert into database! (" + ex.getLocalizedMessage() + ")" );
      return( -1 );
    }
  }

  /**
   * 
   * Gib ein Gascondi Objekt zurück für eine SetId
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 10.09.2012
   * @param setId
   * @return Gasliste für Preset-Id oder null
   */
  public SPX42GasList getPresetForSetId( int setId )
  {
    Statement stat;
    String sql;
    ResultSet rs;
    SPX42GasList gasList = new SPX42GasList( LOGGER );
    //
    LOGGER.fine( "read preselect for setid <" + setId + ">" );
    //@formatter:off
    sql = String.format( 
            "select %s,%s,%s,%s,%s,%s,%s from %s where %s=%s order by %s",
            ProjectConst.PD_O2,
            ProjectConst.PD_HE,
            ProjectConst.PD_DILUENT1,
            ProjectConst.PD_DILUENT2,
            ProjectConst.PD_BAILOUT,
            ProjectConst.PD_SETID,
            ProjectConst.PD_GASNR,
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_SETID,
            setId,
            ProjectConst.PD_GASNR
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      while( rs.next() )
      {
        int gasNr = rs.getInt( 7 );
        gasList.setGas( gasNr, rs.getInt( 1 ), rs.getInt( 2 ) );
        if( rs.getBoolean( 3 ) ) gasList.setDiluent1( gasNr );
        if( rs.getBoolean( 4 ) ) gasList.setDiluent2( gasNr );
        if( rs.getBoolean( 5 ) ) gasList.bailout[gasNr] = 1;
        LOGGER.fine( "gas number <" + gasNr + "> read..." );
      }
      rs.close();
      stat.close();
      gasList.setInitialized();
      return( gasList );
    }
    catch( SQLException ex )
    {
      LOGGER.severe( "Can't read preset (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  /**
   * 
   * Gas Preset entfernen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 23.09.2012
   * @param dbId
   */
  public void deleteGasPreset( int dbId )
  {
    String sql;
    Statement stat;
    //
    //
    // entferne Daten
    //
    //@formatter:off
    sql = String.format( 
            "delete from %s\n" +
            " where %s=%d"
            ,
            ProjectConst.P_TABLE_PRESETS,
            ProjectConst.P_DBID,
            dbId
            );
    //@formatter:on 
    //
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      stat.close();
    }
    catch( SQLException ex )
    {
      try
      {
        conn.rollback();
      }
      catch( SQLException ex1 )
      {}
      LOGGER.severe( "fatal error in delete dataset: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
    }
    //@formatter:off
    sql = String.format( 
            "delete from %s\n" +
            " where %s=%d"
            ,
            ProjectConst.PD_TABLE_PRESETDETAIL,
            ProjectConst.PD_SETID,
            dbId
            );
    //@formatter:on 
    //
    try
    {
      stat = conn.createStatement();
      stat.execute( sql );
      stat.close();
      conn.commit();
    }
    catch( SQLException ex )
    {
      try
      {
        conn.rollback();
      }
      catch( SQLException ex1 )
      {}
      LOGGER.severe( "fatal error in delete dataset: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
    }
  }
}
