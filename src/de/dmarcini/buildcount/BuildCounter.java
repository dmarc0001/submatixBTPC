/**
 * Dienstprogramm zum ergänzen des Buildcounters
 * 
 * @author Dirk Marciniak 31.07.2012
 */
package de.dmarcini.buildcount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;

/**
 * @author dmarc
 */
public class BuildCounter
{
  private File          buildFile;
  private File          configFile;
  private final Pattern patternForFiles = Pattern.compile( "#" );

  /**
   * Standartkonstruktor
   * 
   * @author Dirk Marciniak 31.07.2012
   */
  public BuildCounter()
  {
    buildFile = null;
    configFile = null;
  }

  /**
   * Vom ANT den Filenamen zum Auswerten geben lassen
   * 
   * @author Dirk Marciniak 31.07.2012
   * @param msg
   *          void
   */
  public void setMessage( String msg )
  {
    String[] fields;
    //
    if( msg == null )
    {
      throw new BuildException( "Keine Parameter bekommen!" );
    }
    fields = patternForFiles.split( msg );
    System.out.println( String.format( "Es wurden %d Elemente übergeben.", fields.length ) );
    if( fields.length < 2 )
    {
      throw new BuildException( "Es wurden zu wenig Parameter übergenen!" );
    }
    buildFile = new File( fields[0] );
    configFile = new File( fields[1] );
    // Auswertung
    if( !buildFile.isFile() )
    {
      throw new BuildException( "Die Buildnummerdatei <" + buildFile.getName() + "> existiert nicht." );
    }
    if( !configFile.isFile() )
    {
      throw new BuildException( "Die Configdatei <" + configFile.getName() + "> existiert nicht." );
    }
  }

  /**
   * von ANT Ausführen!
   * 
   * @author Dirk Marciniak 31.07.2012 void
   */
  public void execute()
  {
    long buildNumber = 0L;
    // nochmal zur Sicherheit
    if( buildFile == null )
    {
      throw new BuildException( "Keine Parameter bekommen!" );
    }
    //
    // ich will jetzt die Datei lesen, die Buildnummer hochzählen und die Datei überschreiben
    //
    buildNumber = readFromFile( buildFile );
    // Kurz bescheid geben
    System.out.println( String.format( "Build Nummer war <%d>.", buildNumber ) );
    writeToFile( buildFile, ++buildNumber );
    System.out.println( String.format( "Build Nummer ist nun <%d>.", buildNumber ) );
    // Jetzt die Versionsdatei beackern
    changeVersionFile( configFile, buildNumber, new Date() );
    System.out.println( "ENDE" );
  }

  /**
   * Die Versionsdatei erstellen
   * 
   * @author Dirk Marciniak 31.07.2012
   * @param cFile
   * @param buildNumber
   * @param date
   *          void
   */
  private void changeVersionFile( File cFile, long buildNumber, Date date )
  {
    BufferedReader inFile;
    FileWriter writer;
    String inLine;
    File wFile;
    //
    wFile = new File( cFile.getAbsoluteFile() + ".tmp" );
    try
    {
      inFile = new BufferedReader( new FileReader( cFile ) );
      inLine = inFile.readLine();
      writer = new FileWriter( wFile, false );
      do
      {
        if( Pattern.matches( " +private +final +long buildNumber +=.*", inLine ) )
        {
          writer.write( String.format( "  private final long buildNumber = %dL;%s", buildNumber, System.getProperty( "line.separator" ) ) );
        }
        else if( Pattern.matches( " +private +final +long buildDate +=.*", inLine ) )
        {
          writer.write( String.format( "  private final long buildDate = %dL;%s", date.getTime(), System.getProperty( "line.separator" ) ) );
        }
        else
        {
          writer.write( inLine + System.getProperty( "line.separator" ) );
        }
        inLine = inFile.readLine();
      }
      while( inLine != null );
      writer.close();
      inFile.close();
      cFile.delete();
      wFile.renameTo( cFile );
    }
    catch( FileNotFoundException ex )
    {
      throw new BuildException( "Konnte Versionsdatei nicht öffnen!" );
    }
    catch( IOException ex )
    {
      throw new BuildException( "Konnte Versionsdatei nicht lesen oder Ausgabedatei nicht erzeugen!" );
    }
  }

  /**
   * HEADER
   * 
   * @author Dirk Marciniak 31.07.2012
   * @param l
   *          void
   */
  private void writeToFile( File file, long newBuildNumber )
  {
    FileWriter writer;
    try
    {
      writer = new FileWriter( file, false );
      writer.write( String.format( "%04d%s", newBuildNumber, System.getProperty( "line.separator" ) ) );
      writer.close();
    }
    catch( IOException ex )
    {
      throw new BuildException( "Konnte Datei mit Buildnummer nicht schreiben!" );
    }
  }

  /**
   * Die Buildnummer aus der lokalen datei lesen
   * 
   * @author Dirk Marciniak 31.07.2012
   * @param file
   * @return long
   */
  private long readFromFile( File file )
  {
    Scanner scanner;
    long buildNumber = 0L;
    //
    // Datei öffnen
    //
    try
    {
      scanner = new Scanner( file );
      buildNumber = scanner.nextLong();
      scanner.close();
      return( buildNumber );
    }
    catch( FileNotFoundException ex )
    {
      throw new BuildException( "Konnte Datei mit Buildnummer nicht öffnen!" );
    }
  }
}
