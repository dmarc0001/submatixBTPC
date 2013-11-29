/**
 * Klasse beinhaltet die Buildnummer und das Erstellungsdatum
 * 
 * BuildVersion.java de.dmarcini.netutils.dsl cmdLineSequenzialDslChecker
 * 
 * @author Dirk Marciniak 31.07.2012
 */
package de.dmarcini.submatix.pclogger.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.dmarcini.submatix.pclogger.lang.LangStrings;
import de.dmarcini.submatix.pclogger.res.ProjectConst;

/**
 * @author dmarc
 */
public class BuildVersion
{
  private final long buildNumber = 1970L;
  private final long buildDate = 1385754669535L;

  /**
   * Gib die Buildnummer zurück
   * 
   * @author Dirk Marciniak 31.07.2012
   * @return long
   */
  public long getBuild()
  {
    return( buildNumber );
  }

  /**
   * Gib das Builddatum als String zurück
   * 
   * @author Dirk Marciniak 31.07.2012
   * @return String
   */
  public String getBuildDate()
  {
    Date date = new Date( buildDate );
    return( date.toString() );
  }

  /**
   * 
   * Das Builddatum als lokalisiertes Format
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   * @param fmt
   * @return Datum als String
   */
  public String getLocaleDate( String fmt )
  {
    Date date = new Date( buildDate );
    SimpleDateFormat sdf = new SimpleDateFormat( fmt );
    return( sdf.format( date ) );
  }

  /**
   * 
   * Version aus den Projektdefinitionen zurückgeben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   * @return Versionsstring
   */
  public String getVersion()
  {
    return( ProjectConst.MANUFACTVERS );
  }

  /**
   * 
   * Version mit "Version: " vorweg
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 23.08.2013
   * @return 
   */
  public String getVersionAsString()
  {
    return( LangStrings.getString( "BuildVersion.version.text" ) + ProjectConst.MANUFACTVERS );
  }
}
