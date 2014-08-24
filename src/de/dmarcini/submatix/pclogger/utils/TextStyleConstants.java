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
package de.dmarcini.submatix.pclogger.utils;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class TextStyleConstants
{
  private static final int         FONTSIZEBODY   = 12;
  private static final int         FONTSIZEHEAD   = 14;
  private static final String      FONTFAMILYBODY = "Helvetica";
  private static final String      FONTFAMILHEAD  = "Helvetica";
  public static SimpleAttributeSet HEAD           = new SimpleAttributeSet();
  public static SimpleAttributeSet TITLE          = new SimpleAttributeSet();
  public static SimpleAttributeSet NAME           = new SimpleAttributeSet();
  static
  {
    StyleConstants.setForeground( HEAD, Color.DARK_GRAY );
    StyleConstants.setFontFamily( HEAD, FONTFAMILHEAD );
    StyleConstants.setFontSize( HEAD, FONTSIZEHEAD );
    StyleConstants.setBold( HEAD, true );
    //
    StyleConstants.setForeground( TITLE, Color.blue );
    StyleConstants.setFontFamily( TITLE, FONTFAMILYBODY );
    StyleConstants.setFontSize( TITLE, FONTSIZEBODY );
    StyleConstants.setBold( TITLE, false );
    //
    StyleConstants.setForeground( NAME, Color.magenta );
    StyleConstants.setFontFamily( NAME, FONTFAMILYBODY );
    StyleConstants.setFontSize( NAME, FONTSIZEBODY );
    StyleConstants.setBold( NAME, true );
  }
}
