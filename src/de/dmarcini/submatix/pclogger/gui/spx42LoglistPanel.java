package de.dmarcini.submatix.pclogger.gui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JPanel;

/**
 * 
 * Panel zeigt die Liste der Logeinträge an
 * 
 * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.gui
 * 
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 *         Stand: 22.04.2012
 */
public class spx42LoglistPanel extends JPanel
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected Logger          LOGGER           = null;

  /**
   * Create the panel.
   */
  @SuppressWarnings( "unused" )
  private spx42LoglistPanel()
  {
    setLayout( null );
    initPanel();
  }

  public spx42LoglistPanel( Logger LOGGER )
  {
    this.LOGGER = LOGGER;
    initPanel();
  }

  private void initPanel()
  {
    //
  }

  public void setGlobalChangeListener( MainCommGUI mainCommGUI )
  {
    //
  }

  public int setLanguageStrings( ResourceBundle stringsBundle )
  {
    try
    {
      //
    }
    catch( NullPointerException ex )
    {
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( -1 );
    }
    catch( MissingResourceException ex )
    {
      System.out.println( "ERROR set language strings - the given key can be found <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    catch( ClassCastException ex )
    {
      System.out.println( "ERROR set language strings <" + ex.getMessage() + "> ABORT!" );
      return( 0 );
    }
    return( 1 );
  }

  public void setAllGasPanelsEnabled( boolean en )
  {
    //
  }
}
