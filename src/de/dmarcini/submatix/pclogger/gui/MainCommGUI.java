package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.dmarcini.submatix.pclogger.comm.BTCommunication;
import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.ConfigReadWriteException;
import de.dmarcini.submatix.pclogger.utils.ConnectDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.DirksConsoleLogFormatter;
import de.dmarcini.submatix.pclogger.utils.ReadConfig;
import de.dmarcini.submatix.pclogger.utils.SPX42Config;
import de.dmarcini.submatix.pclogger.utils.SPX42GasList;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;
import de.dmarcini.submatix.pclogger.utils.WriteConfig;

//@formatter:off
/**
 * @author dmarc
 *
 */
public class MainCommGUI extends JFrame implements ActionListener, MouseMotionListener, ChangeListener, ItemListener
{  //
  private static final long                  serialVersionUID    = 3L;
  private final static int                   VERY_CONSERVATIVE   = 0;
  private final static int                   CONSERVATIVE        = 1;
  private final static int                   MODERATE            = 2;
  private final static int                   AGGRESSIVE          = 3;
  private final static int                   VERY_AGGRESSIVE     = 4;
  private final static int                   TAB_CONNECT         = 0; 
  private final static int                   TAB_CONFIG          = 1; 
  private final static int                   TAB_GASLIST         = 2; 
  private final static int                   TAB_LOGREAD         = 3; 
  private final static int                   TAB_LOGGRAPH        = 4;
  // private final static int CUSTOMIZED = 5;
  private static ResourceBundle              stringsBundle       = null;
  private Locale                             programLocale       = null;
  private String                             timeFormatterString = "yyyy-MM-dd - hh:mm:ss";
  @SuppressWarnings( "unused" )
  private final File                         programDir          = new File( System.getProperty("user.dir") );
  static Logger                              LOGGER              = null;
  static Handler                             fHandler            = null;
  static Handler                             cHandler            = null;
  private BTCommunication                    btComm              = null;
  private final ArrayList<String>            messagesList        = new ArrayList<String>();
  private final SPX42Config                  currentConfig       = new SPX42Config();
  private SPX42Config                        savedConfig         = null;
  private SPX42GasList                       currGasList         = null;
  private SpxPcloggerProgramConfig           progConfig          = null;
  private PleaseWaitDialog                   wDial               = null;
  private boolean                            ignoreAction        = false;
  private static Level                       optionLogLevel      = Level.FINE;
  private static boolean                     readBtCacheOnStart  = false;
  private static File                        logFile             = null;
  private static File                        databaseDir         = null;
  private static boolean                     DEBUG               = false;
  private static Color                       gasNameNormalColor  = new Color( 0x000088 );
  private static Color                       gasDangerousColor   = Color.red;
  private static Color                       gasNoNormOxicColor  = Color.MAGENTA;
  private static final Pattern               fieldPatternDp      = Pattern.compile( ":" );
  private static final Pattern               fieldPatternUnderln = Pattern.compile( "[_.]" );
  private int                                licenseState        = -1;
  private int                                customConfig        = -1;
  private ConnectDatabaseUtil                sqliteDbUtil        = null;
  private int waitForMessage = 0;
  //
  // @formatter:on
  private JFrame                   frmMainwindowtitle;
  private JTabbedPane              tabbedPane;
  private spx42ConnectPanel        connectionPanel;
  private spx42ConfigPanel         configPanel;
  private spx42GaslistEditPanel    gasConfigPanel;
  private spx42LoglistPanel        logListPanel;
  private spx42LogGraphPanel       logGraphPanel;
  private JMenuItem                mntmExit;
  private JMenu                    mnLanguages;
  private JMenu                    mnFile;
  private JMenu                    mnOptions;
  private JMenu                    mnHelp;
  private JMenuItem                mntmHelp;
  private JMenuItem                mntmInfo;
  private JTextField               statusTextField;
  private JMenuItem                mntmOptions;

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
    if( cmd.hasOption( "databasedir" ) )
    {
      databaseDir = parseNewDatabaseDir( cmd.getOptionValue( "databasedir" ) );
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
          System.err.println( "Exception: " + e.getLocalizedMessage() + "\n" );
          e.printStackTrace();
        }
      }
    } );
  }

  /**
   * Create the application.
   * 
   * @throws ConfigReadWriteException
   * @throws IOException
   */
  public MainCommGUI() throws IOException, ConfigReadWriteException
  {
    setDefaultLookAndFeelDecorated( isDefaultLookAndFeelDecorated() );
    // Konfiguration aus der Datei einlesen
    ReadConfig rcf = new ReadConfig();
    progConfig = rcf.getConfigClass();
    if( logFile != null )
    {
      // wenn auf der Kommandozeile was anderes vorgegeben ist...
      progConfig.setLogFile( logFile );
    }
    makeLogger( progConfig.getLogFile(), optionLogLevel );
    if( databaseDir != null )
    {
      // wenn auf der Kommandozeile ein neues Verzeichnis angegeben wurde
      progConfig.setDatabaseDir( databaseDir );
    }
    // logdataDir = new File( programDir.getAbsolutePath() + File.separator + ProjectConst.DEFAULTDATADIR );
    if( !progConfig.getDatabaseDir().isDirectory() )
    {
      if( false == progConfig.getDatabaseDir().mkdirs() )
      {
        LOGGER.log( Level.SEVERE, "can't create data directory <" + progConfig.getDatabaseDir().getAbsolutePath() + ">" );
        System.exit( -1 );
      }
      LOGGER.log( Level.FINE, "created data directory <" + progConfig.getDatabaseDir().getAbsolutePath() + ">" );
    }
    try
    {
      programLocale = Locale.getDefault();
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.messages", programLocale );
    }
    catch( MissingResourceException ex )
    {
      System.out.println( "ERROR get resources <" + ex.getMessage() + "> try standart Strings..." );
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.messages_en" );
    }
    prepareDatabase();
    currentConfig.setLogger( LOGGER );
    btComm = new BTCommunication( LOGGER, sqliteDbUtil );
    btComm.addActionListener( this );
    initializeGUI();
    // Listener setzen (braucht auch die Maps)
    setGlobalChangeListener();
    String[] entrys = btComm.getNameArray( false );
    ComboBoxModel portBoxModel = new DefaultComboBoxModel( entrys );
    connectionPanel.deviceToConnectComboBox.setModel( portBoxModel );
    initLanuageMenu( programLocale );
    if( !DEBUG )
    {
      setAllConfigPanlelsEnabled( false );
      gasConfigPanel.setAllGasPanelsEnabled( false );
      logListPanel.setAllLogPanelsEnabled( false );
      setElementsConnected( false );
    }
    if( readBtCacheOnStart )
    {
      LOGGER.log( Level.INFO, "call discover btdevices cached..." );
      btComm.discoverDevices( true );
      connectionPanel.setElementsDiscovering( true );
    }
    if( setLanguageStrings() < 1 )
    {
      System.exit( -1 );
    }
    waitForMessage = 0;
  }

  /**
   * Datenbankfür das Programm vorbereiten oder erzeugen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 24.04.2012
   */
  private void prepareDatabase()
  {
    // Verbindung zum Datenbanktreiber
    sqliteDbUtil = new ConnectDatabaseUtil( LOGGER, progConfig.getDatabaseDir().getAbsolutePath() + File.separator + ProjectConst.DB_FILENAME );
    if( sqliteDbUtil == null )
    {
      LOGGER.log( Level.SEVERE, "can connect to database drivers!" );
      System.exit( -1 );
    }
    // öffne die Datenbank
    // ging das?
    if( sqliteDbUtil.createConnection() == null )
    {
      System.exit( -1 );
    }
    // hier ist alles gut...
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initializeGUI()
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
    // Connection Panel
    connectionPanel = new spx42ConnectPanel( LOGGER, sqliteDbUtil, btComm );
    tabbedPane.addTab( "CONNECTION", null, connectionPanel, null );
    tabbedPane.setEnabledAt( TAB_CONNECT, true );
    // config Panel
    configPanel = new spx42ConfigPanel( LOGGER );
    tabbedPane.addTab( "CONFIG", null, configPanel, null );
    tabbedPane.setEnabledAt( TAB_CONFIG, true );
    // GASPANEL
    gasConfigPanel = new spx42GaslistEditPanel( LOGGER );
    tabbedPane.addTab( "GAS", null, gasConfigPanel, null );
    tabbedPane.setEnabledAt( TAB_GASLIST, true );
    // Loglisten Panel
    logListPanel = new spx42LoglistPanel( LOGGER, this, progConfig.getDatabaseDir().getAbsolutePath() );
    tabbedPane.addTab( "LOG", null, logListPanel, null );
    tabbedPane.setEnabledAt( TAB_LOGREAD, true );
    // Grafik Panel
    logGraphPanel = new spx42LogGraphPanel( LOGGER, sqliteDbUtil );
    tabbedPane.addTab( "GRAPH", null, logGraphPanel, null );
    tabbedPane.setEnabledAt( TAB_LOGGRAPH, true );
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
    mntmOptions = new JMenuItem( "PROPERTYS" );
    mntmOptions.addMouseMotionListener( this );
    mntmOptions.addActionListener( this );
    mntmOptions.setActionCommand( "set_propertys" );
    mnOptions.add( mntmOptions );
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
    connectionPanel.discoverProgressBar.setVisible( false );
  }

  /**
   * Setze alle Strings im Form Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.12.2011
   */
  private int setLanguageStrings()
  {
    // so, ignoriere mal alles....
    ignoreAction = true;
    try
    {
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.messages", programLocale );
    }
    catch( MissingResourceException ex )
    {
      System.out.println( "ERROR get resources <" + ex.getMessage() + "> ABORT!" );
      return( -1 );
    }
    try
    {
      setStatus( "" );
      timeFormatterString = stringsBundle.getString( "MainCommGUI.timeFormatterString" );
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
      mntmOptions.setText( stringsBundle.getString( "MainCommGUI.mntmOptions.text" ) );
      mntmOptions.setToolTipText( stringsBundle.getString( "MainCommGUI.mntmOptions.tooltiptext" ) );
      mnHelp.setText( stringsBundle.getString( "MainCommGUI.mnHelp.text" ) );
      mnHelp.setToolTipText( stringsBundle.getString( "MainCommGUI.mnHelp.tooltiptext" ) );
      mntmHelp.setText( stringsBundle.getString( "MainCommGUI.mntmHelp.text" ) );
      mntmHelp.setToolTipText( stringsBundle.getString( "MainCommGUI.mntmHelp.tooltiptext" ) );
      mntmInfo.setText( stringsBundle.getString( "MainCommGUI.mntmInfo.text" ) );
      mntmInfo.setToolTipText( stringsBundle.getString( "MainCommGUI.mntmInfo.tooltiptext" ) );
      // //////////////////////////////////////////////////////////////////////
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Panes
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane connect
      tabbedPane.setTitleAt( TAB_CONNECT, stringsBundle.getString( "spx42ConnectPanel.title" ) );
      connectionPanel.setLanguageStrings( stringsBundle, btComm.isConnected() );
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane config
      tabbedPane.setTitleAt( TAB_CONFIG, stringsBundle.getString( "spx42ConfigPanel.title" ) );
      configPanel.setLanguageStrings( stringsBundle );
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane gas
      tabbedPane.setTitleAt( TAB_GASLIST, stringsBundle.getString( "spx42GaslistEditPanel.title" ) );
      gasConfigPanel.setLanguageStrings( stringsBundle );
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane log
      tabbedPane.setTitleAt( TAB_LOGREAD, stringsBundle.getString( "spx42LoglistPanel.title" ) );
      logListPanel.setLanguageStrings( stringsBundle );
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane graph
      tabbedPane.setTitleAt( TAB_LOGGRAPH, stringsBundle.getString( "spx42LogGraphPanel.title" ) );
      logGraphPanel.setLanguageStrings( stringsBundle );
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
   * verfügbare Sprachen in Menü eintragen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.12.2011
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
      rb = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.languages" );
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
   * Statustext in der Statuszeile setzen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 24.12.2011
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
   * Eventuell geordnetes Aufräumen hier Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.12.2011
   */
  private void exitProgram()
  {
    if( sqliteDbUtil != null )
    {
      if( sqliteDbUtil.isOpenDB() )
      {
        sqliteDbUtil.closeDB();
      }
      sqliteDbUtil = null;
    }
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
    // testen, ob da noch was zurückgeschrieben werden muss
    if( progConfig != null )
    {
      if( progConfig.isWasChanged() )
      {
        try
        {
          LOGGER.log( Level.INFO, "write config to file..." );
          new WriteConfig( progConfig );
        }
        catch( IOException ex )
        {
          ex.printStackTrace();
        }
        catch( ConfigReadWriteException ex )
        {
          ex.printStackTrace();
        }
      }
    }
    System.exit( 0 );
  }

  /**
   * Systemlogger machen! Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.12.2011
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
   * Bearbeitet Combobox actions Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
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
    if( connectionPanel.deviceToConnectComboBox.equals( srcBox ) )
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
    // Letzter Decostop auf 3 oder 6 Meter
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
      setGradientSpinnersAfterPreset( srcBox.getSelectedIndex() );
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
      if( currentConfig.isBuggyFirmware() )
      {
        if( srcBox.getSelectedIndex() == 0 )
        {
          configPanel.unitsTemperatureComboBox.setSelectedIndex( 1 );
        }
        else
        {
          configPanel.unitsTemperatureComboBox.setSelectedIndex( 0 );
        }
      }
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
   * Wurde das Preset verändert, Spinner entsprechend ausfüllen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 13.04.2012
   * @param selectedIndex
   *          Der Index der Combobox
   */
  private void setGradientSpinnersAfterPreset( int selectedIndex )
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
        configPanel.decoGradientenHighSpinner.setValue( currentConfig.getDecoGfHigh() );
        configPanel.decoGradientenLowSpinner.setValue( currentConfig.getDecoGfLow() );
        ignoreAction = false;
        LOGGER.log( Level.FINE, "spinner korrected for preset." );
        break;
    }
  }

  /**
   * Bearbeitet Button Actions Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
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
        wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.pleaseWaitForConnect" ) );
        wDial.setVisible( true );
        waitForMessage = 0; // auf erst mal nix warten...
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
          wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.readSpxConfig" ) );
          wDial.setMax( BTCommunication.CONFIG_READ_KDO_COUNT );
          wDial.setVisible( true );
          btComm.readConfigFromSPX42();
          // warte auf diese Nachricht....
          waitForMessage = ProjectConst.MESSAGE_SPXALIVE;
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
        if( !currentConfig.isInitialized() || savedConfig == null )
        {
          showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notConfig.text" ) );
          return;
        }
        writeConfigToSPX( savedConfig );
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // Geräteliste neu lesen
    else if( cmd.equals( "refresh_bt_devices" ) )
    {
      LOGGER.log( Level.INFO, "call discover btdevices..." );
      btComm.discoverDevices( false );
      connectionPanel.setElementsDiscovering( true );
    }
    // /////////////////////////////////////////////////////////////////////////
    // PIN für Gerät setzen
    else if( cmd.equals( "set_pin_for_dev" ) )
    {
      LOGGER.log( Level.INFO, "call set pin for device..." );
      setPinForDevice();
    }
    // /////////////////////////////////////////////////////////////////////////
    // Alias editor zeigen
    else if( cmd.equals( "alias_bt_devices_on" ) )
    {
      LOGGER.log( Level.INFO, "alias editor show..." );
      connectionPanel.setAliasesEditable( true );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Alias editor zeigen
    else if( cmd.equals( "alias_bt_devices_off" ) )
    {
      LOGGER.log( Level.INFO, "alias editor hide..." );
      connectionPanel.setAliasesEditable( false );
    }
    // /////////////////////////////////////////////////////////////////////////
    // ich will die Gasliste haben!
    else if( cmd.equals( "read_gaslist" ) )
    {
      LOGGER.log( Level.INFO, "call read gaslist from device..." );
      if( btComm != null )
      {
        if( btComm.isConnected() )
        {
          wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.readGaslist" ) );
          wDial.setMax( BTCommunication.CONFIG_READ_KDO_COUNT );
          wDial.setVisible( true );
          btComm.readGaslistFromSPX42();
        }
        else
        {
          showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
        }
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // ich will die Gasliste schreiben!
    else if( cmd.equals( "write_gaslist" ) )
    {
      LOGGER.log( Level.INFO, "call write gaslist to device..." );
      if( btComm != null )
      {
        if( btComm.isConnected() && currGasList.isInitialized() )
        {
          wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.writeGasList" ) );
          wDial.setMax( BTCommunication.CONFIG_READ_KDO_COUNT );
          wDial.setVisible( true );
          btComm.writeGaslistToSPX42( currGasList, currentConfig.getFirmwareVersion() );
        }
        else
        {
          if( !btComm.isConnected() )
          {
            showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
          }
          else
          {
            showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.gasNotLoadet.text" ) );
          }
        }
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // lese Logdir aus Device
    else if( cmd.equals( "read_logdir_from_spx" ) )
    {
      if( btComm != null )
      {
        if( btComm.isConnected() )
        {
          if( logListPanel.prepareReadLogdir( btComm.getConnectedDevice() ) )
          {
            //
            // an dieser Stelle muss ich sicherstellen, daß ich die Masseinheiten des SPX42 kenne
            // ich gehe mal von den Längeneinheiten aus!
            // also Meter/Fuss
            if( !currentConfig.isInitialized() )
            {
              // jetzt wird es schwierig. erfragfe erst mal die Config!
              wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.readLogDir" ) );
              wDial.setMax( BTCommunication.CONFIG_READ_KDO_COUNT );
              wDial.setVisible( true );
              btComm.readConfigFromSPX42();
            }
            else
            {
              wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.readLogDir" ) );
              wDial.setVisible( true );
            }
            // beginne mit leerem Cache
            logListPanel.clearLogdirCache();
            logListPanel.cleanDetails();
            // Sag dem SPX er soll alles schicken
            btComm.readLogDirectoryFromSPX();
          }
        }
        else
        {
          showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
        }
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // lese Logfile aus Device
    else if( cmd.equals( "read_logfile_from_spx" ) )
    {
      if( btComm != null )
      {
        if( btComm.isConnected() )
        {
          int logListLen = logListPanel.prepareDownloadLogdata( btComm.getConnectedDevice() );
          if( logListLen == 0 )
          {
            showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notLogentrySelected.text" ) );
            return;
          }
          Integer[] logListEntry = logListPanel.getNextEntryToRead();
          if( logListEntry != null )
          {
            computeLogRequest( logListEntry );
          }
        }
        else
        {
          showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
        }
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // Da hab ich nix passendes gefunden!
    else
    {
      LOGGER.log( Level.WARNING, "unknown button command <" + cmd + "> recived." );
    }
    return;
  }

  /**
   * Schreibe die aktuelle Konfiguration in den SPX42 Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.04.2012
   * @param cnf
   *          Config objekt
   */
  private void writeConfigToSPX( SPX42Config cnf )
  {
    //
    wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.writeSpxConfig" ) );
    wDial.setMax( BTCommunication.CONFIG_WRITE_KDO_COUNT );
    wDial.resetProgress();
    wDial.setVisible( true );
    LOGGER.log( Level.INFO, "write config to SPX42..." );
    btComm.writeConfigToSPX( currentConfig );
  }

  /**
   * Setze PIN für Gerät in der Auswahl Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.01.2012
   */
  private void setPinForDevice()
  {
    String deviceName = null;
    ImageIcon icon = null;
    String pinString = null;
    // Welche Schnittstelle?
    if( connectionPanel.deviceToConnectComboBox.getSelectedIndex() == -1 )
    {
      LOGGER.log( Level.WARNING, "no connection device selected!" );
      showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notDeviceSelected.text" ) );
      return;
    }
    deviceName = ( String )connectionPanel.deviceToConnectComboBox.getItemAt( connectionPanel.deviceToConnectComboBox.getSelectedIndex() );
    icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Unlock.png" ) );
    pinString = ( String )JOptionPane.showInputDialog( this, stringsBundle.getString( "MainCommGUI.setPinDialog.text" ) + " <" + deviceName + ">",
            stringsBundle.getString( "MainCommGUI.setPinDialog.headline" ), JOptionPane.PLAIN_MESSAGE, icon, null, btComm.getPinForDevice( deviceName ) );
    if( pinString != null )
    {
      btComm.setPinForDevice( deviceName, pinString );
    }
  }

  /**
   * Bearbeitet Menüaktionen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
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
    // Box für diverse Einstellungen anzeigen
    else if( cmd.equals( "set_propertys" ) )
    {
      showPropertysDialog();
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
   * Bearbeitet meine "Messages" Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param ev
   *          Avtion event
   */
  private void processMessageActions( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    int actionId = ev.getID();
    //
    // wenn ich auf eine Nachricht warten soll, um das "WARTE"-Fenster zu schliessen
    //
    if( waitForMessage != 0 )
    {
      if( waitForMessage == actionId )
      {
        // es ist die Nachricht, auf die ich waren soll
        if( wDial != null )
        {
          wDial.dispose();
          wDial = null;
        }
        // den Merker auf null setzen!
        waitForMessage = 0;
      }
    }
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
        configPanel.firmwareVersionValueLabel.setText( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Seriennummer vom SPX42
      case ProjectConst.MESSAGE_SERIAL_READ:
        LOGGER.log( Level.INFO, "Serial Number from SPX42 recived..." );
        configPanel.serialNumberText.setText( cmd );
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
          configPanel.decoGradientenLowSpinner.setValue( currentConfig.getDecoGfLow() );
          configPanel.decoGradientenHighSpinner.setValue( currentConfig.getDecoGfHigh() );
          configPanel.decoGradientenPresetComboBox.setSelectedIndex( currentConfig.getDecoGfPreset() );
          if( currentConfig.getLastStop() == 3 )
          {
            configPanel.decoLastStopComboBox.setSelectedIndex( 0 );
          }
          else
          {
            configPanel.decoLastStopComboBox.setSelectedIndex( 1 );
          }
          configPanel.decoDynGradientsCheckBox.setSelected( currentConfig.isDynGradientsEnable() );
          configPanel.decoDeepStopCheckBox.setSelected( currentConfig.isDeepStopEnable() );
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
          configPanel.unitsTemperatureComboBox.setSelectedIndex( currentConfig.getUnitTemperature() );
          configPanel.unitsDepthComboBox.setSelectedIndex( currentConfig.getUnitDepth() );
          configPanel.unitsSalnityComboBox.setSelectedIndex( currentConfig.getUnitSalnity() );
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
          configPanel.displayBrightnessComboBox.setSelectedIndex( currentConfig.getDisplayBrightness() );
          configPanel.displayOrientationComboBox.setSelectedIndex( currentConfig.getDisplayOrientation() );
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
          configPanel.autoSetpointComboBox.setSelectedIndex( currentConfig.getAutoSetpoint() );
          configPanel.highSetpointComboBox.setSelectedIndex( currentConfig.getMaxSetpoint() );
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
          if( !configPanel.individualPanel.isEnabled() )
          {
            configPanel.setIndividualsPanelEnabled( true );
          }
          // Sensormode eintragen
          if( currentConfig.getSensorsOn() == 1 )
          {
            configPanel.individualsSensorsOnCheckbox.setSelected( true );
          }
          else
          {
            configPanel.individualsSensorsOnCheckbox.setSelected( false );
          }
          // Passiver MCCR Mode
          if( currentConfig.getPscrModeOn() == 1 )
          {
            configPanel.individualsPscrModeOnCheckbox.setSelected( true );
          }
          else
          {
            configPanel.individualsPscrModeOnCheckbox.setSelected( false );
          }
          // Sensor Anzahl Warning
          configPanel.individualsSensorWarnComboBox.setSelectedIndex( currentConfig.getSensorsCount() );
          // akustische warnuingen
          if( currentConfig.getSoundOn() == 1 )
          {
            configPanel.individualsWarningsOnCheckBox.setSelected( true );
          }
          else
          {
            configPanel.individualsWarningsOnCheckBox.setSelected( false );
          }
          // Loginterval
          configPanel.individualsLogintervalComboBox.setSelectedIndex( currentConfig.getLogInterval() );
        }
        else
        {
          configPanel.setIndividualsPanelEnabled( false );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Der Lizenzstatus
      case ProjectConst.MESSAGE_LICENSE_STATE_READ:
        LOGGER.log( Level.INFO, "lizense state from SPX42 recived..." );
        currentConfig.setLicenseStatus( cmd );
        licenseState = currentConfig.getLicenseState();
        customConfig = currentConfig.getCustomEnabled();
        gasConfigPanel.setLicenseState( licenseState, customConfig );
        gasConfigPanel.setLicenseLabel( stringsBundle );
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
        btComm.askForLicenseFromSPX();
        btComm.askForFirmwareVersion();
        connectionPanel.refreshAliasTable();
        // ware, bis die Nachricht FWVERSION_READ kommt, um das wartefenster zu schliessen
        waitForMessage = ProjectConst.MESSAGE_FWVERSION_READ;
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
        setAllConfigPanlelsEnabled( false );
        gasConfigPanel.setElementsGasMatrixPanelEnabled( false );
        connectionPanel.refreshAliasTable();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // BT Discovering war erfolgreich
      case ProjectConst.MESSAGE_BTRECOVEROK:
        connectionPanel.setElementsDiscovering( false );
        refillPortComboBox();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // BT Discovering war fehlerhaft
      case ProjectConst.MESSAGE_BTRECOVERERR:
        connectionPanel.setElementsDiscovering( false );
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
        setAckuValue( cmd );
        if( savedConfig == null )
        {
          currentConfig.setWasInit( true );
        }
        savedConfig = new SPX42Config( currentConfig );
        setAllConfigPanlelsEnabled( true );
        if( currentConfig.isBuggyFirmware() )
        {
          configPanel.unitsTemperatureComboBox.setBackground( new Color( 0xffafaf ) );
          configPanel.unitsTemperatureComboBox.setEnabled( false );
        }
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
      // Nachricht, daß die Hinweisbox geschlossen werden kann
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
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht über den Empfang einer Gaseinstellung
      case ProjectConst.MESSAGE_GAS_READ:
        // Gibts schon ein GasListObjekt
        if( currGasList == null )
        {
          // erzeuge eines!
          currGasList = new SPX42GasList( LOGGER );
        }
        // läßt sich das Teil parsen?
        if( currGasList.setGas( cmd ) )
        {
          // ist alle initialisiert?
          if( currGasList.isInitialized() )
          {
            // wenn die Gaskiste initialisiert ist
            gasConfigPanel.setElementsGasMatrixPanelEnabled( true );
            // Mach mal alles in die Spinner rein
            ignoreAction = true;
            for( int i = 0; i < currGasList.getGasCount(); i++ )
            {
              ( gasConfigPanel.heSpinnerMap.get( i ) ).setValue( currGasList.getHEFromGas( i ) );
              ( gasConfigPanel.o2SpinnerMap.get( i ) ).setValue( currGasList.getO2FromGas( i ) );
              ( gasConfigPanel.gasLblMap.get( i ) ).setText( getNameForGas( i ) );
              // ist dieses Gas Diluent 1?
              if( currGasList.getDiulent1() == i )
              {
                ( gasConfigPanel.diluent1Map.get( i ) ).setSelected( true );
              }
              // ist dieses Gas Diluent 2?
              if( currGasList.getDiluent2() == i )
              {
                ( gasConfigPanel.diluent2Map.get( i ) ).setSelected( true );
              }
              // Status als Bailoutgas?
              if( currGasList.getBailout( i ) == 3 )
              {
                ( gasConfigPanel.bailoutMap.get( i ) ).setSelected( true );
              }
              else
              {
                ( gasConfigPanel.bailoutMap.get( i ) ).setSelected( false );
              }
            }
            ignoreAction = false;
            // dann kann das fenster ja wech!
            if( wDial != null )
            {
              wDial.dispose();
              wDial = null;
            }
          }
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht Syncronisation wird beendet
      case ProjectConst.MESSAGE_SYCSTAT_OFF:
        LOGGER.log( Level.WARNING, "SPX42 switched SYNC OFF! Connetion will failure...." );
        // disconnect!
        btComm.disconnectDevice();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht Gase wurden erfolgreich geschrieben
      case ProjectConst.MESSAGE_GAS_WRITTEN:
        LOGGER.log( Level.FINE, "gas written to SPX..." );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht ein Logbuch Verzeichniseintrag wurde gelesen
      case ProjectConst.MESSAGE_DIRENTRY_READ:
        LOGGER.log( Level.FINE, "logdir entry recived..." );
        String decodet = decodeLogDirEntry( cmd );
        if( decodet != null )
        {
          logListPanel.addLogdirEntry( decodet );
        }
        if( !logListPanel.isReadingComplete() )
        {
          // hab noch zu tun...
          if( wDial != null )
          {
            wDial.incrementProgress();
          }
        }
        else
        {
          // dann kann das fenster ja wech!
          if( wDial != null )
          {
            wDial.dispose();
            wDial = null;
          }
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Start einer Lagdatenübermittling
      case ProjectConst.MESSAGE_LOGENTRY_START:
        LOGGER.log( Level.FINE, "start transfer logentry <" + cmd + ">..." );
        logListPanel.startTransfer( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Logzeile übertragen
      case ProjectConst.MESSAGE_LOGENTRY_LINE:
        LOGGER.log( Level.FINE, "recive one log line from SPX..." );
        logListPanel.addLogLineFromSPX( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Logzeile übertragen
      case ProjectConst.MESSAGE_LOGENTRY_STOP:
        LOGGER.log( Level.FINE, "loglist transfer done..." );
        // Ab auf die Platte ind die DB damit!
        logListPanel.writeCacheToDatabase();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Daten gesichert....
      case ProjectConst.MESSAGE_DB_SUCCESS:
        LOGGER.log( Level.FINE, "loglist transfer success..." );
        // dann kann das fenster ja wech!
        if( wDial != null )
        {
          wDial.dispose();
          wDial = null;
        }
        if( btComm != null )
        {
          if( btComm.isConnected() )
          {
            Integer[] logListEntry = logListPanel.getNextEntryToRead();
            if( logListEntry != null )
            {
              // unterscheide Update oder Neu
              computeLogRequest( logListEntry );
            }
            else
            {
              if( btComm != null )
              {
                if( btComm.isConnected() )
                {
                  if( logListPanel.prepareReadLogdir( btComm.getConnectedDevice() ) )
                  {
                    wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.readLogDir" ) );
                    wDial.setVisible( true );
                    // beginne mit leerem Cache
                    // TODO: Aus Chache neu aufbauen!
                    logListPanel.clearLogdirCache();
                    logListPanel.cleanDetails();
                    // Sag dem SPX er soll alles schicken
                    btComm.readLogDirectoryFromSPX();
                  }
                }
                else
                {
                  showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
                }
              }
            }
          }
          else
          {
            showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
          }
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Datenbankfehler....
      case ProjectConst.MESSAGE_DB_FAIL:
        LOGGER.log( Level.FINE, "loglist transfer failed..." );
        // dann kann das fenster ja wech!
        if( wDial != null )
        {
          wDial.dispose();
          wDial = null;
        }
        if( cmd != null )
        {
          showErrorDialog( stringsBundle.getString( "spx42LoglistPanel.logListLabel.text" ) + "\n" + cmd );
        }
        else
        {
          showErrorDialog( stringsBundle.getString( "spx42LoglistPanel.logListLabel.text" ) );
        }
        logListPanel.removeFailedDataset();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nichts traf zu....
      default:
        LOGGER.log( Level.WARNING, "unknown message recived!" );
        break;
    }
    return;
  }

  /**
   * 
   * Hilfsfunktion zum start speichern/update eines Tauchlogs
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 12.07.2012
   * @param logListEntry
   */
  private int computeLogRequest( Integer[] logListEntry )
  {
    // liegt ein updatewunsch vor?
    if( logListEntry[1] > 0 )
    {
      LOGGER.log( Level.WARNING, "dive logentry alredy there. Ask user for continue..." );
      //@formatter:off
      // Zeige dem geneigten User eine Dialogbox, in welcher er entscheiden muss: Update oder überspringen
      Object[] options =
      { 
        stringsBundle.getString( "MainCommGui.updateWarnDialog.updateButton" ),
        stringsBundle.getString( "MainCommGui.updateWarnDialog.cancelButton" )
      };
      int retOption =  JOptionPane.showOptionDialog( 
              null, 
              stringsBundle.getString( "MainCommGui.updateWarnDialog.messageUpdate" ), 
              stringsBundle.getString( "MainCommGui.updateWarnDialog.headLine" ), 
              JOptionPane.DEFAULT_OPTION, 
              JOptionPane.WARNING_MESSAGE, 
              null, 
              options, 
              options[0] 
              );
      //@formatter:on
      if( retOption == 1 )
      {
        LOGGER.log( Level.INFO, "user has cancel to update divelog entry." );
        return( 0 );
      }
      else
      {
        // update datensatz!
        logListPanel.setNextLogIsAnUpdate( true, logListEntry[0] );
      }
    }
    // datensatz anlegen
    wDial = new PleaseWaitDialog( stringsBundle.getString( "PleaseWaitDialog.title" ), stringsBundle.getString( "PleaseWaitDialog.waitForReadDive" ) );
    wDial.setDetailMessage( String.format( stringsBundle.getString( "PleaseWaitDialog.readDiveNumber" ), logListEntry[0] ) );
    wDial.setVisible( true );
    // Sag dem SPX er soll alles schicken
    LOGGER.log( Level.FINE, "send command to spx: send logfile number <" + logListEntry[0] + ">" );
    btComm.readLogDetailFromSPX( logListEntry[0] );
    return( 1 );
  }

  /**
   * 
   * Decodiere die Nachricht über einen Logverzeichniseintrag
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param entryMsg
   * @return
   */
  private String decodeLogDirEntry( String entryMsg )
  {
    // Message etwa so <~41:21:9_4_10_20_44_55.txt:22>
    String[] fields;
    String fileName;
    int number, max;
    int day, month, year, hour, minute, second;
    //
    // Felder aufteilen
    fields = fieldPatternDp.split( entryMsg );
    if( fields.length < 4 )
    {
      LOGGER.log( Level.SEVERE, "recived message for logdir has lower than 4 fields. It is wrong! Abort!" );
      return( null );
    }
    // Wandel die Nummerierung in Integer um
    try
    {
      number = Integer.parseInt( fields[1], 16 );
      max = Integer.parseInt( fields[3], 16 );
    }
    catch( NumberFormatException ex )
    {
      LOGGER.log( Level.SEVERE, "Fail to convert Hex to int: " + ex.getLocalizedMessage() );
      return( null );
    }
    fileName = fields[2];
    // verwandle die Dateiangabe in eine lesbare Datumsangabe
    // Format des Strings ist ja
    // TAG_MONAT_JAHR_STUNDE_MINUTE_SEKUNDE
    // des Beginns der Aufzeichnung
    fields = fieldPatternUnderln.split( fields[2] );
    try
    {
      day = Integer.parseInt( fields[0] );
      month = Integer.parseInt( fields[1] );
      year = Integer.parseInt( fields[2] ) + 2000;
      hour = Integer.parseInt( fields[3] );
      minute = Integer.parseInt( fields[4] );
      second = Integer.parseInt( fields[5] );
    }
    catch( NumberFormatException ex )
    {
      LOGGER.log( Level.SEVERE, "Fail to convert Hex to int: " + ex.getLocalizedMessage() );
      return( null );
    }
    // So, die Angaben des SPX sind immer im Localtime-Format
    // daher werde ich die auch so interpretieren
    // Die Funktion macht das in der default-Lokalzone, sollte also
    // da sein, wio der SPX auch ist... (schwieriges Thema)
    DateTime tm = new DateTime( year, month, day, hour, minute, second );
    DateTimeFormatter fmt = DateTimeFormat.forPattern( timeFormatterString );
    return( String.format( "%d;%s;%s;%d", number, fileName, tm.toString( fmt ), max ) );
  }

  /**
   * Ackuwert des SPX anzeigen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.01.2012
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
      connectionPanel.ackuLabel.setText( String.format( stringsBundle.getString( "MainCommGUI.ackuLabel.text" ), ackuValue ) );
      LOGGER.log( Level.FINE, String.format( "Acku value: %02.02f", ackuValue ) );
    }
  }

  /**
   * Die devicebox neu befüllen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.01.2012
   */
  private void refillPortComboBox()
  {
    String[] entrys = btComm.getNameArray( false );
    ComboBoxModel portBoxModel = new DefaultComboBoxModel( entrys );
    connectionPanel.deviceToConnectComboBox.setModel( portBoxModel );
  }

  /**
   * Die Statusbar soll sich bewegen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 10.01.2012
   */
  private void moveStatusBar()
  {
    if( connectionPanel.discoverProgressBar.getMaximum() == connectionPanel.discoverProgressBar.getValue() )
    {
      connectionPanel.discoverProgressBar.setValue( 0 );
      return;
    }
    connectionPanel.discoverProgressBar.setValue( connectionPanel.discoverProgressBar.getValue() + 1 );
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
   * Zeige eine Fehlermeldung mit Hinweistext ind Icon
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @param header
   * @param message
   */
  private void showErrorDialog( String message )
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Terminate.png" ) );
      JOptionPane.showMessageDialog( this, message, stringsBundle.getString( "MainCommGUI.errorDialog.headline" ), JOptionPane.INFORMATION_MESSAGE, icon );
    }
    catch( NullPointerException ex )
    {
      statusTextField.setText( "ERROR showErrorBox" );
      LOGGER.log( Level.SEVERE, "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      statusTextField.setText( "ERROR showErrorBox" );
      LOGGER.log( Level.SEVERE, "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      statusTextField.setText( "ERROR showErrorBox" );
      LOGGER.log( Level.SEVERE, "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }

  /**
   * 
   * Zeige eine klein Info über das Proggi an Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
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
   * Zeige ein Hilfe-Fenster Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.01.2012
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
   * Beim Verbindungsaufbau inaktiv zeigen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 13.04.2012
   * @param active
   *          Aktiv oder nicht
   */
  private void setElementsInactive( boolean active )
  {
    connectionPanel.setElementsInactive( active );
    tabbedPane.setEnabledAt( TAB_CONFIG, active );
  }

  /**
   * Elemente abhängig vom Connectstatus erlauben/sperren Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.12.2011
   * @param active
   */
  private void setElementsConnected( boolean active )
  {
    connectionPanel.setElementsConnected( active );
    tabbedPane.setEnabledAt( TAB_CONFIG, active );
    tabbedPane.setEnabledAt( TAB_GASLIST, active );
    tabbedPane.setEnabledAt( TAB_LOGREAD, active );
    if( !active )
    {
      configPanel.serialNumberText.setText( "-" );
      configPanel.firmwareVersionValueLabel.setText( "-" );
      if( savedConfig != null )
      {
        savedConfig = null;
      }
      currentConfig.clear();
    }
  }

  /**
   * Verbine mit SPX42 Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 20.12.2011
   */
  private void connectSPX()
  {
    // Welche Schnittstelle?
    if( connectionPanel.deviceToConnectComboBox.getSelectedIndex() == -1 )
    {
      LOGGER.log( Level.WARNING, "no connection device selected!" );
      showWarnBox( stringsBundle.getString( "MainCommGUI.warnDialog.notDeviceSelected.text" ) );
      return;
    }
    String deviceName = ( String )connectionPanel.deviceToConnectComboBox.getItemAt( connectionPanel.deviceToConnectComboBox.getSelectedIndex() );
    LOGGER.log( Level.FINE, "connect via device <" + deviceName + ">..." );
    if( btComm.isConnected() )
    {
      // ist verbunden, was nun?
      return;
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
   * Verbindung trennen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 20.12.2011
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
   * Programmsprache wechseln Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 19.12.2011
   * @param cmd
   *          Sprachenkürzel
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
      // da verändern sich die Einstellungen, daher ungültig setzen
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
    // //////////////////////////////////////////////////////////////////////
    // war es ein spinner?
    if( ev.getSource() instanceof JSpinner )
    {
      currSpinner = ( JSpinner )ev.getSource();
      // //////////////////////////////////////////////////////////////////////
      // Deco gradient Hith
      if( currSpinner.equals( configPanel.decoGradientenHighSpinner ) )
      {
        // wert für High ändern
        currValue = ( Integer )currSpinner.getValue();
        LOGGER.log( Level.FINE, String.format( "change decoGradientHighSpinner <%d/%x>...", currValue, currValue ) );
        currentConfig.setDecoGfHigh( currValue );
        setDecoComboAfterSpinnerChange();
      }
      // //////////////////////////////////////////////////////////////////////
      // Deco gradient Low
      else if( currSpinner.equals( configPanel.decoGradientenLowSpinner ) )
      {
        // Wert für LOW ändern
        currValue = ( Integer )currSpinner.getValue();
        LOGGER.log( Level.FINE, String.format( "change decoGradientLowSpinner <%d/%x>...", currValue, currValue ) );
        currentConfig.setDecoGfLow( currValue );
        setDecoComboAfterSpinnerChange();
      }
      else
      {
        // //////////////////////////////////////////////////////////////////////
        // Nix gefunden, also versuch mal die Listen durch
        for( int gasNr = 0; gasNr < currGasList.getGasCount(); gasNr++ )
        {
          if( currSpinner.equals( gasConfigPanel.o2SpinnerMap.get( gasNr ) ) )
          {
            // O2 Spinner betätigt
            // Gas <gasNr> Sauerstoffanteil ändern
            currValue = ( Integer )currSpinner.getValue();
            changeO2FromGas( gasNr, currValue );
            return;
          }
          else if( currSpinner.equals( gasConfigPanel.heSpinnerMap.get( gasNr ) ) )
          {
            // Heliumspinner betätigt
            // Gas <gasNr> Heliumanteil ändern
            currValue = ( Integer )currSpinner.getValue();
            changeHEFromGas( gasNr, currValue );
            return;
          }
        }
        LOGGER.log( Level.WARNING, "unknown spinner recived!" );
      }
    }
    // //////////////////////////////////////////////////////////////////////
    // war es ein tabbedPane
    else if( ev.getSource() instanceof JTabbedPane )
    {
      if( tabbedPane.equals( ev.getSource() ) )
      {
        int tabIdx = tabbedPane.getSelectedIndex();
        LOGGER.log( Level.FINE, String.format( "tabbedPane changed to %02d!", tabIdx ) );
        if( tabIdx == TAB_LOGGRAPH )
        {
          LOGGER.log( Level.FINE, "graph tab select, init grapic..." );
          String connDev = null;
          if( btComm != null )
          {
            connDev = btComm.getConnectedDevice();
          }
          // Grafiksachen initialisieren
          try
          {
            logGraphPanel.initGraph( connDev, progConfig.getDatabaseDir() );
          }
          catch( Exception ex )
          {
            LOGGER.log( Level.SEVERE, "initGraph Exception: <" + ex.getLocalizedMessage() + ">" );
            showErrorDialog( stringsBundle.getString( "MainCommGui.errorDialog.openGraphWindow" ) );
            return;
          }
        }
        else
        {
          // grafiksachen freigeben
          logGraphPanel.releaseGraph();
        }
      }
    }
    else
    {
      LOGGER.log( Level.WARNING, "unknown source type recived!" );
    }
  }

  /**
   * Ändere Heliumanteil vom Gas Nummer X Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.04.2012
   * @param gasNr
   *          welches Gas denn
   * @param he
   *          Heliumanteil
   */
  private void changeHEFromGas( int gasNr, int he )
  {
    int o2;
    o2 = currGasList.getO2FromGas( gasNr );
    ignoreAction = true;
    if( he < 0 )
    {
      he = 0;
      ( gasConfigPanel.heSpinnerMap.get( gasNr ) ).setValue( 0 );
    }
    else if( he > 100 )
    {
      // Mehr als 100% geht nicht!
      // ungesundes Zeug!
      o2 = 0;
      he = 100;
      ( gasConfigPanel.heSpinnerMap.get( gasNr ) ).setValue( he );
      ( gasConfigPanel.o2SpinnerMap.get( gasNr ) ).setValue( o2 );
      LOGGER.log( Level.WARNING, String.format( "change helium (max) in Gas %d Value: <%d/0x%02x>...", gasNr, he, he ) );
    }
    else if( ( o2 + he ) > 100 )
    {
      // Auch hier geht nicht mehr als 100%
      // Sauerstoff verringern!
      o2 = 100 - he;
      ( gasConfigPanel.o2SpinnerMap.get( gasNr ) ).setValue( o2 );
      LOGGER.log( Level.FINE, String.format( "change helium in Gas %d Value: <%d/0x%02x>, reduct O2 <%d/0x%02x...", gasNr, he, he, o2, o2 ) );
    }
    else
    {
      LOGGER.log( Level.FINE, String.format( "change helium in Gas %d Value: <%d/0x%02x> O2: <%d/0x%02x>...", gasNr, he, he, o2, o2 ) );
    }
    currGasList.setGas( gasNr, o2, he );
    ( gasConfigPanel.gasLblMap.get( gasNr ) ).setText( getNameForGas( gasNr ) );
    if( o2 < 14 )
    {
      ( gasConfigPanel.gasLblMap.get( gasNr ) ).setForeground( gasDangerousColor );
    }
    else if( o2 < 21 )
    {
      ( gasConfigPanel.gasLblMap.get( gasNr ) ).setForeground( gasNoNormOxicColor );
    }
    else
    {
      ( gasConfigPanel.gasLblMap.get( gasNr ) ).setForeground( gasNameNormalColor );
    }
    ignoreAction = false;
  }

  /**
   * Ändere Sauerstoffanteil vom Gas Nummer X Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.04.2012
   * @param gasNr
   *          welches Gas
   * @param o2
   *          Sauerstoffanteil
   */
  private void changeO2FromGas( int gasNr, int o2 )
  {
    int he;
    he = currGasList.getHEFromGas( gasNr );
    ignoreAction = true;
    if( o2 < 0 )
    {
      // das Zeut ist dann auch ungesund!
      o2 = 0;
      ( gasConfigPanel.o2SpinnerMap.get( gasNr ) ).setValue( 0 );
    }
    else if( o2 > 100 )
    {
      // Mehr als 100% geht nicht!
      o2 = 100;
      he = 0;
      ( gasConfigPanel.heSpinnerMap.get( gasNr ) ).setValue( he );
      ( gasConfigPanel.o2SpinnerMap.get( gasNr ) ).setValue( o2 );
      LOGGER.log( Level.WARNING, String.format( "change oxygen (max) in Gas %d Value: <%d/0x%02x>...", gasNr, o2, o2 ) );
    }
    else if( ( o2 + he ) > 100 )
    {
      // Auch hier geht nicht mehr als 100%
      // Helium verringern!
      he = 100 - o2;
      ( gasConfigPanel.heSpinnerMap.get( gasNr ) ).setValue( he );
      LOGGER.log( Level.FINE, String.format( "change oxygen in Gas %d Value: <%d/0x%02x>, reduct HE <%d/0x%02x...", gasNr, o2, o2, he, he ) );
    }
    else
    {
      LOGGER.log( Level.FINE, String.format( "change oxygen in Gas %d Value: <%d/0x%02x>...", gasNr, o2, o2 ) );
    }
    currGasList.setGas( gasNr, o2, he );
    // erzeuge und setze noch den Gasnamen
    ( gasConfigPanel.gasLblMap.get( gasNr ) ).setText( getNameForGas( gasNr ) );
    if( o2 < 14 )
    {
      ( gasConfigPanel.gasLblMap.get( gasNr ) ).setForeground( gasDangerousColor );
    }
    else if( o2 < 21 )
    {
      ( gasConfigPanel.gasLblMap.get( gasNr ) ).setForeground( gasNoNormOxicColor );
    }
    else
    {
      ( gasConfigPanel.gasLblMap.get( gasNr ) ).setForeground( gasNameNormalColor );
    }
    ignoreAction = false;
  }

  /**
   * Gib einen Kurznamen für das Gasgemisch Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.04.2012
   * @param gasNr
   *          Gasnummer
   */
  private String getNameForGas( int gasNr )
  {
    int o2, he, n2;
    // Hexenküche: Was haben wir denn da....
    o2 = currGasList.getO2FromGas( gasNr );
    he = currGasList.getHEFromGas( gasNr );
    n2 = currGasList.getN2FromGas( gasNr );
    // Mal sondieren
    if( n2 == 0 )
    {
      // heliox oder O2
      if( o2 == 100 )
      {
        return( "O2" );
      }
      // Es gibt Helium und O2....
      return( String.format( "HX%d/%d", o2, he ) );
    }
    if( he == 0 )
    {
      // eindeutig Nitrox
      if( o2 == 21 )
      {
        return( "AIR" );
      }
      return( String.format( "NX%02d", o2 ) );
    }
    else
    {
      // das ist dan wohl Trimix
      return( String.format( "TX%d/%d", o2, he ) );
    }
  }

  /**
   * Setze combobox für Deco Gradienten Preset entsprechend der Angaben in den Spinnern Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 13.04.2012
   * @param decoValue
   */
  private void setDecoComboAfterSpinnerChange()
  {
    int currentPreset = currentConfig.getDecoGfPreset();
    if( configPanel.decoGradientenPresetComboBox.getSelectedIndex() != currentPreset )
    {
      ignoreAction = true;
      configPanel.decoGradientenPresetComboBox.setSelectedIndex( currentPreset );
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
    // ////////////////////////////////////////////////////////////////////////
    // Checkbox Event?
    if( ev.getSource() instanceof JCheckBox )
    {
      JCheckBox cb = ( JCheckBox )ev.getItemSelectable();
      String cmd = cb.getActionCommand();
      // //////////////////////////////////////////////////////////////////////
      // Dynamische Gradienten?
      if( cmd.equals( "dyn_gradients_on" ) )
      {
        LOGGER.log( Level.FINE, "dynamic gradients <" + cb.isSelected() + ">" );
        currentConfig.setDynGradientsEnable( cb.isSelected() );
      }
      // //////////////////////////////////////////////////////////////////////
      // Deepstops
      else if( cmd.equals( "deepstops_on" ) )
      {
        LOGGER.log( Level.FINE, "depstops <" + cb.isSelected() + ">" );
        currentConfig.setDeepStopEnable( cb.isSelected() );
      }
      // //////////////////////////////////////////////////////////////////////
      // Passive Semiclose Rebreather Mode?
      else if( cmd.equals( "individuals_pscr_on" ) )
      {
        LOGGER.log( Level.FINE, "pscr mode  <" + cb.isSelected() + ">" );
        currentConfig.setPscrModeEnabled( cb.isSelected() );
      }
      // //////////////////////////////////////////////////////////////////////
      // Sensor warning on/off
      else if( cmd.equals( "individual_sensors_on" ) )
      {
        LOGGER.log( Level.FINE, "sensors on  <" + cb.isSelected() + ">" );
        currentConfig.setSensorsEnabled( cb.isSelected() );
      }
      // //////////////////////////////////////////////////////////////////////
      // Warnungen an/aus
      else if( cmd.equals( "individuals_warnings_on" ) )
      {
        LOGGER.log( Level.FINE, "warnings on  <" + cb.isSelected() + ">" );
        currentConfig.setSountEnabled( cb.isSelected() );
      }
      // //////////////////////////////////////////////////////////////////////
      // Bailout checkbox für ein Gas?
      else if( cmd.startsWith( "bailout:" ) )
      {
        String[] fields = fieldPatternDp.split( cmd );
        try
        {
          int idx = Integer.parseInt( fields[1] );
          LOGGER.log( Level.FINE, String.format( "Bailout %s changed.", cmd ) );
          currGasList.setBailout( idx, cb.isSelected() );
        }
        catch( NumberFormatException ex )
        {
          LOGGER.log( Level.SEVERE, "Exception while recive bailout checkbox event: " + ex.getLocalizedMessage() );
        }
      }
      // //////////////////////////////////////////////////////////////////////
      // Diluent 1 für ein Gsas setzen?
      else if( cmd.startsWith( "diluent1:" ) )
      {
        String[] fields = fieldPatternDp.split( cmd );
        try
        {
          int idx = Integer.parseInt( fields[1] );
          LOGGER.log( Level.FINE, String.format( "Diluent 1  to %d changed.", idx ) );
          if( cb.isSelected() )
          {
            currGasList.setDiluent1( idx );
          }
        }
        catch( NumberFormatException ex )
        {
          LOGGER.log( Level.SEVERE, "Exception while recive diluent1 checkbox event: " + ex.getLocalizedMessage() );
        }
      }
      // //////////////////////////////////////////////////////////////////////
      // Diluent 2 für ein Gsas setzen?
      else if( cmd.startsWith( "diluent2:" ) )
      {
        String[] fields = fieldPatternDp.split( cmd );
        try
        {
          int idx = Integer.parseInt( fields[1] );
          LOGGER.log( Level.FINE, String.format( "Diluent 2  to %d changed.", idx ) );
          if( cb.isSelected() )
          {
            currGasList.setDiluent2( idx );
          }
        }
        catch( NumberFormatException ex )
        {
          LOGGER.log( Level.SEVERE, "Exception while recive diluent1 checkbox event: " + ex.getLocalizedMessage() );
        }
      }
      else
      {
        LOGGER.log( Level.WARNING, "unknown item changed: <" + cb.getActionCommand() + "> <" + cb.isSelected() + ">" );
      }
    }
  }

  /**
   * CLI-Optionen einlesen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.01.2012
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
    // Daternverzeichnis?
    Option optDatabaseDir = new Option( "s", "databasedir", true, "set database directory" );
    options.addOption( optDatabaseDir );
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
   * Aus dem String von Loglevel den Logging-Wert machen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.01.2012
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
   * Setze das neue Logfile, wenn gewünscht Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.01.2012
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

  /**
   * 
   * Neues Datenverzeichnis
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.07.2012
   */
  private static File parseNewDatabaseDir( String optionValue )
  {
    File tempLogFile;
    try
    {
      tempLogFile = new File( optionValue );
      return( tempLogFile );
    }
    catch( NullPointerException ex )
    {
      System.err.println( "parseNewDatabaseDir: Dirname was <null>" );
      return( null );
    }
  }

  /**
   * 
   * Setze alle Kon´figurationspanels auf "Enabled" wenn möglich
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.07.2012
   * @param en
   *          TODO
   */
  private void setAllConfigPanlelsEnabled( boolean en )
  {
    configPanel.setDecoPanelEnabled( en );
    configPanel.setDisplayPanelEnabled( en );
    configPanel.setUnitsPanelEnabled( en );
    configPanel.setSetpointPanel( en );
    // nur, wenn eine gültige Konfiguration gelesen wurde
    if( savedConfig != null )
    {
      // Gibt es eine Lizenz für Custom Config?
      if( currentConfig.getCustomEnabled() == 1 )
      {
        configPanel.setIndividualsPanelEnabled( true );
      }
      else
      {
        configPanel.setIndividualsPanelEnabled( false );
      }
    }
    else
    {
      // Keine Config gelesen!
      configPanel.setIndividualsPanelEnabled( false );
    }
    logListPanel.setAllLogPanelsEnabled( en );
  }

  /**
   * Die Callbacks setzen, wenn sich in den Panels was ändert! Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   */
  private void setGlobalChangeListener()
  {
    tabbedPane.addChangeListener( this );
    connectionPanel.setGlobalChangeListener( this );
    configPanel.setGlobalChangeListener( this );
    gasConfigPanel.setGlobalChangeListener( this );
    logListPanel.setGlobalChangeListener( this );
    logGraphPanel.setGlobalChangeListener( this );
  }

  /**
   * 
   * Zeige einen Optionendialog zur Einstellung von Programmgeschichten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.07.2012
   */
  private void showPropertysDialog()
  {
    if( btComm != null )
    {
      if( btComm.isConnected() )
      {
        showErrorDialog( stringsBundle.getString( "MainCommGUI.errorDialog.onlyNotConnected" ) );
        return;
      }
    }
    LOGGER.log( Level.FINE, "create an show propertys dialog..." );
    ProgramProperetysDialog pDial = new ProgramProperetysDialog( stringsBundle, progConfig );
    // pDial.setVisible( true );
    if( pDial.showModal() )
    {
      LOGGER.log( Level.FINE, "dialog whith OK closed...." );
      // progConfig = pDial.getProcConfig();
      if( progConfig.isWasChanged() )
      {
        showWarnBox( "RESTART PROGRAMM!" );
        pDial.dispose();
        exitProgram();
      }
      LOGGER.log( Level.FINE, "dialog whith OK closed NO Changes...." );
    }
    else
    {
      LOGGER.log( Level.FINE, "dialog canceled...." );
    }
    pDial.dispose();
    LOGGER.log( Level.FINE, "dialog disposed..." );
  }
}
