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

import javax.swing.table.DefaultTableModel;

/**
 * 
 * Eigene Klasse für die Liste der Tauchgänge (Tabellenmodell)
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 16.08.2012
 */
public class FileManagerTableModel extends DefaultTableModel
{
  // Objekt[][0..4] für das Modell erstellen
  // [0] DIVENUMBERONSPX
  // [1] Start Datum und Zeite localisiert
  // [2] Max Tiefe
  // [3] Länge
  // [4] DBID
  /**
   * 
   */
  private static final long serialVersionUID = 4949908890954476602L;
  private static final int  countColummns    = 4;
  private String[][]        dataAsString     = null;

  public FileManagerTableModel( Object[][] data, String[] columnNames )
  {
    super( data, columnNames );
    dataAsString = ( String[][] )data;
  }

  @Override
  public boolean isCellEditable( int rowIndex, int columnIndex )
  {
    if( columnIndex == 1 )
    {
      return( true );
    }
    return false;
  }

  @Override
  public Object getValueAt( int row, int column )
  {
    return( super.getValueAt( row, column ) );
  }

  @Override
  public int getColumnCount()
  {
    return( countColummns );
  }

  /**
   * 
   * Gib den Datensatz an Position setNumber zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 16.08.2012
   * @param setNumber
   * @return Datenset als String[]
   */
  public int getDbIdAt( int setNumber )
  {
    try
    {
      return( Integer.parseInt( dataAsString[setNumber][4] ) );
    }
    catch( NumberFormatException ex )
    {
      return( -1 );
    }
  }
}
