package de.dmarcini.submatix.pclogger.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import de.dmarcini.submatix.pclogger.utils.SpxPcloggerProgramConfig;

public class spx42TestPanel extends JPanel implements ActionListener
{
  /**
   * 
   */
  private static final long        serialVersionUID = -3585312247859587113L;
  protected Logger                 LOGGER           = null;
  private SpxPcloggerProgramConfig progConfig       = null;
  private JTextField               msgTextField;
  private ActionListener           aListener        = null;

  /**
   * 
   * Der Konstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 12.08.2012
   * @param LOGGER
   * @param progConfig
   * @param aListener
   */
  public spx42TestPanel( Logger LOGGER, SpxPcloggerProgramConfig progConfig, ActionListener aListener )
  {
    LOGGER.log( Level.FINE, "constructor..." );
    this.LOGGER = LOGGER;
    this.progConfig = progConfig;
    this.aListener = aListener;
    initPanel();
  }

  private void initPanel()
  {
    setPreferredSize( new Dimension( 697, 504 ) );
    JLabel lblKommando = new JLabel( "command:" );
    msgTextField = new JTextField();
    msgTextField.setForeground( Color.RED );
    msgTextField.setFont( new Font( "Tahoma", Font.BOLD, 14 ) );
    msgTextField.setColumns( 25 );
    JButton btnSend = new JButton( "SEND!" );
    btnSend.addActionListener( this );
    btnSend.setActionCommand( "send_debug_string" );
    GroupLayout groupLayout = new GroupLayout( this );
    groupLayout.setHorizontalGroup( groupLayout.createParallelGroup( Alignment.LEADING ).addGroup(
            groupLayout
                    .createSequentialGroup()
                    .addGap( 43 )
                    .addGroup(
                            groupLayout.createParallelGroup( Alignment.LEADING, false ).addComponent( lblKommando, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE )
                                    .addComponent( msgTextField, Alignment.TRAILING )
                                    .addComponent( btnSend, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
                    .addContainerGap( 543, Short.MAX_VALUE ) ) );
    groupLayout.setVerticalGroup( groupLayout.createParallelGroup( Alignment.LEADING ).addGroup(
            groupLayout.createSequentialGroup().addGap( 57 ).addComponent( lblKommando ).addPreferredGap( ComponentPlacement.UNRELATED )
                    .addComponent( msgTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ).addGap( 18 ).addComponent( btnSend )
                    .addContainerGap( 358, Short.MAX_VALUE ) ) );
    setLayout( groupLayout );
  }

  @Override
  public void actionPerformed( ActionEvent ev )
  {
    if( ev.getSource() instanceof JButton )
    {
      if( ev.getActionCommand().equals( "send_debug_string" ) )
      {
        if( aListener != null )
        {
          LOGGER.fine( "fire Event to main..." );
          // Leite das Ereignis modifiziert weiter
          ActionEvent nEv = new ActionEvent( msgTextField, ev.getID(), ev.getActionCommand() );
          aListener.actionPerformed( nEv );
        }
      }
    }
  }
}
