/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.staticlist;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.medselect.common.ReturnMessage;
import com.medselect.server.BaseServerResource;
import java.util.Date;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
 * @author Mike Gordon
 */
public class StaticlistServerResource extends BaseServerResource {
 
  public StaticlistServerResource() {
    super();
  }

  @Get
  public JsonRepresentation readStaticlistValues() {
    Form cForm = this.getRequest().getResourceRef().getQueryAsForm();
    Map<String, String> paramMap = cForm.getValuesMap();
    StaticlistManager cManager = new StaticlistManager();
    ReturnMessage response = cManager.readStaticlistValues(paramMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Post
  public JsonRepresentation insertStaticlistValue(Representation staticlistValue) {
    Form cForm = new Form(staticlistValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    StaticlistManager cManager = new StaticlistManager();
    ReturnMessage response = cManager.insertStaticlistValue(valueMap);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Put
  public JsonRepresentation UpdateStaticlistValue(Representation staticlistValue) {
    Form cForm = new Form(staticlistValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    StaticlistManager cManager = new StaticlistManager();
    ReturnMessage response = cManager.updateStaticlistValue(valueMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Delete
  public JsonRepresentation DeleteStaticlistValue(Representation staticlistValue) {
    StaticlistManager cManager = new StaticlistManager();
    ReturnMessage response = cManager.deleteStaticlistValue(itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
}
