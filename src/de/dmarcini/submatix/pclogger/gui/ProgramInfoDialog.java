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
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import de.dmarcini.submatix.pclogger.lang.LangStrings;
import de.dmarcini.submatix.pclogger.utils.BuildVersion;
import de.dmarcini.submatix.pclogger.utils.TextStyleConstants;

/**
 * INFO über das Programm
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 06.12.2013
 */
public class ProgramInfoDialog extends JDialog
{
  /**
   * 
   */
  private static final long       serialVersionUID = 1880409081700630690L;
  private JPanel                  contentPanel     = null;
  private Thread                  scrollThread     = null;
  private static volatile Boolean isRunning        = false;
  private JButton                 okButton;
  private final Action            action           = new SwingAction();
  private JScrollPane             fameScrollPane;
  private JTextPane               fameTextPane;

  /**
   * 
   * Der Konstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   */
  public ProgramInfoDialog()
  {
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
      setTitle( LangStrings.getString( "ProgramInfoDialog.infoDlg.headline" ) );
      setIconImage( Toolkit.getDefaultToolkit().getImage( ProgramInfoDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/45.png" ) ) );
      setBounds( 100, 100, 447, 459 );
      getContentPane().setLayout( new BorderLayout() );
      contentPanel = new JPanel();
      contentPanel.setBackground( Color.WHITE );
      contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
      getContentPane().add( contentPanel, BorderLayout.CENTER );
      JLabel lblNewLabel = new JLabel( "" );
      lblNewLabel.setIcon( new ImageIcon( ProgramInfoDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/logosub_400.png" ) ) );
      JLabel line01Label = new JLabel( LangStrings.getString( "ProgramInfoDialog.infoDlg.line1" ) );
      JLabel line02Label = new JLabel( LangStrings.getString( "ProgramInfoDialog.infoDlg.line2" ) );
      JLabel line03Label = new JLabel( LangStrings.getString( "ProgramInfoDialog.infoDlg.line3" ) );
      JLabel line04Label = new JLabel( LangStrings.getString( "ProgramInfoDialog.infoDlg.line4" ) );
      JLabel line05Label = new JLabel( LangStrings.getString( "ProgramInfoDialog.infoDlg.line5" ) );
      JLabel versionLabel = new JLabel( String.format( LangStrings.getString( "ProgramInfoDialog.infoDlg.version" ), versObj.getVersion() ) );
      versionLabel.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
      JLabel buildNumLabel = new JLabel( String.format( LangStrings.getString( "ProgramInfoDialog.infoDlg.build" ), versObj.getBuild() ) );
      buildNumLabel.setFont( new Font( "Tahoma", Font.ITALIC, 11 ) );
      buildNumLabel.setForeground( Color.GRAY );
      JLabel buildDateLabel = new JLabel( String.format( LangStrings.getString( "ProgramInfoDialog.infoDlg.buildDate" ),
              versObj.getLocaleDate( LangStrings.getString( "MainCommGUI.timeFormatterString" ) ) ) );
      buildDateLabel.setFont( new Font( "Tahoma", Font.ITALIC, 11 ) );
      buildDateLabel.setForeground( Color.GRAY );
      fameScrollPane = new JScrollPane();
      fameScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
      fameScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
      fameScrollPane.setDoubleBuffered( true );
      fameScrollPane.setBorder( new LineBorder( Color.DARK_GRAY ) );
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
                                                              gl_contentPanel.createParallelGroup( Alignment.LEADING )
                                                                      .addComponent( buildDateLabel, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE )
                                                                      .addComponent( buildNumLabel, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE )
                                                                      .addComponent( versionLabel, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE )
                                                                      .addComponent( line04Label, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE )
                                                                      .addComponent( line03Label, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE )
                                                                      .addComponent( line02Label, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE ) ).addGap( 11 ) )
                                      .addGroup( gl_contentPanel.createSequentialGroup().addComponent( line05Label, GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE ).addGap( 29 ) )
                                      .addGroup(
                                              gl_contentPanel
                                                      .createSequentialGroup()
                                                      .addGroup(
                                                              gl_contentPanel
                                                                      .createParallelGroup( Alignment.TRAILING )
                                                                      .addComponent( line01Label, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                              Short.MAX_VALUE )
                                                                      .addComponent( lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                              Short.MAX_VALUE ).addComponent( fameScrollPane, Alignment.LEADING ) ).addGap( 11 ) ) ) ) );
      gl_contentPanel.setVerticalGroup( gl_contentPanel.createParallelGroup( Alignment.LEADING ).addGroup(
              gl_contentPanel.createSequentialGroup().addContainerGap().addComponent( lblNewLabel ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( line01Label )
                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( line02Label ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( line03Label )
                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( line04Label ).addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( versionLabel )
                      .addPreferredGap( ComponentPlacement.RELATED ).addComponent( buildNumLabel ).addPreferredGap( ComponentPlacement.RELATED ).addComponent( buildDateLabel )
                      .addPreferredGap( ComponentPlacement.UNRELATED ).addComponent( line05Label ).addPreferredGap( ComponentPlacement.RELATED )
                      .addComponent( fameScrollPane, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE ) ) );
      fameTextPane = new JTextPane();
      fameTextPane.setAutoscrolls( false );
      fameTextPane.setBorder( null );
      fameTextPane.setEditable( false );
      fameTextPane.setText( "-" );
      fameScrollPane.setViewportView( fameTextPane );
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
    if( scrollThread != null )
    {
      isRunning = false;
      try
      {
        Thread.sleep( 800 );
      }
      catch( InterruptedException ex )
      {}
      scrollThread = null;
    }
    //
    // Creiere einen Threwad, der dsas durchscrollt
    //
    scrollThread = new Thread() {
      @Override
      public void run()
      {
        isRunning = true;
        Point vPoint;
        int diff = 0;
        boolean countUp = true;
        //
        // Viewpoint auf Anfang setzen
        //
        try
        {
          sleep( 500 );
        }
        catch( InterruptedException ex )
        {}
        vPoint = fameScrollPane.getViewport().getViewPosition();
        vPoint.y = 0;
        fameScrollPane.getViewport().setViewPosition( vPoint );
        //
        // und nun wandern lassen
        //
        while( isRunning )
        {
          try
          {
            sleep( 70 );
          }
          catch( InterruptedException ex )
          {}
          JViewport vPort = fameScrollPane.getViewport();
          vPoint = vPort.getViewPosition();
          diff = vPort.getView().getHeight() - vPort.getHeight();
          //
          // ist die Differenz größer als der angezeigte Bereich
          // also würde die Anzeige über den Bereich des textes hinausgehen?
          //
          if( vPoint.y >= diff )
          {
            countUp = false;
            System.out.println( "Ab hier rückwärts zählen" );
          }
          //
          // sind wir an Anfang ganz oben angekommen?
          //
          else if( vPoint.y == 0 )
          {
            try
            {
              sleep( 500 );
            }
            catch( InterruptedException ex )
            {}
            countUp = true;
            System.out.println( "Ab hier vorwärts zählen" );
          }
          //
          // zähle jetzt mal die Pixel hoch oder runter
          //
          if( countUp )
          {
            vPoint.y++;
          }
          else
          {
            vPoint.y--;
          }
          //
          // immer größer oder gleich null und kleiner oder gleich der Differnenz aus ViewHöhe und dem Viewport
          //
          vPoint.y = Math.max( 0, vPoint.y );
          vPoint.y = Math.min( vPort.getView().getHeight() - vPort.getHeight(), vPoint.y );
          vPort.setViewPosition( vPoint );
        }
        System.out.println( "Thread endet......" );
      }
    };
    // Thread benamen
    scrollThread.setName( "scrollThread" );
    // text einfüllen
    setHalOfFame();
    // Thread starten
    scrollThread.start();
    setVisible( true );
  }

  /**
   * 
   * Die Ruhmeshalle füllen
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 02.12.2012
   */
  public void setHalOfFame()
  {
    fameTextPane.setText( "" );
    Document doc = fameTextPane.getDocument();
    try
    {
      doc.insertString( doc.getLength(), " \n", TextStyleConstants.HEAD );
      doc.insertString( doc.getLength(), "Translations:\n", TextStyleConstants.HEAD );
      doc.insertString( doc.getLength(), "deutsch\t", TextStyleConstants.TITLE );
      doc.insertString( doc.getLength(), "Dirk Marciniak\n", TextStyleConstants.NAME );
      doc.insertString( doc.getLength(), "english\t", TextStyleConstants.TITLE );
      doc.insertString( doc.getLength(), "Christian Marciniak\n", TextStyleConstants.NAME );
      doc.insertString( doc.getLength(), "français\t", TextStyleConstants.TITLE );
      doc.insertString( doc.getLength(), "Pilippe Belmont\n", TextStyleConstants.NAME );
      //
      // Dank den Betatestern
      //
      doc.insertString( doc.getLength(), " \n", TextStyleConstants.HEAD );
      doc.insertString( doc.getLength(), "BETA:\n", TextStyleConstants.HEAD );
      doc.insertString( doc.getLength(), "Paul Dressler (D)\n", TextStyleConstants.NAME );
      doc.insertString( doc.getLength(), "Phillipe Belmont (F)\n", TextStyleConstants.NAME );
      doc.insertString( doc.getLength(), "Carsten Schäl (D)\n", TextStyleConstants.NAME );
      doc.insertString( doc.getLength(), "Steffen Paetke (D)\n", TextStyleConstants.NAME );
      //
      // noch ne Zeile
      //
      doc.insertString( doc.getLength(), " \n", TextStyleConstants.HEAD );
    }
    catch( BadLocationException ex )
    {}
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
      isRunning = false;
      dispose();
    }
  }
}
