/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.config;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.util.logging.Logger;

import com.medselect.config.ConfigManager;
import com.medselect.common.ReturnMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.HashMap;

/**
 * Class to manage import Config data.
 * @author karlo.smid@gmail.com, boris.kuzmic@gmail.com
 */
public class ImportConfigData extends HttpServlet {

  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(ImportConfigData.class.getName());

  /**
   * Imports Config data into the database.
   * @param request HttpServletRequest is http request object which contains file properties and file itself.
   * @param response HttpServletResponse is http reponse object which will contain link to uploaded file.
   * @return
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {

    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(request);
    BlobKey blobKey = blobs.get("myFile");
    
    if (blobKey == null) {
      response.sendRedirect("/server/include/config-import-data.jsp?res=failed");
    } else {
      // Now read from the file using the Blobstore API
      String importedData = new String(blobstoreService.fetchData(blobKey, 0, BlobstoreService.MAX_BLOB_FETCH_SIZE-1));
      boolean importSuccessful = true;
      try{
        JSONArray configsAsJsonArray = new JSONArray(importedData);
        JSONObject jsonElement = null;
        JSONArray listTitles = null;
        JSONArray listSpecialties = null;
        ConfigManager configManager = new ConfigManager();
        for(int index = 0;index < configsAsJsonArray.length(); index++){
          jsonElement = configsAsJsonArray.getJSONObject(index);
          Map<String,String> configParams= new HashMap<String,String>();
          
          configParams.put("text", jsonElement.getString("text"));
          configParams.put("description", jsonElement.getString("description"));
          configParams.put("imageid", jsonElement.getString("valueImageId"));
          configParams.put("user", jsonElement.getString("modifiedBy"));
          configParams.put("modifyDate", jsonElement.getString("modifyDate"));
          configParams.put("name", jsonElement.getString("name"));
          configParams.put("value", jsonElement.getString("value"));
          configParams.put("appCode", jsonElement.getString("appCode"));
          configParams.put("createDate", jsonElement.getString("createDate"));
          configParams.put("createdBy", jsonElement.getString("createdBy"));
          ReturnMessage responseForInsert = configManager.insertConfigValue(configParams);
          if (responseForInsert.getStatus().equals("FAILURE")){
            importSuccessful = false;
          }
        }
      } catch(JSONException jsonEx){
        LOGGER.severe(jsonEx.getMessage());
        importSuccessful = false;
      }
      if (importSuccessful) {
        response.sendRedirect("/server/include/config-import-data.jsp?res=success");
      } else {
        response.sendRedirect("/server/include/config-import-data.jsp?res=failed");
      }
    }
  }
}
