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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;


/**
 * Class to manage MedLayer app values.
 * @author antonio@pomodoro.com
 */
public class MedLayerAppManager extends BaseManager {
	public static final String MEDLAYERAPP_ENTITY_NAME = "MedLayerApp";
	public static final String MEDLAYERAPP_DISPLAY_NAME = "MedLayer App";
	public static final Map<String, BaseManager.FieldType> MEDLAYERAPP_STRUCTURE =
			new ImmutableMap.Builder<String,BaseManager.FieldType>()
			.put("appName", BaseManager.FieldType.STRING)
			.build();

	public MedLayerAppManager() {
		super(MEDLAYERAPP_STRUCTURE, MEDLAYERAPP_ENTITY_NAME, MEDLAYERAPP_DISPLAY_NAME);
	}
	/**
	 * Inserts medlayer app value into the database.
	 * @param params Map request parameters in form parameter name:parameter value.
	 * @return ReturnMessage JSON format message with operation status and info message.
	 */
	public ReturnMessage insertMedLayerAppValue(Map<String, String> params) {
		String insertValueStatus = "SUCCESS";
		String message = "";

		//***Read the app value information from the request
		String appName = params.get("appName");

		if(appName == null || appName.isEmpty()){
			message = "appName is mandatory parameter!";
			insertValueStatus = "FAILURE";
		}
		if(insertValueStatus.equals("FAILURE")){
			ReturnMessage.Builder builder = new ReturnMessage.Builder();
			ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
			return response;
		}
		Key dsKey = KeyFactory.createKey(entityName, appName);
		this.value = new Entity(dsKey);

		return this.doCreate(params, false, null);
	}

	/**
	 * Update medlayerapp value from the database based on value GAE key.
	 * @param params Map request parameters in form parameter name:parameter value.
	 * @param billinggroupValueKey String is unique GAE entity key value.
	 * @return ReturnMessage JSON format message with operation status and info message.
	 */
	public ReturnMessage updateMedLayerAppValue(Map<String, String> params, String medLayerAppValueKey) {
		String medlayerAppUpdateStatus = "SUCCESS";
		String message = "";
		Key dsKey;

		if (medLayerAppValueKey != null) {
			dsKey = KeyFactory.stringToKey(medLayerAppValueKey);
			try {
				value = ds.get(dsKey);
				params.put("key", medLayerAppValueKey);
			} catch (EntityNotFoundException ex) {
				LOGGER.warning("MedLayerApp value identified by " + medLayerAppValueKey + " does not exist.");
				message = "MedLayerApp value identified by " + medLayerAppValueKey + " does not exist.";
				medlayerAppUpdateStatus = "FAILURE";
			}
		}

		//***Set the updated values.
		if (medlayerAppUpdateStatus.equals("SUCCESS")) {
			//
			return this.doUpdate(params);
		}
		return createReturnMessage(message, medlayerAppUpdateStatus);
	}
	/**
	 * Returns medlayerapp values from the database based on value GAE key.
	 * @param params Map request parameters in form parameter name:parameter value.
	 * @param itemKey String is unique GAE entity key value.
	 * @return ReturnMessage JSON format message with operation status, info message and medlayerapp data.
	 */
	public ReturnMessage readMedLayerAppValues(Map<String, String> params, String itemKey) {

		//*** If this request was for a single value, return that request.
		if (itemKey != null) {
			return this.doGet(itemKey);
		}

		//** lets parse the filter parameters
		List<Filter> medlayerAppFilter = new ArrayList<Filter>();
		String appNameValue = params.get("appName");
		if (appNameValue != null && !appNameValue.isEmpty()){
			Filter appNameFilter = this.createFilterForFormParameter("appName", appNameValue);
			if (appNameFilter != null){
				medlayerAppFilter.add(appNameFilter);
			}
		}
		String directionParam = params.get("direction");

		//*** Assemble the query.
		if (medlayerAppFilter.size() == 1){
			q = new Query(entityName).setFilter(medlayerAppFilter.get(0));
		}
		else{
			q = new Query(entityName);
		}

		if (directionParam != null && !directionParam.isEmpty()){
			if (directionParam.toUpperCase().equals("ASC")){
				q.addSort("appName", SortDirection.ASCENDING);
			}
			else if (directionParam.toUpperCase().equals("DESC")){
				q.addSort("appName", SortDirection.DESCENDING);
			}
			else{
				return createReturnMessage(directionParam + " is wrong value for sort order!", "FAILURE"); 
			}
		}
		return this.doRead(params, itemKey);
	}
	/**
	 * Deletes medlayer app values from the database based on value GAE key.
	 * @param itemKey String is unique GAE entity key value.
	 * @return ReturnMessage JSON format message with operation status and info message.
	 */
	public ReturnMessage deleteMedLayerAppValue(String itemKey) {
		Map<String,String> medlayerAppParams= new HashMap<String,String>();
		medlayerAppParams.put("appName", itemKey);
		medlayerAppParams.put("format", "list");
		MedLayerAppManager medlayerAppManager = new MedLayerAppManager();
		ReturnMessage response = medlayerAppManager.readMedLayerAppValues(medlayerAppParams, null);

		if (!response.getMessage().equals("Returned 0 MedLayerApps.")){
			JSONArray jsonArray = null;
			String medlayerAppKey = null; 
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
					medlayerAppKey = json.getString(json.length() - 1);
				}
				catch(org.json.JSONException jsonEx){
					return createReturnMessage(jsonEx.getMessage(), "BUG");
				}
				ReturnMessage rm = medlayerAppManager.deleteMedLayerAppValue(medlayerAppKey);
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
						value.getProperty("appName") +
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

	/**
	 * Returns medlayerapp values from the database based on value GAE key together with all the values of its datasets and POIs.
	 * @param itemKey String is unique GAE entity key value.
	 * @return ReturnMessage JSON format message with operation status, info message and medlayerapp data.
	 */
	public ReturnMessage readMedLayerAppAllValues(String itemKey) {
		String itemGetStatus = "SUCCESS";
		String message = "";
		Key dsKey;
		JSONObject valueJson = null;
		boolean deleted = false;

		if (itemKey != null) {
			dsKey = KeyFactory.stringToKey(itemKey);
			try {
				if (value == null ){
					value = ds.get(dsKey);
				}
				deleted = flagDeleteValue(value);
				if (!deleted){
					message = "Returned 1 " + entityDisplayName + ".";
					setAncestorFor(value);
					valueJson = new JSONObject(modifyMap(value.getProperties(), dsKey));
				}
			} catch (EntityNotFoundException ex) {
				LOGGER.warning(entityDisplayName + " identified by " + itemKey + " does not exist.");
				message = entityDisplayName + " not found!";
				itemGetStatus = "FAILURE";
			}
		}
		Map<String,String> medlayerDatasetParams= new HashMap<String,String>();
		medlayerDatasetParams.put( "medlayerApp", itemKey );
		MedLayerDatasetManager medlayerDatasetManager = new MedLayerDatasetManager();
		ReturnMessage responseDataset = medlayerDatasetManager.readMedLayerDatasetValues(medlayerDatasetParams, null);
		JSONObject jsonDataset = responseDataset.getValue();

		try {
			JSONArray arrayDataset = jsonDataset.getJSONArray("values");
			for(int i = 0; i < arrayDataset.length(); i++){
				JSONObject value = arrayDataset.getJSONObject(i);
				String datasetkey = (String)value.get("key");
				LOGGER.warning("Reading POIs for Dataset " + datasetkey);
				Map<String,String> medlayerPOIParams= new HashMap<String,String>();
				medlayerPOIParams.put( "medlayerDataset", datasetkey );
				MedLayerPOIManager medlayerPOIManager = new MedLayerPOIManager();
				ReturnMessage responsePOI = medlayerPOIManager.readMedLayerPOIValues(medlayerPOIParams, null);
				JSONObject jsonPOI = responsePOI.getValue();
				arrayDataset.getJSONObject(i).put("data", jsonPOI);  
			}

		} catch (JSONException e1) {
			message =  "Problems parsing JSON objects: " + e1.getMessage();
			itemGetStatus = "FAILURE";
		}  

		try {
			valueJson.put("dataset", jsonDataset);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		ReturnMessage.Builder builder = new ReturnMessage.Builder();
		ReturnMessage response =
				builder.status(itemGetStatus).message(message).value(valueJson).build();
		return response;

	}

}
