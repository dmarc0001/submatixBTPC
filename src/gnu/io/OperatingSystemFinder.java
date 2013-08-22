package gnu.io;

@SuppressWarnings( "unused" )
public final class OperatingSystemFinder
{
  private final static String operatingSytem = System.getProperty( "os.name" ).toLowerCase();
  private final static String systermArch    = System.getProperty( "os.arch" );
  private final static String dataModel      = System.getProperty( "sun.arch.data.model" );

  public static String getArch()
  {
    return( systermArch );
  }

  public static String getOsTypString()
  {
    if( operatingSytem.indexOf( "nix" ) >= 0 )
    {
      return( "unix" );
    }
    else if( operatingSytem.indexOf( "nux" ) >= 0 )
    {
      return( "linux" );
    }
    else if( operatingSytem.indexOf( "win" ) >= 0 )
    {
      return( "win" );
    }
    else if( operatingSytem.indexOf( "mac" ) >= 0 )
    {
      return( "mac" );
    }
    else if( operatingSytem.indexOf( "sunos" ) >= 0 )
    {
      return( "sunos" );
    }
    else
    {
      return( "unknown" );
    }
  }
}
