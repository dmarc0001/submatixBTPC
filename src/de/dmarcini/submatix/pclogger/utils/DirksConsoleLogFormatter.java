package de.dmarcini.submatix.pclogger.utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class DirksConsoleLogFormatter extends Formatter
{
  private final Date date = new Date();
  private final DateFormat dateformater = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM );
  private String subst = "";
  
  
  public DirksConsoleLogFormatter( String sub )
  {
    subst = sub;
  }
  
  /**
   * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
   */
  @Override
  public String format(LogRecord record) 
  {
    date.setTime(record.getMillis());

    StringBuffer sb = new StringBuffer();

    // Datum formatieren und in den Puffer
    sb.append( "[" + dateformater.format(date) + "]" );
    sb.append(" ");

    // Klasse oder Logger-Name vergewaltigen
    if (record.getSourceClassName() != null) 
    {
      if( subst.isEmpty() )
      {
        sb.append( record.getSourceClassName()  );
      }
      else
      {
        sb.append( (record.getSourceClassName()).replaceFirst( subst, "" )  );
      }
    } 
    else 
    {
      sb.append(record.getLoggerName());
    }

    // Method
    if (record.getSourceMethodName() != null) {
      sb.append(" ");
      sb.append(record.getSourceMethodName());
      sb.append("() ");
    }

    // Level
    sb.append(record.getLevel().getName() );
    sb.append(" - ");

    // Message
    sb.append(record.getMessage() );

    // Newline
    sb.append(System.getProperty("line.separator") );

    return sb.toString();
  }

}
