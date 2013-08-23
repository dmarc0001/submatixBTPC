package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.dmarcini.submatix.pclogger.comm.BTCommunication;
import de.dmarcini.submatix.pclogger.lang.LangStrings;
import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.ConfigReadWriteException;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.OperatingSystemDetector;
import de.dmarcini.submatix.pclogger.utils.ReadConfig;
import de.dmarcini.submatix.pclogger.utils.SPX42Config;
import de.dmarcini.submatix.pclogger.utils.SPX42GasList;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;
import de.dmarcini.submatix.pclogger.utils.VirtualSerialPortsFinder;
import de.dmarcini.submatix.pclogger.utils.WriteConfig;

//@formatter:off
/**
 * @author dmarc
 *
 */
public class MainCommGUI extends JFrame implements ActionListener, MouseMotionListener, ChangeListener, ItemListener
{  //
  /**
   * 
   * Eigene Klasse zum reagieren auf den Close-Button bei Windows
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.09.2012
   */
  private class MainWindowListener implements WindowListener
  {
    @Override
    public void windowActivated( WindowEvent arg0 )
    {}

    @Override
    public void windowClosed( WindowEvent arg0 )
    {}

    @Override
    public void windowClosing( WindowEvent arg0 )
    {
      lg.warn( "WINDOW CLOSING VIA CLOSEBUTTON..." );
      exitProgram();
    }

    @Override
    public void windowDeactivated( WindowEvent arg0 )
    {}

    @Override
    public void windowDeiconified( WindowEvent arg0 )
    {}

    @Override
    public void windowIconified( WindowEvent arg0 )
    {}

    @Override
    public void windowOpened( WindowEvent arg0 )
    {}
  }
  private enum programTabs {
    TAB_CONNECT,
    TAB_CONFIG,
    TAB_GASLIST,
    TAB_LOGREAD,
    TAB_LOGGRAPH,
    TAB_FILEMANAGER
  }
  private static final long       serialVersionUID    = 3L;
  private final static int        VERY_CONSERVATIVE   = 0;
  private final static int        CONSERVATIVE        = 1;
  private final static int        MODERATE            = 2;
  private final static int        AGGRESSIVE          = 3;                                            ;
  private final static int        VERY_AGGRESSIVE     = 4;
  private int                     licenseState        = -1;
  private int                     customConfig        = -1;
  private LogDerbyDatabaseUtil    databaseUtil        = null;
  private int                     waitForMessage      = 0;
  //
  // @formatter:on
  private JFrame                  frmMainWindow;
  private JTabbedPane             tabbedPane;
  private spx42ConnectPanel       connectionPanel;
  private spx42ConfigPanel        configPanel;
  private spx42GaslistEditPanel   gasConfigPanel;
  private spx42LoglistPanel       logListPanel;
  private spx42LogGraphPanel      logGraphPanel;
  private spx42FileManagerPanel   fileManagerPanel;
  private JMenuItem               mntmExit;
  private JMenu                   mnLanguages;
  private JMenu                   mnFile;
  private JMenu                   mnOptions;
  private JMenu                   mnHelp;
  private JMenuItem               mntmHelp;
  private JMenuItem               mntmInfo;
  private JTextField              statusTextField;
  private JMenuItem               mntmOptions;
  private static ResourceBundle   stringsBundle       = null;
  private Locale                  programLocale       = null;
  private String                  timeFormatterString = "yyyy-MM-dd - hh:mm:ss";
  @SuppressWarnings( "unused" )
  private final File              programDir          = new File( System.getProperty( "user.dir" ) );
  private Logger                  lg                  = null;
  private BTCommunication         btComm              = null;
  private final ArrayList<String> messagesList        = new ArrayList<String>();
  private final SPX42Config       currentConfig       = new SPX42Config();
  private SPX42Config             savedConfig         = null;
  private PleaseWaitDialog        wDial               = null;
  private boolean                 ignoreAction        = false;
  private static final Pattern    fieldPatternDp      = Pattern.compile( ":" );
  private static final Pattern    fieldPatternUnderln = Pattern.compile( "[_.]" );

  /**
   * Launch the application.
   * 
   * @param args
   */
  public static void main( String[] args )
  {
    StartSplashWindow splashWin = null;
    Thread tr = null;
    //
    // Kommandozeilenargumente parsen
    //
    if( !parseCliOptions( args ) )
    {
      System.err.println( "Error while scanning CLI-Args...." );
      System.exit( -1 );
    }
    //
    // So, hier könnte ich splashen, alle "gefährlichen" Sachen sind erledigt
    //
    splashWin = new StartSplashWindow();
    tr = new Thread( splashWin );
    tr.start();
    try
    {
      Thread.sleep( 500 );
    }
    catch( InterruptedException ex1 )
    {}
    //
    // GUI starten
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
          window.frmMainWindow.setVisible( true );
        }
        catch( Exception e )
        {
          System.err.println( "Exception: " + e.getLocalizedMessage() + "\n" );
          e.printStackTrace();
        }
      }
    } );
    splashWin.terminate();
  }

  /**
   * CLI-Optionen einlesen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.01.2012
   * @param args
   * @return
   */
  private static boolean parseCliOptions( String[] args )
  {
    CommandLine cmdLine = null;
    String argument;
    // Optionenobjet anlegen
    Options options = new Options();
    Option optLogLevel;
    Option optLogFile;
    Option optDatabaseDir;
    Option optExportDir;
    Option optLangTwoLetter;
    Option optConsoleLog;
    Option optHelp;
    Option optDeveloperDebug;
    GnuParser parser;
    //
    // Optionen für das Parsing anlegen und zu den Optionen zufügen
    //
    // Hilfe
    optHelp = new Option( "help", "give help..." );
    // Logleven festlegen
    OptionBuilder.withArgName( "loglevel" );
    OptionBuilder.hasArg();
    OptionBuilder.withDescription( "Loglevel  (ALL|DEBUG|INFO|WARN|ERROR|FATAL|OFF)" );
    optLogLevel = OptionBuilder.create( "loglevel" );
    // Logfile abgefragt?
    OptionBuilder.withArgName( "logfile" );
    OptionBuilder.hasArg();
    OptionBuilder.withDescription( "custom logfile (directory must exist)" );
    optLogFile = OptionBuilder.create( "logfile" );
    // Daternverzeichnis?
    OptionBuilder.withArgName( "databasedir" );
    OptionBuilder.hasArg();
    OptionBuilder.withDescription( "directory for create ans saving database" );
    optDatabaseDir = OptionBuilder.create( "databasedir" );
    // Exportverzeichnis?
    OptionBuilder.withArgName( "exportdir" );
    OptionBuilder.hasArg();
    OptionBuilder.withDescription( "directory for export UDDF files" );
    optExportDir = OptionBuilder.create( "exportdir" );
    // Landescode vorgeben?
    OptionBuilder.withArgName( "langcode" );
    OptionBuilder.hasArg();
    OptionBuilder.withDescription( "language code for overridign system default (eg. 'en' or 'de' etc.)" );
    optLangTwoLetter = OptionBuilder.create( "langcode" );
    // Log auf console?
    optConsoleLog = new Option( "console", "logging to console for debugging purposes..." );
    // Entwicklerdebug
    optDeveloperDebug = new Option( "developer", "for programmers..." );
    // Optionen zufügen
    options.addOption( optHelp );
    options.addOption( optLogLevel );
    options.addOption( optLogFile );
    options.addOption( optDatabaseDir );
    options.addOption( optExportDir );
    options.addOption( optLangTwoLetter );
    options.addOption( optConsoleLog );
    options.addOption( optDeveloperDebug );
    // Parser anlegen
    parser = new GnuParser();
    try
    {
      cmdLine = parser.parse( options, args );
    }
    catch( ParseException e )
    {
      System.out.println( e.getLocalizedMessage() );
      System.exit( -1 );
    }
    //
    // auswerten der Argumente
    //
    //
    // hilfe?
    //
    if( cmdLine.hasOption( "help" ) )
    {
      HelpFormatter formatter = new HelpFormatter();
      formatter.setWidth( 120 );
      formatter.printHelp( ProjectConst.CREATORPROGRAM, options );
      System.out.println( "ENDE nach HELP..." );
      System.exit( 0 );
    }
    //
    // Loglevel
    //
    if( cmdLine.hasOption( "loglevel" ) )
    {
      argument = cmdLine.getOptionValue( "loglevel" ).toLowerCase();
      // ALL | DEBU G | INFO | WARN | ERROR | FATAL | OFF
      if( argument.equalsIgnoreCase( "all" ) )
        SpxPcloggerProgramConfig.logLevel = Level.ALL;
      else if( argument.equalsIgnoreCase( "debug" ) )
        SpxPcloggerProgramConfig.logLevel = Level.DEBUG;
      else if( argument.equalsIgnoreCase( "info" ) )
        SpxPcloggerProgramConfig.logLevel = Level.INFO;
      else if( argument.equalsIgnoreCase( "warn" ) )
        SpxPcloggerProgramConfig.logLevel = Level.WARN;
      else if( argument.equalsIgnoreCase( "error" ) )
        SpxPcloggerProgramConfig.logLevel = Level.ERROR;
      else if( argument.equalsIgnoreCase( "fatal" ) )
        SpxPcloggerProgramConfig.logLevel = Level.FATAL;
      else if( argument.equalsIgnoreCase( "off" ) )
        SpxPcloggerProgramConfig.logLevel = Level.OFF;
      else
      {
        // Ausgabe der Hilfe, wenn da was unverständliches passierte
        System.err.println( "unbekanntes Argument bei --loglevel <" + argument + ">" );
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( ProjectConst.CREATORPROGRAM, options );
        System.out.println( "ENDE nach DEBUGLEVEL/HELP..." );
        System.exit( -1 );
      }
      SpxPcloggerProgramConfig.wasCliLogLevel = true;
    }
    //
    // Logfile
    //
    if( cmdLine.hasOption( "logfile" ) )
    {
      argument = cmdLine.getOptionValue( "logfile" );
      File tempLogFile, tempParentDir;
      try
      {
        tempLogFile = new File( argument );
        tempParentDir = tempLogFile.getParentFile();
        if( tempParentDir.exists() && tempParentDir.isDirectory() )
        {
          SpxPcloggerProgramConfig.logFile = tempLogFile;
          SpxPcloggerProgramConfig.wasCliLogfile = true;
        }
      }
      catch( NullPointerException ex )
      {
        System.err.println( "logfile was <null>" );
      }
    }
    //
    // database Directory
    //
    if( cmdLine.hasOption( "databasedir" ) )
    {
      argument = cmdLine.getOptionValue( "databasedir" );
      File tempDataDir;
      try
      {
        tempDataDir = new File( argument );
        if( tempDataDir.exists() && tempDataDir.isDirectory() )
        {
          SpxPcloggerProgramConfig.databaseDir = tempDataDir;
          SpxPcloggerProgramConfig.wasCliDatabaseDir = true;
        }
      }
      catch( NullPointerException ex )
      {
        System.err.println( "dataDir was <null>" );
      }
    }
    //
    // Export Directory
    //
    if( cmdLine.hasOption( "exportdir" ) )
    {
      argument = cmdLine.getOptionValue( "exportdir" );
      File tempExportDir;
      try
      {
        tempExportDir = new File( argument );
        if( tempExportDir.exists() && tempExportDir.isDirectory() )
        {
          SpxPcloggerProgramConfig.exportDir = tempExportDir;
          SpxPcloggerProgramConfig.wasCliExportDir = true;
        }
      }
      catch( NullPointerException ex )
      {
        System.err.println( "dataDir was <null>" );
      }
    }
    //
    // Sprachcode (abweichend vom lokelen)
    //
    if( cmdLine.hasOption( "langcode" ) )
    {
      argument = cmdLine.getOptionValue( "langcode" );
      if( argument.length() >= 2 )
      {
        SpxPcloggerProgramConfig.langCode = argument;
        SpxPcloggerProgramConfig.wasCliLangCode = true;
      }
    }
    //
    // Console
    //
    if( cmdLine.hasOption( "console" ) )
    {
      SpxPcloggerProgramConfig.consoleLog = true;
      SpxPcloggerProgramConfig.wasCliConsoleLog = true;
    }
    //
    // Entwicklerdebug
    //
    if( cmdLine.hasOption( "developer" ) )
    {
      SpxPcloggerProgramConfig.developDebug = true;
    }
    return( true );
  }

  /**
   * Create the application.
   * 
   * @throws ConfigReadWriteException
   * @throws IOException
   */
  public MainCommGUI() throws IOException, ConfigReadWriteException
  {
    lg = SpxPcloggerProgramConfig.LOGGER;
    setDefaultLookAndFeelDecorated( isDefaultLookAndFeelDecorated() );
    // Konfiguration aus der Datei einlesen
    // berücksichtigt schon per CLI angegebene Werte als gesetzt
    new ReadConfig();
    makeLogger();
    //
    // gib ein paar informationen
    //
    lg.info( "Operating System: <" + OperatingSystemDetector.getOsName() + ">" );
    lg.info( "Java VM Datamodel: " + OperatingSystemDetector.getDataModel() + " bits" );
    lg.info( "Java VM Datamodel: <" + OperatingSystemDetector.getArch() + ">" );
    //
    // Grundsätzliche Sachen einstellen
    //
    if( !SpxPcloggerProgramConfig.databaseDir.isDirectory() )
    {
      lg.error( "can't create data directory <" + SpxPcloggerProgramConfig.databaseDir.getAbsolutePath() + ">" );
      System.exit( -1 );
    }
    try
    {
      ResourceBundle.clearCache();
      if( SpxPcloggerProgramConfig.langCode != null )
      {
        lg.info( "try make locale from cmd options <" + SpxPcloggerProgramConfig.langCode + ">..." );
        programLocale = new Locale( SpxPcloggerProgramConfig.langCode );
      }
      else
      {
        lg.debug( "try get locale from system..." );
        programLocale = Locale.getDefault();
      }
      LangStrings.setLocale( programLocale );
      lg.debug( String.format( "getLocale says: Display Language :<%s>, lang: <%s>", programLocale.getDisplayLanguage(), programLocale.getLanguage() ) );
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.messages", programLocale );
      if( stringsBundle.getLocale().equals( programLocale ) )
      {
        lg.debug( "language accepted.." );
      }
      else
      {
        lg.debug( "language fallback default..." );
        programLocale = Locale.ENGLISH;
        Locale.setDefault( programLocale );
        stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.messages", programLocale );
      }
    }
    catch( MissingResourceException ex )
    {
      lg.error( "ERROR get resources <" + ex.getMessage() + "> try standart Strings..." );
      System.err.println( "ERROR get resources <" + ex.getMessage() + "> try standart Strings..." );
      try
      {
        lg.debug( "try get  default english locale from system..." );
        programLocale = Locale.ENGLISH;
        Locale.setDefault( programLocale );
        stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.messages_en" );
      }
      catch( Exception ex1 )
      {
        lg.error( "ERROR get resources <" + ex1.getMessage() + "> give up..." );
        System.exit( -1 );
      }
    }
    //
    // jetzt die wichtigen anderen Sachen, die dauern.
    //
    prepareDatabase();
    currentConfig.setLogger( lg );
    btComm = new BTCommunication( databaseUtil );
    btComm.addActionListener( this );
    try
    {
      initializeGUI();
    }
    catch( SQLException ex )
    {
      lg.error( "SQL ERROR <" + ex.getMessage() + "> give up..." );
      System.err.println( "ERROR while create GUI: <" + ex.getLocalizedMessage() + ">" );
      ex.printStackTrace();
      System.exit( -1 );
    }
    catch( ClassNotFoundException ex )
    {
      lg.error( "CLASS NOT FOUND EXCEPTION <" + ex.getMessage() + "> give up..." );
      System.err.println( "ERROR while create GUI: <" + ex.getLocalizedMessage() + ">" );
      ex.printStackTrace();
      System.exit( -1 );
    }
    // Listener setzen (braucht auch die Maps)
    setGlobalChangeListener();
    //
    initLanuageMenu( programLocale );
    if( !SpxPcloggerProgramConfig.developDebug )
    {
      configPanel.setAllConfigPanlelsEnabled( false );
      logListPanel.setAllLogPanelsEnabled( false );
      setElementsConnected( false );
    }
    if( setLanguageStrings() < 1 )
    {
      lg.error( "setLanguageStrings() faild. give up..." );
      System.exit( -1 );
    }
    connectionPanel.setVirtDevicesBoxEnabled( false );
    startVirtualPortFinder( null );
    waitForMessage = 0;
  }

  /**
   * Starte einen virtuellen Portfinder Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 21.08.2013
   * @param _model
   */
  private void startVirtualPortFinder( DefaultComboBoxModel<String> _model )
  {
    VirtualSerialPortsFinder vPortFinder = new VirtualSerialPortsFinder( this, _model );
    Thread th = new Thread( vPortFinder );
    th.setName( "virtual_port_finder" );
    th.start();
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
    lg.debug( "change language to <" + cmd + ">" );
    if( langs.length > 1 && langs[1] != null )
    {
      programLocale = new Locale( langs[0], langs[1] );
    }
    else
    {
      programLocale = new Locale( langs[0] );
    }
    Locale.setDefault( programLocale );
    LangStrings.setLocale( programLocale );
    if( currentConfig != null )
    {
      // da verändern sich die Einstellungen, daher ungültig setzen
      currentConfig.setWasInit( false );
    }
    setLanguageStrings();
  }

  /**
   * Hilfsfunktion zum start speichern/update eines Tauchlogs Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 12.07.2012
   * @param logListEntry
   */
  private int computeLogRequest( Integer[] logListEntry )
  {
    // liegt ein updatewunsch vor?
    if( logListEntry[1] > 0 )
    {
      lg.warn( "dive logentry alredy there. Ask user for continue..." );
      //@formatter:off
      // Zeige dem geneigten User eine Dialogbox, in welcher er entscheiden muss: Update oder überspringen
      Object[] options =
      { 
        LangStrings.getString("MainCommGUI.updateWarnDialog.updateButton"),
        LangStrings.getString("MainCommGUI.updateWarnDialog.cancelButton")
      };
      int retOption =  JOptionPane.showOptionDialog( 
              null, 
              LangStrings.getString("MainCommGUI.updateWarnDialog.messageUpdate"), 
              LangStrings.getString("MainCommGUI.updateWarnDialog.headLine"), 
              JOptionPane.DEFAULT_OPTION, 
              JOptionPane.WARNING_MESSAGE, 
              null, 
              options, 
              options[0] 
              );
      //@formatter:on
      if( retOption == 1 )
      {
        lg.info( "user has cancel to update divelog entry." );
        return( 0 );
      }
      else
      {
        // update datensatz!
        logListPanel.setNextLogIsAnUpdate( true, logListEntry[0] );
      }
    }
    // datensatz anlegen
    wDial = new PleaseWaitDialog( LangStrings.getString( "PleaseWaitDialog.title" ), LangStrings.getString( "PleaseWaitDialog.waitForReadDive" ) );
    wDial.setDetailMessage( String.format( LangStrings.getString( "PleaseWaitDialog.readDiveNumber" ), logListEntry[0] ) );
    wDial.setTimeout( 90 * 1000 );
    wDial.setVisible( true );
    // Sag dem SPX er soll alles schicken
    lg.debug( "send command to spx: send logfile number <" + logListEntry[0] + ">" );
    btComm.readLogDetailFromSPX( logListEntry[0] );
    return( 1 );
  }

  /**
   * Verbine mit SPX42 Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 20.12.2011
   */
  private void connectSPX( String deviceName )
  {
    lg.debug( "connect via device <" + deviceName + ">..." );
    if( btComm.isConnected() )
    {
      // ist verbunden, was nun?
      return;
    }
    else
    {
      // nicht verbunden, tu was!
      try
      {
        wDial = new PleaseWaitDialog( LangStrings.getString( "PleaseWaitDialog.title" ), LangStrings.getString( "PleaseWaitDialog.pleaseWaitForConnect" ) );
        wDial.setVisible( true );
        wDial.setTimeout( 90 * 1000 );
      }
      catch( Exception ex )
      {
        showErrorDialog( ex.getLocalizedMessage() );
        lg.error( "Exception: <" + ex.getMessage() + ">" );
      }
    }
  }

  private void connectVirtSPX( String deviceName )
  {
    lg.debug( "connect via virtual device <" + deviceName + ">..." );
    if( btComm.isConnected() )
    {
      // ist verbunden, was nun?
      return;
    }
    else
    {
      // nicht verbunden, tu was!
      try
      {
        wDial = new PleaseWaitDialog( LangStrings.getString( "PleaseWaitDialog.title" ), LangStrings.getString( "PleaseWaitDialog.pleaseWaitForConnect" ) );
        wDial.setVisible( true );
        wDial.setTimeout( 90 * 1000 );
        btComm.connectVirtDevice( deviceName );
      }
      catch( Exception ex )
      {
        showErrorDialog( ex.getLocalizedMessage() );
        lg.error( "Exception: <" + ex.getMessage() + ">" );
      }
    }
  }

  /**
   * Decodiere die Nachricht über einen Logverzeichniseintrag Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.05.2012
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
      lg.error( "recived message for logdir has lower than 4 fields. It is wrong! Abort!" );
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
      lg.error( "Fail to convert Hex to int: " + ex.getLocalizedMessage() );
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
      lg.error( "Fail to convert Hex to int: " + ex.getLocalizedMessage() );
      return( null );
    }
    // So, die Angaben des SPX sind immer im Localtime-Format
    // daher werde ich die auch so interpretieren
    // Die Funktion macht das in der default-Lokalzone, sollte also
    // da sein, wio der SPX auch ist... (schwieriges Thema)
    DateTime tm = new DateTime( year, month, day, hour, minute, second );
    DateTimeFormatter fmt = DateTimeFormat.forPattern( timeFormatterString );
    return( String.format( "%d;%s;%s;%d;%d", number, fileName, tm.toString( fmt ), max, tm.getMillis() ) );
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
      lg.debug( "disconnect SPX42..." );
      btComm.disconnectDevice();
    }
  }

  /**
   * Eventuell geordnetes Aufräumen hier Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.12.2011
   */
  private void exitProgram()
  {
    // Ereignisse ignorieren!
    ignoreAction = true;
    //
    if( databaseUtil != null )
    {
      if( databaseUtil.isOpenDB() )
      {
        databaseUtil.closeDB();
      }
      databaseUtil = null;
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
    if( SpxPcloggerProgramConfig.wasChanged )
    {
      try
      {
        lg.info( "write config to file..." );
        new WriteConfig();
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
    dispose();
    System.exit( NORMAL );
  }

  /**
   * Initialize the contents of the frame.
   * 
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  private void initializeGUI() throws SQLException, ClassNotFoundException
  {
    frmMainWindow = new JFrame();
    frmMainWindow.setFont( new Font( "Arial", Font.PLAIN, 12 ) );
    frmMainWindow.setSize( new Dimension( 810, 600 ) );
    frmMainWindow.setResizable( false );
    frmMainWindow.setIconImage( Toolkit.getDefaultToolkit().getImage( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112.png" ) ) );
    frmMainWindow.setTitle( LangStrings.getString( "MainCommGUI.frmMainWindow.title" ) ); //$NON-NLS-1$
    frmMainWindow.setBounds( 100, 100, 800, 600 );
    frmMainWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frmMainWindow.getContentPane().setLayout( new BorderLayout( 0, 0 ) );
    frmMainWindow.addWindowListener( new MainWindowListener() );
    statusTextField = new JTextField();
    statusTextField.setEditable( false );
    statusTextField.setText( "-" );
    frmMainWindow.getContentPane().add( statusTextField, BorderLayout.SOUTH );
    statusTextField.setColumns( 10 );
    tabbedPane = new JTabbedPane( JTabbedPane.TOP );
    frmMainWindow.getContentPane().add( tabbedPane, BorderLayout.CENTER );
    tabbedPane.addMouseMotionListener( this );
    // Connection Panel
    connectionPanel = new spx42ConnectPanel( databaseUtil );
    tabbedPane.addTab( "CONNECTION", null, connectionPanel, null );
    tabbedPane.setEnabledAt( programTabs.TAB_CONNECT.ordinal(), true );
    // config Panel
    configPanel = new spx42ConfigPanel();
    tabbedPane.addTab( "CONFIG", null, configPanel, null );
    tabbedPane.setEnabledAt( programTabs.TAB_CONFIG.ordinal(), true );
    // GASPANEL
    gasConfigPanel = new spx42GaslistEditPanel( databaseUtil );
    tabbedPane.addTab( "GAS", null, gasConfigPanel, null );
    tabbedPane.setEnabledAt( programTabs.TAB_GASLIST.ordinal(), true );
    // Loglisten Panel
    logListPanel = new spx42LoglistPanel( this, databaseUtil );
    tabbedPane.addTab( "LOG", null, logListPanel, null );
    tabbedPane.setEnabledAt( programTabs.TAB_LOGREAD.ordinal(), true );
    // Grafik Panel
    logGraphPanel = new spx42LogGraphPanel( databaseUtil );
    tabbedPane.addTab( "GRAPH", null, logGraphPanel, null );
    tabbedPane.setEnabledAt( programTabs.TAB_LOGGRAPH.ordinal(), true );
    // import/export Panel
    fileManagerPanel = new spx42FileManagerPanel( this, databaseUtil );
    tabbedPane.addTab( "EXPORT", null, fileManagerPanel, null );
    tabbedPane.setEnabledAt( programTabs.TAB_FILEMANAGER.ordinal(), true );
    // MENÜ
    JMenuBar menuBar = new JMenuBar();
    frmMainWindow.setJMenuBar( menuBar );
    mnFile = new JMenu( LangStrings.getString( "MainCommGUI.mnFile.text" ) ); //$NON-NLS-1$
    menuBar.add( mnFile );
    mntmExit = new JMenuItem( LangStrings.getString( "MainCommGUI.mntmExit.text" ) ); //$NON-NLS-1$
    mntmExit.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/176.png" ) ) );
    mntmExit.setActionCommand( "exit" );
    mntmExit.addActionListener( this );
    mntmExit.addMouseMotionListener( this );
    mntmExit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, InputEvent.CTRL_MASK ) );
    mnFile.add( mntmExit );
    mnLanguages = new JMenu( LangStrings.getString( "MainCommGUI.mnLanguages.text" ) ); //$NON-NLS-1$
    mnLanguages.addMouseMotionListener( this );
    menuBar.add( mnLanguages );
    mnOptions = new JMenu( LangStrings.getString( "MainCommGUI.mnOptions.text" ) ); //$NON-NLS-1$
    mnOptions.addMouseMotionListener( this );
    menuBar.add( mnOptions );
    mntmOptions = new JMenuItem( LangStrings.getString( "MainCommGUI.mntmOptions.text" ) ); //$NON-NLS-1$
    mntmOptions.addMouseMotionListener( this );
    mntmOptions.addActionListener( this );
    mntmOptions.setActionCommand( "set_propertys" );
    mnOptions.add( mntmOptions );
    mnHelp = new JMenu( LangStrings.getString( "MainCommGUI.mnHelp.text" ) ); //$NON-NLS-1$
    mnHelp.addMouseMotionListener( this );
    menuBar.add( mnHelp );
    mntmHelp = new JMenuItem( LangStrings.getString( "MainCommGUI.mntmHelp.text" ) ); //$NON-NLS-1$
    mntmHelp.addActionListener( this );
    mntmHelp.setActionCommand( "help" );
    mntmHelp.addMouseMotionListener( this );
    mnHelp.add( mntmHelp );
    mntmInfo = new JMenuItem( LangStrings.getString( "MainCommGUI.mntmInfo.text" ) ); //$NON-NLS-1$
    mntmInfo.addActionListener( this );
    mntmInfo.setActionCommand( "info" );
    mntmInfo.addMouseMotionListener( this );
    mntmInfo.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/javax/swing/plaf/metal/icons/ocean/expanded.gif" ) ) );
    mntmInfo.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_I, InputEvent.CTRL_MASK ) );
    mnHelp.add( mntmInfo );
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
      lg.debug( "try init language menu..." );
      ignoreAction = true;
      // Lies die Resource aus
      rb = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.languages" );
      // Alle KEYS lesen
      enu = rb.getKeys();
      try
      {
        lg.debug( "try init language menuitems..." );
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
        lg.debug( "try init language menuitems...done" );
      }
      catch( NullPointerException ex )
      {
        lg.error( "NULL POINTER EXCEPTION <" + ex.getMessage() + ">" );
        statusTextField.setText( "ERROR set language strings" );
        System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
        System.exit( -1 );
      }
      catch( MissingResourceException ex )
      {
        lg.error( "MISSING RESOURCE EXCEPTION <" + ex.getMessage() + ">" );
        statusTextField.setText( "ERROR set language strings" );
        System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
        System.exit( -1 );
      }
      catch( ClassCastException ex )
      {
        lg.error( "CLASS CAST EXCEPTION <" + ex.getMessage() + ">" );
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
      lg.error( "NULL POINTER EXCEPTION <" + ex.getMessage() + ">" );
      statusTextField.setText( "ERROR set language strings" );
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      System.exit( -1 );
    }
    catch( MissingResourceException ex )
    {
      lg.error( "MISSING RESOURCE EXCEPTION <" + ex.getMessage() + ">" );
      statusTextField.setText( "ERROR set language strings - the given key can be found" );
      System.out.println( "ERROR set language strings - the given key can be found <" + ex.getMessage() + "> ABORT!" );
      System.exit( -1 );
    }
    catch( ClassCastException ex )
    {
      lg.error( "CLASS CAST EXCEPTION <" + ex.getMessage() + ">" );
      statusTextField.setText( "ERROR set language strings" );
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      System.exit( -1 );
    }
    finally
    {
      ignoreAction = false;
    }
    lg.debug( "try init language menu...done" );
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
        lg.debug( "dynamic gradients <" + cb.isSelected() + ">" );
        currentConfig.setDynGradientsEnable( cb.isSelected() );
      }
      // //////////////////////////////////////////////////////////////////////
      // Deepstops
      else if( cmd.equals( "deepstops_on" ) )
      {
        lg.debug( "depstops <" + cb.isSelected() + ">" );
        currentConfig.setDeepStopEnable( cb.isSelected() );
      }
      // //////////////////////////////////////////////////////////////////////
      // Passive Semiclose Rebreather Mode?
      else if( cmd.equals( "individuals_pscr_on" ) )
      {
        lg.debug( "pscr mode  <" + cb.isSelected() + ">" );
        currentConfig.setPscrModeEnabled( cb.isSelected() );
      }
      // //////////////////////////////////////////////////////////////////////
      // Sensor warning on/off
      else if( cmd.equals( "individual_sensors_on" ) )
      {
        lg.debug( "sensors on  <" + cb.isSelected() + ">" );
        currentConfig.setSensorsEnabled( cb.isSelected() );
      }
      // //////////////////////////////////////////////////////////////////////
      // Warnungen an/aus
      else if( cmd.equals( "individuals_warnings_on" ) )
      {
        lg.debug( "warnings on  <" + cb.isSelected() + ">" );
        currentConfig.setSountEnabled( cb.isSelected() );
      }
      else
      {
        lg.warn( "unknown item changed: <" + cb.getActionCommand() + "> <" + cb.isSelected() + ">" );
      }
    }
  }

  /**
   * Systemlogger machen! Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.12.2011
   * @param logFile
   * @param logLevel
   */
  private void makeLogger()
  {
    Logger lg = SpxPcloggerProgramConfig.LOGGER;
    try
    {
      //
      // alle appender löschen
      //
      lg.removeAllAppenders();
      // ALL | D EBUG | INFO | WARN | ERROR | FATAL | OFF:
      // Logs mit Datum-Zeit,Class,Threadname,LogLevel,Threadname, Kategorie
      // Meldung...
      PatternLayout fileLayout;
      if( SpxPcloggerProgramConfig.logLevel == Level.DEBUG )
      {
        fileLayout = new PatternLayout( "%d{ISO8601}|%p|%C|%t|%m%n" );
      }
      else
      {
        fileLayout = new PatternLayout( "%d{ISO8601}|%p|%C|%t|%M|%m%n" );
      }
      if( SpxPcloggerProgramConfig.consoleLog )
      {
        PatternLayout consoleLayout;
        if( SpxPcloggerProgramConfig.logLevel == Level.DEBUG )
        {
          consoleLayout = new PatternLayout( "[%d{ISO8601}] %-6p %C{1} %M [%t] \"%m\"%n" );
        }
        else
        {
          consoleLayout = new PatternLayout( "[%d{ISO8601}] %-6p %C{1} [%t] \"%m\"%n" );
        }
        ConsoleAppender consoleAppender = new ConsoleAppender( consoleLayout );
        // consoleAppender.setEncoding("ISO-8859-15");
        lg.addAppender( consoleAppender );
      }
      FileAppender fileAppender = new FileAppender( fileLayout, SpxPcloggerProgramConfig.logFile.getAbsolutePath(), true );
      lg.addAppender( fileAppender );
      lg.setLevel( SpxPcloggerProgramConfig.logLevel );
      if( lg.isDebugEnabled() ) lg.debug( "lg Created..." );
    }
    catch( Exception ex )
    {
      System.err.println( ex );
      System.exit( -1 );
    }
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
    else if( ev.getSource() instanceof JComboBox<?> )
    {
      setStatus( ( ( JComboBox<?> )ev.getSource() ).getToolTipText() );
    }
    else if( ev.getSource() instanceof JMenuItem )
    {
      setStatus( ( ( JMenuItem )ev.getSource() ).getToolTipText() );
    }
    else if( ev.getSource() instanceof JSpinner )
    {
      setStatus( ( ( JSpinner )ev.getSource() ).getToolTipText() );
    }
    else if( ev.getSource() instanceof JCheckBox )
    {
      setStatus( ( ( JCheckBox )ev.getSource() ).getToolTipText() );
    }
    else if( ev.getSource() instanceof JTable )
    {
      setStatus( ( ( JTable )ev.getSource() ).getToolTipText() );
    }
    else
    {
      setStatus( "" );
    }
  }

  /**
   * Datenbankfür das Programm vorbereiten oder erzeugen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 24.04.2012
   */
  private void prepareDatabase()
  {
    // Verbindung zum Datenbanktreiber
    databaseUtil = new LogDerbyDatabaseUtil( this );
    if( databaseUtil == null )
    {
      lg.error( "can connect to database drivers!" );
      System.exit( -1 );
    }
    // öffne die Datenbank
    // ging das?
    try
    {
      if( databaseUtil.createConnection() == null )
      {
        System.exit( -1 );
      }
    }
    catch( SQLException ex )
    {
      ex.printStackTrace();
      System.exit( -1 );
    }
    catch( ClassNotFoundException ex )
    {
      ex.printStackTrace();
      System.exit( -1 );
    }
    // hier ist alles gut...
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
    // lese Config aus Device
    if( cmd.equals( "read_config" ) )
    {
      if( btComm != null )
      {
        if( btComm.isConnected() )
        {
          wDial = new PleaseWaitDialog( LangStrings.getString( "PleaseWaitDialog.title" ), LangStrings.getString( "PleaseWaitDialog.readSpxConfig" ) );
          wDial.setMax( BTCommunication.CONFIG_READ_KDO_COUNT );
          wDial.setVisible( true );
          wDial.setTimeout( 90 * 1000 );
          btComm.readConfigFromSPX42();
          // warte auf diese Nachricht....
          waitForMessage = ProjectConst.MESSAGE_SPXALIVE;
        }
        else
        {
          showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
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
          showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notConfig.text" ) );
          return;
        }
        writeConfigToSPX( savedConfig );
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // Alias editor zeigen
    else if( cmd.equals( "alias_bt_devices_on" ) )
    {
      lg.info( "alias editor show..." );
      connectionPanel.setAliasesEditable( true );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Alias editor zeigen
    else if( cmd.equals( "alias_bt_devices_off" ) )
    {
      lg.info( "alias editor hide..." );
      connectionPanel.setAliasesEditable( false );
    }
    // /////////////////////////////////////////////////////////////////////////
    // ich will die Gasliste haben!
    else if( cmd.equals( "read_gaslist" ) )
    {
      lg.info( "call read gaslist from device..." );
      if( btComm != null )
      {
        if( btComm.isConnected() )
        {
          wDial = new PleaseWaitDialog( LangStrings.getString( "PleaseWaitDialog.title" ), LangStrings.getString( "PleaseWaitDialog.readGaslist" ) );
          wDial.setMax( BTCommunication.CONFIG_READ_KDO_COUNT );
          wDial.setTimeout( 90 * 1000 );
          wDial.setVisible( true );
          btComm.readGaslistFromSPX42();
        }
        else
        {
          showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
        }
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // ich will die Gasliste schreiben!
    else if( cmd.equals( "write_gaslist" ) )
    {
      lg.info( "call write gaslist to device..." );
      SPX42GasList currGasList = gasConfigPanel.getCurrGasList();
      if( currGasList == null )
      {
        lg.warn( "Not gaslist in gaseditPanle created!" );
        return;
      }
      if( btComm != null )
      {
        if( btComm.isConnected() && currGasList.isInitialized() )
        {
          wDial = new PleaseWaitDialog( LangStrings.getString( "PleaseWaitDialog.title" ), LangStrings.getString( "PleaseWaitDialog.writeGasList" ) );
          wDial.setMax( BTCommunication.CONFIG_READ_KDO_COUNT );
          wDial.setTimeout( 90 * 1000 );
          wDial.setVisible( true );
          btComm.writeGaslistToSPX42( currGasList, currentConfig.getFirmwareVersion() );
        }
        else
        {
          if( !btComm.isConnected() )
          {
            showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
          }
          else
          {
            showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.gasNotLoaded.text" ) );
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
            // Die Liste direkt vom SPX einlesen!
            // an dieser Stelle muss ich sicherstellen, daß ich die Masseinheiten des SPX42 kenne
            // ich gehe mal von den Längeneinheiten aus!
            // also Meter/Fuss
            wDial = new PleaseWaitDialog( LangStrings.getString( "PleaseWaitDialog.title" ), LangStrings.getString( "PleaseWaitDialog.readLogDir" ) );
            wDial.setTimeout( 90 * 1000 );
            wDial.setVisible( true );
            if( !currentConfig.isInitialized() )
            {
              // jetzt wird es schwierig. erfrage erst mal die Config!
              btComm.readConfigFromSPX42();
            }
            // Sag dem SPX er soll alles schicken
            btComm.readLogDirectoryFromSPX();
          }
        }
        else
        {
          showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
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
            showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notLogentrySelected.text" ) );
            return;
          }
          if( !currentConfig.isInitialized() )
          {
            showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notConfig.text" ) );
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
          showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
        }
      }
    }
    // /////////////////////////////////////////////////////////////////////////
    // Virtual Coms neu einlesen
    else if( cmd.equals( "renew_virt_buttons" ) )
    {
      connectionPanel.setVirtDevicesBoxEnabled( false );
      startVirtualPortFinder( null );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Da hab ich nix passendes gefunden!
    else
    {
      lg.warn( "unknown button command <" + cmd + "> recived." );
    }
    return;
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
    JComboBox<?> srcBox = ( JComboBox<?> )ev.getSource();
    // /////////////////////////////////////////////////////////////////////////
    // Letzter Decostop auf 3 oder 6 Meter
    if( cmd.equals( "deco_last_stop" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "deco last stop <" + entry + ">..." );
      currentConfig.setLastStop( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Preset für Deco-Gradienten ausgewählt
    else if( cmd.equals( "deco_gradient_preset" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "gradient preset <" + entry + ">, Index: <" + srcBox.getSelectedIndex() + ">..." );
      currentConfig.setDecoGfPreset( srcBox.getSelectedIndex() );
      // Spinner setzen
      setGradientSpinnersAfterPreset( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Autosetpoint Voreinstellung
    else if( cmd.equals( "set_autosetpoint" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "autosetpoint preset <" + entry + ">..." );
      currentConfig.setAutoSetpoint( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Setpoint für höchsten PPO2 Wert einstellen
    else if( cmd.equals( "set_highsetpoint" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "hightsetpoint <" + entry + ">..." );
      currentConfig.setMaxSetpoint( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Helligkeit des Displays
    else if( cmd.equals( "set_disp_brightness" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "brightness <" + entry + ">..." );
      currentConfig.setDisplayBrithtness( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Ausrichtung des Displays
    else if( cmd.equals( "set_display_orientation" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "orientation <" + entry + ">..." );
      currentConfig.setDisplayOrientation( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Grad Celsius oder Fahrenheit einstellen
    else if( cmd.equals( "set_temperature_unit" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "temperature unit <" + entry + ">..." );
      currentConfig.setUnitTemperature( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Maßeinheit für Tiefe festlegen
    else if( cmd.equals( "set_depth_unit" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "depth unit <" + entry + ">..." );
      currentConfig.setUnitDepth( srcBox.getSelectedIndex() );
      configPanel.setUnitDepth( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Süßwasser oder Salzwasser
    else if( cmd.equals( "set_salnity" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "salnity <" + entry + ">..." );
      currentConfig.setUnitSalnyty( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Loginterval einstellen
    else if( cmd.equals( "set_loginterval" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "loginterval <" + entry + ">..." );
      currentConfig.setLogInterval( srcBox.getSelectedIndex() );
    }
    // /////////////////////////////////////////////////////////////////////////
    // Anzahl der sensoren für Messung/Warung einstellen
    else if( cmd.equals( "set_sensorwarnings" ) )
    {
      entry = ( String )srcBox.getSelectedItem();
      lg.debug( "sensorwarnings <" + entry + ">...Index: <" + srcBox.getSelectedIndex() + ">" );
      currentConfig.setSensorsCount( srcBox.getSelectedIndex() );
    }
    else
    {
      lg.warn( "unknown combobox command <" + cmd + "> recived." );
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
      // Ordentlich verlassen ;-)
      tabbedPane.setSelectedIndex( programTabs.TAB_CONNECT.ordinal() );
      exitProgram();
    }
    // /////////////////////////////////////////////////////////////////////////
    // Info Box anzeigen
    else if( cmd.equals( "info" ) )
    {
      lg.debug( "Call INFO-Dialog..." );
      showInfoDialog();
    }
    // /////////////////////////////////////////////////////////////////////////
    // Hilfe Box anzeigen
    else if( cmd.equals( "help" ) )
    {
      lg.debug( "Call HELP-Dialog..." );
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
      lg.debug( "Change Language..." );
      String lang = cmd.replace( "lang_", "" );
      lg.info( "change language to <" + lang + ">" );
      changeProgramLanguage( lang );
    }
    else
    {
      lg.warn( "unknown menu command <" + cmd + "> recived." );
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
      // timer ereignis UND das Fenster ist offen
      if( ( actionId == ProjectConst.MESSAGE_TICK ) && ( wDial != null ) )
      {
        // ist der Timeout um?
        if( wDial.isTimeout() )
        {
          lg.error( "dialog window timeout is over!" );
          wDial.dispose();
          wDial = null;
          showErrorDialog( LangStrings.getString( "MainCommGUI.errorDialog.timeout" ) );
          // den Merker auf null setzen!
          waitForMessage = 0;
        }
      }
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
    // Virtuelle Ports verändert
      case ProjectConst.MESSAGE_PORT_STATE_CHANGE:
        lg.debug( "VIRTUAL PORT CHANGED command!" );
        if( ev.getSource() instanceof VirtualSerialPortsFinder )
        {
          VirtualSerialPortsFinder finder = ( VirtualSerialPortsFinder )ev.getSource();
          connectionPanel.setNewVirtDeviceList( finder.getComboBoxModel() );
          connectionPanel.setVirtDevicesBoxEnabled( true );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Info anzeigen...
      case ProjectConst.MESSAGE_TOAST:
        lg.debug( "TOAST command!" );
        connectionPanel.setToastMessage( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Hab was gelesen!
      case ProjectConst.MESSAGE_READ:
        lg.debug( "READ Command!" );
        // soll den reader Thread und die GUI nicht blockieren
        // daher nur in die Liste schmeissen (die ist thread-sicher)
        if( ( !cmd.isEmpty() ) && ( !cmd.equals( "\n" ) ) )
        {
          messagesList.add( cmd );
          lg.debug( "RECIVED: <" + cmd + ">" );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Gerätename ausgelesen
      case ProjectConst.MESSAGE_MANUFACTURER_READ:
        lg.info( "Device Manufacturer Name from SPX42 <" + cmd + "> recived..." );
        currentConfig.setDeviceName( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Firmwareversion gelesen!
      case ProjectConst.MESSAGE_FWVERSION_READ:
        lg.info( "Firmware Version <" + cmd + "> recived..." );
        currentConfig.setFirmwareVersion( cmd );
        configPanel.setFirmwareLabel( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Seriennummer vom SPX42
      case ProjectConst.MESSAGE_SERIAL_READ:
        lg.info( "Serial Number from SPX42 recived..." );
        configPanel.setSerialNumber( cmd );
        currentConfig.setSerial( cmd );
        // bei Verbindung mit einem virtuellen Device muss ich noch einen Namen eintragen. Das ist dann die Seriennummer
        if( btComm.getConnectedDevice().equals( "virtual" ) )
        {
          btComm.setNameForVirtualDevice( cmd );
          // jetzt guck mal, ob ein Alias vorhanden ist
          if( null == databaseUtil.getAliasForNameConn( cmd ) )
          {
            // dann muss ich den auch noch eintragen
            databaseUtil.addAliasForNameConn( cmd, cmd, "virtual" );
          }
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Decompressionseinstellungen gelesen
      case ProjectConst.MESSAGE_DECO_READ:
        lg.info( "DECO propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setDecoGf( cmd ) )
        {
          lg.info( "DECO propertys set to GUI..." );
          configPanel.setDecoGradient();
          lg.info( "DECO propertys set to GUI...OK" );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Einheiten Einstellungen vom SPX42 gelesen
      case ProjectConst.MESSAGE_UNITS_READ:
        lg.info( "UNITS propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setUnits( cmd ) )
        {
          lg.info( "UNITS propertys set to GUI..." );
          configPanel.setUnits();
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Displayeinstellungen gelesen
      case ProjectConst.MESSAGE_DISPLAY_READ:
        lg.info( "DISPLAY propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setDisplay( cmd ) )
        {
          lg.info( "DISPLAY propertys set to GUI..." );
          configPanel.setDisplayPropertys();
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Einstellungen zum O2 Setpint gelesen
      case ProjectConst.MESSAGE_SETPOINT_READ:
        lg.info( "SETPOINT propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setSetpoint( cmd ) )
        {
          lg.info( "SETPOINT propertys set to GUI..." );
          configPanel.setSetpoint();
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Einstellungen für Individuell gelesen (Extra-Lizenz erforderlich )
      case ProjectConst.MESSAGE_INDIVID_READ:
        lg.info( "INDIVIDUAL propertys from SPX42 recived..." );
        if( wDial != null )
        {
          wDial.incrementProgress();
        }
        if( currentConfig.setIndividuals( cmd ) )
        {
          lg.info( "INDIVIDUAL propertys set to GUI..." );
          configPanel.setIndividuals( true );
        }
        else
        {
          configPanel.setIndividuals( false );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Der Lizenzstatus
      case ProjectConst.MESSAGE_LICENSE_STATE_READ:
        lg.info( "lizense state from SPX42 recived..." );
        currentConfig.setLicenseStatus( cmd );
        licenseState = currentConfig.getLicenseState();
        customConfig = currentConfig.getCustomEnabled();
        gasConfigPanel.setLicenseState( licenseState, customConfig );
        gasConfigPanel.setLicenseLabel( stringsBundle );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Versuche Verbindung mit Bluetooht Gerät
      case ProjectConst.MESSAGE_CONNECTING:
        lg.info( "CONNECTING..." );
        setElementsInactive( true );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Device wurde verbunden
      case ProjectConst.MESSAGE_CONNECTED:
        lg.info( "CONNECT" );
        setElementsConnected( true );
        // Gleich mal Fragen, wer da dran ist!
        btComm.askForDeviceName();
        btComm.askForSerialNumber();
        btComm.askForLicenseFromSPX();
        btComm.askForFirmwareVersion();
        connectionPanel.setAliasesEditable( false );
        connectionPanel.refreshAliasTable();
        gasConfigPanel.setPanelOnlineMode( true );
        // ware, bis die Nachricht FWVERSION_READ kommt, um das wartefenster zu schliessen
        waitForMessage = ProjectConst.MESSAGE_FWVERSION_READ;
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Device wurde getrennt
      case ProjectConst.MESSAGE_DISCONNECTED:
        lg.info( "DISCONNECT" );
        if( wDial != null )
        {
          wDial.dispose();
          wDial = null;
        }
        setElementsConnected( false );
        configPanel.setAllConfigPanlelsEnabled( false );
        gasConfigPanel.setPanelOnlineMode( false );
        connectionPanel.refreshAliasTable();
        connectionPanel.setAliasesEditable( true );
        if( tabbedPane.getSelectedIndex() != programTabs.TAB_CONNECT.ordinal() )
        {
          showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.connectionClosed" ) );
        }
        if( tabbedPane.getSelectedIndex() != programTabs.TAB_LOGGRAPH.ordinal() )
        {
          // wen nicht grad loggrafik angezeigt wird, auf den Connecttab wechseln
          tabbedPane.setSelectedIndex( programTabs.TAB_CONNECT.ordinal() );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Kein Gerät zum Verbinden gefunden!
      case ProjectConst.MESSAGE_BTNODEVCONN:
        lg.error( "no device found..." );
        showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notDeviceSelected.text" ) );
        setElementsConnected( false );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Lebenszeichen mit Ackuspannugn empfangen
      case ProjectConst.MESSAGE_SPXALIVE:
        lg.info( "acku value from spx42 recived..." );
        setAckuValue( cmd );
        if( savedConfig == null )
        {
          currentConfig.setWasInit( true );
        }
        savedConfig = new SPX42Config( currentConfig );
        configPanel.setAllConfigPanlelsEnabled( true );
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
        SPX42GasList currGasList = gasConfigPanel.getCurrGasList();
        if( currGasList == null )
        {
          lg.warn( "not alloacated gaslist in gasEditPanel yet!" );
          return;
        }
        // läßt sich das Teil parsen?
        if( currGasList.setGas( cmd ) )
        {
          // ist alle initialisiert?
          if( currGasList.isInitialized() )
          {
            // wenn die Gaskiste initialisiert ist
            gasConfigPanel.initGasesFromCurrent();
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
        lg.warn( "SPX42 switched SYNC OFF! Connetion will failure...." );
        // disconnect!
        btComm.disconnectDevice();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht Gase wurden erfolgreich geschrieben
      case ProjectConst.MESSAGE_GAS_WRITTEN:
        lg.debug( "gas written to SPX..." );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht ein Logbuch Verzeichniseintrag wurde gelesen
      case ProjectConst.MESSAGE_DIRENTRY_READ:
        lg.debug( "logdir entry recived..." );
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
      // Nachricht: Start einer Logdatenübermittling
      case ProjectConst.MESSAGE_LOGENTRY_START:
        lg.debug( "start transfer logentry <" + cmd + ">..." );
        logListPanel.startTransfer( cmd, currentConfig.getUnitSystem() );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Logzeile übertragen
      case ProjectConst.MESSAGE_LOGENTRY_LINE:
        lg.debug( "recive one log line from SPX..." );
        logListPanel.addLogLineFromSPX( cmd );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Logzeile übertragen
      case ProjectConst.MESSAGE_LOGENTRY_STOP:
        lg.debug( "logfile transfer done..." );
        // Ab auf die Platte ind die DB damit!
        logListPanel.writeCacheToDatabase();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Logdirectory aus Cache neu aufbauen
      case ProjectConst.MESSAGE_LOGDIRFROMCACHE:
        lg.debug( "log directory from cache rebuilding..." );
        logListPanel.addLogDirFromCache();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Daten gesichert....
      case ProjectConst.MESSAGE_DB_SUCCESS:
        lg.debug( "loglist transfer success..." );
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
              // Da sind keine Einträge mehr zu lesen. Mach ein Update der Logverzeichnisliste
              if( btComm != null )
              {
                if( btComm.isConnected() )
                {
                  // Kann ich vom Cache lesen?
                  if( logListPanel.canReadFromCache() )
                  {
                    // Baue die Liste mit dem Cache wieder auf
                    ev = new ActionEvent( this, ProjectConst.MESSAGE_LOGDIRFROMCACHE, "from_ache" );
                    actionPerformed( ev );
                  }
                  else
                  {
                    // lese die Liste der Logeinträge neu ein
                    if( logListPanel.prepareReadLogdir( btComm.getConnectedDevice() ) )
                    {
                      // Sag dem SPX er soll alles schicken
                      wDial = new PleaseWaitDialog( LangStrings.getString( "PleaseWaitDialog.title" ), LangStrings.getString( "PleaseWaitDialog.readLogDir" ) );
                      wDial.setVisible( true );
                      wDial.setTimeout( 120 * 1000 );
                      btComm.readLogDirectoryFromSPX();
                    }
                  }
                }
                else
                {
                  showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
                }
              }
            }
          }
          else
          {
            showWarnBox( LangStrings.getString( "MainCommGUI.warnDialog.notConnected.text" ) );
          }
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nachricht: Datenbankfehler....
      case ProjectConst.MESSAGE_DB_FAIL:
        lg.debug( "loglist transfer failed..." );
        // dann kann das fenster ja wech!
        if( wDial != null )
        {
          wDial.dispose();
          wDial = null;
        }
        if( cmd != null )
        {
          showErrorDialog( LangStrings.getString( "spx42LoglistPanel.logListLabel.text" ) + "\n" + cmd );
        }
        else
        {
          showErrorDialog( LangStrings.getString( "spx42LoglistPanel.logListLabel.text" ) );
        }
        logListPanel.removeFailedDataset();
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Der 10-Sekunden Ticker
      case ProjectConst.MESSAGE_TICK:
        if( SpxPcloggerProgramConfig.developDebug ) lg.debug( "TICK!" );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Ich soll mit BT Device direkt verbinden
      case ProjectConst.MESSAGE_CONNECTBTDEVICE:
        if( btComm != null )
        {
          waitForMessage = 0; // auf erst mal nix warten...
          connectSPX( cmd );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Ich soll mit BT Device direkt verbinden
      case ProjectConst.MESSAGE_CONNECTVIRTDEVICE:
        if( btComm != null )
        {
          waitForMessage = 0; // auf erst mal nix warten...
          connectVirtSPX( cmd );
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Trenne Verbindung
      case ProjectConst.MESSAGE_DISCONNECTBTDEVICE:
        if( btComm != null )
        {
          disconnectSPX();
        }
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Timeout bei Schreiben auf Gerät
      case ProjectConst.MESSAGE_COMMTIMEOUT:
        lg.error( "TIMEOUT (write to comm) recived! Disconnect!" );
        System.exit( -1 );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Firmware nicht unterstützt beim schreiben
      case ProjectConst.MESSAGE_FWNOTSUPPORTED:
        if( wDial != null )
        {
          wDial.dispose();
          wDial = null;
        }
        showErrorDialog( "Firmware not supported" );
        break;
      // /////////////////////////////////////////////////////////////////////////
      // Nichts traf zu....
      default:
        lg.warn( "unknown message recived!" );
        break;
    }
    return;
  }

  /**
   * Ackuwert des SPX anzeigen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.01.2012
   */
  private void setAckuValue( String vl )
  {
    lg.debug( "Value: <" + vl + ">" );
    double ackuValue = 0.0;
    Pattern fieldPatternDp = Pattern.compile( ":" );
    String[] fields = fieldPatternDp.split( vl );
    if( fields.length > 1 )
    {
      int val = Integer.parseInt( fields[1], 16 );
      ackuValue = ( float )( val / 100.0 );
      // Hauptfenster
      frmMainWindow.setTitle( LangStrings.getString( "MainCommGUI.frmMainwindowtitle.title" ) + " "
              + String.format( LangStrings.getString( "MainCommGUI.akkuLabel.text" ), ackuValue ) );
      lg.debug( String.format( "Acku value: %02.02f", ackuValue ) );
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
    ignoreAction = true;
    configPanel.setDecoGradientenPreset( currentPreset );
    ignoreAction = false;
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
    tabbedPane.setEnabledAt( programTabs.TAB_CONFIG.ordinal(), active );
    // tabbedPane.setEnabledAt( programTabs.TAB_GASLIST.ordinal(), active );
    tabbedPane.setEnabledAt( programTabs.TAB_LOGREAD.ordinal(), active );
    if( !active )
    {
      configPanel.setSerialNumber( "-" );
      configPanel.setFirmwareLabel( "-" );
      if( savedConfig != null )
      {
        savedConfig = null;
      }
      currentConfig.clear();
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
    tabbedPane.setEnabledAt( programTabs.TAB_CONFIG.ordinal(), active );
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
        configPanel.setDecoGradientenSpinner();
        configPanel.setDecoGradientenPreset( selectedIndex );
        ignoreAction = false;
        lg.debug( "spinner corrected for preset." );
        break;
    }
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
    lg.debug( "setLanguageStrings( ) START..." );
    try
    {
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.messages", programLocale );
      lg.debug( "setLanguageStrings( ) get stringsBundle OK......" );
    }
    catch( MissingResourceException ex )
    {
      lg.error( "setLanguageStrings( ) get stringsBundle ERROR! <" + ex.getMessage() + ">" );
      System.out.println( "ERROR get resources <" + ex.getMessage() + "> ABORT!" );
      return( -1 );
    }
    try
    {
      setStatus( "" );
      timeFormatterString = LangStrings.getString( "MainCommGUI.timeFormatterString" );
      // Hauptfenster
      frmMainWindow.setTitle( LangStrings.getString( "MainCommGUI.frmMainwindowtitle.title" ) );
      // Menü
      mnFile.setText( LangStrings.getString( "MainCommGUI.mnFile.text" ) );
      mnFile.setToolTipText( LangStrings.getString( "MainCommGUI.mnFile.tooltiptext" ) );
      mntmExit.setText( LangStrings.getString( "MainCommGUI.mntmExit.text" ) );
      mntmExit.setToolTipText( LangStrings.getString( "MainCommGUI.mntmExit.tooltiptext" ) );
      mnLanguages.setText( LangStrings.getString( "MainCommGUI.mnLanguages.text" ) );
      mnLanguages.setToolTipText( LangStrings.getString( "MainCommGUI.mnLanguages.tooltiptext" ) );
      mnOptions.setText( LangStrings.getString( "MainCommGUI.mnOptions.text" ) );
      mnOptions.setToolTipText( LangStrings.getString( "MainCommGUI.mnOptions.tooltiptext" ) );
      mntmOptions.setText( LangStrings.getString( "MainCommGUI.mntmOptions.text" ) );
      mntmOptions.setToolTipText( LangStrings.getString( "MainCommGUI.mntmOptions.tooltiptext" ) );
      mnHelp.setText( LangStrings.getString( "MainCommGUI.mnHelp.text" ) );
      mnHelp.setToolTipText( LangStrings.getString( "MainCommGUI.mnHelp.tooltiptext" ) );
      mntmHelp.setText( LangStrings.getString( "MainCommGUI.mntmHelp.text" ) );
      mntmHelp.setToolTipText( LangStrings.getString( "MainCommGUI.mntmHelp.tooltiptext" ) );
      mntmInfo.setText( LangStrings.getString( "MainCommGUI.mntmInfo.text" ) );
      mntmInfo.setToolTipText( LangStrings.getString( "MainCommGUI.mntmInfo.tooltiptext" ) );
      // //////////////////////////////////////////////////////////////////////
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Panes
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane connect
      tabbedPane.setTitleAt( programTabs.TAB_CONNECT.ordinal(), LangStrings.getString( "spx42ConnectPanel.title" ) );
      connectionPanel.setLanguageStrings( stringsBundle, btComm.isConnected() );
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane config
      tabbedPane.setTitleAt( programTabs.TAB_CONFIG.ordinal(), LangStrings.getString( "spx42ConfigPanel.title" ) );
      configPanel.setLanguageStrings();
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane gas
      tabbedPane.setTitleAt( programTabs.TAB_GASLIST.ordinal(), LangStrings.getString( "spx42GaslistEditPanel.title" ) );
      gasConfigPanel.setLanguageStrings( stringsBundle );
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane log
      tabbedPane.setTitleAt( programTabs.TAB_LOGREAD.ordinal(), LangStrings.getString( "spx42LoglistPanel.title" ) );
      logListPanel.setLanguageStrings( stringsBundle );
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane graph
      tabbedPane.setTitleAt( programTabs.TAB_LOGGRAPH.ordinal(), LangStrings.getString( "spx42LogGraphPanel.title" ) );
      logGraphPanel.setLanguageStrings( stringsBundle );
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane import/export
      tabbedPane.setTitleAt( programTabs.TAB_FILEMANAGER.ordinal(), LangStrings.getString( "fileManagerPanel.title" ) );
      fileManagerPanel.setLanguageStrings();
    }
    catch( NullPointerException ex )
    {
      lg.error( "setLanguageStrings( ) NULLPOINTER EXCEPTION <" + ex.getMessage() + ">" );
      statusTextField.setText( "ERROR set language strings" );
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( -1 );
    }
    catch( MissingResourceException ex )
    {
      lg.error( "setLanguageStrings( ) MISSING RESOURCE EXCEPTION <" + ex.getMessage() + ">" );
      statusTextField.setText( "ERROR set language strings - the given key can be found" );
      System.out.println( "ERROR set language strings - the given key can be found <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    catch( ClassCastException ex )
    {
      lg.error( "setLanguageStrings( ) CLASS CAST EXCEPTION <" + ex.getMessage() + ">" );
      statusTextField.setText( "ERROR set language strings" );
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    finally
    {
      ignoreAction = false;
    }
    lg.debug( "setLanguageStrings( ) END." );
    return( 1 );
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
   * Zeige eine Fehlermeldung mit Hinweistext ind Icon Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 25.06.2012
   * @param header
   * @param message
   */
  private void showErrorDialog( String message )
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Terminate.png" ) );
      JOptionPane.showMessageDialog( this, message, LangStrings.getString( "MainCommGUI.errorDialog.headline" ), JOptionPane.INFORMATION_MESSAGE, icon );
    }
    catch( NullPointerException ex )
    {
      statusTextField.setText( "ERROR showErrorBox" );
      lg.error( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      statusTextField.setText( "ERROR showErrorBox" );
      lg.error( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      statusTextField.setText( "ERROR showErrorBox" );
      lg.error( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
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
    new HelpFrameClass( programLocale );
  }

  /**
   * Zeige eine klein Info über das Proggi an Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui Project: SubmatixBTForPC Package:
   * de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.01.2012
   */
  private void showInfoDialog()
  {
    try
    {
      ProgramInfoDialog pDial = new ProgramInfoDialog();
      pDial.showDialog();
    }
    catch( NullPointerException ex )
    {
      statusTextField.setText( "ERROR showInfoDialog" );
      lg.error( "ERROR showInfoDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      statusTextField.setText( "ERROR showInfoDialog" );
      lg.error( "ERROR showInfoDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      statusTextField.setText( "ERROR showInfoDialog" );
      lg.error( "ERROR showInfoDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }

  /**
   * Zeige einen Optionendialog zur Einstellung von Programmgeschichten Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.07.2012
   */
  private void showPropertysDialog()
  {
    if( btComm != null )
    {
      if( btComm.isConnected() )
      {
        showErrorDialog( LangStrings.getString( "MainCommGUI.errorDialog.onlyNotConnected" ) );
        return;
      }
    }
    lg.debug( "create an show propertys dialog..." );
    ProgramProperetysDialog pDial = new ProgramProperetysDialog();
    // pDial.setVisible( true );
    if( pDial.showModal() )
    {
      lg.debug( "dialog whith OK closed...." );
      // progConfig = pDial.getProcConfig();
      if( SpxPcloggerProgramConfig.wasChanged )
      {
        // Wenn da was passieren sollte, muss die DB geschlossen sein.
        if( databaseUtil != null )
        {
          databaseUtil.closeDB();
        }
        showWarnBox( "RESTART PROGRAMM!" );
        pDial.dispose();
        exitProgram();
      }
      lg.debug( "dialog whith OK closed NO Changes...." );
    }
    else
    {
      lg.debug( "dialog canceled...." );
    }
    pDial.dispose();
    lg.debug( "dialog disposed..." );
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
      statusTextField.setText( "ERROR showWarnDialog" );
      lg.error( "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      statusTextField.setText( "ERROR showWarnDialog" );
      lg.error( "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      statusTextField.setText( "ERROR showWarnDialog" );
      lg.error( "ERROR showWarnDialog <" + ex.getMessage() + "> ABORT!" );
      return;
    }
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
      if( currSpinner.equals( configPanel.getDecoGradientenHighSpinner() ) )
      {
        // wert für High ändern
        currValue = ( Integer )currSpinner.getValue();
        lg.debug( String.format( "change decoGradientHighSpinner <%d/%x>...", currValue, currValue ) );
        currentConfig.setDecoGfHigh( currValue );
        setDecoComboAfterSpinnerChange();
      }
      // //////////////////////////////////////////////////////////////////////
      // Deco gradient Low
      else if( currSpinner.equals( configPanel.getDecoGradientenLowSpinner() ) )
      {
        // Wert für LOW ändern
        currValue = ( Integer )currSpinner.getValue();
        lg.debug( String.format( "change decoGradientLowSpinner <%d/%x>...", currValue, currValue ) );
        currentConfig.setDecoGfLow( currValue );
        setDecoComboAfterSpinnerChange();
      }
      else
      {
        lg.warn( "unknown spinner recived!" );
      }
    }
    // //////////////////////////////////////////////////////////////////////
    // war es ein tabbedPane
    // //////////////////////////////////////////////////////////////////////
    else if( ev.getSource() instanceof JTabbedPane )
    {
      if( tabbedPane.equals( ev.getSource() ) )
      {
        int tabIdx = tabbedPane.getSelectedIndex();
        lg.debug( String.format( "tabbedPane changed to %02d!", tabIdx ) );
        //
        // ist es das Grafikpanel?
        //
        if( tabIdx == programTabs.TAB_LOGGRAPH.ordinal() )
        {
          lg.debug( "graph tab select, init grapic..." );
          String connDev = null;
          if( btComm != null )
          {
            connDev = btComm.getConnectedDevice();
          }
          // Grafiksachen initialisieren
          try
          {
            logGraphPanel.initGraph( connDev );
          }
          catch( Exception ex )
          {
            lg.error( "initGraph Exception: <" + ex.getLocalizedMessage() + ">" );
            showErrorDialog( LangStrings.getString( "MainCommGUI.errorDialog.openGraphWindow" ) );
            return;
          }
        }
        else
        {
          // grafiksachen freigeben
          logGraphPanel.releaseGraph();
        }
        //
        // ist es das Exportpanel zum exportieren der Daten (importieren für Service)
        //
        if( tabIdx == programTabs.TAB_FILEMANAGER.ordinal() )
        {
          lg.debug( "export/import tab select, init db..." );
          String connDev = null;
          if( btComm != null )
          {
            connDev = btComm.getConnectedDevice();
          }
          // Panel initialisieren
          try
          {
            fileManagerPanel.initData( connDev );
          }
          catch( Exception ex )
          {
            lg.error( "initData Exception: <" + ex.getLocalizedMessage() + ">" );
            showErrorDialog( LangStrings.getString( "MainCommGUI.errorDialog.openExportWindow" ) );
            return;
          }
        }
        //
        // ist es das Config Panel?
        //
        if( tabIdx == programTabs.TAB_CONFIG.ordinal() )
        {
          lg.debug( "config tab select, init gui..." );
          configPanel.prepareConfigPanel( currentConfig );
        }
        else
        {
          // Daten freigeben
          configPanel.releaseConfig();
        }
        //
        // ist es das Loglistpanel
        //
        if( tabIdx == programTabs.TAB_LOGREAD.ordinal() )
        {
          // Panel initialisieren
          lg.debug( "logreader tab select, init gui..." );
          String connDev = null;
          if( btComm != null )
          {
            connDev = btComm.getConnectedDevice();
          }
          logListPanel.prepareLogListPanel( connDev );
        }
        else
        {
          logListPanel.releasePanel();
        }
        //
        // ist es das Gaspanel?
        //
        if( tabIdx == programTabs.TAB_GASLIST.ordinal() )
        {
          // Panel initialisieren
          lg.debug( "gaslist tab select, init gui..." );
          gasConfigPanel.prepareGasslistPanel();
        }
        else
        {
          // Panel Daten freigeben
          gasConfigPanel.releasePanel();
        }
      }
    }
    else
    {
      lg.warn( "unknown source type recived!" );
    }
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
    wDial = new PleaseWaitDialog( LangStrings.getString( "PleaseWaitDialog.title" ), LangStrings.getString( "PleaseWaitDialog.writeSpxConfig" ) );
    wDial.setMax( BTCommunication.CONFIG_WRITE_KDO_COUNT );
    wDial.resetProgress();
    wDial.setTimeout( 90 * 1000 );
    wDial.setVisible( true );
    lg.info( "write config to SPX42..." );
    btComm.writeConfigToSPX( currentConfig );
  }
}
