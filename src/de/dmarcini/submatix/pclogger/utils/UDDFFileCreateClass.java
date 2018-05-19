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
/**
 * Helferklasse zum erzeugen von UDDF 2.2 Files
 */
package de.dmarcini.submatix.pclogger.utils;

import de.dmarcini.submatix.pclogger.ProjectConst;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Klasse zum Erzeugen von UDDF Version 2.0 Dateien Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
 *
 * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 27.10.2011
 */
public class UDDFFileCreateClass
{
  private final String               gasPattern     = "0.";
  private       Document             uddfDoc        = null;
  private final Logger               lg             = LogManager.getLogger(UDDFFileCreateClass.class.getName()); // log4j.configurationFile
  private       LogDerbyDatabaseUtil sqliteDbUtil   = null;
  private       Transformer          transformer    = null;
  private       DocumentBuilder      builder        = null;
  private       ArrayList<String>    gases          = null;
  private final Pattern              fieldPatternDp = Pattern.compile(":");
  private       int[]                headData       = null;
  private       String               diveComment    = null;

  @SuppressWarnings("unused")
  private UDDFFileCreateClass()
  {
  }

  ;

  /**
   * Der Konstruktor der Helperklasse Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   *
   * @param databaseUtil
   * @throws ParserConfigurationException
   * @throws TransformerException
   * @throws TransformerFactoryConfigurationError
   * @throws Exception
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 24.10.2011
   */
  public UDDFFileCreateClass(final LogDerbyDatabaseUtil databaseUtil) throws ParserConfigurationException, TransformerException, TransformerFactoryConfigurationError, Exception
  {
    // initialisiere die Klasse
    this.sqliteDbUtil = databaseUtil;
    if ( databaseUtil == null || !databaseUtil.isOpenDB() )
    {
      throw new Exception("database not initiated");
    }
    lg.debug("create helperclass for uddf-export...");
    transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    builder = factory.newDocumentBuilder();
    lg.debug("create helperclass for uddf-export...OK");
  }

  /**
   * Erzeuge die XML-Datei (UDDF) für einen Logeintrag Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   *
   * @param exportDir Verzeichnis, in die das Ergebnis nachher kommt
   * @param diveNum   Nummer in der Datenbank (dive_id)
   * @return true oder false
   * @throws Exception
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 24.10.2011
   */
  public File createXML(File exportDir, int diveNum) throws Exception
  {
    int[] diveNums = new int[1];
    diveNums[0] = diveNum;
    return (createXML(exportDir, diveNums));
  }

  /**
   * Erzeuge die XML-Datei (UDDF) Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   *
   * @param exportDir
   * @param diveNums
   * @return Dateiobjekt für UDDF-Datei
   * @throws Exception
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 30.08.2012
   */
  public File createXML(File exportDir, int[] diveNums) throws Exception
  {
    Element rootNode = null;
    String msg = null;
    String fileName = null;
    File retFile = null;
    File saveFile = null;
    //
    lg.debug("create uddf file...");
    if ( sqliteDbUtil == null || !sqliteDbUtil.isOpenDB() )
    {
      throw new Exception("database not initiated");
    }
    //
    // Daten vom ersten Tauchgang auslesen
    //
    headData = sqliteDbUtil.getHeadDiveDataFromIdLog(diveNums[0]);
    // die Tauchzeit rausbekommen
    long diveTimeUnix = (getDiveTime()) * 1000L;
    DateTime dateTime = new DateTime(diveTimeUnix);
    // den Export-Dateinamen machen
    if ( diveNums.length == 1 )
    {
      fileName = String.format("%s%s%s-dive-from-%s.uddf", exportDir.getAbsolutePath(), File.separatorChar, sqliteDbUtil.getDeviceIdLog(diveNums[0]),
                               dateTime.toString("yyyy-MM-dd-hh-mm"));
    }
    else
    {
      fileName = String.format("%s%s%s-dive-from-%s-plus-%d.uddf", exportDir.getAbsolutePath(), File.separatorChar, sqliteDbUtil.getDeviceIdLog(diveNums[0]),
                               dateTime.toString("yyyy-MM-dd-hh-mm"), diveNums.length);
    }
    saveFile = new File(fileName);
    //
    // Erzeuge Dokument neu
    //
    uddfDoc = builder.newDocument();
    // Root-Element erzeugen
    rootNode = uddfDoc.createElement("uddf");
    rootNode.setAttribute("version", ProjectConst.UDDFVERSION);
    uddfDoc.appendChild(rootNode);
    // Programmname einfügen
    rootNode.appendChild(uddfDoc.createComment(ProjectConst.CREATORPROGRAM));
    // Appliziere Generator
    rootNode.appendChild(makeGeneratorNode(uddfDoc));
    // appliziere Gasdefinitionen
    rootNode.appendChild(makeGasdefinitions(uddfDoc, diveNums));
    // appliziere profiledata
    rootNode.appendChild(makeProfilesData(uddfDoc, diveNums));
    // DEBUG uddfDoc.normalizeDocument();
    try
    {
      // mach eine Datei aus dem DOM-Baum
      retFile = domToFile(saveFile, uddfDoc);
    }
    catch ( TransformerException ex )
    {
      msg = "transformer Exception " + ex.getLocalizedMessage();
      lg.error("createXML: <" + msg + ">");
      return (null);
    }
    catch ( IOException ex )
    {
      msg = "IOException " + ex.getLocalizedMessage();
      lg.error("createXML: <" + msg + ">");
      return (null);
    }
    catch ( Exception ex )
    {
      msg = "allgemeine Exception " + ex.getLocalizedMessage();
      lg.error("createXML: <" + msg + ">");
      return (null);
    }
    return (retFile);
  }

  /**
   * Erzeuge die XML-Datei aus dem DOM-Baum im speicher Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   *
   * @param file     File Objekt für die Zieldatei
   * @param document Document Objekt
   * @return Ok oder nicht OK
   * @throws IOException
   * @throws TransformerException
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 27.10.2011
   */
  private File domToFile(File file, Document document) throws IOException, TransformerException
  {
    lg.debug("make dom to file...");

    // Vorbereitungen
    if ( file.exists() )
    {
      // Datei ist da, ich will sie ueberschreiben
      file.delete();
    }
    // jetzt geht es los
    try
    {
      lg.debug("...create transformer dom -> xml-file...");

      transformer = TransformerFactory.newInstance().newTransformer();
      lg.debug("...config transformer...");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "no");
      transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

      lg.debug("...transform!");
      transformer.transform(
          new DOMSource(document),
          new StreamResult(file));
    }
    catch ( DOMException | IllegalArgumentException | TransformerFactoryConfigurationError | TransformerException e1 )
    {
      lg.error(e1.getLocalizedMessage());
      return (null);
    }

    lg.debug("...ok ");
    return (file);
  }

  /**
   * Anzahl der Samples zum Tauchgang Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   *
   * @return
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.08.2012
   */
  private int getDiveSamples()
  {
    return (headData[4]);
  }

  /**
   * Gibt den Anfang des Tauchganges als unix timestamp zurück Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   *
   * @param dive_id
   * @return
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.08.2012
   */
  private int getDiveTime()
  {
    return (headData[0]);
  }

  /**
   * Allerersten Temperaturwert für Tauchgang erfragen Temeraturen in KELVIN Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   *
   * @return Allererste Temperatur beim Tauchgang (müßte in etwa Lufttemperatur sein)
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 25.10.2011
   */
  private String getFirstTempForDive()
  {
    float tempValue = 0;
    String temperature;
    //
    tempValue = (float)headData[1];
    tempValue = tempValue / 10;
    tempValue += ProjectConst.KELVIN;
    temperature = String.format(Locale.ENGLISH, "%.1f", tempValue);
    return (temperature);
  }

  /**
   * Grösste Tiefe des Tauchganges zurückgeben Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   *
   * @return Größte Tiefe des Tauchganges in dm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.08.2012
   */
  private String getGreatestDepthForDive()
  {
    return (String.format(Locale.ENGLISH, "%.1f", (float) (headData[3] / 10.0)));
  }

  /**
   * Die tiefste Temperatur finden Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   *
   * @param dive_id
   * @return
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.08.2012
   */
  private String getLowestTempForDive()
  {
    float tempValue = 0;
    String temperature;
    //
    tempValue = (float)headData[2];
    tempValue = tempValue / 10;
    tempValue += ProjectConst.KELVIN;
    temperature = String.format(Locale.ENGLISH, "%.1f", tempValue);
    return (temperature);
  }

  /**
   * Tauchgang Teibaum bauen Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   *
   * @param doc        Document Objekt
   * @param diveNumber Nummer des Tauchganges in der Datenbank
   * @return Teilbaum Tauchgang
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 25.10.2011
   */
  private Node makeDiveNode(Document doc, int diveNum)
  {
    // TODO Süßwasser/Salzwasser Dichte eintragen (Datenbankfeld einrichten)
    Element diveNode, dateNode, yNode, mNode, dNode;
    Element timeNode, hNode, minNode;
    Element dnNode, atNode, ltNode, gdNode, deNode, noNode, txNode;
    String year, month, day, hour, minute;
    String temperature, lowesttemp;
    String greatestdepth;
    String density;
    long diveTimeUnix;
    DateTime dateTime;
    //
    // die Zeitstrings basteln
    //
    diveTimeUnix = (getDiveTime()) * 1000L;
    dateTime = new DateTime(diveTimeUnix);
    year = dateTime.toString("yyyy");
    month = dateTime.toString("MM");
    day = dateTime.toString("dd");
    hour = dateTime.toString("hh");
    minute = dateTime.toString("mm");
    // Stsart (Luft) Temperatur
    temperature = getFirstTempForDive();
    // kältesten Punkt
    lowesttemp = getLowestTempForDive();
    // größteTiefe
    greatestdepth = getGreatestDepthForDive();
    // Dichte des Wassers
    density = "1034.0";
    diveNode = doc.createElement("dive");
    diveNode.setAttribute("id", String.valueOf(diveNum));
    // # date
    dateNode = doc.createElement("date");
    // ## date -> year
    yNode = doc.createElement("year");
    yNode.appendChild(doc.createTextNode(year));
    dateNode.appendChild(yNode);
    // ## date -> month
    mNode = doc.createElement("month");
    mNode.appendChild(doc.createTextNode(month));
    dateNode.appendChild(mNode);
    // ## date -> day
    dNode = doc.createElement("day");
    dNode.appendChild(doc.createTextNode(day));
    dateNode.appendChild(dNode);
    diveNode.appendChild(dateNode);
    // # time
    timeNode = doc.createElement("time");
    // ## time -> hour
    hNode = doc.createElement("hour");
    hNode.appendChild(doc.createTextNode(hour));
    timeNode.appendChild(hNode);
    // ## time -> minute
    minNode = doc.createElement("minute");
    minNode.appendChild(doc.createTextNode(minute));
    timeNode.appendChild(minNode);
    diveNode.appendChild(timeNode);
    // # divenumber
    dnNode = doc.createElement("divenumber");
    dnNode.appendChild(doc.createTextNode(String.valueOf(diveNum)));
    diveNode.appendChild(dnNode);
    // # airtemp
    atNode = doc.createElement("airtemperature");
    atNode.appendChild(doc.createTextNode(temperature));
    diveNode.appendChild(atNode);
    // # lowesttemp
    ltNode = doc.createElement("lowesttemperature");
    ltNode.appendChild(doc.createTextNode(lowesttemp));
    diveNode.appendChild(ltNode);
    // # greatestdepth
    gdNode = doc.createElement("greatestdepth");
    gdNode.appendChild(doc.createTextNode(greatestdepth));
    diveNode.appendChild(gdNode);
    // # density
    deNode = doc.createElement("density");
    deNode.appendChild(doc.createTextNode(density));
    diveNode.appendChild(deNode);
    // # notes
    noNode = doc.createElement("notes");
    txNode = doc.createElement("text");
    if ( diveComment != null )
    {
      txNode.appendChild(doc.createTextNode(diveComment));
    }
    else
    {
      txNode.appendChild(doc.createTextNode(""));
    }
    noNode.appendChild(txNode);
    diveNode.appendChild(noNode);
    // Teilbaum einhängen
    diveNode.appendChild(makeSamplesForDive(doc, diveNum));
    return (diveNode);
  }

  /**
   * Eerzeuge Teilbaum von Gsasdefinitionen für mehrere Tauchgänge Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   *
   * @param doc
   * @param diveNums
   * @return
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 30.08.2012
   */
  private Node makeGasdefinitions(Document doc, int[] diveNums)
  {
    Element gasNode, mixNode, nameNode, o2Node, n2Node, heNode, arNode, h2Node;
    String gasName;
    String[] fields;
    // gases füllen mit stringliste a'la O2:N2:HE:AR:H2 als Strings "%.3f"
    gases = sqliteDbUtil.getGaslistForDiveLog(diveNums);
    // # gasdefinitions
    gasNode = doc.createElement("gasdefinitions");
    if ( gases == null )
    {
      // Notbremse, falls es keine Gaase gibt
      return (gasNode);
    }
    for ( String gas : gases )
    {
      gasName = makeGasName(gas);
      fields = fieldPatternDp.split(gas);
      // ## gasdefinitions -> mix
      mixNode = doc.createElement("mix");
      mixNode.setAttribute("id", gasName);
      gasNode.appendChild(mixNode);
      // ### gasdefinitions -> mix -> name
      nameNode = doc.createElement("name");
      nameNode.appendChild(doc.createTextNode(gasName));
      mixNode.appendChild(nameNode);
      // ### gasdefinitions -> mix -> O2
      o2Node = doc.createElement("o2");
      o2Node.appendChild(doc.createTextNode(fields[0]));
      mixNode.appendChild(o2Node);
      // ### gasdefinitions -> mix -> n2
      n2Node = doc.createElement("n2");
      n2Node.appendChild(doc.createTextNode(fields[1]));
      mixNode.appendChild(n2Node);
      // ### gasdefinitions -> mix -> he
      heNode = doc.createElement("he");
      heNode.appendChild(doc.createTextNode(fields[2]));
      mixNode.appendChild(heNode);
      // ### gasdefinitions -> mix -> he
      arNode = doc.createElement("ar");
      arNode.appendChild(doc.createTextNode(fields[3]));
      mixNode.appendChild(arNode);
      // ### gasdefinitions -> mix -> ar
      h2Node = doc.createElement("h2");
      h2Node.appendChild(doc.createTextNode(fields[4]));
      mixNode.appendChild(h2Node);
    }
    return gasNode;
  }

  /**
   * Kleines Helferlein, macht einen Gasnamen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   *
   * @param fields
   * @return
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 31.08.2012
   */
  private String makeGasName(String gas)
  {
    String gasName = gas.replace(gasPattern, "");
    String[] fields = fieldPatternDp.split(gasName);
    // Fehler abfangen
    try
    {
      return (String.format("%02d%02d%02d", Integer.parseInt(fields[0]) / 10, Integer.parseInt(fields[1]) / 10, Integer.parseInt(fields[2]) / 10));
    }
    catch ( NumberFormatException ex )
    {
      lg.error("Number format from value <" + fields + "> are wrong (not a integer): " + ex.getLocalizedMessage());
      return ("210000");
    }
  }

  /**
   * Erzeuge Teilbaum "generator" (Erzeuger der Datei) Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   *
   * @param doc Referenz zum Dokument
   * @return Der erzeugte Teilbaum.
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 24.10.2011
   */
  private Node makeGeneratorNode(Document doc)
  {
    Element genNode, nameNode, mNameNode, manuNode, contactNode, mailNode, hpNode, versionNode, dateNode, yNode, mNode, dNode;
    // Wurzel dieser Ebene
    genNode = doc.createElement("generator");
    // Creators Name einf�gen
    nameNode = doc.createElement("name");
    nameNode.appendChild(doc.createTextNode(ProjectConst.CREATORNAME));
    genNode.appendChild(nameNode);
    // # Hersteller
    manuNode = doc.createElement("manufacturer");
    // ## Hersteller -> Name
    mNameNode = doc.createElement("name");
    mNameNode.appendChild(doc.createTextNode(ProjectConst.MANUFACTNAME));
    manuNode.appendChild(mNameNode);
    // ## Hersteller -> contact
    contactNode = doc.createElement("contact");
    // ### hersteller -> contact -> mail
    mailNode = doc.createElement("email");
    mailNode.appendChild(doc.createTextNode(ProjectConst.MANUFACTMAIL));
    contactNode.appendChild(mailNode);
    // ### hersteller -> contact -> homepagel
    hpNode = doc.createElement("homepage");
    hpNode.appendChild(doc.createTextNode(ProjectConst.MANUFACTHOME));
    contactNode.appendChild(hpNode);
    manuNode.appendChild(contactNode);
    genNode.appendChild(manuNode);
    // ## version
    versionNode = doc.createElement("version");
    versionNode.appendChild(doc.createTextNode(ProjectConst.MANUFACTVERS));
    genNode.appendChild(versionNode);
    // ## date
    dateNode = doc.createElement("date");
    // ### date -> year
    yNode = doc.createElement("year");
    yNode.appendChild(doc.createTextNode(ProjectConst.GENYEAR));
    dateNode.appendChild(yNode);
    // ### date -> month
    mNode = doc.createElement("month");
    mNode.appendChild(doc.createTextNode(ProjectConst.GENMONTH));
    dateNode.appendChild(mNode);
    // ### date -> day
    dNode = doc.createElement("day");
    dNode.appendChild(doc.createTextNode(ProjectConst.GENDAY));
    dateNode.appendChild(dNode);
    genNode.appendChild(dateNode);
    return (genNode);
  }

  /**
   * Teilbaum profilesData erzeugen Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   *
   * @param doc Dokument Objekt
   * @return Teilbam -Rootelement
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 25.10.2011
   */
  private Node makeProfilesData(Document doc, int[] diveNums)
  {
    Element profileNode;
    int repNumber = 0;
    //
    // Alle Tauchgänge als Repetivgroup einfügen
    //
    profileNode = doc.createElement("profiledata");
    for ( int diveNum : diveNums )
    {
      repNumber++;
      // Kopfdaten zu diesen Tauchgang holen
      headData = sqliteDbUtil.getHeadDiveDataFromIdLog(diveNum);
      // Kommentar, falls vorhanden...
      diveComment = sqliteDbUtil.getNotesForIdLog(diveNum);
      profileNode.appendChild(makeRepetitiongroup(doc, repNumber, diveNum));
    }
    return (profileNode);
  }

  /**
   * Teilbaum Wiederholungsgruppe einbauen Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   *
   * @param doc       Dokument Objekt
   * @param repNumber Nummer des Repetivtauchgangee (bei mir immer 1 :-( )
   * @param number    Nummer des Logs in der Datenbank
   * @return Teilbaum Repetitiongroup
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 25.10.2011
   */
  private Node makeRepetitiongroup(Document doc, int repNumber, int diveNum)
  {
    Element repNode;
    repNode = doc.createElement("repetitiongroup");
    repNode.setAttribute("id", String.valueOf(repNumber));
    repNode.appendChild(makeDiveNode(doc, diveNum));
    return (repNode);
  }

  /**
   * hole die samples für den Tauchgang Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   *
   * @param doc
   * @param diveNum
   * @return
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.08.2012
   */
  private Node makeSamplesForDive(final Document doc, int diveNum)
  {
    final Element sampleNode;
    int diveSamples = 0;
    int diveTimeCurrent = 0;
    Vector<Integer[]> diveSamplesVector;
    UDDFLogEntry entry = null;
    String gasSample = "";
    double setpoint = 0.0;
    //
    sampleNode = doc.createElement("samples");
    // der erste waypoint hat immer Zeit 0, tiefe 0 und switchmix
    // hole die anzahl der Samples aus der Datenbank
    diveSamples = getDiveSamples();
    if ( diveSamples == 0 )
    {
      return (sampleNode);
    }
    //
    // jetzt les ich alle Samples aus der Datenbank
    //
    diveSamplesVector = sqliteDbUtil.getDiveDataFromIdLog(diveNum);
    // einen Iterator zum durchkurbeln machen
    Iterator<Integer[]> it = diveSamplesVector.iterator();
    //
    // Alle Samples durchmachen
    //
    while ( it.hasNext() )
    {
      entry = new UDDFLogEntry();
      Integer[] sampleSet = it.next();
      //
      // Daten in das Objekt übernehmen
      //
      entry.presure = sampleSet[LogDerbyDatabaseUtil.PRESURE];
      entry.depth = (double) sampleSet[LogDerbyDatabaseUtil.DEPTH] / 10.0;
      entry.temp = (double) sampleSet[LogDerbyDatabaseUtil.TEMPERATURE] + ProjectConst.KELVIN;
      entry.acku = (double) sampleSet[LogDerbyDatabaseUtil.ACKU] / 10.0;
      entry.ppo2 = sampleSet[LogDerbyDatabaseUtil.PPO2];
      entry.setpoint = sampleSet[LogDerbyDatabaseUtil.SETPOINT];
      entry.n2 = (double) (sampleSet[LogDerbyDatabaseUtil.N2PERCENT]) / 100.0;
      entry.he = (double) (sampleSet[LogDerbyDatabaseUtil.HEPERCENT]) / 100.0;
      entry.o2 = 1.0 - (entry.n2 + entry.he);
      entry.zerotime = sampleSet[LogDerbyDatabaseUtil.NULLTIME];
      diveTimeCurrent += sampleSet[LogDerbyDatabaseUtil.DELTATIME];
      entry.time = diveTimeCurrent;
      entry.makeGasSample();
      //
      // Jetzt mach ich einen Waypoint Knoten aus dem Teil
      // gab es einen Gaswechsel?
      //
      if ( !entry.gasSample.equals(gasSample) )
      {
        entry.gasswitch = true;
        gasSample = entry.gasSample;
      }
      if ( entry.setpoint != setpoint )
      {
        entry.ppo2switch = true;
        setpoint = entry.setpoint;
      }
      // und papp den dran
      sampleNode.appendChild(makeWaypoint(doc, entry));
    }
    return (sampleNode);
  }

  /**
   * Node für einen Wegpunkt machen Project: SubmatixBTLogger Package: de.dmarcini.bluethooth.support
   *
   * @param doc
   * @param entry
   * @return Kompletter waypoint Knoten
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 29.08.2012
   */
  private Node makeWaypoint(Document doc, UDDFLogEntry entry)
  {
    Element wpNode, dNode, dtNode, tNode, sNode, po2Node;
    // # waypoint
    wpNode = doc.createElement("waypoint");
    // ## waypoint -> depth
    dNode = doc.createElement("depth");
    dNode.appendChild(doc.createTextNode(String.format(Locale.ENGLISH, "%.2f", entry.depth)));
    wpNode.appendChild(dNode);
    // ## waypoint -> divetime
    dtNode = doc.createElement("divetime");
    dtNode.appendChild(doc.createTextNode(String.format(Locale.ENGLISH, "%d.0", entry.time)));
    wpNode.appendChild(dtNode);
    // ## waypoint -> temperature
    tNode = doc.createElement("temperature");
    tNode.appendChild(doc.createTextNode(String.format(Locale.ENGLISH, "%.1f", entry.temp)));
    wpNode.appendChild(tNode);
    // wenn sich das Gas geändert hat oder am anfang IMMER
    if ( entry.gasswitch == true )
    {
      // ## waypoint -> switch
      sNode = doc.createElement("switchmix");
      sNode.setAttribute("ref", makeGasName(entry.gasSample));
      wpNode.appendChild(sNode);
    }
    // wenn sich der Setpoint ge�ndert hat...
    if ( entry.ppo2switch )
    {
      // ## waypoint -> setpo2
      po2Node = doc.createElement("setpo2");
      po2Node.appendChild(doc.createTextNode(String.format(Locale.ENGLISH, "%.2f", entry.setpoint)));
      wpNode.appendChild(po2Node);
    }
    return (wpNode);
  }
}
