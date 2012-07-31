package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import de.dmarcini.submatix.pclogger.utils.BuildVersion;

public class ProgramInfoDialog extends JDialog
{
  /**
   * 
   */
  private static final long serialVersionUID = 1880409081700630690L;
  private final JPanel      contentPanel     = new JPanel();
  private ResourceBundle    stringsBundle    = null;
  private JButton           okButton;
  private final Action      action           = new SwingAction();

  @SuppressWarnings( "unused" )
  private ProgramInfoDialog()
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
   */
  public ProgramInfoDialog( ResourceBundle stringsBundle )
  {
    this.stringsBundle = stringsBundle;
    initGui();
  }

  private void initGui()
  {
    try
    {
      BuildVersion versObj = new BuildVersion();
      setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
      setTitle( stringsBundle.getString( "ProgramInfoDialog.infoDlg.headline" ) );
      setIconImage( Toolkit.getDefaultToolkit().getImage( ProgramInfoDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/45.png" ) ) );
      setBounds( 100, 100, 510, 267 );
      getContentPane().setLayout( new BorderLayout() );
      contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
      getContentPane().add( contentPanel, BorderLayout.CENTER );
      JLabel lblNewLabel = new JLabel( "" );
      lblNewLabel.setIcon( new ImageIcon( ProgramInfoDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/Wiki2.png" ) ) );
      JLabel line01Label = new JLabel( stringsBundle.getString( "ProgramInfoDialog.infoDlg.line1" ) );
      JLabel line02Label = new JLabel( stringsBundle.getString( "ProgramInfoDialog.infoDlg.line2" ) );
      JLabel line03Label = new JLabel( stringsBundle.getString( "ProgramInfoDialog.infoDlg.line3" ) );
      JLabel line04Label = new JLabel( stringsBundle.getString( "ProgramInfoDialog.infoDlg.line4" ) );
      JLabel line05Label = new JLabel( stringsBundle.getString( "ProgramInfoDialog.infoDlg.line5" ) );
      JLabel versionLabel = new JLabel( String.format( stringsBundle.getString( "ProgramInfoDialog.infoDlg.version" ), versObj.getVersion() ) );
      versionLabel.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
      JLabel buildNumLabel = new JLabel( String.format( stringsBundle.getString( "ProgramInfoDialog.infoDlg.build" ), versObj.getBuild() ) );
      buildNumLabel.setFont( new Font( "Tahoma", Font.ITALIC, 11 ) );
      buildNumLabel.setForeground( Color.GRAY );
      JLabel buildDateLabel = new JLabel( String.format( stringsBundle.getString( "ProgramInfoDialog.infoDlg.buildDate" ), versObj.getBuildDate() ) );
      buildDateLabel.setFont( new Font( "Tahoma", Font.ITALIC, 11 ) );
      buildDateLabel.setForeground( Color.GRAY );
      GroupLayout gl_contentPanel = new GroupLayout( contentPanel );
      gl_contentPanel.setHorizontalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING )
              .addGroup(
                      gl_contentPanel
                              .createSequentialGroup()
                              .addContainerGap()
                              .addGroup(
                                      gl_contentPanel
                                              .createParallelGroup( Alignment.LEADING )
                                              .addGroup(
                                                      gl_contentPanel
                                                              .createSequentialGroup()
                                                              .addComponent( lblNewLabel )
                                                              .addGap( 26 )
                                                              .addGroup(
                                                                      gl_contentPanel.createParallelGroup( Alignment.LEADING, false )
                                                                              .addComponent( buildDateLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                              .addComponent( buildNumLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                              .addComponent( versionLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                              .addComponent( line04Label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                              .addComponent( line03Label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                              .addComponent( line02Label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                              .addComponent( line01Label, GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE ) )
                                                              .addContainerGap( 29, Short.MAX_VALUE ) )
                                              .addGroup(
                                                      gl_contentPanel.createSequentialGroup().addComponent( line05Label, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE )
                                                              .addGap( 29 ) ) ) ) );
      gl_contentPanel.setVerticalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING ).addGroup(
              gl_contentPanel
                      .createSequentialGroup()
                      .addContainerGap()
                      .addGroup(
                              gl_contentPanel
                                      .createParallelGroup( Alignment.LEADING )
                                      .addGroup(
                                              gl_contentPanel.createSequentialGroup().addComponent( line01Label ).addPreferredGap( ComponentPlacement.RELATED )
                                                      .addComponent( line02Label ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( line03Label )
                                                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( line04Label ).addPreferredGap( ComponentPlacement.UNRELATED )
                                                      .addComponent( versionLabel ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( buildNumLabel )
                                                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( buildDateLabel ) ).addComponent( lblNewLabel ) )
                      .addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( line05Label ).addContainerGap( 28, Short.MAX_VALUE ) ) );
      contentPanel.setLayout( gl_contentPanel );
      {
        JPanel buttonPane = new JPanel();
        getContentPane().add( buttonPane, BorderLayout.SOUTH );
        {
          okButton = new JButton( "OK" );
          okButton.setAction( action );
          okButton.setActionCommand( "OK" );
          getRootPane().setDefaultButton( okButton );
        }
        GroupLayout gl_buttonPane = new GroupLayout( buttonPane );
        gl_buttonPane.setHorizontalGroup( gl_buttonPane.createParallelGroup( Alignment.LEADING ).addGroup(
                Alignment.TRAILING,
                gl_buttonPane.createSequentialGroup().addContainerGap( 349, Short.MAX_VALUE ).addComponent( okButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE )
                        .addContainerGap() ) );
        gl_buttonPane.setVerticalGroup( gl_buttonPane.createParallelGroup( Alignment.LEADING ).addGroup(
                gl_buttonPane.createSequentialGroup().addComponent( okButton ).addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) );
        buttonPane.setLayout( gl_buttonPane );
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  public void showDialog()
  {
    setVisible( true );
  }

  private class SwingAction extends AbstractAction
  {
    public SwingAction()
    {
      putValue( NAME, "OK" );
      putValue( SHORT_DESCRIPTION, "OK" );
    }

    @Override
    public void actionPerformed( ActionEvent ev )
    {
      dispose();
    }
  }
}
