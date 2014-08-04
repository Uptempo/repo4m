/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.doctor;

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
public class DoctorServerResource extends BaseServerResource {
 
  public DoctorServerResource() {
    super();
  }

  @Get
  public JsonRepresentation readDoctorValues() {
    Form cForm = this.getRequest().getResourceRef().getQueryAsForm();
    Map<String, String> paramMap = cForm.getValuesMap();
    DoctorManager dManager = new DoctorManager();
    ReturnMessage response = dManager.readDoctorValues(paramMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Post
  public JsonRepresentation insertDoctorValue(Representation doctorValue) {
    Form cForm = new Form(doctorValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    DoctorManager dManager = new DoctorManager();
    ReturnMessage response = dManager.insertDoctorValue(valueMap);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Put
  public JsonRepresentation UpdateDoctorValue(Representation doctorValue) {
    Form cForm = new Form(doctorValue);
    Map<String, String> valueMap = cForm.getValuesMap();
    DoctorManager dManager = new DoctorManager();
    ReturnMessage response = dManager.updateDoctorValue(valueMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Delete
  public JsonRepresentation DeleteDoctorValue(Representation doctorValue) {
    DoctorManager dManager = new DoctorManager();
    ReturnMessage response = dManager.deleteDoctorValue(itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
}
