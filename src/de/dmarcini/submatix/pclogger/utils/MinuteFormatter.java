/**
 * Der eigene zusammengeschusterte Formwetierer für Minuten:Sekunden Anzeige
 * 
 * MinuteFormatter.java de.dmarcini.formattertest FormatterTest
 * 
 * @author Dirk Marciniak 30.07.2012
 */
package de.dmarcini.submatix.pclogger.utils;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * @author dmarc
 */
public class MinuteFormatter extends NumberFormat
{
  /**
   *
   */
  private static final long serialVersionUID = -943205178868106609L;
  private String            extendStr        = "";

  /**
   * Der Standartkonstruktor
   * 
   * @author Dirk Marciniak 30.07.2012
   */
  public MinuteFormatter()
  {
    extendStr = "";
  }

  /**
   * Konstruktor, der einen Extend (Maßeinheit) zufügt
   * 
   * @author Dirk Marciniak 30.07.2012
   * @param ext
   */
  public MinuteFormatter( String ext )
  {
    extendStr = " " + ext;
  }

  @Override
  public StringBuffer format( double number, StringBuffer toAppendTo, FieldPosition pos )
  {
    String ret = String.format( "%d:%02d%s", ( long )( number / 60 ), ( long )( number % 60 ), extendStr );
    return( new StringBuffer( ret ) );
  }

  @Override
  public StringBuffer format( long number, StringBuffer toAppendTo, FieldPosition pos )
  {
    String ret = String.format( "%d:%02d%s", number / 60, number % 60, extendStr );
    return( new StringBuffer( ret ) );
  }

  @Override
  public Number parse( String source, ParsePosition parsePosition )
  {
    // TODO Auto-generated method stub
    return null;
  }
}
