/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.appointment;

import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.medselect.common.ReturnMessage;
import com.medselect.server.BaseServerResource;
import java.util.Map;
import org.json.JSONException;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;


/**
 *
 * @author Mike Gordon
 */
public class AppointmentServerResource extends BaseServerResource {
  
  AppointmentManager aManager = new AppointmentManager();

  public AppointmentServerResource() {
    super();
    itemValueMap = AppointmentManager.APPT_STRUCTURE;
    entityName = AppointmentManager.APPT_ENTITY_NAME;
    entityDisplayName = AppointmentManager.APPT_DISPLAY_NAME;
  }

  @Get
  public Representation readAppointments() {  
    //*** If this is a GET request with a key, return a single appointment.
    if (itemKey != null) {
      return getSingleAppt(itemKey);
    }
  
    Form aForm = this.getRequest().getResourceRef().getQueryAsForm();
    //*** Get the parameters of the search.
    String apptDoctor = aForm.getFirstValue("apptDoctor");
    String futureOnlyVal = aForm.getFirstValue("futureOnly");
    String searchStartDate = aForm.getFirstValue("apptStartDay");
    String searchEndDate = aForm.getFirstValue("apptEndDay");
    String showPatientsVal = aForm.getFirstValue("showPatients");
    
    boolean futureOnly = false;
    if (futureOnlyVal != null) {
      if (futureOnlyVal.equalsIgnoreCase("TRUE")) {
        futureOnly = true;
      }
    }

    boolean showPatients = false;
    if (showPatientsVal != null) {
      if (showPatientsVal.equalsIgnoreCase("TRUE")) {
        showPatients = true;
      }
    }

    String officeKey = null;
    officeKey = aForm.getFirstValue("apptOffice");
    ReturnMessage result = aManager.getAppointments(
        searchStartDate,
        searchEndDate,
        apptDoctor,
        SortDirection.ASCENDING,
        0,
        officeKey,
        showPatients,
        futureOnly);

    JsonRepresentation response =
        this.getJsonRepresentation(result.getStatus(), result.getMessage(), result.getValue());
    return response;
  }

  /**
   * Helper function to get a single appointment.
   * @param apptKey The appointment key.
   * @return 
   */
  private Representation getSingleAppt(String apptKey) {
    if (apptKey.equalsIgnoreCase("csv")) {
      return getReservedApptCSV();
    }
    ReturnMessage message = aManager.getAppointment(apptKey);
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = "ANONYMOUS";
    if (userService.isUserLoggedIn()) {
      userEmail = userService.getCurrentUser().getEmail();
    }
    //*** Get the appointment info for a better audit message.
    String apptAuditMessage;
    try {
      String doctor = message.getValue().getString("apptDoctor");
      String date = message.getValue().getString("apptDate");
      String startHr = message.getValue().getString("apptStartHr");
      String startMin = message.getValue().getString("apptStartMin");
      apptAuditMessage = "with " + doctor + " on " + date + " at " + startHr + ":" + startMin;
    } catch (JSONException ex) {
      //*** Fill this in with a generic message.
      apptAuditMessage = "(apppointment info not found)";
    }
    aManager.logAppointmentView("User viewing appointment " + apptAuditMessage, userEmail);
    JsonRepresentation response =
      this.getJsonRepresentation(message.getStatus(), message.getMessage(), message.getValue());
    return response;
  }
  
  /**
   * Get a CSV representation for all reserved appointments.
   * @return The CSV representation of reserved appointments.
   */
  private Representation getReservedApptCSV() {
    ReturnMessage result = aManager.getAllAppointments("RESERVED");
    return this.getCSV(result, "appts.csv");
  }

  @Post
  public Representation insertAppointment(Representation apptValue) {
    Form aForm = new Form(apptValue);

    Map<String, String> valueMap = aForm.getValuesMap();

    //*** Set appointment status default to available if not set.
    if (valueMap.get("status") == null) {
      valueMap.put("status", "AVAILABLE");
    }
    ReturnMessage response = aManager.createAppointment(valueMap);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }

  @Put
  public Representation UpdateAppointment(Representation apptValue) {
    Form aForm = new Form(apptValue);
    Map<String, String> valueMap = aForm.getValuesMap();
    //*** If a patient e-mail is provided, figure out if the patient user exists.
    //*** If not, create the user.
    ReturnMessage response = aManager.updateAppointment(valueMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;  
  }

  @Delete
  public Representation DeleteAppointment() {
    //*** Create a new appointment manager and delete the selected appointment.
    ReturnMessage response = aManager.deleteAppointment(itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
}
