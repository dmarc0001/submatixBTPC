/*
 * Klasse als Container für Programmkonstanten Bluethooth service
 */
package de.dmarcini.submatix.pclogger.res;

import java.awt.event.ActionEvent;


public final class ProjectConst
{

  //@formatter:off
  // 0 Grad Celsius
  public static final float  KELVIN                 = (float)(273.15);
  
  // UDDF Festlegungen
  public static final String UDDFVERSION            = "2.2.0";
  public static final String CREATORNAME            = "SPX42";
  public static final String MANUFACTNAME           = "Dirk Marciniak";
  public static final String MANUFACTMAIL           = "dirk_marciniak@arcor.de";
  public static final String MANUFACTHOME           = "http://localhost";
  public static final String MANUFACTVERS           = "2.3beta";
  public static final String GENYEAR                = "2011";
  public static final String GENMONTH               = "12";
  public static final String GENDAY                 = "16";
  
  public static final int    MAXINBUFFER            = 10 * 1024;

  // Messages für SPX 42
  public static final String STX                    = new String( new byte[]
                                                    { 0x02 } );
  public static final String ETX                    = new String( new byte[]
                                                    { 0x03 } );
  public static final String FILLER                 = new String( new byte[]
                                                    { 0x0d, 0x0a } );
  public static final String FILLERCHAR             = "[\\n\\r]";                                               // Zeichen zum entfernen
  public static final String LOGSELECTOR            = new String ( new byte[] { 0x09 } );
  //
  public static final String KDO_DEVNAME            = "~0x01";
  public static final String IS_DEVNAME             = "~1:";
  public static final String KDO_ACKU               = "~0x03";                                                  // Antwort war "~3:19c" Ackuspannung?
  public static final String ISKDO_ACKU             = "~3:";
  public static final String KDO_VERSION            = "~0x04";
  public static final String IS_VERSION             = "~4:";
  public static final String KDO_LOGLIST_DETAIL     = "~0x46";
  public static final String KDO_47                 = "~0x47";                                                  // ~0x74:1:1 oder sowas...
  
  //
  public static final String KDO_GETSERIAL          = "~0x07";
  public static final String ISKDO_SERIAL           = "~7:";
  public static final String KDO_DECO               = "~0x34";
  public static final String ISKDO_DECO             = "~34:";
  public static final String KDO_SETPOINT           = "~0x30";
  public static final String ISKDO_SETPOINT         = "~35:";
  public static final String KDO_DISPLAY            = "~0x36";
  public static final String ISKDO_DISPLAY          = "~36:";
  public static final String KDO_UNIS               = "~0x37";
  public static final String ISKDO_UNITS            = "~37:";
  public static final String KDO_INDIVIDUAL         = "~0x38";
  public static final String IS_INDIVIDUAL          = "~38:";
  public static final String KDO_GAS                = "~0x39";
  public static final String ISKDO_GAS              = "~39:";
  public static final String KDO_LOGLIST            = "~0x41";
  public static final String IS_LOGLISTENTRY        = "~41:";
  public static final String IS_END_LOGLISTENTRY    = ":41";
  public static final String KDO_LOGENTRY           = "~0x42";
  public static final String ISKDO_LOGENTRY_START   = "~43:1";                                                  // plus :NUMMER
  public static final String ISKDO_LOGENTRY_STOP    = "~43:0";                                                  // plus :NUMMER
  public static final String KDO45                  = "~0x45";
  public static final String ISKDO45                = "~45:";

  // SENDE Config Kommandos
  public static final String KDOSETDECO             = "~29";
  public static final String QKDOSETDECO            = "~29";
  public static final String KDOSETSETPOINT         = "~30";
  public static final String KDOSETDISPLAY          = "~31";
  public static final String KDOSETUNITS            = "~32";
  public static final String KDOSETINDIVIDUAL       = "~33";
  public static final String KDOSETGAS              = "~40";

  // Message Bezeichnungen

  public static final int    MESSAGE_NONE           = ActionEvent.RESERVED_ID_MAX + 1;
  public static final int    MESSAGE_STATE_CHANGE   = ActionEvent.RESERVED_ID_MAX + 2;
  public static final int    MESSAGE_READ           = ActionEvent.RESERVED_ID_MAX + 3;
  public static final int    MESSAGE_WRITE          = ActionEvent.RESERVED_ID_MAX + 4;
  public static final int    MESSAGE_DEVICE_NAME    = ActionEvent.RESERVED_ID_MAX + 5;
  public static final int    MESSAGE_TOAST          = ActionEvent.RESERVED_ID_MAX + 6;
  public static final int    MESSAGE_SERIAL_READ    = ActionEvent.RESERVED_ID_MAX + 7;
  public static final int    MESSAGE_DECO_READ      = ActionEvent.RESERVED_ID_MAX + 8;
  public static final int    MESSAGE_SETPOINT_READ  = ActionEvent.RESERVED_ID_MAX + 9;
  public static final int    MESSAGE_DISPLAY_READ   = ActionEvent.RESERVED_ID_MAX + 10;
  public static final int    MESSAGE_INDIVID_READ   = ActionEvent.RESERVED_ID_MAX + 11;
  public static final int    MESSAGE_GAS_READ       = ActionEvent.RESERVED_ID_MAX + 12;
  public static final int    MESSAGE_KDO45_READ     = ActionEvent.RESERVED_ID_MAX + 13;
  public static final int    MESSAGE_DIRENTRY_READ  = ActionEvent.RESERVED_ID_MAX + 14;
  public static final int    MESSAGE_DIRENTRY_END   = ActionEvent.RESERVED_ID_MAX + 15;
  public static final int    MESSAGE_LOGENTRY_START = ActionEvent.RESERVED_ID_MAX + 16;
  public static final int    MESSAGE_LOGENTRY_LINE  = ActionEvent.RESERVED_ID_MAX + 17;
  public static final int    MESSAGE_LOGENTRY_STOP  = ActionEvent.RESERVED_ID_MAX + 18;
  public static final int    MESSAGE_DB_SUCCESS     = ActionEvent.RESERVED_ID_MAX + 19;
  public static final int    MESSAGE_DB_FAIL        = ActionEvent.RESERVED_ID_MAX + 29;
  public static final int    MESSAGE_DB_WRITE_WAIT  = ActionEvent.RESERVED_ID_MAX + 30;
  public static final int    MESSAGE_DB_DELETE_WAIT = ActionEvent.RESERVED_ID_MAX + 31;
  public static final int    MESSAGE_GAS_WRITTEN    = ActionEvent.RESERVED_ID_MAX + 32;
  public static final int    MESSAGE_CONNECTING     = ActionEvent.RESERVED_ID_MAX + 33;
  public static final int    MESSAGE_CONNECTED      = ActionEvent.RESERVED_ID_MAX + 34;
  public static final int    MESSAGE_DISCONNECTED   = ActionEvent.RESERVED_ID_MAX + 35;
  public static final int    MESSAGE_UNITS_READ     = ActionEvent.RESERVED_ID_MAX + 36;
  public static final int    MESSAGE_TCNAME_READ    = ActionEvent.RESERVED_ID_MAX + 37;
  public static final int    MESSAGE_FWVERSION_READ = ActionEvent.RESERVED_ID_MAX + 38;
  public static final int    MESSAGE_BTRECOVEROK    = ActionEvent.RESERVED_ID_MAX + 39;
  public static final int    MESSAGE_BTRECOVERERR   = ActionEvent.RESERVED_ID_MAX + 40;
  public static final int    MESSAGE_BTWAITFOR      = ActionEvent.RESERVED_ID_MAX + 41;
  public static final int    MESSAGE_BTNODEVCONN    = ActionEvent.RESERVED_ID_MAX + 42;
  public static final int    MESSAGE_BTAUTHREQEST   = ActionEvent.RESERVED_ID_MAX + 43;
  public static final int    MESSAGE_SPXACKU        = ActionEvent.RESERVED_ID_MAX + 44;
  

  
  // Interne Messages
  private static final int   INT_MESSAGE_OFFSET     = 100;
  public static final int    INT_MESSAGE_READ       = INT_MESSAGE_OFFSET + 1;
  public static final int    INT_LOGENTRY_LINE      = INT_MESSAGE_OFFSET + 2;
  public static final int    INT_STARTWAITDIAL      = INT_MESSAGE_OFFSET + 3;
  public static final int    INT_STOPTWAITDIAL      = INT_MESSAGE_OFFSET + 4;
  public static final int    INT_SETTITLEDIAL       = INT_MESSAGE_OFFSET + 5;


  //@formatter:on

}
