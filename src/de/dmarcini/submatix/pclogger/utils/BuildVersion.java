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

import de.dmarcini.submatix.pclogger.ProjectConst;
import de.dmarcini.submatix.pclogger.lang.LangStrings;

/**
 * @author dmarc
 */
public class BuildVersion
{
  private final long buildNumber = 2051L;
  private final long buildDate = 1526734324811L;

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
