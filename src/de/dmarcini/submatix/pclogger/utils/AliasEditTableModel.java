package de.dmarcini.submatix.pclogger.utils;

import javax.swing.table.DefaultTableModel;

public class AliasEditTableModel extends DefaultTableModel
{
  /**
   * 
   */
  private static final long serialVersionUID = 6816415249327822541L;

  public AliasEditTableModel( Object[][] data, String[] columnNames )
  {
    super( data, columnNames );
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
