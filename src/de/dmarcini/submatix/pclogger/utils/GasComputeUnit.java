/**
 * Pamb = p0 + d * const.
 * 
 * Die Konstante const. dient zur Umrechnung einer Längenangabe (Tauchtiefe) in eine Druckeinheit Wird die Tauchtiefe d in [m] angegeben, gilt deshalb in etwa folgender
 * Zusammenhang für den Druck in [Bar]: const. = 0,0980665 [Bar/m], für Süsswasser, und const. = 0,100522 [Bar/m], für Salzwasser, und damit z.B.:
 */
// 1 PSI = 0.06984757293 BAR
// 1 Bar = 14.50377 PSI
// 1 Meter = 3,2808399 Fuß
// 1 Fuß = 0,3048 Meter
// 33,071 feet sea water (fsw) = 33,8995 feet fresh water = 14,6960 psi
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
  private static double barConstClearWater = 0.0980665D;        // Bar per 1 Meter
  private static double barConstSaltWater  = 0.100522D;         // Bar Per 1 Meter
  private static double psiConstClearWater = 0.4414814377793183; // PSI per feet
  private static double psiConstSaltWater  = 0.4443772489492304; // PSI per feet
  private static double barOffset          = 1.0D;              // Oberflächendruck Meereshöhe
  private static double psiOffset          = 14.6960;           // Oberflächendruck Meereshöhe

  public static String getNameForGas( final int o2, final int he )
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
  public static double getMODForGasMetric( final int o2, final double ppOMax, final boolean salnity )
  {
    // Also, gegeben O2 in Prozent, PPOMax (meist wohl 1.6 Bar)
    double pEnv, mod;
    // errechne den Umgebungsdruck für ppOMax und Sauerstoffanteil
    pEnv = ( ppOMax * 100.0D ) / o2;
    //
    if( salnity == true )
    {
      mod = ( pEnv - barOffset ) / barConstSaltWater;
    }
    else
    {
      mod = ( pEnv - barOffset ) / barConstClearWater;
    }
    if( mod < 0 ) return( 0 );
    return( mod );
  }

  /**
   * 
   * Gibt die MOD in feet für einen Druck in psi an
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 04.09.2012
   * @param o2
   * @param ppOMax
   * @param salnity
   * @return MOD in feet
   */
  public static double getMODForGasImperial( final int o2, final double ppOMax, final boolean salnity )
  {
    // Also, gegeben O2 in Prozent, PPOMax
    double pEnv, mod;
    // errechne den Umgebungsdruck für ppOMax und Sauerstoffanteil
    pEnv = ( ppOMax * 100.0D ) / o2;
    //
    if( salnity == true )
    {
      mod = ( pEnv - psiOffset ) / psiConstSaltWater;
    }
    else
    {
      mod = ( pEnv - psiOffset ) / psiConstClearWater;
    }
    if( mod < 0 ) return( 0 );
    return( mod );
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
  public static double getEADForGasMetric( final int n2, final double depth, final boolean salnity )
  {
    // Gegeben n2 in Prozent, ich will wissen, wie die equivalente Tiefe ist
    double ead;
    double n2d = n2 / 100.0D;
    if( salnity )
    {
      ead = ( ( ( n2d / .79D ) * ( barOffset + ( depth * barConstClearWater ) ) ) - 1D ) * 10.0D;
    }
    else
    {
      ead = ( ( ( n2d / .79D ) * ( barOffset + ( depth * barConstSaltWater ) ) ) - 1D ) * 10.0D;
    }
    if( ead < 0 ) return( 0 );
    return( ead );
  }

  /**
   * 
   * EAD in feet ausgeben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 04.09.2012
   * @param n2
   * @param depth
   * @param salnity
   * @return EAD n feet
   */
  public static double getEADForGasImperial( final int n2, final double depth, final boolean salnity )
  {
    // Gegeben n2 in Prozent, ich will wissen, wie die equivalente Tiefe ist
    double ead;
    double n2d = n2 / 100D;
    // The equivalent air depth can be calculated for depths in feet as follows:
    // EAD = (Depth + 33) × Fraction of N2 / 0.79 − 33
    if( salnity )
    {
      ead = ( ( depth + 33.071D ) * ( n2d / 0.79 ) ) - 33.071D;
    }
    else
    {
      // clearwater
      ead = ( ( depth + 33.8995D ) * ( n2d / 0.79 ) ) - 33.8995D;
    }
    if( ead < 0 ) return( 0 );
    return( ead );
  }

  /**
   * 
   * EAD unter Berücksichtigung des Setpoint bei bekanntem Gas
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 23.09.2012
   * @param he
   *          Helium im Diluent
   * @param o2
   *          Sauerstoff im Diluent
   * @param ppo2
   *          Setpoint
   * @param depth
   *          tiefe in Metern
   * @param salnity
   *          Salzwasser oder nicht
   * @return ead Equivalente Lufttiefe
   */
  public static double getEADForDilMetric( final int o2, final int he, final double ppo2, final double depth, final boolean salnity )
  {
    // Gegeben n2 in Prozent, ich will wissen, wie die equivalente Tiefe ist
    double ead, o2Result, pEnv;
    double restgas;
    double n2FromRest, n2ForEAD;
    //
    // wieviel Prozent vom Restgas fällt auf die Bestandteile?
    //
    restgas = 100D - o2;
    n2FromRest = ( restgas * ( 100 - he - o2 ) ) / 100D;
    //
    // Umgebungsdruck
    //
    if( salnity )
    {
      pEnv = ( depth * barConstSaltWater ) + barOffset;
    }
    else
    {
      pEnv = ( depth * barConstClearWater ) + barOffset;
    }
    // Sauerstoff im Diluent
    // o2Dil = ( o2 / 100D );
    //
    // wieviel Sauerstoff braucht es für den Setpoint?
    //
    o2Result = ( ppo2 * 100.0D ) / pEnv;
    //
    // wie hoch ist dann der Anteil vom Restgas?
    //
    restgas = 100D - o2Result;
    //
    // dann ist also restgas Prozent aufgeteilt in N2 und he
    // und Stickstoff in Prozenten läßt sich jetzt ausrechnen
    //
    n2ForEAD = ( ( restgas / 100D ) * n2FromRest ) / 100D;
    if( salnity )
    {
      ead = ( ( ( n2ForEAD / .79D ) * ( barOffset + ( depth * barConstClearWater ) ) ) - 1D ) * 10.0D;
    }
    else
    {
      ead = ( ( ( n2ForEAD / .79D ) * ( barOffset + ( depth * barConstSaltWater ) ) ) - 1D ) * 10.0D;
    }
    if( ead < 0 ) return( 0 );
    return( ead );
  }

  /**
   * 
   * EAD für Diluent mit Setpoint errechnen (imperiale Einheiten)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 23.09.2012
   * @param o2
   *          Sauerstoff im Duiluent
   * @param he
   *          Helium im Diluent
   * @param ppo2
   *          Setpoint
   * @param depth
   *          Tiefe in fuss
   * @param salnity
   *          Salzwasser?
   * @return EAD in fuss
   */
  public static double getEADForDilImperial( final int o2, final int he, final double ppo2, final double depth, final boolean salnity )
  {
    // Gegeben n2 in Prozent, ich will wissen, wie die equivalente Tiefe ist
    double ead, o2Result, pEnv;
    double restgas;
    double n2FromRest, n2ForEAD;
    //
    // wieviel Prozent vom Restgas fällt auf die Bestandteile?
    //
    restgas = 100D - o2;
    n2FromRest = ( restgas * ( 100 - he - o2 ) ) / 100D;
    //
    // Umgebungsdruck
    //
    if( salnity )
    {
      pEnv = ( depth * psiConstSaltWater ) + psiOffset;
    }
    else
    {
      pEnv = ( depth * psiConstClearWater ) + psiOffset;
    }
    // Sauerstoff im Diluent
    // o2Dil = ( o2 / 100D );
    //
    // wieviel Sauerstoff braucht es für den Setpoint?
    //
    o2Result = ( ppo2 * 100.0D ) / pEnv;
    //
    // wie hoch ist dann der Anteil vom Restgas?
    //
    restgas = 100D - o2Result;
    //
    // dann ist also restgas Prozent aufgeteilt in N2 und he
    // und Stickstoff in Prozenten läßt sich jetzt ausrechnen
    //
    n2ForEAD = ( ( restgas / 100D ) * n2FromRest ) / 100D;
    // EAD = (Depth + 33) × Fraction of N2 / 0.79 − 33
    if( salnity )
    {
      ead = ( ( depth + 33.071D ) * ( n2ForEAD / 0.79 ) ) - 33.071D;
    }
    else
    {
      // clearwater
      ead = ( ( depth + 33.8995D ) * ( n2ForEAD / 0.79 ) ) - 33.8995D;
    }
    if( ead < 0 ) return( 0 );
    return( ead );
  }
}
