//@formatter:off
/*
    programm: SubmatixSPXLog
    purpose:  configuration and read logs from SUBMATIX SPX42 divecomputer via Bluethooth    
    Copyright (C) 2012  Dirk Marciniak

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/
*/
//@formatter:on
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
    public int    numberOnSpx   = 0;
    public String readableName  = null;
    public String fileName      = null;
    public int    dbId          = 0;
    public long   javaTimeStamp = 0;

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
     * @param tm
     *          Timestamp des Logeintrages
     */
    public DataSave( int num, String nam, String fn, int id, long tm )
    {
      numberOnSpx = num;
      readableName = nam;
      fileName = fn;
      dbId = id;
      javaTimeStamp = tm;
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
   * @param tm
   *          Timestamp des Logeintrages
   */
  public void addLogentry( final int numberOnSpx, final String readableName, final String fileName, final int dbId, final long tm )
  {
    listCache.add( new DataSave( numberOnSpx, readableName, fileName, dbId, tm ) );
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
