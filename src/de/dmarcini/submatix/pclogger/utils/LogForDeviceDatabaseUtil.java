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
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
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
 *         Stand: 06.05.2012
 */
public class LogForDeviceDatabaseUtil implements ILogForDeviceDatabaseUtil
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
  private Logger                    LOGGER      = null;
  private ActionListener            aListener   = null;
  private File                      dbFile      = null;
  private Connection                conn        = null;
  private Vector<LogLineDataObject> logDataList = null;
  private int                       currentDiveId;

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
   * @param al
   * @param device
   * @param logdataDir
   */
  public LogForDeviceDatabaseUtil( Logger lg, ActionListener al, String device, String logdataDir )
  {
    LOGGER = lg;
    this.aListener = al;
    dbFile = new File( String.format( "%s%sdivelog_%s.db", logdataDir, File.separator, device ) );
    conn = null;
    logDataList = null;
    currentDiveId = -1;
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
            "   %s integer not null,\n" +
            "   %s text not null,\n" +
            "   %s text not null,\n" +
            "   %s integer not null,\n" +
            "   %s integer,\n" +
            "   %s real,\n" +
            "   %s real,\n" +
            "   %s integer,\n" +
            "   %s integer,\n" +
            "   %s integer, \n" +
            "   %s integer, \n" +
            "   %s text \n" + 
            " );",
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
    LOGGER.log( Level.FINE, String.format( "create table: %s", ProjectConst.H_TABLE_DIVELOGS ) );
    stat.execute( sql );
    conn.commit();
    // Indize fuer die Tabelle erzeugen
    // index für Tauchnummer auf SPX
    //@formatter:off
    sql = String.format(
            "create index idx_%s_%s on %s ( %s ASC);",
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVENUMBERONSPX,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVENUMBERONSPX );
    //@formatter:off     
    LOGGER.log( Level.FINE, String.format( "create index on  table: %s", ProjectConst.H_TABLE_DIVELOGS ) );
    stat.execute( sql );
    conn.commit();
    //
    // index für Startzeit
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
            "  %s integer primary key asc autoincrement, \n" + 
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
    //
    // TODO: weitere Tabellen :-)
    //
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
        break;
      default:
        // Tja, das gibt ja wohl nicht
        LOGGER.log( Level.INFO, String.format( "create new database version:%d", ProjectConst.DB_VERSION ) );
        _dropTablesFromDatabase();
        _createNewDatabase( dbFile );
    }
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
    try
    {
      //@formatter:off
      sql = String.format( 
              "insert into %s (%s) values ( '%d' );",
              ProjectConst.V_DBVERSION,
              ProjectConst.V_VERSION,
              3
             );
      //@formatter:on
      stat = conn.createStatement();
      rs = stat.execute( sql );
      if( rs )
      {
        LOGGER.log( Level.INFO, "Version updated." );
      }
      conn.commit();
      stat.close();
      //@formatter:off
      sql = String.format( 
              "alter table %s add column %s text;",
              ProjectConst.H_TABLE_DIVELOGS,
              ProjectConst.H_NOTES
             );
      //@formatter:on
      stat = conn.createStatement();
      rs = stat.execute( sql );
      if( rs )
      {
        LOGGER.log( Level.INFO, "Database (table) updated." );
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

  @Override
  public int allocateCache( int diveId )
  {
    // immer eine neue anlegen, löscht durch garbage collector auch eventuell vorhandene alte Liste
    LOGGER.log( Level.FINE, "allocate new cache for update dive <" + diveId + ">..." );
    logDataList = new Vector<LogLineDataObject>();
    // aktuelle Id setzen
    currentDiveId = diveId;
    if( logDataList != null )
    {
      return( 1 );
    }
    LOGGER.log( Level.FINE, "allocate new cache for update dive <" + diveId + ">...OK" );
    return( 0 );
  }

  @Override
  public int appendLogToCache( int diveId, LogLineDataObject logLineObj )
  {
    if( logDataList == null )
    {
      LOGGER.log( Level.SEVERE, "no logDataList for caching allocated! ABORT" );
      return( -1 );
    }
    if( currentDiveId == -1 || currentDiveId != diveId )
    {
      LOGGER.log( Level.SEVERE, "diveid for this logline is not correct in this situation! ABORT" );
      return( -1 );
    }
    logDataList.add( logLineObj );
    LOGGER.log( Level.FINE, "line dataset cached..." );
    return( 1 );
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
  public void deleteAllSetsForIds( int[] dbIds )
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
      LOGGER.log( Level.SEVERE, "fatal error in delete dataset: " + ex.getLocalizedMessage() );
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
      LOGGER.log( Level.SEVERE, "fatal error in delete dataset: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
    }
  }

  @Override
  public int deleteLogFromDatabease()
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
      LOGGER.log( Level.SEVERE, "fatal error in delete dataset: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
    }
    currentDiveId = -1;
    if( logDataList != null )
    {
      logDataList.clear();
      logDataList = null;
    }
    return 1;
  }

  @Override
  public Double[] getDiveHeadsForDiveNumAsDouble( int numberOnSpx )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    Double[] diveHeadData = new Double[12];
    //
    LOGGER.log( Level.FINE, "read head data for spx dive number <" + numberOnSpx + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s where %s=%d;",
            ProjectConst.H_DIVEID,
            ProjectConst.H_DIVENUMBERONSPX,
            ProjectConst.H_STARTTIME,
            ProjectConst.H_HADSEND,
            ProjectConst.H_FIRSTTEMP,
            ProjectConst.H_LOWTEMP,
            ProjectConst.H_MAXDEPTH,
            ProjectConst.H_SAMPLES,
            ProjectConst.H_DIVELENGTH,
            ProjectConst.H_UNITS,
            ProjectConst.H_TABLE_DIVELOGS,
            ProjectConst.H_DIVENUMBERONSPX,
            numberOnSpx
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        // Daten kosolidieren
        diveHeadData[0] = rs.getDouble( 1 );
        diveHeadData[1] = rs.getDouble( 2 );
        diveHeadData[2] = 0.0;
        diveHeadData[3] = 0.0;
        diveHeadData[4] = rs.getDouble( 3 );
        diveHeadData[5] = rs.getDouble( 4 );
        diveHeadData[6] = rs.getDouble( 5 );
        diveHeadData[7] = rs.getDouble( 6 );
        diveHeadData[8] = rs.getDouble( 7 );
        diveHeadData[9] = rs.getDouble( 8 );
        diveHeadData[10] = rs.getDouble( 9 );
        diveHeadData[11] = rs.getDouble( 10 );
      }
      rs.close();
      LOGGER.log( Level.FINE, "read head data for spx dive number <" + numberOnSpx + "> from DB...OK" );
      return( diveHeadData );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't read dive head data from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  @Override
  public String[] getDiveHeadsForDiveNumAsStrings( int numberOnSpx )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String[] diveHeadData = new String[12];
    //
    LOGGER.log( Level.FINE, "read head data for spx dive number <" + numberOnSpx + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s where %s=%d;",
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
            ProjectConst.H_DIVENUMBERONSPX,
            numberOnSpx
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
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
      LOGGER.log( Level.FINE, "read head data for spx dive number <" + numberOnSpx + "> from DB...OK" );
      return( diveHeadData );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't read dive head data from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  @Override
  public Vector<String[]> getDiveListForDevice( String device )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    Vector<String[]> results = new Vector<String[]>();
    //
    LOGGER.log( Level.FINE, "read divelist for device <" + device + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s,%s,%s from %s order by %s desc;",
            ProjectConst.H_DIVEID,
            ProjectConst.H_DIVENUMBERONSPX,
            ProjectConst.H_STARTTIME,
            ProjectConst.H_TABLE_DIVELOGS,
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
        LOGGER.log( Level.FINE, String.format( "database read dive nr <%s>", rs.getString( 1 ) ) );
      }
      rs.close();
      return( results );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't read device list from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  @Override
  public String getNotesForId( int dbId )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    String notesForDive = null;
    //
    LOGGER.log( Level.FINE, "read notes for dive <" + dbId + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s from %s where %s=%d;",
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
      LOGGER.log( Level.SEVERE, "Can't read notes from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
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
   * 
   * Lese die Version der Datenbank, wenn möglich
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
  public Vector<Integer[]> readDiveDataFromId( int dbId )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    Vector<Integer[]> diveData = new Vector<Integer[]>();
    //
    diveData.clear();
    LOGGER.log( Level.FINE, "read logdata for dbId <" + dbId + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from %s where %s=%d;",
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
        Integer[] resultSet = new Integer[12];
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
        // ab in den vector
        diveData.add( resultSet );
      }
      rs.close();
      LOGGER.log( Level.FINE, "read logdata for dbId <" + dbId + "> from DB...OK" );
      return( diveData );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't read dive data from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  @Override
  public int[] readHeadDiveDataFromId( int dbId )
  {
    String sql;
    Statement stat;
    ResultSet rs;
    int[] diveHeadData = new int[7];
    //
    LOGGER.log( Level.FINE, "read head data for database id <" + dbId + "> from DB..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( null );
    }
    //@formatter:off
    sql = String.format( 
            "select %s,%s,%s,%s,%s,%s,%s from %s where %s=%d;",
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
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      if( rs.next() )
      {
        // Daten kosolidieren
        diveHeadData[0] = rs.getInt( 1 );
        diveHeadData[1] = ( int )( rs.getDouble( 2 ) * 10.0 );
        diveHeadData[2] = ( int )( rs.getDouble( 3 ) * 10.0 );
        diveHeadData[3] = rs.getInt( 4 );
        diveHeadData[4] = rs.getInt( 5 );
        diveHeadData[5] = rs.getInt( 6 );
        diveHeadData[6] = rs.getInt( 7 );
      }
      rs.close();
      LOGGER.log( Level.FINE, "read head data for database id <" + dbId + "> from DB...OK" );
      return( diveHeadData );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "Can't read dive head data from db! (" + ex.getLocalizedMessage() + ")" );
      return( null );
    }
  }

  @Override
  public int removeLogdataForId( int diveId )
  {
    String sql;
    Statement stat;
    //
    if( diveId == -1 )
    {
      // das war nix...
      return( 0 );
    }
    LOGGER.log( Level.FINE, "remove logdatedata for dive (update) <" + diveId + ">..." );
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
      LOGGER.log( Level.FINE, "remove logdatedata for dive (update) <" + diveId + ">...OK" );
    }
    catch( SQLException ex )
    {
      LOGGER.log( Level.SEVERE, "fatal error in delete dataset: " + ex.getLocalizedMessage() );
      ex.printStackTrace();
      return 0;
    }
    return 1;
  }

  @Override
  public int saveNoteForId( int dbId, String notes )
  {
    String sql;
    Statement stat;
    boolean rs;
    //
    LOGGER.log( Level.FINE, "update notes for dive dbid: " + dbId + "..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( -1 );
    }
    try
    {
      //@formatter:off
      sql = String.format( 
              "update %s set %s='%s' where %s=%d;",
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
      LOGGER.log( Level.SEVERE, "Can't update dbversion <" + dbFile.getName() + "> (" + ex.getLocalizedMessage() + ")" );
      return( -1 );
    }
    return( dbId );
  }

  @Override
  public int writeLogToDatabase( final int diveId )
  {
    Thread writeDb;
    //
    if( logDataList == null )
    {
      LOGGER.log( Level.SEVERE, "no logDataList for write to databasde allocated! ABORT" );
      return( -1 );
    }
    if( currentDiveId == -1 || currentDiveId != diveId )
    {
      LOGGER.log( Level.SEVERE, "diveid for this chache is not correct in this situation! ABORT" );
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
        double markAirtemp = -999.99; // merke mir die Lufttemperatur (erster Wert der Temp => Luft...)
        double markLowestTemp = 100.0; // Merke mir die tiefste Temperatur
        long markMaxDepth = 0; // merke mir die Maximaltiefe
        long markSamples = 0;
        long markDiveLength = 0;
        //
        //@formatter:off
         sql = String.format( 
                 "insert into %s\n" +
                 " (\n" + 
                 "  %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n" + 
                 " )\n" + 
                 " values\n" +
                 " (\n" +
                 "  ?,?,?,?,?,?,?,?,?,?,?,?\n" + 
                 " );\n" 
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
                 ProjectConst.D_DELTATIME );
         //@formatter:off 
         try
         {
           prep = conn.prepareStatement( sql );
         }
         catch( SQLException ex )
         {
           LOGGER.log( Level.SEVERE, "fatal error : " + ex.getLocalizedMessage() );
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
           }
           catch( SQLException ex )
           {
             LOGGER.log( Level.SEVERE, "fatal error in sql prepare: " + ex.getLocalizedMessage() );
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
           // Tiefste Temperatur
           if( markLowestTemp > logLineObj.temperature )
           {
             // ja, die Temperatur war tiefer
             markLowestTemp = logLineObj.temperature;
           }
           // Maximale Tiefe
           if( markMaxDepth < logLineObj.depth )
           {
             // setze die größere Tiefe
             markMaxDepth = logLineObj.depth;
           }
         }
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
              //Doppelfehler...LogForDeviceDatabaseUtil Programm hart beenden!
              LOGGER.log( Level.SEVERE, "fatal double error in batch execute: " + ex1.getLocalizedMessage() );
              LOGGER.log( Level.SEVERE, "ABORT PROGRAM!!!!!!!!!" );
              ex1.printStackTrace();
              System.exit( -1 );
            }
           LOGGER.log( Level.SEVERE, "fatal error in batch execute: " + ex.getLocalizedMessage() );
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
         LOGGER.log( Level.FINE, "writed cache dataset to database." );
         // aufräumen!
         logDataList.clear();
         logDataList = null;
         // 
         // Statistische Daten in der DB updaten
         //
         //@formatter:off
         sql = String.format( 
                 Locale.ENGLISH,
                 "update %s \n" +
                 " set %s=%-3.1f, \n" + 
                 "     %s=%-3.2f, \n" + 
                 "     %s=%d, \n" + 
                 "     %s=%d, \n" + 
                 "     %s=%d \n" + 
                 " where %s=%d;",                    
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
         }
         catch( SQLException ex )
         {
           LOGGER.log( Level.SEVERE, "fatal error in data update: " + ex.getLocalizedMessage() );
           LOGGER.log( Level.FINE, "SQL:" + sql );
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
         return;
       };
    };
    writeDb.start();
    return 0;
  }

  @Override
  public int writeNewDive( String deviceId, String fileOnSPX, int units, long numberOnSPX, long startTime )
  {
    Statement stat;
    String sql;
    ResultSet rs;
    int generatedKey;
    //
    LOGGER.log( Level.FINE, "create new diving entry..." );
    if( conn == null )
    {
      LOGGER.log( Level.WARNING, "no databese connection..." );
      return( -1 );
    }
    // immer eine neue anlegen, löscht durch garbage collector auch eventuell vorhandene alte Liste
    logDataList = new Vector<LogLineDataObject>();
    //
    try
    {
      stat = conn.createStatement();
      LOGGER.log( Level.FINE, "insert new dataset into database..." );
      //@formatter:off
      sql = String.format( 
              "insert into %s ( %s,%s,%s,%s,%s ) values ( '%s','%s', %d, %d, %d );",
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
      LOGGER.log( Level.FINE, "write database... " );
      stat.execute( sql );
      conn.commit();
      //@formatter:off
      sql = String.format( 
              "select max(%s) from %s ;",
              ProjectConst.H_DIVEID,
              ProjectConst.H_TABLE_DIVELOGS
             );
      //@formatter:on
      LOGGER.log( Level.FINE, "read generated key... " );
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
      LOGGER.log( Level.SEVERE, "Can't insert into database! (" + ex.getLocalizedMessage() + ")" );
      return( -1 );
    }
  }
}
