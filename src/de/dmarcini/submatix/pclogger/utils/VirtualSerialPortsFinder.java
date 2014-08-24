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

import gnu.io.CommPortIdentifier;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.DefaultComboBoxModel;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

public class VirtualSerialPortsFinder implements Runnable
{
  private ActionListener               aListener        = null;
  private DefaultComboBoxModel<String> virtDeviceModell = null;

  public VirtualSerialPortsFinder( ActionListener _listener, DefaultComboBoxModel<String> _virtDeviceModell )
  {
    aListener = _listener;
    virtDeviceModell = _virtDeviceModell;
  }

  public DefaultComboBoxModel<String> getComboBoxModel()
  {
    return( virtDeviceModell );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public void run()
  {
    CommPortIdentifier portId;
    Enumeration<CommPortIdentifier> portList;
    boolean hasChanged = false;
    //
    if( aListener != null )
    {
      ActionEvent evnt = new ActionEvent( this, ProjectConst.MESSAGE_TOAST, "searching ports..." );
      aListener.actionPerformed( evnt );
    }
    //
    // Liste der ports holen
    //
    portList = CommPortIdentifier.getPortIdentifiers();
    if( aListener != null )
    {
      ActionEvent evnt = new ActionEvent( this, ProjectConst.MESSAGE_TOAST, "searching ports...OK" );
      aListener.actionPerformed( evnt );
    }
    //
    // die Liste abklappern
    // und Ergebnisse in Combo-Liste eintragen
    //
    if( virtDeviceModell == null )
    {
      virtDeviceModell = new DefaultComboBoxModel<String>();
    }
    while( portList.hasMoreElements() )
    {
      portId = portList.nextElement();
      if( portId.getPortType() == CommPortIdentifier.PORT_SERIAL )
      {
        if( -1 == virtDeviceModell.getIndexOf( portId.getName() ) )
        {
          virtDeviceModell.addElement( portId.getName() );
          hasChanged = true;
        }
      }
    }
    if( aListener != null )
    {
      ActionEvent evnt;
      if( hasChanged )
      {
        // wenn verändert, bescheid geben
        evnt = new ActionEvent( this, ProjectConst.MESSAGE_PORT_STATE_CHANGE, null );
        aListener.actionPerformed( evnt );
      }
      // Toast durchstreichen
      evnt = new ActionEvent( this, ProjectConst.MESSAGE_TOAST, " " );
      aListener.actionPerformed( evnt );
    }
  }
}
