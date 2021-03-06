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
import java.util.HashMap;
import java.util.Map;
import com.medselect.common.ReturnMessage;
import org.json.JSONException;
import java.io.IOException;
import com.medselect.common.ServeExportImport;

/**
 * Class to serve Audit export.
 * @author karlo.smid@gmail.com
 */
public class ServeAuditExport extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(ServeAuditExport.class.getName());

/**
   * Serves Audit export from the database.
   * @param request HttpServletRequest is http export request object.
   * @param response HttpServletResponse is http reponse object which will contain export file.
   * @return
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    String clientKey = request.getParameter("authKey");
    ServeExportImport baseExport = new ServeExportImport();
    if(!baseExport.isApplicationKeyValid(clientKey)) {
      response.sendError(response.SC_UNAUTHORIZED);
    }
    try {
      String AUDIT_ENTITY_NAME = "Audit";
      String AUDIT_DISPLAY_NAME = "Audit";
      Map<String, BaseManager.FieldType> AUDIT_STRUCTURE =
        new ImmutableMap.Builder<String,BaseManager.FieldType>()
            .put("appCode", BaseManager.FieldType.STRING)
            .put("eventCode", BaseManager.FieldType.STRING)
            .put("description", BaseManager.FieldType.STRING)
            .put("severity", BaseManager.FieldType.LONG)
            .put("alertThreshold", BaseManager.FieldType.LONG)
            .put("alertType", BaseManager.FieldType.STRING)
            .put("alertEmail", BaseManager.FieldType.STRING)
            .put("alertPhone", BaseManager.FieldType.PHONE_NUMBER)                            
            .build();
      BaseManager auditManager = new BaseManager(AUDIT_STRUCTURE, AUDIT_ENTITY_NAME, AUDIT_DISPLAY_NAME);
      Map<String,String> auditParams= new HashMap<String,String>();
      ReturnMessage jsonResponse = auditManager.doRead(auditParams, null);
      String exportData = "";
      if (!jsonResponse.getStatus().equals("FAILURE")){
        JSONArray jsonArray = null;
        jsonArray = jsonResponse.getValue().getJSONArray( "values" );
        exportData = jsonArray.toString();
      }
      BlobKey blobKey = baseExport.doGetBlobKey(exportData);
      response.setHeader("Content-Disposition", "attachment; filename=auditExport.json");
      blobstoreService.serve(blobKey, response);
    } catch(IOException e) {
      LOGGER.info(e.getMessage());
      response.sendError(response.SC_INTERNAL_SERVER_ERROR);
    } catch(JSONException e) {
      LOGGER.info(e.getMessage());
      response.sendError(response.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
