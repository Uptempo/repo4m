/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.imageservice;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Mike Gordon (mgordon)
 */
public class AttachmentDownloadServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  /**
   * Serves the attachment given the blob key.
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobKey blobKey = new BlobKey(request.getParameter("blobKey"));
    //response.addHeader("Access-Control-Allow-Origin", "*");
    //response.addHeader(
    //    "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, uptempokey");
    blobstoreService.serve(blobKey, response);
  }
  
  /**
   * Provides the options call to allow CORS for this resource.
   * @param request HTTP request.
   * @param response HTTP response.
   */
  public void doOptions(HttpServletRequest request, HttpServletResponse response) {
    response.setCharacterEncoding("UTF-8");
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader(
        "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, uptempokey");
  }
}
