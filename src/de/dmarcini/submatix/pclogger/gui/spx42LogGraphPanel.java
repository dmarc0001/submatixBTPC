package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.joda.time.DateTime;

import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.ConnectDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.LogForDeviceDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.LogListComboBoxModel;
import de.dmarcini.submatix.pclogger.utils.MinuteFormatter;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

public class spx42LogGraphPanel extends JPanel implements ActionListener
{
  /**
   * 
   */
  private static final long        serialVersionUID = 1L;
  protected Logger                 LOGGER           = null;
  private ConnectDatabaseUtil      dbUtil           = null;
  private ResourceBundle           stringsBundle    = null;
  private File                     dataDir          = null;
  private ChartPanel               chartPanel       = null;
  private SpxPcloggerProgramConfig progConfig       = null;
  private JPanel                   topPanel;
  private JPanel                   bottomPanel;
  private JComboBox                deviceComboBox;
  private JComboBox                diveSelectComboBox;
  private JButton                  computeGraphButton;
  private JLabel                   maxDepthLabel;
  private JLabel                   coldestLabel;
  private JLabel                   diveLenLabel;
  private JLabel                   maxDepthValueLabel;
  private JLabel                   coldestTempValueLabel;
  private JLabel                   diveLenValueLabel;

  @SuppressWarnings( "unused" )
  private spx42LogGraphPanel()
  {
    initPanel();
  }

  /**
   * Create the panel.
   * 
   * @param LOGGER
   * @param _dbUtil
   * @param progConfig
   */
  public spx42LogGraphPanel( Logger LOGGER, final ConnectDatabaseUtil _dbUtil, SpxPcloggerProgramConfig progConfig )
  {
    this.LOGGER = LOGGER;
    LOGGER.log( Level.FINE, "constructor..." );
    this.dbUtil = _dbUtil;
    this.progConfig = progConfig;
    initPanel();
  }

  /**
   * Initialisiere das Panel für die Verbindungen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   */
  private void initPanel()
  {
    setLayout( new BorderLayout( 0, 0 ) );
    topPanel = new JPanel();
    add( topPanel, BorderLayout.NORTH );
    deviceComboBox = new JComboBox();
    deviceComboBox.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
    deviceComboBox.setActionCommand( "change_device_to_display" );
    diveSelectComboBox = new JComboBox();
    diveSelectComboBox.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
    diveSelectComboBox.setActionCommand( "change_dive_to_display" );
    computeGraphButton = new JButton( "GRAPHBUTTON" );
    computeGraphButton.setActionCommand( "show_log_graph" );
    GroupLayout gl_topPanel = new GroupLayout( topPanel );
    gl_topPanel
            .setHorizontalGroup( gl_topPanel.createParallelGroup( Alignment.LEADING ).addGroup(
                    Alignment.TRAILING,
                    gl_topPanel.createSequentialGroup().addContainerGap().addComponent( deviceComboBox, 0, 235, Short.MAX_VALUE ).addGap( 18 )
                            .addComponent( diveSelectComboBox, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE ).addGap( 18 ).addComponent( computeGraphButton )
                            .addGap( 122 ) ) );
    gl_topPanel.setVerticalGroup( gl_topPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_topPanel
                    .createSequentialGroup()
                    .addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                    .addGroup(
                            gl_topPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( deviceComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( diveSelectComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( computeGraphButton ) ) ) );
    topPanel.setLayout( gl_topPanel );
    bottomPanel = new JPanel();
    add( bottomPanel, BorderLayout.SOUTH );
    maxDepthLabel = new JLabel( "MAXDEPTH" );
    maxDepthValueLabel = new JLabel( "00m" );
    coldestLabel = new JLabel( "COLDEST" );
    coldestTempValueLabel = new JLabel( "00Grd" );
    diveLenLabel = new JLabel( "LENGTH" );
    diveLenValueLabel = new JLabel( "00:00min" );
    GroupLayout gl_bottomPanel = new GroupLayout( bottomPanel );
    gl_bottomPanel.setHorizontalGroup( gl_bottomPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_bottomPanel.createSequentialGroup().addContainerGap().addComponent( maxDepthLabel, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE ).addGap( 18 )
                    .addComponent( maxDepthValueLabel, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE ).addGap( 18 )
                    .addComponent( coldestLabel, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE ).addGap( 18 )
                    .addComponent( coldestTempValueLabel, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE ).addGap( 37 )
                    .addComponent( diveLenLabel, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE ).addPreferredGap( ComponentPlacement.UNRELATED )
                    .addComponent( diveLenValueLabel, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE ).addContainerGap( 81, Short.MAX_VALUE ) ) );
    gl_bottomPanel.setVerticalGroup( gl_bottomPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_bottomPanel
                    .createSequentialGroup()
                    .addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                    .addGroup(
                            gl_bottomPanel.createParallelGroup( Alignment.BASELINE ).addComponent( maxDepthLabel ).addComponent( maxDepthValueLabel ).addComponent( coldestLabel )
                                    .addComponent( coldestTempValueLabel ).addComponent( diveLenLabel ).addComponent( diveLenValueLabel ) ) ) );
    bottomPanel.setLayout( gl_bottomPanel );
    chartPanel = null;
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
    computeGraphButton.addMouseMotionListener( mainCommGUI );
    // die Aktionen mach ich im Objekt selber
    deviceComboBox.addActionListener( this );
    computeGraphButton.addActionListener( this );
  }

  /**
   * Setze alle Strings in die entsprechende Landessprache! Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   * @param stringsBundle
   *          Resource für die Strings
   * @return in Ordnung oder nicht
   */
  public int setLanguageStrings( ResourceBundle stringsBundle )
  {
    this.stringsBundle = stringsBundle;
    try
    {
      deviceComboBox.setToolTipText( stringsBundle.getString( "spx42LogGraphPanel.deviceComboBox.tooltiptext" ) );
      diveSelectComboBox.setToolTipText( stringsBundle.getString( "spx42LogGraphPanel.diveSelectComboBox.tooltiptext" ) );
      computeGraphButton.setText( stringsBundle.getString( "spx42LogGraphPanel.computeGraphButton.text" ) );
      computeGraphButton.setToolTipText( stringsBundle.getString( "spx42LogGraphPanel.computeGraphButton.tooltiptext" ) );
      maxDepthLabel.setText( stringsBundle.getString( "spx42LogGraphPanel.maxDepthLabel.text" ) );
      coldestLabel.setText( stringsBundle.getString( "spx42LogGraphPanel.coldestLabel.text" ) );
      diveLenLabel.setText( stringsBundle.getString( "spx42LogGraphPanel.diveLenLabel.text" ) );
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

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    String entry = null;
    int index, dbId;
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
        LOGGER.log( Level.FINE, "show log graph initiated." );
        // welches Device (wichtig um das richtige DB-File zu wählen)
        index = deviceComboBox.getSelectedIndex();
        if( index < 0 )
        {
          // kein Gerät ausgewählt
          LOGGER.log( Level.WARNING, "no device selected." );
          return;
        }
        device = dbUtil.getNameForAlias( ( String )deviceComboBox.getSelectedItem() );
        // welchen Tauchgang?
        index = diveSelectComboBox.getSelectedIndex();
        if( index < 0 )
        {
          LOGGER.log( Level.WARNING, "no dive selected." );
          return;
        }
        dbId = ( ( LogListComboBoxModel )diveSelectComboBox.getModel() ).getDatabaseIdAt( index );
        if( dbId < 0 )
        {
          LOGGER.log( Level.SEVERE, "can't find database id for dive." );
          return;
        }
        makeGraphForLog( dbId, device );
        return;
      }
      else
      {
        LOGGER.log( Level.WARNING, "unknown button command <" + cmd + "> recived." );
      }
      return;
    }
    // /////////////////////////////////////////////////////////////////////////
    // Combobox
    else if( ev.getSource() instanceof JComboBox )
    {
      JComboBox srcBox = ( JComboBox )ev.getSource();
      // /////////////////////////////////////////////////////////////////////////
      // Gerät zur Grafischen Darstellung auswählen
      if( cmd.equals( "change_device_to_display" ) )
      {
        entry = ( String )srcBox.getSelectedItem();
        LOGGER.log( Level.FINE, "device <" + entry + ">...Index: <" + srcBox.getSelectedIndex() + ">" );
        fillDiveComboBox( entry );
      }
      // /////////////////////////////////////////////////////////////////////////
      // Dive zur Grafischen Darstellung auswählen
      else if( cmd.equals( "change_dive_to_display" ) )
      {
        entry = ( String )srcBox.getSelectedItem();
        LOGGER.log( Level.FINE, "dive <" + entry + ">...Index: <" + srcBox.getSelectedIndex() + ">" );
        // fillDiveComboBox( entry );
      }
      else
      {
        LOGGER.log( Level.WARNING, "unknown combobox command <" + cmd + "> recived." );
      }
      return;
    }
    else
    {
      LOGGER.log( Level.WARNING, "unknown action command <" + cmd + "> recived." );
    }
  }

  /**
   * 
   * Anhand des Alias des Gerätes die Tauchgangsliste füllen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.07.2012
   * @param deviceAlias
   */
  private void fillDiveComboBox( String deviceAlias )
  {
    LogForDeviceDatabaseUtil logDatabaseUtil;
    String device;
    DateTime dateTime;
    long javaTime;
    //
    // Alias fürs Gerät zurücksuchen
    //
    device = dbUtil.getNameForAlias( deviceAlias );
    if( device != null )
    {
      LOGGER.log( Level.FINE, "search dive list for device <" + device + ">..." );
      releaseGraph();
      //
      // richtige Datenbank öffnen
      //
      // das Datenbankutility initialisieren
      logDatabaseUtil = new LogForDeviceDatabaseUtil( LOGGER, this, device, dataDir.getAbsolutePath() );
      if( logDatabaseUtil.createConnection() == null )
      {
        logDatabaseUtil = null;
        return;
      }
      // Eine Liste der Dives lesen
      LOGGER.log( Level.FINE, "read dive list for device from DB..." );
      Vector<String[]> entrys = logDatabaseUtil.getDiveListForDevice( device );
      if( entrys.size() < 1 )
      {
        LOGGER.log( Level.INFO, "no dives for device <" + deviceAlias + "/" + device + "> found in DB." );
        clearDiveComboBox();
        logDatabaseUtil.closeDB();
        logDatabaseUtil = null;
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
        elem[1] = String.format( "%4s", origSet[1] );
        // Die UTC-Zeit als ASCII/UNIX wieder zu der originalen Zeit für Java zusammenbauen
        try
        {
          // LOGGER.log( Level.FINE, "unix Timestamp <" + origSet[1] + ">..." );
          javaTime = Long.parseLong( origSet[2] ) * 1000;
          dateTime = new DateTime( javaTime );
          elem[0] = origSet[0];
          elem[2] = dateTime.toString( stringsBundle.getString( "MainCommGUI.timeFormatterString" ) );
        }
        catch( NumberFormatException ex )
        {
          LOGGER.log( Level.SEVERE, "Number format exception (value=<" + origSet[1] + ">: <" + ex.getLocalizedMessage() + ">" );
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
      logDatabaseUtil.closeDB();
      logDatabaseUtil = null;
    }
    else
    {
      LOGGER.log( Level.FINE, "no device found...." );
    }
  }

  /**
   * 
   * Die Combobox mit einem leeren Modell ausstatten...
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.07.2012
   */
  public void clearDiveComboBox()
  {
    Vector<String[]> diveEntrys = new Vector<String[]>();
    LogListComboBoxModel listBoxModel = new LogListComboBoxModel( diveEntrys );
    diveSelectComboBox.setModel( listBoxModel );
  }

  /**
   * 
   * Initialisiere alle Felder, Objekte, die zur grafischen Darstellung gebraucht werden
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 26.06.2012
   * @param connDev
   *          Falls verbunden, das aktuelle Gerät übergeben (für Voreinstellungen)
   * @param dDir
   * @throws Exception
   */
  public void initGraph( String connDev, File dDir ) throws Exception
  {
    String connDevAlias = null;
    //
    // entsorge für alle Fälle das Zeug von vorher
    releaseGraph();
    dataDir = dDir;
    if( connDev == null )
    {
      LOGGER.log( Level.FINE, "init graphic objects whitout active Device" );
    }
    else
    {
      LOGGER.log( Level.FINE, "init graphic objects Device <" + connDev + ">..." );
    }
    //
    // Ist überhaupt eine Datenbank zum Auslesen vorhanden?
    //
    if( dbUtil == null || ( !dbUtil.isOpenDB() ) )
    {
      throw new Exception( "no database object initiated!" );
    }
    // Alias fürs Gerät
    if( connDev != null )
    {
      connDevAlias = dbUtil.getAliasForName( connDev );
      LOGGER.log( Level.FINE, "Device <" + connDev + "> has alias <" + connDevAlias + ">..." );
    }
    //
    // Lese eine Liste der Tauchgänge für dieses Gerät
    //
    String[] entrys = dbUtil.readDevicesFromDatabase();
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
    ComboBoxModel portBoxModel = new DefaultComboBoxModel( entrys );
    deviceComboBox.setModel( portBoxModel );
    if( entrys.length > 1 )
    {
      // wenn kein Alias da ist, brauch ich auchnicht zu suchen
      if( connDevAlias != null )
      {
        // Alle Einträge testen
        for( int index = 0; index < entrys.length; index++ )
        {
          if( entrys[index].equals( connDevAlias ) )
          {
            deviceComboBox.setSelectedIndex( index );
            LOGGER.log( Level.FINE, "device alias found and set as index für combobox..." );
            break;
          }
        }
      }
    }
    //
    // gibt es einen Eintrag in der Combobox müßte ja deren entsprechende Liste von
    // Einträgen gelesen werden....
    //
    if( deviceComboBox.getSelectedIndex() != -1 )
    {
      connDevAlias = ( String )deviceComboBox.getSelectedItem();
      if( connDevAlias != null )
      {
        fillDiveComboBox( connDevAlias );
      }
    }
  }

  /**
   * 
   * Gib alle Felder,Objekte frei, die zur grafischen Darstellung gebraucht wurden, falls vorhanden
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 26.06.2012
   */
  public void releaseGraph()
  {
    LOGGER.log( Level.FINE, "release graphic objects..." );
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
   * Erzeuge ein XY-Dataset aus einem Vector
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.07.2012
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
      if( y == LogForDeviceDatabaseUtil.DEPTH )
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
      else if( y == LogForDeviceDatabaseUtil.PPO2 )
      {
        double fPpo2 = new Double( dataSet[y] / 1000.00 );
        series.add( secounds, fPpo2 );
      }
      else if( y == LogForDeviceDatabaseUtil.TEMPERATURE )
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
   * 
   * Zeichne die eigentliche Grafik
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.07.2012
   * @param dbId
   *          dbid in Tabelle D_TABLE_DIVEDETAIL
   * @param device
   */
  private void makeGraphForLog( int dbId, String device )
  {
    LogForDeviceDatabaseUtil logDatabaseUtil;
    Vector<Integer[]> diveList;
    int[] headData;
    XYPlot thePlot;
    XYDataset depthDataSet, tempDataSet, ppo2DataSet;
    JFreeChart logChart;
    int min, sec, progUnitSystem, diveUnitSystem;
    String depthUnitName, tempUnitName;
    // das alte Zeug entsorgen
    releaseGraph();
    //
    // richtige Datenbank öffnen
    //
    // das Datenbankutility initialisieren
    logDatabaseUtil = new LogForDeviceDatabaseUtil( LOGGER, this, device, dataDir.getAbsolutePath() );
    if( logDatabaseUtil.createConnection() == null )
    {
      // Tja, das ging schief
      logDatabaseUtil = null;
      showWarnBox( stringsBundle.getString( "spx42LogGraphPanel.warnBox.noDiveDataFound" ) );
      return;
    }
    // Daten eines TG lesen
    LOGGER.log( Level.FINE, "read dive log from DB..." );
    diveList = logDatabaseUtil.readDiveDataFromId( dbId );
    if( diveList == null || diveList.isEmpty() )
    {
      return;
    }
    //
    // Labels für Tachgangseckdaten füllen
    //
    headData = logDatabaseUtil.readHeadDiveDataFromId( dbId );
    progUnitSystem = progConfig.getUnitsProperty();
    diveUnitSystem = headData[6];
    // jetzt die Strings für Masseinheiten holen
    // Bei UNITS_DEFAULT gehts nach diveUnitSystem
    if( progUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      if( diveUnitSystem == ProjectConst.UNITS_IMPERIAL )
      {
        // also, ist der Tauchgang imperial geloggt
        depthUnitName = stringsBundle.getString( "spx42LogGraphPanel.unit.imperial.lenght" );
        tempUnitName = stringsBundle.getString( "spx42LogGraphPanel.unit.imperial.temperature" );
      }
      else
      {
        // der tauhcgang ist metrisch geloggt.
        depthUnitName = stringsBundle.getString( "spx42LogGraphPanel.unit.metric.lenght" );
        tempUnitName = stringsBundle.getString( "spx42LogGraphPanel.unit.metric.temperature" );
      }
    }
    else if( progUnitSystem == ProjectConst.UNITS_METRIC )
    {
      // der User wünscht Metrische Anzeige
      depthUnitName = stringsBundle.getString( "spx42LogGraphPanel.unit.metric.lenght" );
      tempUnitName = stringsBundle.getString( "spx42LogGraphPanel.unit.metric.temperature" );
    }
    else
    {
      // der User wünscht imperiale anzeige
      depthUnitName = stringsBundle.getString( "spx42LogGraphPanel.unit.imperial.lenght" );
      tempUnitName = stringsBundle.getString( "spx42LogGraphPanel.unit.imperial.temperature" );
    }
    //
    // entscheide ob etwas umgerechnet werden sollte
    //
    if( progUnitSystem == diveUnitSystem || progUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      // nein, alles schick
      maxDepthValueLabel.setText( String.format( "%1.2f %s", ( headData[3] / 10.0 ), depthUnitName ) );
      coldestTempValueLabel.setText( String.format( "%1.2f %s", ( headData[2] / 10.0 ), tempUnitName ) );
    }
    else
    {
      // umrechnen!
      if( progUnitSystem == ProjectConst.UNITS_IMPERIAL )
      {
        // metrisch-> imperial konvertieren
        // 1 foot == 30,48 cm == 0.3048 Meter
        maxDepthValueLabel.setText( String.format( "%1.2f %s", ( headData[3] / 10.0 ) / 0.3048, depthUnitName ) );
        // t °F = 5⁄9 (t − 32) °C
        coldestTempValueLabel.setText( String.format( "%1.2f %s", ( 5.0 / 9.0 ) * ( ( headData[2] / 10.0 ) - 32 ), tempUnitName ) );
      }
      else
      {
        maxDepthValueLabel.setText( String.format( "%1.2f %s", ( headData[3] / 10.0 ) * 0.3048, depthUnitName ) );
        // t °C = (9⁄5 t + 32) °F
        coldestTempValueLabel.setText( String.format( "%1.2f %s", ( ( 9.0 / 5.0 ) * ( headData[2] / 10.0 ) ) + 32, tempUnitName ) );
      }
    }
    min = headData[5] / 60;
    sec = headData[5] % 60;
    diveLenValueLabel.setText( String.format( "%d:%02d min", min, sec ) );
    //
    // einen Plot machen (Grundlage des Diagramms)
    //
    LOGGER.log( Level.FINE, "create graph..." );
    thePlot = new XYPlot();
    //
    // ein Chart zur Anzeige in einem Panel erzeugen
    //
    logChart = new JFreeChart( stringsBundle.getString( "spx42LogGraphPanel.graph.chartTitle" ), thePlot );
    //
    // ein Diagramm-Panel erzeugen
    //
    chartPanel = new ChartPanel( logChart );
    chartPanel.setMouseZoomable( true );
    chartPanel.setMouseWheelEnabled( true );
    chartPanel.setRangeZoomable( false );
    add( chartPanel, BorderLayout.CENTER );
    //
    // Datumsachse umformatieren
    final NumberAxis axis = new NumberAxis( stringsBundle.getString( "spx42LogGraphPanel.graph.dateAxisTitle" ) );
    MinuteFormatter formatter = new MinuteFormatter( stringsBundle.getString( "spx42LogGraphPanel.graph.dateAxisUnit" ) );
    axis.setNumberFormatOverride( formatter );
    thePlot.setDomainAxis( axis );
    //
    // Temperatur einfügen
    //
    LOGGER.log( Level.FINE, "create temp dataset" );
    if( progUnitSystem == diveUnitSystem || progUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      // Keine Änderung norwendig!
      tempDataSet = createXYDataset( stringsBundle.getString( "spx42LogGraphPanel.graph.tempScalaTitle" ) + " " + tempUnitName, diveList, ProjectConst.UNITS_DEFAULT, 0,
              LogForDeviceDatabaseUtil.TEMPERATURE );
    }
    else
    {
      // bitte konvertiere die Einheiten ins gewünschte Format!
      tempDataSet = createXYDataset( stringsBundle.getString( "spx42LogGraphPanel.graph.tempScalaTitle" ) + " " + tempUnitName, diveList, progUnitSystem, 0,
              LogForDeviceDatabaseUtil.TEMPERATURE );
    }
    final NumberAxis tempAxis = new NumberAxis( stringsBundle.getString( "spx42LogGraphPanel.graph.tempAxisTitle" ) + " " + tempUnitName );
    tempAxis.setNumberFormatOverride( new DecimalFormat( "###.##" ) );
    final XYLineAndShapeRenderer lineTemperatureRenderer = new XYLineAndShapeRenderer( true, true );
    lineTemperatureRenderer.setSeriesPaint( 0, Color.RED );
    lineTemperatureRenderer.setSeriesShapesVisible( 0, false );
    lineTemperatureRenderer.setDrawSeriesLineAsPath( true );
    tempAxis.setAutoRangeIncludesZero( true );
    thePlot.setRangeAxis( 2, tempAxis );
    thePlot.mapDatasetToRangeAxis( 2, 0 );
    thePlot.setDataset( 0, tempDataSet );
    thePlot.setRenderer( 0, lineTemperatureRenderer );
    //
    // Partialdruck einfügen
    //
    LOGGER.log( Level.FINE, "create ppo2 dataset" );
    if( progUnitSystem == diveUnitSystem || progUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      ppo2DataSet = createXYDataset( stringsBundle.getString( "spx42LogGraphPanel.graph.ppo2ScalaTitle" ), diveList, ProjectConst.UNITS_DEFAULT, 0, LogForDeviceDatabaseUtil.PPO2 );
    }
    else
    {
      ppo2DataSet = createXYDataset( stringsBundle.getString( "spx42LogGraphPanel.graph.ppo2ScalaTitle" ), diveList, progUnitSystem, 0, LogForDeviceDatabaseUtil.PPO2 );
    }
    final NumberAxis ppo2Axis = new NumberAxis( stringsBundle.getString( "spx42LogGraphPanel.graph.ppo2AxisTitle" ) );
    final XYLineAndShapeRenderer ppo2Renderer = new XYLineAndShapeRenderer( true, true );
    ppo2Axis.setAutoRangeIncludesZero( false );
    ppo2Axis.setAutoRange( false );
    ppo2Axis.setRange( 0.0, 3.5 );
    thePlot.setRangeAxis( 1, ppo2Axis );
    thePlot.setDataset( 1, ppo2DataSet );
    thePlot.mapDatasetToRangeAxis( 1, 1 );
    ppo2Renderer.setSeriesPaint( 0, Color.CYAN );
    ppo2Renderer.setSeriesShapesVisible( 0, false );
    ppo2Renderer.setDrawSeriesLineAsPath( true );
    thePlot.setRenderer( 1, ppo2Renderer );
    //
    // die Tiefe einfügen
    //
    LOGGER.log( Level.FINE, "create depth dataset" );
    if( progUnitSystem == diveUnitSystem || progUnitSystem == ProjectConst.UNITS_DEFAULT )
    {
      depthDataSet = createXYDataset( stringsBundle.getString( "spx42LogGraphPanel.graph.depthScalaTitle" ) + " " + depthUnitName, diveList, ProjectConst.UNITS_DEFAULT, 0,
              LogForDeviceDatabaseUtil.DEPTH );
    }
    else
    {
      depthDataSet = createXYDataset( stringsBundle.getString( "spx42LogGraphPanel.graph.depthScalaTitle" ) + " " + depthUnitName, diveList, progUnitSystem, 0,
              LogForDeviceDatabaseUtil.DEPTH );
    }
    final NumberAxis depthAxis = new NumberAxis( stringsBundle.getString( "spx42LogGraphPanel.graph.depthAxisTitle" ) + " " + depthUnitName );
    final XYAreaRenderer areaDepthRenderer = new XYAreaRenderer( XYAreaRenderer.AREA );
    depthAxis.setAutoRangeIncludesZero( true );
    thePlot.setRangeAxis( 0, depthAxis );
    thePlot.setDataset( 2, depthDataSet );
    thePlot.mapDatasetToRangeAxis( 0, 2 );
    areaDepthRenderer.setSeriesPaint( 0, new Color( 0xa0a0ff ) );
    thePlot.setRenderer( 2, areaDepthRenderer, true );
    // brauch ich doch nicht
    // chartPanel.paint( chartPanel.getGraphics() );
    LOGGER.log( Level.FINE, "create graph...OK" );
  }
}
