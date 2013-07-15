/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.common;

import com.google.appengine.api.datastore.Entity;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 * POJO class to provide a return message from Manager level classes.
 * 
 * @author Mike Gordon.
 */
public class ReturnMessage {
  //*** Status of the operation.
  private String status;
  //*** Message resulting from the operation.
  private String message;
  //*** The JSON value to send back to the client.
  private JSONObject value;
  //*** A list of the values returned, for use within the services layer.
  private List <Map> valueList;
  //*** The key of created/updated Entities.
  private String key;
  //*** The entity to store the data in.
  private List <Entity> entities;

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  private void setStatus(String status) {
    this.status = status;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message the message to set
   */
  private void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the value
   */
  public JSONObject getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  private void setValue(JSONObject value) {
    this.value = value;
  }

  /**
   * @return the valueList
   */
  public List<Map> getValueList() {
    return valueList;
  }

  /**
   * @param valueList the valueList to set
   */
  private void setValueList(List<Map> valueList) {
    this.valueList = valueList;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key the key to set
   */
  private void setKey(String key) {
    this.key = key;
  }

  /*
   * Returns the entities as a CSV.
   */
  public String getCSV() {
    //***Construct the entity JSON from the query.
    String csv = "";
    //*** Create the header.
    String delimiter = "";
    if (!entities.isEmpty()) {
      for (String key : getEntities().get(0).getProperties().keySet()) {
        csv += delimiter + key;
        delimiter = ",";
      }
      csv += "\n";

      //*** Create the body.
      for (Entity result : getEntities()) {
        Map<String, Object> valueMap = result.getProperties(); 
        delimiter = "";
        for (String key : valueMap.keySet()) {
          csv += delimiter + valueMap.get(key).toString();
          delimiter = ",";
        }
        csv += "\n";
      }
    } else {
      csv = "NO DATA FOUND!";
    }
    return csv;
  }

  /**
   * @return the entities
   */
  public List <Entity> getEntities() {
    return entities;
  }

  /**
   * @param entities the entities to set
   */
  public void setEntities(List <Entity> entities) {
    this.entities = entities;
  }

  /**
   * Builder class for the ServerParamDescriptor.
   */
  public static class Builder {
    private String bStatus;
    private String bMessage;
    private JSONObject bValue;
    private List<Map> bValueList;
    private String bKey;
    private List <Entity> bEntities;

    /**
     * Builds an immutable ReturnMessage from the provided values.
     * @return The finished {@link ReturnMessage}.
     */
    public ReturnMessage build() {
      ReturnMessage rMessage = new ReturnMessage();
      rMessage.setStatus(bStatus);
      rMessage.setMessage(bMessage);
      rMessage.setValue(bValue);
      rMessage.setValueList(bValueList);
      rMessage.setKey(bKey);
      rMessage.setEntities(bEntities);
      return rMessage;
    }
    
    public ReturnMessage.Builder message(String value) {
      this.bMessage = value;
      return this;
    }
    
    public ReturnMessage.Builder status(String value) {
      this.bStatus = value;
      return this;
    }
    
    public ReturnMessage.Builder value(JSONObject value) {
      this.bValue = value;
      return this;
    }

    public ReturnMessage.Builder valueList(List<Map> value) {
      this.bValueList = value;
      return this;
    }
    
    public ReturnMessage.Builder key(String value) {
      this.bKey = value;
      return this;
    }

    public ReturnMessage.Builder entities(List<Entity> value) {
      this.bEntities = value;
      return this;
    }
  }
}
