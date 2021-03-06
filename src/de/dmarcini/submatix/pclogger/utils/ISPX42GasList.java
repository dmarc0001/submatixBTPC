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

  public int getBailout( int number );

  public int getCurrGas( int number );

  public boolean isInitialized();

  public void setInitialized();

  public int getGasCount();
}
