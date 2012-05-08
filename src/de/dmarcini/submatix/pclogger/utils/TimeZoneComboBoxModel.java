package de.dmarcini.submatix.pclogger.utils;

import javax.swing.DefaultComboBoxModel;

public class TimeZoneComboBoxModel extends DefaultComboBoxModel
{
  /**
   * 
   */
  private static final long serialVersionUID = -7957814923054349950L;
  String[][]                timezones        =
                                             {
                                             { "-12:00", "UTC -12:00" },
                                             { "-11:00", "UTC -11:00" },
                                             { "-10:00", "UTC -10:00" },
                                             { "-09:00", "UTC -09:00" },
                                             { "-08:00", "UTC -08:00" },
                                             { "-07:00", "UTC -07:00" },
                                             { "-06:00", "UTC -06:00" },
                                             { "-05:00", "UTC -05:00" },
                                             { "-04:30", "UTC -04:30" },
                                             { "-04:00", "UTC -04:00" },
                                             { "-03:30", "UTC -03:30" },
                                             { "-03:00", "UTC -03:00" },
                                             { "-02:00", "UTC -02:00" },
                                             { "-01:00", "UTC -01:00" },
                                             { "00:00", "UTC GMT" },
                                             { "01:00", "UTC +01:00 CET" },
                                             { "02:00", "UTC +02:00 CEST" },
                                             { "03:00", "UTC +03:00" },
                                             { "03:30", "UTC +03:30" },
                                             { "04:00", "UTC +04:00" },
                                             { "04:30", "UTC +04:30" },
                                             { "05:00", "UTC +05:00" },
                                             { "05:30", "UTC +05:30" },
                                             { "05:45", "UTC +05:45" },
                                             { "06:00", "UTC +06:00" },
                                             { "06:30", "UTC +06:30" },
                                             { "07:00", "UTC +07:00" },
                                             { "08:00", "UTC +08:00" },
                                             { "09:00", "UTC +09:00" },
                                             { "09:30", "UTC +09:30" },
                                             { "10:00", "UTC +10:00" },
                                             { "11:00", "UTC +11:00" },
                                             { "12:00", "UTC +12:00" },
                                             { "13:00", "UTC +13:00" } };
  private final int         countEntrys      = timezones.length;

  /**
   * 
   * Konstruktor, soll Liste gleich initialisieren
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.05.2012
   */
  public TimeZoneComboBoxModel()
  {
    super();
    // Alle Stringpaare in das Object (vector) einfügen
    for( String[] entry : timezones )
    {
      super.addElement( entry );
    }
  }

  /**
   * Gib den für die Anzeige vorgesehenen Wert zurück
   */
  @Override
  public Object getElementAt( int index )
  {
    if( index > countEntrys )
    {
      return( null );
    }
    if( index <= -1 )
    {
      return( new String( "" ) );
    }
    String[] value = ( String[] )super.getElementAt( index );
    return( value[1] );
  }

  /**
   * 
   * Gib den DifferenzString des selektierten Eintrages zurück
   * 
   * Project: SubmatixBTForPC Package: de.dmarcini.submatix.pclogger.utils
   * 
   * @author Dirk Marciniak (dirk_marciniak@arcor.de)
   * 
   *         Stand: 08.05.2012
   * @param index
   * @return Differenz als String
   */
  public String getTimeValAt( int index )
  {
    if( index > countEntrys )
    {
      return( null );
    }
    String[] value = ( String[] )super.getElementAt( index );
    return( value[0] );
  }

  @Override
  public void addElement( Object elem )
  {
    if( elem instanceof String[] )
    {
      super.addElement( elem );
    }
  }

  @Override
  public void insertElementAt( Object anObject, int index )
  {
    if( anObject instanceof String[] )
    {
      super.addElement( anObject );
    }
  }
}
