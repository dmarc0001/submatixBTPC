/**
 * Objekt zur Speicherung der SPX42 Konfiguration
 * 
 * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 30.12.2011 TODO
 */
package de.dmarcini.submatix.pclogger.utils;

import org.apache.log4j.Logger;

/**
 * HEADLINE Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
 */
public interface ISPX42Config
{
  public boolean canSetDate();

  public void clear();

  /**
   * Vergleiche, ob zwei Konfigurationenen übereinstimmen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param conf
   *          zu vergleichende Konfiguration
   * @return gleichheit
   */
  public boolean compareWith( SPX42Config conf );

  public int getAutoSetpoint();

  /**
   * Ist Custom Config erlaubt? Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.04.2012
   * @return Ja oder nein
   */
  public int getCustomEnabled();

  /**
   * Erfrage HIGHT-Wert des Deco-Gradienten Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @return HIGH-Wert Deco Gradient
   */
  public int getDecoGfHigh();

  /**
   * erfrage den LOW-Wert des Deko-Gradienten Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @return Deco-gradient
   */
  public int getDecoGfLow();

  /**
   * Erfrage die Nummer des eingestellten Presets Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @return Preset
   */
  public int getDecoGfPreset();

  /**
   * Erfrage, ob tiefe Stops erlaubt sind Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @return erlaubt/unterbunden
   */
  public int getDeepStopEnable();

  /**
   * gib den vom SPX42 gelieferten Gerätenamen zurück Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @return Gerätename
   */
  public String getDeviceName();

  /**
   * Erfrage Displayhelligkeit Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @return Helligkeit 0..2
   */
  public int getDisplayBrightness();

  /**
   * Erfrage Diaplayausrichtung Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @return TODO
   */
  public int getDisplayOrientation();

  /**
   * Erfrage, ob dyn. Gradienten erlaubt sind Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @return 1==ja/ 2==nein
   */
  public int getDynGradientsEnable();

  /**
   * Gib die Firmware Versionsnummer zurück Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @return TODO
   */
  public String getFirmwareVersion();

  /**
   * Erfrage den eingestellten Wert für den letzen Decostop Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @return 0 == 3 Meter, 1 == 6 Meter
   */
  public int getLastStop();

  /**
   * Welchen Status hat die Lizenz= Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.04.2012
   * @return 0=Nitrox, 1=Normoxic Trimix, 2=Full Trimix
   */
  public int getLicenseState();

  public int getLogInterval();

  public int getMaxSetpoint();

  public int getPscrModeOn();

  public int getSensorsCount();

  public int getSensorsOn();

  /**
   * Erfrage die seriennummer des SPX42. Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @return Seriennummer
   */
  public String getSerial();

  public int getSoundOn();

  public int getTempStickVer();

  public int getUnitDepth();

  public int getUnitSalnity();

  public int getUnitSystem();

  public int getUnitTemperature();

  public boolean hasFahrenheidBug();

  public boolean hasSixValuesIndividual();

  /**
   * Erfrage, ob tiefe Stops erlaubt sind Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @return erlaubt/unterbunden
   */
  public boolean isDeepStopEnable();

  /**
   * Erfrage, ob dyn. Gradienten erlaubt sind Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @return erlaubt?
   */
  public boolean isDynGradientsEnable();

  public boolean isFirmwareSupported();

  /**
   * Frage, ob das Objekt gültig ist. Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @return Gültigkeit
   */
  public boolean isInitialized();

  public boolean isNewerDisplayBrightness();

  public boolean isOldParamSorting();

  public boolean isPscrModeOn();

  public boolean isSensorsOn();

  public boolean isSoundOn();

  public boolean isSixMetersAutoSetpoint();

  public void setAutoSetpoint( int auto );

  /**
   * Setze Status fürt Custom Config Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.04.2012
   * @param en
   */
  public void setCustomEnabled( boolean en );

  /**
   * Setze Deco-Gradienten direkt Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param gfLow
   *          LOW-Wert GDeco-Gradient
   * @param gfHigh
   *          HIGH-Wert Deco-Gradient
   */
  public void setDecoGf( int gfLow, int gfHigh );

  /**
   * Setze Deco-Gradienten direkt als String vom SPX42 Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param fromSpx
   * @return Erfolgreich oder nicht
   */
  public boolean setDecoGf( String fromSpx );

  /**
   * Setze HIGH-Wert Deco Gradient direkt Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param gfHith
   *          HIGH-Wert Deco-Gradient
   */
  public void setDecoGfHigh( int gfHith );

  /**
   * Setze LOW Deco-Gradient direkt Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param gfLow
   *          LOW-Wert Deco gradient
   */
  public void setDecoGfLow( int gfLow );

  /**
   * Setze ein PRESET für die Decompressions-Gradienten Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param preset
   *          Nummer des Presets
   */
  public void setDecoGfPreset( int preset );

  /**
   * Erlaube/unterbinde tiefe Stops Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param enabled
   *          erlaubt/unterbunden
   */
  public void setDeepStopEnable( boolean enabled );

  /**
   * Setze den vom SPX zurückgegebenen Gerätenamen Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @param name
   *          String vom SPX42
   */
  public void setDeviceName( String name );

  /**
   * Setze Display Helligkeit und Ausrichtung Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @param bright
   *          Helligkeit (10%, 50%, 100%)
   * @param orient
   *          Quer/108 Grad
   * @throws Exception
   */
  public void setDisplay( int bright, int orient ) throws Exception;

  /**
   * Setze Display Helligkeit und Ausrichtung von SPX42 String Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @param fromSpx
   *          Steuerstring vom SPX42
   * @return hats geklappt?
   */
  public boolean setDisplay( String fromSpx );

  /**
   * Setze Display Helligkeit Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @param brightness
   *          Helligkeit (0==10%, 1==50%, 2==100%)
   */
  public void setDisplayBrithtness( int brightness );

  /**
   * setze Display-Ausrichtung Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @param orientation
   *          Ausrichtung
   */
  public void setDisplayOrientation( int orientation );

  /**
   * Erlaube/Unterbinde dynamische Gradienten Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @param enabled
   *          erlauben/unterbinden
   */
  public void setDynGradientsEnable( boolean enabled );

  /**
   * Schtreibe in die Konfighuration die Firmware-Seriennummer Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.01.2012
   * @param version
   *          Seriennummer
   */
  public void setFirmwareVersion( String version );

  public void setIndividuals( int so, int pscr, int sc, int snd, int li, int unknown );

  public boolean setIndividuals( String fromSpx );

  /**
   * Setze den letzen Deco-Stop auf 3 oder 6 Meter Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param lastStop
   *          0 == 3 Meter, 1 == 6 Meter
   */
  public void setLastStop( int lastStop );

  /**
   * Setze den Lizenzstatus des SPX Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.04.2012
   * @param msg
   * @return hat es geklappt?
   */
  public boolean setLicenseStatus( String msg );

  /**
   * Setze Status: 0=Nitrox, 1=Normoxic Trimix, 2=Full Trimix Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 11.04.2012
   * @param status
   */
  public void setLizenseStatus( int status );

  /**
   * Setze ein Logger-Objekt Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param logger
   *          Logger Objekt
   */
  public void setLogger( Logger logger );

  public void setLogInterval( int interval );

  public void setMaxSetpoint( int appo );

  public void setPscrModeEnabled( boolean on );

  public void setSensorsCount( int cnt );

  public void setSensorsEnabled( boolean on );

  /**
   * Setze die Seriennummer des SPX 42 Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param serial
   *          Seriennummer
   */
  public void setSerial( String serial );

  public void setSetpoint( int auto, int ppo );

  public boolean setSetpoint( String fromSpx );

  public void setSountEnabled( boolean on );

  public void setTempStickVersion( int tempStickVer );

  public void setUnitDepth( int dpt );

  public void setUnits( int tmp, int dpt, int sal );

  public boolean setUnits( String fromSpx );

  public void setUnitSalnyty( int sal );

  public void setUnitTemperature( int tmp );

  /**
   * Setze ob das Objekt komplett geschrieben wurde (gültig ist). Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.01.2012
   * @param wasInit
   *          Ist das Objekt gültig
   */
  public void setWasInit( boolean wasInit );
}
