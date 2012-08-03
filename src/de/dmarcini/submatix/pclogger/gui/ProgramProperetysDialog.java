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
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.dmarcini.submatix.pclogger.res.ProjectConst;
import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

public class ProgramProperetysDialog extends JDialog implements ActionListener, MouseMotionListener
{
  private static final long        serialVersionUID    = 4117246672129154876L;
  private Logger                   LOGGER              = null;
  private String                   approveLogButtonText;
  private String                   approveLogButtonTooltip;
  private String                   fileChooserLogTitle;
  private String                   approveDirButtonText;
  private String                   approveDirButtonTooltip;
  private String                   fileChooserDirTitle;
  private final JPanel             contentPanel        = new JPanel();
  private JButton                  btnCancel;
  private JButton                  btnOk;
  private boolean                  closeWithOk         = false;
  private boolean                  wasChangedParameter = false;
  private JLabel                   databaseDirLabel;
  private JLabel                   logfileLabel;
  private JTextField               databaseDirTextField;
  private JTextField               logfileNameTextField;
  private JCheckBox                moveDataCheckBox;
  private SpxPcloggerProgramConfig progConfig;
  private JPanel                   pahtsPanel;
  private JPanel                   unitsPanel;
  private JRadioButton             defaultUnitsRadioButton;
  private JRadioButton             metricUnitsRadioButton;
  private JRadioButton             imperialUnitsRadioButton;
  private JLabel                   defaultUnitsLabel;
  private JLabel                   metricUnitsLabel;
  private JLabel                   imperialUnitsLabel;
  private ButtonGroup              unitsButtonGroup;
  private JButton                  databaseDirFileButton;
  private JButton                  logfileNameButton;

  /**
   * Vor Aufruf schützen
   */
  @SuppressWarnings( "unused" )
  private ProgramProperetysDialog()
  {
    initDialog();
  }

  /**
   * 
   * Konstruiere den Dialog mit den Eingenschaften
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.07.2012
   * @param stringsBundle
   * @param progConfig
   * @param LOGGER
   */
  public ProgramProperetysDialog( ResourceBundle stringsBundle, SpxPcloggerProgramConfig progConfig, Logger LOGGER )
  {
    this.progConfig = progConfig;
    this.LOGGER = LOGGER;
    initDialog();
    setLanguageStrings( stringsBundle );
    databaseDirTextField.setText( progConfig.getDatabaseDir().getAbsolutePath() );
    logfileNameTextField.setText( progConfig.getLogFile().getAbsolutePath() );
    // Buttons entsprechend setzen
    switch ( progConfig.getUnitsProperty() )
    {
      case ProjectConst.UNITS_DEFAULT:
        defaultUnitsRadioButton.setSelected( true );
        LOGGER.fine( "units is DEFAULT in config" );
        break;
      case ProjectConst.UNITS_METRIC:
        metricUnitsRadioButton.setSelected( true );
        LOGGER.fine( "units is METRIC in config" );
        break;
      case ProjectConst.UNITS_IMPERIAL:
        imperialUnitsRadioButton.setSelected( true );
        LOGGER.fine( "units is IMPERIAL in config" );
        break;
      default:
        defaultUnitsRadioButton.setSelected( true );
    }
    wasChangedParameter = false;
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
        LOGGER.fine( "Cancel Dialog." );
        setVisible( false );
        closeWithOk = false;
        return;
      }
      // /////////////////////////////////////////////////////////////////////////
      // Abbrechen
      if( cmd.equals( "set_propertys" ) )
      {
        LOGGER.fine( "Dialog OK pressed..." );
        closeWithOk = false;
        if( wasChangedParameter )
        {
          if( !progConfig.getLogFile().getAbsolutePath().equals( logfileNameTextField.getText() ) )
          {
            // da wurde was geändert!
            progConfig.setLogFile( new File( logfileNameTextField.getText() ) );
          }
          if( !progConfig.getDatabaseDir().getAbsolutePath().equals( databaseDirTextField.getText() ) )
          {
            // da hat einer was dran gemacht
            testMoveDatafiles();
          }
          // Log und Daten über Dialog
          // Einstellung für Maßeinheiten...
          if( defaultUnitsRadioButton.isSelected() && ( progConfig.getUnitsProperty() != ProjectConst.UNITS_DEFAULT ) )
          {
            // da war doch jemand dran!
            progConfig.setUnitsProperty( ProjectConst.UNITS_DEFAULT );
          }
          else if( metricUnitsRadioButton.isSelected() && ( progConfig.getUnitsProperty() != ProjectConst.UNITS_METRIC ) )
          {
            // da war einer dran!
            progConfig.setUnitsProperty( ProjectConst.UNITS_METRIC );
          }
          else if( imperialUnitsRadioButton.isSelected() && ( progConfig.getUnitsProperty() != ProjectConst.UNITS_IMPERIAL ) )
          {
            // da war einer dran!
            progConfig.setUnitsProperty( ProjectConst.UNITS_IMPERIAL );
          }
          // wenn da also was in der Config geändert wurde....
          if( progConfig.isWasChanged() ) closeWithOk = true;
        }
        setVisible( false );
        return;
      }
      else if( cmd.equals( "choose_logfile" ) )
      {
        LOGGER.fine( "choose logfile pressed..." );
        chooseLogFile();
      }
      else if( cmd.equals( "choose_datadir" ) )
      {
        LOGGER.fine( "choose datadir pressed..." );
        chooseDataDir();
      }
      else
      {
        LOGGER.warning( "unknown command <" + cmd + "> recived!" );
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
   * 
   * Verzeichnis für die Daten auswählen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.08.2012
   */
  private void chooseDataDir()
  {
    JFileChooser fileChooser;
    int retVal;
    //
    // Einen Dateiauswahldialog Creieren
    //
    fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
    fileChooser.setDialogTitle( fileChooserDirTitle );
    fileChooser.setDialogType( JFileChooser.CUSTOM_DIALOG );
    fileChooser.setApproveButtonToolTipText( approveDirButtonTooltip );
    // das existierende Logfile voreinstellen
    fileChooser.setSelectedFile( progConfig.getDatabaseDir() );
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
   * 
   * Suche einen Platz und den Namen fürs Logfile
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.08.2012
   */
  private void chooseLogFile()
  {
    JFileChooser fileChooser;
    int retVal;
    //
    // Einen Dateiauswahldialog Creieren
    //
    fileChooser = new JFileChooser();
    fileChooser.setDialogTitle( fileChooserLogTitle );
    fileChooser.setDialogType( JFileChooser.CUSTOM_DIALOG );
    fileChooser.setApproveButtonToolTipText( approveLogButtonTooltip );
    // das existierende Logfile voreinstellen
    fileChooser.setSelectedFile( progConfig.getLogFile() );
    retVal = fileChooser.showDialog( this, approveLogButtonText );
    // Mal sehen, was der User gewollt hat
    if( retVal == JFileChooser.APPROVE_OPTION )
    {
      // Ja, ich wollte das so
      // nach dem nächsten Programmstart dieses File anlegen/nutzen
      logfileNameTextField.setText( fileChooser.getSelectedFile().getAbsolutePath() );
      wasChangedParameter = true;
      LOGGER.fine( "select <" + fileChooser.getSelectedFile().getName() + "> as new logfile after restart." );
    }
  }

  /**
   * 
   * Das eventuell veränderte Objekt zurückgeben
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.07.2012
   * @return config
   */
  public SpxPcloggerProgramConfig getProcConfig()
  {
    return( progConfig );
  }

  /**
   * 
   * Initialisiere das Fenster
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.08.2012
   */
  private void initDialog()
  {
    setResizable( false );
    setIconImage( Toolkit.getDefaultToolkit().getImage( ProgramProperetysDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/search.png" ) ) );
    // setVisible( true );
    setBounds( 100, 100, 750, 345 );
    getContentPane().setLayout( new BorderLayout() );
    contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    getContentPane().add( contentPanel, BorderLayout.SOUTH );
    {
      btnCancel = new JButton( "CANCEL" );
      btnCancel.setHorizontalTextPosition( SwingConstants.LEADING );
      btnCancel.setPreferredSize( new Dimension( 180, 40 ) );
      btnCancel.setMaximumSize( new Dimension( 160, 40 ) );
      btnCancel.setMargin( new Insets( 2, 30, 2, 30 ) );
      btnCancel.setForeground( Color.RED );
      btnCancel.setBackground( new Color( 255, 192, 203 ) );
      btnCancel.setActionCommand( "cancel" );
      btnCancel.addActionListener( this );
      btnCancel.addMouseMotionListener( this );
    }
    {
      btnOk = new JButton( "OK" );
      btnOk.setHorizontalTextPosition( SwingConstants.LEADING );
      btnOk.setPreferredSize( new Dimension( 180, 40 ) );
      btnOk.setMaximumSize( new Dimension( 160, 40 ) );
      btnOk.setMargin( new Insets( 2, 30, 2, 30 ) );
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
            gl_contentPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_contentPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_contentPanel.createSequentialGroup().addComponent( btnCancel, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.RELATED, 394, Short.MAX_VALUE )
                                                    .addComponent( btnOk, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE ) )
                                    .addComponent( unitsPanel, GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE )
                                    .addComponent( pahtsPanel, GroupLayout.PREFERRED_SIZE, 714, GroupLayout.PREFERRED_SIZE ) ).addContainerGap() ) );
    gl_contentPanel.setVerticalGroup( gl_contentPanel.createParallelGroup( Alignment.TRAILING ).addGroup(
            gl_contentPanel
                    .createSequentialGroup()
                    .addContainerGap( 50, Short.MAX_VALUE )
                    .addComponent( pahtsPanel, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE )
                    .addPreferredGap( ComponentPlacement.UNRELATED )
                    .addComponent( unitsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addGroup(
                            gl_contentPanel.createParallelGroup( Alignment.BASELINE ).addComponent( btnOk, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( btnCancel, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE ) ) ) );
    databaseDirLabel = new JLabel( "DATABASEDIR" );
    databaseDirTextField = new JTextField();
    databaseDirTextField.setEditable( false );
    databaseDirTextField.addMouseMotionListener( this );
    databaseDirTextField.setColumns( 10 );
    logfileLabel = new JLabel( "LOGFILENAME" );
    logfileNameTextField = new JTextField();
    logfileNameTextField.setEditable( false );
    logfileNameTextField.addMouseMotionListener( this );
    logfileNameTextField.setColumns( 10 );
    moveDataCheckBox = new JCheckBox( "MOVECHECKBOX" );
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
                                                                                            .addComponent( databaseDirTextField, GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE )
                                                                                            .addPreferredGap( ComponentPlacement.RELATED ) ) )
                                                            .addComponent( databaseDirFileButton, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE ) )
                                            .addComponent( moveDataCheckBox )
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
                                                                                    gl_pahtsPanel.createSequentialGroup()
                                                                                            .addComponent( logfileNameTextField, GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE )
                                                                                            .addPreferredGap( ComponentPlacement.RELATED ) ) )
                                                            .addComponent( logfileNameButton, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE ) ) ).addContainerGap() ) );
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
                                            gl_pahtsPanel.createSequentialGroup().addGroup( gl_pahtsPanel.createSequentialGroup().addComponent( logfileLabel ).addGap( 6 ) )
                                                    .addPreferredGap( ComponentPlacement.RELATED )
                                                    .addComponent( logfileNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ) ) )
                    .addGap( 23 ) ) );
    pahtsPanel.setLayout( gl_pahtsPanel );
    defaultUnitsRadioButton = new JRadioButton( "DEFAULT" );
    defaultUnitsRadioButton.setSelected( true );
    defaultUnitsRadioButton.setActionCommand( "rbutton" );
    defaultUnitsRadioButton.addActionListener( this );
    metricUnitsRadioButton = new JRadioButton( "METRIC" );
    metricUnitsRadioButton.setActionCommand( "rbutton" );
    metricUnitsRadioButton.addActionListener( this );
    imperialUnitsRadioButton = new JRadioButton( "IMPERIAL" );
    imperialUnitsRadioButton.addActionListener( this );
    imperialUnitsRadioButton.setActionCommand( "rbutton" );
    defaultUnitsLabel = new JLabel( "as is" );
    metricUnitsLabel = new JLabel( "to metric" );
    imperialUnitsLabel = new JLabel( "to imperial" );
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
   * 
   * Alle sprachabhängigen String setzen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.08.2012
   * @param stringsBundle
   * @return alles ok?
   */
  public int setLanguageStrings( ResourceBundle stringsBundle )
  {
    try
    {
      setTitle( stringsBundle.getString( "ProgramProperetysDialog.title.text" ) );
      databaseDirLabel.setText( stringsBundle.getString( "ProgramProperetysDialog.databaseDirLabel.text" ) );
      logfileLabel.setText( stringsBundle.getString( "ProgramProperetysDialog.logFileLabel.text" ) );
      databaseDirTextField.setToolTipText( stringsBundle.getString( "ProgramProperetysDialog.databaseDirTextField.tooltiptext" ) );
      logfileNameTextField.setToolTipText( stringsBundle.getString( "ProgramProperetysDialog.logfileNameTextField.tooltiptext" ) );
      moveDataCheckBox.setText( stringsBundle.getString( "ProgramProperetysDialog.moveDataCheckBox.text" ) );
      moveDataCheckBox.setToolTipText( stringsBundle.getString( "ProgramProperetysDialog.moveDataCheckBox.tooltiptext" ) );
      btnCancel.setText( stringsBundle.getString( "ProgramProperetysDialog.btnCancel.text" ) );
      btnCancel.setToolTipText( stringsBundle.getString( "ProgramProperetysDialog.btnCancel.tooltiptext" ) );
      btnOk.setText( stringsBundle.getString( "ProgramProperetysDialog.btnOk.text" ) );
      btnOk.setToolTipText( stringsBundle.getString( "ProgramProperetysDialog.btnOk.tooltiptext" ) );
      ( ( TitledBorder )( pahtsPanel.getBorder() ) ).setTitle( " " + stringsBundle.getString( "ProgramProperetysDialog.pathBorderTitle.text" ) + " " );
      ( ( TitledBorder )( unitsPanel.getBorder() ) ).setTitle( " " + stringsBundle.getString( "ProgramProperetysDialog.unitsBorderTitle.text" ) + " " );
      defaultUnitsLabel.setText( stringsBundle.getString( "ProgramProperetysDialog.defaultUnitsLabel.text" ) );
      metricUnitsLabel.setText( stringsBundle.getString( "ProgramProperetysDialog.metricUnitsLabel.text" ) );
      imperialUnitsLabel.setText( stringsBundle.getString( "ProgramProperetysDialog.imperialUnitsLabel.text" ) );
      approveLogButtonText = stringsBundle.getString( "ProgramProperetysDialog.approveLogButtonText.text" );
      approveLogButtonTooltip = stringsBundle.getString( "ProgramProperetysDialog.approveLogButtonTooltip.text" );
      fileChooserLogTitle = stringsBundle.getString( "ProgramProperetysDialog.fileChooserLogTitle.text" );
      approveDirButtonText = stringsBundle.getString( "ProgramProperetysDialog.approveDirButtonText.text" );
      approveDirButtonTooltip = stringsBundle.getString( "ProgramProperetysDialog.approveDirButtonTooltip.text" );
      fileChooserDirTitle = stringsBundle.getString( "ProgramProperetysDialog.fileChooserDirTitle.text" );
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
   * Den Dialog modal anzeigen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.08.2012
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
   * 
   * Verändere das Datenverzeichnis ggf mit Verschiebung der Daten
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.08.2012
   */
  private void testMoveDatafiles()
  {
    File srcDir, destDir;
    File[] fileList;
    boolean moveWasOk = true;
    //
    // woher und wohin?
    srcDir = new File( progConfig.getDatabaseDir().getAbsolutePath() );
    destDir = new File( databaseDirTextField.getText() );
    if( destDir.getAbsolutePath().equals( srcDir.getAbsolutePath() ) )
    {
      // Kein Grund zur Veranlassung, Gleicheit
      return;
    }
    if( !destDir.exists() )
    {
      // Gibts das noch nicht?
      destDir.mkdirs();
    }
    // geht das nun?
    if( destDir.exists() && destDir.isDirectory() && srcDir.exists() && srcDir.isDirectory() )
    {
      // ja hier geht was
      // nach dem nächsten Programmstart dieses File anlegen/nutzen
      progConfig.setDatabaseDir( destDir );
      LOGGER.fine( "select <" + destDir.getName() + "> as new datadir after restart." );
      //
      // sollen die Daten noch verschoben werden?
      //
      if( moveDataCheckBox.isSelected() )
      {
        // dann wolln wir mal...
        fileList = srcDir.listFiles();
        for( File theFile : fileList )
        {
          if( theFile.renameTo( new File( destDir.getAbsolutePath() + System.getProperty( "file.separator" ) + theFile.getName() ) ) )
          {
            LOGGER.info( "File <" + theFile.getName() + "> was moved to " + System.getProperty( "file.separator" ) + destDir.getAbsolutePath() );
          }
          else
          {
            moveWasOk = false;
            LOGGER.severe( "File <" + theFile.getName() + "> was NOT moved to " + destDir.getAbsolutePath() );
          }
        }
        // So, daten verschoben (voraussichtlich)
        if( moveWasOk )
        {
          // Ok, also könnte das alte Verzeichnis ja weg...
          LOGGER.fine( "source directory can be deleted..." );
          fileList = srcDir.listFiles();
          if( fileList.length == 0 )
          {
            LOGGER.fine( "source directory is empty, delete directory..." );
            // Dateien sind auch nicht mehr übrig...
            if( srcDir.delete() )
            {
              LOGGER.fine( "source directory is empty, delete directory...OK" );
            }
          }
          else
          {
            LOGGER.info( "source directory is NOT empty, can NOT delete directory..." );
          }
        }
      }
    }
    else
    {
      if( ( !destDir.exists() ) || ( !destDir.isDirectory() ) )
      {
        LOGGER.severe( "Destination Folder <" + destDir.getName() + "> is not exist or not a directory!" );
      }
      if( ( !srcDir.exists() ) || ( !srcDir.isDirectory() ) )
      {
        LOGGER.severe( "Source Folder <" + srcDir.getName() + "> is not exist or not a directory!" );
      }
    }
  }
}
