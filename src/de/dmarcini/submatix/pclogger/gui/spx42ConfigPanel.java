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

import de.dmarcini.submatix.pclogger.utils.SPX42Config;

//@formatter:off
public class spx42ConfigPanel extends JPanel
{                                                                                                                                  /**
   * 
   */
  private static final long serialVersionUID     = 1L;
  @SuppressWarnings( "unused" )
  private Logger            LOGGER               = null;
  private boolean           isPanelInitiated;
  private boolean           areAllConfigPanelsEnabled;
  private SPX42Config       currentConfig        = null;
  private MainCommGUI       mainCommGUI          = null;
  private String            serialNumber         = "0";
  private int               currentPreset        = -1;
  private String            firmwareLabelText    = "-";
  private boolean           isIndividualsEnabled = false;
  private ResourceBundle    stringsBundle        = null;
  // @formatter:on
  private JLabel            serialNumberText;
  private JLabel            firmwareVersionValueLabel;
  private JSpinner          decoGradientenLowSpinner;
  private JComboBox         decoGradientenPresetComboBox;
  private JComboBox         decoLastStopComboBox;
  private JCheckBox         decoDynGradientsCheckBox;
  private JCheckBox         decoDeepStopCheckBox;
  private JComboBox         autoSetpointComboBox;
  private JComboBox         highSetpointComboBox;
  private JComboBox         displayBrightnessComboBox;
  private JComboBox         displayOrientationComboBox;
  private JComboBox         unitsTemperatureComboBox;
  private JComboBox         unitsDepthComboBox;
  private JComboBox         unitsSalnityComboBox;
  private JPanel            individualPanel;
  private JCheckBox         individualsSensorsOnCheckbox;
  private JCheckBox         individualsPscrModeOnCheckbox;
  private JComboBox         individualsSensorWarnComboBox;
  private JCheckBox         individualsWarningsOnCheckBox;
  private JComboBox         individualsLogintervalComboBox;
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

  /**
   * Create the panel.
   */
  @SuppressWarnings( "unused" )
  private spx42ConfigPanel()
  {
    setPreferredSize( new Dimension( 796, 504 ) );
    currentConfig = null;
    areAllConfigPanelsEnabled = false;
    initPanel();
  }

  public spx42ConfigPanel( Logger lg )
  {
    LOGGER = lg;
    isPanelInitiated = false;
    currentConfig = null;
    areAllConfigPanelsEnabled = false;
    // initPanel();
  }

  public JSpinner getDecoGradientenHighSpinner()
  {
    if( !isPanelInitiated ) return( null );
    return( decoGradientenHighSpinner );
  }

  public JSpinner getDecoGradientenLowSpinner()
  {
    if( !isPanelInitiated ) return( null );
    return( decoGradientenLowSpinner );
  }

  private void initPanel()
  {
    readSPX42ConfigButton = new JButton( "READ" );
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
    writeSPX42ConfigButton = new JButton( "WRITE" );
    writeSPX42ConfigButton.setHorizontalAlignment( SwingConstants.LEFT );
    writeSPX42ConfigButton.setIconTextGap( 15 );
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
    isPanelInitiated = true;
  }

  /**
   * 
   * Erzeuge alle Elemente des Panels und setze config in die Elemente
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
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
    setLanguageStrings( stringsBundle );
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
   * 
   * Alle Resourcen des Panels freigeben!
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   */
  public void releaseConfig()
  {
    this.removeAll();
    isPanelInitiated = false;
    currentConfig = null;
  }

  /**
   * 
   * Setze alle Kon´figurationspanels auf "Enabled" wenn möglich
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.07.2012
   * @param en
   * 
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
    if( currentConfig.isBuggyFirmware() )
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
   * 
   * Deco Gradienten setzen (aus currentConfig)
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
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
   * 
   * Setze Preset nach Änderung in Config korrekt
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
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
   * 
   * Setze dei Spinner auf den Korrekten Wert nach Änderung in config
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   */
  public void setDecoGradientenSpinner()
  {
    if( !isPanelInitiated ) return;
    decoGradientenHighSpinner.setValue( currentConfig.getDecoGfHigh() );
    decoGradientenLowSpinner.setValue( currentConfig.getDecoGfLow() );
  }

  public void setDecoPanelEnabled( boolean en )
  {
    if( !isPanelInitiated ) return;
    for( Component cp : decompressionPanel.getComponents() )
    {
      cp.setEnabled( en );
    }
    decompressionPanel.setEnabled( en );
  }

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
   * 
   * Setze Displayeinstelungen (aus currentConfig )
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012 TODO
   */
  public void setDisplayPropertys()
  {
    if( !isPanelInitiated ) return;
    displayBrightnessComboBox.setSelectedIndex( currentConfig.getDisplayBrightness() );
    displayOrientationComboBox.setSelectedIndex( currentConfig.getDisplayOrientation() );
  }

  /**
   * 
   * Firmware Label anzeigen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   * @param cmd
   */
  public void setFirmwareLabel( String cmd )
  {
    this.firmwareLabelText = cmd;
    if( !isPanelInitiated ) return;
    firmwareVersionValueLabel.setText( cmd );
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
  }

  /**
   * 
   * Setze Individuelle Einstelungenin der Anzeige (aus currentConfig )
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
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

  public int setLanguageStrings( ResourceBundle stringsBundle )
  {
    String[] entrys;
    DefaultComboBoxModel portBoxModel;
    this.stringsBundle = stringsBundle;
    if( !isPanelInitiated ) return( -1 );
    if( stringsBundle == null ) return( -1 );
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
      { stringsBundle.getString( "spx42ConfigPanel.unitsTemperatureComboBox.celsius.text" ), stringsBundle.getString( "spx42ConfigPanel.unitsTemperatureComboBox.fahrenheit.text" ) };
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

  /**
   * 
   * Seriennummer anzeigen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   * @param cmd
   */
  public void setSerialNumber( String cmd )
  {
    this.serialNumber = cmd;
    if( !isPanelInitiated ) return;
    serialNumberText.setText( cmd );
  }

  /**
   * 
   * Setpoint Einstellungen übernehmen (aus currentConfig )
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
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
   * 
   * Setze die Einheit für die Tiefe
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   * @param depthUnit
   *          Metrisch oder Imperial
   */
  public void setUnitDepth( int depthUnit )
  {
    if( !isPanelInitiated ) return;
    if( currentConfig.isBuggyFirmware() )
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
   * 
   * Einheiten setzen (aus currentConfig )
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
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
