//@formatter:off
/*
    programm: SubmatixSPXLog
    purpose:  configuration and read logs from SUBMATIX SPX42 divecomputer via Bluethooth    
    Copyright (C) 2012  Dirk Marciniak

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/
*/
//@formatter:on
package de.dmarcini.submatix.pclogger.gui;

import de.dmarcini.submatix.pclogger.ProjectConst;
import de.dmarcini.submatix.pclogger.lang.LangStrings;
import de.dmarcini.submatix.pclogger.utils.*;
import de.dmarcini.submatix.pclogger.utils.LogListCache.DataSave;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Panel zeigt die Liste der Logeinträge an Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
 *
 * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
 */
public class Spx42LoglistPanel extends JPanel implements ListSelectionListener
{
  /**
   * Lokale implizite Klasse zur Aufbewahrung der Logdir Daten Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.09.2012
   */
  private class LogDirData
  {
    public String fileNameOnSPX = null;
    @SuppressWarnings("unused")
    public String readableName  = null;
    public long   timeStamp     = 0;

    /**
     * Privater (verbotener) Konstruktor Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
     *
     * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.09.2012
     */
    @SuppressWarnings("unused")
    private LogDirData()
    {
    }

    /**
     * Der Konstruktor mit Daten Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
     *
     * @param fName
     * @param rName
     * @param tm
     * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.09.2012
     */
    public LogDirData(final String fName, final String rName, final long tm)
    {
      fileNameOnSPX = fName;
      readableName = rName;
      timeStamp = tm;
    }
  }

  /**
   *
   */
  private static final long                         serialVersionUID        = 1L;
  private final        Logger                       lg                      = LogManager.getLogger(Spx42LoglistPanel.class.getName()); // log4j.configurationFile
  private              ActionListener               aListener               = null;
  private final        HashMap<Integer, LogDirData> logDirDataHash          = new HashMap<Integer, LogDirData>();
  private              boolean                      isPanelInitiated        = false;
  private static final Pattern                      fieldPatternSem         = Pattern.compile(";");
  private static final Pattern                      fieldPattern0x09        = Pattern.compile(ProjectConst.LOGSELECTOR);
  private              String                       timeFormatterStringDate = "dd.MM.yyyy";
  private              String                       timeFormatterStringTime = "HH:mm:ss";
  private              boolean                      isDirectoryComplete     = false;
  private              boolean                      isNextLogAnUpdate       = false;
  private              boolean                      shouldReadFromSpx       = true;
  private              int                          nextDiveIdForUpdate     = -1;
  private              LogDerbyDatabaseUtil         databaseUtil            = null;
  private              String                       deviceToLog             = null;
  private              int                          currLogEntry            = -1;
  private              Vector<Integer[]>            logListForRecive        = null;
  private              int                          fileIndex               = -1;
  private              LogListCache                 logListCache            = null;
  private JList       logListField;
  private JButton     readLogDirectoryButton;
  private JButton     readLogfilesFromSPXButton;
  private JScrollPane logListScrollPane;
  private JLabel      logListLabel;
  private JLabel      fileNameLabel;
  private JLabel      fileNameShowLabel;
  private JLabel      diveDateLabel;
  private JLabel      diveDateShowLabel;
  private JLabel      diveTimeLabel;
  private JLabel      diveTimeShowLabel;
  private JLabel      diveMaxDepthLabel;
  private JLabel      diveMaxDepthShowLabel;
  private JLabel      diveLengthLabel;
  private JLabel      diveLengthShowLabel;
  private JLabel      remarksLabel;
  private JLabel      diveLowTempLabel;
  private JLabel      diveLowTempShowLabel;
  private String      metricLength;
  private String      metricTemperature;
  private String      imperialLength;
  private String      imperialTemperature;
  private String      timeMinutes;
  private JLabel      diveNotesLabel;
  private JLabel      diveNotesShowLabel;
  private JTextArea   tipTextArea;

  /**
   * Create the panel.
   */
  @SuppressWarnings("unused")
  private Spx42LoglistPanel()
  {
    setPreferredSize(new Dimension(796, 504));
    initPanel();
  }

  /**
   * Der Konstruktor, übergibt eine Log-Instanz mit Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param al
   * @param ldb
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.05.2012
   */
  public Spx42LoglistPanel(ActionListener al, LogDerbyDatabaseUtil ldb)
  {
    this.aListener = al;
    databaseUtil = ldb;
    logDirDataHash.clear();
    isDirectoryComplete = false;
    deviceToLog = null;
    fileIndex = -1;
    currLogEntry = -1;
    isNextLogAnUpdate = false;
    nextDiveIdForUpdate = -1;
    isPanelInitiated = false;
  }

  /**
   * Einen Eintrag in das Verzeichnis einfügen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param entryMsg
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.05.2012
   */
  public void addLogdirEntry(String entryMsg)
  {
    // Message etwa so "Nummer;filename;readableName;maxNumber;unixTimeStampString"
    String[] fields;
    String fileName, readableName, wasSaved = " ", timeStampStr;
    int numberOnSpx, max, dbId = -1;
    long timeStamp = 0;
    //
    if ( !isPanelInitiated )
    {
      return;
    }
    //
    // Felder aufteilen
    fields = fieldPatternSem.split(entryMsg);
    if ( fields.length < 5 )
    {
      lg.error("recived message for logdir has lower than 4 fields. It is wrong! Abort!");
      return;
    }
    // Wandel die Nummerierung in Integer um
    try
    {
      numberOnSpx = Integer.parseInt(fields[0]);
      max = Integer.parseInt(fields[3]);
    }
    catch ( NumberFormatException ex )
    {
      lg.error("Fail to convert Hex to int: " + ex.getLocalizedMessage());
      return;
    }
    fileName = fields[1];
    // Der lesbare Teil
    readableName = fields[2];
    // Timestamp in ms
    timeStampStr = fields[4];
    // Alles ging gut....
    if ( numberOnSpx == max )
    {
      isDirectoryComplete = true;
      shouldReadFromSpx = false;
      return;
    }
    try
    {
      timeStamp = Long.parseLong(timeStampStr);
    }
    catch ( NumberFormatException ex )
    {
      lg.error("Numberformat Exception while scan timestamp for fileOnSPX: <" + ex.getLocalizedMessage() + ">");
      timeStamp = 0L;
    }
    // Sichere die Dateiangabe
    LogDirData ld = new LogDirData(fileName, readableName, timeStamp);
    logDirDataHash.put(numberOnSpx, ld);
    // schon in der Datenbank?
    if ( databaseUtil != null )
    {
      dbId = databaseUtil.isLogSavedLog(fileName, deviceToLog);
      if ( dbId != -1 )
      {
        wasSaved = "x";
      }
    }
    lg.debug("add to logdir number: <" + numberOnSpx + "> " + wasSaved + " name: <" + readableName + "> device: <" + deviceToLog + ">");
    ((LogDirListModel) logListField.getModel()).addLogentry(numberOnSpx, readableName, wasSaved, dbId);
    logListCache.addLogentry(numberOnSpx, readableName, fileName, dbId, timeStamp);
  }

  /**
   * Baue vom Cache aus auf! Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.09.2012
   */
  public void addLogDirFromCache()
  {
    LogDirListModel listMod = null;
    if ( !isPanelInitiated )
    {
      return;
    }
    if ( logListCache == null )
    {
      return;
    }
    if ( deviceToLog == null )
    {
      return;
    }
    lg.debug("read logdir from cache...");
    // beginne mit leeren Datenhashes
    clearLogdirData(false);
    // hole mal die Liste
    Vector<DataSave> logList = logListCache.getLogList();
    // erzeuge einen iterator für die Liste
    Iterator<DataSave> iterator = logList.iterator();
    //
    // Alle Listeneinträge abarbeiten
    // Anzeigeliste löschen
    //
    listMod = new LogDirListModel();
    //
    // alle Cacheinträge bearbeiten
    //
    while ( iterator.hasNext() )
    {
      int dbId = 0;
      String wasSaved = null;
      // Eintrag holen...
      DataSave entry = iterator.next();
      // Sichere die Dateiangabe
      LogDirData ld = new LogDirData(entry.fileName, entry.readableName, entry.javaTimeStamp);
      logDirDataHash.put(entry.numberOnSpx, ld);
      // ist das schon in der Datenbank?
      if ( databaseUtil != null )
      {
        dbId = databaseUtil.isLogSavedLog(entry.fileName, deviceToLog);
        if ( dbId != -1 )
        {
          wasSaved = new String("x");
        }
        else
        {
          wasSaved = new String(" ");
        }
      }
      // in die Liste einfügen
      lg.debug("add to logdir number: <" + entry.numberOnSpx + " " + wasSaved + "> name: <" + entry.readableName + "> device: <" + deviceToLog + ">");
      listMod.addLogentry(entry.numberOnSpx, entry.readableName, wasSaved, dbId);
    }
    // Lesen ist beendet ;-)
    logListField.setModel(listMod);
    logListField.clearSelection();
    logListField.validate();
    isDirectoryComplete = true;
    lg.debug("read logdir from cache...OK");
  }

  /**
   * Sichert eine weitere Zeile der Logdatei... Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param logLine
   * @return ok oder nicht
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 09.05.2012
   */
  public int addLogLineFromSPX(String logLine)
  {
    String fields[] = null;
    LogLineDataObject lineData = null;
    //
    if ( !isPanelInitiated )
    {
      return (-1);
    }
    //
    if ( fileIndex == -1 || currLogEntry < 0 )
    {
      lg.error("not opened a file for reading via startTransfer()! ABORT");
      return (-1);
    }
    lg.debug("LINE: <" + logLine.substring(10).replaceAll("\t", " ") + "...>...");
    // teile die Logline in Felder auf
    fields = fieldPattern0x09.split(logLine);
    try
    {
      lineData = new LogLineDataObject();
      // die Felder in Werte für die Datenbank umrechnen...
      lineData.pressure = Integer.parseInt(fields[0].trim());
      lineData.depth = Integer.parseInt(fields[1].trim());
      lineData.temperature = Integer.parseInt(fields[2].trim());
      lineData.acku = Double.parseDouble(fields[3].trim());
      lineData.ppo2 = Double.parseDouble(fields[5].trim());
      lineData.ppo2_1 = Double.parseDouble(fields[13].trim());
      lineData.ppo2_2 = Double.parseDouble(fields[14].trim());
      lineData.ppo2_3 = Double.parseDouble(fields[15].trim());
      lineData.setpoint = Integer.parseInt(fields[6].trim());
      lineData.n2 = Integer.parseInt(fields[16].trim());
      lineData.he = Integer.parseInt(fields[17].trim());
      lineData.zeroTime = Integer.parseInt(fields[20].trim());
      lineData.nextStep = Integer.parseInt(fields[24].trim());
    }
    catch ( NumberFormatException ex )
    {
      lg.error("error in converting numbers <" + ex.getLocalizedMessage() + ">");
      return (-1);
    }
    databaseUtil.appendLogToCacheLog(currLogEntry, lineData);
    return (1);
  }

  /**
   * kann/sollte ich vom Cache lesen? Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @return vom Cache lesen
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.09.2012
   */
  public boolean canReadFromCache()
  {
    return (!shouldReadFromSpx);
  }

  /**
   * Leere das Array für das Logverzeichnis des SPX (Cache) Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param clearCache Verzeichniscache löschen
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.05.2012
   */
  private void clearLogdirData(boolean clearCache)
  {
    if ( !isPanelInitiated )
    {
      return;
    }
    // Datenhash leeren
    logDirDataHash.clear();
    // Anzeigeliste
    ((LogDirListModel) logListField.getModel()).clear();
    isDirectoryComplete = false;
    if ( clearCache )
    {
      // Erzeuge einen neuen Cache
      logListCache = new LogListCache();
      shouldReadFromSpx = true;
    }
    clearLabels();
  }

  private void clearLabels()
  {
    fileNameLabel.setText("-");
    diveDateShowLabel.setText("-");
    diveTimeShowLabel.setText("-");
    diveMaxDepthShowLabel.setText("-");
    diveLengthShowLabel.setText("-");
    diveLowTempShowLabel.setText("-");
    diveNotesShowLabel.setText("-");
    remarksLabel.setText(" ");
  }

  /**
   * Gib die nächste Nummer eines zu lesenden Eintrages zurück Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @return Nummer oder -1
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 13.05.2012
   */
  public Integer[] getNextEntryToRead()
  {
    if ( !isPanelInitiated )
    {
      return (null);
    }
    // Ist die Liste (Vector) allociert?
    if ( logListForRecive == null )
    {
      // Nein, nix zu tun!
      return (null);
    }
    // Sind Elemente vorhanden?
    if ( logListForRecive.isEmpty() )
    {
      // Liste ist Leer, nix zu tun
      logListForRecive = null;
      return (null);
    }
    // den ersten Eintrag zurückgeben
    return (logListForRecive.remove(0));
  }

  /**
   * Initialisiert die GUI des Panels Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.05.2012
   */
  private void initPanel()
  {
    setLayout(null);
    logListScrollPane = new JScrollPane();
    logListScrollPane.setBounds(10, 31, 248, 455);
    add(logListScrollPane);
    logListField = new JList();
    logListField.setForeground(Color.BLUE);
    logListField.setFont(new Font("Dialog", Font.PLAIN, 12));
    logListField.setModel(new LogDirListModel());
    logListScrollPane.setViewportView(logListField);
    readLogDirectoryButton = new JButton(LangStrings.getString("spx42LoglistPanel.readLogDirectoryButton.text")); //$NON-NLS-1$
    readLogDirectoryButton.setHorizontalAlignment(SwingConstants.LEFT);
    readLogDirectoryButton.setIconTextGap(15);
    readLogDirectoryButton.setIcon(new ImageIcon(Spx42LoglistPanel.class.getResource("/de/dmarcini/submatix/pclogger/res/109.png")));
    readLogDirectoryButton.setPreferredSize(new Dimension(180, 40));
    readLogDirectoryButton.setMaximumSize(new Dimension(160, 40));
    readLogDirectoryButton.setMargin(new Insets(2, 30, 2, 30));
    readLogDirectoryButton.setForeground(new Color(0, 100, 0));
    readLogDirectoryButton.setBackground(new Color(152, 251, 152));
    readLogDirectoryButton.setActionCommand("read_logdir_from_spx");
    readLogDirectoryButton.setBounds(505, 12, 254, 60);
    add(readLogDirectoryButton);
    readLogfilesFromSPXButton = new JButton(LangStrings.getString("spx42LoglistPanel.readLogfilesFromSPXButton.text")); //$NON-NLS-1$
    readLogfilesFromSPXButton.setIconTextGap(15);
    readLogfilesFromSPXButton.setHorizontalAlignment(SwingConstants.LEFT);
    readLogfilesFromSPXButton.setIcon(new ImageIcon(Spx42LoglistPanel.class.getResource("/de/dmarcini/submatix/pclogger/res/Down.png")));
    readLogfilesFromSPXButton.setPreferredSize(new Dimension(180, 40));
    readLogfilesFromSPXButton.setMaximumSize(new Dimension(160, 40));
    readLogfilesFromSPXButton.setMargin(new Insets(2, 30, 2, 30));
    readLogfilesFromSPXButton.setForeground(Color.BLUE);
    readLogfilesFromSPXButton.setBackground(new Color(153, 255, 255));
    readLogfilesFromSPXButton.setActionCommand("read_logfile_from_spx");
    readLogfilesFromSPXButton.setBounds(505, 83, 254, 60);
    add(readLogfilesFromSPXButton);
    logListLabel = new JLabel(LangStrings.getString("spx42LoglistPanel.logListLabel.text")); //$NON-NLS-1$
    logListLabel.setLabelFor(logListScrollPane);
    logListLabel.setHorizontalAlignment(SwingConstants.LEFT);
    logListLabel.setBounds(10, 11, 248, 14);
    add(logListLabel);
    fileNameLabel = new JLabel(LangStrings.getString("spx42LoglistPanel.fileNameLabel.text")); //$NON-NLS-1$
    fileNameLabel.setForeground(new Color(128, 128, 128));
    fileNameLabel.setBounds(268, 31, 282, 14);
    add(fileNameLabel);
    fileNameShowLabel = new JLabel("-");
    fileNameShowLabel.setForeground(new Color(0, 0, 139));
    fileNameLabel.setLabelFor(fileNameShowLabel);
    fileNameShowLabel.setBounds(268, 46, 282, 14);
    add(fileNameShowLabel);
    diveDateLabel = new JLabel(LangStrings.getString("spx42LoglistPanel.diveDateLabel.text")); //$NON-NLS-1$
    diveDateLabel.setForeground(new Color(128, 128, 128));
    diveDateLabel.setBounds(268, 69, 282, 14);
    add(diveDateLabel);
    diveDateShowLabel = new JLabel("-");
    diveDateShowLabel.setForeground(new Color(0, 0, 139));
    diveDateLabel.setLabelFor(diveDateShowLabel);
    diveDateShowLabel.setBounds(268, 83, 282, 14);
    add(diveDateShowLabel);
    diveTimeLabel = new JLabel(LangStrings.getString("spx42LoglistPanel.diveTimeLabel.text")); //$NON-NLS-1$
    diveTimeLabel.setForeground(new Color(128, 128, 128));
    diveTimeLabel.setBounds(268, 106, 282, 14);
    add(diveTimeLabel);
    diveTimeShowLabel = new JLabel("-");
    diveTimeShowLabel.setForeground(new Color(0, 0, 139));
    diveTimeLabel.setLabelFor(diveTimeShowLabel);
    diveTimeShowLabel.setBounds(268, 119, 282, 14);
    add(diveTimeShowLabel);
    diveMaxDepthLabel = new JLabel(LangStrings.getString("spx42LoglistPanel.diveMaxDepthLabel.text")); //$NON-NLS-1$
    diveMaxDepthLabel.setForeground(new Color(128, 128, 128));
    diveMaxDepthLabel.setBounds(268, 144, 282, 14);
    add(diveMaxDepthLabel);
    diveMaxDepthShowLabel = new JLabel("-");
    diveMaxDepthShowLabel.setForeground(new Color(0, 0, 139));
    diveMaxDepthLabel.setLabelFor(diveMaxDepthShowLabel);
    diveMaxDepthShowLabel.setBounds(268, 158, 282, 14);
    add(diveMaxDepthShowLabel);
    diveLengthLabel = new JLabel(LangStrings.getString("fileManagerPanel.diveListHeaders.diveLen")); //$NON-NLS-1$
    diveLengthLabel.setForeground(new Color(128, 128, 128));
    diveLengthLabel.setBounds(268, 183, 282, 14);
    add(diveLengthLabel);
    diveLengthShowLabel = new JLabel("-");
    diveLengthShowLabel.setForeground(new Color(0, 0, 139));
    diveLengthShowLabel.setBounds(268, 196, 282, 14);
    add(diveLengthShowLabel);
    remarksLabel = new JLabel(LangStrings.getString("spx42LogListPanel.remarksLabel.text")); //$NON-NLS-1$
    remarksLabel.setForeground(new Color(210, 105, 30));
    remarksLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
    remarksLabel.setBounds(267, 300, 492, 14);
    add(remarksLabel);
    diveLowTempLabel = new JLabel(LangStrings.getString("spx42LoglistPanel.diveLowTempLabel.text")); //$NON-NLS-1$
    diveLowTempLabel.setForeground(new Color(128, 128, 128));
    diveLowTempLabel.setBounds(268, 220, 282, 14);
    add(diveLowTempLabel);
    diveLowTempShowLabel = new JLabel("-");
    diveLowTempShowLabel.setForeground(new Color(0, 0, 139));
    diveLowTempShowLabel.setBounds(268, 235, 282, 14);
    add(diveLowTempShowLabel);
    diveNotesLabel = new JLabel(LangStrings.getString("spx42LoglistPanel.diveNotesLabel.text")); //$NON-NLS-1$
    diveNotesLabel.setForeground(new Color(128, 128, 128));
    diveNotesLabel.setBounds(268, 260, 282, 14);
    add(diveNotesLabel);
    diveNotesShowLabel = new JLabel("-");
    diveNotesShowLabel.setForeground(new Color(0, 128, 0));
    diveNotesShowLabel.setBounds(268, 275, 492, 14);
    add(diveNotesShowLabel);
    tipTextArea = new JTextArea();
    tipTextArea.setEditable(false);
    tipTextArea.setWrapStyleWord(true);
    tipTextArea.setLineWrap(true);
    tipTextArea.setBackground(UIManager.getColor("Label.background"));
    tipTextArea.setForeground(new Color(0, 0, 255));
    tipTextArea.setFont(new Font("Tahoma", Font.ITALIC, 12));
    tipTextArea.setText(LangStrings.getString("spx42LoglistPanel.tipTextArea.text")); //$NON-NLS-1$
    tipTextArea.setBounds(267, 426, 492, 60);
    add(tipTextArea);
    remarksLabel.setVisible(true);
  }

  /**
   * Lesen vollständig? Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @return Lesen vollständig opder nicht
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.05.2012
   */
  public boolean isReadingComplete()
  {
    return (isDirectoryComplete);
  }

  /**
   * Vorbereitungen zum download der Logdaten treffen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param device
   * @return Array von Logdateinummern (Nummer des Logs auf dem SPX42)
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 06.05.2012
   */
  public int prepareDownloadLogdata(String device)
  {
    int[] logSelected = null;
    deviceToLog = device;
    if ( deviceToLog == null )
    {
      return (0);
    }
    if ( !isPanelInitiated )
    {
      return (-1);
    }
    lg.debug("prepare to download logdata...");
    //
    // Ok, das sieht so aus, als könne es losgehen
    //
    lg.debug("test for selected logentrys...");
    logSelected = logListField.getSelectedIndices();
    if ( logSelected.length > 0 )
    {
      // es ist auch etwas markiert!
      // Array erzeugen
      logListForRecive = new Vector<Integer[]>();
      // für jeden markierten index die Lognummer holen
      for ( int idx = 0; idx < logSelected.length; idx++ )
      {
        Integer[] lEntry = new Integer[2];
        lEntry[0] = ((LogDirListModel) logListField.getModel()).getLognumberAt(logSelected[idx]);
        lEntry[1] = ((LogDirListModel) logListField.getModel()).istInDb(logSelected[idx]) ? 1 : 0;
        logListForRecive.add(lEntry);
        lg.debug("select dive number <" + logSelected[idx] + "> for download...");
      }
      lg.debug("prepare to download logdata...OK");
      return (logListForRecive.size());
    }
    // Es ist nichts markiert
    lg.warn("prepare to download logdata...NOTHING selected!");
    return (0);
  }

  /**
   * Das Panel zur Anzeige bereit machen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param connDev
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.09.2012
   */
  public void prepareLogListPanel(String connDev)
  {
    initPanel();
    logDirDataHash.clear();
    isDirectoryComplete = false;
    deviceToLog = connDev;
    fileIndex = -1;
    currLogEntry = -1;
    isNextLogAnUpdate = false;
    nextDiveIdForUpdate = -1;
    remarksLabel.setText(" ");
    isPanelInitiated = true;
    setLanguageStrings();
    setGlobalChangeListener((MainCommGUI) aListener);
  }

  /**
   * Bereite das Lesen des Logverzeichnisses vor Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param device
   * @return In Ordnung
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.05.2012
   */
  public boolean prepareReadLogdir(String device)
  {
    if ( !isPanelInitiated )
    {
      return (false);
    }
    // die Voreinstellung:
    shouldReadFromSpx = true;
    // Kein Gerät, keine Aktion
    if ( device == null )
    {
      return (false);
    }
    // welches Gerät loggen wir
    deviceToLog = device;
    // ich soll direkt vom SPX lesen!
    clearLogdirData(true);
    lg.debug("prepare to read logdir...");
    return (true);
  }

  /**
   * Das Panel entfernen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.09.2012
   */
  public void releasePanel()
  {
    isPanelInitiated = false;
    this.removeAll();
    logDirDataHash.clear();
    deviceToLog = null;
    fileIndex = -1;
    currLogEntry = -1;
    isNextLogAnUpdate = false;
    nextDiveIdForUpdate = -1;
  }

  /**
   * Wenn Datenbankfehler auftraten, Datenreste entfernen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @return ging oder Fehler
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 25.06.2012
   */
  public int removeFailedDataset()
  {
    return (databaseUtil.deleteLogFromDatabeaseLog());
  }

  /**
   * Elemente, die je nach Status erlaubt oder ausgeblendet werden sollen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param en
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.05.2012
   */
  public void setAllLogPanelsEnabled(boolean en)
  {
    if ( !isPanelInitiated )
    {
      return;
    }
    logListScrollPane.setEnabled(en);
  }

  /**
   * Alle Listener setzen, die benötigt werden Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param mainCommGUI
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.05.2012
   */
  public void setGlobalChangeListener(MainCommGUI mainCommGUI)
  {
    this.aListener = mainCommGUI;
    if ( !isPanelInitiated )
    {
      return;
    }
    readLogDirectoryButton.addActionListener(mainCommGUI);
    readLogDirectoryButton.addMouseMotionListener(mainCommGUI);
    readLogfilesFromSPXButton.addActionListener(mainCommGUI);
    readLogfilesFromSPXButton.addMouseMotionListener(mainCommGUI);
    logListField.addMouseMotionListener(mainCommGUI);
    logListField.addListSelectionListener(this);
  }

  /**
   * Die Sprache anpassen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @return -1 Fehler, 0 naja, 1 alles OK
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 05.05.2012
   */
  public int setLanguageStrings()
  {
    if ( !isPanelInitiated )
    {
      return (1);
    }
    try
    {
      clearLogdirData(true);
      logListLabel.setText(LangStrings.getString("spx42LoglistPanel.logListLabel.text"));
      readLogDirectoryButton.setText(LangStrings.getString("spx42LoglistPanel.readLogDirectoryButton.text"));
      readLogDirectoryButton.setToolTipText(LangStrings.getString("spx42LoglistPanel.readLogDirectoryButton.tooltiptext"));
      readLogfilesFromSPXButton.setText(LangStrings.getString("spx42LoglistPanel.readLogfilesFromSPXButton.text"));
      readLogfilesFromSPXButton.setToolTipText(LangStrings.getString("spx42LoglistPanel.readLogfilesFromSPXButton.tooltiptext"));
      logListField.setToolTipText(LangStrings.getString("spx42LoglistPanel.logListField.tooltiptext"));
      fileNameLabel.setText(LangStrings.getString("spx42LoglistPanel.fileNameLabel.text"));
      diveDateLabel.setText(LangStrings.getString("spx42LoglistPanel.diveDateLabel.text"));
      diveTimeLabel.setText(LangStrings.getString("spx42LoglistPanel.diveTimeLabel.text"));
      diveMaxDepthLabel.setText(LangStrings.getString("spx42LoglistPanel.diveMaxDepthLabel.text"));
      diveLengthLabel.setText(LangStrings.getString("spx42LoglistPanel.diveLengthLabel.text"));
      diveLowTempLabel.setText(LangStrings.getString("spx42LoglistPanel.diveLowTempLabel.text"));
      diveNotesLabel.setText(LangStrings.getString("spx42LoglistPanel.diveNotesLabel.text"));
      metricLength = LangStrings.getString("spx42LoglistPanel.unit.metric.length");
      metricTemperature = LangStrings.getString("spx42LoglistPanel.unit.metric.temperature");
      imperialLength = LangStrings.getString("spx42LoglistPanel.unit.imperial.length");
      imperialTemperature = LangStrings.getString("spx42LoglistPanel.unit.imperial.temperature");
      timeMinutes = LangStrings.getString("spx42LoglistPanel.unit.minutes");
      timeFormatterStringDate = LangStrings.getString("MainCommGUI.timeFormatterStringDate");
      timeFormatterStringTime = LangStrings.getString("MainCommGUI.timeFormatterStringTime");
      tipTextArea.setText(LangStrings.getString("spx42LoglistPanel.tipTextArea.text"));
    }
    catch ( NullPointerException ex )
    {
      System.out.println("ERROR set language strings <" + ex.getMessage() + "> ABORT!");
      return (-1);
    }
    catch ( MissingResourceException ex )
    {
      System.out.println("ERROR set language strings - the given key can be found <" + ex.getMessage() + "> ABORT!");
      return (0);
    }
    catch ( ClassCastException ex )
    {
      System.out.println("ERROR set language strings <" + ex.getMessage() + "> ABORT!");
      return (0);
    }
    return (1);
  }

  /**
   * Ist der nächste Start eines Logs ein Update? Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param isUpdate
   * @return ja oder nein
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 12.07.2012
   */
  boolean setNextLogIsAnUpdate(boolean isUpdate, int diveId)
  {
    isNextLogAnUpdate = isUpdate;
    nextDiveIdForUpdate = diveId;
    return (isNextLogAnUpdate);
  }

  /**
   * Vorbereiten: Es kommt ein Logfile mit der Indexnummer aus "fileNumberStr" Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @param fileNumberStr Indexnummer aus der Liste im Panel
   * @param unitSystem    metrisch oder imperial
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 13.05.2012
   */
  public void startTransfer(String fileNumberStr, int unitSystem)
  {
    int diveId;
    LogDirData ld = null;
    if ( !isPanelInitiated )
    {
      return;
    }
    //
    // Datenbank bereit für mich?
    //
    lg.debug("start transfer for file on SPX with number <" + fileNumberStr + ">...");
    if ( databaseUtil == null )
    {
      lg.error("logDatabaseUtil not allocated!");
      fileIndex = -1;
      return;
    }
    try
    {
      // Die Nummer des Logeintrages auf dem SPX
      fileIndex = Integer.parseInt(fileNumberStr, 16);
    }
    catch ( NumberFormatException ex )
    {
      lg.error("wrong filenumber in String: <" + ex.getLocalizedMessage() + ">");
      fileIndex = -1;
      return;
    }
    //
    // soll ein Update gemacht werden?
    //
    if ( isNextLogAnUpdate )
    {
      currLogEntry = nextDiveIdForUpdate;
      nextDiveIdForUpdate = -1;
      isNextLogAnUpdate = false;
      // Logdaten aus der DB entfernen
      databaseUtil.removeLogdataForIdLog(currLogEntry);
      databaseUtil.allocateCacheLog(currLogEntry);
    }
    else
    {
      // Kein Update....
      ld = logDirDataHash.get(fileIndex);
      // Ersten Eintrag für den Tauchgang machen
      diveId = databaseUtil.writeNewDiveLog(deviceToLog, ld.fileNameOnSPX, unitSystem, fileIndex, (ld.timeStamp) / 1000);
      if ( diveId < 0 )
      {
        fileIndex = -1;
        currLogEntry = -1;
        lg.error("dive id for this function was smaller then 0! ABORT");
        return;
      }
      currLogEntry = diveId;
    }
    lg.debug("start transfer for file on SPX with number <" + fileNumberStr + ">...OK");
  }

  @Override
  public void valueChanged(ListSelectionEvent ev)
  {
    // Wen die Selektion der Liste verändert wurde...
    int fIndex, spxNumber, dbId, savedUnits;
    //
    if ( ev.getSource().equals(logListField) )
    {
      if ( !ev.getValueIsAdjusting() )
      {
        // Das Ende der Serie, jetzt guck ich mal nach der ersten markierten...
        lg.debug("ist last or once change...");
        fIndex = logListField.getSelectedIndex();
        lg.debug(String.format("first selected Index: %d ", fIndex));
        spxNumber = ((LogDirListModel) logListField.getModel()).getLognumberAt(fIndex);
        if ( spxNumber == -1 )
        {
          diveDateShowLabel.setText("-");
          diveTimeShowLabel.setText("-");
          return;
        }
        dbId = ((LogDirListModel) logListField.getModel()).getDbIdAt(fIndex);
        lg.debug(String.format("number on SPX: %d, DBID: %d, readable Name: %s, filename: %s", spxNumber, dbId,
                               ((LogDirListModel) logListField.getModel()).getLogNameAt(fIndex), logDirDataHash.get(spxNumber).fileNameOnSPX));
        // erst mal die allgemeinen Daten des Dives anzeigen
        fileNameShowLabel.setText(logDirDataHash.get(spxNumber).fileNameOnSPX);
        // Zeitstempel für Datum und Zeitanzeige nutzen
        DateTime dt = new DateTime(logDirDataHash.get(spxNumber).timeStamp);
        // Landesspezifische Formatierung
        DateTimeFormatter fmtDate = DateTimeFormat.forPattern(timeFormatterStringDate);
        DateTimeFormatter fmtTime = DateTimeFormat.forPattern(timeFormatterStringTime);
        // setze Datum und Zeit
        diveDateShowLabel.setText(dt.toString(fmtDate));
        diveTimeShowLabel.setText(dt.toString(fmtTime));
        // Jetzt schau ich mal, ob da was in der Datenbank zu finden ist
        // Ja, der ist in der Datenbank erfasst!
        String[] headers = databaseUtil.getHeadDiveDataFromIdAsSTringLog(dbId);
        if ( headers != null )
        {
          diveNotesShowLabel.setText(databaseUtil.getNotesForIdLog(dbId));
          // Was war gespeichert?
          if ( headers[11].equals("METRIC") )
          {
            savedUnits = ProjectConst.UNITS_METRIC;
          }
          else
          {
            savedUnits = ProjectConst.UNITS_IMPERIAL;
          }
          if ( (SpxPcloggerProgramConfig.unitsProperty == ProjectConst.UNITS_DEFAULT) || SpxPcloggerProgramConfig.unitsProperty == savedUnits )
          {
            //
            // alles wie gespeichert anzeigen
            //
            if ( savedUnits == ProjectConst.UNITS_METRIC )
            {
              // Maximale Tiefe anzeigen
              diveMaxDepthShowLabel.setText(String.format("%s %s", headers[8], metricLength));
              // kälteste Temperatur anzeigen
              diveLowTempShowLabel.setText(String.format("%s %s", headers[7], metricTemperature));
            }
            else
            {
              // Maximale Tiefe anzeigen
              diveMaxDepthShowLabel.setText(String.format("%s %s", headers[8], imperialLength));
              // kälteste Temperatur anzeigen
              diveLowTempShowLabel.setText(String.format("%s %s", headers[7], imperialTemperature));
            }
            remarksLabel.setText(" ");
          }
          else
          {
            //
            // hier wird es schwierig == umrechnen
            //
            try
            {
              double depth = Double.parseDouble(headers[8].trim());
              double temp = Double.parseDouble(headers[7].trim());
              if ( savedUnits == ProjectConst.UNITS_METRIC )
              {
                // Maximale Tiefe anzeigen
                // imperial -> metrisch
                // 1 foot == 30,48 cm == 0.3048 Meter
                diveMaxDepthShowLabel.setText(String.format("%2.1f %s", (depth / 0.3048), imperialLength));
                // kälteste Temperatur anzeigen
                // imperial -> metrisch
                // t °C = (9⁄5 t + 32) °F
                diveLowTempShowLabel.setText(String.format("%d %s", Math.round(((9.0 / 5.0) * temp) + 32.0), imperialTemperature));
              }
              else
              {
                // Maximale Tiefe anzeigen
                // metrisch-> imperial konvertieren
                // 1 foot == 30,48 cm == 0.3048 Meter
                diveMaxDepthShowLabel.setText(String.format("%2.1f %s", (depth * 0.3048), metricLength));
                // kälteste Temperatur anzeigen
                // metrisch-> imperial konvertieren
                // t °F = 5⁄9 (t − 32) °C
                diveLowTempShowLabel.setText(String.format("%d %s", Math.round((5.0 / 9.0) * (temp - 32)), metricTemperature));
              }
              remarksLabel.setText(LangStrings.getString("spx42LogGraphPanel.remarksLabel.computed"));
            }
            catch ( NumberFormatException ex )
            {
              lg.error("NumberFormatException while compute temperature/depth for showing: <" + ex.getLocalizedMessage() + ">");
              diveMaxDepthShowLabel.setText("-");
              diveLengthShowLabel.setText("-");
              diveLowTempShowLabel.setText("-");
              diveNotesShowLabel.setText("-");
              remarksLabel.setText(" ");
            }
          }
          // Länge des Tauchgangs anzeigen
          diveLengthShowLabel.setText(String.format("%s %s", headers[10], timeMinutes));
        }
        else
        {
          diveMaxDepthShowLabel.setText("-");
          diveLengthShowLabel.setText("-");
          diveLowTempShowLabel.setText("-");
          diveNotesShowLabel.setText("-");
          remarksLabel.setText(" ");
        }
      }
    }
  }

  /**
   * Den Cache (der im Datenbankobjekt vorgehalten wird) nun zur Datenbank schreiben Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   *
   * @return ok oder nicht
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 17.06.2012
   */
  public int writeCacheToDatabase()
  {
    lg.debug("write to Database....");
    int ret = databaseUtil.writeLogToDatabaseLog(currLogEntry);
    currLogEntry = -1;
    return (ret);
  }
}
