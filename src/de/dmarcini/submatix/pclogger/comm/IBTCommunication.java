/**
 * Kommunikation mit der Bluethooth-Schnittstelle
 * 
 * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 08.01.2012
 */
package de.dmarcini.submatix.pclogger.comm;

import java.awt.event.ActionListener;
import java.util.Vector;

import de.dmarcini.submatix.pclogger.utils.SPX42Config;
import de.dmarcini.submatix.pclogger.utils.SPX42GasList;

/**
 * HEADLINE
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

  public void removeActionListener();

  public boolean discoverDevices( final boolean cached );

  /**
   * 
   * Gibt ein Array von Namen zurück. Es handelt sich aber um die Aliase
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 26.04.2012
   * @param aliasFromDB
   * @return NAmen und Aliase
   */
  public Vector<String[]> getNameArray( boolean aliasFromDB );

  public boolean isConnected();

  public void connectDevice( String deviceName ) throws Exception;

  public String getConnectedDevice();

  public void disconnectDevice();

  public void writeToDevice( String msg );

  public void writeSPXMsgToDevice( String msg );

  public void askForSerialNumber();

  public void readConfigFromSPX42();

  public void readGaslistFromSPX42();

  public void askForLicenseFromSPX();

  public void writeGaslistToSPX42( SPX42GasList gList, String spxVersion );

  public void askForDeviceName();

  public void askForFirmwareVersion();

  public void askForSPXAlive();

  public void setPinForDevice( String dev, String pin );

  public String getPinForDevice( String dev );

  public void writeConfigToSPX( SPX42Config config );

  public void readLogDirectoryFromSPX();

  public void readLogDetailFromSPX( int logNumber );

  // experimentell...
  public String getDeviceInfos();

  public void putDeviceInfos( String infos ) throws Exception;
}
