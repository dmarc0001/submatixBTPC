package de.dmarcini.submatix.pclogger.utils;

public interface ISPX42GasList
{
  public boolean setGas( String cmd );

  public boolean setGas( int number, int o2, int he );

  public boolean setDiluent1( int number );

  public boolean setDiluent2( int number );

  public boolean setBailout( int number, boolean toSet );

  public int getO2FromGas( int number );

  public int getHEFromGas( int number );

  public int getN2FromGas( int Number );

  public int getDiulent1();

  public int getDiluent2();

  public boolean isBailout( int number );

  public boolean isInitialized();

  public int getGasCount();
}
