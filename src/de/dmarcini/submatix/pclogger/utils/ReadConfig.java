/**
 * Config fürs Programm einlesen
 * 
 * ReadConfigClass.de.dmarcini.submatix.pclogger
 * 
 * @author Dirk Marciniak 09.12.2011
 */
package de.dmarcini.submatix.pclogger.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Level;

import de.dmarcini.submatix.pclogger.res.ProjectConst;

/**
 * Lese Config ein. Konfig im Muster BEZEICHNUNG=WERT
 * 
 * @author dmarc
 */
public class ReadConfig
{
  private final ArrayList<String>        pairs     = new ArrayList<String>();
  private final SpxPcloggerProgramConfig prgConfig = new SpxPcloggerProgramConfig();
  private boolean                        debug     = false;

  /**
   * Konstruktor
   * 
   * @author Dirk Marciniak 09.12.2011
   * @throws IOException
   * @throws ConfigReadWriteException
   */
  public ReadConfig() throws IOException, ConfigReadWriteException
  {
    BufferedReader in;
    // gibts die Datei?
    if (SpxPcloggerProgramConfig.logLevel == Level.DEBUG) debug = true;
    if (SpxPcloggerProgramConfig.configFile.exists() && SpxPcloggerProgramConfig.configFile.canRead())
    {
      if (null != (in = openConfFile(SpxPcloggerProgramConfig.configFile)))
      {
        if (readConfInArray(in))
        {
          in.close();
          // alles ist gut :-)
          return;
        }
        in.close();
      }
    }
    else
    {
      //
      // Wenn keine Configdatei da ist, creiere eine beim Programmende
      //
      SpxPcloggerProgramConfig.wasChanged = true;
    }
  }

  /**
   * Datei öffnen, Reader zurück geben
   * 
   * @author Dirk Marciniak 05.12.2011
   * @param confFile
   * @return BufferedReader
   */
  private BufferedReader openConfFile(File confFile)
  {
    BufferedReader in;
    try
    {
      in = new BufferedReader(new FileReader(confFile));
      return (in);
    }
    catch (NullPointerException ex)
    {
      System.err.println("can not open config file: " + ex.getLocalizedMessage());
    }
    catch (FileNotFoundException ex)
    {
      System.err.println("can not open config file: " + ex.getLocalizedMessage());
    }
    return null;
  }

  /**
   * geöffnete Datei in Array einlesen
   * 
   * @author Dirk Marciniak 05.12.2011
   * @param in
   * @return boolean
   */
  private boolean readConfInArray(BufferedReader in)
  {
    String zeile = null;
    try
    {
      // die Config-Datei einlesen
      while ((zeile = in.readLine()) != null)
      {
        pairs.add(zeile);
      }
    }
    catch (NullPointerException ex)
    {
      System.err.println("can not read config file: " + ex.getLocalizedMessage());
      return (false);
    }
    catch (IOException ex)
    {
      System.err.println("can not read config file: " + ex.getLocalizedMessage());
      return (false);
    }
    //
    // auswertung der Parameter, wenn vorhanden
    //
    // sind Einträge vorhanden?
    for (String ln : pairs)
    {
      try
      {
        // rudimentäre Kommentare überspringen
        if (ln.startsWith("#")) continue;
        // Zeilen ohne "=" ignorieren
        if (-1 == ln.indexOf("=")) continue;
        // Zeile splitten
        String[] fields = ln.split("=");
        // nur wenn es zwei Felder sind, mach ich weiter
        if (fields.length == 2)
        {
          fields[0] = fields[0].trim();
          fields[1] = fields[1].trim();
        }
        else continue;
        //
        // unterscheide die Parameter
        //
        if (0 == fields[0].indexOf(ProjectConst.CONFIG_LANGCODE))
        {
          if (!SpxPcloggerProgramConfig.wasCliLangCode)
          {
            if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
            SpxPcloggerProgramConfig.langCode = fields[1];
          }
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_DATABASEDIR))
        {
          if (!SpxPcloggerProgramConfig.wasCliDatabaseDir)
          {
            if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
            SpxPcloggerProgramConfig.databaseDir = (new File(fields[1]));
          }
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_LOGFILE))
        {
          if (!SpxPcloggerProgramConfig.wasCliLogfile)
          {
            if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
            SpxPcloggerProgramConfig.logFile = new File(fields[1]);
          }
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_EXPORTDIR))
        {
          if (!SpxPcloggerProgramConfig.wasCliExportDir)
          {
            if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
            SpxPcloggerProgramConfig.exportDir = new File(fields[1]);
          }
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWUNITS))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          if (0 == fields[1].indexOf("default"))
          {
            SpxPcloggerProgramConfig.unitsProperty = ProjectConst.UNITS_DEFAULT;
          }
          else if (0 == fields[1].indexOf("metric"))
          {
            SpxPcloggerProgramConfig.unitsProperty = ProjectConst.UNITS_METRIC;
          }
          else if (0 == fields[1].indexOf("imperial"))
          {
            SpxPcloggerProgramConfig.unitsProperty = ProjectConst.UNITS_IMPERIAL;
          }
          else
          {
            SpxPcloggerProgramConfig.unitsProperty = ProjectConst.UNITS_DEFAULT;
          }
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWTEMPERRATURE))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          SpxPcloggerProgramConfig.showTemperature = Boolean.parseBoolean(fields[1]);
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWPPORESULT))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          SpxPcloggerProgramConfig.showPpoResult = Boolean.parseBoolean(fields[1]);
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWPPO1))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          SpxPcloggerProgramConfig.showPpo01 = Boolean.parseBoolean(fields[1]);
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWPPO2))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          SpxPcloggerProgramConfig.showPpo02 = Boolean.parseBoolean(fields[1]);
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWPPO3))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          SpxPcloggerProgramConfig.showPpo03 = Boolean.parseBoolean(fields[1]);
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWSETPOINT))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          SpxPcloggerProgramConfig.showSetpoint = Boolean.parseBoolean(fields[1]);
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWHE))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          SpxPcloggerProgramConfig.showHe = Boolean.parseBoolean(fields[1]);
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWN2))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          SpxPcloggerProgramConfig.showN2 = Boolean.parseBoolean(fields[1]);
        }
        else if (0 == fields[0].indexOf(ProjectConst.CONFIG_SHOWNULLTIME))
        {
          if (debug) System.out.println(String.format("ReadConfig: read <%s> = <%s>", fields[0], fields[1]));
          SpxPcloggerProgramConfig.showNulltime = Boolean.parseBoolean(fields[1]);
        }
      }
      catch (NumberFormatException ex)
      {
        // nicht sooo sauber, aber sollte funktionieren
        continue;
      }
    }
    // nach dem Einlesen ist das nicht geändert!
    SpxPcloggerProgramConfig.wasChanged = false;
    return true;
  }

  /**
   * Gib das Config-Objekt gefüllt zurück!
   * 
   * @author Dirk Marciniak 24.01.2012
   * @return SnifferConfigClass
   */
  public SpxPcloggerProgramConfig getConfigClass()
  {
    return (prgConfig);
  }
}
