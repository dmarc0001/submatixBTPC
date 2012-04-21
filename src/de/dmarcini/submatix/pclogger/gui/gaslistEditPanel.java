package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.LineBorder;

public class gaslistEditPanel extends JPanel
{
  private final JLabel   gasLabel_00;
  private final JLabel   gasLabel_01;
  private final JLabel   gasLabel_03;
  private final JSpinner gasO2Spinner;
  private final JSpinner gasHESpinner_00;

  /**
   * Create the panel.
   */
  public gaslistEditPanel()
  {
    setLayout( null );
    JButton button = new JButton( "READ" );
    button.setSize( new Dimension( 199, 60 ) );
    button.setPreferredSize( new Dimension( 180, 40 ) );
    button.setMaximumSize( new Dimension( 160, 40 ) );
    button.setMargin( new Insets( 2, 30, 2, 30 ) );
    button.setForeground( new Color( 0, 100, 0 ) );
    button.setBackground( new Color( 152, 251, 152 ) );
    button.setActionCommand( "read_gaslist" );
    button.setBounds( 10, 421, 199, 60 );
    add( button );
    JButton button_1 = new JButton( "WRITE" );
    button_1.setForeground( Color.RED );
    button_1.setBackground( new Color( 255, 192, 203 ) );
    button_1.setActionCommand( "write_gaslist" );
    button_1.setBounds( 346, 421, 217, 60 );
    add( button_1 );
    JPanel panel = new JPanel();
    panel.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );
    panel.setBounds( 10, 11, 553, 368 );
    add( panel );
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[]
    { 16, 70, 0, 50, 0, 50, 0, 55, 55, 55, 90, 0 };
    gbl_panel.rowHeights = new int[]
    { 37, 40, 40, 40, 40, 40, 40, 40, 40, 0 };
    gbl_panel.columnWeights = new double[]
    { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_panel.rowWeights = new double[]
    { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    panel.setLayout( gbl_panel );
    JLabel label = new JLabel( "O2" );
    label.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
    GridBagConstraints gbc_label = new GridBagConstraints();
    gbc_label.anchor = GridBagConstraints.SOUTH;
    gbc_label.insets = new Insets( 0, 0, 5, 5 );
    gbc_label.gridx = 3;
    gbc_label.gridy = 0;
    panel.add( label, gbc_label );
    JLabel label_1 = new JLabel( "HE" );
    label_1.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
    GridBagConstraints gbc_label_1 = new GridBagConstraints();
    gbc_label_1.anchor = GridBagConstraints.SOUTH;
    gbc_label_1.insets = new Insets( 0, 0, 5, 5 );
    gbc_label_1.gridx = 5;
    gbc_label_1.gridy = 0;
    panel.add( label_1, gbc_label_1 );
    gasLabel_00 = new JLabel( "GAS00" );
    GridBagConstraints gbc_gasLabel_00 = new GridBagConstraints();
    gbc_gasLabel_00.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_00.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_00.gridx = 1;
    gbc_gasLabel_00.gridy = 1;
    panel.add( gasLabel_00, gbc_gasLabel_00 );
    gasO2Spinner = new JSpinner();
    gasO2Spinner.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner = new GridBagConstraints();
    gbc_gasO2Spinner.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner.gridx = 3;
    gbc_gasO2Spinner.gridy = 1;
    panel.add( gasO2Spinner, gbc_gasO2Spinner );
    gasHESpinner_00 = new JSpinner();
    gasHESpinner_00.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_00 = new GridBagConstraints();
    gbc_gasHESpinner_00.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_00.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_00.gridx = 5;
    gbc_gasHESpinner_00.gridy = 1;
    panel.add( gasHESpinner_00, gbc_gasHESpinner_00 );
    JCheckBox checkBox = new JCheckBox( "D1" );
    GridBagConstraints gbc_checkBox = new GridBagConstraints();
    gbc_checkBox.anchor = GridBagConstraints.WEST;
    gbc_checkBox.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox.gridx = 7;
    gbc_checkBox.gridy = 1;
    panel.add( checkBox, gbc_checkBox );
    JCheckBox checkBox_1 = new JCheckBox( "D2" );
    GridBagConstraints gbc_checkBox_1 = new GridBagConstraints();
    gbc_checkBox_1.anchor = GridBagConstraints.WEST;
    gbc_checkBox_1.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_1.gridx = 8;
    gbc_checkBox_1.gridy = 1;
    panel.add( checkBox_1, gbc_checkBox_1 );
    JCheckBox checkBox_2 = new JCheckBox( "B" );
    GridBagConstraints gbc_checkBox_2 = new GridBagConstraints();
    gbc_checkBox_2.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_2.gridx = 9;
    gbc_checkBox_2.gridy = 1;
    panel.add( checkBox_2, gbc_checkBox_2 );
    JLabel label_3 = new JLabel( "AIR" );
    label_3.setForeground( new Color( 0, 0, 128 ) );
    label_3.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_label_3 = new GridBagConstraints();
    gbc_label_3.anchor = GridBagConstraints.WEST;
    gbc_label_3.insets = new Insets( 0, 0, 5, 0 );
    gbc_label_3.gridx = 10;
    gbc_label_3.gridy = 1;
    panel.add( label_3, gbc_label_3 );
    gasLabel_01 = new JLabel( "GAS01" );
    GridBagConstraints gbc_gasLabel_01 = new GridBagConstraints();
    gbc_gasLabel_01.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_01.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_01.gridx = 1;
    gbc_gasLabel_01.gridy = 2;
    panel.add( gasLabel_01, gbc_gasLabel_01 );
    JSpinner gasO2Spinner_01 = new JSpinner();
    gasO2Spinner_01.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_01.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_01 = new GridBagConstraints();
    gbc_gasO2Spinner_01.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_01.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_01.gridx = 3;
    gbc_gasO2Spinner_01.gridy = 2;
    panel.add( gasO2Spinner_01, gbc_gasO2Spinner_01 );
    JSpinner gasHESpinner_01 = new JSpinner();
    gasHESpinner_01.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_01 = new GridBagConstraints();
    gbc_gasHESpinner_01.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_01.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_01.gridx = 5;
    gbc_gasHESpinner_01.gridy = 2;
    panel.add( gasHESpinner_01, gbc_gasHESpinner_01 );
    JCheckBox checkBox_3 = new JCheckBox( "D1" );
    GridBagConstraints gbc_checkBox_3 = new GridBagConstraints();
    gbc_checkBox_3.anchor = GridBagConstraints.WEST;
    gbc_checkBox_3.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_3.gridx = 7;
    gbc_checkBox_3.gridy = 2;
    panel.add( checkBox_3, gbc_checkBox_3 );
    JCheckBox checkBox_4 = new JCheckBox( "D2" );
    GridBagConstraints gbc_checkBox_4 = new GridBagConstraints();
    gbc_checkBox_4.anchor = GridBagConstraints.WEST;
    gbc_checkBox_4.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_4.gridx = 8;
    gbc_checkBox_4.gridy = 2;
    panel.add( checkBox_4, gbc_checkBox_4 );
    JCheckBox checkBox_5 = new JCheckBox( "B" );
    GridBagConstraints gbc_checkBox_5 = new GridBagConstraints();
    gbc_checkBox_5.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_5.gridx = 9;
    gbc_checkBox_5.gridy = 2;
    panel.add( checkBox_5, gbc_checkBox_5 );
    JLabel label_5 = new JLabel( "AIR" );
    label_5.setForeground( new Color( 0, 0, 128 ) );
    label_5.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_label_5 = new GridBagConstraints();
    gbc_label_5.anchor = GridBagConstraints.WEST;
    gbc_label_5.insets = new Insets( 0, 0, 5, 0 );
    gbc_label_5.gridx = 10;
    gbc_label_5.gridy = 2;
    panel.add( label_5, gbc_label_5 );
    JLabel gasLabel_02 = new JLabel( "GAS02" );
    GridBagConstraints gbc_gasLabel_02 = new GridBagConstraints();
    gbc_gasLabel_02.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_02.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_02.gridx = 1;
    gbc_gasLabel_02.gridy = 3;
    panel.add( gasLabel_02, gbc_gasLabel_02 );
    JSpinner gasO2Spinner_02 = new JSpinner();
    gasO2Spinner_02.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_02.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_02 = new GridBagConstraints();
    gbc_gasO2Spinner_02.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_02.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_02.gridx = 3;
    gbc_gasO2Spinner_02.gridy = 3;
    panel.add( gasO2Spinner_02, gbc_gasO2Spinner_02 );
    JSpinner gasHESpinner_02 = new JSpinner();
    gasHESpinner_02.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_02 = new GridBagConstraints();
    gbc_gasHESpinner_02.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_02.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_02.gridx = 5;
    gbc_gasHESpinner_02.gridy = 3;
    panel.add( gasHESpinner_02, gbc_gasHESpinner_02 );
    JCheckBox checkBox_6 = new JCheckBox( "D1" );
    GridBagConstraints gbc_checkBox_6 = new GridBagConstraints();
    gbc_checkBox_6.anchor = GridBagConstraints.WEST;
    gbc_checkBox_6.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_6.gridx = 7;
    gbc_checkBox_6.gridy = 3;
    panel.add( checkBox_6, gbc_checkBox_6 );
    JCheckBox checkBox_7 = new JCheckBox( "D2" );
    GridBagConstraints gbc_checkBox_7 = new GridBagConstraints();
    gbc_checkBox_7.anchor = GridBagConstraints.WEST;
    gbc_checkBox_7.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_7.gridx = 8;
    gbc_checkBox_7.gridy = 3;
    panel.add( checkBox_7, gbc_checkBox_7 );
    JCheckBox checkBox_8 = new JCheckBox( "B" );
    GridBagConstraints gbc_checkBox_8 = new GridBagConstraints();
    gbc_checkBox_8.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_8.gridx = 9;
    gbc_checkBox_8.gridy = 3;
    panel.add( checkBox_8, gbc_checkBox_8 );
    JLabel label_7 = new JLabel( "AIR" );
    label_7.setForeground( new Color( 0, 0, 128 ) );
    label_7.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_label_7 = new GridBagConstraints();
    gbc_label_7.anchor = GridBagConstraints.WEST;
    gbc_label_7.insets = new Insets( 0, 0, 5, 0 );
    gbc_label_7.gridx = 10;
    gbc_label_7.gridy = 3;
    panel.add( label_7, gbc_label_7 );
    gasLabel_03 = new JLabel( "GAS03" );
    GridBagConstraints gbc_gasLabel_03 = new GridBagConstraints();
    gbc_gasLabel_03.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_03.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_03.gridx = 1;
    gbc_gasLabel_03.gridy = 4;
    panel.add( gasLabel_03, gbc_gasLabel_03 );
    JSpinner gasO2Spinner_03 = new JSpinner();
    gasO2Spinner_03.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_03.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_03 = new GridBagConstraints();
    gbc_gasO2Spinner_03.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_03.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_03.gridx = 3;
    gbc_gasO2Spinner_03.gridy = 4;
    panel.add( gasO2Spinner_03, gbc_gasO2Spinner_03 );
    JSpinner gasHESpinner_03 = new JSpinner();
    gasHESpinner_03.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_03 = new GridBagConstraints();
    gbc_gasHESpinner_03.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_03.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_03.gridx = 5;
    gbc_gasHESpinner_03.gridy = 4;
    panel.add( gasHESpinner_03, gbc_gasHESpinner_03 );
    JCheckBox checkBox_9 = new JCheckBox( "D1" );
    GridBagConstraints gbc_checkBox_9 = new GridBagConstraints();
    gbc_checkBox_9.anchor = GridBagConstraints.WEST;
    gbc_checkBox_9.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_9.gridx = 7;
    gbc_checkBox_9.gridy = 4;
    panel.add( checkBox_9, gbc_checkBox_9 );
    JCheckBox checkBox_10 = new JCheckBox( "D2" );
    GridBagConstraints gbc_checkBox_10 = new GridBagConstraints();
    gbc_checkBox_10.anchor = GridBagConstraints.WEST;
    gbc_checkBox_10.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_10.gridx = 8;
    gbc_checkBox_10.gridy = 4;
    panel.add( checkBox_10, gbc_checkBox_10 );
    JCheckBox checkBox_11 = new JCheckBox( "B" );
    GridBagConstraints gbc_checkBox_11 = new GridBagConstraints();
    gbc_checkBox_11.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_11.gridx = 9;
    gbc_checkBox_11.gridy = 4;
    panel.add( checkBox_11, gbc_checkBox_11 );
    JLabel label_9 = new JLabel( "AIR" );
    label_9.setForeground( new Color( 0, 0, 128 ) );
    label_9.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_label_9 = new GridBagConstraints();
    gbc_label_9.anchor = GridBagConstraints.WEST;
    gbc_label_9.insets = new Insets( 0, 0, 5, 0 );
    gbc_label_9.gridx = 10;
    gbc_label_9.gridy = 4;
    panel.add( label_9, gbc_label_9 );
    JLabel gasLabel_04 = new JLabel( "GAS04" );
    GridBagConstraints gbc_gasLabel_04 = new GridBagConstraints();
    gbc_gasLabel_04.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_04.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_04.gridx = 1;
    gbc_gasLabel_04.gridy = 5;
    panel.add( gasLabel_04, gbc_gasLabel_04 );
    JSpinner gasO2Spinner_04 = new JSpinner();
    gasO2Spinner_04.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_04.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_04 = new GridBagConstraints();
    gbc_gasO2Spinner_04.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_04.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_04.gridx = 3;
    gbc_gasO2Spinner_04.gridy = 5;
    panel.add( gasO2Spinner_04, gbc_gasO2Spinner_04 );
    JSpinner gasHESpinner_04 = new JSpinner();
    gasHESpinner_04.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_04 = new GridBagConstraints();
    gbc_gasHESpinner_04.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_04.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_04.gridx = 5;
    gbc_gasHESpinner_04.gridy = 5;
    panel.add( gasHESpinner_04, gbc_gasHESpinner_04 );
    JCheckBox checkBox_12 = new JCheckBox( "D1" );
    GridBagConstraints gbc_checkBox_12 = new GridBagConstraints();
    gbc_checkBox_12.anchor = GridBagConstraints.WEST;
    gbc_checkBox_12.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_12.gridx = 7;
    gbc_checkBox_12.gridy = 5;
    panel.add( checkBox_12, gbc_checkBox_12 );
    JCheckBox checkBox_13 = new JCheckBox( "D2" );
    GridBagConstraints gbc_checkBox_13 = new GridBagConstraints();
    gbc_checkBox_13.anchor = GridBagConstraints.WEST;
    gbc_checkBox_13.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_13.gridx = 8;
    gbc_checkBox_13.gridy = 5;
    panel.add( checkBox_13, gbc_checkBox_13 );
    JCheckBox checkBox_14 = new JCheckBox( "B" );
    GridBagConstraints gbc_checkBox_14 = new GridBagConstraints();
    gbc_checkBox_14.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_14.gridx = 9;
    gbc_checkBox_14.gridy = 5;
    panel.add( checkBox_14, gbc_checkBox_14 );
    JLabel label_11 = new JLabel( "AIR" );
    label_11.setForeground( new Color( 0, 0, 128 ) );
    label_11.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_label_11 = new GridBagConstraints();
    gbc_label_11.anchor = GridBagConstraints.WEST;
    gbc_label_11.insets = new Insets( 0, 0, 5, 0 );
    gbc_label_11.gridx = 10;
    gbc_label_11.gridy = 5;
    panel.add( label_11, gbc_label_11 );
    JLabel gasLabel_05 = new JLabel( "GAS05" );
    GridBagConstraints gbc_gasLabel_05 = new GridBagConstraints();
    gbc_gasLabel_05.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_05.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_05.gridx = 1;
    gbc_gasLabel_05.gridy = 6;
    panel.add( gasLabel_05, gbc_gasLabel_05 );
    JSpinner gasO2Spinner_05 = new JSpinner();
    gasO2Spinner_05.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_05.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_05 = new GridBagConstraints();
    gbc_gasO2Spinner_05.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_05.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_05.gridx = 3;
    gbc_gasO2Spinner_05.gridy = 6;
    panel.add( gasO2Spinner_05, gbc_gasO2Spinner_05 );
    JSpinner gasHESpinner_05 = new JSpinner();
    gasHESpinner_05.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_05 = new GridBagConstraints();
    gbc_gasHESpinner_05.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_05.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_05.gridx = 5;
    gbc_gasHESpinner_05.gridy = 6;
    panel.add( gasHESpinner_05, gbc_gasHESpinner_05 );
    JCheckBox checkBox_15 = new JCheckBox( "D1" );
    GridBagConstraints gbc_checkBox_15 = new GridBagConstraints();
    gbc_checkBox_15.anchor = GridBagConstraints.WEST;
    gbc_checkBox_15.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_15.gridx = 7;
    gbc_checkBox_15.gridy = 6;
    panel.add( checkBox_15, gbc_checkBox_15 );
    JCheckBox checkBox_16 = new JCheckBox( "D2" );
    GridBagConstraints gbc_checkBox_16 = new GridBagConstraints();
    gbc_checkBox_16.anchor = GridBagConstraints.WEST;
    gbc_checkBox_16.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_16.gridx = 8;
    gbc_checkBox_16.gridy = 6;
    panel.add( checkBox_16, gbc_checkBox_16 );
    JCheckBox checkBox_17 = new JCheckBox( "B" );
    GridBagConstraints gbc_checkBox_17 = new GridBagConstraints();
    gbc_checkBox_17.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_17.gridx = 9;
    gbc_checkBox_17.gridy = 6;
    panel.add( checkBox_17, gbc_checkBox_17 );
    JLabel label_13 = new JLabel( "AIR" );
    label_13.setForeground( new Color( 0, 0, 128 ) );
    label_13.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_label_13 = new GridBagConstraints();
    gbc_label_13.anchor = GridBagConstraints.WEST;
    gbc_label_13.insets = new Insets( 0, 0, 5, 0 );
    gbc_label_13.gridx = 10;
    gbc_label_13.gridy = 6;
    panel.add( label_13, gbc_label_13 );
    JLabel gasLabel_06 = new JLabel( "GAS06" );
    GridBagConstraints gbc_gasLabel_06 = new GridBagConstraints();
    gbc_gasLabel_06.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_06.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasLabel_06.gridx = 1;
    gbc_gasLabel_06.gridy = 7;
    panel.add( gasLabel_06, gbc_gasLabel_06 );
    JSpinner gasO2Spinner_06 = new JSpinner();
    gasO2Spinner_06.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_06.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_06 = new GridBagConstraints();
    gbc_gasO2Spinner_06.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_06.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasO2Spinner_06.gridx = 3;
    gbc_gasO2Spinner_06.gridy = 7;
    panel.add( gasO2Spinner_06, gbc_gasO2Spinner_06 );
    JSpinner gasHESpinner_06 = new JSpinner();
    gasHESpinner_06.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_06 = new GridBagConstraints();
    gbc_gasHESpinner_06.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_06.insets = new Insets( 0, 0, 5, 5 );
    gbc_gasHESpinner_06.gridx = 5;
    gbc_gasHESpinner_06.gridy = 7;
    panel.add( gasHESpinner_06, gbc_gasHESpinner_06 );
    JCheckBox checkBox_18 = new JCheckBox( "D1" );
    GridBagConstraints gbc_checkBox_18 = new GridBagConstraints();
    gbc_checkBox_18.anchor = GridBagConstraints.WEST;
    gbc_checkBox_18.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_18.gridx = 7;
    gbc_checkBox_18.gridy = 7;
    panel.add( checkBox_18, gbc_checkBox_18 );
    JCheckBox checkBox_19 = new JCheckBox( "D2" );
    GridBagConstraints gbc_checkBox_19 = new GridBagConstraints();
    gbc_checkBox_19.anchor = GridBagConstraints.WEST;
    gbc_checkBox_19.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_19.gridx = 8;
    gbc_checkBox_19.gridy = 7;
    panel.add( checkBox_19, gbc_checkBox_19 );
    JCheckBox checkBox_20 = new JCheckBox( "B" );
    GridBagConstraints gbc_checkBox_20 = new GridBagConstraints();
    gbc_checkBox_20.insets = new Insets( 0, 0, 5, 5 );
    gbc_checkBox_20.gridx = 9;
    gbc_checkBox_20.gridy = 7;
    panel.add( checkBox_20, gbc_checkBox_20 );
    JLabel label_15 = new JLabel( "AIR" );
    label_15.setForeground( new Color( 0, 0, 128 ) );
    label_15.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_label_15 = new GridBagConstraints();
    gbc_label_15.anchor = GridBagConstraints.WEST;
    gbc_label_15.insets = new Insets( 0, 0, 5, 0 );
    gbc_label_15.gridx = 10;
    gbc_label_15.gridy = 7;
    panel.add( label_15, gbc_label_15 );
    JLabel gasLabel_07 = new JLabel( "GAS07" );
    GridBagConstraints gbc_gasLabel_07 = new GridBagConstraints();
    gbc_gasLabel_07.anchor = GridBagConstraints.WEST;
    gbc_gasLabel_07.insets = new Insets( 0, 0, 0, 5 );
    gbc_gasLabel_07.gridx = 1;
    gbc_gasLabel_07.gridy = 8;
    panel.add( gasLabel_07, gbc_gasLabel_07 );
    JSpinner gasO2Spinner_07 = new JSpinner();
    gasO2Spinner_07.setBounds( new Rectangle( 0, 0, 50, 0 ) );
    gasO2Spinner_07.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasO2Spinner_07 = new GridBagConstraints();
    gbc_gasO2Spinner_07.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasO2Spinner_07.insets = new Insets( 0, 0, 0, 5 );
    gbc_gasO2Spinner_07.gridx = 3;
    gbc_gasO2Spinner_07.gridy = 8;
    panel.add( gasO2Spinner_07, gbc_gasO2Spinner_07 );
    JSpinner gasHESpinner_07 = new JSpinner();
    gasHESpinner_07.setAlignmentX( 1.0f );
    GridBagConstraints gbc_gasHESpinner_07 = new GridBagConstraints();
    gbc_gasHESpinner_07.fill = GridBagConstraints.HORIZONTAL;
    gbc_gasHESpinner_07.insets = new Insets( 0, 0, 0, 5 );
    gbc_gasHESpinner_07.gridx = 5;
    gbc_gasHESpinner_07.gridy = 8;
    panel.add( gasHESpinner_07, gbc_gasHESpinner_07 );
    JCheckBox checkBox_21 = new JCheckBox( "D1" );
    GridBagConstraints gbc_checkBox_21 = new GridBagConstraints();
    gbc_checkBox_21.anchor = GridBagConstraints.WEST;
    gbc_checkBox_21.insets = new Insets( 0, 0, 0, 5 );
    gbc_checkBox_21.gridx = 7;
    gbc_checkBox_21.gridy = 8;
    panel.add( checkBox_21, gbc_checkBox_21 );
    JCheckBox checkBox_22 = new JCheckBox( "D2" );
    GridBagConstraints gbc_checkBox_22 = new GridBagConstraints();
    gbc_checkBox_22.anchor = GridBagConstraints.WEST;
    gbc_checkBox_22.insets = new Insets( 0, 0, 0, 5 );
    gbc_checkBox_22.gridx = 8;
    gbc_checkBox_22.gridy = 8;
    panel.add( checkBox_22, gbc_checkBox_22 );
    JCheckBox checkBox_23 = new JCheckBox( "B" );
    GridBagConstraints gbc_checkBox_23 = new GridBagConstraints();
    gbc_checkBox_23.insets = new Insets( 0, 0, 0, 5 );
    gbc_checkBox_23.gridx = 9;
    gbc_checkBox_23.gridy = 8;
    panel.add( checkBox_23, gbc_checkBox_23 );
    JLabel label_17 = new JLabel( "AIR" );
    label_17.setForeground( new Color( 0, 0, 128 ) );
    label_17.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    GridBagConstraints gbc_label_17 = new GridBagConstraints();
    gbc_label_17.anchor = GridBagConstraints.WEST;
    gbc_label_17.gridx = 10;
    gbc_label_17.gridy = 8;
    panel.add( label_17, gbc_label_17 );
    JLabel label_18 = new JLabel( "USERPRESET" );
    label_18.setBounds( 569, 11, 210, 14 );
    add( label_18 );
    JComboBox comboBox = new JComboBox();
    comboBox.setEnabled( false );
    comboBox.setBounds( 569, 31, 210, 20 );
    add( comboBox );
    JButton button_2 = new JButton( "WRITEPRESELECT" );
    button_2.setForeground( Color.RED );
    button_2.setBackground( new Color( 255, 192, 203 ) );
    button_2.setActionCommand( "write_gaslist_preset" );
    button_2.setBounds( 569, 310, 210, 33 );
    add( button_2 );
    JButton button_3 = new JButton( "READPRESET" );
    button_3.setSize( new Dimension( 210, 35 ) );
    button_3.setPreferredSize( new Dimension( 180, 40 ) );
    button_3.setMaximumSize( new Dimension( 160, 40 ) );
    button_3.setMargin( new Insets( 2, 30, 2, 30 ) );
    button_3.setForeground( new Color( 0, 100, 0 ) );
    button_3.setBackground( new Color( 152, 251, 152 ) );
    button_3.setActionCommand( "read_gaslist_preset" );
    button_3.setBounds( 569, 349, 210, 35 );
    add( button_3 );
    JLabel label_19 = new JLabel( "LICENSE" );
    label_19.setBounds( 10, 385, 41, 14 );
    add( label_19 );
  }

  public JLabel getLabel_2()
  {
    return gasLabel_00;
  }
}
