/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.billing;

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
 * @author karlo.smid@gmail.com 
 */
public class BillingPlanServerResource extends BaseServerResource {
 
  public BillingPlanServerResource() {
    super();
  }

  @Get
  /*public JsonRepresentation readBillingPlanValues() {
    Form cForm = this.getRequest().getResourceRef().getQueryAsForm();
    Map<String, String> paramMap = cForm.getValuesMap();
    BillingPlanManager cManager = new BillingPlanManager();
    ReturnMessage response = cManager.readBillingPlanValues(paramMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }*/

  @Post
  public JsonRepresentation insertBillingPlanValue(Representation billingPlanValue) {
    Form cForm = new Form(billingPlanValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    BillingPlanManager cManager = new BillingPlanManager();
    ReturnMessage response = cManager.insertBillingPlanValue(valueMap);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  /*@Put
  public JsonRepresentation UpdateBillingPlanValue(Representation billingPlanValue) {
    Form cForm = new Form(billingPlanValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    BillingPlanManager cManager = new BillingPlanManager();
    ReturnMessage response = cManager.updateBillingPlanValue(valueMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Delete
  public JsonRepresentation DeleteBillingPlanValue(Representation billingPlanValue) {
    BillingPlanManager cManager = new BillingPlanManager();
    ReturnMessage response = cManager.deleteBillingPlanValue(itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }*/
}
