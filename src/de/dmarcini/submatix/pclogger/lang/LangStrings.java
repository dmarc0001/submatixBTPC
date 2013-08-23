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
  private static ResourceBundle RESOURCE_BUNDLE = loadBundle(locale);

  private static ResourceBundle loadBundle(Locale _locale)
  {
    return ResourceBundle.getBundle(BUNDLE_NAME, _locale);
  }

  public static void setLocale(Locale _locale)
  {
    locale = _locale;
    RESOURCE_BUNDLE = loadBundle(_locale);
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Strings access
  //
  // //////////////////////////////////////////////////////////////////////////
  public static String getString(String key)
  {
    try
    {
      ResourceBundle bundle = Beans.isDesignTime() ? loadBundle(locale) : RESOURCE_BUNDLE;
      return bundle.getString(key);
    }
    catch (MissingResourceException e)
    {
      return "!" + key + "!";
    }
  }
}
