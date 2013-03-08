/*
 * Copyright 2013 Uptempo Group Inc.
 */

package com.medselect.user;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.collect.ImmutableMap;
import com.medselect.appointment.AppointmentServerResource;
import com.medselect.audit.AuditLogManager;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import com.medselect.config.ConfigManager;
import com.medselect.util.Constants;

import com.medselect.config.SimpleConfigValue;
import com.medselect.util.MailUtils;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * The user manager provides user management functions to server resources.  The following server
 * resources will need to use this manager level class:
 *
 * {@link UserServerResource}
 * {@link AppointmentServerResource}
 * @author Mike Gordon (mgordon)
 */
public class UserManager extends BaseManager {
  private static final String PASSWORD_KEY = "PAZZETAMCRAEUUOP";
  private static final boolean SIGN_UP_EMAIL = false;
  public static final String USER_ENTITY_NAME = "Users";
  public static final String USER_DISPLAY_NAME = "Uptempo System Users";
  public static final Map<String, BaseManager.FieldType> USER_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("email", BaseManager.FieldType.STRING)
          .put("title", BaseManager.FieldType.STRING)
          .put("firstName", BaseManager.FieldType.STRING)
          .put("lastName", BaseManager.FieldType.STRING)
          .put("address1", BaseManager.FieldType.STRING)
          .put("address2", BaseManager.FieldType.STRING)
          .put("city", BaseManager.FieldType.STRING)
          .put("state", BaseManager.FieldType.STRING)
          .put("cell", BaseManager.FieldType.PHONE_NUMBER)
          .put("password", BaseManager.FieldType.SHORT_BLOB)
          .put("source", BaseManager.FieldType.STRING)
          .build();

  //*** appCode is used to identify the app to get app settings around sending the user e-mail.
  private String appCode;

  public UserManager(String app) {
    super(USER_STRUCTURE, USER_ENTITY_NAME, USER_DISPLAY_NAME);
    this.appCode = app;
  }

  /**
   * Creates a user.
   * @param userMap The user attributes.
   * @return The status and the messsage from the attempted create operation.
   */
  public ReturnMessage createUser(Map<String, String> userMap) {
    boolean pwdSuccess = true;
    boolean inputHasPassword = false;

    //*** Detect an empty cell phone to avoid a Google App Engine bug
    //*** that doesn't allow the datastore viewer to see the Users entity
    //*** with an empty cell phone field.
    String cellPhone = userMap.get("cell");
    if (cellPhone == null || cellPhone.isEmpty()) {
      userMap.put("cell", "none");
    }

    //*** Change e-mail to lower case.
    String userEmail = userMap.get("email").toLowerCase();
    userMap.put("email", userEmail);

    //*** Get the user first and last names.
    String firstName = userMap.get("firstName");
    String lastName = userMap.get("lastName");

    //*** If the password is provided, encrypt and save it.
    byte[] encryptedPwd = null;
    if (userMap.containsKey("password")) {
      inputHasPassword = true;
      encryptedPwd = this.generateCipherPassword(userMap.get("password"));
      if (encryptedPwd == null) {
        pwdSuccess = false;
      } else {
        userMap.put("password", new String(encryptedPwd));
      }
    }

    ReturnMessage result = this.doCreate(userMap, true, null);
    
    //*** If a password was provided for the user, then hash it and save it properly.
    if (inputHasPassword) {
      if (pwdSuccess == false) {
        ReturnMessage.Builder builder = new ReturnMessage.Builder();
        result = builder
            .status("FAILURE")
            .message("Failed to save password hash!")
            .value(null)
            .build();
      } else {
        //*** Save the byte array password correctly.  This is kind of kludgy and should be replaced
        //*** with a correct saving of the user password, once.
        Key userKey = KeyFactory.createKey(entityName, userEmail);
        try {
          Entity user = ds.get(userKey);
          user.setProperty("password", new ShortBlob(encryptedPwd));
          ds.put(user);
        } catch (EntityNotFoundException ex) {
          LOGGER.severe("Could not find user with e-mail " + userEmail);
        }
      }
    }

    //*** Create the audit entry for the new user.
    AuditLogManager aManager = new AuditLogManager();
    aManager.logAudit(
        appCode,
        Constants.AUDIT_NEW_USER,
        "New user " + userEmail + " created.",
        "N/A",
        userEmail);
    
    //*** Check if the app has a flag to send e-mail.  If it's null or FALSE, then don't send
    //*** e-mail.
    ConfigManager cm = new ConfigManager();
    SimpleConfigValue sendEmailFlag = cm.getSimpleConfigValue(appCode, Constants.SEND_USER_EMAIL);
    
    if (sendEmailFlag != null && sendEmailFlag.getConfigValue().toLowerCase().equals("true")) {
      //*** Send an e-mail indicating signup for the application.
      SimpleConfigValue sendEmailFrom = cm.getSimpleConfigValue(appCode, Constants.NO_REPLY_EMAIL);
      SimpleConfigValue sendEmailFromDisplay =
          cm.getSimpleConfigValue(appCode, Constants.NO_REPLY_DISPLAY);
      SimpleConfigValue subject =
          cm.getSimpleConfigValue(appCode, Constants.NEW_USER_EMAIL_SUBJECT);
      SimpleConfigValue emailMessage =
          cm.getSimpleConfigValue(appCode, Constants.NEW_USER_EMAIL_MESSAGE);
      //*** Setup the e-mail contents.
      String messageText = emailMessage.getConfigText();
      messageText = messageText.replace(Constants.NEW_USER_EMAIL_EMAIL, userEmail);
      messageText = messageText.replace(Constants.NEW_USER_EMAIL_FNAME, firstName);
      messageText = messageText.replace(Constants.NEW_USER_EMAIL_LNAME, lastName);
      String userEmailDisplay = firstName + " " + lastName;
      MailUtils mailSender = new MailUtils();
      mailSender.sendMail(
          sendEmailFrom.getConfigValue(),
          sendEmailFromDisplay.getConfigValue(),
          userEmail,
          userEmailDisplay,
          subject.getConfigValue(),
          messageText);
    }
    
    return result;
  }

  public ReturnMessage updateUser(Map<String, String> data, String userKey) {
    data.put("key", userKey);
    return this.doUpdate(data);
  }

  /**
   * Read users using filter parameters.
   * 
   * @param params A {@link Map} containing the parameters passed from the query string or POST
   *     request.
   * @param itemKey An optional key to indicate that a specific user should be returned based on
   *     the GAE key.
   *
   * @return A status, message, and JSONObject containing the matching users.  If the format
   * parameter was set to list, then a list of users is returned in JSON.  If the format wasn't
   * specified or set to object then a key/value object of users is returned.
   */
  public ReturnMessage readUsers(Map<String, String> params, String itemKey) {
    //*** Setup a custom query for reading users, if the email param is set.
    if(params.containsKey("email")) {
      FilterPredicate emailFilter =
          new FilterPredicate("email", FilterOperator.EQUAL, params.get("email"));
      q = new Query(entityName).setFilter(emailFilter);
    }

    return this.doRead(params, itemKey);
  }

  /**
   * Checks if a user exists from the e-mail.
   * 
   * @param userEmail  The user e-mail to check.
   * @return true/false, depending on whether the user exists.
   */
  public boolean doesUserExist(String userEmail) {
    boolean returnVal = true;
    Key userKey = KeyFactory.createKey(entityName, userEmail);
    try {
      ds.get(userKey);
    } catch (EntityNotFoundException ex) {
      returnVal = false;
    }
    return returnVal;
  }
  
  private byte[] generateCipherPassword(String clearPassword) {
    byte[] encryptedPwd = null;
    try {
      byte[] key = PASSWORD_KEY.getBytes();
      //*** Could generate NoSuchAlgorithmException.
      Cipher cipher = Cipher.getInstance("AES");
      SecretKeySpec k = new SecretKeySpec(key, "AES");
      //*** Could generate InvalidKeyException.
      cipher.init(Cipher.ENCRYPT_MODE, k);
      encryptedPwd = cipher.doFinal(clearPassword.getBytes());
    } catch (Exception ex) {
      //***Log the error and add to the user output.
      LOGGER.severe("Error encrypting password: " + ex.toString());
      return null;
    }
    return encryptedPwd;
  }
}
