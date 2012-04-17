package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class PleaseWaitDialog extends JDialog
{
  private static final long serialVersionUID = 1L;
  private String            titleString      = "TITLE";
  private final JPanel      contentPanel     = new JPanel();
  private String            messageString    = "WAIT";
  private JProgressBar      progressBar;
  private JLabel            lblLabel;

  /**
   * 
   * Konstruktor des warte-Dialoges
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 15.04.2012
   */
  public PleaseWaitDialog()
  {
    constructDialog();
  }

  /**
   * 
   * Konstruktor mit Titel und Message
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 15.04.2012
   * @param title
   * @param message
   */
  public PleaseWaitDialog( String title, String message )
  {
    titleString = title;
    messageString = message;
    constructDialog();
  }

  /**
   * 
   * Bastel den Dialog zusammen
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 16.04.2012
   */
  private void constructDialog()
  {
    setTitle( titleString );
    setIconImage( Toolkit.getDefaultToolkit().getImage( PleaseWaitDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/Refresh.png" ) ) );
    setResizable( false );
    setBounds( 100, 100, 450, 100 );
    getContentPane().setLayout( new BorderLayout() );
    getContentPane().add( contentPanel, BorderLayout.CENTER );
    contentPanel.setLayout( new BorderLayout( 0, 0 ) );
    {
      progressBar = new JProgressBar();
      progressBar.setIndeterminate( true );
      progressBar.setDoubleBuffered( true );
      progressBar.setMaximum( 40 );
      progressBar.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
      progressBar.setForeground( new Color( 46, 139, 87 ) );
      progressBar.setBackground( Color.LIGHT_GRAY );
      progressBar.setValue( 10 );
      contentPanel.add( progressBar, BorderLayout.SOUTH );
    }
    lblLabel = new JLabel( messageString );
    lblLabel.setForeground( Color.RED );
    lblLabel.setFont( new Font( "Tahoma", Font.PLAIN, 14 ) );
    lblLabel.setHorizontalAlignment( SwingConstants.CENTER );
    contentPanel.add( lblLabel, BorderLayout.CENTER );
  }

  public void setMax( int max )
  {
    progressBar.setMaximum( max );
  }

  /**
   * 
   * Setze die Progressbar auf Minimum!
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 16.04.2012 TODO
   */
  public void resetProgress()
  {
    int min = progressBar.getMinimum();
    progressBar.setValue( min );
  }

  /**
   * 
   * Stelle den Fortschrittsbalken eins weiter
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 16.04.2012
   */
  public void incrementProgress()
  {
    int val = progressBar.getValue();
    if( val < progressBar.getMaximum() )
    {
      progressBar.setValue( ++val );
    }
  }

  /**
   * 
   * Setze den Fortschrittsbalken auf einen Wert
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 16.04.2012
   * @param prg
   */
  public void setProgress( String prg )
  {
    int in;
    try
    {
      in = Integer.parseInt( prg );
      if( in <= progressBar.getMaximum() )
      {
        progressBar.setValue( in );
      }
    }
    catch( NumberFormatException ex )
    {}
  }

  /**
   * 
   * Den fortschrittsballken einen weiter
   * 
   * Project: SubmatixBTConfigPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 16.04.2012
   * @param in
   */
  public void setProgress( int in )
  {
    if( in <= progressBar.getMaximum() )
    {
      progressBar.setValue( in );
    }
  }
}
