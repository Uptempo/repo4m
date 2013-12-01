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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import com.medselect.imageservice.AttachmentManager;
import com.medselect.util.Constants;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import org.json.JSONArray;

import java.util.Map;
import org.json.JSONException;

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
          .put("officeTimeZone", BaseManager.FieldType.INTEGER)
          .put("officeCountry", BaseManager.FieldType.STRING)
          .put("officePhone", BaseManager.FieldType.STRING_LIST)
          .put("officeFax", BaseManager.FieldType.STRING_LIST)
          .put("officeEmail", BaseManager.FieldType.STRING)
          .put("officeNotes", BaseManager.FieldType.TEXT)
          .put("officeHours", BaseManager.FieldType.TEXT)
          .put("officeEmailTemplate", BaseManager.FieldType.TEXT)
          .put("officeUserEmailTemplate", BaseManager.FieldType.TEXT)
          .build();

  private AttachmentManager atManager = new AttachmentManager();
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

    //*** Read the Billing Office value information from the request
    String officeGroup = params.get("officeGroup");
    String officeName = params.get("officeName");
    String officeAddress1 = params.get("officeAddress1");
    String officeCity = params.get("officeCity");
    String officeState = params.get("officeState");
    String officePostalCode = params.get("officePostalCode");
    String officeCountry = params.get("officeCountry");
    String officeEmail = params.get("officeEmail");

    if( officeGroup == null || officeGroup.isEmpty() ){
      message = "officeGroup is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    if( officeName == null || officeName.isEmpty() ){
      message = "officeName is a mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( officeAddress1 == null || officeAddress1.isEmpty() ){
      message = "officeAddress1 is a mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( officeCity == null || officeCity.isEmpty() ){
      message = "officeCity is a mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( officeState == null || officeState.isEmpty() ){
      message = "officeState is a mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( officePostalCode == null || officePostalCode.isEmpty() ){
      message = "officePostalCode is a mandatory parameter!";
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
      String clearPhone = params.get("clearPhone");
      params.remove( "clearPhone" );
      String clearFax = params.get("clearFax");
      params.remove( "clearFax" );
      boolean replacePhone; 
      if (clearPhone != null) {
        if ( clearPhone.toUpperCase().equals( "TRUE" ) ){
          replacePhone = true;
        }
        else{
          replacePhone = false;
        }
      }
      else{
        replacePhone = false;
      }
      boolean replaceFax = true; 
      if (clearFax != null) {
        if ( clearFax.toUpperCase().equals( "TRUE" ) ){
          replaceFax = true;
        }
        else{
          replaceFax = false;
        }
      }
      else{
        replaceFax = false;
      }
      try{
        parseAndSetPhoneAndFaxValues( params, this.value, replacePhone, replaceFax, true, "office" );
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
      ReturnMessage response = this.doGet(itemKey);
      List<Entity> attachments = atManager.getAttachments(itemKey);
      if (!attachments.isEmpty()) {
        String attachmentUrl = (String)attachments.get(0).getProperty("url");
        try {
          response.getValue().put("bannerUrl", attachmentUrl);
        } catch (JSONException ex) {
          LOGGER.severe("Couldn't convert banner URL JSON for office.");
        }
      }
      return response;
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
   * Gets the values for an office into a POJO given the String value of the office key.
   * @param officeKeyVal The office GAE key.
   * @return The office values, null if the office could not be found.
   */
  public SimpleBillingOffice getSimpleBillingOffice(String officeKeyVal) {
    Key officeKey = KeyFactory.stringToKey(officeKeyVal);
    try {
      Entity officeEntity = ds.get(officeKey);
      SimpleBillingOffice office = new SimpleBillingOffice();
      office.setOfficeName((String)officeEntity.getProperty("officeName"));
      office.setOfficeAddress1((String)officeEntity.getProperty("officeAddress1"));
      office.setOfficeAddress2((String)officeEntity.getProperty("officeAddress2"));
      office.setOfficeCity((String)officeEntity.getProperty("officeCity"));
      office.setOfficeState((String)officeEntity.getProperty("officeState"));
      office.setOfficePostalCode((String)officeEntity.getProperty("officePostalCode"));
      Long officeTimeZoneVal = (Long)officeEntity.getProperty("officeTimeZone");
      //*** Change the office time zone offset based on daylight savings time.
      int officeTimeZoneOffset = officeTimeZoneVal.intValue();
      ConfigManager cManager = new ConfigManager();
      SimpleConfigValue dstConfig = cManager.getSimpleConfigValue(Constants.COMMON_APP, Constants.DAYLIGHT_SAVINGS_TIME_ON);
      if (dstConfig != null && dstConfig.getConfigValue().equalsIgnoreCase("TRUE")) {
        //*** Except Arizona, which doesn't observe daylight savings time.
        if (!office.getOfficeState().equals("AZ")) {
          officeTimeZoneOffset++;
        }
      }
      office.setOfficeTimeZoneOffset(officeTimeZoneOffset);
      office.setOfficeEmail((String)officeEntity.getProperty("officeEmail"));
      office.setOfficePhones((List<String>)officeEntity.getProperty("officePhone"));
      office.setOfficeFaxes((List<String>)officeEntity.getProperty("officeFax"));
      if (officeEntity.hasProperty("officeNotes")) {
        Text noteText = (Text)officeEntity.getProperty("officeNotes");
        office.setOfficeNotes(noteText.getValue());
      }
      if (officeEntity.hasProperty("officeHours")) {
        Text hoursText = (Text)officeEntity.getProperty("officeHours");
        office.setOfficeHours(hoursText.getValue());
      }
      if (officeEntity.hasProperty("officeEmailTemplate")) {
        Text emailText = (Text)officeEntity.getProperty("officeEmailTemplate");
        office.setOfficeEmailTemplate(emailText.getValue());
      }
      if (officeEntity.hasProperty("officeUserEmailTemplate")) {
        Text emailText = (Text)officeEntity.getProperty("officeUserEmailTemplate");
        office.setOfficeUserEmailTemplate(emailText.getValue());
      }
      return office;
    } catch (EntityNotFoundException ex) {
      LOGGER.severe("Could not find office for key: " + officeKeyVal);
      return null;
    }
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
