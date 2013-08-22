package de.dmarcini.submatix.pclogger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class GasPresetNameDialog extends JDialog implements ActionListener
{
  /**
   * 
   */
  private static final long serialVersionUID = 188040908171560690L;
  private static int        maxChars         = 128;
  private boolean           closeWithOk      = false;
  private final JPanel      contentPanel     = new JPanel();
  private ResourceBundle    stringsBundle    = null;
  private JLabel            nameForPresetLabel;
  private JTextField        nameTextField;
  private JButton           cancelButton;
  private JButton           okButton;

  @SuppressWarnings( "unused" )
  private GasPresetNameDialog()
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
  public GasPresetNameDialog( ResourceBundle stringsBundle )
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
      setModalExclusionType( ModalExclusionType.APPLICATION_EXCLUDE );
      setAlwaysOnTop( true );
      setTitle( stringsBundle.getString( "GasPresetNameDialog.title.text" ) );
      setIconImage( Toolkit.getDefaultToolkit().getImage( GasPresetNameDialog.class.getResource( "/de/dmarcini/submatix/pclogger/res/142.png" ) ) );
      setBounds( 100, 100, 748, 146 );
      getContentPane().setLayout( new BorderLayout() );
      contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
      getContentPane().add( contentPanel, BorderLayout.CENTER );
      okButton = new JButton( stringsBundle.getString( "GasPresetNameDialog.okButton.text" ) );
      okButton.setBounds( 525, 67, 199, 33 );
      okButton.setPreferredSize( new Dimension( 180, 40 ) );
      okButton.setMaximumSize( new Dimension( 160, 40 ) );
      okButton.setMargin( new Insets( 2, 30, 2, 30 ) );
      okButton.setForeground( new Color( 0, 100, 0 ) );
      okButton.setBackground( new Color( 152, 251, 152 ) );
      okButton.setActionCommand( "commit" );
      okButton.addActionListener( this );
      contentPanel.setLayout( null );
      cancelButton = new JButton( stringsBundle.getString( "GasPresetNameDialog.cancelButton.text" ) );
      cancelButton.setBounds( 298, 67, 217, 33 );
      cancelButton.setForeground( Color.RED );
      cancelButton.setBackground( new Color( 255, 192, 203 ) );
      cancelButton.setActionCommand( "cancel" );
      cancelButton.addActionListener( this );
      contentPanel.add( cancelButton );
      contentPanel.add( okButton );
      nameForPresetLabel = new JLabel( stringsBundle.getString( "GasPresetNameDialog.nameForPresetLabel.text" ) );
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
