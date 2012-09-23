/**
 * Combobox kapselt hier die Datenbank-Set-Id des Preset
 */
package de.dmarcini.submatix.pclogger.utils;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

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

  @Override
  public void addElement( Object elem )
  {
    if( elem instanceof GasPresetComboObject )
    {
      super.addElement( elem );
    }
  }

  @Override
  public void insertElementAt( Object elem, int index )
  {
    if( elem instanceof GasPresetComboObject )
    {
      super.insertElementAt( elem, index );
    }
  }
}
