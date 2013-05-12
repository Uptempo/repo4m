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
    //*** Get the operation.
    Map<String, String> valueMap = sForm.getValuesMap();
    if (valueMap.containsKey("op")) {
      AppointmentManager aManager = new AppointmentManager();

      //*** Do the reserved appointment cleanup.
      if(valueMap.get("op").equals("cleanup")) {
        aManager.resetReservedAppointments();
        StringRepresentation sr = new StringRepresentation("SUCCESS");
        return sr;
      }

      //*** Activate the task queue to do the time conversion.
      if(valueMap.get("op").equals("convert1task")) {
        String officeKey = valueMap.get("apptOffice");
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(
            withUrl("/service/appointmentcleanup")
            .method(TaskOptions.Method.GET)
            .param("op", "convert1").param("apptOffice", officeKey));
        StringRepresentation sr =
            new StringRepresentation("Started conversion job, office key: " + officeKey);
        return sr;
      }

      //*** Do the conversion to local office time storage.
      if(valueMap.get("op").equals("convert1")) {
        String officeKey = valueMap.get("apptOffice");
        aManager.changeApptStructureForTime(officeKey);
        StringRepresentation sr =
            new StringRepresentation("SUCCESS");
        return sr;
      }
    }

    StringRepresentation sr = new StringRepresentation("FAILURE: No operation specified.");
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
