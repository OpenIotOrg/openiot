package org.openiot.gsn.http;


public interface WebConstants {
   
   public static final String REQUEST                                = "REQUEST";
   
   public static final int    REQUEST_LIST_VIRTUAL_SENSORS           = 0;
   
   public static final int    REGISTER_PACKET                        = 110;
   
   public static final int    DEREGISTER_PACKET                      = 111;
   
   public static final int    DATA_PACKET                            = 112;
   
   public static final int    REQUEST_OUTPUT_FORMAT                  = 113;
   
   public static final int    REQUEST_ONE_SHOT_QUERY                 = 114;
   
   public static final int    REQUEST_ADDRESSING                     = 115;
   
   public static final int    REQUEST_ONE_SHOT_QUERY_WITH_ADDRESSING = 116;
   
   public static final String WEB_APP_NAME                           = "WEB_APP_NAME";
   
   public static final String RESPONSE                               = "RESPOND";
   
   public static final String RES_HEADER_DATA_FIELD_DESCRIPTION      = "RES_HEADER_DATA_FIELD_DESCRIPTION";
   
   public static final String WEB_APP_AUTHOR                         = "WEB_APP_AUTHOR";
   
   public static final String WEB_APP_DESCRIPTION                    = "WEB_APP_DESCRIPTION";
   
   public static final String WEB_APP_EMAIL                          = "WEB_APP_EMAIL";
   
   public final String        RESPONSE_FIELD_TYPES                   = "FIELD_TYPES";
   
   public final String        RESPONSE_FIELD_NAMES                   = "FIELD_NAMES";
   
   public final String        QUERY_VS_NAME                          = "QUERY_VS_NAME";
   
   public final String        NOTIFICATION_CODE                      = "NOTIFICATION_CODE";
   
   public final String        INVALID_REQUEST                        = "INVALID_NOTIFICATION_CODE_RECEIVED";
   
   public final String        RESPONSE_STATUS                        = "RES_STATUS";
   
   public final String        REQUEST_HANDLED_SUCCESSFULLY           = "VALID_NOTIFICATION_CODE_RECEIVED";
   
   public final String        DATA                                   = "DATA";
   
   public final String        VS_QUERY                               = "VS_QUERY";
   
   public final String        STREAM_SOURCE_ACTIVE_ADDRESS_BEAN      = "STREAM_SOURCE_ACTIVE_ADDRESS_BEAN";
   
   /**
    * HTTP RETURN CODES :
    * ---------------------------------------------------------------------
    */
   
   public static final int    CORRECT_REQUEST                        = 200;
   
   public static final int    UNSUPPORTED_REQUEST_ERROR              = 400;
   
   public static final int    MISSING_VSNAME_ERROR                   = 401;
   
   public static final int    ERROR_INVALID_VSNAME                   = 402;
   
   public static final int    WRONG_VSFIELD_ERROR                    = 403;

   public static final int    ACCESS_DENIED                          = 406;   //if a user has not access to a page. 
   
  
}
