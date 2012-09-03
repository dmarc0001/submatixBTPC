/**
 * Pamb = p0 + d * const.
 * 
 * Die Konstante const. dient zur Umrechnung einer Längenangabe (Tauchtiefe) in eine Druckeinheit Wird die Tauchtiefe d in [m] angegeben, gilt deshalb in etwa folgender
 * Zusammenhang für den Druck in [Bar]: const. = 0,0980665 [Bar/m], für Süsswasser, und const. = 0,100522 [Bar/m], für Salzwasser, und damit z.B.:
 */
package de.dmarcini.submatix.pclogger.utils;

/**
 * 
 * Der Konstruktor...
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 02.09.2012
 */
public class GasComputeUnit
{
  private static double barConstcw = 0.0980665D; // Bar per Meter
  private static double barConstsw = 0.100522D; // Bar Per Meter
  private static double barOffset  = 1.0D;      // Oberflächendruck Meereshöhe

  public static String getNameForGas( int o2, int he )
  {
    //
    // Wieviel Stickstoff?
    //
    int n2 = 100 - o2 - he;
    //
    // Mal sondieren
    //
    if( n2 == 0 )
    {
      //
      // heliox oder O2
      //
      if( o2 == 100 )
      {
        return( "O2" );
      }
      // Es gibt Helium und O2.... == Heliox
      return( String.format( "HX%d/%d", o2, he ) );
    }
    if( he == 0 )
    {
      // eindeutig Nitrox
      if( o2 == 21 )
      {
        return( "AIR" );
      }
      return( String.format( "NX%02d", o2 ) );
    }
    else
    {
      // das ist dan wohl Trimix/Triox
      return( String.format( "TX%d/%d", o2, he ) );
    }
  }

  /**
   * 
   * Gib die Maximale Tiefe für O2 in Metern an
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   * @param o2
   *          Sauerstoff in Prozent
   * @param ppOMax
   *          Maximalker Partialdruck
   * @param salnity
   *          Salzwasser?
   * @return Maximale Tiefe für dasGas auf O2 bezogen
   */
  public static double getMODForGasMetric( int o2, double ppOMax, boolean salnity )
  {
    // Also, gegeben O2 in Prozent, PPOMax (meist wohl 1.6 Bar)
    double pEnv;
    // errechne den Umgebungsdruck für ppOMax und Sauerstoffanteil
    pEnv = ( ppOMax * 100.0D ) / o2;
    if( salnity )
    {
      return( ( pEnv - barOffset ) * ( 100.0D * barConstsw ) );
    }
    else
    {
      return( ( pEnv - barOffset ) * ( 100.0D * barConstcw ) );
    }
  }

  public static double getMODForGasImperial( int o2, double ppOMax, boolean salnity )
  {
    return ppOMax;
    // 1 PSI = 0.06984757293 BAR
    // 1 Bar = 14.50377 PSI
    // 1 Meter = 3,2808399 Fuß
    // 1 Fuß = 0,3048 Meter
    // 33,071 feet sea water (fsw) = 33,8995 feet fresh water = 14,6960 psi
  }

  /**
   * 
   * Equivalente Lufttiefe für Gas
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   * @param n2
   *          Stickstoff in Prozent
   * @param depth
   *          Tiefe in Metern
   * @param salnity
   *          Salzwasser?
   * @return Equivalente Lufttiefe in Metern
   */
  public static double getEADForGasMetric( int n2, double depth, boolean salnity )
  {
    // Gegeben n2 in Prozent, ich will wissen, wie die equivalente Tiefe ist
    double p_env;
    // Umgebungsdruck multipliziert mit quotioent aus Stickstoffanteil und Normal Luftanteil Sticksstoff
    // das Ergebnis mal bar per Meter mal 10 ergibt die EAD
    if( salnity )
    {
      p_env = ( depth * barConstsw ) + barOffset;
      return( ( ( p_env * ( n2 / 79.0D ) ) - 1.0D ) * ( 100.0D * barConstsw ) );
    }
    else
    {
      p_env = ( depth * barConstcw ) + barOffset;
      return( ( ( p_env * ( n2 / 79.0D ) ) - 1.0D ) * ( 100.0D * barConstcw ) );
    }
  }
}
