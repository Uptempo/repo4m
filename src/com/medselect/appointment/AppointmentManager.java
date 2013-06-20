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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
          .build();

  private static final String APPT_AVAILABLE = "AVAILABLE";
  private static final String APPT_RESERVED = "HELD";
  private static final String APPT_CANCELLED = "CANCELLED";
  private static final String APPT_SCHEDULED = "RESERVED";
  private static final String APPT_UPDATE = "UPDATE";
  private static final String APPT_NEW = "NEW";
  private BillingOfficeManager officeManager;
  private AuditLogManager am = new AuditLogManager();
  private ConfigManager cm = new ConfigManager();

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
    if (!this.validateAppointmentInputs(data, "NEW", null)) {
      return new ReturnMessage.Builder()
          .message("Required fields missing from the appointment data!")
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
            officeData.getOfficeEmail(),
            officeData.getOfficeTimeZoneOffset(),
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
    String doctorName =
        doctor.getTitles().get(0) + " " + doctor.getFirstName() + " " + doctor.getLastName();
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
    if (!this.validateAppointmentInputs(dataCopy, transitionOperation, apptEntity)) {
      return new ReturnMessage.Builder()
          .message("Required fields missing from the appointment data!")
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
          officeData.getOfficeEmail(),
          officeData.getOfficeTimeZoneOffset(),
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
  private boolean validateAppointmentInputs(Map<String, String> data, String transition, Entity appointment) {
    //*** If the status transition is scheduled, the patient first name, last name, and e-mail must
    //*** be included.
    if (transition.equals(AppointmentManager.APPT_SCHEDULED)) {
      if (data.get("patientUser") == null ||
          data.get("patientFName") == null ||
          data.get("patientLName") == null) {
        return false;
      }
      if (data.get("patientUser").isEmpty() ||
          data.get("patientFName").isEmpty() ||
          data.get("patientLName").isEmpty()) {
        return false;
      }
    }
    if (appointment == null) {
      if (data.get("apptDoctorKey") == null ||
          data.get("status") == null ||
          data.get("apptStartHr") == null ||
          data.get("apptEndHr") == null ||
          data.get("apptStartMin") == null ||
          data.get("apptEndMin") == null ||    
          data.get("apptDate") == null) {
        return false;
      }
      if (data.get("apptDoctorKey").isEmpty() ||
          data.get("status").isEmpty() ||
          data.get("apptStartHr").isEmpty() ||
          data.get("apptEndHr").isEmpty() ||
          data.get("apptStartMin") == null ||
          data.get("apptEndMin") == null ||
          data.get("apptDate").isEmpty()) {
        return false;
      }
      //*** Make sure an attached office key is provided on a new appointment.
      if (data.get("apptOffice") == null || data.get("apptOffice").isEmpty()) {
        return false;
      }
    }
    return true;
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
      String officeEmail, 
      int timeZoneOffset,
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
      SimpleConfigValue userEmailMessage =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, userMessageCode);
      SimpleConfigValue officeEmailMessage =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, officeMessageCode);
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
        String emailBody = assembleApptEmailBody(
            userEmailMessage.getConfigText(), data, timeZoneOffset, operation);
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
            officeEmailMessage.getConfigText(), data, timeZoneOffset, operation);
        MailUtils mailSender = new MailUtils();
        mailSender.sendMail(
            sendEmailFrom.getConfigValue(),
            sendEmailFromDisplay.getConfigValue(),
            officeEmail,
            officeEmail,
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
      int timeZoneOffset,
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
        DateUtils.getReadableTime(apptDate, apptStartHr, apptStartMin, timeZoneOffset);
    String endDateStr =
        DateUtils.getReadableTime(apptDate, apptEndHr, apptEndMin, timeZoneOffset);
    emailBody = emailBody.replace(Constants.APPT_START_TIME, startDateStr);
    emailBody = emailBody.replace(Constants.APPT_END_TIME, endDateStr);
    emailBody = emailBody.replace(Constants.APPT_EMAIL_STATUS, status);

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
   * @return
   */
  public ReturnMessage getAppointments(
      String startDate,
      String endDate,
      String apptDoctor,
      SortDirection direction,
      int maxResults,
      boolean showPatients) {
    return getAppointments(startDate, endDate, apptDoctor, direction, maxResults, null, showPatients);
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
   * @return 
   */
  public ReturnMessage getAppointments(
      String startDate,
      String endDate,
      String apptDoctor,
      SortDirection direction,
      int maxResults,
      String officeKey,
      boolean showPatients) {
    ConfigManager cm = new ConfigManager();
    String status = "SUCCESS";
    String message = "";
    JSONObject returnObj = null;
 
    if (startDate == null) {
      Calendar today = Calendar.getInstance();
      startDate = DateUtils.makeDateStringFromDate(today);
    }
 
    if (endDate == null) {
      status = "FAILURE";
      message = "The parameter for end date is required to search for appointments.";
    }
 
    //*** Only execute the search if required parameters were provided.
    if (status.equals("SUCCESS")) {
      Query q = new Query("Appointment")
          .addSort("apptStartLong", SortDirection.ASCENDING);
      //***If a doctor was specified, get the list of appointments under the selected doctor.
      if (apptDoctor != null) {
        Filter apptDoctorFilter =
            new FilterPredicate("apptDoctor",
                                FilterOperator.EQUAL,
                                apptDoctor);
        q.setFilter(apptDoctorFilter);
      }

      //*** If an office was specified, get the list of appointments for the office.
      int officeTimeZoneOffset = -7;
      Key office = null;
      if (officeKey != null) {
        office = KeyFactory.stringToKey(officeKey);
        q.setAncestor(office);
        //*** Get the office time zone offset.
        SimpleBillingOffice officeData = officeManager.getSimpleBillingOffice(officeKey);
        officeTimeZoneOffset = officeData.getOfficeTimeZoneOffset();
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
          pq = ds.prepare(q.setFilter(apptDateFilter));
        } else {
          pq = ds.prepare(q);
        }
      } else {
        pq = ds.prepare(q);
      }

      //*** Setup the item value map.
      ImmutableList.Builder matchingApptBuilder =
          new ImmutableList.Builder<Entity>();
      //*** If the dates are equal, include all appointments, otherwise, filter them specifically
      //*** by the date (should not be used for now).
      if (doDatesEqual) {
        for (Entity result : pq.asIterable()) {
          matchingApptBuilder.add(result);
        }
      } else {
        for (Entity result : pq.asIterable()) {
          //*** Check to see if the result falls within the start/end dates.
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

  /**
   * Gets all appointments, used for JSON export.
   * @return The status, message, and appointment values.
   */
  public ReturnMessage getAllAppointments() {
    Query q = new Query("Appointment")
          .addSort("apptStartLong", SortDirection.DESCENDING);
    pq = ds.prepare(q);
    JSONArray apptReturnArray = new JSONArray();
    int apptListSize = 0;
    for (Entity appt : pq.asIterable()) {
      JSONObject apptObj = new JSONObject(appt.getProperties());
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
    return builder.status("SUCCESS").message(message).value(returnObj).build();
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
