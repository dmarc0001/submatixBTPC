//@formatter:off
/*
    programm: SubmatixSPXLog
    purpose:  configuration and read logs from SUBMATIX SPX42 divecomputer via Bluethooth    
    Copyright (C) 2012  Dirk Marciniak

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/
*/
//@formatter:on
package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.dmarcini.submatix.pclogger.lang.LangStrings;
import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.LogDerbyDatabaseUtil;
import de.dmarcini.submatix.pclogger.utils.NoDatabaseException;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

/**
 * Dialog zu Programmeinstellungen TODO: Datenbank verschieben möglich machen
 * 
 * @author Dirk Marciniak 22.08.2013
 */
public class ProgramProperetysDialog extends JDialog implements ActionListener, MouseMotionListener
{
  private static final long    serialVersionUID    = 4117246672129154876L;
  private Logger               lg                  = null;
  private LogDerbyDatabaseUtil databaseUtil        = null;
  private String               approveLogButtonText;
  private String               approveLogButtonTooltip;
  private String               fileChooserLogTitle;
  private String               approveDirButtonText;
  private String               approveDirButtonTooltip;
  private String               fileChooserDirTitle;
  private final JPanel         contentPanel        = new JPanel();
  private JButton              btnCancel;
  private JButton              btnOk;
  private boolean              closeWithOk         = false;
  private boolean              wasChangedParameter = false;
  private JLabel               databaseDirLabel;
  private JLabel               logfileLabel;
  private JTextField           databaseDirTextField;
  private JTextField           logfileNameTextField;
  private JCheckBox            moveDataCheckBox;
  private JPanel               pahtsPanel;
  private JPanel               unitsPanel;
  private JRadioButton         defaultUnitsRadioButton;
  private JRadioButton         metricUnitsRadioButton;
  private JRadioButton         imperialUnitsRadioButton;
  private JLabel               defaultUnitsLabel;
  private JLabel               metricUnitsLabel;
  private JLabel               imperialUnitsLabel;
  private ButtonGroup          unitsButtonGroup;
  private JButton              databaseDirFileButton;
  private JButton              logfileNameButton;
  private JLabel               exportDirLabel;
  private JTextField           exportDirTextField;
  private JButton              exportDirButton;
  private String               fileChooserExportDirTitle;

  /**
   * Konstruiere den Dialog mit den Eingenschaften Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 18.07.2012
   * @param _databaseUtil
   */
  public ProgramProperetysDialog( LogDerbyDatabaseUtil _databaseUtil )
  {
    this.lg = SpxPcloggerProgramConfig.LOGGER;
    this.databaseUtil = _databaseUtil;
    initDialog();
    lg.debug( "ProgramProperetysDialog created..." );
    JComponent.setDefaultLocale( Locale.getDefault() );
    setLanguageStrings();
    databaseDirTextField.setText( SpxPcloggerProgramConfig.databaseDir.getAbsolutePath() );
    logfileNameTextField.setText( SpxPcloggerProgramConfig.logFile.getAbsolutePath() );
    exportDirTextField.setText( SpxPcloggerProgramConfig.exportDir.getAbsolutePath() );
    // Buttons entsprechend setzen
    switch ( SpxPcloggerProgramConfig.unitsProperty )
    {
      case ProjectConst.UNITS_DEFAULT:
        defaultUnitsRadioButton.setSelected( true );
        lg.debug( "units is DEFAULT in config" );
        break;
      case ProjectConst.UNITS_METRIC:
        metricUnitsRadioButton.setSelected( true );
        lg.debug( "units is METRIC in config" );
        break;
      case ProjectConst.UNITS_IMPERIAL:
        imperialUnitsRadioButton.setSelected( true );
        lg.debug( "units is IMPERIAL in config" );
        break;
      default:
        defaultUnitsRadioButton.setSelected( true );
    }
    wasChangedParameter = false;
    lg.debug( "ProgramProperetysDialog created..." );
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
        lg.debug( "Cancel ProgramProperetysDialog." );
        setVisible( false );
        closeWithOk = false;
        return;
      }
      // /////////////////////////////////////////////////////////////////////////
      // Mache!
      if( cmd.equals( "set_propertys" ) )
      {
        lg.debug( "OK pressed..." );
        closeWithOk = false;
        if( wasChangedParameter )
        {
          if( !SpxPcloggerProgramConfig.logFile.getAbsolutePath().equals( logfileNameTextField.getText() ) )
          {
            // da wurde was geändert!
            SpxPcloggerProgramConfig.logFile = new File( logfileNameTextField.getText() );
            SpxPcloggerProgramConfig.wasChanged = true;
            lg.debug( "logfile name changed..." );
          }
          if( !SpxPcloggerProgramConfig.databaseDir.getAbsolutePath().equals( databaseDirTextField.getText() ) )
          {
            // da hat einer was dran gemacht
            try
            {
              testMoveDatafiles();
            }
            catch( NoDatabaseException ex )
            {
              showErrorDialog( ex.getLocalizedMessage() );
            }
            SpxPcloggerProgramConfig.wasChanged = true;
            lg.debug( "database directory changed..." );
            SpxPcloggerProgramConfig.databaseDir = new File( databaseDirTextField.getText() );
          }
          if( !SpxPcloggerProgramConfig.exportDir.getAbsolutePath().equals( exportDirTextField.getText() ) )
          {
            // da wurde was geändert!
            SpxPcloggerProgramConfig.exportDir = new File( exportDirTextField.getText() );
            SpxPcloggerProgramConfig.wasChanged = true;
            lg.debug( "exportdir changed..." );
          }
          // Log und Daten über Dialog
          // Einstellung für Maßeinheiten...
          if( defaultUnitsRadioButton.isSelected() && ( SpxPcloggerProgramConfig.unitsProperty != ProjectConst.UNITS_DEFAULT ) )
          {
            // da war doch jemand dran!
            SpxPcloggerProgramConfig.unitsProperty = ProjectConst.UNITS_DEFAULT;
            SpxPcloggerProgramConfig.wasChanged = true;
            lg.debug( "units to default changed..." );
          }
          else if( metricUnitsRadioButton.isSelected() && ( SpxPcloggerProgramConfig.unitsProperty != ProjectConst.UNITS_METRIC ) )
          {
            // da war einer dran!
            SpxPcloggerProgramConfig.unitsProperty = ProjectConst.UNITS_METRIC;
            SpxPcloggerProgramConfig.wasChanged = true;
            lg.debug( "units to metric changed..." );
          }
          else if( imperialUnitsRadioButton.isSelected() && ( SpxPcloggerProgramConfig.unitsProperty != ProjectConst.UNITS_IMPERIAL ) )
          {
            // da war einer dran!
            SpxPcloggerProgramConfig.unitsProperty = ProjectConst.UNITS_IMPERIAL;
            SpxPcloggerProgramConfig.wasChanged = true;
            lg.debug( "units to imperial changed..." );
          }
          // wenn da also was in der Config geändert wurde....
          if( SpxPcloggerProgramConfig.wasChanged ) closeWithOk = true;
        }
        setVisible( false );
        return;
      }
      else if( cmd.equals( "choose_logfile" ) )
      {
        lg.debug( "choose logfile pressed..." );
        chooseLogFile();
      }
      else if( cmd.equals( "choose_datadir" ) )
      {
        lg.debug( "choose datadir pressed..." );
        chooseDataDir();
      }
      else if( cmd.equals( "choose_exportdir" ) )
      {
        lg.debug( "choose datadir pressed..." );
        chooseExportDir();
      }
      else
      {
        lg.warn( "unknown command <" + cmd + "> recived!" );
      }
      return;
    }
    else if( ev.getActionCommand().equals( "rbutton" ) )
    {
      // da hat jemand dran rumgefummelt
      wasChangedParameter = true;
    }
  }

  /**
   * Das exportverzeichis auswählen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 28.08.2012
   */
  private void chooseExportDir()
  {
    JFileChooser fileChooser;
    int retVal;
    //
    // Einen Dateiauswahldialog Creieren
    //
    fileChooser = new JFileChooser();
    fileChooser.setLocale( Locale.getDefault() );
    fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
    fileChooser.setDialogTitle( fileChooserExportDirTitle );
    fileChooser.setDialogType( JFileChooser.CUSTOM_DIALOG );
    fileChooser.setApproveButtonToolTipText( approveDirButtonTooltip );
    // das existierende Verzeichnis voreinstellen
    fileChooser.setSelectedFile( SpxPcloggerProgramConfig.exportDir );
    retVal = fileChooser.showDialog( this, approveDirButtonText );
    // Mal sehen, was der User gewollt hat
    if( retVal == JFileChooser.APPROVE_OPTION )
    {
      // Ja, ich wollte das so
      exportDirTextField.setText( fileChooser.getSelectedFile().getAbsolutePath() );
      wasChangedParameter = true;
    }
  }

  /**
   * Verzeichnis für die Daten auswählen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.08.2012
   */
  private void chooseDataDir()
  {
    JFileChooser fileChooser;
    int retVal;
    //
    // Einen Dateiauswahldialog Creieren
    //
    fileChooser = new JFileChooser();
    fileChooser.setLocale( Locale.getDefault() );
    fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
    fileChooser.setDialogTitle( fileChooserDirTitle );
    fileChooser.setDialogType( JFileChooser.CUSTOM_DIALOG );
    fileChooser.setApproveButtonToolTipText( approveDirButtonTooltip );
    // das existierende Logfile voreinstellen
    fileChooser.setSelectedFile( SpxPcloggerProgramConfig.databaseDir );
    retVal = fileChooser.showDialog( this, approveDirButtonText );
    // Mal sehen, was der User gewollt hat
    if( retVal == JFileChooser.APPROVE_OPTION )
    {
      // Ja, ich wollte das so
      databaseDirTextField.setText( fileChooser.getSelectedFile().getAbsolutePath() );
      wasChangedParameter = true;
    }
  }

  /**
   * Suche einen Platz und den Namen fürs Logfile Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.08.2012
   */
  private void chooseLogFile()
  {
    JFileChooser fileChooser;
    int retVal;
    //
    // Einen Dateiauswahldialog Creieren
    //
    fileChooser = new JFileChooser();
    fileChooser.setLocale( Locale.getDefault() );
    fileChooser.setDialogTitle( fileChooserLogTitle );
    fileChooser.setDialogType( JFileChooser.CUSTOM_DIALOG );
    fileChooser.setApproveButtonToolTipText( approveLogButtonTooltip );
    // das existierende Logfile voreinstellen
    fileChooser.setSelectedFile( SpxPcloggerProgramConfig.logFile );
    retVal = fileChooser.showDialog( this, approveLogButtonText );
    // Mal sehen, was der User gewollt hat
    if( retVal == JFileChooser.APPROVE_OPTION )
    {
      // Ja, ich wollte das so
      // nach dem nächsten Programmstart dieses File anlegen/nutzen
      logfileNameTextField.setText( fileChooser.getSelectedFile().getAbsolutePath() );
      wasChangedParameter = true;
      lg.debug( "select <" + fileChooser.getSelectedFile().getName() + "> as new logfile after restart." );
    }
  }

  /**
   * Initialisiere das Fenster Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.08.2012
   */
  private void initDialog()
  {
    setResizable( false );
    setIconImage( Toolkit.getDefaultToolkit().getImage( ProgramProperetysDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/search.png" ) ) );
    // setVisible( true );
    setBounds( 100, 100, 750, 417 );
    getContentPane().setLayout( new BorderLayout() );
    contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    getContentPane().add( contentPanel, BorderLayout.SOUTH );
    {
      btnCancel = new JButton( LangStrings.getString( "ProgramProperetysDialog.btnCancel.text" ) ); //$NON-NLS-1$
      btnCancel.setIcon( new ImageIcon( ProgramProperetysDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/114.png" ) ) );
      btnCancel.setHorizontalAlignment( SwingConstants.LEFT );
      btnCancel.setIconTextGap( 15 );
      btnCancel.setPreferredSize( new Dimension( 180, 40 ) );
      btnCancel.setMaximumSize( new Dimension( 160, 40 ) );
      btnCancel.setMargin( new Insets( 6, 30, 6, 30 ) );
      btnCancel.setForeground( Color.RED );
      btnCancel.setBackground( new Color( 255, 192, 203 ) );
      btnCancel.setActionCommand( "cancel" );
      btnCancel.addActionListener( this );
      btnCancel.addMouseMotionListener( this );
    }
    {
      btnOk = new JButton( LangStrings.getString( "ProgramProperetysDialog.btnOk.text" ) ); //$NON-NLS-1$
      btnOk.setIconTextGap( 15 );
      btnOk.setHorizontalAlignment( SwingConstants.LEFT );
      btnOk.setIcon( new ImageIcon( ProgramProperetysDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/31.png" ) ) );
      btnOk.setPreferredSize( new Dimension( 180, 40 ) );
      btnOk.setMaximumSize( new Dimension( 160, 40 ) );
      btnOk.setMargin( new Insets( 6, 30, 6, 30 ) );
      btnOk.setForeground( new Color( 0, 100, 0 ) );
      btnOk.setBackground( new Color( 152, 251, 152 ) );
      btnOk.setActionCommand( "set_propertys" );
      btnOk.addActionListener( this );
      btnOk.addMouseMotionListener( this );
    }
    unitsPanel = new JPanel();
    unitsPanel.setBorder( new TitledBorder( new LineBorder( new Color( 0, 0, 0 ) ), " UNITS ", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    pahtsPanel = new JPanel();
    pahtsPanel.setBorder( new TitledBorder( new LineBorder( new Color( 0, 0, 0 ) ), " DIRECTORYS ", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
    GroupLayout gl_contentPanel = new GroupLayout( contentPanel );
    gl_contentPanel.setHorizontalGroup( gl_contentPanel.createParallelGroup( Alignment.TRAILING ).addGroup(
            Alignment.LEADING,
            gl_contentPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_contentPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addComponent( pahtsPanel, GroupLayout.PREFERRED_SIZE, 714, GroupLayout.PREFERRED_SIZE )
                                    .addGroup(
                                            gl_contentPanel.createSequentialGroup().addComponent( btnCancel, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.RELATED, 394, Short.MAX_VALUE )
                                                    .addComponent( btnOk, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE ) )
                                    .addComponent( unitsPanel, GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE ) ).addContainerGap() ) );
    gl_contentPanel.setVerticalGroup( gl_contentPanel.createParallelGroup( Alignment.TRAILING ).addGroup(
            gl_contentPanel
                    .createSequentialGroup()
                    .addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                    .addComponent( pahtsPanel, GroupLayout.PREFERRED_SIZE, 225, GroupLayout.PREFERRED_SIZE )
                    .addPreferredGap( ComponentPlacement.UNRELATED )
                    .addComponent( unitsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_contentPanel.createParallelGroup( Alignment.BASELINE ).addComponent( btnOk, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( btnCancel, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE ) ) ) );
    databaseDirLabel = new JLabel( LangStrings.getString( "ProgramProperetysDialog.databaseDirLabel.text" ) ); //$NON-NLS-1$
    databaseDirTextField = new JTextField();
    databaseDirTextField.setEditable( false );
    databaseDirTextField.addMouseMotionListener( this );
    databaseDirTextField.setColumns( 10 );
    logfileLabel = new JLabel( LangStrings.getString( "ProgramProperetysDialog.logFileLabel.text" ) ); //$NON-NLS-1$
    logfileNameTextField = new JTextField();
    logfileNameTextField.setEditable( false );
    logfileNameTextField.addMouseMotionListener( this );
    logfileNameTextField.setColumns( 10 );
    moveDataCheckBox = new JCheckBox( LangStrings.getString( "ProgramProperetysDialog.moveDataCheckBox.text" ) ); //$NON-NLS-1$
    moveDataCheckBox.addMouseMotionListener( this );
    databaseDirFileButton = new JButton( "" );
    databaseDirFileButton.setIcon( new ImageIcon( ProgramProperetysDialog.class.getResource( "/javax/swing/plaf/metal/icons/ocean/directory.gif" ) ) );
    databaseDirFileButton.addActionListener( this );
    databaseDirFileButton.setActionCommand( "choose_datadir" );
    databaseDirFileButton.addMouseMotionListener( this );
    logfileNameButton = new JButton( "" );
    logfileNameButton.setIcon( new ImageIcon( ProgramProperetysDialog.class.getResource( "/javax/swing/plaf/metal/icons/ocean/directory.gif" ) ) );
    logfileNameButton.addActionListener( this );
    logfileNameButton.setActionCommand( "choose_logfile" );
    logfileNameButton.addMouseMotionListener( this );
    exportDirLabel = new JLabel( LangStrings.getString( "ProgramProperetysDialog.exportDirLabel.text" ) ); //$NON-NLS-1$
    exportDirTextField = new JTextField();
    exportDirTextField.setEditable( false );
    exportDirTextField.setColumns( 10 );
    exportDirButton = new JButton( "" );
    exportDirButton.setIcon( new ImageIcon( ProgramProperetysDialog.class.getResource( "/javax/swing/plaf/metal/icons/ocean/directory.gif" ) ) );
    exportDirButton.setActionCommand( "choose_exportdir" );
    exportDirButton.addActionListener( this );
    exportDirButton.addMouseMotionListener( this );
    GroupLayout gl_pahtsPanel = new GroupLayout( pahtsPanel );
    gl_pahtsPanel.setHorizontalGroup( gl_pahtsPanel.createParallelGroup( Alignment.LEADING )
            .addGroup(
                    gl_pahtsPanel
                            .createSequentialGroup()
                            .addContainerGap()
                            .addGroup(
                                    gl_pahtsPanel
                                            .createParallelGroup( Alignment.LEADING )
                                            .addGroup(
                                                    Alignment.TRAILING,
                                                    gl_pahtsPanel
                                                            .createSequentialGroup()
                                                            .addGroup(
                                                                    gl_pahtsPanel
                                                                            .createParallelGroup( Alignment.LEADING )
                                                                            .addGroup(
                                                                                    gl_pahtsPanel.createSequentialGroup()
                                                                                            .addComponent( databaseDirLabel, GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE )
                                                                                            .addGap( 193 ) )
                                                                            .addGroup(
                                                                                    gl_pahtsPanel.createSequentialGroup()
                                                                                            .addComponent( databaseDirTextField, GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE )
                                                                                            .addPreferredGap( ComponentPlacement.RELATED ) ) )
                                                            .addComponent( databaseDirFileButton, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE ) )
                                            .addComponent( moveDataCheckBox, Alignment.TRAILING )
                                            .addGroup(
                                                    Alignment.TRAILING,
                                                    gl_pahtsPanel
                                                            .createSequentialGroup()
                                                            .addGroup(
                                                                    gl_pahtsPanel
                                                                            .createParallelGroup( Alignment.LEADING )
                                                                            .addGroup(
                                                                                    gl_pahtsPanel.createSequentialGroup()
                                                                                            .addComponent( logfileLabel, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE )
                                                                                            .addGap( 267 ) )
                                                                            .addGroup(
                                                                                    Alignment.TRAILING,
                                                                                    gl_pahtsPanel
                                                                                            .createSequentialGroup()
                                                                                            .addGroup(
                                                                                                    gl_pahtsPanel
                                                                                                            .createParallelGroup( Alignment.TRAILING )
                                                                                                            .addComponent( exportDirLabel, Alignment.LEADING,
                                                                                                                    GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE )
                                                                                                            .addComponent( logfileNameTextField, GroupLayout.DEFAULT_SIZE, 606,
                                                                                                                    Short.MAX_VALUE )
                                                                                                            .addComponent( exportDirTextField, Alignment.LEADING,
                                                                                                                    GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE ) )
                                                                                            .addPreferredGap( ComponentPlacement.RELATED ) ) )
                                                            .addGroup(
                                                                    gl_pahtsPanel.createParallelGroup( Alignment.LEADING )
                                                                            .addComponent( logfileNameButton, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE )
                                                                            .addComponent( exportDirButton, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE ) ) ) )
                            .addContainerGap() ) );
    gl_pahtsPanel.setVerticalGroup( gl_pahtsPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_pahtsPanel
                    .createSequentialGroup()
                    .addGroup(
                            gl_pahtsPanel
                                    .createParallelGroup( Alignment.TRAILING )
                                    .addComponent( databaseDirFileButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE )
                                    .addGroup(
                                            gl_pahtsPanel.createSequentialGroup().addComponent( databaseDirLabel ).addPreferredGap( ComponentPlacement.RELATED )
                                                    .addComponent( databaseDirTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.RELATED ) ) )
                    .addGap( 1 )
                    .addComponent( moveDataCheckBox )
                    .addGap( 18 )
                    .addGroup(
                            gl_pahtsPanel
                                    .createParallelGroup( Alignment.TRAILING )
                                    .addComponent( logfileNameButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE )
                                    .addGroup(
                                            gl_pahtsPanel.createSequentialGroup().addComponent( logfileLabel ).addPreferredGap( ComponentPlacement.RELATED )
                                                    .addComponent( logfileNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) ) )
                    .addGap( 18 )
                    .addComponent( exportDirLabel )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_pahtsPanel.createParallelGroup( Alignment.BASELINE )
                                    .addComponent( exportDirTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( exportDirButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE ) ).addGap( 24 ) ) );
    pahtsPanel.setLayout( gl_pahtsPanel );
    defaultUnitsRadioButton = new JRadioButton( LangStrings.getString( "ProgramPropertysDialog.defaultUnitsRadioButton.text" ) ); //$NON-NLS-1$
    defaultUnitsRadioButton.setSelected( true );
    defaultUnitsRadioButton.setActionCommand( "rbutton" );
    defaultUnitsRadioButton.addActionListener( this );
    metricUnitsRadioButton = new JRadioButton( LangStrings.getString( "ProgramPropertysDialog.metricUnitsRadioButton.text" ) ); //$NON-NLS-1$
    metricUnitsRadioButton.setActionCommand( "rbutton" );
    metricUnitsRadioButton.addActionListener( this );
    imperialUnitsRadioButton = new JRadioButton( LangStrings.getString( "ProgramPropertysDialog.imperialUnitsRadioButton.text" ) ); //$NON-NLS-1$
    imperialUnitsRadioButton.addActionListener( this );
    imperialUnitsRadioButton.setActionCommand( "rbutton" );
    defaultUnitsLabel = new JLabel( LangStrings.getString( "ProgramProperetysDialog.defaultUnitsLabel.text" ) ); //$NON-NLS-1$
    metricUnitsLabel = new JLabel( LangStrings.getString( "ProgramProperetysDialog.metricUnitsLabel.text" ) ); //$NON-NLS-1$
    imperialUnitsLabel = new JLabel( LangStrings.getString( "ProgramProperetysDialog.imperialUnitsLabel.text" ) ); //$NON-NLS-1$
    GroupLayout gl_untitsPanel = new GroupLayout( unitsPanel );
    gl_untitsPanel.setHorizontalGroup( gl_untitsPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_untitsPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_untitsPanel.createParallelGroup( Alignment.LEADING, false )
                                    .addComponent( metricUnitsRadioButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                    .addComponent( imperialUnitsRadioButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                    .addComponent( defaultUnitsRadioButton, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE ) )
                    .addGap( 18 )
                    .addGroup(
                            gl_untitsPanel.createParallelGroup( Alignment.LEADING ).addComponent( metricUnitsLabel, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE )
                                    .addComponent( defaultUnitsLabel, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE )
                                    .addComponent( imperialUnitsLabel, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE ) ).addContainerGap() ) );
    gl_untitsPanel.setVerticalGroup( gl_untitsPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_untitsPanel.createSequentialGroup()
                    .addGroup( gl_untitsPanel.createParallelGroup( Alignment.BASELINE ).addComponent( defaultUnitsRadioButton ).addComponent( defaultUnitsLabel ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup( gl_untitsPanel.createParallelGroup( Alignment.BASELINE ).addComponent( metricUnitsRadioButton ).addComponent( metricUnitsLabel ) )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup( gl_untitsPanel.createParallelGroup( Alignment.BASELINE ).addComponent( imperialUnitsRadioButton ).addComponent( imperialUnitsLabel ) )
                    .addContainerGap( 10, Short.MAX_VALUE ) ) );
    unitsPanel.setLayout( gl_untitsPanel );
    contentPanel.setLayout( gl_contentPanel );
    unitsButtonGroup = new ButtonGroup();
    unitsButtonGroup.add( defaultUnitsRadioButton );
    unitsButtonGroup.add( metricUnitsRadioButton );
    unitsButtonGroup.add( imperialUnitsRadioButton );
  }

  @Override
  public void mouseDragged( MouseEvent ev )
  {
    //
  }

  @Override
  public void mouseMoved( MouseEvent ev )
  {
    //
  }

  /**
   * Alle sprachabhängigen String setzen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.08.2012
   * @return alles ok?
   */
  public int setLanguageStrings()
  {
    try
    {
      setTitle( LangStrings.getString( "ProgramProperetysDialog.title.text" ) );
      databaseDirLabel.setText( LangStrings.getString( "ProgramProperetysDialog.databaseDirLabel.text" ) );
      logfileLabel.setText( LangStrings.getString( "ProgramProperetysDialog.logFileLabel.text" ) );
      databaseDirTextField.setToolTipText( LangStrings.getString( "ProgramProperetysDialog.databaseDirTextField.tooltiptext" ) );
      logfileNameTextField.setToolTipText( LangStrings.getString( "ProgramProperetysDialog.logfileNameTextField.tooltiptext" ) );
      moveDataCheckBox.setText( LangStrings.getString( "ProgramProperetysDialog.moveDataCheckBox.text" ) );
      moveDataCheckBox.setToolTipText( LangStrings.getString( "ProgramProperetysDialog.moveDataCheckBox.tooltiptext" ) );
      btnCancel.setText( LangStrings.getString( "ProgramProperetysDialog.btnCancel.text" ) );
      btnCancel.setToolTipText( LangStrings.getString( "ProgramProperetysDialog.btnCancel.tooltiptext" ) );
      btnOk.setText( LangStrings.getString( "ProgramProperetysDialog.btnOk.text" ) );
      btnOk.setToolTipText( LangStrings.getString( "ProgramProperetysDialog.btnOk.tooltiptext" ) );
      ( ( TitledBorder )( pahtsPanel.getBorder() ) ).setTitle( " " + LangStrings.getString( "ProgramProperetysDialog.pathBorderTitle.text" ) + " " );
      ( ( TitledBorder )( unitsPanel.getBorder() ) ).setTitle( " " + LangStrings.getString( "ProgramProperetysDialog.unitsBorderTitle.text" ) + " " );
      defaultUnitsLabel.setText( LangStrings.getString( "ProgramProperetysDialog.defaultUnitsLabel.text" ) );
      metricUnitsLabel.setText( LangStrings.getString( "ProgramProperetysDialog.metricUnitsLabel.text" ) );
      imperialUnitsLabel.setText( LangStrings.getString( "ProgramProperetysDialog.imperialUnitsLabel.text" ) );
      approveLogButtonText = LangStrings.getString( "ProgramProperetysDialog.approveLogButtonText.text" );
      approveLogButtonTooltip = LangStrings.getString( "ProgramProperetysDialog.approveLogButtonTooltip.text" );
      fileChooserLogTitle = LangStrings.getString( "ProgramProperetysDialog.fileChooserLogTitle.text" );
      approveDirButtonText = LangStrings.getString( "ProgramProperetysDialog.approveDirButtonText.text" );
      approveDirButtonTooltip = LangStrings.getString( "ProgramProperetysDialog.approveDirButtonTooltip.text" );
      fileChooserDirTitle = LangStrings.getString( "ProgramProperetysDialog.fileChooserDirTitle.text" );
      fileChooserExportDirTitle = LangStrings.getString( "ProgramProperetysDialog.fileChooserExportDirTitle.text" );
      exportDirLabel.setText( LangStrings.getString( "ProgramProperetysDialog.exportDirLabel.text" ) );
      exportDirTextField.setToolTipText( LangStrings.getString( "ProgramProperetysDialog.exportDirTextField.tooltiptext" ) );
      defaultUnitsRadioButton = new JRadioButton( LangStrings.getString( "ProgramPropertysDialog.defaultUnitsRadioButton.text" ) ); //$NON-NLS-1$
      metricUnitsRadioButton = new JRadioButton( LangStrings.getString( "ProgramPropertysDialog.metricUnitsRadioButton.text" ) );
      imperialUnitsRadioButton = new JRadioButton( LangStrings.getString( "ProgramPropertysDialog.imperialUnitsRadioButton.text" ) );
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
   * Den Dialog modal anzeigen Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.08.2012
   * @return mit OK zurück?
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

  /**
   * Verändere das Datenverzeichnis ggf mit Verschiebung der Daten Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de) Stand: 03.08.2012
   * @throws NoDatabaseException
   */
  private void testMoveDatafiles() throws NoDatabaseException
  {
    File srcDir, destDir;
    //
    // woher und wohin?
    //
    lg.debug( "test to move Files..." );
    srcDir = new File( SpxPcloggerProgramConfig.databaseDir.getAbsolutePath() );
    destDir = new File( databaseDirTextField.getText() );
    if( destDir.getAbsolutePath().equals( srcDir.getAbsolutePath() ) )
    {
      // Kein Grund zur Veranlassung, Gleicheit
      lg.info( "no move, directory not changed..." );
      return;
    }
    if( databaseUtil != null )
    {
      databaseUtil.closeDB();
      try
      {
        // Gib dem Thread etwas Zeit zum beenden
        Thread.sleep( 1200 );
      }
      catch( InterruptedException ex )
      {}
      databaseUtil = null;
    }
    try
    {
      lg.debug( "copy directoy <" + srcDir.getAbsolutePath() + "> to <" + destDir.getAbsolutePath() + ">..." );
      FileUtils.copyDirectory( srcDir, destDir );
    }
    catch( IOException ex )
    {
      if( ( !destDir.exists() ) || ( !destDir.isDirectory() ) )
      {
        lg.error( "Destination Folder <" + destDir.getName() + "> is not exist or not a directory!" );
        throw new NoDatabaseException( LangStrings.getString( "ProgramPropertysDialog.database.isNotexistOrDir.text" ) );
      }
      if( ( !srcDir.exists() ) || ( !srcDir.isDirectory() ) )
      {
        lg.error( "Source Folder <" + srcDir.getName() + "> is not exist or not a directory!" );
        throw new NoDatabaseException( LangStrings.getString( "ProgramPropertysDialog.database.srcIsNotexistOrDir.text" ) );
      }
      showErrorDialog( ex.getLocalizedMessage() );
    }
    // if (!destDir.exists())
    // {
    // // Gibts das noch nicht?
    // lg.debug("make directorys...");
    // destDir.mkdirs();
    // }
    // // geht das nun?
    // if (destDir.exists() && destDir.isDirectory() && srcDir.exists() && srcDir.isDirectory())
    // {
    // lg.debug("all directorys exists...");
    // // ja hier geht was
    // // nach dem nächsten Programmstart dieses File anlegen/nutzen
    // SpxPcloggerProgramConfig.databaseDir = destDir;
    // lg.debug("select <" + destDir.getName() + "> as new datadir after restart.");
    // //
    // // sollen die Daten noch verschoben werden?
    // //
    // if (moveDataCheckBox.isSelected())
    // {
    // // dann wolln wir mal...
    // lg.debug("try to move Files...");
    // fileList = srcDir.listFiles();
    // for (File theFile : fileList)
    // {
    // if (theFile.renameTo(new File(destDir.getAbsolutePath() + System.getProperty("file.separator") + theFile.getName())))
    // {
    // lg.info("File <" + theFile.getName() + "> was moved to " + System.getProperty("file.separator") + destDir.getAbsolutePath());
    // }
    // else
    // {
    // moveWasOk = false;
    // lg.error("File <" + theFile.getName() + "> was NOT moved to " + destDir.getAbsolutePath());
    // }
    // }
    // // So, daten verschoben (voraussichtlich)
    // if (moveWasOk)
    // {
    // // Ok, also könnte das alte Verzeichnis ja weg...
    // lg.debug("source directory can be deleted...");
    // fileList = srcDir.listFiles();
    // if (fileList.length == 0)
    // {
    // lg.debug("source directory is empty, delete directory...");
    // // Dateien sind auch nicht mehr übrig...
    // if (srcDir.delete())
    // {
    // lg.debug("source directory is empty, delete directory...OK");
    // }
    // }
    // else
    // {
    // lg.info("source directory is NOT empty, can NOT delete directory...");
    // }
    // }
    // }
    // }
    // else
    // {
    // if ((!destDir.exists()) || (!destDir.isDirectory()))
    // {
    // lg.error("Destination Folder <" + destDir.getName() + "> is not exist or not a directory!");
    // throw new NoDatabaseException(LangStrings.getString("ProgramPropertysDialog.database.isNotexistOrDir.text"));
    // }
    // if ((!srcDir.exists()) || (!srcDir.isDirectory()))
    // {
    // lg.error("Source Folder <" + srcDir.getName() + "> is not exist or not a directory!");
    // throw new NoDatabaseException(LangStrings.getString("ProgramPropertysDialog.database.srcIsNotexistOrDir.text"));
    // }
    // }
  }

  /**
   * zeige eine Fehlermeldung erstellt: 26.08.2013
   * 
   * @param message
   *          void
   */
  private void showErrorDialog( String message )
  {
    ImageIcon icon = null;
    try
    {
      icon = new ImageIcon( MainCommGUI.class.getResource( "/de/dmarcini/submatix/pclogger/res/Terminate.png" ) );
      JOptionPane.showMessageDialog( this, message, LangStrings.getString( "MainCommGUI.errorDialog.headline" ), JOptionPane.INFORMATION_MESSAGE, icon );
    }
    catch( NullPointerException ex )
    {
      lg.error( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( MissingResourceException ex )
    {
      lg.error( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
    catch( ClassCastException ex )
    {
      lg.error( "ERROR showErrorBox <" + ex.getMessage() + "> ABORT!" );
      return;
    }
  }
}
