/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.audit;

import com.medselect.common.ReturnMessage;
import com.medselect.server.BaseServerResource;
import java.util.HashMap;
import java.util.Map;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

/**
 *
 * @author Ritu Jain
 */
public class AuditLogServerResource extends BaseServerResource {
  
  public AuditLogServerResource() {
    super();
    //*** Setup the item value map.
    itemValueMap = AuditLogManager.AUDIT_LOG_STRUCTURE;
    entityName = AuditLogManager.AUDIT_LOG_ENTITY_NAME;
    entityDisplayName = AuditLogManager.AUDIT_LOG_DISPLAY_NAME;
  }

  @Override
  protected void doInit() throws ResourceException {
    // Get the "itemName" attribute value taken from the URI template.
    // /application/{key}
    
    // If the application key is not empty, fill it in.
    if (!getRequest().getAttributes().isEmpty()) {
      itemKey = (String)getRequest().getAttributes().get("auditlogKey");
    } else {
      itemKey = null;
    }
  }

  @Get
  public Representation getAuditEvents() {
    Form aForm = this.getRequest().getResourceRef().getQueryAsForm();
    //*** Assemble the parameters.
    Integer days = Integer.parseInt(aForm.getFirstValue("days"));
    
    Map<String, String> params = new HashMap<String, String>();
    params.put("orderBy", "eventTime");
    params.put("direction", "DESC");
    params.put("format", aForm.getFirstValue("format"));

    AuditLogManager aManager = new AuditLogManager();
    ReturnMessage response = aManager.listLogs(params, days);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
 
  @Post
  public Representation insertAuditEvent(Representation appValue) {
    Form aForm = new Form(appValue);

    //pass false for the key required
    return this.doPost(aForm, false);
  }

  @Put
  public Representation updateAuditEvent(Representation appValue) {
    Form aForm = new Form(appValue);
    return this.doPut(aForm);
  }

  @Delete
  public Representation deleteAuditEvent() {
    return this.doDelete();
  }
}
