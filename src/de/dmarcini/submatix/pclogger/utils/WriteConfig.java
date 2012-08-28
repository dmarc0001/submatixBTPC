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

import de.dmarcini.submatix.pclogger.res.ProjectConst;

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
      out.append( String.format( "%s=%s%s", "exportDir", conf.getExportDir().getAbsoluteFile(), LINE_SEPARATOR ) );
      switch ( conf.getUnitsProperty() )
      {
        case ProjectConst.UNITS_DEFAULT:
          out.append( String.format( "%s=%s%s", "showUnits", "default", LINE_SEPARATOR ) );
          break;
        case ProjectConst.UNITS_METRIC:
          out.append( String.format( "%s=%s%s", "showUnits", "metric", LINE_SEPARATOR ) );
          break;
        case ProjectConst.UNITS_IMPERIAL:
          out.append( String.format( "%s=%s%s", "showUnits", "imperial", LINE_SEPARATOR ) );
          break;
        default:
          out.append( String.format( "%s=%s%s", "showUnits", "default", LINE_SEPARATOR ) );
      }
      out.append( String.format( "%s=%b%s", "showTemperature", conf.isShowTemperature(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%b%s", "showPpoResult", conf.isShowPpoResult(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%b%s", "showPpo01", conf.isShowPpo01(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%b%s", "showPpo02", conf.isShowPpo02(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%b%s", "showPpo03", conf.isShowPpo03(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%b%s", "showSetpoint", conf.isShowSetpoint(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%b%s", "showHe", conf.isShowHe(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%b%s", "showN2", conf.isShowN2(), LINE_SEPARATOR ) );
      out.append( String.format( "%s=%b%s", "showNulltime", conf.isShowNulltime(), LINE_SEPARATOR ) );
      out.flush();
    }
    catch( IOException ex )
    {
      System.err.println( "Kann Config nicht schreibenn\n\n" + ex.getLocalizedMessage() );
    }
    return true;
  }
}
