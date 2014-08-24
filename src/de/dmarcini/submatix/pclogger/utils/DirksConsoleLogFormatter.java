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

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DirksConsoleLogFormatter extends Formatter
{
  private final Date       date         = new Date();
  private final DateFormat dateformater = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM );
  private String           subst        = "";

  public DirksConsoleLogFormatter( String sub )
  {
    subst = sub;
  }

  /**
   * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
   */
  @Override
  public String format( LogRecord record )
  {
    date.setTime( record.getMillis() );
    StringBuffer sb = new StringBuffer();
    // Datum formatieren und in den Puffer
    sb.append( "[" + dateformater.format( date ) + "]" );
    sb.append( " " );
    // Level
    sb.append( record.getLevel().getName() );
    sb.append( " " );
    // Klasse oder Logger-Name vergewaltigen
    if( record.getSourceClassName() != null )
    {
      if( subst.isEmpty() )
      {
        sb.append( record.getSourceClassName() );
      }
      else
      {
        sb.append( ( record.getSourceClassName() ).replaceFirst( subst, "" ) );
      }
    }
    else
    {
      sb.append( record.getLoggerName() );
    }
    // Method
    if( record.getSourceMethodName() != null )
    {
      sb.append( " " );
      sb.append( record.getSourceMethodName() );
      sb.append( "() " );
    }
    // Tennstrich
    sb.append( " - " );
    // Message
    sb.append( record.getMessage() );
    // Newline
    sb.append( System.getProperty( "line.separator" ) );
    return sb.toString();
  }
}
