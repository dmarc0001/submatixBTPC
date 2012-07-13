package de.dmarcini.submatix.pclogger.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

public interface ILogForDeviceDatabaseUtil
{
  /**
   * 
   * Erzeuge eine Verbindung mit der Datenbank (Datei vom Konstruktoraufruf)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @return Verbindung
   */
  public Connection createConnection();

  /**
   * 
   * Erzeuge eine neue Datenbank mit dem Namen aus dem Konstruktoraufruf
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @return Verbindung
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public Connection createNewDatabase() throws SQLException, ClassNotFoundException;

  /**
   * 
   * Erzeuge eine Datenbank mit spezifidschem Dateinamen als Basis
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @param dbFileName
   * @return Verbindung
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  public Connection createNewDatabase( String dbFileName ) throws SQLException, ClassNotFoundException;

  /**
   * 
   * Schliesse die Datenbank
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   */
  public void closeDB();

  /**
   * 
   * Teste, ob die DB offen ist
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @return Ist offen/ist nicht offen
   */
  public boolean isOpenDB();

  /**
   * 
   * Wurde die Logdatei schon in der Db gesichert?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @param filename
   * @return ja/nein
   */
  public boolean isLogSaved( String filename );

  /**
   * 
   * In der Kopftabelle einen neuen TG anlegen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @param deviceId
   * @param fileOnSPX
   * @param numberOnSPX
   * @param startTime
   * @return Erfolgrecih/nicht erfolgreich
   */
  public int writeNewDive( String deviceId, String fileOnSPX, long numberOnSPX, long startTime );

  /**
   * 
   * Für ein Update muß noch ein Cache allociert werden
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 12.07.2012
   * @param diveId
   *          welcher Log
   * @return ok oder nicht
   */
  public int allocateCache( int diveId );

  /**
   * 
   * Schreibe eine Zeile aus dem SPX in den Cache
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @param diveId
   * @param logLineObj
   * @return Zeile erfolgreich/nicht erfolgreich abgelegt
   */
  public int appendLogToCache( int diveId, LogLineDataObject logLineObj );

  /**
   * 
   * Schreibe den Cache in die SQLite Database
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @param diveId
   * @return erfolgreich/nicht erfolgreich
   */
  public int writeLogToDatabase( int diveId );

  /**
   * 
   * Nach DB-Fehler Kopfdaten und Detail des aktuellen Logs aus der DD entfernen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @return erfolgreich oder nicht
   */
  public int deleteLogFromDatabease();

  /**
   * 
   * Erstelle einen Vector aus String[] mit den gespeicherten Logs eines Gerätes
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.07.2012
   * @param device
   * @return Liste von array mit Logeinträgen
   */
  public Vector<String[]> getDiveListForDevice( String device );

  /**
   * 
   * Lese aus der Datenbank Logdaten eines Tauchganges
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.07.2012
   * @param dbId
   * @return Liste von Stringarrays (Daten)
   */
  public Vector<Integer[]> readDiveDataFromId( int dbId );

  /**
   * 
   * Entferne die Logdaten eines Tauchgangs vor einem Update
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 12.07.2012
   * @param diveId
   * @return erfolgtreich?
   */
  public int removeLogdataForId( int diveId );

  /**
   * 
   * Gibt die Headerdaten eines Dives aus der DB zurück (
   * H_DIVEID,H_DIVENUMBERONSPX,H_FILEONSPX,H_DEVICEID,H_STARTTIME,H_HADSEND,H_FIRSTTEMP,H_LOWTEMP,H_MAXDEPTH,H_SAMPLES,H_DIVELENGTH )
   * 
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.07.2012
   * @param numberOnSpx
   * @return Werte als Vector<Strings>
   */
  public String[] getDiveHeadsForDiveNumAsStrings( int numberOnSpx );

  /**
   * 
   * Gibt die Headerdaten eines Dives aus der DB zurück (
   * H_DIVEID,H_DIVENUMBERONSPX,H_FILEONSPX,H_DEVICEID,H_STARTTIME,H_HADSEND,H_FIRSTTEMP,H_LOWTEMP,H_MAXDEPTH,H_SAMPLES,H_DIVELENGTH )
   * 
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.07.2012
   * @param numberOnSpx
   * @return Werte als Vector<Double>
   */
  public Double[] getDiveHeadsForDiveNumAsDouble( int numberOnSpx );
}
