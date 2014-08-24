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
