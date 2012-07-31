/**
 * Klasse beinhaltet die Buildnummer und das Erstellungsdatum
 * 
 * BuildVersion.java de.dmarcini.netutils.dsl cmdLineSequenzialDslChecker
 * 
 * @author Dirk Marciniak 31.07.2012
 */
package de.dmarcini.submatix.pclogger.utils;

import java.util.Date;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

/**
 * @author dmarc
 */
public class BuildVersion
{
  private final long buildNumber = 386L;
  private final long buildDate = 1343756640712L;

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
}
