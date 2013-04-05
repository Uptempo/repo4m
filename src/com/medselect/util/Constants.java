/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.util;

/**
 * Class which provides a central location for static constants that drive configuration values
 * for the services platform.
 * 
 * @author Mike Gordon (mgordon)
 */
public class Constants {
  //*** Start config codes ***
  //*** Common application code.
  public static String COMMON_APP = "COMMON";
  public static String APPOINTMENT_APP = "APPOINTMENT";

  //*** The following config codes vary by application.
  //*** Indicates if e-mail should be sent when a new user signs up.
  public static String SEND_USER_EMAIL = "NEWUSERSENDEMAIL";
  //*** No reply e-mail config codes.
  public static String NO_REPLY_EMAIL = "NOREPLYEMAIL";
  public static String NO_REPLY_DISPLAY = "NOREPLYEMAILDISPLAY";
  //*** User sign up e-mail config codes.
  public static String NEW_USER_EMAIL_SUBJECT = "NEWUSEREMAILSUBJECT";
  public static String NEW_USER_EMAIL_MESSAGE = "NEWUSEREMAILMESSAGE";
  //*** Office time zone.
  public static String DAYLIGHT_SAVINGS_TIME_ON = "DAYLIGHTSAVINGSTIME";
  //*** Flag to turn API key requirement on/off.
  public static String API_SECURITY_FLAG = "APISECURITYFLAG";
  //*** End variable config codes.
  
  //*** Appointment system e-mail data.
  public static String APPT_REPLY_EMAIL = "APPTREPLYEMAIL";
  public static String APPT_REPLY_DISPLAY = "APPTREPLYDISPLAY";
  public static String APPT_NEW_EMAIL_SUBJECT = "APPTNEWEMAILSUBJECT";
  public static String APPT_NEW_EMAIL_MESSAGE = "APPTNEWEMAILMESSAGE";
  //*** Update an appointment.
  public static String APPT_UPDATE_EMAIL_SUBJECT = "APPTUPDATEEMAILSUBJECT";
  public static String APPT_UPDATE_EMAIL_MESSAGE = "APPTUPDATEEMAILMESSAGE";
  public static String APPT_UPDATE_EMAIL_MESSAGE_OFFICE = "APPTUPDATEEMAILMESSAGEOFFICE";
  //*** Schedule an appointment.
  public static String APPT_SCHEDULE_EMAIL_SUBJECT = "APPTSCHEDULEDMAILSUBJECT";
  public static String APPT_SCHEDULE_EMAIL_MESSAGE = "APPTSCHEDULEDMAILMESSAGE";
  public static String APPT_SCHEDULE_EMAIL_MESSAGE_OFFICE = "APPTUPDATEEMAILMESSAGEOFFICE";
  //*** Cancel an appointment.
  public static String APPT_CANCEL_EMAIL_SUBJECT = "APPTCANCELLEDMAILSUBJECT";
  public static String APPT_CANCEL_EMAIL_MESSAGE = "APPTCANCELLEDMAILMESSAGE";
  public static String APPT_CANCEL_EMAIL_MESSAGE_OFFICE = "APPTUPDATEEMAILMESSAGEOFFICE";
  //*** Appointment system calendar data.
  public static String APPT_CALENDAR_USER = "APPTDEFAULTCALUSER";
  public static String APPT_CALENDAR_PWD = "APPTDEFAULTCALPWD";
  //*** Appointment Google client ID codes.
  public static String APPT_GOOGLE_CLIENT_ID = "APPTGOOGLECLIENTID";
  public static String APPT_GOOGLE_CLIENT_SECRET = "APPTGOOGLECLIENTSECRET";
  //*** Appointment Patient calendar authorization callback URL.
  public static String APPT_PATIENT_CAL_CALLBACK_URL = "APPTPATCALCALLBACKURL";
  //*** Appointment Office calendar authorization callback URL.
  public static String APPT_OFFICE_CAL_CALLBACK_URL = "APPTOFFICECALCALLBACKURL";
  //*** (Temporary) Access token and refresh token for Google Calendar integration.
  public static String APPT_OFFICE_CAL_ACCESS_TOKEN = "CALACCESSTOKEN";
  public static String APPT_OFFICE_CAL_REFRESH_TOKEN = "CALREFRESHTOKEN";
  public static String APPT_OFFICE_CAL_ACCESS_EXPIRATION = "CALACCESSEXPIRATION";
  //*** Switch to turn on/off native date filtering.
  public static String APPT_NATIVE_DATE_FILTER = "APPTNATIVEDATEFILTER";
  //*** End config codes ***
  
  //*** Start audit codes ***
  //*** Indicates when a new user is created.
  public static String AUDIT_NEW_USER = "NEWUSER";
  public static String AUDIT_NEW_APPT = "NEWAPPT";
  public static String AUDIT_UPDATE_APPT = "UPDATEAPPT";
  public static String AUDIT_SCHEDULE_APPT = "SCHEDULEAPPT";
  public static String APP_KEY_GENERATE = "APPKEYGENERATE";
  public static String AUDIT_VIEW_APPTS = "VIEWAPPTS";
  //*** End audit codes ***
  
  //*** External Service URLs/Params
  public static String GOOGLE_ACCOUNTS_API_URL = "https://accounts.google.com/o/oauth2/token";
  public static String GOOGLE_CALENDAR_API_URL = "https://www.googleapis.com/calendar/v3/calendars";
  public static String GOOGLE_USERINFO_API_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
  public static String GOOGLE_CALENDAR_API_URL_EVENTS =
      "https://www.googleapis.com/calendar/v3/calendars/%s/events";
  public static String GOOGLE_CALENDAR_API_URL_EVENTS_UPDATE =
      "https://www.googleapis.com/calendar/v3/calendars/%s/events/%s";
  //*** End external service URLs/Params

  //*** E-mail constants
  public static String NEW_USER_EMAIL_EMAIL = "%useremail%";
  public static String NEW_USER_EMAIL_FNAME = "%userfname%";
  public static String NEW_USER_EMAIL_LNAME = "%userlname%";
  //*** Appointment E-mail constants
  public static String APPT_DR_NAME = "%doctorname%";
  public static String APPT_START_TIME = "%starttime%";
  public static String APPT_END_TIME = "%endtime%";
  public static String APPT_PATIENT_FNAME = "%patientfname%";
  public static String APPT_PATIENT_LNAME = "%patientlname%";
  public static String APPT_DR_OFFICE_PHONE = "%doctorofficephone%";
  public static String APPT_EMAIL_DESCRIPTION = "%apptdescription%";
  public static String APPT_EMAIL_DATE = "%apptdate%";
  public static String APPT_EMAIL_SUBJECT = "%subject%";
  public static String APPT_EMAIL_SOURCE = "%apptsource%";
  public static String APPT_PATIENT_PHONE = "%patientphone%";
  //*** End E-mail constants
  
  //*** Misc. Constants.
  //*** The field in the appointment entity that maps the Google appointment key.
  public static String APPT_GOOGLE_KEY_FIELD = "googleApptId";
  //*** Time in seconds until the key cache expires for API security.
  public static int KEY_CACHE_EXPIRATION = 300;
  //*** Time to reach back to reset appointments to available.
  public static long APPT_RESET_REACHBACK = 300000;
  //*** End Misc. Constants.
  
}
