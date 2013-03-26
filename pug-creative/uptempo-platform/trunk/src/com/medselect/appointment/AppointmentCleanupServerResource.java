/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.appointment;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
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
}
