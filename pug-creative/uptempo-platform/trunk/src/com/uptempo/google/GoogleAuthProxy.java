/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.uptempo.google;

import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import com.medselect.util.Constants;
import com.medselect.util.URLRequestUtil;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Mike Gordon (mgordon)
 */
public class GoogleAuthProxy {
  protected static final Logger LOGGER = Logger.getLogger(GoogleAuthProxy.class.getName());
  
  public static String doGoogleAuth(String accessToken) throws GoogleAuthException {
    return GoogleAuthProxy.doGoogleCodeAuth(accessToken);
  }
  
  /**
   * Executes the Google authentication to get the access token and refresh token.
   * @param accessToken  The access code provided from the user login request to Google.
   * @param refreshToken The refresh token that's stored in the app
   * @return A message containing the result of the authentication.
   */
  public static String doGoogleCodeAuth(String accessCode) throws GoogleAuthException {
    String message = null;
    final Map<String, String> params;
    if (accessCode != null) {
      //*** Assemble the parameters.
      ConfigManager cManager = new ConfigManager();
      String clientId = cManager.getSimpleConfigValue(
          Constants.APPOINTMENT_APP,
          Constants.APPT_GOOGLE_CLIENT_ID).getConfigValue();
      String clientSecret = cManager.getSimpleConfigValue(
          Constants.APPOINTMENT_APP,
          Constants.APPT_GOOGLE_CLIENT_SECRET).getConfigValue();
      String callbackURL = cManager.getSimpleConfigValue(
          Constants.APPOINTMENT_APP,
          Constants.APPT_OFFICE_CAL_CALLBACK_URL).getConfigValue();
      //*** Assemble the access code request.
      params = new ImmutableMap.Builder<String,String>()
          .put("code", accessCode)
          .put("client_id", clientId)
          .put("client_secret", clientSecret)
          .put("redirect_uri", callbackURL)
          .put("grant_type", "authorization_code")
          .build();
      LOGGER.info("Attempting Google access token authentication.");

      //*** Initiate the next request to get the access token and refresh token.
      String tokenResponse = URLRequestUtil.doPost(Constants.GOOGLE_ACCOUNTS_API_URL, params);
      try {
        JSONObject responseObj = new JSONObject(tokenResponse);
        String storedRefreshToken = null;
        String storedAccessToken = responseObj.getString("access_token");
        if (responseObj.has("refresh_token")) {
          storedRefreshToken = responseObj.getString("refresh_token");
        }
        int tokenExpiration = responseObj.getInt("expires_in");
        if (!storeTokensAsConfigs(storedAccessToken, storedRefreshToken, tokenExpiration)) {
          message = "Tokens from Google could not be stored.";
          LOGGER.severe(message);
          throw new GoogleAuthException(message);
        } else {
          message = "Google token auth successful, expires in: " + Integer.toString(tokenExpiration);
        }
      } catch(JSONException ex) {
        message = "Error parsing authentication response: " + ex.toString();
        LOGGER.severe(message);
        throw new GoogleAuthException(message);
      }  
    }
    return message;
  }

  /**
   * Executes the Google authentication to get the access token and refresh token (if applicable).
   * @param accessToken  The access code provided from the user login request to Google.
   * @param refreshToken The refresh token that's stored in the app
   * @return A new access token.
   */
  public static String doGoogleRefreshAuth(String refreshToken) throws GoogleAuthException {
    String message = null;
    String newAccessToken = null;
    final Map<String, String> params;
    if (refreshToken != null) {
      //*** Assemble the parameters.
      ConfigManager cManager = new ConfigManager();
      String clientId = cManager.getSimpleConfigValue(
          Constants.APPOINTMENT_APP,
          Constants.APPT_GOOGLE_CLIENT_ID).getConfigValue();
      String clientSecret = cManager.getSimpleConfigValue(
          Constants.APPOINTMENT_APP,
          Constants.APPT_GOOGLE_CLIENT_SECRET).getConfigValue();
      String callbackURL = cManager.getSimpleConfigValue(
          Constants.APPOINTMENT_APP,
          Constants.APPT_OFFICE_CAL_CALLBACK_URL).getConfigValue();
      //*** Assemble the request depending on whether it has auth token or refresh token.
      params = new ImmutableMap.Builder<String,String>()
          .put("refresh_token", refreshToken)
          .put("client_id", clientId)
          .put("client_secret", clientSecret)
          .put("grant_type", "refresh_token")
          .build();
      LOGGER.info("Attempting Google refresh token authentication.");
   
      //*** Initiate the next request to get the access token and refresh token.
      String tokenResponse = URLRequestUtil.doPost(Constants.GOOGLE_ACCOUNTS_API_URL, params);
      try {
        JSONObject responseObj = new JSONObject(tokenResponse);
        newAccessToken = responseObj.getString("access_token");
        String storedRefreshToken = null;
        if (responseObj.has("refresh_token")) {
          storedRefreshToken = responseObj.getString("refresh_token");
        }
        int tokenExpiration = responseObj.getInt("expires_in");
        if (!storeTokensAsConfigs(newAccessToken, storedRefreshToken, tokenExpiration)) {
          message = "Tokens from Google could not be stored.";
          LOGGER.severe(message);
          throw new GoogleAuthException(message);
        } else {
          message = "Google token auth successful, expires in: " + Integer.toString(tokenExpiration);
          LOGGER.info(message);
        }
      } catch(JSONException ex) {
        message = "Error parsing authentication response: " + ex.toString();
        LOGGER.severe(message);
        throw new GoogleAuthException(message);
      }  
    }
    return newAccessToken;
  }

  /**
   * Stores the access and refresh tokens as Config Values.
   * 
   * @param accessToken The Google access token for the office calendar.
   * @param refreshToken The Google refresh token for the office calendar.
   * @param accessExpiration The expiration of the access token, in seconds.
   * @return true|false depending on success of the token storage.
   */
  private static boolean storeTokensAsConfigs(
      String accessToken,
      String refreshToken,
      int accessExpiration) {
    boolean refreshResult = true;
    boolean expirationResult = true;
    SimpleConfigValue accessTokenValue = new SimpleConfigValue();
    SimpleConfigValue refreshTokenValue = new SimpleConfigValue();
    SimpleConfigValue accessExpirationValue = new SimpleConfigValue();
    ConfigManager cManager = new ConfigManager();

    //*** TODO(mgordon): Change this logic to set the values for the specific offics.
    accessTokenValue.setAppCode(Constants.APPOINTMENT_APP);
    accessTokenValue.setConfigName(Constants.APPT_OFFICE_CAL_ACCESS_TOKEN);
    accessTokenValue.setConfigDescription(
        "(Temporary) Place to store single app value" +
        "to store calendar access token.");
    accessTokenValue.setConfigValue(accessToken);
    accessTokenValue.setConfigUser("appointment-office@test.com");
    //*** Only store the refresh token if it was included.
    if (refreshToken != null) {
      refreshTokenValue.setAppCode(Constants.APPOINTMENT_APP);
      refreshTokenValue.setConfigName(Constants.APPT_OFFICE_CAL_REFRESH_TOKEN);
      refreshTokenValue.setConfigDescription(
          "(Temporary) Place to store single app value" +
          "to store calendar refresh token.");
      refreshTokenValue.setConfigValue(refreshToken);
      refreshTokenValue.setConfigUser("appointment-office@test.com");
      refreshResult = cManager.insertSimpleConfigValue(refreshTokenValue);
    }
    accessExpirationValue.setAppCode(Constants.APPOINTMENT_APP);
    accessExpirationValue.setConfigName(Constants.APPT_OFFICE_CAL_ACCESS_EXPIRATION);
    accessExpirationValue.setConfigDescription(
         "(Temporary) Place to store the access token expiration date.");
    Calendar now = Calendar.getInstance();
    now.add(Calendar.SECOND, accessExpiration);
    String epochString = Long.toString(now.getTimeInMillis());
    accessExpirationValue.setConfigValue(epochString);
    accessExpirationValue.setConfigUser("appointment-office@test.com");
    expirationResult = cManager.insertSimpleConfigValue(accessExpirationValue);
    
    return cManager.insertSimpleConfigValue(accessTokenValue) && refreshResult && expirationResult;
  }
}
