/**
 * Interface zu Serial Communication
 *
 * Project: SubmatixBTConfigPC
 * Package: de.dmarcini.submatix.pclogger.comm
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 * Stand: 20.12.2011
 */
package de.dmarcini.submatix.pclogger.comm;

import gnu.io.CommPortIdentifier;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;



/**
 * Interface zu SerialCommunication
 *
 * Project: SubmatixBTConfigPC
 * Package: de.dmarcini.submatix.pclogger.comm
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 * Stand: 20.12.2011
 */
public interface ISerialCommunication
{
  /**
   * 
   * Logger f�r das Objekt setzen
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 19.12.2011
   * @param lg
   */
  public void setLogger( Logger lg );

  /**
   * 
   * Gib die Liste der Ports zur�ck
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 19.12.2011
   * @return Liste der Ports
   */
  public ArrayList<CommPortIdentifier> getPortList();

  /**
   * 
   * Gib die Namen der Ports als StringArray zur�ck
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 19.12.2011
   * @return Array mit Namen der Schnittstellen
   */
  public String[] getNameArray();

  /**
   * 
   * Callback für Actions
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 21.12.2011
   * @param al der Listener
   */
  public void addActionListener( ActionListener al );
  
  /**
   * 
   * Action Listene entfernen
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 21.12.2011
   */
  public void removeActionListener();
  
  /**
   * 
   * Portliste des Systems auffrischen
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 19.12.2011
   * @return Liste der Ports
   */
  public ArrayList<CommPortIdentifier> refreshPortList();
  
  /**
   * 
   * Ist das Comm-Ger�t verbunden?
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 20.12.2011
   * @return ist device verbunden
   */
  public boolean isConnected();
  
  /**
   * 
   * Verbinde mit dem SPX42 
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 20.12.2011
   * @param portName
   * @throws Exception 
   */
  public void connectDevice( String portName ) throws Exception;
  
  /**
   * 
   * L�st die Verbindung zum Ger�t
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 20.12.2011
   */
  public void disconnectDevice();

  /**
   * 
   * Schreibe direkt an das Ger�t
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 20.12.2011
   * @param msg
   */
  public void writeToDevice( String msg );
  
  /**
   * 
   * Formatiere für SPX-Übertragung und sende an SPX42
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 20.12.2011
   * @param msg
   */
  public void writeSPXMsgToDevice( String msg );
 
  /**
   * 
   * Seriennummer erfragen
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.comm
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 24.12.2011
   */
  public void askForSerialNumber();
  
  public void readConfigFromSPX42();
  
  
}
