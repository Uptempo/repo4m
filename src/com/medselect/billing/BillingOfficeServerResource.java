/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.billing;

import com.medselect.common.ReturnMessage;
import com.medselect.server.BaseServerResource;
import java.util.Map;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

/**
 *
 * @author karlo.smid@gmail.com 
 */
public class BillingOfficeServerResource extends BaseServerResource {
 
  public BillingOfficeServerResource() {
    super();
  }

  @Get
  public JsonRepresentation readBillingOfficeValues() {
    Form cForm = this.getRequest().getResourceRef().getQueryAsForm();
    Map<String, String> paramMap = cForm.getValuesMap();
    BillingOfficeManager cManager = new BillingOfficeManager();
    ReturnMessage response = cManager.readBillingOfficeValues(paramMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Post
  public JsonRepresentation insertBillingOfficeValue(Representation billingOfficeValue) {
    Form cForm = new Form(billingOfficeValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    BillingOfficeManager cManager = new BillingOfficeManager();
    ReturnMessage response = cManager.insertBillingOfficeValue(valueMap);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Put
  public JsonRepresentation UpdateBillingOfficeValue(Representation billingOfficeValue) {
    Form cForm = new Form(billingOfficeValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    BillingOfficeManager cManager = new BillingOfficeManager();
    ReturnMessage response = cManager.updateBillingOfficeValue(valueMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Delete
  public JsonRepresentation DeleteBillingOfficeValue(Representation billingOfficeValue) {
    BillingOfficeManager cManager = new BillingOfficeManager();
    ReturnMessage response = cManager.deleteBillingOfficeValue(itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
}
