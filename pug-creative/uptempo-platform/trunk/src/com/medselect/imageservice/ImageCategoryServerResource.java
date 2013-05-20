/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.imageservice;

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
public class ImageCategoryServerResource extends BaseServerResource {
  protected ImageCategoryManager manager = new ImageCategoryManager();
  public ImageCategoryServerResource() {
        super();
  }

  @Get
  public JsonRepresentation readValues() {
    Form cForm = this.getRequest().getResourceRef().getQueryAsForm();
    Map<String, String> paramMap = cForm.getValuesMap();
    ReturnMessage response = this.manager.readValues(paramMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Post
  public JsonRepresentation insertValue(Representation value) {
    Form cForm = new Form(value);
    Map<String, String> valueMap = cForm.getValuesMap();
    ReturnMessage response = this.manager.insertValue(valueMap);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Put
  public JsonRepresentation UpdateValue(Representation value) {
    Form cForm = new Form(value);
    Map<String, String> valueMap = cForm.getValuesMap();
    ReturnMessage response = this.manager.updateValue(valueMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Delete
  public JsonRepresentation DeleteValue(Representation value) {
    ReturnMessage response = this.manager.deleteValue(itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
}
