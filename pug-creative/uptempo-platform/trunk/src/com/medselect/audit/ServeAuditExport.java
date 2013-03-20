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

import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileWriteChannel;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.logging.Logger;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;
import com.medselect.common.ReturnMessage;
import org.json.JSONException;
import java.io.IOException;
import java.io.FileNotFoundException;

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
    try {
      BlobKey blobKey = doGetBlobKey();
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
/**
   * Get Audit export file blob key from the database on demand.
   * @return fileBlobKey BlobKey export file blob key
   */
  private BlobKey doGetBlobKey() throws IOException, FileNotFoundException, JSONException {
    FileService fileService = FileServiceFactory.getFileService();
    AppEngineFile file = fileService.createNewBlobFile("application/json", "audits.json");
    boolean lock = true;
    FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
    PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
    
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
    ReturnMessage response = auditManager.doRead(auditParams, null);
    if (!response.getStatus().equals("FAILURE")){
      JSONArray jsonArray = null;
      jsonArray = response.getValue().getJSONArray( "values" );
      out.println(jsonArray.toString());
    } else {
      throw new FileNotFoundException();
    }
    out.close();
    writeChannel.closeFinally();
    return fileService.getBlobKey(file);
  }
}
