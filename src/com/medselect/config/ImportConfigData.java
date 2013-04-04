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
import com.medselect.common.ServeExportImport;


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
    
    String clientKey = request.getParameter("authKey");
    ServeExportImport baseExport = new ServeExportImport();
    if(!baseExport.isApplicationKeyValid(clientKey)) {
      response.sendError(response.SC_UNAUTHORIZED);
    }
    if (blobKey == null) {
      response.sendRedirect("/server/include/config-import-data.jsp?res=failed&authKey="+clientKey);
    } else {
      // Now read from the file using the Blobstore API
      String importedData = new String(blobstoreService.fetchData(blobKey, 0, BlobstoreService.MAX_BLOB_FETCH_SIZE-1));
      boolean importSuccessful = true;
      try{
        JSONArray configsAsJsonArray = new JSONArray(importedData);
        JSONObject jsonElement = null;
        ConfigManager configManager = new ConfigManager();
        for(int index = 0;index < configsAsJsonArray.length(); index++){
          jsonElement = configsAsJsonArray.getJSONObject(index);
          Map<String,String> configParams= new HashMap<String,String>();
          try{ 
            configParams.put("text", jsonElement.getString("text"));
          }catch(JSONException jsonEx){
            LOGGER.info(jsonEx.getMessage());
            configParams.put("text", "");
          }try{
            configParams.put("description", jsonElement.getString("description"));
          }catch(JSONException jsonEx){
            LOGGER.info(jsonEx.getMessage());
            configParams.put("description", "");
           }
          try{
            configParams.put("imageid", jsonElement.getString("valueImageId"));
          }catch(JSONException jsonEx){
            LOGGER.info(jsonEx.getMessage());
            configParams.put("imageid", "");
          }
          try{
            configParams.put("user", jsonElement.getString("createdBy"));
          }catch(JSONException jsonEx){
            LOGGER.info(jsonEx.getMessage());
            configParams.put("user", "");
          }
          try{
            configParams.put("name", jsonElement.getString("name"));
          }catch(JSONException jsonEx){
            LOGGER.info(jsonEx.getMessage());
            configParams.put("name", "");
          }
          try{
            configParams.put("value", jsonElement.getString("value"));
          }catch(JSONException jsonEx){
            LOGGER.info(jsonEx.getMessage());
            configParams.put("value", "");
          }
          try{
            configParams.put("appCode", jsonElement.getString("appCode"));
          }catch(JSONException jsonEx){
            LOGGER.info(jsonEx.getMessage());
            configParams.put("appCode", "");
          }
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
        response.sendRedirect("/server/include/config-import-data.jsp?res=success&authKey="+clientKey);
      } else {
        response.sendRedirect("/server/include/config-import-data.jsp?res=failed&authKey="+clientKey);
      }
    }
  }
}
