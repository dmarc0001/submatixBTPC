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

import javax.swing.DefaultListModel;

/**
 * 
 * Mein eigenes Listmodell
 * 
 * Speichert in der elementeliste Stringarrays mit drei Einträgen Nummer, Lesbarer Name, in der DB vorhanden ("x" oder " ")
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 05.05.2012
 */
public class LogDirListModel extends DefaultListModel
{
  /**
   * 
   */
  private static final long serialVersionUID = -7898034061953387121L;

  /**
   * Überschreibe original, damit die Liste den Namen darstellen kann, gibt lesbsaren Namen zurück
   */
  @Override
  public Object getElementAt( int index )
  {
    if( super.isEmpty() )
    {
      return( null );
    }
    String[] element = ( String[] )super.getElementAt( index );
    if( element[2] == null )
    {
      return( element[1] );
    }
    if( element[2].equals( " " ) )
    {
      return( String.format( "Nr %4s - %s", element[0], element[1] ) );
    }
    return( String.format( "Nr %4s - %s *", element[0], element[1] ) );
  }

  /**
   * 
   * Einen logeintrag in die Liste einfügen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param numberOnSpx
   *          Numer auf SPX
   * @param name
   *          Lesbarer Name des Eintrages
   * @param wasSaved
   *          Schon in der DB gesichert?
   * @param dbId
   *          DAtenbankid
   */
  public void addLogentry( int numberOnSpx, String name, String wasSaved, int dbId )
  {
    // Die Einträge in der Form:
    // String[0] = Nummer
    // String[1] = Lesbarer Name
    // String[2] = Schon in der DB?
    // String[3] = DatenbankID
    String[] element =
    { String.format( "%03d", numberOnSpx ), name, wasSaved, String.format( "%d", dbId ) };
    super.add( 0, element );
  }

  /**
   * 
   * Einen Logeintrag aus der Liste lesen (Stringarray)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param number
   * @return Stringarray mit einem Eintrag
   */
  public String[] getLogentryAt( int number )
  {
    if( super.isEmpty() )
    {
      return( null );
    }
    String[] element = ( String[] )super.getElementAt( number );
    return( element );
  }

  /**
   * 
   * Gib die Nummer des Logeintrtages (auf dem SPX) zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param number
   * @return Nummer des Eintrages auf dem SPX
   */
  public int getLognumberAt( int number )
  {
    int logNumber = 0;
    if( super.isEmpty() || number < 0 )
    {
      return( -1 );
    }
    String[] element = ( String[] )super.getElementAt( number );
    try
    {
      logNumber = Integer.parseInt( element[0] );
    }
    catch( NumberFormatException ex )
    {
      System.err.println( "NUMBERFORMATEXEPTION on " + element[0] + "(" + ex.getMessage() + ")" );
    }
    return( logNumber );
  }

  /**
   * 
   * Gib den (angezeigten) Inhalt der Liste zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param number
   * @return Name des Eintrages
   */
  public String getLogNameAt( int number )
  {
    if( super.isEmpty() )
    {
      return( null );
    }
    String[] element = ( String[] )super.getElementAt( number );
    return( element[1] );
  }

  /**
   * 
   * Gib zurück, ob der Eintrag bereits in der DB ist
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 09.07.2012
   * @param number
   * @return ist schon gesichert?
   */
  public boolean istInDb( int number )
  {
    if( super.isEmpty() )
    {
      return( false );
    }
    String[] element = ( String[] )super.getElementAt( number );
    if( element[2] == null )
    {
      return( false );
    }
    if( element[2].equals( " " ) )
    {
      return( false );
    }
    return( true );
  }

  /**
   * 
   * Gib die DatenbankID für einen Eintrag zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   * @param number
   *          auf dem SPX
   * @return Datenbankid des Eintrages
   */
  public int getDbIdAt( int number )
  {
    int dbId = -1;
    if( super.isEmpty() )
    {
      return( -1 );
    }
    String[] element = ( String[] )super.getElementAt( number );
    if( element[3] == null )
    {
      return( -1 );
    }
    try
    {
      dbId = Integer.parseInt( element[3] );
    }
    catch( NumberFormatException ex )
    {
      return( -1 );
    }
    return( dbId );
  }
}
