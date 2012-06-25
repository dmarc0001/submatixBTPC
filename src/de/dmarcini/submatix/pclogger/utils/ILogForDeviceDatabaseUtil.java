package de.dmarcini.submatix.pclogger.utils;

import java.sql.Connection;
import java.sql.SQLException;

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
   * @return
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
   * @return
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
   * @return
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
   * @param timeZone
   * @param startTime
   * @return Erfolgrecih/nicht erfolgreich
   */
  public int writeNewDive( String deviceId, String fileOnSPX, String timeZone, long startTime );

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
}
