package de.dmarcini.submatix.pclogger.utils;

import javax.swing.table.AbstractTableModel;

public class AliasTableModel extends AbstractTableModel
{
  /**
   * 
   */
  private static final long serialVersionUID = -5758119595877923301L;
  private int               rowCount         = 0;
  private final int         colCount         = 2;
  private String[][]        fields           = null;
  private final String[]    colnames;

  /**
   * 
   * Konstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 24.04.2012
   * @param stringfield
   *          stringfield[rows][cols=2]
   */
  public AliasTableModel( String[][] stringfield )
  {
    fields = stringfield;
    rowCount = fields.length;
    colnames = new String[2];
    colnames[0] = "DEVICE";
    colnames[1] = "ALIAS";
  }

  @Override
  public int getRowCount()
  {
    return( rowCount );
  }

  @Override
  public int getColumnCount()
  {
    return( colCount );
  }

  @Override
  public String getColumnName( int col )
  {
    if( col < 2 )
    {
      return( colnames[col] );
    }
    return( null );
  }

  @Override
  public String getValueAt( int row, int col )
  {
    // Kein Inhalt, keine Rückgabe.
    if( fields == null )
    {
      return null;
    }
    // Feldgrenzen wollen wir nicht überschreiten
    if( row >= rowCount || col >= 2 )
    {
      return( null );
    }
    return( fields[row][col] );
  }

  public void setCoumnNames( String[] names )
  {
    if( names.length >= 2 )
    {
      colnames[0] = names[0];
      colnames[1] = names[1];
    }
  }

  public boolean isCelleditable( int row, int col )
  {
    if( col == 1 )
    {
      return( true );
    }
    return( false );
  }

  public void setValueAt( String val, int row, int col )
  {
    if( col <= 1 )
    {
      if( row <= rowCount )
      {
        fields[row][col] = val;
        this.fireTableDataChanged();
      }
    }
  }
}
