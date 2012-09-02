package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;

//@formatter:off
public class spx42GaslistEditPanel extends JPanel
{  //
  public final HashMap<Integer, JSpinner>  o2SpinnerMap        = new HashMap<Integer, JSpinner>();
  public final HashMap<Integer, JSpinner>  heSpinnerMap        = new HashMap<Integer, JSpinner>();
  public final HashMap<Integer, JLabel>    gasLblMap           = new HashMap<Integer, JLabel>();
  public final HashMap<Integer, JCheckBox> bailoutMap          = new HashMap<Integer, JCheckBox>();
  public final HashMap<Integer, JCheckBox> diluent1Map         = new HashMap<Integer, JCheckBox>();
  public final HashMap<Integer, JCheckBox> diluent2Map         = new HashMap<Integer, JCheckBox>();
  @SuppressWarnings( "unused" )
  private final Logger                     LOGGER              = null;
  private int                              licenseState        = -1;
  private int                              customConfig        = -1;
  private boolean                          isPanelInitiated    = false;
  private ResourceBundle                   stringsBundle       = null;
  private MainCommGUI                      mainCommGUI         = null;
  private boolean                          isElementsGasMatrixEnabled = false;
  // @formatter:on
  /**
   * 
   */
  private static final long                serialVersionUID           = 1L;
  private JLabel                           gasLabel_00;
  private JLabel                           gasLabel_01;
  private JLabel                           gasLabel_03;
  private JSpinner                         gasO2Spinner_00;
  private JSpinner                         gasHESpinner_00;
  private JCheckBox                        diluent1Checkbox_02;
  private JCheckBox                        diluent1Checkbox_00;
  private JCheckBox                        diluent1Checkbox_01;
  private JCheckBox                        diluent1Checkbox_03;
  private JCheckBox                        diluent1Checkbox_04;
  private JCheckBox                        diluent1Checkbox_05;
  private JCheckBox                        diluent1Checkbox_06;
  private JCheckBox                        diluent1Checkbox_07;
  private JCheckBox                        diluent2Checkbox_00;
  private JCheckBox                        diluent2Checkbox_01;
  private JCheckBox                        diluent2Checkbox_02;
  private JCheckBox                        diluent2Checkbox_03;
  private JCheckBox                        diluent2Checkbox_04;
  private JCheckBox                        diluent2Checkbox_05;
  private JCheckBox                        diluent2Checkbox_06;
  private JCheckBox                        diluent2Checkbox_07;
  private JCheckBox                        bailoutCheckbox_00;
  private JCheckBox                        bailoutCheckbox_01;
  private JCheckBox                        bailoutCheckbox_02;
  private JCheckBox                        bailoutCheckbox_03;
  private JCheckBox                        bailoutCheckbox_04;
  private JCheckBox                        bailoutCheckbox_05;
  private JCheckBox                        bailoutCheckbox_06;
  private JCheckBox                        bailoutCheckbox_07;
  private JLabel                           gasNameLabel_00;
  private JLabel                           gasNameLabel_01;
  private JLabel                           gasNameLabel_02;
  private JLabel                           gasNameLabel_04;
  private JLabel                           gasNameLabel_05;
  private JLabel                           gasNameLabel_06;
  private JLabel                           gasNameLabel_07;
  private final ButtonGroup                duluent1ButtonGroup        = new ButtonGroup();
  private final ButtonGroup                diluent2ButtonGroup        = new ButtonGroup();
  private JLabel                           licenseStatusLabel;
  private JButton                          gasReadFromSPXButton;
  private JButton                          gasWriteToSPXButton;
  private JSpinner                         gasO2Spinner_01;
  private JSpinner                         gasO2Spinner_02;
  private JSpinner                         gasO2Spinner_03;
  private JSpinner                         gasO2Spinner_04;
  private JSpinner                         gasO2Spinner_05;
  private JSpinner                         gasO2Spinner_06;
  private JSpinner                         gasO2Spinner_07;
  private JSpinner                         gasHESpinner_01;
  private JSpinner                         gasHESpinner_03;
  private JSpinner                         gasHESpinner_04;
  private JSpinner                         gasHESpinner_05;
  private JSpinner                         gasHESpinner_06;
  private JSpinner                         gasHESpinner_07;
  private JSpinner                         gasHESpinner_02;
  private JLabel                           gasNameLabel_03;
  private JLabel                           gasLabel_02;
  private JLabel                           gasLabel_04;
  private JLabel                           gasLabel_05;
  private JLabel                           gasLabel_06;
  private JLabel                           gasLabel_07;
  private JButton                          readGasPresetButton;
  private JButton                          writeGasPresetButton;
  private JComboBox                        customPresetComboBox;
  private JLabel                           userPresetLabel;
  private JPanel                           gasMatrixPanel;

  /**
   * 
   * gesperrte Version
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 21.04.2012
   */
  @SuppressWarnings( "unused" )
  private spx42GaslistEditPanel()
  {
    setPreferredSize( new Dimension( 796, 504 ) );
    isPanelInitiated = false;
    initPanel();
    initGasObjectMaps();
  }

  /**
   * 
   * Mache das Panel!
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 21.04.2012
   * @param logger
   */
  public spx42GaslistEditPanel( Logger logger )
  {
    if( logger == null )
    {
      throw new NullPointerException( "no logger in constructor!" );
    }
    isPanelInitiated = false;
    // initPanel();
    // initGasObjectMaps();
  }

  /**
   * 
   * Interne Funktion erzeugt die GUI
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   */
  private void initPanel()
  {
    setLayout( null );
    gasReadFromSPXButton = new JButton( "READ" );
    gasReadFromSPXButton.setIcon( new ImageIcon( spx42GaslistEditPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Download.png" ) ) );
    gasReadFromSPXButton.setSize( new Dimension( 199, 60 ) );
    gasReadFromSPXButton.setPreferredSize( new Dimension( 180, 40 ) );
    gasReadFromSPXButton.setMaximumSize( new Dimension( 160, 40 ) );
    gasReadFromSPXButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    gasReadFromSPXButton.setForeground( new Color( 0, 100, 0 ) );
    gasReadFromSPXButton.setBackground( new Color( 152, 251, 152 ) );
    gasReadFromSPXButton.setActionCommand( "read_gaslist" );
    gasReadFromSPXButton.setBounds( 10, 421, 199, 60 );
    add( gasReadFromSPXButton );
    gasWriteToSPXButton = new JButton( "WRITE" );
    gasWriteToSPXButton.setIcon( new ImageIcon( spx42GaslistEditPanel.class.getResource( "/de/dmarcini/submatix/pclogger/res/Upload.png" ) ) );
    gasWriteToSPXButton.setForeground( Color.RED );
    gasWriteToSPXButton.setBackground( new Color( 255, 192, 203 ) );
    gasWriteToSPXButton.setActionCommand( "write_gaslist" );
    gasWriteToSPXButton.setBounds( 346, 421, 217, 60 );
    add( gasWriteToSPXButton );
    gasMatrixPanel = new JPanel();
    gasMatrixPanel.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );
    gasMatrixPanel.setBounds( 10, 11, 553, 368 );
    add( gasMatrixPanel );
    GridBagLayout gbl_gasMatrixPanel = new GridBagLayout();
    gbl_gasMatrixPanel.columnWidths = new int[]
    { 16, 70, 0, 50, 0, 50, 0, 55, 55, 55, 90, 0 };
    gbl_gasMatrixPanel.rowHeights = new int[]
    { 37, 40, 40, 40, 40, 40, 40, 40, 40, 0 };
    gbl_gasMatrixPanel.columnWeights = new double[]
    { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_gasMatrixPanel.rowWeights = new double[]
    { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gasMatrixPanel.setLayout( gbl_gasMatrixPanel );
    JLabel label = new JLabel( "O2" );
    label.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
    GridBagConstraints gbc_label = new GridBagConstraints();
    gbc_label.anchor = GridBagConstraints.SOUTH;
    gbc_label.insets = new Insets( 0, 0, 5, 5 );
    gbc_label.gridx = 3;
    gbc_label.gridy = 0;
    gasMatrixPanel.add( label, gbc_label );
    JLabel label_1 = new JLabel( "HE" );
    label_1.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
    GridBagConstraints gbc_label_1 = new GridBagConstraints();
    gbc_label_1.anchor = GridBagConstraints.SOUTH;
    gbc_label_1.insets = new Insets( 0, 0, 5, 5 );
    gbc_label_1.gridx = 5;
    gbc_label_1.gridy = 0;
    gasMatrixPanel.add( label_1, gbc_label_1 );
    gasLabel_00 = new JLabel( "GAS00" );
    GridBagConstraints gbc_gasLabel_00 = new GridBagConstraints();
    gbc_gasLabel_00.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_00.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_00.gridx = 1;
    gbc_gasLabel_00.gridy = 1;
    gasMatrixPanel.add( gasLabel_00, gbc_gasLabel_00 );
    gasO2Spinner_00 = new JSpinner();
    gasO2Spinner_00.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_00.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_00 = new GridBagConstraints();
    gbc_gasO2Spinner_00.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_00.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_00.gridx = 3;
    gbc_gasO2Spinner_00.gridy = 1;
    gasMatrixPanel.add( gasO2Spinner_00, gbc_gasO2Spinner_00 );
    gasHESpinner_00 = new JSpinner();
    gasHESpinner_00.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_00 = new GridBagConstraints();
    gbc_gasHESpinner_00.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_00.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_00.gridx = 5;
    gbc_gasHESpinner_00.gridy = 1;
    gasMatrixPanel.add( gasHESpinner_00, gbc_gasHESpinner_00 );
    diluent1Checkbox_00 = new JCheckBox( "D1" );
    duluent1ButtonGroup.add( diluent1Checkbox_00 );
    GridBagConstraints gbc_diluent1Checkbox_00 = new GridBagConstraints();
    gbc_diluent1Checkbox_00.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_00.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent1Checkbox_00.gridx = 7;
    gbc_diluent1Checkbox_00.gridy = 1;
    gasMatrixPanel.add( diluent1Checkbox_00, gbc_diluent1Checkbox_00 );
    diluent2Checkbox_00 = new JCheckBox( "D2" );
    diluent2ButtonGroup.add( diluent2Checkbox_00 );
    GridBagConstraints gbc_diluent2Checkbox_00 = new GridBagConstraints();
    gbc_diluent2Checkbox_00.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_00.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent2Checkbox_00.gridx = 8;
    gbc_diluent2Checkbox_00.gridy = 1;
    gasMatrixPanel.add( diluent2Checkbox_00, gbc_diluent2Checkbox_00 );
    bailoutCheckbox_00 = new JCheckBox( "B" );
    GridBagConstraints gbc_bailoutCheckbox_00 = new GridBagConstraints();
    gbc_bailoutCheckbox_00.insets = new Insets( 0, 0, 5, 5 );
    gbc_bailoutCheckbox_00.gridx = 9;
    gbc_bailoutCheckbox_00.gridy = 1;
    gasMatrixPanel.add( bailoutCheckbox_00, gbc_bailoutCheckbox_00 );
    gasNameLabel_00 = new JLabel( "AIR" );
    gasNameLabel_00.setForeground( new Color( 0, 0, 128 ) );
    gasNameLabel_00.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_gasNameLabel_00 = new GridBagConstraints();
    gbc_gasNameLabel_00.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_00.insets = new Insets( 0, 0, 5, 0 );
    gbc_gasNameLabel_00.gridx = 10;
    gbc_gasNameLabel_00.gridy = 1;
    gasMatrixPanel.add( gasNameLabel_00, gbc_gasNameLabel_00 );
    gasLabel_01 = new JLabel( "GAS01" );
    GridBagConstraints gbc_gasLabel_01 = new GridBagConstraints();
    gbc_gasLabel_01.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_01.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_01.gridx = 1;
    gbc_gasLabel_01.gridy = 2;
    gasMatrixPanel.add( gasLabel_01, gbc_gasLabel_01 );
    gasO2Spinner_01 = new JSpinner();
    gasO2Spinner_01.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_01.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_01 = new GridBagConstraints();
    gbc_gasO2Spinner_01.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_01.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_01.gridx = 3;
    gbc_gasO2Spinner_01.gridy = 2;
    gasMatrixPanel.add( gasO2Spinner_01, gbc_gasO2Spinner_01 );
    gasHESpinner_01 = new JSpinner();
    gasHESpinner_01.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_01 = new GridBagConstraints();
    gbc_gasHESpinner_01.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_01.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_01.gridx = 5;
    gbc_gasHESpinner_01.gridy = 2;
    gasMatrixPanel.add( gasHESpinner_01, gbc_gasHESpinner_01 );
    diluent1Checkbox_01 = new JCheckBox( "D1" );
    duluent1ButtonGroup.add( diluent1Checkbox_01 );
    GridBagConstraints gbc_diluent1Checkbox_01 = new GridBagConstraints();
    gbc_diluent1Checkbox_01.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_01.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent1Checkbox_01.gridx = 7;
    gbc_diluent1Checkbox_01.gridy = 2;
    gasMatrixPanel.add( diluent1Checkbox_01, gbc_diluent1Checkbox_01 );
    diluent2Checkbox_01 = new JCheckBox( "D2" );
    diluent2ButtonGroup.add( diluent2Checkbox_01 );
    GridBagConstraints gbc_diluent2Checkbox_01 = new GridBagConstraints();
    gbc_diluent2Checkbox_01.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_01.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent2Checkbox_01.gridx = 8;
    gbc_diluent2Checkbox_01.gridy = 2;
    gasMatrixPanel.add( diluent2Checkbox_01, gbc_diluent2Checkbox_01 );
    bailoutCheckbox_01 = new JCheckBox( "B" );
    GridBagConstraints gbc_bailoutCheckbox_01 = new GridBagConstraints();
    gbc_bailoutCheckbox_01.insets = new Insets( 0, 0, 5, 5 );
    gbc_bailoutCheckbox_01.gridx = 9;
    gbc_bailoutCheckbox_01.gridy = 2;
    gasMatrixPanel.add( bailoutCheckbox_01, gbc_bailoutCheckbox_01 );
    gasNameLabel_01 = new JLabel( "AIR" );
    gasNameLabel_01.setForeground( new Color( 0, 0, 128 ) );
    gasNameLabel_01.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_gasNameLabel_01 = new GridBagConstraints();
    gbc_gasNameLabel_01.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_01.insets = new Insets( 0, 0, 5, 0 );
    gbc_gasNameLabel_01.gridx = 10;
    gbc_gasNameLabel_01.gridy = 2;
    gasMatrixPanel.add( gasNameLabel_01, gbc_gasNameLabel_01 );
    gasLabel_02 = new JLabel( "GAS02" );
    GridBagConstraints gbc_gasLabel_02 = new GridBagConstraints();
    gbc_gasLabel_02.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_02.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_02.gridx = 1;
    gbc_gasLabel_02.gridy = 3;
    gasMatrixPanel.add( gasLabel_02, gbc_gasLabel_02 );
    gasO2Spinner_02 = new JSpinner();
    gasO2Spinner_02.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_02.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_02 = new GridBagConstraints();
    gbc_gasO2Spinner_02.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_02.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_02.gridx = 3;
    gbc_gasO2Spinner_02.gridy = 3;
    gasMatrixPanel.add( gasO2Spinner_02, gbc_gasO2Spinner_02 );
    gasHESpinner_02 = new JSpinner();
    gasHESpinner_02.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_02 = new GridBagConstraints();
    gbc_gasHESpinner_02.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_02.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_02.gridx = 5;
    gbc_gasHESpinner_02.gridy = 3;
    gasMatrixPanel.add( gasHESpinner_02, gbc_gasHESpinner_02 );
    diluent1Checkbox_02 = new JCheckBox( "D1" );
    duluent1ButtonGroup.add( diluent1Checkbox_02 );
    GridBagConstraints gbc_diluent1Checkbox_02 = new GridBagConstraints();
    gbc_diluent1Checkbox_02.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_02.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent1Checkbox_02.gridx = 7;
    gbc_diluent1Checkbox_02.gridy = 3;
    gasMatrixPanel.add( diluent1Checkbox_02, gbc_diluent1Checkbox_02 );
    diluent2Checkbox_02 = new JCheckBox( "D2" );
    diluent2ButtonGroup.add( diluent2Checkbox_02 );
    GridBagConstraints gbc_diluent2Checkbox_02 = new GridBagConstraints();
    gbc_diluent2Checkbox_02.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_02.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent2Checkbox_02.gridx = 8;
    gbc_diluent2Checkbox_02.gridy = 3;
    gasMatrixPanel.add( diluent2Checkbox_02, gbc_diluent2Checkbox_02 );
    bailoutCheckbox_02 = new JCheckBox( "B" );
    GridBagConstraints gbc_bailoutCheckbox_02 = new GridBagConstraints();
    gbc_bailoutCheckbox_02.insets = new Insets( 0, 0, 5, 5 );
    gbc_bailoutCheckbox_02.gridx = 9;
    gbc_bailoutCheckbox_02.gridy = 3;
    gasMatrixPanel.add( bailoutCheckbox_02, gbc_bailoutCheckbox_02 );
    gasNameLabel_02 = new JLabel( "AIR" );
    gasNameLabel_02.setForeground( new Color( 0, 0, 128 ) );
    gasNameLabel_02.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_gasNameLabel_02 = new GridBagConstraints();
    gbc_gasNameLabel_02.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_02.insets = new Insets( 0, 0, 5, 0 );
    gbc_gasNameLabel_02.gridx = 10;
    gbc_gasNameLabel_02.gridy = 3;
    gasMatrixPanel.add( gasNameLabel_02, gbc_gasNameLabel_02 );
    gasLabel_03 = new JLabel( "GAS03" );
    GridBagConstraints gbc_gasLabel_03 = new GridBagConstraints();
    gbc_gasLabel_03.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_03.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_03.gridx = 1;
    gbc_gasLabel_03.gridy = 4;
    gasMatrixPanel.add( gasLabel_03, gbc_gasLabel_03 );
    gasO2Spinner_03 = new JSpinner();
    gasO2Spinner_03.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_03.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_03 = new GridBagConstraints();
    gbc_gasO2Spinner_03.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_03.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_03.gridx = 3;
    gbc_gasO2Spinner_03.gridy = 4;
    gasMatrixPanel.add( gasO2Spinner_03, gbc_gasO2Spinner_03 );
    gasHESpinner_03 = new JSpinner();
    gasHESpinner_03.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_03 = new GridBagConstraints();
    gbc_gasHESpinner_03.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_03.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_03.gridx = 5;
    gbc_gasHESpinner_03.gridy = 4;
    gasMatrixPanel.add( gasHESpinner_03, gbc_gasHESpinner_03 );
    diluent1Checkbox_03 = new JCheckBox( "D1" );
    duluent1ButtonGroup.add( diluent1Checkbox_03 );
    GridBagConstraints gbc_diluent1Checkbox_03 = new GridBagConstraints();
    gbc_diluent1Checkbox_03.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_03.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent1Checkbox_03.gridx = 7;
    gbc_diluent1Checkbox_03.gridy = 4;
    gasMatrixPanel.add( diluent1Checkbox_03, gbc_diluent1Checkbox_03 );
    diluent2Checkbox_03 = new JCheckBox( "D2" );
    diluent2ButtonGroup.add( diluent2Checkbox_03 );
    GridBagConstraints gbc_diluent2Checkbox_03 = new GridBagConstraints();
    gbc_diluent2Checkbox_03.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_03.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent2Checkbox_03.gridx = 8;
    gbc_diluent2Checkbox_03.gridy = 4;
    gasMatrixPanel.add( diluent2Checkbox_03, gbc_diluent2Checkbox_03 );
    bailoutCheckbox_03 = new JCheckBox( "B" );
    GridBagConstraints gbc_bailoutCheckbox_03 = new GridBagConstraints();
    gbc_bailoutCheckbox_03.insets = new Insets( 0, 0, 5, 5 );
    gbc_bailoutCheckbox_03.gridx = 9;
    gbc_bailoutCheckbox_03.gridy = 4;
    gasMatrixPanel.add( bailoutCheckbox_03, gbc_bailoutCheckbox_03 );
    gasNameLabel_03 = new JLabel( "AIR" );
    gasNameLabel_03.setForeground( new Color( 0, 0, 128 ) );
    gasNameLabel_03.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_gasNameLabel_03 = new GridBagConstraints();
    gbc_gasNameLabel_03.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_03.insets = new Insets( 0, 0, 5, 0 );
    gbc_gasNameLabel_03.gridx = 10;
    gbc_gasNameLabel_03.gridy = 4;
    gasMatrixPanel.add( gasNameLabel_03, gbc_gasNameLabel_03 );
    gasLabel_04 = new JLabel( "GAS04" );
    GridBagConstraints gbc_gasLabel_04 = new GridBagConstraints();
    gbc_gasLabel_04.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_04.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_04.gridx = 1;
    gbc_gasLabel_04.gridy = 5;
    gasMatrixPanel.add( gasLabel_04, gbc_gasLabel_04 );
    gasO2Spinner_04 = new JSpinner();
    gasO2Spinner_04.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_04.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_04 = new GridBagConstraints();
    gbc_gasO2Spinner_04.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_04.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_04.gridx = 3;
    gbc_gasO2Spinner_04.gridy = 5;
    gasMatrixPanel.add( gasO2Spinner_04, gbc_gasO2Spinner_04 );
    gasHESpinner_04 = new JSpinner();
    gasHESpinner_04.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_04 = new GridBagConstraints();
    gbc_gasHESpinner_04.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_04.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_04.gridx = 5;
    gbc_gasHESpinner_04.gridy = 5;
    gasMatrixPanel.add( gasHESpinner_04, gbc_gasHESpinner_04 );
    diluent1Checkbox_04 = new JCheckBox( "D1" );
    duluent1ButtonGroup.add( diluent1Checkbox_04 );
    GridBagConstraints gbc_diluent1Checkbox_04 = new GridBagConstraints();
    gbc_diluent1Checkbox_04.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_04.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent1Checkbox_04.gridx = 7;
    gbc_diluent1Checkbox_04.gridy = 5;
    gasMatrixPanel.add( diluent1Checkbox_04, gbc_diluent1Checkbox_04 );
    diluent2Checkbox_04 = new JCheckBox( "D2" );
    diluent2ButtonGroup.add( diluent2Checkbox_04 );
    GridBagConstraints gbc_diluent2Checkbox_04 = new GridBagConstraints();
    gbc_diluent2Checkbox_04.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_04.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent2Checkbox_04.gridx = 8;
    gbc_diluent2Checkbox_04.gridy = 5;
    gasMatrixPanel.add( diluent2Checkbox_04, gbc_diluent2Checkbox_04 );
    bailoutCheckbox_04 = new JCheckBox( "B" );
    GridBagConstraints gbc_bailoutCheckbox_04 = new GridBagConstraints();
    gbc_bailoutCheckbox_04.insets = new Insets( 0, 0, 5, 5 );
    gbc_bailoutCheckbox_04.gridx = 9;
    gbc_bailoutCheckbox_04.gridy = 5;
    gasMatrixPanel.add( bailoutCheckbox_04, gbc_bailoutCheckbox_04 );
    gasNameLabel_04 = new JLabel( "AIR" );
    gasNameLabel_04.setForeground( new Color( 0, 0, 128 ) );
    gasNameLabel_04.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_gasNameLabel_04 = new GridBagConstraints();
    gbc_gasNameLabel_04.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_04.insets = new Insets( 0, 0, 5, 0 );
    gbc_gasNameLabel_04.gridx = 10;
    gbc_gasNameLabel_04.gridy = 5;
    gasMatrixPanel.add( gasNameLabel_04, gbc_gasNameLabel_04 );
    gasLabel_05 = new JLabel( "GAS05" );
    GridBagConstraints gbc_gasLabel_05 = new GridBagConstraints();
    gbc_gasLabel_05.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_05.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_05.gridx = 1;
    gbc_gasLabel_05.gridy = 6;
    gasMatrixPanel.add( gasLabel_05, gbc_gasLabel_05 );
    gasO2Spinner_05 = new JSpinner();
    gasO2Spinner_05.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_05.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_05 = new GridBagConstraints();
    gbc_gasO2Spinner_05.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_05.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_05.gridx = 3;
    gbc_gasO2Spinner_05.gridy = 6;
    gasMatrixPanel.add( gasO2Spinner_05, gbc_gasO2Spinner_05 );
    gasHESpinner_05 = new JSpinner();
    gasHESpinner_05.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_05 = new GridBagConstraints();
    gbc_gasHESpinner_05.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_05.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_05.gridx = 5;
    gbc_gasHESpinner_05.gridy = 6;
    gasMatrixPanel.add( gasHESpinner_05, gbc_gasHESpinner_05 );
    diluent1Checkbox_05 = new JCheckBox( "D1" );
    duluent1ButtonGroup.add( diluent1Checkbox_05 );
    GridBagConstraints gbc_diluent1Checkbox_05 = new GridBagConstraints();
    gbc_diluent1Checkbox_05.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_05.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent1Checkbox_05.gridx = 7;
    gbc_diluent1Checkbox_05.gridy = 6;
    gasMatrixPanel.add( diluent1Checkbox_05, gbc_diluent1Checkbox_05 );
    diluent2Checkbox_05 = new JCheckBox( "D2" );
    diluent2ButtonGroup.add( diluent2Checkbox_05 );
    GridBagConstraints gbc_diluent2Checkbox_05 = new GridBagConstraints();
    gbc_diluent2Checkbox_05.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_05.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent2Checkbox_05.gridx = 8;
    gbc_diluent2Checkbox_05.gridy = 6;
    gasMatrixPanel.add( diluent2Checkbox_05, gbc_diluent2Checkbox_05 );
    bailoutCheckbox_05 = new JCheckBox( "B" );
    GridBagConstraints gbc_bailoutCheckbox_05 = new GridBagConstraints();
    gbc_bailoutCheckbox_05.insets = new Insets( 0, 0, 5, 5 );
    gbc_bailoutCheckbox_05.gridx = 9;
    gbc_bailoutCheckbox_05.gridy = 6;
    gasMatrixPanel.add( bailoutCheckbox_05, gbc_bailoutCheckbox_05 );
    gasNameLabel_05 = new JLabel( "AIR" );
    gasNameLabel_05.setForeground( new Color( 0, 0, 128 ) );
    gasNameLabel_05.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_gasNameLabel_05 = new GridBagConstraints();
    gbc_gasNameLabel_05.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_05.insets = new Insets( 0, 0, 5, 0 );
    gbc_gasNameLabel_05.gridx = 10;
    gbc_gasNameLabel_05.gridy = 6;
    gasMatrixPanel.add( gasNameLabel_05, gbc_gasNameLabel_05 );
    gasLabel_06 = new JLabel( "GAS06" );
    GridBagConstraints gbc_gasLabel_06 = new GridBagConstraints();
    gbc_gasLabel_06.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_06.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_06.gridx = 1;
    gbc_gasLabel_06.gridy = 7;
    gasMatrixPanel.add( gasLabel_06, gbc_gasLabel_06 );
    gasO2Spinner_06 = new JSpinner();
    gasO2Spinner_06.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_06.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_06 = new GridBagConstraints();
    gbc_gasO2Spinner_06.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_06.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_06.gridx = 3;
    gbc_gasO2Spinner_06.gridy = 7;
    gasMatrixPanel.add( gasO2Spinner_06, gbc_gasO2Spinner_06 );
    gasHESpinner_06 = new JSpinner();
    gasHESpinner_06.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_06 = new GridBagConstraints();
    gbc_gasHESpinner_06.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_06.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_06.gridx = 5;
    gbc_gasHESpinner_06.gridy = 7;
    gasMatrixPanel.add( gasHESpinner_06, gbc_gasHESpinner_06 );
    diluent1Checkbox_06 = new JCheckBox( "D1" );
    duluent1ButtonGroup.add( diluent1Checkbox_06 );
    GridBagConstraints gbc_diluent1Checkbox_06 = new GridBagConstraints();
    gbc_diluent1Checkbox_06.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_06.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent1Checkbox_06.gridx = 7;
    gbc_diluent1Checkbox_06.gridy = 7;
    gasMatrixPanel.add( diluent1Checkbox_06, gbc_diluent1Checkbox_06 );
    diluent2Checkbox_06 = new JCheckBox( "D2" );
    diluent2ButtonGroup.add( diluent2Checkbox_06 );
    GridBagConstraints gbc_diluent2Checkbox_06 = new GridBagConstraints();
    gbc_diluent2Checkbox_06.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_06.insets = new Insets( 0, 0, 5, 5 );
    gbc_diluent2Checkbox_06.gridx = 8;
    gbc_diluent2Checkbox_06.gridy = 7;
    gasMatrixPanel.add( diluent2Checkbox_06, gbc_diluent2Checkbox_06 );
    bailoutCheckbox_06 = new JCheckBox( "B" );
    GridBagConstraints gbc_bailoutCheckbox_06 = new GridBagConstraints();
    gbc_bailoutCheckbox_06.insets = new Insets( 0, 0, 5, 5 );
    gbc_bailoutCheckbox_06.gridx = 9;
    gbc_bailoutCheckbox_06.gridy = 7;
    gasMatrixPanel.add( bailoutCheckbox_06, gbc_bailoutCheckbox_06 );
    gasNameLabel_06 = new JLabel( "AIR" );
    gasNameLabel_06.setForeground( new Color( 0, 0, 128 ) );
    gasNameLabel_06.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_gasNameLabel_06 = new GridBagConstraints();
    gbc_gasNameLabel_06.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_06.insets = new Insets( 0, 0, 5, 0 );
    gbc_gasNameLabel_06.gridx = 10;
    gbc_gasNameLabel_06.gridy = 7;
    gasMatrixPanel.add( gasNameLabel_06, gbc_gasNameLabel_06 );
    gasLabel_07 = new JLabel( "GAS07" );
    GridBagConstraints gbc_gasLabel_07 = new GridBagConstraints();
    gbc_gasLabel_07.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_07.insets = new Insets( 0, 0, 0, 5 );
    gbc_gasLabel_07.gridx = 1;
    gbc_gasLabel_07.gridy = 8;
    gasMatrixPanel.add( gasLabel_07, gbc_gasLabel_07 );
    gasO2Spinner_07 = new JSpinner();
    gasO2Spinner_07.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_07.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_07 = new GridBagConstraints();
    gbc_gasO2Spinner_07.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_07.insets = new Insets( 0, 0, 0, 5 );
    gbc_gasO2Spinner_07.gridx = 3;
    gbc_gasO2Spinner_07.gridy = 8;
    gasMatrixPanel.add( gasO2Spinner_07, gbc_gasO2Spinner_07 );
    gasHESpinner_07 = new JSpinner();
    gasHESpinner_07.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_07 = new GridBagConstraints();
    gbc_gasHESpinner_07.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_07.insets = new Insets( 0, 0, 0, 5 );
    gbc_gasHESpinner_07.gridx = 5;
    gbc_gasHESpinner_07.gridy = 8;
    gasMatrixPanel.add( gasHESpinner_07, gbc_gasHESpinner_07 );
    diluent1Checkbox_07 = new JCheckBox( "D1" );
    duluent1ButtonGroup.add( diluent1Checkbox_07 );
    GridBagConstraints gbc_diluent1Checkbox_07 = new GridBagConstraints();
    gbc_diluent1Checkbox_07.anchor = GridBagConstraints.WEST;
    gbc_diluent1Checkbox_07.insets = new Insets( 0, 0, 0, 5 );
    gbc_diluent1Checkbox_07.gridx = 7;
    gbc_diluent1Checkbox_07.gridy = 8;
    gasMatrixPanel.add( diluent1Checkbox_07, gbc_diluent1Checkbox_07 );
    diluent2Checkbox_07 = new JCheckBox( "D2" );
    diluent2ButtonGroup.add( diluent2Checkbox_07 );
    GridBagConstraints gbc_diluent2Checkbox_07 = new GridBagConstraints();
    gbc_diluent2Checkbox_07.anchor = GridBagConstraints.WEST;
    gbc_diluent2Checkbox_07.insets = new Insets( 0, 0, 0, 5 );
    gbc_diluent2Checkbox_07.gridx = 8;
    gbc_diluent2Checkbox_07.gridy = 8;
    gasMatrixPanel.add( diluent2Checkbox_07, gbc_diluent2Checkbox_07 );
    bailoutCheckbox_07 = new JCheckBox( "B" );
    GridBagConstraints gbc_bailoutCheckbox_07 = new GridBagConstraints();
    gbc_bailoutCheckbox_07.insets = new Insets( 0, 0, 0, 5 );
    gbc_bailoutCheckbox_07.gridx = 9;
    gbc_bailoutCheckbox_07.gridy = 8;
    gasMatrixPanel.add( bailoutCheckbox_07, gbc_bailoutCheckbox_07 );
    gasNameLabel_07 = new JLabel( "AIR" );
    gasNameLabel_07.setForeground( new Color( 0, 0, 128 ) );
    gasNameLabel_07.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_gasNameLabel_07 = new GridBagConstraints();
    gbc_gasNameLabel_07.anchor = GridBagConstraints.WEST;
    gbc_gasNameLabel_07.gridx = 10;
    gbc_gasNameLabel_07.gridy = 8;
    gasMatrixPanel.add( gasNameLabel_07, gbc_gasNameLabel_07 );
    userPresetLabel = new JLabel( "USERPRESET" );
    userPresetLabel.setBounds( 569, 11, 210, 14 );
    add( userPresetLabel );
    customPresetComboBox = new JComboBox();
    customPresetComboBox.setEnabled( false );
    customPresetComboBox.setBounds( 569, 31, 210, 20 );
    add( customPresetComboBox );
    writeGasPresetButton = new JButton( "WRITEPRESELECT" );
    writeGasPresetButton.setForeground( Color.RED );
    writeGasPresetButton.setBackground( new Color( 255, 192, 203 ) );
    writeGasPresetButton.setActionCommand( "write_gaslist_preset" );
    writeGasPresetButton.setBounds( 569, 310, 210, 33 );
    add( writeGasPresetButton );
    readGasPresetButton = new JButton( "READPRESET" );
    readGasPresetButton.setSize( new Dimension( 210, 35 ) );
    readGasPresetButton.setPreferredSize( new Dimension( 180, 40 ) );
    readGasPresetButton.setMaximumSize( new Dimension( 160, 40 ) );
    readGasPresetButton.setMargin( new Insets( 2, 30, 2, 30 ) );
    readGasPresetButton.setForeground( new Color( 0, 100, 0 ) );
    readGasPresetButton.setBackground( new Color( 152, 251, 152 ) );
    readGasPresetButton.setActionCommand( "read_gaslist_preset" );
    readGasPresetButton.setBounds( 569, 349, 210, 35 );
    add( readGasPresetButton );
    licenseStatusLabel = new JLabel( "LICENSE" );
    licenseStatusLabel.setBounds( 10, 385, 553, 14 );
    add( licenseStatusLabel );
    isPanelInitiated = true;
  }

  /**
   * 
   * Panel GUI initialisieren
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   */
  public void prepareGasslistPanel()
  {
    initPanel();
    initGasObjectMaps();
    setAllGasPanelsEnabled( isElementsGasMatrixEnabled );
    setLanguageStrings( stringsBundle );
    setLicenseLabel( stringsBundle );
    setGlobalChangeListener( mainCommGUI );
  }

  /**
   * 
   * Daten des Panels freigeben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.09.2012
   */
  public void releasePanel()
  {
    isPanelInitiated = true;
    this.removeAll();
    o2SpinnerMap.clear();
    heSpinnerMap.clear();
    gasLblMap.clear();
    bailoutMap.clear();
    diluent1Map.clear();
    diluent2Map.clear();
  }

  /**
   * 
   * Lizenzstatus setzen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.04.2012
   * @param lic
   * @param cust
   */
  public void setLicenseState( int lic, int cust )
  {
    licenseState = lic;
    customConfig = cust;
  }

  public int setLanguageStrings( ResourceBundle stringsBundle )
  {
    this.stringsBundle = stringsBundle;
    if( !isPanelInitiated ) return( -1 );
    if( stringsBundle == null ) return( -1 );
    // so, ignoriere mal alles....
    try
    {
      // //////////////////////////////////////////////////////////////////////
      // Tabbes Pane gas
      String gasLabelStr = stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.gasLabel" );
      gasLabel_00.setText( String.format( "%s %02d", gasLabelStr, 1 ) );
      gasLabel_01.setText( String.format( "%s %02d", gasLabelStr, 2 ) );
      gasLabel_02.setText( String.format( "%s %02d", gasLabelStr, 3 ) );
      gasLabel_03.setText( String.format( "%s %02d", gasLabelStr, 4 ) );
      gasLabel_04.setText( String.format( "%s %02d", gasLabelStr, 5 ) );
      gasLabel_05.setText( String.format( "%s %02d", gasLabelStr, 6 ) );
      gasLabel_06.setText( String.format( "%s %02d", gasLabelStr, 7 ) );
      gasLabel_07.setText( String.format( "%s %02d", gasLabelStr, 8 ) );
      gasLabelStr = stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.diluentLabel" ) + "1";
      diluent1Checkbox_00.setText( gasLabelStr );
      diluent1Checkbox_01.setText( gasLabelStr );
      diluent1Checkbox_02.setText( gasLabelStr );
      diluent1Checkbox_03.setText( gasLabelStr );
      diluent1Checkbox_04.setText( gasLabelStr );
      diluent1Checkbox_05.setText( gasLabelStr );
      diluent1Checkbox_06.setText( gasLabelStr );
      diluent1Checkbox_07.setText( gasLabelStr );
      gasLabelStr = stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.diluentLabel" ) + "2";
      diluent2Checkbox_00.setText( gasLabelStr );
      diluent2Checkbox_01.setText( gasLabelStr );
      diluent2Checkbox_02.setText( gasLabelStr );
      diluent2Checkbox_03.setText( gasLabelStr );
      diluent2Checkbox_04.setText( gasLabelStr );
      diluent2Checkbox_05.setText( gasLabelStr );
      diluent2Checkbox_06.setText( gasLabelStr );
      diluent2Checkbox_07.setText( gasLabelStr );
      gasLabelStr = stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.bailoutLabel" );
      bailoutCheckbox_00.setText( gasLabelStr );
      bailoutCheckbox_01.setText( gasLabelStr );
      bailoutCheckbox_02.setText( gasLabelStr );
      bailoutCheckbox_03.setText( gasLabelStr );
      bailoutCheckbox_04.setText( gasLabelStr );
      bailoutCheckbox_05.setText( gasLabelStr );
      bailoutCheckbox_06.setText( gasLabelStr );
      bailoutCheckbox_07.setText( gasLabelStr );
      gasReadFromSPXButton.setText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.gasReadFromSPXButton.text" ) );
      gasReadFromSPXButton.setToolTipText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.gasReadFromSPXButton.tooltiptext" ) );
      gasWriteToSPXButton.setText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.gasWriteToSPXButton.text" ) );
      gasWriteToSPXButton.setToolTipText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.gasWriteToSPXButton.tooltiptext" ) );
      readGasPresetButton.setText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.readGasPresetButton.text" ) );
      readGasPresetButton.setToolTipText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.readGasPresetButton.tooltiptext" ) );
      writeGasPresetButton.setText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.writeGasPresetButton.text" ) );
      writeGasPresetButton.setToolTipText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.writeGasPresetButton.tooltiptext" ) );
      customPresetComboBox.setToolTipText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.customPresetComboBox.tooltiptext" ) );
      userPresetLabel.setText( stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.userPresetLabel.text" ) );
      setLicenseLabel( stringsBundle );
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
   * Entsprechend der Lizenzlage Anzeigen, welcher Lizenzstatus korrekt ist.
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 21.04.2012
   * @param stringsBundle
   */
  public void setLicenseLabel( ResourceBundle stringsBundle )
  {
    if( !isPanelInitiated ) return;
    String licString;
    switch ( licenseState )
    {
      case -1:
        // nicht konfiguriert
        licString = " ";
      case 0:
        // Nitrox
        licString = stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.licenseLabel.nitrox.text" );
        break;
      case 1:
        licString = stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.licenseLabel.normoxic.text" );
        break;
      default:
        licString = stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.licenseLabel.fulltrimix.text" );
    }
    //
    switch ( customConfig )
    {
      case -1:
      case 0:
        // nicht konfiguriert/nicht erlaubt
        licenseStatusLabel.setText( licString );
        break;
      case 1:
        // erlaubt
        licenseStatusLabel.setText( licString + " - " + stringsBundle.getString( "spx42GaslistEditPanel.gasPanel.licenseLabel.customconfigEnabled.text" ) );
      default:
        licenseStatusLabel.setText( licString );
    }
  }

  /**
   * 
   * Für unkomplizierteren Zugriff die Objekte Indizieren
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.04.2012
   */
  private void initGasObjectMaps()
  {
    o2SpinnerMap.put( 0, gasO2Spinner_00 );
    o2SpinnerMap.put( 1, gasO2Spinner_01 );
    o2SpinnerMap.put( 2, gasO2Spinner_02 );
    o2SpinnerMap.put( 3, gasO2Spinner_03 );
    o2SpinnerMap.put( 4, gasO2Spinner_04 );
    o2SpinnerMap.put( 5, gasO2Spinner_05 );
    o2SpinnerMap.put( 6, gasO2Spinner_06 );
    o2SpinnerMap.put( 7, gasO2Spinner_07 );
    //
    heSpinnerMap.put( 0, gasHESpinner_00 );
    heSpinnerMap.put( 1, gasHESpinner_01 );
    heSpinnerMap.put( 2, gasHESpinner_02 );
    heSpinnerMap.put( 3, gasHESpinner_03 );
    heSpinnerMap.put( 4, gasHESpinner_04 );
    heSpinnerMap.put( 5, gasHESpinner_05 );
    heSpinnerMap.put( 6, gasHESpinner_06 );
    heSpinnerMap.put( 7, gasHESpinner_07 );
    //
    gasLblMap.put( 0, gasNameLabel_00 );
    gasLblMap.put( 1, gasNameLabel_01 );
    gasLblMap.put( 2, gasNameLabel_02 );
    gasLblMap.put( 3, gasNameLabel_03 );
    gasLblMap.put( 4, gasNameLabel_04 );
    gasLblMap.put( 5, gasNameLabel_05 );
    gasLblMap.put( 6, gasNameLabel_06 );
    gasLblMap.put( 7, gasNameLabel_07 );
    //
    bailoutMap.put( 0, bailoutCheckbox_00 );
    bailoutMap.put( 1, bailoutCheckbox_01 );
    bailoutMap.put( 2, bailoutCheckbox_02 );
    bailoutMap.put( 3, bailoutCheckbox_03 );
    bailoutMap.put( 4, bailoutCheckbox_04 );
    bailoutMap.put( 5, bailoutCheckbox_05 );
    bailoutMap.put( 6, bailoutCheckbox_06 );
    bailoutMap.put( 7, bailoutCheckbox_07 );
    //
    diluent1Map.put( 0, diluent1Checkbox_00 );
    diluent1Map.put( 1, diluent1Checkbox_01 );
    diluent1Map.put( 2, diluent1Checkbox_02 );
    diluent1Map.put( 3, diluent1Checkbox_03 );
    diluent1Map.put( 4, diluent1Checkbox_04 );
    diluent1Map.put( 5, diluent1Checkbox_05 );
    diluent1Map.put( 6, diluent1Checkbox_06 );
    diluent1Map.put( 7, diluent1Checkbox_07 );
    //
    diluent2Map.put( 0, diluent2Checkbox_00 );
    diluent2Map.put( 1, diluent2Checkbox_01 );
    diluent2Map.put( 2, diluent2Checkbox_02 );
    diluent2Map.put( 3, diluent2Checkbox_03 );
    diluent2Map.put( 4, diluent2Checkbox_04 );
    diluent2Map.put( 5, diluent2Checkbox_05 );
    diluent2Map.put( 6, diluent2Checkbox_06 );
    diluent2Map.put( 7, diluent2Checkbox_07 );
  }

  /**
   * 
   * Alle Change Listener für Spinner setzen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.04.2012
   * @param mainCommGUI
   */
  public void setGlobalChangeListener( MainCommGUI mainCommGUI )
  {
    this.mainCommGUI = mainCommGUI;
    if( !isPanelInitiated ) return;
    for( Integer idx : o2SpinnerMap.keySet() )
    {
      JSpinner sp = o2SpinnerMap.get( idx );
      sp.addChangeListener( mainCommGUI );
      sp.setModel( new SpinnerNumberModel( 21, 21, 100, 1 ) );
    }
    //
    for( Integer idx : heSpinnerMap.keySet() )
    {
      JSpinner sp = heSpinnerMap.get( idx );
      sp.addChangeListener( mainCommGUI );
      sp.setModel( new SpinnerNumberModel( 0, 0, 99, 1 ) );
    }
    //
    for( Integer idx : bailoutMap.keySet() )
    {
      JCheckBox cb = bailoutMap.get( idx );
      cb.addItemListener( mainCommGUI );
      cb.setActionCommand( String.format( "bailout:%d", idx ) );
    }
    //
    for( Integer idx : diluent1Map.keySet() )
    {
      JCheckBox cb = diluent1Map.get( idx );
      cb.addItemListener( mainCommGUI );
      cb.setActionCommand( String.format( "diluent1:%d", idx ) );
    }
    //
    for( Integer idx : diluent2Map.keySet() )
    {
      JCheckBox cb = diluent2Map.get( idx );
      cb.addItemListener( mainCommGUI );
      cb.setActionCommand( String.format( "diluent2:%d", idx ) );
    }
    //
    gasReadFromSPXButton.setActionCommand( "read_gaslist" );
    gasReadFromSPXButton.addActionListener( mainCommGUI );
    gasReadFromSPXButton.addMouseMotionListener( mainCommGUI );
    //
    gasWriteToSPXButton.setActionCommand( "write_gaslist" );
    gasWriteToSPXButton.addActionListener( mainCommGUI );
    gasWriteToSPXButton.addMouseMotionListener( mainCommGUI );
  }

  /**
   * 
   * Alles inm Panel für die Gsasmatrix de/aktivieren
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.04.2012
   * @param en
   */
  public void setElementsGasMatrixPanelEnabled( boolean en )
  {
    this.isElementsGasMatrixEnabled = en;
    if( !isPanelInitiated ) return;
    for( Component cp : gasMatrixPanel.getComponents() )
    {
      // License State 0=Nitrox,1=Normoxic Trimix,2=Full Trimix
      // isses ein Spinner?
      if( cp instanceof JSpinner )
      {
        JSpinner currSpinner = ( JSpinner )cp;
        // welcher Lizenzstatus
        // ////////////////////////////////////////////////////////////////////
        // Ist es NITROX?
        if( licenseState < 1 )
        {
          // issen einer von den Helium-Teilen
          for( Integer idx : heSpinnerMap.keySet() )
          {
            JSpinner sp = heSpinnerMap.get( idx );
            if( currSpinner.equals( sp ) )
            {
              // ja, ein Helium-Teil, NITROX enabled
              cp.setEnabled( false );
            }
          }
          // ein Sauerstoffteil?
          for( Integer idx : o2SpinnerMap.keySet() )
          {
            JSpinner sp = o2SpinnerMap.get( idx );
            if( currSpinner.equals( sp ) )
            {
              // ja, ein Helium-Teil, NITROX enabled
              cp.setEnabled( en );
              currSpinner.setModel( new SpinnerNumberModel( 21, 21, 100, 1 ) );
            }
          }
        }
        // ////////////////////////////////////////////////////////////////////
        // ist es Normoxic Trimix?
        if( licenseState == 1 )
        {
          // issen einer von den Helium-Teilen / Normoxic Trimix enabled
          for( Integer idx : heSpinnerMap.keySet() )
          {
            JSpinner sp = heSpinnerMap.get( idx );
            if( currSpinner.equals( sp ) )
            {
              // ja, ein Helium-Teil, max 79 Prozent Helium
              cp.setEnabled( en );
              currSpinner.setModel( new SpinnerNumberModel( 0, 0, 79, 1 ) );
            }
          }
          // ein Sauerstoffteil?
          for( Integer idx : o2SpinnerMap.keySet() )
          {
            JSpinner sp = o2SpinnerMap.get( idx );
            if( currSpinner.equals( sp ) )
            {
              // ja, ein O2-Teil, NORMOXIC Trimix enabled
              cp.setEnabled( en );
              currSpinner.setModel( new SpinnerNumberModel( 21, 21, 100, 1 ) );
            }
          }
        }
        // ////////////////////////////////////////////////////////////////////
        // ist es FULL Trimix
        else if( licenseState == 2 )
        {
          // Normoxic Trimix
          // issen einer von den Helium-Teilen
          for( Integer idx : heSpinnerMap.keySet() )
          {
            JSpinner sp = heSpinnerMap.get( idx );
            if( currSpinner.equals( sp ) )
            {
              // ja, ein Helium-Teil, full Trimix
              cp.setEnabled( en );
              sp.setModel( new SpinnerNumberModel( 0, 0, 99, 1 ) );
            }
          }
          // ein Sauerstoffteil?
          for( Integer idx : o2SpinnerMap.keySet() )
          {
            JSpinner sp = o2SpinnerMap.get( idx );
            if( currSpinner.equals( sp ) )
            {
              // ja, ein O2-Teil, Full Trimmix enabled
              cp.setEnabled( en );
              currSpinner.setModel( new SpinnerNumberModel( 1, 1, 100, 1 ) );
            }
          }
        }
      }
      else
      {
        cp.setEnabled( en );
      }
    }
    gasMatrixPanel.setEnabled( en );
    gasWriteToSPXButton.setEnabled( en );
  }

  /**
   * 
   * Alle Gaseinstellungsdinger de/aktivieren
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.04.2012
   * @param en
   *          enabled?
   */
  public void setAllGasPanelsEnabled( boolean en )
  {
    setElementsGasMatrixPanelEnabled( en );
    // momentan IMMER disabled
    setGasPresetObjectsEnabled( false );
  }

  /**
   * 
   * Presets erlauben/verbieten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.04.2012
   * @param en
   */
  public void setGasPresetObjectsEnabled( boolean en )
  {
    if( !isPanelInitiated ) return;
    customPresetComboBox.setEnabled( en );
    writeGasPresetButton.setEnabled( en );
    readGasPresetButton.setEnabled( en );
  }
}
