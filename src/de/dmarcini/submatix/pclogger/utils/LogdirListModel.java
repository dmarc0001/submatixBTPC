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
public class LogdirListModel extends DefaultListModel
{
  /**
   * 
   */
  private static final long serialVersionUID = -7898034061953387121L;

  /**
   * Überschreibe original, damit die Liste den Namen darstellen kann
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
      return( element[1] );
    }
    return( element[1] + " *" );
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
   * @param number
   * @param name
   * @param inDB
   */
  public void addLogentry( int number, String name, String inDB )
  {
    String[] element =
    { String.format( "%02d", number ), name, inDB };
    super.addElement( element );
  }

  /**
   * 
   * Einen Logeintrag aus der Liste lesen
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
}
