/**
 * Datenobjekt zur Übergabe von Daten für eine Logzeile des SPX42
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 17.06.2012
 */
package de.dmarcini.submatix.pclogger.utils;

/**
 * Datenobjekt zur Übergabe einer Logzeile vom SPX42
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 17.06.2012
 */
public class LogLineDataObject
{
  public int pressure, depth, temperature, setpoint, n2, he, zeroTime, nextStep;
  public double acku, ppo2, ppo2_1, ppo2_2, ppo2_3;
}
