package de.dmarcini.submatix.pclogger.utils;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

/**
 * 
 * Objekt für die Device-Combobox
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 08.09.2012
 * 
 */
public class DeviceComboBoxModel extends DefaultComboBoxModel
{
  //
  // Daten sind in einem Vector<String[]> gesichert
  // Jedes Element des Vectors elem ist
  // elem[0] == device ID
  // elem[1] == Device Alias
  // elem[2] == Flag, ob Device im BT gefunden wurde
  // elem[3] == PIN
  // elem[4] == Typ
  //
  /**
   * 
   */
  private static final long serialVersionUID = -6359596019517895785L;

  /**
   * 
   * Standartkonstruktor ohne Parameter
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.09.2012
   */
  public DeviceComboBoxModel()
  {
    super( new Vector<String[]>() );
  }

  /**
   * 
   * der gewöhnliche Konstruktor, Speichert Alias und DeviceID
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.09.2012
   * @param entrys
   *          Vactor von Stingarrays
   */
  public DeviceComboBoxModel( Vector<String[]> entrys )
  {
    super( entrys );
  }

  /**
   * Gib den für die Anzeige vorgesehenen Wert zurück
   */
  @Override
  public Object getElementAt( int index )
  {
    if( index > super.getSize() )
    {
      return( null );
    }
    if( index <= -1 )
    {
      return( new String( "" ) );
    }
    String[] value = ( String[] )super.getElementAt( index );
    if( value.length < 3 )
    {
      return( "" );
    }
    return( value[1] + " " + value[2] );
  }

  /**
   * 
   * Gib die Device-Id zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.09.2012
   * @param index
   * @return geräteID
   */
  public String getDeviceIdAt( int index )
  {
    String[] value;
    if( index > super.getSize() )
    {
      return( "" );
    }
    if( index <= -1 )
    {
      return( new String( "" ) );
    }
    value = ( String[] )super.getElementAt( index );
    if( value.length < 3 )
    {
      return( "" );
    }
    return( value[0] );
  }

  /**
   * 
   * Aliasname aus der Liste zurückgeben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   * @param index
   * @return Alias des Gerätes
   */
  public String getDeviceAliasAt( int index )
  {
    String[] value;
    if( index > super.getSize() )
    {
      return( "" );
    }
    if( index <= -1 )
    {
      return( new String( "" ) );
    }
    value = ( String[] )super.getElementAt( index );
    if( value.length < 3 )
    {
      return( "" );
    }
    return( value[1] );
  }

  /**
   * 
   * War das Device nach dem Discovering online?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   * @param index
   * @return War bei discover online ?
   */
  public boolean getWasOnlineAt( int index )
  {
    String[] value;
    if( index > super.getSize() )
    {
      return( false );
    }
    if( index <= -1 )
    {
      return( false );
    }
    value = ( String[] )super.getElementAt( index );
    if( value.length < 3 )
    {
      return( false );
    }
    if( value[2].equals( "*" ) )
    {
      return( true );
    }
    return( false );
  }

  /**
   * 
   * Setze den Onlinestatus des Gerätes
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   * @param index
   * @param wasOnline
   */
  public void setWasOnlineAt( int index, boolean wasOnline )
  {
    String[] value;
    if( index > super.getSize() )
    {
      return;
    }
    if( index <= -1 )
    {
      return;
    }
    value = ( String[] )super.getElementAt( index );
    if( value.length < 3 )
    {
      return;
    }
    if( wasOnline )
    {
      value[2] = "*";
    }
    else
    {
      value[2] = "";
    }
  }
}
