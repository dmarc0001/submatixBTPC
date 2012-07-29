/**
 * Klasse zur Aufnahme der Konfiguration des Programmes
 * 
 * SnifferConfigClass.java de.dmarcini.netutils.dsl sequenzialDslChecker
 * 
 * @author Dirk Marciniak 24.01.2012
 */
package de.dmarcini.submatix.pclogger.utils;

import java.io.File;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

/**
 * 
 * Beinhalte die Konfiguration
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 18.07.2012
 */
public class SpxPcloggerProgramConfig
{
  // statische Variablen, sind IMMER fürs ganze Programm gleich
  public static final File configFile        = new File( System.getProperty( "user.dir" ) + File.separator + ProjectConst.CONFIGFILENAME );
  public static final File programDir        = new File( System.getProperty( "user.dir" ) );
  private File             databaseDir       = null;
  private File             logFile           = null;
  private boolean          wasChanged        = false;
  private int              unitsProperty     = ProjectConst.UNITS_DEFAULT;
  public int               geheimerParameter = 0;

  /**
   * 
   * Na dann wolln wir mal... (ein defaultverzeichnis gibt es, falls keine config existiert)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.07.2012
   */
  public SpxPcloggerProgramConfig()
  {
    setDatabaseDir( new File( programDir.getAbsolutePath() + File.separator + ProjectConst.DEFAULTDATADIR ) );
    setLogFile( new File( programDir.getAbsolutePath() + File.separator + ProjectConst.DEFAULTLOGFILE ) );
    setWasChanged( false );
  }

  /**
   * @return the databaseDir
   */
  public File getDatabaseDir()
  {
    return databaseDir;
  }

  /**
   * @param databaseDir
   *          the databaseDir to set
   */
  public void setDatabaseDir( File databaseDir )
  {
    setWasChanged( true );
    this.databaseDir = databaseDir;
  }

  /**
   * @return the logFile
   */
  public File getLogFile()
  {
    return logFile;
  }

  /**
   * @param logFile
   *          the logFile to set
   */
  public void setLogFile( File logFile )
  {
    setWasChanged( true );
    this.logFile = logFile;
  }

  /**
   * @return the wasChanged
   */
  public boolean isWasChanged()
  {
    return wasChanged;
  }

  /**
   * @param wasChanged
   *          the wasChanged to set
   */
  public void setWasChanged( boolean wasChanged )
  {
    this.wasChanged = wasChanged;
  }

  /**
   * @return the unitsProperty
   */
  public int getUnitsProperty()
  {
    return unitsProperty;
  }

  /**
   * @param unitsProperty
   *          the unitsProperty to set
   */
  public void setUnitsProperty( int unitsProperty )
  {
    this.unitsProperty = unitsProperty;
  }
}
