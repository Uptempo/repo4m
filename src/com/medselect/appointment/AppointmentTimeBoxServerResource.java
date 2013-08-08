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
public class AppointmentTimeBoxServerResource extends BaseServerResource {
  AppointmentManager aManager = new AppointmentManager();

  public AppointmentTimeBoxServerResource() {
    super();
    itemValueMap = AppointmentManager.APPT_STRUCTURE;
    entityName = AppointmentManager.APPT_ENTITY_NAME;
    entityDisplayName = AppointmentManager.APPT_DISPLAY_NAME;
  }

  @Get
  public Representation readAppointments() {
    String apptStartTime, apptEndTime, apptDate, officeKey, doctorKey;
    //*** Read the parameters.
    Form aForm = this.getRequest().getResourceRef().getQueryAsForm();
    apptStartTime = aForm.getFirstValue("apptStartTime");
    apptEndTime = aForm.getFirstValue("apptEndTime");
    apptDate = aForm.getFirstValue("apptDate");
    officeKey = aForm.getFirstValue("apptOffice");
    if (apptStartTime != null &&
        apptEndTime != null &&
        apptDate != null &&
        officeKey != null) {
        doctorKey = aForm.getFirstValue("apptDoctor");

      ReturnMessage result = aManager.getTimeBoxedAppointments(
          officeKey, doctorKey, apptDate, apptStartTime, apptEndTime);
      return this.getJsonRepresentation(result.getStatus(), result.getMessage(), result.getValue());
    } else {
      return this.getJsonRepresentation(
          "FAILURE", "apptOffice, apptDate, apptStartTime, and apptEndTime are required!", null);
    }
  }
}
