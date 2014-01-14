/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.imageservice;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesServiceFailureException;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Class to upload image values.
 * @author karlo.smid@gmail.com
 */
public class AttachmentManager extends BaseManager {
  public static final String ATTACHMENT_ENTITY_NAME = "Attachment";
  public static final String ATTACHMENT_DISPLAY_NAME = "Attachment";
  public static final Map<String, BaseManager.FieldType> ATTACHMENT_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("blobKey", BaseManager.FieldType.STRING)
          .put("fileName", BaseManager.FieldType.STRING)
          .put("entityKey", BaseManager.FieldType.STRING)
          .put("category", BaseManager.FieldType.STRING)
          .build();
  private ImagesService imageService = ImagesServiceFactory.getImagesService();
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  public AttachmentManager() {
    super(ATTACHMENT_STRUCTURE, ATTACHMENT_ENTITY_NAME, ATTACHMENT_DISPLAY_NAME);
  }

  /**
   * Gets attachments given an Entity key.  Returns an attachment URL for the current instance.
   * @param entityKeyVal
   * @return A {@link ReturnMessage} with the attachment keys/URLs connected to the given entity.
   */
  public List<Entity> getAttachments(String entityKeyVal) {
    PreparedQuery results = getAttachmentFromEntity(entityKeyVal, false);
    List <Entity> returnVal = new ArrayList();
    for (Entity result : results.asIterable()) {
      //*** Set the URL based on the current instance, if this an image.
      try {
        String blobKeyVal = (String)result.getProperty("blobKey");
        BlobKey blobKey = new BlobKey(blobKeyVal);
        ServingUrlOptions servingUrlOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
        String imageUrl = imageService.getServingUrl(servingUrlOptions);
        result.setProperty("url", imageUrl);
      } catch (ImagesServiceFailureException ex) {
        //*** Do nothing, this must not be an image.
      }
      returnVal.add(result);
    }
    LOGGER.info("Get Attachments: Returned " + returnVal.size() + " attachments.");
    return returnVal;
  }
  
  /**
   * Gets the attachments from an Entity key.
   * @param entityKey The key of the entity to search in the attachment table.
   * @return Query results containing the list of attachment entities.
   */
  private PreparedQuery getAttachmentFromEntity(String entityKey, boolean keysOnly) {
    Query q = new Query(ATTACHMENT_ENTITY_NAME);
    if (keysOnly) {
      q.setKeysOnly();
    }
    FilterPredicate fp = new FilterPredicate("entityKey", Query.FilterOperator.EQUAL, entityKey);
    q.setFilter(fp);
    return ds.prepare(q);
  }
  
  /**
   * Inserts Attachment to an entity in the datastore.
   * @param params Parameters for insert.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertAttachment(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //*** Read the Attachment value information from the request
    String entityKey = params.get("entityKey");
    String category = params.get("category");
    String replace = params.get("replace");
    boolean replaceForEntity = false;
    if (replace != null && replace.equalsIgnoreCase("true")) {
      replaceForEntity = true;
      params.remove("replace");
    }

    if (entityKey == null || entityKey.isEmpty()) {
      message = "Entity Key is required!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    }
    if (category == null || category.isEmpty()){
      message = "Category is required!";
      insertValueStatus = "FAILURE";
      return createReturnMessage(message, insertValueStatus);
    }
    
    //*** If the replace flag has been set, delete all attachments for the provided entity.
    if (replaceForEntity) {
      //*** Get the images by entity key and delete the old ones.
      PreparedQuery results = getAttachmentFromEntity(entityKey, true);
      for (Entity result : results.asIterable()) {
        BlobKey fileKey = new BlobKey((String)result.getProperty("blobKey"));
        blobstoreService.delete(fileKey);
        ds.delete(result.getKey());
      }
    }
    return this.doCreate(params, false);
  }
  
  public String getAttachmentUrl (String blobKeyVal) {
    BlobKey blobKey = new BlobKey(blobKeyVal);
    ServingUrlOptions servingUrlOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
    return imageService.getServingUrl(servingUrlOptions);
  }
  
  public ReturnMessage getAttachmentsForEntity(String entityKeyVal) {
    List<Entity> attachments = this.getAttachments(entityKeyVal);
    JSONArray attachmentArray = new JSONArray();
    JSONObject wrapper = new JSONObject();
    try {
      for (Entity e : attachments) {
        JSONObject item = new JSONObject(e.getProperties());
        attachmentArray.put(item);
      }
      wrapper.put("value", attachmentArray);
    } catch (JSONException ex) {
      LOGGER.severe("Error converting JSON for attachment array: " + ex.toString());
    }
    
    String message = "Returned " + attachments.size() + " attachments.";
    String status = "SUCCESS";
    ReturnMessage r = new ReturnMessage
        .Builder().status(status).message(message).value(wrapper).build();
    return r;
  }
}
