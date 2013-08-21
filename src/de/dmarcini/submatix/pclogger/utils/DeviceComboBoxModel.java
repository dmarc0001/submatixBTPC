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
public class DeviceComboBoxModel extends DefaultComboBoxModel<String>
{
  private final Vector<String[]> data             = new Vector<String[]>();
  private String[]               selectedItem     = null;
  //
  // Daten sind in einem Vector<String[]> gesichert
  // Jedes Element des Vectors elem ist
  // elem[0] == device ID (Serial)
  // elem[1] == Device Alias
  //
  /**
   * 
   */
  private static final long      serialVersionUID = -6359596019517895785L;

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
  @SuppressWarnings( "unused" )
  private DeviceComboBoxModel()
  {}

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
    for( String[] ent : entrys )
    {
      data.add( ent );
    }
    if( data.size() > 0 ) selectedItem = data.get( 0 );
    // data.addAll( entrys );
  }

  /**
   * Gib den für die Anzeige vorgesehenen Wert zurück
   */
  @Override
  public String getElementAt( int index )
  {
    if( index > data.size() )
    {
      return( null );
    }
    if( index <= -1 )
    {
      return( "-" );
    }
    String[] value = data.elementAt( index );
    if( value.length < 2 )
    {
      return( "-" );
    }
    return( value[0] );
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
  public String getDeviceSerialAt( int index )
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
    value = data.elementAt( index );
    if( value.length < 2 )
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
    if( index > data.size() )
    {
      return( "" );
    }
    if( index <= -1 )
    {
      return( new String( "" ) );
    }
    value = data.elementAt( index );
    if( value.length < 2 )
    {
      return( "" );
    }
    return( value[1] );
  }

  public void addElement( String[] _elem )
  {
    data.add( _elem );
  }

  public int getIndexOf( String[] _obj )
  {
    return( data.indexOf( _obj ) );
  }

  @Override
  public int getSize()
  {
    return( data.size() );
  }

  public void insertElementAt( String[] _elem, int index )
  {
    data.insertElementAt( _elem, index );
  }

  @Override
  public void removeAllElements()
  {
    data.clear();
  }

  public void removeElement( String[] _elem )
  {
    data.remove( _elem );
  }

  @Override
  public void removeElementAt( int index )
  {
    data.remove( index );
  }

  public void setSelectedItem( String _elem )
  {
    this.selectedItem = getItemForSerial( _elem );
  }

  private String[] getItemForSerial( final String _elem )
  {
    for( String[] elem : data )
    {
      if( elem[0].equals( _elem ) )
      {
        return( elem );
      }
    }
    return( null );
  }

  public void setSelectedItem( String[] _elem )
  {
    this.selectedItem = _elem;
  }

  @Override
  public String getSelectedItem()
  {
    return( this.selectedItem[0] );
  }
}
