/**
 * Config fürs Programm einlesen
 * 
 * ReadConfigClass.java de.dmarcini.netutils.dsl
 * 
 * @author Dirk Marciniak 09.12.2011
 */
package de.dmarcini.submatix.pclogger.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Schreibe Konfig im Muster BEZEICHNUNG=WERT
 * 
 * @author dmarc
 */
public class WriteConfig
{
  public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

  /**
   * Privater Konstruktor, verhindert die Benutzung....
   * 
   * @author Dirk Marciniak 09.12.2011
   */
  @SuppressWarnings( "unused" )
  private WriteConfig()
  {
    return;
  }

  /**
   * Konstruktor mit Dateinamenübergabe
   * 
   * @author Dirk Marciniak 09.12.2011
   * @param prgConfig
   * @throws IOException
   * @throws ConfigReadWriteException
   */
  public WriteConfig( SpxPcloggerProgramConfig prgConfig ) throws IOException, ConfigReadWriteException
  {
    BufferedWriter out;
    if( null != ( out = openConfFile( SpxPcloggerProgramConfig.configFile ) ) )
    {
      if( writeConfArray( out, prgConfig ) )
      {
        out.close();
        // alles ist gut :-)
        return;
      }
      out.close();
    }
  }

  /**
   * Datei öffnen, Reader zurück geben
   * 
   * @author Dirk Marciniak 05.12.2011
   * @param confFile
   * @return BufferedReader
   */
  private BufferedWriter openConfFile( File confFile )
  {
    BufferedWriter out;
    try
    {
      out = new BufferedWriter( new FileWriter( confFile, false ) );
      return( out );
    }
    catch( NullPointerException ex )
    {
      System.err.println( "can not open config file:" + ex.getLocalizedMessage() );
    }
    catch( IOException ex )
    {
      System.err.println( "can not open config file:" + ex.getLocalizedMessage() );
    }
    return null;
  }

  /**
   * Konfiguration in Datei schreiben
   * 
   * @author Dirk Marciniak 05.01.2012
   * @param out
   * @return boolean
   */
  private boolean writeConfArray( BufferedWriter out, SpxPcloggerProgramConfig conf )
  {
    if( conf == null ) return( false );
    try
    {
      out.append( "# generated file, do not edit." + LINE_SEPARATOR );
      out.append( String.format( "%s=%s%s", "databaseDir", conf.getDatabaseDir().getAbsolutePath(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%s%s", "logFile", conf.getLogFile().getAbsoluteFile(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%d%s", "geheimerParameter", conf.geheimerParameter, LINE_SEPARATOR ) );
      out.flush();
    }
    catch( IOException ex )
    {
      System.err.println( "Kann Config nicht schreibenn\n\n" + ex.getLocalizedMessage() );
    }
    return true;
  }
}
