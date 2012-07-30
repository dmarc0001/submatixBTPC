package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

//@formatter:off
public class spx42ConfigPanel extends JPanel
{                                  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @SuppressWarnings( "unused" )
  private Logger            LOGGER           = null;
  // @formatter:on
  public JLabel             serialNumberText;
  public JLabel             firmwareVersionValueLabel;
  public JSpinner           decoGradientenLowSpinner;
  public JComboBox          decoGradientenPresetComboBox;
  public JComboBox          decoLastStopComboBox;
  public JCheckBox          decoDynGradientsCheckBox;
  public JCheckBox          decoDeepStopCheckBox;
  public JComboBox          autoSetpointComboBox;
  public JComboBox          highSetpointComboBox;
  public JComboBox          displayBrightnessComboBox;
  public JComboBox          displayOrientationComboBox;
  public JComboBox          unitsTemperatureComboBox;
  public JComboBox          unitsDepthComboBox;
  public JComboBox          unitsSalnityComboBox;
  public JPanel             individualPanel;
  public JCheckBox          individualsSensorsOnCheckbox;
  public JCheckBox          individualsPscrModeOnCheckbox;
  public JComboBox          individualsSensorWarnComboBox;
  public JCheckBox          individualsWarningsOnCheckBox;
  public JComboBox          individualsLogintervalComboBox;
  public JSpinner           decoGradientenHighSpinner;
  private JButton           readSPX42ConfigButton;
  private JButton           writeSPX42ConfigButton;
  private JLabel            serialNumberLabel;
  private JLabel            firmwareVersionLabel;
  private JPanel            decompressionPanel;
  private JLabel            decoGradientsLowLabel;
  private JLabel            decoGradientsHighLabel;
  private JLabel            decoLaststopLabel;
  private JLabel            decoDyngradientsLabel;
  private JLabel            decoDeepstopsLabel;
  private JPanel            setpointPanel;
  private JLabel            lblSetpointAutosetpoint;
  private JLabel            lblSetpointHighsetpoint;
  private JPanel            displayPanel;
  private JLabel            lblDisplayBrightness;
  private JLabel            lblDisplayOrientation;
  private JPanel            unitsPanel;
  private JLabel            lblUnitsTemperature;
  private JLabel            lblUnitsDepth;
  private JLabel            lblUnitsSalinity;
  private JLabel            lblSenormode;
  private JLabel            lblIndividualsPscrMode;
  private JLabel            lblSensorwarnings;
  private JLabel            individualsAcusticWarningsLabel;
  private JLabel            individualsLogintervalLabel;
  private JLabel            individualsNotLicensedLabel;

  /**
   * Create the panel.
   */
  @SuppressWarnings( "unused" )
  private spx42ConfigPanel()
  {
    initPanel();
  }

  public spx42ConfigPanel( Logger lg )
  {
    LOGGER = lg;
    initPanel();
  }

  private void initPanel()
  {
    readSPX42ConfigButton = new JButton( "READ" );
    readSPX42ConfigButton.setBounds( 10, 432, 199, 60 );
    readSPX42ConfigButton.setIcon( new ImageIcon( spx42ConfigPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Download.png" ) ) );
    readSPX42ConfigButton.setForeground( new Color( 0, 100, 0 ) );
    readSPX42ConfigButton.setBackground( new Color( 152, 251, 152 ) );
    readSPX42ConfigButton.setActionCommand( "read_config" );
    readSPX42ConfigButton.setPreferredSize( new Dimension( 180, 40 ) );
    readSPX42ConfigButton.setMaximumSize( new Dimension( 160, 40 ) );
    readSPX42ConfigButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    writeSPX42ConfigButton = new JButton( "WRITE" );
    writeSPX42ConfigButton.setBounds( 548, 432, 217, 60 );
    writeSPX42ConfigButton.setIcon( new ImageIcon( spx42ConfigPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Upload.png" ) ) );
    writeSPX42ConfigButton.setForeground( new Color( 255, 0, 0 ) );
    writeSPX42ConfigButton.setBackground( new Color( 255, 192, 203 ) );
    writeSPX42ConfigButton.setActionCommand( "write_config" );
    serialNumberLabel = new JLabel( "SERIAL" );
    serialNumberLabel.setBounds( 10, 20, 140, 20 );
    serialNumberLabel.setAlignmentX( Component.RIGHT_ALIGNMENT );
    serialNumberLabel.setMaximumSize( new Dimension( 250, 40 ) );
    serialNumberLabel.setPreferredSize( new Dimension( 140, 20 ) );
    serialNumberText = new JLabel( "0" );
    serialNumberText.setBounds( 160, 20, 235, 20 );
    serialNumberText.setMaximumSize( new Dimension( 250, 40 ) );
    serialNumberText.setPreferredSize( new Dimension( 140, 20 ) );
    firmwareVersionLabel = new JLabel( "FIRMW-VERSION" );
    firmwareVersionLabel.setBounds( 428, 23, 159, 14 );
    firmwareVersionValueLabel = new JLabel( "V0.0" );
    firmwareVersionValueLabel.setBounds( 597, 23, 182, 14 );
    // config -> DECO-Panel
    decompressionPanel = new JPanel();
    decompressionPanel.setBounds( 10, 51, 385, 154 );
    decompressionPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Deco", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    // config -> DECO-Panel -> inhalt
    decoGradientsLowLabel = new JLabel( "GF-low" );
    decoGradientsLowLabel.setBounds( 15, 19, 68, 14 );
    decoGradientsLowLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoGradientenLowSpinner = new JSpinner();
    decoGradientenLowSpinner.setBounds( 101, 16, 49, 20 );
    decoGradientsLowLabel.setLabelFor( decoGradientenLowSpinner );
    decoGradientenPresetComboBox = new JComboBox();
    decoGradientenPresetComboBox.setBounds( 168, 16, 202, 20 );
    decoGradientenPresetComboBox.setActionCommand( "deco_gradient_preset" );
    decoGradientsHighLabel = new JLabel( "GF-High" );
    decoGradientsHighLabel.setBounds( 15, 45, 68, 14 );
    decoGradientsHighLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoGradientenHighSpinner = new JSpinner();
    decoGradientenHighSpinner.setBounds( 101, 42, 49, 20 );
    decoGradientsHighLabel.setLabelFor( decoGradientenHighSpinner );
    decoLaststopLabel = new JLabel( "last stop" );
    decoLaststopLabel.setBounds( 15, 71, 68, 14 );
    decoLaststopLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoLastStopComboBox = new JComboBox();
    decoLastStopComboBox.setBounds( 101, 68, 49, 20 );
    decoLastStopComboBox.setActionCommand( "deco_last_stop" );
    decoLaststopLabel.setLabelFor( decoLastStopComboBox );
    decoDyngradientsLabel = new JLabel( "dyn.Gradients" );
    decoDyngradientsLabel.setBounds( 15, 98, 68, 14 );
    decoDyngradientsLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoDynGradientsCheckBox = new JCheckBox( "dyn Gradients ON" );
    decoDynGradientsCheckBox.setBounds( 101, 94, 111, 23 );
    decoDynGradientsCheckBox.setActionCommand( "dyn_gradients_on" );
    decoDyngradientsLabel.setLabelFor( decoDynGradientsCheckBox );
    decoDeepstopsLabel = new JLabel( "deepstops" );
    decoDeepstopsLabel.setBounds( 15, 119, 68, 14 );
    decoDeepstopsLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoDeepStopCheckBox = new JCheckBox( "Deepstops ON" );
    decoDeepStopCheckBox.setBounds( 101, 119, 95, 23 );
    decoDeepStopCheckBox.setActionCommand( "deepstops_on" );
    decoDeepstopsLabel.setLabelFor( decoDeepStopCheckBox );
    // config -> setpoint Panel -> Inhalt
    setpointPanel = new JPanel();
    setpointPanel.setBounds( 428, 51, 344, 147 );
    setpointPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Setpoint", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblSetpointAutosetpoint = new JLabel( "Autosetpoint" );
    lblSetpointAutosetpoint.setBounds( 15, 19, 91, 14 );
    lblSetpointAutosetpoint.setHorizontalAlignment( SwingConstants.RIGHT );
    autoSetpointComboBox = new JComboBox();
    autoSetpointComboBox.setBounds( 124, 16, 205, 20 );
    lblSetpointAutosetpoint.setLabelFor( autoSetpointComboBox );
    autoSetpointComboBox.setActionCommand( "set_autosetpoint" );
    lblSetpointHighsetpoint = new JLabel( "Highsetpoint" );
    lblSetpointHighsetpoint.setBounds( 32, 42, 74, 14 );
    lblSetpointHighsetpoint.setHorizontalAlignment( SwingConstants.RIGHT );
    highSetpointComboBox = new JComboBox();
    highSetpointComboBox.setBounds( 124, 42, 205, 20 );
    highSetpointComboBox.setActionCommand( "set_highsetpoint" );
    lblSetpointHighsetpoint.setLabelFor( highSetpointComboBox );
    // config -> display panel -> Inhalt
    displayPanel = new JPanel();
    displayPanel.setBounds( 10, 211, 385, 81 );
    displayPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Display", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblDisplayBrightness = new JLabel( "brightness" );
    lblDisplayBrightness.setBounds( 15, 19, 106, 14 );
    lblDisplayBrightness.setHorizontalAlignment( SwingConstants.RIGHT );
    displayBrightnessComboBox = new JComboBox();
    displayBrightnessComboBox.setBounds( 139, 16, 231, 20 );
    lblDisplayBrightness.setLabelFor( displayBrightnessComboBox );
    displayBrightnessComboBox.setActionCommand( "set_disp_brightness" );
    lblDisplayOrientation = new JLabel( "orientation" );
    lblDisplayOrientation.setBounds( 15, 45, 106, 14 );
    lblDisplayOrientation.setHorizontalAlignment( SwingConstants.RIGHT );
    displayOrientationComboBox = new JComboBox();
    displayOrientationComboBox.setBounds( 139, 42, 231, 20 );
    lblDisplayOrientation.setLabelFor( displayOrientationComboBox );
    displayOrientationComboBox.setActionCommand( "set_display_orientation" );
    // config -> untits panel -> Inhalt
    unitsPanel = new JPanel();
    unitsPanel.setBounds( 10, 298, 385, 119 );
    unitsPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Units", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblUnitsTemperature = new JLabel( "temperature" );
    lblUnitsTemperature.setBounds( 15, 19, 104, 14 );
    lblUnitsTemperature.setHorizontalAlignment( SwingConstants.RIGHT );
    unitsTemperatureComboBox = new JComboBox();
    unitsTemperatureComboBox.setBounds( 138, 16, 232, 20 );
    lblUnitsTemperature.setLabelFor( unitsTemperatureComboBox );
    unitsTemperatureComboBox.setActionCommand( "set_temperature_unit" );
    lblUnitsDepth = new JLabel( "depth" );
    lblUnitsDepth.setBounds( 15, 45, 105, 14 );
    lblUnitsDepth.setHorizontalAlignment( SwingConstants.RIGHT );
    unitsDepthComboBox = new JComboBox();
    unitsDepthComboBox.setBounds( 138, 42, 232, 20 );
    lblUnitsDepth.setLabelFor( unitsDepthComboBox );
    unitsDepthComboBox.setActionCommand( "set_depth_unit" );
    lblUnitsSalinity = new JLabel( "salinity" );
    lblUnitsSalinity.setBounds( 15, 71, 105, 14 );
    lblUnitsSalinity.setHorizontalAlignment( SwingConstants.RIGHT );
    unitsSalnityComboBox = new JComboBox();
    unitsSalnityComboBox.setBounds( 138, 68, 232, 20 );
    lblUnitsSalinity.setLabelFor( unitsSalnityComboBox );
    unitsSalnityComboBox.setActionCommand( "set_salnity" );
    // config -> individual panel -> inhalt
    individualPanel = new JPanel();
    individualPanel.setBounds( 428, 211, 344, 206 );
    individualPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Individuals", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblSenormode = new JLabel( "sensormode" );
    lblSenormode.setBounds( 15, 20, 107, 14 );
    lblSenormode.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsSensorsOnCheckbox = new JCheckBox( "Sensors ON" );
    individualsSensorsOnCheckbox.setBounds( 140, 16, 182, 23 );
    lblSenormode.setLabelFor( individualsSensorsOnCheckbox );
    individualsSensorsOnCheckbox.setActionCommand( "individual_sensors_on" );
    lblIndividualsPscrMode = new JLabel( "PSCR Mode" );
    lblIndividualsPscrMode.setBounds( 67, 43, 55, 14 );
    lblIndividualsPscrMode.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsPscrModeOnCheckbox = new JCheckBox( "PSCR Mode ON" );
    individualsPscrModeOnCheckbox.setBounds( 140, 39, 182, 23 );
    individualsPscrModeOnCheckbox.setForeground( new Color( 128, 0, 128 ) );
    lblIndividualsPscrMode.setLabelFor( individualsPscrModeOnCheckbox );
    individualsPscrModeOnCheckbox.setActionCommand( "individuals_pscr_on" );
    lblSensorwarnings = new JLabel( "sensorwarnings" );
    lblSensorwarnings.setBounds( 25, 67, 97, 14 );
    lblSensorwarnings.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsSensorWarnComboBox = new JComboBox();
    individualsSensorWarnComboBox.setBounds( 140, 64, 182, 20 );
    lblSensorwarnings.setLabelFor( individualsSensorWarnComboBox );
    individualsSensorWarnComboBox.setActionCommand( "set_sensorwarnings" );
    individualsAcusticWarningsLabel = new JLabel( "acustic warnings" );
    individualsAcusticWarningsLabel.setBounds( 15, 90, 107, 14 );
    individualsAcusticWarningsLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsWarningsOnCheckBox = new JCheckBox( "warnings ON" );
    individualsWarningsOnCheckBox.setBounds( 140, 86, 182, 23 );
    individualsAcusticWarningsLabel.setLabelFor( individualsWarningsOnCheckBox );
    individualsWarningsOnCheckBox.setActionCommand( "individuals_warnings_on" );
    individualsLogintervalLabel = new JLabel( "loginterval" );
    individualsLogintervalLabel.setBounds( 15, 114, 107, 14 );
    individualsLogintervalLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsLogintervalComboBox = new JComboBox();
    individualsLogintervalComboBox.setBounds( 140, 111, 182, 20 );
    individualsLogintervalLabel.setLabelFor( individualsLogintervalComboBox );
    individualsLogintervalComboBox.setActionCommand( "set_loginterval" );
    individualsNotLicensedLabel = new JLabel( "------" );
    individualsNotLicensedLabel.setBounds( 15, 163, 307, 14 );
    individualsNotLicensedLabel.setForeground( Color.DARK_GRAY );
    individualsNotLicensedLabel.setFont( new Font( "Tahoma", Font.ITALIC, 11 ) );
    individualsNotLicensedLabel.setHorizontalAlignment( SwingConstants.CENTER );
    setLayout( null );
    add( readSPX42ConfigButton );
    add( writeSPX42ConfigButton );
    add( unitsPanel );
    unitsPanel.setLayout( null );
    unitsPanel.add( lblUnitsTemperature );
    unitsPanel.add( lblUnitsDepth );
    unitsPanel.add( lblUnitsSalinity );
    unitsPanel.add( unitsSalnityComboBox );
    unitsPanel.add( unitsDepthComboBox );
    unitsPanel.add( unitsTemperatureComboBox );
    add( displayPanel );
    displayPanel.setLayout( null );
    displayPanel.add( lblDisplayOrientation );
    displayPanel.add( lblDisplayBrightness );
    displayPanel.add( displayBrightnessComboBox );
    displayPanel.add( displayOrientationComboBox );
    add( decompressionPanel );
    decompressionPanel.setLayout( null );
    decompressionPanel.add( decoDyngradientsLabel );
    decompressionPanel.add( decoDeepstopsLabel );
    decompressionPanel.add( decoLaststopLabel );
    decompressionPanel.add( decoGradientsHighLabel );
    decompressionPanel.add( decoGradientsLowLabel );
    decompressionPanel.add( decoLastStopComboBox );
    decompressionPanel.add( decoGradientenLowSpinner );
    decompressionPanel.add( decoGradientenHighSpinner );
    decompressionPanel.add( decoGradientenPresetComboBox );
    decompressionPanel.add( decoDynGradientsCheckBox );
    decompressionPanel.add( decoDeepStopCheckBox );
    add( serialNumberLabel );
    add( serialNumberText );
    add( firmwareVersionLabel );
    add( firmwareVersionValueLabel );
    add( setpointPanel );
    setpointPanel.setLayout( null );
    setpointPanel.add( lblSetpointHighsetpoint );
    setpointPanel.add( lblSetpointAutosetpoint );
    setpointPanel.add( autoSetpointComboBox );
    setpointPanel.add( highSetpointComboBox );
    add( individualPanel );
    individualPanel.setLayout( null );
    individualPanel.add( individualsNotLicensedLabel );
    individualPanel.add( individualsLogintervalLabel );
    individualPanel.add( individualsAcusticWarningsLabel );
    individualPanel.add( lblIndividualsPscrMode );
    individualPanel.add( lblSenormode );
    individualPanel.add( lblSensorwarnings );
    individualPanel.add( individualsLogintervalComboBox );
    individualPanel.add( individualsSensorsOnCheckbox );
    individualPanel.add( individualsSensorWarnComboBox );
    individualPanel.add( individualsPscrModeOnCheckbox );
    individualPanel.add( individualsWarningsOnCheckBox );
  }

  /**
   * 
   * Alle Listener für Aktionen in diesem Objekt auf das Mainobjekt kenken
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.04.2012
   * @param mainCommGUI
   */
  public void setGlobalChangeListener( MainCommGUI mainCommGUI )
  {
    readSPX42ConfigButton.addActionListener( mainCommGUI );
    readSPX42ConfigButton.addMouseMotionListener( mainCommGUI );
    writeSPX42ConfigButton.addActionListener( mainCommGUI );
    writeSPX42ConfigButton.addMouseMotionListener( mainCommGUI );
    decoGradientenPresetComboBox.addActionListener( mainCommGUI );
    decoLastStopComboBox.addActionListener( mainCommGUI );
    decoLastStopComboBox.addMouseMotionListener( mainCommGUI );
    decoDynGradientsCheckBox.addMouseMotionListener( mainCommGUI );
    decoDynGradientsCheckBox.addItemListener( mainCommGUI );
    decoDeepStopCheckBox.addItemListener( mainCommGUI );
    decoDeepStopCheckBox.addMouseMotionListener( mainCommGUI );
    autoSetpointComboBox.addActionListener( mainCommGUI );
    autoSetpointComboBox.addMouseMotionListener( mainCommGUI );
    highSetpointComboBox.addActionListener( mainCommGUI );
    highSetpointComboBox.addMouseMotionListener( mainCommGUI );
    displayBrightnessComboBox.addActionListener( mainCommGUI );
    displayBrightnessComboBox.addMouseMotionListener( mainCommGUI );
    displayOrientationComboBox.addActionListener( mainCommGUI );
    displayOrientationComboBox.addMouseMotionListener( mainCommGUI );
    unitsTemperatureComboBox.addActionListener( mainCommGUI );
    unitsTemperatureComboBox.addMouseMotionListener( mainCommGUI );
    unitsDepthComboBox.addActionListener( mainCommGUI );
    unitsDepthComboBox.addMouseMotionListener( mainCommGUI );
    unitsSalnityComboBox.addActionListener( mainCommGUI );
    unitsSalnityComboBox.addMouseMotionListener( mainCommGUI );
    individualsSensorsOnCheckbox.addItemListener( mainCommGUI );
    individualsSensorsOnCheckbox.addMouseMotionListener( mainCommGUI );
    individualsPscrModeOnCheckbox.addItemListener( mainCommGUI );
    individualsPscrModeOnCheckbox.addMouseMotionListener( mainCommGUI );
    individualsSensorWarnComboBox.addActionListener( mainCommGUI );
    individualsSensorWarnComboBox.addMouseMotionListener( mainCommGUI );
    individualsWarningsOnCheckBox.addItemListener( mainCommGUI );
    individualsWarningsOnCheckBox.addMouseMotionListener( mainCommGUI );
    individualsLogintervalComboBox.addActionListener( mainCommGUI );
    individualsLogintervalComboBox.addMouseMotionListener( mainCommGUI );
    decoGradientenLowSpinner.addChangeListener( mainCommGUI );
    decoGradientenHighSpinner.addChangeListener( mainCommGUI );
  }

  public int setLanguageStrings( ResourceBundle stringsBundle )
  {
    String[] entrys;
    DefaultComboBoxModel portBoxModel;
    try
    {
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane config
      serialNumberLabel.setText( stringsBundle.getString( "spx42ConfigPanel.serialNumberLabel.text" ) );
      readSPX42ConfigButton.setText( stringsBundle.getString( "spx42ConfigPanel.readSPX42ConfigButton.text" ) );
      readSPX42ConfigButton.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.readSPX42ConfigButton.tooltiptext" ) );
      writeSPX42ConfigButton.setText( stringsBundle.getString( "spx42ConfigPanel.writeSPX42ConfigButton.text" ) );
      writeSPX42ConfigButton.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.writeSPX42ConfigButton.tooltiptext" ) );
      firmwareVersionLabel.setText( stringsBundle.getString( "spx42ConfigPanel.firmwareVersionLabel.text" ) );
      // DECO
      ( ( TitledBorder )( decompressionPanel.getBorder() ) ).setTitle( stringsBundle.getString( "spx42ConfigPanel.decoTitleBorder.text" ) );
      decoGradientenPresetComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.decoGradientenPresetComboBox.tooltiptext" ) );
      decoGradientsHighLabel.setText( stringsBundle.getString( "spx42ConfigPanel.decoGradientsHighLabel.text" ) );
      decoGradientsLowLabel.setText( stringsBundle.getString( "spx42ConfigPanel.decoGradientsLowLabel.text" ) );
      decoGradientenLowSpinner.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.decoGradientenLowSpinner.tooltiptext" ) );
      decoGradientenHighSpinner.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.decoGradientenHighSpinner.tooltiptext" ) );
      decoLaststopLabel.setText( stringsBundle.getString( "spx42ConfigPanel.decoLaststopLabel.text" ) );
      decoLastStopComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.decoLastStopComboBox.3m.text" ), stringsBundle.getString( "spx42ConfigPanel.decoLastStopComboBox.6m.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      decoLastStopComboBox.setModel( portBoxModel );
      decoLastStopComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.decoLastStopComboBox.tooltipttext" ) );
      decoGradientenPresetComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.decoDyngradientsLabel.vconservative.text" ),
          stringsBundle.getString( "spx42ConfigPanel.decoDyngradientsLabel.conservative.text" ), stringsBundle.getString( "spx42ConfigPanel.decoDyngradientsLabel.moderate.text" ),
          stringsBundle.getString( "spx42ConfigPanel.decoDyngradientsLabel.aggressive.text" ),
          stringsBundle.getString( "spx42ConfigPanel.decoDyngradientsLabel.vaggressive.text" ), stringsBundle.getString( "spx42ConfigPanel.decoDyngradientsLabel.custom.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      decoGradientenPresetComboBox.setModel( portBoxModel );
      decoDyngradientsLabel.setText( stringsBundle.getString( "spx42ConfigPanel.decoDyngradientsLabel.text" ) );
      decoDynGradientsCheckBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.decoDynGradientsCheckBox.tooltiptext" ) );
      decoDeepstopsLabel.setText( stringsBundle.getString( "spx42ConfigPanel.decoDeepstopsLabel.text" ) );
      decoDeepStopCheckBox.setText( stringsBundle.getString( "spx42ConfigPanel.decoDeepStopCheckBox.text" ) );
      decoDynGradientsCheckBox.setText( stringsBundle.getString( "spx42ConfigPanel.decoDynGradientsCheckBox.text" ) );
      decoDeepStopCheckBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.decoDeepStopCheckBox.tooltiptext" ) );
      // SETPOINT
      ( ( TitledBorder )( setpointPanel.getBorder() ) ).setTitle( stringsBundle.getString( "spx42ConfigPanel.setpointPanel.text" ) );
      lblSetpointAutosetpoint.setText( stringsBundle.getString( "spx42ConfigPanel.lblSetpointAutosetpoint.text" ) );
      autoSetpointComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.autoSetpointComboBox.off.text" ), stringsBundle.getString( "spx42ConfigPanel.autoSetpointComboBox.5m.text" ),
          stringsBundle.getString( "spx42ConfigPanel.autoSetpointComboBox.10m.text" ), stringsBundle.getString( "spx42ConfigPanel.autoSetpointComboBox.15m.text" ),
          stringsBundle.getString( "spx42ConfigPanel.autoSetpointComboBox.20m.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      autoSetpointComboBox.setModel( portBoxModel );
      autoSetpointComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.autoSetpointComboBox.tooltiptext" ) );
      lblSetpointHighsetpoint.setText( stringsBundle.getString( "spx42ConfigPanel.lblSetpointHighsetpoint.text" ) );
      highSetpointComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.highSetpointComboBox.10.text" ), stringsBundle.getString( "spx42ConfigPanel.highSetpointComboBox.11.text" ),
          stringsBundle.getString( "spx42ConfigPanel.highSetpointComboBox.12.text" ), stringsBundle.getString( "spx42ConfigPanel.highSetpointComboBox.13.text" ),
          stringsBundle.getString( "spx42ConfigPanel.highSetpointComboBox.14.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      highSetpointComboBox.setModel( portBoxModel );
      highSetpointComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.highSetpointComboBox.tooltiptext" ) );
      // DISPLAY
      ( ( TitledBorder )( displayPanel.getBorder() ) ).setTitle( stringsBundle.getString( "spx42ConfigPanel.displayPanel.text" ) );
      lblDisplayBrightness.setText( stringsBundle.getString( "spx42ConfigPanel.lblDisplayBrightness.text" ) );
      displayBrightnessComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.displayBrightnessComboBox.10.text" ), stringsBundle.getString( "spx42ConfigPanel.displayBrightnessComboBox.50.text" ),
          stringsBundle.getString( "spx42ConfigPanel.displayBrightnessComboBox.100.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      displayBrightnessComboBox.setModel( portBoxModel );
      displayBrightnessComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.displayBrightnessComboBox.tooltiptext" ) );
      lblDisplayOrientation.setText( stringsBundle.getString( "spx42ConfigPanel.lblDisplayOrientation.text" ) );
      displayOrientationComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.displayOrientationComboBox.landscape.text" ),
          stringsBundle.getString( "spx42ConfigPanel.displayOrientationComboBox.landscape180.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      displayOrientationComboBox.setModel( portBoxModel );
      displayOrientationComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.displayOrientationComboBox.tooltiptext" ) );
      // UNITS
      ( ( TitledBorder )( unitsPanel.getBorder() ) ).setTitle( stringsBundle.getString( "spx42ConfigPanel.unitsPanel.text" ) );
      lblUnitsTemperature.setText( stringsBundle.getString( "spx42ConfigPanel.lblUnitsTemperature.text" ) );
      unitsTemperatureComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.unitsTemperatureComboBox.fahrenheit.text" ), stringsBundle.getString( "spx42ConfigPanel.unitsTemperatureComboBox.celsius.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      unitsTemperatureComboBox.setModel( portBoxModel );
      unitsTemperatureComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.unitsTemperatureComboBox.tooltiptext" ) );
      lblUnitsDepth.setText( stringsBundle.getString( "spx42ConfigPanel.lblUnitsDepth.text" ) );
      unitsDepthComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.unitsDepthComboBox.metrical.text" ), stringsBundle.getString( "spx42ConfigPanel.unitsDepthComboBox.imperial.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      unitsDepthComboBox.setModel( portBoxModel );
      unitsDepthComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.unitsDepthComboBox.tooltiptext" ) );
      lblUnitsSalinity.setText( stringsBundle.getString( "spx42ConfigPanel.lblUnitsSalinity.text" ) );
      unitsSalnityComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.unitsSalnityComboBox.saltwater.text" ), stringsBundle.getString( "spx42ConfigPanel.unitsSalnityComboBox.clearwater.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      unitsSalnityComboBox.setModel( portBoxModel );
      unitsSalnityComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.unitsSalnityComboBox.tooltiptext" ) );
      // INDIVIDUALS
      ( ( TitledBorder )( individualPanel.getBorder() ) ).setTitle( stringsBundle.getString( "spx42ConfigPanel.individualPanel.text" ) );
      lblSenormode.setText( stringsBundle.getString( "spx42ConfigPanel.lblSenormode.text" ) );
      individualsSensorsOnCheckbox.setText( stringsBundle.getString( "spx42ConfigPanel.chIndividualsSensorsOnCheckbox.text" ) );
      individualsSensorsOnCheckbox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.chIndividualsSensorsOnCheckbox.tooltiptext" ) );
      lblIndividualsPscrMode.setText( stringsBundle.getString( "spx42ConfigPanel.lblIndividualsPscrMode.text" ) );
      individualsPscrModeOnCheckbox.setText( stringsBundle.getString( "spx42ConfigPanel.IndividualsPscrModoOnCheckbox.text" ) );
      individualsPscrModeOnCheckbox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.IndividualsPscrModoOnCheckbox.tooltiptext" ) );
      lblSensorwarnings.setText( stringsBundle.getString( "spx42ConfigPanel.lblSensorwarnings.text" ) );
      individualsSensorWarnComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.individualsSensorwarnComboBox.1.text" ), stringsBundle.getString( "spx42ConfigPanel.individualsSensorwarnComboBox.2.text" ),
          stringsBundle.getString( "spx42ConfigPanel.individualsSensorwarnComboBox.3.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      individualsSensorWarnComboBox.setModel( portBoxModel );
      individualsSensorWarnComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.individualsSensorwarnComboBox.tooltiptext" ) );
      individualsAcusticWarningsLabel.setText( stringsBundle.getString( "spx42ConfigPanel.individualsAcusticWarningsLabel.text" ) );
      individualsWarningsOnCheckBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.individualsWarningsOnCheckBox.tooltiptext" ) );
      individualsLogintervalLabel.setText( stringsBundle.getString( "spx42ConfigPanel.individualsLogintervalLabel.text" ) );
      individualsLogintervalComboBox.removeAllItems();
      entrys = new String[]
      { stringsBundle.getString( "spx42ConfigPanel.individualsLogintervalComboBox.10s.text" ),
          stringsBundle.getString( "spx42ConfigPanel.individualsLogintervalComboBox.20s.text" ),
          stringsBundle.getString( "spx42ConfigPanel.individualsLogintervalComboBox.60s.text" ) };
      portBoxModel = new DefaultComboBoxModel( entrys );
      individualsLogintervalComboBox.setModel( portBoxModel );
      individualsLogintervalComboBox.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.individualsLogintervalComboBox.tooltiptext" ) );
      individualsNotLicensedLabel.setToolTipText( stringsBundle.getString( "spx42ConfigPanel.individualsNotLicensedLabel.tooltiptext" ) );
      individualsNotLicensedLabel.setText( " " );
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

  public void setIndividualsPanelEnabled( boolean en )
  {
    for( Component cp : individualPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    if( !en )
    {
      individualsNotLicensedLabel.setEnabled( false );
      individualsNotLicensedLabel.setText( individualsNotLicensedLabel.getToolTipText() );
    }
    else
    {
      individualsNotLicensedLabel.setEnabled( true );
      individualsNotLicensedLabel.setText( "" );
    }
    individualPanel.setEnabled( en );
  }

  public void setDecoPanelEnabled( boolean en )
  {
    for( Component cp : decompressionPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    decompressionPanel.setEnabled( en );
  }

  public void setDisplayPanelEnabled( boolean en )
  {
    for( Component cp : displayPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    displayPanel.setEnabled( en );
  }

  public void setUnitsPanelEnabled( boolean en )
  {
    for( Component cp : unitsPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    unitsPanel.setEnabled( en );
  }

  public void setSetpointPanel( boolean en )
  {
    for( Component cp : setpointPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    setpointPanel.setEnabled( en );
  }
}
