/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.imageservice;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesService;

import com.google.appengine.api.images.ServingUrlOptions;

import com.medselect.imageservice.ImageManager;
import com.medselect.common.ReturnMessage;
/**
 * Class to manage upload and serving of category images.
 * @author karlo.smid@gmail.com
 */
public class UploadImage extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  private ImagesService imageService = ImagesServiceFactory.getImagesService();
  protected static final Logger LOGGER = Logger.getLogger(UploadImage.class.getName());
/**
   * Inserts category image into the database.
   * @param request HttpServletRequest is http request object which contains file properties and file itself.
   * @param response HttpServletResponse is http reponse object which will contain link to uploaded image.
   * @return
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {

    Map<String, java.util.List<BlobInfo>> blobs = blobstoreService.getBlobInfos(request);
    java.util.List<BlobInfo> blobInfos = blobs.get("myFile");
    BlobKey blobKey = blobInfos.get(0).getBlobKey();
    String blobFileName = blobInfos.get(0).getFilename();
    String oldImageKey = request.getParameter("oldImageKey");
    String imageKey = request.getParameter("imageKey");
    
    String setOldImage = "";
    if (oldImageKey != null) {
      setOldImage="&img=" + oldImageKey;  
    }
    if(!isFileFormatSupported(blobFileName)){
      response.sendRedirect("/server/include/image-upload.jsp?res=failed&doc=" + imageKey + setOldImage);
    }
    if (blobKey == null) {
      response.sendRedirect("/server/include/image-upload.jsp?res=failed&doc=" + imageKey + setOldImage);
    }
    else {
      String photoKey = blobKey.getKeyString();
      ImageManager imageManager = new ImageManager();
      ServingUrlOptions servingUrlOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
      String photoUrl = imageService.getServingUrl(servingUrlOptions);
      ReturnMessage responseUpdate = imageManager.updateCreateImage(photoUrl, blobFileName, blobKey.getKeyString(), imageKey);
      if ( responseUpdate.getStatus().equals("SUCCESS") ){
        response.sendRedirect("/server/include/image-upload.jsp?res=success&img=" + photoKey + "&doc=" + imageKey);
      } else {
        response.sendRedirect("/server/include/image-upload.jsp?res=failed&doc=" + imageKey + setOldImage);
      }
    }
  }
  
  /**
   * Checks if file is of supported format.
   * @param String filename.
   * @return boolean true if supported, otherwise false.
   */
  protected boolean isFileFormatSupported(String filename){
  String fileFormat = filename.substring(filename.length()-3);
    if(fileFormat.equalsIgnoreCase(".jpg") || fileFormat.equalsIgnoreCase(".png") || fileFormat.equalsIgnoreCase(".gif")){
      return true;
    } else{
      return false;
    }
  }

  /**
   * Checks if image aspect ratio is 4:3 or 3:4.
   * @param blobKey BlobKey of uploaded image.
   * @return true if image satisfies aspect ratio, otherwise false.
   */
  protected boolean isAspectRatio43or34(BlobKey blobKey){
    Image image = ImagesServiceFactory.makeImageFromBlob(blobKey);
    int imageWidth = image.getWidth();
    int imageHeight = image.getHeight();
    float imageRatio = (float) imageWidth/imageHeight;
    float twoDecimalImageRatio = java.lang.Math.round(imageRatio * 100)/100;
    if(twoDecimalImageRatio == 1.33){
      return true;
    } else if(twoDecimalImageRatio == 0.75){
      return true;
    } else{
      return false;
    }
  }
}
