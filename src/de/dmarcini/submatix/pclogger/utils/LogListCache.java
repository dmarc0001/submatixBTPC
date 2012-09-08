package de.dmarcini.submatix.pclogger.utils;

import java.util.Vector;

/**
 * 
 * Speichert Loglist-Einträge zwischen, damit das beim wiederanzeigen schneller geht, solange derselbe SPX noch verbunden ist
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 08.09.2012
 */
public class LogListCache
{
  private Vector<DataSave> listCache = null;

  /**
   * 
   * implizite Klasse zum Datenspeichern
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   */
  public class DataSave
  {
    public int    numberOnSpx  = 0;
    public String readableName = null;
    public String fileName     = null;
    int           dbId         = 0;

    /**
     * 
     * Konstruktor mit Parameterübergabe
     * 
     * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
     * 
     * @author Dirk Marciniak (dirk_marciniak@arcor.de)
     * 
     *         Stand: 08.09.2012
     * @param num
     *          Nummer auf dem SPX
     * @param nam
     *          Menschenlesbarer Name des Log
     * @param fn
     *          Dateiname auf dem SPX
     * @param id
     *          Datenbak-Id des Log
     */
    public DataSave( int num, String nam, String fn, int id )
    {
      numberOnSpx = num;
      readableName = nam;
      fileName = fn;
      dbId = id;
    }
  };

  /**
   * 
   * Standartkonstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   */
  public LogListCache()
  {
    listCache = new Vector<DataSave>();
  }

  /**
   * 
   * Ist überhaupt was drin?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   * @return ist er leer
   */
  public boolean isEmpty()
  {
    if( listCache == null ) return( true );
    return( listCache.isEmpty() );
  }

  /**
   * 
   * Einen Logeintrag in den Cache
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   * @param numberOnSpx
   *          Lognummer auf dem SPX
   * @param readableName
   *          Menschenlesbarer Name
   * @param fileName
   *          Dateiname auf dem SPX
   * @param dbId
   *          Datenbank-Id in der DB für den Log
   */
  public void addLogentry( final int numberOnSpx, final String readableName, final String fileName, final int dbId )
  {
    listCache.add( new DataSave( numberOnSpx, readableName, fileName, dbId ) );
  }

  /**
   * 
   * Lösche den Inhalt
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   */
  public void clear()
  {
    if( listCache != null ) listCache.clear();
  }

  /**
   * 
   * Gib die Anzahl der Einträge zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   * @return Anzahl
   */
  public int size()
  {
    if( listCache != null )
    {
      return( listCache.size() );
    }
    return( 0 );
  }

  /**
   * 
   * Gib die Liste zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   * @return Vector mit Liste der Logs
   */
  public Vector<DataSave> getLogList()
  {
    return( listCache );
  }
}
