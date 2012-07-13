/**
 * Das Modell kapselt für die Anzeige in der Combo-Box den Typ String[2] Damit ist das Modell in der Lage auch die DB-Id einfach zu sichern
 */
package de.dmarcini.submatix.pclogger.utils;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

public class LogListComboBoxModel extends DefaultComboBoxModel
{
  /**
   * 
   */
  private static final long serialVersionUID = -7957814923054349450L;

  /**
   * 
   * Konstruktor, gleich initialisieren der Daten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.07.2012
   * @param listEntrys
   *          Vector mit StringArrays
   */
  public LogListComboBoxModel( Vector<String[]> listEntrys )
  {
    super();
    if( listEntrys == null )
    {
      return;
    }
    // die vectordaten in das Comboboxmodell reintackern
    // iterator nutzen
    for( Enumeration<String[]> en = listEntrys.elements(); en.hasMoreElements(); )
    {
      String[] entry = en.nextElement();
      super.addElement( entry );
    }
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
    return( value[1] + " - " + value[2] );
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
    int dbId;
    String[] value;
    if( index > super.getSize() )
    {
      return( -1 );
    }
    value = ( String[] )super.getElementAt( index );
    try
    {
      dbId = Integer.parseInt( value[0].trim() );
    }
    catch( NumberFormatException ex )
    {
      dbId = -1;
    }
    return( dbId );
  }

  @Override
  public void addElement( Object elem )
  {
    // Felder sind:
    // H_DIVEID,
    // H_H_DIVENUMBERONSPX
    // H_STARTTIME,
    if( elem instanceof String[] )
    {
      super.addElement( elem );
    }
  }

  @Override
  public void insertElementAt( Object anObject, int index )
  {
    if( anObject instanceof String[] )
    {
      super.addElement( anObject );
    }
  }
}
