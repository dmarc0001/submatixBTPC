package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.beans.Beans;
import java.util.MissingResourceException;

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

import org.apache.log4j.Logger;

import de.dmarcini.submatix.pclogger.lang.LangStrings;
import de.dmarcini.submatix.pclogger.utils.SPX42Config;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

//@formatter:off
/**
 * SPX42 Konfigurations Oberfläche
 *
 * Project: SubmatixBTForPC
 * Package: de.dmarcini.submatix.pclogger.gui
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 *
 * Stand: 06.12.2013
 */
public class spx42ConfigPanel extends JPanel
{        /**
   * 
   */
  private static final long serialVersionUID     = 1L;
  @SuppressWarnings( "unused" )
  private Logger            lg                   = null;
  private boolean           isPanelInitiated;
  private boolean           areAllConfigPanelsEnabled;
  private SPX42Config       currentConfig        = null;
  private MainCommGUI       mainCommGUI          = null;
  private String            serialNumber         = "0";
  private int               currentPreset        = -1;
  private String            firmwareLabelText    = "-";
  private boolean           isIndividualsEnabled = false;
  // private ResourceBundle stringsBundle = null;
  // @formatter:on
  private JLabel            serialNumberText;
  private JLabel            firmwareVersionValueLabel;
  private JSpinner          decoGradientenLowSpinner;
  private JComboBox<String> decoGradientenPresetComboBox;
  private JComboBox<String> decoLastStopComboBox;
  private JCheckBox         decoDynGradientsCheckBox;
  private JCheckBox         decoDeepStopCheckBox;
  private JComboBox<String> autoSetpointComboBox;
  private JComboBox<String> highSetpointComboBox;
  private JComboBox<String> displayBrightnessComboBox;
  private JComboBox<String> displayOrientationComboBox;
  private JComboBox<String> unitsTemperatureComboBox;
  private JComboBox<String> unitsDepthComboBox;
  private JComboBox<String> unitsSalnityComboBox;
  private JPanel            individualPanel;
  private JCheckBox         individualsSensorsOnCheckbox;
  private JCheckBox         individualsPscrModeOnCheckbox;
  private JComboBox<String> individualsSensorWarnComboBox;
  private JCheckBox         individualsWarningsOnCheckBox;
  private JComboBox<String> individualsLogintervalComboBox;
  private JSpinner          decoGradientenHighSpinner;
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
  private JComboBox<String> individualsTempStickVersionComboBox;
  private JLabel            individualsTempStickVersionLabel;

  /**
   * Konstruktor
   */
  public spx42ConfigPanel()
  {
    lg = SpxPcloggerProgramConfig.LOGGER;
    isPanelInitiated = false;
    currentConfig = null;
    areAllConfigPanelsEnabled = false;
    // DEBUG: wieder entfernen nach Design
    if( Beans.isDesignTime() ) initPanel();
  }

  /**
   * 
   * Welches ist der Spinner für HIGH-Gradienten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * Stand: 06.12.2013
   * 
   * @return Spinner
   */
  public JSpinner getDecoGradientenHighSpinner()
  {
    if( !isPanelInitiated ) return( null );
    return( decoGradientenHighSpinner );
  }

  /**
   * 
   * Gib den Spinner für den LOW-Gradienten zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * Stand: 06.12.2013
   * 
   * @return Spinner
   */
  public JSpinner getDecoGradientenLowSpinner()
  {
    if( !isPanelInitiated ) return( null );
    return( decoGradientenLowSpinner );
  }

  /***
   * 
   * Initialisiere das Panel
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * Stand: 06.12.2013
   */
  private void initPanel()
  {
    readSPX42ConfigButton = new JButton( LangStrings.getString( "spx42ConfigPanel.readSPX42ConfigButton.text" ) ); //$NON-NLS-1$
    readSPX42ConfigButton.setHorizontalAlignment( SwingConstants.LEFT );
    readSPX42ConfigButton.setIconTextGap( 15 );
    readSPX42ConfigButton.setBounds( 10, 432, 199, 60 );
    readSPX42ConfigButton.setIcon( new ImageIcon( spx42ConfigPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Download.png" ) ) );
    readSPX42ConfigButton.setForeground( new Color( 0, 100, 0 ) );
    readSPX42ConfigButton.setBackground( new Color( 152, 251, 152 ) );
    readSPX42ConfigButton.setActionCommand( "read_config" );
    readSPX42ConfigButton.setPreferredSize( new Dimension( 180, 40 ) );
    readSPX42ConfigButton.setMaximumSize( new Dimension( 160, 40 ) );
    readSPX42ConfigButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    writeSPX42ConfigButton = new JButton( LangStrings.getString( "spx42ConfigPanel.writeSPX42ConfigButton.text" ) ); //$NON-NLS-1$
    writeSPX42ConfigButton.setHorizontalAlignment( SwingConstants.LEFT );
    writeSPX42ConfigButton.setIconTextGap( 15 );
    writeSPX42ConfigButton.setBounds( 548, 432, 217, 60 );
    writeSPX42ConfigButton.setIcon( new ImageIcon( spx42ConfigPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Upload.png" ) ) );
    writeSPX42ConfigButton.setForeground( new Color( 255, 0, 0 ) );
    writeSPX42ConfigButton.setBackground( new Color( 255, 192, 203 ) );
    writeSPX42ConfigButton.setActionCommand( "write_config" );
    serialNumberLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.serialNumberLabel.text" ) ); //$NON-NLS-1$
    serialNumberLabel.setBounds( 10, 20, 140, 20 );
    serialNumberLabel.setAlignmentX( Component.RIGHT_ALIGNMENT );
    serialNumberLabel.setMaximumSize( new Dimension( 250, 40 ) );
    serialNumberLabel.setPreferredSize( new Dimension( 140, 20 ) );
    serialNumberText = new JLabel( "0" );
    serialNumberText.setBounds( 160, 20, 235, 20 );
    serialNumberText.setMaximumSize( new Dimension( 250, 40 ) );
    serialNumberText.setPreferredSize( new Dimension( 140, 20 ) );
    firmwareVersionLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.firmwareVersionLabel.text" ) ); //$NON-NLS-1$
    firmwareVersionLabel.setBounds( 428, 23, 159, 14 );
    firmwareVersionValueLabel = new JLabel( "V0.0" );
    firmwareVersionValueLabel.setBounds( 597, 23, 182, 14 );
    // config -> DECO-Panel
    decompressionPanel = new JPanel();
    decompressionPanel.setBounds( 10, 51, 385, 154 );
    decompressionPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Deco", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    // config -> DECO-Panel -> inhalt
    decoGradientsLowLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.decoGradientsLowLabel.text" ) ); //$NON-NLS-1$
    decoGradientsLowLabel.setBounds( 15, 19, 68, 14 );
    decoGradientsLowLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoGradientenLowSpinner = new JSpinner();
    decoGradientenLowSpinner.setBounds( 101, 16, 49, 20 );
    decoGradientsLowLabel.setLabelFor( decoGradientenLowSpinner );
    decoGradientenPresetComboBox = new JComboBox<String>();
    decoGradientenPresetComboBox.setBounds( 168, 16, 202, 20 );
    decoGradientenPresetComboBox.setActionCommand( "deco_gradient_preset" );
    decoGradientsHighLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.decoGradientsHighLabel.text" ) ); //$NON-NLS-1$
    decoGradientsHighLabel.setBounds( 15, 45, 68, 14 );
    decoGradientsHighLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoGradientenHighSpinner = new JSpinner();
    decoGradientenHighSpinner.setBounds( 101, 42, 49, 20 );
    decoGradientsHighLabel.setLabelFor( decoGradientenHighSpinner );
    decoLaststopLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.decoLaststopLabel.text" ) ); //$NON-NLS-1$
    decoLaststopLabel.setBounds( 15, 71, 68, 14 );
    decoLaststopLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoLastStopComboBox = new JComboBox<String>();
    decoLastStopComboBox.setBounds( 101, 68, 49, 20 );
    decoLastStopComboBox.setActionCommand( "deco_last_stop" );
    decoLaststopLabel.setLabelFor( decoLastStopComboBox );
    decoDyngradientsLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.decoDyngradientsLabel.text" ) ); //$NON-NLS-1$
    decoDyngradientsLabel.setBounds( 15, 98, 68, 14 );
    decoDyngradientsLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoDynGradientsCheckBox = new JCheckBox( LangStrings.getString( "spx42ConfigPanel.decoDynGradientsCheckBox.text" ) ); //$NON-NLS-1$
    decoDynGradientsCheckBox.setBounds( 101, 94, 111, 23 );
    decoDynGradientsCheckBox.setActionCommand( "dyn_gradients_on" );
    decoDyngradientsLabel.setLabelFor( decoDynGradientsCheckBox );
    decoDeepstopsLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.decoDeepstopsLabel.text" ) ); //$NON-NLS-1$
    decoDeepstopsLabel.setBounds( 15, 119, 68, 14 );
    decoDeepstopsLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    decoDeepStopCheckBox = new JCheckBox( LangStrings.getString( "spx42ConfigPanel.decoDeepStopCheckBox.text" ) ); //$NON-NLS-1$
    decoDeepStopCheckBox.setBounds( 101, 119, 111, 23 );
    decoDeepStopCheckBox.setActionCommand( "deepstops_on" );
    decoDeepstopsLabel.setLabelFor( decoDeepStopCheckBox );
    // config -> setpoint Panel -> Inhalt
    setpointPanel = new JPanel();
    setpointPanel.setBounds( 428, 51, 344, 147 );
    setpointPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Setpoint", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblSetpointAutosetpoint = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblSetpointAutosetpoint.text" ) ); //$NON-NLS-1$
    lblSetpointAutosetpoint.setBounds( 15, 19, 91, 14 );
    lblSetpointAutosetpoint.setHorizontalAlignment( SwingConstants.RIGHT );
    autoSetpointComboBox = new JComboBox<String>();
    autoSetpointComboBox.setBounds( 124, 16, 205, 20 );
    lblSetpointAutosetpoint.setLabelFor( autoSetpointComboBox );
    autoSetpointComboBox.setActionCommand( "set_autosetpoint" );
    lblSetpointHighsetpoint = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblSetpointHighsetpoint.text" ) ); //$NON-NLS-1$
    lblSetpointHighsetpoint.setBounds( 32, 42, 74, 14 );
    lblSetpointHighsetpoint.setHorizontalAlignment( SwingConstants.RIGHT );
    highSetpointComboBox = new JComboBox<String>();
    highSetpointComboBox.setBounds( 124, 42, 205, 20 );
    highSetpointComboBox.setActionCommand( "set_highsetpoint" );
    lblSetpointHighsetpoint.setLabelFor( highSetpointComboBox );
    // config -> display panel -> Inhalt
    displayPanel = new JPanel();
    displayPanel.setBounds( 10, 211, 385, 81 );
    displayPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Display", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblDisplayBrightness = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblDisplayBrightness.text" ) ); //$NON-NLS-1$
    lblDisplayBrightness.setBounds( 15, 19, 106, 14 );
    lblDisplayBrightness.setHorizontalAlignment( SwingConstants.RIGHT );
    displayBrightnessComboBox = new JComboBox<String>();
    displayBrightnessComboBox.setBounds( 139, 16, 231, 20 );
    lblDisplayBrightness.setLabelFor( displayBrightnessComboBox );
    displayBrightnessComboBox.setActionCommand( "set_disp_brightness" );
    lblDisplayOrientation = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblDisplayOrientation.text" ) ); //$NON-NLS-1$
    lblDisplayOrientation.setBounds( 15, 45, 106, 14 );
    lblDisplayOrientation.setHorizontalAlignment( SwingConstants.RIGHT );
    displayOrientationComboBox = new JComboBox<String>();
    displayOrientationComboBox.setBounds( 139, 42, 231, 20 );
    lblDisplayOrientation.setLabelFor( displayOrientationComboBox );
    displayOrientationComboBox.setActionCommand( "set_display_orientation" );
    // config -> untits panel -> Inhalt
    unitsPanel = new JPanel();
    unitsPanel.setBounds( 10, 298, 385, 119 );
    unitsPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Units", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblUnitsTemperature = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblUnitsTemperature.text" ) ); //$NON-NLS-1$
    lblUnitsTemperature.setBounds( 15, 19, 104, 14 );
    lblUnitsTemperature.setHorizontalAlignment( SwingConstants.RIGHT );
    unitsTemperatureComboBox = new JComboBox<String>();
    unitsTemperatureComboBox.setBounds( 138, 16, 232, 20 );
    lblUnitsTemperature.setLabelFor( unitsTemperatureComboBox );
    unitsTemperatureComboBox.setActionCommand( "set_temperature_unit" );
    lblUnitsDepth = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblUnitsDepth.text" ) ); //$NON-NLS-1$
    lblUnitsDepth.setBounds( 15, 45, 105, 14 );
    lblUnitsDepth.setHorizontalAlignment( SwingConstants.RIGHT );
    unitsDepthComboBox = new JComboBox<String>();
    unitsDepthComboBox.setBounds( 138, 42, 232, 20 );
    lblUnitsDepth.setLabelFor( unitsDepthComboBox );
    unitsDepthComboBox.setActionCommand( "set_depth_unit" );
    lblUnitsSalinity = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblUnitsSalinity.text" ) ); //$NON-NLS-1$
    lblUnitsSalinity.setBounds( 15, 71, 105, 14 );
    lblUnitsSalinity.setHorizontalAlignment( SwingConstants.RIGHT );
    unitsSalnityComboBox = new JComboBox<String>();
    unitsSalnityComboBox.setBounds( 138, 68, 232, 20 );
    lblUnitsSalinity.setLabelFor( unitsSalnityComboBox );
    unitsSalnityComboBox.setActionCommand( "set_salnity" );
    // config -> individual panel -> inhalt
    individualPanel = new JPanel();
    individualPanel.setBounds( 428, 211, 344, 206 );
    individualPanel.setBorder( new TitledBorder( new LineBorder( new Color( 128, 128, 128 ), 1, true ), "Individuals", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    lblSenormode = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblSenormode.text" ) ); //$NON-NLS-1$
    lblSenormode.setBounds( 15, 20, 107, 14 );
    lblSenormode.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsSensorsOnCheckbox = new JCheckBox( LangStrings.getString( "spx42ConfigPanel.chIndividualsSensorsOnCheckbox.text" ) ); //$NON-NLS-1$
    individualsSensorsOnCheckbox.setBounds( 140, 16, 182, 23 );
    lblSenormode.setLabelFor( individualsSensorsOnCheckbox );
    individualsSensorsOnCheckbox.setActionCommand( "individual_sensors_on" );
    lblIndividualsPscrMode = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblIndividualsPscrMode.text" ) ); //$NON-NLS-1$
    lblIndividualsPscrMode.setBounds( 67, 43, 55, 14 );
    lblIndividualsPscrMode.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsPscrModeOnCheckbox = new JCheckBox( LangStrings.getString( "spx42ConfigPanel.IndividualsPscrModoOnCheckbox.text" ) ); //$NON-NLS-1$
    individualsPscrModeOnCheckbox.setBounds( 140, 39, 182, 23 );
    individualsPscrModeOnCheckbox.setForeground( new Color( 128, 0, 128 ) );
    lblIndividualsPscrMode.setLabelFor( individualsPscrModeOnCheckbox );
    individualsPscrModeOnCheckbox.setActionCommand( "individuals_pscr_on" );
    lblSensorwarnings = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblSensorwarnings.text" ) ); //$NON-NLS-1$
    lblSensorwarnings.setBounds( 25, 67, 97, 14 );
    lblSensorwarnings.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsSensorWarnComboBox = new JComboBox<String>();
    individualsSensorWarnComboBox.setBounds( 140, 64, 182, 20 );
    lblSensorwarnings.setLabelFor( individualsSensorWarnComboBox );
    individualsSensorWarnComboBox.setActionCommand( "set_sensorwarnings" );
    individualsAcusticWarningsLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.individualsAcusticWarningsLabel.text" ) ); //$NON-NLS-1$
    individualsAcusticWarningsLabel.setBounds( 15, 90, 107, 14 );
    individualsAcusticWarningsLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsWarningsOnCheckBox = new JCheckBox( LangStrings.getString( "spx42ConfigPanel.individualsWarningsOnCheckBox.text" ) ); //$NON-NLS-1$
    individualsWarningsOnCheckBox.setBounds( 140, 86, 182, 23 );
    individualsAcusticWarningsLabel.setLabelFor( individualsWarningsOnCheckBox );
    individualsWarningsOnCheckBox.setActionCommand( "individuals_warnings_on" );
    individualsLogintervalLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.individualsLogintervalLabel.text" ) ); //$NON-NLS-1$
    individualsLogintervalLabel.setBounds( 15, 114, 107, 14 );
    individualsLogintervalLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsLogintervalComboBox = new JComboBox<String>();
    individualsLogintervalComboBox.setBounds( 140, 111, 182, 20 );
    individualsLogintervalLabel.setLabelFor( individualsLogintervalComboBox );
    individualsLogintervalComboBox.setActionCommand( "set_loginterval" );
    individualsNotLicensedLabel = new JLabel( "------" );
    individualsNotLicensedLabel.setBounds( 15, 180, 307, 14 );
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
    individualsTempStickVersionComboBox = new JComboBox<String>();
    individualsTempStickVersionComboBox.setActionCommand( "set_tempstickversion" );
    individualsTempStickVersionComboBox.setBounds( 140, 148, 182, 20 );
    individualPanel.add( individualsTempStickVersionComboBox );
    individualsTempStickVersionLabel = new JLabel( LangStrings.getString( "spx42ConfigPanel.lblTempStickVer.text" ) ); //$NON-NLS-1$
    individualsTempStickVersionLabel.setLabelFor( individualsTempStickVersionComboBox );
    individualsTempStickVersionLabel.setHorizontalAlignment( SwingConstants.RIGHT );
    individualsTempStickVersionLabel.setBounds( 15, 150, 107, 14 );
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
    individualPanel.add( individualsTempStickVersionLabel );
    isPanelInitiated = true;
  }

  /**
   * Erzeuge alle Elemente des Panels und setze config in die Elemente Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   * @param conf
   */
  public void prepareConfigPanel( SPX42Config conf )
  {
    currentConfig = conf;
    initPanel();
    setFirmwareLabel( firmwareLabelText );
    setSerialNumber( serialNumber );
    setGlobalChangeListener( mainCommGUI );
    setAllConfigPanlelsEnabled( areAllConfigPanelsEnabled );
    setLanguageStrings( currentConfig );
    setDisplayPropertys();
    setSetpoint();
    setUnits();
    setIndividuals( isIndividualsEnabled );
    if( conf.isInitialized() )
    {
      setConfigValues();
    }
  }

  /**
   * Alle Resourcen des Panels freigeben! Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   */
  public void releaseConfig()
  {
    this.removeAll();
    isPanelInitiated = false;
    currentConfig = null;
  }

  /**
   * Setze alle Kon´figurationspanels auf "Enabled" wenn möglich Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.07.2012
   * @param en
   */
  public void setAllConfigPanlelsEnabled( boolean en )
  {
    areAllConfigPanelsEnabled = en;
    if( !isPanelInitiated ) return;
    setDecoPanelEnabled( en );
    setDisplayPanelEnabled( en );
    setUnitsPanelEnabled( en );
    setSetpointPanel( en );
    // nur, wenn eine gültige Konfiguration gelesen wurde
    if( currentConfig.isInitialized() )
    {
      // Gibt es eine Lizenz für Custom Config?
      if( currentConfig.getCustomEnabled() == 1 )
      {
        setIndividualsPanelEnabled( true );
      }
      else
      {
        setIndividualsPanelEnabled( false );
      }
    }
    else
    {
      // Keine Config gelesen!
      setIndividualsPanelEnabled( false );
    }
    if( currentConfig.hasFahrenheidBug() )
    {
      unitsTemperatureComboBox.setBackground( new Color( 0xffafaf ) );
      unitsTemperatureComboBox.setEnabled( false );
    }
  }

  private void setConfigValues()
  {
    if( ( serialNumber != null ) && ( !serialNumber.equals( "0" ) ) )
    {
      setSerialNumber( serialNumber );
    }
    setDecoGradientenPreset( currentPreset );
    setDecoGradient();
  }

  /**
   * Deco Gradienten setzen (aus currentConfig) Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   */
  public void setDecoGradient()
  {
    if( !isPanelInitiated ) return;
    decoGradientenLowSpinner.setValue( currentConfig.getDecoGfLow() );
    decoGradientenHighSpinner.setValue( currentConfig.getDecoGfHigh() );
    decoGradientenPresetComboBox.setSelectedIndex( currentConfig.getDecoGfPreset() );
    if( currentConfig.getLastStop() == 3 )
    {
      decoLastStopComboBox.setSelectedIndex( 0 );
    }
    else
    {
      decoLastStopComboBox.setSelectedIndex( 1 );
    }
    decoDynGradientsCheckBox.setSelected( currentConfig.isDynGradientsEnable() );
    decoDeepStopCheckBox.setSelected( currentConfig.isDeepStopEnable() );
  }

  /**
   * Setze Preset nach Änderung in Config korrekt Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   * @param currentPreset
   */
  public void setDecoGradientenPreset( int currentPreset )
  {
    this.currentPreset = currentPreset;
    if( !isPanelInitiated ) return;
    if( decoGradientenPresetComboBox.getSelectedIndex() != currentPreset )
    {
      decoGradientenPresetComboBox.setSelectedIndex( currentPreset );
    }
  }

  /**
   * Setze die Spinner auf den Korrekten Wert nach Änderung in config Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   */
  public void setDecoGradientenSpinner()
  {
    if( !isPanelInitiated ) return;
    decoGradientenHighSpinner.setValue( currentConfig.getDecoGfHigh() );
    decoGradientenLowSpinner.setValue( currentConfig.getDecoGfLow() );
  }

  /**
   * 
   * Erlaube das DECO-Panel
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * Stand: 06.12.2013
   * 
   * @param en
   */
  public void setDecoPanelEnabled( boolean en )
  {
    if( !isPanelInitiated ) return;
    for( Component cp : decompressionPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    decompressionPanel.setEnabled( en );
  }

  /**
   * 
   * Das Panel für Displayeinstellungen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * Stand: 06.12.2013
   * 
   * @param en
   */
  public void setDisplayPanelEnabled( boolean en )
  {
    if( !isPanelInitiated ) return;
    for( Component cp : displayPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    displayPanel.setEnabled( en );
  }

  /**
   * Setze Displayeinstelungen (aus currentConfig ) Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012 TODO
   */
  public void setDisplayPropertys()
  {
    if( !isPanelInitiated ) return;
    displayBrightnessComboBox.setSelectedIndex( currentConfig.getDisplayBrightness() );
    displayOrientationComboBox.setSelectedIndex( currentConfig.getDisplayOrientation() );
  }

  /**
   * Firmware Label anzeigen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   * @param cmd
   */
  public void setFirmwareLabel( String cmd )
  {
    this.firmwareLabelText = cmd;
    if( !isPanelInitiated ) return;
    firmwareVersionValueLabel.setText( cmd );
  }

  /**
   * Alle Listener für Aktionen in diesem Objekt auf das Mainobjekt kenken Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 22.04.2012
   * @param mainCommGUI
   */
  public void setGlobalChangeListener( MainCommGUI mainCommGUI )
  {
    this.mainCommGUI = mainCommGUI;
    if( !isPanelInitiated ) return;
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
    individualsTempStickVersionComboBox.addActionListener( mainCommGUI );
    individualsTempStickVersionComboBox.addMouseMotionListener( mainCommGUI );
  }

  /**
   * Setze Individuelle Einstelungenin der Anzeige (aus currentConfig ) Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   * @param isEnabled
   */
  public void setIndividuals( boolean isEnabled )
  {
    if( !isPanelInitiated ) return;
    setIndividualsPanelEnabled( isEnabled );
    if( !isEnabled ) return;
    // Sensormode eintragen
    if( currentConfig.getSensorsOn() == 1 )
    {
      individualsSensorsOnCheckbox.setSelected( true );
    }
    else
    {
      individualsSensorsOnCheckbox.setSelected( false );
    }
    // Passiver MCCR Mode
    if( currentConfig.getPscrModeOn() == 1 )
    {
      individualsPscrModeOnCheckbox.setSelected( true );
    }
    else
    {
      individualsPscrModeOnCheckbox.setSelected( false );
    }
    // Sensor Anzahl Warning
    individualsSensorWarnComboBox.setSelectedIndex( currentConfig.getSensorsCount() );
    // akustische warnuingen
    if( currentConfig.getSoundOn() == 1 )
    {
      individualsWarningsOnCheckBox.setSelected( true );
    }
    else
    {
      individualsWarningsOnCheckBox.setSelected( false );
    }
    // Loginterval
    individualsLogintervalComboBox.setSelectedIndex( currentConfig.getLogInterval() );
    if( currentConfig.hasSixValuesIndividual() )
    {
      individualsTempStickVersionComboBox.setSelectedIndex( currentConfig.getTempStickVer() );
    }
    else
    {
      individualsTempStickVersionComboBox.setVisible( false );
      individualsTempStickVersionLabel.setVisible( false );
    }
  }

  public void setIndividualsPanelEnabled( boolean en )
  {
    this.isIndividualsEnabled = en;
    if( !isPanelInitiated ) return;
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

  public int setLanguageStrings( SPX42Config currentConfig )
  {
    String[] entrys;
    DefaultComboBoxModel<String> portBoxModel;
    if( !isPanelInitiated ) return( -1 );
    try
    {
      // //////////////////////////////////////////////////////////////////////
      // Tabbed Pane config
      serialNumberLabel.setText( LangStrings.getString( "spx42ConfigPanel.serialNumberLabel.text" ) );
      readSPX42ConfigButton.setText( LangStrings.getString( "spx42ConfigPanel.readSPX42ConfigButton.text" ) );
      readSPX42ConfigButton.setToolTipText( LangStrings.getString( "spx42ConfigPanel.readSPX42ConfigButton.tooltiptext" ) );
      writeSPX42ConfigButton.setText( LangStrings.getString( "spx42ConfigPanel.writeSPX42ConfigButton.text" ) );
      writeSPX42ConfigButton.setToolTipText( LangStrings.getString( "spx42ConfigPanel.writeSPX42ConfigButton.tooltiptext" ) );
      firmwareVersionLabel.setText( LangStrings.getString( "spx42ConfigPanel.firmwareVersionLabel.text" ) );
      // DECO
      ( ( TitledBorder )( decompressionPanel.getBorder() ) ).setTitle( LangStrings.getString( "spx42ConfigPanel.decoTitleBorder.text" ) );
      decoGradientenPresetComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.decoGradientenPresetComboBox.tooltiptext" ) );
      decoGradientsHighLabel.setText( LangStrings.getString( "spx42ConfigPanel.decoGradientsHighLabel.text" ) );
      decoGradientsLowLabel.setText( LangStrings.getString( "spx42ConfigPanel.decoGradientsLowLabel.text" ) );
      decoGradientenLowSpinner.setToolTipText( LangStrings.getString( "spx42ConfigPanel.decoGradientenLowSpinner.tooltiptext" ) );
      decoGradientenHighSpinner.setToolTipText( LangStrings.getString( "spx42ConfigPanel.decoGradientenHighSpinner.tooltiptext" ) );
      decoLaststopLabel.setText( LangStrings.getString( "spx42ConfigPanel.decoLaststopLabel.text" ) );
      decoLastStopComboBox.removeAllItems();
      entrys = new String[]
      { LangStrings.getString( "spx42ConfigPanel.decoLastStopComboBox.3m.text" ), LangStrings.getString( "spx42ConfigPanel.decoLastStopComboBox.6m.text" ) };
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      decoLastStopComboBox.setModel( portBoxModel );
      decoLastStopComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.decoLastStopComboBox.tooltipttext" ) );
      decoGradientenPresetComboBox.removeAllItems();
      entrys = new String[]
      { LangStrings.getString( "spx42ConfigPanel.decoDyngradientsLabel.vconservative.text" ), LangStrings.getString( "spx42ConfigPanel.decoDyngradientsLabel.conservative.text" ),
          LangStrings.getString( "spx42ConfigPanel.decoDyngradientsLabel.moderate.text" ), LangStrings.getString( "spx42ConfigPanel.decoDyngradientsLabel.aggressive.text" ),
          LangStrings.getString( "spx42ConfigPanel.decoDyngradientsLabel.vaggressive.text" ), LangStrings.getString( "spx42ConfigPanel.decoDyngradientsLabel.custom.text" ) };
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      decoGradientenPresetComboBox.setModel( portBoxModel );
      decoDyngradientsLabel.setText( LangStrings.getString( "spx42ConfigPanel.decoDyngradientsLabel.text" ) );
      decoDynGradientsCheckBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.decoDynGradientsCheckBox.tooltiptext" ) );
      decoDeepstopsLabel.setText( LangStrings.getString( "spx42ConfigPanel.decoDeepstopsLabel.text" ) );
      decoDeepStopCheckBox.setText( LangStrings.getString( "spx42ConfigPanel.decoDeepStopCheckBox.text" ) );
      decoDynGradientsCheckBox.setText( LangStrings.getString( "spx42ConfigPanel.decoDynGradientsCheckBox.text" ) );
      decoDeepStopCheckBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.decoDeepStopCheckBox.tooltiptext" ) );
      // SETPOINT
      ( ( TitledBorder )( setpointPanel.getBorder() ) ).setTitle( LangStrings.getString( "spx42ConfigPanel.setpointPanel.text" ) );
      lblSetpointAutosetpoint.setText( LangStrings.getString( "spx42ConfigPanel.lblSetpointAutosetpoint.text" ) );
      autoSetpointComboBox.removeAllItems();
      // ist neuere Firmware autosetpoint bei 6 Metern
      if( currentConfig.isSixMetersAutoSetpoint() )
      {
        // 1. Autosetpoint bei 6 Metern
        entrys = new String[]
        { LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.off.text" ), LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.6m.text" ),
            LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.10m.text" ), LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.15m.text" ),
            LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.20m.text" ) };
      }
      else
      {
        // 1. Autosetpoint bei 5 Metern
        entrys = new String[]
        { LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.off.text" ), LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.5m.text" ),
            LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.10m.text" ), LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.15m.text" ),
            LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.20m.text" ) };
      }
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      autoSetpointComboBox.setModel( portBoxModel );
      autoSetpointComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.autoSetpointComboBox.tooltiptext" ) );
      lblSetpointHighsetpoint.setText( LangStrings.getString( "spx42ConfigPanel.lblSetpointHighsetpoint.text" ) );
      highSetpointComboBox.removeAllItems();
      entrys = new String[]
      { LangStrings.getString( "spx42ConfigPanel.highSetpointComboBox.10.text" ), LangStrings.getString( "spx42ConfigPanel.highSetpointComboBox.11.text" ),
          LangStrings.getString( "spx42ConfigPanel.highSetpointComboBox.12.text" ), LangStrings.getString( "spx42ConfigPanel.highSetpointComboBox.13.text" ),
          LangStrings.getString( "spx42ConfigPanel.highSetpointComboBox.14.text" ) };
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      highSetpointComboBox.setModel( portBoxModel );
      highSetpointComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.highSetpointComboBox.tooltiptext" ) );
      // DISPLAY
      ( ( TitledBorder )( displayPanel.getBorder() ) ).setTitle( LangStrings.getString( "spx42ConfigPanel.displayPanel.text" ) );
      lblDisplayBrightness.setText( LangStrings.getString( "spx42ConfigPanel.lblDisplayBrightness.text" ) );
      displayBrightnessComboBox.removeAllItems();
      // Abhängig von der Firmware
      if( ( currentConfig == null ) || ( !currentConfig.isNewerDisplayBrightness() ) )
      {
        entrys = new String[]
        { LangStrings.getString( "spx42ConfigPanel.displayBrightnessComboBox.10.text" ), LangStrings.getString( "spx42ConfigPanel.displayBrightnessComboBox.50.text" ),
            LangStrings.getString( "spx42ConfigPanel.displayBrightnessComboBox.100.text" ) };
        portBoxModel = new DefaultComboBoxModel<String>( entrys );
      }
      else
      {
        entrys = new String[]
        { LangStrings.getString( "spx42ConfigPanel.displayBrightnessComboBox.20.text" ), LangStrings.getString( "spx42ConfigPanel.displayBrightnessComboBox.40.text" ),
            LangStrings.getString( "spx42ConfigPanel.displayBrightnessComboBox.60.text" ), LangStrings.getString( "spx42ConfigPanel.displayBrightnessComboBox.80.text" ),
            LangStrings.getString( "spx42ConfigPanel.displayBrightnessComboBox.100.text" ) };
        portBoxModel = new DefaultComboBoxModel<String>( entrys );
      }
      displayBrightnessComboBox.setModel( portBoxModel );
      displayBrightnessComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.displayBrightnessComboBox.tooltiptext" ) );
      lblDisplayOrientation.setText( LangStrings.getString( "spx42ConfigPanel.lblDisplayOrientation.text" ) );
      displayOrientationComboBox.removeAllItems();
      entrys = new String[]
      { LangStrings.getString( "spx42ConfigPanel.displayOrientationComboBox.landscape.text" ),
          LangStrings.getString( "spx42ConfigPanel.displayOrientationComboBox.landscape180.text" ) };
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      displayOrientationComboBox.setModel( portBoxModel );
      displayOrientationComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.displayOrientationComboBox.tooltiptext" ) );
      // UNITS
      ( ( TitledBorder )( unitsPanel.getBorder() ) ).setTitle( LangStrings.getString( "spx42ConfigPanel.unitsPanel.text" ) );
      lblUnitsTemperature.setText( LangStrings.getString( "spx42ConfigPanel.lblUnitsTemperature.text" ) );
      unitsTemperatureComboBox.removeAllItems();
      entrys = new String[]
      { LangStrings.getString( "spx42ConfigPanel.unitsTemperatureComboBox.celsius.text" ), LangStrings.getString( "spx42ConfigPanel.unitsTemperatureComboBox.fahrenheit.text" ) };
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      unitsTemperatureComboBox.setModel( portBoxModel );
      unitsTemperatureComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.unitsTemperatureComboBox.tooltiptext" ) );
      lblUnitsDepth.setText( LangStrings.getString( "spx42ConfigPanel.lblUnitsDepth.text" ) );
      unitsDepthComboBox.removeAllItems();
      entrys = new String[]
      { LangStrings.getString( "spx42ConfigPanel.unitsDepthComboBox.metrical.text" ), LangStrings.getString( "spx42ConfigPanel.unitsDepthComboBox.imperial.text" ) };
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      unitsDepthComboBox.setModel( portBoxModel );
      unitsDepthComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.unitsDepthComboBox.tooltiptext" ) );
      lblUnitsSalinity.setText( LangStrings.getString( "spx42ConfigPanel.lblUnitsSalinity.text" ) );
      unitsSalnityComboBox.removeAllItems();
      entrys = new String[]
      { LangStrings.getString( "spx42ConfigPanel.unitsSalnityComboBox.saltwater.text" ), LangStrings.getString( "spx42ConfigPanel.unitsSalnityComboBox.clearwater.text" ) };
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      unitsSalnityComboBox.setModel( portBoxModel );
      unitsSalnityComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.unitsSalnityComboBox.tooltiptext" ) );
      // INDIVIDUALS
      ( ( TitledBorder )( individualPanel.getBorder() ) ).setTitle( LangStrings.getString( "spx42ConfigPanel.individualPanel.text" ) );
      lblSenormode.setText( LangStrings.getString( "spx42ConfigPanel.lblSenormode.text" ) );
      individualsSensorsOnCheckbox.setText( LangStrings.getString( "spx42ConfigPanel.chIndividualsSensorsOnCheckbox.text" ) );
      individualsSensorsOnCheckbox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.chIndividualsSensorsOnCheckbox.tooltiptext" ) );
      lblIndividualsPscrMode.setText( LangStrings.getString( "spx42ConfigPanel.lblIndividualsPscrMode.text" ) );
      individualsPscrModeOnCheckbox.setText( LangStrings.getString( "spx42ConfigPanel.IndividualsPscrModoOnCheckbox.text" ) );
      individualsPscrModeOnCheckbox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.IndividualsPscrModoOnCheckbox.tooltiptext" ) );
      lblSensorwarnings.setText( LangStrings.getString( "spx42ConfigPanel.lblSensorwarnings.text" ) );
      individualsSensorWarnComboBox.removeAllItems();
      entrys = new String[]
      { LangStrings.getString( "spx42ConfigPanel.individualsSensorwarnComboBox.1.text" ), LangStrings.getString( "spx42ConfigPanel.individualsSensorwarnComboBox.2.text" ),
          LangStrings.getString( "spx42ConfigPanel.individualsSensorwarnComboBox.3.text" ) };
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      individualsSensorWarnComboBox.setModel( portBoxModel );
      individualsSensorWarnComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.individualsSensorwarnComboBox.tooltiptext" ) );
      individualsAcusticWarningsLabel.setText( LangStrings.getString( "spx42ConfigPanel.individualsAcusticWarningsLabel.text" ) );
      individualsWarningsOnCheckBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.individualsWarningsOnCheckBox.tooltiptext" ) );
      individualsLogintervalLabel.setText( LangStrings.getString( "spx42ConfigPanel.individualsLogintervalLabel.text" ) );
      individualsLogintervalComboBox.removeAllItems();
      entrys = new String[]
      { LangStrings.getString( "spx42ConfigPanel.individualsLogintervalComboBox.10s.text" ), LangStrings.getString( "spx42ConfigPanel.individualsLogintervalComboBox.20s.text" ),
          LangStrings.getString( "spx42ConfigPanel.individualsLogintervalComboBox.60s.text" ) };
      portBoxModel = new DefaultComboBoxModel<String>( entrys );
      individualsLogintervalComboBox.setModel( portBoxModel );
      individualsLogintervalComboBox.setToolTipText( LangStrings.getString( "spx42ConfigPanel.individualsLogintervalComboBox.tooltiptext" ) );
      // Abhängig von der Firmware
      if( ( currentConfig == null ) || ( currentConfig.hasSixValuesIndividual() ) )
      {
        entrys = new String[]
        { LangStrings.getString( "spx42ConfigPanel.individualsTempStickVersionComboBox.T1" ), LangStrings.getString( "spx42ConfigPanel.individualsTempStickVersionComboBox.T2" ),
            LangStrings.getString( "spx42ConfigPanel.individualsTempStickVersionComboBox.T3" ) };
        portBoxModel = new DefaultComboBoxModel<String>( entrys );
        individualsTempStickVersionComboBox.setModel( portBoxModel );
        individualsTempStickVersionLabel.setToolTipText( LangStrings.getString( "spx42ConfigPanel.individualsTempStickVersionLabel.tooltiptext" ) );
      }
      individualsNotLicensedLabel.setToolTipText( LangStrings.getString( "spx42ConfigPanel.individualsNotLicensedLabel.tooltiptext" ) );
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

  /**
   * Seriennummer anzeigen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   * @param cmd
   */
  public void setSerialNumber( String cmd )
  {
    this.serialNumber = cmd;
    if( !isPanelInitiated ) return;
    serialNumberText.setText( cmd );
  }

  /**
   * Setpoint Einstellungen übernehmen (aus currentConfig ) Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   */
  public void setSetpoint()
  {
    if( !isPanelInitiated ) return;
    autoSetpointComboBox.setSelectedIndex( currentConfig.getAutoSetpoint() );
    highSetpointComboBox.setSelectedIndex( currentConfig.getMaxSetpoint() );
  }

  public void setSetpointPanel( boolean en )
  {
    if( !isPanelInitiated ) return;
    for( Component cp : setpointPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    setpointPanel.setEnabled( en );
  }

  /**
   * Setze die Einheit für die Tiefe Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   * @param depthUnit
   *          Metrisch oder Imperial
   */
  public void setUnitDepth( int depthUnit )
  {
    if( !isPanelInitiated ) return;
    if( currentConfig.hasFahrenheidBug() )
    {
      if( depthUnit == 0 )
      {
        unitsTemperatureComboBox.setSelectedIndex( 0 );
      }
      else
      {
        unitsTemperatureComboBox.setSelectedIndex( 1 );
      }
    }
  }

  /**
   * Einheiten setzen (aus currentConfig ) Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 02.09.2012
   */
  public void setUnits()
  {
    if( !isPanelInitiated ) return;
    unitsTemperatureComboBox.setSelectedIndex( currentConfig.getUnitTemperature() );
    unitsDepthComboBox.setSelectedIndex( currentConfig.getUnitDepth() );
    unitsSalnityComboBox.setSelectedIndex( currentConfig.getUnitSalnity() );
  }

  public void setUnitsPanelEnabled( boolean en )
  {
    if( !isPanelInitiated ) return;
    for( Component cp : unitsPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    unitsPanel.setEnabled( en );
  }
}
