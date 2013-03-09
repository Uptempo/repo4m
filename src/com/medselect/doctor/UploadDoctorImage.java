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

import java.lang.StringBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

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
    String body = getBodyFrom( request );
    String oldImageKey = "";
    // first test if oldImage exists in request
    if (body.indexOf("oldImage=") != -1 ) {    
      oldImageKey = body.substring( body.indexOf( "oldImage=" ) + "oldImage=".length(), body.indexOf( "=oldImage" ) );
    }
    String doctorKey = body.substring( body.indexOf( "doctorKey=" ) + "doctorKey=".length(), body.indexOf( "=doctorKey" ) );

    if (blobKey == null) {
      response.sendRedirect("/server/include/doctor-image-upload.jsp?res=failed&img=" + oldImageKey + "&doc=" + doctorKey);
    }
    else {
      DoctorManager doctorManager = new DoctorManager();
      String photoKey = blobKey.getKeyString();
      ReturnMessage responseUpdate = doctorManager.updateCreateDoctorImage( photoKey, doctorKey );
      if ( responseUpdate.getStatus().equals("SUCCESS") ){
        response.sendRedirect("/server/include/doctor-image-upload.jsp?res=success&img=" + photoKey + "&doc=" + doctorKey);
      } else {
        response.sendRedirect("/server/include/doctor-image-upload.jsp?res=failed&img=" + oldImageKey + "&doc=" + doctorKey);
      }
    }
  }
/**
   * Gets request body as a string.
   * @param request HttpServletRequest is http request object which contains file properties and file itself.
   * @throws IOException
   * @return body String is request body as a string.
   */
  protected String getBodyFrom( HttpServletRequest request ) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;
    try {
      InputStream inputStream = request.getInputStream();
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] charBuffer = new char[128];
        int bytesRead = -1;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      } else {
        stringBuilder.append("");
      }
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException ex) {
          throw ex;
        }
      }
    }
    return stringBuilder.toString();
  }
}
