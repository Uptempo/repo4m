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
public class MedLayerDatasetServerResource extends BaseServerResource {
 
  public MedLayerDatasetServerResource() {
    super();
  }

  @Get
  public JsonRepresentation readMedlayerDatasetValues() {
    Form cForm = this.getRequest().getResourceRef().getQueryAsForm();
    Map<String, String> paramMap = cForm.getValuesMap();
    MedLayerDatasetManager cManager = new MedLayerDatasetManager();
    ReturnMessage response = cManager.readMedLayerDatasetValues(paramMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Post
  public JsonRepresentation insertMedlayerDatasetValue(Representation medlayerDatasetValue) {
    Form cForm = new Form(medlayerDatasetValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    MedLayerDatasetManager cManager = new MedLayerDatasetManager();
    ReturnMessage response = cManager.insertMedLayerDatasetValue(valueMap);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Put
  public JsonRepresentation updateMedLayerDatasetValue(Representation medlayerDatasetValue) {
    Form cForm = new Form(medlayerDatasetValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    MedLayerDatasetManager cManager = new MedLayerDatasetManager();
    ReturnMessage response = cManager.updateMedLayerDatasetValue(valueMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Delete
  public JsonRepresentation DeleteMedlayerDatasetValue(Representation medlayerDatasetValue) {
	MedLayerDatasetManager cManager = new MedLayerDatasetManager();
    ReturnMessage response = cManager.deleteMedLayerDatasetValue(itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
}
