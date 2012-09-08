package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.joda.time.DateTime;

import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.LogDirListModel;
import de.dmarcini.submatix.pclogger.utils.LogLineDataObject;
import de.dmarcini.submatix.pclogger.utils.LogListCache;
import de.dmarcini.submatix.pclogger.utils.LogListCache.DataSave;

/**
 * 
 * Panel zeigt die Liste der Logeinträge an
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 22.04.2012
 */
public class spx42LoglistPanel extends JPanel implements ListSelectionListener
{
  /**
   * 
   */
  private static final long              serialVersionUID    = 1L;
  protected Logger                       LOGGER              = null;
  private ActionListener                 aListener           = null;
  private final HashMap<Integer, String> logdirFiles         = new HashMap<Integer, String>();
  private final HashMap<Integer, String> logdirReadable      = new HashMap<Integer, String>();
  private boolean                        isPanelInitiated    = false;
  private ResourceBundle                 stringsBundle       = null;
  private static final Pattern           fieldPatternSem     = Pattern.compile( ";" );
  private static final Pattern           fieldPatternDp      = Pattern.compile( ":" );
  private static final Pattern           fieldPatternDot     = Pattern.compile( "\\." );
  private static final Pattern           fieldPatternDtTm    = Pattern.compile( " - " );
  private static final Pattern           fieldPattern0x09    = Pattern.compile( ProjectConst.LOGSELECTOR );
  private boolean                        isDirectoryComplete = false;
  private boolean                        isNextLogAnUpdate   = false;
  private boolean                        shouldReadFromSpx   = true;
  private int                            nextDiveIdForUpdate = -1;
  private LogDerbyDatabaseUtil           databaseUtil        = null;
  private String                         deviceToLog         = null;
  private int                            currLogEntry        = -1;
  private Vector<Integer[]>              logListForRecive    = null;
  private int                            fileIndex           = -1;
  private LogListCache                   logListCache        = null;
  private JList                          logListField;
  private JButton                        readLogDirectoryButton;
  private JButton                        readLogfilesFromSPXButton;
  private JScrollPane                    logListScrollPane;
  private JLabel                         logListLabel;
  private JLabel                         fileNameLabel;
  private JLabel                         fileNameShowLabel;
  private JLabel                         diveDateLabel;
  private JLabel                         diveDateShowLabel;
  private JLabel                         diveTimeLabel;
  private JLabel                         diveTimeShowLabel;
  private JLabel                         diveMaxDepthLabel;
  private JLabel                         diveMaxDepthShowLabel;
  private JLabel                         diveLengthLabel;
  private JLabel                         diveLengthShowLabel;
  private JLabel                         logfileCommLabel;
  private JLabel                         diveLowTempLabel;
  private JLabel                         diveLowTempShowLabel;
  private String                         metricLength;
  private String                         metricTemperature;
  private String                         imperialLength;
  private String                         imperialTemperature;
  private String                         timeMinutes;
  private JLabel                         diveNotesLabel;
  private JLabel                         diveNotesShowLabel;

  /**
   * Create the panel.
   */
  @SuppressWarnings( "unused" )
  private spx42LoglistPanel()
  {
    setPreferredSize( new Dimension( 796, 504 ) );
    initPanel();
  }

  /**
   * 
   * Der Konstruktor, übergibt eine Log-Instanz mit
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param LOGGER
   * @param al
   * @param ldb
   */
  public spx42LoglistPanel( Logger LOGGER, ActionListener al, LogDerbyDatabaseUtil ldb )
  {
    this.LOGGER = LOGGER;
    this.aListener = al;
    // initPanel();
    databaseUtil = ldb;
    logdirFiles.clear();
    logdirReadable.clear();
    isDirectoryComplete = false;
    deviceToLog = null;
    fileIndex = -1;
    currLogEntry = -1;
    isNextLogAnUpdate = false;
    nextDiveIdForUpdate = -1;
    isPanelInitiated = false;
  }

  /**
   * 
   * Einen Eintrag in das Verzeichnis einfügen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param entryMsg
   * 
   */
  public void addLogdirEntry( String entryMsg )
  {
    // Message etwa so "Nummer;filename;readableName;maxNumber"
    String[] fields;
    String fileName, readableName, wasSaved = " ";
    int numberOnSpx, max, dbId = -1;
    //
    if( !isPanelInitiated ) return;
    //
    // Felder aufteilen
    fields = fieldPatternSem.split( entryMsg );
    if( fields.length < 4 )
    {
      LOGGER.log( Level.SEVERE, "recived message for logdir has lower than 4 fields. It is wrong! Abort!" );
      return;
    }
    // Wandel die Nummerierung in Integer um
    try
    {
      numberOnSpx = Integer.parseInt( fields[0] );
      max = Integer.parseInt( fields[3] );
    }
    catch( NumberFormatException ex )
    {
      LOGGER.log( Level.SEVERE, "Fail to convert Hex to int: " + ex.getLocalizedMessage() );
      return;
    }
    fileName = fields[1];
    // Der lesbare Teil
    readableName = fields[2];
    // Alles ging gut....
    if( numberOnSpx == max )
    {
      isDirectoryComplete = true;
      shouldReadFromSpx = false;
      return;
    }
    // Sichere die Dateiangabe
    logdirFiles.put( numberOnSpx, fileName );
    // Sichere Lesbares Format
    logdirReadable.put( numberOnSpx, readableName );
    // in die Liste einfügen
    if( databaseUtil != null )
    {
      dbId = databaseUtil.isLogSavedLog( fileName, deviceToLog );
      if( dbId != -1 )
      {
        wasSaved = "x";
      }
    }
    LOGGER.log( Level.FINE, "add to logdir number: <" + numberOnSpx + " " + wasSaved + "> name: <" + readableName + "> device: <" + deviceToLog + ">" );
    ( ( LogDirListModel )logListField.getModel() ).addLogentry( numberOnSpx, readableName, wasSaved, dbId );
    logListCache.addLogentry( numberOnSpx, readableName, fileName, dbId );
  }

  /**
   * 
   * Baue vom Cache aus auf!
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   */
  public void addLogDirFromCache()
  {
    LogDirListModel listMod = null;
    if( !isPanelInitiated ) return;
    if( logListCache == null ) return;
    if( deviceToLog == null ) return;
    LOGGER.log( Level.FINE, "read logdir from cache..." );
    // beginne mit leeren Datenhashes
    clearLogdirData( false );
    // hole mal die Liste
    Vector<DataSave> logList = logListCache.getLogList();
    // erzeuge einen iterator für die Liste
    Iterator<DataSave> iterator = logList.iterator();
    //
    // Alle Listeneinträge abarbeiten
    // Anzeigeliste löschen
    //
    listMod = new LogDirListModel();
    // ( ( LogDirListModel )logListField.getModel() ).clear();
    //
    // alle Cacheinträge bearbeiten
    //
    while( iterator.hasNext() )
    {
      int dbId = 0;
      String wasSaved = null;
      // Eintrag holen...
      DataSave entry = iterator.next();
      // Sichere die Dateiangabe
      logdirFiles.put( entry.numberOnSpx, entry.fileName );
      // Sichere Lesbares Format
      logdirReadable.put( entry.numberOnSpx, entry.readableName );
      if( databaseUtil != null )
      {
        dbId = databaseUtil.isLogSavedLog( entry.fileName, deviceToLog );
        if( dbId != -1 )
        {
          wasSaved = new String( "x" );
        }
        else
        {
          wasSaved = new String( " " );
        }
      }
      // in die Liste einfügen
      LOGGER.log( Level.FINE, "add to logdir number: <" + entry.numberOnSpx + " " + wasSaved + "> name: <" + entry.readableName + "> device: <" + deviceToLog + ">" );
      listMod.addLogentry( entry.numberOnSpx, entry.readableName, wasSaved, dbId );
      // ( ( LogDirListModel )logListField.getModel() ).addLogentry( entry.numberOnSpx, entry.readableName, wasSaved, dbId );
      // validate();
    }
    // Lesen ist beendet ;-)
    logListField.setModel( listMod );
    logListField.clearSelection();
    logListField.validate();
    isDirectoryComplete = true;
    LOGGER.log( Level.FINE, "read logdir from cache...OK" );
  }

  /**
   * 
   * Sichert eine weitere Zeile der Logdatei...
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 09.05.2012
   * @param logLine
   * @return ok oder nicht
   */
  public int addLogLineFromSPX( String logLine )
  {
    String fields[] = null;
    LogLineDataObject lineData = null;
    //
    if( !isPanelInitiated ) return( -1 );
    //
    if( fileIndex == -1 || currLogEntry < 0 )
    {
      LOGGER.log( Level.SEVERE, "not opened a file for reading via startTransfer()! ABORT" );
      return( -1 );
    }
    LOGGER.log( Level.FINE, "LINE: <" + logLine + ">..." );
    // teile die Logline in Felder auf
    fields = fieldPattern0x09.split( logLine );
    try
    {
      lineData = new LogLineDataObject();
      // die Felder in Werte für die Datenbank umrechnen...
      lineData.pressure = Integer.parseInt( fields[0].trim() );
      lineData.depth = Integer.parseInt( fields[1].trim() );
      lineData.temperature = Integer.parseInt( fields[2].trim() );
      lineData.acku = Double.parseDouble( fields[3].trim() );
      lineData.ppo2 = Double.parseDouble( fields[5].trim() );
      lineData.ppo2_1 = Double.parseDouble( fields[13].trim() );
      lineData.ppo2_2 = Double.parseDouble( fields[14].trim() );
      lineData.ppo2_3 = Double.parseDouble( fields[15].trim() );
      lineData.setpoint = Integer.parseInt( fields[6].trim() );
      lineData.n2 = Integer.parseInt( fields[16].trim() );
      lineData.he = Integer.parseInt( fields[17].trim() );
      lineData.zeroTime = Integer.parseInt( fields[20].trim() );
      lineData.nextStep = Integer.parseInt( fields[24].trim() );
    }
    catch( NumberFormatException ex )
    {
      LOGGER.log( Level.SEVERE, "error in converting numbers <" + ex.getLocalizedMessage() + ">" );
      return( -1 );
    }
    databaseUtil.appendLogToCacheLog( currLogEntry, lineData );
    return( 1 );
  }

  /**
   * 
   * kann/sollte ich vom Cache lesen?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.09.2012
   * @return vom Cache lesen
   */
  public boolean canReadFromCache()
  {
    return( !shouldReadFromSpx );
  }

  /**
   * 
   * Leere das Array für das Logverzeichnis des SPX (Cache)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param clearCache
   *          Verzeichniscache löschen
   */
  private void clearLogdirData( boolean clearCache )
  {
    if( !isPanelInitiated ) return;
    // Dateinamen auf dem SPX
    logdirFiles.clear();
    // Nummerierung auf dem SPX
    logdirReadable.clear();
    // Anzeigeliste
    ( ( LogDirListModel )logListField.getModel() ).clear();
    isDirectoryComplete = false;
    if( clearCache )
    {
      // Erzeuge einen neuen Cache
      logListCache = new LogListCache();
      shouldReadFromSpx = true;
    }
    diveDateShowLabel.setText( "-" );
    diveTimeShowLabel.setText( "-" );
  }

  /**
   * 
   * Gib die nächste Nummer eines zu lesenden Eintrages zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.05.2012
   * @return Nummer oder -1
   */
  public Integer[] getNextEntryToRead()
  {
    if( !isPanelInitiated ) return( null );
    // Ist die Liste (Vector) allociert?
    if( logListForRecive == null )
    {
      // Nein, nix zu tun!
      return( null );
    }
    // Sind Elemente vorhanden?
    if( logListForRecive.isEmpty() )
    {
      // Liste ist Leer, nix zu tun
      logListForRecive = null;
      return( null );
    }
    // den ersten Eintrag zurückgeben
    return( logListForRecive.remove( 0 ) );
  }

  /**
   * 
   * Initialisiert die GUI des Panels
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   */
  private void initPanel()
  {
    setLayout( null );
    logListScrollPane = new JScrollPane();
    logListScrollPane.setBounds( 10, 31, 248, 455 );
    add( logListScrollPane );
    logListField = new JList();
    logListField.setForeground( Color.BLUE );
    logListField.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
    logListField.setModel( new LogDirListModel() );
    logListScrollPane.setViewportView( logListField );
    readLogDirectoryButton = new JButton( "READDIR" );
    readLogDirectoryButton.setIcon( new ImageIcon( spx42LoglistPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/109.png" ) ) );
    readLogDirectoryButton.setPreferredSize( new Dimension( 180, 40 ) );
    readLogDirectoryButton.setMaximumSize( new Dimension( 160, 40 ) );
    readLogDirectoryButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    readLogDirectoryButton.setForeground( new Color( 0, 100, 0 ) );
    readLogDirectoryButton.setBackground( new Color( 152, 251, 152 ) );
    readLogDirectoryButton.setActionCommand( "read_logdir_from_spx" );
    readLogDirectoryButton.setBounds( 560, 11, 199, 60 );
    add( readLogDirectoryButton );
    readLogfilesFromSPXButton = new JButton( "READLOGS" );
    readLogfilesFromSPXButton.setIcon( new ImageIcon( spx42LoglistPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Down.png" ) ) );
    readLogfilesFromSPXButton.setPreferredSize( new Dimension( 180, 40 ) );
    readLogfilesFromSPXButton.setMaximumSize( new Dimension( 160, 40 ) );
    readLogfilesFromSPXButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    readLogfilesFromSPXButton.setForeground( Color.BLUE );
    readLogfilesFromSPXButton.setBackground( new Color( 153, 255, 255 ) );
    readLogfilesFromSPXButton.setActionCommand( "read_logfile_from_spx" );
    readLogfilesFromSPXButton.setBounds( 560, 83, 199, 60 );
    add( readLogfilesFromSPXButton );
    logListLabel = new JLabel( "LOGLIST" );
    logListLabel.setLabelFor( logListScrollPane );
    logListLabel.setHorizontalAlignment( SwingConstants.LEFT );
    logListLabel.setBounds( 10, 11, 248, 14 );
    add( logListLabel );
    fileNameLabel = new JLabel( "FILENAME" );
    fileNameLabel.setForeground( new Color( 128, 128, 128 ) );
    fileNameLabel.setBounds( 268, 31, 282, 14 );
    add( fileNameLabel );
    fileNameShowLabel = new JLabel( "-" );
    fileNameShowLabel.setForeground( new Color( 0, 0, 139 ) );
    fileNameLabel.setLabelFor( fileNameShowLabel );
    fileNameShowLabel.setBounds( 268, 46, 282, 14 );
    add( fileNameShowLabel );
    diveDateLabel = new JLabel( "DIVEDATE" );
    diveDateLabel.setForeground( new Color( 128, 128, 128 ) );
    diveDateLabel.setBounds( 268, 69, 282, 14 );
    add( diveDateLabel );
    diveDateShowLabel = new JLabel( "-" );
    diveDateShowLabel.setForeground( new Color( 0, 0, 139 ) );
    diveDateLabel.setLabelFor( diveDateShowLabel );
    diveDateShowLabel.setBounds( 268, 83, 282, 14 );
    add( diveDateShowLabel );
    diveTimeLabel = new JLabel( "DIVETIME" );
    diveTimeLabel.setForeground( new Color( 128, 128, 128 ) );
    diveTimeLabel.setBounds( 268, 106, 282, 14 );
    add( diveTimeLabel );
    diveTimeShowLabel = new JLabel( "-" );
    diveTimeShowLabel.setForeground( new Color( 0, 0, 139 ) );
    diveTimeLabel.setLabelFor( diveTimeShowLabel );
    diveTimeShowLabel.setBounds( 268, 119, 282, 14 );
    add( diveTimeShowLabel );
    diveMaxDepthLabel = new JLabel( "DIVEMAXDEPTH" );
    diveMaxDepthLabel.setForeground( new Color( 128, 128, 128 ) );
    diveMaxDepthLabel.setBounds( 268, 144, 282, 14 );
    add( diveMaxDepthLabel );
    diveMaxDepthShowLabel = new JLabel( "-" );
    diveMaxDepthShowLabel.setForeground( new Color( 0, 0, 139 ) );
    diveMaxDepthLabel.setLabelFor( diveMaxDepthShowLabel );
    diveMaxDepthShowLabel.setBounds( 268, 158, 282, 14 );
    add( diveMaxDepthShowLabel );
    diveLengthLabel = new JLabel( "DIVELENGTH" );
    diveLengthLabel.setForeground( new Color( 128, 128, 128 ) );
    diveLengthLabel.setBounds( 268, 183, 282, 14 );
    add( diveLengthLabel );
    diveLengthShowLabel = new JLabel( "-" );
    diveLengthShowLabel.setForeground( new Color( 0, 0, 139 ) );
    diveLengthShowLabel.setBounds( 268, 196, 282, 14 );
    add( diveLengthShowLabel );
    logfileCommLabel = new JLabel( "SAVINGLABEL" );
    logfileCommLabel.setBounds( 267, 472, 492, 14 );
    add( logfileCommLabel );
    diveLowTempLabel = new JLabel( "DIVELOWTEMP" );
    diveLowTempLabel.setForeground( new Color( 128, 128, 128 ) );
    diveLowTempLabel.setBounds( 268, 220, 282, 14 );
    add( diveLowTempLabel );
    diveLowTempShowLabel = new JLabel( "-" );
    diveLowTempShowLabel.setForeground( new Color( 0, 0, 139 ) );
    diveLowTempShowLabel.setBounds( 268, 235, 282, 14 );
    add( diveLowTempShowLabel );
    diveNotesLabel = new JLabel( "DIVENOTES" );
    diveNotesLabel.setForeground( new Color( 128, 128, 128 ) );
    diveNotesLabel.setBounds( 268, 260, 282, 14 );
    add( diveNotesLabel );
    diveNotesShowLabel = new JLabel( "-" );
    diveNotesShowLabel.setForeground( new Color( 0, 128, 0 ) );
    diveNotesShowLabel.setBounds( 268, 275, 492, 14 );
    add( diveNotesShowLabel );
    logfileCommLabel.setVisible( false );
  }

  /**
   * 
   * Lesen vollständig?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @return Lesen vollständig opder nicht
   */
  public boolean isReadingComplete()
  {
    return( isDirectoryComplete );
  }

  /**
   * 
   * Vorbereitungen zum download der Logdaten treffen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 06.05.2012
   * @param device
   * @return Array von Logdateinummern (Nummer des Logs auf dem SPX42)
   */
  public int prepareDownloadLogdata( String device )
  {
    int[] logSelected = null;
    deviceToLog = device;
    if( deviceToLog == null )
    {
      return( 0 );
    }
    if( !isPanelInitiated ) return( -1 );
    LOGGER.log( Level.FINE, "prepare to download logdata..." );
    //
    // Ok, das sieht so aus, als könne es losgehen
    //
    LOGGER.log( Level.FINE, "test for selected logentrys..." );
    logSelected = logListField.getSelectedIndices();
    if( logSelected.length > 0 )
    {
      // es ist auch etwas markiert!
      // Array erzeugen
      logListForRecive = new Vector<Integer[]>();
      // für jeden markierten index die Lognummer holen
      for( int idx = 0; idx < logSelected.length; idx++ )
      {
        Integer[] lEntry = new Integer[2];
        lEntry[0] = ( ( LogDirListModel )logListField.getModel() ).getLognumberAt( logSelected[idx] );
        lEntry[1] = ( ( LogDirListModel )logListField.getModel() ).istInDb( logSelected[idx] ) ? 1 : 0;
        logListForRecive.add( lEntry );
        LOGGER.log( Level.FINE, "select dive number <" + logSelected[idx] + "> for download..." );
      }
      return( logListForRecive.size() );
    }
    // Es ist nichts markiert
    LOGGER.log( Level.WARNING, "prepare to download logdata...NOTHING selected!" );
    return( 0 );
  }

  /**
   * 
   * Das Panel zur Anzeige bereit machen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   * @param connDev
   */
  public void prepareLogListPanel( String connDev )
  {
    initPanel();
    logdirFiles.clear();
    logdirReadable.clear();
    isDirectoryComplete = false;
    deviceToLog = connDev;
    fileIndex = -1;
    currLogEntry = -1;
    isNextLogAnUpdate = false;
    nextDiveIdForUpdate = -1;
    isPanelInitiated = true;
    setLanguageStrings( stringsBundle );
    setGlobalChangeListener( ( MainCommGUI )aListener );
  }

  /**
   * 
   * Bereite das Lesen des Logverzeichnisses vor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.05.2012
   * @param device
   * @return In Ordnung
   */
  public boolean prepareReadLogdir( String device )
  {
    if( !isPanelInitiated ) return( false );
    // die Voreinstellung:
    shouldReadFromSpx = true;
    // Kein Gerät, keine Aktion
    if( device == null )
    {
      return( false );
    }
    // welches Gerät loggen wir
    deviceToLog = device;
    // ich soll direkt vom SPX lesen!
    clearLogdirData( true );
    LOGGER.log( Level.FINE, "prepare to read logdir..." );
    return( true );
  }

  /**
   * 
   * Das Panel entfernen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.09.2012
   */
  public void releasePanel()
  {
    isPanelInitiated = false;
    this.removeAll();
    logdirFiles.clear();
    logdirReadable.clear();
    deviceToLog = null;
    fileIndex = -1;
    currLogEntry = -1;
    isNextLogAnUpdate = false;
    nextDiveIdForUpdate = -1;
  }

  /**
   * 
   * Wenn Datenbankfehler auftraten, Datenreste entfernen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.06.2012
   * @return ging oder Fehler
   */
  public int removeFailedDataset()
  {
    return( databaseUtil.deleteLogFromDatabeaseLog() );
  }

  /**
   * 
   * Elemente, die je nach Status erlaubt oder ausgeblendet werden sollen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param en
   */
  public void setAllLogPanelsEnabled( boolean en )
  {
    if( !isPanelInitiated ) return;
    logListScrollPane.setEnabled( en );
  }

  /**
   * 
   * Alle Listener setzen, die benötigt werden
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param mainCommGUI
   */
  public void setGlobalChangeListener( MainCommGUI mainCommGUI )
  {
    this.aListener = mainCommGUI;
    if( !isPanelInitiated ) return;
    readLogDirectoryButton.addActionListener( mainCommGUI );
    readLogDirectoryButton.addMouseMotionListener( mainCommGUI );
    readLogfilesFromSPXButton.addActionListener( mainCommGUI );
    readLogfilesFromSPXButton.addMouseMotionListener( mainCommGUI );
    logListField.addMouseMotionListener( mainCommGUI );
    logListField.addListSelectionListener( this );
  }

  /**
   * 
   * Die Sprache anpassen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 05.05.2012
   * @param stringsBundle
   * @return -1 Fehler, 0 naja, 1 alles OK
   */
  public int setLanguageStrings( ResourceBundle stringsBundle )
  {
    this.stringsBundle = stringsBundle;
    if( !isPanelInitiated ) return( 1 );
    try
    {
      clearLogdirData( true );
      logListLabel.setText( stringsBundle.getString( "spx42LoglistPanel.logListLabel.text" ) );
      readLogDirectoryButton.setText( stringsBundle.getString( "spx42LoglistPanel.readLogDirectoryButton.text" ) );
      readLogDirectoryButton.setToolTipText( stringsBundle.getString( "spx42LoglistPanel.readLogDirectoryButton.tooltiptext" ) );
      readLogfilesFromSPXButton.setText( stringsBundle.getString( "spx42LoglistPanel.readLogfilesFromSPXButton.text" ) );
      readLogfilesFromSPXButton.setToolTipText( stringsBundle.getString( "spx42LoglistPanel.readLogfilesFromSPXButton.tooltiptext" ) );
      logListField.setToolTipText( stringsBundle.getString( "spx42LoglistPanel.logListField.tooltiptext" ) );
      fileNameLabel.setText( stringsBundle.getString( "spx42LoglistPanel.fileNameLabel.text" ) );
      diveDateLabel.setText( stringsBundle.getString( "spx42LoglistPanel.diveDateLabel.text" ) );
      diveTimeLabel.setText( stringsBundle.getString( "spx42LoglistPanel.diveTimeLabel.text" ) );
      diveMaxDepthLabel.setText( stringsBundle.getString( "spx42LoglistPanel.diveMaxDepthLabel.text" ) );
      diveLengthLabel.setText( stringsBundle.getString( "spx42LoglistPanel.diveLengthLabel.text" ) );
      diveLowTempLabel.setText( stringsBundle.getString( "spx42LoglistPanel.diveLowTempLabel.text" ) );
      diveNotesLabel.setText( stringsBundle.getString( "spx42LoglistPanel.diveNotesLabel.text" ) );
      metricLength = stringsBundle.getString( "spx42LoglistPanel.unit.metric.length" );
      metricTemperature = stringsBundle.getString( "spx42LoglistPanel.unit.metric.temperature" );
      imperialLength = stringsBundle.getString( "spx42LoglistPanel.unit.imperial.length" );
      imperialTemperature = stringsBundle.getString( "spx42LoglistPanel.unit.imperial.temperature" );
      timeMinutes = stringsBundle.getString( "spx42LoglistPanel.unit.minutes" );
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

  /**
   * 
   * Ist der nächste Start eines Logs ein Update?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 12.07.2012
   * @param isUpdate
   * @return ja oder nein
   */
  boolean setNextLogIsAnUpdate( boolean isUpdate, int diveId )
  {
    isNextLogAnUpdate = isUpdate;
    nextDiveIdForUpdate = diveId;
    return( isNextLogAnUpdate );
  }

  /**
   * 
   * Vorbereiten: Es kommt ein Logfile mit der Indexnummer aus "fileNumberStr"
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 13.05.2012
   * @param fileNumberStr
   *          Indexnummer aus der Liste im Panel
   * @param unitSystem
   *          metrisch oder imperial
   */
  public void startTransfer( String fileNumberStr, int unitSystem )
  {
    String fileName, diveDate, diveTime;
    String[] fields;
    DateTime dateTime;
    int year, month, day, hour, minute, second;
    int diveId;
    if( !isPanelInitiated ) return;
    //
    // Datenbank bereit für mich?
    //
    if( databaseUtil == null )
    {
      LOGGER.log( Level.SEVERE, "logDatabaseUtil not allocated!" );
      fileIndex = -1;
      return;
    }
    try
    {
      // Die Nummer des Logeintrages auf dem SPX
      fileIndex = Integer.parseInt( fileNumberStr, 16 );
    }
    catch( NumberFormatException ex )
    {
      LOGGER.log( Level.SEVERE, "wrong filenumber in String: <" + ex.getLocalizedMessage() + ">" );
      fileIndex = -1;
      return;
    }
    //
    // soll ein Update gemacht werden?
    //
    if( isNextLogAnUpdate )
    {
      currLogEntry = nextDiveIdForUpdate;
      nextDiveIdForUpdate = -1;
      isNextLogAnUpdate = false;
      // Logdaten aus der DB entfernen
      databaseUtil.removeLogdataForIdLog( currLogEntry );
      databaseUtil.allocateCacheLog( currLogEntry );
    }
    else
    {
      // Kein Update....
      // So, ich habe einen Index. Ich brauche für die Datenbank informationen
      // Dateinamen, Geräteid
      // bekomme ich aus dem hash logdirFiles
      fileName = logdirFiles.get( fileIndex );
      // Aus der Anzeige Datum und Zeitstring trennen
      fields = fieldPatternDtTm.split( logdirReadable.get( fileIndex ) );
      if( fields.length == 2 )
      {
        diveDate = fields[0];
        diveTime = fields[1];
      }
      else
      {
        LOGGER.log( Level.SEVERE, "internal format error while decoding dive date and time from filename!" );
        fileIndex = -1;
        return;
      }
      // Jetzt die Zeitangaben parsen
      try
      {
        // Datum splitten
        fields = fieldPatternDot.split( diveDate );
        year = Integer.parseInt( fields[2] );
        month = Integer.parseInt( fields[1] );
        day = Integer.parseInt( fields[0] );
        // zeit splitten
        fields = fieldPatternDp.split( diveTime );
        hour = Integer.parseInt( fields[0] );
        minute = Integer.parseInt( fields[1] );
        second = Integer.parseInt( fields[2] );
        // Zeit erzeugen
        dateTime = new DateTime( year, month, day, hour, minute, second );
      }
      catch( NumberFormatException ex )
      {
        LOGGER.log( Level.SEVERE, "internal format error while decoding dive date and time!" );
        fileIndex = -1;
        return;
      }
      // Ersten Eintrag für den Tauchgang machen
      diveId = databaseUtil.writeNewDiveLog( deviceToLog, fileName, unitSystem, fileIndex, ( dateTime.getMillis() ) / 1000 );
      if( diveId < 0 )
      {
        fileIndex = -1;
        currLogEntry = -1;
        return;
      }
      currLogEntry = diveId;
    }
  }

  @Override
  public void valueChanged( ListSelectionEvent ev )
  {
    // Wen die Selektion der Liste verändert wurde...
    int fIndex, spxNumber, dbId;
    String[] fields;
    //
    if( ev.getSource().equals( logListField ) )
    {
      if( !ev.getValueIsAdjusting() )
      {
        // Das Ende der Serie, jetzt guck ich mal nach der ersten markierten...
        LOGGER.log( Level.FINE, "ist last or once change..." );
        fIndex = logListField.getSelectedIndex();
        LOGGER.log( Level.FINE, String.format( "first selected Index: %d ", fIndex ) );
        spxNumber = ( ( LogDirListModel )logListField.getModel() ).getLognumberAt( fIndex );
        if( spxNumber == -1 )
        {
          diveDateShowLabel.setText( "-" );
          diveTimeShowLabel.setText( "-" );
          return;
        }
        dbId = ( ( LogDirListModel )logListField.getModel() ).getDbIdAt( fIndex );
        LOGGER.log(
                Level.FINE,
                String.format( "number on SPX: %d, DBID: %d, readable Name: %s, filename: %s", spxNumber, dbId,
                        ( ( LogDirListModel )logListField.getModel() ).getLogNameAt( fIndex ), logdirFiles.get( spxNumber ) ) );
        // erst mal die allgemeinen Daten des Dives anzeigen
        fileNameShowLabel.setText( logdirFiles.get( spxNumber ) );
        // Aus der Anzeige Datum und Zeitstring trennen
        fields = fieldPatternDtTm.split( logdirReadable.get( spxNumber ) );
        if( fields.length == 2 )
        {
          diveDateShowLabel.setText( fields[0] );
          diveTimeShowLabel.setText( fields[1] );
        }
        else
        {
          diveDateShowLabel.setText( "??" );
          diveTimeShowLabel.setText( "??" );
        }
        // Jetzt schau ich mal, ob da was in der Datenbank zu finden ist
        // Ja, der ist in der Datenbank erfasst!
        String[] headers = databaseUtil.getHeadDiveDataFromIdAsSTringLog( dbId );
        if( headers != null )
        {
          diveNotesShowLabel.setText( databaseUtil.getNotesForIdLog( dbId ) );
          if( headers[11].equals( "METRIC" ) )
          {
            // Maximale Tiefe anzeigen
            diveMaxDepthShowLabel.setText( String.format( "%s %s", headers[8], metricLength ) );
            // kälteste Temperatur anzeigen
            diveLowTempShowLabel.setText( String.format( "%s %s", headers[7], metricTemperature ) );
          }
          else
          {
            // Maximale Tiefe anzeigen
            diveMaxDepthShowLabel.setText( String.format( "%s %s", headers[8], imperialLength ) );
            // kälteste Temperatur anzeigen
            diveLowTempShowLabel.setText( String.format( "%s %s", headers[7], imperialTemperature ) );
          }
          // Länge des Tauchgangs anzeigen
          diveLengthShowLabel.setText( String.format( "%s %s", headers[10], timeMinutes ) );
        }
        else
        {
          diveMaxDepthShowLabel.setText( "-" );
          diveLengthShowLabel.setText( "-" );
          diveLowTempShowLabel.setText( "-" );
          diveNotesShowLabel.setText( "-" );
        }
      }
    }
  }

  /**
   * 
   * Den Cache (der im Datenbankobjekt vorgehalten wird) nun zur Datenbank schreiben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 17.06.2012
   * @return ok oder nicht
   */
  public int writeCacheToDatabase()
  {
    int ret = databaseUtil.writeLogToDatabaseLog( currLogEntry );
    currLogEntry = -1;
    return( ret );
  }
}
