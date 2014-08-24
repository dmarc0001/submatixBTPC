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

import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class AliasEditTableModel extends DefaultTableModel
{
  /**
   * 
   */
  private static final long serialVersionUID = 6816415249327822541L;

  public AliasEditTableModel( Vector<String[]> data, Vector<String> columnNames )
  {
    super( convertToArray( data ), columnNames.toArray() );
  }

  /**
   * 
   * Für die Tabelle schmackhaft machen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.09.2012
   * @param data
   * @return Objektarray
   */
  private static Object[][] convertToArray( Vector<String[]> data )
  {
    Object[][] dt = new Object[data.size()][2];
    int i = 0;
    //
    Iterator<String[]> it = data.iterator();
    while( it.hasNext() )
    {
      String[] str = it.next();
      dt[i][0] = str[0];
      dt[i][1] = str[1];
      i++;
    }
    return( dt );
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
}
