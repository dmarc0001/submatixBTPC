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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.joda.time.DateTime;

/**
 * Dialog "bitte warten"
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 06.12.2013
 */
public class PleaseWaitDialog extends JDialog
{
  private static final long serialVersionUID    = 1L;
  private DateTime          lastActive          = new DateTime();
  private String            titleString         = "TITLE";
  private final JPanel      contentPanel        = new JPanel();
  private String            messageString       = "WAIT";
  private String            messageDetailString = "DETEIL OR NULL";
  private JProgressBar      progressBar;
  private JLabel            messageLabel;
  private JLabel            messageDetailLabel;
  private long              timeOut             = 60 * 1000;

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
    lastActive = new DateTime();
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
    lastActive = new DateTime();
    titleString = title;
    messageString = message;
    messageDetailString = " ";
    constructDialog();
  }

  /**
   * 
   * Konstruktor mit Titel, Nachricht und Detailnachricht
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * Stand: 06.12.2013
   * 
   * @param title
   * @param message
   * @param detail
   */
  public PleaseWaitDialog( String title, String message, String detail )
  {
    titleString = title;
    messageString = message;
    messageDetailString = detail;
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
    setBounds( 100, 100, 450, 115 );
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
    JPanel messagePanel = new JPanel();
    contentPanel.add( messagePanel, BorderLayout.CENTER );
    messagePanel.setLayout( new BoxLayout( messagePanel, BoxLayout.Y_AXIS ) );
    JLabel lblNewLabel = new JLabel( " " );
    lblNewLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
    messagePanel.add( lblNewLabel );
    messageLabel = new JLabel( messageString );
    messageLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
    messagePanel.add( messageLabel );
    messageLabel.setForeground( Color.RED );
    messageLabel.setFont( new Font( "Tahoma", Font.PLAIN, 14 ) );
    messageLabel.setHorizontalAlignment( SwingConstants.CENTER );
    JLabel new2label = new JLabel( " " );
    new2label.setAlignmentX( Component.CENTER_ALIGNMENT );
    new2label.setHorizontalAlignment( SwingConstants.CENTER );
    messagePanel.add( new2label );
    messageDetailLabel = new JLabel( messageDetailString );
    messageDetailLabel.setForeground( Color.BLUE );
    messageDetailLabel.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    messageDetailLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
    messagePanel.add( messageDetailLabel );
  }

  /**
   * 
   * Setze Maximum
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * Stand: 06.12.2013
   * 
   * @param max
   */
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
   *         Stand: 16.04.2012
   */
  public void resetProgress()
  {
    int min = progressBar.getMinimum();
    progressBar.setValue( min );
    lastActive = new DateTime();
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
    lastActive = new DateTime();
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
    lastActive = new DateTime();
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
    lastActive = new DateTime();
  }

  /**
   * 
   * Die Hauptmessage setzen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 15.07.2012
   * @param msg
   */
  public void setMessage( String msg )
  {
    messageString = msg;
    messageLabel.setText( msg );
    lastActive = new DateTime();
  }

  /**
   * 
   * Detailnachricht setzen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 15.07.2012
   * @param detail
   * 
   */
  public void setDetailMessage( String detail )
  {
    messageDetailString = detail;
    messageDetailLabel.setText( detail );
    lastActive = new DateTime();
  }

  /**
   * 
   * Gib Milisekunden seit dem letzten Ereignis zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.09.2012
   * @return milisekunden seti dem letzten Ereignis
   */
  public long getTimeSinceLastEvent()
  {
    return( DateTime.now().getMillis() - lastActive.getMillis() );
  }

  /**
   * 
   * Setze einen Timeout
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.09.2012
   * @param timeout
   */
  public void setTimeout( long timeout )
  {
    this.timeOut = timeout;
  }

  /**
   * 
   * Teste, ob der Timeout abgelaufen ist
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 22.09.2012
   * @return timeout abgelaufen?
   */
  public boolean isTimeout()
  {
    if( timeOut == 0 || lastActive == null ) return( false );
    if( DateTime.now().getMillis() - lastActive.getMillis() > timeOut ) return( true );
    return( false );
  }
}
