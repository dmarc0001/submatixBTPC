package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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
  private final HashMap<Integer, String> logdirFiles         = new HashMap<Integer, String>();
  private final HashMap<Integer, String> logdirReadable      = new HashMap<Integer, String>();
  private static final Pattern           fieldPatternSem     = Pattern.compile( ";" );
  private static final Pattern           fieldPatternDtTm    = Pattern.compile( " - " );
  private final LogdirListModel          logListModel        = new LogdirListModel();
  private boolean                        isDirectoryComplete = false;
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
   */
  public spx42LoglistPanel( Logger LOGGER )
  {
    this.LOGGER = LOGGER;
    initPanel();
    logdirFiles.clear();
    logdirReadable.clear();
    isDirectoryComplete = false;
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
    fileNameLabel.setLabelFor( fileNameShowLabel );
    fileNameShowLabel.setBounds( 268, 56, 282, 14 );
    add( fileNameShowLabel );
    diveDateLabel = new JLabel( "DIVEDATE" );
    diveDateLabel.setForeground( Color.DARK_GRAY );
    diveDateLabel.setBounds( 268, 90, 282, 14 );
    add( diveDateLabel );
    diveDateShowLabel = new JLabel( "-" );
    diveDateLabel.setLabelFor( diveDateShowLabel );
    diveDateShowLabel.setBounds( 268, 106, 282, 14 );
    add( diveDateShowLabel );
    diveTimeLabel = new JLabel( "DIVETIME" );
    diveTimeLabel.setForeground( Color.DARK_GRAY );
    diveTimeLabel.setBounds( 268, 141, 282, 14 );
    add( diveTimeLabel );
    diveTimeShowLabel = new JLabel( "-" );
    diveTimeLabel.setLabelFor( diveTimeShowLabel );
    diveTimeShowLabel.setBounds( 268, 158, 282, 14 );
    add( diveTimeShowLabel );
    diveMaxDepthLabel = new JLabel( "DIVEMAXDEPTH" );
    diveMaxDepthLabel.setForeground( Color.DARK_GRAY );
    diveMaxDepthLabel.setBounds( 268, 194, 282, 14 );
    add( diveMaxDepthLabel );
    diveMaxDepthShowLabel = new JLabel( "-" );
    diveMaxDepthLabel.setLabelFor( diveMaxDepthShowLabel );
    diveMaxDepthShowLabel.setBounds( 268, 208, 282, 14 );
    add( diveMaxDepthShowLabel );
    diveLengthLabel = new JLabel( "DIVELENGTH" );
    diveLengthLabel.setForeground( Color.DARK_GRAY );
    diveLengthLabel.setBounds( 268, 243, 282, 14 );
    add( diveLengthLabel );
    diveLengthShowLabel = new JLabel( "-" );
    diveLengthShowLabel.setBounds( 268, 259, 282, 14 );
    add( diveLengthShowLabel );
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
    // Dateiunamen auf dem SPX
    logdirFiles.clear();
    // Nummerierung auf dem SPX
    logdirReadable.clear();
    logListModel.clear();
    isDirectoryComplete = false;
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
    // Message etwa so "Nummer;filename;readableName;maxNumber;wasSaved"
    String[] fields;
    String fileName, readableName, wasSaved;
    int number, max;
    //
    // Felder aufteilen
    fields = fieldPatternSem.split( entryMsg );
    if( fields.length < 5 )
    {
      LOGGER.log( Level.SEVERE, "recived message for logdir has lower than 5 fields. It is wrong! Abort!" );
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
    readableName = fields[2];
    wasSaved = fields[4];
    // Alles ging gut....
    if( number == max )
    {
      isDirectoryComplete = true;
    }
    LOGGER.log( Level.FINE, "add to logdir number: <" + number + "> name: <" + readableName + ">" );
    // Sichere die Dateiangabe
    logdirFiles.put( number, fileName );
    // Sichere Lesbares Format
    logdirReadable.put( number, readableName );
    // in die Liste einfügen
    logListModel.addLogentry( number, readableName, wasSaved );
  }

  @Override
  public void valueChanged( ListSelectionEvent ev )
  {
    // Wen die Selektion verändert wurde...
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
        // logListField.getSelectedIndices();
      }
    }
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
}
