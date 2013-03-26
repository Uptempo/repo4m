/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.appointment;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
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
          .put("apptStart", BaseManager.FieldType.DATE)
          .put("apptEnd", BaseManager.FieldType.DATE)
          .put("apptDate", BaseManager.FieldType.STRING)
          .put("status", BaseManager.FieldType.STRING)
          .build();

  private static final String APPT_AVAILABLE = "AVAILABLE";
  private static final String APPT_RESERVED = "RESERVED";
  private static final String APPT_CANCELLED = "CANCELLED";
  private static final String APPT_SCHEDULED = "SCHEDULED";
  private BillingOfficeManager officeManager;

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
    String officeKeyVal = null;
    ConfigManager cm = new ConfigManager();
    String message = "";
    String googleApptId = null;
    Map <String, String> dataCopy = new HashMap();
    
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
    
    //*** Fill in the doctor name from the key, for quick display.
    DoctorManager dManager = new DoctorManager();
    SimpleDoctorValue doctor = dManager.getSimpleDoctorValues(data.get("apptDoctorKey"));
    String doctorName =
        doctor.getTitles().get(0) + " " + doctor.getFirstName() + " " + doctor.getLastName();
    data.put("apptDoctor", doctorName);
    dataCopy.putAll(data);
    
    //*** Check if this is a new user.  If so, create the user too.
    String newUserKey = this.checkAndCreateUser(data);
    String userEmail = data.get("patientUser");
    
    //*** Check if the user was included in this update.  A user can be included and already exist,
    //*** so the checkAndCreateUser result is not a reliable return value.
    boolean userExists = false;
    if (userEmail != null && !userEmail.isEmpty()) {
      userExists = true;
    }

    //*** Create the Google Calendar ID entry.  Ignore the error, but report it as a log error.
    try {
      googleApptId = this.createUpdateGcalEntry(data, null);
      message += "Google calendar event created with ID: " + googleApptId + "<br />";
    } catch (Exception ex) {
      LOGGER.severe("Google calendar update failed for new appointment");
    }

    //*** Check if the status is now scheduled and whether the patient user exists.
    if (apptStatus.toUpperCase().equals(AppointmentManager.APPT_SCHEDULED) && !userExists) {
      message += "Patient e-mail required to schedule appointment.<br />";
      result = new ReturnMessage.Builder()
          .message(message)
          .status("FAILED")
          .value(null)
          .build();
    } else {
      String user = data.get("user");
      //*** Assemble date values for filter.
      data.put("apptStartLong", data.get("apptStart"));
      data.put("apptEndLong", data.get("apptEnd"));
      data.put(Constants.APPT_GOOGLE_KEY_FIELD, googleApptId);
      //*** Assemble the office ancestor key.
      officeKeyVal = data.get("apptOffice");
      Key officeKey = KeyFactory.stringToKey(officeKeyVal);
      data.remove("apptOffice");
      result = this.doCreate(data, false, officeKey);
      //*** Log appointment creation
      if(result.getStatus().equals("SUCCESS")) {
        message += result.getMessage() + "<br />";
        if (userEmail == null) {
          userEmail = "UNKNOWN";
        }
        message = "New Appointment created.";
        logAppointmentEvent("NEW", message, user);
      }
    }
    
    //*** E-mail the user if the appointment creation was successful and there is a patient set.
    if (result.getStatus().equals("SUCCESS") && userExists) {
      SimpleConfigValue sendEmailFlag =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.SEND_USER_EMAIL);
      if (sendEmailFlag != null && sendEmailFlag.getConfigValue().toLowerCase().equals("true")) {
        SimpleBillingOffice officeData = officeManager.getSimpleBillingOffice(officeKeyVal);
        sendAppointmentEmail(
            dataCopy,
            userEmail,
            officeData.getOfficeEmail(),
            officeData.getOfficeTimeZoneOffset(),
            "NEW");
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

  public ReturnMessage updateAppointment(Map<String, String> data, String apptKey) {
    ReturnMessage result;
    String message = "";
    Map <String, String> dataCopy = new HashMap();
    data.put("key", apptKey);
    //*** Get the appointment attributes for later use.
    String apptStatus = data.get("status");
    String userEmail = data.get("patientUser");
    
    boolean userExists = false;
    if (userEmail != null && !userEmail.isEmpty()) {
      userExists = true;
    }
    
    //*** Fill in the doctor name from the key, for quick display, if it's included.
    if (data.get("appDoctorKey") != null) {
      DoctorManager dManager = new DoctorManager();
      SimpleDoctorValue doctor = dManager.getSimpleDoctorValues(data.get("apptDoctorKey"));
      String doctorName =
          doctor.getTitles().get(0) + " " + doctor.getFirstName() + " " + doctor.getLastName();
      data.put("apptDoctor", doctorName);
    }

    //*** Get this appointment's information to detect what type of transition this is.
    Key dsKey = KeyFactory.stringToKey(apptKey);
    Entity apptEntity = null;
    String transitionOperation = "UPDATED";
    try {
      apptEntity = ds.get(dsKey);
      //*** Make a copy of the data, including all of the existing data.
      dataCopy = getUpdateMapEntity(apptEntity, data);
      //*** Set the dates as string values to make them uniform, if start/end were changed.
      if (data.containsKey("apptStart")) {
        dataCopy.put("apptStart", data.get("apptStart"));
        //*** Make a date string.
        Date startDate = new Date(Long.parseLong(data.get("apptStart")));
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        dataCopy.put("apptDate", DateUtils.makeDateStringFromDate(c));
      }
      if (data.containsKey("apptEnd")) {
        dataCopy.put("apptEnd", data.get("apptEnd"));
      }
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
 
    //*** Update the Google Calendar ID entry.  Ignore the error, but report it as a log error.
    try {
      String calendarId = this.createUpdateGcalEntry(data, apptEntity);
      message += "Google calendar event updated with ID: " + calendarId + "<br />";
    } catch (Exception ex) {
      LOGGER.severe("Google calendar update failed for appointment (key): " + apptKey);
    }

    //*** Check if this is a new user.  If so, create the user too.
    String newUserKey = this.checkAndCreateUser(data);
    
    //*** Check if the user was included in this update.  A user can be included and already exist,
    //*** so the checkAndCreateUser result is not a reliable return value.
    if (apptStatus.toUpperCase().equals(AppointmentManager.APPT_SCHEDULED) && !userExists) {
      result = new ReturnMessage.Builder()
          .message("Patient e-mail required to schedule appointment.")
          .status("FAILED")
          .value(null)
          .build();
    } else {
      //*** If the appointment start or end changed, also update the stored long values.
      if (data.containsKey("apptStart")) {
        data.put("apptStartLong", data.get("apptStart"));
      }
      if (data.containsKey("apptEnd")) {
        data.put("apptEndLong", data.get("apptEnd"));
      }
      result = this.doUpdate(data);
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
          transitionOperation);
    }

    return result;
  }

  /**
   * Helper function to log an appointment event.
   * @param transition The appointment transition.
   * @param message The appointment message.
   * @param userEmail The user e-mail of the user who created/updated the appointment.
   */
  private void logAppointmentEvent(String transition, String message, String userEmail) {
    AuditLogManager am = new AuditLogManager();
    if (transition.equals("SCHEDULED")) {
      am.logAudit(
          Constants.APPOINTMENT_APP, Constants.AUDIT_SCHEDULE_APPT, message, "N/A", userEmail);
    } else if (transition.equals("NEW")) {
      am.logAudit(Constants.APPOINTMENT_APP, Constants.AUDIT_NEW_APPT, message, "N/A", userEmail);
    } else if (transition.equals("UPDATE")) {
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
    if (transition.equals("SCHEDULED")) {
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
          data.get("apptStart") == null ||
          data.get("apptEnd") == null ||
          data.get("apptDate") == null) {
        return false;
      }
      if (data.get("apptDoctorKey").isEmpty() ||
          data.get("status").isEmpty() ||
          data.get("apptStart").isEmpty() ||
          data.get("apptEnd").isEmpty() ||
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
    if (data.containsKey("patientUser") && !(data.get("patientUser").isEmpty())) {
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
   * @return The Google calendar entry key.
   */
  private String createUpdateGcalEntry(Map<String, String> data, Entity apptEntity) {
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
 
      long apptStartVal ,apptEndVal;
      if (data.containsKey("apptStart")) {
        apptStartVal = Long.parseLong(data.get("apptStart"));
        apptStartDate = new Date(apptStartVal);
      } else {
        apptStartDate = (Date)apptEntity.getProperty("apptStart");
      }
      if (data.containsKey("apptEnd")) {
        apptEndVal = Long.parseLong(data.get("apptEnd"));
        apptEndDate = new Date(apptEndVal);
      } else {
        apptEndDate = (Date)apptEntity.getProperty("apptEnd");
      }
      calendarBody = 
        "An appointment is scheduled starting at " + apptStartDate.toString() +
        " and ending at " + apptEndDate.toString() + ".";
      
      String calendarSubject =
          apptStatus + " Appointment for " + patientFname + " " + patientLname +
          " with " + apptDoctor;
      
      String apptLocation = apptDoctor + "'s Office";
      if (userEmail != null && !userEmail.isEmpty()) {
        calendarBody += 
            " This appointment is scheduled with " + apptDoctor +
            " for " + patientFname + " " + patientLname + ", patient e-mail:" + userEmail + ".";
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
      String operation) {
    ConfigManager cm = new ConfigManager();
    String subjectVal;
    String messageVal;
    String officeMessageVal;
    if (operation.equalsIgnoreCase("SCHEDULED")) {
      messageVal = Constants.APPT_SCHEDULE_EMAIL_MESSAGE;
      officeMessageVal = Constants.APPT_SCHEDULE_EMAIL_MESSAGE_OFFICE;
      subjectVal = Constants.APPT_SCHEDULE_EMAIL_SUBJECT;
    } else if (operation.equalsIgnoreCase("CANCELLED")) {
      messageVal = Constants.APPT_CANCEL_EMAIL_MESSAGE;
      subjectVal = Constants.APPT_CANCEL_EMAIL_SUBJECT;
      officeMessageVal = Constants.APPT_CANCEL_EMAIL_MESSAGE_OFFICE;
    } else if (operation.equalsIgnoreCase("UPDATED")) {
      messageVal = Constants.APPT_UPDATE_EMAIL_MESSAGE;
      subjectVal = Constants.APPT_UPDATE_EMAIL_SUBJECT;
      officeMessageVal = Constants.APPT_UPDATE_EMAIL_MESSAGE_OFFICE;
    } else {
      messageVal = Constants.APPT_UPDATE_EMAIL_MESSAGE;
      subjectVal = Constants.APPT_UPDATE_EMAIL_SUBJECT;
      officeMessageVal = Constants.APPT_UPDATE_EMAIL_MESSAGE_OFFICE;
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
      SimpleConfigValue subject =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, subjectVal);
      SimpleConfigValue emailMessage =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, messageVal);
      SimpleConfigValue officeMessage =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, officeMessageVal);
      String userEmailDisplay = data.get("patientFName") + " " + data.get("patientLName");
      //*** Replace the subject with values.
      String emailSubject = subject.getConfigValue();
      emailSubject = emailSubject.replace(Constants.APPT_DR_NAME, data.get("apptDoctor"));
      data.put("emailSubject", emailSubject);

      //*** Send the e-mails.
      try {
        //*** Replace the body vars.
        String emailBody = assembleApptEmailBody(
            emailMessage.getConfigText(), data, timeZoneOffset);
        MailUtils mailSender = new MailUtils();
        mailSender.sendMail(
            sendEmailFrom.getConfigValue(),
            sendEmailFromDisplay.getConfigValue(),
            userEmail,
            userEmailDisplay,
            emailSubject,
            emailBody);
      } catch (Exception ex) {
        LOGGER.severe("Failed to send e-mail on appointment " + operation + "!  Error was: " + ex.toString());
      }
      
      try {
        //*** Replace the body vars.
        String officeEmailBody = assembleApptEmailBody(
            officeMessage.getConfigText(), data, timeZoneOffset);
        MailUtils mailSender = new MailUtils();
        mailSender.sendMail(
            sendEmailFrom.getConfigValue(),
            sendEmailFromDisplay.getConfigValue(),
            officeEmail,
            officeEmail,
            emailSubject,
            officeEmailBody);
      } catch (Exception ex) {
        LOGGER.severe("Failed to send office e-mail on appointment " + operation + "!  Error was: " + ex.toString());
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
      int timeZoneOffset) {
    String emailBody = template;
    if (data.get("patientFName") != null) {
      emailBody = emailBody.replace(Constants.APPT_PATIENT_FNAME, data.get("patientFName"));
    }
    if (data.get("patientLName") != null) {
      emailBody = emailBody.replace(Constants.APPT_PATIENT_LNAME, data.get("patientLName"));
    }

    emailBody = emailBody.replace(Constants.APPT_DR_NAME, data.get("apptDoctor"));
    //*** Assemble the local date strings.
    Calendar apptStartDate = Calendar.getInstance();
    Calendar apptEndDate = Calendar.getInstance();
    apptStartDate.setTimeInMillis(Long.parseLong(data.get("apptStart")));
    apptEndDate.setTimeInMillis(Long.parseLong(data.get("apptEnd")));

    String startDateStr = DateUtils.getReadableTime(apptStartDate, timeZoneOffset);
    String endDateStr = DateUtils.getReadableTime(apptEndDate, timeZoneOffset);
    emailBody = emailBody.replace(Constants.APPT_START_TIME, startDateStr);
    emailBody = emailBody.replace(Constants.APPT_END_TIME, endDateStr);

    emailBody = emailBody.replace(Constants.APPT_DR_OFFICE_PHONE, "(323)866-2216");
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
      return "RESERVED";
    } else if ((oldState.equalsIgnoreCase(AppointmentManager.APPT_AVAILABLE) ||
                oldState.equalsIgnoreCase(AppointmentManager.APPT_RESERVED)) &&
                newState.equalsIgnoreCase(AppointmentManager.APPT_SCHEDULED)) {
      return "SCHEDULED";
    } else if (oldState.equalsIgnoreCase(AppointmentManager.APPT_RESERVED) ||
               oldState.equalsIgnoreCase(AppointmentManager.APPT_SCHEDULED) &&
               newState.equalsIgnoreCase(AppointmentManager.APPT_CANCELLED)) {
      return "CANCELLED";
    } else if (oldState.equalsIgnoreCase("NONE") &&
               newState.equalsIgnoreCase(AppointmentManager.APPT_SCHEDULED)) {
      return "SCHEDULED";
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
   * @param startDate The start date to search.
   * @param endDate The end date to search.
   * @param apptDoctor The doctor's name to search.
   * @param direction The direction of the results (ASC/DESC).
   * @param maxResults The maximum number of results to return.
   * @return
   */
  public ReturnMessage getAppointments(
      Calendar startDate,
      Calendar endDate,
      String apptDoctor,
      SortDirection direction,
      int maxResults) {
    return getAppointments(startDate, endDate, apptDoctor, direction, maxResults, null);
  }

  /**
   * Get a list of appointments based on filter parameters.
   * 
   * @param startDate The start date to search.
   * @param endDate The end date to search.
   * @param apptDoctor The doctor's name to search.
   * @param direction The direction of the results (ASC/DESC).
   * @param maxResults The maximum number of results to return.
   * @param officeKey The office key to filter by.
   * @return 
   */
  public ReturnMessage getAppointments(
      Calendar startDate,
      Calendar endDate,
      String apptDoctor,
      SortDirection direction,
      int maxResults,
      String officeKey) {
    ConfigManager cm = new ConfigManager();
    String status = "SUCCESS";
    String message = "";
    JSONObject returnObj = null;
 
    if (startDate == null) {
      startDate = Calendar.getInstance();
    }
 
    if (endDate == null) {
      status = "FAILURE";
      message = "The parameter for end date is required to search for appointments.";
    }
 
    //*** Only execute the search if required parameters were provided.
    if (status.equals("SUCCESS")) {
      //*** Set the start date to start at midnight and end date to end at 11:59PM.
      startDate.set(
          startDate.get(Calendar.YEAR),
          startDate.get(Calendar.MONTH),
          startDate.get(Calendar.DAY_OF_MONTH),
          0,  // Hour.
          0,  // Minute.
          0); // Second.

      endDate.set(
          endDate.get(Calendar.YEAR),
          endDate.get(Calendar.MONTH),
          endDate.get(Calendar.DAY_OF_MONTH),
          23,  // Hour.
          59,  // Minute.
          59); // Second.

      Query q = new Query("Appointment")
          .addSort("apptStart", SortDirection.ASCENDING);
      //***If a doctor was specified, get the list of appointments under the selected doctor.
      if (apptDoctor != null) {
        Filter apptDoctorFilter =
            new FilterPredicate("apptDoctor",
                                FilterOperator.EQUAL,
                                apptDoctor);
        q.setFilter(apptDoctorFilter);
      }

      //*** If an office was specified, get the list of appointments for the office.
      if (officeKey != null) {
        Key office = KeyFactory.stringToKey(officeKey);
        q.setAncestor(office);
      }

      //*** Check config value to turn on native date filtering.
      PreparedQuery pq;
      SimpleConfigValue dateFilterOn =
          cm.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.APPT_NATIVE_DATE_FILTER);
      if (dateFilterOn != null && dateFilterOn.getConfigValue().equalsIgnoreCase("TRUE")) {
        //*** Most of the appointment searches will have the same start/end date, so this
        //*** will cut down on datastore reads.
        if (DateUtils.makeDateStringFromDate(startDate).equals(DateUtils.makeDateStringFromDate(endDate))) {
          Filter apptDateFilter = new FilterPredicate(
              "apptDate", FilterOperator.EQUAL, DateUtils.makeDateStringFromDate(startDate));
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
      for (Entity result : pq.asIterable()) {
        //*** Check to see if the result falls within the start/end dates.
        Date apptStart = (Date)result.getProperty("apptStart");
        Calendar apptStartCal = Calendar.getInstance();
        apptStartCal.setTime(apptStart);

        Date apptEnd = (Date)result.getProperty("apptEnd");
        Calendar apptEndCal = Calendar.getInstance();
        apptEndCal.setTime(apptEnd);

        if (DateUtils.doDateBlocksOverlap(
            startDate.getTimeInMillis(),
            endDate.getTimeInMillis(),
            apptStart.getTime(),
            apptEnd.getTime())) {
          matchingApptBuilder.add(result);
        }
      }

      List<Entity> matchingAppts = matchingApptBuilder.build();
      message = "Returned " + matchingAppts.size() + " appointments.";

      JSONArray apptReturnArray = new JSONArray();
      for (Entity appt : matchingAppts) {
        appt.setProperty("key", KeyFactory.keyToString(appt.getKey()));
        JSONObject apptObj = new JSONObject(appt.getProperties());
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
          .addSort("apptStart", SortDirection.DESCENDING);
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
    for (Entity result : pq.asIterable()) {
      //*** Set them to active.
      result.setProperty("status", APPT_AVAILABLE);
      //*** Save them.
      ds.put(result);
      changeCount++;
    }
    LOGGER.info("Reset " + Integer.toString(changeCount) + " reserved appointments.");
  }
}
