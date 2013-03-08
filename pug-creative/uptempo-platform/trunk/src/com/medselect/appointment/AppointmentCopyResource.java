/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.appointment;

import com.medselect.server.BaseServerResource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

/**
 * Provides methods to copy batches of appointments.
 * @author Mike Gordon
 */
public class AppointmentCopyResource extends BaseServerResource {

  public AppointmentCopyResource() {
    super();
  }


  /**
   * Copy Appointments from one doctor to another, one date to another, or both.
   */
  @Post
  public Representation copyAppointments() {
    String status = "SUCCESS";
    String message = "";
    Form aForm = this.getRequest().getResourceRef().getQueryAsForm();
    String apptDoctor = aForm.getFirstValue("apptDoctor");
    //*** Assume that the start/end dates are provided as integers, seconds since Jan 1, 1970.
    String apptStartString = aForm.getFirstValue("apptStartDate");
    String apptEndString = aForm.getFirstValue("apptEndDate");
    Date apptStartDate;
    Date apptEndDate;
    if (apptStartString != null) {
      try {
        apptStartDate =
            new SimpleDateFormat("mm-dd-yyyy", Locale.ENGLISH).parse(apptStartString);
        apptEndDate =
            new SimpleDateFormat("mm-dd-yyyy", Locale.ENGLISH).parse(apptEndString);
      } catch (ParseException ex) {
        status = "FAILURE";
        message = "Could not parse dates: " + ex.toString();
      }
    } else {
      status = "FAILURE";
    }

    if (status.equals("SUCCESS")) {
      
    }
    return null;
  }
}
