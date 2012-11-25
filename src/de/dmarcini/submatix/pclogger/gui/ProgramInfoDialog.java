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

  /**
   * 
   * Initiiere die Anzeige
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   */
  private void initGui()
  {
    try
    {
      BuildVersion versObj = new BuildVersion();
      setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
      setTitle( stringsBundle.getString( "ProgramInfoDialog.infoDlg.headline" ) );
      setIconImage( Toolkit.getDefaultToolkit().getImage( ProgramInfoDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/45.png" ) ) );
      setBounds( 100, 100, 453, 405 );
      getContentPane().setLayout( new BorderLayout() );
      contentPanel.setBackground( Color.WHITE );
      contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
      getContentPane().add( contentPanel, BorderLayout.NORTH );
      JLabel lblNewLabel = new JLabel( "" );
      lblNewLabel.setIcon( new ImageIcon( ProgramInfoDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/logosub_400.png" ) ) );
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
      JLabel buildDateLabel = new JLabel( String.format( stringsBundle.getString( "ProgramInfoDialog.infoDlg.buildDate" ),
              versObj.getLocaleDate( stringsBundle.getString( "MainCommGUI.timeFormatterString" ) ) ) );
      buildDateLabel.setFont( new Font( "Tahoma", Font.ITALIC, 11 ) );
      buildDateLabel.setForeground( Color.GRAY );
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
                                                      .createSequentialGroup()
                                                      .addGroup(
                                                              gl_contentPanel.createParallelGroup( Alignment.LEADING, false )
                                                                      .addComponent( buildDateLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                      .addComponent( buildNumLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                      .addComponent( versionLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                      .addComponent( line04Label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                      .addComponent( line03Label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
                                                                      .addComponent( line02Label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                                                      .addContainerGap( 17, Short.MAX_VALUE ) )
                                      .addGroup( gl_contentPanel.createSequentialGroup().addComponent( line05Label, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE ).addGap( 29 ) )
                                      .addGroup(
                                              gl_contentPanel
                                                      .createSequentialGroup()
                                                      .addGroup(
                                                              gl_contentPanel
                                                                      .createParallelGroup( Alignment.TRAILING, false )
                                                                      .addComponent( line01Label, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                              Short.MAX_VALUE )
                                                                      .addComponent( lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                              Short.MAX_VALUE ) ).addContainerGap( 17, Short.MAX_VALUE ) ) ) ) );
      gl_contentPanel.setVerticalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING ).addGroup(
              gl_contentPanel.createSequentialGroup().addContainerGap().addComponent( lblNewLabel ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( line01Label )
                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( line02Label ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( line03Label )
                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( line04Label ).addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( versionLabel )
                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( buildNumLabel ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( buildDateLabel )
                      .addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( line05Label ).addContainerGap( 19, Short.MAX_VALUE ) ) );
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
        gl_buttonPane.setHorizontalGroup( gl_buttonPane.createParallelGroup( Alignment.TRAILING ).addGroup(
                gl_buttonPane.createSequentialGroup().addContainerGap( 352, Short.MAX_VALUE ).addComponent( okButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE )
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

  /**
   * 
   * Zeig das Teil an!
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   */
  public void showDialog()
  {
    setVisible( true );
  }

  /**
   * 
   * Wenn der Button gedrückt wurde...
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   */
  private class SwingAction extends AbstractAction
  {
    /**
     * 
     */
    private static final long serialVersionUID = 4460557581978763994L;

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
