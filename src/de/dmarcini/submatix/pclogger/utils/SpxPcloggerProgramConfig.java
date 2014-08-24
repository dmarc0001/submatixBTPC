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
  public static final File configFile        = new File( System.getProperty( "user.home" ) + File.separator + ProjectConst.CONFIGFILENAME );
  public static final File programDir        = new File( System.getProperty( "user.dir" ) );
  public static boolean    consoleLog        = false;
  public static Logger     LOGGER            = Logger.getRootLogger();
  public static Level      logLevel          = Level.FATAL;
  public static File       logFile           = new File( System.getProperty( "user.home" ) + File.separator + ProjectConst.DEFAULTLOGFILE );
  public static File       databaseDir       = new File( System.getProperty( "user.home" ) + File.separator + ProjectConst.DEFAULTDATADIR );
  public static File       exportDir         = new File( System.getProperty( "user.home" ) + File.separator + ProjectConst.DEFAULTEXPORTDIR );
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
