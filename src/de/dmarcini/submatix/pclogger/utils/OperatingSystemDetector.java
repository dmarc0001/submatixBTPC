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
