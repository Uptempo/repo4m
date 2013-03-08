/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.billing;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.doctor.DoctorManager;
import com.medselect.common.ReturnMessage;
import com.medselect.util.ValidationException;
import com.medselect.util.ValidatorUtil;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Map;
import org.json.JSONException;
import org.restlet.ext.json.JsonRepresentation;
import java.io.UnsupportedEncodingException;

/**
 * Class to manage BillingOffice values.
 * @author karlo.smid@gmail.com
 */
public class BillingOfficeManager extends BaseManager {
  public static final String BILLINGOFFICE_ENTITY_NAME = "BillingOffice";
  public static final String BILLINGOFFICE_DISPLAY_NAME = "Billing Office";
  public static final Map<String, BaseManager.FieldType> BILLINGOFFICE_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("officeGroup", BaseManager.FieldType.STRING)
          .put("officeName", BaseManager.FieldType.STRING)
          .put("officeAddress1", BaseManager.FieldType.STRING)
          .put("officeAddress2", BaseManager.FieldType.STRING)
          .put("officeCity", BaseManager.FieldType.STRING)
          .put("officeState", BaseManager.FieldType.STRING)
          .put("officePostalCode", BaseManager.FieldType.STRING)
          .put("officeCountry", BaseManager.FieldType.STRING)
          .put("officePhone", BaseManager.FieldType.STRING_LIST)
          .put("officeFax", BaseManager.FieldType.STRING_LIST)
          .put("officeEmail", BaseManager.FieldType.STRING)
          .put("officeNotes", BaseManager.FieldType.TEXT)
          .put("officeHours", BaseManager.FieldType.TEXT)
          .build();

  public BillingOfficeManager() {
    super(BILLINGOFFICE_STRUCTURE, BILLINGOFFICE_ENTITY_NAME, BILLINGOFFICE_DISPLAY_NAME);
  }
/**
   * Inserts Billing Office value into the database.
   * @param params Map request parameters in form parameter name:parameter value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertBillingOfficeValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the Billing Office value information from the request
    String officeGroup = params.get("officeGroup");
    String officeName = params.get("officeName");
    String officeAddress1 = params.get("officeAddress1");
    String officeAddress2 = params.get("officeAddress2");
    String officeCity = params.get("officeCity");
    String officeState = params.get("officeState");
    String officePostalCode = params.get("officePostalCode");
    String officeCountry = params.get("officeCountry");
    String officeEmail = params.get("officeEmail");
    String officeNotes = params.get("officeNotes");
    String officeHours = params.get("officeHours");

    if( officeGroup == null || officeGroup.isEmpty() ){
      message = "officeGroup is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    if( officeName == null || officeName.isEmpty() ){
      message = "officeName is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( officeAddress1 == null || officeAddress1.isEmpty() ){
      message = "officeAddress1 is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( officeCity == null || officeCity.isEmpty() ){
      message = "officeCity is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( officeState == null || officeState.isEmpty() ){
      message = "officeState is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( officePostalCode == null || officePostalCode.isEmpty() ){
      message = "officePostalCode is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( !this.dataValidator.isUSZIPcode( officePostalCode ) ){
      message = "officePostalCode not according to US ZIP standard!";
      insertValueStatus = "FAILURE";
    }
    
    else if( officeEmail != null && !officeEmail.isEmpty() ){
      if( !this.dataValidator.isEmail( officeEmail ) ){
        message = "officeEmail is not valid!";
        insertValueStatus = "FAILURE";
      }
    }
    if( insertValueStatus.equals( "FAILURE" ) ){
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
      return response;
    }
    
    Key dsKey = KeyFactory.createKey(entityName, officeName);
    this.value = new Entity(dsKey);
    if( officeCountry == null || officeCountry.isEmpty() ){
      this.value.setProperty( "officeCountry", "US" );
      params.remove( "officeCountry" );
    }
    try {
      parseAndSetPhoneAndFaxValues( params, value, true, true, false, "office" );
    } catch(ValidationException validEx){
      message = validEx.getMessageList().get(0);
      insertValueStatus = "FAILURE";
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
      return response;
    }
    
    return this.doCreate(params, false, null);
  }

/**
   * Update Billing Office value from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param billingOfficeValueKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage updateBillingOfficeValue(Map<String, String> params, String billingOfficeValueKey) {
    String UpdateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
 
    Entity updateValue = null;

    if (billingOfficeValueKey != null) {
      dsKey = KeyFactory.stringToKey(billingOfficeValueKey);
      try {
        value = ds.get(dsKey);
        params.put("key", billingOfficeValueKey);
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("Billing Office value identified by " + billingOfficeValueKey + " does not exist.");
        message = "Billing Office value identified by " + billingOfficeValueKey + " does not exist.";
        UpdateStatus = "FAILURE";
      }
    }

    //***Set the updated values.
    if (UpdateStatus.equals("SUCCESS")) {
      //***Read the Billing Office value information from the request
      String officeGroup = params.get("officeGroup");
      String officeName = params.get("officeName");
      String officeAddress1 = params.get("officeAddress1");
      String officeAddress2 = params.get("officeAddress2");
      String officeCity = params.get("officeCity");
      String officeState = params.get("officeState");
      String officePostalCode = params.get("officePostalCode");
      if ( officePostalCode == null || officePostalCode.isEmpty() ){
        return createReturnMessage( "officePostalCode is mandatory element!", "FAILURE" );
      }
      else{
        if ( !this.dataValidator.isUSZIPcode( officePostalCode ) ){
          return createReturnMessage( officePostalCode + " is not valid!",
                                      "FAILURE" );
        }
      }
      String officeCountry = params.get("officeCountry");
      String officeEmail = params.get("officeEmail");
      if ( officeEmail != null ){
        if ( !this.dataValidator.isEmail( officeEmail ) ){
          return createReturnMessage( officeEmail + " is not valid!",
                                      "FAILURE" );
        }
      }
      String officeNotes = params.get("officeNotes");
      String officeHours = params.get("officeHours");
      String clearPhone = params.get("clearPhone");
      params.remove( "clearPhone" );
      String clearFax = params.get("clearFax");
      params.remove( "clearFax" );

      if (officeGroup == null || officeGroup.isEmpty() ) {
        return createReturnMessage( "officeGroup is mandatory element!", "FAILURE" );
      }
      if (officeName == null || officeName.isEmpty() ) {
        return createReturnMessage( "officeName is mandatory element!", "FAILURE" );
      }
      if (officeAddress1 == null || officeAddress1.isEmpty() ) {
        return createReturnMessage( "officeAddress1 is mandatory element!", "FAILURE" );
      }
      if (officeCity == null || officeCity.isEmpty() ) {
        return createReturnMessage( "officeCity is mandatory element!", "FAILURE" );
      }
      if (officeState == null || officeState.isEmpty() ) {
        return createReturnMessage( "officeState is mandatory element!", "FAILURE" );
      }
      if (officeCountry == null || officeCountry.isEmpty() ) {
        return createReturnMessage( "officeCountry is mandatory element!", "FAILURE" );
      }
      boolean repleacePhone = true; 
      if (clearPhone != null) {
        if ( clearPhone.toUpperCase().equals( "TRUE" ) ){
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
        if ( clearFax.toUpperCase().equals( "TRUE" ) ){
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
        parseAndSetPhoneAndFaxValues( params, this.value, repleacePhone, repleaceFax, true, "office" );
      }catch( ValidationException validEx ){
        return createReturnMessage( validEx.getMessageList().get(0), "FAILURE" );
      }
      return this.doUpdate( params );
      
    }
    return createReturnMessage( message, UpdateStatus );
  }
/**
   * Returns Billing Office values from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status, info message and Billing Office data.
   */
  public ReturnMessage readBillingOfficeValues(Map<String, String> params, String itemKey) {
    
    //*** If this request was for a single value, return that request.
    if (itemKey != null) {
      return this.doGet(itemKey);
    }
    
    //** lets parse the filter parameters
    List<Filter> billingOfficeFilter = new ArrayList<Filter>();
    String officeNameValue = params.get("officeName");
    if ( officeNameValue != null && !officeNameValue.isEmpty() ){
      Filter officeNameFilter = this.createFilterForFormParameter( "officeName", officeNameValue );
      if ( officeNameFilter != null ){
        billingOfficeFilter.add( officeNameFilter );
      }
    }
    String officeGroupValue = params.get("officeGroup");
    if ( officeGroupValue != null && !officeGroupValue.isEmpty() ){
      Filter officeGroupFilter = this.createFilterForFormParameter( "officeGroup", officeGroupValue );
      if ( officeGroupFilter != null ){
        billingOfficeFilter.add( officeGroupFilter );
      }
    }
    //*** Assemble the query.
    if ( billingOfficeFilter.size() == 1  ){
      q = new Query(entityName).setFilter( billingOfficeFilter.get( 0 ) );
    }
    else{
      q = new Query( entityName );
    }
    String directionParam = params.get("direction");
    
    if ( directionParam != null && !directionParam.isEmpty() ){
      if ( directionParam.toUpperCase().equals( "ASC" ) ){
        q.addSort( "officeName", SortDirection.ASCENDING );
      }
      else if ( directionParam.toUpperCase().equals( "DESC" ) ){
        q.addSort( "officeName", SortDirection.DESCENDING );
      }
      else{
        return createReturnMessage( directionParam + " is wrong value for sort order!", "FAILURE" ); 
      }
    }
    return this.doRead(params, itemKey);
  }

  /**
   * Deletes Billing Office values from the database based on value GAE key.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage deleteBillingOfficeValue(String itemKey) {
    String itemDeleteStatus = "SUCCESS";
    String message = "";
    Key dsKey;
    Map<String,String> doctorParams= new HashMap<String,String>();
    doctorParams.put( "billingOffice", itemKey );
    doctorParams.put( "format", "list" );
    DoctorManager doctorManager = new DoctorManager();
    ReturnMessage response = doctorManager.readDoctorValues( doctorParams, null );
      if ( !response.getMessage().equals( "Returned 0 Doctors." ) ){
        JSONArray jsonArray = null;
        String doctorKey = null; 
        try{
          jsonArray = response.getValue().getJSONArray( "values" );
        }
        catch( org.json.JSONException jsonEx ){
          return createReturnMessage( jsonEx.getMessage(), "BUG" );
        }
        for ( int i = 0; i < jsonArray.length(); i++)
        {
          try{
            JSONArray json = jsonArray.getJSONArray( i );
            doctorKey = json.getString( json.length() - 1 );
          }
          catch( org.json.JSONException jsonEx ){
            return createReturnMessage( jsonEx.getMessage(), "BUG" );
          }
          ReturnMessage rm = doctorManager.deleteDoctorValue(doctorKey);
          if ( rm.getStatus().equals( "FAILURE" ) ){
            return rm;
          }
        }
      }
    if (itemKey != null) {
      dsKey = KeyFactory.stringToKey(itemKey);
      try {
        Entity value = ds.get(dsKey);
        message = "Deleting item " +
                  value.getProperty("officeName") +
                  " identified by key " + itemKey;
        LOGGER.info(message);
        value.setProperty( "deleted", "true" );
        ds.put( value );
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
