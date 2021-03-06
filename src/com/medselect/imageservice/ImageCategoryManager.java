/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.imageservice;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.medselect.application.ApplicationManager;
import java.util.List;

import java.util.Map;


/**
 * Class to manage image category values.
 * @author karlo.smid@gmail.com
 */
public class ImageCategoryManager extends BaseManager {
  public static final String IMAGECATEGORY_ENTITY_NAME = "ImageCategory";
  public static final String IMAGECATEGORY_DISPLAY_NAME = "Image category";
  public static final Map<String, BaseManager.FieldType> IMAGECATEGORY_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("name", BaseManager.FieldType.STRING)
          .put("description", BaseManager.FieldType.TEXT)
          .put("accessCode", BaseManager.FieldType.STRING)
          .put("ancestor", BaseManager.FieldType.STRING)
          .build();

  public ImageCategoryManager() {
    super(IMAGECATEGORY_STRUCTURE, IMAGECATEGORY_ENTITY_NAME, IMAGECATEGORY_DISPLAY_NAME);
  }

/**
   * Inserts Image category value into the database.
   * @param params Map request parameters in form parameter name:parameter value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the Image category value information from the request
    String name = params.get("name");
    String description = params.get("description");
    String applicationId = params.get("applicationId");
    params.remove("applicationId");
    String user = params.get("user");

    if(name == null || name.isEmpty()){
      message = "name is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    } else if(description == null || description.isEmpty()){
      message = "description is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    } else if(applicationId == null || applicationId.isEmpty()){
      message = "applicationId is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    } else if(user == null || user.isEmpty()){
      message = "user email is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    }
    if(!this.dataValidator.isEmail(user)){
        message = "email does not have valid syntax!";
        insertValueStatus = "FAILURE";
        return createReturnMessage(message, insertValueStatus);
    }
    String accessCode = java.util.UUID.randomUUID().toString();
    params.put("accessCode", accessCode);
    ReturnMessage createResponse = this.doCreate(params, false, KeyFactory.stringToKey(applicationId));
    Document imageCategoryDocument = Document.newBuilder().setId(accessCode)
      .addField(Field.newBuilder().setName("name").setText(name))
      .addField(Field.newBuilder().setName("description").setText(description))
      .addField(Field.newBuilder().setName("entityId").setText(createResponse.getKey()))
      .build();
    Index index = getIndex("imageCategory");
    index.put(imageCategoryDocument);
    return createResponse;
  }

/**
   * Update Image category value from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param gaeKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage updateValue(Map<String, String> params, String gaeKey) {
    String updateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
    String user = params.get("user");
    String name = params.get("name");
    String description = params.get("description");
    if (user == null || user.isEmpty()) {
      message = "user email is mandatory parameter!";
      updateStatus = "FAILURE";
    } else if (!this.dataValidator.isEmail(user)) {
      message = "user email does not have valid syntax!";
      updateStatus = "FAILURE";
    }
    Entity updateValue = null;
    if (gaeKey != null) {
      dsKey = KeyFactory.stringToKey(gaeKey);
      try {
        updateValue = ds.get(dsKey);
        params.put("key", gaeKey);
      } catch (EntityNotFoundException ex) {
        message = "Image category value identified by " + gaeKey + " does not exist.";
        LOGGER.warning(message);
        updateStatus = "FAILURE";
      }
    }
    if (updateStatus.equals("FAILURE")) {
      message = "Image category key is required.";
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(updateStatus).message(message).value(null).build();
      return response;
    } else {
      ReturnMessage updateResponse = this.doUpdate( params );
      Document imageCategoryDocument = Document.newBuilder().setId((String) updateValue.getProperty("accessCode"))
          .addField(Field.newBuilder().setName("name").setText(name))
          .addField(Field.newBuilder().setName("description").setText(description))
          .addField(Field.newBuilder().setName("entityId").setText(gaeKey))
          .build();
      Index index = getIndex("imageCategory");
      index.put(imageCategoryDocument);
      return updateResponse;
    }
  }
  
/**
 * Returns Image category values from the database based on value GAE key.
 * @param params Map request parameters in form parameter name:parameter value.
 * @param itemKey String is unique GAE entity key value.
 * @return ReturnMessage JSON format message with operation status, info message and Image category data.
 */
  public ReturnMessage readValues(Map<String, String> params, String itemKey) {
    //*** If this request was for a single value, return that request.
    if (itemKey != null) {
      return this.doGet(itemKey);
    }
    if (params.isEmpty()){
      return this.doRead(params, itemKey);
    }
    if (params.containsKey("applicationKey")) {
      String applicationKey = params.get("applicationKey");
      try {
        return this.doRead(
            params,
            null,
            KeyFactory.createKey(ApplicationManager.APPLICATION_ENTITY_NAME, applicationKey));
      } catch (Exception ex) {
        return new ReturnMessage.Builder().status("FAILURE")
            .message("No categories available.").build();
      }
    }
    return new ReturnMessage.Builder().status("FAILURE")
        .message("No categories available.").build();
  }

/**
   * Deletes image category values from the database based on value GAE key.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
*/
  public ReturnMessage deleteValue(String itemKey) {
    Key dsKey = null;
    Entity deleteValue = null;
    if (itemKey != null) {
      dsKey = KeyFactory.stringToKey(itemKey);
      try {
        deleteValue = ds.get(dsKey);
        getIndex("imageCategory").delete((String)deleteValue.getProperty("accessCode"));
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("Image category value identified by " + itemKey + " does not exist.");
      }
      //*** Get the images associated with this category and delete them.
      ImageManager iManager = new ImageManager();
      List<Entity> imagesForCategory = iManager.getImagesForCategory(itemKey);
      LOGGER.info("Deleting " +
          imagesForCategory.size() +
          " images for category " +
          deleteValue.getProperty("name"));
      for (Entity image : imagesForCategory) {
        String imageKeyVal = KeyFactory.keyToString(image.getKey());
        //*** Make sure the image and the blob are deleted.
        ReturnMessage result = iManager.deleteValue(imageKeyVal);
        if (!result.getStatus().equalsIgnoreCase("SUCCESS")) {
          LOGGER.warning("Could not delete image with key: " + imageKeyVal + " during category delete.");
        }
      }
      return this.doDelete(itemKey, "name");
    } else {
      return new ReturnMessage.Builder()
          .status("FAILURE").message("No category key provided for deletion!").build();
    }
  }
}
