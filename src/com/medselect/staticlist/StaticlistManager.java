/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.staticlist;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Text;
import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import com.medselect.util.ValidationException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;


import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class to manage staticlist values.
 * @author karlo.smid@gmail.com
 */
public class StaticlistManager extends BaseManager {
  public static final String STATICLIST_ENTITY_NAME = "Staticlist";
  public static final String STATICLIST_DISPLAY_NAME = "Static list";
  public static final Map<String, BaseManager.FieldType> STATICLIST_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("listApp", BaseManager.FieldType.STRING)
          .put("listCode", BaseManager.FieldType.STRING)
          .put("listKey", BaseManager.FieldType.STRING)
          .put("listValue", BaseManager.FieldType.STRING_LIST)
          .put("listText", BaseManager.FieldType.TEXT_LIST)	
          .build();

  public StaticlistManager() {
    super(STATICLIST_STRUCTURE, STATICLIST_ENTITY_NAME, STATICLIST_DISPLAY_NAME);
  }
/**
   * Inserts new staticlists into database
   * @param params Map of submited form parameters.
   * @return ReturnMessage JSON format message with status and information message of the operation.
   */
  public ReturnMessage insertStaticlistValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the staticlist value information from the request
    String listApp = params.get("listApp");
    String listCode = params.get("listCode");
    String listKey = params.get("listKey");
    String userEmail = params.get("user");

    if( listApp == null || listApp.isEmpty() ){
      message = "listApp is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( listCode == null || listCode.isEmpty() ){
      message = "listCode is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( listKey == null || listKey.isEmpty() ){
      message = "listKey is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    else if( userEmail == null || userEmail.isEmpty() ){
      message = "user is mandatory parameter!";
      insertValueStatus = "FAILURE";
    }
    if( insertValueStatus.equals( "FAILURE" ) ){
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
      return response;
    }

    //*** Do transformations of the App and Code
    listApp = listApp.toUpperCase();
    params.remove( "listApp" );
    params.put( "listApp", listApp );
    listCode = listCode.toUpperCase();
    params.remove( "listCode" );
    params.put( "listCode", listCode );

    Key dsKey = KeyFactory.createKey(entityName, listKey);
    this.value = new Entity(dsKey);

    try{
      parseAndSetStaticlistsValues( params, this.value, true );
    }
    catch( ValidationException validEx  ){
      message = validEx.getMessageList().get(0);
      insertValueStatus = "FAILURE";
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
      return response;
    }
    return this.doCreate( params, false, null );

  }

  /**
   * Parse and set staticlists values and text lists
   * @param params Map of submited form parameters.
   * @param newValue is Entity object
   * @param replaceLists bool true is for repleace list elements, false is for add list elements
   * @throws ValidationException
   * @return
   */
  public void parseAndSetStaticlistsValues(Map<String, String> params, Entity newValue, boolean replaceLists ) throws ValidationException
  {
    List<String> listValue = new ArrayList<String>();
    List<Text> listText = new ArrayList<Text>();
    SortedSet<String> sortedParamKeys = new TreeSet<String>(params.keySet());
    for (String vKey : sortedParamKeys){
      if ( vKey.matches("^listValue[1-9][0-9]*") ){
        String keyValue = params.get( vKey );
        params.remove( vKey );
        if( keyValue.length() > 500 ){
          ValidationException validationException = new ValidationException();
          validationException.addMessage( vKey + " element is more than 500 characters long.");
          throw validationException;
        }
        else{
          listValue.add( keyValue );
        }
      }
      else if( vKey.matches("^listText[1-9][0-9]*") ){
          String keyValue = params.get( vKey );
          params.remove( vKey );
          listText.add( new Text( keyValue ) );
          }
    }
    if ( !listValue.isEmpty() ){
      if ( replaceLists ){
        newValue.setProperty("listValue", listValue);
      }
      else{
        List<String> currentValues = ( List<String> ) newValue.getProperty( "listValue" );
        if ( currentValues != null ){
          List<String> current = new ArrayList<String>( currentValues );
          current.addAll( listValue );
          newValue.setProperty("listValue", current );
        }
        else{
          newValue.setProperty("listValue", listValue );
        }
      }
    }
    if ( !listText.isEmpty() ){
      if ( replaceLists ){
        newValue.setProperty("listText", listText);
      }
      else{
        List<Text> currentText = ( List<Text> ) newValue.getProperty( "listText" );
        if ( currentText != null ){
          List<Text> current = new ArrayList<Text>( currentText );
          current.addAll( listText );
          newValue.setProperty( "listText", current );
        }
        else{
          newValue.setProperty( "listText", listText );
        }
      }
    }
  }
/**
   * Updates existing staticlist.
   * @param params Map of submited form parameters.
   * @param staticlistValueKey String is staticlist unique GAE key value.
   * @return ReturnMessage JSON format message with status and info message about the operation.
   */
  public ReturnMessage updateStaticlistValue(Map<String, String> params, String staticlistValueKey) {
    String staticlistUpdateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
 
    if (staticlistValueKey != null) {
      dsKey = KeyFactory.stringToKey(staticlistValueKey);
      try {
        value = ds.get(dsKey);
        params.put( "key", staticlistValueKey );
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("Staticlist value identified by " + staticlistValueKey + " does not exist.");
        message = "Staticlist value identified by " + staticlistValueKey + " does not exist.";
        staticlistUpdateStatus = "FAILURE";
      }
    }

    //***Set the updated values.
    if (staticlistUpdateStatus.equals("SUCCESS")) {
      //***Read the staticlist value information from the request
      String listApp = params.get("listApp");
      String listCode = params.get("listCode");
      String listKey = params.get("listKey");
      //*** Do transformations of the App and Code
      listApp = listApp.toUpperCase();
      params.remove( "listApp" );
      params.put( "listApp", listApp );
      listCode = listCode.toUpperCase();
      params.remove( "listCode" );
      params.put( "listCode", listCode );
      String clearValues = params.get("clearValues");
      params.remove( "clearValues" );
      boolean repleaceLists = true; 
      if (clearValues != null) {
        if ( clearValues.toUpperCase().equals( "TRUE" ) ){
          repleaceLists = true;
        }
        else{
          repleaceLists = false;
        }
      }
      else{
        repleaceLists = false;
      } 
      try{
        parseAndSetStaticlistsValues( params, value, repleaceLists );
      }catch( ValidationException validEx ){
        message = validEx.getMessageList().get(0);
        staticlistUpdateStatus = "FAILURE";
        ReturnMessage.Builder builder = new ReturnMessage.Builder();
        ReturnMessage response = builder.status(staticlistUpdateStatus).message(message).value(null).build();
        return response;
      }
      return this.doUpdate( params );
    }

    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response = builder.status(staticlistUpdateStatus).message(message).build();
    return response;
  }

  /**
   * Method to get a simple list of Strings from a static list.
   * @param application The list application.
   * @param listCode The list code.
   * @return The list values.
   */
  public List<String> readStaticlistValue(String application, String listCode) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("listApp", application);
    params.put("listCode", listCode);
    params.put("listKey", listCode);
    
    List<Filter> slFilter = createStaticListFilter(params);
    Filter staticListCompositeFilter = CompositeFilterOperator.and(slFilter);
    q = new Query(entityName).setFilter(staticListCompositeFilter);
    PreparedQuery pq = ds.prepare(q);

    Entity result = pq.asSingleEntity();
    List<String> data = new ArrayList<String>();
    if (result != null) {
      data = (List<String>)result.getProperty("listValue");
    }
    return data;
  }
  
  /**
   * Helper method to create a static list filter from parameters.  Parameters are listApp,
   * listCode, and listKey.
   * @param params The parameters.
   * @return A list of {@link Filter} objects with the filters for the list.
   */
  private List<Filter> createStaticListFilter(Map<String, String> params) {
    //*** Parse the filter parameters, create the filter.
    List<Filter> staticListFilter = new ArrayList<Filter>();
    String listAppValue = params.get("listApp");
    Filter listAppFilter = createFilterForFormParameter( "listApp", listAppValue );
    if ( listAppFilter != null ){
      staticListFilter.add( listAppFilter );
    }
    String listCodeValue = params.get("listCode");
    Filter listCodeFilter = createFilterForFormParameter( "listCode", listCodeValue );
    if ( listCodeFilter != null ){
      staticListFilter.add( listCodeFilter );
    }
    String listKeyValue = params.get("listKey");
    Filter listKeyFilter = createFilterForFormParameter( "listKey", listKeyValue );
    if ( listKeyFilter != null && listCodeFilter != null ){
      staticListFilter.add( listKeyFilter );
    }
    return staticListFilter;
  }
  
  /**
   * Reads staticlist values from the database.
   * @param params Map of submited form parameters.
   * @param itemKey String is staticlist unique GAE key value.
   * @return ReturnMessage JSON format message with status of the operation and staticlist values.
   */
  public ReturnMessage readStaticlistValues(Map<String, String> params, String itemKey) {
    
    //*** If this request was for a single value, return that request.
    if (itemKey != null) {
      return this.doGet(itemKey);
    }
    List<Filter> staticListFilter = createStaticListFilter(params);
    //*** Assemble the query.
    if ( staticListFilter.size() > 1 ){
        Filter staticListCompositeFilter =
          CompositeFilterOperator.and( staticListFilter );
        q = new Query(entityName).setFilter( staticListCompositeFilter );
    }
    else if ( staticListFilter.size() == 1  ){
      q = new Query(entityName).setFilter( staticListFilter.get( 0 ) );
    }
    else{
      q = new Query( entityName );
    }
    
    return this.doRead(params, itemKey);
  }

  /**
   * Create GAE filter for form parametera and value
   * @param String formParameter is name of the filter.
   * @param String formParameterValue is filter value.
   * @return FilterPredicate GAE object.
   */
  protected FilterPredicate createFilterForFormParameter( String formParameter, String formParameterValue ){
      if ( !( formParameterValue == null || formParameterValue.isEmpty() ) ){
        FilterPredicate filterForFormParameter = new FilterPredicate(formParameter,
                                                                     FilterOperator.EQUAL,
                                                                     formParameterValue);
        return filterForFormParameter;
      }
      else{
          return null;
      }
  }
/**
   * Deletes staticlist value from the database.
   * @param valueKey String is staticlist unique GAE key value.
   * @return ReturnMessage JSON format message with status of the operation and deleted staticlist GAE key value.
   */
  public ReturnMessage deleteStaticlistValue(String valueKey) {
    String itemDeleteStatus = "SUCCESS";
    String message = "";
    Key dsKey;

    if (valueKey != null) {
      dsKey = KeyFactory.stringToKey(valueKey);
      try {
        Entity value = ds.get(dsKey);
        message = "Deleting item " +
                  value.getProperty("listCode") +
                  " identified by key " + valueKey;
        LOGGER.info(message);
        value.setProperty( "deleted", "true" );
        ds.put( value );
      } catch (EntityNotFoundException ex) {
        message = entityDisplayName + " not found!";
        itemDeleteStatus = "FAILURE";
      }
    } else {
      itemDeleteStatus = "FAILURE";
      message = "No key specified for " + entityDisplayName;
    }

    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response =
        builder.status(itemDeleteStatus).message(message).build();
    return response;
  }
}
