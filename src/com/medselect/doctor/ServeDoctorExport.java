/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.doctor;

import com.medselect.doctor.DoctorManager;
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
 * Class to serve Doctor export.
 * @author karlo.smid@gmail.com
 */
public class ServeDoctorExport extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(ServeDoctorExport.class.getName());

/**
   * Serves Doctor export from the database.
   * @param request HttpServletRequest is http export request object.
   * @param response HttpServletResponse is http reponse object which will contain export file.
   * @return
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    try {
      BlobKey blobKey = doGetBlobKey();
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
   * Get Doctor export file blob key from the database on demand.
   * @param request HttpServletRequest is http export request object.
   * @param response HttpServletResponse is http reponse object which will contain export file.
   * @return fileBlobKey BlobKey export file blob key
   */
  public BlobKey doGetBlobKey() throws IOException, FileNotFoundException, JSONException {
    FileService fileService = FileServiceFactory.getFileService();
    AppEngineFile file = fileService.createNewBlobFile("text/plain", "doctors.json");
    boolean lock = true;
    FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
    PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
    
    Map<String,String> doctorParams= new HashMap<String,String>();
    doctorParams.put("format", "list");
    DoctorManager doctorManager = new DoctorManager();
    ReturnMessage response = doctorManager.readDoctorValues(doctorParams, null);
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
