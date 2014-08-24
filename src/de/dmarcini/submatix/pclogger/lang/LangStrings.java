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
package de.dmarcini.submatix.pclogger.lang;

import java.beans.Beans;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LangStrings
{
  // //////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // //////////////////////////////////////////////////////////////////////////
  private LangStrings()
  {
    // do not instantiate
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Bundle access
  //
  // //////////////////////////////////////////////////////////////////////////
  private static final String   BUNDLE_NAME     = "de.dmarcini.submatix.pclogger.lang.messages"; //$NON-NLS-1$
  private static Locale         locale          = Locale.getDefault();
  private static ResourceBundle RESOURCE_BUNDLE = loadBundle( locale );

  private static ResourceBundle loadBundle( Locale _locale )
  {
    return ResourceBundle.getBundle( BUNDLE_NAME, _locale );
  }

  public static void setLocale( Locale _locale )
  {
    locale = _locale;
    RESOURCE_BUNDLE = loadBundle( _locale );
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Strings access
  //
  // //////////////////////////////////////////////////////////////////////////
  public static String getString( String key )
  {
    try
    {
      ResourceBundle bundle = Beans.isDesignTime() ? loadBundle( locale ) : RESOURCE_BUNDLE;
      return bundle.getString( key );
    }
    catch( MissingResourceException e )
    {
      return "!" + key + "!";
    }
  }
}
