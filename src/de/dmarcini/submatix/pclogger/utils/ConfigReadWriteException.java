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
