/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.application;

import com.medselect.common.BaseManager;
import com.google.common.collect.ImmutableMap;
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
 * @author Mike Gordon
 */
public class ApplicationServerResource extends BaseServerResource {
  
  public ApplicationServerResource() {
    super();
    //*** Setup the item value map.
    ImmutableMap.Builder vmBuilder =
        new ImmutableMap.Builder<String, BaseManager.FieldType>();
    itemValueMap = vmBuilder.put("appCode", BaseManager.FieldType.STRING)
                            .put("appName", BaseManager.FieldType.STRING)
                            .put("appDescription", BaseManager.FieldType.STRING)
                            .put("url", BaseManager.FieldType.STRING)
                            .put("createdBy", BaseManager.FieldType.STRING)
                            .put("createDate", BaseManager.FieldType.DATE)
                            .put("modifiedBy", BaseManager.FieldType.STRING)
                            .put("modifyDate", BaseManager.FieldType.DATE)
                            .put("accessKey", BaseManager.FieldType.STRING)
                            .build();
    entityName = "Application";
    entityDisplayName = "Application";
  }

  @Override
  protected void doInit() throws ResourceException {
    // Get the "itemName" attribute value taken from the URI template.
    // /application/{key}
    
    // If the application key is not empty, fill it in.
    if (!getRequest().getAttributes().isEmpty()) {
      itemKey = (String)getRequest().getAttributes().get("key");
    } else {
      itemKey = null;
    }
  }

  @Get
  public Representation readApplications() {
    Form aForm = this.getRequest().getResourceRef().getQueryAsForm();
    return this.doRead(aForm);
  }

  @Post
  public Representation insertApplication(Representation appValue) {
    Form aForm = new Form(appValue);
    //*** Special logic only executed for applications.
    aForm.add("accessKey", "GeneratedKeyHere");
    
    //*** Transform the appCode to upper case.
    String appCode = aForm.getFirstValue("appCode");
    appCode = appCode.toUpperCase();
    aForm.removeAll("appCode");
    aForm.add("appCode", appCode);
 
    return this.doPost(aForm, true);
  }

  @Put
  public Representation UpdateApplication(Representation appValue) {
    Form aForm = new Form(appValue);
    return this.doPut(aForm);
  }

  @Delete
  public Representation DeleteApplication() {
    return this.doDelete();
  }
}
