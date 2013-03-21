/*
 * Copyright 2013 Uptempo Group Inc.
 */

package com.medselect.application;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.medselect.audit.AuditLogManager;
import com.medselect.common.ReturnMessage;
import com.medselect.server.BaseServerResource;
import com.medselect.util.Constants;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.ext.json.JsonRepresentation;

/**
 *
 * @author Mike Gordon
 */
public class ApplicationKeyResource extends BaseServerResource {
  
  public ApplicationKeyResource() {
    super();
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

  @Post
  public Representation regenerateKey(Representation appValue) {
    ApplicationManager aManager = new ApplicationManager();
    ReturnMessage response = aManager.regenerateKey(itemKey);
    if (response.getStatus().equalsIgnoreCase("SUCCESS")) {
      AuditLogManager am = new AuditLogManager();
      UserService userService = UserServiceFactory.getUserService();
      String userEmail = userService.getCurrentUser().getEmail();
      am.logAudit(
          Constants.COMMON_APP,
          Constants.APP_KEY_GENERATE,
          response.getMessage(),
          "N/A",
          userEmail);
    }
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a; 
  }
}
