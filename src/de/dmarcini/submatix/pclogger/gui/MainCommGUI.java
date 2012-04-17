package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.dmarcini.submatix.pclogger.comm.BTCommunication;
import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.DirksConsoleLogFormatter;
import de.dmarcini.submatix.pclogger.utils.SPX42Config;

//@formatter:off
public class MainCommGUI extends JFrame implements ActionListener, MouseMotionListener, ChangeListener, ItemListener
{
  //
  private static final long                 serialVersionUID = 2L;
  private static ResourceBundle                stringsBundle = null;
  private Locale                               programLocale = null;
  static Logger                                       LOGGER = null;
  static Handler                                    fHandler = null; 
  static Handler                                    cHandler = null; 
  private BTCommunication                             btComm = null;
  private final ArrayList<String>               messagesList = new ArrayList<String>();
  private final SPX42Config                    currentConfig = new SPX42Config();
  private SPX42Config                            savedConfig = null;
  private PleaseWaitDialog                             wDial = null;
  private boolean                               ignoreAction = false;
  private static Level                        optionLogLevel = Level.FINE;
  private static boolean                  readBtCacheOnStart = false;
  private static File                                logFile = new File("logfile.log");
  private static boolean                               DEBUG = false; 
  private final static int                 VERY_CONSERVATIVE = 0;
  private final static int                      CONSERVATIVE = 1;
  private final static int                          MODERATE = 2;
  private final static int                        AGGRESSIVE = 3;
  private final static int                   VERY_AGGRESSIVE = 4;
  //private final static int                        CUSTOMIZED = 5;
  
  //
  //@formatter:on
  private JFrame                  frmMainwindowtitle;
  private JTextField              statusTextField;
  private JMenuItem               mntmExit;
  private JPanel                  connectionPanel;
  private JPanel                  conigPanel;
  private JButton                 connectButton;
  private JComboBox               deviceToConnectComboBox;
  private JMenu                   mnLanguages;
  private JTabbedPane             tabbedPane;
  private JMenu                   mnFile;
  private JMenu                   mnOptions;
  private JMenu                   mnHelp;
  private JMenuItem               mntmHelp;
  private JMenuItem               mntmInfo;
  private JLabel                  serialNumberLabel;
  protected JLabel                serialNumberText;
  private JButton                 readSPX42ConfigButton;
  private JPanel                  decompressionPanel;
  private JLabel                  decoGradientsHighLabel;
  private JSpinner                decoGradientenHighSpinner;
  private JLabel                  decoLaststopLabel;
  private JComboBox               decoLastStopComboBox;
  private JLabel                  decoDyngradientsLabel;
  private JLabel                  decoDeepstopsLabel;
  private JLabel                  decoGradientsLowLabel;
  private JCheckBox               decoDeepStopCheckBox;
  private JSpinner                decoGradientenLowSpinner;
  private JComboBox               decoGradientenPresetComboBox;
  private JLabel                  lblSetpointAutosetpoint;
  private JComboBox               autoSetpointComboBox;
  private JLabel                  lblSetpointHighsetpoint;
  private JComboBox               highSetpointComboBox;
  private JPanel                  setpointPanel;
  private JPanel                  displayPanel;
  private JLabel                  lblDisplayBrightness;
  private JComboBox               displayBrightnessComboBox;
  private JLabel                  lblDisplayOrientation;
  private JComboBox               displayOrientationComboBox;
  private JPanel                  unitsPanel;
  private JLabel                  lblUnitsTemperature;
  private JComboBox               unitsTemperatureComboBox;
  private JLabel                  lblUnitsDepth;
  private JComboBox               unitsDepthComboBox;
  private JLabel                  lblUnitsSalinity;
  private JComboBox               unitsSalnityComboBox;
  private JPanel                  individualPanel;
  private JLabel                  lblSenormode;
  private JLabel                  individualsLogintervalLabel;
  private JComboBox               individualsLogintervalComboBox;
  private JLabel                  lblIndividualsPscrMode;
  private JCheckBox               individualsSensorsOnCheckbox;
  private JCheckBox               individualsPscrModeOnCheckbox;
  private JComboBox               individualsSensorWarnComboBox;
  private JLabel                  individualsAcusticWarningsLabel;
  private JCheckBox               decoDynGradientsCheckBox;
  private JLabel                  lblSensorwarnings;
  private JCheckBox               individualsWarningsOnCheckBox;
  private JLabel                  individualsNotLicensedLabel;
  private JButton                 writeSPX42ConfigButton;
  private JPanel                  debugPanel;
  private JTextField              testCmdTextField;
  private JButton                 testSubmitButton;
  private JLabel                  firmwareVersionLabel;
  private JLabel                  firmwareVersionValueLabel;
  private JButton                 connectBtRefreshButton;
  private JProgressBar            discoverProgressBar;
  private JButton                 pinButton;
  private JLabel                  ackuLabel;
  private JLabel                  lblGas;
  private JSpinner                gas01O2Spinner;
  private JSpinner                gas02HeSpinne;
  private JLabel                  lblO;
  private JLabel                  lblHe;
  private JLabel                  lblGas_1;
  private JCheckBox               chckbxD;
  private JCheckBox               chckbxD_1;
  private JCheckBox               chckbxB;
  private JSpinner                spinner;
  private JSpinner                spinner_1;
  private JSpinner                spinner_2;
  private JSpinner                spinner_3;
  private JSpinner                spinner_4;
  private JSpinner                spinner_5;
  private JLabel                  lblGas_2;

  /**
   * Launch the application.
   * 
   * @param args
   */
  public static void main( String[] args )
  {
    CommandLine cmd = null;
    //
    // Kommandozeilenargumente parsen
    //
    if( null == ( cmd = parseCliOptions( args ) ) )
    {
      System.err.println( "Error while scanning CLI-Args...." );
      System.exit( -1 );
    }
    if( cmd.hasOption( "loglevel" ) )
    {
      optionLogLevel = parseLogLevel( cmd.getOptionValue( "loglevel" ) );
    }
    if( cmd.hasOption( "cacheonstart" ) )
    {
      readBtCacheOnStart = true;
    }
    if( cmd.hasOption( "logfile" ) )
    {
      logFile = parseNewLogFile( cmd.getOptionValue( "logfile" ) );
    }
    if( cmd.hasOption( "debug" ) )
    {
      DEBUG = true;
    }
    //
    // Style bestimmen, wenn möglich
    //
    EventQueue.invokeLater( new Runnable() {
      @Override
      public void run()
      {
        try
        {
          UIManager.setLookAndFeel( UIManager.getLookAndFeel() );
          // Set cross-platform Java L&F (also called "Metal")
          UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        }
        catch( UnsupportedLookAndFeelException ex )
        {
          System.out.print( "fallback to standart look an feel.." );
        }
        catch( ClassNotFoundException ex )
        {
          System.out.print( "fallback to standart look an feel.." );
        }
        catch( InstantiationException ex )
        {
          System.out.print( "fallback to standart look an feel.." );
        }
        catch( IllegalAccessException ex )
        {
          System.out.print( "fallback to standart look an feel.." );
        }
        try
        {
          //
          // das Mainobjekt erzeugen
          //
          MainCommGUI window = new MainCommGUI();
          window.frmMainwindowtitle.setVisible( true );
        }
        catch( Exception e )
        {
          e.printStackTrace();
        }
      }
    } );
  }

  /**
   * Create the application.
   */
  public MainCommGUI()
  {
    setDefaultLookAndFeelDecorated( isDefaultLookAndFeelDecorated() );
    makeLogger( logFile, optionLogLevel );
    try
    {
      programLocale = Locale.getDefault();
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.res.messages", programLocale );
    }
    catch( MissingResourceException ex )
    {
      System.out.println( "ERROR get resources <" + ex.getMessage() + "> try standart Strings..." );
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.res.messages_en" );
    }
    initialize();
    currentConfig.setLogger( LOGGER );
    btComm = new BTCommunication( LOGGER );
    btComm.addActionListener( this );
    String[] entrys = btComm.getNameArray();
    ComboBoxModel portBoxModel = new DefaultComboBoxModel( entrys );
    deviceToConnectComboBox.setModel( portBoxModel );
    initLanuageMenu( programLocale );
    tabbedPane.setEnabledAt( 3, DEBUG );
    if( !DEBUG )
    {
      setAllConfigPanlelsEnabled( false );
      setElementsConnected( false );
    }
    if( readBtCacheOnStart )
    {
      LOGGER.log( Level.INFO, "call discover btdevices cached..." );
      btComm.discoverDevices( true );
      setElementsDiscovering( true );
    }
    if( setLanguageStrings() < 1 )
    {
      System.exit( -1 );
    }
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize()
  {
    frmMainwindowtitle = new JFrame();
    frmMainwindowtitle.setFont( new Font( "Arial", Font.PLAIN, 12 ) );
    frmMainwindowtitle.setSize( new Dimension( 810, 600 ) );
    frmMainwindowtitle.setResizable( false );
    frmMainwindowtitle.setIconImage( Toolkit.getDefaultToolkit().getImage( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/apple.png" ) ) );
    frmMainwindowtitle.setTitle( "TITLE" );
    frmMainwindowtitle.setBounds( 100, 100, 800, 600 );
    frmMainwindowtitle.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frmMainwindowtitle.getContentPane().setLayout( new BorderLayout( 0, 0 ) );
    statusTextField = new JTextField();
    statusTextField.setEditable( false );
    statusTextField.setText( "-" );
    frmMainwindowtitle.getContentPane().add( statusTextField, BorderLayout.SOUTH );
    statusTextField.setColumns( 10 );
    tabbedPane = new JTabbedPane( JTabbedPane.TOP );
    frmMainwindowtitle.getContentPane().add( tabbedPane, BorderLayout.CENTER );
    tabbedPane.addMouseMotionListener( this );
    // GASPANEL
    JPanel panel = new JPanel();
    tabbedPane.addTab( "GAS", null, panel, null );
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[]
    { 20, 20, 20, 20, 50, 20, 50, 0, 0, 0, 0, 0, 0, 0 };
    gbl_panel.rowHeights = new int[]
    { 35, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    gbl_panel.columnWeights = new double[]
    { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_panel.rowWeights = new double[]
    { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    panel.setLayout( gbl_panel );
    lblO = new JLabel( "O2" );
    GridBagConstraints gbc_lblO = new GridBagConstraints();
    gbc_lblO.insets = new Insets( 0, 0, 5, 5 );
    gbc_lblO.gridx = 4;
    gbc_lblO.gridy = 1;
    panel.add( lblO, gbc_lblO );
    lblHe = new JLabel( "HE" );
    GridBagConstraints gbc_lblHe = new GridBagConstraints();
    gbc_lblHe.insets = new Insets( 0, 0, 5, 5 );
    gbc_lblHe.gridx = 6;
    gbc_lblHe.gridy = 1;
    panel.add( lblHe, gbc_lblHe );
    lblGas = new JLabel( "GAS01" );
    GridBagConstraints gbc_lblGas = new GridBagConstraints();
    gbc_lblGas.insets = new Insets( 0, 0, 5, 5 );
    gbc_lblGas.gridx = 1;
    gbc_lblGas.gridy = 2;
    panel.add( lblGas, gbc_lblGas );
    gas01O2Spinner = new JSpinner();
    gas01O2Spinner.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gas01O2Spinner.setAlignmentX( Component.RIGHT_ALIGNMENT );
    GridBagConstraints gbc_gas01O2Spinner = new GridBagConstraints();
    gbc_gas01O2Spinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_gas01O2Spinner.insets = new Insets( 0, 0, 5, 5 );
    gbc_gas01O2Spinner.gridx = 4;
    gbc_gas01O2Spinner.gridy = 2;
    panel.add( gas01O2Spinner, gbc_gas01O2Spinner );
    gas02HeSpinne = new JSpinner();
    gas02HeSpinne.setAlignmentX( Component.RIGHT_ALIGNMENT );
    GridBagConstraints gbc_gas02HeSpinner = new GridBagConstraints();
    gbc_gas02HeSpinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_gas01O2Spinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_gas02HeSpinner.insets = new Insets( 0, 0, 5, 5 );
    gbc_gas02HeSpinner.gridx = 6;
    gbc_gas02HeSpinner.gridy = 2;
    panel.add( gas02HeSpinne, gbc_gas02HeSpinner );
    chckbxD = new JCheckBox( "D1" );
    GridBagConstraints gbc_chckbxD = new GridBagConstraints();
    gbc_chckbxD.anchor = GridBagConstraints.WEST;
    gbc_chckbxD.insets = new Insets( 0, 0, 5, 5 );
    gbc_chckbxD.gridx = 8;
    gbc_chckbxD.gridy = 2;
    panel.add( chckbxD, gbc_chckbxD );
    chckbxD_1 = new JCheckBox( "D2" );
    GridBagConstraints gbc_chckbxD_1 = new GridBagConstraints();
    gbc_chckbxD_1.anchor = GridBagConstraints.WEST;
    gbc_chckbxD_1.insets = new Insets( 0, 0, 5, 5 );
    gbc_chckbxD_1.gridx = 10;
    gbc_chckbxD_1.gridy = 2;
    panel.add( chckbxD_1, gbc_chckbxD_1 );
    chckbxB = new JCheckBox( "B" );
    GridBagConstraints gbc_chckbxB = new GridBagConstraints();
    gbc_chckbxB.insets = new Insets( 0, 0, 5, 0 );
    gbc_chckbxB.gridx = 12;
    gbc_chckbxB.gridy = 2;
    panel.add( chckbxB, gbc_chckbxB );
    lblGas_1 = new JLabel( "GAS02" );
    GridBagConstraints gbc_lblGas_1 = new GridBagConstraints();
    gbc_lblGas_1.insets = new Insets( 0, 0, 5, 5 );
    gbc_lblGas_1.gridx = 1;
    gbc_lblGas_1.gridy = 3;
    panel.add( lblGas_1, gbc_lblGas_1 );
    spinner = new JSpinner();
    spinner.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    spinner.setAlignmentX( 1.0f );
    GridBagConstraints gbc_spinner = new GridBagConstraints();
    gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_spinner.insets = new Insets( 0, 0, 5, 5 );
    gbc_spinner.gridx = 4;
    gbc_spinner.gridy = 3;
    panel.add( spinner, gbc_spinner );
    lblGas_2 = new JLabel( "GAS03" );
    GridBagConstraints gbc_lblGas_2 = new GridBagConstraints();
    gbc_lblGas_2.insets = new Insets( 0, 0, 5, 5 );
    gbc_lblGas_2.gridx = 1;
    gbc_lblGas_2.gridy = 4;
    panel.add( lblGas_2, gbc_lblGas_2 );
    spinner_1 = new JSpinner();
    spinner_1.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    spinner_1.setAlignmentX( 1.0f );
    GridBagConstraints gbc_spinner_1 = new GridBagConstraints();
    gbc_spinner_1.fill = GridBagConstraints.HORIZONTAL;
    gbc_spinner_1.insets = new Insets( 0, 0, 5, 5 );
    gbc_spinner_1.gridx = 4;
    gbc_spinner_1.gridy = 4;
    panel.add( spinner_1, gbc_spinner_1 );
    spinner_2 = new JSpinner();
    spinner_2.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    spinner_2.setAlignmentX( 1.0f );
    GridBagConstraints gbc_spinner_2 = new GridBagConstraints();
    gbc_spinner_2.fill = GridBagConstraints.HORIZONTAL;
    gbc_spinner_2.insets = new Insets( 0, 0, 5, 5 );
    gbc_spinner_2.gridx = 4;
    gbc_spinner_2.gridy = 5;
    panel.add( spinner_2, gbc_spinner_2 );
    spinner_3 = new JSpinner();
    spinner_3.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    spinner_3.setAlignmentX( 1.0f );
    GridBagConstraints gbc_spinner_3 = new GridBagConstraints();
    gbc_spinner_3.fill = GridBagConstraints.HORIZONTAL;
    gbc_spinner_3.insets = new Insets( 0, 0, 5, 5 );
    gbc_spinner_3.gridx = 4;
    gbc_spinner_3.gridy = 6;
    panel.add( spinner_3, gbc_spinner_3 );
    spinner_4 = new JSpinner();
    spinner_4.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    spinner_4.setAlignmentX( 1.0f );
    GridBagConstraints gbc_spinner_4 = new GridBagConstraints();
    gbc_spinner_4.fill = GridBagConstraints.HORIZONTAL;
    gbc_spinner_4.insets = new Insets( 0, 0, 5, 5 );
    gbc_spinner_4.gridx = 4;
    gbc_spinner_4.gridy = 7;
    panel.add( spinner_4, gbc_spinner_4 );
    spinner_5 = new JSpinner();
    spinner_5.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    spinner_5.setAlignmentX( 1.0f );
    GridBagConstraints gbc_spinner_5 = new GridBagConstraints();
    gbc_spinner_5.fill = GridBagConstraints.HORIZONTAL;
    gbc_spinner_5.insets = new Insets( 0, 0, 0, 5 );
    gbc_spinner_5.gridx = 4;
    gbc_spinner_5.gridy = 8;
    panel.add( spinner_5, gbc_spinner_5 );
    // Connection Panel
    connectionPanel = new JPanel();
    tabbedPane.addTab( "CONNECTION", null, connectionPanel, null );
    tabbedPane.setEnabledAt( 1, true );
    deviceToConnectComboBox = new JComboBox();
    deviceToConnectComboBox.addActionListener( this );
    deviceToConnectComboBox.addMouseMotionListener( this );
    deviceToConnectComboBox.setPreferredSize( new Dimension( 220, 40 ) );
    deviceToConnectComboBox.setMinimumSize( new Dimension( 180, 20 ) );
    deviceToConnectComboBox.setMaximumSize( new Dimension( 500, 40 ) );
    connectButton = new JButton( "CONNECT" );
    connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112-mono.png" ) ) );
    connectButton.addActionListener( this );
    connectButton.setActionCommand( "connect" );
    connectButton.addMouseMotionListener( this );
    connectButton.setPreferredSize( new Dimension( 180, 40 ) );
    connectButton.setMaximumSize( new Dimension( 160, 40 ) );
    connectButton.setSize( new Dimension( 160, 40 ) );
    connectButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    connectBtRefreshButton = new JButton( "REFRESH" );
    connectBtRefreshButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Refresh.png" ) ) );
    connectBtRefreshButton.addActionListener( this );
    connectBtRefreshButton.addMouseMotionListener( this );
    connectBtRefreshButton.setActionCommand( "refresh_bt_devices" );
    discoverProgressBar = new JProgressBar();
    discoverProgressBar.setBorder( null );
    discoverProgressBar.setBackground( new Color( 240, 248, 255 ) );
    discoverProgressBar.setForeground( new Color( 176, 224, 230 ) );
    pinButton = new JButton( "PINBUTTON" );
    pinButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Unlock.png" ) ) );
    pinButton.addActionListener( this );
    pinButton.setActionCommand( "set_pin_for_dev" );
    pinButton.addMouseMotionListener( this );
    ackuLabel = new JLabel( " " );
    GroupLayout gl_connectionPanel = new GroupLayout( connectionPanel );
    gl_connectionPanel.setHorizontalGroup( gl_connectionPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_connectionPanel
                    .createSequentialGroup()
                    .addGroup(
                            gl_connectionPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_connectionPanel
                                                    .createSequentialGroup()
                                                    .addGap( 45 )
                                                    .addGroup(
                                                            gl_connectionPanel.createParallelGroup( Alignment.LEADING, false )
                                                                    .addComponent( ackuLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                    .addComponent( deviceToConnectComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                                                    .addGap( 70 )
                                                    .addGroup(
                                                            gl_connectionPanel
                                                                    .createParallelGroup( Alignment.LEADING, false )
                                                                    .addComponent( connectBtRefreshButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                    .addGroup(
                                                                            gl_connectionPanel.createSequentialGroup()
                                                                                    .addComponent( connectButton, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE )
                                                                                    .addPreferredGap( ComponentPlacement.RELATED ).addComponent( pinButton ) ) ) )
                                    .addGroup(
                                            gl_connectionPanel.createSequentialGroup().addContainerGap()
                                                    .addComponent( discoverProgressBar, GroupLayout.PREFERRED_SIZE, 763, GroupLayout.PREFERRED_SIZE ) ) )
                    .addContainerGap( 52, Short.MAX_VALUE ) ) );
    gl_connectionPanel.setVerticalGroup( gl_connectionPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_connectionPanel
                    .createSequentialGroup()
                    .addGap( 22 )
                    .addGroup(
                            gl_connectionPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_connectionPanel
                                                    .createSequentialGroup()
                                                    .addGroup(
                                                            gl_connectionPanel.createParallelGroup( Alignment.LEADING, false )
                                                                    .addComponent( pinButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                    .addComponent( connectButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                                                    .addGap( 18 ).addComponent( connectBtRefreshButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE ) )
                                    .addGroup(
                                            gl_connectionPanel.createSequentialGroup()
                                                    .addComponent( deviceToConnectComboBox, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( ackuLabel ) ) )
                    .addPreferredGap( ComponentPlacement.RELATED, 356, Short.MAX_VALUE )
                    .addComponent( discoverProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ).addContainerGap() ) );
    connectionPanel.setLayout( gl_connectionPanel );
    // config Panel
    conigPanel = new JPanel();
    tabbedPane.addTab( "CONFIG", null, conigPanel, null );
    readSPX42ConfigButton = new JButton( "READ" );
    readSPX42ConfigButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Download.png" ) ) );
    readSPX42ConfigButton.setForeground( new Color( 0, 100, 0 ) );
    readSPX42ConfigButton.setBackground( new Color( 152, 251, 152 ) );
    readSPX42ConfigButton.addActionListener( this );
    readSPX42ConfigButton.addMouseMotionListener( this );
    readSPX42ConfigButton.setActionCommand( "read_config" );
    readSPX42ConfigButton.setPreferredSize( new Dimension( 180, 40 ) );
    readSPX42ConfigButton.setMaximumSize( new Dimension( 160, 40 ) );
    readSPX42ConfigButton.setSize( new Dimension( 160, 40 ) );
    readSPX42ConfigButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    writeSPX42ConfigButton = new JButton( "WRITE" );
    writeSPX42ConfigButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Upload.png" ) ) );
    writeSPX42ConfigButton.setForeground( new Color( 255, 0, 0 ) );
    writeSPX42ConfigButton.setBackground( new Color( 255, 192, 203 ) );
    writeSPX42ConfigButton.addActionListener( this );
    writeSPX42ConfigButton.setActionCommand( "write_config" );
    writeSPX42ConfigButton.addMouseMotionListener( this );
    serialNumberLabel = new JLabel( "SERIAL" );
    serialNumberLabel.setAlignmentX( Component.RIGHT_ALIGNMENT );
    serialNumberLabel.setMaximumSize( new Dimension( 250, 40 ) );
    serialNumberLabel.setPreferredSize( new Dimension( 140, 20 ) );
    serialNumberText = new JLabel( "0" );
    serialNumberText.setMaximumSize( new Dimension( 250, 40 ) );
    serialNumberText.setPreferredSize( new Dimension( 140, 20 ) );
    firmwareVersionLabel = new JLabel( "FIRMW-VERSION" );
    firmwareVersionValueLabel = new JLabel( "V0.0" );
    // config -> DECO-Panel
    decompressionPanel = new JPanel();
    decompressionPanel.setBounds( new Rectangle( 0, 0, 200, 160 ) );
    decompressionPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Deco", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    // config -> DECO-Panel -> inhalt
    decoGradientsLowLabel = new JLabel( "GF-low" );
    decoGradientsLowLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoGradientenLowSpinner = new JSpinner();
    decoGradientsLowLabel.setLabelFor( decoGradientenLowSpinner );
    decoGradientenLowSpinner.addChangeListener( this );
    decoGradientenLowSpinner.addMouseMotionListener( this );
    decoGradientenPresetComboBox = new JComboBox();
    decoGradientenPresetComboBox.addActionListener( this );
    decoGradientenPresetComboBox.setActionCommand( "deco_gradient_preset" );
    decoGradientenPresetComboBox.addMouseMotionListener( this );
    decoGradientsHighLabel = new JLabel( "GF-High" );
    decoGradientsHighLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoGradientenHighSpinner = new JSpinner();
    decoGradientsHighLabel.setLabelFor( decoGradientenHighSpinner );
    decoGradientenHighSpinner.addChangeListener( this );
    decoGradientenHighSpinner.addMouseMotionListener( this );
    decoLaststopLabel = new JLabel( "last stop" );
    decoLaststopLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoLastStopComboBox = new JComboBox();
    decoLastStopComboBox.addActionListener( this );
    decoLastStopComboBox.setActionCommand( "deco_last_stop" );
    decoLastStopComboBox.addMouseMotionListener( this );
    decoLaststopLabel.setLabelFor( decoLastStopComboBox );
    decoDyngradientsLabel = new JLabel( "dyn.Gradients" );
    decoDyngradientsLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoDynGradientsCheckBox = new JCheckBox( "dyn Gradients ON" );
    decoDynGradientsCheckBox.setActionCommand( "dyn_gradients_on" );
    // decoDynGradientsCheckBox.addChangeListener( this );
    decoDynGradientsCheckBox.addMouseMotionListener( this );
    decoDynGradientsCheckBox.addItemListener( this );
    decoDyngradientsLabel.setLabelFor( decoDynGradientsCheckBox );
    decoDeepstopsLabel = new JLabel( "deepstops" );
    decoDeepstopsLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoDeepStopCheckBox = new JCheckBox( "Deepstops ON" );
    decoDeepStopCheckBox.setActionCommand( "deepstops_on" );
    // decoDeepStopCheckBox.addChangeListener( this );
    decoDeepStopCheckBox.addItemListener( this );
    decoDeepStopCheckBox.addMouseMotionListener( this );
    decoDeepstopsLabel.setLabelFor( decoDeepStopCheckBox );
    // config -> DECO-Panel -> Positionierung
    GroupLayout gl_decompressionPanel = new GroupLayout( decompressionPanel );
    gl_decompressionPanel.setHorizontalGroup( gl_decompressionPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_decompressionPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_decompressionPanel.createParallelGroup( Alignment.TRAILING, false )
                                    .addComponent( decoDyngradientsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                    .addComponent( decoDeepstopsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                    .addComponent( decoLaststopLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                    .addComponent( decoGradientsHighLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                    .addComponent( decoGradientsLowLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                    .addGap( 18 )
                    .addGroup(
                            gl_decompressionPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_decompressionPanel
                                                    .createSequentialGroup()
                                                    .addGroup(
                                                            gl_decompressionPanel.createParallelGroup( Alignment.LEADING, false )
                                                                    .addComponent( decoLastStopComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                    .addComponent( decoGradientenLowSpinner, GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE )
                                                                    .addComponent( decoGradientenHighSpinner ) ).addGap( 18 )
                                                    .addComponent( decoGradientenPresetComboBox, 0, 151, Short.MAX_VALUE ) ).addComponent( decoDynGradientsCheckBox )
                                    .addComponent( decoDeepStopCheckBox ) ).addContainerGap() ) );
    gl_decompressionPanel.setVerticalGroup( gl_decompressionPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_decompressionPanel
                    .createSequentialGroup()
                    .addGroup(
                            gl_decompressionPanel.createParallelGroup( Alignment.BASELINE ).addComponent( decoGradientsLowLabel )
                                    .addComponent( decoGradientenLowSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( decoGradientenPresetComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_decompressionPanel.createParallelGroup( Alignment.BASELINE ).addComponent( decoGradientsHighLabel )
                                    .addComponent( decoGradientenHighSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_decompressionPanel.createParallelGroup( Alignment.BASELINE ).addComponent( decoLaststopLabel )
                                    .addComponent( decoLastStopComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                    .addPreferredGap( ComponentPlacement.RELATED, 6, Short.MAX_VALUE )
                    .addGroup( gl_decompressionPanel.createParallelGroup( Alignment.BASELINE ).addComponent( decoDyngradientsLabel ).addComponent( decoDynGradientsCheckBox ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup( gl_decompressionPanel.createParallelGroup( Alignment.LEADING ).addComponent( decoDeepstopsLabel ).addComponent( decoDeepStopCheckBox ) )
                    .addContainerGap() ) );
    decompressionPanel.setLayout( gl_decompressionPanel );
    // config -> setpoint Panel -> Inhalt
    setpointPanel = new JPanel();
    setpointPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Setpoint", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblSetpointAutosetpoint = new JLabel( "Autosetpoint" );
    lblSetpointAutosetpoint.setHorizontalAlignment( SwingConstants.RIGHT );
    autoSetpointComboBox = new JComboBox();
    lblSetpointAutosetpoint.setLabelFor( autoSetpointComboBox );
    autoSetpointComboBox.setActionCommand( "set_autosetpoint" );
    autoSetpointComboBox.addActionListener( this );
    autoSetpointComboBox.addMouseMotionListener( this );
    lblSetpointHighsetpoint = new JLabel( "Highsetpoint" );
    lblSetpointHighsetpoint.setHorizontalAlignment( SwingConstants.RIGHT );
    highSetpointComboBox = new JComboBox();
    highSetpointComboBox.setActionCommand( "set_highsetpoint" );
    highSetpointComboBox.addActionListener( this );
    highSetpointComboBox.addMouseMotionListener( this );
    lblSetpointHighsetpoint.setLabelFor( highSetpointComboBox );
    // config -> setpoint panel => layout
    GroupLayout gl_setpointPanel = new GroupLayout( setpointPanel );
    gl_setpointPanel.setHorizontalGroup( gl_setpointPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_setpointPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_setpointPanel.createParallelGroup( Alignment.TRAILING )
                                    .addComponent( lblSetpointHighsetpoint, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( lblSetpointAutosetpoint, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                    .addGap( 18 )
                    .addGroup(
                            gl_setpointPanel.createParallelGroup( Alignment.LEADING ).addComponent( autoSetpointComboBox, 0, 188, Short.MAX_VALUE )
                                    .addComponent( highSetpointComboBox, 0, 188, Short.MAX_VALUE ) ).addContainerGap() ) );
    gl_setpointPanel.setVerticalGroup( gl_setpointPanel.createParallelGroup( Alignment.TRAILING ).addGroup(
            Alignment.LEADING,
            gl_setpointPanel
                    .createSequentialGroup()
                    .addGroup(
                            gl_setpointPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( autoSetpointComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( lblSetpointAutosetpoint ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_setpointPanel.createParallelGroup( Alignment.LEADING ).addComponent( lblSetpointHighsetpoint )
                                    .addComponent( highSetpointComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                    .addContainerGap( 78, Short.MAX_VALUE ) ) );
    setpointPanel.setLayout( gl_setpointPanel );
    // config -> display panel -> Inhalt
    displayPanel = new JPanel();
    displayPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Display", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblDisplayBrightness = new JLabel( "brightness" );
    lblDisplayBrightness.setHorizontalAlignment( SwingConstants.RIGHT );
    displayBrightnessComboBox = new JComboBox();
    lblDisplayBrightness.setLabelFor( displayBrightnessComboBox );
    displayBrightnessComboBox.setActionCommand( "set_disp_brightness" );
    displayBrightnessComboBox.addActionListener( this );
    displayBrightnessComboBox.addMouseMotionListener( this );
    lblDisplayOrientation = new JLabel( "orientation" );
    lblDisplayOrientation.setHorizontalAlignment( SwingConstants.RIGHT );
    displayOrientationComboBox = new JComboBox();
    lblDisplayOrientation.setLabelFor( displayOrientationComboBox );
    displayOrientationComboBox.setActionCommand( "set_display_orientation" );
    displayOrientationComboBox.addActionListener( this );
    displayOrientationComboBox.addMouseMotionListener( this );
    // config -> display panel .-> layout
    GroupLayout gl_displayPanel = new GroupLayout( displayPanel );
    gl_displayPanel.setHorizontalGroup( gl_displayPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_displayPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_displayPanel.createParallelGroup( Alignment.LEADING, false )
                                    .addComponent( lblDisplayOrientation, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                    .addComponent( lblDisplayBrightness, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE ) )
                    .addGap( 18 )
                    .addGroup(
                            gl_displayPanel.createParallelGroup( Alignment.TRAILING ).addComponent( displayBrightnessComboBox, 0, 235, Short.MAX_VALUE )
                                    .addComponent( displayOrientationComboBox, 0, 235, Short.MAX_VALUE ) ).addContainerGap() ) );
    gl_displayPanel.setVerticalGroup( gl_displayPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_displayPanel
                    .createSequentialGroup()
                    .addGroup(
                            gl_displayPanel.createParallelGroup( Alignment.BASELINE ).addComponent( lblDisplayBrightness )
                                    .addComponent( displayBrightnessComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_displayPanel.createParallelGroup( Alignment.BASELINE ).addComponent( lblDisplayOrientation )
                                    .addComponent( displayOrientationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                    .addContainerGap( 14, Short.MAX_VALUE ) ) );
    displayPanel.setLayout( gl_displayPanel );
    // config -> untits panel -> Inhalt
    unitsPanel = new JPanel();
    unitsPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Units", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblUnitsTemperature = new JLabel( "temperature" );
    lblUnitsTemperature.setHorizontalAlignment( SwingConstants.RIGHT );
    unitsTemperatureComboBox = new JComboBox();
    lblUnitsTemperature.setLabelFor( unitsTemperatureComboBox );
    unitsTemperatureComboBox.setActionCommand( "set_temperature_unit" );
    unitsTemperatureComboBox.addActionListener( this );
    unitsTemperatureComboBox.addMouseMotionListener( this );
    lblUnitsDepth = new JLabel( "depth" );
    lblUnitsDepth.setHorizontalAlignment( SwingConstants.RIGHT );
    unitsDepthComboBox = new JComboBox();
    lblUnitsDepth.setLabelFor( unitsDepthComboBox );
    unitsDepthComboBox.setActionCommand( "set_depth_unit" );
    unitsDepthComboBox.addActionListener( this );
    unitsDepthComboBox.addMouseMotionListener( this );
    lblUnitsSalinity = new JLabel( "salinity" );
    lblUnitsSalinity.setHorizontalAlignment( SwingConstants.RIGHT );
    unitsSalnityComboBox = new JComboBox();
    lblUnitsSalinity.setLabelFor( unitsSalnityComboBox );
    unitsSalnityComboBox.setActionCommand( "set_salnity" );
    unitsSalnityComboBox.addActionListener( this );
    unitsSalnityComboBox.addMouseMotionListener( this );
    // config -> units panel -> Layout
    GroupLayout gl_unitsPanel = new GroupLayout( unitsPanel );
    gl_unitsPanel.setHorizontalGroup( gl_unitsPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_unitsPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_unitsPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup( Alignment.TRAILING,
                                            gl_unitsPanel.createSequentialGroup().addComponent( lblUnitsTemperature, GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE ).addGap( 19 ) )
                                    .addGroup(
                                            Alignment.TRAILING,
                                            gl_unitsPanel
                                                    .createSequentialGroup()
                                                    .addGroup(
                                                            gl_unitsPanel.createParallelGroup( Alignment.TRAILING )
                                                                    .addComponent( lblUnitsDepth, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE )
                                                                    .addComponent( lblUnitsSalinity, GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE ) ).addGap( 18 ) ) )
                    .addGroup(
                            gl_unitsPanel.createParallelGroup( Alignment.TRAILING ).addComponent( unitsSalnityComboBox, 0, 207, Short.MAX_VALUE )
                                    .addComponent( unitsDepthComboBox, 0, 207, Short.MAX_VALUE ).addComponent( unitsTemperatureComboBox, 0, 207, Short.MAX_VALUE ) )
                    .addContainerGap() ) );
    gl_unitsPanel.setVerticalGroup( gl_unitsPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_unitsPanel
                    .createSequentialGroup()
                    .addGroup(
                            gl_unitsPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( unitsTemperatureComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( lblUnitsTemperature ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_unitsPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( unitsDepthComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( lblUnitsDepth ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_unitsPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( unitsSalnityComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( lblUnitsSalinity ) ).addContainerGap( 13, Short.MAX_VALUE ) ) );
    unitsPanel.setLayout( gl_unitsPanel );
    // config -> individual panel -> inhalt
    individualPanel = new JPanel();
    individualPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Individuals", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblSenormode = new JLabel( "sensormode" );
    lblSenormode.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsSensorsOnCheckbox = new JCheckBox( "Sensors ON" );
    lblSenormode.setLabelFor( individualsSensorsOnCheckbox );
    // chIndividualsSensorsOnCheckbox.addChangeListener( this );
    individualsSensorsOnCheckbox.setActionCommand( "individual_sensors_on" );
    individualsSensorsOnCheckbox.addItemListener( this );
    individualsSensorsOnCheckbox.addMouseMotionListener( this );
    // chIndividualsSensorsOnCheckbox.addActionListener( this );
    lblIndividualsPscrMode = new JLabel( "PSCR Mode" );
    lblIndividualsPscrMode.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsPscrModeOnCheckbox = new JCheckBox( "PSCR Mode ON" );
    individualsPscrModeOnCheckbox.setForeground( new Color( 128, 0, 128 ) );
    lblIndividualsPscrMode.setLabelFor( individualsPscrModeOnCheckbox );
    // IndividualsPscrModoOnCheckbox.addChangeListener( this );
    individualsPscrModeOnCheckbox.setActionCommand( "individuals_pscr_on" );
    individualsPscrModeOnCheckbox.addItemListener( this );
    individualsPscrModeOnCheckbox.addMouseMotionListener( this );
    lblSensorwarnings = new JLabel( "sensorwarnings" );
    lblSensorwarnings.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsSensorWarnComboBox = new JComboBox();
    lblSensorwarnings.setLabelFor( individualsSensorWarnComboBox );
    individualsSensorWarnComboBox.addActionListener( this );
    individualsSensorWarnComboBox.setActionCommand( "set_sensorwarnings" );
    individualsSensorWarnComboBox.addMouseMotionListener( this );
    individualsAcusticWarningsLabel = new JLabel( "acustic warnings" );
    individualsAcusticWarningsLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsWarningsOnCheckBox = new JCheckBox( "warnings ON" );
    individualsAcusticWarningsLabel.setLabelFor( individualsWarningsOnCheckBox );
    // individualsWarningsOnCheckBox.addChangeListener( this );
    individualsWarningsOnCheckBox.setActionCommand( "individuals_warnings_on" );
    individualsWarningsOnCheckBox.addItemListener( this );
    individualsWarningsOnCheckBox.addMouseMotionListener( this );
    individualsLogintervalLabel = new JLabel( "loginterval" );
    individualsLogintervalLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsLogintervalComboBox = new JComboBox();
    individualsLogintervalLabel.setLabelFor( individualsLogintervalComboBox );
    individualsLogintervalComboBox.addActionListener( this );
    individualsLogintervalComboBox.setActionCommand( "set_loginterval" );
    individualsLogintervalComboBox.addMouseMotionListener( this );
    individualsNotLicensedLabel = new JLabel( "------" );
    individualsNotLicensedLabel.setForeground( Color.DARK_GRAY );
    individualsNotLicensedLabel.setFont( new Font( "Tahoma", Font.ITALIC, 11 ) );
    individualsNotLicensedLabel.setHorizontalAlignment( SwingConstants.CENTER );
    // config -> individuals panel -> layout
    GroupLayout gl_individualPanel = new GroupLayout( individualPanel );
    gl_individualPanel.setHorizontalGroup( gl_individualPanel.createParallelGroup( Alignment.TRAILING ).addGroup(
            gl_individualPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_individualPanel
                                    .createParallelGroup( Alignment.TRAILING )
                                    .addComponent( individualsNotLicensedLabel, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE )
                                    .addGroup(
                                            gl_individualPanel
                                                    .createSequentialGroup()
                                                    .addGroup(
                                                            gl_individualPanel
                                                                    .createParallelGroup( Alignment.LEADING )
                                                                    .addComponent( individualsLogintervalLabel, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE )
                                                                    .addComponent( individualsAcusticWarningsLabel, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE )
                                                                    .addComponent( lblIndividualsPscrMode, Alignment.TRAILING )
                                                                    .addComponent( lblSenormode, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE )
                                                                    .addComponent( lblSensorwarnings, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 97,
                                                                            GroupLayout.PREFERRED_SIZE ) )
                                                    .addGap( 18 )
                                                    .addGroup(
                                                            gl_individualPanel.createParallelGroup( Alignment.LEADING )
                                                                    .addComponent( individualsLogintervalComboBox, 0, 181, Short.MAX_VALUE )
                                                                    .addComponent( individualsSensorsOnCheckbox, GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE )
                                                                    .addComponent( individualsSensorWarnComboBox, 0, 181, Short.MAX_VALUE )
                                                                    .addComponent( individualsPscrModeOnCheckbox, GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE )
                                                                    .addComponent( individualsWarningsOnCheckBox, GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE ) ) ) )
                    .addGap( 17 ) ) );
    gl_individualPanel.setVerticalGroup( gl_individualPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_individualPanel
                    .createSequentialGroup()
                    .addGroup( gl_individualPanel.createParallelGroup( Alignment.BASELINE ).addComponent( individualsSensorsOnCheckbox ).addComponent( lblSenormode ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup( gl_individualPanel.createParallelGroup( Alignment.BASELINE ).addComponent( lblIndividualsPscrMode ).addComponent( individualsPscrModeOnCheckbox ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_individualPanel.createParallelGroup( Alignment.BASELINE ).addComponent( lblSensorwarnings )
                                    .addComponent( individualsSensorWarnComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_individualPanel.createParallelGroup( Alignment.BASELINE ).addComponent( individualsAcusticWarningsLabel )
                                    .addComponent( individualsWarningsOnCheckBox ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_individualPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( individualsLogintervalComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( individualsLogintervalLabel ) ).addGap( 32 ).addComponent( individualsNotLicensedLabel )
                    .addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) );
    individualPanel.setLayout( gl_individualPanel );
    // config -> layout
    GroupLayout gl_conigPanel = new GroupLayout( conigPanel );
    gl_conigPanel.setHorizontalGroup( gl_conigPanel.createParallelGroup( Alignment.TRAILING )
            .addGroup(
                    gl_conigPanel
                            .createSequentialGroup()
                            .addGroup(
                                    gl_conigPanel
                                            .createParallelGroup( Alignment.TRAILING )
                                            .addGroup(
                                                    Alignment.LEADING,
                                                    gl_conigPanel.createSequentialGroup().addContainerGap()
                                                            .addComponent( readSPX42ConfigButton, GroupLayout.PREFERRED_SIZE, 199, GroupLayout.PREFERRED_SIZE ).addGap( 339 )
                                                            .addComponent( writeSPX42ConfigButton, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE ) )
                                            .addGroup(
                                                    gl_conigPanel
                                                            .createSequentialGroup()
                                                            .addGroup(
                                                                    gl_conigPanel
                                                                            .createParallelGroup( Alignment.TRAILING )
                                                                            .addGroup(
                                                                                    gl_conigPanel
                                                                                            .createSequentialGroup()
                                                                                            .addContainerGap()
                                                                                            .addGroup(
                                                                                                    gl_conigPanel
                                                                                                            .createParallelGroup( Alignment.LEADING )
                                                                                                            .addComponent( unitsPanel, GroupLayout.DEFAULT_SIZE, 389,
                                                                                                                    Short.MAX_VALUE )
                                                                                                            .addComponent( displayPanel, GroupLayout.PREFERRED_SIZE,
                                                                                                                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                                                            .addComponent( decompressionPanel, GroupLayout.PREFERRED_SIZE, 385,
                                                                                                                    GroupLayout.PREFERRED_SIZE ) )
                                                                                            .addPreferredGap( ComponentPlacement.UNRELATED ) )
                                                                            .addGroup(
                                                                                    gl_conigPanel
                                                                                            .createSequentialGroup()
                                                                                            .addGap( 82 )
                                                                                            .addComponent( serialNumberLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                                                    GroupLayout.PREFERRED_SIZE )
                                                                                            .addPreferredGap( ComponentPlacement.RELATED )
                                                                                            .addComponent( serialNumberText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                                                    GroupLayout.PREFERRED_SIZE ).addGap( 41 ) ) )
                                                            .addGroup(
                                                                    gl_conigPanel
                                                                            .createParallelGroup( Alignment.LEADING )
                                                                            .addGroup(
                                                                                    gl_conigPanel
                                                                                            .createSequentialGroup()
                                                                                            .addComponent( firmwareVersionLabel )
                                                                                            .addGap( 56 )
                                                                                            .addComponent( firmwareVersionValueLabel, GroupLayout.PREFERRED_SIZE, 212,
                                                                                                    GroupLayout.PREFERRED_SIZE ) )
                                                                            .addComponent( setpointPanel, GroupLayout.PREFERRED_SIZE, 344, GroupLayout.PREFERRED_SIZE )
                                                                            .addComponent( individualPanel, GroupLayout.PREFERRED_SIZE, 344, GroupLayout.PREFERRED_SIZE ) ) ) )
                            .addGap( 10 ) ) );
    gl_conigPanel.setVerticalGroup( gl_conigPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_conigPanel
                    .createSequentialGroup()
                    .addGap( 20 )
                    .addGroup(
                            gl_conigPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( serialNumberLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( serialNumberText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( firmwareVersionLabel ).addComponent( firmwareVersionValueLabel ) )
                    .addPreferredGap( ComponentPlacement.UNRELATED )
                    .addGroup(
                            gl_conigPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( decompressionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( setpointPanel, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_conigPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_conigPanel.createSequentialGroup().addComponent( displayPanel, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.RELATED )
                                                    .addComponent( unitsPanel, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE ) )
                                    .addComponent( individualPanel, 0, 0, Short.MAX_VALUE ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_conigPanel.createParallelGroup( Alignment.LEADING ).addComponent( writeSPX42ConfigButton, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE )
                                    .addComponent( readSPX42ConfigButton, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE ) ).addContainerGap() ) );
    conigPanel.setLayout( gl_conigPanel );
    // Debug-Panel
    debugPanel = new JPanel();
    debugPanel.setBorder( new EtchedBorder( EtchedBorder.LOWERED, Color.GRAY, Color.DARK_GRAY ) );
    tabbedPane.addTab( "DEBUG/TEST", null, debugPanel, null );
    testCmdTextField = new JTextField();
    testCmdTextField.setForeground( Color.MAGENTA );
    testCmdTextField.setFont( new Font( "SansSerif", Font.PLAIN, 12 ) );
    testCmdTextField.setBackground( Color.LIGHT_GRAY );
    testCmdTextField.setColumns( 10 );
    testSubmitButton = new JButton( "submit !" );
    testSubmitButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/57.png" ) ) );
    testSubmitButton.addActionListener( this );
    testSubmitButton.setActionCommand( "send_test_cmd" );
    // debug-Panel Layout
    GroupLayout gl_debugPanel = new GroupLayout( debugPanel );
    gl_debugPanel.setHorizontalGroup( gl_debugPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_debugPanel.createSequentialGroup().addGap( 22 ).addComponent( testCmdTextField, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE ).addGap( 18 )
                    .addComponent( testSubmitButton, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE ).addContainerGap( 248, Short.MAX_VALUE ) ) );
    gl_debugPanel.setVerticalGroup( gl_debugPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_debugPanel
                    .createSequentialGroup()
                    .addGap( 23 )
                    .addGroup(
                            gl_debugPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( testCmdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( testSubmitButton ) ).addContainerGap( 456, Short.MAX_VALUE ) ) );
    debugPanel.setLayout( gl_debugPanel );
    tabbedPane.setEnabledAt( 2, true );
    // tabbedPane.setEnabledAt(1, false);
    // MENÜ
    JMenuBar menuBar = new JMenuBar();
    frmMainwindowtitle.setJMenuBar( menuBar );
    mnFile = new JMenu( "FILE" );
    menuBar.add( mnFile );
    mntmExit = new JMenuItem( "EXIT" );
    mntmExit.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/176.png" ) ) );
    mntmExit.setActionCommand( "exit" );
    mntmExit.addActionListener( this );
    mntmExit.addMouseMotionListener( this );
    mntmExit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, InputEvent.CTRL_MASK ) );
    mnFile.add( mntmExit );
    mnLanguages = new JMenu( "LANGUAGES" );
    mnLanguages.addMouseMotionListener( this );
    menuBar.add( mnLanguages );
    mnOptions = new JMenu( "OPTIONS" );
    mnOptions.addMouseMotionListener( this );
    menuBar.add( mnOptions );
    JMenuItem mntmEmpty = new JMenuItem( "EMPTY" );
    mntmEmpty.addMouseMotionListener( this );
    mnOptions.add( mntmEmpty );
    mnHelp = new JMenu( "HELP" );
    mnHelp.addMouseMotionListener( this );
    menuBar.add( mnHelp );
    mntmHelp = new JMenuItem( "HELP" );
    mntmHelp.addActionListener( this );
    mntmHelp.setActionCommand( "help" );
    mntmHelp.addMouseMotionListener( this );
    mnHelp.add( mntmHelp );
    mntmInfo = new JMenuItem( "INFO" );
    mntmInfo.addActionListener( this );
    mntmInfo.setActionCommand( "info" );
    mntmInfo.addMouseMotionListener( this );
    mntmInfo.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/javax/swing/plaf/metal/icons/ocean/expanded.gif" ) ) );
    mntmInfo.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_I, InputEvent.CTRL_MASK ) );
    mnHelp.add( mntmInfo );
    discoverProgressBar.setVisible( false );
  }

  /**
   * 
   * Setze alle Strings im Form
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.12.2011
   */
  private int setLanguageStrings()
  {
    String[] entrys = null;
    ComboBoxModel portBoxModel = null;
    // so, ignoriere mal alles....
    ignoreAction = true;
    try
    {
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.res.messages", programLocale );
    }
    catch( MissingResourceException ex )
    {
      System.out.println( "ERROR get resources <" + ex.getMessage() + "> ABORT!" );
      return( -1 );
    }
    try
    {
      setStatus( "" );
      // Hauptfenster
      frmMainwindowtitle.setTitle( stringsBundle.getString( "MainCommGUI.frmMainwindowtitle.title" ) );
      // Menü
      mnFile.setText( stringsBundle.getString( "MainCommGUI.mnFile.text" ) );
      mnFile.setToolTipText( stringsBundle.getString( "MainCommGUI.mnFile.tooltiptext" ) );
      mntmExit.setText( stringsBundle.getString( "MainCommGUI.mntmExit.text" ) );
      mntmExit.setToolTipText( stringsBundle.getString( "MainCommGUI.mntmExit.tooltiptext" ) );
      mnLanguages.setText( stringsBundle.getString( "MainCommGUI.mnLanguages.text" ) );
      mnLanguages.setToolTipText( stringsBundle.getString( "MainCommGUI.mnLanguages.tooltiptext" ) );
      mnOptions.setText( stringsBundle.getString( "MainCommGUI.mnOptions.text" ) );
      mnOptions.setToolTipText( stringsBundle.getString( "MainCommGUI.mnOptions.tooltiptext" ) );
      mnHelp.setText( stringsBundle.getString( "MainCommGUI.mnHelp.text" ) );
      mnHelp.setToolTipText( stringsBundle.getString( "MainCommGUI.mnHelp.tooltiptext" ) );
      mntmHelp.setText( stringsBundle.getString( "MainCommGUI.mntmHelp.text" ) );
      mntmHelp.setToolTipText( stringsBundle.getString( "MainCommGUI.mntmHelp.tooltiptext" ) );
      mntmInfo.setText( stringsBundle.getString( "MainCommGUI.mntmInfo.text" ) );
      mntmInfo.setToolTipText( stringsBundle.getString( "MainCommGUI.mntmInfo.tooltiptext" ) );
      // Tabbed Panes
      // //////////////////////////////////////////////////////////////////////
      // Tabbes Pane connect
      tabbedPane.setTitleAt( 0, stringsBundle.getString( "MainCommGUI.connectPanel.title" ) );
      deviceToConnectComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.portComboBox.tooltiptext" ) );
      connectButton.setToolTipText( stringsBundle.getString( "MainCommGUI.connectButton.tooltiptext" ) );
      pinButton.setToolTipText( stringsBundle.getString( "MainCommGUI.pinButton.tooltiptext" ) );
      pinButton.setText( stringsBundle.getString( "MainCommGUI.pinButton.text" ) );
      // if( sComm.isConnected() )
      if( btComm.isConnected() )
      {
        connectButton.setText( stringsBundle.getString( "MainCommGUI.connectButton.disconnectText" ) );
        connectButton.setActionCommand( "disconnect" );
      }
      else
      {
        connectButton.setText( stringsBundle.getString( "MainCommGUI.connectButton.connectText" ) );
        connectButton.setActionCommand( "connect" );
      }
      connectBtRefreshButton.setText( stringsBundle.getString( "MainCommGUI.connectBtRefreshButton.text" ) );
      connectBtRefreshButton.setToolTipText( stringsBundle.getString( "MainCommGUI.connectBtRefreshButton.tooltiptext" ) );
      // //////////////////////////////////////////////////////////////////////
      // Tabbes Pane config
      tabbedPane.setTitleAt( 1, stringsBundle.getString( "MainCommGUI.conigPanel.title" ) );
      serialNumberLabel.setText( stringsBundle.getString( "MainCommGUI.serialNumberLabel.text" ) );
      readSPX42ConfigButton.setText( stringsBundle.getString( "MainCommGUI.readSPX42ConfigButton.text" ) );
      readSPX42ConfigButton.setToolTipText( stringsBundle.getString( "MainCommGUI.readSPX42ConfigButton.tooltiptext" ) );
      writeSPX42ConfigButton.setText( stringsBundle.getString( "MainCommGUI.writeSPX42ConfigButton.text" ) );
      writeSPX42ConfigButton.setToolTipText( stringsBundle.getString( "MainCommGUI.writeSPX42ConfigButton.tooltiptext" ) );
      firmwareVersionLabel.setText( stringsBundle.getString( "MainCommGUI.firmwareVersionLabel.text" ) );
      // DECO
      ( ( TitledBorder )( decompressionPanel.getBorder() ) ).setTitle( stringsBundle.getString( "MainCommGUI.decoTitleBorder.text" ) );
      decoGradientenPresetComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.decoGradientenPresetComboBox.tooltiptext" ) );
      decoGradientsHighLabel.setText( stringsBundle.getString( "MainCommGUI.decoGradientsHighLabel.text" ) );
      decoGradientsLowLabel.setText( stringsBundle.getString( "MainCommGUI.decoGradientsLowLabel.text" ) );
      decoGradientenLowSpinner.setToolTipText( stringsBundle.getString( "MainCommGUI.decoGradientenLowSpinner.tooltiptext" ) );
      decoGradientenHighSpinner.setToolTipText( stringsBundle.getString( "MainCommGUI.decoGradientenHighSpinner.tooltiptext" ) );
      decoLaststopLabel.setText( stringsBundle.getString( "MainCommGUI.decoLaststopLabel.text" ) );
      decoLastStopComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.decoLastStopComboBox.3m.text" ), stringsBundle.getString( "MainCommGUI.decoLastStopComboBox.6m.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      decoLastStopComboBox.setModel( portBoxModel );
      decoLastStopComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.decoLastStopComboBox.tooltipttext" ) );
      decoGradientenPresetComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.decoDyngradientsLabel.vconservative.text" ), stringsBundle.getString( "MainCommGUI.decoDyngradientsLabel.conservative.text" ),
          stringsBundle.getString( "MainCommGUI.decoDyngradientsLabel.moderate.text" ), stringsBundle.getString( "MainCommGUI.decoDyngradientsLabel.aggressive.text" ),
          stringsBundle.getString( "MainCommGUI.decoDyngradientsLabel.vaggressive.text" ), stringsBundle.getString( "MainCommGUI.decoDyngradientsLabel.custom.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      decoGradientenPresetComboBox.setModel( portBoxModel );
      decoDyngradientsLabel.setText( stringsBundle.getString( "MainCommGUI.decoDyngradientsLabel.text" ) );
      decoDynGradientsCheckBox.setToolTipText( stringsBundle.getString( "MainCommGUI.decoDynGradientsCheckBox.tooltiptext" ) );
      decoDeepstopsLabel.setText( stringsBundle.getString( "MainCommGUI.decoDeepstopsLabel.text" ) );
      decoDeepStopCheckBox.setText( stringsBundle.getString( "MainCommGUI.decoDeepStopCheckBox.text" ) );
      decoDynGradientsCheckBox.setText( stringsBundle.getString( "MainCommGUI.decoDynGradientsCheckBox.text" ) );
      decoDeepStopCheckBox.setToolTipText( stringsBundle.getString( "MainCommGUI.decoDeepStopCheckBox.tooltiptext" ) );
      // SETPOINT
      ( ( TitledBorder )( setpointPanel.getBorder() ) ).setTitle( stringsBundle.getString( "MainCommGUI.setpointPanel.text" ) );
      lblSetpointAutosetpoint.setText( stringsBundle.getString( "MainCommGUI.lblSetpointAutosetpoint.text" ) );
      autoSetpointComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.autoSetpointComboBox.off.text" ), stringsBundle.getString( "MainCommGUI.autoSetpointComboBox.5m.text" ),
          stringsBundle.getString( "MainCommGUI.autoSetpointComboBox.10m.text" ), stringsBundle.getString( "MainCommGUI.autoSetpointComboBox.15m.text" ),
          stringsBundle.getString( "MainCommGUI.autoSetpointComboBox.20m.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      autoSetpointComboBox.setModel( portBoxModel );
      autoSetpointComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.autoSetpointComboBox.tooltiptext" ) );
      lblSetpointHighsetpoint.setText( stringsBundle.getString( "MainCommGUI.lblSetpointHighsetpoint.text" ) );
      highSetpointComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.highSetpointComboBox.10.text" ), stringsBundle.getString( "MainCommGUI.highSetpointComboBox.11.text" ),
          stringsBundle.getString( "MainCommGUI.highSetpointComboBox.12.text" ), stringsBundle.getString( "MainCommGUI.highSetpointComboBox.13.text" ),
          stringsBundle.getString( "MainCommGUI.highSetpointComboBox.14.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      highSetpointComboBox.setModel( portBoxModel );
      highSetpointComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.highSetpointComboBox.tooltiptext" ) );
      // DISPLAY
      ( ( TitledBorder )( displayPanel.getBorder() ) ).setTitle( stringsBundle.getString( "MainCommGUI.displayPanel.text" ) );
      lblDisplayBrightness.setText( stringsBundle.getString( "MainCommGUI.lblDisplayBrightness.text" ) );
      displayBrightnessComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.displayBrightnessComboBox.10.text" ), stringsBundle.getString( "MainCommGUI.displayBrightnessComboBox.50.text" ),
          stringsBundle.getString( "MainCommGUI.displayBrightnessComboBox.100.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      displayBrightnessComboBox.setModel( portBoxModel );
      displayBrightnessComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.displayBrightnessComboBox.tooltiptext" ) );
      lblDisplayOrientation.setText( stringsBundle.getString( "MainCommGUI.lblDisplayOrientation.text" ) );
      displayOrientationComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.displayOrientationComboBox.landscape.text" ), stringsBundle.getString( "MainCommGUI.displayOrientationComboBox.landscape180.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      displayOrientationComboBox.setModel( portBoxModel );
      displayOrientationComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.displayOrientationComboBox.tooltiptext" ) );
      // UNITS
      ( ( TitledBorder )( unitsPanel.getBorder() ) ).setTitle( stringsBundle.getString( "MainCommGUI.unitsPanel.text" ) );
      lblUnitsTemperature.setText( stringsBundle.getString( "MainCommGUI.lblUnitsTemperature.text" ) );
      unitsTemperatureComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.unitsTemperatureComboBox.fahrenheit.text" ), stringsBundle.getString( "MainCommGUI.unitsTemperatureComboBox.celsius.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      unitsTemperatureComboBox.setModel( portBoxModel );
      unitsTemperatureComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.unitsTemperatureComboBox.tooltiptext" ) );
      lblUnitsDepth.setText( stringsBundle.getString( "MainCommGUI.lblUnitsDepth.text" ) );
      unitsDepthComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.unitsDepthComboBox.metrical.text" ), stringsBundle.getString( "MainCommGUI.unitsDepthComboBox.imperial.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      unitsDepthComboBox.setModel( portBoxModel );
      unitsDepthComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.unitsDepthComboBox.tooltiptext" ) );
      lblUnitsSalinity.setText( stringsBundle.getString( "MainCommGUI.lblUnitsSalinity.text" ) );
      unitsSalnityComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.unitsSalnityComboBox.saltwater.text" ), stringsBundle.getString( "MainCommGUI.unitsSalnityComboBox.clearwater.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      unitsSalnityComboBox.setModel( portBoxModel );
      unitsSalnityComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.unitsSalnityComboBox.tooltiptext" ) );
      // INDIVIDUALS
      ( ( TitledBorder )( individualPanel.getBorder() ) ).setTitle( stringsBundle.getString( "MainCommGUI.individualPanel.text" ) );
      lblSenormode.setText( stringsBundle.getString( "MainCommGUI.lblSenormode.text" ) );
      individualsSensorsOnCheckbox.setText( stringsBundle.getString( "MainCommGUI.chIndividualsSensorsOnCheckbox.text" ) );
      individualsSensorsOnCheckbox.setToolTipText( "MainCommGUI.chIndividualsSensorsOnCheckbox.tooltiptext" );
      lblIndividualsPscrMode.setText( stringsBundle.getString( "MainCommGUI.lblIndividualsPscrMode.text" ) );
      individualsPscrModeOnCheckbox.setText( stringsBundle.getString( "MainCommGUI.IndividualsPscrModoOnCheckbox.text" ) );
      individualsPscrModeOnCheckbox.setToolTipText( stringsBundle.getString( "MainCommGUI.IndividualsPscrModoOnCheckbox.tooltiptext" ) );
      lblSensorwarnings.setText( stringsBundle.getString( "MainCommGUI.lblSensorwarnings.text" ) );
      individualsSensorWarnComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.individualsSensorwarnComboBox.1.text" ), stringsBundle.getString( "MainCommGUI.individualsSensorwarnComboBox.2.text" ),
          stringsBundle.getString( "MainCommGUI.individualsSensorwarnComboBox.3.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      individualsSensorWarnComboBox.setModel( portBoxModel );
      individualsSensorWarnComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.individualsSensorwarnComboBox.tooltiptext" ) );
      individualsAcusticWarningsLabel.setText( stringsBundle.getString( "MainCommGUI.individualsAcusticWarningsLabel.text" ) );
      individualsWarningsOnCheckBox.setToolTipText( stringsBundle.getString( "MainCommGUI.individualsWarningsOnCheckBox.tooltiptext" ) );
      individualsLogintervalLabel.setText( stringsBundle.getString( "MainCommGUI.individualsLogintervalLabel.text" ) );
      individualsLogintervalComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "MainCommGUI.individualsLogintervalComboBox.10s.text" ), stringsBundle.getString( "MainCommGUI.individualsLogintervalComboBox.20s.text" ),
          stringsBundle.getString( "MainCommGUI.individualsLogintervalComboBox.60s.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      individualsLogintervalComboBox.setModel( portBoxModel );
      individualsLogintervalComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.individualsLogintervalComboBox.tooltiptext" ) );
      individualsNotLicensedLabel.setToolTipText( stringsBundle.getString( "MainCommGUI.individualsNotLicensedLabel.tooltiptext" ) );
      individualsNotLicensedLabel.setText( " " );
      // //////////////////////////////////////////////////////////////////////
      // Tabbes Pane gas
      tabbedPane.setTitleAt( 2, stringsBundle.getString( "MainCommGUI.gasPanel.title" ) );
    }
    catch( NullPointerException ex )
    {
      statusTextField.setText( "ERROR set language strings" );
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( -1 );
    }
    catch( MissingResourceException ex )
    {
      statusTextField.setText( "ERROR set language strings - the given key can be found" );
      System.out.println( "ERROR set language strings - the given key can be found <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    catch( ClassCastException ex )
    {
      statusTextField.setText( "ERROR set language strings" );
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    finally
    {
      ignoreAction = false;
    }
    return( 1 );
  }

  /**
   * 
   * verfügbare Sprachen in Menü eintragen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.12.2011
   * @param programLocale
   */
  private void initLanuageMenu( Locale programLocale )
  {
    ResourceBundle rb;
    Enumeration<String> enu;
    String key = null;
    String cmd = null;
    try
    {
      ignoreAction = true;
      // Lies die Resource aus
      rb = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.res.languages" );
      // Alle KEYS lesen
      enu = rb.getKeys();
      try
      {
        while( enu.hasMoreElements() )
        {
          JMenuItem menuItem = new JMenuItem();
          key = enu.nextElement();
          cmd = rb.getString( key );
          menuItem.setText( key );
          menuItem.addActionListener( this );
          menuItem.setActionCommand( cmd );
          menuItem.addMouseMotionListener( this );
          mnLanguages.add( menuItem );
        }
      }
      catch( NullPointerException ex )
      {
        statusTextField.setText( "ERROR set language strings" );
        System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
        System.exit( -1 );
      }
      catch( MissingResourceException ex )
      {
        statusTextField.setText( "ERROR set language strings" );
        System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
        System.exit( -1 );
      }
      catch( ClassCastException ex )
      {
        statusTextField.setText( "ERROR set language strings" );
        System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
        System.exit( -1 );
      }
      finally
      {
        ignoreAction = false;
      }
    }
    catch( NullPointerException ex )
    {
      statusTextField.setText( "ERROR set language strings" );
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      System.exit( -1 );
    }
    catch( MissingResourceException ex )
    {
      statusTextField.setText( "ERROR set language strings - the given key can be found" );
      System.out.println( "ERROR set language strings - the given key can be found <" + ex.getMessage() + "> ABORT!" );
      System.exit( -1 );
    }
    catch( ClassCastException ex )
    {
      statusTextField.setText( "ERROR set language strings" );
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      System.exit( -1 );
    }
    finally
    {
      ignoreAction = false;
    }
  }

  /**
   * 
   * Statustext in der Statuszeile setzen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 24.12.2011
   * @param msg
   */
  private void setStatus( String msg )
  {
    if( statusTextField != null )
    {
      statusTextField.setText( msg );
    }
  }

  /**
   * 
   * Eventuell geordnetes Aufräumen hier
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.12.2011
   */
  private void exitProgram()
  {
    if( btComm != null )
    {
      if( btComm.isConnected() )
      {
        btComm.disconnectDevice();
        try
        {
          Thread.sleep( 350 );
        }
        catch( InterruptedException ex )
        {}
      }
    }
    System.exit( 0 );
  }

  /**
   * 
   * Systemlogger machen!
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.12.2011
   * @param logFile
   * @param logLevel
   */
  private void makeLogger( File logFile, Level logLevel )
  {
    Properties loggingProperties = new Properties();
    String name = MainCommGUI.class.getPackage().getName();
    name = name.replace( ".gui", "" );
    try
    {
      loggingProperties.put( ".level", "FINEST" );
      // Root-Logger Handler spezifizieren
      loggingProperties.put( ".handlers", "java.util.logging.ConsoleHandler, java.util.logging.FileHandler" );
      // Konfiguration des ConsoleHandlers
      loggingProperties.put( "java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter" );
      loggingProperties.put( "java.util.logging.ConsoleHandler.level", "FINEST" );
      // Konfiguration des FileHandlers
      loggingProperties.put( "java.util.logging.FileHandler.pattern", logFile.getAbsolutePath() );
      loggingProperties.put( "java.util.logging.FileHandler.limit", "100000" );
      loggingProperties.put( "java.util.logging.FileHandler.count", "1" );
      loggingProperties.put( "java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter" );
      loggingProperties.put( "java.util.logging.FileHandler.level", "FINEST" );
      // Properties an LogManager übergeben
      // ////////////////////////////////////////////////////////////////
      PipedOutputStream pos = new PipedOutputStream();
      PipedInputStream pis = new PipedInputStream( pos );
      loggingProperties.store( pos, "" );
      pos.close();
      LogManager.getLogManager().readConfiguration( pis );
      pis.close();
      //
      LOGGER = Logger.getLogger( MainCommGUI.class.getSimpleName() );
      cHandler = new ConsoleHandler();
      cHandler.setFormatter( new DirksConsoleLogFormatter( name ) );
      if( logFile != null )
      {
        fHandler = new FileHandler();
        fHandler.setFormatter( new DirksConsoleLogFormatter( name ) );
        fHandler.setLevel( logLevel );
        LOGGER.addHandler( fHandler );
      }
    }
    catch( SecurityException ex )
    {
      System.out.println( "ERROR create File Logger: <" + ex.getMessage() + ">" );
      System.exit( -1 );
    }
    catch( IOException ex )
    {
      System.out.println( "ERROR create File Logger: <" + ex.getMessage() + ">" );
      System.exit( -1 );
    }
    cHandler.setLevel( logLevel );
    LOGGER.addHandler( cHandler );
    LOGGER.setUseParentHandlers( false );
  }

  /**
   * Wenn ein element meint, was zu melden...
   */
  @Override
  public void actionPerformed( ActionEvent ev )
  {
    if( ignoreAction ) return;
    // /////////////////////////////////////////////////////////////////////////
    // Meine Actions
    if( ev.getID() > ActionEvent.ACTION_FIRST )
    {
      processMessageActions( ev );
      return;
    }
    // /////////////////////////////////////////////////////////////////////////
    // MENÜ
    else if( ev.getSource() instanceof JMenuItem )
    {
      processMenuActions( ev );
      return;
    }
    // /////////////////////////////////////////////////////////////////////////
    // Button
    else if( ev.getSource() instanceof JButton )
    {
      processButtonActions( ev );
      return;
    }
    // /////////////////////////////////////////////////////////////////////////
    // Combobox
    else if( ev.getSource() instanceof JComboBox )
    {
      processComboBoxActions( ev );
      return;
    }
  }

  /**
   * 
   * Bearbeitet Combobox actions
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.01.2012
   * @param ev
   *          Avtion event
   */
  private void processComboBoxActions( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    String entry = null;
    JComboBox srcBox = ( JComboBox )ev.getSource();
    // /////////////////////////////////////////////////////////////////////////
    // Auswahl welches Gerät soll verbunden werden
    if( deviceToConnectComboBox.equals( srcBox ) )
    {
      if( srcBox.getSelectedIndex() == -1 )
      {
        // nix selektiert
        return;
      }
      entry = ( String )srcBox.getItemAt( srcBox.getSelectedIndex() );
      LOGGER.log( Level.FINE, "select port <" + entry + ">..." );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Letzter Decostop aauf 3 oder 6 Meter
    else if( cmd.equals( "deco_last_stop" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "deco last stop <" + entry + ">..." );
      currentConfig.setLastStop( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Preset für Deco-Gradienten ausgewählt
    else if( cmd.equals( "deco_gradient_preset" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "gradient preset <" + entry + ">, Index: <" + srcBox.getSelectedIndex() + ">..." );
      currentConfig.setDecoGfPreset( srcBox.getSelectedIndex() );
      // Spinner setzen
      setSpinnersAfterPreset( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Autosetpoint Voreinstellung
    else if( cmd.equals( "set_autosetpoint" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "autosetpoint preset <" + entry + ">..." );
      currentConfig.setAutoSetpoint( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Setpoint für höchsten PPO2 Wert einstellen
    else if( cmd.equals( "set_highsetpoint" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "hightsetpoint <" + entry + ">..." );
      currentConfig.setMaxSetpoint( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Helligkeit des Displays
    else if( cmd.equals( "set_disp_brightness" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "brightness <" + entry + ">..." );
      currentConfig.setDisplayBrithtness( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Ausrichtung des Displays
    else if( cmd.equals( "set_display_orientation" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "orientation <" + entry + ">..." );
      currentConfig.setDisplayOrientation( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Grad Celsius oder Fahrenheit einstellen
    else if( cmd.equals( "set_temperature_unit" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "temperature unit <" + entry + ">..." );
      currentConfig.setUnitTemperature( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Maßeinheit für Tiefe festlegen
    else if( cmd.equals( "set_depth_unit" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "depth unit <" + entry + ">..." );
      currentConfig.setUnitDepth( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Süßwasser oder Salzwasser
    else if( cmd.equals( "set_salnity" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "salnity <" + entry + ">..." );
      currentConfig.setUnitSalnyty( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Loginterval einstellen
    else if( cmd.equals( "set_loginterval" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "loginterval <" + entry + ">..." );
      currentConfig.setLogInterval( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Anzahl der sensoren für Messung/Warung einstellen
    else if( cmd.equals( "set_sensorwarnings" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      LOGGER.log( Level.FINE, "sensorwarnings <" + entry + ">...Index: <" + srcBox.getSelectedIndex() + ">" );
      currentConfig.setSensorsCount( srcBox.getSelectedIndex() );
    }
    else
    {
      LOGGER.log( Level.WARNING, "unknown combobox command <" + cmd + "> recived." );
    }
  }

  /**
   * 
   * Wurde das Preset verändert, Spinner entsprechend ausfüllen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.04.2012
   * @param selectedIndex
   *          Der Index der Combobox
   */
  private void setSpinnersAfterPreset( int selectedIndex )
  {
    // nach Preset einstellen?
    switch ( selectedIndex )
    {
      case VERY_CONSERVATIVE:
      case CONSERVATIVE:
      case MODERATE:
      case AGGRESSIVE:
      case VERY_AGGRESSIVE:
        // in den oben genannten Fällen die Spinner auf den Preset einstellen
        ignoreAction = true;
        decoGradientenHighSpinner.setValue( currentConfig.getDecoGfHigh() );
        decoGradientenLowSpinner.setValue( currentConfig.getDecoGfLow() );
        ignoreAction = false;
        LOGGER.log( Level.FINE, "spinner korrected for preset." );
        break;
    }
  }

  /**
   * 
   * Bearbeitet Button Actions
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.01.2012
   * @param ev
   *          Avtion event
   */
  private void processButtonActions( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    // /////////////////////////////////////////////////////////////////////////
    // Verbinde mit Device
    if( cmd.equals( "connect" ) )
    {
      if( btComm != null )
      {
        wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.pleaseWaitforCom" ) );
        wDial.setVisible( true );
        connectSPX();
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // Trenne Verbindung
    else if( cmd.equals( "disconnect" ) )
    {
      if( btComm != null )
      {
        disconnectSPX();
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // lese Config aus Device
    else if( cmd.equals( "read_config" ) )
    {
      if( btComm != null )
      {
        if( btComm.isConnected() )
        {
          wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.pleaseWaitforCom" ) );
          wDial.setMax( BTCommunication.CONFIG_READ_KDO_COUNT );
          wDial.setVisible( true );
          btComm.readConfigFromSPX42();
        }
        else
        {
          showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
        }
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // schreibe Config auf SPX42
    else if( cmd.equals( "write_config" ) )
    {
      if( btComm != null )
      {
        if( !currentConfig.wasInit() || savedConfig == null )
        {
          showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notConfig.text" ) );
          return;
        }
        writeConfigToSPX( savedConfig );
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // Test von DEBUG-Seite an SPX senden
    else if( cmd.equals( "send_test_cmd" ) )
    {
      if( btComm != null )
      {
        if( btComm.isConnected() )
        {
          String cmdStr = testCmdTextField.getText();
          if( cmdStr.isEmpty() )
          {
            LOGGER.log( Level.FINER, "not command to send found!" );
          }
          else
          {
            LOGGER.log( Level.FINER, "send Command to SPX42 <" + cmdStr + ">..." );
            btComm.writeSPXMsgToDevice( cmdStr );
          }
        }
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // Geräteliste neu lesen
    else if( cmd.equals( "refresh_bt_devices" ) )
    {
      LOGGER.log( Level.INFO, "call discover btdevices..." );
      btComm.discoverDevices( false );
      setElementsDiscovering( true );
    }
    // /////////////////////////////////////////////////////////////////////////
    // PIN für Gerät setzen
    else if( cmd.equals( "set_pin_for_dev" ) )
    {
      LOGGER.log( Level.INFO, "call set pin for device..." );
      setPinForDevice();
    }
    else
    {
      LOGGER.log( Level.WARNING, "unknown button command <" + cmd + "> recived." );
    }
    return;
  }

  /**
   * 
   * Schreibe die aktuelle Konfiguration in den SPX42
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 11.04.2012
   * @param cnf
   *          TODO: Auslagern der eigentlichen Kommandierung in ein Objekt, Vorgehen abhängig von der Versionsnummer der Firmware
   */
  private void writeConfigToSPX( SPX42Config cnf )
  {
    //
    wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.pleaseWaitforCom" ) );
    wDial.setMax( BTCommunication.CONFIG_WRITE_KDO_COUNT );
    wDial.resetProgress();
    wDial.setVisible( true );
    LOGGER.log( Level.INFO, "write config to SPX42..." );
    btComm.writeConfigToSPX( currentConfig );
  }

  /**
   * 
   * Setze PIN für Gerät in der Auswahl
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.01.2012
   */
  private void setPinForDevice()
  {
    String deviceName = null;
    ImageIcon icon = null;
    String pinString = null;
    // Welche Schnittstelle?
    if( deviceToConnectComboBox.getSelectedIndex() == -1 )
    {
      LOGGER.log( Level.WARNING, "no connection device selected!" );
      showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notDeviceSelected.text" ) );
      return;
    }
    deviceName = ( String )deviceToConnectComboBox.getItemAt( deviceToConnectComboBox.getSelectedIndex() );
    icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Unlock.png" ) );
    pinString = ( String )JOptionPane.showInputDialog( this, stringsBundle.getString( "MainCommGUI.setPinDialog.text" ) + " <" + deviceName + ">",
            stringsBundle.getString( "MainCommGUI.setPinDialog.headline" ), JOptionPane.PLAIN_MESSAGE, icon, null, btComm.getPinForDevice( deviceName ) );
    if( pinString != null )
    {
      btComm.setPinForDevice( deviceName, pinString );
    }
  }

  /**
   * 
   * Bearbeitet Menüaktionen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.01.2012
   * @param ev
   *          Avtion event
   */
  private void processMenuActions( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    // /////////////////////////////////////////////////////////////////////////
    // Menü EXIT Programm
    if( cmd.equals( "exit" ) )
    {
      exitProgram();
    }
    // /////////////////////////////////////////////////////////////////////////
    // Info Box anzeigen
    else if( cmd.equals( "info" ) )
    {
      LOGGER.log( Level.FINE, "Call INFO-Dialog..." );
      showInfoDialog();
    }
    // /////////////////////////////////////////////////////////////////////////
    // Hilfe Box anzeigen
    else if( cmd.equals( "help" ) )
    {
      LOGGER.log( Level.FINE, "Call HELP-Dialog..." );
      showHelpForm();
    }
    // /////////////////////////////////////////////////////////////////////////
    // Sprachenmenü wurde ausgewählt
    else if( cmd.startsWith( "lang_" ) )
    {
      // welche Sprache hättens denn gern?
      LOGGER.log( Level.FINE, "Change Language..." );
      String lang = cmd.replace( "lang_", "" );
      LOGGER.log( Level.INFO, "change language to <" + lang + ">" );
      changeProgramLanguage( lang );
    }
    else
    {
      LOGGER.log( Level.WARNING, "unknown menu command <" + cmd + "> recived." );
    }
    return;
  }

  /**
   * 
   * Bearbeitet meine "Messages"
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.01.2012
   * @param ev
   *          Avtion event
   */
  private void processMessageActions( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    int actionId = ev.getID();
    switch ( actionId )
    {
    // /////////////////////////////////////////////////////////////////////////
    // Hab was gelesen!
      case ProjectConst.MESSAGE_READ:
        LOGGER.log( Level.FINE, "READ Command!" );
        // soll den reader Thread und die GUI nicht blockieren
        // daher nur in die Liste schmeissen (die ist thread-sicher)
        if( ( !cmd.isEmpty() ) && ( !cmd.equals( "\n" ) ) )
        {
          messagesList.add( cmd );
          LOGGER.log( Level.FINE, "RECIVED: <" + cmd + ">" );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Gerätename ausgelesen
      case ProjectConst.MESSAGE_MANUFACTURER_READ:
        LOGGER.log( Level.INFO, "Device Manufacturer Name from SPX42 <" + cmd + "> recived..." );
        currentConfig.setDeviceName( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Firmwareversion gelesen!
      case ProjectConst.MESSAGE_FWVERSION_READ:
        LOGGER.log( Level.INFO, "Firmware Version <" + cmd + "> recived..." );
        currentConfig.setFirmwareVersion( cmd );
        firmwareVersionValueLabel.setText( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Seriennummer vom SPX42
      case ProjectConst.MESSAGE_SERIAL_READ:
        LOGGER.log( Level.INFO, "Serial Number from SPX42 recived..." );
        serialNumberText.setText( cmd );
        currentConfig.setSerial( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Decompressionseinstellungen gelesen
      case ProjectConst.MESSAGE_DECO_READ:
        LOGGER.log( Level.INFO, "DECO propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setDecoGf( cmd ) )
        {
          LOGGER.log( Level.INFO, "DECO propertys set to GUI..." );
          decoGradientenLowSpinner.setValue( currentConfig.getDecoGfLow() );
          decoGradientenHighSpinner.setValue( currentConfig.getDecoGfHigh() );
          decoGradientenPresetComboBox.setSelectedIndex( currentConfig.getDecoGfPreset() );
          if( currentConfig.getLastStop() == 3 )
          {
            decoLastStopComboBox.setSelectedIndex( 0 );
          }
          else
          {
            decoLastStopComboBox.setSelectedIndex( 1 );
          }
          decoDynGradientsCheckBox.setSelected( currentConfig.isDynGradientsEnable() );
          decoDeepStopCheckBox.setSelected( currentConfig.isDeepStopEnable() );
          LOGGER.log( Level.INFO, "DECO propertys set to GUI...OK" );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Einheiten Einstellungen vom SPX42 gelesen
      case ProjectConst.MESSAGE_UNITS_READ:
        LOGGER.log( Level.INFO, "UNITS propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setUnits( cmd ) )
        {
          LOGGER.log( Level.INFO, "UNITS propertys set to GUI..." );
          unitsTemperatureComboBox.setSelectedIndex( currentConfig.getUnitTemperature() );
          unitsDepthComboBox.setSelectedIndex( currentConfig.getUnitDepth() );
          unitsSalnityComboBox.setSelectedIndex( currentConfig.getUnitSalnity() );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Displayeinstellungen gelesen
      case ProjectConst.MESSAGE_DISPLAY_READ:
        LOGGER.log( Level.INFO, "DISPLAY propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setDisplay( cmd ) )
        {
          LOGGER.log( Level.INFO, "DISPLAY propertys set to GUI..." );
          displayBrightnessComboBox.setSelectedIndex( currentConfig.getDisplayBrightness() );
          displayOrientationComboBox.setSelectedIndex( currentConfig.getDisplayOrientation() );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Einstellungen zum O2 Setpint gelesen
      case ProjectConst.MESSAGE_SETPOINT_READ:
        LOGGER.log( Level.INFO, "SETPOINT propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setSetpoint( cmd ) )
        {
          LOGGER.log( Level.INFO, "SETPOINT propertys set to GUI..." );
          autoSetpointComboBox.setSelectedIndex( currentConfig.getAutoSetpoint() );
          highSetpointComboBox.setSelectedIndex( currentConfig.getMaxSetpoint() );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Einstellungen für Individuell gelesen (Extra-Lizenz erforderlich )
      case ProjectConst.MESSAGE_INDIVID_READ:
        LOGGER.log( Level.INFO, "INDIVIDUAL propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setIndividuals( cmd ) )
        {
          LOGGER.log( Level.INFO, "INDIVIDUAL propertys set to GUI..." );
          if( !individualPanel.isEnabled() )
          {
            setIndividualsPanelEnabled( true );
          }
          // Sensormode eintragen
          if( currentConfig.getSensorsOn() == 1 )
          {
            individualsSensorsOnCheckbox.setSelected( true );
          }
          else
          {
            individualsSensorsOnCheckbox.setSelected( false );
          }
          // Passiver MCCR Mode
          if( currentConfig.getPscrModeOn() == 1 )
          {
            individualsPscrModeOnCheckbox.setSelected( true );
          }
          else
          {
            individualsPscrModeOnCheckbox.setSelected( false );
          }
          // Sensor Anzahl Warning
          individualsSensorWarnComboBox.setSelectedIndex( currentConfig.getSensorsCount() );
          // akustische warnuingen
          if( currentConfig.getSoundOn() == 1 )
          {
            individualsWarningsOnCheckBox.setSelected( true );
          }
          else
          {
            individualsWarningsOnCheckBox.setSelected( false );
          }
          // Loginterval
          individualsLogintervalComboBox.setSelectedIndex( currentConfig.getLogInterval() );
        }
        else
        {
          setIndividualsPanelEnabled( false );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Der Lizenzstatus
      case ProjectConst.MESSAGE_LICENSE_STATE_READ:
        currentConfig.setLicenseStatus( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Versuche Verbindung mit Bluetooht Gerät
      case ProjectConst.MESSAGE_CONNECTING:
        LOGGER.log( Level.INFO, "CONNECTING..." );
        setElementsInactive( true );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Device wurde verbunden
      case ProjectConst.MESSAGE_CONNECTED:
        LOGGER.log( Level.INFO, "CONNECT" );
        setElementsConnected( true );
        // Gleich mal Fragen, wer da dran ist!
        btComm.askForDeviceName();
        btComm.askForSerialNumber();
        btComm.askForFirmwareVersion();
        if( wDial != null )
        {
          wDial.dispose();
          wDial = null;
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Device wurde getrennt
      case ProjectConst.MESSAGE_DISCONNECTED:
        LOGGER.log( Level.INFO, "DISCONNECT" );
        if( wDial != null )
        {
          wDial.dispose();
          wDial = null;
        }
        setElementsConnected( false );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // BT Discovering war erfolgreich
      case ProjectConst.MESSAGE_BTRECOVEROK:
        setElementsDiscovering( false );
        refillPortComboBox();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // BT Discovering war fehlerhaft
      case ProjectConst.MESSAGE_BTRECOVERERR:
        setElementsDiscovering( false );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht bitte noch warten
      case ProjectConst.MESSAGE_BTWAITFOR:
        moveStatusBar();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Kein Gerät zum Verbinden gefunden!
      case ProjectConst.MESSAGE_BTNODEVCONN:
        LOGGER.log( Level.SEVERE, "no device found..." );
        showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notDeviceSelected.text" ) );
        setElementsConnected( false );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Gerät benötigt PIN
      case ProjectConst.MESSAGE_BTAUTHREQEST:
        LOGGER.log( Level.INFO, "authentification requested..." );
        setPinForDevice();
        connectSPX();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Lebenszeichen mit Ackuspannugn empfangen
      case ProjectConst.MESSAGE_SPXALIVE:
        LOGGER.log( Level.INFO, "acku value from spx42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        setAckuValue( cmd );
        // wenn noch keine Konfiguration fertig ist,
        // dann sollte das hier zeigen, daß ich alles gelsen habe
        // und eine gesicherte Config erstellt werden kann
        // ALIVE wird bei readSPXConfig als letztes Kommando gesendet.
        if( savedConfig == null )
        {
          currentConfig.setWasInit( true );
        }
        savedConfig = new SPX42Config( currentConfig );
        setAllConfigPanlelsEnabled( true );
        if( currentConfig.isBuggyFirmware() )
        {
          unitsTemperatureComboBox.setBackground( new Color( 0xffafaf ) );
        }
        wDial.dispose();
        wDial = null;
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht, daß da etwas passiert, also Hinweisbox weiterzählen lassen
      case ProjectConst.MESSAGE_PROCESS_NEXT:
        if( wDial != null )
        {
          if( cmd == null )
          {
            wDial.incrementProgress();
          }
          else
          {
            wDial.setProgress( cmd );
          }
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht, daß die hinweisbox geschlossen werden kann
      case ProjectConst.MESSAGE_PROCESS_END:
        if( wDial != null )
        {
          wDial.dispose();
          wDial = null;
        }
        if( cmd != null )
        {
          if( cmd.equals( "config_write" ) )
          {
            savedConfig = new SPX42Config( currentConfig );
          }
        }
        break;
      default:
        LOGGER.log( Level.WARNING, "unknown message recived!" );
        break;
    }
    return;
  }

  /**
   * 
   * Ackuwert des SPX anzeigen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.01.2012
   */
  private void setAckuValue( String vl )
  {
    LOGGER.log( Level.FINE, "Value: <" + vl + ">" );
    double ackuValue = 0.0;
    Pattern fieldPatternDp = Pattern.compile( ":" );
    String[] fields = fieldPatternDp.split( vl );
    if( fields.length > 1 )
    {
      int val = Integer.parseInt( fields[1], 16 );
      ackuValue = ( float )( val / 100.0 );
      ackuLabel.setText( String.format( stringsBundle.getString( "MainCommGUI.ackuLabel.text" ), ackuValue ) );
      LOGGER.log( Level.FINE, String.format( "Acku value: %02.02f", ackuValue ) );
    }
  }

  /**
   * 
   * Die devicebox neu befüllen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 11.01.2012
   */
  private void refillPortComboBox()
  {
    String[] entrys = btComm.getNameArray();
    ComboBoxModel portBoxModel = new DefaultComboBoxModel( entrys );
    deviceToConnectComboBox.setModel( portBoxModel );
  }

  /**
   * 
   * Die Statusbar soll sich bewegen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 10.01.2012
   */
  private void moveStatusBar()
  {
    if( discoverProgressBar.getMaximum() == discoverProgressBar.getValue() )
    {
      discoverProgressBar.setValue( 0 );
      return;
    }
    discoverProgressBar.setValue( discoverProgressBar.getValue() + 1 );
  }

  /**
   * 
   * Oberfläche für/nach Discover bereiten
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 10.01.2012
   * @param isDiscovering
   */
  private void setElementsDiscovering( boolean isDiscovering )
  {
    connectBtRefreshButton.setEnabled( !isDiscovering );
    discoverProgressBar.setVisible( isDiscovering );
    connectButton.setEnabled( !isDiscovering );
    pinButton.setEnabled( !isDiscovering );
    deviceToConnectComboBox.setEnabled( !isDiscovering );
  }

  /**
   * 
   * Zeigt eine Warnung an
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.01.2012
   * @param msg
   *          Warnmessage
   */
  private void showWarnBox( String msg )
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Abort.png" ) );
      // stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.res.messages", programLocale );
      JOptionPane.showMessageDialog( this, msg, stringsBundle.getString( "MainCommGUI.warnDialog.headline" ), JOptionPane.WARNING_MESSAGE, icon );
    }
    catch( NullPointerException ex )
    {
      statusTextField.setText( "ERROR showWarnDialog" );
      LOGGER.log( Level.SEVERE, "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      statusTextField.setText( "ERROR showWarnDialog" );
      LOGGER.log( Level.SEVERE, "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      statusTextField.setText( "ERROR showWarnDialog" );
      LOGGER.log( Level.SEVERE, "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }

  /**
   * 
   * Zeige eine klein Info über das Proggi an
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.01.2012
   */
  private void showInfoDialog()
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Wiki2.png" ) );
      // stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.res.messages", programLocale );
      JOptionPane.showMessageDialog(
              this,
              stringsBundle.getString( "MainCommGUI.infoDlg.line1" ) + "\n" + stringsBundle.getString( "MainCommGUI.infoDlg.line2" ) + "\n"
                      + stringsBundle.getString( "MainCommGUI.infoDlg.line3" ) + "\n" + stringsBundle.getString( "MainCommGUI.infoDlg.line4" ) + "\n"
                      + stringsBundle.getString( "MainCommGUI.infoDlg.line5" ), stringsBundle.getString( "MainCommGUI.infoDlg.headline" ), JOptionPane.INFORMATION_MESSAGE, icon );
    }
    catch( NullPointerException ex )
    {
      statusTextField.setText( "ERROR showInfoDialog" );
      LOGGER.log( Level.SEVERE, "ERROR showInfoDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      statusTextField.setText( "ERROR showInfoDialog" );
      LOGGER.log( Level.SEVERE, "ERROR showInfoDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      statusTextField.setText( "ERROR showInfoDialog" );
      LOGGER.log( Level.SEVERE, "ERROR showInfoDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }

  /**
   * 
   * Zeige ein Hilfe-Fenster
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.01.2012
   */
  private void showHelpForm()
  {
    new HelpFrameClass( programLocale, LOGGER );
  }

  @Override
  public void mouseDragged( MouseEvent ev )
  {}

  /**
   * Wenn sich die Maus über was bewegt...
   */
  @Override
  public void mouseMoved( MouseEvent ev )
  {
    // Ist die Maus da irgendwo hingefahren?
    if( ev.getSource() instanceof JButton )
    {
      setStatus( ( ( JButton )ev.getSource() ).getToolTipText() );
    }
    else if( ev.getSource() instanceof JComboBox )
    {
      setStatus( ( ( JComboBox )ev.getSource() ).getToolTipText() );
    }
    else if( ev.getSource() instanceof JMenuItem )
    {
      setStatus( ( ( JMenuItem )ev.getSource() ).getToolTipText() );
    }
    else if( ev.getSource() instanceof JSpinner )
    {
      setStatus( ( ( JSpinner )ev.getSource() ).getToolTipText() );
    }
    else
    {
      setStatus( "" );
    }
  }

  /**
   * 
   * Beim Verbindungsaufbau inaktiv zeigen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.04.2012
   * @param active
   *          TODO
   */
  private void setElementsInactive( boolean active )
  {
    deviceToConnectComboBox.setEnabled( !active );
    tabbedPane.setEnabledAt( 1, active );
    connectButton.setEnabled( !active );
    pinButton.setEnabled( !active );
    connectBtRefreshButton.setEnabled( active );
  }

  /**
   * 
   * Elemente abhängig vom Connectstatus erlauben/sperren
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.12.2011
   * @param active
   */
  private void setElementsConnected( boolean active )
  {
    deviceToConnectComboBox.setEnabled( !active );
    connectBtRefreshButton.setEnabled( !active );
    tabbedPane.setEnabledAt( 1, active );
    tabbedPane.setEnabledAt( 2, active );
    connectButton.setEnabled( true );
    pinButton.setEnabled( !active );
    if( active )
    {
      connectButton.setText( stringsBundle.getString( "MainCommGUI.connectButton.disconnectText" ) );
      connectButton.setActionCommand( "disconnect" );
      connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112.png" ) ) );
    }
    else
    {
      connectButton.setText( stringsBundle.getString( "MainCommGUI.connectButton.connectText" ) );
      connectButton.setActionCommand( "connect" );
      connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112-mono.png" ) ) );
      ackuLabel.setText( "-" );
      serialNumberText.setText( "-" );
      firmwareVersionValueLabel.setText( "-" );
      if( savedConfig != null )
      {
        savedConfig = null;
      }
      currentConfig.clear();
    }
  }

  /**
   * 
   * Verbine mit SPX42
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 20.12.2011
   */
  private void connectSPX()
  {
    // Welche Schnittstelle?
    if( deviceToConnectComboBox.getSelectedIndex() == -1 )
    {
      LOGGER.log( Level.WARNING, "no connection device selected!" );
      showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notDeviceSelected.text" ) );
      return;
    }
    String deviceName = ( String )deviceToConnectComboBox.getItemAt( deviceToConnectComboBox.getSelectedIndex() );
    LOGGER.log( Level.FINE, "connect via device <" + deviceName + ">..." );
    if( btComm.isConnected() )
    {
      // ist verbunden, was nun?
    }
    else
    {
      try
      {
        btComm.connectDevice( deviceName );
      }
      catch( Exception ex )
      {
        // TODO sinnvoll anzeigen
        LOGGER.log( Level.SEVERE, "Exception: <" + ex.getMessage() + ">" );
      }
    }
  }

  /**
   * 
   * Verbindung trennen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 20.12.2011
   */
  private void disconnectSPX()
  {
    if( btComm.isConnected() )
    {
      LOGGER.log( Level.FINE, "disconnect SPX42..." );
      btComm.disconnectDevice();
    }
  }

  /**
   * 
   * Programmsprache wechseln
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 19.12.2011
   * @param cmd
   *          Sprachenk�rzel
   */
  private void changeProgramLanguage( String cmd )
  {
    String[] langs = null;
    langs = cmd.split( "_" );
    if( langs.length > 1 && langs[1] != null )
    {
      programLocale = new Locale( langs[0], langs[1] );
    }
    else
    {
      programLocale = new Locale( langs[0] );
    }
    Locale.setDefault( programLocale );
    if( currentConfig != null )
    {
      // da verändern sich die Einstellungen, dahre ungültig setzen
      currentConfig.setWasInit( false );
    }
    setLanguageStrings();
  }

  @Override
  public void stateChanged( ChangeEvent ev )
  {
    JSpinner currSpinner = null;
    int currValue;
    if( ignoreAction ) return;
    //
    // war es ein spinner?
    if( ev.getSource() instanceof JSpinner )
    {
      currSpinner = ( JSpinner )ev.getSource();
      if( currSpinner.equals( decoGradientenHighSpinner ) )
      {
        // wert für High ändern
        currValue = ( Integer )currSpinner.getValue();
        LOGGER.log( Level.FINE, String.format( "change decoGradientHighSpinner <%d/%x>...", currValue, currValue ) );
        currentConfig.setDecoGfHigh( currValue );
        setDecoComboAfterSpinnerChange();
      }
      else if( currSpinner.equals( decoGradientenLowSpinner ) )
      {
        // Wert für LOW ändern
        currValue = ( Integer )currSpinner.getValue();
        LOGGER.log( Level.FINE, String.format( "change decoGradientLowSpinner <%d/%x>...", currValue, currValue ) );
        currentConfig.setDecoGfLow( currValue );
        setDecoComboAfterSpinnerChange();
      }
      else
      {
        LOGGER.log( Level.WARNING, "unknown spinner recived!" );
      }
    }
    else
    {
      LOGGER.log( Level.WARNING, "unknown source type recived!" );
    }
  }

  /**
   * 
   * Setze combobox für Deco Gradienten Preset entsprechend der Angaben in den Spinnern
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.04.2012
   * @param decoValue
   */
  private void setDecoComboAfterSpinnerChange()
  {
    int currentPreset = currentConfig.getDecoGfPreset();
    if( decoGradientenPresetComboBox.getSelectedIndex() != currentPreset )
    {
      ignoreAction = true;
      decoGradientenPresetComboBox.setSelectedIndex( currentPreset );
      ignoreAction = false;
    }
  }

  /**
   * Checkbox hat sich verändert
   */
  @Override
  public void itemStateChanged( ItemEvent ev )
  {
    if( ignoreAction ) return;
    if( ev.getSource() instanceof JCheckBox )
    {
      JCheckBox cb = ( JCheckBox )ev.getItemSelectable();
      String cmd = cb.getActionCommand();
      if( cmd.equals( "dyn_gradients_on" ) )
      {
        LOGGER.log( Level.FINE, "dynamic gradients <" + cb.isSelected() + ">" );
        currentConfig.setDynGradientsEnable( cb.isSelected() );
      }
      else if( cmd.equals( "deepstops_on" ) )
      {
        LOGGER.log( Level.FINE, "depstops <" + cb.isSelected() + ">" );
        currentConfig.setDeepStopEnable( cb.isSelected() );
      }
      else if( cmd.equals( "individuals_pscr_on" ) )
      {
        LOGGER.log( Level.FINE, "pscr mode  <" + cb.isSelected() + ">" );
        currentConfig.setPscrModeEnabled( cb.isSelected() );
      }
      else if( cmd.equals( "individual_sensors_on" ) )
      {
        LOGGER.log( Level.FINE, "sensors on  <" + cb.isSelected() + ">" );
        currentConfig.setSensorsEnabled( cb.isSelected() );
      }
      else if( cmd.equals( "individuals_warnings_on" ) )
      {
        LOGGER.log( Level.FINE, "warnings on  <" + cb.isSelected() + ">" );
        currentConfig.setSountEnabled( cb.isSelected() );
      }
      else
      {
        LOGGER.log( Level.WARNING, "unknown item changed: <" + cb.getActionCommand() + "> <" + cb.isSelected() + ">" );
      }
    }
  }

  /**
   * 
   * CLI-Optionen einlesen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.01.2012
   * @param args
   * @return
   */
  private static CommandLine parseCliOptions( String[] args )
  {
    // Optionenobjet anlegen
    Options options = new Options();
    //
    // Optionen für das Parsing anlegen und zu den Optionen zufügen
    //
    // Logleven festlegen
    Option optLogLevel = new Option( "l", "loglevel", true, "set loglevel for program" );
    options.addOption( optLogLevel );
    // Bluethooth Caching Abfrage
    Option optBtCaching = new Option( "c", "cacheonstart", false, "read cached bt devices on start" );
    options.addOption( optBtCaching );
    // Logfile abgefragt?
    Option optLogFile = new Option( "f", "logfile", true, "set logfile, \"OFF\" set NO logfile" );
    options.addOption( optLogFile );
    // Debugging aktivieren
    Option optDebug = new Option( "d", "debug", false, "set debugging for a lot of GUI effects" );
    options.addOption( optDebug );
    // Parser anlegen
    CommandLineParser cliParser = new BasicParser();
    // Argumente parsen!
    try
    {
      return( cliParser.parse( options, args ) );
    }
    catch( ParseException ex )
    {
      System.err.println( "Parser error: " + ex.getLocalizedMessage() );
      return( null );
    }
  }

  /**
   * 
   * Aus dem String von Loglevel den Logging-Wert machen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.01.2012
   * @param optionValue
   * @return
   */
  private static Level parseLogLevel( String level )
  {
    String levelString = level.toUpperCase();
    // SEVERE (highest value) ERROR
    // WARNING WARNUNG
    // INFO INFO
    // CONFIG CONFIG
    // FINE DEBUG
    // FINER
    // FINEST (lowest value)
    if( levelString.equals( "FINEST" ) )
    {
      return( Level.FINE );
    }
    if( levelString.equals( "FINER" ) )
    {
      return( Level.FINE );
    }
    if( levelString.equals( "FINE" ) )
    {
      return( Level.FINE );
    }
    if( levelString.equals( "DEBUG" ) )
    {
      return( Level.FINE );
    }
    if( levelString.equals( "CONFIG" ) )
    {
      return( Level.CONFIG );
    }
    if( levelString.equals( "INFO" ) )
    {
      return( Level.INFO );
    }
    if( levelString.equals( "WARNING" ) )
    {
      return( Level.WARNING );
    }
    if( levelString.equals( "SERVE" ) )
    {
      return( Level.SEVERE );
    }
    return( Level.OFF );
  }

  /**
   * 
   * Setze das neue Logfile, wenn gewünscht
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.01.2012
   * @param optionValue
   * @return File
   */
  private static File parseNewLogFile( String optionValue )
  {
    File tempLogFile = null;
    File parentDir = null;
    if( optionValue.equals( "NONE" ) )
    {
      // abschalten des Logfiles
      return( null );
    }
    try
    {
      tempLogFile = new File( optionValue );
    }
    catch( NullPointerException ex )
    {
      System.err.println( "parseNewLogFile: Logfilename was <null>" );
      return( logFile );
    }
    try
    {
      parentDir = new File( tempLogFile.getParent() );
    }
    catch( NullPointerException ex )
    {
      // nix Parent, ist nur eine Datei....
      return( tempLogFile );
    }
    if( parentDir.exists() && parentDir.isDirectory() )
    {
      return( tempLogFile );
    }
    System.err.println( "parseNewLogFile: Logfile Directory not exists! (" + parentDir.getAbsolutePath() + ")" );
    return( logFile );
  }

  private void setAllConfigPanlelsEnabled( boolean en )
  {
    setDecoPanelEnabled( en );
    setDisplayPanelEnabled( en );
    setUnitsPanelEnabled( en );
    setSetpointPanel( en );
    // nur, wenn eine gültige Konfiguration gelesen wurde
    if( savedConfig != null )
    {
      // Gibt es eine Lizenz für Custom Config?
      if( currentConfig.isCustomEnabled() )
      {
        setIndividualsPanelEnabled( true );
      }
      else
      {
        setIndividualsPanelEnabled( false );
      }
    }
    else
    {
      // Keine Config gelesen!
      setIndividualsPanelEnabled( false );
    }
  }

  /**
   * 
   * Das Individual Panel ausblenden
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.04.2012
   * @param en
   */
  private void setIndividualsPanelEnabled( boolean en )
  {
    for( Component cp : individualPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    if( !en )
    {
      individualsNotLicensedLabel.setEnabled( false );
      individualsNotLicensedLabel.setText( individualsNotLicensedLabel.getToolTipText() );
    }
    else
    {
      individualsNotLicensedLabel.setEnabled( true );
    }
    individualPanel.setEnabled( en );
  }

  /**
   * 
   * Das Panel für Dekompressionseinstellungen ein/ausblenden
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.04.2012
   * @param en
   *          aktiv oder nicht
   */
  private void setDecoPanelEnabled( boolean en )
  {
    for( Component cp : decompressionPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    decompressionPanel.setEnabled( en );
  }

  /**
   * 
   * Das Panel für Displayeinstellungen erlauben/verbieten
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.04.2012
   * @param en
   *          aktiv oder nicht
   */
  private void setDisplayPanelEnabled( boolean en )
  {
    for( Component cp : displayPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    displayPanel.setEnabled( en );
  }

  /**
   * 
   * Das Panel für Einheiten Einstellungen erlauben/verbieten
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.04.2012
   * @param en
   *          erlauben oder nicht
   */
  private void setUnitsPanelEnabled( boolean en )
  {
    for( Component cp : unitsPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    unitsPanel.setEnabled( en );
  }

  /**
   * 
   * Das Panel für Autosetpoint/Setpint erlauben/verbieten
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.04.2012
   * @param en
   *          erlauben oder nicht
   */
  private void setSetpointPanel( boolean en )
  {
    for( Component cp : setpointPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    setpointPanel.setEnabled( en );
  }
}
