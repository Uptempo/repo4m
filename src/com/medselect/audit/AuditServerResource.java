/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.audit;

import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.server.BaseServerResource;
import org.restlet.data.Form;
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
public class AuditServerResource extends BaseServerResource {
  
  public AuditServerResource() {
    super();
    //*** Setup the item value map.
    ImmutableMap.Builder vmBuilder =
        new ImmutableMap.Builder<String, BaseManager.FieldType>();
    itemValueMap = vmBuilder.put("appCode", BaseManager.FieldType.STRING)
                            .put("eventCode", BaseManager.FieldType.STRING)
                            .put("description", BaseManager.FieldType.STRING)
                            .put("severity", BaseManager.FieldType.LONG)
                            .put("alertThreshold", BaseManager.FieldType.LONG)
                            .put("alertType", BaseManager.FieldType.STRING)
                            .put("alertEmail", BaseManager.FieldType.STRING)
                            .put("alertPhone", BaseManager.FieldType.PHONE_NUMBER)                            
                            .build();
    entityName = "Audit";
    entityDisplayName = "Audit";
  }

  @Override
  protected void doInit() throws ResourceException {
    // Get the "itemName" attribute value taken from the URI template.
    // /application/{key}
    
    // If the application key is not empty, fill it in.
    if (!getRequest().getAttributes().isEmpty()) {
      itemKey = (String)getRequest().getAttributes().get("auditKey");
    } else {
      itemKey = null;
    }
  }

  @Get
  public Representation getAuditEventTypes() {
    Form aForm = this.getRequest().getResourceRef().getQueryAsForm();
    return this.doRead(aForm);
  }
 
  @Post
  public Representation insertAuditEventType(Representation appValue) {
    Form aForm = new Form(appValue);
    //String appCode = aForm.getFirstValue("appCode");
    //appCode = appCode.toUpperCase();
    //pass true for the key required
    return this.doPost(aForm, true);
  }

  @Put
  public Representation UpdateAuditEventType(Representation appValue) {
    Form aForm = new Form(appValue);
    return this.doPut(aForm);
  }

  @Delete
  public Representation deleteAuditEventType() {
    return this.doDelete();
  }
}
