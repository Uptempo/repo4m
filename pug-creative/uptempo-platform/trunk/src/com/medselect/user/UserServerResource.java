/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.user;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.medselect.common.ReturnMessage;
import com.medselect.server.BaseServerResource;
import com.medselect.util.Constants;
import com.medselect.util.MailUtils;
import com.medselect.util.SystemUtils;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

/**
 *
 * @author Mike Gordon
 */
public class UserServerResource extends BaseServerResource {

  private static final String PASSWORD_KEY = "PAZZETAMCRAEUUOP";
  private static final boolean SIGN_UP_EMAIL = false;
  
  public UserServerResource() {
    super();
  }

  @Get
  public JsonRepresentation readUsers(Representation params) {
    LOGGER.info("Reading Users.");
    Form pForm = this.getRequest().getResourceRef().getQueryAsForm();

    //***Create the filter
    String lengthParam = pForm.getFirstValue("length");
    String directionParam = pForm.getFirstValue("direction");
    //***Used to indicate if it's formatted as an array of strings or objects.
    String dataFormatParam = pForm.getFirstValue("format");
    //***Used to filter by e-mail as a "contains".
    String userEmailParam = pForm.getFirstValue("email");

    int lengthValue = 0;
    try {
      lengthValue = Integer.parseInt(lengthParam);
    } catch(NumberFormatException ex) {
      //***No-op
    }

    //***Get the user list
    // Use class Query to assemble a query
    Query q = new Query("Users");

    //*** Set the filters.
    if (userEmailParam != null && !userEmailParam.isEmpty()) {
      Filter emailFilter = new FilterPredicate(
          "email",
          FilterOperator.IN,
          userEmailParam);
      q.setFilter(emailFilter);
    }

    // Use PreparedQuery interface to retrieve results
    PreparedQuery pq = ds.prepare(q);

    //***Construct the user data array, determine if the return format should be an object or array.
    int usersReturned = 0;
    JSONArray userArray = new JSONArray();
    if (dataFormatParam == null || dataFormatParam.isEmpty()) {
      for (Entity result : pq.asIterable()) {
        JSONObject userJson = new JSONObject(result.getProperties());
        userArray.put(userJson);
        usersReturned++;
      }
    } else {
      for (Entity result : pq.asIterable()) {
        Map <String, Object>userProps = result.getProperties();
        JSONArray userEntryArray = new JSONArray();
        //***Add the values from each user in order.
        userEntryArray.put(result.getKey().getName());
        userEntryArray.put(userProps.get("lastName"));
        userEntryArray.put(userProps.get("firstName"));
        userEntryArray.put(userProps.get("title"));
        userEntryArray.put(userProps.get("city"));
        userEntryArray.put(userProps.get("state"));
        PhoneNumber cell = (PhoneNumber)userProps.get("cell");
        userEntryArray.put(cell.getNumber());
        userEntryArray.put(userProps.get("address1"));
        userEntryArray.put(userProps.get("address2"));
        userEntryArray.put(userProps.get("notifyEmail"));
        userEntryArray.put(userProps.get("notifyText"));
        userEntryArray.put(userProps.get("addFooter"));
        userArray.put(userEntryArray);
        usersReturned++;
      }
    }

    String message = "Returned " + usersReturned + " users.";
    JSONObject obj = new JSONObject();
    try {
      obj.put("users", userArray);
    } catch (JSONException ex) {
      message = "Error converting error list to JSON: " + ex.toString();
    }

    LOGGER.info(message);
    JsonRepresentation response = this.getJsonRepresentation("SUCCESS", message, obj);

    return response;
  }

  @Post
  public JsonRepresentation createUser(Representation user) {
    Form uForm = new Form(user);

    Map<String, String> valueMap = uForm.getValuesMap();

    UserManager uManager = new UserManager(Constants.COMMON_APP);
    ReturnMessage response = uManager.createUser(valueMap);
    JsonRepresentation a = this.getJsonRepresentation(
        response.getStatus(),
        response.getMessage(),
        response.getValue());
    return a;
  }
  
  public JsonRepresentation createUserOld(Representation user) {
    Form uForm = new Form(user);
    String createUserStatus = "SUCCESS";
    JSONArray errorList = new JSONArray();

    //***Read the user information from the request
    String userEmail = uForm.getFirstValue("email");
    String userTitle = uForm.getFirstValue("title");
    String userFName = uForm.getFirstValue("firstname");
    String userLName = uForm.getFirstValue("lastname");
    String userAddress1 = uForm.getFirstValue("address1");
    String userAddress2 = uForm.getFirstValue("address2");
    //*** Detect an empty cell phone to avoid a Google App Engine bug
    //*** that doesn't allow the datastore viewer to see the Users entity
    //*** with an empty cell phone field.
    String cellPhone = uForm.getFirstValue("cell");
    if (cellPhone == null || cellPhone.isEmpty()) {
      cellPhone = "none";
    }

    Key dsKey = KeyFactory.createKey("Users", userEmail.toLowerCase());
    Entity newUser = new Entity(dsKey);
    newUser.setUnindexedProperty("title", userTitle);
    newUser.setUnindexedProperty("firstName", userFName);
    newUser.setProperty("lastName", userLName);
    newUser.setProperty("address1", new Text(userAddress1));
    newUser.setProperty("address2", new Text(userAddress2));
    newUser.setProperty("city", uForm.getFirstValue("city"));
    newUser.setProperty("state", uForm.getFirstValue("state"));
    newUser.setUnindexedProperty("cell", new PhoneNumber(cellPhone));
    String clearPwd = uForm.getFirstValue("password");
    
    LOGGER.info("Creating user " + dsKey.getName());

    if (clearPwd != null) {
      byte[] encryptedPwd = this.generateCipherPassword(clearPwd, errorList);
      newUser.setProperty("password", new ShortBlob(encryptedPwd));
    }
   
    //***Report the status
    String message = "Successfully inserted new user " + dsKey.getName();

    //***Add the user object to persistent storage
    if (errorList.length() == 0) {
      ds.put(newUser);
      //*** E-mail the user about the add.
      String from = "info@patienteducation-rheumatology.com";
      String fromDisplay = "Rheumatology Patient Education Tool Admin";
      String toDisplay = userTitle + " " +
                         userFName + " " +
                         userLName;
      String subject = "Your Rhuematology Patient Education App";
      String emailMessage = "Thank you for visiting our Booth at ACR" +
                            "and registering for the Rheumatology Patient Education App.  Here is the " +
                            "link to download the patient education tool.  Once you open " +
                            "the tool, we recommend you save a shortcut to your home screen for " +
                            "easy access and full viewing";
      emailMessage = emailMessage + "<br /><br />";
      String url = SystemUtils.getAppURL();
      emailMessage = emailMessage + "<a href='" + url + "'>" +
                     url + "</a>";
      if (SIGN_UP_EMAIL) {
        MailUtils mailSender = new MailUtils();
        mailSender.sendMail(from, fromDisplay, userEmail, toDisplay, subject, emailMessage);
      }
    }

    JSONObject obj = new JSONObject();
    try {
    	
      if (errorList.length() > 0) {
        createUserStatus = "FAILED";
        obj.put("errors", errorList);
      }
    } catch (JSONException ex) {
      message = "Error converting error list to JSON: " + ex.toString();
    }

    JsonRepresentation a = this.getJsonRepresentation(createUserStatus, message, obj);

    return a;
  }

  @Put
  public JsonRepresentation updateUser(Representation user) {
    Form uForm = new Form(user);
    String updateUserStatus = "SUCCESS";
    JSONArray errorList = new JSONArray();
    
    //***Read the user information from the request
    String userEmail = uForm.getFirstValue("email");
    
    //***Set the success message as default.
    String message = "User " + userEmail + " successfully updated.";
    Key dsKey = KeyFactory.createKey("Users", userEmail);
    LOGGER.info("Updating user " + dsKey.getName());
    Entity updateUser = null;
    try {
      updateUser = ds.get(dsKey);
    } catch (EntityNotFoundException ex) {
      LOGGER.warning("User " + userEmail + " does not exist.");
      message = "Update failed. The user did not exist.";
      updateUserStatus = "FAILURE";
    }
 
    String title = uForm.getFirstValue("title");
    String firstName = uForm.getFirstValue("firstname");
    String lastName = uForm.getFirstValue("lastname");
    String address1 = uForm.getFirstValue("address1");
    String address2 = uForm.getFirstValue("address2");
    String city = uForm.getFirstValue("city");
    String state = uForm.getFirstValue("state");
    String cell = uForm.getFirstValue("cell");
    String newPwd = uForm.getFirstValue("newpwd");
    String notifyEmail = uForm.getFirstValue("notifyemail");
    String notifyText = uForm.getFirstValue("notifytext");
    String addFooter = uForm.getFirstValue("addfooter");
    
    //*** Make the update a sparse update by detecting filled in fields.
    if (title != null && !title.isEmpty()) {
      updateUser.setUnindexedProperty("title", title);
    }
    if (firstName != null && !firstName.isEmpty()) {
      updateUser.setUnindexedProperty("firstName", firstName);
    }
    if (lastName != null && !lastName.isEmpty()) {
      updateUser.setProperty("lastName", lastName);
    }
    if (address1 != null && !address1.isEmpty()) {
      updateUser.setProperty("address1", new Text(address1));
    }
    if (address2 != null && !address2.isEmpty()) {
      updateUser.setProperty("address2", new Text(address2));
    }
    if (city != null && !city.isEmpty()) {
      updateUser.setProperty("city", city);
    }
    if (state != null && !state.isEmpty()) {
      updateUser.setProperty("state", state);
    }
    if (cell != null && !cell.isEmpty()) {
      updateUser.setUnindexedProperty("cell", new PhoneNumber(cell));
    }
    if (notifyEmail != null && !notifyEmail.isEmpty()) {
      updateUser.setUnindexedProperty("notifyEmail", notifyEmail);
    }
    if (notifyText != null && !notifyText.isEmpty()) {
      updateUser.setUnindexedProperty("notifyText", notifyText);
    }
    if (addFooter != null && !addFooter.isEmpty()) {
      updateUser.setUnindexedProperty("addFooter", addFooter);
    }

    if (newPwd != null && !newPwd.isEmpty() && updateUserStatus.equals("SUCCESS")) {
      //*** Check if the password field was given.  If so, change it.
      byte[] encryptedPwd = this.generateCipherPassword(newPwd, errorList);
      updateUser.setProperty("password", new ShortBlob(encryptedPwd));
      if (encryptedPwd == null) {
        LOGGER.severe("Change password failed for user " + userEmail);
        message = "Password change failed for user " + userEmail;
        updateUserStatus = "FAILURE";
      }

      MailUtils mailSender = new MailUtils();
      //*** Compose an e-mail message telling the user that the password was changed.
      String htmlMessage = "<strong>Your password for the Patient Education Rheumatology App " +
                           " has been changed. If this change was not approved or requested " +
                           " by you, please contact the help desk at " +
                           " help@patienteduction-rheumatology.com." +
                           "</strong><br />";
      htmlMessage += "<br />Your new Patient Education Rheumatology App password is: " + newPwd;
      String subject = "Your Patient Education Rheumatology App password has changed!";
      mailSender.sendMail(MailUtils.EMAIL_FROM,
                          MailUtils.EMAIL_FROM_DISPLAY,
                          userEmail,
                          getUserFullName(updateUser),
                          subject,
                          htmlMessage);
    }

    ds.put(updateUser);

    JsonRepresentation a = this.getJsonRepresentation(updateUserStatus, message, null);
    return a;
  }

  private byte[] generateCipherPassword(String clearPassword, JSONArray errorList) {
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
      errorList.put("Could not save password!");
    }
    return encryptedPwd;
  }

  private String getUserFullName(Entity u) {
    String uTitle = (String)u.getProperty("title");
    String uFirstName = (String)u.getProperty("firstName");
    String uLastName = (String)u.getProperty("lastName");
    String uFullName = (String)u.getKey().getName();
    if (uFirstName != null && uLastName != null) {
      uTitle = ((uTitle != null) ? (uTitle + " ") : "");
      uFullName = uTitle + uFirstName + " " + uLastName;
    }
    return uFullName;
  }
    
}
