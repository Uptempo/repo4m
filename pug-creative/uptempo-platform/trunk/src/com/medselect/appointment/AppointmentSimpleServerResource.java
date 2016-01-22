/*
 * Copyright 2013 Uptempo Group Inc.
 */

package com.medselect.appointment;

import com.medselect.common.ReturnMessage;
import com.medselect.server.BaseServerResource;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 *
 * @author Mike Gordon (mgordon)
 */
public class AppointmentSimpleServerResource extends BaseServerResource {
  AppointmentManager aManager = new AppointmentManager();

  public AppointmentSimpleServerResource() {
    super();
    itemValueMap = AppointmentManager.APPT_STRUCTURE;
    entityName = AppointmentManager.APPT_ENTITY_NAME;
    entityDisplayName = AppointmentManager.APPT_DISPLAY_NAME;
  }

  @Get
  public Representation readAppointments() {
    //*** Read the parameters.
    Form aForm = this.getRequest().getResourceRef().getQueryAsForm();
    String officeKey = aForm.getFirstValue("apptOffice");
    String status = aForm.getFirstValue("status");
    if (officeKey != null) {
      ReturnMessage result = aManager.getFutureAppointmentsForOffice(officeKey, status);
      return this.getJsonRepresentation(result.getStatus(), result.getMessage(), result.getValue());
    } else {
      return this.getJsonRepresentation(
          "FAILURE", "apptOffice key is required!", null);
    }
  }
}
