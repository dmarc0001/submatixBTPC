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
  private static final int       ID_SERIAL        = 0;
  private static final int       ID_ALIAS         = 1;
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

  public void addElement( String[] _elem )
  {
    data.add( _elem );
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
    return( value[ID_ALIAS] );
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
    return( value[ID_SERIAL] );
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
    return( value[ID_ALIAS] );
  }

  public int getIndexOf( String[] _obj )
  {
    return( data.indexOf( _obj ) );
  }

  @SuppressWarnings( "unused" )
  private String[] getItemForSerial( final String _elem )
  {
    for( String[] elem : data )
    {
      if( elem[ID_SERIAL].equals( _elem ) )
      {
        return( elem );
      }
    }
    return( null );
  }

  private String[] getItemForAlias( final String _elem )
  {
    for( String[] elem : data )
    {
      if( elem[ID_ALIAS].equals( _elem ) )
      {
        return( elem );
      }
    }
    return( null );
  }

  @Override
  public String getSelectedItem()
  {
    if( this.selectedItem == null ) return( null );
    return( this.selectedItem[ID_ALIAS] );
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

  @Override
  public void setSelectedItem( Object _item )
  {
    if( _item instanceof String )
    {
      setSelectedItem( ( String )_item );
    }
    else if( _item instanceof String[] )
    {
      setSelectedItem( ( String[] )_item );
    }
  }

  public void setSelectedItem( String _elem )
  {
    this.selectedItem = getItemForAlias( _elem );
  }

  public void setSelectedItem( String[] _elem )
  {
    this.selectedItem = _elem;
  }
}
