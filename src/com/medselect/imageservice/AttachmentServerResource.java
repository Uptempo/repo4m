/*
 * Copyright 2013 Uptempo Group LLC.
 */

package com.medselect.imageservice;

import com.medselect.common.ReturnMessage;
import com.medselect.server.BaseServerResource;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
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
    String status = "SUCCESS";
    String message = "Returned Attachment URL";
    JSONObject obj = new JSONObject();

    if (itemKey != null) {
      String url = aManager.getAttachmentUrl(itemKey);
      try {
        obj.put("url", url);
      } catch (JSONException ex) {
        status = "FAILURE";
        message = "Failed to get Attachment URL: " + ex.toString();
      }
    } else {
      Form cForm = this.getRequest().getResourceRef().getQueryAsForm();
      Map<String, String> paramMap = cForm.getValuesMap();
      String entityKey = paramMap.get("entityKey");
      if (entityKey != null) {
        ReturnMessage attachments = aManager.getAttachmentsForEntity(entityKey);
        obj = attachments.getValue();
        status = attachments.getStatus();
        message = attachments.getMessage();
      } else {
        status = "FAILURE";
        message = "Failed to get Attachments for entity with key " + entityKey;
      }
    }
    JsonRepresentation a = this.getJsonRepresentation(
        status,
        message,
        obj);
    return a;
  }
}
