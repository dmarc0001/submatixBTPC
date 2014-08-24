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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import de.dmarcini.submatix.pclogger.lang.LangStrings;

/**
 * Name für ein Gaspreset editieren
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 06.12.2013
 */
public class GasPresetNameDialog extends JDialog implements ActionListener
{
  /**
   * 
   */
  private static final long serialVersionUID = 188040908171560690L;
  private static int        maxChars         = 128;
  private boolean           closeWithOk      = false;
  private final JPanel      contentPanel     = new JPanel();
  private JLabel            nameForPresetLabel;
  private JTextField        nameTextField;
  private JButton           cancelButton;
  private JButton           okButton;

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
  public GasPresetNameDialog()
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
      setModalExclusionType( ModalExclusionType.APPLICATION_EXCLUDE );
      setAlwaysOnTop( true );
      setTitle( LangStrings.getString( "GasPresetNameDialog.title.text" ) );
      setIconImage( Toolkit.getDefaultToolkit().getImage( GasPresetNameDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/142.png" ) ) );
      setBounds( 100, 100, 748, 146 );
      getContentPane().setLayout( new BorderLayout() );
      contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
      getContentPane().add( contentPanel, BorderLayout.CENTER );
      okButton = new JButton( LangStrings.getString( "GasPresetNameDialog.okButton.text" ) );
      okButton.setBounds( 525, 67, 199, 33 );
      okButton.setPreferredSize( new Dimension( 180, 40 ) );
      okButton.setMaximumSize( new Dimension( 160, 40 ) );
      okButton.setMargin( new Insets( 2, 30, 2, 30 ) );
      okButton.setForeground( new Color( 0, 100, 0 ) );
      okButton.setBackground( new Color( 152, 251, 152 ) );
      okButton.setActionCommand( "commit" );
      okButton.addActionListener( this );
      contentPanel.setLayout( null );
      cancelButton = new JButton( LangStrings.getString( "GasPresetNameDialog.cancelButton.text" ) ); //$NON-NLS-1$
      cancelButton.setBounds( 298, 67, 217, 33 );
      cancelButton.setForeground( Color.RED );
      cancelButton.setBackground( new Color( 255, 192, 203 ) );
      cancelButton.setActionCommand( "cancel" );
      cancelButton.addActionListener( this );
      contentPanel.add( cancelButton );
      contentPanel.add( okButton );
      nameForPresetLabel = new JLabel( LangStrings.getString( "GasPresetNameDialog.nameForPresetLabel.text" ) ); //$NON-NLS-1$
      nameForPresetLabel.setBounds( 10, 11, 385, 14 );
      contentPanel.add( nameForPresetLabel );
      nameTextField = new JTextField();
      nameTextField.setSelectedTextColor( new Color( 255, 0, 0 ) );
      nameTextField.setForeground( new Color( 0, 128, 0 ) );
      nameTextField.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
      nameTextField.setDoubleBuffered( true );
      nameTextField.setBounds( 10, 36, 714, 20 );
      nameTextField.setColumns( 128 );
      nameTextField.setDocument( new JTextFieldLimit( maxChars ) );
      contentPanel.add( nameTextField );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * 
   * Zeige das Fenster MODAL an
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 31.07.2012
   * @return Mit Ok geschlossen?
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
      if( cmd.equals( "commit" ) )
      {
        setVisible( false );
        closeWithOk = true;
        return;
      }
      return;
    }
  }

  /**
   * 
   * lese den Namen des Presets
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.08.2012
   * @return Notes
   */
  @Override
  public String getName()
  {
    if( nameTextField == null ) return( null );
    return( nameTextField.getText() );
  }

  /**
   * 
   * Setze Name des Preset
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 03.08.2012
   * @param msg
   */
  @Override
  public void setName( String msg )
  {
    nameTextField.setText( msg );
  }

  protected class JTextFieldLimit extends PlainDocument
  {
    /**
     * 
     */
    private static final long serialVersionUID = -2816882742026262454L;
    private int               limit;
    // optional uppercase conversion
    private boolean           toUppercase      = false;

    public JTextFieldLimit()
    {
      super();
    }

    public JTextFieldLimit( int limit )
    {
      super();
      this.limit = limit;
    }

    public JTextFieldLimit( int limit, boolean upper )
    {
      super();
      this.limit = limit;
      toUppercase = upper;
    }

    @Override
    public void insertString( int offset, String str, AttributeSet attr ) throws BadLocationException
    {
      if( str == null )
      {
        return;
      }
      if( ( getLength() + str.length() ) <= limit )
      {
        if( toUppercase )
        {
          str = str.toUpperCase();
        }
        super.insertString( offset, str, attr );
      }
    }
  }
}
