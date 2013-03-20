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
    try {
      BlobKey blobKey = doGetBlobKey();
      response.setHeader("Content-Disposition", "attachment; filename=auditlogExport.json");
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
   * Get AuditLog export file blob key from the database on demand.
   * @return fileBlobKey BlobKey export file blob key
   */
  private BlobKey doGetBlobKey() throws IOException, FileNotFoundException, JSONException {
    FileService fileService = FileServiceFactory.getFileService();
    AppEngineFile file = fileService.createNewBlobFile("application/json", "auditlogs.json");
    boolean lock = true;
    FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
    PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
    
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
    ReturnMessage response = auditlogManager.doRead(auditlogParams, null);
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
