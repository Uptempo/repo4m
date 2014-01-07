/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.medlayer;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import com.medselect.util.ValidationException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.Map;
import java.util.HashMap;


/**
 * Class to manage MedLayer dataset values.
 * @author antonio@pomodoro.com
 */
public class MedLayerPOIManager extends BaseManager {
  public static final String MEDLAYERPOI_ENTITY_NAME = "MedLayerPOI";
  public static final String MEDLAYERPOI_DISPLAY_NAME = "MedLayer POI";
  public static final Map<String, BaseManager.FieldType> MEDLAYERPOI_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("title", BaseManager.FieldType.STRING)
          .put("lat", BaseManager.FieldType.DECIMAL)
          .put("lon", BaseManager.FieldType.DECIMAL)
          .put("alt", BaseManager.FieldType.DECIMAL)
          .put("description", BaseManager.FieldType.TEXT)
          .put("details", BaseManager.FieldType.TEXT)
          .put("normalImg", BaseManager.FieldType.STRING)
          .put("selectedImg", BaseManager.FieldType.STRING)
          .put("directionImg", BaseManager.FieldType.STRING)
          .put("medlayerDataset", BaseManager.FieldType.STRING)
          .build();

  public MedLayerPOIManager() {
    super(MEDLAYERPOI_STRUCTURE, MEDLAYERPOI_ENTITY_NAME, MEDLAYERPOI_DISPLAY_NAME);
      }
/**
   * Inserts medlayer POI value into the database.
   * @param params Map request parameters in form parameter name:parameter value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertMedLayerPOIValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the dataset value information from the request
    String title = params.get("title");
    try{
    	float lon = Float.parseFloat(params.get("lon"));
    } catch(NumberFormatException e){
    	message = "longitude badly formatted";
    	LOGGER.info(message);
        insertValueStatus = "FAILURE";
    }
    try{
    	float lat = Float.parseFloat(params.get("lat"));
    } catch(NumberFormatException e){
    	message = "latitude badly formatted";
    	LOGGER.info(message);
        insertValueStatus = "FAILURE";
    }
    try{
    	float alt = Float.parseFloat(params.get("alt"));
    } catch(NumberFormatException e){
    	message = "altitude badly formatted";
    	LOGGER.info(message);
        insertValueStatus = "FAILURE";
    }
    String description = params.get("description");
    String details = params.get("details");
    String normalImg = params.get("normalImg");
    String selectedImg = params.get("selectedImg");
    String directionImg = params.get("directionImg");
    String medlayerDataset = params.get("medlayerDataset");
    
    if(medlayerDataset == null || medlayerDataset.isEmpty()){
      message = "medlayerDataset is mandatory parameter!";
      insertValueStatus = "FAILURE";
    } else{
        Map<String,String> medlayerDatasetParams= new HashMap<String,String>();
        medlayerDatasetParams.put( "tabName", medlayerDataset );
        MedLayerDatasetManager medlayerDatasetManager = new MedLayerDatasetManager();
        ReturnMessage response = medlayerDatasetManager.readMedLayerDatasetValues(medlayerDatasetParams, medlayerDataset);
        message = response.getStatus();
        if ( message.equals( "FAILURE" ) ){
          insertValueStatus = "FAILURE";
          message = "medlayerDataset value: "+medlayerDataset+" does not exist in the system!";
          return createReturnMessage( message, insertValueStatus );
        }
      }
    
    if(title == null || title.isEmpty()){
        message = "title is mandatory parameter!";
        insertValueStatus = "FAILURE";
      }
    if(params.get("lat") == null || params.get("lat").isEmpty()){
        message = "lat is mandatory parameter!";
        insertValueStatus = "FAILURE";
      }
    if(params.get("lon") == null || params.get("lon").isEmpty()){
        message = "lon is mandatory parameter!";
        insertValueStatus = "FAILURE";
      }
    if(params.get("alt") == null || params.get("alt").isEmpty()){
        message = "alt is mandatory parameter!";
        insertValueStatus = "FAILURE";
      }
    
    if(insertValueStatus.equals("FAILURE")){
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
      return response;
    }
    Key dsKey = KeyFactory.createKey(entityName, title);
    this.value = new Entity(dsKey);
    
    return this.doCreate(params, false, null);
  }

/**
   * Update medlayer POI value from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param billinggroupValueKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage updateMedLayerPOIValue(Map<String, String> params, String medLayerPOIKey) {
    String medlayerPOIUpdateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
    
    String medlayerDataset = params.get("medlayerDataset");
 
    if (medLayerPOIKey != null) {
      dsKey = KeyFactory.stringToKey(medLayerPOIKey);
      try {
        value = ds.get(dsKey);
        params.put("key", medLayerPOIKey);
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("MedLayer POI value identified by " + medLayerPOIKey + " does not exist.");
        message = "MedLayer Dataset value identified by " + medLayerPOIKey + " does not exist.";
        medlayerPOIUpdateStatus = "FAILURE";
        return createReturnMessage( message, medlayerPOIUpdateStatus );
      }
    }
    
    if(medlayerDataset != null && !medlayerDataset.isEmpty()){
        Map<String,String> medlayerAppParams= new HashMap<String,String>();
        medlayerAppParams.put( "tabName", medlayerDataset );
        MedLayerDatasetManager medlayerDatasetManager = new MedLayerDatasetManager();
        ReturnMessage response = medlayerDatasetManager.readMedLayerDatasetValues(medlayerAppParams, medlayerDataset);
        message = response.getStatus();
        if ( message.equals( "FAILURE" ) ){
        	LOGGER.warning("MedLayer Dataset identified by " + medlayerDataset + " does not exist.");
        	medlayerPOIUpdateStatus = "FAILURE";
        	message = "medlayerApp value: "+medlayerDataset+" does not exist in the system!";
          return createReturnMessage( message, medlayerPOIUpdateStatus );
        }
      }
    
    //***Set the updated values.
    if (medlayerPOIUpdateStatus.equals("SUCCESS")) {
      //
      return this.doUpdate(params);
    }
    return createReturnMessage(message, medlayerPOIUpdateStatus);
  }
/**
   * Returns medlayer POI values from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status, info message and medlayerPOI data.
   */
  public ReturnMessage readMedLayerPOIValues(Map<String, String> params, String itemKey) {
    
    //*** If this request was for a single value, return that request.
    if (itemKey != null) {
      return this.doGet(itemKey);
    }
    
    //** lets parse the filter parameters
    List<Filter> medlayerPOIFilter = new ArrayList<Filter>();
    String titleValue = params.get("title");
    if (titleValue != null && !titleValue.isEmpty()){
      Filter titleFilter = this.createFilterForFormParameter("title", titleValue);
      if (titleFilter != null){
    	  medlayerPOIFilter.add(titleFilter);
      }
    }
    String datasetValue = params.get("medlayerDataset");
    if (datasetValue != null && !datasetValue.isEmpty()){
      Filter datasetFilter = this.createFilterForFormParameter("medlayerDataset", datasetValue);
      if (datasetFilter != null){
    	  medlayerPOIFilter.add(datasetFilter);
      }
    }
    String directionParam = params.get("direction");
    //*** Assemble the query.
    if (medlayerPOIFilter.size() == 1){
    	LOGGER.info("1 solo filtro");
    	LOGGER.info(medlayerPOIFilter.get(0).toString());
      q = new Query(entityName).setFilter(medlayerPOIFilter.get(0));
    } else if ( medlayerPOIFilter.size() > 1 ){
    	LOGGER.info("filtro > 1");
      Filter medlayerPOICompositeFilter =
                CompositeFilterOperator.or( medlayerPOIFilter );
      q = new Query(entityName).setFilter( medlayerPOICompositeFilter );
    } else{
    	LOGGER.info("SENXZA filtro");
      q = new Query( entityName );
    }
    
    if (directionParam != null && !directionParam.isEmpty()){
      if (directionParam.toUpperCase().equals("ASC")){
        q.addSort("title", SortDirection.ASCENDING);
      }
      else if (directionParam.toUpperCase().equals("DESC")){
        q.addSort("title", SortDirection.DESCENDING);
      }
      else{
        return createReturnMessage(directionParam + " is wrong value for sort order!", "FAILURE"); 
      }
    }
    return this.doRead(params, itemKey);
  }
/**
   * Deletes medlayer POI values from the database based on value GAE key.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage deleteMedLayerPOIValue(String itemKey) {
      Map<String,String> medlayerPOIParams= new HashMap<String,String>();
      medlayerPOIParams.put("title", itemKey);
      medlayerPOIParams.put("format", "list");
      MedLayerPOIManager medlayerDatasetManager = new MedLayerPOIManager();
      ReturnMessage response = medlayerDatasetManager.readMedLayerPOIValues(medlayerPOIParams, null);
      if (!response.getMessage().equals("Returned 0 MedLayerPOIs.")){
        JSONArray jsonArray = null;
        String medlayerPOIKey = null; 
        try{
          jsonArray = response.getValue().getJSONArray("values");
        }
        catch(org.json.JSONException jsonEx){
          return createReturnMessage(jsonEx.getMessage(), "BUG");
        }
        for (int i = 0; i < jsonArray.length(); i++)
        {
          try{
            JSONArray json = jsonArray.getJSONArray(i);
            medlayerPOIKey = json.getString(json.length() - 1);
            LOGGER.info("Item " + i + ": " + medlayerPOIKey);
          }
          catch(org.json.JSONException jsonEx){
            return createReturnMessage(jsonEx.getMessage(), "BUG");
          }
          ReturnMessage rm = medlayerDatasetManager.deleteMedLayerPOIValue(medlayerPOIKey);
          LOGGER.info("Return Message: " + rm.getMessage());
          if (rm.getStatus().equals("FAILURE")){
            return rm;
          }
        }
      }
      String itemDeleteStatus = "SUCCESS";
      String message = "";
      Key dsKey;

      if (itemKey != null) {
        dsKey = KeyFactory.stringToKey(itemKey);
        try {
          Entity value = ds.get(dsKey);
          message = "Deleting item " +
                    value.getProperty("title") +
                    " identified by key " + itemKey;
          LOGGER.info(message);
          value.setProperty("deleted", "true");
          ds.put(value);
        } catch (EntityNotFoundException ex) {
          message = entityDisplayName + " not found!";
          itemDeleteStatus = "FAILURE";
        }
      }
      else {
        itemDeleteStatus = "FAILURE";
        message = "No key specified for " + entityDisplayName;
      }

      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      response =
          builder.status(itemDeleteStatus).message(message).build();
      return response;
  }
}
