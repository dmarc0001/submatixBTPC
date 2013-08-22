/**
 * Klasse zur Aufnahme der Konfiguration des Programmes
 * 
 * SnifferConfigClass.java de.dmarcini.netutils.dsl sequenzialDslChecker
 * 
 * @author Dirk Marciniak 24.01.2012
 */
package de.dmarcini.submatix.pclogger.utils;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

/**
 * Beinhalte die Konfiguration Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.07.2012
 */
public class SpxPcloggerProgramConfig
{
  // statische Variablen, sind IMMER fürs ganze Programm gleich
  public static int        unitsProperty     = ProjectConst.UNITS_DEFAULT;
  public static final File configFile        = new File( System.getProperty( "user.dir" ) + File.separator + ProjectConst.CONFIGFILENAME );
  public static final File programDir        = new File( System.getProperty( "user.dir" ) );
  public static boolean    consoleLog        = false;
  public static Logger     LOGGER            = Logger.getRootLogger();
  public static Level      logLevel          = Level.FATAL;
  public static File       logFile           = new File( programDir.getAbsolutePath() + File.separator + ProjectConst.DEFAULTLOGFILE );
  public static File       databaseDir       = new File( programDir.getAbsolutePath() + File.separator + ProjectConst.DEFAULTDATADIR );
  public static File       exportDir         = new File( programDir.getAbsolutePath() + File.separator + ProjectConst.DEFAULTEXPORTDIR );
  public static String     langCode          = null;
  public static boolean    wasCliLogfile     = false;
  public static boolean    wasCliLogLevel    = false;
  public static boolean    wasCliExportDir   = false;
  public static boolean    wasCliConsoleLog  = false;
  public static boolean    wasCliLangCode    = false;
  public static boolean    wasCliDatabaseDir = false;
  public static boolean    developDebug      = false;
  public static boolean    wasChanged        = false;
  public static boolean    showTemperature   = true;
  public static boolean    showPpoResult     = true;
  public static boolean    showPpo01         = false;
  public static boolean    showPpo02         = false;
  public static boolean    showPpo03         = false;
  public static boolean    showSetpoint      = false;
  public static boolean    showHe            = false;
  public static boolean    showN2            = false;
  public static boolean    showNulltime      = false;
}
