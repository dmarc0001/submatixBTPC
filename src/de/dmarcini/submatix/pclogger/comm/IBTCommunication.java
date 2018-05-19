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
 * Kommunikation mit der Bluethooth-Schnittstelle
 * 
 * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.comm
 * 
 */
package de.dmarcini.submatix.pclogger.comm;

import java.awt.event.ActionListener;

import jssc.SerialPortException;
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
  /**
   * 
   * Füge einen ActionListener eines Prozesses hinzu
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param al
   */
  public void addActionListener( ActionListener al );

  /**
   * 
   * Wie ist der Gerätename (SPX anfragen)?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void askForDeviceName();

  /**
   * 
   * Welche Firmwareversion (SPX anfragen)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void askForFirmwareVersion();

  /**
   * 
   * Den SPX nach der Lizenz fragen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void askForLicenseFromSPX();

  /**
   * 
   * Den SPX42 nach der Seriennummer fragen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void askForSerialNumber();

  /**
   * 
   * Heartbeat fragen (und Ackuzustand)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void askForSPXAlive();

  /**
   * 
   * Verbinde mit virtuellem Comport zum SPX42
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param deviceName
   * @throws SerialPortException
   * @throws Exception
   */
  public void connectVirtDevice( String deviceName ) throws SerialPortException, Exception;

  /**
   * 
   * Verbindung zum SPX42 trennen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void disconnectDevice();

  /**
   * 
   * Mit welchem Gerät bin ich momentan verbunden?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @return verbundenes Gerät
   */
  public String getConnectedDevice();

  /**
   * 
   * Experimentelle Funktion
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @return Geräteinfos
   * 
   */
  public String getDeviceInfos();

  /**
   * 
   * Ist eine Verbindung zu einem SPX42 etsabliert?
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @return Verbunden oder nicht
   */
  public boolean isConnected();

  // experimentell...
  /**
   * 
   * Lege Infos ze einem Gerät ab (experimentell)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param infos
   * @throws Exception
   */
  public void putDeviceInfos( String infos ) throws Exception;

  /**
   * 
   * Lese Konfiguration aus dem SPX42 aus
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void readConfigFromSPX42();

  /**
   * 
   * LEse Gesliste aus dem SPX42 aus
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void readGaslistFromSPX42();

  /**
   * 
   * Lese Detail zu einem geloggten Tauchgang aus
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param logNumber
   */
  public void readLogDetailFromSPX( int logNumber );

  /**
   * 
   * Lese das Verzeichnis geloggter Tauchgänge aus
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void readLogDirectoryFromSPX();

  /**
   * 
   * Entferne den Actionlistener für einen Prozess
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   */
  public void removeActionListener();

  /**
   * 
   * Setze den Namen für ein virtuelles Gerät
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param serialNumber
   */
  public void setNameForVirtualDevice( String serialNumber );

  /**
   * 
   * Schreibe die Konfiguration in den SPX42
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param config
   */
  public void writeConfigToSPX( SPX42Config config );

  /**
   * 
   * Schreibe Datum und Zeit in den SPX42
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param dTime
   */
  public void writeDateTimeToDevice( DateTime dTime );

  /**
   * 
   * Schreibe die Gasliste in den SPX42
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param gList
   * @param isOldParamSorting
   */
  public void writeGaslistToSPX42( SPX42GasList gList, boolean isOldParamSorting );

  /**
   * 
   * Schreibe ein Kommando direkt zum SPX42
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param msg
   */
  public void writeSPXMsgToDevice( String msg );

  /**
   * 
   * Schreibe ROH in den SPX42
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.comm
   * 
   * Stand: 06.12.2013
   * 
   * @param msg
   */
  public void writeToDevice( String msg );
}
