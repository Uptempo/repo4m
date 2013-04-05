/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.billing;

import com.medselect.billing.BillingGroupManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import java.util.logging.Logger;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;
import com.medselect.common.ReturnMessage;
import org.json.JSONException;
import java.io.IOException;
import com.medselect.common.ServeExportImport;


/**
 * Class to serve BillingGroup export.
 * @author karlo.smid@gmail.com
 */
public class ServeBillingGroupExport extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(ServeBillingGroupExport.class.getName());

/**
   * Serves BillingGroup export from the database.
   * @param request HttpServletRequest is http export request object.
   * @param response HttpServletResponse is http reponse object which will contain export file.
   * @return
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    String clientKey = request.getParameter("authKey");
    ServeExportImport baseExport = new ServeExportImport();
    if(!baseExport.isApplicationKeyValid(clientKey)) {
      response.sendError(response.SC_UNAUTHORIZED);
    }
    try {
      Map<String,String> billinggroupParams= new HashMap<String,String>();
      BillingGroupManager billinggroupManager = new BillingGroupManager();
      ReturnMessage jsonResponse = billinggroupManager.readBillinggroupValues(billinggroupParams, null);
      String exportData = "";
      if (!jsonResponse.getStatus().equals("FAILURE")){
        JSONArray jsonArray = null;
        jsonArray = jsonResponse.getValue().getJSONArray( "values" );
        exportData = jsonArray.toString();
      }
      BlobKey blobKey = baseExport.doGetBlobKey(exportData);
      response.setHeader("Content-Disposition", "attachment; filename=billingGroupExport.json");
      blobstoreService.serve(blobKey, response);
    } catch(IOException e) {
      LOGGER.info(e.getMessage());
      response.sendError(response.SC_INTERNAL_SERVER_ERROR);
    } catch(JSONException e) {
      LOGGER.info(e.getMessage());
      response.sendError(response.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
