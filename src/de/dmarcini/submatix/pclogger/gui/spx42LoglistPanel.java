package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
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
import de.dmarcini.submatix.pclogger.utils.LogForDeviceDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.LogLineDataObject;
import de.dmarcini.submatix.pclogger.utils.LogdirListModel;

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
  private File                           dataDir             = null;
  private static final Pattern           fieldPatternSem     = Pattern.compile( ";" );
  private static final Pattern           fieldPatternDp      = Pattern.compile( ":" );
  private static final Pattern           fieldPatternDot     = Pattern.compile( "\\." );
  private static final Pattern           fieldPatternDtTm    = Pattern.compile( " - " );
  private static final Pattern           fieldPattern0x09    = Pattern.compile( ProjectConst.LOGSELECTOR );
  private final LogdirListModel          logListModel        = new LogdirListModel();
  private boolean                        isDirectoryComplete = false;
  private boolean                        isNextLogAnUpdate   = false;
  private int                            nextDiveIdForUpdate = -1;
  private LogForDeviceDatabaseUtil       logDatabaseUtil     = null;
  private String                         deviceToLog         = null;
  private int                            currLogEntry        = -1;
  private Vector<Integer[]>              logList             = null;
  private int                            fileIndex           = -1;
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

  /**
   * Create the panel.
   */
  @SuppressWarnings( "unused" )
  private spx42LoglistPanel()
  {
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
   * @param dataDir
   */
  public spx42LoglistPanel( Logger LOGGER, ActionListener al, String dataDir )
  {
    this.LOGGER = LOGGER;
    this.aListener = al;
    initPanel();
    logdirFiles.clear();
    logdirReadable.clear();
    isDirectoryComplete = false;
    logDatabaseUtil = null;
    deviceToLog = null;
    fileIndex = -1;
    currLogEntry = -1;
    this.dataDir = new File( dataDir );
    isNextLogAnUpdate = false;
    nextDiveIdForUpdate = -1;
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
    logListField.setModel( logListModel );
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
    fileNameLabel.setForeground( Color.DARK_GRAY );
    fileNameLabel.setBounds( 268, 31, 282, 14 );
    add( fileNameLabel );
    fileNameShowLabel = new JLabel( "-" );
    fileNameShowLabel.setForeground( new Color( 0, 0, 139 ) );
    fileNameLabel.setLabelFor( fileNameShowLabel );
    fileNameShowLabel.setBounds( 268, 57, 282, 14 );
    add( fileNameShowLabel );
    diveDateLabel = new JLabel( "DIVEDATE" );
    diveDateLabel.setForeground( Color.DARK_GRAY );
    diveDateLabel.setBounds( 268, 90, 282, 14 );
    add( diveDateLabel );
    diveDateShowLabel = new JLabel( "-" );
    diveDateShowLabel.setForeground( new Color( 0, 0, 139 ) );
    diveDateLabel.setLabelFor( diveDateShowLabel );
    diveDateShowLabel.setBounds( 268, 106, 282, 14 );
    add( diveDateShowLabel );
    diveTimeLabel = new JLabel( "DIVETIME" );
    diveTimeLabel.setForeground( Color.DARK_GRAY );
    diveTimeLabel.setBounds( 268, 131, 282, 14 );
    add( diveTimeLabel );
    diveTimeShowLabel = new JLabel( "-" );
    diveTimeShowLabel.setForeground( new Color( 0, 0, 139 ) );
    diveTimeLabel.setLabelFor( diveTimeShowLabel );
    diveTimeShowLabel.setBounds( 268, 146, 282, 14 );
    add( diveTimeShowLabel );
    diveMaxDepthLabel = new JLabel( "DIVEMAXDEPTH" );
    diveMaxDepthLabel.setForeground( Color.DARK_GRAY );
    diveMaxDepthLabel.setBounds( 268, 181, 282, 14 );
    add( diveMaxDepthLabel );
    diveMaxDepthShowLabel = new JLabel( "-" );
    diveMaxDepthShowLabel.setForeground( new Color( 0, 0, 139 ) );
    diveMaxDepthLabel.setLabelFor( diveMaxDepthShowLabel );
    diveMaxDepthShowLabel.setBounds( 268, 195, 282, 14 );
    add( diveMaxDepthShowLabel );
    diveLengthLabel = new JLabel( "DIVELENGTH" );
    diveLengthLabel.setForeground( Color.DARK_GRAY );
    diveLengthLabel.setBounds( 268, 220, 282, 14 );
    add( diveLengthLabel );
    diveLengthShowLabel = new JLabel( "-" );
    diveLengthShowLabel.setForeground( new Color( 0, 0, 139 ) );
    diveLengthShowLabel.setBounds( 268, 236, 282, 14 );
    add( diveLengthShowLabel );
    logfileCommLabel = new JLabel( "SAVINGLABEL" );
    logfileCommLabel.setBounds( 267, 472, 492, 14 );
    add( logfileCommLabel );
    logfileCommLabel.setVisible( false );
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
    try
    {
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
    logListScrollPane.setEnabled( en );
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
   */
  public void clearLogdirCache()
  {
    // Dateinamen auf dem SPX
    logdirFiles.clear();
    // Nummerierung auf dem SPX
    logdirReadable.clear();
    logListModel.clear();
    isDirectoryComplete = false;
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
    deviceToLog = device;
    if( deviceToLog == null )
    {
      return( false );
    }
    LOGGER.log( Level.FINE, "prepare to read logdir..." );
    // wenn da noch was ist, entferne
    if( logDatabaseUtil != null )
    {
      if( logDatabaseUtil.isOpenDB() )
      {
        logDatabaseUtil.closeDB();
      }
      logDatabaseUtil = null;
    }
    // das Datenbankutility initialisieren
    logDatabaseUtil = new LogForDeviceDatabaseUtil( LOGGER, aListener, deviceToLog, dataDir.getAbsolutePath() );
    if( logDatabaseUtil.createConnection() == null )
    {
      return( false );
    }
    return( true );
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
    int number, max;
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
      number = Integer.parseInt( fields[0] );
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
    if( number == max )
    {
      isDirectoryComplete = true;
      return;
    }
    LOGGER.log( Level.FINE, "add to logdir number: <" + number + "> name: <" + readableName + ">" );
    // Sichere die Dateiangabe
    logdirFiles.put( number, fileName );
    // Sichere Lesbares Format
    logdirReadable.put( number, readableName );
    // in die Liste einfügen
    if( logDatabaseUtil != null )
    {
      if( logDatabaseUtil.isLogSaved( fileName ) )
      {
        wasSaved = "x";
      }
    }
    logListModel.addLogentry( number, readableName, wasSaved );
  }

  @Override
  public void valueChanged( ListSelectionEvent ev )
  {
    // Wen die Selektion der Liste verändert wurde...
    int fIndex, spxNumber;
    String[] fields;
    //
    if( ev.getSource().equals( logListField ) )
    {
      if( !ev.getValueIsAdjusting() )
      {
        // Das Ende der Serie, jetzt guck ich mal nach der ersten markierten...//
        LOGGER.log( Level.FINE, "ist last or once change..." );
        fIndex = logListField.getSelectedIndex();
        LOGGER.log( Level.FINE, String.format( "first selected Index: %d ", fIndex ) );
        spxNumber = logListModel.getLognumberAt( fIndex );
        if( spxNumber == -1 )
        {
          cleanDetails();
          return;
        }
        LOGGER.log( Level.FINE, String.format( "number on SPX: %d, readable Name: %s, filename: %s", spxNumber, logListModel.getLogNameAt( fIndex ), logdirFiles.get( spxNumber ) ) );
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
      }
    }
  }

  /**
   * 
   * Die Details für einen TG löschen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 06.05.2012
   */
  public void cleanDetails()
  {
    diveDateShowLabel.setText( "-" );
    diveTimeShowLabel.setText( "-" );
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
    LOGGER.log( Level.FINE, "prepare to download logdata..." );
    // wen da noch was ist, entfernen
    if( logDatabaseUtil != null )
    {
      if( logDatabaseUtil.isOpenDB() )
      {
        logDatabaseUtil.closeDB();
      }
      logDatabaseUtil = null;
    }
    // das Datenbankutility initialisieren
    logDatabaseUtil = new LogForDeviceDatabaseUtil( LOGGER, aListener, deviceToLog, dataDir.getAbsolutePath() );
    if( logDatabaseUtil.createConnection() == null )
    {
      return( 0 );
    }
    //
    // Ok, das sieht so aus, als könne es losgehen
    //
    LOGGER.log( Level.FINE, "test for selected logentrys..." );
    logSelected = logListField.getSelectedIndices();
    if( logSelected.length > 0 )
    {
      // es ist auch etwas markiert!
      // Array erzeugen
      logList = new Vector<Integer[]>();
      // für jeden markierten index die Lognummer holen
      for( int idx = 0; idx < logSelected.length; idx++ )
      {
        Integer[] lEntry = new Integer[2];
        lEntry[0] = logListModel.getLognumberAt( logSelected[idx] );
        lEntry[1] = logListModel.istInDb( logSelected[idx] ) ? 1 : 0;
        logList.add( lEntry );
        LOGGER.log( Level.FINE, "select dive number <" + logSelected[idx] + "> for download..." );
      }
      return( logList.size() );
    }
    // Es ist nichts markiert
    LOGGER.log( Level.WARNING, "prepare to download logdata...NOTHING selected!" );
    return( 0 );
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
    // Ist die Liste (Vector) allociert?
    if( logList == null )
    {
      // Nein, nix zu tun!
      return( null );
    }
    // Sind Elemente vorhanden?
    if( logList.isEmpty() )
    {
      // Liste ist Leer, nic zu tun
      logList = null;
      return( null );
    }
    // den ersten Eintrag zurückgeben
    return( logList.remove( 0 ) );
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
   */
  public void startTransfer( String fileNumberStr )
  {
    String fileName, diveDate, diveTime;
    String[] fields;
    DateTime dateTime;
    int year, month, day, hour, minute, second;
    int diveId;
    //
    // Datenbank bereit für mich?
    //
    if( logDatabaseUtil == null )
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
      logDatabaseUtil.removeLogdataForId( currLogEntry );
      logDatabaseUtil.allocateCache( currLogEntry );
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
      diveId = logDatabaseUtil.writeNewDive( deviceToLog, fileName, fileIndex, ( dateTime.getMillis() ) / 1000 );
      if( diveId < 0 )
      {
        fileIndex = -1;
        currLogEntry = -1;
        return;
      }
      currLogEntry = diveId;
    }
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
    if( fileIndex == -1 || currLogEntry < 0 )
    {
      LOGGER.log( Level.SEVERE, "not opened a file for reading via startTransfer()! ABORT" );
      return( -1 );
    }
    LOGGER.log( Level.SEVERE, "LINE: <" + logLine + ">..." );
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
    logDatabaseUtil.appendLogToCache( currLogEntry, lineData );
    return( 1 );
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
    int ret = logDatabaseUtil.writeLogToDatabase( currLogEntry );
    currLogEntry = -1;
    return( ret );
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
    return( logDatabaseUtil.deleteLogFromDatabease() );
  }
}
