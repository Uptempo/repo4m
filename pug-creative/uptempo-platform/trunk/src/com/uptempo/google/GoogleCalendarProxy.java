/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.uptempo.google;

import com.google.common.collect.ImmutableMap;
import com.medselect.common.ReturnMessage;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import com.medselect.util.Constants;
import com.medselect.util.DateUtils;
import com.medselect.util.URLRequestUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Calendar proxy for managing office and user calendars.
 * @author Mike Gordon (mgordon)
 * 
 */
public class GoogleCalendarProxy {
  protected static final Logger LOGGER = Logger.getLogger(GoogleCalendarProxy.class.getName());
  /**
   * Inserts or updates a Google calendar appointment entry.
   * @param accessToken The Google OAuth access token.
   * @param refreshToken The Google OAuth refresh token.
   * @param startTime
   * @param endTime
   * @param title
   * @param body
   * @param location
   * @param calendarGmail
   * @param apptKey Existing appointment key.  Can be null, if not null the operation is an update.
   * @return 
   */
  public static ReturnMessage insertUpdateAppointmentEntry(
      String accessToken,
      String refreshToken,
      Date startTime,
      Date endTime,
      String title,
      String body,
      String location,
      String calendarGmail,
      String apptKey) {
    String status = "SUCCESS";
    String message = null;
    //*** Check to see if the access token has expired.
    ReturnMessage refreshStatus =
        GoogleCalendarProxy.checkAndRefreshAuthToken(refreshToken);
    status = refreshStatus.getStatus();
    message = refreshStatus.getMessage();
    Calendar startCal = Calendar.getInstance();
    Calendar endCal = Calendar.getInstance();
    startCal.setTime(startTime);
    endCal.setTime(endTime);
    String startRfcDate = DateUtils.getRfcTime(startCal);
    String endRfcDate = DateUtils.getRfcTime(endCal);
    JSONObject requestContainer = new JSONObject();
    JSONObject startContainer = new JSONObject();
    JSONObject endContainer = new JSONObject();
    String url = null;
    String requestBody = null;
    
    try {
      if (apptKey == null) {
        url = String.format(
            Constants.GOOGLE_CALENDAR_API_URL_EVENTS,
            URLEncoder.encode(calendarGmail, "UTF-8"));
      } else {
        url = String.format(
            Constants.GOOGLE_CALENDAR_API_URL_EVENTS_UPDATE,
            URLEncoder.encode(calendarGmail, "UTF-8"), apptKey);
      }
    } catch(UnsupportedEncodingException ex) {
      message = "Error encoding calendar ID e-mail.";
      LOGGER.severe(message);
      status = "FAILURE";
    }
    
    if (status.equals("SUCCESS")) {
      try {
        startContainer.put("dateTime", startRfcDate);
        endContainer.put("dateTime", endRfcDate);
        requestContainer.put("summary", title);
        requestContainer.put("description", body);
        requestContainer.put("location", location);
        requestContainer.put("start", startContainer);
        requestContainer.put("end", endContainer);
        requestBody = requestContainer.toString();
      } catch (JSONException ex) {
        message = "Error encoding calendar JSON.";
        LOGGER.severe(message);
        status = "FAILURE";
      }
    }
    
    String authString = "Bearer " + accessToken;
 
    Map<String, String> header = new ImmutableMap.Builder<String,String>()
            .put("Content-Type", "application/json")
            .put("Authorization", authString)
            .put("X-JavaScript-User-Agent", "Uptempo Appointment System")
            .build();
    
    LOGGER.info("Google Calendar API URL: " + url);
    String calResponse;
    if (apptKey != null) {
      calResponse = URLRequestUtil.doPut(url, null, header, requestBody);
    } else {
      calResponse = URLRequestUtil.doPost(url, null, header, requestBody);
    }

    JSONObject calResponseObj = null;
    try {
      calResponseObj = new JSONObject(calResponse);
    } catch (JSONException ex) {
      message = "Error decoding calendar response.";
      LOGGER.severe(message);
      status = "FAILURE";
    }
    ReturnMessage response =
        new ReturnMessage.Builder().status(status).message(message).value(calResponseObj).build();
    return response;
  }
  
  /**
   * 
   * @param refreshToken
   * @return 
   */
  private static ReturnMessage checkAndRefreshAuthToken(String refreshToken) {
    String status = "SUCCESS";
    String newToken = null;
    String message = "";
    try {
      ConfigManager cManager = new ConfigManager();
      SimpleConfigValue accessExpiration = cManager.getSimpleConfigValue(
          Constants.APPOINTMENT_APP,
          Constants.APPT_OFFICE_CAL_ACCESS_EXPIRATION);
      long expirationSeconds;
      expirationSeconds = Long.parseLong(accessExpiration.getConfigValue());
      Calendar now = Calendar.getInstance();

      if (expirationSeconds < now.getTimeInMillis()) {
        //*** Get new access token, refresh needs to occur.
        newToken = GoogleAuthProxy.doGoogleRefreshAuth(refreshToken);
      }
    } catch (GoogleAuthException ex) {
      status = "FAILURE";
      message = "Google refresh authentication failed: " + ex.toString();
    } catch (Exception ex) {
      status = "FAILURE";
      message = "Google authentication not complete!";
    }
    
    return new ReturnMessage.Builder().status(status).message(message).key(newToken).build();
  }
}
