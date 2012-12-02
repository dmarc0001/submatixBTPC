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
