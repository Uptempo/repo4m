/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.user;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.medselect.server.BaseServerResource;
import com.medselect.util.Constants;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

/**
 *
 * @author Mike Gordon
 */
public class UserAuthServerResource extends BaseServerResource {

  private static final String PASSWORD_KEY = "PAZZETAMCRAEUUOP";
  private static final String[] OPTIONAL_PROPERTIES =
      {"title", "firstName", "lastName", "address1", "address2", "city", "state", "cell"};
  
  public UserAuthServerResource() {
    super();
    this.ignoreUptempoKey = true;
  }

  @Post
  public JsonRepresentation authenticateUser(Representation user) {
    Form uForm = new Form(user);
    boolean userAuthSuccess = true;
    String userAuthStatus = "SUCCESS";
    String message = "";

    String userEmail = uForm.getFirstValue("email");
    String userPassword = uForm.getFirstValue("password");
    //*** Read the user information from the request
    Key dsKey = KeyFactory.createKey("Users", userEmail.toLowerCase());

    LOGGER.info("Authenticating user " + userEmail);
   
    //*** Get the user entry.
    Entity userEntity = null;
    try {
      userEntity = ds.get(dsKey);
    } catch (EntityNotFoundException ex) {
      userAuthSuccess = false;
      LOGGER.warning("User " + userEmail + " does not exist.");
      message = "Login failed.  The username did not exist.";
    }

    //*** Encrypt the password entered by the user
    byte[] encryptedPwd = null;
    if (userAuthSuccess) {
      try {
        byte[] key = PASSWORD_KEY.getBytes();
        //*** Could generate NoSuchAlgorithmException.
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        //*** Could generate InvalidKeyException.
        cipher.init(Cipher.ENCRYPT_MODE, k);
        encryptedPwd = cipher.doFinal(userPassword.getBytes());
      } catch (Exception ex) {
        //***Log the error and add to the user output.
        LOGGER.severe("Error encrypting password: " + ex.toString());
        message = "Login failed.  There was an internal application error.";
        userAuthSuccess = false;
      }
    }

    //*** If successful so far, compare the encrypted passwords.
    if (userAuthSuccess) {
      ShortBlob savedUserPwd = (ShortBlob)userEntity.getProperty("password");
      if (Arrays.equals(savedUserPwd.getBytes(), encryptedPwd)) {
        //*** Success, do nothing.
        //*** Login success - generate a one time login key, send it back, and enable login.
        String loginKey = UUID.randomUUID().toString();
        //*** Save to memcache.
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        Expiration expiration = Expiration.byDeltaSeconds(Constants.USER_AUTH_KEY_CACHE_EXPIRATION);
        syncCache.put(Constants.MEMCACHE_LOGIN_KEY + "." + loginKey, loginKey, expiration);
        //*** Send back the login key.
        userEntity.setProperty("loginKey", loginKey);
      } else {
        userAuthSuccess = false;
        userAuthStatus = "FAILURE";
        message = "Login failed. The password did not match.";
      }
    } else {
      userAuthStatus = "FAILURE";
      message = "Login failed.  The password did not work.";
    }

    //*** Return the user information to set a client side cookie.
    JSONObject obj = new JSONObject();
    //*** Only assemble the JSON data if login was succesful.
    if (userAuthSuccess) {
      try {
        obj.put("email", userEntity.getKey().getName());
        if (userEntity.hasProperty("loginKey")) {
            obj.put("loginKey", userEntity.getProperty("loginKey"));
          }
        if (userEntity.hasProperty("title")) {
          obj.put("title", userEntity.getProperty("title"));
        }
        if (userEntity.hasProperty("firstName")) {
          obj.put("fname", userEntity.getProperty("firstName"));
        }
        if (userEntity.hasProperty("lastName")) {
          obj.put("lname", userEntity.getProperty("lastName"));
        }
        Text address;
        if (userEntity.hasProperty("address1")) {
          address = (Text)userEntity.getProperty("address1");
          obj.put("address1", address.getValue());
        }
        if (userEntity.hasProperty("address2")) {
          address = (Text)userEntity.getProperty("address2");
          obj.put("address2", address.getValue());
        }
        if (userEntity.hasProperty("city")) {
          obj.put("city", userEntity.getProperty("city"));
        }
        if (userEntity.hasProperty("state")) {
          obj.put("state", userEntity.getProperty("state"));
        }
        if (userEntity.hasProperty("cell")) {
          PhoneNumber cell = (PhoneNumber)userEntity.getProperty("cell");
          obj.put("cell", cell.getNumber());
        }
        if (userEntity.hasProperty("notifyEmail")) {
          obj.put("notifyemail", userEntity.getProperty("notifyEmail"));
        }
        if (userEntity.hasProperty("notifyText")) {
          obj.put("notifytext", userEntity.getProperty("notifyText"));
        }
        if (userEntity.hasProperty("addFooter")) {
          obj.put("addfooter", userEntity.getProperty("addFooter"));
        }
      } catch (JSONException ex) {
        userAuthStatus = "FAILURE";
        message = "There was an internal error: " + ex.toString();
      } catch (NullPointerException ex) {
        message = "There was an internal error: " + ex.toString();
      }
    }
    JsonRepresentation a = this.getJsonRepresentation(userAuthStatus, message, obj);

    return a;
  }    
}
