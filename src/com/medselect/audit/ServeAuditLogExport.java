/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.audit;

import com.medselect.common.BaseManager;
import com.google.common.collect.ImmutableMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import com.medselect.common.ReturnMessage;
import org.json.JSONException;
import java.io.IOException;
import com.medselect.common.ServeExportImport;

/**
 * Class to serve AuditLog export.
 * @author karlo.smid@gmail.com
 */
public class ServeAuditLogExport extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(ServeAuditLogExport.class.getName());

/**
   * Serves AuditLog export from the database.
   * @param request HttpServletRequest is http export request object.
   * @param response HttpServletResponse is http reponse object which will contain export file.
   * @return
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    String clientKey = request.getParameter("authKey");
    String extension = request.getParameter("extension");
    String delimiter = request.getParameter("delimiter");
    if(delimiter == null || delimiter.isEmpty()){
      delimiter = "\t";
    }
    if(extension == null || extension.isEmpty()){
      extension = "json";
    }
    ServeExportImport baseExport = new ServeExportImport();
    if(!baseExport.isApplicationKeyValid(clientKey)) {
      response.sendError(response.SC_UNAUTHORIZED);
    }
    try {
      String AUDITLOG_ENTITY_NAME = "AuditLog";
      String AUDITLOG_DISPLAY_NAME = "Audit Log";
      Map<String, BaseManager.FieldType> AUDITLOG_STRUCTURE =
        new ImmutableMap.Builder<String,BaseManager.FieldType>()
            .put("appCode", BaseManager.FieldType.STRING)
            .put("eventCode", BaseManager.FieldType.STRING)
            .put("eventDescription", BaseManager.FieldType.STRING)
            .put("remoteIP", BaseManager.FieldType.STRING)
            .put("remoteUser", BaseManager.FieldType.STRING)
            .put("eventTime", BaseManager.FieldType.DATE)
            .build();
      BaseManager auditlogManager = new BaseManager(AUDITLOG_STRUCTURE, AUDITLOG_ENTITY_NAME, AUDITLOG_DISPLAY_NAME);
      Map<String,String> auditlogParams= new HashMap<String,String>();
      ReturnMessage jsonResponse = auditlogManager.doRead(auditlogParams, null);
      String exportData = "";
      if (!jsonResponse.getStatus().equals("FAILURE")){
        JSONArray jsonArray = null;
        JSONObject jsonElement = null;
        jsonArray = jsonResponse.getValue().getJSONArray( "values" );
        if(extension.equalsIgnoreCase("csv")){
          exportData = exportData + "appCode" + delimiter + "eventCode" + delimiter + "eventDescription" + delimiter + "remoteIP" + delimiter + "remoteUser" + delimiter + "eventTime" + delimiter + "createdBy" + delimiter + "createDate" + delimiter + "modifiedBy" + delimiter + "modifyDate" + delimiter + "key" + "\n";
          for(int index = 0;index < jsonArray.length(); index++){
            jsonElement = jsonArray.getJSONObject(index);
            try{
              exportData = exportData + jsonElement.getString("appCode") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }
            try{
              exportData = exportData + jsonElement.getString("eventCode") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }
            try{
              exportData = exportData + jsonElement.getString("eventDescription") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }try{
              exportData = exportData + jsonElement.getString("eventDescription") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }try{
              exportData = exportData + jsonElement.getString("remoteIP") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }try{
              exportData = exportData + jsonElement.getString("remoteUser") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }try{
              exportData = exportData + jsonElement.getString("eventTime") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }try{
              exportData = exportData + jsonElement.getString("createdBy") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }try{
              exportData = exportData + jsonElement.getString("createDate") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }try{
              exportData = exportData + jsonElement.getString("modifiedBy") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }try{
              exportData = exportData + jsonElement.getString("modifyDate") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }try{
              exportData = exportData + jsonElement.getString("key") + delimiter;
            }catch(JSONException jsonEx){
              LOGGER.info(jsonEx.getMessage());
              exportData = exportData + delimiter;
            }
            exportData = exportData + "\n";
          }
        }
        else{
          exportData = jsonArray.toString();
        }
      }
      BlobKey blobKey = baseExport.doGetBlobKey(exportData);
      if(extension.equalsIgnoreCase("csv")){
        response.setHeader("Content-Disposition", "attachment; filename=auditlogExport.csv");
      }else{
        response.setHeader("Content-Disposition", "attachment; filename=auditlogExport.json");
      }
      blobstoreService.serve(blobKey, response);
    } catch(IOException e) {
      LOGGER.info(e.getMessage());
      response.sendError(response.SC_INTERNAL_SERVER_ERROR);
    } catch(JSONException e) {
      LOGGER.info(e.getMessage());
      response.sendError(response.SC_INTERNAL_SERVER_ERROR);
    } catch(Exception e) {
      LOGGER.info(e.getMessage());
      response.sendError(response.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
