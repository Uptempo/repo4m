/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.appointment;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.medselect.audit.AuditLogManager;
import com.medselect.billing.BillingOfficeManager;
import com.medselect.billing.SimpleBillingOffice;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import com.medselect.doctor.DoctorManager;
import com.medselect.doctor.SimpleDoctorValue;
import com.medselect.user.UserManager;
import com.medselect.util.Constants;
import com.medselect.util.DateUtils;
import com.medselect.util.MailUtils;
import com.uptempo.google.GoogleCalendarProxy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Appointment manager class. Provides methods to get and create appointments.
 * 
 * @author Mike Gordon
 */
public class AppointmentManager extends BaseManager {
  public static final String APPT_ENTITY_NAME = "Appointment";
  public static final String APPT_DISPLAY_NAME = "Appointment";
  public static final Map<String, BaseManager.FieldType> APPT_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("patientUser", BaseManager.FieldType.STRING)
          .put("apptDoctor", BaseManager.FieldType.STRING)
          .put("apptDoctorKey", BaseManager.FieldType.STRING)
          .put("description", BaseManager.FieldType.STRING)
          .put("notes", BaseManager.FieldType.TEXT)
          .put("apptStartHr", BaseManager.FieldType.INTEGER)
          .put("apptEndHr", BaseManager.FieldType.INTEGER)
          .put("apptStartMin", BaseManager.FieldType.INTEGER)
          .put("apptEndMin", BaseManager.FieldType.INTEGER)
          .put("apptDate", BaseManager.FieldType.STRING)
          .put("apptStartLong", BaseManager.FieldType.LONG)
          .put("status", BaseManager.FieldType.STRING)
          .put("isPast", BaseManager.FieldType.STRING)
          .build();

  private static final long APPT_CHANGE_REACHBACK = 172800000;
  private static final String APPT_AVAILABLE = "AVAILABLE";
  private static final String APPT_RESERVED = "HELD";
  private static final String APPT_CANCELLED = "CANCELLED";
  private static final String APPT_SCHEDULED = "RESERVED";
  private static final String APPT_UPDATE = "UPDATE";
  private static final String APPT_NEW = "NEW";
  private final BillingOfficeManager officeManager;
  private final AuditLogManager am = new AuditLogManager();
  private final ConfigManager cm = new ConfigManager();

  public AppointmentManager() {
    super(APPT_STRUCTURE, APPT_ENTITY_NAME, APPT_DISPLAY_NAME);
    officeManager = new BillingOfficeManager();
  }

  /**
   * Creates an appointment given appointment data.
   * @param data  The appointment data.
   * @return A status, message, and data.  The data will contain the GAE key of the new appointment.
   */
  public ReturnMessage createAppointment(Map<String, String> data) {    
    ReturnMessage result;
    String message = "";
    String googleApptId = null;
    Map <String, String> dataCopy = new HashMap<String, String>();
    SimpleBillingOffice officeData = null;
    
    //*** Get the appointment attributes for later use.
    String apptStatus = data.get("status");
    //*** Validate the appointment inputs for the service call.
    List<String> validation =
        this.validateAppointmentInputs(data, "NEW", null);
    if (!validation.isEmpty()) {
      message = "Required fields missing from the appointment data:";
      //*** Assemble the return message.
      for (String m : validation) {
        message += m + " ";
      }
      return new ReturnMessage.Builder()
          .message(message)
          .status("FAILED")
          .value(null)
          .build();
    }

    //*** Check if this is a new user.  If so, create the user too.
    String newUserKey = this.checkAndCreateUser(data);
    String userEmail = data.get("patientUser");
    
    //*** Check if the user was included in this update.  A user can be included and already exist,
    //*** so the checkAndCreateUser result is not a reliable return value.
    boolean userExists = false;
    if (userEmail != null && !userEmail.isEmpty()) {
      userExists = true;
    }

    //*** Check if the status is now scheduled and whether the patient user exists.
    if (apptStatus.toUpperCase().equals(AppointmentManager.APPT_SCHEDULED) && !userExists) {
      message += "Attendee e-mail required to schedule appointment.<br />";
      result = new ReturnMessage.Builder()
          .message(message)
          .status("FAILED")
          .value(null)
          .build();
    } else {
      //*** Get the office key and time zone.
      String officeKeyVal = data.get("apptOffice");
      Key officeKey = KeyFactory.stringToKey(officeKeyVal);
      officeData = officeManager.getSimpleBillingOffice(officeKeyVal);
      int tzOffset = officeData.getOfficeTimeZoneOffset();
      tzOffset = DateUtils.convertOffsetForDst(tzOffset, cm, officeData);

      //*** Create the Google Calendar ID entry.  Ignore the error, but report it as a log error.
      try {
        googleApptId = this.createUpdateGcalEntry(data, null, tzOffset);
        message += "Google calendar event created with ID: " + googleApptId + "<br />";
      } catch (Exception ex) {
        LOGGER.severe("Google calendar update failed for new appointment");
      }
      //*** Transform the data as necessary.
      data = this.transformAppointmentData(data, googleApptId, tzOffset);
      dataCopy.putAll(data);
      result = this.doCreate(data, false, officeKey);
      //*** Log appointment creation
      if(result.getStatus().equals("SUCCESS")) {
        message += result.getMessage() + "<br />";
        if (userEmail == null) {
          userEmail = "UNKNOWN";
        }
        message = "New Appointment created.";
        logAppointmentEvent("NEW", message, data.get("user"));
      }
    }
    
    //*** E-mail the user if the appointment creation was successful and there is a patient set.
    if (result.getStatus().equals("SUCCESS") && userExists) {
      SimpleConfigValue sendEmailFlag =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.SEND_USER_EMAIL);
      if (sendEmailFlag != null && sendEmailFlag.getConfigValue().toLowerCase().equals("true")) {
        sendAppointmentEmail(
            dataCopy,
            userEmail,
            "NEW",
            officeData);
      }
    }
    
    //*** Assemble return value
    if (newUserKey != null) {
      message += result.getMessage() + " <br />Created new user " + data.get("patientUser");
    }
    return new ReturnMessage.Builder()
        .message(message)
        .status(result.getStatus())
        .value(result.getValue())
        .build();
  }

  /**
   * Executes all of the data transformations that have to occur to make the appointment a valid
   * appointment.
   * @param data The appointment data for insert/update.
   * @param googleApptId The id of a Google calendar entry created, if any.
   * @param offset The time zone offset for the office, in hours, from GMT.
   * @return 
   */
  private Map<String, String> transformAppointmentData(
      Map<String, String> data, String googleApptId, int offset) {
    //*** Assume the appointment is a current/future appointment.
    data.put("isPast", Constants.APPT_FUTURE);

    //*** Assemble date values for filter and sort.
    int apptStartHr = Integer.parseInt(data.get("apptStartHr"));
    int apptStartMin = Integer.parseInt(data.get("apptStartMin"));
    Date apptStartDate =
        DateUtils.getDateFromValues(data.get("apptDate"), apptStartHr, apptStartMin, offset);
    long startDateVal = apptStartDate.getTime();
    data.put("apptStartLong", String.valueOf(startDateVal));

    //*** Add the Google calendar ID.
    data.put(Constants.APPT_GOOGLE_KEY_FIELD, googleApptId);
    //*** Remove the office key, if it's included.
    data.remove("apptOffice");
    //*** Fill in the doctor name from the key, for quick display.
    DoctorManager dManager = new DoctorManager();
    SimpleDoctorValue doctor = dManager.getSimpleDoctorValues(data.get("apptDoctorKey"));
    String doctorTitle = "Dr.";
    if (doctor.getTitles() != null) {
      doctorTitle = doctor.getTitles().get(0);
    }
    String doctorName = doctorTitle + " " + doctor.getFirstName() + " " + doctor.getLastName();
    data.put("apptDoctor", doctorName);
    return data;
  }
  
  public ReturnMessage updateAppointment(Map<String, String> data, String apptKey) {
    ReturnMessage result;
    String message = "";
    String googleApptId = null;
    Map <String, String> dataCopy = new HashMap();
    data.put("key", apptKey);
    //*** Get the appointment attributes for later use.
    String apptStatus = data.get("status");
    String userEmail = data.get("patientUser");
    
    boolean userExists = false;
    if (userEmail != null && !userEmail.isEmpty()) {
      userExists = true;
    }

    //*** Get this appointment's information to detect what type of transition this is.
    Key dsKey = KeyFactory.stringToKey(apptKey);
    Entity apptEntity = null;
    String transitionOperation = "UPDATED";
    try {
      apptEntity = ds.get(dsKey);
      //*** Make a copy of the data, including all of the existing data.
      dataCopy = getUpdateMapEntity(apptEntity, data);

      //*** Figure out the transition of this appointment, from state to state.
      String oldApptState = (String)apptEntity.getProperty("status");
      transitionOperation = getAppointmentTransition(oldApptState, apptStatus);
      if (transitionOperation.equals("UNKNOWN")) {
        transitionOperation = "UPDATED";
      }
    } catch (EntityNotFoundException ex) {
      LOGGER.severe("Error getting existing appointment: " + ex.toString());
      return new ReturnMessage.Builder()
          .message("Existing appointment not found!")
          .status("FAILED")
          .value(null)
          .build();
    }

    //*** Validate the appointment inputs for the service call.
    List<String> validation =
        this.validateAppointmentInputs(dataCopy, transitionOperation, apptEntity);
    if (!validation.isEmpty()) {
      message = "Required fields missing from the appointment data:";
      //*** Assemble the return message.
      for (String m : validation) {
        message += m + " ";
      }
      return new ReturnMessage.Builder()
          .message(message)
          .status("FAILED")
          .value(null)
          .build();
    }
 
    //*** Check if this is a new user.  If so, create the user too.
    String newUserKey = this.checkAndCreateUser(dataCopy);
    
    //*** Check if the user was included in this update.  A user can be included and already exist,
    //*** so the checkAndCreateUser result is not a reliable return value.
    if (apptStatus.toUpperCase().equals(AppointmentManager.APPT_SCHEDULED) && !userExists) {
      result = new ReturnMessage.Builder()
          .message("Patient e-mail required to schedule appointment.")
          .status("FAILED")
          .value(null)
          .build();
    } else {
      Key officeKey = apptEntity.getParent();
        SimpleBillingOffice office =
          officeManager.getSimpleBillingOffice(KeyFactory.keyToString(officeKey));
      int tzOffset = office.getOfficeTimeZoneOffset();
      tzOffset = DateUtils.convertOffsetForDst(tzOffset, cm, office);
      //*** Update the Google Calendar ID entry.  Ignore the error, but report it as a log error.
      try {
        googleApptId =
            this.createUpdateGcalEntry(dataCopy, apptEntity, tzOffset);
        message += "Google calendar event updated with ID: " + googleApptId + "<br />";
      } catch (Exception ex) {
        LOGGER.severe("Google calendar update failed for appointment (key): " + apptKey);
      }

      //*** Transform the data as necessary.
      data = this.transformAppointmentData(dataCopy, googleApptId, tzOffset);
      result = this.doUpdate(dataCopy);
      //*** Log appointment update.
      if(result.getStatus().equals("SUCCESS")) {
        if (userEmail == null) {
          userEmail = "UNKNOWN";
        }
        String dateVal = "UNKNOWN";
        if (dataCopy.get("apptDate") != null) {
          dateVal = dataCopy.get("apptDate");
        }
        message = "Appointment " + transitionOperation +
                  ", on date " + dateVal + " with user " + userEmail;
        if (dataCopy.containsKey("source") && dataCopy.get("source") != null) {
          message += ", source:" + dataCopy.get("source");
        } 
        
        logAppointmentEvent(transitionOperation, message, userEmail);
      }
    }

    if (newUserKey != null && userExists) {
      message = message + result.getMessage() + " <br />Created new user " + userEmail;
      result = new ReturnMessage.Builder()
          .message(message)
          .status(result.getStatus())
          .value(result.getValue())
          .build();
      LOGGER.info(message);
    }
 
    //*** E-mail the user if the appointment update was successful and user is set.
    if (result.getStatus().equals("SUCCESS") && userExists) {
      //*** If the operation is scheduled, then also e-mail the office e-mail.
      String officeKey = KeyFactory.keyToString(apptEntity.getParent());
      SimpleBillingOffice officeData = officeManager.getSimpleBillingOffice(officeKey);
      sendAppointmentEmail(
          dataCopy,
          userEmail,
          transitionOperation,
          officeData);
    }

    return result;
  }

  /**
   * Helper function to log an appointment view event
   * @param message The appointment message.
   * @param userEmail The user e-mail of the user who created/updated the appointment.
   */
  public void logAppointmentView(String message, String userEmail) {
    am.logAudit(Constants.APPOINTMENT_APP, Constants.AUDIT_VIEW_APPTS, message, "N/A", userEmail);
  }

  /**
   * Helper function to log an appointment event.
   * @param transition The appointment transition.
   * @param message The appointment message.
   * @param userEmail The user e-mail of the user who created/updated the appointment.
   */
  private void logAppointmentEvent(String transition, String message, String userEmail) {
    if (transition.equals(AppointmentManager.APPT_SCHEDULED)) {
      am.logAudit(
          Constants.APPOINTMENT_APP, Constants.AUDIT_SCHEDULE_APPT, message, "N/A", userEmail);
    } else if (transition.equals(AppointmentManager.APPT_RESERVED)) {
      am.logAudit(
          Constants.APPOINTMENT_APP, Constants.AUDIT_RESERVE_APPT, message, "N/A", userEmail);
    } else if (transition.equals(AppointmentManager.APPT_NEW)) {
      am.logAudit(Constants.APPOINTMENT_APP, Constants.AUDIT_NEW_APPT, message, "N/A", userEmail);
    } else if (transition.equals(AppointmentManager.APPT_UPDATE)) {
      am.logAudit(
          Constants.APPOINTMENT_APP, Constants.AUDIT_UPDATE_APPT, message, "N/A", userEmail);
    }
  }
  
  /**
   * Validates the appointment inputs.
   * @param data
   * @param transition The transition value for the appointment.
   * @param appointment The existing appointment or null.
   * @return true/false depending on the validity of the data.
   */
  private List<String> validateAppointmentInputs(Map<String, String> data, String transition, Entity appointment) {
    //*** If the status transition is scheduled, the patient first name, last name, and e-mail must
    //*** be included.
    List<String> result = new ArrayList<String>();
    if (transition.equals(AppointmentManager.APPT_SCHEDULED)) {
      if (StringUtils.isEmpty(data.get("patientUser"))) {
        result.add("Missing patientUser parameter");
      }
      if (StringUtils.isEmpty(data.get("patientFName"))) {
        result.add("Missing patientFName parameter");
      }
      if (StringUtils.isEmpty(data.get("patientLName"))) {
        result.add("Missing patientLName parameter");
      }
    }
    if (appointment == null) {
      if (StringUtils.isEmpty(data.get("apptDoctorKey"))) {
        result.add("Missing apptDoctorKey parameter");
      }
      if (StringUtils.isEmpty(data.get("status"))) {
        result.add("Missing status parameter");
      }
      if (StringUtils.isEmpty(data.get("apptStartHr"))) {
        result.add("Missing apptStartHr parameter");
      }
      if (StringUtils.isEmpty(data.get("apptEndHr"))) {
        result.add("Missing apptEndHr parameter");
      }
      if (StringUtils.isEmpty(data.get("apptStartMin"))) {
        result.add("Missing apptStartMin parameter");
      }
      if (StringUtils.isEmpty(data.get("apptEndMin"))) {
        result.add("Missing apptEndMin parameter");
      }
      if (StringUtils.isEmpty(data.get("apptDate"))) {
        result.add("Missing apptDate parameter");
      }
      //*** Make sure an attached office key is provided on a new appointment.
      if (StringUtils.isEmpty(data.get("apptOffice"))) {
        result.add("Missing apptOffice parameter");
      }
    }
    return result;
  }
  
  /**
   * Checks the parameters to determine if a user exists in the user management function.  Creates
   * the user if the user doesn't exist and returns the GAE key of the newly created user.
   * 
   * @param data The data from the current appointment creation operation.
   * @return A GAE key of the new user if a new user was created, otherwise null.
   */
  private String checkAndCreateUser(Map<String, String> data) {
    if (data.get("patientUser") != null && !(data.get("patientUser").isEmpty())) {
      UserManager uManager = new UserManager(Constants.APPOINTMENT_APP);
      Map <String, String> params = new HashMap <String, String>();
      params.put("email", data.get("patientUser"));
      if (!data.get("patientUser").isEmpty() && !uManager.doesUserExist(data.get("patientUser"))) {
        //*** Re-use the params.
        params.clear();
        params.put("key", data.get("patientUser"));
        params.put("email", data.get("patientUser"));
        params.put("firstName", data.get("patientFName"));
        params.put("lastName", data.get("patientLName"));
        params.put("cell", data.get("patientPhone"));
        if (data.containsKey("password")) {
          params.put("password", data.get("password"));
          data.remove("password");
        }
        if (data.containsKey("address1")) {
          params.put("address1", data.get("address1"));
          data.remove("address1");
        }
        if (data.containsKey("address2")) {
          params.put("address2", data.get("address2"));
          data.remove("address2");
        }
        if (data.containsKey("city")) {
          params.put("city", data.get("city"));
          data.remove("city");
        }
        if (data.containsKey("state")) {
          params.put("state", data.get("state"));
          data.remove("state");
        }
        if (data.containsKey("age")) {
          params.put("age", data.get("age"));
          data.remove("age");
        }
        if (data.containsKey("source")) {
          params.put("source", data.get("source"));
        }
        ReturnMessage m = uManager.createUser(params);
        return m.getKey();
      } else {
        return null;
      }
    }
    return null;
  }

  /**
   * Creates or updates a Google calendar entry corresponding to the appointment.  Checks if the
   * office has a Google calendar integration setup, then sets up the request to Google to
   * create/update the calendar entry.
   * 
   * @param data The appointment data.
   * @param apptEntity Either an appointment entity for an existing appointment or null to signal
   *     that an existing appointment doesn't exist.
   * @param offset Offset from GMT for the local office time.
   * @return The Google calendar entry key.
   */
  private String createUpdateGcalEntry(Map<String, String> data, Entity apptEntity, int offset) {
    ConfigManager cm = new ConfigManager();
    String apptStatus = data.get("status");
    String apptDoctor = data.get("apptDoctor");
    String userEmail = data.get("patientUser");
    String patientFname = data.get("patientFName");
    String patientLname = data.get("patientLName");
    String calendarIdVal = null;
 
    //*** Check if this office is setup to use Google Calendar.
    //*** If the office calendar is accessible, add this calendar entry to it.
    SimpleConfigValue calendarUser =
        cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.APPT_CALENDAR_USER);
    SimpleConfigValue calendarAccessToken =
        cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.APPT_OFFICE_CAL_ACCESS_TOKEN);
    SimpleConfigValue calendarRefreshToken =
        cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.APPT_OFFICE_CAL_REFRESH_TOKEN);

    //*** Only execute this if the Google calendar is configured.
    if (calendarUser != null && calendarAccessToken != null) {
      Date apptStartDate = null, apptEndDate = null;
      //*** Get the appointment calendar key, if one exists.  Get existing appt attributes.
      String apptGoogleId = null;
      String calendarBody = "ERROR";

      //*** If the appointment exists, use the Google Calendar ID and do an update.
      if (apptEntity != null && apptEntity.hasProperty(Constants.APPT_GOOGLE_KEY_FIELD)) {
        apptGoogleId = (String)apptEntity.getProperty(Constants.APPT_GOOGLE_KEY_FIELD);
      }
 
      String apptDate;
      int apptStartHr, apptEndHr, apptStartMin, apptEndMin;
      if (data.containsKey("apptStartHr")) {
        apptStartHr = Integer.parseInt(data.get("apptStartHr"));
      } else {
        apptStartHr = (Integer)apptEntity.getProperty("apptStartHr");
      }
      if (data.containsKey("apptEndHr")) {
        apptEndHr = Integer.parseInt(data.get("apptEndHr"));
      } else {
        apptEndHr = (Integer)apptEntity.getProperty("apptEndHr");
      }
      if (data.containsKey("apptStartMin")) {
        apptStartMin = Integer.parseInt(data.get("apptStartMin"));
      } else {
        apptStartMin = (Integer)apptEntity.getProperty("apptStartMin");
      }
      if (data.containsKey("apptEndMin")) {
        apptEndMin = Integer.parseInt(data.get("apptEndMin"));
      } else {
        apptEndMin = (Integer)apptEntity.getProperty("apptEndMin");
      }
      if (data.containsKey("apptDate")) {
        apptDate = data.get("apptDate");
      } else {
        apptDate = (String)apptEntity.getProperty("apptDate");
      }

      apptStartDate = DateUtils.getDateFromValues(apptDate, apptStartHr, apptStartMin, offset);
      apptEndDate = DateUtils.getDateFromValues(apptDate, apptEndHr, apptEndMin, offset);

      calendarBody = 
          "An appointment is scheduled starting at " +
          DateUtils.getReadableTime(apptDate, apptStartHr, apptStartMin, offset) +
          " and ending at " +
          DateUtils.getReadableTime(apptDate, apptStartHr, apptStartMin, offset) + ".";
      
      String calendarSubject =
          apptStatus + " Appointment for " + patientFname + " " + patientLname +
          " with " + apptDoctor;
      
      String apptLocation = apptDoctor + "'s Office";
      if (userEmail != null && !userEmail.isEmpty()) {
        calendarBody += 
            " This appointment is scheduled with " + apptDoctor +
            " for " + patientFname + " " + patientLname + ", attendee e-mail:" + userEmail + ".";
      }

      //*** Check if the Google calendar ID exists, if so, do an update instead of an insert.
      ReturnMessage calEntryMessage;
      if (apptGoogleId == null) {
        calEntryMessage = GoogleCalendarProxy.insertUpdateAppointmentEntry(
            calendarAccessToken.getConfigValue(),
            calendarRefreshToken.getConfigValue(),
            apptStartDate,
            apptEndDate,
            calendarSubject,
            calendarBody,
            apptLocation,
            calendarUser.getConfigValue(),
            apptGoogleId);
      } else {
        calEntryMessage = GoogleCalendarProxy.insertUpdateAppointmentEntry(
          calendarAccessToken.getConfigValue(),
          calendarRefreshToken.getConfigValue(),
          apptStartDate,
          apptEndDate,
          calendarSubject,
          calendarBody,
          apptLocation,
          calendarUser.getConfigValue(),
          null);
      }
      JSONObject calEntryResponse = calEntryMessage.getValue();
      try {
        if (calEntryResponse.has("status") &&
            !((String)calEntryResponse.get("status")).equals("cancelled")) {
          calendarIdVal = (String)calEntryResponse.get("id");
        } 
      } catch (JSONException ex) {
        LOGGER.severe("Google calendar response parse failed: " + ex.toString());
      }
    }
 
    return calendarIdVal;
  }
  
  private void sendAppointmentEmail(
      Map<String, String> data,
      String userEmail, 
      String operation,
      SimpleBillingOffice officeData) {
    ConfigManager cm = new ConfigManager();
    String officeSubject, userSubject;
    String officeMessageCode, userMessageCode;
    officeMessageCode = Constants.APPT_OFFICE_EMAIL_MESSAGE;
    userMessageCode = Constants.APPT_USER_EMAIL_MESSAGE;
    if (operation.equalsIgnoreCase(AppointmentManager.APPT_SCHEDULED)) {
      officeSubject =
          cm.getSimpleConfigValue(
              Constants.APPOINTMENT_APP,
              Constants.APPT_EMAIL_SUBJECT_OFFICE_SCHEDULE_CODE)
          .getConfigValue();
      userSubject =
          cm.getSimpleConfigValue(
              Constants.APPOINTMENT_APP,
              Constants.APPT_EMAIL_SUBJECT_USER_SCHEDULE_CODE)
          .getConfigValue();
    } else if (operation.equalsIgnoreCase("CANCELLED")) {
      officeSubject =
          cm.getSimpleConfigValue(
              Constants.APPOINTMENT_APP,
              Constants.APPT_EMAIL_SUBJECT_OFFICE_CANCEL_CODE)
          .getConfigValue();
      userSubject =
          cm.getSimpleConfigValue(
              Constants.APPOINTMENT_APP,
              Constants.APPT_EMAIL_SUBJECT_USER_CANCEL_CODE)
          .getConfigValue();
    } else if (operation.equalsIgnoreCase("UPDATED")) {
      officeSubject =
          cm.getSimpleConfigValue(
              Constants.APPOINTMENT_APP,
              Constants.APPT_EMAIL_SUBJECT_OFFICE_UPDATE_CODE)
          .getConfigValue();
      userSubject =
          cm.getSimpleConfigValue(
              Constants.APPOINTMENT_APP,
              Constants.APPT_EMAIL_SUBJECT_USER_UPDATE_CODE)
          .getConfigValue();
    } else {
      officeSubject =
          cm.getSimpleConfigValue(
              Constants.APPOINTMENT_APP,
              Constants.APPT_EMAIL_SUBJECT_OFFICE_UPDATE_CODE)
          .getConfigValue();
      userSubject =
          cm.getSimpleConfigValue(
              Constants.APPOINTMENT_APP,
              Constants.APPT_EMAIL_SUBJECT_USER_UPDATE_CODE)
          .getConfigValue();
      LOGGER.warning("Unknown appointment operation");
    }
    SimpleConfigValue sendEmailFlag =
        cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.SEND_USER_EMAIL);

    if (sendEmailFlag != null && sendEmailFlag.getConfigValue().toLowerCase().equals("true")) {
      //*** Send an e-mail indicating appointment creation.
      SimpleConfigValue sendEmailFrom =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.APPT_REPLY_EMAIL);
      SimpleConfigValue sendEmailFromDisplay =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.APPT_REPLY_DISPLAY);
      //*** Setup the user e-mail message.  If the office has a custom e-mail message, use it.
      String userEmailMessage;
      if (officeData.getOfficeUserEmailTemplate() != null) {
        userEmailMessage = officeData.getOfficeUserEmailTemplate();
      } else {
        userEmailMessage =
            cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, userMessageCode).getConfigText();
      }
      String officeEmailMessage;
      if (officeData.getOfficeEmailTemplate() != null) {
        officeEmailMessage = officeData.getOfficeEmailTemplate();
      } else {
        officeEmailMessage =
            cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, officeMessageCode).getConfigText();
      }

      String userEmailDisplay = data.get("patientFName") + " " + data.get("patientLName");
      //*** Replace the subject with values.
      officeSubject = officeSubject.replace(Constants.APPT_DR_NAME, data.get("apptDoctor"));
      userSubject = userSubject.replace(Constants.APPT_DR_NAME, data.get("apptDoctor"));
      //*** Fill in the office phone number.
      if (!officeData.getOfficePhones().isEmpty()) {
        data.put("officePhone", officeData.getOfficePhones().get(0));
      } else {
        data.put("officePhone", "UNKNOWN");
      }

      //*** Send the e-mails.
      //*** Send e-mail to the user/patient.
      try {
        data.put("emailSubject", userSubject);
        //*** Replace the body vars.
        String emailBody = assembleApptEmailBody(userEmailMessage, data, officeData, operation);
        MailUtils mailSender = new MailUtils();
        mailSender.sendMail(
            sendEmailFrom.getConfigValue(),
            sendEmailFromDisplay.getConfigValue(),
            userEmail,
            userEmailDisplay,
            userSubject,
            emailBody);
      } catch (Exception ex) {
        LOGGER.severe(
            "Failed to send e-mail to user on appointment " +
            operation + "!  Error was: " + ex.toString());
      }
      
      //*** Send e-mail to the office.
      try {
        data.put("emailSubject", officeSubject);
        //*** Replace the body vars.
        String officeEmailBody = assembleApptEmailBody(
            officeEmailMessage, data, officeData, operation);
        MailUtils mailSender = new MailUtils();
        mailSender.sendMail(
            sendEmailFrom.getConfigValue(),
            sendEmailFromDisplay.getConfigValue(),
            officeData.getOfficeEmail(),
            officeData.getOfficeEmail(),
            officeSubject,
            officeEmailBody);
      } catch (Exception ex) {
        LOGGER.severe(
            "Failed to send office e-mail on appointment " +
            operation + "!  Error was: " + ex.toString());
      }
    }
  }
  
  /**
   * Assembles the e-mail body of an appointment, given the template and values.
   * 
   * @param template The e-mail template.
   * @param data The values for the appointment.
   * @param timeZoneOffset The correct time zone offset for any dates and times in the e-mail.
   * @return The assembled e-mail body.
   */
  private String assembleApptEmailBody(
      String template,
      Map<String, String> data,
      SimpleBillingOffice officeData,
      String status) {
    String emailBody = template;
    if (data.get("patientFName") != null) {
      emailBody = emailBody.replace(Constants.APPT_PATIENT_FNAME, data.get("patientFName"));
    }
    if (data.get("patientLName") != null) {
      emailBody = emailBody.replace(Constants.APPT_PATIENT_LNAME, data.get("patientLName"));
    }
    if (data.get("patientPhone") != null) {
      emailBody = emailBody.replace(Constants.APPT_PATIENT_PHONE, data.get("patientPhone"));
    }
    if (data.get("patientUser") != null) {
      emailBody = emailBody.replace(Constants.APPT_PATIENT_EMAIL, data.get("patientUser"));
    }

    emailBody = emailBody.replace(Constants.APPT_DR_NAME, data.get("apptDoctor"));

    //*** Assemble the date/time strings.
    String apptDate = data.get("apptDate");
    int apptStartHr = Integer.parseInt(data.get("apptStartHr"));
    int apptEndHr = Integer.parseInt(data.get("apptEndHr"));
    int apptStartMin = Integer.parseInt(data.get("apptStartMin"));
    int apptEndMin = Integer.parseInt(data.get("apptEndMin"));
    String startDateStr =
        DateUtils.getReadableTime(
            apptDate, apptStartHr, apptStartMin, officeData.getOfficeTimeZoneOffset());
    String endDateStr =
        DateUtils.getReadableTime(
            apptDate, apptEndHr, apptEndMin, officeData.getOfficeTimeZoneOffset());
    emailBody = emailBody.replace(Constants.APPT_START_TIME, startDateStr);
    emailBody = emailBody.replace(Constants.APPT_END_TIME, endDateStr);
    emailBody = emailBody.replace(Constants.APPT_EMAIL_STATUS, status);

    //*** Fill in office data.
    String fullOfficeAddress =
        officeData.getOfficeAddress1() + " " + officeData.getOfficeAddress2();
    emailBody = emailBody.replace(Constants.APPT_DR_OFFICE_ADDRESS, fullOfficeAddress);
    emailBody = emailBody.replace(Constants.APPT_DR_OFFICE_CITY, officeData.getOfficeCity());
    emailBody = emailBody.replace(Constants.APPT_DR_OFFICE_STATE, officeData.getOfficeState());
    emailBody = emailBody.replace(Constants.APPT_DR_OFFICE_NAME, officeData.getOfficeName());
    emailBody = emailBody.replace(Constants.APPT_DR_OFFICE_PHONE, data.get("officePhone"));
    if (data.get("description") != null && !data.get("description").isEmpty()) {
      emailBody = emailBody.replace(Constants.APPT_EMAIL_DESCRIPTION, data.get("description"));
    } else {
      emailBody = emailBody.replace(Constants.APPT_EMAIL_DESCRIPTION, "No additional information.");
    }

    if (data.get("apptDate") != null) {
      emailBody = emailBody.replace(Constants.APPT_EMAIL_DATE, data.get("apptDate"));
    }
    if (data.get("emailSubject") != null) {
      emailBody = emailBody.replace(Constants.APPT_EMAIL_SUBJECT, data.get("emailSubject"));
    }
    if (data.get("source") != null) {
      emailBody = emailBody.replace(Constants.APPT_EMAIL_SOURCE, data.get("source"));
    }
   
    return emailBody;
  }

  /**
   * Get the appointment transition value, given the old and new state.
   * @param oldState Old appointment state.
   * @param newState New appointment state.
   * @return The appointment transition value, representing the transition that's taking place.
   */
  private String getAppointmentTransition(String oldState, String newState) {
    if (oldState.equalsIgnoreCase(AppointmentManager.APPT_AVAILABLE) &&
        newState.equalsIgnoreCase(AppointmentManager.APPT_RESERVED)) {
      return AppointmentManager.APPT_RESERVED;
    } else if ((oldState.equalsIgnoreCase(AppointmentManager.APPT_AVAILABLE) ||
                oldState.equalsIgnoreCase(AppointmentManager.APPT_RESERVED)) &&
                newState.equalsIgnoreCase(AppointmentManager.APPT_SCHEDULED)) {
      return AppointmentManager.APPT_SCHEDULED;
    } else if (oldState.equalsIgnoreCase(AppointmentManager.APPT_RESERVED) ||
               oldState.equalsIgnoreCase(AppointmentManager.APPT_SCHEDULED) &&
               newState.equalsIgnoreCase(AppointmentManager.APPT_CANCELLED)) {
      return AppointmentManager.APPT_CANCELLED;
    } else if (oldState.equalsIgnoreCase("NONE") &&
               newState.equalsIgnoreCase(AppointmentManager.APPT_SCHEDULED)) {
      return AppointmentManager.APPT_SCHEDULED;
    } else {
      return "UNKNOWN";
    }
  }
  
  public ReturnMessage deleteAppointment(String apptKey) {
    return this.doDelete(apptKey, null);
  }
  
  /**
   * Gets the information for a single appointment.
   * @param apptKey The appointment GAE key.
   * @return The status, message, and appointment data.
   */
  public ReturnMessage getAppointment(String apptKey) {
    return this.doGet(apptKey);
  }

  /**
   * Wraps getAppointments with a simple call without office key.  Gets all appointments for all
   * offices by default.
   * 
   * @param startDate The start date to search, in the format mm/dd/yyyy.
   * @param endDate The end date to search, in the format mm/dd/yyyy.
   * @param apptDoctor The doctor's name to search.
   * @param direction The direction of the results (ASC/DESC).
   * @param maxResults The maximum number of results to return.
   * @param showPatients Whether to show patient information in the results returned.
   * @param futureOnly Whether to only show future appointments.
   * @return
   */
  public ReturnMessage getAppointments(
      String startDate,
      String endDate,
      String apptDoctor,
      SortDirection direction,
      int maxResults,
      boolean showPatients,
      boolean futureOnly) {
    return getAppointments(
        startDate, endDate, apptDoctor, direction, maxResults, null, showPatients, futureOnly);
  }

  /**
   * Get a list of appointments based on filter parameters.
   * 
   * @param startDate The start date to search, in the format mm/dd/yyyy.
   * @param endDate The end date to search, in the format mm/dd/yyyy.
   * @param apptDoctor The doctor's name to search.
   * @param direction The direction of the results (ASC/DESC).
   * @param maxResults The maximum number of results to return.
   * @param officeKey The office key to filter by.
   * @param showPatients Whether to show patient information on the returned appointments.
   * @param futureOnly Whether to only show future appointments.
   * @return 
   */
  public ReturnMessage getAppointments(
      String startDate,
      String endDate,
      String apptDoctor,
      SortDirection direction,
      int maxResults,
      String officeKey,
      boolean showPatients,
      boolean futureOnly) {
    ConfigManager cm = new ConfigManager();
    String status = "SUCCESS";
    String message = "";
    JSONObject returnObj = null;
    Filter apptDoctorFilter = null;
 
    if (endDate == null) {
      status = "FAILURE";
      message = "The parameter for end date is required to search for appointments.";
    }
 
    if (startDate == null) {
      Calendar today = Calendar.getInstance();
      startDate = DateUtils.makeDateStringFromDate(today);
      Calendar testEndDate = DateUtils.getDateFromDateString(endDate);
      // *** If the end date is in the past, then a start date must be provided.
      if (testEndDate.before(today)) {
        message = "Both a start date and end date must be provided if the query period is in " +
                  "the past.";
        status = "FAILURE";
        ReturnMessage.Builder builder = new ReturnMessage.Builder();
        ReturnMessage result = builder.status(status).message(message).build();
        return result;
      }
    }
    
    //*** Only execute the search if required parameters were provided.
    if (status.equals("SUCCESS")) {
      Query q = new Query("Appointment")
          .addSort("apptStartLong", SortDirection.ASCENDING);
      //***If a doctor was specified, get the list of appointments under the selected doctor.
      if (apptDoctor != null) {
        apptDoctorFilter = new FilterPredicate(
            "apptDoctorKey",
            FilterOperator.EQUAL,
            apptDoctor);
        q.setFilter(apptDoctorFilter);
      }
      
      //*** If only future appointments were selected, filter out past appointments.
      if (futureOnly) {
        Filter apptPastFilter = new FilterPredicate(
            "isPast", FilterOperator.NOT_EQUAL, Constants.APPT_PAST);
        q.setFilter(apptPastFilter);
      }

      //*** If an office was specified, get the list of appointments for the office.
      Key office = null;
      if (officeKey != null) {
        office = KeyFactory.stringToKey(officeKey);
        q.setAncestor(office);
        //*** Get the office time zone offset.
        SimpleBillingOffice officeData = officeManager.getSimpleBillingOffice(officeKey);
      }
      
      //*** If the showPatients flag is true, audit this view.
      try {
        if (showPatients) {
          UserService userService = UserServiceFactory.getUserService();
          String userEmail = userService.getCurrentUser().getEmail();
          String auditMessage;
          if (office != null) {
            Entity officeVal = ds.get(office);
            auditMessage = "User viewing appointments for office " +
                           officeVal.getProperty("officeName");
            logAppointmentView(auditMessage, userEmail);
          } else {
            auditMessage = "User viewing appointments, no office.";
            logAppointmentView(auditMessage, userEmail);
          }
        }
      } catch (Exception ex) {
        LOGGER.severe("ERROR trying to log appointment view: " + ex.toString());
      }

      //*** Check config value to turn on native date filtering.
      boolean doDatesEqual = startDate.equals(endDate); 
      PreparedQuery pq;
      SimpleConfigValue dateFilterOn =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.APPT_NATIVE_DATE_FILTER);
      if (dateFilterOn != null && dateFilterOn.getConfigValue().equalsIgnoreCase("TRUE")) {
        //*** Most of the appointment searches will have the same start/end date, so this
        //*** will cut down on datastore reads.
        if (doDatesEqual) {
          Filter apptDateFilter = new FilterPredicate(
              "apptDate",
              FilterOperator.EQUAL,
              startDate);
          //*** Setup a composite filter if the date filter is added.
          if (apptDoctorFilter != null) {
            Filter doctorAndDateFilter =
                CompositeFilterOperator.and(apptDoctorFilter, apptDateFilter);
            q.setFilter(doctorAndDateFilter);
          } else {
            q.setFilter(apptDateFilter);
          }
        }
      }
      pq = ds.prepare(q);

      //*** Setup the item value map.
      ImmutableList.Builder matchingApptBuilder = new ImmutableList.Builder<Entity>();

      //*** If the dates are equal, include all appointments, otherwise, filter them specifically
      //*** by the date (should not be used for now).
      if (doDatesEqual) {
        for (Entity result : pq.asIterable()) {
          matchingApptBuilder.add(result);
        }
      } else {
        for (Entity result : pq.asIterable()) {
          //*** Check to see if the result falls within the start/end dates.
          //*** If the start date can't be converted to a long, omit this result and log an
          //*** error.
          try {
            long apptStartLong = (Long)result.getProperty("apptStartLong");
            Date apptStart = new Date(apptStartLong);
            Calendar apptStartCal = Calendar.getInstance();
            apptStartCal.setTime(apptStart);

            Calendar startDateObj = DateUtils.getDateFromDateString(startDate);
            Calendar endDateObj = DateUtils.getDateFromDateString(endDate);
            endDateObj.set(Calendar.HOUR_OF_DAY, 23);
            endDateObj.set(Calendar.MINUTE, 59);
            endDateObj.set(Calendar.SECOND, 59);
            if (DateUtils.doDateBlocksOverlap(
                startDateObj.getTimeInMillis(),
                endDateObj.getTimeInMillis(),
                apptStart.getTime(),
                apptStart.getTime())) {
              matchingApptBuilder.add(result);
            }
          } catch(NumberFormatException ex) {
            LOGGER.severe("Error converting start date of appointment with key " + result.getKey().toString());
          } catch(ClassCastException ex) {
            LOGGER.warning("Error converting start date of appointment, stored as String, key: " + result.getKey().toString());
          }
        }
      }

      List<Entity> matchingAppts = matchingApptBuilder.build();
      message = "Returned " + matchingAppts.size() + " appointments.";

      JSONArray apptReturnArray = new JSONArray();
      for (Entity appt : matchingAppts) {
        appt.setProperty("key", KeyFactory.keyToString(appt.getKey()));
        JSONObject apptObj;
        //*** If the show patient flag is false, remove patient data.
        if (!showPatients) {
          Map<String, Object> apptMap = appt.getProperties();
          Map mutableMap = new HashMap();
          mutableMap.putAll(apptMap);
          mutableMap.remove("patientFName");
          mutableMap.remove("patientLName");
          mutableMap.remove("patientUser");
          mutableMap.remove("createdBy");
          mutableMap.remove("modifiedBy");
          apptObj = new JSONObject(mutableMap);
        } else {
          apptObj = new JSONObject(appt.getProperties());
        }
        apptReturnArray.put(apptObj);
      }

      returnObj = new JSONObject();

      try {
        returnObj.put("values", apptReturnArray);
      } catch (JSONException ex) {
        message = "Error converting appointment list to JSON: " + ex.toString();
      }
    }

    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage result = builder.status(status).message(message).value(returnObj).build();
    return result;
  }

  public ReturnMessage getTimeBoxedAppointments(
      String officeKey,
      String doctorKey,
      String startDate,
      String endDate,
      String startTime,
      String endTime) {
    JSONObject returnObj = new JSONObject();
    List<Filter> filters = new ArrayList<>();
    String message = "";
    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    if (officeKey.equals("") ||
        startTime.equals("") ||
        endTime.equals("") ||
        startDate.equals("") ||
        endDate.equals("")) {
      return builder.status("FAILED")
          .message("Parameters for officeKey, apptStartDate, apptEndDate, startTime, and endTime are required!")
          .build();
    }
    //*** Get the start/end hour and minute.
    String[] startTimeArr = startTime.split(":");
    String[] endTimeArr = endTime.split(":");
    if (startTimeArr.length != 2 || endTimeArr.length != 2) {
      return builder.status("FAILED")
          .message("Start/End time was provided in the wrong format!")
          .build();
    }
    int startHr = Integer.parseInt(startTimeArr[0]);
    int startMin = Integer.parseInt(startTimeArr[1]);
    int endHr = Integer.parseInt(endTimeArr[0]);
    int endMin = Integer.parseInt(startTimeArr[1]);
    //*** Get the office data to get the time zone offset for the office.
    int officeOffset = 0;
    try {
      SimpleBillingOffice officeData = officeManager.getSimpleBillingOffice(officeKey);
      officeOffset = officeData.getOfficeTimeZoneOffset();
      officeOffset = DateUtils.convertOffsetForDst(-officeOffset, cm, officeData);
    } catch (Exception ex) {
      return builder.status("FAILED")
          .message("Office not found!")
          .build();
    }
    
    //*** Create the query.
    Query q = new Query("Appointment")
        .addSort("apptStartLong", SortDirection.ASCENDING);
    //*** Attach the query to the office.
    Key officeKeyVal = KeyFactory.stringToKey(officeKey);
    q.setAncestor(officeKeyVal);
    if (doctorKey != null && !doctorKey.equals("")) {
      //*** Set the doctor filter.
      Filter doctorFilter = new FilterPredicate(
          "apptDoctorKey",
          FilterOperator.EQUAL,
          doctorKey);
      filters.add(doctorFilter);
    }
    //*** Set the date filters.
    Calendar startDateCal = DateUtils.getDateFromDateString(startDate);
    startDateCal.set(Calendar.HOUR_OF_DAY, startHr + officeOffset);
    startDateCal.set(Calendar.MINUTE, startMin);
    Calendar endDateCal = DateUtils.getDateFromDateString(endDate);
    endDateCal.set(Calendar.HOUR_OF_DAY, endHr + officeOffset);
    endDateCal.set(Calendar.MINUTE, endMin);
    Filter startDateFilter = new FilterPredicate(
        "apptStartLong",
        FilterOperator.GREATER_THAN_OR_EQUAL,
        startDateCal.getTimeInMillis());

    Filter endDateFilter = new FilterPredicate(
        "apptStartLong",
        FilterOperator.LESS_THAN_OR_EQUAL,
        endDateCal.getTimeInMillis());
    filters.add(startDateFilter);
    filters.add(endDateFilter);
    
    //*** Create the filter and execute the query.
    Filter doctorAndDateFilter =
        CompositeFilterOperator.and(filters);
    q.setFilter(doctorAndDateFilter);
    PreparedQuery pq = ds.prepare(q);
    JSONArray returnList = new JSONArray();
    for (Entity result : pq.asIterable()) {
      returnList.put(result.getProperties());
    }
    try {
      returnObj.put("values", returnList);
    } catch (JSONException ex) {
      message = "Error converting appointment list to JSON: " + ex.toString();
      return builder.status("FAILED")
          .message(message)
          .build();
    }
    message = "Returned " + returnList.length() + " appointments.";
    return builder.status("SUCCESS").message(message).value(returnObj).build();
  }
  
  /**
   * Shortcut method optimized for speed.  Uses the is_future setting to get appointments only
   * in the future for a particular office, sorted DESC by date/time.
   * 
   * @param officeKey The key of the office for which to return appointments.
   * @return 
   */
  public ReturnMessage getFutureAppointmentsForOffice(String officeKey, String status) {
    String message = "";
    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    Query q = new Query("Appointment");
    
    //*** Make sure the office key is included.
    if (officeKey != null) {
        Key office = KeyFactory.stringToKey(officeKey);
        q.setAncestor(office);
    } else {
      message = "Office Key is Required.";
      return builder.status("FAILED")
          .message(message)
          .build();
    }
    
    //*** If no status was provided, set the status to filter for RESERVED.
    if (status == null || status.equals("")) {
      status = APPT_RESERVED;
    }
    
    //*** Setup the past filter.
    Filter apptPastFilter = new FilterPredicate(
            "isPast", FilterOperator.EQUAL, Constants.APPT_FUTURE);
    q.setFilter(apptPastFilter);
    
    //*** Setup the status filter, if status was provided.
    if (status != null && !status.equals("")) {
      Filter apptStatusFilter = new FilterPredicate("status", FilterOperator.EQUAL, status);
      Filter compositeFilter = CompositeFilterOperator.and(apptPastFilter, apptStatusFilter);
      q.setFilter(compositeFilter);
    }
    
    //*** Create the sorted list of appointments.
    PriorityQueue<Entity> sortedAppointmentQueue = new PriorityQueue<>(500, new AppointmentTimeComparator());
 
    //*** Execute the query.
    PreparedQuery pq = ds.prepare(q);
    for (Entity result : pq.asIterable()) {
      sortedAppointmentQueue.add(result);
    }
 
    LOGGER.info("Future appointment query, returned "
        + sortedAppointmentQueue.size() + " appointments.");
    
    //*** Convert the sorted appointment list to JSON.
    JSONObject returnObj = new JSONObject();
    JSONArray returnList = new JSONArray();
    for (Entity appt : sortedAppointmentQueue) {
      returnList.put(appt.getProperties());
    }
    try {
      returnObj.put("values", returnList);
    } catch (JSONException ex) {
      message = "Error converting appointment list to JSON: " + ex.toString();
      return builder.status("FAILED")
          .message(message)
          .build();
    }
    message = "Returned " + returnList.length() + " appointments.";
    return builder.status("SUCCESS").message(message).value(returnObj).build();
  }

  /**
   * Gets all appointments, used for JSON export.
   * @param apptStatus The status to filter by.
   * @return The status, message, and appointment values.
   */
  public ReturnMessage getAllAppointments(String apptStatus) {
    Filter apptStatusFilter;
    
    Query q = new Query("Appointment")
          .addSort("apptStartLong", SortDirection.DESCENDING);
    if (apptStatus != null) {
      apptStatusFilter = new FilterPredicate(
              "status",
              FilterOperator.EQUAL,
              apptStatus);
      q.setFilter(apptStatusFilter);
    }
    pq = ds.prepare(q);
    JSONArray apptReturnArray = new JSONArray();
    int apptListSize = 0;
    List <Entity> apptList = new ArrayList<Entity>();
    for (Entity appt : pq.asIterable(FetchOptions.Builder.withLimit(1000))) {
      JSONObject apptObj = new JSONObject(appt.getProperties());
      try {
        appt.setProperty("apptOffice", appt.getParent().getName());
        apptList.add(appt);
      } catch (Exception ex) {
        //*** Do nothing just don't include office.
      }
      apptReturnArray.put(apptObj);
      apptListSize++;
    }
    
    JSONObject returnObj = new JSONObject();

    try {
      returnObj.put("values", apptReturnArray);
    } catch (JSONException ex) {
      String logMessage = "Error converting appointment list to JSON: " + ex.toString();
      LOGGER.severe(logMessage);
    }

    String message = "Returned " + Integer.toString(apptListSize) + " appointments.";
    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    return builder.status("SUCCESS").message(message).value(returnObj).entities(apptList).build();
  }
  
  /**
   * Resets all appointments from RESERVED status to AVAILABLE.
   */
  public void resetReservedAppointments() {
    //*** Select all appointments with reserved status.
    q = new Query("Appointment");
    Filter apptReservedFilter =
            new FilterPredicate("status",
                                FilterOperator.EQUAL,
                                APPT_RESERVED);
    q.setFilter(apptReservedFilter);
    pq = ds.prepare(q);
    int changeCount = 0;
    long resetTime = new Date().getTime() - Constants.APPT_RESET_REACHBACK;
    for (Entity result : pq.asIterable()) {
      Date lastModify = (Date)result.getProperty("modifyDate");
      long lastModifyLong = lastModify.getTime();
      if (lastModifyLong < resetTime) {
        //*** Set them to active.
        result.setProperty("status", APPT_AVAILABLE);
        //*** Save them.
        ds.put(result);
        changeCount++;
      }
    }
    LOGGER.info("Reset " + Integer.toString(changeCount) + " reserved appointments.");
  }

  
  /**
   * Marks all appointments as current. Used to fill in the isPast value, but shouldn't be used
   * more than once.
   * @return The number of affected appointments.
   */
  public int markAllAsCurrent() {
    int attemptCount = 0;
    
    Calendar now = Calendar.getInstance();
    long timeToTest = now.getTimeInMillis() - APPT_CHANGE_REACHBACK;
    
    
    //*** Query to get all appointment keys for appointments that start recently.
    Query q = new Query("Appointment");
    Filter apptCurrentFilter =
            new FilterPredicate("apptStartLong",
                                FilterOperator.GREATER_THAN,
                                timeToTest);
    q.setFilter(apptCurrentFilter);
    PreparedQuery allPQ = ds.prepare(q);
    for (Entity appointment : allPQ.asIterable()) {
      Queue markAllCurrentQueue = QueueFactory.getQueue(Constants.MARK_APPTS_AS_CURRENT_QUEUE);
      markAllCurrentQueue.add(
        withUrl("/service/appointmentcleanup")
        .method(TaskOptions.Method.GET)
        .param("op", "markOneCurrent").param("key", KeyFactory.keyToString(appointment.getKey())));
      attemptCount++;
    }
    return attemptCount;
  }
  
  /**
   * Mark a single appointment as current.
   * @param keyValue The string representing the key.
   * @return 1 if the operation was successful, 0 if it wasn't.
   */
  public int markOneAsCurrent(String keyValue) {
    try {
      Entity appointment = ds.get(KeyFactory.createKey("Appointment", keyValue));
      appointment.setProperty("isPast", Constants.APPT_FUTURE);
      ds.put(appointment);
      LOGGER.info("Marked appointment as current: " + appointment.getProperty("apptStartLong"));
      return 1;
    } catch (EntityNotFoundException ex) {
      return 0;
    }
  }
  
  /**
   * Marks appointments that are more than 48 hours old from the current date/time as past.
   * 
   * @return The number of appointments changed.
   */
  public int markPastAppointments() {
     //*** Select all appointments that aren't marked as past.
    q = new Query("Appointment");
    Filter apptPastFilter =
            new FilterPredicate("isPast",
                                FilterOperator.NOT_EQUAL,
                                Constants.APPT_PAST);
    q.setFilter(apptPastFilter);
    pq = ds.prepare(q);
    int changeCount = 0;
    long markAsPastTime = new Date().getTime() - Constants.APPT_PAST_REACHBACK;
    for (Entity result : pq.asIterable()) {
      long apptStart = Calendar.getInstance().getTimeInMillis();
      try {
        apptStart = (Long)result.getProperty("apptStartLong");
      } catch(ClassCastException ex) {
        //*** Do nothing.
      }

      if (apptStart < markAsPastTime) {
        //*** Set them to active.
        result.setProperty("isPast", Constants.APPT_PAST);
        //*** Save them.
        ds.put(result);
        changeCount++;
      }
    }
    LOGGER.info("Set " + Integer.toString(changeCount) + " appointments to past.");
    return changeCount;
  }
  
  
  /**
   * Resets all appointments from RESERVED status to AVAILABLE.
   * 
   * @param officeKey The office key to shift appointments.
   * @param shiftHours Number of hours to shift appointments.
   */
  public void shiftOfficeAppointmentTimes(String officeKey, int shiftHours) {
    Key officeKeyVal = KeyFactory.stringToKey(officeKey);
    //*** Get the office time zone offset.
    SimpleBillingOffice office = officeManager.getSimpleBillingOffice(officeKey);
    int tzOffset = office.getOfficeTimeZoneOffset();
    //*** Select all appointments with reserved status.
    q = new Query("Appointment");
    q.setAncestor(officeKeyVal);
    pq = ds.prepare(q);
    int changeCount = 0;
    for (Entity result : pq.asIterable()) {
      String apptDate = (String)result.getProperty("apptDate");
      int startHr = (Integer)result.getProperty("apptStartHr");
      int startMin = (Integer)result.getProperty("apptStartMin");
      int endHr = (Integer)result.getProperty("apptEndHr");
      startHr = startHr + shiftHours;
      endHr = endHr + shiftHours;

      Date longShiftDate = DateUtils.getDateFromValues(apptDate, startHr, startMin, tzOffset);
      long shiftedLong = longShiftDate.getTime();
      result.setProperty("apptStartHr", startHr);
      result.setProperty("apptEndHr", endHr);
      result.setProperty("apptStartLong", shiftedLong);
      ds.put(result);
      changeCount++;
    }
    LOGGER.info("Changed " + Integer.toString(changeCount) +
                " appointments, shifted " + shiftHours + "hours.");
  }
  
  /**
   * Changes all appointments for the selected office to the updated format, with hours and
   * minutes instead of Java dates for the appointment times.
   * 
   * @param officeKey The office key to shift appointments.
   */
  public void changeApptStructureForTime(String officeKey) {
    Key officeKeyVal = KeyFactory.stringToKey(officeKey);
    //*** Get the office time zone offset.
    SimpleBillingOffice office = officeManager.getSimpleBillingOffice(officeKey);
    int tzOffset = office.getOfficeTimeZoneOffset();
    //*** Select all appointments with reserved status.
    q = new Query("Appointment");
    q.setAncestor(officeKeyVal);
    pq = ds.prepare(q);
    int changeCount = 0;
    int invalidCount = 0;
    for (Entity result : pq.asIterable(FetchOptions.Builder.withChunkSize(250))) {
      Date oldApptStart = (Date)result.getProperty("apptStart");
      Date oldApptEnd = (Date)result.getProperty("apptEnd");
      if (oldApptStart != null && oldApptEnd != null) {
        Calendar oldApptStartCal = Calendar.getInstance();
        oldApptStartCal.setTime(oldApptStart);
        Calendar oldApptEndCal = Calendar.getInstance();
        oldApptEndCal.setTime(oldApptEnd);
        int startHr = oldApptStartCal.get(Calendar.HOUR_OF_DAY) + tzOffset;
        int startMin = oldApptStartCal.get(Calendar.MINUTE);
        int endHr = oldApptEndCal.get(Calendar.HOUR_OF_DAY) + tzOffset;
        int endMin = oldApptEndCal.get(Calendar.MINUTE);
        result.setUnindexedProperty("apptStartHr", startHr);
        result.setUnindexedProperty("apptStartMin", startMin);
        result.setUnindexedProperty("apptEndHr", endHr);
        result.setUnindexedProperty("apptEndMin", endMin);
        //*** Convert the date to strip leading/trailing zeros.
        String apptDate = (String)result.getProperty("apptDate");
        Calendar dateToConvert = DateUtils.getDateFromDateString(apptDate);
        String newApptDate = DateUtils.makeDateStringFromDate(dateToConvert);
        result.setProperty("apptDate", newApptDate);

        ds.put(result);
        changeCount++;
      } else {
        //*** Increment the invalid appointment count.
        invalidCount++;
      }
    }
    LOGGER.info("Changed " + Integer.toString(changeCount) +
                " appointments into the new format. " + Integer.toString(invalidCount) +
                " are invalid (no start/end timestamp).");
  }
}
