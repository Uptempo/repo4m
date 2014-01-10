/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.imageservice;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.medselect.common.ReturnMessage;
import com.medselect.util.Constants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author Mike Gordon (mgordon)
 */
public class AttachmentUploadServlet extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  private ImagesService imageService = ImagesServiceFactory.getImagesService();

  /**
   * Gets the attachment URL.
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String newUploadUrl = blobstoreService.createUploadUrl(Constants.UPLOAD_URL_ATTACHMENT);
    String jsonSuccessResponse = "{\"status\":\"SUCCESS\", \"uploadUrl\":\"" + newUploadUrl + "\"}";
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader(
        "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, uptempokey");
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(jsonSuccessResponse);
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
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Map<String, java.util.List<BlobInfo>> blobs = blobstoreService.getBlobInfos(request);
    java.util.List<BlobInfo> blobInfos = blobs.get("attachmentFile");
    BlobKey blobKey = blobInfos.get(0).getBlobKey();
    String blobFileName = blobInfos.get(0).getFilename();
    Map <String, String> params = new HashMap();
    params.put("blobKey", blobKey.getKeyString());
    params.put("fileName", blobFileName);
    params.put("entityKey", request.getParameter("entityKey"));
    params.put("category", request.getParameter("category"));
    AttachmentManager aManager = new AttachmentManager();
    ReturnMessage result = aManager.insertAttachment(params);
    //*** Format a JSON response to the front end.
    String returnData = "";
    JSONObject obj = new JSONObject();
    try {
      obj.put("status", result.getStatus());
      obj.put("message", result.getMessage());
      if (result.getStatus().equals("SUCCESS")) {
        ServingUrlOptions servingUrlOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
        String photoUrl = imageService.getServingUrl(servingUrlOptions);
        obj.put("key", blobKey.getKeyString());
        obj.put("value", photoUrl);
      }
      
      returnData = obj.toString();
    } catch (Exception ex) {
      returnData = "ERROR: JSON assembly failed.";
    }
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(returnData);
  }
}
