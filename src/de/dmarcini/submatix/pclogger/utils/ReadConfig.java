/**
 * Config fürs Programm einlesen
 * 
 * ReadConfigClass.de.dmarcini.submatix.pclogger
 * 
 * @author Dirk Marciniak 09.12.2011
 */
package de.dmarcini.submatix.pclogger.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Lese Config ein. Konfig im Muster BEZEICHNUNG=WERT
 * 
 * @author dmarc
 */
public class ReadConfig
{
  private final ArrayList<String>        pairs     = new ArrayList<String>();
  private final SpxPcloggerProgramConfig prgConfig = new SpxPcloggerProgramConfig();

  /**
   * Konstruktor
   * 
   * @author Dirk Marciniak 09.12.2011
   * @throws IOException
   * @throws ConfigReadWriteException
   */
  public ReadConfig() throws IOException, ConfigReadWriteException
  {
    BufferedReader in;
    // gibts die Datei?
    if( SpxPcloggerProgramConfig.configFile.exists() && SpxPcloggerProgramConfig.configFile.canRead() )
    {
      if( null != ( in = openConfFile( SpxPcloggerProgramConfig.configFile ) ) )
      {
        if( readConfInArray( in ) )
        {
          in.close();
          // alles ist gut :-)
          return;
        }
        in.close();
      }
    }
  }

  /**
   * Datei öffnen, Reader zurück geben
   * 
   * @author Dirk Marciniak 05.12.2011
   * @param confFile
   * @return BufferedReader
   */
  private BufferedReader openConfFile( File confFile )
  {
    BufferedReader in;
    try
    {
      in = new BufferedReader( new FileReader( confFile ) );
      return( in );
    }
    catch( NullPointerException ex )
    {
      System.err.println( "can not open config file: " + ex.getLocalizedMessage() );
    }
    catch( FileNotFoundException ex )
    {
      System.err.println( "can not open config file: " + ex.getLocalizedMessage() );
    }
    return null;
  }

  /**
   * geöffnete Datei in Array einlesen
   * 
   * @author Dirk Marciniak 05.12.2011
   * @param in
   * @return boolean
   */
  private boolean readConfInArray( BufferedReader in )
  {
    String zeile = null;
    try
    {
      // die Config-Datei einlesen
      while( ( zeile = in.readLine() ) != null )
      {
        pairs.add( zeile );
      }
    }
    catch( NullPointerException ex )
    {
      System.err.println( "can not read config file: " + ex.getLocalizedMessage() );
      return( false );
    }
    catch( IOException ex )
    {
      System.err.println( "can not read config file: " + ex.getLocalizedMessage() );
      return( false );
    }
    //
    // auswertung der Parameter, wenn vorhanden
    //
    // sind Einträge vorhanden?
    for( String ln : pairs )
    {
      try
      {
        // rudimentäre Kommentare überspringen
        if( ln.startsWith( "#" ) ) continue;
        // Zeilen ohne "=" ignorieren
        if( -1 == ln.indexOf( "=" ) ) continue;
        // Zeile splitten
        String[] fields = ln.split( "=" );
        // nur wenn es zwei Felder sind, mach ich weiter
        if( fields.length == 2 )
        {
          fields[0].trim();
          fields[1].trim();
        }
        else
          continue;
        // unterscheide die Parameter
        if( 0 == fields[0].indexOf( "databaseDir" ) )
        {
          prgConfig.setDatabaseDir( new File( fields[1] ) );
        }
        if( 0 == fields[0].indexOf( "logFile" ) )
        {
          prgConfig.setLogFile( new File( fields[1] ) );
        }
        if( 0 == fields[0].indexOf( "showUnits" ) )
        {
          if( 0 == fields[1].indexOf( "default" ) )
          {
            prgConfig.setUnitsProperty( SpxPcloggerProgramConfig.UNITS_DEFAULT );
          }
          else if( 0 == fields[1].indexOf( "metric" ) )
          {
            prgConfig.setUnitsProperty( SpxPcloggerProgramConfig.UNITS_METRIC );
          }
          else if( 0 == fields[1].indexOf( "imperial" ) )
          {
            prgConfig.setUnitsProperty( SpxPcloggerProgramConfig.UNITS_IMPERIAL );
          }
          else
          {
            prgConfig.setUnitsProperty( SpxPcloggerProgramConfig.UNITS_DEFAULT );
          }
        }
        if( 0 == fields[0].indexOf( "geheimerParameter" ) )
        {
          prgConfig.geheimerParameter = Integer.parseInt( fields[1] );
        }
      }
      catch( NumberFormatException ex )
      {
        // nicht sooo sauber, aber sollte funktionieren
        continue;
      }
    }
    // nach dem Einlesen ist das nicht geändert!
    prgConfig.setWasChanged( false );
    return true;
  }

  /**
   * Gib das Config-Objekt gefüllt zurück!
   * 
   * @author Dirk Marciniak 24.01.2012
   * @return SnifferConfigClass
   */
  public SpxPcloggerProgramConfig getConfigClass()
  {
    return( prgConfig );
  }
}
