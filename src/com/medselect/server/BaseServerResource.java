/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.server;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.medselect.application.ApplicationManager;
import com.medselect.common.BaseManager;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import com.medselect.util.ValidationException;
import com.medselect.util.Constants;
import java.util.Date;
import java.util.logging.Logger;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

/**
 * @author Mike Gordon
 *
 * The BaseServerResource provides the following base functions:
 *
 * 1. Provides a function to wrap each JSON message in the standard application JSON format.
 */
public class BaseServerResource extends ServerResource {
  protected static final Logger LOGGER = Logger.getLogger(BaseServerResource.class.getName());
  protected static final String AUTH_KEY_PROP = "com.uptempo.appAuthKey";
  protected final DatastoreService ds;
  //*** Key used to distinguish between get one item and get many.
  protected String itemKey;
  
  //*** Map that sets the structure of the entity that this resource supports.
  //*** The key is the property name, the value is the property type.
  protected Map<String, BaseManager.FieldType> itemValueMap;
  //*** The name of the entity for the Datastore.
  protected String entityName;
  //*** The name of the entity for display to the front end.
  protected String entityDisplayName;
  //*** Indicates whether documentation URL was requested.
  protected boolean isDocumentation = false;
  //*** Config manager shared to subclasses.
  protected ConfigManager cManager = new ConfigManager();
  
  public BaseServerResource() {
    ds = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  /**
   * Initializes the resource by looking for the key parameter in the URL.  The URL mapping should
   * be setup so that the item key at the end of the URL maps to the string 'key' as such:
   *
   * /service/entity/{key}
   */
  protected void doInit() throws ResourceException {
    // If the application key is not empty, fill it in.
    if (!getRequest().getAttributes().isEmpty()) {
      itemKey = (String)getRequest().getAttributes().get("key");
    } else {
      itemKey = null;
    }

    //*** Check for proper service authentication.
    SimpleConfigValue keyFlag =
        cManager.getSimpleConfigValue(Constants.COMMON_APP, Constants.API_SECURITY_FLAG);
    if (keyFlag != null && keyFlag.getConfigValue().equalsIgnoreCase("TRUE")) {
      String authKey = System.getProperty(AUTH_KEY_PROP);
      Series<Parameter> headers =
          (Series<Parameter>)getRequest().getAttributes().get("org.restlet.http.headers");
      String clientKey = headers.getFirstValue("uptempokey");
      if (clientKey == null) {
        clientKey = "";
      }
      
      if (!authKey.equals(clientKey)) {
        //*** The master key didn't match, so check the stored application keys.
        ApplicationManager appManager = new ApplicationManager();
        if (!appManager.isValidKey(clientKey)) {
          throw new ResourceException(
              Status.CLIENT_ERROR_UNAUTHORIZED, "Client authorization key bad/missing!");
        }
      }
    }
    
  }
  
  /**
   * Checks whether the URL contains "documentation".
   */
  protected void doCheckForDoc() {
    if(this.getReference().getPath().contains("documentation")) {
      this.isDocumentation = false;
    }
  }

  /**
   * Writes out the documentation for this resource.
   * @return A {@link StringRepresentation} with the resource documentation.
   */
  protected StringRepresentation writeDocs() {
    String displayDoc = "<h2>Get/Post/Put Parameters</h2>";
    for (String mapItemName : itemValueMap.keySet()) {
      displayDoc += mapItemName + "<br />";
    }
    StringRepresentation r = new StringRepresentation(displayDoc);
    return r;
  }
  
  /**
   * Creates a {@link JsonRepresentation} that contains the operation status, message, and data.
   *
   * @param status Status of the operation, usually SUCCESS|FAILURE
   * @param message Message returned by the operation.
   * @param data JSON data returned by the operation.
   * @return The JSON data containing the result of the operation.
   */
  protected JsonRepresentation getJsonRepresentation(
      String status,
      String message,
      JSONObject data) {
    JSONObject newObj = new JSONObject();
    JsonRepresentation r;

    try {
      newObj.put("status", status);
      newObj.put("message", message);
      newObj.put("data", data);
      r = new JsonRepresentation(newObj);
    } catch (JSONException ex) {
      String jsonError = "{status:\"FAILED\"," +
                         "message: \"" + ex.toString() + "\"}";
      r = new JsonRepresentation(jsonError);
    }

    return r;
  }

  protected JSONArray getJsonErrorMessage(ValidationException ex) {
    JSONArray messageList = new JSONArray(ex.getMessageList());
    return messageList;
  }
  
  //***Functions for doing operations on generic types.
  //***Used for fast implementation of new services with minimum logic.
  
  /**
   * Implement a generic GET operation using a {@link Map} to define the
   * fields and field types.
   * 
   * @return 
   */
  protected Representation doGet(Form itemForm) {
    String itemGetStatus = "SUCCESS";
    String message = "";
    Key dsKey;
    Entity value = null;
    JSONObject valueJson = null;
    if (itemKey != null) {
      dsKey = KeyFactory.stringToKey(itemKey);
      try {
        value = ds.get(dsKey);
        message = "Returned 1 " + entityDisplayName + ".";
        valueJson = new JSONObject(value.getProperties());
      } catch (EntityNotFoundException ex) {
        LOGGER.warning(entityDisplayName + " identified by " + itemKey + " does not exist.");
        message = entityDisplayName + " not found!";
        itemGetStatus = "FAILURE";
      }
    }

    LOGGER.info(message);
    JsonRepresentation response = this.getJsonRepresentation(itemGetStatus, message, valueJson);
    return response;
  }
  
  protected Representation doRead(Form itemForm) {
    //*** If this request was for a single value, return that request.
    if (itemKey != null) {
      return this.doGet(itemForm);
    }

    //*** TODO(mgordon): Check to see if the documentation was requested from this resource.
    String itemReadStatus = "SUCCESS";
    String message = "";
    //*** Get common query parameters.
    //*** Format can either be a map or a list.  If format is empty, a map is returned.
    //*** If format is not empty, a list is returned.
    String dataFormatParam = itemForm.getFirstValue("format");
    String lengthParam = itemForm.getFirstValue("length");
    String orderByParam = itemForm.getFirstValue("orderBy");
    String directionParam = itemForm.getFirstValue("direction");
    String logMessage = "Reading " + entityDisplayName +
                        ", length=" + lengthParam +
                        ", direction=" + directionParam;
    LOGGER.info(logMessage);

    //*** Assemble the query.
    Query q = null;
    q = new Query(entityName);
    if(directionParam != null && orderByParam != null ){
      if (directionParam.equalsIgnoreCase("DESC")) {
        q = q.addSort(orderByParam, SortDirection.DESCENDING);
      }else {
        q = q.addSort(orderByParam, SortDirection.ASCENDING);
      }
    }
    //*** Get the results.
    PreparedQuery pq = ds.prepare(q);

    //***Construct the entity JSON from the query.
    int valuesReturned = 0;
    JSONArray valueArray = new JSONArray();
    if (dataFormatParam == null || dataFormatParam.isEmpty()) {
      for (Entity result : pq.asIterable()) {
        JSONObject valueJson = new JSONObject(result.getProperties());
        valueArray.put(valueJson);
        valuesReturned++;
      }
    } else {
      for (Entity result : pq.asIterable()) {
        JSONArray valueRow = new JSONArray();
        for (String propKey : itemValueMap.keySet()) {
          switch(itemValueMap.get(propKey)) {
            case BLOB:
              Blob bValue = (Blob)result.getProperty(propKey);
              valueRow.put(bValue);
              break;
            case DATE:
              Date date = (Date)result.getProperty(propKey);
              valueRow.put(date.getTime());
              break;
            case DECIMAL:
              Double dValue = (Double)result.getProperty(propKey);
              valueRow.put(dValue);
              break;
            case INTEGER:
              Integer iValue = (Integer)result.getProperty(propKey);
              valueRow.put(iValue);
              break;
            case LONG:
              Long lValue = (Long)result.getProperty(propKey);
              valueRow.put(lValue);
              break;
            case PHONE_NUMBER:
              PhoneNumber pValue = (PhoneNumber)result.getProperty(propKey);
              valueRow.put(pValue);
              break;
            case STRING:
              valueRow.put(result.getProperty(propKey));
              break;
            case TEXT:
              Text tValue = (Text)result.getProperty(propKey);
              valueRow.put(tValue);
              break;
            default:
              valueRow.put(result.getProperty(propKey));
              break;
          }
        }
        //*** Add the key at the end of the well defined fields for table manipulation.
        valueRow.put(KeyFactory.keyToString(result.getKey()));

        //*** Add the extra fields that were dynamically added.
        for (String propKey : result.getProperties().keySet()) {
          if (!itemValueMap.containsKey(propKey)) {
            valueRow.put(result.getProperty(propKey));
          }
        }

        valuesReturned++;
        //*** Add this array to the return array.
        valueArray.put(valueRow);
      }
    }

    message = "Returned " + valuesReturned + " " + entityDisplayName + "s.";

    JSONObject obj = new JSONObject();
    try {
      obj.put("values", valueArray);
    } catch (JSONException ex) {
      message = "Error converting error list to JSON: " + ex.toString();
    }

    LOGGER.info(message);
    JsonRepresentation response = this.getJsonRepresentation(itemReadStatus, message, obj);

    return response;
  }

  /**
   * Execute an update of a generic Entity.
   * @param itemForm
   * @return
   */
  protected Representation doPut(Form itemForm) {
    String updateStatus = "SUCCESS";
    String message = "";
    String itemUpdateId = "";
    itemKey = itemForm.getFirstValue( "key" );
    LOGGER.info(itemKey);
    message = "Successfully updated " + itemKey;
    if (itemKey != null) {
      Key dsKey = KeyFactory.stringToKey(itemKey);
      try {
        Entity currentEntity = ds.get(dsKey);
        //*** Fill in the created by and modified by info.
        String user = itemForm.getFirstValue("user");
        //*** If the user param was provided, assume that the service will stamp created/modified data.
        if (user != null) {
          currentEntity.setProperty("modifiedBy", user);
          currentEntity.setProperty("modifyDate", new Date());
          itemForm.removeAll("user");
        }
        for (String formKey : itemValueMap.keySet()) {
          String formValue = itemForm.getFirstValue(formKey);
          if (formValue != null) {
            switch(itemValueMap.get(formKey)) {
              case BLOB:
                currentEntity.setProperty(formKey, new Blob(formValue.getBytes()));
                break;
              case DATE:
                long timeVal = Long.parseLong(formValue);
                Date valueDate = new Date(timeVal);
                currentEntity.setProperty(formKey, valueDate);
                break;
              case DECIMAL:
                currentEntity.setProperty(formKey, Double.parseDouble(formValue));
                break;
              case INTEGER:
                currentEntity.setProperty(formKey, Integer.parseInt(formValue));
                break;
              case LONG:
                currentEntity.setProperty(formKey, Long.parseLong(formValue));
                break;
              case PHONE_NUMBER:
                currentEntity.setProperty(formKey, new PhoneNumber(formValue));
                break;
              case STRING:
                currentEntity.setProperty(formKey, formValue);
                break;
              case TEXT:
                currentEntity.setProperty(formKey, new Text(formValue));
                break;
              default:
                currentEntity.setProperty(formKey, formValue);
                break;
            }
            itemForm.removeAll(formKey);
          }
        }
        //*** To allow flexibility on the front end, find any leftover properties and set them
        //*** as String values.
        Map <String, String> rValues = itemForm.getValuesMap();
        for (String vKey : rValues.keySet()) {
          currentEntity.setProperty(vKey, rValues.get(vKey));
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
 
    JsonRepresentation a = this.getJsonRepresentation(updateStatus, message, obj);
    return a;
  }

  /**
   * Implements a generic POST operation to write an entity.
   * @param itemForm RESTlet form with submitted values.
   * @param keyRequired Indicates whether a unique identifier is required to generate the App Engine
   *    key.  If true, then the method looks for the 'key' element in the form.  If false, no
   *    key is required and the put() operation generates the key.
   * @return
   */
  protected Representation doPost(Form itemForm, boolean keyRequired) {
    String insertStatus = "SUCCESS";
    String message = "";
    String newItemId = "";

    if (itemForm.getFirstValue("key") != null || !keyRequired) {
      Entity newValue;
      Key dsKey = null;
      if (keyRequired) {
        String keyValue = itemForm.getFirstValue("key");
        itemForm.removeAll("key");
        dsKey = KeyFactory.createKey(entityName, keyValue);
        newValue = new Entity(dsKey);
      } else {
        newValue = new Entity(entityName);
      }

      //*** Fill in the created by and modified by info.
      String user = itemForm.getFirstValue("user");
      //*** If the user param was provided, assume that the service will stamp created/modified data.
      if (user != null) {
        newValue.setProperty("createdBy", user);
        newValue.setProperty("createDate", new Date());
        newValue.setProperty("modifiedBy", user);
        newValue.setProperty("modifyDate", new Date());
        itemForm.removeAll("user");
      }

      //*** Loop through the explicitly defined values for this entity.
      //*** If the submitted value is != null, create the correct data type for it.
      //*** TODO: Add required field validation here
      for (String formKey : itemValueMap.keySet()) {
        String formValue = itemForm.getFirstValue(formKey);
        if (formValue != null) {
          switch(itemValueMap.get(formKey)) {
            case BLOB:
              newValue.setProperty(formKey, new Blob(formValue.getBytes()));
              break;
            case DATE:
              long timeVal = Long.parseLong(formValue);
              Date valueDate = new Date(timeVal);
              newValue.setProperty(formKey, valueDate);
              break;
            case DECIMAL:
              newValue.setProperty(formKey, Double.parseDouble(formValue));
              break;
            case INTEGER:
              newValue.setProperty(formKey, Integer.parseInt(formValue));
              break;
            case LONG:
              newValue.setProperty(formKey, Long.parseLong(formValue));
              break;
            case PHONE_NUMBER:
              newValue.setProperty(formKey, new PhoneNumber(formValue));
              break;
            case STRING:
              newValue.setProperty(formKey, formValue);
              break;
            case TEXT:
              newValue.setProperty(formKey, new Text(formValue));
              break;
            default:
              newValue.setProperty(formKey, formValue);
              break;
          }
          itemForm.removeAll(formKey);
        }
      }
      //*** To allow flexibility on the front end, find any leftover properties and set them
      //*** as String values.
      Map <String, String> rValues = itemForm.getValuesMap();
      for (String vKey : rValues.keySet()) {
        newValue.setProperty(vKey, rValues.get(vKey));
      }
      
      if (dsKey != null) {
        LOGGER.info("Creating " + entityName + ": " + dsKey.getName());
      } else {
        LOGGER.info("Creating " + entityName);
      }
 
      Key newItemKey = ds.put(newValue);
      newItemId = KeyFactory.keyToString(newItemKey);
    } else {
      insertStatus = "FAILURE";
      message = "Key was not provided for " + entityDisplayName;
    }

    //*** Add the key id into the response.
    JSONObject obj = new JSONObject();
    try {
      obj.put("entityId", newItemId);
    } catch (JSONException ex) {
      message = "Error converting error list to JSON: " + ex.toString();
    }
    
    JsonRepresentation a = this.getJsonRepresentation(insertStatus, message, null);
    return a;
  }

  /**
   * Execute the delete function for an item.
   * @return The JSON result from the delete operation.
   */
  protected Representation doDelete() {
    String itemDeleteStatus = "SUCCESS";
    String message = "";
    Key dsKey;

    if (itemKey != null) {
      dsKey = KeyFactory.stringToKey(itemKey);
      try {
        Entity value = ds.get(dsKey);
        ds.delete(dsKey);
        message = "Deleting item identified by key " + itemKey;
      } catch (EntityNotFoundException ex) {
        message = entityDisplayName + " not found!";
        itemDeleteStatus = "FAILURE";
      }
    }

    JsonRepresentation a = this.getJsonRepresentation(itemDeleteStatus, message, null);

    return a;
  }
}
