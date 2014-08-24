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
 * Das Modell kapselt für die Anzeige in der Combo-Box den Typ String[2] Damit ist das Modell in der Lage auch die DB-Id einfach zu sichern
 */
package de.dmarcini.submatix.pclogger.utils;

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
    super( listEntrys );
    // if( listEntrys == null )
    // {
    // return;
    // }
    // // die vectordaten in das Comboboxmodell reintackern
    // // iterator nutzen
    // for( Enumeration<String[]> en = listEntrys.elements(); en.hasMoreElements(); )
    // {
    // String[] entry = en.nextElement();
    // super.addElement( entry );
    // }
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
