/**
 * Helferklasse zum erzeugen von UDDF 2.2 Files
 */
package de.dmarcini.submatix.pclogger.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

/**
 * 
 * Klasse zum Erzeugen von UDDF Version 2.0 Dateien
 * 
 * Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 27.10.2011
 */
public class UDDFFileCreateClass
{
  private final String             gasPattern     = "0.";
  private Document                 uddfDoc        = null;
  private Logger                   LOGGER         = null;
  private LogForDeviceDatabaseUtil sqliteDbUtil   = null;
  private Transformer              transformer    = null;
  private DocumentBuilder          builder        = null;
  private ArrayList<String>        gases          = null;
  private final Pattern            fieldPatternDp = Pattern.compile( ":" );
  private int[]                    headData       = null;
  private String                   diveComment    = null;

  @SuppressWarnings( "unused" )
  private UDDFFileCreateClass()
  {};

  /**
   * 
   * Der Konstruktor der Helperklasse
   * 
   * Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 24.10.2011
   * @param lg
   * @param sqliteDbUtil
   * @throws ParserConfigurationException
   * @throws TransformerException
   * @throws TransformerFactoryConfigurationError
   * @throws Exception
   */
  public UDDFFileCreateClass( Logger lg, final LogForDeviceDatabaseUtil sqliteDbUtil ) throws ParserConfigurationException, TransformerException,
          TransformerFactoryConfigurationError, Exception
  {
    // initialisiere die Klasse
    this.sqliteDbUtil = sqliteDbUtil;
    if( sqliteDbUtil == null || !sqliteDbUtil.isOpenDB() )
    {
      throw new Exception( "database not initiated" );
    }
    LOGGER = lg;
    LOGGER.fine( "create helperclass for uddf-export..." );
    transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
    transformer.setOutputProperty( OutputKeys.STANDALONE, "yes" );
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware( true );
    builder = factory.newDocumentBuilder();
    LOGGER.fine( "create helperclass for uddf-export...OK" );
  }

  /**
   * 
   * Erzeuge die XML-Datei (UDDF) für einen Logeintrag
   * 
   * Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 24.10.2011
   * @param exportDir
   *          Verzeichnis, in die das Ergebnis nachher kommt
   * @param diveNum
   *          Nummer in der Datenbank (dive_id)
   * @param zipped
   *          Komprimieren?
   * @return true oder false
   * @throws Exception
   */
  public File createXML( File exportDir, int diveNum, boolean zipped ) throws Exception
  {
    int[] diveNums = new int[1];
    diveNums[0] = diveNum;
    return( createXML( exportDir, diveNums, zipped ) );
  }

  /**
   * 
   * Erzeuge die XML-Datei (UDDF)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 30.08.2012
   * @param exportDir
   * @param diveNums
   * @param zipped
   * @return Dateiobjekt für UDDF-Datei
   * @throws Exception
   */
  public File createXML( File exportDir, int[] diveNums, boolean zipped ) throws Exception
  {
    Element rootNode = null;
    String msg = null;
    String fileName = null;
    File retFile = null;
    File saveFile = null;
    //
    LOGGER.fine( "create uddf file..." );
    if( sqliteDbUtil == null || !sqliteDbUtil.isOpenDB() )
    {
      throw new Exception( "database not initiated" );
    }
    //
    // Daten vom ersten Tauchgang auslesen
    //
    headData = sqliteDbUtil.getHeadDiveDataFromId( diveNums[0] );
    // die Tauchzeit rausbekommen
    long diveTimeUnix = ( getDiveTime() ) * 1000L;
    DateTime dateTime = new DateTime( diveTimeUnix );
    // den Export-Dateinamen machen
    if( diveNums.length == 1 )
    {
      fileName = String.format( "%s%s%s-dive-from-%s.uddf", exportDir.getAbsolutePath(), File.separatorChar, sqliteDbUtil.getDeviceId(), dateTime.toString( "yyyy-MM-dd-hh-mm" ) );
    }
    else
    {
      fileName = String.format( "%s%s%s-dive-from-%s-plus-%d.uddf", exportDir.getAbsolutePath(), File.separatorChar, sqliteDbUtil.getDeviceId(),
              dateTime.toString( "yyyy-MM-dd-hh-mm" ), diveNums.length );
    }
    saveFile = new File( fileName );
    //
    // Erzeuge Dokument neu
    //
    uddfDoc = builder.newDocument();
    // Root-Element erzeugen
    rootNode = uddfDoc.createElement( "uddf" );
    rootNode.setAttribute( "version", ProjectConst.UDDFVERSION );
    uddfDoc.appendChild( rootNode );
    // Programmname einfügen
    rootNode.appendChild( uddfDoc.createComment( ProjectConst.CREATORPROGRAM ) );
    // Appliziere Generator
    rootNode.appendChild( makeGeneratorNode( uddfDoc ) );
    // appliziere Gasdefinitionen
    rootNode.appendChild( makeGasdefinitions( uddfDoc, diveNums ) );
    // appliziere profiledata
    rootNode.appendChild( makeProfilesData( uddfDoc, diveNums ) );
    uddfDoc.normalizeDocument();
    try
    {
      // mach eine Datei aus dem DOM-Baum
      retFile = domToFile( saveFile, uddfDoc, zipped );
    }
    catch( TransformerException ex )
    {
      msg = "transformer Exception " + ex.getLocalizedMessage();
      LOGGER.severe( "createXML: <" + msg + ">" );
      return( null );
    }
    catch( IOException ex )
    {
      msg = "IOException " + ex.getLocalizedMessage();
      LOGGER.severe( "createXML: <" + msg + ">" );
      return( null );
    }
    catch( Exception ex )
    {
      msg = "allgemeine Exception " + ex.getLocalizedMessage();
      LOGGER.severe( "createXML: <" + msg + ">" );
      return( null );
    }
    return( retFile );
  }

  /**
   * 
   * Erzeuge die XML-Datei aus dem DOM-Baum im speicher
   * 
   * Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 27.10.2011
   * @param file
   *          File Objekt für die Zieldatei
   * @param document
   *          Document Objekt
   * @return Ok oder nicht OK
   * @throws IOException
   * @throws TransformerException
   */
  private File domToFile( File file, Document document, boolean zipped ) throws IOException, TransformerException
  {
    LOGGER.fine( "make dom to file..." );
    // die Vorbereitungen treffen
    LOGGER.fine( "...create writer..." );
    StringWriter writer = new StringWriter();
    DOMSource doc = new DOMSource( document );
    StreamResult res = new StreamResult( writer );
    LOGGER.fine( "...transform... " );
    transformer.transform( doc, res );
    // nun zur Frage: gezippt oder nicht
    if( zipped )
    {
      // gezipptes File erzeugen
      LOGGER.fine( "...write to zipped file... " );
      file = new File( file.getAbsoluteFile() + ".gz" );
      if( file.exists() )
      {
        // Datei ist da, ich will sie ueberschreiben
        file.delete();
      }
      OutputStream fos = new FileOutputStream( file );
      OutputStream zipOut = new GZIPOutputStream( fos );
      zipOut.write( writer.toString().getBytes() );
      zipOut.close();
      LOGGER.fine( "...ok " );
      return( file );
    }
    else
    {
      // ungezipptes file erzeugen
      LOGGER.fine( "...write to unzipped file... " );
      if( file.exists() )
      {
        // Datei ist da, ich will sie ueberschreiben
        file.delete();
      }
      RandomAccessFile xmlFile = new RandomAccessFile( file, "rw" );
      xmlFile.writeBytes( writer.toString() );
      xmlFile.close();
      LOGGER.fine( "...ok " );
      return( file );
    }
  }

  /**
   * 
   * Anzahl der Samples zum Tauchgang
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.08.2012
   * @return
   */
  private int getDiveSamples()
  {
    return( headData[4] );
  }

  /**
   * 
   * Gibt den Anfang des Tauchganges als unix timestamp zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.08.2012
   * @param dive_id
   * @return
   */
  private int getDiveTime()
  {
    return( headData[0] );
  }

  /**
   * 
   * Allerersten Temperaturwert für Tauchgang erfragen
   * 
   * Temeraturen in KELVIN
   * 
   * Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.10.2011
   * @param dive_id
   *          Nummer des Tauchganges in der Datenbank
   * @return Allererste Temperatur beim Tauchgang (müßte in etwa Lufttemperatur sein)
   */
  private String getFirstTempForDive()
  {
    float tempValue = 0;
    String temperature;
    //
    tempValue = new Float( headData[1] );
    tempValue = tempValue / 10;
    tempValue += ProjectConst.KELVIN;
    temperature = String.format( Locale.ENGLISH, "%.1f", tempValue );
    return( temperature );
  }

  /**
   * 
   * Grösste Tiefe des Tauchganges zurückgeben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.08.2012
   * @return Größte Tiefe des Tauchganges in dm
   */
  private String getGreatestDepthForDive()
  {
    return( String.format( Locale.ENGLISH, "%.1f", ( float )( headData[3] / 10.0 ) ) );
  }

  /**
   * 
   * Die tiefste Temperatur finden
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.08.2012
   * @param dive_id
   * @return
   */
  private String getLowestTempForDive()
  {
    float tempValue = 0;
    String temperature;
    //
    tempValue = new Float( headData[2] );
    tempValue = tempValue / 10;
    tempValue += ProjectConst.KELVIN;
    temperature = String.format( Locale.ENGLISH, "%.1f", tempValue );
    return( temperature );
  }

  /**
   * 
   * Tauchgang Teibaum bauen
   * 
   * Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.10.2011
   * @param doc
   *          Document Objekt
   * @param diveNumber
   *          Nummer des Tauchganges in der Datenbank
   * @return Teilbaum Tauchgang
   */
  private Node makeDiveNode( Document doc, int diveNum )
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
    diveTimeUnix = ( getDiveTime() ) * 1000L;
    dateTime = new DateTime( diveTimeUnix );
    year = dateTime.toString( "yyyy" );
    month = dateTime.toString( "MM" );
    day = dateTime.toString( "dd" );
    hour = dateTime.toString( "hh" );
    minute = dateTime.toString( "mm" );
    // Stsart (Luft) Temperatur
    temperature = getFirstTempForDive();
    // kältesten Punkt
    lowesttemp = getLowestTempForDive();
    // größteTiefe
    greatestdepth = getGreatestDepthForDive();
    // Dichte des Wassers
    density = "1034.0";
    diveNode = doc.createElement( "dive" );
    diveNode.setAttribute( "id", String.valueOf( diveNum ) );
    // # date
    dateNode = doc.createElement( "date" );
    // ## date -> year
    yNode = doc.createElement( "year" );
    yNode.appendChild( doc.createTextNode( year ) );
    dateNode.appendChild( yNode );
    // ## date -> month
    mNode = doc.createElement( "month" );
    mNode.appendChild( doc.createTextNode( month ) );
    dateNode.appendChild( mNode );
    // ## date -> day
    dNode = doc.createElement( "day" );
    dNode.appendChild( doc.createTextNode( day ) );
    dateNode.appendChild( dNode );
    diveNode.appendChild( dateNode );
    // # time
    timeNode = doc.createElement( "time" );
    // ## time -> hour
    hNode = doc.createElement( "hour" );
    hNode.appendChild( doc.createTextNode( hour ) );
    timeNode.appendChild( hNode );
    // ## time -> minute
    minNode = doc.createElement( "minute" );
    minNode.appendChild( doc.createTextNode( minute ) );
    timeNode.appendChild( minNode );
    diveNode.appendChild( timeNode );
    // # divenumber
    dnNode = doc.createElement( "divenumber" );
    dnNode.appendChild( doc.createTextNode( String.valueOf( diveNum ) ) );
    diveNode.appendChild( dnNode );
    // # airtemp
    atNode = doc.createElement( "airtemperature" );
    atNode.appendChild( doc.createTextNode( temperature ) );
    diveNode.appendChild( atNode );
    // # lowesttemp
    ltNode = doc.createElement( "lowesttemperature" );
    ltNode.appendChild( doc.createTextNode( lowesttemp ) );
    diveNode.appendChild( ltNode );
    // # greatestdepth
    gdNode = doc.createElement( "greatestdepth" );
    gdNode.appendChild( doc.createTextNode( greatestdepth ) );
    diveNode.appendChild( gdNode );
    // # density
    deNode = doc.createElement( "density" );
    deNode.appendChild( doc.createTextNode( density ) );
    diveNode.appendChild( deNode );
    // # notes
    noNode = doc.createElement( "notes" );
    txNode = doc.createElement( "text" );
    if( diveComment != null )
    {
      txNode.appendChild( doc.createTextNode( diveComment ) );
    }
    else
    {
      txNode.appendChild( doc.createTextNode( "-" ) );
    }
    noNode.appendChild( txNode );
    diveNode.appendChild( noNode );
    // Teilbaum einhängen
    diveNode.appendChild( makeSamplesForDive( doc, diveNum ) );
    return( diveNode );
  }

  /**
   * 
   * Eerzeuge Teilbaum von Gsasdefinitionen für mehrere Tauchgänge
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 30.08.2012
   * @param doc
   * @param diveNums
   * @return
   */
  private Node makeGasdefinitions( Document doc, int[] diveNums )
  {
    Element gasNode, mixNode, nameNode, o2Node, n2Node, heNode, arNode, h2Node;
    String gasName;
    String[] fields;
    // gases füllen mit stringliste a'la O2:N2:HE:AR:H2 als Strings "%.3f"
    gases = sqliteDbUtil.getGaslistForDive( diveNums );
    // # gasdefinitions
    gasNode = doc.createElement( "gasdefinitions" );
    if( gases == null )
    {
      // Notbremse, falls es keine Gaase gibt
      return( gasNode );
    }
    for( String gas : gases )
    {
      gasName = makeGasName( gas );
      fields = fieldPatternDp.split( gas );
      // ## gasdefinitions -> mix
      mixNode = doc.createElement( "mix" );
      mixNode.setAttribute( "id", gasName );
      gasNode.appendChild( mixNode );
      // ### gasdefinitions -> mix -> name
      nameNode = doc.createElement( "name" );
      nameNode.appendChild( doc.createTextNode( gasName ) );
      mixNode.appendChild( nameNode );
      // ### gasdefinitions -> mix -> O2
      o2Node = doc.createElement( "o2" );
      o2Node.appendChild( doc.createTextNode( fields[0] ) );
      mixNode.appendChild( o2Node );
      // ### gasdefinitions -> mix -> n2
      n2Node = doc.createElement( "n2" );
      n2Node.appendChild( doc.createTextNode( fields[1] ) );
      mixNode.appendChild( n2Node );
      // ### gasdefinitions -> mix -> he
      heNode = doc.createElement( "he" );
      heNode.appendChild( doc.createTextNode( fields[2] ) );
      mixNode.appendChild( heNode );
      // ### gasdefinitions -> mix -> he
      arNode = doc.createElement( "ar" );
      arNode.appendChild( doc.createTextNode( fields[3] ) );
      mixNode.appendChild( arNode );
      // ### gasdefinitions -> mix -> ar
      h2Node = doc.createElement( "h2" );
      h2Node.appendChild( doc.createTextNode( fields[4] ) );
      mixNode.appendChild( h2Node );
    }
    return gasNode;
  }

  /**
   * 
   * Kleines Helferlein, macht einen Gasnamen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.08.2012
   * @param fields
   * @return
   */
  private String makeGasName( String gas )
  {
    String gasName = gas.replace( gasPattern, "" );
    String[] fields = fieldPatternDp.split( gasName );
    // TODO: Fehler bei parseInt abfangen!
    return( String.format( "%02d%02d%02d", Integer.parseInt( fields[0] ) / 10, Integer.parseInt( fields[1] ) / 10, Integer.parseInt( fields[2] ) / 10 ) );
  }

  /**
   * 
   * Erzeuge Teilbaum "generator" (Erzeuger der Datei)
   * 
   * Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 24.10.2011
   * @param doc
   *          Referenz zum Dokument
   * @return Der erzeugte Teilbaum.
   */
  private Node makeGeneratorNode( Document doc )
  {
    Element genNode, nameNode, mNameNode, manuNode, contactNode, mailNode, hpNode, versionNode, dateNode, yNode, mNode, dNode;
    // Wurzel dieser Ebene
    genNode = doc.createElement( "generator" );
    // Creators Name einf�gen
    nameNode = doc.createElement( "name" );
    nameNode.appendChild( doc.createTextNode( ProjectConst.CREATORNAME ) );
    genNode.appendChild( nameNode );
    // # Hersteller
    manuNode = doc.createElement( "manufacturer" );
    // ## Hersteller -> Name
    mNameNode = doc.createElement( "name" );
    mNameNode.appendChild( doc.createTextNode( ProjectConst.MANUFACTNAME ) );
    manuNode.appendChild( mNameNode );
    // ## Hersteller -> contact
    contactNode = doc.createElement( "contact" );
    // ### hersteller -> contact -> mail
    mailNode = doc.createElement( "email" );
    mailNode.appendChild( doc.createTextNode( ProjectConst.MANUFACTMAIL ) );
    contactNode.appendChild( mailNode );
    // ### hersteller -> contact -> homepagel
    hpNode = doc.createElement( "homepage" );
    hpNode.appendChild( doc.createTextNode( ProjectConst.MANUFACTHOME ) );
    contactNode.appendChild( hpNode );
    manuNode.appendChild( contactNode );
    genNode.appendChild( manuNode );
    // ## version
    versionNode = doc.createElement( "version" );
    versionNode.appendChild( doc.createTextNode( ProjectConst.MANUFACTVERS ) );
    genNode.appendChild( versionNode );
    // ## date
    dateNode = doc.createElement( "date" );
    // ### date -> year
    yNode = doc.createElement( "year" );
    yNode.appendChild( doc.createTextNode( ProjectConst.GENYEAR ) );
    dateNode.appendChild( yNode );
    // ### date -> month
    mNode = doc.createElement( "month" );
    mNode.appendChild( doc.createTextNode( ProjectConst.GENMONTH ) );
    dateNode.appendChild( mNode );
    // ### date -> day
    dNode = doc.createElement( "day" );
    dNode.appendChild( doc.createTextNode( ProjectConst.GENDAY ) );
    dateNode.appendChild( dNode );
    genNode.appendChild( dateNode );
    return( genNode );
  }

  /**
   * 
   * Teilbaum profilesData erzeugen
   * 
   * Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.10.2011
   * @param doc
   *          Dokument Objekt
   * @return Teilbam -Rootelement
   */
  private Node makeProfilesData( Document doc, int[] diveNums )
  {
    Element profileNode;
    int repNumber = 0;
    //
    // Alle Tauchgänge als Repetivgroup einfügen
    //
    profileNode = doc.createElement( "profiledata" );
    for( int diveNum : diveNums )
    {
      repNumber++;
      // Kopfdaten zu diesen Tauchgang holen
      headData = sqliteDbUtil.getHeadDiveDataFromId( diveNum );
      // Kommentar, falls vorhanden...
      diveComment = sqliteDbUtil.getNotesForId( diveNum );
      profileNode.appendChild( makeRepetitiongroup( doc, repNumber, diveNum ) );
    }
    return( profileNode );
  }

  /**
   * 
   * Teilbaum Wiederholungsgruppe einbauen
   * 
   * Project: SubmatixXMLTest Package: de.dmarcini.bluethooth.submatix.xml
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 25.10.2011
   * @param doc
   *          Dokument Objekt
   * @param repNumber
   *          Nummer des Repetivtauchgangee (bei mir immer 1 :-( )
   * @param number
   *          Nummer des Logs in der Datenbank
   * @return Teilbaum Repetitiongroup
   */
  private Node makeRepetitiongroup( Document doc, int repNumber, int diveNum )
  {
    Element repNode;
    repNode = doc.createElement( "repetitiongroup" );
    repNode.setAttribute( "id", String.valueOf( repNumber ) );
    repNode.appendChild( makeDiveNode( doc, diveNum ) );
    return( repNode );
  }

  /**
   * 
   * hole die samples für den Tauchgang
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 28.08.2012
   * @param doc
   * @param diveNum
   * @return
   */
  private Node makeSamplesForDive( final Document doc, int diveNum )
  {
    final Element sampleNode;
    int diveSamples = 0;
    int diveTimeCurrent = 0;
    Vector<Integer[]> diveSamplesVector;
    UDDFLogEntry entry = null;
    String gasSample = "";
    double setpoint = 0.0;
    //
    sampleNode = doc.createElement( "samples" );
    // der erste waypoint hat immer Zeit 0, tiefe 0 und switchmix
    // hole die anzahl der Samples aus der Datenbank
    diveSamples = getDiveSamples();
    if( diveSamples == 0 ) return( sampleNode );
    //
    // jetzt les ich alle Samples aus der Datenbank
    //
    diveSamplesVector = sqliteDbUtil.getDiveDataFromId( diveNum );
    // einen Iterator zum durchkurbeln machen
    Iterator<Integer[]> it = diveSamplesVector.iterator();
    //
    // Alle Samples durchmachen
    //
    while( it.hasNext() )
    {
      entry = new UDDFLogEntry();
      Integer[] sampleSet = it.next();
      //
      // Daten in das Objekt übernehmen
      //
      entry.presure = sampleSet[LogForDeviceDatabaseUtil.PRESURE];
      entry.depth = ( double )sampleSet[LogForDeviceDatabaseUtil.DEPTH] / 10.0;
      entry.temp = ( double )sampleSet[LogForDeviceDatabaseUtil.TEMPERATURE] + ProjectConst.KELVIN;
      entry.acku = ( double )sampleSet[LogForDeviceDatabaseUtil.ACKU] / 10.0;
      entry.ppo2 = sampleSet[LogForDeviceDatabaseUtil.PPO2];
      entry.setpoint = sampleSet[LogForDeviceDatabaseUtil.SETPOINT];
      entry.n2 = ( double )( sampleSet[LogForDeviceDatabaseUtil.N2PERCENT] ) / 100.0;
      entry.he = ( double )( sampleSet[LogForDeviceDatabaseUtil.HEPERCENT] ) / 100.0;
      entry.o2 = 1.0 - ( entry.n2 + entry.he );
      entry.zerotime = sampleSet[LogForDeviceDatabaseUtil.NULLTIME];
      diveTimeCurrent += sampleSet[LogForDeviceDatabaseUtil.DELTATIME];
      entry.time = diveTimeCurrent;
      entry.makeGasSample();
      //
      // Jetzt mach ich einen Waypoint Knoten aus dem Teil
      // gab es einen Gaswechsel?
      //
      if( !entry.gasSample.equals( gasSample ) )
      {
        entry.gasswitch = true;
        gasSample = entry.gasSample;
      }
      if( entry.setpoint != setpoint )
      {
        entry.ppo2switch = true;
        setpoint = entry.setpoint;
      }
      // und papp den dran
      sampleNode.appendChild( makeWaypoint( doc, entry ) );
    }
    return( sampleNode );
  }

  /**
   * 
   * Node für einen Wegpunkt machen
   * 
   * Project: SubmatixBTLogger Package: de.dmarcini.bluethooth.support
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 29.08.2012
   * @param doc
   * @param entry
   * @return Kompletter waypoint Knoten
   */
  private Node makeWaypoint( Document doc, UDDFLogEntry entry )
  {
    Element wpNode, dNode, dtNode, tNode, sNode, po2Node;
    // # waypoint
    wpNode = doc.createElement( "waypoint" );
    // ## waypoint -> depth
    dNode = doc.createElement( "depth" );
    dNode.appendChild( doc.createTextNode( String.format( Locale.ENGLISH, "%.2f", entry.depth ) ) );
    wpNode.appendChild( dNode );
    // ## waypoint -> divetime
    dtNode = doc.createElement( "divetime" );
    dtNode.appendChild( doc.createTextNode( String.format( Locale.ENGLISH, "%d.0", entry.time ) ) );
    wpNode.appendChild( dtNode );
    // ## waypoint -> temperature
    tNode = doc.createElement( "temperature" );
    tNode.appendChild( doc.createTextNode( String.format( Locale.ENGLISH, "%.1f", entry.temp ) ) );
    wpNode.appendChild( tNode );
    // wenn sich das Gas geändert hat oder am anfang IMMER
    if( entry.gasswitch == true )
    {
      // ## waypoint -> switch
      sNode = doc.createElement( "switchmix" );
      sNode.setAttribute( "ref", makeGasName( entry.gasSample ) );
      wpNode.appendChild( sNode );
    }
    // wenn sich der Setpoint ge�ndert hat...
    if( entry.ppo2switch )
    {
      // ## waypoint -> setpo2
      po2Node = doc.createElement( "setpo2" );
      po2Node.appendChild( doc.createTextNode( String.format( Locale.ENGLISH, "%.2f", entry.setpoint ) ) );
      wpNode.appendChild( po2Node );
    }
    return( wpNode );
  }
}
