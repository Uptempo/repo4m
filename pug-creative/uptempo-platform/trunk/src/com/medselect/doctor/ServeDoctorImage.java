/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.doctor;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
/**
 * Class to serve Doctor images.
 * @author karlo.smid@gmail.com
 */
public class ServeDoctorImage extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
/**
   * Serves Doctor image from the database.
   * @param request HttpServletRequest is http request object which contains file blob-key.
   * @param response HttpServletResponse is http reponse object which will contain link to uploaded image.
   * @return
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    BlobKey blobKey = new BlobKey(request.getParameter("blob-key"));
    blobstoreService.serve(blobKey, response);
  }
}
