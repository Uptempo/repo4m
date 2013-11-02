/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.user;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.Text;
import com.medselect.server.BaseServerResource;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 *
 * @author Mike Gordon
 */
public class UserModServerResource extends BaseServerResource {

  private static final String PASSWORD_KEY = "PAZZETAMCRAEUUOP";
  private static final boolean SIGN_UP_EMAIL = false;
  private String modUserEmail;
  
  public UserModServerResource() {
    super();
  }

  @Override
  protected void doInit() throws ResourceException {
    // Get the "itemName" attribute value taken from the URI template
    // /user/{userEmail}
    modUserEmail = (String) getRequest().getAttributes().get("userEmail");
  }

  /**
   * Reads a single user and returns the user information as JSON.
   * 
   * @param params Query string parameters.
   * @return An {@link JsonRepresentation} containing the JSON for a user with these values:
   *     lastName - user last name
   *     firstName - user first name
   *     title - user title
   *     city - user city
   *     state - user state
   *     address1 - address, line 1
   *     address2 - address, line 2
   *     cell - user cell phone #
   */
  @Get
  public JsonRepresentation readUser(Representation params) {
    LOGGER.info("Reading Single User: " + modUserEmail + ".");
    String message = "Returned user " + modUserEmail;
    String status = "FAILURE";
    Key dsKey = KeyFactory.createKey("Users", this.modUserEmail);

    
    JSONObject obj = new JSONObject();
    try {
      Entity userEntity = ds.get(dsKey);
      obj.put("lastName", userEntity.getProperty("lastName"));
      obj.put("firstName", userEntity.getProperty("firstName"));
      obj.put("title", userEntity.getProperty("title"));
      obj.put("city", userEntity.getProperty("city"));
      obj.put("state", userEntity.getProperty("state"));
      PhoneNumber cell = (PhoneNumber)userEntity.getProperty("cell");
      obj.put("cell", cell.getNumber());
      Text address1Text = (Text)userEntity.getProperty("address1");
      Text address2Text = (Text)userEntity.getProperty("address2");
      obj.put("address1", getTextValueOrNull(address1Text));
      obj.put("address2", getTextValueOrNull(address2Text));
      obj.put("notifyEmail", userEntity.getProperty("notifyEmail"));
      obj.put("notifyText", userEntity.getProperty("notifyText"));
      obj.put("addFooter", userEntity.getProperty("addFooter"));
      obj.put("officeGroupKey", userEntity.getProperty("officeGroupKey"));
      obj.put("officeKey", userEntity.getProperty("officeKey"));
      status = "SUCCESS";
    } catch (JSONException ex) {
      message = "Error converting error list to JSON: " + ex.toString();
    } catch (EntityNotFoundException ex) {
      message = "User " + modUserEmail + " not available.";
    }
    LOGGER.info(message);
    JsonRepresentation response = this.getJsonRepresentation(status, message, obj);

    return response;
  }


  /**
   * Deletes a user.
   *
   * @return {@link JsonRepresentation} containing the status and delete message.
   */
  @Delete
  public JsonRepresentation deleteUser() {
    Form uForm = this.getRequest().getResourceRef().getQueryAsForm();
    String deleteUserStatus = "SUCCESS";
    String message = "User " + this.modUserEmail + " deleted.";
    Key dsKey = KeyFactory.createKey("Users", this.modUserEmail);
    LOGGER.info("Deleting user " + dsKey.getName());

    //*** If the delete fails, return that result to the front end.
    try {
      ds.delete(dsKey);
    } catch (Exception ex) {
      LOGGER.severe("Delete of user " + this.modUserEmail + " failed: " + ex.toString());
      deleteUserStatus = "FAILURE";
      message = "Could not delete user " + this.modUserEmail;
    }

    JsonRepresentation a = this.getJsonRepresentation(
        deleteUserStatus,
        message,
        null);

    return a;
  }

  private String getTextValueOrNull(Text value) {
    String valueText;
    if (value == null) {
      valueText = "";
    } else {
      valueText = value.getValue();
    }
    return valueText;
  }
}
