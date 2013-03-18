/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.doctor;
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

import com.medselect.doctor.DoctorManager;
import com.medselect.common.ReturnMessage;
/**
 * Class to manage upload and serving of Doctor image.
 * @author karlo.smid@gmail.com
 */
public class UploadDoctorImage extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(UploadDoctorImage.class.getName());
/**
   * Inserts Doctor image into the database.
   * @param request HttpServletRequest is http request object which contains file properties and file itself.
   * @param response HttpServletResponse is http reponse object which will contain link to uploaded image.
   * @return
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {

    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(request);
    BlobKey blobKey = blobs.get("myFile");
    
    String oldImageKey = request.getParameter("oldImageKey");
    String doctorKey = request.getParameter("doctorKey");

    String setOldImage = "";
    if (oldImageKey != null) {
      setOldImage="&img=" + oldImageKey;  
    }

    if (blobKey == null) {
      response.sendRedirect("/server/include/doctor-image-upload.jsp?res=failed&doc=" + doctorKey + setOldImage);
    }
    else {
      DoctorManager doctorManager = new DoctorManager();
      String photoKey = blobKey.getKeyString();
      ReturnMessage responseUpdate = doctorManager.updateCreateDoctorImage( photoKey, doctorKey );
      if ( responseUpdate.getStatus().equals("SUCCESS") ){
        response.sendRedirect("/server/include/doctor-image-upload.jsp?res=success&img=" + photoKey + "&doc=" + doctorKey);
      } else {
        response.sendRedirect("/server/include/doctor-image-upload.jsp?res=failed&doc=" + doctorKey + setOldImage);
      }
    }
  }
}
