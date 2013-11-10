/**
 * Kommunikation mit der Bluethooth-Schnittstelle
 * 
 * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
 * 
 */
package de.dmarcini.submatix.pclogger.comm;

import gnu.io.PortInUseException;

import java.awt.event.ActionListener;

import org.joda.time.DateTime;

import de.dmarcini.submatix.pclogger.utils.SPX42Config;
import de.dmarcini.submatix.pclogger.utils.SPX42GasList;

/**
 * Schnittstellenbeschreibung für die BT-Kommunikation
 * 
 * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 08.01.2012
 */
public interface IBTCommunication
{
  public void addActionListener( ActionListener al );

  public void askForDeviceName();

  public void askForFirmwareVersion();

  public void askForLicenseFromSPX();

  public void askForSerialNumber();

  public void askForSPXAlive();

  public void connectVirtDevice( String deviceName ) throws PortInUseException, Exception;

  public void disconnectDevice();

  public String getConnectedDevice();

  // experimentell...
  public String getDeviceInfos();

  public boolean isConnected();

  // experimentell...
  public void putDeviceInfos( String infos ) throws Exception;

  public void readConfigFromSPX42();

  public void readGaslistFromSPX42();

  public void readLogDetailFromSPX( int logNumber );

  public void readLogDirectoryFromSPX();

  public void removeActionListener();

  public void setNameForVirtualDevice( String serialNumber );

  public void writeConfigToSPX( SPX42Config config );

  public void writeDateTimeToDevice( DateTime dTime );

  public void writeGaslistToSPX42( SPX42GasList gList, boolean isOldParamSorting );

  public void writeSPXMsgToDevice( String msg );

  public void writeToDevice( String msg );
}
