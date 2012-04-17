/**
 * Hilfefenster
 */
package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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

import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.Dimension;

public class HelpFrameClass extends JFrame implements ActionListener
{
  /**
   * 
   */
  private static final long serialVersionUID = -2698425836684008770L;
  private static ResourceBundle                stringsBundle = null;
  static Logger                       LOGGER = null;
  private JPanel contentPane;
  private boolean log = false;
  private JTextPane htmlTextPane;;

  /**
   * Create the frame.
   * @param programLocale 
   * @param lg 
   */
  public HelpFrameClass(Locale programLocale, Logger lg )
  {
    setMinimumSize(new Dimension(400, 350));
    if( lg != null )
    {
      LOGGER = lg;
      log  = true;
    }
    try
    {
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.res.messages", programLocale );
    }
    catch( MissingResourceException ex )
    {
      System.out.println( "ERROR get resources <" + ex.getMessage() + "> try standart Strings..." );
      stringsBundle = ResourceBundle.getBundle( "de.dmarcini.submatix.pclogger.res.messages" );
    }
    setVisible( true );
    
    setTitle( stringsBundle.getString( "HelpFrameClass.Windowtitle.title" ) );
    setIconImage(Toolkit.getDefaultToolkit().getImage(HelpFrameClass.class.getResource("/de/dmarcini/submatix/pclogger/res/search.png")));
    //setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setBounds( 100, 100, 912, 645 );
    contentPane = new JPanel();
    contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    contentPane.setLayout( new BorderLayout( 0, 0 ) );
    setContentPane( contentPane );
    
    JButton btnClose = new JButton( stringsBundle.getString( "HelpFrameClass.btnClose.text" ) );
    btnClose.addActionListener( this );
    btnClose.setActionCommand( "close" );
    contentPane.add(btnClose, BorderLayout.SOUTH);
    
    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);
    
    htmlTextPane = new JTextPane();
    htmlTextPane.setEditable(false);
    htmlTextPane.setContentType("text/html");
    scrollPane.setViewportView(htmlTextPane);
    
    //
    // Test setzen
    //
    setHelpText(programLocale);
  }
  
  private void setHelpText(Locale programLocale)
  {
    URL url = HelpFrameClass.class.getResource("/de/dmarcini/submatix/pclogger/res/helpFile_" + programLocale.toString() + ".html");
    if( url == null )
    {
      htmlTextPane.setText( "<body><h1>" + stringsBundle.getString( "HelpFrameClass.helpFileNotFound.text" ) + "<h3>/de/dmarcini/submatix/pclogger/res/helpFile_" + programLocale.toString() + ".html</h3></body></html>" );
      return;
    }
    try
    {
      if( log ) LOGGER.log( Level.FINEST, "open helpfile: " + url.getFile() );
      htmlTextPane.setPage( url );
    }
    catch( NullPointerException ex )
    {
      if( log ) LOGGER.log( Level.SEVERE, "Null Pointer Exception! (" + ex.getLocalizedMessage() + ")");
      this.dispose();
    }
    catch( IOException ex )
    {
       if( log ) LOGGER.log( Level.SEVERE, "can't open helpfile! (" + ex.getLocalizedMessage() + ")");
       this.dispose();
    }
    
  }

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    String cmd = ev.getActionCommand();
    
    if( cmd.equals( "close" ))
    {
      this.dispose();
    }
    
  }
}
