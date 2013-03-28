/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.appointment;

import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import com.medselect.config.ConfigManager;
import com.medselect.server.BaseServerResource;
import com.medselect.util.DateUtils;
import java.util.Calendar;
import java.util.Date;
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
 * @author Mike Gordon
 */
public class AppointmentServerResource extends BaseServerResource {
  
  public AppointmentServerResource() {
    super();
    itemValueMap = AppointmentManager.APPT_STRUCTURE;
    entityName = AppointmentManager.APPT_ENTITY_NAME;
    entityDisplayName = AppointmentManager.APPT_DISPLAY_NAME;
  }

  @Get
  public Representation readAppointments() {
    //*** Create a new appointment manager and get appointments.
    AppointmentManager manager = new AppointmentManager();
  
    //*** If this is a GET request with a key, return a single appointment.
    if (itemKey != null) {
      ReturnMessage message = manager.getAppointment(itemKey);
      UserService userService = UserServiceFactory.getUserService();
      String userEmail = "ANONYMOUS";
      if (userService.isUserLoggedIn()) {
        userEmail = userService.getCurrentUser().getEmail();
      }
      manager.logAppointmentView("User viewing appointment " + itemKey, userEmail);
      JsonRepresentation response =
        this.getJsonRepresentation(message.getStatus(), message.getMessage(), message.getValue());
      return response;
    }
  
    Form aForm = this.getRequest().getResourceRef().getQueryAsForm();
    //*** Get the parameters of the search.
    String apptDoctor = aForm.getFirstValue("apptDoctor");
    String searchStartDate = aForm.getFirstValue("apptStartDay");
    String searchEndDate = aForm.getFirstValue("apptEndDay");
    String showPatientsVal = aForm.getFirstValue("showPatients");
    boolean showPatients = false;
    if (showPatientsVal != null) {
      if (showPatientsVal.equalsIgnoreCase("TRUE")) {
        showPatients = true;
      }
    }

    String officeKey = null;
    officeKey = aForm.getFirstValue("apptOffice");

    Calendar sDate = null;
    if (searchStartDate != null) {
      sDate = DateUtils.getDateFromDateString(searchStartDate);
    }
    Calendar eDate = DateUtils.getDateFromDateString(searchEndDate);
  
    ReturnMessage message =
        manager.getAppointments(
            sDate, eDate, apptDoctor, SortDirection.ASCENDING, 0, officeKey, showPatients);
    
    JsonRepresentation response =
        this.getJsonRepresentation(message.getStatus(), message.getMessage(), message.getValue());
    return response;
  }

  @Post
  public Representation insertAppointment(Representation apptValue) {
    Form aForm = new Form(apptValue);

    Map<String, String> valueMap = aForm.getValuesMap();

    //*** Set appointment status default to available if not set.
    if (valueMap.get("status") == null) {
      valueMap.put("status", "AVAILABLE");
    }
    AppointmentManager aManager = new AppointmentManager();
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
    AppointmentManager manager = new AppointmentManager();
    ReturnMessage response = manager.updateAppointment(valueMap, itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;  
  }

  @Delete
  public Representation DeleteAppointment() {
    //*** Create a new appointment manager and delete the selected appointment.
    AppointmentManager manager = new AppointmentManager();
    ReturnMessage response = manager.deleteAppointment(itemKey);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
}
