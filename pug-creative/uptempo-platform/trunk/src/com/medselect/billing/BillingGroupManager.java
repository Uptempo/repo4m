/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.billing;

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

import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.Map;
import java.util.HashMap;


/**
 * Class to manage billinggroup values.
 * @author karlo.smid@gmail.com
 */
public class BillingGroupManager extends BaseManager {
  public static final String BILLINGGROUP_ENTITY_NAME = "BillingGroup";
  public static final String BILLINGGROUP_DISPLAY_NAME = "Billing Group";
  public static final Map<String, BaseManager.FieldType> BILLINGGROUP_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("groupName", BaseManager.FieldType.STRING)
          .put("groupAddress1", BaseManager.FieldType.STRING)
          .put("groupAddress2", BaseManager.FieldType.STRING)
          .put("groupCity", BaseManager.FieldType.STRING)
          .put("groupState", BaseManager.FieldType.STRING)
          .put("groupPostalCode", BaseManager.FieldType.STRING)
          .put("groupCountry", BaseManager.FieldType.STRING)
          .put("groupPhone", BaseManager.FieldType.STRING_LIST)
          .put("groupFax", BaseManager.FieldType.STRING_LIST)
          .put("groupEmail", BaseManager.FieldType.STRING)
          .put("groupNotes", BaseManager.FieldType.TEXT)
          .put("groupHours", BaseManager.FieldType.TEXT)
          .build();

  public BillingGroupManager() {
    super(BILLINGGROUP_STRUCTURE, BILLINGGROUP_ENTITY_NAME, BILLINGGROUP_DISPLAY_NAME);
      }
/**
   * Inserts billinggroup value into the database.
   * @param params Map request parameters in form parameter name:parameter value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertBillinggroupValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the billinggroup value information from the request
    String groupName = params.get("groupName");
    String groupAddress1 = params.get("groupAddress1");
    String groupAddress2 = params.get("groupAddress2");
    String groupCity = params.get("groupCity");
    String groupState = params.get("groupState");
    String groupPostalCode = params.get("groupPostalCode");
    String groupCountry = params.get("groupCountry");
    String groupEmail = params.get("groupEmail");
    String groupNotes = params.get("groupNotes");
    String groupHours = params.get("groupHours");

    if(groupName == null || groupName.isEmpty()){
      message = "groupName is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if(groupAddress1 == null || groupAddress1.isEmpty()){
      message = "groupAddress1 is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if(groupCity == null || groupCity.isEmpty()){
      message = "groupCity is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if(groupState == null || groupState.isEmpty()){
      message = "groupState is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if(groupPostalCode == null || groupPostalCode.isEmpty()){
      message = "groupPostalCode is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if(! this.dataValidator.isUSZIPcode(groupPostalCode)){
      message = "groupPostalCode not according to US ZIP standard!";
      insertValueStatus = "FAILURE";
    }
    
    else if(groupEmail != null && !groupEmail.isEmpty()){
      if(!this.dataValidator.isEmail(groupEmail)){
        message = "groupEmail is not valid!";
        insertValueStatus = "FAILURE";
      }
    }
    if(insertValueStatus.equals("FAILURE")){
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
      return response;
    }
    Key dsKey = KeyFactory.createKey(entityName, groupName);
    this.value = new Entity(dsKey);
    
    if(groupCountry == null || groupCountry.isEmpty()){
      this.value.setProperty("groupCountry", "US");
      params.remove("groupCountry");
    }
    try{
      this.parseAndSetPhoneAndFaxValues(params, value, true, true, false, "group");
    }
    catch(ValidationException validEx){
      message = validEx.getMessageList().get(0);
      insertValueStatus = "FAILURE";
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
      return response;
    }
    
    return this.doCreate(params, false, null);
  }

/**
   * Update billinggroup value from the databasei based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param billinggroupValueKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage updateBillinggroupValue(Map<String, String> params, String billinggroupValueKey) {
    String billinggroupUpdateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
 
    if (billinggroupValueKey != null) {
      dsKey = KeyFactory.stringToKey(billinggroupValueKey);
      try {
        value = ds.get(dsKey);
        params.put("key", billinggroupValueKey);
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("Billinggroup value identified by " + billinggroupValueKey + " does not exist.");
        message = "Billinggroup value identified by " + billinggroupValueKey + " does not exist.";
        billinggroupUpdateStatus = "FAILURE";
      }
    }

    //***Set the updated values.
    if (billinggroupUpdateStatus.equals("SUCCESS")) {
      //***Read the billinggroup value information from the request
      String groupPostalCode = params.get("groupPostalCode");
      if (groupPostalCode != null){
        if (!this.dataValidator.isUSZIPcode(groupPostalCode)){
          return createReturnMessage(groupPostalCode + " is not valid!",
                                      "FAILURE");
        }
      }
      String groupEmail = params.get("groupEmail");
      if (groupEmail != null){
        if (!this.dataValidator.isEmail(groupEmail)){
          return createReturnMessage(groupEmail + " is not valid!",
                                      "FAILURE");
        }
      }
      String clearPhone = params.get("clearPhone");
      params.remove("clearPhone");
      String clearFax = params.get("clearFax");
      params.remove("clearFax");

      boolean repleacePhone = true; 
      if (clearPhone != null) {
        if (clearPhone.toUpperCase().equals("TRUE")){
          repleacePhone = true;
        }
        else{
          repleacePhone = false;
        }
      }
      else{
        repleacePhone = false;
      }
      boolean repleaceFax = true; 
      if (clearFax != null) {
        if (clearFax.toUpperCase().equals("TRUE")){
          repleaceFax = true;
        }
        else{
          repleaceFax = false;
        }
      }
      else{
        repleaceFax = false;
      }
      try{
        this.parseAndSetPhoneAndFaxValues(params, value, repleacePhone, repleaceFax, true, "group");
      } catch(ValidationException validEx){
        return createReturnMessage(validEx.getMessageList().get(0), "FAILURE");
      }
      return this.doUpdate(params);
    }
    return createReturnMessage(message, billinggroupUpdateStatus);
  }
/**
   * Returns billinggroup values from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status, info messagei and billinggroup data.
   */
  public ReturnMessage readBillinggroupValues(Map<String, String> params, String itemKey) {
    
    //*** If this request was for a single value, return that request.
    if (itemKey != null) {
      return this.doGet(itemKey);
    }
    
    //** lets parse the filter parameters
    List<Filter> billinggroupFilter = new ArrayList<Filter>();
    String groupNameValue = params.get("groupName");
    if (groupNameValue != null && !groupNameValue.isEmpty()){
      Filter groupNameFilter = this.createFilterForFormParameter("groupName", groupNameValue);
      if (groupNameFilter != null){
        billinggroupFilter.add(groupNameFilter);
      }
    }
    String directionParam = params.get("direction");
    
    //*** Assemble the query.
    if (billinggroupFilter.size() == 1){
      q = new Query(entityName).setFilter(billinggroupFilter.get(0));
    }
    else{
      q = new Query(entityName);
    }
    
    if (directionParam != null && !directionParam.isEmpty()){
      if (directionParam.toUpperCase().equals("ASC")){
        q.addSort("groupName", SortDirection.ASCENDING);
      }
      else if (directionParam.toUpperCase().equals("DESC")){
        q.addSort("groupName", SortDirection.DESCENDING);
      }
      else{
        return createReturnMessage(directionParam + " is wrong value for sort order!", "FAILURE"); 
      }
    }
    return this.doRead(params, itemKey);
  }
/**
   * Deletes Billing office values from the database based on value GAE key.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage deleteBillingGroupValue(String itemKey) {
      Map<String,String> billingOfficeParams= new HashMap<String,String>();
      billingOfficeParams.put("officeGroup", itemKey);
      billingOfficeParams.put("format", "list");
      BillingOfficeManager billingOfficeManager = new BillingOfficeManager();
      ReturnMessage response = billingOfficeManager.readBillingOfficeValues(billingOfficeParams, null);
      if (!response.getMessage().equals("Returned 0 Billing Offices.")){
        JSONArray jsonArray = null;
        String officeKey = null; 
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
            officeKey = json.getString(json.length() - 1);
          }
          catch(org.json.JSONException jsonEx){
            return createReturnMessage(jsonEx.getMessage(), "BUG");
          }
          ReturnMessage rm = billingOfficeManager.deleteBillingOfficeValue(officeKey);
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
                    value.getProperty("groupName") +
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
