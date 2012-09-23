package de.dmarcini.submatix.pclogger.utils;

public class GasPresetComboObject
{
  /**
   * Hier kapsel ich die Einträge
   */
  public String presetName = "";
  public int    dbId       = -1;

  public GasPresetComboObject( String name, int id )
  {
    presetName = name;
    dbId = id;
  }
}
