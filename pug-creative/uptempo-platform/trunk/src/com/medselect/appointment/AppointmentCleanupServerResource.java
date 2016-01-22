/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.appointment;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;
import com.google.common.collect.ImmutableMap;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import com.medselect.util.Constants;
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
  ConfigManager cManager = new ConfigManager();

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
        case "startMarkAllCurrent": //*** Start the mark as current task with 10 min execution limit.
          Queue markAllCurrentQueue = QueueFactory.getQueue(Constants.MARK_APPTS_AS_CURRENT_QUEUE);
          markAllCurrentQueue.add(
            withUrl("/service/appointmentcleanup")
            .method(TaskOptions.Method.GET)
            .param("op", "markAllCurrent"));
        case "markAllCurrent": //*** Mark all appointments with no isPast field as current.
          aManager.markAllAsCurrent();
          
          sr = new StringRepresentation("Started mark all appointments as current task.");
          return sr;
        case "markOneCurrent": //*** Mark one appointment as current.
          String apptKey = valueMap.get("key");
          int success = aManager.markOneAsCurrent(apptKey);
          sr = new StringRepresentation("# marked:" + success);
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
  
  /**
   * Increment the marked as current count for all tasks.
   */
  private void incrementCurrentCount() {
    // Use memecache instead of config manager.
    
    SimpleConfigValue c = cManager.getSimpleConfigValue(
        Constants.APPOINTMENT_APP, Constants.CONFIG_APPTS_MARKED_CURRENT);
  }
}
