/*
 * Copyright 2013 Uptempo Group Inc.
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.HashMap;

/**
 * Class to manage import Doctor data.
 * @author karlo.smid@gmail.com, boris.kuzmic@gmail.com
 */
public class ImportDoctorData extends HttpServlet {

  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(ImportDoctorData.class.getName());

  /**
   * Imports Doctor data into the database.
   * @param request HttpServletRequest is http request object which contains file properties and file itself.
   * @param response HttpServletResponse is http reponse object which will contain link to uploaded file.
   * @return
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {

    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(request);
    BlobKey blobKey = blobs.get("myFile");
    
    if (blobKey == null) {
      response.sendRedirect("/server/include/doctor-import-data.jsp?res=failed");
    } else {
      // Now read from the file using the Blobstore API
      String importedData = new String(blobstoreService.fetchData(blobKey, 0, BlobstoreService.MAX_BLOB_FETCH_SIZE-1));
      boolean importSuccessful = true;
      try{
        JSONArray doctorsAsJsonArray = new JSONArray(importedData);
        JSONObject jsonElement = null;
        JSONArray listTitles = null;
        JSONArray listSpecialties = null;
        DoctorManager doctorManager = new DoctorManager();
        for(int index = 0;index < doctorsAsJsonArray.length(); index++){
          jsonElement = doctorsAsJsonArray.getJSONObject(index);
          Map<String,String> doctorParams= new HashMap<String,String>();
          
          listTitles = (JSONArray) jsonElement.get("title");
          listSpecialties = (JSONArray) jsonElement.get("speciality");
          for( int i = 1,n = listTitles.length();i <= n;i++){
            doctorParams.put("title"+Integer.toString(i), listTitles.getString(i-1));
          }
          for( int i = 1,n = listSpecialties.length();i <= n;i++){
            doctorParams.put("speciality"+Integer.toString(i), listSpecialties.getString(i-1));
          }
          doctorParams.put("firstName", jsonElement.getString("firstName"));
          doctorParams.put("lastName", jsonElement.getString("lastName"));
          doctorParams.put("primarySpeciality", jsonElement.getString("primarySpeciality"));
          doctorParams.put("modifiedBy", jsonElement.getString("modifiedBy"));
          doctorParams.put("modifyDate", jsonElement.getString("modifyDate"));
          doctorParams.put("education", jsonElement.getString("education"));
          doctorParams.put("createDate", jsonElement.getString("createDate"));
          ReturnMessage responseForInsert = doctorManager.insertDoctorValue(doctorParams);
          if (responseForInsert.getStatus().equals("FAILURE")){
            importSuccessful = false;
          }
        }
      } catch(JSONException jsonEx){
        LOGGER.severe(jsonEx.getMessage());
        importSuccessful = false;
      }
      if (importSuccessful) {
        response.sendRedirect("/server/include/doctor-import-data.jsp?res=success");
      } else {
        response.sendRedirect("/server/include/doctor-import-data.jsp?res=failed");
      }
    }
  }
}
