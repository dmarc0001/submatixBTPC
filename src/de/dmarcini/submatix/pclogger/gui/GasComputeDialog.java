package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import de.dmarcini.submatix.pclogger.lang.LangStrings;
import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.GasComputeUnit;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

/**
 * Dialog zum berechnen von Gaasen
 * 
 * @author Dirk Marciniak 22.08.2013
 */
public class GasComputeDialog extends JDialog implements ActionListener, ChangeListener, ItemListener
{
  /**
   * 
   */
  private static final long serialVersionUID = 1744972231275930537L;
  private Logger            lg               = null;
  private boolean           ignoreAction     = false;
  private String            unitsString      = "metric";
  private double            ppOMax           = 1.6D;
  private double            ppOSetpoint      = 1.0D;
  private boolean           salnity          = false;
  //
  private JPanel            contentPanel;
  private JButton           buttonClose;
  private JSpinner          oxygenSpinner;
  private JSpinner          heliumSpinner;
  private JSpinner          nitrogenSpinner;
  private JComboBox         partialPressureComboBox;
  private JLabel            pressureUnitLabel;
  private JLabel            bailoutGasValsLabel;
  private JLabel            diluentGasValsLabel;
  private JCheckBox         salnityCheckBox;
  private JLabel            gasDescriptionLabel;
  private JLabel            advisedDepthLabel;
  private JSpinner          advisedDepthSpinner;
  private JLabel            advisedDepthUnitLabel;
  private JLabel            advisedDepthEadLabel;
  private JComboBox         setpointComboBox;
  private JLabel            pressureUnitLabel2;

  /**
   * Der Konstruktor Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.10.2012
   * @param stringsBundle
   * @param unitsString
   */
  public GasComputeDialog( String unitsString )
  {
    lg = SpxPcloggerProgramConfig.LOGGER;
    this.unitsString = unitsString;
    ignoreAction = true;
    initDialog();
    ignoreAction = false;
    oxygenSpinner.setValue( 21 );
    heliumSpinner.setValue( 0 );
    nitrogenSpinner.setValue( 79 );
    setDescriptionForGas( 21, 0 );
  }

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    if( ev.getSource() instanceof JButton )
    {
      String cmd = ev.getActionCommand();
      // /////////////////////////////////////////////////////////////////////////
      // Abbrechen
      if( cmd.equals( "close" ) )
      {
        lg.debug( "Close Dialog." );
        setVisible( false );
        return;
      }
    }
    else if( ev.getSource() instanceof JComboBox )
    {
      JComboBox cb = ( JComboBox )ev.getSource();
      if( ev.getActionCommand().equals( "set_ppomax" ) )
      {
        try
        {
          ppOMax = Double.parseDouble( ( String )( cb.getModel().getElementAt( cb.getSelectedIndex() ) ) );
          lg.debug( String.format( "ppoMax set to %2.2f", ppOMax ) );
          // Gas neu berechnen und zeigen
          setDescriptionForGas( ( Integer )oxygenSpinner.getValue(), ( Integer )heliumSpinner.getValue() );
        }
        catch( NumberFormatException ex )
        {
          lg.error( ex.getLocalizedMessage() );
          ppOMax = 1.6D;
        }
      }
      else if( ev.getActionCommand().equals( "set_setpoint" ) )
      {
        try
        {
          ppOSetpoint = Double.parseDouble( ( String )( cb.getModel().getElementAt( cb.getSelectedIndex() ) ) );
          lg.debug( String.format( "ppoSetpoint set to %2.2f", ppOSetpoint ) );
          // Gas neu berechnen und zeigen
          changeAdvisedDepthSpinner();
        }
        catch( NumberFormatException ex )
        {
          lg.error( ex.getLocalizedMessage() );
          ppOMax = 1.6D;
        }
      }
      else
      {
        lg.warn( "unknown combobox action event <" + ev.getActionCommand() + ">" );
      }
    }
    else
    {
      lg.debug( "unknown command <" + ev.getActionCommand() + ">" );
    }
  }

  /**
   * Wurde der Spinner für die Anvisierte Tiefe geändert, mach was Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.10.2012
   */
  private void changeAdvisedDepthSpinner()
  {
    // if( ignoreAction ) return;
    double ead;
    int o2 = ( Integer )oxygenSpinner.getValue();
    int he = ( Integer )heliumSpinner.getValue();
    int adDepth = ( Integer )advisedDepthSpinner.getValue();
    lg.debug( "advised depth changed to <" + adDepth + ">..." );
    ead = GasComputeUnit.getEADForDilMetric( o2, he, ppOSetpoint, new Double( adDepth ), salnity );
    if( unitsString.equals( "metric" ) )
    {
      lg.debug( "EAD on depth <" + adDepth + "> is <" + Math.round( ead ) + "> m" );
      advisedDepthEadLabel.setText( String.format( LangStrings.getString( "GasComputeDialog.fordepthcompute.eadString.metric" ), Math.round( ead ) ) );
    }
    else
    {
      lg.debug( "EAD on depth <" + adDepth + "> is <" + Math.round( ead ) + "> ft" );
      advisedDepthEadLabel.setText( String.format( LangStrings.getString( "GasComputeDialog.fordepthcompute.eadString.imperial" ), Math.round( ead ) ) );
    }
  }

  /**
   * Verändere Helium... Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.10.2012
   */
  private void changeHeSpinner()
  {
    int o2 = ( Integer )oxygenSpinner.getValue();
    int he = ( Integer )heliumSpinner.getValue();
    int n2 = ( Integer )nitrogenSpinner.getValue();
    //
    ignoreAction = true;
    //
    // Wenn Helium größer als 99%
    // muß ich eingreifen
    //
    if( he > 99 )
    {
      o2 = 1;
      he = 99;
      n2 = 0;
    }
    //
    // Stickstoff soll berechnet werden, Sauerstoff gleich bleiben
    //
    if( he + o2 <= 100 )
    {
      // ich muß den Stickstoff berechnen
      n2 = 100 - ( he + o2 );
      lg.debug( "compute nitrogen....<" + n2 + "> %" );
    }
    else
    {
      // das geht nicht, ich müßte je den Sauerstoff verringern!
      // also werde ich den Heliumanteil anpassen
      n2 = 0;
      he = 100 - o2;
      lg.warn( "helium to high! to expand heluim must reduce oxigen first!" );
    }
    //
    // jetzt zurückschreiben
    //
    oxygenSpinner.setValue( o2 );
    heliumSpinner.setValue( he );
    nitrogenSpinner.setValue( n2 );
    setDescriptionForGas( o2, he );
    changeAdvisedDepthSpinner();
    ignoreAction = false;
  }

  /**
   * Verändere den Sauerstoff Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.10.2012
   */
  private void changeO2Spinner()
  {
    int o2 = ( Integer )oxygenSpinner.getValue();
    int he = ( Integer )heliumSpinner.getValue();
    int n2 = ( Integer )nitrogenSpinner.getValue();
    //
    ignoreAction = true;
    //
    // Stickstoff soll berechnet werden, Helium gleich bleiben
    //
    if( he + o2 <= 100 )
    {
      // ich muß den Stickstoff berechnen
      n2 = 100 - ( he + o2 );
      lg.debug( "compute nitrogen....<" + n2 + "> %" );
    }
    else
    {
      // das geht nicht, ich müßte je den Sauerstoff verringern!
      // also werde ich den Heliumanteil anpassen
      n2 = 0;
      he = 100 - o2;
      lg.warn( "oxygen to high! to expand oxygen must reduce helium first!" );
    }
    //
    // jetzt zurückschreiben
    //
    oxygenSpinner.setValue( o2 );
    heliumSpinner.setValue( he );
    nitrogenSpinner.setValue( n2 );
    setDescriptionForGas( o2, he );
    changeAdvisedDepthSpinner();
    ignoreAction = false;
  }

  /**
   * Initialisiere den Dialog Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.10.2012
   * @param stringsBundle
   */
  private void initDialog()
  {
    setResizable( false );
    setTitle( LangStrings.getString( "GasComputeDialog.title" ) );
    setIconImage( Toolkit.getDefaultToolkit().getImage( GasComputeDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/calcColor.png" ) ) );
    contentPanel = new JPanel();
    setBounds( 100, 100, 545, 378 );
    getContentPane().setLayout( new BorderLayout() );
    contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    getContentPane().add( contentPanel, BorderLayout.CENTER );
    JPanel panel = new JPanel();
    panel.setBorder( new TitledBorder( null, LangStrings.getString( "GasComputeDialog.gasdef.title" ), TitledBorder.CENTER, TitledBorder.TOP, null, new Color( 0, 0, 128 ) ) );
    JPanel panel_1 = new JPanel();
    panel_1.setBorder( new TitledBorder( UIManager.getBorder( "TitledBorder.border" ), LangStrings.getString( "GasComputeDialog.computed.title" ), TitledBorder.CENTER,
            TitledBorder.TOP, null, new Color( 0, 0, 128 ) ) );
    JPanel panel_2 = new JPanel();
    panel_2.setBorder( new TitledBorder( UIManager.getBorder( "TitledBorder.border" ), LangStrings.getString( "GasComputeDialog.fordepthcompute.title" ), TitledBorder.CENTER,
            TitledBorder.TOP, null, new Color( 0, 100, 0 ) ) );
    GroupLayout gl_contentPanel = new GroupLayout( contentPanel );
    gl_contentPanel.setHorizontalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_contentPanel
                    .createSequentialGroup()
                    .addGroup(
                            gl_contentPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_contentPanel.createSequentialGroup().addComponent( panel, GroupLayout.PREFERRED_SIZE, 252, GroupLayout.PREFERRED_SIZE ).addGap( 18 )
                                                    .addComponent( panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                                    .addComponent( panel_2, GroupLayout.PREFERRED_SIZE, 526, GroupLayout.PREFERRED_SIZE ) )
                    .addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) );
    gl_contentPanel.setVerticalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_contentPanel
                    .createSequentialGroup()
                    .addGroup(
                            gl_contentPanel.createParallelGroup( Alignment.BASELINE ).addComponent( panel, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                    .addPreferredGap( ComponentPlacement.RELATED ).addComponent( panel_2, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE )
                    .addContainerGap( 70, Short.MAX_VALUE ) ) );
    advisedDepthSpinner = new JSpinner();
    advisedDepthLabel = new JLabel( LangStrings.getString( "GasComputeDialog.fordepthcompute.advisedDepth" ) );
    advisedDepthLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    advisedDepthUnitLabel = new JLabel( "-" );
    advisedDepthEadLabel = new JLabel( "-" );
    advisedDepthEadLabel.setForeground( new Color( 0, 0, 139 ) );
    advisedDepthEadLabel.setHorizontalAlignment( SwingConstants.CENTER );
    setpointComboBox = new JComboBox();
    setpointComboBox.setToolTipText( ( String )null );
    setpointComboBox.setFont( new Font( "Segoe UI", Font.PLAIN, 11 ) );
    setpointComboBox.setActionCommand( "set_setpoint" );
    pressureUnitLabel2 = new JLabel( "BAR" );
    GroupLayout gl_panel_2 = new GroupLayout( panel_2 );
    gl_panel_2.setHorizontalGroup( gl_panel_2.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_panel_2
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_panel_2
                                    .createParallelGroup( Alignment.LEADING )
                                    .addComponent( advisedDepthEadLabel, GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE )
                                    .addGroup(
                                            gl_panel_2.createSequentialGroup().addComponent( advisedDepthLabel, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE )
                                                    .addGap( 18 ).addComponent( advisedDepthSpinner, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.RELATED )
                                                    .addComponent( advisedDepthUnitLabel, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.RELATED )
                                                    .addComponent( setpointComboBox, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE ).addGap( 12 )
                                                    .addComponent( pressureUnitLabel2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE ) ) ).addContainerGap() ) );
    gl_panel_2.setVerticalGroup( gl_panel_2.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_panel_2
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_panel_2
                                    .createParallelGroup( Alignment.TRAILING )
                                    .addGroup(
                                            gl_panel_2.createParallelGroup( Alignment.BASELINE ).addComponent( advisedDepthLabel )
                                                    .addComponent( advisedDepthSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                                    .addComponent( advisedDepthUnitLabel ) )
                                    .addGroup(
                                            gl_panel_2
                                                    .createParallelGroup( Alignment.LEADING )
                                                    .addComponent( setpointComboBox, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE )
                                                    .addGroup(
                                                            gl_panel_2.createSequentialGroup().addGap( 2 )
                                                                    .addComponent( pressureUnitLabel2, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE ) ) ) )
                    .addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( advisedDepthEadLabel ).addContainerGap( 14, Short.MAX_VALUE ) ) );
    panel_2.setLayout( gl_panel_2 );
    JLabel label_6 = new JLabel( "Bailout:" );
    bailoutGasValsLabel = new JLabel( "GASVALUES" );
    partialPressureComboBox = new JComboBox();
    partialPressureComboBox.setFont( new Font( "Segoe UI", Font.PLAIN, 11 ) );
    partialPressureComboBox.setActionCommand( "set_ppomax" );
    partialPressureComboBox.setToolTipText( LangStrings.getString( "spx42GaslistEditPanel.gasPanel.customPresetComboBox.tooltiptext" ) );
    pressureUnitLabel = new JLabel( "BAR" );
    JLabel lblDiluent = new JLabel( "Diluent EAD by MOD:" );
    diluentGasValsLabel = new JLabel( "GASVALUES" );
    salnityCheckBox = new JCheckBox( LangStrings.getString( "spx42GaslistEditPanel.salnityCheckBox.text" ) );
    salnityCheckBox.setFont( new Font( "Segoe UI", Font.PLAIN, 11 ) );
    salnityCheckBox.setActionCommand( "check_salnity" );
    salnityCheckBox.setToolTipText( LangStrings.getString( "spx42GaslistEditPanel.salnityCheckBox.tooltiptext" ) );
    GroupLayout gl_panel_1 = new GroupLayout( panel_1 );
    gl_panel_1.setHorizontalGroup( gl_panel_1.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_panel_1
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_panel_1
                                    .createParallelGroup( Alignment.LEADING )
                                    .addComponent( salnityCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                    .addGroup(
                                            gl_panel_1.createSequentialGroup().addComponent( partialPressureComboBox, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE )
                                                    .addGap( 12 ).addComponent( pressureUnitLabel, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE ) )
                                    .addComponent( label_6, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( bailoutGasValsLabel, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( lblDiluent, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( diluentGasValsLabel, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE ) ).addContainerGap() ) );
    gl_panel_1.setVerticalGroup( gl_panel_1.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_panel_1
                    .createSequentialGroup()
                    .addGroup(
                            gl_panel_1.createParallelGroup( Alignment.BASELINE ).addComponent( pressureUnitLabel )
                                    .addComponent( partialPressureComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) )
                    .addPreferredGap( ComponentPlacement.RELATED ).addComponent( salnityCheckBox ).addPreferredGap( ComponentPlacement.RELATED )
                    .addComponent( label_6, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE ).addPreferredGap( ComponentPlacement.RELATED )
                    .addComponent( bailoutGasValsLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE ).addPreferredGap( ComponentPlacement.RELATED )
                    .addComponent( lblDiluent, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE ).addPreferredGap( ComponentPlacement.RELATED )
                    .addComponent( diluentGasValsLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE ).addContainerGap( 15, Short.MAX_VALUE ) ) );
    panel_1.setLayout( gl_panel_1 );
    JLabel label = new JLabel( LangStrings.getString( "GasComputeDialog.gasdef.oxygen" ) );
    label.setHorizontalAlignment( SwingConstants.RIGHT );
    JLabel label_1 = new JLabel( LangStrings.getString( "GasComputeDialog.gasdef.helium" ) );
    label_1.setHorizontalAlignment( SwingConstants.RIGHT );
    JLabel label_2 = new JLabel( LangStrings.getString( "GasComputeDialog.gasdef.nitrogen" ) );
    label_2.setHorizontalAlignment( SwingConstants.RIGHT );
    oxygenSpinner = new JSpinner();
    oxygenSpinner.setModel( new SpinnerNumberModel( 21, 1, 100, 1 ) );
    heliumSpinner = new JSpinner();
    heliumSpinner.setModel( new SpinnerNumberModel( 0, 0, 99, 1 ) );
    nitrogenSpinner = new JSpinner();
    nitrogenSpinner.setEnabled( false );
    nitrogenSpinner.setModel( new SpinnerNumberModel( 0, 0, 99, 1 ) );
    JLabel label_3 = new JLabel( "%" );
    JLabel label_4 = new JLabel( "%" );
    JLabel label_5 = new JLabel( "%" );
    GroupLayout gl_panel = new GroupLayout( panel );
    gl_panel.setHorizontalGroup( gl_panel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_panel.createParallelGroup( Alignment.LEADING ).addComponent( label, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( label_1, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( label_2, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE ) )
                    .addGap( 33 )
                    .addGroup(
                            gl_panel.createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_panel.createSequentialGroup().addPreferredGap( ComponentPlacement.RELATED )
                                                    .addComponent( nitrogenSpinner, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.RELATED )
                                                    .addComponent( label_5, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE ) )
                                    .addGroup(
                                            gl_panel.createSequentialGroup().addComponent( heliumSpinner, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.RELATED )
                                                    .addComponent( label_4, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE ) )
                                    .addGroup(
                                            gl_panel.createSequentialGroup().addComponent( oxygenSpinner, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.RELATED ).addComponent( label_3 ) ) ).addContainerGap( 13, Short.MAX_VALUE ) ) );
    gl_panel.setVerticalGroup( gl_panel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_panel.createParallelGroup( Alignment.BASELINE ).addComponent( label )
                                    .addComponent( oxygenSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ).addComponent( label_3 ) )
                    .addGap( 18 )
                    .addGroup(
                            gl_panel.createParallelGroup( Alignment.BASELINE ).addComponent( label_1 )
                                    .addComponent( heliumSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( label_4, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE ) )
                    .addGroup(
                            gl_panel.createParallelGroup( Alignment.LEADING )
                                    .addGroup( gl_panel.createSequentialGroup().addGap( 29 ).addComponent( label_5, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE ) )
                                    .addGroup(
                                            gl_panel.createSequentialGroup()
                                                    .addGap( 18 )
                                                    .addGroup(
                                                            gl_panel.createParallelGroup( Alignment.BASELINE )
                                                                    .addComponent( nitrogenSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                            GroupLayout.PREFERRED_SIZE ).addComponent( label_2 ) ) ) ).addContainerGap( 21, Short.MAX_VALUE ) ) );
    panel.setLayout( gl_panel );
    contentPanel.setLayout( gl_contentPanel );
    {
      JPanel buttonPane = new JPanel();
      getContentPane().add( buttonPane, BorderLayout.SOUTH );
      {
        buttonClose = new JButton( LangStrings.getString( "GasComputeDialog.buttonClose.text" ) );
        buttonClose.setRolloverIcon( new ImageIcon( GasComputeDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/quit.png" ) ) );
        buttonClose.setIcon( new ImageIcon( GasComputeDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/quitBw.png" ) ) );
        buttonClose.setForeground( new Color( 0, 0, 205 ) );
        buttonClose.setBackground( new Color( 192, 192, 192 ) );
        buttonClose.setActionCommand( "close" );
      }
      gasDescriptionLabel = new JLabel( "AIR" );
      gasDescriptionLabel.setHorizontalAlignment( SwingConstants.CENTER );
      gasDescriptionLabel.setFont( new Font( "Segoe UI Semibold", Font.BOLD, 14 ) );
      GroupLayout gl_buttonPane = new GroupLayout( buttonPane );
      gl_buttonPane.setHorizontalGroup( gl_buttonPane.createParallelGroup( Alignment.LEADING ).addGroup(
              gl_buttonPane.createSequentialGroup().addContainerGap().addComponent( gasDescriptionLabel, GroupLayout.PREFERRED_SIZE, 276, GroupLayout.PREFERRED_SIZE ).addGap( 116 )
                      .addComponent( buttonClose ).addContainerGap() ) );
      gl_buttonPane.setVerticalGroup( gl_buttonPane.createParallelGroup( Alignment.LEADING ).addGroup(
              gl_buttonPane
                      .createSequentialGroup()
                      .addGap( 5 )
                      .addGroup(
                              gl_buttonPane.createParallelGroup( Alignment.BASELINE )
                                      .addComponent( gasDescriptionLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE ).addComponent( buttonClose ) )
                      .addContainerGap() ) );
      buttonPane.setLayout( gl_buttonPane );
    }
    // der Spinner sollte noch ein Modell bekommen
    advisedDepthSpinner.setModel( new SpinnerNumberModel( 10, 1, 2500, 1 ) );
    //
    // jetz hab ich eine gewünschte Einheiteneinstellung für die Berechnungen
    //
    String[] pressureStrings = new String[7];
    if( unitsString.equals( "metric" ) )
    {
      pressureStrings[0] = LangStrings.getString( "spx42GaslistEditPanel.pressures.metric.0" );
      pressureStrings[1] = LangStrings.getString( "spx42GaslistEditPanel.pressures.metric.1" );
      pressureStrings[2] = LangStrings.getString( "spx42GaslistEditPanel.pressures.metric.2" );
      pressureStrings[3] = LangStrings.getString( "spx42GaslistEditPanel.pressures.metric.3" );
      pressureStrings[4] = LangStrings.getString( "spx42GaslistEditPanel.pressures.metric.4" );
      pressureStrings[5] = LangStrings.getString( "spx42GaslistEditPanel.pressures.metric.5" );
      pressureStrings[6] = LangStrings.getString( "spx42GaslistEditPanel.pressures.metric.6" );
      pressureUnitLabel.setText( LangStrings.getString( "spx42GaslistEditPanel.pressureUnitLabel.metric" ) );
      pressureUnitLabel2.setText( LangStrings.getString( "spx42GaslistEditPanel.pressureUnitLabel.metric" ) );
      advisedDepthUnitLabel.setText( LangStrings.getString( "spx42GaslistEditPanel.depthUnitLabel.metric" ) );
    }
    else
    {
      pressureStrings[0] = LangStrings.getString( "spx42GaslistEditPanel.pressures.imperial.0" );
      pressureStrings[1] = LangStrings.getString( "spx42GaslistEditPanel.pressures.imperial.1" );
      pressureStrings[2] = LangStrings.getString( "spx42GaslistEditPanel.pressures.imperial.2" );
      pressureStrings[3] = LangStrings.getString( "spx42GaslistEditPanel.pressures.imperial.3" );
      pressureStrings[4] = LangStrings.getString( "spx42GaslistEditPanel.pressures.imperial.4" );
      pressureStrings[5] = LangStrings.getString( "spx42GaslistEditPanel.pressures.imperial.5" );
      pressureStrings[6] = LangStrings.getString( "spx42GaslistEditPanel.pressures.imperial.6" );
      pressureUnitLabel.setText( LangStrings.getString( "spx42GaslistEditPanel.pressureUnitLabel.imperial" ) );
      pressureUnitLabel2.setText( LangStrings.getString( "spx42GaslistEditPanel.pressureUnitLabel.imperial" ) );
      advisedDepthUnitLabel.setText( LangStrings.getString( "spx42GaslistEditPanel.depthUnitLabel.imperial" ) );
    }
    partialPressureComboBox.setModel( new DefaultComboBoxModel( pressureStrings ) );
    partialPressureComboBox.setSelectedIndex( pressureStrings.length - 1 );
    setpointComboBox.setModel( new DefaultComboBoxModel( pressureStrings ) );
    setpointComboBox.setSelectedIndex( pressureStrings.length - 1 );
    //
    partialPressureComboBox.addActionListener( this );
    setpointComboBox.addActionListener( this );
    advisedDepthSpinner.addChangeListener( this );
    salnityCheckBox.addItemListener( this );
    oxygenSpinner.addChangeListener( this );
    heliumSpinner.addChangeListener( this );
    nitrogenSpinner.addChangeListener( this );
    buttonClose.addActionListener( this );
  }

  @Override
  public void itemStateChanged( ItemEvent ev )
  {
    if( ignoreAction ) return;
    // ////////////////////////////////////////////////////////////////////////
    // Checkbox Event?
    if( ev.getSource() instanceof JCheckBox )
    {
      JCheckBox cb = ( JCheckBox )ev.getItemSelectable();
      String cmd = cb.getActionCommand();
      // //////////////////////////////////////////////////////////////////////
      // Salzwasser?
      if( cmd.equals( "check_salnity" ) )
      {
        lg.debug( "salnity <" + cb.isSelected() + ">" );
        salnity = cb.isSelected();
        // Gas neu berechnen und zeigen
        setDescriptionForGas( ( Integer )oxygenSpinner.getValue(), ( Integer )heliumSpinner.getValue() );
      }
      else
      {
        lg.warn( "unknown checkbox command <" + cmd + "> recived." );
        return;
      }
    }
    else
    {
      lg.warn( "unknown element for itemStateChanged recived." );
      return;
    }
  }

  /**
   * Setze alle Beschreibungen und Farben für die Gase Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.10.2012
   * @param o2
   * @param he
   */
  public void setDescriptionForGas( int o2, int he )
  {
    double bailoutMOD, bailoutEAD, diluentEAD;
    int n2 = 100 - ( o2 + he );
    //
    // Gas noch einfärben
    //
    if( o2 < 14 )
    {
      gasDescriptionLabel.setForeground( ProjectConst.GASNAMECOLOR_DANGEROUS );
      ( ( NumberEditor )( oxygenSpinner.getEditor() ) ).getTextField().setForeground( ProjectConst.GASNAMECOLOR_DANGEROUS );
    }
    else if( o2 < 21 )
    {
      gasDescriptionLabel.setForeground( ProjectConst.GASNAMECOLOR_NONORMOXIC );
      ( ( NumberEditor )( oxygenSpinner.getEditor() ) ).getTextField().setForeground( ProjectConst.GASNAMECOLOR_NONORMOXIC );
    }
    else
    {
      gasDescriptionLabel.setForeground( ProjectConst.GASNAMECOLOR_NORMAL );
      ( ( NumberEditor )( oxygenSpinner.getEditor() ) ).getTextField().setForeground( ProjectConst.GASNAMECOLOR_NORMAL );
    }
    //
    // jetzt den namen des gases errechnen
    //
    gasDescriptionLabel.setText( GasComputeUnit.getNameForGas( o2, he ) );
    if( unitsString.equals( "metric" ) )
    {
      // MOD und EAD berechnen
      bailoutMOD = GasComputeUnit.getMODForGasMetric( o2, ppOMax, salnity );
      bailoutEAD = GasComputeUnit.getEADForGasMetric( n2, bailoutMOD, salnity );
      // MOD und EAD in String umformen und in das richtige Label schreiben
      bailoutGasValsLabel.setText( String.format( LangStrings.getString( "spx42GaslistEditPanel.mod-ead-label.metric" ), Math.round( bailoutMOD ), Math.round( bailoutEAD ) ) );
      diluentEAD = GasComputeUnit.getEADForDilMetric( o2, he, ppOMax, bailoutMOD, salnity );
      diluentGasValsLabel.setText( String.format( LangStrings.getString( "spx42GaslistEditPanel.ead-on-mod-label.metric" ), Math.round( diluentEAD ) ) );
    }
    else
    {
      bailoutMOD = GasComputeUnit.getMODForGasImperial( o2, ppOMax, salnity );
      bailoutEAD = GasComputeUnit.getEADForGasImperial( n2, bailoutMOD, salnity );
      bailoutGasValsLabel.setText( String.format( LangStrings.getString( "spx42GaslistEditPanel.mod-ead-label.imperial" ), Math.round( bailoutMOD ), Math.round( bailoutEAD ) ) );
      diluentEAD = GasComputeUnit.getEADForDilImperial( o2, he, ppOMax, bailoutMOD, salnity );
      diluentGasValsLabel.setText( String.format( LangStrings.getString( "spx42GaslistEditPanel.ead-on-mod-label.imperial" ), Math.round( diluentEAD ) ) );
    }
    advisedDepthSpinner.setValue( ( int )Math.round( bailoutMOD ) );
  }

  /**
   * Zeige den Dialog Modal an! Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 07.10.2012
   * @return OK
   */
  public boolean showModal()
  {
    setModalityType( ModalityType.APPLICATION_MODAL );
    setModalExclusionType( ModalExclusionType.APPLICATION_EXCLUDE );
    setModal( true );
    setAlwaysOnTop( true );
    setVisible( true );
    return( true );
  }

  @Override
  public void stateChanged( ChangeEvent ev )
  {
    JSpinner currSpinner;
    if( ignoreAction ) return;
    // //////////////////////////////////////////////////////////////////////
    // war es ein spinner?
    if( ev.getSource() instanceof JSpinner )
    {
      currSpinner = ( JSpinner )ev.getSource();
      // //////////////////////////////////////////////////////////////////////
      // Ja aber welcher?
      if( currSpinner.equals( oxygenSpinner ) )
      {
        lg.debug( "change oxygenSpinner..." );
        changeO2Spinner();
        return;
      }
      else if( currSpinner.equals( heliumSpinner ) )
      {
        lg.debug( "change heliumSpinner..." );
        changeHeSpinner();
        return;
      }
      else if( currSpinner.equals( nitrogenSpinner ) )
      {
        lg.warn( "change nitrogenSpinner...(inform programmer, it should NOT do)" );
        return;
      }
      else if( currSpinner.equals( advisedDepthSpinner ) )
      {
        lg.debug( "advised depth changed..." );
        changeAdvisedDepthSpinner();
        return;
      }
      lg.warn( "unknown spinner recived!" );
    }
  }
}
