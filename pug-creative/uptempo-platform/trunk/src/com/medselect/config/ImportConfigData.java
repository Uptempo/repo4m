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
      // TODO: Karlo, implement this
      boolean importSuccessful = true;
     
      if (importSuccessful) {
        response.sendRedirect("/server/include/config-import-data.jsp?res=success");
      } else {
        response.sendRedirect("/server/include/config-import-data.jsp?res=failed");
      }
    }
  }
}
