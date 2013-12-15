/*
 * Copyright 2013 Uptempo Group LLC.
 */

package com.medselect.imageservice;

import com.medselect.server.BaseServerResource;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;

/**
 *
 * @author Mike Gordon (mgordon). 
 */
public class AttachmentServerResource extends BaseServerResource {
  protected AttachmentManager aManager = new AttachmentManager();
  public AttachmentServerResource() {
    super();
  }

  @Get
  public JsonRepresentation getUrl() {
    String url = aManager.getAttachmentUrl(itemKey);
    String status = "SUCCESS";
    String message = "Returned Attachment URL";
    JSONObject obj = new JSONObject();
    try {
    obj.put("url", url);
    } catch (JSONException ex) {
      status = "FAILURE";
      message = "Failed to get Attachment URL: " + ex.toString();
    }
    JsonRepresentation a = this.getJsonRepresentation(
        status,
        message,
        obj);
    return a;
  }
}
