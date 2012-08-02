package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

public class SelectGraphDetailsDialog extends JDialog implements ActionListener
{
  /**
   * 
   */
  private static final long        serialVersionUID = 1880409081700634690L;
  private boolean                  closeWithOk      = false;
  private ResourceBundle           stringsBundle    = null;
  private SpxPcloggerProgramConfig progConfig       = null;
  private JButton                  cancelButton;
  private JButton                  okButton;
  private JCheckBox                depthCheckBox;
  private JCheckBox                ppo2ResultCheckBox;
  private JCheckBox                temperatureCheckBox;
  private JCheckBox                ppo2_01CheckBox;
  private JCheckBox                ppo2_02CheckBox;
  private JCheckBox                ppo2_03CheckBox;
  private JCheckBox                ppo2SetpointCheckBox;
  private JCheckBox                hePercentCheckBox;
  private JCheckBox                n2PercentCheckBox;
  private JCheckBox                nullTimeCheckBox;

  @SuppressWarnings( "unused" )
  private SelectGraphDetailsDialog()
  {
    initGui();
  }

  /**
   * 
   * Der Konstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   * @param stringsBundle
   * @param progConfig
   */
  public SelectGraphDetailsDialog( ResourceBundle stringsBundle, SpxPcloggerProgramConfig progConfig )
  {
    this.stringsBundle = stringsBundle;
    this.progConfig = progConfig;
    initGui();
  }

  /**
   * 
   * Initiiere die Anzeige
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   */
  private void initGui()
  {
    try
    {
      setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
      setIconImage( Toolkit.getDefaultToolkit().getImage( SelectGraphDetailsDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/search.png" ) ) );
      setTitle( stringsBundle.getString( "SelectGraphDetailsDialog.title.text" ) );
      setBounds( 100, 100, 309, 357 );
      getContentPane().setLayout( new BorderLayout( 0, 0 ) );
      JPanel buttonPane = new JPanel();
      getContentPane().add( buttonPane, BorderLayout.SOUTH );
      cancelButton = new JButton( stringsBundle.getString( "SelectGraphDetailsDialog.cancelButton.text" ) );
      cancelButton.setForeground( Color.RED );
      cancelButton.setBackground( new Color( 255, 192, 203 ) );
      cancelButton.setActionCommand( "cancel" );
      cancelButton.addActionListener( this );
      cancelButton.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.cancelButton.tooltiptext" ) );
      okButton = new JButton( stringsBundle.getString( "SelectGraphDetailsDialog.okButton.text" ) );
      okButton.setSize( new Dimension( 199, 60 ) );
      okButton.setPreferredSize( new Dimension( 180, 40 ) );
      okButton.setMaximumSize( new Dimension( 160, 40 ) );
      okButton.setMargin( new Insets( 2, 30, 2, 30 ) );
      okButton.setForeground( new Color( 0, 100, 0 ) );
      okButton.setBackground( new Color( 152, 251, 152 ) );
      okButton.setActionCommand( "commit" );
      okButton.addActionListener( this );
      okButton.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.cancelButton.tooltiptext" ) );
      GroupLayout gl_buttonPane = new GroupLayout( buttonPane );
      gl_buttonPane.setHorizontalGroup( gl_buttonPane.createParallelGroup( Alignment.TRAILING ).addGroup(
              Alignment.LEADING,
              gl_buttonPane.createSequentialGroup().addGap( 18 ).addComponent( cancelButton, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE )
                      .addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( okButton, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE )
                      .addContainerGap( 16, Short.MAX_VALUE ) ) );
      gl_buttonPane.setVerticalGroup( gl_buttonPane.createParallelGroup( Alignment.TRAILING ).addGroup(
              gl_buttonPane
                      .createSequentialGroup()
                      .addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                      .addGroup(
                              gl_buttonPane.createParallelGroup( Alignment.BASELINE ).addComponent( cancelButton, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE )
                                      .addComponent( okButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE ) ).addContainerGap() ) );
      buttonPane.setLayout( gl_buttonPane );
      JPanel contentPanel = new JPanel();
      getContentPane().add( contentPanel, BorderLayout.CENTER );
      depthCheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.depthCheckBox.text" ) );
      depthCheckBox.setEnabled( false );
      depthCheckBox.setHorizontalAlignment( SwingConstants.LEFT );
      depthCheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.depthCheckBox.tooltiptext" ) );
      depthCheckBox.setSelected( true );
      temperatureCheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.temperatureCheckBox.text" ) );
      temperatureCheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.temperatureCheckBox.tooltiptext" ) );
      temperatureCheckBox.setSelected( progConfig.isShowTemperature() );
      ppo2ResultCheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2ResultCheckBox.text" ) );
      ppo2ResultCheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2ResultCheckBox.tooltiptext" ) );
      ppo2ResultCheckBox.setSelected( progConfig.isShowPpoResult() );
      ppo2_01CheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2_01CheckBox.text" ) );
      ppo2_01CheckBox.setForeground( Color.DARK_GRAY );
      ppo2_01CheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2_01CheckBox.tooltiptext" ) );
      ppo2_01CheckBox.setSelected( progConfig.isShowPpo01() );
      ppo2_02CheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2_02CheckBox.text" ) );
      ppo2_02CheckBox.setForeground( Color.DARK_GRAY );
      ppo2_02CheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2_02CheckBox.tooltiptext" ) );
      ppo2_02CheckBox.setSelected( progConfig.isShowPpo02() );
      ppo2_03CheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2_03CheckBox.text" ) );
      ppo2_03CheckBox.setForeground( Color.DARK_GRAY );
      ppo2_03CheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2_03CheckBox.tooltiptext" ) );
      ppo2_03CheckBox.setSelected( progConfig.isShowPpo03() );
      ppo2SetpointCheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2SetpointCheckBox.text" ) );
      ppo2SetpointCheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.ppo2SetpointCheckBox.tooltiptext" ) );
      ppo2SetpointCheckBox.setSelected( progConfig.isShowSetpoint() );
      hePercentCheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.hePercentCheckBox.text" ) );
      hePercentCheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.hePercentCheckBox.tooltiptext" ) );
      hePercentCheckBox.setSelected( progConfig.isShowHe() );
      n2PercentCheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.n2PercentCheckBox.text" ) );
      n2PercentCheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.n2PercentCheckBox.tooltiptext" ) );
      n2PercentCheckBox.setSelected( progConfig.isShowN2() );
      nullTimeCheckBox = new JCheckBox( stringsBundle.getString( "SelectGraphDetailsDialog.nullTimeCheckBox.text" ) );
      nullTimeCheckBox.setToolTipText( stringsBundle.getString( "SelectGraphDetailsDialog.nullTimeCheckBox.tooltiptext" ) );
      nullTimeCheckBox.setSelected( progConfig.isShowNulltime() );
      GroupLayout gl_contentPanel = new GroupLayout( contentPanel );
      gl_contentPanel.setHorizontalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING ).addGroup(
              gl_contentPanel
                      .createSequentialGroup()
                      .addContainerGap()
                      .addGroup(
                              gl_contentPanel
                                      .createParallelGroup( Alignment.TRAILING, false )
                                      .addComponent( nullTimeCheckBox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                      .addComponent( ppo2SetpointCheckBox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                      .addComponent( ppo2ResultCheckBox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                      .addComponent( temperatureCheckBox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                      .addComponent( depthCheckBox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE )
                                      .addGroup(
                                              Alignment.LEADING,
                                              gl_contentPanel
                                                      .createSequentialGroup()
                                                      .addGap( 21 )
                                                      .addGroup(
                                                              gl_contentPanel.createParallelGroup( Alignment.LEADING )
                                                                      .addComponent( ppo2_02CheckBox, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE )
                                                                      .addComponent( ppo2_01CheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                      .addComponent( ppo2_03CheckBox, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE ) ) )
                                      .addGroup(
                                              Alignment.LEADING,
                                              gl_contentPanel
                                                      .createSequentialGroup()
                                                      .addGap( 21 )
                                                      .addGroup(
                                                              gl_contentPanel.createParallelGroup( Alignment.LEADING )
                                                                      .addComponent( n2PercentCheckBox, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE )
                                                                      .addComponent( hePercentCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) ) )
                      .addContainerGap( 175, Short.MAX_VALUE ) ) );
      gl_contentPanel.setVerticalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING ).addGroup(
              gl_contentPanel.createSequentialGroup().addContainerGap().addComponent( depthCheckBox ).addPreferredGap( ComponentPlacement.UNRELATED )
                      .addComponent( temperatureCheckBox ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( ppo2ResultCheckBox )
                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( ppo2_01CheckBox ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( ppo2_02CheckBox )
                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( ppo2_03CheckBox ).addPreferredGap( ComponentPlacement.RELATED )
                      .addComponent( ppo2SetpointCheckBox ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( hePercentCheckBox )
                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( n2PercentCheckBox ).addPreferredGap( ComponentPlacement.RELATED )
                      .addComponent( nullTimeCheckBox ).addContainerGap( 150, Short.MAX_VALUE ) ) );
      contentPanel.setLayout( gl_contentPanel );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * 
   * Zeige das Fenster MODAL an
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   * @return Mit Ok geschlossen?
   */
  public boolean showModal()
  {
    setModalityType( ModalityType.APPLICATION_MODAL );
    setModalExclusionType( ModalExclusionType.APPLICATION_EXCLUDE );
    setModal( true );
    setAlwaysOnTop( true );
    setVisible( true );
    return( closeWithOk );
  }

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    if( ev.getSource() instanceof JButton )
    {
      String cmd = ev.getActionCommand();
      // /////////////////////////////////////////////////////////////////////////
      // Abbrechen
      if( cmd.equals( "cancel" ) )
      {
        setVisible( false );
        closeWithOk = false;
        return;
      }
      // /////////////////////////////////////////////////////////////////////////
      // Abbrechen
      if( cmd.equals( "commit" ) )
      {
        progConfig.setShowPpoResult( ppo2ResultCheckBox.isSelected() );
        progConfig.setShowTemperature( temperatureCheckBox.isSelected() );
        progConfig.setShowPpo01( ppo2_01CheckBox.isSelected() );
        progConfig.setShowPpo02( ppo2_02CheckBox.isSelected() );
        progConfig.setShowPpo03( ppo2_03CheckBox.isSelected() );
        progConfig.setShowSetpoint( ppo2SetpointCheckBox.isSelected() );
        progConfig.setShowHe( hePercentCheckBox.isSelected() );
        progConfig.setShowN2( n2PercentCheckBox.isSelected() );
        progConfig.setShowNulltime( nullTimeCheckBox.isSelected() );
        setVisible( false );
        closeWithOk = true;
        return;
      }
      return;
    }
  }
}
