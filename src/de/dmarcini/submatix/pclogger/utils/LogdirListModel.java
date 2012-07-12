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
   * @param number
   * @param name
   * @param inDB
   */
  public void addLogentry( int number, String name, String inDB )
  {
    // Die Einträge in der Form:
    // String[0] = Nummer
    // String[1] = Lesbarer Name
    // String[2] = Schon in der DB?
    String[] element =
    { String.format( "%03d", number ), name, inDB };
    // super.addElement( element );
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
}
