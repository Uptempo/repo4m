/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.medlayer;

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
 * @author antonio@pomodoro.com 
 */
public class MedLayerAppServerResource extends BaseServerResource {
 
  public MedLayerAppServerResource() {
    super();
  }

  @Get
  public JsonRepresentation readMedlayerAppValues() {
    Form cForm = this.getRequest().getResourceRef().getQueryAsForm();
    Map<String, String> paramMap = cForm.getValuesMap();
    MedLayerAppManager cManager = new MedLayerAppManager();
    ReturnMessage response = cManager.readMedLayerAppValues(paramMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Post
  public JsonRepresentation insertMedlayerAppValue(Representation medlayerAppValue) {
    Form cForm = new Form(medlayerAppValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    MedLayerAppManager cManager = new MedLayerAppManager();
    ReturnMessage response = cManager.insertMedLayerAppValue(valueMap);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Put
  public JsonRepresentation UpdateMedlayerAppValue(Representation medlayerAppValue) {
    Form cForm = new Form(medlayerAppValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    MedLayerAppManager cManager = new MedLayerAppManager();
    ReturnMessage response = cManager.updateMedLayerAppValue(valueMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Delete
  public JsonRepresentation DeleteMedlayerAppValue(Representation medlayerAppValue) {
	MedLayerAppManager cManager = new MedLayerAppManager();
    ReturnMessage response = cManager.deleteMedLayerAppValue(itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
}
