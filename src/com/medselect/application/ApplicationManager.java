/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.application;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.collect.ImmutableMap;
import com.medselect.audit.AuditLogManager;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import com.medselect.util.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages Application entities.
 * @author Mike Gordon (mgordon)
 */
public class ApplicationManager extends BaseManager {
  private static final String APPLICATION_KEY_MEMCACHE = "uptempoAppKeys";
  public static final String APPLICATION_ENTITY_NAME = "Application";
  public static final String APPLICATION_DISPLAY_NAME = "Application";
  public static final Map<String, BaseManager.FieldType> APPLICATION_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("appName", BaseManager.FieldType.STRING)
          .put("appDescription", BaseManager.FieldType.STRING)
          .put("url", BaseManager.FieldType.STRING)
          .put("accessKey", BaseManager.FieldType.STRING)
          .build();
 
  public ApplicationManager() {
    super(APPLICATION_STRUCTURE, APPLICATION_ENTITY_NAME, APPLICATION_DISPLAY_NAME);
  }
  
  public boolean isValidKey(String key) {
    boolean isValid = false;
    Expiration expiration = Expiration.byDeltaSeconds(Constants.KEY_CACHE_EXPIRATION);

    //*** First check memcache for the key.
    MemcacheService cacheService = MemcacheServiceFactory.getMemcacheService();
    if (cacheService.contains(APPLICATION_KEY_MEMCACHE)) {
      List <String> keyList = (List<String>)cacheService.get(APPLICATION_KEY_MEMCACHE);
      for (String storedKey : keyList) {
        if (key.equals(storedKey)) {
          isValid = true;
        }
      }
    } else {
      //*** Get all of the keys.
      List<String> keyStorageList = new ArrayList<String>();
      Query q = new Query(ApplicationManager.APPLICATION_ENTITY_NAME);
      PreparedQuery pq = ds.prepare(q);
      for (Entity app : pq.asIterable()) {
        String keyCandidate = (String)app.getProperty("accessKey");
        if (key.equals(keyCandidate)) {
          isValid = true;
        }
        keyStorageList.add(keyCandidate);
      }
      cacheService.put(APPLICATION_KEY_MEMCACHE, keyStorageList, expiration);
    }
    return isValid;
  }
  
  public ReturnMessage regenerateKey(String appKey) {
    String regenerateStatus = "SUCCESS";
    String message = "Application key sucessfully regenerated.";
    try {
      if (appKey != null) {
        //*** Regenerate the key.
        Key dsKey = KeyFactory.stringToKey(appKey);
        Entity appToUpdate = ds.get(dsKey);
        appToUpdate.setProperty("accessKey", UUID.randomUUID().toString());
        ds.put(appToUpdate);
        //*** Clear the memcache.
        MemcacheService cacheService = MemcacheServiceFactory.getMemcacheService();
        cacheService.delete(APPLICATION_KEY_MEMCACHE);
      }
    } catch (Exception ex) {
      message = "Application key generation failed: " + ex.toString();
      LOGGER.severe(message);
      regenerateStatus = "FAILURE";
    }

    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response = builder.status(regenerateStatus).message(message).build();
    return response;
  }
}
