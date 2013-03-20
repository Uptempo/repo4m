/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.staticlist;

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

import com.medselect.staticlist.StaticlistManager;
import com.medselect.common.ReturnMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.HashMap;

/**
 * Class to manage import StaticList data.
 * @author karlo.smid@gmail.com, boris.kuzmic@gmail.com
 */
public class ImportStaticListData extends HttpServlet {

  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  protected static final Logger LOGGER = Logger.getLogger(ImportStaticListData.class.getName());

  /**
   * Imports StaticList data into the database.
   * @param request HttpServletRequest is http request object which contains file properties and file itself.
   * @param response HttpServletResponse is http reponse object which will contain link to uploaded file.
   * @return
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {

    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(request);
    BlobKey blobKey = blobs.get("myFile");
    
    if (blobKey == null) {
      response.sendRedirect("/server/include/staticlists-import-data.jsp?res=failed");
    } else {
      // Now read from the file using the Blobstore API
      String importedData = new String(blobstoreService.fetchData(blobKey, 0, BlobstoreService.MAX_BLOB_FETCH_SIZE-1));
      boolean importSuccessful = true;
      try{
        JSONArray listsAsJsonArray = new JSONArray(importedData);
        JSONObject jsonElement = null;
        JSONArray listValue = null;
        JSONArray listText = null;
        StaticlistManager staticlistManager = new StaticlistManager();
        for(int index = 0;index < listsAsJsonArray.length(); index++){
          jsonElement = listsAsJsonArray.getJSONObject(index);
          Map<String,String> listParams= new HashMap<String,String>();
          
          listValue = (JSONArray) jsonElement.get("listValue");
          listText = (JSONArray) jsonElement.get("listText");
          for( int i = 1,n = listValue.length();i <= n;i++){
            listParams.put("listValue"+Integer.toString(i), listValue.getString(i-1));
          }
          for( int i = 1,n = listText.length();i <= n;i++){
            listParams.put("listText"+Integer.toString(i), listText.getString(i-1));
          }
          listParams.put("listCode", jsonElement.getString("listCode"));
          listParams.put("listKey", jsonElement.getString("listKey"));
          listParams.put("listApp", jsonElement.getString("listApp"));
          listParams.put("user", jsonElement.getString("createdBy"));
          ReturnMessage responseForInsert = staticlistManager.insertStaticlistValue(listParams);
          if (responseForInsert.getStatus().equals("FAILURE")){
            importSuccessful = false;
          }
        }
      } catch(JSONException jsonEx){
        LOGGER.severe(jsonEx.getMessage());
        importSuccessful = false;
      }
      if (importSuccessful) {
        response.sendRedirect("/server/include/staticlists-import-data.jsp?res=success");
      } else {
        response.sendRedirect("/server/include/staticlists-import-data.jsp?res=failed");
      }
    }
  }
}
