/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.appointment;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;
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
  public Representation clearReservedAppointments(Representation params) {
    Form sForm = this.getRequest().getResourceRef().getQueryAsForm();
    String operation = "NONE";
    //*** Get the operation.
    Map<String, String> valueMap = sForm.getValuesMap();
    if (valueMap.containsKey("op")) {
      AppointmentManager aManager = new AppointmentManager();
      String officeKey;
      StringRepresentation sr;
      operation = valueMap.get("op");
      
      switch (operation) {
        case "cleanup":  //*** Do the reserved appointment cleanup.
          aManager.resetReservedAppointments();
          sr = new StringRepresentation("SUCCESS");
          return sr;
        case "convert1task":  //*** Activate the task queue to do the time conversion.
          officeKey = valueMap.get("apptOffice");
          Queue queue = QueueFactory.getDefaultQueue();
          queue.add(
            withUrl("/service/appointmentcleanup")
            .method(TaskOptions.Method.GET)
            .param("op", "convert1").param("apptOffice", officeKey));
          sr = new StringRepresentation("Started conversion job, office key: " + officeKey);
          return sr;
        case "convert1":
          officeKey = valueMap.get("apptOffice");
          aManager.changeApptStructureForTime(officeKey);
          sr = new StringRepresentation("SUCCESS");
          return sr;
        case "markPast": //*** Mark appointments that are in the past as past.
          int markedPast = aManager.markPastAppointments();
          sr = new StringRepresentation("Marked " + markedPast + " appointments as past.");
          return sr;
      }
    }

    StringRepresentation sr = new StringRepresentation(
        "FAILURE: No operation specified, op=" + operation + ".");
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
