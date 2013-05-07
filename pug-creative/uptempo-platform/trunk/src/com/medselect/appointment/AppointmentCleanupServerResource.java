/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.appointment;

import java.util.Map;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Mike Gordon (mgordon)
 */
public class AppointmentCleanupServerResource extends ServerResource {
  @Get
  public Representation clearReservedAppointments() {
    AppointmentManager aManager = new AppointmentManager();
    aManager.resetReservedAppointments();
    StringRepresentation sr = new StringRepresentation("SUCCESS");
    return sr;
  }
  
  @Post
  public Representation shiftAppointments(Representation apptValue) {
    Form sForm = new Form(apptValue);
 
    //*** Get the office key string and shift value.
    Map<String, String> valueMap = sForm.getValuesMap();
    String officeKey = valueMap.get("apptOffice");
    String offsetString = valueMap.get("offset");
    int offset = Integer.parseInt(offsetString);
    AppointmentManager aManager = new AppointmentManager();
    aManager.shiftOfficeAppointmentTimes(officeKey, offset);
    StringRepresentation sr = new StringRepresentation("SUCCESS");
    return sr;
  }
}
