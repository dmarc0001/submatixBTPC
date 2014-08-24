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
 * Eigene Ausnahme....
 * 
 * ConfigReadException.java de.dmarcini.swing.filesplitter newFileSplitter
 * 
 * @author Dirk Marciniak 09.12.2011
 */
package de.dmarcini.submatix.pclogger.utils;

/**
 * @author dmarc
 */
public class ConfigReadWriteException extends Exception
{
  /**
   * Seriennummer
   */
  private static final long serialVersionUID = 12L;

  /**
   * Standartkonstruktor, nicht zu benutzen
   * 
   * @author Dirk Marciniak 09.12.2011
   */
  @SuppressWarnings( "unused" )
  private ConfigReadWriteException()
  {}

  /**
   * Konstruktor mit Message
   * 
   * @author Dirk Marciniak 09.12.2011
   * @param msg
   *          Nachricht
   */
  public ConfigReadWriteException( String msg )
  {
    super( msg );
  }
}
