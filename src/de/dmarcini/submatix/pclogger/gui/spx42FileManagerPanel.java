package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.joda.time.DateTime;

import de.dmarcini.submatix.pclogger.utils.DeviceComboBoxModel;
import de.dmarcini.submatix.pclogger.utils.FileManagerTableModel;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;
import de.dmarcini.submatix.pclogger.utils.UDDFFileCreateClass;

public class spx42FileManagerPanel extends JPanel implements ActionListener, ListSelectionListener
{
  /**
   * 
   */
  private static final long              serialVersionUID = 2149212648166152026L;
  private final Logger                   LOGGER;
  private String                         device;
  private final SpxPcloggerProgramConfig progConfig;
  private ResourceBundle                 stringsBundle;
  private final LogDerbyDatabaseUtil     dbUtil;
  private final MouseMotionListener      mListener;
  private JTable                         dataViewTable;
  private JComboBox                      deviceComboBox;
  private JButton                        cancelButton;
  private JButton                        deleteButton;
  private JButton                        exportButton;

  public spx42FileManagerPanel( Logger LOGGER, MouseMotionListener mListener, LogDerbyDatabaseUtil sqliteDbUtil, SpxPcloggerProgramConfig progConfig )
  {
    this.LOGGER = LOGGER;
    this.dbUtil = sqliteDbUtil;
    this.progConfig = progConfig;
    this.mListener = mListener;
    initPanel();
  }

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    // ////////////////////////////////////////////////////////////////////////
    // Kommando wechsle das Gerät zur Anzeige
    if( cmd.equals( "change_device_to_display" ) )
    {
      // wenn das eine ComboBox ist
      if( ev.getSource() instanceof JComboBox )
      {
        // ist das die DeviceBox?
        JComboBox theBox = ( JComboBox )ev.getSource();
        if( theBox.equals( deviceComboBox ) )
        {
          releaseLists();
          // ist was ausgewählt?
          if( deviceComboBox.getSelectedIndex() != -1 )
          {
            String connDev = ( ( DeviceComboBoxModel )deviceComboBox.getModel() ).getDeviceIdAt( deviceComboBox.getSelectedIndex() );
            if( connDev != null )
            {
              fillDiveTable( connDev );
            }
          }
        }
      }
    }
    else if( cmd.equals( "cancel_selection" ) )
    {
      dataViewTable.clearSelection();
    }
    else if( cmd.equals( "delete_selection" ) )
    {
      int result = showAskBox( stringsBundle.getString( "fileManagerPanel.showAskBox.message" ) );
      if( result == 1 )
      {
        LOGGER.info( "DELETE DATASETS!" );
        int[] sets = dataViewTable.getSelectedRows();
        deleteDatasetsForIdx( sets );
        // merke mir das ausgewählte Teilchen
        int selectedIndex = deviceComboBox.getSelectedIndex();
        deviceComboBox.setSelectedIndex( -1 );
        // wenn da eine Auswahl war, wieder setzen und dann wird die Box auch neu befüllt
        if( selectedIndex != -1 )
        {
          deviceComboBox.setSelectedIndex( selectedIndex );
        }
      }
      else
      {
        LOGGER.fine( "abort deleting..." );
      }
    }
    else if( cmd.equals( "export_selection" ) )
    {
      LOGGER.fine( "export selected dives to file" );
      int[] sets = dataViewTable.getSelectedRows();
      exportDatasetsForIdx( sets );
      dataViewTable.clearSelection();
    }
    else
    {
      LOGGER.warning( "unknown action command!" );
    }
  }

  /**
   * 
   * Aus den Indizi der Tabelle die DBID erfragen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.08.2012
   * @param sets
   * @return set datenbankID's
   */
  private int[] getDbIdsForTableIdx( int[] sets )
  {
    // Sets lesen, Array für DatenbankID erzeugen
    int[] dbIds = new int[sets.length];
    FileManagerTableModel tm = ( FileManagerTableModel )dataViewTable.getModel();
    for( int setNumber = 0; setNumber < sets.length; setNumber++ )
    {
      int dataSet = tm.getDbIdAt( sets[setNumber] );
      LOGGER.info( String.format( "DBID: <%d>, setNumber: <%d>", dataSet, setNumber ) );
      // datenbankid zufügen
      dbIds[setNumber] = dataSet;
    }
    return( dbIds );
  }

  /**
   * 
   * exportiere alle selektierten Tauchgänge in je eine Datei als UDDF 2.2
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.08.2012
   * @param sets
   */
  private void exportDatasetsForIdx( int[] sets )
  {
    UDDFFileCreateClass uddf = null;
    PleaseWaitDialog wDial = null;
    //
    if( sets.length == 0 ) return;
    if( device == null )
    {
      LOGGER.severe( "no device known in programobject! abort function!" );
    }
    if( !progConfig.getExportDir().exists() )
    {
      if( !progConfig.getExportDir().mkdirs() )
      {
        LOGGER.severe( "cant create export directory!" );
        return;
      }
    }
    if( !progConfig.getExportDir().isDirectory() )
    {
      LOGGER.severe( "export directory <" + progConfig.getExportDir().getAbsolutePath() + "> is NOT a directory!" );
      return;
    }
    //
    // versuche ein Transformobjekt für uddf zu erzeugen
    //
    try
    {
      uddf = new UDDFFileCreateClass( LOGGER, dbUtil );
    }
    catch( ParserConfigurationException ex )
    {
      LOGGER.severe( ex.getLocalizedMessage() );
      if( LOGGER.getLevel().intValue() < Level.CONFIG.intValue() )
      {
        ex.printStackTrace();
      }
      return;
    }
    catch( TransformerException ex )
    {
      LOGGER.severe( ex.getLocalizedMessage() );
      if( LOGGER.getLevel().intValue() < Level.CONFIG.intValue() )
      {
        ex.printStackTrace();
      }
      return;
    }
    catch( TransformerFactoryConfigurationError ex )
    {
      LOGGER.severe( ex.getLocalizedMessage() );
      if( LOGGER.getLevel().intValue() < Level.CONFIG.intValue() )
      {
        ex.printStackTrace();
      }
      return;
    }
    catch( Exception ex )
    {
      LOGGER.severe( ex.getLocalizedMessage() );
      if( LOGGER.getLevel().intValue() < Level.CONFIG.intValue() )
      {
        ex.printStackTrace();
      }
      return;
    }
    //
    // Sets lesen, Array für DatenbankID erzeugen
    //
    int[] dbIds = getDbIdsForTableIdx( sets );
    if( dbIds.length == 0 )
    {
      LOGGER.severe( "no database id's for export!" );
      return;
    }
    try
    {
      if( dbIds.length == 1 )
      {
        LOGGER.info( "export to dir: <" + progConfig.getExportDir().getAbsolutePath() + ">" );
        uddf.createXML( progConfig.getExportDir(), dbIds[0] );
      }
      else
      {
        // könnte dauern, Dialog machen
        wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.exportDive" ) );
        wDial.setMax( 100 );
        wDial.resetProgress();
        wDial.setVisible( true );
        LOGGER.info( "export to dir: <" + progConfig.getExportDir().getAbsolutePath() + ">" );
        uddf.createXML( progConfig.getExportDir(), dbIds );
        // wDial.setVisible( false );
        showSuccessBox( stringsBundle.getString( "fileManagerPanel.succesExport" ) );
      }
    }
    catch( Exception ex )
    {
      LOGGER.severe( ex.getLocalizedMessage() );
      if( LOGGER.getLevel().intValue() < Level.CONFIG.intValue() )
      {
        ex.printStackTrace();
      }
      showWarnBox( stringsBundle.getString( "fileManagerPanel.notSuccesExport" ) );
      return;
    }
    finally
    {
      if( wDial != null )
      {
        wDial.setVisible( false );
        wDial.dispose();
        wDial = null;
      }
    }
  }

  /**
   * 
   * Aus der Database Tauchgänge löschen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 16.08.2012
   * @param sets
   */
  private void deleteDatasetsForIdx( int[] sets )
  {
    if( device == null )
    {
      LOGGER.severe( "no device known in programobject! abort function!" );
      return;
    }
    // Sets lesen, Array für DatenbankID erzeugen
    int[] dbIds = getDbIdsForTableIdx( sets );
    //
    // so, jetzt sollte ich die ID haben, löschen angehen
    //
    dbUtil.deleteAllSetsForIdsLog( dbIds );
  }

  private void fillDiveTable( String cDevice )
  {
    DateTime dateTime;
    long javaTime;
    int row = 0;
    int dbId = -1;
    //
    device = cDevice;
    if( device != null )
    {
      LOGGER.log( Level.FINE, "search dive list for device <" + device + ">..." );
      // Eine Liste der Dives lesen
      LOGGER.log( Level.FINE, "read dive list for device from DB..." );
      Vector<String[]> entrys = dbUtil.getDiveListForDeviceLog( device );
      if( entrys.size() < 1 )
      {
        LOGGER.log( Level.INFO, "no dives for device <" + device + "/" + dbUtil.getAliasForNameConn( device ) + "> found in DB." );
        return;
      }
      //
      // Objekt für das Modell erstellen
      // [0] DIVENUMBERONSPX
      // [1] Start Datum und Zeiten localisiert
      // [2] Max Tiefe
      // [3] Länge
      // [4] DBID (ab 04 zeigt die Tabelle mit dem Tabellenmodell DiveExportTableModel nix mehr an!)
      String[][] diveEntrys = new String[entrys.size()][5];
      // die erfragten details zurechtrücken
      for( Enumeration<String[]> enu = entrys.elements(); enu.hasMoreElements(); )
      {
        // Felder sind:
        // [0] H_DIVEID,
        // [1] H_H_DIVENUMBERONSPX
        // [2] H_STARTTIME als unix timestamp
        String[] origSet = enu.nextElement();
        // zusammenbauen fuer Anzeige
        // SPX-DiveNumber für vierstellige Anzeige
        diveEntrys[row][1] = String.format( "%4s", origSet[1] );
        // Die UTC-Zeit als ASCII/UNIX wieder zu der originalen Zeit für Java zusammenbauen
        try
        {
          // Die Tauchgangszeit formatieren
          javaTime = Long.parseLong( origSet[2] ) * 1000;
          dbId = Integer.parseInt( origSet[0] );
          dateTime = new DateTime( javaTime );
          diveEntrys[row][4] = origSet[0];
          diveEntrys[row][0] = origSet[1];
          diveEntrys[row][1] = dateTime.toString( stringsBundle.getString( "MainCommGUI.timeFormatterString" ) );
          // jetzt will ich alle Kopfdaten, die gespeichert sind
          // [0] H_DIVEID,
          // [1] H_DIVENUMBERONSPX,
          // [2] H_FILEONSPX,
          // [3] H_DEVICEID,
          // [4] H_STARTTIME,
          // [5] H_HADSEND,
          // [6] H_FIRSTTEMP,
          // [7] H_LOWTEMP,
          // [8] H_MAXDEPTH,
          // [9] H_SAMPLES,
          // [10] H_DIVELENGTH,
          // [11] H_UNITS,
          String[] headers = dbUtil.getHeadDiveDataFromIdAsSTringLog( dbId );
          if( headers[11].equals( "METRIC" ) )
          {
            diveEntrys[row][2] = headers[8] + " m";
          }
          else
          {
            diveEntrys[row][2] = headers[8] + " ft";
          }
          diveEntrys[row][3] = headers[10] + " min";
        }
        catch( NumberFormatException ex )
        {
          LOGGER.log( Level.SEVERE, "Number format exception (value=<" + origSet[1] + ">: <" + ex.getLocalizedMessage() + ">" );
          diveEntrys[row][0] = "error";
        }
        finally
        {
          row++;
        }
      }
      // aufräumen
      entrys.clear();
      entrys = null;
      // die Tabelle initialisieren
      String[] title = new String[5];
      title[0] = stringsBundle.getString( "fileManagerPanel.diveListHeaders.numberOnSpx" );
      title[1] = stringsBundle.getString( "fileManagerPanel.diveListHeaders.startTime" );
      title[2] = stringsBundle.getString( "fileManagerPanel.diveListHeaders.maxDepth" );
      title[3] = stringsBundle.getString( "fileManagerPanel.diveListHeaders.diveLen" );
      title[4] = stringsBundle.getString( "fileManagerPanel.diveListHeaders.dbId" );
      FileManagerTableModel mTable = new FileManagerTableModel( diveEntrys, title );
      dataViewTable.setModel( mTable );
      // jetzt noch die rechte Spalte verschönern. Rechtsbündig und schmal bitte!
      DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
      rightRenderer.setHorizontalAlignment( JLabel.RIGHT );
      TableColumn column = dataViewTable.getColumnModel().getColumn( 0 );
      column.setPreferredWidth( 60 );
      column.setMaxWidth( 120 );
      column.setCellRenderer( rightRenderer );
    }
    else
    {
      LOGGER.log( Level.FINE, "no device found...." );
    }
  }

  /**
   * 
   * Fenster vorbereiten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.08.2012
   * @param connDev
   * @throws Exception
   */
  public void initData( String connDev ) throws Exception
  {
    //
    // entsorge für alle Fälle das Zeug von vorher
    //
    deviceComboBox.removeActionListener( this );
    releaseLists();
    if( connDev == null )
    {
      LOGGER.log( Level.FINE, "init export objects whitout active Device" );
    }
    else
    {
      LOGGER.log( Level.FINE, "init export objects Device <" + connDev + ">..." );
    }
    //
    // Ist überhaupt eine Datenbank zum Auslesen vorhanden?
    //
    if( dbUtil == null || ( !dbUtil.isOpenDB() ) )
    {
      throw new Exception( "no database object initiated!" );
    }
    //
    // Lese eine Liste der Geräte/Aliase
    //
    Vector<String[]> entrys = dbUtil.getAliasDataConn();
    if( entrys == null )
    {
      LOGGER.log( Level.WARNING, "no devices found in database." );
      if( stringsBundle != null )
      {
        showWarnBox( stringsBundle.getString( "spx42LogGraphPanel.warnBox.noDevicesInDatabase" ) );
      }
      return;
    }
    //
    // fülle deviceComboBox
    //
    DeviceComboBoxModel portBoxModel = new DeviceComboBoxModel( entrys );
    deviceComboBox.setModel( portBoxModel );
    if( entrys.isEmpty() )
    {
      // sind keine Geräte verbunden, nix selektieren
      deviceComboBox.setSelectedIndex( -1 );
    }
    else
    {
      // Alle Einträge testen
      int index = 0;
      for( String[] entr : entrys )
      {
        if( entr[0].equals( connDev ) )
        {
          deviceComboBox.setSelectedIndex( index );
          LOGGER.log( Level.FINE, "device found and set as index für combobox..." );
          break;
        }
        index++;
      }
    }
    //
    // gibt es einen Eintrag in der Combobox müßte ja deren entsprechende Liste von
    // Einträgen gelesen werden....
    //
    if( deviceComboBox.getSelectedIndex() != -1 )
    {
      connDev = ( ( DeviceComboBoxModel )deviceComboBox.getModel() ).getDeviceIdAt( deviceComboBox.getSelectedIndex() );
      if( connDev != null )
      {
        fillDiveTable( connDev );
      }
    }
    deviceComboBox.addActionListener( this );
  }

  private void initPanel()
  {
    setPreferredSize( new Dimension( 796, 504 ) );
    setLayout( new BorderLayout( 0, 0 ) );
    JPanel topComboBoxPanel = new JPanel();
    topComboBoxPanel.setPreferredSize( new Dimension( 10, 40 ) );
    add( topComboBoxPanel, BorderLayout.NORTH );
    deviceComboBox = new JComboBox();
    deviceComboBox.setMaximumRowCount( 26 );
    deviceComboBox.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
    deviceComboBox.setActionCommand( "change_device_to_display" );
    deviceComboBox.addMouseMotionListener( mListener );
    GroupLayout gl_topComboBoxPanel = new GroupLayout( topComboBoxPanel );
    gl_topComboBoxPanel.setHorizontalGroup( gl_topComboBoxPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_topComboBoxPanel.createSequentialGroup().addGap( 36 ).addComponent( deviceComboBox, GroupLayout.PREFERRED_SIZE, 277, GroupLayout.PREFERRED_SIZE ).addGap( 483 ) ) );
    gl_topComboBoxPanel.setVerticalGroup( gl_topComboBoxPanel.createParallelGroup( Alignment.LEADING )
            .addGroup(
                    gl_topComboBoxPanel.createSequentialGroup().addGap( 5 )
                            .addComponent( deviceComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) ) );
    topComboBoxPanel.setLayout( gl_topComboBoxPanel );
    JPanel bottomButtonPanel = new JPanel();
    bottomButtonPanel.setPreferredSize( new Dimension( 10, 60 ) );
    add( bottomButtonPanel, BorderLayout.SOUTH );
    cancelButton = new JButton( "CANCEL" );
    cancelButton.setHorizontalAlignment( SwingConstants.LEFT );
    cancelButton.setIconTextGap( 15 );
    cancelButton.setIcon( new ImageIcon( spx42FileManagerPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/quit.png" ) ) );
    cancelButton.setEnabled( false );
    cancelButton.setPreferredSize( new Dimension( 180, 40 ) );
    cancelButton.setMaximumSize( new Dimension( 160, 40 ) );
    cancelButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    cancelButton.setForeground( new Color( 0, 100, 0 ) );
    cancelButton.setBackground( new Color( 152, 251, 152 ) );
    cancelButton.setActionCommand( "cancel_selection" );
    cancelButton.addMouseMotionListener( mListener );
    cancelButton.addActionListener( this );
    deleteButton = new JButton( "DELETE" );
    deleteButton.setHorizontalAlignment( SwingConstants.LEFT );
    deleteButton.setIconTextGap( 15 );
    deleteButton.setIcon( new ImageIcon( spx42FileManagerPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/36.png" ) ) );
    deleteButton.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
    deleteButton.setEnabled( false );
    deleteButton.setPreferredSize( new Dimension( 180, 40 ) );
    deleteButton.setMaximumSize( new Dimension( 160, 40 ) );
    deleteButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    deleteButton.setForeground( Color.RED );
    deleteButton.setBackground( new Color( 255, 192, 203 ) );
    deleteButton.setActionCommand( "delete_selection" );
    deleteButton.addMouseMotionListener( mListener );
    deleteButton.addActionListener( this );
    exportButton = new JButton( "EXPORT" );
    exportButton.setHorizontalAlignment( SwingConstants.LEFT );
    exportButton.setIconTextGap( 15 );
    exportButton.setIcon( new ImageIcon( spx42FileManagerPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Download.png" ) ) );
    exportButton.setPreferredSize( new Dimension( 180, 40 ) );
    exportButton.setMaximumSize( new Dimension( 160, 40 ) );
    exportButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    exportButton.setForeground( new Color( 0, 0, 205 ) );
    exportButton.setEnabled( false );
    exportButton.setBackground( new Color( 0, 255, 255 ) );
    exportButton.setActionCommand( "export_selection" );
    exportButton.addMouseMotionListener( mListener );
    exportButton.addActionListener( this );
    GroupLayout gl_bottomButtonPanel = new GroupLayout( bottomButtonPanel );
    gl_bottomButtonPanel.setHorizontalGroup( gl_bottomButtonPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_bottomButtonPanel.createSequentialGroup().addGap( 36 ).addComponent( cancelButton, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE )
                    .addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( deleteButton, GroupLayout.PREFERRED_SIZE, 199, GroupLayout.PREFERRED_SIZE )
                    .addPreferredGap( ComponentPlacement.RELATED, 127, Short.MAX_VALUE ).addComponent( exportButton, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE )
                    .addGap( 42 ) ) );
    gl_bottomButtonPanel.setVerticalGroup( gl_bottomButtonPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_bottomButtonPanel
                    .createSequentialGroup()
                    .addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                    .addGroup(
                            gl_bottomButtonPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( deleteButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( exportButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE ) ).addGap( 20 ) ) );
    bottomButtonPanel.setLayout( gl_bottomButtonPanel );
    JPanel dataListPanel = new JPanel();
    add( dataListPanel, BorderLayout.CENTER );
    JScrollPane contentScrollPane = new JScrollPane();
    GroupLayout gl_dataListPanel = new GroupLayout( dataListPanel );
    gl_dataListPanel.setHorizontalGroup( gl_dataListPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_dataListPanel.createSequentialGroup().addGap( 35 ).addComponent( contentScrollPane, GroupLayout.PREFERRED_SIZE, 720, GroupLayout.PREFERRED_SIZE )
                    .addContainerGap( 41, Short.MAX_VALUE ) ) );
    gl_dataListPanel.setVerticalGroup( gl_dataListPanel.createParallelGroup( Alignment.TRAILING ).addGroup( Alignment.LEADING,
            gl_dataListPanel.createSequentialGroup().addContainerGap().addComponent( contentScrollPane, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE ).addContainerGap() ) );
    dataViewTable = new JTable();
    dataViewTable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
    dataViewTable.getSelectionModel().addListSelectionListener( this );
    dataViewTable.addMouseMotionListener( mListener );
    contentScrollPane.setViewportView( dataViewTable );
    JLabel lblNewLabel = new JLabel( "DATALABEL" );
    contentScrollPane.setColumnHeaderView( lblNewLabel );
    dataListPanel.setLayout( gl_dataListPanel );
  }

  /**
   * 
   * Tabelle leeren
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.08.2012
   */
  private void releaseLists()
  {
    dataViewTable.setModel( new FileManagerTableModel( null, null ) );
    deleteButton.setEnabled( false );
    cancelButton.setEnabled( false );
  }

  /**
   * 
   * Sprachtexte einstellen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.08.2012
   * @param stringsBundle
   * @return ok oder nicht
   */
  public int setLanguageStrings( ResourceBundle stringsBundle )
  {
    int selectedIndex;
    //
    this.stringsBundle = stringsBundle;
    try
    {
      // merke mir das ausgewählte Teilchen
      selectedIndex = deviceComboBox.getSelectedIndex();
      // Sprache richten
      deviceComboBox.setToolTipText( stringsBundle.getString( "fileManagerPanel.deviceComboBox.tooltiptext" ) );
      dataViewTable.setToolTipText( stringsBundle.getString( "fileManagerPanel.dataViewTable.tooltiptext" ) );
      cancelButton.setText( stringsBundle.getString( "fileManagerPanel.cancelButton.text" ) );
      cancelButton.setToolTipText( stringsBundle.getString( "fileManagerPanel.cancelButton.tooltiptext" ) );
      deleteButton.setText( stringsBundle.getString( "fileManagerPanel.deleteButton.text" ) );
      deleteButton.setToolTipText( stringsBundle.getString( "fileManagerPanel.deleteButton.tooltiptext" ) );
      exportButton.setText( stringsBundle.getString( "fileManagerPanel.exportButton.text" ) );
      exportButton.setToolTipText( stringsBundle.getString( "fileManagerPanel.exportButton.tooltiptext" ) );
      // jetzt die Box neu befüllen, mit Trick 17...
      releaseLists();
      deviceComboBox.setSelectedIndex( -1 );
      // wenn da eine Auswahl war, wieder setzen und dann wird die Box auch neu befüllt
      if( selectedIndex != -1 )
      {
        deviceComboBox.setSelectedIndex( selectedIndex );
      }
    }
    catch( NullPointerException ex )
    {
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( -1 );
    }
    catch( MissingResourceException ex )
    {
      System.out.println( "ERROR set language strings - the given key can be found <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    catch( ClassCastException ex )
    {
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    return( 1 );
  }

  private int showAskBox( String msg )
  {
    try
    {
      Object[] options =
      { stringsBundle.getString( "fileManagerPanel.showAskBox.no" ), stringsBundle.getString( "fileManagerPanel.showAskBox.yes" ) };
      return JOptionPane.showOptionDialog( this, msg, stringsBundle.getString( "fileManagerPanel.showAskBox.headline" ), JOptionPane.OK_CANCEL_OPTION,
              JOptionPane.QUESTION_MESSAGE, null, options, options[1] );
    }
    catch( NullPointerException ex )
    {
      LOGGER.log( Level.SEVERE, "ERROR showAskBox <" + ex.getMessage() + "> ABORT!" );
      return JOptionPane.CANCEL_OPTION;
    }
    catch( MissingResourceException ex )
    {
      LOGGER.log( Level.SEVERE, "ERROR showAskBox <" + ex.getMessage() + "> ABORT!" );
      return JOptionPane.CANCEL_OPTION;
    }
    catch( ClassCastException ex )
    {
      LOGGER.log( Level.SEVERE, "ERROR showAskBox <" + ex.getMessage() + "> ABORT!" );
      return JOptionPane.CANCEL_OPTION;
    }
  }

  /**
   * 
   * Zeige eine Warnung an!
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 30.08.2012
   * @param msg
   */
  private void showWarnBox( String msg )
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Abort.png" ) );
      JOptionPane.showMessageDialog( this, msg, stringsBundle.getString( "MainCommGUI.warnDialog.headline" ), JOptionPane.WARNING_MESSAGE, icon );
    }
    catch( NullPointerException ex )
    {
      LOGGER.log( Level.SEVERE, "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      LOGGER.log( Level.SEVERE, "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      LOGGER.log( Level.SEVERE, "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }

  /**
   * 
   * ERfolgreich beendet-Box
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 30.08.2012
   * @param msg
   */
  private void showSuccessBox( String msg )
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/94.png" ) );
      JOptionPane.showMessageDialog( this, msg, stringsBundle.getString( "MainCommGUI.successDialog.headline" ), JOptionPane.OK_OPTION, icon );
    }
    catch( NullPointerException ex )
    {
      LOGGER.log( Level.SEVERE, "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      LOGGER.log( Level.SEVERE, "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      LOGGER.log( Level.SEVERE, "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }

  @Override
  public void valueChanged( ListSelectionEvent ev )
  {
    if( !ev.getValueIsAdjusting() )
    {
      // es haben sich selektierte Spalten geändert?
      LOGGER.fine( "selected Rows changed...." );
      // es war meine Datentabelle
      if( dataViewTable.getSelectedRowCount() > 0 )
      {
        LOGGER.fine( String.format( "selected Rows <%02d>....", dataViewTable.getSelectedRowCount() ) );
        deleteButton.setEnabled( true );
        cancelButton.setEnabled( true );
        exportButton.setEnabled( true );
      }
      else
      {
        LOGGER.fine( "NO selected Rows...." );
        deleteButton.setEnabled( false );
        cancelButton.setEnabled( false );
        exportButton.setEnabled( false );
      }
    }
    else
    {
      LOGGER.fine( "selected Rows changing in progress...." );
    }
  }
}
