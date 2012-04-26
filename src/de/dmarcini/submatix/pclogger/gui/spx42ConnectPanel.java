package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import de.dmarcini.submatix.pclogger.utils.AliasEditTableModel;
import de.dmarcini.submatix.pclogger.utils.DatabaseUtil;

public class spx42ConnectPanel extends JPanel implements TableModelListener
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected Logger          LOGGER           = null;
  public JComboBox          deviceToConnectComboBox;
  public JButton            connectButton;
  public JButton            connectBtRefreshButton;
  public JProgressBar       discoverProgressBar;
  public JButton            pinButton;
  public JLabel             ackuLabel;
  public JButton            deviceAliasButton;
  public JTable             AliasEditTable;
  private JScrollPane       aliasScrollPane;
  private String[]          columnNames      = null;
  private String[][]        aliasData        = null;
  private DatabaseUtil      dbUtil           = null;
  private ResourceBundle    stringsBundle    = null;

  @SuppressWarnings( "unused" )
  private spx42ConnectPanel()
  {
    initPanel();
  }

  /**
   * Create the panel.
   * 
   * @param LOGGER
   * @param _dbUtil
   * 
   */
  public spx42ConnectPanel( Logger LOGGER, final DatabaseUtil _dbUtil )
  {
    this.LOGGER = LOGGER;
    LOGGER.log( Level.FINE, "constructor..." );
    this.dbUtil = _dbUtil;
    dbUtil.closeDB();
    aliasData = null;
    columnNames = new String[2];
    columnNames[0] = "DEVICE";
    columnNames[1] = "ALIAS";
    initPanel();
    dbUtil.createConnection();
    aliasData = dbUtil.getAliasData();
    dbUtil.closeDB();
  }

  /**
   * 
   * Initialisiere das Panel für die Verbindungen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.04.2012 TODO
   */
  private void initPanel()
  {
    deviceToConnectComboBox = new JComboBox();
    deviceToConnectComboBox.setBounds( 10, 29, 315, 26 );
    deviceToConnectComboBox.setPreferredSize( new Dimension( 220, 40 ) );
    deviceToConnectComboBox.setMinimumSize( new Dimension( 180, 20 ) );
    deviceToConnectComboBox.setMaximumSize( new Dimension( 500, 40 ) );
    connectButton = new JButton( "CONNECT" );
    connectButton.setLocation( 335, 22 );
    connectButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/112-mono.png" ) ) );
    connectButton.setActionCommand( "connect" );
    connectButton.setPreferredSize( new Dimension( 180, 40 ) );
    connectButton.setMaximumSize( new Dimension( 160, 40 ) );
    connectButton.setSize( new Dimension( 180, 41 ) );
    connectButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    connectBtRefreshButton = new JButton( "REFRESH" );
    connectBtRefreshButton.setBounds( 335, 81, 311, 39 );
    connectBtRefreshButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Refresh.png" ) ) );
    connectBtRefreshButton.setActionCommand( "refresh_bt_devices" );
    discoverProgressBar = new JProgressBar();
    discoverProgressBar.setBounds( 10, 441, 763, 14 );
    discoverProgressBar.setBorder( null );
    discoverProgressBar.setBackground( new Color( 240, 248, 255 ) );
    discoverProgressBar.setForeground( new Color( 176, 224, 230 ) );
    pinButton = new JButton( "PINBUTTON" );
    pinButton.setBounds( 521, 22, 125, 41 );
    pinButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Unlock.png" ) ) );
    pinButton.setActionCommand( "set_pin_for_dev" );
    ackuLabel = new JLabel( " " );
    ackuLabel.setBounds( 45, 59, 220, 14 );
    deviceAliasButton = new JButton( "ALIAS" );
    deviceAliasButton.setBounds( 335, 131, 311, 39 );
    deviceAliasButton.setIcon( new ImageIcon( spx42ConnectPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/45.png" ) ) );
    deviceAliasButton.setActionCommand( "alias_bt_devices" );
    setLayout( null );
    add( ackuLabel );
    add( deviceToConnectComboBox );
    add( connectBtRefreshButton );
    add( connectButton );
    add( pinButton );
    add( deviceAliasButton );
    add( discoverProgressBar );
    aliasScrollPane = new JScrollPane();
    aliasScrollPane.setBounds( 124, 181, 522, 247 );
    add( aliasScrollPane );
    AliasEditTable = new JTable();
    AliasEditTable.setCellSelectionEnabled( true );
    AliasEditTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    aliasScrollPane.setViewportView( AliasEditTable );
  }

  /**
   * 
   * Setze die Listener auf das Hauptobjekt
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.04.2012
   * @param mainCommGUI
   *          das Hauptobjekt
   */
  public void setGlobalChangeListener( MainCommGUI mainCommGUI )
  {
    deviceToConnectComboBox.addActionListener( mainCommGUI );
    deviceToConnectComboBox.addMouseMotionListener( mainCommGUI );
    connectButton.addActionListener( mainCommGUI );
    connectButton.addMouseMotionListener( mainCommGUI );
    connectBtRefreshButton.addActionListener( mainCommGUI );
    connectBtRefreshButton.addMouseMotionListener( mainCommGUI );
    pinButton.addActionListener( mainCommGUI );
    pinButton.addMouseMotionListener( mainCommGUI );
    deviceAliasButton.addActionListener( mainCommGUI );
    deviceAliasButton.addMouseMotionListener( mainCommGUI );
  }

  /**
   * 
   * Sezte alle Strings in die entsprechende Landessprache!
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.04.2012
   * @param stringsBundle
   *          REsource für die Strings
   * @param connected
   *          ISt der SPX verbbunden?
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
      deviceAliasButton.setText( stringsBundle.getString( "spx42ConnectPanel.deviceAliasButton.text" ) );
      deviceAliasButton.setToolTipText( stringsBundle.getString( "spx42ConnectPanel.deviceAliasButton.tooltiptext" ) );
      //
      //
      columnNames[0] = stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn00.text" );
      columnNames[1] = stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn01.text" );
      LOGGER.log( Level.FINE, "fill aliases in stringarray..." );
      dbUtil.createConnection();
      aliasData = dbUtil.getAliasData();
      dbUtil.closeDB();
      if( aliasData != null )
      {
        AliasEditTableModel alMod = new AliasEditTableModel( aliasData, columnNames );
        alMod.addTableModelListener( this );
        AliasEditTable.setModel( alMod );
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

  /**
   * 
   * Bei wechsel des Verbindungszustandes muss einiges umgeräumt wereden
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 26.04.2012
   * @param active
   */
  public void setElementsConnected( boolean active )
  {
    deviceToConnectComboBox.setEnabled( !active );
    connectBtRefreshButton.setEnabled( !active );
    connectButton.setEnabled( true );
    pinButton.setEnabled( !active );
    if( active )
    {
      connectButton.setText( stringsBundle.getString( "MainCommGUI.connectButton.disconnectText" ) );
      connectButton.setActionCommand( "disconnect" );
      connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112.png" ) ) );
    }
    else
    {
      connectButton.setText( stringsBundle.getString( "MainCommGUI.connectButton.connectText" ) );
      connectButton.setActionCommand( "connect" );
      connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112-mono.png" ) ) );
    }
  }

  /**
   * 
   * Alias tabelle auffrischen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 26.04.2012 TODO
   */
  public void refreshAliasTable()
  {
    columnNames[0] = stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn00.text" );
    columnNames[1] = stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn01.text" );
    LOGGER.log( Level.FINE, "fill aliases in stringarray..." );
    dbUtil.createConnection();
    aliasData = dbUtil.getAliasData();
    dbUtil.closeDB();
    if( aliasData != null )
    {
      AliasEditTableModel alMod = new AliasEditTableModel( aliasData, columnNames );
      alMod.addTableModelListener( this );
      AliasEditTable.setModel( alMod );
    }
  }

  /**
   * 
   * Elemente bei Bedarf abschalten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 26.04.2012
   * @param active
   */
  public void setElementsInactive( boolean active )
  {
    deviceToConnectComboBox.setEnabled( !active );
    connectButton.setEnabled( !active );
    pinButton.setEnabled( !active );
    connectBtRefreshButton.setEnabled( active );
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
    devName = ( String )AliasEditTable.getModel().getValueAt( row, 0 );
    // AliasName erfrage
    devAlias = ( String )AliasEditTable.getModel().getValueAt( row, 1 );
    dbUtil.createConnection();
    dbUtil.updateDeviceAlias( devName, devAlias );
    dbUtil.closeDB();
    // TODO: Combobox ändern!!!!
  }
}
