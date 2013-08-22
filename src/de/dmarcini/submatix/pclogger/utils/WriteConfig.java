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
import java.util.Locale;

import org.apache.log4j.Level;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

/**
 * Schreibe Konfig im Muster BEZEICHNUNG=WERT
 * 
 * @author dmarc
 */
public class WriteConfig
{
  private boolean            debug          = false;
  public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

  /**
   * Konstruktor mit Dateinamenübergabe
   * 
   * @author Dirk Marciniak 09.12.2011
   * @throws IOException
   * @throws ConfigReadWriteException
   */
  public WriteConfig() throws IOException, ConfigReadWriteException
  {
    BufferedWriter out;
    if( SpxPcloggerProgramConfig.logLevel == Level.DEBUG ) debug = true;
    if( debug ) System.out.println( String.format( "WriteConfig: write File <%s>", SpxPcloggerProgramConfig.configFile.getAbsolutePath() ) );
    if( null != ( out = openConfFile( SpxPcloggerProgramConfig.configFile ) ) )
    {
      if( writeConfArray( out ) )
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
  private boolean writeConfArray( BufferedWriter out )
  {
    try
    {
      out.append( "#" + LINE_SEPARATOR );
      out.append( "# generated file, do not edit." + LINE_SEPARATOR );
      out.append( "#" + LINE_SEPARATOR );
      out.append( String.format( "%-18s = %s%s", ProjectConst.CONFIG_DATABASEDIR, SpxPcloggerProgramConfig.databaseDir.getAbsolutePath(), LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %s%s", ProjectConst.CONFIG_LOGFILE, SpxPcloggerProgramConfig.logFile.getAbsoluteFile(), LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %s%s", ProjectConst.CONFIG_EXPORTDIR, SpxPcloggerProgramConfig.exportDir.getAbsoluteFile(), LINE_SEPARATOR ) );
      if( SpxPcloggerProgramConfig.langCode != null )
      {
        out.append( String.format( "%-18s = %s%s", ProjectConst.CONFIG_LANGCODE, SpxPcloggerProgramConfig.langCode, LINE_SEPARATOR ) );
      }
      else
      {
        out.append( String.format( "#%-18s = %s%s", ProjectConst.CONFIG_LANGCODE, Locale.getDefault().getLanguage(), LINE_SEPARATOR ) );
      }
      switch ( SpxPcloggerProgramConfig.unitsProperty )
      {
        case ProjectConst.UNITS_DEFAULT:
          out.append( String.format( "%-18s = %s%s", ProjectConst.CONFIG_SHOWUNITS, "default", LINE_SEPARATOR ) );
          break;
        case ProjectConst.UNITS_METRIC:
          out.append( String.format( "%-18s = %s%s", ProjectConst.CONFIG_SHOWUNITS, "metric", LINE_SEPARATOR ) );
          break;
        case ProjectConst.UNITS_IMPERIAL:
          out.append( String.format( "%-18s = %s%s", ProjectConst.CONFIG_SHOWUNITS, "imperial", LINE_SEPARATOR ) );
          break;
        default:
          out.append( String.format( "%-18s = %s%s", ProjectConst.CONFIG_SHOWUNITS, "default", LINE_SEPARATOR ) );
      }
      out.append( String.format( "%-18s = %b%s", ProjectConst.CONFIG_SHOWTEMPERRATURE, SpxPcloggerProgramConfig.showTemperature, LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %b%s", ProjectConst.CONFIG_SHOWPPORESULT, SpxPcloggerProgramConfig.showPpoResult, LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %b%s", ProjectConst.CONFIG_SHOWPPO1, SpxPcloggerProgramConfig.showPpo01, LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %b%s", ProjectConst.CONFIG_SHOWPPO2, SpxPcloggerProgramConfig.showPpo02, LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %b%s", ProjectConst.CONFIG_SHOWPPO3, SpxPcloggerProgramConfig.showPpo03, LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %b%s", ProjectConst.CONFIG_SHOWSETPOINT, SpxPcloggerProgramConfig.showSetpoint, LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %b%s", ProjectConst.CONFIG_SHOWHE, SpxPcloggerProgramConfig.showHe, LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %b%s", ProjectConst.CONFIG_SHOWN2, SpxPcloggerProgramConfig.showN2, LINE_SEPARATOR ) );
      out.append( String.format( "%-18s = %b%s", ProjectConst.CONFIG_SHOWNULLTIME, SpxPcloggerProgramConfig.showNulltime, LINE_SEPARATOR ) );
      out.flush();
    }
    catch( IOException ex )
    {
      System.err.println( "Kann Config nicht schreibenn\n\n" + ex.getLocalizedMessage() );
    }
    return true;
  }
}
