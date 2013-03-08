/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.audit;

import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager class to provide simple audit logging for other services.
 * 
 * @author Mike Gordon (mgordon)
 */
public class AuditLogManager extends BaseManager {
  //*** Setup the item value map.
    ImmutableMap.Builder vmBuilder =
        new ImmutableMap.Builder<String, BaseManager.FieldType>();
    public static final Map<String, BaseManager.FieldType> AUDIT_LOG_STRUCTURE = 
        new ImmutableMap.Builder<String, BaseManager.FieldType>()
            .put("appCode", BaseManager.FieldType.STRING)
            .put("eventCode", BaseManager.FieldType.STRING)
            .put("eventDescription", BaseManager.FieldType.STRING)
            .put("remoteIP", BaseManager.FieldType.STRING)
            .put("remoteUser", BaseManager.FieldType.STRING)
            .put("eventTime", BaseManager.FieldType.DATE)
            .build();
    public static final String AUDIT_LOG_ENTITY_NAME = "AuditLog";
    public static final String AUDIT_LOG_DISPLAY_NAME = "Audit Log";
  
  public AuditLogManager() {
    super(AUDIT_LOG_STRUCTURE, AUDIT_LOG_ENTITY_NAME, AUDIT_LOG_DISPLAY_NAME);
  }
  
  /**
   * Convenience method to provide audit logging to other services.
   * 
   * @param appCode The application code for the audit event.
   * @param eventCode The audit event code.
   * @param eventDescription The audit event description.
   * @param remoteIP The remote IP address, if available.  Null values are accepted without error.
   * @param remoteUser The e-mail of the remote user that is related to the audit log entry.
   * @return A status, message, and the key for the audit log entry.
   */
  public ReturnMessage logAudit(
      String appCode,
      String eventCode,
      String eventDescription,
      String remoteIP,
      String remoteUser) {
    
    //*** Assemble the params and do the insert.
    Map<String, String> params = new HashMap();
    //*** Set the audit date/time to the current date.
    Date now = new Date();
    params.put("eventTime", String.valueOf(now.getTime()));
    params.put("appCode", appCode);
    params.put("eventCode", eventCode);
    params.put("eventDescription", eventDescription);
    params.put("remoteIP", remoteIP);
    params.put("remoteUser", remoteUser);
    params.put("user", remoteUser);

    return this.doCreate(params, false, null);
  }
}
