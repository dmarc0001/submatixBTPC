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
 * Combobox kapselt hier die Datenbank-Set-Id des Preset
 */
package de.dmarcini.submatix.pclogger.utils;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

@SuppressWarnings( "rawtypes" )
public class GasPresetComboBoxModel extends DefaultComboBoxModel
{
  private static final long serialVersionUID = -7957814923054449450L;

  /**
   * 
   * Konstruktor, gleich initialisieren der Daten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.07.2012
   * @param presetEntrys
   */
  @SuppressWarnings( "unchecked" )
  public GasPresetComboBoxModel( Vector<GasPresetComboObject> presetEntrys )
  {
    super( presetEntrys );
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
    GasPresetComboObject entry = ( GasPresetComboObject )super.getElementAt( index );
    return( entry.presetName );
  }

  public String getNameAt( int index )
  {
    return( ( String )getElementAt( index ) );
  }

  /**
   * 
   * Gib Datenbankid des Eintrages zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.05.2012
   * @param index
   * @return Differenz als String
   */
  public int getDatabaseIdAt( int index )
  {
    if( index > super.getSize() )
    {
      return( -1 );
    }
    GasPresetComboObject entry = ( GasPresetComboObject )super.getElementAt( index );
    return( entry.dbId );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public void addElement( Object elem )
  {
    if( elem instanceof GasPresetComboObject )
    {
      super.addElement( elem );
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public void insertElementAt( Object elem, int index )
  {
    if( elem instanceof GasPresetComboObject )
    {
      super.insertElementAt( elem, index );
    }
  }
}
