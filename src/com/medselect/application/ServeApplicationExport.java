/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.application;

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
 * Class to serve Application export.
 * @author karlo.smid@gmail.com
 */
public class ServeApplicationExport extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(ServeApplicationExport.class.getName());

/**
   * Serves Application export from the database.
   * @param request HttpServletRequest is http export request object.
   * @param response HttpServletResponse is http reponse object which will contain export file.
   * @return
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    try {
      BlobKey blobKey = doGetBlobKey();
      response.setHeader("Content-Disposition", "attachment; filename=applicationExport.json");
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
   * Get Application export file blob key from the database on demand.
   * @return fileBlobKey BlobKey export file blob key
   */
  private BlobKey doGetBlobKey() throws IOException, FileNotFoundException, JSONException {
    FileService fileService = FileServiceFactory.getFileService();
    AppEngineFile file = fileService.createNewBlobFile("application/json", "applications.json");
    boolean lock = true;
    FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
    PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
    
    String CONFIG_ENTITY_NAME = "Application";
    String CONFIG_DISPLAY_NAME = "Application";
    Map<String, BaseManager.FieldType> CONFIG_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("appCode", BaseManager.FieldType.STRING)
          .put("appName", BaseManager.FieldType.STRING)
          .put("appDescription", BaseManager.FieldType.STRING)
          .put("url", BaseManager.FieldType.STRING)
          .build();
    BaseManager applicationManager = new BaseManager(CONFIG_STRUCTURE, CONFIG_ENTITY_NAME, CONFIG_DISPLAY_NAME);

    Map<String,String> applicationParams= new HashMap<String,String>();
    ReturnMessage response = applicationManager.doRead(applicationParams, null);
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
