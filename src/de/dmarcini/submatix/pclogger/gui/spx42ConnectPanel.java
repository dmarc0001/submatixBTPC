package de.dmarcini.submatix.pclogger.gui;

import gnu.io.CommPortIdentifier;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import de.dmarcini.submatix.pclogger.comm.BTCommunication;
import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.AliasEditTableModel;
import de.dmarcini.submatix.pclogger.utils.DeviceComboBoxModel;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;

public class spx42ConnectPanel extends JPanel implements TableModelListener, ActionListener
{
  /**
   * 
   */
  private static final long    serialVersionUID = 1L;
  protected Logger             LOGGER           = null;
  public JComboBox             deviceToConnectComboBox;
  public JButton               connectButton;
  public JButton               connectBtRefreshButton;
  public JProgressBar          discoverProgressBar;
  public JButton               pinButton;
  public JButton               deviceAliasButton;
  public JTable                aliasEditTable;
  private JScrollPane          aliasScrollPane;
  private Vector<String>       columnNames      = null;
  private Vector<String[]>     aliasData        = null;
  private LogDerbyDatabaseUtil databaseUtil     = null;
  private ResourceBundle       stringsBundle    = null;
  private BTCommunication      btComm           = null;
  private ActionListener       aListener        = null;
  private JLabel               discoverBtLabel;
  private JLabel               messageBtLabel;
  private JComboBox            virtualDeviceComboBox;
  private JLabel               bluethoothDirectLabel;
  private JLabel               virtualDevicesLabel;
  private final ButtonGroup    buttonGroup      = new ButtonGroup();
  private JRadioButton         btDirectRadioButton;
  private JRadioButton         virtualDeviceRadioButton;

  @SuppressWarnings( "unused" )
  private spx42ConnectPanel()
  {
    setPreferredSize( new Dimension( 796, 504 ) );
    initPanel();
  }

  /**
   * Create the panel.
   * 
   * @param LOGGER
   * @param _dbUtil
   * @param btComm
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public spx42ConnectPanel( final Logger LOGGER, final LogDerbyDatabaseUtil _dbUtil, final BTCommunication btComm ) throws SQLException, ClassNotFoundException
  {
    this.LOGGER = LOGGER;
    LOGGER.log( Level.FINE, "constructor..." );
    this.databaseUtil = _dbUtil;
    this.btComm = btComm;
    // dbUtil.closeDB();
    aliasData = null;
    columnNames = new Vector<String>();
    columnNames.add( "DEVICE" );
    columnNames.add( "ALIAS" );
    initPanel();
    if( !databaseUtil.isOpenDB() )
    {
      databaseUtil.createConnection();
    }
    // aliasData = databaseUtil.getAliasDataConn();
    setAliasesEditable( false );
    fillVirtualDevicesComboBox();
    btDirectRadioButton.setSelected( true );
    setBtDirect();
  }

  /**
   * 
   * GUI für Direct Bluethooth vorbereiten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.10.2012
   */
  private void setBtDirect()
  {
    deviceToConnectComboBox.setEnabled( true );
    virtualDeviceComboBox.setEnabled( false );
    pinButton.setEnabled( true );
    deviceAliasButton.setEnabled( true );
    connectBtRefreshButton.setEnabled( true );
  }

  /**
   * 
   * GUI für Verbindung via Virtual Device vorbereiten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.10.2012
   */
  private void setVirtualDevice()
  {
    deviceToConnectComboBox.setEnabled( false );
    virtualDeviceComboBox.setEnabled( true );
    pinButton.setEnabled( false );
    deviceAliasButton.setEnabled( false );
    connectBtRefreshButton.setEnabled( false );
  }

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    //
    if( ev.getSource() instanceof JRadioButton )
    {
      JRadioButton rb = ( JRadioButton )ev.getSource();
      // //////////////////////////////////////////////////////////////////////
      // Bluethooth direkt Kommunizieren
      if( cmd.equals( "connect_bt_direct" ) )
      {
        LOGGER.fine( "Radiobutton <bt direct>" );
        setBtDirect();
        return;
      }
      // //////////////////////////////////////////////////////////////////////
      // Virtual Device zum verbinden
      else if( cmd.equals( "connect_virt_dev" ) )
      {
        LOGGER.fine( "Radiobutton <virtual devices>" );
        setVirtualDevice();
        return;
      }
      else
      {
        LOGGER.warning( "unknown Radiobutton slected!" );
      }
    }
    else if( ev.getSource() instanceof JComboBox )
    {
      JComboBox srcBox = ( JComboBox )ev.getSource();
      String entry;
      // /////////////////////////////////////////////////////////////////////////
      // Auswahl welches Gerät soll verbunden werden
      if( cmd.equals( "bt_device_to_connect" ) )
      {
        if( srcBox.getSelectedIndex() == -1 )
        {
          // nix selektiert
          return;
        }
        entry = ( String )srcBox.getItemAt( srcBox.getSelectedIndex() );
        LOGGER.fine( "select bluethooth port <" + entry + ">..." );
      }
      // /////////////////////////////////////////////////////////////////////////
      // Auswahl welches virtuelle Gerät soll verbunden werden
      else if( cmd.equals( "virt_dev_to_connect" ) )
      {
        if( srcBox.getSelectedIndex() == -1 )
        {
          // nix selektiert
          return;
        }
        entry = ( String )srcBox.getItemAt( srcBox.getSelectedIndex() );
        LOGGER.fine( "select virtual port <" + entry + ">..." );
      }
      else
      {
        LOGGER.warning( "unknown combobox command <" + cmd + "> recived!" );
      }
    }
    else if( ev.getSource() instanceof JButton )
    {
      // /////////////////////////////////////////////////////////////////////////
      // Verbinde mit Device
      if( cmd.equals( "connect" ) )
      {
        if( btDirectRadioButton.isSelected() )
        {
          // ich will direkt mit BT verbinden
          if( deviceToConnectComboBox.getSelectedIndex() != -1 )
          {
            int itemIndex = deviceToConnectComboBox.getSelectedIndex();
            String device = ( ( DeviceComboBoxModel )deviceToConnectComboBox.getModel() ).getDeviceIdAt( itemIndex );
            // guck nach, ob das Gerät online ist
            if( ( ( DeviceComboBoxModel )deviceToConnectComboBox.getModel() ).getWasOnlineAt( itemIndex ) )
            {
              LOGGER.fine( "connect bluethooth device <" + device + ">..." );
              if( aListener != null )
              {
                ActionEvent evnt = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTBTDEVICE, device );
                aListener.actionPerformed( evnt );
              }
            }
            else
            {
              String deviceAlias = ( ( DeviceComboBoxModel )deviceToConnectComboBox.getModel() ).getDeviceAliasAt( itemIndex );
              showErrorDialog( String.format( stringsBundle.getString( "MainCommGUI.errorDialog.deviceNotConnected" ), device + "/" + deviceAlias ) );
              LOGGER.warning( "the device <" + device + "> was not online!" );
            }
          }
        }
        else
        {
          // ich will mit virtuellem Gerät verbinden
          if( virtualDeviceComboBox.getSelectedIndex() != -1 )
          {
            String device = ( String )virtualDeviceComboBox.getItemAt( virtualDeviceComboBox.getSelectedIndex() );
            LOGGER.fine( "connect virtual port <" + device + ">..." );
            if( aListener != null )
            {
              ActionEvent evnt = new ActionEvent( this, ProjectConst.MESSAGE_CONNECTVIRTDEVICE, device );
              aListener.actionPerformed( evnt );
            }
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
        LOGGER.fine( "disconnect!" );
      }
    }
    else
    {
      LOGGER.warning( "unknown action command <" + cmd + "> recived!" );
    }
  }

  /**
   * 
   * Virtualle und echte Serial ports auflisten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.10.2012
   */
  @SuppressWarnings( "unchecked" )
  private void fillVirtualDevicesComboBox()
  {
    CommPortIdentifier portId;
    Enumeration<CommPortIdentifier> portList;
    DefaultComboBoxModel mod;
    //
    // Liste der ports holen
    //
    portList = CommPortIdentifier.getPortIdentifiers();
    //
    // die Liste abklappern
    // und Ergebnisse in Combo-Liste eintragen
    //
    mod = new DefaultComboBoxModel();
    while( portList.hasMoreElements() )
    {
      portId = portList.nextElement();
      if( portId.getPortType() == CommPortIdentifier.PORT_SERIAL )
      {
        mod.addElement( portId.getName() );
      }
    }
    virtualDeviceComboBox.setModel( mod );
  }

  /**
   * Initialisiere das Panel für die Verbindungen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   */
  private void initPanel()
  {
    deviceToConnectComboBox = new JComboBox();
    deviceToConnectComboBox.setActionCommand( "bt_device_to_connect" );
    deviceToConnectComboBox.setBounds( 44, 39, 281, 26 );
    deviceToConnectComboBox.setPreferredSize( new Dimension( 220, 40 ) );
    deviceToConnectComboBox.setMinimumSize( new Dimension( 180, 20 ) );
    deviceToConnectComboBox.setMaximumSize( new Dimension( 500, 40 ) );
    deviceToConnectComboBox.setModel( new DeviceComboBoxModel() );
    connectButton = new JButton( "CONNECT" );
    connectButton.setHorizontalAlignment( SwingConstants.LEFT );
    connectButton.setIconTextGap( 15 );
    connectButton.setLocation( 347, 24 );
    connectButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/112-mono.png" ) ) );
    connectButton.setActionCommand( "connect" );
    connectButton.setPreferredSize( new Dimension( 180, 40 ) );
    connectButton.setMaximumSize( new Dimension( 160, 40 ) );
    connectButton.setSize( new Dimension( 295, 41 ) );
    connectButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    connectBtRefreshButton = new JButton( "REFRESH" );
    connectBtRefreshButton.setIconTextGap( 15 );
    connectBtRefreshButton.setBounds( 347, 83, 426, 39 );
    connectBtRefreshButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Refresh.png" ) ) );
    connectBtRefreshButton.setActionCommand( "refresh_bt_devices" );
    discoverProgressBar = new JProgressBar();
    discoverProgressBar.setBounds( 10, 441, 763, 14 );
    discoverProgressBar.setBorder( null );
    discoverProgressBar.setBackground( new Color( 240, 248, 255 ) );
    discoverProgressBar.setForeground( new Color( 176, 224, 230 ) );
    pinButton = new JButton( "PINBUTTON" );
    pinButton.setHorizontalAlignment( SwingConstants.LEFT );
    pinButton.setIconTextGap( 15 );
    pinButton.setBounds( 648, 24, 125, 41 );
    pinButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Unlock.png" ) ) );
    pinButton.setActionCommand( "set_pin_for_dev" );
    deviceAliasButton = new JButton( "ALIAS" );
    deviceAliasButton.setIconTextGap( 15 );
    deviceAliasButton.setBounds( 347, 133, 426, 39 );
    deviceAliasButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/45.png" ) ) );
    deviceAliasButton.setActionCommand( "alias_bt_devices_on" );
    setLayout( null );
    add( deviceToConnectComboBox );
    add( connectBtRefreshButton );
    add( connectButton );
    add( pinButton );
    add( deviceAliasButton );
    add( discoverProgressBar );
    aliasScrollPane = new JScrollPane();
    aliasScrollPane.setBounds( 347, 183, 426, 247 );
    add( aliasScrollPane );
    aliasEditTable = new JTable();
    aliasEditTable.setCellSelectionEnabled( true );
    aliasEditTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    aliasScrollPane.setViewportView( aliasEditTable );
    discoverBtLabel = new JLabel( "SEARCHING BT" );
    discoverBtLabel.setLabelFor( discoverProgressBar );
    discoverBtLabel.setHorizontalAlignment( SwingConstants.CENTER );
    discoverBtLabel.setBounds( 10, 461, 763, 14 );
    add( discoverBtLabel );
    messageBtLabel = new JLabel( "-" );
    messageBtLabel.setForeground( Color.DARK_GRAY );
    messageBtLabel.setFont( new Font( "Tahoma", Font.ITALIC, 11 ) );
    messageBtLabel.setHorizontalAlignment( SwingConstants.CENTER );
    messageBtLabel.setBounds( 10, 479, 763, 14 );
    add( messageBtLabel );
    virtualDeviceComboBox = new JComboBox();
    virtualDeviceComboBox.setActionCommand( "virt_dev_to_connect" );
    virtualDeviceComboBox.setBounds( 44, 109, 281, 26 );
    add( virtualDeviceComboBox );
    bluethoothDirectLabel = new JLabel( "BLUETOOTH DIRECT:" );
    bluethoothDirectLabel.setBounds( 44, 24, 281, 14 );
    add( bluethoothDirectLabel );
    virtualDevicesLabel = new JLabel( "VIRTUAL DEVICE CONNECT:" );
    virtualDevicesLabel.setBounds( 44, 95, 281, 14 );
    add( virtualDevicesLabel );
    btDirectRadioButton = new JRadioButton( "" );
    btDirectRadioButton.setActionCommand( "connect_bt_direct" );
    buttonGroup.add( btDirectRadioButton );
    btDirectRadioButton.setBounds( 10, 41, 28, 23 );
    add( btDirectRadioButton );
    virtualDeviceRadioButton = new JRadioButton( "" );
    virtualDeviceRadioButton.setActionCommand( "connect_virt_dev" );
    buttonGroup.add( virtualDeviceRadioButton );
    virtualDeviceRadioButton.setBounds( 10, 111, 28, 23 );
    add( virtualDeviceRadioButton );
  }

  /**
   * Alias tabelle auffrischen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 26.04.2012 TODO
   */
  public void refreshAliasTable()
  {
    columnNames.clear();
    columnNames.add( stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn00.text" ) );
    columnNames.add( stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn01.text" ) );
    LOGGER.log( Level.FINE, "fill aliases in stringarray..." );
    aliasData = databaseUtil.getAliasDataConn();
    if( aliasData != null )
    {
      AliasEditTableModel alMod = new AliasEditTableModel( aliasData, columnNames );
      alMod.addTableModelListener( this );
      aliasEditTable.setModel( alMod );
    }
  }

  /**
   * 
   * Aliastabelle ein oder ausblenden
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.05.2012
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
        deviceAliasButton.setText( stringsBundle.getString( "spx42ConnectPanel.deviceAliasButton.noedit.text" ) );
      }
      else
      {
        deviceAliasButton.setText( stringsBundle.getString( "spx42ConnectPanel.deviceAliasButton.edit.text" ) );
      }
    }
  }

  /**
   * 
   * Schreibe eine Meldung für BT auf die Oberfläche
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 07.09.2012
   * @param msg
   */
  public void setBtMessage( String msg )
  {
    messageBtLabel.setText( msg );
  }

  /**
   * Bei wechsel des Verbindungszustandes muss einiges umgeräumt wereden Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 26.04.2012
   * @param active
   */
  public void setElementsConnected( boolean active )
  {
    btDirectRadioButton.setEnabled( !active );
    virtualDeviceRadioButton.setEnabled( !active );
    connectBtRefreshButton.setEnabled( !active );
    connectButton.setEnabled( true );
    // unterscheide connected oder nicht
    if( active )
    {
      // einfacher Fall, Verbunden, alles deaktivieren
      deviceToConnectComboBox.setEnabled( false );
      virtualDeviceComboBox.setEnabled( false );
      deviceAliasButton.setEnabled( false );
      pinButton.setEnabled( false );
      if( stringsBundle != null )
      {
        connectButton.setText( stringsBundle.getString( "spx42ConnectPanel.connectButton.disconnectText" ) );
      }
      connectButton.setActionCommand( "disconnect" );
      connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112.png" ) ) );
    }
    else
    {
      // nicht verbunden
      if( btDirectRadioButton.isSelected() )
      {
        setBtDirect();
      }
      else
      {
        setVirtualDevice();
      }
      if( stringsBundle != null )
      {
        connectButton.setText( stringsBundle.getString( "spx42ConnectPanel.connectButton.connectText" ) );
      }
      connectButton.setActionCommand( "connect" );
      connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112-mono.png" ) ) );
    }
  }

  /**
   * 
   * Setze Elemente während des BT Suchvorganges
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 09.05.2012
   * @param isDiscovering
   * 
   */
  public void setElementsDiscovering( boolean isDiscovering )
  {
    connectBtRefreshButton.setEnabled( !isDiscovering );
    discoverProgressBar.setVisible( isDiscovering );
    discoverBtLabel.setVisible( isDiscovering );
    messageBtLabel.setVisible( isDiscovering );
    connectButton.setEnabled( !isDiscovering );
    btDirectRadioButton.setEnabled( !isDiscovering );
    virtualDeviceRadioButton.setEnabled( !isDiscovering );
    if( isDiscovering )
    {
      // Klarer Fall alles disablen
      deviceToConnectComboBox.setEnabled( false );
      pinButton.setEnabled( false );
      virtualDeviceComboBox.setEnabled( false );
    }
    else
    {
      // enablen aber die richtigen
      if( btDirectRadioButton.isSelected() )
      {
        // der Fall für direct Bluethooth
        setBtDirect();
      }
      else
      {
        // der Fall für virtual Devices
        setVirtualDevice();
      }
      messageBtLabel.setText( "-" );
    }
  }

  /**
   * Elemente bei Bedarf abschalten Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 26.04.2012
   * @param inactive
   */
  public void setElementsInactive( boolean inactive )
  {
    deviceToConnectComboBox.setEnabled( !inactive );
    connectButton.setEnabled( !inactive );
    pinButton.setEnabled( !inactive );
    connectBtRefreshButton.setEnabled( inactive );
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
    deviceToConnectComboBox.addActionListener( this );
    deviceToConnectComboBox.addMouseMotionListener( mainCommGUI );
    virtualDeviceComboBox.addActionListener( this );
    virtualDeviceComboBox.addMouseMotionListener( mainCommGUI );
    connectButton.addActionListener( this );
    connectButton.addMouseMotionListener( mainCommGUI );
    connectBtRefreshButton.addActionListener( mainCommGUI );
    connectBtRefreshButton.addMouseMotionListener( mainCommGUI );
    pinButton.addActionListener( mainCommGUI );
    pinButton.addMouseMotionListener( mainCommGUI );
    deviceAliasButton.addActionListener( mainCommGUI );
    deviceAliasButton.addMouseMotionListener( mainCommGUI );
    btDirectRadioButton.addActionListener( this );
    virtualDeviceRadioButton.addActionListener( this );
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
      deviceToConnectComboBox.setToolTipText( stringsBundle.getString( "spx42ConnectPanel.portComboBox.tooltiptext" ) );
      connectButton.setToolTipText( stringsBundle.getString( "spx42ConnectPanel.connectButton.tooltiptext" ) );
      pinButton.setToolTipText( stringsBundle.getString( "spx42ConnectPanel.pinButton.tooltiptext" ) );
      pinButton.setText( stringsBundle.getString( "spx42ConnectPanel.pinButton.text" ) );
      discoverBtLabel.setText( stringsBundle.getString( "spx42ConnectPanel.discoverBtLabel.text" ) );
      if( connected )
      {
        connectButton.setText( stringsBundle.getString( "spx42ConnectPanel.connectButton.disconnectText" ) );
        connectButton.setActionCommand( "disconnect" );
      }
      else
      {
        connectButton.setText( stringsBundle.getString( "spx42ConnectPanel.connectButton.connectText" ) );
        connectButton.setActionCommand( "connect" );
      }
      connectBtRefreshButton.setText( stringsBundle.getString( "spx42ConnectPanel.connectBtRefreshButton.text" ) );
      connectBtRefreshButton.setToolTipText( stringsBundle.getString( "spx42ConnectPanel.connectBtRefreshButton.tooltiptext" ) );
      // Abhängig von der Sichtbarkeit der Aliaseditfläche
      if( aliasScrollPane.isVisible() )
      {
        deviceAliasButton.setText( stringsBundle.getString( "spx42ConnectPanel.deviceAliasButton.noedit.text" ) );
      }
      else
      {
        deviceAliasButton.setText( stringsBundle.getString( "spx42ConnectPanel.deviceAliasButton.edit.text" ) );
      }
      deviceAliasButton.setToolTipText( stringsBundle.getString( "spx42ConnectPanel.deviceAliasButton.tooltiptext" ) );
      //
      //
      columnNames.clear();
      columnNames.add( stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn00.text" ) );
      columnNames.add( stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn01.text" ) );
      LOGGER.log( Level.FINE, "fill aliases in stringarray..." );
      aliasData = databaseUtil.getAliasDataConn();
      if( aliasData != null )
      {
        AliasEditTableModel alMod = new AliasEditTableModel( aliasData, columnNames );
        alMod.addTableModelListener( this );
        aliasEditTable.setModel( alMod );
      }
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
    LOGGER.log( Level.FINE, String.format( "changedd row %d", row ) );
    // devicename erfragen
    devName = ( String )aliasEditTable.getModel().getValueAt( row, 0 );
    // AliasName erfrage
    devAlias = ( String )aliasEditTable.getModel().getValueAt( row, 1 );
    databaseUtil.updateDeviceAliasConn( devName, devAlias );
    // Jetzt die Verbindungsbox neu einlesen, sonst gibte Chaos ;-)
    LOGGER.log( Level.FINE, "read combobox entrys again...." );
    btComm.refreshNameArray();
    Vector<String[]> entrys = btComm.getNameArray();
    DeviceComboBoxModel portBoxModel = new DeviceComboBoxModel( entrys );
    deviceToConnectComboBox.setModel( portBoxModel );
  }

  /**
   * 
   * Fehlerdialog anzeigen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.10.2012
   * @param message
   * 
   */
  private void showErrorDialog( String message )
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Terminate.png" ) );
      JOptionPane.showMessageDialog( this, message, stringsBundle.getString( "MainCommGUI.errorDialog.headline" ), JOptionPane.INFORMATION_MESSAGE, icon );
    }
    catch( NullPointerException ex )
    {
      LOGGER.severe( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      LOGGER.severe( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      LOGGER.severe( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }
}
