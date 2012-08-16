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
public class DiveExportTableModel extends DefaultTableModel
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

  public DiveExportTableModel( Object[][] data, String[] columnNames )
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
  public String[] getDataRowAt( int setNumber )
  {
    return( ( String[] )super.dataVector.elementAt( setNumber ) );
  }
}
