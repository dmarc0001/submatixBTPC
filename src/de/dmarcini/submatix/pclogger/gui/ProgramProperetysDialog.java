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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

public class ProgramProperetysDialog extends JDialog implements ActionListener, MouseMotionListener
{
  private static final long        serialVersionUID = 4117246672129154876L;
  private final JPanel             contentPanel     = new JPanel();
  private JButton                  btnCancel;
  private JButton                  btnOk;
  private boolean                  closeWithOk      = false;
  private JLabel                   databaseDirLabel;
  private JLabel                   logfileLabel;
  private JTextField               databaseDirTextField;
  private JTextField               logfileNameTextField;
  private JCheckBox                moveDataCheckBox;
  private SpxPcloggerProgramConfig progConfig;

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
   */
  public ProgramProperetysDialog( ResourceBundle stringsBundle, SpxPcloggerProgramConfig progConfig )
  {
    this.progConfig = progConfig;
    initDialog();
    setLanguageStrings( stringsBundle );
    databaseDirTextField.setText( progConfig.getDatabaseDir().getAbsolutePath() );
    logfileNameTextField.setText( progConfig.getLogFile().getAbsolutePath() );
  }

  private void initDialog()
  {
    setResizable( false );
    setIconImage( Toolkit.getDefaultToolkit().getImage( ProgramProperetysDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/search.png" ) ) );
    // setVisible( true );
    setBounds( 100, 100, 450, 300 );
    getContentPane().setLayout( new BorderLayout() );
    contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    getContentPane().add( contentPanel, BorderLayout.CENTER );
    databaseDirLabel = new JLabel( "DATABASEDIR" );
    logfileLabel = new JLabel( "LOGFILENAME" );
    databaseDirTextField = new JTextField();
    databaseDirTextField.addMouseMotionListener( this );
    databaseDirTextField.setColumns( 10 );
    logfileNameTextField = new JTextField();
    logfileNameTextField.addMouseMotionListener( this );
    logfileNameTextField.setColumns( 10 );
    moveDataCheckBox = new JCheckBox( "MOVECHECKBOX" );
    moveDataCheckBox.addMouseMotionListener( this );
    moveDataCheckBox.setEnabled( false );
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
    GroupLayout gl_contentPanel = new GroupLayout( contentPanel );
    gl_contentPanel.setHorizontalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_contentPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_contentPanel
                                    .createParallelGroup( Alignment.LEADING )
                                    .addGroup(
                                            gl_contentPanel
                                                    .createParallelGroup( Alignment.LEADING )
                                                    .addGroup(
                                                            gl_contentPanel
                                                                    .createParallelGroup( Alignment.LEADING )
                                                                    .addGroup(
                                                                            gl_contentPanel.createSequentialGroup().addComponent( databaseDirLabel )
                                                                                    .addContainerGap( 344, Short.MAX_VALUE ) )
                                                                    .addGroup(
                                                                            gl_contentPanel.createSequentialGroup()
                                                                                    .addComponent( logfileLabel, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE ).addGap( 286 ) )
                                                                    .addGroup(
                                                                            gl_contentPanel
                                                                                    .createSequentialGroup()
                                                                                    .addGroup(
                                                                                            gl_contentPanel
                                                                                                    .createParallelGroup( Alignment.TRAILING, false )
                                                                                                    .addComponent( logfileNameTextField, Alignment.LEADING )
                                                                                                    .addComponent( databaseDirTextField, Alignment.LEADING,
                                                                                                            GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE ) ).addContainerGap() ) )
                                                    .addComponent( moveDataCheckBox ) )
                                    .addGroup(
                                            Alignment.TRAILING,
                                            gl_contentPanel.createSequentialGroup().addComponent( btnCancel, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE )
                                                    .addPreferredGap( ComponentPlacement.UNRELATED )
                                                    .addComponent( btnOk, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE ).addContainerGap() ) ) ) );
    gl_contentPanel.setVerticalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_contentPanel
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent( databaseDirLabel )
                    .addGap( 4 )
                    .addComponent( databaseDirTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                    .addGap( 18 )
                    .addComponent( logfileLabel )
                    .addPreferredGap( ComponentPlacement.RELATED )
                    .addComponent( logfileNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
                    .addGap( 48 )
                    .addComponent( moveDataCheckBox )
                    .addGap( 35 )
                    .addGroup(
                            gl_contentPanel.createParallelGroup( Alignment.BASELINE ).addComponent( btnOk, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( btnCancel, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE ) ).addContainerGap() ) );
    contentPanel.setLayout( gl_contentPanel );
  }

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
      if( cmd.equals( "set_propertys" ) )
      {
        // Plausibilität testen
        // Ist das Verzeichnis vorhanden?
        // TODO: Fehlerbehandlung
        File dbDir = new File( databaseDirTextField.getText() );
        if( dbDir.exists() && dbDir.isDirectory() )
        {
          progConfig.setDatabaseDir( dbDir );
        }
        // Ist das Verzeichnis für die Logdatei vorhanden?
        // TODO: Fehlerbehandlung
        File logFile = new File( logfileNameTextField.getText() );
        if( logFile.getParentFile().isDirectory() )
        {
          progConfig.setLogFile( logFile );
        }
        setVisible( false );
        closeWithOk = true;
        return;
      }
      return;
    }
  }

  @Override
  public void mouseDragged( MouseEvent ev )
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void mouseMoved( MouseEvent ev )
  {
    // TODO Auto-generated method stub
  }
}
