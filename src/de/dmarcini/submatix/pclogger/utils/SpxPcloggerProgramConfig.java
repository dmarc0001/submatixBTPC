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
  private int              unitsProperty     = ProjectConst.UNITS_DEFAULT;
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
  // statische Variablen, sind IMMER fürs ganze Programm gleich
  private boolean          wasChanged        = false;
  private boolean          showTemperature   = true;
  private boolean          showPpoResult     = true;
  private boolean          showPpo01         = false;
  private boolean          showPpo02         = false;
  private boolean          showPpo03         = false;
  private boolean          showSetpoint      = false;
  private boolean          showHe            = false;
  private boolean          showN2            = false;
  private boolean          showNulltime      = false;

  /**
   * Na dann wolln wir mal... (ein defaultverzeichnis gibt es, falls keine config existiert) Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.07.2012
   */
  public SpxPcloggerProgramConfig()
  {
    setWasChanged( false );
  }

  /**
   * @return the unitsProperty
   */
  public int getUnitsProperty()
  {
    return unitsProperty;
  }

  /**
   * @return the showHe
   */
  public boolean isShowHe()
  {
    return showHe;
  }

  /**
   * @return the showN2
   */
  public boolean isShowN2()
  {
    return showN2;
  }

  /**
   * @return the showNulltime
   */
  public boolean isShowNulltime()
  {
    return showNulltime;
  }

  /**
   * @return the showPpo01
   */
  public boolean isShowPpo01()
  {
    return showPpo01;
  }

  /**
   * @return the showPpo02
   */
  public boolean isShowPpo02()
  {
    return showPpo02;
  }

  /**
   * @return the showPpo03
   */
  public boolean isShowPpo03()
  {
    return showPpo03;
  }

  /**
   * @return the showPpoResult
   */
  public boolean isShowPpoResult()
  {
    return showPpoResult;
  }

  /**
   * @return the showSetpoint
   */
  public boolean isShowSetpoint()
  {
    return showSetpoint;
  }

  /**
   * @return the showTemperature
   */
  public boolean isShowTemperature()
  {
    return showTemperature;
  }

  /**
   * @return the wasChanged
   */
  public boolean isWasChanged()
  {
    return wasChanged;
  }

  /**
   * @param showHe
   *          the showHe to set
   */
  public void setShowHe( boolean showHe )
  {
    setWasChanged( true );
    this.showHe = showHe;
  }

  /**
   * @param showN2
   *          the showN2 to set
   */
  public void setShowN2( boolean showN2 )
  {
    setWasChanged( true );
    this.showN2 = showN2;
  }

  /**
   * @param showNulltime
   *          the showNulltime to set
   */
  public void setShowNulltime( boolean showNulltime )
  {
    setWasChanged( true );
    this.showNulltime = showNulltime;
  }

  /**
   * @param showPpo01
   *          the showPpo01 to set
   */
  public void setShowPpo01( boolean showPpo01 )
  {
    setWasChanged( true );
    this.showPpo01 = showPpo01;
  }

  /**
   * @param showPpo02
   *          the showPpo02 to set
   */
  public void setShowPpo02( boolean showPpo02 )
  {
    setWasChanged( true );
    this.showPpo02 = showPpo02;
  }

  /**
   * @param showPpo03
   *          the showPpo03 to set
   */
  public void setShowPpo03( boolean showPpo03 )
  {
    setWasChanged( true );
    this.showPpo03 = showPpo03;
  }

  /**
   * @param showPpoResult
   *          the showPpoResult to set
   */
  public void setShowPpoResult( boolean showPpoResult )
  {
    setWasChanged( true );
    this.showPpoResult = showPpoResult;
  }

  /**
   * @param showSetpoint
   *          the showSetpoint to set
   */
  public void setShowSetpoint( boolean showSetpoint )
  {
    setWasChanged( true );
    this.showSetpoint = showSetpoint;
  }

  /**
   * @param showTemperature
   *          the showTemperature to set
   */
  public void setShowTemperature( boolean showTemperature )
  {
    setWasChanged( true );
    this.showTemperature = showTemperature;
  }

  /**
   * @param unitsProperty
   *          the unitsProperty to set
   */
  public void setUnitsProperty( int unitsProperty )
  {
    setWasChanged( true );
    this.unitsProperty = unitsProperty;
  }

  /**
   * @param wasChanged
   *          the wasChanged to set
   */
  public void setWasChanged( boolean wasChanged )
  {
    this.wasChanged = wasChanged;
  }
}
