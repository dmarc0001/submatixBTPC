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
            "   %s integer not null,\n" +
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

  @Override
  public int writeNewDive( String deviceId, String fileOnSPX, long startTime )
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
              "insert into %s ( %s,%s,%s,%s ) values ( '%s','%s','%s','%d' );",
              ProjectConst.H_TABLE_DIVELOGS,
              ProjectConst.H_DEVICEID,
              ProjectConst.H_FILEONSPX,
              ProjectConst.H_STARTTIME,
              deviceId, 
              fileOnSPX,
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
        String sql;
        LogLineDataObject logLineObj;
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
            "insert from %s\n" +
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
    return 0;
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
            "select %s,%s from %s;",
            ProjectConst.H_DIVEID,
            ProjectConst.H_STARTTIME,
            ProjectConst.H_TABLE_DIVELOGS
           );
    //@formatter:on
    try
    {
      stat = conn.createStatement();
      rs = stat.executeQuery( sql );
      while( rs.next() )
      {
        // Daten kosolidieren
        String[] resultSet = new String[2];
        resultSet[0] = rs.getString( 1 );
        resultSet[1] = rs.getString( 2 );
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
            "select %s,%s,%s,%s,%s,%s from %s where dive_id=%d;",
            ProjectConst.D_DELTATIME,
            ProjectConst.D_DEPTH,
            ProjectConst.D_TEMPERATURE,
            ProjectConst.D_PPO,
            ProjectConst.D_SETPOINT,
            ProjectConst.D_NULLTIME,
            ProjectConst.D_TABLE_DIVEDETAIL,
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
        Integer[] resultSet = new Integer[6];
        resultSet[0] = rs.getInt( 1 );
        resultSet[1] = rs.getInt( 2 );
        resultSet[2] = rs.getInt( 3 );
        resultSet[3] = rs.getInt( 4 );
        resultSet[4] = rs.getInt( 5 );
        resultSet[5] = rs.getInt( 6 );
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
}
