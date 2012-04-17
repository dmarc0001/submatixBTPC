/**
 * Kommunikation mit der Bluethooth-Schnittstelle
 *
 * Project: SubmatixBTConfigPC
 * Package: de.dmarcini.submatix.pclogger.comm
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 * Stand: 08.01.2012
 * TODO
 */
package de.dmarcini.submatix.pclogger.comm;

import java.awt.event.ActionListener;

/**
 * HEADLINE
 *
 * Project: SubmatixBTConfigPC
 * Package: de.dmarcini.submatix.pclogger.comm
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 * Stand: 08.01.2012
 */
public interface IBTCommunication
{
  public void addActionListener( ActionListener al );
  public void removeActionListener();
  public boolean discoverDevices();
  public String[] getNameArray();
  public boolean isConnected();
  public void connectDevice( String deviceName ) throws Exception;
  public void disconnectDevice();
  public void writeToDevice( String msg );
  public void writeSPXMsgToDevice( String msg );
  public void askForSerialNumber();
  public void readConfigFromSPX42();
  public void askForDeviceName();
  public void askForFirmwareVersion();
}
