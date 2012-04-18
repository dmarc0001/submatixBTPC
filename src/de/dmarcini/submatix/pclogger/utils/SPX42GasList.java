package de.dmarcini.submatix.pclogger.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

//@formatter:off
public class SPX42GasList implements ISPX42GasList
{
  private static int GASCOUNT                                = 8;
  protected Logger                                    LOGGER = null;
  private final Pattern                       fieldPatternDp = Pattern.compile( ":" );
  protected boolean                                      log = false;
  protected final int                                   n2[] = new int[GASCOUNT];
  protected final int                                   he[] = new int[GASCOUNT];
  protected final int                              bailout[] = new int[GASCOUNT];
  protected final int                              diluent[] = new int[GASCOUNT];
  protected final boolean                             init[] = new boolean[GASCOUNT]; 
  protected int                                      currgas = 0;
  protected boolean                            isInitialized = false;

//@formatter:on
  /**
   * 
   * Privater (und damit gesperrter) Konstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.04.2012
   */
  @SuppressWarnings( "unused" )
  private SPX42GasList()
  {};

  /**
   * 
   * Öffentlicher Konstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.04.2012
   * @param lg
   */
  public SPX42GasList( Logger lg )
  {
    LOGGER = lg;
    if( lg == null )
      log = false;
    else
      log = true;
    isInitialized = false;
    // Alle Gase als niocht initialisiert markieren
    for( int i = 0; i < GASCOUNT; i++ )
    {
      init[i] = false;
    }
  }

  /**
   * 
   * Ein Kopierkonstruktor
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 18.04.2012
   * @param gl
   *          das zu kopierende Objekt
   */
  public SPX42GasList( SPX42GasList gl )
  {
    LOGGER = gl.LOGGER;
    log = gl.log;
    for( int i = 0; i < GASCOUNT; i++ )
    {
      n2[i] = gl.n2[i];
      he[i] = gl.he[i];
      bailout[i] = gl.bailout[i];
      diluent[i] = gl.diluent[i];
      init[i] = gl.init[i];
      currgas = gl.currgas;
      isInitialized = gl.isInitialized;
    }
  }

  @Override
  public boolean setGas( String fromSpx )
  {
    // Kommando SPX_GET_SETUP_GASLIST ~39
    // ~39:NR:N2:HE:BO:DI:CU
    // NR: Nummer des Gases 0..7
    // N2: Sticksoff in %
    // HE: Heluim in %
    // BO: Bailout
    // DI: Diluent 1 oder 2
    // CU: Current Gas
    if( log ) LOGGER.log( Level.FINE, "setGas() <" + fromSpx + ">" );
    String[] fields = fieldPatternDp.split( fromSpx );
    int[] vals = new int[6];
    try
    {
      vals[0] = Integer.parseInt( fields[1], 16 ); // Gasnummer
      vals[1] = Integer.parseInt( fields[2], 16 ); // Stickstoff
      vals[2] = Integer.parseInt( fields[3], 16 ); // Helium
      vals[3] = Integer.parseInt( fields[4], 16 ); // Bailout
      vals[4] = Integer.parseInt( fields[5], 16 ); // Diluent
      vals[5] = Integer.parseInt( fields[5], 16 ); // Current Gas
    }
    catch( NumberFormatException ex )
    {
      if( log ) LOGGER.log( Level.SEVERE, "setGas() <" + fromSpx + "> - not expected String!" );
      return false;
    }
    // die Werte zuordnen
    n2[vals[0]] = vals[1];
    he[vals[0]] = vals[2];
    bailout[vals[0]] = vals[3];
    diluent[vals[0]] = vals[4];
    if( vals[5] > 0 )
    {
      currgas = vals[0];
    }
    init[vals[0]] = true;
    if( log )
      LOGGER.log( Level.INFO, String.format( "gas: NR %d: %d%% O2, %d%% HE, %d%% N2, DIL: %d, Bailout: %d...", vals[0], 100 - n2[vals[0]] - he[vals[0]], he[vals[0]], n2[vals[0]],
              diluent[vals[0]], bailout[vals[0]] ) );
    // vor dem Ende noch checken, ob nun alle Gase eingetragen sind
    for( int i = 0; i < GASCOUNT; i++ )
    {
      if( init[i] == false )
      {
        return( true );
      }
    }
    isInitialized = true;
    return( true );
  }

  @Override
  public boolean isInitialized()
  {
    return( isInitialized );
  }

  @Override
  public boolean setGas( int number, int o2, int he )
  {
    if( ( o2 + he ) > 100 )
    {
      if( log ) LOGGER.log( Level.SEVERE, "setGas(): o2 + he > 100% ! Not success!" );
      return( false );
    }
    if( number >= GASCOUNT )
    {
      return false;
    }
    this.he[number] = he;
    n2[number] = 100 - he - o2;
    return true;
  }

  @Override
  public int getO2FromGas( int number )
  {
    if( number >= GASCOUNT )
    {
      return( 0 );
    }
    return( 100 - he[number] - n2[number] );
  }

  @Override
  public int getHEFromGas( int number )
  {
    if( number >= GASCOUNT )
    {
      return( 0 );
    }
    return( he[number] );
  }

  @Override
  public int getN2FromGas( int number )
  {
    if( number >= GASCOUNT )
    {
      return( 0 );
    }
    return( n2[number] );
  }

  @Override
  public boolean setDiluent1( int number )
  {
    if( number >= GASCOUNT )
    {
      return false;
    }
    diluent[number] = 1;
    return( true );
  }

  @Override
  public boolean setDiluent2( int number )
  {
    if( number >= GASCOUNT )
    {
      return false;
    }
    diluent[number] = 2;
    return( true );
  }

  @Override
  public boolean setBailout( int number, boolean toSet )
  {
    if( number >= GASCOUNT )
    {
      return false;
    }
    if( toSet )
    {
      bailout[number] = 1;
    }
    else
    {
      bailout[number] = 0;
    }
    return( true );
  }

  @Override
  public int getDiulent1()
  {
    for( int i = 0; i < GASCOUNT; i++ )
    {
      if( diluent[i] == 1 )
      {
        return( i );
      }
    }
    return( 0 );
  }

  @Override
  public int getDiluent2()
  {
    for( int i = 0; i < GASCOUNT; i++ )
    {
      if( diluent[i] == 2 )
      {
        return( i );
      }
    }
    return( 0 );
  }

  @Override
  public boolean isBailout( int number )
  {
    if( number >= GASCOUNT )
    {
      return false;
    }
    if( bailout[number] > 0 )
    {
      return( true );
    }
    return( false );
  }

  @Override
  public int getGasCount()
  {
    return( GASCOUNT );
  }
}
