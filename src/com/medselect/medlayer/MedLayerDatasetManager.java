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
public class MedLayerDatasetManager extends BaseManager {
  public static final String MEDLAYERDATASET_ENTITY_NAME = "MedLayerDataset";
  public static final String MEDLAYERDATASET_DISPLAY_NAME = "MedLayer Dataset";
  public static final Map<String, BaseManager.FieldType> MEDLAYERDATASET_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("tabName", BaseManager.FieldType.STRING)
          .put("tabIcon", BaseManager.FieldType.STRING)
          .put("medlayerApp", BaseManager.FieldType.STRING)
          .build();

  public MedLayerDatasetManager() {
    super(MEDLAYERDATASET_STRUCTURE, MEDLAYERDATASET_ENTITY_NAME, MEDLAYERDATASET_DISPLAY_NAME);
      }
/**
   * Inserts medlayer dataset value into the database.
   * @param params Map request parameters in form parameter name:parameter value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertMedLayerDatasetValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the dataset value information from the request
    String tabName = params.get("tabName");
    String tabIcon = params.get("tabIcon");
    String medlayerApp = params.get("medlayerApp");
    
    if(medlayerApp == null || medlayerApp.isEmpty()){
      message = "medlayerApp is mandatory parameter!";
      insertValueStatus = "FAILURE";
    } else{
        Map<String,String> medlayerAppParams= new HashMap<String,String>();
        medlayerAppParams.put( "appName", medlayerApp );
        MedLayerAppManager medlayerAppManager = new MedLayerAppManager();
        ReturnMessage response = medlayerAppManager.readMedLayerAppValues(medlayerAppParams, medlayerApp);
        message = response.getStatus();
        if ( message.equals( "FAILURE" ) ){
          insertValueStatus = "FAILURE";
          message = "medlayerApp value: "+medlayerApp+" does not exist in the system!";
          return createReturnMessage( message, insertValueStatus );
        }
      }
    
    if(tabName == null || tabName.isEmpty()){
        message = "tabName is mandatory parameter!";
        insertValueStatus = "FAILURE";
      } 
    
    if(insertValueStatus.equals("FAILURE")){
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
      return response;
    }
    Key dsKey = KeyFactory.createKey(entityName, tabName);
    this.value = new Entity(dsKey);
    
    return this.doCreate(params, false, null);
  }

/**
   * Update medlayer dataset value from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param billinggroupValueKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage updateMedLayerDatasetValue(Map<String, String> params, String medLayerDatasetKey) {
    String medlayerDatasetUpdateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
    
    String medlayerApp = params.get("medlayerApp");
 
    if (medLayerDatasetKey != null) {
      dsKey = KeyFactory.stringToKey(medLayerDatasetKey);
      try {
        value = ds.get(dsKey);
        params.put("key", medLayerDatasetKey);
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("MedLayer Dataset value identified by " + medLayerDatasetKey + " does not exist.");
        message = "MedLayer Dataset value identified by " + medLayerDatasetKey + " does not exist.";
        medlayerDatasetUpdateStatus = "FAILURE";
        return createReturnMessage( message, medlayerDatasetUpdateStatus );
      }
    }
    
    if(medlayerApp != null && !medlayerApp.isEmpty()){
        Map<String,String> medlayerAppParams= new HashMap<String,String>();
        medlayerAppParams.put( "appName", medlayerApp );
        MedLayerAppManager medlayerAppManager = new MedLayerAppManager();
        ReturnMessage response = medlayerAppManager.readMedLayerAppValues(medlayerAppParams, medlayerApp);
        message = response.getStatus();
        if ( message.equals( "FAILURE" ) ){
        	LOGGER.warning("MedLayer App identified by " + medLayerDatasetKey + " does not exist.");
        	medlayerDatasetUpdateStatus = "FAILURE";
        	message = "medlayerApp value: "+medlayerApp+" does not exist in the system!";
          return createReturnMessage( message, medlayerDatasetUpdateStatus );
        }
      }
    
    //I should also check if the medlayerApp is updated thus check if the new one exists

    //***Set the updated values.
    if (medlayerDatasetUpdateStatus.equals("SUCCESS")) {
      //
      return this.doUpdate(params);
    }
    return createReturnMessage(message, medlayerDatasetUpdateStatus);
  }
/**
   * Returns medlayer dataset values from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status, info message and medlayerapp data.
   */
  public ReturnMessage readMedLayerDatasetValues(Map<String, String> params, String itemKey) {
    
    //*** If this request was for a single value, return that request.
    if (itemKey != null) {
      return this.doGet(itemKey);
    }
    
    //** lets parse the filter parameters
    List<Filter> medlayerDatasetFilter = new ArrayList<Filter>();
    String appNameValue = params.get("medlayerApp");
    if (appNameValue != null && !appNameValue.isEmpty()){
      Filter appNameFilter = this.createFilterForFormParameter("medlayerApp", appNameValue);
      if (appNameFilter != null){
    	  medlayerDatasetFilter.add(appNameFilter);
      }
    }
    String tabNameValue = params.get("tabName");
    if (tabNameValue != null && !tabNameValue.isEmpty()){
      Filter tabNameFilter = this.createFilterForFormParameter("tabName", tabNameValue);
      if (tabNameFilter != null){
    	  medlayerDatasetFilter.add(tabNameFilter);
      }
    }
    String directionParam = params.get("direction");
    //*** Assemble the query.
    if (medlayerDatasetFilter.size() == 1){
      q = new Query(entityName).setFilter(medlayerDatasetFilter.get(0));
    } else if ( medlayerDatasetFilter.size() > 1 ){
      Filter medlayerDatasetCompositeFilter =
                CompositeFilterOperator.or( medlayerDatasetFilter );
      q = new Query(entityName).setFilter( medlayerDatasetCompositeFilter );
    } else{
      q = new Query( entityName );
    }
    
    if (directionParam != null && !directionParam.isEmpty()){
      if (directionParam.toUpperCase().equals("ASC")){
        q.addSort("tabName", SortDirection.ASCENDING);
      }
      else if (directionParam.toUpperCase().equals("DESC")){
        q.addSort("tabName", SortDirection.DESCENDING);
      }
      else{
        return createReturnMessage(directionParam + " is wrong value for sort order!", "FAILURE"); 
      }
    }
    return this.doRead(params, itemKey);
  }
/**
   * Deletes medlayer dataset values from the database based on value GAE key.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage deleteMedLayerDatasetValue(String itemKey) {
      Map<String,String> medlayerDatasetParams= new HashMap<String,String>();
      medlayerDatasetParams.put("tabName", itemKey);
      medlayerDatasetParams.put("format", "list");
      MedLayerDatasetManager medlayerDatasetManager = new MedLayerDatasetManager();
      ReturnMessage response = medlayerDatasetManager.readMedLayerDatasetValues(medlayerDatasetParams, null);
      if (!response.getMessage().equals("Returned 0 MedLayerDatasets.")){
        JSONArray jsonArray = null;
        String medlayerDatasetKey = null; 
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
            medlayerDatasetKey = json.getString(json.length() - 1);
            LOGGER.info("Item " + i + ": " + medlayerDatasetKey);
          }
          catch(org.json.JSONException jsonEx){
            return createReturnMessage(jsonEx.getMessage(), "BUG");
          }
          ReturnMessage rm = medlayerDatasetManager.deleteMedLayerDatasetValue(medlayerDatasetKey);
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
                    value.getProperty("tabName") +
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
