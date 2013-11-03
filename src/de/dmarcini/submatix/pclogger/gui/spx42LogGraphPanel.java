package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.joda.time.DateTime;

import de.dmarcini.submatix.pclogger.lang.LangStrings;
import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.DeviceComboBoxModel;
import de.dmarcini.submatix.pclogger.utils.GasComputeUnit;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.LogListComboBoxModel;
import de.dmarcini.submatix.pclogger.utils.MinuteFormatter;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

public class spx42LogGraphPanel extends JPanel implements ActionListener
{
  /**
   * 
   */
  private static final long    serialVersionUID  = 1L;
  private static int           GRAPH_TEMPERATURE = 0;
  private static int           GRAPH_PPO2ALL     = 1;
  private static int           GRAPH_PPO2_01     = 2;
  private static int           GRAPH_PPO2_02     = 3;
  private static int           GRAPH_PPO2_03     = 4;
  private static int           GRAPH_SETPOINT    = 5;
  private static int           GRAPH_HE          = 6;
  private static int           GRAPH_N2          = 7;
  private static int           GRAPH_NULLTIME    = 8;
  private static int           GRAPH_DEPTH       = 9;
  protected Logger             lg                = null;
  private LogDerbyDatabaseUtil databaseUtil      = null;
  private ChartPanel           chartPanel        = null;
  private int                  showingUnitSystem = ProjectConst.UNITS_DEFAULT;
  private int                  savedUnitSystem   = ProjectConst.UNITS_DEFAULT;
  private int                  showingDbIdForDiveWasShowing;
  private String               maxDepthLabelString;
  private String               coldestLabelString;
  private String               diveLenLabelString;
  private String               depthUnitName;
  private String               tempUnitName;
  private String               pressureUnitName;
  private JPanel               topPanel;
  private JPanel               bottomPanel;
  @SuppressWarnings( "rawtypes" )
  private JComboBox            deviceComboBox;
  @SuppressWarnings( "rawtypes" )
  private JComboBox            diveSelectComboBox;
  private JButton              computeGraphButton;
  private JLabel               maxDepthValueLabel;
  private JLabel               coldestTempValueLabel;
  private JLabel               diveLenValueLabel;
  private JButton              detailGraphButton;
  private JLabel               notesLabel;
  private JButton              notesEditButton;
  private JLabel               diluentLabel;

  @SuppressWarnings( "unused" )
  private spx42LogGraphPanel()
  {
    setBackground( Color.WHITE );
    setPreferredSize( new Dimension( 796, 504 ) );
    initPanel();
  }

  /**
   * Create the panel.
   * 
   * @param _dbUtil
   */
  public spx42LogGraphPanel( final LogDerbyDatabaseUtil _dbUtil )
  {
    this.lg = SpxPcloggerProgramConfig.LOGGER;
    lg.debug( "constructor..." );
    this.databaseUtil = _dbUtil;
    initPanel();
    showingDbIdForDiveWasShowing = -1;
  }

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    String entry = null;
    int dbId;
    String device;
    //
    // /////////////////////////////////////////////////////////////////////////
    // Button
    if( ev.getSource() instanceof JButton )
    {
      // JButton srcButton = ( JButton )ev.getSource();
      // /////////////////////////////////////////////////////////////////////////
      // Anzeigebutton?
      if( cmd.equals( "show_log_graph" ) )
      {
        lg.debug( "show log graph initiated." );
        // welches Device ?
        if( deviceComboBox.getSelectedIndex() < 0 )
        {
          // kein Gerät ausgewählt
          lg.warn( "no device selected." );
          return;
        }
        // welchen Tauchgang?
        if( diveSelectComboBox.getSelectedIndex() < 0 )
        {
          lg.warn( "no dive selected." );
          return;
        }
        device = ( ( DeviceComboBoxModel )deviceComboBox.getModel() ).getDeviceSerialAt( deviceComboBox.getSelectedIndex() );
        dbId = ( ( LogListComboBoxModel )diveSelectComboBox.getModel() ).getDatabaseIdAt( diveSelectComboBox.getSelectedIndex() );
        lg.debug( "Select Device-Serial: " + device + ", DBID: " + dbId );
        if( dbId < 0 )
        {
          lg.error( "can't find database id for dive." );
          return;
        }
        makeGraphForLog( dbId, device );
        return;
      }
      else if( cmd.equals( "set_detail_for_show_graph" ) )
      {
        lg.debug( "select details for log selected." );
        SelectGraphDetailsDialog sgd = new SelectGraphDetailsDialog();
        if( sgd.showModal() )
        {
          lg.debug( "dialog returned 'true' => change propertys..." );
          computeGraphButton.doClick();
        }
      }
      else if( cmd.equals( "edit_notes_for_dive" ) )
      {
        if( chartPanel == null || showingDbIdForDiveWasShowing == -1 )
        {
          lg.warn( "it was not showing a dive! do nothing!" );
          return;
        }
        lg.debug( "edit a note for this dive..." );
        showNotesEditForm( showingDbIdForDiveWasShowing );
      }
      else
      {
        lg.warn( "unknown button command <" + cmd + "> recived." );
      }
      return;
    }
    // /////////////////////////////////////////////////////////////////////////
    // Combobox
    else if( ev.getSource() instanceof JComboBox<?> )
    {
      @SuppressWarnings( "unchecked" )
      JComboBox<String> srcBox = ( JComboBox<String> )ev.getSource();
      // /////////////////////////////////////////////////////////////////////////
      // Gerät zur Grafischen Darstellung auswählen
      if( cmd.equals( "change_device_to_display" ) )
      {
        if( srcBox.getModel() instanceof DeviceComboBoxModel )
        {
          entry = ( ( DeviceComboBoxModel )srcBox.getModel() ).getDeviceSerialAt( srcBox.getSelectedIndex() );
          lg.debug( "device <" + entry + ">...Index: <" + srcBox.getSelectedIndex() + ">" );
          fillDiveComboBox( entry );
        }
      }
      // /////////////////////////////////////////////////////////////////////////
      // Dive zur Grafischen Darstellung auswählen
      else if( cmd.equals( "change_dive_to_display" ) )
      {
        entry = ( String )srcBox.getSelectedItem();
        lg.debug( "dive <" + entry + ">...Index: <" + srcBox.getSelectedIndex() + ">" );
        // fillDiveComboBox( entry );
      }
      else
      {
        lg.warn( "unknown combobox command <" + cmd + "> recived." );
      }
      return;
    }
    else
    {
      lg.warn( "unknown action command <" + cmd + "> recived." );
    }
  }

  /**
   * Die Combobox mit einem leeren Modell ausstatten... Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.07.2012
   */
  @SuppressWarnings( "unchecked" )
  public void clearDiveComboBox()
  {
    Vector<String[]> diveEntrys = new Vector<String[]>();
    LogListComboBoxModel listBoxModel = new LogListComboBoxModel( diveEntrys );
    diveSelectComboBox.setModel( listBoxModel );
  }

  /**
   * Erzeuge ein XY-Dataset aus einem Vector Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.07.2012
   * @param diveList
   * @param x
   *          X-Achse (Sekundenoffset)
   * @param y
   *          Y-Achse
   * @return Datenset
   */
  private XYDataset createXYDataset( String scalaTitle, Vector<Integer[]> diveList, int unitToConvert, int x, int y )
  {
    final XYSeries series = new XYSeries( scalaTitle );
    double secounds = 0;
    Integer[] dataSet;
    //
    // alle Datensätze abklappern
    //
    for( Enumeration<Integer[]> enu = diveList.elements(); enu.hasMoreElements(); )
    {
      dataSet = enu.nextElement();
      if( y == LogDerbyDatabaseUtil.DEPTH )
      {
        double fDepth = new Double( dataSet[y] );
        fDepth = 0.00 - ( fDepth / 10.00 );
        // muss konvertiert werden?
        if( unitToConvert == ProjectConst.UNITS_IMPERIAL )
        {
          // metrisch-> imperial konvertieren
          // 1 foot == 30,48 cm == 0.3048 Meter
          fDepth = fDepth / 0.3048;
        }
        else if( unitToConvert == ProjectConst.UNITS_METRIC )
        {
          // imperial -> metrisch
          // 1 foot == 30,48 cm == 0.3048 Meter
          fDepth = fDepth * 0.3048;
        }
        series.add( secounds, fDepth );
      }
      else if( y == LogDerbyDatabaseUtil.PPO2 || y == LogDerbyDatabaseUtil.PPO2_01 || y == LogDerbyDatabaseUtil.PPO2_02 || y == LogDerbyDatabaseUtil.PPO2_03 )
      {
        double fPpo2 = new Double( dataSet[y] / 1000.00 );
        if( unitToConvert == ProjectConst.UNITS_IMPERIAL )
        {
          // metrisch -> imperial
          // 1 bar = 14,504 psi
          fPpo2 = fPpo2 * 14.504;
        }
        else if( unitToConvert == ProjectConst.UNITS_METRIC )
        {
          // imperial -> metrisch
          // 1 psi = 0,0689 bar
          fPpo2 = fPpo2 * 0.0689;
        }
        series.add( secounds, fPpo2 );
      }
      else if( y == LogDerbyDatabaseUtil.SETPOINT )
      {
        double fSetPoint = new Double( dataSet[y] / 10.00 );
        if( unitToConvert == ProjectConst.UNITS_IMPERIAL )
        {
          // metrisch -> imperial
          // 1 bar = 14,504 psi
          fSetPoint = fSetPoint * 14.504;
        }
        else if( unitToConvert == ProjectConst.UNITS_METRIC )
        {
          // imperial -> metrisch
          // 1 psi = 0,0689 bar
          fSetPoint = fSetPoint * 0.0689;
        }
        series.add( secounds, fSetPoint );
      }
      else if( y == LogDerbyDatabaseUtil.TEMPERATURE )
      {
        double fTemp = new Double( dataSet[y] );
        // muss konvertiert werden?
        if( unitToConvert == ProjectConst.UNITS_IMPERIAL )
        {
          // metrisch-> imperial konvertieren
          // t °F = 5⁄9 (t − 32) °C
          fTemp = ( 5.0 / 9.0 ) * ( fTemp - 32.0 );
        }
        else if( unitToConvert == ProjectConst.UNITS_METRIC )
        {
          // imperial -> metrisch
          // t °C = (9⁄5 t + 32) °F
          fTemp = ( ( 5.0 / 9.0 ) * fTemp ) + 32.0;
        }
        series.add( secounds, fTemp );
      }
      else
      {
        series.add( secounds, new Double( dataSet[y] ) );
      }
      // das offset/schrittweite ist in Sekunden gespeichert
      secounds += ( dataSet[x] );
    }
    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries( series );
    return dataset;
  }

  /**
   * Anhand des Alias des Gerätes die Tauchgangsliste füllen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.07.2012
   * @param deviceAlias
   */
  @SuppressWarnings( "unchecked" )
  private void fillDiveComboBox( String cDevice )
  {
    String device;
    DateTime dateTime;
    long javaTime;
    //
    device = cDevice;
    if( device != null )
    {
      lg.debug( "search dive list for device <" + device + ">..." );
      releaseGraph();
      // Eine Liste der Dives lesen
      lg.debug( "read dive list for device from DB..." );
      Vector<String[]> entrys = databaseUtil.getDiveListForDeviceLog( device );
      if( entrys.size() < 1 )
      {
        lg.info( "no dives for device <" + cDevice + "/" + databaseUtil.getAliasForNameConn( device ) + "> found in DB." );
        clearDiveComboBox();
        return;
      }
      //
      // Objekt für das Modell erstellen
      Vector<String[]> diveEntrys = new Vector<String[]>();
      // die erfragten details zurechtrücken
      // Felder sind:
      // H_DIVEID,
      // H_H_DIVENUMBERONSPX
      // H_STARTTIME,
      for( Enumeration<String[]> enu = entrys.elements(); enu.hasMoreElements(); )
      {
        String[] origSet = enu.nextElement();
        // zusammenbauen fuer Anzeige
        String[] elem = new String[3];
        // SPX-DiveNumber etwas einrücken, für vierstellige Anzeige
        elem[0] = origSet[0];
        elem[1] = String.format( "%4s", origSet[1] );
        // Die UTC-Zeit als ASCII/UNIX wieder zu der originalen Zeit für Java zusammenbauen
        try
        {
          javaTime = Long.parseLong( origSet[2] ) * 1000;
          dateTime = new DateTime( javaTime );
          elem[2] = dateTime.toString( LangStrings.getString( "MainCommGUI.timeFormatterString" ) );
        }
        catch( NumberFormatException ex )
        {
          lg.error( "Number format exception (value=<" + origSet[1] + ">: <" + ex.getLocalizedMessage() + ">" );
          elem[1] = "error";
        }
        diveEntrys.add( elem );
      }
      // aufräumen
      entrys.clear();
      entrys = null;
      // die box initialisieren
      LogListComboBoxModel listBoxModel = new LogListComboBoxModel( diveEntrys );
      diveSelectComboBox.setModel( listBoxModel );
      if( diveEntrys.size() > 0 )
      {
        diveSelectComboBox.setSelectedIndex( 0 );
      }
    }
    else
    {
      lg.debug( "no device found...." );
    }
  }

  /**
   * Masseinheiten für Labels herausfinden Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.08.2012
   * @param showingUnitSystem
   * @param savedUnitSystem
   * @return Strings für tiefe und temperatur
   */
  private String[] getUnitsLabel( int progUnitSystem, int diveUnitSystem )
  {
    String[] labels = new String[3];
    //
    // Bei UNITS_DEFAULT gehts nach savedUnitSystem
    if( progUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      if( diveUnitSystem == ProjectConst.UNITS_IMPERIAL )
      {
        // also, ist der Tauchgang imperial geloggt
        labels[0] = LangStrings.getString( "spx42LogGraphPanel.unit.imperial.lenght" );
        labels[1] = LangStrings.getString( "spx42LogGraphPanel.unit.imperial.temperature" );
        labels[2] = LangStrings.getString( "spx42LogGraphPanel.unit.imperial.pressure" );
      }
      else
      {
        // der tauhcgang ist metrisch geloggt.
        labels[0] = LangStrings.getString( "spx42LogGraphPanel.unit.metric.lenght" );
        labels[1] = LangStrings.getString( "spx42LogGraphPanel.unit.metric.temperature" );
        labels[2] = LangStrings.getString( "spx42LogGraphPanel.unit.metric.pressure" );
      }
    }
    else if( progUnitSystem == ProjectConst.UNITS_METRIC )
    {
      // der User wünscht Metrische Anzeige
      labels[0] = LangStrings.getString( "spx42LogGraphPanel.unit.metric.lenght" );
      labels[1] = LangStrings.getString( "spx42LogGraphPanel.unit.metric.temperature" );
      labels[2] = LangStrings.getString( "spx42LogGraphPanel.unit.metric.pressure" );
    }
    else
    {
      // der User wünscht imperiale anzeige
      labels[0] = LangStrings.getString( "spx42LogGraphPanel.unit.imperial.lenght" );
      labels[1] = LangStrings.getString( "spx42LogGraphPanel.unit.imperial.temperature" );
      labels[2] = LangStrings.getString( "spx42LogGraphPanel.unit.imperial.pressure" );
    }
    return( labels );
  }

  /**
   * Initialisiere alle Felder, Objekte, die zur grafischen Darstellung gebraucht werden Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 26.06.2012
   * @param connDev
   *          Falls verbunden, das aktuelle Gerät übergeben (für Voreinstellungen)
   * @throws Exception
   */
  @SuppressWarnings( "unchecked" )
  public void initGraph( String connDev ) throws Exception
  {
    //
    // entsorge für alle Fälle das Zeug von vorher
    releaseGraph();
    if( connDev == null )
    {
      lg.debug( "init graphic objects whitout active Device" );
    }
    else
    {
      lg.debug( "init graphic objects Device <" + connDev + ">..." );
    }
    //
    // Ist überhaupt eine Datenbank zum Auslesen vorhanden?
    //
    if( databaseUtil == null || ( !databaseUtil.isOpenDB() ) )
    {
      throw new Exception( "no database object initiated!" );
    }
    //
    // Lese eine Liste der Tauchgänge für dieses Gerät
    //
    Vector<String[]> entrys = databaseUtil.getAliasDataConn();
    if( entrys == null )
    {
      lg.warn( "no devices found in database." );
      showWarnBox( LangStrings.getString( "spx42LogGraphPanel.warnBox.noDevicesInDatabase" ) );
      return;
    }
    //
    // fülle deviceComboBox
    //
    DeviceComboBoxModel portBoxModel = new DeviceComboBoxModel( entrys );
    deviceComboBox.setModel( portBoxModel );
    if( !entrys.isEmpty() )
    {
      // wen kein Gerät da ist, brauch ich nicht suchen
      if( connDev != null )
      {
        // Alle Einträge testen
        int index = 0;
        for( String[] entr : entrys )
        {
          if( entr[0].equals( connDev ) )
          {
            deviceComboBox.setSelectedIndex( index );
            lg.debug( "device found and set as index für combobox..." );
            break;
          }
          index++;
        }
      }
    }
    //
    // gibt es einen Eintrag in der Combobox müßte ja deren entsprechende Liste von
    // Einträgen gelesen werden....
    //
    if( deviceComboBox.getSelectedIndex() != -1 )
    {
      connDev = ( ( DeviceComboBoxModel )deviceComboBox.getModel() ).getDeviceSerialAt( deviceComboBox.getSelectedIndex() );
      fillDiveComboBox( connDev );
    }
  }

  /**
   * Initialisiere das Panel für die Verbindungen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   */
  private void initPanel()
  {
    setLayout( new BorderLayout( 0, 0 ) );
    setBackground( Color.WHITE );
    topPanel = new JPanel();
    topPanel.setBackground( Color.WHITE );
    add( topPanel, BorderLayout.NORTH );
    deviceComboBox = new JComboBox<String>();
    deviceComboBox.setBackground( Color.WHITE );
    deviceComboBox.setMaximumRowCount( 26 );
    deviceComboBox.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
    deviceComboBox.setActionCommand( "change_device_to_display" );
    diveSelectComboBox = new JComboBox<String>();
    diveSelectComboBox.setBackground( Color.WHITE );
    diveSelectComboBox.setMaximumRowCount( 26 );
    diveSelectComboBox.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
    diveSelectComboBox.setActionCommand( "change_dive_to_display" );
    computeGraphButton = new JButton( LangStrings.getString( "spx42LogGraphPanel.computeGraphButton.text" ) ); //$NON-NLS-1$
    computeGraphButton.setMinimumSize( new Dimension( 80, 23 ) );
    computeGraphButton.setPreferredSize( new Dimension( 80, 23 ) );
    computeGraphButton.setSize( new Dimension( 80, 23 ) );
    computeGraphButton.setMaximumSize( new Dimension( 80, 23 ) );
    computeGraphButton.setActionCommand( "show_log_graph" );
    detailGraphButton = new JButton( LangStrings.getString( "spx42LogGraphPanel.detailGraphButton.text" ) ); //$NON-NLS-1$
    detailGraphButton.setMinimumSize( new Dimension( 80, 23 ) );
    detailGraphButton.setSize( new Dimension( 80, 23 ) );
    detailGraphButton.setPreferredSize( new Dimension( 80, 23 ) );
    detailGraphButton.setMaximumSize( new Dimension( 80, 23 ) );
    detailGraphButton.setActionCommand( "set_detail_for_show_graph" );
    GroupLayout gl_topPanel = new GroupLayout( topPanel );
    gl_topPanel.setHorizontalGroup( gl_topPanel.createParallelGroup( Alignment.TRAILING ).addGroup(
            gl_topPanel.createSequentialGroup().addContainerGap().addComponent( deviceComboBox, 0, 270, Short.MAX_VALUE ).addGap( 18 )
                    .addComponent( diveSelectComboBox, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE ).addGap( 32 )
                    .addComponent( computeGraphButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addComponent( detailGraphButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ).addGap( 18 ) ) );
    gl_topPanel.setVerticalGroup( gl_topPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_topPanel
                    .createSequentialGroup()
                    .addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                    .addGroup(
                            gl_topPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( deviceComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( computeGraphButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( detailGraphButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( diveSelectComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) ) ) );
    topPanel.setLayout( gl_topPanel );
    bottomPanel = new JPanel();
    bottomPanel.setBackground( Color.WHITE );
    add( bottomPanel, BorderLayout.SOUTH );
    maxDepthValueLabel = new JLabel( "0" );
    coldestTempValueLabel = new JLabel( "0" );
    diveLenValueLabel = new JLabel( "0" );
    notesLabel = new JLabel( LangStrings.getString( "spx42LoglistPanel.diveNotesLabel.text" ) ); //$NON-NLS-1$
    notesLabel.setForeground( new Color( 0, 100, 0 ) );
    notesLabel.setFont( new Font( "Tahoma", Font.ITALIC, 12 ) );
    notesEditButton = new JButton( "..." );
    notesEditButton.setActionCommand( "edit_notes_for_dive" );
    notesEditButton.setIcon( new ImageIcon( spx42LogGraphPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/142.png" ) ) );
    notesEditButton.setForeground( new Color( 0, 100, 0 ) );
    diluentLabel = new JLabel( "" ); //$NON-NLS-1$
    diluentLabel.setForeground( new Color( 0, 0, 128 ) );
    diluentLabel.setFont( new Font( "Segoe UI", Font.PLAIN, 12 ) );
    GroupLayout gl_bottomPanel = new GroupLayout( bottomPanel );
    gl_bottomPanel.setHorizontalGroup( gl_bottomPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_bottomPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_bottomPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_bottomPanel.createSequentialGroup().addComponent( maxDepthValueLabel, GroupLayout.PREFERRED_SIZE, 206, GroupLayout.PREFERRED_SIZE )
                                                    .addGap( 18 ).addComponent( coldestTempValueLabel, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE ).addGap( 18 )
                                                    .addComponent( diveLenValueLabel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE ).addGap( 18 ) )
                                    .addGroup(
                                            Alignment.TRAILING,
                                            gl_bottomPanel
                                                    .createSequentialGroup()
                                                    .addGroup(
                                                            gl_bottomPanel.createParallelGroup( Alignment.TRAILING )
                                                                    .addComponent( diluentLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE )
                                                                    .addComponent( notesLabel, GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE ) )
                                                    .addPreferredGap( ComponentPlacement.RELATED ) ) )
                    .addComponent( notesEditButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE ).addGap( 40 ) ) );
    gl_bottomPanel.setVerticalGroup( gl_bottomPanel
            .createParallelGroup( Alignment.LEADING )
            .addGroup(
                    gl_bottomPanel
                            .createSequentialGroup()
                            .addGap( 18 )
                            .addGroup(
                                    gl_bottomPanel
                                            .createParallelGroup( Alignment.LEADING )
                                            .addGroup(
                                                    gl_bottomPanel
                                                            .createSequentialGroup()
                                                            .addGroup(
                                                                    gl_bottomPanel.createParallelGroup( Alignment.BASELINE ).addComponent( notesEditButton )
                                                                            .addComponent( notesLabel ) ).addContainerGap() )
                                            .addGroup(
                                                    Alignment.TRAILING,
                                                    gl_bottomPanel.createParallelGroup( Alignment.BASELINE ).addComponent( maxDepthValueLabel )
                                                            .addComponent( coldestTempValueLabel ).addComponent( diveLenValueLabel ) ) ) )
            .addGroup( gl_bottomPanel.createSequentialGroup().addContainerGap().addComponent( diluentLabel ) ) );
    bottomPanel.setLayout( gl_bottomPanel );
    chartPanel = null;
  }

  /**
   * Erzeuge den Graphen für die Tiefe Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.08.2012
   * @param diveList
   * @param thePlot
   */
  private void makeDepthGraph( Vector<Integer[]> diveList, XYPlot thePlot )
  {
    XYDataset depthDataSet;
    lg.debug( "create depth dataset" );
    if( showingUnitSystem == savedUnitSystem || showingUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      depthDataSet = createXYDataset( LangStrings.getString( "spx42LogGraphPanel.graph.depthScalaTitle" ) + " " + depthUnitName, diveList, ProjectConst.UNITS_DEFAULT, 0,
              LogDerbyDatabaseUtil.DEPTH );
    }
    else
    {
      depthDataSet = createXYDataset( LangStrings.getString( "spx42LogGraphPanel.graph.depthScalaTitle" ) + " " + depthUnitName, diveList, showingUnitSystem, 0,
              LogDerbyDatabaseUtil.DEPTH );
    }
    final NumberAxis depthAxis = new NumberAxis( LangStrings.getString( "spx42LogGraphPanel.graph.depthAxisTitle" ) + " " + depthUnitName );
    final XYAreaRenderer areaDepthRenderer = new XYAreaRenderer( XYAreaRenderer.AREA );
    depthAxis.setAutoRangeIncludesZero( true );
    depthAxis.setLabelPaint( new Color( ProjectConst.GRAPH_DEPTH_ACOLOR ) );
    depthAxis.setTickLabelPaint( new Color( ProjectConst.GRAPH_DEPTH_ACOLOR ) );
    thePlot.setRangeAxis( 0, depthAxis );
    thePlot.setDataset( GRAPH_DEPTH, depthDataSet );
    thePlot.mapDatasetToRangeAxis( 0, GRAPH_DEPTH );
    areaDepthRenderer.setSeriesPaint( 0, new Color( ProjectConst.GRAPH_DEPTH_RCOLOR ) );
    thePlot.setRenderer( GRAPH_DEPTH, areaDepthRenderer, true );
  }

  /**
   * Zeichne die eigentliche Grafik Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.07.2012
   * @param dbId
   *          dbid in Tabelle D_TABLE_DIVEDETAIL
   * @param device
   */
  private void makeGraphForLog( int dbId, String device )
  {
    Vector<Integer[]> diveList;
    Vector<String> diluents;
    int[] headData;
    XYPlot thePlot;
    JFreeChart logChart;
    int min, sec;
    // das alte Zeug entsorgen
    releaseGraph();
    //
    // Daten eines TG lesen
    //
    lg.debug( "read dive log from DB..." );
    diveList = databaseUtil.getDiveDataFromIdLog( dbId );
    if( diveList == null || diveList.isEmpty() )
    {
      return;
    }
    //
    // verwendete Diluents finden
    //
    diluents = getDiluentNamesFromDive( diveList );
    // Anzeigen
    String diluentString = StringUtils.join( diluents, ", " );
    diluentLabel.setText( String.format( LangStrings.getString( "spx42LogGraphPanel.diluentLabel.text" ), diluentString ) );
    lg.debug( diluents );
    //
    // Labels für Tachgangseckdaten füllen
    //
    headData = databaseUtil.getHeadDiveDataFromIdLog( dbId );
    notesLabel.setText( databaseUtil.getNotesForIdLog( dbId ) );
    showingUnitSystem = SpxPcloggerProgramConfig.unitsProperty;
    savedUnitSystem = headData[6];
    // jetzt die Strings für Masseinheiten holen
    String[] labels = getUnitsLabel( showingUnitSystem, savedUnitSystem );
    depthUnitName = labels[0];
    tempUnitName = labels[1];
    pressureUnitName = labels[2];
    //
    // entscheide ob etwas umgerechnet werden sollte
    //
    if( showingUnitSystem == savedUnitSystem || showingUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      // nein, alles schick
      maxDepthValueLabel.setText( String.format( maxDepthLabelString, ( headData[3] / 10.0 ), depthUnitName ) );
      coldestTempValueLabel.setText( String.format( coldestLabelString, ( headData[2] / 10.0 ), tempUnitName ) );
    }
    else
    {
      // umrechnen!
      if( showingUnitSystem == ProjectConst.UNITS_IMPERIAL )
      {
        // metrisch-> imperial konvertieren
        // 1 foot == 30,48 cm == 0.3048 Meter
        maxDepthValueLabel.setText( String.format( maxDepthLabelString, ( headData[3] / 10.0 ) / 0.3048, depthUnitName ) );
        // t °F = 5⁄9 (t − 32) °C
        coldestTempValueLabel.setText( String.format( coldestLabelString, ( 5.0 / 9.0 ) * ( ( headData[2] / 10.0 ) - 32 ), tempUnitName ) );
      }
      else
      {
        maxDepthValueLabel.setText( String.format( maxDepthLabelString, ( headData[3] / 10.0 ) * 0.3048, depthUnitName ) );
        // t °C = (9⁄5 t + 32) °F
        coldestTempValueLabel.setText( String.format( coldestLabelString, ( ( 9.0 / 5.0 ) * ( headData[2] / 10.0 ) ) + 32, tempUnitName ) );
      }
    }
    min = headData[5] / 60;
    sec = headData[5] % 60;
    diveLenValueLabel.setText( String.format( diveLenLabelString, min, sec, "min" ) );
    //
    // einen Plot machen (Grundlage des Diagramms)
    //
    lg.debug( "create graph..." );
    thePlot = new XYPlot();
    //
    // Eigenschaften definieren
    //
    thePlot.setBackgroundPaint( Color.lightGray );
    thePlot.setDomainGridlinesVisible( true );
    thePlot.setDomainGridlinePaint( Color.white );
    thePlot.setRangeGridlinesVisible( true );
    thePlot.setRangeGridlinePaint( Color.white );
    thePlot.setDomainPannable( true );
    thePlot.setRangePannable( false );
    //
    // ein Chart zur Anzeige in einem Panel erzeugen
    //
    logChart = new JFreeChart( LangStrings.getString( "spx42LogGraphPanel.graph.chartTitle" ), thePlot );
    logChart.setAntiAlias( true );
    logChart.addSubtitle( new TextTitle( LangStrings.getString( "spx42LogGraphPanel.graph.chartSubTitle" ) ) );
    // ein Thema zufügen, damit ich eigene Farben einbauen kann
    ChartUtilities.applyCurrentTheme( logChart );
    //
    // ein Diagramm-Panel erzeugen
    //
    chartPanel = new ChartPanel( logChart );
    chartPanel.setMouseZoomable( true );
    chartPanel.setAutoscrolls( true );
    chartPanel.setMouseWheelEnabled( true );
    chartPanel.setRangeZoomable( false );
    chartPanel.setDisplayToolTips( false );
    chartPanel.setZoomTriggerDistance( 10 );
    add( chartPanel, BorderLayout.CENTER );
    //
    // Datumsachse umformatieren
    //
    final NumberAxis axis = new NumberAxis( LangStrings.getString( "spx42LogGraphPanel.graph.dateAxisTitle" ) );
    MinuteFormatter formatter = new MinuteFormatter( LangStrings.getString( "spx42LogGraphPanel.graph.dateAxisUnit" ) );
    axis.setNumberFormatOverride( formatter );
    thePlot.setDomainAxis( axis );
    //
    // Temperatur einfügen
    //
    if( SpxPcloggerProgramConfig.showTemperature )
    {
      makeTemperatureGraph( diveList, thePlot, labels );
    }
    //
    // Partialdruck einfügen
    // die Achse erst mal machen
    final NumberAxis ppo2Axis = new NumberAxis( LangStrings.getString( "spx42LogGraphPanel.graph.ppo2AxisTitle" ) + " " + pressureUnitName );
    final NumberAxis percentAxis = new NumberAxis( LangStrings.getString( "spx42LogGraphPanel.graph.inertgas" ) );
    //
    // wenn eine der Achsen dargesstellt werden muss, dann sollte die Achse auch in der Grafil da sein
    //
    if( SpxPcloggerProgramConfig.showPpo01 || SpxPcloggerProgramConfig.showPpo02 || SpxPcloggerProgramConfig.showPpo03 || SpxPcloggerProgramConfig.showPpoResult
            || SpxPcloggerProgramConfig.showSetpoint )
    {
      ppo2Axis.setAutoRangeIncludesZero( false );
      ppo2Axis.setAutoRange( false );
      //
      // wie skaliere ich die Achse?
      //
      if( showingUnitSystem == ProjectConst.UNITS_DEFAULT )
      {
        // so wie gespeichert
        if( savedUnitSystem == ProjectConst.UNITS_METRIC )
        {
          ppo2Axis.setRange( 0.0, 3.5 );
        }
        else
        {
          ppo2Axis.setRange( 0.0, ( 3.5 * 14.504 ) );
        }
      }
      else if( showingUnitSystem == ProjectConst.UNITS_METRIC )
      {
        ppo2Axis.setRange( 0.0, 3.5 );
      }
      else
      {
        ppo2Axis.setRange( 0.0, ( 3.5 * 14.504 ) );
      }
      ppo2Axis.setLabelPaint( new Color( ProjectConst.GRAPH_PPO2ALL_ACOLOR ) );
      ppo2Axis.setTickLabelPaint( new Color( ProjectConst.GRAPH_PPO2ALL_ACOLOR ) );
      thePlot.setRangeAxis( GRAPH_PPO2ALL, ppo2Axis );
    }
    if( SpxPcloggerProgramConfig.showHe || SpxPcloggerProgramConfig.showN2 )
    {
      percentAxis.setAutoRangeIncludesZero( false );
      percentAxis.setAutoRange( false );
      percentAxis.setRange( 0.0, 100.0 );
      percentAxis.setLabelPaint( new Color( ProjectConst.GRAPH_INNERTGAS_ACOLOR ) );
      percentAxis.setTickLabelPaint( new Color( ProjectConst.GRAPH_INNERTGAS_ACOLOR ) );
      thePlot.setRangeAxis( GRAPH_HE, percentAxis );
    }
    //
    // Partialdrücke der einzelnen Sensoren einfügen
    //
    // Sensor 01 anzeigen
    if( SpxPcloggerProgramConfig.showPpo01 )
    {
      makePpoGraph( diveList, thePlot, 1 );
    }
    // Sensor 02 anzeigen
    if( SpxPcloggerProgramConfig.showPpo02 )
    {
      makePpoGraph( diveList, thePlot, 2 );
    }
    // Sensor 03 anzeigen
    if( SpxPcloggerProgramConfig.showPpo03 )
    {
      makePpoGraph( diveList, thePlot, 3 );
    }
    // Resultierenden PPO anzeigen
    if( SpxPcloggerProgramConfig.showPpoResult )
    {
      makePpoGraph( diveList, thePlot, 0 );
      // makePpoResultGraph( diveList, thePlot );
    }
    if( SpxPcloggerProgramConfig.showSetpoint )
    {
      makeSetpointGraph( diveList, thePlot );
    }
    //
    // Helium und Stickstoffanteil im Gas?
    //
    if( SpxPcloggerProgramConfig.showHe )
    {
      makeInnertGasGraph( diveList, thePlot, "he" );
    }
    if( SpxPcloggerProgramConfig.showN2 )
    {
      makeInnertGasGraph( diveList, thePlot, "n2" );
    }
    //
    // die Nullzeit auf Wunsch
    //
    if( SpxPcloggerProgramConfig.showNulltime )
    {
      makeNulltimeGraph( diveList, thePlot );
    }
    //
    // die Tiefe einfügen
    //
    makeDepthGraph( diveList, thePlot );
    //
    showingDbIdForDiveWasShowing = dbId;
    lg.debug( "create graph...OK" );
  }

  /**
   * Graph für Anteil Inertgas machen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.08.2012
   * @param diveList
   * @param thePlot
   * @param string
   */
  private void makeInnertGasGraph( Vector<Integer[]> diveList, XYPlot thePlot, String gasName )
  {
    XYDataset percentDataSet;
    int graphPos;
    int lRenderColor;
    //
    lg.debug( "create percent dataset (" + gasName + ")" );
    final XYLineAndShapeRenderer setpointRenderer = new XYLineAndShapeRenderer( true, true );
    if( 0 == gasName.indexOf( "he" ) )
    {
      percentDataSet = createXYDataset( gasName, diveList, showingUnitSystem, 0, LogDerbyDatabaseUtil.HEPERCENT );
      graphPos = GRAPH_HE;
      lRenderColor = ProjectConst.GRAPH_HE_RCOLOR;
    }
    else
    {
      percentDataSet = createXYDataset( gasName, diveList, showingUnitSystem, 0, LogDerbyDatabaseUtil.N2PERCENT );
      graphPos = GRAPH_N2;
      lRenderColor = ProjectConst.GRAPH_N2_RCOLOR;
    }
    // die Achse sollte schon erstellt sein
    thePlot.setDataset( graphPos, percentDataSet );
    thePlot.mapDatasetToRangeAxis( graphPos, GRAPH_HE );
    setpointRenderer.setSeriesPaint( 0, new Color( lRenderColor ) );
    setpointRenderer.setSeriesShapesVisible( 0, false );
    setpointRenderer.setDrawSeriesLineAsPath( true );
    thePlot.setRenderer( graphPos, setpointRenderer );
  }

  /**
   * Erzeuge eine Grafik für die Nullzeitanzeige Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.08.2012
   * @param diveList
   * @param thePlot
   */
  private void makeNulltimeGraph( Vector<Integer[]> diveList, XYPlot thePlot )
  {
    XYDataset nullTimeDataSet;
    //
    lg.debug( "create nulltime dataset" );
    nullTimeDataSet = createXYDataset( LangStrings.getString( "spx42LogGraphPanel.graph.nulltimeScalaTitle" ), diveList, ProjectConst.UNITS_DEFAULT, 0,
            LogDerbyDatabaseUtil.NULLTIME );
    final XYLineAndShapeRenderer lineNullTimeRenderer = new XYLineAndShapeRenderer( true, true );
    final LogarithmicAxis nullTimeAxis = new LogarithmicAxis( LangStrings.getString( "spx42LogGraphPanel.graph.nulltimeAxisTitle" ) );
    nullTimeAxis.setNumberFormatOverride( new DecimalFormat( "#.###" ) );
    lineNullTimeRenderer.setSeriesPaint( 0, new Color( ProjectConst.GRAPH_NULLTIME_ACOLOR ) );
    lineNullTimeRenderer.setSeriesShapesVisible( 0, false );
    lineNullTimeRenderer.setDrawSeriesLineAsPath( true );
    nullTimeAxis.setAutoRange( false );
    nullTimeAxis.setRange( 0.0D, 200.0D ); // Lege die Nullzeit Axenreichweite auf ein übersichtliches Maß fest
    // nullTimeAxis.setAutoRangeIncludesZero( true );
    thePlot.setRangeAxis( GRAPH_NULLTIME, nullTimeAxis );
    thePlot.mapDatasetToRangeAxis( GRAPH_NULLTIME, GRAPH_NULLTIME );
    thePlot.setDataset( GRAPH_NULLTIME, nullTimeDataSet );
    thePlot.setRenderer( GRAPH_NULLTIME, lineNullTimeRenderer );
  }

  /**
   * Erzeuge je einen Graphen für die Sensoren Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.08.2012
   * @param diveList
   * @param thePlot
   * @param sensor
   */
  private void makePpoGraph( Vector<Integer[]> diveList, XYPlot thePlot, int sensor )
  {
    XYDataset ppo2DataSet;
    int indexForCreate;
    int posForGraph;
    int posColor;
    String title;
    //
    lg.debug( "create Sensor <" + sensor + "> dataset" );
    // Titel schon mal...
    title = String.format( LangStrings.getString( "spx42LogGraphPanel.graph.ppo2SensorScalaTitle" ), sensor );
    //
    // Dataset Index einstellen
    switch ( sensor )
    {
      case 0:
        indexForCreate = LogDerbyDatabaseUtil.PPO2;
        posForGraph = GRAPH_PPO2ALL;
        posColor = ProjectConst.GRAPH_PPO2ALL_RCOLOR;
        title = LangStrings.getString( "spx42LogGraphPanel.graph.ppo2ScalaTitle" );
        break;
      case 1:
        indexForCreate = LogDerbyDatabaseUtil.PPO2_01;
        posForGraph = GRAPH_PPO2_01;
        posColor = ProjectConst.GRAPH_PPO2_01_RCOLOR;
        break;
      case 2:
        indexForCreate = LogDerbyDatabaseUtil.PPO2_02;
        posForGraph = GRAPH_PPO2_02;
        posColor = ProjectConst.GRAPH_PPO2_02_RCOLOR;
        break;
      case 3:
        indexForCreate = LogDerbyDatabaseUtil.PPO2_03;
        posForGraph = GRAPH_PPO2_03;
        posColor = ProjectConst.GRAPH_PPO2_02_RCOLOR;
        break;
      default:
        indexForCreate = LogDerbyDatabaseUtil.PPO2_01;
        posForGraph = GRAPH_PPO2_01;
        posColor = ProjectConst.GRAPH_PPO2_01_RCOLOR;
    }
    if( showingUnitSystem == savedUnitSystem || showingUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      ppo2DataSet = createXYDataset( title, diveList, ProjectConst.UNITS_DEFAULT, 0, indexForCreate );
    }
    else
    {
      ppo2DataSet = createXYDataset( title, diveList, showingUnitSystem, 0, indexForCreate );
    }
    final XYLineAndShapeRenderer ppo2Renderer = new XYLineAndShapeRenderer( true, true );
    // die Achse sollte schon erstellt sein
    thePlot.setDataset( posForGraph, ppo2DataSet );
    thePlot.mapDatasetToRangeAxis( posForGraph, GRAPH_PPO2ALL );
    ppo2Renderer.setSeriesPaint( 0, new Color( posColor ) );
    ppo2Renderer.setSeriesShapesVisible( 0, false );
    ppo2Renderer.setDrawSeriesLineAsPath( true );
    thePlot.setRenderer( posForGraph, ppo2Renderer );
  }

  /**
   * Mach mir den Graphen für den Setpoint Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.08.2012
   * @param diveList
   * @param thePlot
   */
  private void makeSetpointGraph( Vector<Integer[]> diveList, XYPlot thePlot )
  {
    XYDataset setPointDataSet;
    //
    lg.debug( "create setpoint dataset" );
    if( showingUnitSystem == savedUnitSystem || showingUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      setPointDataSet = createXYDataset( LangStrings.getString( "spx42LogGraphPanel.graph.setpointScalaTitle" ), diveList, ProjectConst.UNITS_DEFAULT, 0,
              LogDerbyDatabaseUtil.SETPOINT );
    }
    else
    {
      setPointDataSet = createXYDataset( LangStrings.getString( "spx42LogGraphPanel.graph.setpointScalaTitle" ), diveList, showingUnitSystem, 0, LogDerbyDatabaseUtil.SETPOINT );
    }
    // final NumberAxis setpoint2Axis = new NumberAxis( LangStrings.getString("spx42LogGraphPanel.graph.setpointAxisTitle") );
    final XYLineAndShapeRenderer setpointRenderer = new XYLineAndShapeRenderer( true, true );
    // die Achse sollte schon erstellt sein
    thePlot.setDataset( GRAPH_SETPOINT, setPointDataSet );
    thePlot.mapDatasetToRangeAxis( GRAPH_SETPOINT, GRAPH_PPO2ALL );
    setpointRenderer.setSeriesPaint( 0, new Color( ProjectConst.GRAPH_SETPOINT_ACOLOR ) );
    setpointRenderer.setSeriesShapesVisible( 0, false );
    setpointRenderer.setDrawSeriesLineAsPath( true );
    thePlot.setRenderer( GRAPH_SETPOINT, setpointRenderer );
  }

  /**
   * Temperaturgraph machen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.08.2012
   * @param labels
   * @param thePlot
   * @param diveList
   */
  private void makeTemperatureGraph( Vector<Integer[]> diveList, XYPlot thePlot, String[] labels )
  {
    XYDataset tempDataSet;
    Color axisColor = new Color( ProjectConst.GRAPH_TEMPERATURE_ACOLOR );
    Color renderColor = new Color( ProjectConst.GRAPH_TEMPERATURE_RCOLOR );
    //
    lg.debug( "create temp dataset" );
    if( showingUnitSystem == savedUnitSystem || showingUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      // Keine Änderung norwendig!
      tempDataSet = createXYDataset( LangStrings.getString( "spx42LogGraphPanel.graph.tempScalaTitle" ), diveList, ProjectConst.UNITS_DEFAULT, 0, LogDerbyDatabaseUtil.TEMPERATURE );
    }
    else
    {
      // bitte konvertiere die Einheiten ins gewünschte Format!
      tempDataSet = createXYDataset( LangStrings.getString( "spx42LogGraphPanel.graph.tempScalaTitle" ), diveList, showingUnitSystem, 0, LogDerbyDatabaseUtil.TEMPERATURE );
    }
    final XYLineAndShapeRenderer lineTemperatureRenderer = new XYLineAndShapeRenderer( true, true );
    final NumberAxis tempAxis = new NumberAxis( LangStrings.getString( "spx42LogGraphPanel.graph.tempAxisTitle" ) + " " + labels[1] );
    tempAxis.setLabelPaint( axisColor );
    tempAxis.setTickLabelPaint( axisColor );
    tempAxis.setNumberFormatOverride( new DecimalFormat( "###.##" ) );
    lineTemperatureRenderer.setSeriesPaint( 0, renderColor );
    lineTemperatureRenderer.setSeriesShapesVisible( 0, false );
    lineTemperatureRenderer.setDrawSeriesLineAsPath( true );
    tempAxis.setAutoRangeIncludesZero( true );
    thePlot.setRangeAxis( GRAPH_DEPTH, tempAxis );
    thePlot.mapDatasetToRangeAxis( GRAPH_DEPTH, 0 );
    thePlot.setDataset( GRAPH_TEMPERATURE, tempDataSet );
    thePlot.setRenderer( GRAPH_TEMPERATURE, lineTemperatureRenderer );
  }

  /**
   * Gib alle Felder,Objekte frei, die zur grafischen Darstellung gebraucht wurden, falls vorhanden Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 26.06.2012
   */
  public void releaseGraph()
  {
    lg.debug( "release graphic objects..." );
    showingDbIdForDiveWasShowing = -1;
    if( chartPanel != null )
    {
      chartPanel.removeAll();
      chartPanel.setEnabled( false );
      chartPanel.setVisible( false );
      remove( chartPanel );
      chartPanel = null;
      maxDepthValueLabel.setText( "-" );
      coldestTempValueLabel.setText( "-" );
      diveLenValueLabel.setText( "-" );
    }
  }

  /**
   * Setze die Listener auf das Hauptobjekt Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   * @param mainCommGUI
   *          das Hauptobjekt
   */
  public void setGlobalChangeListener( MainCommGUI mainCommGUI )
  {
    deviceComboBox.addMouseMotionListener( mainCommGUI );
    diveSelectComboBox.addMouseMotionListener( mainCommGUI );
    computeGraphButton.addMouseMotionListener( mainCommGUI );
    detailGraphButton.addMouseMotionListener( mainCommGUI );
    notesEditButton.addMouseMotionListener( mainCommGUI );
    // die Aktionen mach ich im Objekt selber
    deviceComboBox.addActionListener( this );
    computeGraphButton.addActionListener( this );
    detailGraphButton.addActionListener( this );
    notesEditButton.addActionListener( this );
  }

  /**
   * Setze alle Strings in die entsprechende Landessprache! Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   * @return in Ordnung oder nicht
   */
  public int setLanguageStrings()
  {
    try
    {
      deviceComboBox.setToolTipText( LangStrings.getString( "spx42LogGraphPanel.deviceComboBox.tooltiptext" ) );
      diveSelectComboBox.setToolTipText( LangStrings.getString( "spx42LogGraphPanel.diveSelectComboBox.tooltiptext" ) );
      computeGraphButton.setText( LangStrings.getString( "spx42LogGraphPanel.computeGraphButton.text" ) );
      computeGraphButton.setToolTipText( LangStrings.getString( "spx42LogGraphPanel.computeGraphButton.tooltiptext" ) );
      detailGraphButton.setText( LangStrings.getString( "spx42LogGraphPanel.detailGraphButton.text" ) );
      detailGraphButton.setToolTipText( LangStrings.getString( "spx42LogGraphPanel.detailGraphButton.tooltiptext" ) );
      maxDepthLabelString = LangStrings.getString( "spx42LogGraphPanel.maxDepthLabel.text" );
      coldestLabelString = LangStrings.getString( "spx42LogGraphPanel.coldestLabel.text" );
      diveLenLabelString = LangStrings.getString( "spx42LogGraphPanel.diveLenLabel.text" );
      notesEditButton.setToolTipText( LangStrings.getString( "spx42LogGraphPanel.computeGraphButton.tooltiptext" ) );
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
    clearDiveComboBox();
    return( 1 );
  }

  /**
   * Zeige ein einfaches Formular zum Eineben einer kleinen Notitz Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.08.2012
   * @param dbId
   */
  private void showNotesEditForm( int dbId )
  {
    DiveNotesEditDialog edDial = new DiveNotesEditDialog();
    edDial.setNotes( notesLabel.getText() );
    if( edDial.showModal() )
    {
      if( ( notesLabel.getText() != null ) && ( !notesLabel.getText().isEmpty() ) )
      {
        if( notesLabel.getText().equals( edDial.getNotes() ) )
        {
          // hier hat sich nix geändert, ENTE
          lg.debug( "not a change in note, ignoring..." );
          return;
        }
      }
      lg.info( "save new Notes in database..." );
      notesLabel.setText( edDial.getNotes() );
      edDial.dispose();
      // jetzt ab in die Datenbank damit!
      if( -1 == databaseUtil.saveNoteForIdLog( dbId, notesLabel.getText() ) )
      {
        lg.error( "can't update notes for dive!" );
      }
    }
    else
    {
      edDial.dispose();
    }
  }

  /**
   * Zeigt eine Warnung an Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param msg
   *          Warnmessage
   */
  private void showWarnBox( String msg )
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Abort.png" ) );
      JOptionPane.showMessageDialog( this, msg, LangStrings.getString( "MainCommGUI.warnDialog.headline" ), JOptionPane.WARNING_MESSAGE, icon );
    }
    catch( NullPointerException ex )
    {
      lg.error( "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      lg.error( "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      lg.error( "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }

  /**
   * 
   * Ist das Diluent in der Liste ?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.11.2013
   * @param list
   * @param dil
   * @return
   */
  private boolean isDiluentInList( Vector<Integer[]> list, Integer[] dil )
  {
    Iterator<Integer[]> it = list.iterator();
    while( it.hasNext() )
    {
      Integer[] toTest = it.next();
      if( ( toTest[0] == dil[0] ) && ( toTest[1] == dil[1] ) )
      {
        // gefunden, Suche abbrechen
        return( true );
      }
    }
    // wenn nichts gefunden
    return( false );
  }

  /**
   * 
   * Erzeige eine Liste der während des Tauchgangs erzeugten Diluents (Robert Wimmer angefragt)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.11.2013
   * @param diveList
   * @return
   */
  private Vector<String> getDiluentNamesFromDive( Vector<Integer[]> diveList )
  {
    Vector<String> retValue = new Vector<String>();
    Vector<Integer[]> diluents = new Vector<Integer[]>();
    // int currN2, currHe;
    //
    // erst mal alle verwendeten Diluents finden
    //
    Iterator<Integer[]> it = diveList.iterator();
    while( it.hasNext() )
    {
      // erfrage das Diluent des Timestamps
      Integer[] currDive = it.next();
      // berechne das Diluent mit O2/HE
      Integer[] currDiluent = new Integer[]
      { 0, 0 };
      currDiluent[0] = 100 - ( currDive[LogDerbyDatabaseUtil.HEPERCENT] + currDive[LogDerbyDatabaseUtil.N2PERCENT] );
      currDiluent[1] = currDive[LogDerbyDatabaseUtil.HEPERCENT];
      // ist das Diluent schon in der Liste?
      if( !isDiluentInList( diluents, currDiluent ) )
      {
        // Wenn nicht in der Liste, hinzufügen!
        diluents.add( currDiluent );
      }
    }
    //
    // Jetzt sollten alle Einträge besarbeitet sein
    //
    // alle Diluents, wenn vorhanden benennen
    it = diluents.iterator();
    while( it.hasNext() )
    {
      Integer[] dil = it.next();
      retValue.add( GasComputeUnit.getNameForGas( dil[0], dil[1] ) );
    }
    return( retValue );
  }
}
