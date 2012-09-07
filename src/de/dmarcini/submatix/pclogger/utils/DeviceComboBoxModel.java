package de.dmarcini.submatix.pclogger.utils;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

public class DeviceComboBoxModel extends DefaultComboBoxModel
{
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
    if( value.length < 2 )
    {
      return( "" );
    }
    return( value[1] );
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
    if( value.length < 2 )
    {
      return( "" );
    }
    return( value[0] );
  }
}
