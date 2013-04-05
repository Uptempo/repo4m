/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.appointment;

import com.medselect.appointment.AppointmentManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import java.util.logging.Logger;
import org.json.JSONArray;
import com.medselect.common.ReturnMessage;
import org.json.JSONException;
import java.io.IOException;
import com.medselect.common.ServeExportImport;


/**
 * Class to serve Appointment export.
 * @author karlo.smid@gmail.com
 */
public class ServeAppointmentExport extends HttpServlet {
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(ServeAppointmentExport.class.getName());

/**
   * Serves Appointment export from the database.
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
      AppointmentManager appointmentManager = new AppointmentManager();
      ReturnMessage jsonResponse = appointmentManager.getAllAppointments();
      String exportData = "";
      if (!jsonResponse.getStatus().equals("FAILURE")){
        JSONArray jsonArray = null;
        jsonArray = jsonResponse.getValue().getJSONArray( "values" );
        exportData = jsonArray.toString();
      }
      BlobKey blobKey = baseExport.doGetBlobKey(exportData);
      response.setHeader("Content-Disposition", "attachment; filename=appointmentExport.json");
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
