package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import de.dmarcini.submatix.pclogger.lang.LangStrings;
import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.AliasEditTableModel;
import de.dmarcini.submatix.pclogger.utils.DeviceComboBoxModel;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

/**
 * Panel zur Verbindung mit den SPX-Computern
 * 
 * @author Dirk Marciniak 22.08.2013
 */
public class spx42ConnectPanel extends JPanel implements TableModelListener, ActionListener
{
  /**
   * 
   */
  private static final long    serialVersionUID = 1L;
  protected Logger             lg               = null;
  public JButton               connectButton;
  public JButton               deviceAliasButton;
  public JTable                aliasEditTable;
  private JScrollPane          aliasScrollPane;
  private Vector<String>       columnNames      = null;
  private Vector<String[]>     aliasData        = null;
  private LogDerbyDatabaseUtil databaseUtil     = null;
  private ResourceBundle       stringsBundle    = null;
  private ActionListener       aListener        = null;
  private JLabel               messageConnectToastLabel;
  private JComboBox<String>    virtualDeviceComboBox;
  private JLabel               virtualDevicesLabel;
  private JButton              renewVirtButton;

  @SuppressWarnings( "unused" )
  private spx42ConnectPanel()
  {
    setPreferredSize( new Dimension( 796, 504 ) );
    initPanel();
  }

  /**
   * Create the panel.
   * 
   * @param _dbUtil
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public spx42ConnectPanel( final LogDerbyDatabaseUtil _dbUtil ) throws SQLException, ClassNotFoundException
  {
    this.lg = SpxPcloggerProgramConfig.LOGGER;
    this.databaseUtil = _dbUtil;
    aliasData = null;
    columnNames = new Vector<String>();
    columnNames.add( "DEVICE" );
    columnNames.add( "ALIAS" );
    initPanel();
    if( !databaseUtil.isOpenDB() )
    {
      databaseUtil.createConnection();
    }
    setAliasesEditable( false );
  }

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    //
    if( ev.getSource() instanceof JComboBox<?> )
    {
      @SuppressWarnings( "unchecked" )
      JComboBox<String> srcBox = ( JComboBox<String> )ev.getSource();
      String entry;
      // /////////////////////////////////////////////////////////////////////////
      // Auswahl welches virtuelle Gerät soll verbunden werden
      if( cmd.equals( "virt_dev_to_connect" ) )
      {
        if( srcBox.getSelectedIndex() == -1 )
        {
          // nix selektiert
          return;
        }
        entry = srcBox.getItemAt( srcBox.getSelectedIndex() );
        lg.debug( "select virtual port <" + entry + ">..." );
      }
      else
      {
        lg.warn( "unknown combobox command <" + cmd + "> recived!" );
      }
    }
    else if( ev.getSource() instanceof JButton )
    {
      // /////////////////////////////////////////////////////////////////////////
      // Verbinde mit Device
      if( cmd.equals( "connect" ) )
      {
        // ich will mit virtuellem Gerät verbinden
        if( virtualDeviceComboBox.getSelectedIndex() != -1 )
        {
          String device = virtualDeviceComboBox.getItemAt( virtualDeviceComboBox.getSelectedIndex() );
          if( device.equals( LangStrings.getString( "spx42ConnectPanel.virtualDeviceComboBox.initialModel" ) ) )
          {
            lg.debug( "No port selected, ports are searching..." );
            return;
          }
          lg.debug( "connect virtual port <" + device + ">..." );
          if( aListener != null )
          {
            ActionEvent evnt = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTVIRTDEVICE, device );
            aListener.actionPerformed( evnt );
          }
        }
      }
      // /////////////////////////////////////////////////////////////////////////
      // Trenne Verbindung
      else if( cmd.equals( "disconnect" ) )
      {
        if( aListener != null )
        {
          ActionEvent evnt = new ActionEvent( this, ProjectConst.MESSAGE_DISCONNECTBTDEVICE, " " );
          aListener.actionPerformed( evnt );
        }
        lg.debug( "disconnect!" );
      }
    }
    else
    {
      lg.warn( "unknown action command <" + cmd + "> recived!" );
    }
  }

  /**
   * Initialisiere das Panel für die Verbindungen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   */
  private void initPanel()
  {
    connectButton = new JButton( LangStrings.getString( "spx42ConnectPanel.connectButton.connectText" ) );
    connectButton.setHorizontalAlignment( SwingConstants.LEFT );
    connectButton.setIconTextGap( 15 );
    connectButton.setLocation( 347, 24 );
    connectButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/112-mono.png" ) ) );
    connectButton.setActionCommand( "connect" );
    connectButton.setPreferredSize( new Dimension( 180, 40 ) );
    connectButton.setMaximumSize( new Dimension( 160, 40 ) );
    connectButton.setSize( new Dimension( 426, 41 ) );
    connectButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    deviceAliasButton = new JButton( LangStrings.getString( "spx42ConnectPanel.deviceAliasButton.edit.text" ) ); //$NON-NLS-1$
    deviceAliasButton.setMargin( new Insets( 2, 30, 2, 14 ) );
    deviceAliasButton.setHorizontalAlignment( SwingConstants.LEFT );
    deviceAliasButton.setIconTextGap( 15 );
    deviceAliasButton.setBounds( 347, 76, 426, 39 );
    deviceAliasButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/45.png" ) ) );
    deviceAliasButton.setActionCommand( "alias_bt_devices_on" );
    setLayout( null );
    add( connectButton );
    add( deviceAliasButton );
    aliasScrollPane = new JScrollPane();
    aliasScrollPane.setBounds( 347, 126, 426, 298 );
    add( aliasScrollPane );
    aliasEditTable = new JTable();
    aliasEditTable.setCellSelectionEnabled( true );
    aliasEditTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    aliasScrollPane.setViewportView( aliasEditTable );
    messageConnectToastLabel = new JLabel( "-" );
    messageConnectToastLabel.setForeground( Color.DARK_GRAY );
    messageConnectToastLabel.setFont( new Font( "Tahoma", Font.ITALIC, 11 ) );
    messageConnectToastLabel.setHorizontalAlignment( SwingConstants.CENTER );
    messageConnectToastLabel.setBounds( 10, 479, 763, 14 );
    add( messageConnectToastLabel );
    virtualDeviceComboBox = new JComboBox<String>();
    virtualDeviceComboBox.setActionCommand( "virt_dev_to_connect" );
    virtualDeviceComboBox.setBounds( 39, 89, 281, 26 );
    add( virtualDeviceComboBox );
    virtualDevicesLabel = new JLabel( LangStrings.getString( "spx42ConnectPanel.virtualDevicesLabel.text" ) ); //$NON-NLS-1$
    virtualDevicesLabel.setBounds( 39, 75, 281, 14 );
    add( virtualDevicesLabel );
    renewVirtButton = new JButton( LangStrings.getString( "spx42ConnectPanel.renewVirtButton.text" ) ); //$NON-NLS-1$
    renewVirtButton.setIconTextGap( 15 );
    renewVirtButton.setMargin( new Insets( 2, 30, 2, 14 ) );
    renewVirtButton.setHorizontalAlignment( SwingConstants.LEFT );
    renewVirtButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/112.png" ) ) );
    renewVirtButton.setBounds( 39, 24, 281, 41 );
    renewVirtButton.setActionCommand( "renew_virt_buttons" );
    add( renewVirtButton );
  }

  /**
   * Alias tabelle auffrischen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 26.04.2012
   */
  public void refreshAliasTable()
  {
    columnNames.clear();
    columnNames.add( LangStrings.getString( "spx42ConnectPanel.aliasTableColumn00.text" ) );
    columnNames.add( LangStrings.getString( "spx42ConnectPanel.aliasTableColumn01.text" ) );
    lg.debug( "fill aliases in stringarray..." );
    aliasData = databaseUtil.getAliasDataConn();
    if( aliasData != null )
    {
      AliasEditTableModel alMod = new AliasEditTableModel( aliasData, columnNames );
      alMod.addTableModelListener( this );
      aliasEditTable.setModel( alMod );
    }
  }

  /**
   * Aliastabelle ein oder ausblenden Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.05.2012
   * @param editable
   */
  public void setAliasesEditable( boolean editable )
  {
    aliasScrollPane.setVisible( editable );
    aliasEditTable.setEnabled( editable );
    // Kommando für den Button ändern
    if( editable )
    {
      deviceAliasButton.setActionCommand( "alias_bt_devices_off" );
    }
    else
    {
      deviceAliasButton.setActionCommand( "alias_bt_devices_on" );
    }
    // Beschriftung des buttons ändern
    if( stringsBundle != null )
    {
      if( editable )
      {
        deviceAliasButton.setText( LangStrings.getString( "spx42ConnectPanel.deviceAliasButton.noedit.text" ) );
      }
      else
      {
        deviceAliasButton.setText( LangStrings.getString( "spx42ConnectPanel.deviceAliasButton.edit.text" ) );
      }
    }
  }

  /**
   * Schreibe eine Meldung für BT auf die Oberfläche Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.09.2012
   * @param msg
   */
  public void setToastMessage( String msg )
  {
    messageConnectToastLabel.setText( msg );
  }

  /**
   * Setze eine neue Deviceliste Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 21.08.2013
   * @param _virtDeviceModell
   */
  public void setNewVirtDeviceList( final DefaultComboBoxModel<String> _virtDeviceModell )
  {
    virtualDeviceComboBox.setModel( _virtDeviceModell );
  }

  /**
   * Bei wechsel des Verbindungszustandes muss einiges umgeräumt wereden Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 26.04.2012
   * @param active
   */
  public void setElementsConnected( boolean active )
  {
    connectButton.setEnabled( true );
    // unterscheide connected oder nicht
    if( active )
    {
      // einfacher Fall, Verbunden, alles deaktivieren
      virtualDeviceComboBox.setEnabled( false );
      deviceAliasButton.setEnabled( false );
      if( stringsBundle != null )
      {
        connectButton.setText( LangStrings.getString( "spx42ConnectPanel.connectButton.disconnectText" ) );
      }
      connectButton.setActionCommand( "disconnect" );
      connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112.png" ) ) );
    }
    else
    {
      virtualDeviceComboBox.setEnabled( true );
      deviceAliasButton.setEnabled( true );
      if( stringsBundle != null )
      {
        connectButton.setText( LangStrings.getString( "spx42ConnectPanel.connectButton.connectText" ) );
      }
      connectButton.setActionCommand( "connect" );
      connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112-mono.png" ) ) );
    }
  }

  /**
   * Die Combobox enablen/disablen (wenn Devices gesucht werden sollte die diabled sein) Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 21.08.2013
   * @param _en
   */
  public void setVirtDevicesBoxEnabled( boolean _en )
  {
    virtualDeviceComboBox.setEnabled( _en );
    renewVirtButton.setEnabled( _en );
  }

  /**
   * Elemente bei Bedarf abschalten Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 26.04.2012
   * @param inactive
   */
  public void setElementsInactive( boolean inactive )
  {
    connectButton.setEnabled( !inactive );
  }

  /**
   * Setze die Listener auf das Hauptobjekt Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   * @param mainCommGUI
   *          das Hauptobjekt
   */
  public void setGlobalChangeListener( MainCommGUI mainCommGUI )
  {
    this.aListener = mainCommGUI;
    //
    virtualDeviceComboBox.addActionListener( this );
    virtualDeviceComboBox.addMouseMotionListener( mainCommGUI );
    connectButton.addActionListener( this );
    connectButton.addMouseMotionListener( mainCommGUI );
    deviceAliasButton.addActionListener( mainCommGUI );
    deviceAliasButton.addMouseMotionListener( mainCommGUI );
    renewVirtButton.addActionListener( mainCommGUI );
  }

  /**
   * Sezte alle Strings in die entsprechende Landessprache! Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   * @param stringsBundle
   *          Resource für die Strings
   * @param connected
   *          Ist der SPX verbunden?
   * @return in Ordnung oder nicht
   */
  public int setLanguageStrings( ResourceBundle stringsBundle, boolean connected )
  {
    this.stringsBundle = stringsBundle;
    try
    {
      connectButton.setToolTipText( LangStrings.getString( "spx42ConnectPanel.connectButton.tooltiptext" ) );
      if( connected )
      {
        connectButton.setText( LangStrings.getString( "spx42ConnectPanel.connectButton.disconnectText" ) );
        connectButton.setActionCommand( "disconnect" );
      }
      else
      {
        connectButton.setText( LangStrings.getString( "spx42ConnectPanel.connectButton.connectText" ) );
        connectButton.setActionCommand( "connect" );
      }
      // Abhängig von der Sichtbarkeit der Aliaseditfläche
      if( aliasScrollPane.isVisible() )
      {
        deviceAliasButton.setText( LangStrings.getString( "spx42ConnectPanel.deviceAliasButton.noedit.text" ) );
      }
      else
      {
        deviceAliasButton.setText( LangStrings.getString( "spx42ConnectPanel.deviceAliasButton.edit.text" ) );
      }
      deviceAliasButton.setToolTipText( LangStrings.getString( "spx42ConnectPanel.deviceAliasButton.tooltiptext" ) );
      //
      //
      columnNames.clear();
      columnNames.add( LangStrings.getString( "spx42ConnectPanel.aliasTableColumn00.text" ) );
      columnNames.add( LangStrings.getString( "spx42ConnectPanel.aliasTableColumn01.text" ) );
      lg.debug( "fill aliases in stringarray..." );
      aliasData = databaseUtil.getAliasDataConn();
      if( aliasData != null )
      {
        AliasEditTableModel alMod = new AliasEditTableModel( aliasData, columnNames );
        alMod.addTableModelListener( this );
        aliasEditTable.setModel( alMod );
      }
      virtualDevicesLabel.setText( LangStrings.getString( "spx42ConnectPanel.virtualDevicesLabel.text" ) );
      virtualDeviceComboBox.setToolTipText( LangStrings.getString( "spx42ConnectPanel.virtualDeviceComboBox.tooltiptext" ) );
      if( !( virtualDeviceComboBox.getModel() instanceof DeviceComboBoxModel ) )
      {
        virtualDeviceComboBox.setModel( new DefaultComboBoxModel<String>( new String[]
        { stringsBundle.getString( "spx42ConnectPanel.virtualDeviceComboBox.initialModel" ) } ) );
      }
      renewVirtButton.setText( LangStrings.getString( "spx42ConnectPanel.renewVirtButton.text" ) );
      renewVirtButton.setToolTipText( LangStrings.getString( "spx42ConnectPanel.renewVirtButton.tooltiptext" ) );
    }
    catch( NullPointerException ex )
    {
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( -1 );
    }
    catch( MissingResourceException ex )
    {
      System.out.println( "ERROR set language strings - the given key can be found <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    catch( ClassCastException ex )
    {
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    return( 1 );
  }

  @Override
  public void tableChanged( TableModelEvent ev )
  {
    int row;
    String devName, devAlias;
    //
    // Es wurde die ALIAS-Tabelle verändert
    // was hat er verändert?
    row = ev.getFirstRow();
    lg.debug( String.format( "changedd row %d", row ) );
    // devicename erfragen
    devName = ( String )aliasEditTable.getModel().getValueAt( row, 0 );
    // AliasName erfrage
    devAlias = ( String )aliasEditTable.getModel().getValueAt( row, 1 );
    databaseUtil.updateDeviceAliasConn( devName, devAlias );
    // Jetzt die Verbindungsbox neu einlesen, sonst gibte Chaos ;-)
    lg.debug( "read combobox entrys again...." );
  }

  /**
   * Fehlerdialog anzeigen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 08.10.2012
   * @param message
   */
  @SuppressWarnings( "unused" )
  private void showErrorDialog( String message )
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Terminate.png" ) );
      JOptionPane.showMessageDialog( this, message, LangStrings.getString( "MainCommGUI.errorDialog.headline" ), JOptionPane.INFORMATION_MESSAGE, icon );
    }
    catch( NullPointerException ex )
    {
      lg.error( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      lg.error( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      lg.error( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }
}
