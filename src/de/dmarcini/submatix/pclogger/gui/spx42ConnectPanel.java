package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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

import de.dmarcini.submatix.pclogger.utils.AliasTableModel;

public class spx42ConnectPanel extends JPanel
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected Logger          LOGGER           = null;
  JComboBox                 deviceToConnectComboBox;
  JButton                   connectButton;
  JButton                   connectBtRefreshButton;
  JProgressBar              discoverProgressBar;
  JButton                   pinButton;
  JLabel                    ackuLabel;
  JButton                   deviceAliasButton;
  private JTable            AliasEditTable;
  private JScrollPane       aliasScrollPane;

  @SuppressWarnings( "unused" )
  private spx42ConnectPanel()
  {
    initPanel();
  }

  /**
   * Create the panel.
   * 
   * @param LOGGER
   * 
   */
  public spx42ConnectPanel( Logger LOGGER )
  {
    this.LOGGER = LOGGER;
    initPanel();
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
  @SuppressWarnings( "serial" )
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
      String[][] strField = new String[5][2];
      strField[0][0] = "A0";
      strField[0][1] = "D0";
      strField[1][0] = "A1";
      strField[1][1] = "D1";
      strField[2][0] = "A2";
      strField[2][1] = "D2";
      strField[3][0] = "A3";
      strField[3][1] = "D3";
      strField[4][0] = "A4";
      strField[4][1] = "D4";
      AliasTableModel alMod = new AliasTableModel( strField );
      String[] cNames = new String[2];
      cNames[0] = stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn00.text" );
      cNames[1] = stringsBundle.getString( "spx42ConnectPanel.aliasTableColumn01.text" );
      alMod.setCoumnNames( cNames );
      AliasEditTable.setModel( alMod );
      AliasEditTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 153 );
      AliasEditTable.getColumnModel().getColumn( 1 ).setPreferredWidth( 186 );
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
}
