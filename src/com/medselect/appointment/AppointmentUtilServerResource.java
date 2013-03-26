/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.appointment;

import com.medselect.common.ReturnMessage;
import com.medselect.server.BaseServerResource;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 *
 * @author Mike Gordon(mgordon)
 */
public class AppointmentUtilServerResource extends BaseServerResource {
  @Get
  public Representation getAllAppointments() {
    AppointmentManager aManager = new AppointmentManager();
    ReturnMessage response = aManager.getAllAppointments();
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
}
