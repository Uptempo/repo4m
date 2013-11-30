/*
 * Copyright 2013 Uptempo Group Inc.
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
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesService;
import java.util.HashMap;
import java.util.Map;
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

  public AttachmentManager() {
    super(ATTACHMENT_STRUCTURE, ATTACHMENT_ENTITY_NAME, ATTACHMENT_DISPLAY_NAME);
  }

  /**
   * Inserts Attachment to an entity in the datastore.
   * @param params Parameters for insert.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertAttachment(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the Image value information from the request
    String entityKey = params.get("entityKey");
    String category = params.get("category");

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
    return this.doCreate(params, false);
  }
}
