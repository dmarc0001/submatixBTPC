package de.dmarcini.submatix.pclogger.utils;

@SuppressWarnings( "unused" )
public final class OperatingSystemDetector
{
  private final static String  operatingSytem = System.getProperty( "os.name" );
  private final static String  systermArch    = System.getProperty( "os.arch" );
  private final static String  dataModel      = System.getProperty( "sun.arch.data.model" );
  private static final boolean is32Bit        = System.getProperty( "sun.arch.data.model" ).contains( "32" );
  private static final boolean is64Bit        = System.getProperty( "sun.arch.data.model" ).contains( "64" );

  public static String getArch()
  {
    return( systermArch );
  }

  public static String getDataModel()
  {
    return( dataModel );
  }

  public static String getOsName()
  {
    return( operatingSytem );
  }

  public static boolean is32Bit()
  {
    return( is64Bit );
  }

  public static boolean is64Bit()
  {
    return( is64Bit );
  }

  public static boolean isMac()
  {
    return( operatingSytem.indexOf( "mac" ) >= 0 );
  }

  public static boolean isSolaris()
  {
    return( operatingSytem.indexOf( "sunos" ) >= 0 );
  }

  public static boolean isUnix()
  {
    return( operatingSytem.indexOf( "nix" ) >= 0 || operatingSytem.indexOf( "nux" ) >= 0 || operatingSytem.indexOf( "aix" ) > 0 );
  }

  public static boolean isWindows()
  {
    return( operatingSytem.indexOf( "win" ) >= 0 );
  }
}
