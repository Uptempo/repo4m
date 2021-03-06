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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Class to upload image values.
 * @author karlo.smid@gmail.com
 */
public class ImageManager extends BaseManager {
  public static final String IMAGE_ENTITY_NAME = "Image";
  public static final String IMAGE_DISPLAY_NAME = "Image";
  public static final Map<String, BaseManager.FieldType> IMAGE_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("url", BaseManager.FieldType.STRING)
          .put("filename", BaseManager.FieldType.STRING)
          .put("caption", BaseManager.FieldType.STRING)
          .put("blobKey", BaseManager.FieldType.STRING)
          .build();

  public ImageManager() {
    super(IMAGE_STRUCTURE, IMAGE_ENTITY_NAME, IMAGE_DISPLAY_NAME);
  }

  /**
   * Inserts Image value into the database.
   * @param params Map request parameters in form parameter name:parameter value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the Image value information from the request
    String caption = params.get("caption");
    String user = params.get("user");
    String categoryId = params.get("categoryId");
    params.remove("categoryId");

    if(caption == null || caption.isEmpty()){
      message = "caption is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    }
    if(categoryId == null || categoryId.isEmpty()){
      message = "categoryId is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    }
    if(user == null || user.isEmpty()){
      message = "user email is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    }
    if(!this.dataValidator.isEmail(user)){
      message = "email does not have valid syntax!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    }
    return this.doCreate(params, false, KeyFactory.stringToKey(categoryId));
  }

/**
   * Update Image value from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage updateValue(Map<String, String> params, String itemKey) {
    String updateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
    
    String categoryId = params.get("categoryId");
    String caption = params.get("caption");
    String user = params.get("user");
    
    Entity updateValue = null;

    if (itemKey != null && !itemKey.isEmpty()) {
      dsKey = KeyFactory.stringToKey(itemKey);
      try {
        updateValue = ds.get(dsKey);
        params.put("key", itemKey);
      } catch (EntityNotFoundException ex) {
        message = "Image value identified by " + itemKey + " does not exist.";
        LOGGER.warning(message);
        updateStatus = "FAILURE";
        return createReturnMessage(message, updateStatus);
      }
    } else {
      message = "gae key is mandatory update parameter!";
      LOGGER.warning(message);
      updateStatus = "FAILURE";
      return createReturnMessage(message, updateStatus);
    }
    if(user == null || user.isEmpty()){
      message = "user email is mandatory parameter!";
      updateStatus = "FAILURE";
      return createReturnMessage(message, updateStatus);
    }
    if(!this.dataValidator.isEmail(user)){
      message = "email does not have valid syntax!";
      updateStatus = "FAILURE";
      return createReturnMessage(message, updateStatus);
    }
    if(caption == null){
      params.remove("caption");
      Object captionInDatabase = updateValue.getProperty("caption");
      if(captionInDatabase != null){
        caption = (String) captionInDatabase;
      }
    }
    if(categoryId != null && !categoryId.isEmpty()){
      if(!categoryId.equals(KeyFactory.keyToString(updateValue.getParent()))){
        Map<String, String> keepParams = new HashMap<String,String>();
        keepParams.put("url", (String)updateValue.getProperty("url"));
        keepParams.put("filename", (String)updateValue.getProperty("filename"));
        keepParams.put("caption", caption);
        keepParams.put("blobKey", (String)updateValue.getProperty("blobKey"));
        keepParams.put("user", user);
        keepParams.put("categoryId", categoryId);
        ReturnMessage deleteResponse = this.doDelete(itemKey, "caption");
        if(deleteResponse.getStatus().equalsIgnoreCase("FAILURE")){
          return deleteResponse;
        }
        ReturnMessage insertResponse = insertValue(keepParams);
        return insertResponse;
      }
    }
    //***Set the updated values.
    return this.doUpdate(params);
  }
  
  /**
   * Update/create image from the database based on value GAE key.
   * @param photoUrl String image url for retrival from GAE blobstore
   * @param fileName String image file name
   * @param photoKey String image blob key.
   * @param imageKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage updateCreateImage(
      String photoUrl, String fileName, String photoKey, String imageKey, String categoryKey) {
    String UpdateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
    Entity updateValue = null;

    if (imageKey != null) {
      dsKey = KeyFactory.stringToKey(imageKey);
      try {
        updateValue = ds.get(dsKey);
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("Image value identified by " + imageKey + " does not exist.");
        message = "Image value identified by " + imageKey + " does not exist.";
        UpdateStatus = "FAILURE";
        return createReturnMessage(message, UpdateStatus);
      }
    }
    if (photoKey == null || photoKey.isEmpty()) {
      message = "photo database key is mandatory parameter!";
      UpdateStatus = "FAILURE";
      return createReturnMessage(message, UpdateStatus);
    }
    
    if (updateValue != null) {
      String currentImage = (String) updateValue.getProperty("blobKey");
      if (currentImage != null) {
        ReturnMessage operationResult = deleteImageBy(currentImage);
        if (operationResult.getStatus().equals("FAILURE")){
          return operationResult;
        }
      }
    }
    Map<String,String> params= new HashMap<String,String>();
    params.put("blobKey", photoKey);
    params.put("key", imageKey);
    params.put("url", photoUrl);
    params.put("filename", fileName);
    //*** If this is a new image, assume the category key has been provided.
    if (imageKey == null) {
      return this.doCreate(params, false, KeyFactory.stringToKey(categoryKey));
    } else {
      return this.doUpdate(params);
    }
  }

  /**
   * Deletes image based on image blob key.
   * @param  String image blob key.
   * @return ReturnMessage JSON format message with operation status, info message and image data.
   */
  protected ReturnMessage deleteImageBy(String imageKey){
    if(imageKey != null){
      BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      ImagesService imageService = ImagesServiceFactory.getImagesService();
      BlobKey blobKey = new BlobKey(imageKey);
      try{
        imageService.deleteServingUrl(blobKey);
        blobstoreService.delete(blobKey);
      } catch(com.google.appengine.api.blobstore.BlobstoreFailureException bsf){
        String message = bsf.getMessage();
        String UpdateStatus = "FAILURE";
        return createReturnMessage(message, UpdateStatus);
      } catch(java.lang.IllegalArgumentException iae){
        String message = iae.getMessage();
        String UpdateStatus = "FAILURE";
        return createReturnMessage(message, UpdateStatus);
      }
    }
    return createReturnMessage("", "SUCCESS");
  }
  
  /**
   * Returns image values from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status, info message and image data.
   */
  public ReturnMessage readValues(Map<String, String> params, String itemKey) {
    //*** If this request was for a single value, return that request.
    if (itemKey != null) {
      return this.doGet(itemKey);
    }
    //** lets parse the filter parameters
    String categoryId = params.get("categoryId");
    Key categoryIdAsKey = null;
    if(categoryId != null && !categoryId.isEmpty()){
      LOGGER.info("Reading image category for ID: " + categoryId);
      categoryIdAsKey = KeyFactory.stringToKey(categoryId);
    }
    return this.doRead(params, itemKey, categoryIdAsKey);
  }

  /**
   * Deletes image values from the database based on value GAE key.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage deleteValue(String itemKey) {
    //*** Get the image data.
    Key imageKey = KeyFactory.stringToKey(itemKey);
    try {
      Entity image = ds.get(imageKey);
      String blobKeyVal = (String)image.getProperty("blobKey");
      //*** Only delete the blob if it exists.
      if (blobKeyVal != null) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        BlobKey bsKey = new BlobKey(blobKeyVal);
        blobstoreService.delete(bsKey);
      }
      return this.doDelete(itemKey, "caption");
    } catch (EntityNotFoundException ex) {
      String message = "Image delete attempted, didn't exist.  Key: " + itemKey;
      LOGGER.warning(message);
      return new ReturnMessage.Builder().status("FAILURE").message(message).build();
    }
  }
  
  public List<Entity> getImagesForCategory(String categoryKeyVal) {
    List<Entity> images = new ArrayList();
    Key categoryKey = KeyFactory.stringToKey(categoryKeyVal);
    q = new Query().setAncestor(categoryKey);
    PreparedQuery pq = ds.prepare(q);
    for (Entity result : pq.asIterable()) {
      images.add(result);
    }
    return images;
  }
}
