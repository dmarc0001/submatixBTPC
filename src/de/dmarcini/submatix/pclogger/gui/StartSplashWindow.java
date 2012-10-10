package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.dmarcini.submatix.pclogger.utils.BuildVersion;

/**
 * das Splash-Fenster
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 10.10.2012
 */
public class StartSplashWindow extends JWindow implements Runnable
{
  /**
   * 
   */
  private static final long serialVersionUID = 812433427614768184L;
  private JPanel            contentPane;
  private volatile long     howLong;
  private boolean           isRunnable       = true;

  public StartSplashWindow()
  {
    this.howLong = 3 * 60 * 10;
    initDialog();
  }

  /**
   * Create the frame.
   */
  public void initDialog()
  {
    TitledBorder tBorder;
    BuildVersion versObj = new BuildVersion();
    //
    setPreferredSize( new Dimension( 440, 440 ) );
    tBorder = new TitledBorder( new EtchedBorder( EtchedBorder.LOWERED, null, null ), "SUBMATIX SPX42 Utility", TitledBorder.CENTER, TitledBorder.BELOW_TOP, new Font( "Tahoma",
            Font.BOLD, 14 ), new Color( 0, 0, 64 ) );
    contentPane = new JPanel();
    contentPane.setBorder( new CompoundBorder( new EtchedBorder( EtchedBorder.LOWERED, new Color( 192, 192, 192 ), new Color( 64, 64, 64 ) ), tBorder ) );
    setContentPane( contentPane );
    JLabel label = new JLabel( "" );
    label.setHorizontalAlignment( SwingConstants.CENTER );
    label.setBorder( new LineBorder( new Color( 255, 0, 0 ), 2, true ) );
    label.setIcon( new ImageIcon( StartSplashWindow.class.getResource( "/de/dmarcini/submatix/pclogger/res/splashFoto.jpg" ) ) );
    JLabel versionLabel = new JLabel( "<VERSION>" );
    versionLabel.setHorizontalAlignment( SwingConstants.CENTER );
    versionLabel.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
    versionLabel.setText( versObj.getVersion() );
    GroupLayout gl_contentPane = new GroupLayout( contentPane );
    gl_contentPane.setHorizontalGroup( gl_contentPane.createParallelGroup( Alignment.LEADING ).addGroup(
            Alignment.TRAILING,
            gl_contentPane
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                            gl_contentPane.createParallelGroup( Alignment.TRAILING ).addComponent( versionLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE )
                                    .addComponent( label, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ).addGap( 25 ) ) );
    gl_contentPane.setVerticalGroup( gl_contentPane.createParallelGroup( Alignment.LEADING ).addGroup(
            gl_contentPane.createSequentialGroup().addComponent( label ).addPreferredGap( ComponentPlacement.RELATED )
                    .addComponent( versionLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE ).addContainerGap( 17, Short.MAX_VALUE ) ) );
    contentPane.setLayout( gl_contentPane );
    pack();
  }

  @Override
  public void run()
  {
    isRunnable = true;
    setLocationRelativeTo( null );
    setVisible( true );
    validate();
    toFront();
    while( isRunnable )
    {
      waitALittle();
    }
    dispose();
  }

  /**
   * 
   * Kunstpausen machen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 10.10.2012
   */
  private synchronized void waitALittle()
  {
    try
    {
      wait( 100 );
      howLong--;
      if( howLong < 1 )
      {
        isRunnable = false;
      }
    }
    catch( InterruptedException ex )
    {}
  }

  /**
   * 
   * Fenster wieder schliessen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 10.10.2012
   */
  public void terminate()
  {
    howLong = 2 * 10; // setze die Timeoutzeit auf 2 Sekunden
  }
}
