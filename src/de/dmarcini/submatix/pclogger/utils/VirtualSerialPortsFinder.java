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

import de.dmarcini.submatix.pclogger.ProjectConst;
import jssc.SerialPortList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

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
    SerialPortList serialPortList = new SerialPortList();
    boolean hasChanged = false;
    //
    if( aListener != null )
    {
      ActionEvent evnt = new ActionEvent(this, ProjectConst.MESSAGE_TOAST, "searching ports..." );
      aListener.actionPerformed( evnt );
    }
    //
    // Liste der ports holen
    //
    if( aListener != null )
    {
      ActionEvent evnt = new ActionEvent( this, ProjectConst.MESSAGE_TOAST, "searching ports...OK" );
      aListener.actionPerformed( evnt );
    }
    String[] list = serialPortList.getPortNames();
    //
    // die Liste abklappern
    // und Ergebnisse in Combo-Liste eintragen
    //
    if( virtDeviceModell == null )
    {
      virtDeviceModell = new DefaultComboBoxModel<>();
    }
    for( int i=0; i<list.length; i++)
    {
      // wenn der Name noch nicht aufgetaucht ist
      if( -1 == virtDeviceModell.getIndexOf( list[i] ) )
      {
        virtDeviceModell.addElement( list[i] );
        hasChanged = true;
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
