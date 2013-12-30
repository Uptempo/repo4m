/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.common;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;

import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.medselect.util.ValidatorUtil;
import com.medselect.util.ValidationException;


/**
 * Base Manager class, from which manager classes inherit.
 * @author Mike Gordon
 */
public class BaseManager {
  protected static final Logger LOGGER = Logger.getLogger(BaseManager.class.getName());
  protected final DatastoreService ds;
  public ValidatorUtil dataValidator = new ValidatorUtil();

  //*** Map that sets the structure of the entity that this resource supports.
  //*** The key is the property name, the value is the property type.
  protected Map<String, BaseManager.FieldType> itemValueMap;
  //*** The name of the entity for the Datastore.
  protected String entityName;
  //*** The name of the entity for display to the front end.
  protected String entityDisplayName;
  //*** The app code of this application, used for determining config values to retrieve.
  protected String currentAppCode;
  //** The service query object.
  protected Query q = null;
  //** The Entity value retrieved from the database.
  protected Entity value = null; 
  //** The PreparedQuery when we retrieve several values
  protected PreparedQuery pq = null;
  //*** The field types available for defining entities.
  public enum FieldType {
    BLOB, BLOB_KEY, DATE, DECIMAL, INTEGER, LONG, PHONE_NUMBER, STRING, TEXT, TEXT_LIST,
    SHORT_BLOB, STRING_LIST
  }
 
  public BaseManager(
      Map<String, BaseManager.FieldType> ivMap, 
      String entityName,
      String entityDisplayName) {
    this.itemValueMap = ivMap;
    this.entityName = entityName;
    this.entityDisplayName = entityDisplayName;
    ds = DatastoreServiceFactory.getDatastoreService();
    currentAppCode = System.getProperty("com.uptempo.appcode");
  }
  
  /**
   * Wrapper for the doCreate method for no parent key.
   * @param itemMap The values of the new entity.
   * @param keyRequired Indicator to indicate whether a key name is required.
   * @return A <@link ReturnMessage> containing the status, message, and ID of the new entity.
   */
  public ReturnMessage doCreate(Map<String, String> itemMap, boolean keyRequired) {
    return this.doCreate(itemMap, keyRequired, null);
  }

  /**
   * Creates an entity given the parameters provided.
   * 
   * @param itemMap A Map of the data intended for insert as a new entity.
   * @param keyRequired Indicates whether a key is required for placement of this entity in the
   *    datastore.  Keys may be required so that the Entity's key can be generated from the
   *    human-readable unique index.
   * @param parentKey The parent key under which to create the entity.
   * @return A <@link ReturnMessage> containing the status, message, and ID of the new entity.
   */
  public ReturnMessage doCreate(
      Map<String, String> itemMap,
      boolean keyRequired,
      Key parentKey) {
    String insertStatus = "SUCCESS";
    String message = "";
    Key newItemKey = null;

    //*** If the key is included or the key isn't required.
    if (itemMap.get("key") != null || !keyRequired) {
      Entity newValue;
      Key dsKey = null;
      if (keyRequired) {
        String keyValue = itemMap.get("key");
        itemMap.remove("key");
        //*** Check if this item has a parent.
        if (parentKey != null) {
          dsKey = KeyFactory.createKey(parentKey, entityName, keyValue);
        } else {
          dsKey = KeyFactory.createKey(entityName, keyValue);
        }
        newValue = new Entity(dsKey);
      } else {
        if (value == null) {
          //*** Check if this item has a parent.
          if (parentKey != null) {
            newValue = new Entity(entityName, parentKey);
          } else {
            newValue = new Entity(entityName);
          }
        }
        else {
          newValue = value;
        }
      }

      //*** Fill in the created by and modified by info.
      String user = itemMap.get("user");
      //*** If the user param was provided, assume that the service will stamp created/modified data.
      if (user != null) {
        newValue.setProperty("createdBy", user);
        newValue.setProperty("createDate", new Date());
        newValue.setProperty("modifiedBy", user);
        newValue.setProperty("modifyDate", new Date());
        itemMap.remove("user");
      }

      //*** Set the mapped fields based on the schema.
      newValue = setMappedFields(itemMap, newValue);
      itemMap.remove("key");

      //*** To allow flexibility on the front end, find any leftover properties and set them
      //*** as String values.
      for (String vKey : itemMap.keySet()) {
        //*** Don't set empty fields on the entity, because they could be the wrong type.
        if (itemMap.get(vKey) != null && !itemMap.get(vKey).isEmpty()) {
          newValue.setUnindexedProperty(vKey, itemMap.get(vKey));
        }
      }
      
      if (dsKey != null) {
        LOGGER.info("Creating " + entityName + ": " + dsKey.getName());
      } else {
        LOGGER.info("Creating " + entityName);
      }

      newItemKey = ds.put(newValue);
      message = "Added " + entityName + ":" + newValue.getKey().getName();
    } else {
      insertStatus = "FAILURE";
      message = "Key was not provided for " + entityDisplayName;
    }
    
     //*** Add the key id into the response.
    JSONObject obj = this.createJSONFromKey(newItemKey);

    //*** Build the response.
    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response = builder
        .status(insertStatus)
        .message(message)
        .value(obj)
        .key(KeyFactory.keyToString(newItemKey))
        .build();
    return response;
  }

  /**
   * Maps parameters to a String for easy log display.
   *
   * @param map The values to map.
   * @return A String containing the parameter list.
   */
  public String mapToString(Map<String, String> map) {
    java.lang.StringBuilder sb = new StringBuilder();
    java.util.Iterator<java.util.Map.Entry<String, String>> iter = map.entrySet().iterator();
    while (iter.hasNext()) {
      java.util.Map.Entry<String, String> entry = iter.next();
      sb.append(entry.getKey());
      sb.append('=').append('"');
      sb.append(entry.getValue());
      sb.append('"');
      if (iter.hasNext()) {
        sb.append(',').append(' ');
      }
    }
    return sb.toString();
  }

  public ReturnMessage doUpdate(Map<String, String> itemMap) {
    //*** Log the parameters.
    //*** Make a defensive copy of the itemMap so it doesn't get mutated.
    Map <String, String> dataCopy = new HashMap();
    dataCopy.putAll(itemMap);
    String updateStatus = "SUCCESS";
    String message = "";
    String itemUpdateId = "";
    String itemKey = dataCopy.get("key");
    dataCopy.remove("key");
    message = "Successfully updated " + itemKey;
    if (itemKey != null) {
      Key dsKey = KeyFactory.stringToKey(itemKey);
      try {
        Entity currentEntity = null;
        if (value != null){
          currentEntity = value;
        }
        else{
          currentEntity = ds.get(dsKey);
        }
        //*** Fill in the created by and modified by info.
        String user = dataCopy.get("user");
        //*** If the user param was provided, assume that the service will stamp created/modified data.
        if (user != null) {
          currentEntity.setProperty("modifiedBy", user);
          currentEntity.setProperty("modifyDate", new Date());
          dataCopy.remove("user");
        }

        //*** Set the mapped fields based on the schema.
        currentEntity = setMappedFields(dataCopy, currentEntity);
       
        //*** To allow flexibility on the front end, find any leftover properties and set them
        //*** as String values.
        for (String vKey : dataCopy.keySet()) {
          //*** Don't set empty fields, to avoid fields that are mapped to a type being set as String.
          if (dataCopy.get(vKey) != null && !dataCopy.get(vKey).isEmpty()) {
            currentEntity.setUnindexedProperty(vKey, dataCopy.get(vKey));
          }
        }

        LOGGER.info("Updating " + entityName + ": " + dsKey.getName());
        Key itemUpdateKey = ds.put(currentEntity);
        itemUpdateId = KeyFactory.keyToString(itemUpdateKey);
      } catch(EntityNotFoundException ex) {
        updateStatus = "FAILURE";
        message = "Valid Key was not provided for " + entityDisplayName;
        LOGGER.warning(entityName + " identified by " + itemKey + " does not exist.");
      }
    } else {
      updateStatus = "FAILURE";
      message = "Key was not provided for " + entityDisplayName;
    }

    //*** Add the key id into the response.
    JSONObject obj = new JSONObject();
    try {
      obj.put("entityId", itemUpdateId);
    } catch (JSONException ex) {
      message = "Error converting error list to JSON: " + ex.toString();
    }

    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response = builder.status(updateStatus).message(message).value(obj).build();
    return response;
  }

  /**
   * Create filter for form parameter
   *
   * @param String formParameter
   * @param String formParameterValue
   * @return FilterPredicate formParameterFilter
   */
  protected FilterPredicate createFilterForFormParameter(
    String formParameter,
    String formParameterValue ){
    if (!( formParameterValue == null || formParameterValue.isEmpty())){
      FilterPredicate filterForFormParameter = new FilterPredicate(formParameter,
                                                                   FilterOperator.EQUAL,
                                                                   formParameterValue);
      return filterForFormParameter;
    } else{
      return null;
    }
  }

  /**
   * Parse and set phone and fax values
   * @param params Map of submited form parameters.
   * @param newValue is Entity object
   * @param replacePhones boolean true is for repleace list elements, false is for add list elements
   * @param replaceFaxes boolean true is for repleace list elements, false is for add list elements
   * @param update boolean true if update of data is in progress.
   * @param entityPrefix String perfix for Entity properties
   * @throws ValidationException
   * @return
   */
  public void parseAndSetPhoneAndFaxValues(Map<String, String> params, Entity newValue, boolean replacePhones, boolean replaceFaxes, boolean update, String entityPrefix ) throws ValidationException
  {
    List<String> listPhones = new ArrayList<String>();
    List<String> listFaxes = new ArrayList<String>();
    SortedSet<String> sortedParamKeys = new TreeSet<String>(params.keySet());
    for (String vKey : sortedParamKeys){
      if ( vKey.matches("^"+entityPrefix+"Phone[1-9][0-9]*") ){
        String keyValue = params.get( vKey );
        params.remove( vKey );
        if ( !dataValidator.isPhoneNumber( keyValue ) ){
          ValidationException validationException = new ValidationException();
          validationException.addMessage( "Not a valid "+entityPrefix+"Phone value: " + keyValue );
          throw validationException;
        } else{
          if ( !keyValue.isEmpty() ){
            listPhones.add( keyValue );
          }
        }
      }
      else if( vKey.matches("^"+entityPrefix+"Fax[1-9][0-9]*") ){
          String keyValue = params.get( vKey );
          params.remove( vKey );
          if ( !dataValidator.isPhoneNumber( keyValue ) ){
            ValidationException validationException = new ValidationException();
            validationException.addMessage( "Not a valid "+entityPrefix+"Fax value: " + keyValue );
            throw validationException;
          }else{
            if ( !keyValue.isEmpty() ){
              listFaxes.add( keyValue );
            }
          }
      }
    }
    if ( !listPhones.isEmpty() ){
      if ( replacePhones ){
        newValue.setProperty(entityPrefix+"Phone", listPhones);
      }
      else{
        List<String> currentPhone = ( List<String> ) newValue.getProperty( entityPrefix+"Phone" );
        if ( currentPhone != null ){
          List<String> current = new ArrayList<String>( currentPhone );
          current.addAll( listPhones );
          newValue.setProperty(entityPrefix+"Phone", current );
        }
        else{
          newValue.setProperty(entityPrefix+"Phone", listPhones );
        }
      }
    }else{
      String groupEmail = params.get(entityPrefix+"Email");
      if( ( groupEmail == null || groupEmail.isEmpty() ) && !update ){
        ValidationException validationException = new ValidationException();
        validationException.addMessage( entityPrefix+"Email or at least one phone number are mandatory parameters!" );
        throw validationException;
      }
      if ( update && replacePhones ){
        newValue.setProperty( entityPrefix+"Phone", listPhones );
      }
    }
    if ( !listFaxes.isEmpty() ){
      if ( replaceFaxes ){
        newValue.setProperty(entityPrefix+"Fax", listFaxes);
      }
      else{
        List<String> currentFax = ( List<String> ) newValue.getProperty( entityPrefix+"Fax" );
        if ( currentFax != null ){
          List<String> current = new ArrayList<String>( currentFax );
          current.addAll( listFaxes );
          newValue.setProperty( entityPrefix+"Fax", current );
        }
        else{
          newValue.setProperty( entityPrefix+"Fax", listFaxes );
        }
      }
    }
    else{
      if ( update && replaceFaxes ){
        newValue.setProperty( entityPrefix+"Fax", listFaxes );
      }
    }
  }

  /**
   * Wraps the simple read operation for no parent key.
   *
   * @param params
   * @param itemKey An optional key for the read operation.  If this is provided then the read
   *    operation should only read one item.
   * @return
   */
  public ReturnMessage doRead(Map<String, String> params, String itemKey) {
    return this.doRead(params, itemKey, null);
  }

  /**
   * Executes a simple read operation.
   *
   * @param params
   * @param itemKey An optional key for the read operation.  If this is provided then the read
   *    operation should only read one item.
   * @param parentKey
   * @return
   */
  public ReturnMessage doRead(Map<String, String> params, String itemKey, Key parentKey) {
    //*** If this request was for a single value, return that request.
    if (itemKey != null) {
      return this.doGet(itemKey);
    }

    //*** TODO(mgordon): Check to see if the documentation was requested from this resource.
    String itemReadStatus = "SUCCESS";
    String message = "";
    //*** Get common query parameters.
    //*** Format can either be a map or a list.  If format is empty, a map is returned.
    //*** If format is not empty, a list is returned.
    String dataFormatParam = params.get("format");
    String lengthParam = params.get("length");
    String orderByParam = params.get("orderBy");
    int maxResults = 0;

    //*** Get length parameter, check to make sure it's a positive integer.
    try{
      maxResults = checkLengthParameter(lengthParam);
    } catch(NumberFormatException ex) {
      return createReturnMessage(ex.getMessage(), "FAILURE");
    }
    String directionParam = params.get("direction");

    String logMessage = "Reading " + entityDisplayName +
                        ", length=" + lengthParam +
                        ", direction=" + directionParam +
                        ", orderBy" + orderByParam +
                        ", format=" + dataFormatParam;
    LOGGER.info(logMessage);

    //*** Assemble the query.
    if ( q == null ){
      q = new Query(entityName);
      if (orderByParam != null) {
        if (directionParam.equalsIgnoreCase("DESC")) {
          q = q.addSort(orderByParam, SortDirection.DESCENDING);
        } else {
          q = q.addSort(orderByParam, SortDirection.ASCENDING);
        }
      }  
    }

    //*** If a parent key is given, filter by parent.
    if (parentKey != null) {
      q.setAncestor(parentKey);
    }

    //*** Get the results.
    if (pq == null){
      pq = ds.prepare(q);
    }
    Iterable<Entity> entityIterator = null;
    //*** If a maximum numbr of results was provided.
    if (maxResults > 0) {
      FetchOptions returnOptions = FetchOptions.Builder.withLimit(maxResults);
      entityIterator = pq.asIterable(returnOptions);
    } else {
      entityIterator = pq.asIterable();
    }
    
    boolean deleted = false;
    //***Construct the entity JSON from the query.
    int valuesReturned = 0;
    JSONArray valueArray = new JSONArray();
    List <Map> valueMapArray = new ArrayList <Map> ();
    if (dataFormatParam == null || dataFormatParam.isEmpty()) {
      for (Entity result : entityIterator) {
        deleted = flagDeleteValue(result);
        if (!deleted){
          setAncestorFor(result);
          Map<String, Object> valueMap = modifyMap(result.getProperties(), result.getKey());
          JSONObject valueJson = new JSONObject(valueMap);
          valueArray.put(valueJson);
          valueMapArray.add(valueMap);
          valuesReturned++;
        }
      }
    }
    else {
      for (Entity result : entityIterator) {
        deleted = flagDeleteValue( result );
        if (!deleted){
          setAncestorFor(result);
          valueMapArray.add(result.getProperties());
          JSONArray valueRow = new JSONArray();
          addAppropriateEntityType( valueRow, result );
          //*** Add the extra fields that were dynamically added.
          for (String propKey : result.getProperties().keySet()) {
            if (!itemValueMap.containsKey(propKey)) {
              valueRow.put(result.getProperty(propKey));
            }
          }
          //*** Add the key at the end of the well defined fields for table manipulation.
          valueRow.put(KeyFactory.keyToString(result.getKey()));
          valuesReturned++;
          //*** Add this array to the return array.
          valueArray.put(valueRow);
        }
      }
    }
    
    message = "Returned " + valuesReturned + " " + entityDisplayName + "s.";

    //*** Return both a List (for Java) and a JSONObject (for services)
    JSONObject obj = new JSONObject();
    try {
      obj.put("values", valueArray);
    } catch (JSONException ex) {
      message = "Error converting value list to JSON: " + ex.toString();
    }

    LOGGER.info(message);
    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response = builder.status(itemReadStatus)
        .message(message)
        .value(obj)
        .valueList(valueMapArray)
        .build();
    return response;
  }
  
  /**
   * Utility function which determines if the delete flag is used.
   *
   * @param Entity Entity to check for value.
   * @return boolean If true, the delete flag is set, if false, it wasn't set.
   */
  protected boolean flagDeleteValue(Entity value){
    boolean deleted = false;
    if (!value.hasProperty("deleted")){
      deleted = false;
    }
    else if (value.getProperty("deleted").equals("false")){
      deleted = false;
    }
    else if (value.getProperty("deleted").equals("true")){
      deleted = true;
    }
    return deleted;
  }

  /**
   * Modifies an Entity property map to return to the service.  Converts text fields to String and
   * text list fields to a String list.  Also adds the key as a parameter.
   * @param Map< String, Object >  properties is Entity properties.
   * @return Map< String, Object > modifiedMap is modified Map.
   */
  protected Map<String,Object> modifyMap(Map<String, Object> properties, Key key){
    Map<String,Object> modifiedMap = new HashMap();
    for (String propKey : properties.keySet()){
      if (itemValueMap.containsKey(propKey)) {
        switch(itemValueMap.get(propKey)) {
          case TEXT:
            Text itemText = (Text)properties.get(propKey);
            modifiedMap.put(propKey, itemText.getValue());
            break;
          case TEXT_LIST:
            modifiedMap.put(propKey, convertTextListToStringList((List<Text>)properties.get(propKey)));
            break;
          default:
            modifiedMap.put(propKey, properties.get(propKey));
            break;
        }
      } else {
        modifiedMap.put(propKey, properties.get(propKey));
      }
    }
    //*** Add the key.
    modifiedMap.put("key", KeyFactory.keyToString(key));
    return modifiedMap;
  }
  
  
  /**
   * Adds to response appropriate Entity value Type
   * @param JSONArray jsonArray JSON array that will be returned as response.
   * @param Entity result is entity value retrieved from database
   * jsnoArray value is modified in this method.
   */	
  protected void addAppropriateEntityType(JSONArray jsonArray, Entity result){
    for (String propKey : itemValueMap.keySet()) {
      switch(itemValueMap.get(propKey)) {
        case BLOB:
          Blob bValue = (Blob)result.getProperty(propKey);
          jsonArray.put(bValue);
          break;
        case BLOB_KEY:
          BlobKey bkValue = (BlobKey)result.getProperty(propKey);
          jsonArray.put(bkValue);
          break;
        case DATE:
          Date date = (Date)result.getProperty(propKey);
          jsonArray.put(date.getTime());
          break;
        case DECIMAL:
          Double dValue = (Double)result.getProperty(propKey);
          jsonArray.put(dValue);
          break;
        case INTEGER:
          //*** Note, this looks strange, but Google app engine stores all ints as long values,
          //*** so the values must be downconverted.
          Long iValue = (Long)result.getProperty(propKey);
          jsonArray.put(iValue.intValue());
          break;
        case LONG:
          Long lValue = (Long)result.getProperty(propKey);
          jsonArray.put(lValue);
          break;
        case PHONE_NUMBER:
          PhoneNumber pValue = (PhoneNumber)result.getProperty(propKey);
          jsonArray.put(pValue);
          break;
        case STRING:
          jsonArray.put(result.getProperty(propKey));
          break;
        case TEXT:
          Text tValue = (Text)result.getProperty(propKey);
          if (tValue != null) {
            jsonArray.put(tValue.getValue());
          } else {
            //*** Put this value here as a placeholder.
            jsonArray.put(tValue);
          }
          break;
        case TEXT_LIST:
          jsonArray.put(convertTextListToStringList((List<Text>) result.getProperty(propKey)));
          break;
        default:
          jsonArray.put(result.getProperty(propKey));
          break;
     }
   }
  }
  
  /** Converts List of Text values to List of String values
    * @param List<Text> list of Text objects
    * @return List<String> list of String objects 
  */
  protected List<String> convertTextListToStringList(List<Text> listText){
    List<String> listString = new ArrayList<String>();
    if( listText == null ){
      return null;
    }
    for( Text vKey: listText ){
      listString.add(vKey.getValue());
    }
    return listString;
  }

  
  /**
   * Executes a simple read operation, given the item's GAE key.
   * @param itemKey The GAE key of the item to get.
   * @return A status, message, and the item's data.
   */
  public ReturnMessage doGet(String itemKey) {
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

    LOGGER.info(message);
    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response =
        builder.status(itemGetStatus).message(message).value(valueJson).build();
    return response;
  }

  /**
   * Executes the delete of an entity given the key.
   *
   * @param itemKey The GAE key of the item to delete.
   * @param displayNameField The name of the field to display when assembling the delete message.
   * @return A status, and message of the delete operation.
   */
  public ReturnMessage doDelete(String itemKey, String displayNameField) {
    String itemDeleteStatus = "SUCCESS";
    String message = "";
    Key dsKey;

    if (itemKey != null) {
      dsKey = KeyFactory.stringToKey(itemKey);
      try {
        Entity value = ds.get(dsKey);
        if (displayNameField != null) {
          message = "Deleting item " +
                    value.getProperty(displayNameField) +
                    " identified by key " + itemKey;
        } else {
          message = "Deleting item identified by key " + itemKey;
        }
        LOGGER.info(message);
        ds.delete( dsKey );
      } catch (Exception ex) {
        message = "Delete action on "+entityDisplayName + " with Key: "+itemKey+" throw following exception: "+ex.getMessage();
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

  /**
   * Helper function to map input fields to the entity schema, given the prepared entity and
   *
   * @param itemMap Input values provided for the entity.
   * @param item The current {@link Entity}.
   * @return An updated {@link Entity} with the values filled in with the correct type.
   */
  protected Entity setMappedFields(Map<String, String> itemMap, Entity item) {
    //*** TODO(mgordon): Add checks for required fields and other validation.
    for (String formKey : itemValueMap.keySet()) {
      String formValue = itemMap.get(formKey);
      if (formValue != null && (!formValue.isEmpty())) {
        switch(itemValueMap.get(formKey)) {
          case BLOB:
            item.setProperty(formKey, new Blob(formValue.getBytes()));
            break;
          case BLOB_KEY:
            item.setProperty(formKey, new BlobKey(formValue));
            break;
          case DATE:
            long timeVal = Long.parseLong(formValue);
            Date valueDate = new Date(timeVal);
            item.setProperty(formKey, valueDate);
            break;
          case DECIMAL:
            item.setProperty(formKey, Double.parseDouble(formValue));
            break;
          case INTEGER:
            item.setProperty(formKey, Integer.parseInt(formValue));
            break;
          case LONG:
            item.setProperty(formKey, Long.parseLong(formValue));
            break;
          case PHONE_NUMBER:
            item.setProperty(formKey, new PhoneNumber(formValue));
            break;
          case STRING:
            item.setProperty(formKey, formValue);
            break;
          case TEXT:
            item.setProperty(formKey, new Text(formValue));
            break;
          case SHORT_BLOB:
            item.setProperty(formKey, new ShortBlob(formValue.getBytes()));
            break;
          default:
            item.setProperty(formKey, formValue);
            break;
        }
        itemMap.remove(formKey);
      }
    }
    return item;
  }
  
  /**
   * Combines a set of input parameters and an existing entity into a set of String parameters
   * for use in subclasses.
   * 
   * @param e The entity that exists.
   * @param params The parameters of the update operation.
   * @return A combined map of values, filled in with parameter values, then entity values if the
   *     parameter doesn't exist.
   */
  protected Map<String, String> getUpdateMapEntity(Entity e, Map<String, String> params) {
    Map<String, String> output = new HashMap <String, String>();
    output.putAll(params);
    for (String key : itemValueMap.keySet()) {
      if (params.containsKey(key) && !params.get(key).isEmpty()) {
        output.put(key, params.get(key));
      } else {
        switch(itemValueMap.get(key)) {
          case BLOB:
            output.put(key, (String)e.getProperty(key));
            break;
          case BLOB_KEY:
            output.put(key, (String)e.getProperty(key));
            break;
          case DATE:
            Date tempDate = (Date)e.getProperty(key);
            output.put(key, String.valueOf(tempDate.getTime()));
            break;
          case DECIMAL:
            Double tempDouble = (Double)e.getProperty(key);
            output.put(key, tempDouble.toString());
            break;
          case INTEGER:
            Long tempLongToInt = (Long)e.getProperty(key);
            Integer tempInteger = tempLongToInt.intValue();
            output.put(key, tempInteger.toString());
            break;
          case LONG:
            Long tempLong = (Long)e.getProperty(key);
            output.put(key, tempLong.toString());
            break;
          case PHONE_NUMBER:
            output.put(key, (String)e.getProperty(key));
            break;
          case STRING:
            output.put(key, (String)e.getProperty(key));
            break;
          case TEXT:
            output.put(key, (String)e.getProperty(key));
            break;
          case SHORT_BLOB:
            output.put(key, (String)e.getProperty(key));
            break;
          default:
            output.put(key, (String)e.getProperty(key));
            break;
        } //*** End switch.
      }  //*** End if.
    } //*** End for.
    return output;
  }

  protected JSONObject createJSONFromKey(Key key) {
    JSONObject obj = new JSONObject();
    try {
      obj.put("key", KeyFactory.keyToString(key));
    } catch (JSONException ex) {
      LOGGER.severe("Could not convert key to JSON: " + ex.toString());
    }
    return obj;
  }
  
  /**
   * Creates return message in JSON format.
   * @param message String message that describes status in more detail.
   * @param status String is message status.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  protected ReturnMessage createReturnMessage(String message, String status){
    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response = builder.status(status).message(message).value(null).build();
    return response;
  }
/** Check length parameter, check to make sure it's a positive integer.a
 * @param String length parameter
 * @throws NumberFormatException
 * @return Integer length as Integer
 */
  protected Integer checkLengthParameter( String length ) throws NumberFormatException {
    int maxResults;
    if (length != null && !length.isEmpty()){
      maxResults = Integer.parseInt(length);
      if (maxResults < 1){
        throw new NumberFormatException("Result set length must be a positive integer!");
      }
      return maxResults;
    }
    return 0;
  }

/**
   * Returns Entity search index based on its name.
   * @return Index is search API index.
   */
  public Index getIndex(String name) {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName(name).build();
    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
  }

/**
   * Returns list of ScoredDocument resulsts for search API query string.
   * @param String queryString is search fuzzy string.
   * @param int limit is limit for number of results. If set to zero default limit is 1000.
   * @param String returnedField document filed that will be returned.
   * @param Index gae API Index object for which we are doing search.
   * @return Results<ScoredDocument> search API socred document results according to query string. If nothing found, value is null.
*/
  public Results<ScoredDocument> findDocuments(String queryString, int limit, String returnedField, Index index) {
    SortExpression.SortDirection direction = SortExpression.SortDirection.DESCENDING;

    if ( limit == 0 ){
      limit = 1000;
    }
    try {
      SortOptions sortOptions = SortOptions.newBuilder()
          .setLimit(limit)
          .build();
      QueryOptions options = QueryOptions.newBuilder()
          .setLimit(limit)
          .setFieldsToReturn(returnedField)
          .setSortOptions(sortOptions)
          .build();
      com.google.appengine.api.search.Query query = com.google.appengine.api.search.Query.newBuilder().setOptions(options).build(queryString);
      return index.search(query);
    } catch (SearchException e) {
      LOGGER.info( "Search request with query " + queryString + " failed: "+ e.getMessage() );
      return null;
    }
  }
  
/**
 * Sets entity ancestor property
 * @param Entity value gae entity
 */
  protected void setAncestorFor(Entity value){
    Key parentKey = value.getParent();
    if(parentKey != null){
      value.setProperty("ancestor", KeyFactory.keyToString(parentKey));
    }
  }
}
