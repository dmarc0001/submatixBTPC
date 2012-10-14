/**
 * Hilfefenster
 */
package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

public class HelpFrameClass extends JFrame implements ActionListener, HyperlinkListener
{
  /**
   * 
   */
  private static final long     serialVersionUID = -2698425836684008770L;
  private static ResourceBundle stringsBundle    = null;
  static Logger                 LOGGER           = null;
  private final JPanel          contentPane;
  private boolean               log              = false;
  private final JTextPane       htmlTextPane;                             ;

  /**
   * Create the frame.
   * 
   * @param programLocale
   * @param lg
   */
  public HelpFrameClass( Locale programLocale, Logger lg )
  {
    setMinimumSize( new Dimension( 400, 350 ) );
    if( lg != null )
    {
      LOGGER = lg;
      log = true;
    }
    try
    {
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.messages", programLocale );
    }
    catch( MissingResourceException ex )
    {
      System.out.println( "ERROR get resources <" + ex.getMessage() + "> try standart Strings..." );
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.lang.messages" );
    }
    setVisible( true );
    setTitle( stringsBundle.getString( "HelpFrameClass.Windowtitle.title" ) );
    setIconImage( Toolkit.getDefaultToolkit().getImage( HelpFrameClass.class.getResource( "/de/dmarcini/submatix/pclogger/res/search.png" ) ) );
    setBounds( 100, 100, 912, 645 );
    contentPane = new JPanel();
    contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    contentPane.setLayout( new BorderLayout( 0, 0 ) );
    setContentPane( contentPane );
    JButton btnClose = new JButton( stringsBundle.getString( "HelpFrameClass.btnClose.text" ) );
    btnClose.addActionListener( this );
    btnClose.setActionCommand( "close" );
    contentPane.add( btnClose, BorderLayout.SOUTH );
    JScrollPane scrollPane = new JScrollPane();
    contentPane.add( scrollPane, BorderLayout.CENTER );
    htmlTextPane = new JTextPane();
    htmlTextPane.setEditable( false );
    htmlTextPane.setContentType( "text/html" );
    htmlTextPane.setEditable( false );
    htmlTextPane.addHyperlinkListener( this );
    scrollPane.setViewportView( htmlTextPane );
    //
    // Test setzen
    //
    setHelpText( programLocale );
  }

  private void setHelpText( Locale programLocale )
  {
    URL url = HelpFrameClass.class.getResource( "/de/dmarcini/submatix/pclogger/res/helpfiles/" + programLocale.toString() + "_helpFileMain.html" );
    if( url == null )
    {
      htmlTextPane.setText( "<body><h1>" + stringsBundle.getString( "HelpFrameClass.helpFileNotFound.text" ) + "<h3>/de/dmarcini/submatix/pclogger/res/helpfiles/"
              + programLocale.toString() + "_helpFileMain.html</h3></body></html>" );
      return;
    }
    try
    {
      if( log ) LOGGER.log( Level.FINEST, "open helpfile: " + url.getFile() );
      htmlTextPane.setPage( url );
    }
    catch( NullPointerException ex )
    {
      if( log ) LOGGER.log( Level.SEVERE, "Null Pointer Exception! (" + ex.getLocalizedMessage() + ")" );
      this.dispose();
    }
    catch( IOException ex )
    {
      if( log ) LOGGER.log( Level.SEVERE, "can't open helpfile! (" + ex.getLocalizedMessage() + ")" );
      this.dispose();
    }
  }

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    if( cmd.equals( "close" ) )
    {
      this.dispose();
    }
  }

  @Override
  public void hyperlinkUpdate( HyperlinkEvent ev )
  {
    //
    // ist das Ereignis aktivierung eines Hyperlinks?
    //
    if( ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
    {
      // Das Panel ist
      JTextPane pane = ( JTextPane )ev.getSource();
      if( ev instanceof HTMLFrameHyperlinkEvent )
      {
        HTMLFrameHyperlinkEvent evt = ( HTMLFrameHyperlinkEvent )ev;
        HTMLDocument doc = ( HTMLDocument )pane.getDocument();
        doc.processHTMLFrameHyperlinkEvent( evt );
      }
      else
      {
        try
        {
          pane.setPage( ev.getURL() );
        }
        catch( Throwable t )
        {
          t.printStackTrace();
        }
      }
    }
  }
}
