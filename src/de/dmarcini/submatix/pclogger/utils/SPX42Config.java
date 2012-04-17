/**
* Implementation der Konfiguration für SPX42
 *
 * Project: SubmatixBTConfigPC
 * Package: de.dmarcini.submatix.pclogger.utils
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 * Stand: 30.12.2011
 */
package de.dmarcini.submatix.pclogger.utils;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Objekt zur Sicherung der SPX42 Konfiguration
 *
 * Project: SubmatixBTConfigPC
 * Package: de.dmarcini.submatix.pclogger.utils
 * @author Dirk Marciniak (dirk_marciniak@arcor.de)
 * 
 * Stand: 02.01.2012
 */
public class SPX42Config implements ISPX42Config
{
  //@formatter:off
  //
  protected Logger                                    LOGGER = null;
  private boolean                      wasCorrectInitialized = false;
  private final Pattern                       fieldPatternDp = Pattern.compile( ":" );
  protected String                                deviceName = "no name";
  protected String                           firmwareVersion = "0";
  protected String                              serialNumber = "0";
  protected int                                  gradientLow = 0x19;
  protected int                                 gradientHigh = 0x55;
  protected int                                 presetNumber = 2;
  protected ArrayList<String>                          prefs = new ArrayList<String>();
  protected int                                 lastDecoStop = 3;
  protected boolean                          enableDeepStops = false;
  protected boolean                       enableDynGradients = false;
  protected int                            displayBrightness = 0;
  protected int                           displayOrientation = 0;
  protected int                             unitsTemperature = 0;
  protected int                                   unitsDepth = 0;
  protected int                                 unitsSalnyty = 0;
  protected int                                 autoSetpoint = 0;
  protected int                                          ppo = 1;
  protected boolean                                sensorsOn = false;
  protected boolean                               pscrModeOn = false;
  protected int                                 sensorsCount = 3;
  protected boolean                                  soundOn = false;
  protected int                                  logInterval = 0;

  
  //
  //@formatter:on

  /**
   * 
   * Der Konstruktor, füllt falls notwendig sinnvolle Anfangswerte
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.utils
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 02.01.2012
   */
  public SPX42Config()
  {
    // Preferenzen als String erzeugen und ablegen
    prefs.add( 0, "22:45" );      // very conservative
    prefs.add( 1, "1e:55" );      // conservative
    prefs.add( 2, "19:55" );      // moderate
    prefs.add( 3, "0f:59" );      // agressive
    prefs.add( 4, "0a:64" );      // very aggressive
    prefs.add( 5, "32:32" );      // custom
    wasCorrectInitialized = false;
  }
  
  /**
   * 
   * Kopierkonstruktor
   *
   * Project: SubmatixBTConfigPC
   * Package: de.dmarcini.submatix.pclogger.utils
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   * Stand: 03.01.2012
   * @param cf 
   */
  @SuppressWarnings( "unchecked" )
  public SPX42Config( SPX42Config cf )
  {
    LOGGER                = cf.LOGGER;
    wasCorrectInitialized = cf.wasCorrectInitialized;
    serialNumber          = cf.serialNumber;
    deviceName            = cf.deviceName;
    firmwareVersion       = cf.firmwareVersion;
    gradientLow           = cf.gradientLow;
    gradientHigh          = cf.gradientHigh;
    presetNumber          = cf.presetNumber;
    prefs                 = ( ArrayList<String> )cf.prefs.clone();
    lastDecoStop          = cf.lastDecoStop;
    enableDeepStops       = cf.enableDeepStops;
    enableDynGradients    = cf.enableDynGradients;
    displayBrightness     = cf.displayBrightness;
    displayOrientation    = cf.displayOrientation;
    unitsTemperature      = cf.unitsTemperature;
    unitsDepth            = cf.unitsDepth;
    unitsSalnyty          = cf.unitsSalnyty;
    autoSetpoint          = cf.autoSetpoint;
    ppo                   = cf.ppo;
    sensorsOn             = cf.sensorsOn;
    pscrModeOn            = cf.pscrModeOn;
    sensorsCount          = cf.sensorsCount;
    soundOn               = cf.soundOn;
    logInterval           = cf.logInterval;
  }

  /**
   * Vergleiche mit anderer Config
   */
  @Override
  public boolean compareWith( SPX42Config cf )
  {
    // immer wenn was nicht übereinstimmt ist Übertragung norwendig
    if( ! wasCorrectInitialized ) return( false );
    if( ! serialNumber.equals( cf.serialNumber ) ) return( false );
    if( ! deviceName.equals( cf.deviceName ) ) return( false );
    if( ! firmwareVersion.equals( cf.firmwareVersion ) ) return( false );
    if( gradientLow        != cf.gradientLow ) return( false );
    if( gradientHigh       != cf.gradientHigh ) return( false );
    if( presetNumber       != cf.presetNumber ) return( false );
    if( ! prefs.equals( cf.prefs ) ) return( false );;
    if( lastDecoStop       != cf.lastDecoStop ) return( false );
    if( enableDeepStops    != cf.enableDeepStops ) return( false );
    if( enableDynGradients != cf.enableDynGradients ) return( false );
    if( displayBrightness  != cf.displayBrightness ) return( false );
    if( displayOrientation != cf.displayOrientation ) return( false );
    if( unitsTemperature   != cf.unitsTemperature ) return( false );
    if( unitsDepth         != cf.unitsDepth ) return( false );
    if( unitsSalnyty       != cf.unitsSalnyty ) return( false );
    if( autoSetpoint       != cf.autoSetpoint ) return( false );
    if( ppo                != cf.ppo ) return( false );
    if( sensorsOn          != cf.sensorsOn ) return( false );
    if( pscrModeOn         != cf.pscrModeOn ) return( false );
    if( sensorsCount       != cf.sensorsCount ) return( false );
    if( soundOn            != cf.soundOn ) return( false );
    if( logInterval        != cf.logInterval ) return( false );
    return( true );
  }

  /**
   * Seriennummer setzen
   */
  @Override
  public void setSerial( String serial )
  {
    if( serial != null )
    {
      serialNumber = serial;
    }
  }

  /**
   * Seriennummer lesen
   */
  @Override
  public String getSerial()
  {
    return( serialNumber );
  }

  
  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#setDecoGf(int, int)
   */
  @Override
  public void setDecoGf( int gfLow, int gfHigh )
  {
    gradientLow = gfLow;
    gradientHigh = gfHigh;
  }

  @Override
  public void setDecoGfLow( int gfLow )
  {
    gradientLow = gfLow;
  }
  
  @Override
  public void setDecoGfHigh( int gfHigh )
  {
    gradientHigh = gfHigh;
  }


  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#setDecoGf(String)
   */
  @Override
  public boolean setDecoGf( String fromSpx )
  {
    // Kommando DEC liefert zurück:
    // ~34:LL:HH:D:Y:C
    // LL=GF-Low, HH=GF-High,
    // D=Deepstops (0/1)
    // Y=Dynamische Gradienten (0/1)
    // C=Last Decostop (0=3 Meter/1=6 Meter)
    if( LOGGER != null ) LOGGER.log( Level.FINEST, "setDecoGf() <" + fromSpx + ">");
    String[] fields = fieldPatternDp.split( fromSpx );
    int[] vals = new int[5];
    try
    {
      vals[0] = Integer.parseInt( fields[1], 16 );  // Low
      vals[1] = Integer.parseInt( fields[2], 16 );  // High
      vals[2] = Integer.parseInt( fields[3] );  // Deepstops
      vals[3] = Integer.parseInt( fields[4] );  // Dyn gradienten
      vals[4] = Integer.parseInt( fields[5] );  // Last Stop
    }
    catch ( NumberFormatException ex )
    {
      if( LOGGER != null ) LOGGER.log( Level.SEVERE, "setDecoGf() <" + fromSpx + "> - not expected String!");
      return false;
    }
    gradientLow = vals[0];
    gradientHigh = vals[1];
    if( vals[2] == 0 ) { enableDeepStops = false; } else { enableDeepStops = true; }
    if( vals[3] == 0 ) { enableDynGradients = false; } else { enableDynGradients = true; }
    if( vals[4] == 0 ) { lastDecoStop = 3; } else { lastDecoStop = 6; }
    return( true );
  }


  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#getDecoGfLow()
   */
  @Override
  public int getDecoGfLow()
  {
    return( gradientLow );
  }

  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#getDecoGfHigh()
   */
  @Override
  public int getDecoGfHigh()
  {
    return( gradientHigh );
  }

  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#setDecoGfPreset(int)
   */
  @Override
  public void setDecoGfPreset( int preset )
  {
    // wenn das in der erlaubten Größe liegt, sonst EXCEPTION
    if( preset < 0 || preset >= prefs.size() )
    {
      throw new IndexOutOfBoundsException( "not a preset number!" );
    }
      
    // werte aus dem preset holen
    String presetStr = prefs.get(  preset  );
    String[] fields = fieldPatternDp.split( presetStr );
    // wenn was schief geht => moderate!
    try
    {
      gradientLow = Integer.parseInt(fields[0],16);
    }
    catch ( NumberFormatException ex )
    {
      gradientLow = 0x19;
      gradientHigh = 0x55;
      presetNumber = 2;
      return;
    }
    try
    {
      gradientHigh = Integer.parseInt(fields[1],16);
    }
    catch ( NumberFormatException ex )
    {
      gradientLow = 0x19;
      gradientHigh = 0x55;
      presetNumber = 2;
      return;
    }
    // scheinbar geklappt, presetnummer setzen
    presetNumber = preset;
  }

  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#getDecoGfPreset()
   */
  @Override
  public int getDecoGfPreset()
  {
    // so sollte as dann aussehen
    String presTest = String.format( "%02x:%02x", gradientLow, gradientHigh );
    if( LOGGER != null ) LOGGER.log( Level.FINEST, "compare to <" + presTest + ">");
    // custom voreingestellt
    int tempPre =5; 
    // durchprobieren
    for( int index = 0; index < prefs.size(); index++ )
    {
      if( presTest.equals( prefs.get( index ) ) )
      {
        // Treffer == Preset gefunden
        tempPre=index;
        if( LOGGER != null ) LOGGER.log( Level.FINEST, "found to <" + prefs.get( index ) + ">");
      }
    }
    presetNumber = tempPre;    
    return( presetNumber );
  }

  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#setLastStop(int)
   */
  @Override
  public void setLastStop( int lastStop )
  {
    if( lastStop >= 0 && lastStop < 3  )
    {
      lastDecoStop = lastStop;
    }
    else
    {
      throw new IndexOutOfBoundsException( "not a valid last stop value!" );
    }
  }

  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#getLastStop()
   */
  @Override
  public int getLastStop()
  {
    return( lastDecoStop );
  }

  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#setDeepStopEnable(boolean)
   */
  @Override
  public void setDeepStopEnable( boolean enabled )
  {
    enableDeepStops = enabled;
  }

  /* (non-Javadoc)
   * @see de.dmarcini.submatix.pclogger.utils.ISPX42Config#getDeepStopEnable()
   */
  @Override
  public int getDeepStopEnable()
  {
    if( enableDeepStops )
    {
      return( 1 );
    }
    return( 0 );
  }

  @Override
  public boolean isDeepStopEnable()
  {
    return( enableDeepStops );
  }
  
  @Override
  public void setDynGradientsEnable( boolean enabled )
  {
    enableDynGradients = enabled;
  }

  @Override
  public int getDynGradientsEnable()
  {
    if( enableDynGradients )
    {
      return( 1 );
    }
    return( 0 );
  }
  
  @Override
  public boolean isDynGradientsEnable()
  {
    return( enableDynGradients );
  }

  @Override
  public void setLogger( Logger logger )
  {
    LOGGER = logger;
  }

  @Override
  public void setDisplay( int bright, int orient )
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean setDisplay( String fromSpx )
  {
    // Kommando DISPLAY liefert
    // ~36:D:A
    // D= 0->10&, 1->50%, 2->100%
    // A= 0->Landscape 1->180Grad
    if( LOGGER != null ) LOGGER.log( Level.FINEST, "setDisplay() <" + fromSpx + ">");
    String[] fields = fieldPatternDp.split( fromSpx );
    int[] vals = new int[2];
    try
    {
      vals[0] = Integer.parseInt( fields[1] );
      vals[1] = Integer.parseInt( fields[2] );
    }
    catch ( NumberFormatException ex )
    {
      if( LOGGER != null ) LOGGER.log( Level.SEVERE, "setDisplay() <" + fromSpx + "> - not expected String!");
      return false;
    }
    displayBrightness = vals[0];
    displayOrientation = vals[1];
    return true;
  }

  @Override
  public void setDisplayBrithtness( int brightness )
  {
    if( brightness < 0 || brightness > 3 )
    {
      throw new IndexOutOfBoundsException( "not a valid display brightness value!" );
    }
    displayBrightness = brightness;
  }

  @Override
  public int getDisplayBrightness()
  {
    return( displayBrightness );
  }

  @Override
  public void setDisplayOrientation( int orientation )
  {
    if( orientation < 0 || orientation > 1 )
    {
      throw new IndexOutOfBoundsException( "not a valid display orientation value!" );
    }
    displayOrientation = orientation;
  }

  @Override
  public int getDisplayOrientation()
  {
    return( displayOrientation );
  }

  @Override
  public boolean setUnits( String fromSpx )
  {
    // Kommando UNITS
    // ~37:UD:UL:UW
    // UD= Fahrenheit/Celsius => immer 0 in der aktuellen Firmware 2.6.7.7_U
    // UL= 0=metrisch 1=imperial
    // UW= 0->Salzwasser 1->Süßwasser
    if( LOGGER != null ) LOGGER.log( Level.FINEST, "setUnits() <" + fromSpx + ">");
    String[] fields = fieldPatternDp.split( fromSpx );
    int[] vals = new int[3];
    try
    {
      vals[0] = Integer.parseInt( fields[1] );
      vals[1] = Integer.parseInt( fields[2] );
      vals[2] = Integer.parseInt( fields[3] );
    }
    catch ( NumberFormatException ex )
    {
      if( LOGGER != null ) LOGGER.log( Level.SEVERE, "setUnits() <" + fromSpx + "> - not expected String!");
      return false;
    }
    unitsTemperature = vals[0];
    unitsDepth = vals[1];
    unitsSalnyty = vals[2];
    return true;
  }

  @Override
  public void setUnits( int tmp, int dpt, int sal )
  {
    setUnitTemperature( tmp );
    setUnitDepth( dpt );
    setUnitSalnyty( sal );
  }

  @Override
  public void setUnitTemperature( int tmp )
  {
    if( tmp > 1 || tmp < 0 )
    {
      throw new IndexOutOfBoundsException( "not a valid display orientation value!" );
    }
    unitsTemperature = tmp;
  }

  @Override
  public int getUnitTemperature()
  {
    return( unitsTemperature );
  }

  @Override
  public void setUnitDepth( int dpt )
  {
    if( dpt < 0 || dpt > 1 )
    {
      throw new IndexOutOfBoundsException( "not a valid display orientation value!" );
    }
    unitsDepth = dpt;
  }

  @Override
  public int getUnitDepth()
  {
    return( unitsDepth );
  }

  @Override
  public void setUnitSalnyty( int sal )
  {
    if( sal < 0 || sal > 1 )
    {
      throw new IndexOutOfBoundsException( "not a valid display orientation value!" );
    }
    unitsSalnyty = sal;
  }

  @Override
  public int getUnitSalnity()
  {
    return( unitsSalnyty );
  }

  @Override
  public boolean setSetpoint( String fromSpx )
  {
    // Kommando SETPOINT liefert
    // ~35:A:P
    // A = Setpoint bei (0,1,2,3) = (0,5,15,20)
    // P = Partialdruck (0..4) 1.0 .. 1.4
    if( LOGGER != null ) LOGGER.log( Level.FINEST, "setSetpoint() <" + fromSpx + ">");
    String[] fields = fieldPatternDp.split( fromSpx );
    int[] vals = new int[2];
    try
    {
      vals[0] = Integer.parseInt( fields[1] );
      vals[1] = Integer.parseInt( fields[2] );
    }
    catch ( NumberFormatException ex )
    {
      if( LOGGER != null ) LOGGER.log( Level.SEVERE, "setSetpoint() <" + fromSpx + "> - not expected String!");
      return false;
    }
    autoSetpoint = vals[0];
    ppo = vals[1];
    return true;
  }

  @Override
  public void setSetpoint( int auto, int ppo )
  {
    setAutoSetpoint( auto );
    setHighSetpoint( ppo );
  }

  @Override
  public void setAutoSetpoint( int auto )
  {
    if( auto < 0 || auto > 3 )
    {
      throw new IndexOutOfBoundsException( "not a valid autosetpoint value!" );
    }
    autoSetpoint = auto;
  }

  @Override
  public int getAutoSetpoint()
  {
    return( autoSetpoint );
  }

  @Override
  public void setHighSetpoint( int appo )
  {
    if( appo < 0 || appo > 4 )
    {
      throw new IndexOutOfBoundsException( "not a valid setpoint value!" );
    }
    ppo = appo;
  }

  @Override
  public int getHighSetpoint()
  {
    return( ppo );
  }

  @Override
  public boolean setIndividuals( String fromSpx )
  {
    // Kommando INDIVIDUAL liefert
    // ~38:SE:PS:SC:SN:LI
    // SE: Sensors 0->ON 1->OFF
    // PS: PSCRMODE 0->OFF 1->ON
    // SC: SensorCount
    // SN: Sound 0->OFF 1->ON
    // LI: Loginterval 0->10sec 1->30Sec 2->60 Sec
    if( LOGGER != null ) LOGGER.log( Level.FINEST, "setIndividuals() <" + fromSpx + ">");
    String[] fields = fieldPatternDp.split( fromSpx );
    int[] vals = new int[5];
    try
    {
      vals[0] = Integer.parseInt( fields[1] );
      vals[1] = Integer.parseInt( fields[2] );
      vals[2] = Integer.parseInt( fields[3] );
      vals[3] = Integer.parseInt( fields[4] );
      vals[4] = Integer.parseInt( fields[5] );
    }
    catch ( NumberFormatException ex )
    {
      if( LOGGER != null ) LOGGER.log( Level.SEVERE, "setIndividuals() <" + fromSpx + "> - not expected String! (no individuals license?)");
      return false;
    }
    if( vals[0] > 0 ) { sensorsOn = true; } else { sensorsOn = false; }
    if( vals[1] > 0 ) { pscrModeOn = true; } else { pscrModeOn = false; }
    sensorsCount = vals[2];
    if( vals[3] > 0 ) { soundOn = true; } else { soundOn = false; }
    logInterval = vals[4];
    return true;
  }

  @Override
  public void setIndividuals( int so, int pscr, int sc, int snd, int li )
  {
    if( so > 0 ) { sensorsOn = true; } else { sensorsOn = false; }
    if( pscr > 0 ) { pscrModeOn = true; } else { pscrModeOn = false; }
    if( sc > 3 | sc < 0 )
    {
      throw new IndexOutOfBoundsException( "not a valid sensor count value!" );
    }
    sensorsCount = sc;
    if(snd > 0 ) { soundOn = true; } else { soundOn = false; }
    if( li > 3 | li < 0 )
    {
      throw new IndexOutOfBoundsException( "not a valid loginterval value!" );
    }
    logInterval = li;
  }

  @Override
  public void setSensorsEnabled( boolean so )
  {
    sensorsOn = so;
  }

  @Override
  public int getSensorsOn()
  {
    if( sensorsOn )
    {
      return(1);
    }
    return 0;
  }

  @Override
  public boolean isSensorsOn()
  {
    return( sensorsOn );
  }

  @Override
  public void setPscrModeEnabled( boolean pscr )
  {
    pscrModeOn = pscr;
  }

  @Override
  public int getPscrModeOn()
  {
    if( pscrModeOn )
    {
      return( 1 );
    }
    return 0;
  }

  @Override
  public boolean isPscrModeOn()
  {
    return( pscrModeOn );
  }

  @Override
  public void setSensorsCount( int sc )
  {
    sc++;
    if( sc > 3 | sc < 0 )
    {
      throw new IndexOutOfBoundsException( "not a valid sensor count value!" );
    }
    sensorsCount = sc;
  }

  @Override
  public int getSensorsCount()
  {
    return( sensorsCount );
  }

  @Override
  public void setSountEnabled( boolean snd )
  {
    soundOn = snd; 
  }

  @Override
  public int getSoundOn()
  {
    if( soundOn )
    {
      return( 1 );
    }
    return 0;
  }

  @Override
  public boolean isSoundOn()
  {
    return( soundOn );
  }

  @Override
  public void setLogInterval( int li )
  {
    if( li > 3 | li < 0 )
    {
      throw new IndexOutOfBoundsException( "not a valid loginterval value!" );
    }
    logInterval = li;
  }

  @Override
  public int getLogInterval()
  {
    return( logInterval );
  }

  @Override
  public void setWasInit( boolean wasInit )
  {
    wasCorrectInitialized = wasInit; 
  }

  @Override
  public boolean wasInit()
  {
    return( wasCorrectInitialized );
  }

  @Override
  public void setDeviceName( String name )
  {
      deviceName = name ;
  }

  @Override
  public String getDeviceName()
  {
    return( deviceName );
  }

  @Override
  public void setFirmwareVersion( String version )
  {
    firmwareVersion = version;    
  }

  @Override
  public String getFirmwareVersion()
  {
    return( firmwareVersion );
  }

  
  

}
