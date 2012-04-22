package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;

public class spx42ConnectPanel extends JPanel
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  JComboBox                 deviceToConnectComboBox;
  JButton                   connectButton;
  JButton                   connectBtRefreshButton;
  JProgressBar              discoverProgressBar;
  JButton                   pinButton;
  JLabel                    ackuLabel;

  @SuppressWarnings( "unused" )
  private spx42ConnectPanel()
  {
    initPanel();
  }

  /**
   * Create the panel.
   * 
   * @param lOGGER
   */
  public spx42ConnectPanel( Logger lOGGER )
  {
    initPanel();
  }

  private void initPanel()
  {
    deviceToConnectComboBox = new JComboBox();
    deviceToConnectComboBox.setPreferredSize( new Dimension( 220, 40 ) );
    deviceToConnectComboBox.setMinimumSize( new Dimension( 180, 20 ) );
    deviceToConnectComboBox.setMaximumSize( new Dimension( 500, 40 ) );
    connectButton = new JButton( "CONNECT" );
    connectButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/112-mono.png" ) ) );
    connectButton.setActionCommand( "connect" );
    connectButton.setPreferredSize( new Dimension( 180, 40 ) );
    connectButton.setMaximumSize( new Dimension( 160, 40 ) );
    connectButton.setSize( new Dimension( 160, 40 ) );
    connectButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    connectBtRefreshButton = new JButton( "REFRESH" );
    connectBtRefreshButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Refresh.png" ) ) );
    connectBtRefreshButton.setActionCommand( "refresh_bt_devices" );
    discoverProgressBar = new JProgressBar();
    discoverProgressBar.setBorder( null );
    discoverProgressBar.setBackground( new Color( 240, 248, 255 ) );
    discoverProgressBar.setForeground( new Color( 176, 224, 230 ) );
    pinButton = new JButton( "PINBUTTON" );
    pinButton.setIcon( new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Unlock.png" ) ) );
    pinButton.setActionCommand( "set_pin_for_dev" );
    ackuLabel = new JLabel( " " );
    GroupLayout gl_connectionPanel = new GroupLayout( this );
    gl_connectionPanel.setHorizontalGroup( gl_connectionPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_connectionPanel
                    .createSequentialGroup()
                    .addGroup(
                            gl_connectionPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_connectionPanel
                                                    .createSequentialGroup()
                                                    .addGap( 45 )
                                                    .addGroup(
                                                            gl_connectionPanel.createParallelGroup( Alignment.LEADING, false )
                                                                    .addComponent( ackuLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                    .addComponent( deviceToConnectComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                                                    .addGap( 70 )
                                                    .addGroup(
                                                            gl_connectionPanel
                                                                    .createParallelGroup( Alignment.LEADING, false )
                                                                    .addComponent( connectBtRefreshButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                    .addGroup(
                                                                            gl_connectionPanel.createSequentialGroup()
                                                                                    .addComponent( connectButton, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE )
                                                                                    .addPreferredGap( ComponentPlacement.RELATED ).addComponent( pinButton ) ) ) )
                                    .addGroup(
                                            gl_connectionPanel.createSequentialGroup().addContainerGap()
                                                    .addComponent( discoverProgressBar, GroupLayout.PREFERRED_SIZE, 763, GroupLayout.PREFERRED_SIZE ) ) )
                    .addContainerGap( 52, Short.MAX_VALUE ) ) );
    gl_connectionPanel.setVerticalGroup( gl_connectionPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_connectionPanel
                    .createSequentialGroup()
                    .addGap( 22 )
                    .addGroup(
                            gl_connectionPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_connectionPanel
                                                    .createSequentialGroup()
                                                    .addGroup(
                                                            gl_connectionPanel.createParallelGroup( Alignment.LEADING, false )
                                                                    .addComponent( pinButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                    .addComponent( connectButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                                                    .addGap( 18 ).addComponent( connectBtRefreshButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE ) )
                                    .addGroup(
                                            gl_connectionPanel.createSequentialGroup()
                                                    .addComponent( deviceToConnectComboBox, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( ackuLabel ) ) )
                    .addPreferredGap( ComponentPlacement.RELATED, 356, Short.MAX_VALUE )
                    .addComponent( discoverProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ).addContainerGap() ) );
    setLayout( gl_connectionPanel );
  }

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
  }

  public int setLanguageStrings( ResourceBundle stringsBundle, boolean connected )
  {
    String[] entrys;
    try
    {
      deviceToConnectComboBox.setToolTipText( stringsBundle.getString( "MainCommGUI.portComboBox.tooltiptext" ) );
      connectButton.setToolTipText( stringsBundle.getString( "MainCommGUI.connectButton.tooltiptext" ) );
      pinButton.setToolTipText( stringsBundle.getString( "MainCommGUI.pinButton.tooltiptext" ) );
      pinButton.setText( stringsBundle.getString( "MainCommGUI.pinButton.text" ) );
      if( connected )
      {
        connectButton.setText( stringsBundle.getString( "MainCommGUI.connectButton.disconnectText" ) );
        connectButton.setActionCommand( "disconnect" );
      }
      else
      {
        connectButton.setText( stringsBundle.getString( "MainCommGUI.connectButton.connectText" ) );
        connectButton.setActionCommand( "connect" );
      }
      connectBtRefreshButton.setText( stringsBundle.getString( "MainCommGUI.connectBtRefreshButton.text" ) );
      connectBtRefreshButton.setToolTipText( stringsBundle.getString( "MainCommGUI.connectBtRefreshButton.tooltiptext" ) );
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
