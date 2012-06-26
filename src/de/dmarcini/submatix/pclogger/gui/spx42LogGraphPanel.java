package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import de.dmarcini.submatix.pclogger.utils.ConnectDatabaseUtil;

public class spx42LogGraphPanel extends JPanel implements ActionListener
{
  /**
   * 
   */
  private static final long   serialVersionUID = 1L;
  protected Logger            LOGGER           = null;
  private ConnectDatabaseUtil dbUtil           = null;
  private ResourceBundle      stringsBundle    = null;
  private JPanel              topPanel;
  private JPanel              bottomPanel;
  private JPanel              graphPanel;
  private JComboBox           deviceComboBox;
  private JComboBox           diveSelectComboBox;
  private JButton             computeGraphButton;
  private JLabel              lblLabel;

  @SuppressWarnings( "unused" )
  private spx42LogGraphPanel()
  {
    initPanel();
  }

  /**
   * Create the panel.
   * 
   * @param LOGGER
   * @param _dbUtil
   * @param btComm
   */
  public spx42LogGraphPanel( Logger LOGGER, final ConnectDatabaseUtil _dbUtil )
  {
    this.LOGGER = LOGGER;
    LOGGER.log( Level.FINE, "constructor..." );
    this.dbUtil = _dbUtil;
    initPanel();
  }

  /**
   * Initialisiere das Panel für die Verbindungen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012 TODO
   */
  private void initPanel()
  {
    setLayout( new BorderLayout( 0, 0 ) );
    topPanel = new JPanel();
    add( topPanel, BorderLayout.NORTH );
    deviceComboBox = new JComboBox();
    diveSelectComboBox = new JComboBox();
    computeGraphButton = new JButton( "GRAPHBUTTON" );
    computeGraphButton.setActionCommand( "show_log_graph" );
    GroupLayout gl_topPanel = new GroupLayout( topPanel );
    gl_topPanel.setHorizontalGroup( gl_topPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_topPanel.createSequentialGroup().addContainerGap().addComponent( deviceComboBox, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE )
                    .addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( diveSelectComboBox, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE )
                    .addPreferredGap( ComponentPlacement.RELATED ).addComponent( computeGraphButton ).addContainerGap( 305, Short.MAX_VALUE ) ) );
    gl_topPanel.setVerticalGroup( gl_topPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_topPanel
                    .createSequentialGroup()
                    .addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                    .addGroup(
                            gl_topPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( deviceComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( diveSelectComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( computeGraphButton ) ) ) );
    topPanel.setLayout( gl_topPanel );
    bottomPanel = new JPanel();
    add( bottomPanel, BorderLayout.SOUTH );
    lblLabel = new JLabel( "LABEL" );
    bottomPanel.add( lblLabel );
    graphPanel = new JPanel();
    add( graphPanel, BorderLayout.CENTER );
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
    deviceComboBox.addMouseMotionListener( mainCommGUI );
    computeGraphButton.addMouseMotionListener( mainCommGUI );
    // die Aktionen mach ich im objekt
    deviceComboBox.addActionListener( this );
    computeGraphButton.addActionListener( this );
  }

  /**
   * Setze alle Strings in die entsprechende Landessprache! Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   * @param stringsBundle
   *          REsource für die Strings
   * @param connected
   *          ISt der SPX verbbunden?
   * @return in Ordnung oder nicht
   */
  public int setLanguageStrings( ResourceBundle stringsBundle )
  {
    this.stringsBundle = stringsBundle;
    try
    {
      deviceComboBox.setToolTipText( stringsBundle.getString( "spx42LogGraphPanel.deviceComboBox.tooltiptext" ) );
      diveSelectComboBox.setToolTipText( stringsBundle.getString( "spx42LogGraphPanel.diveSelectComboBox.tooltiptext" ) );
      computeGraphButton.setText( stringsBundle.getString( "spx42LogGraphPanel.computeGraphButton.text" ) );
      computeGraphButton.setToolTipText( stringsBundle.getString( "spx42LogGraphPanel.computeGraphButton.tooltiptext" ) );
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
  public void actionPerformed( ActionEvent ev )
  {
    // /////////////////////////////////////////////////////////////////////////
    // Button
    if( ev.getSource() instanceof JButton )
    {
      // processButtonActions( ev );
      return;
    }
    // /////////////////////////////////////////////////////////////////////////
    // Combobox
    else if( ev.getSource() instanceof JComboBox )
    {
      // processComboBoxActions( ev );
      return;
    }
  }
}
