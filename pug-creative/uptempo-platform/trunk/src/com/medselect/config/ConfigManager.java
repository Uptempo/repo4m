/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.config;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Text;
import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

import java.util.Map;
import org.json.JSONException;

/**
 * Class to manage configuration values.
 * @author Mike Gordon
 */
public class ConfigManager extends BaseManager {
  public static final String CONFIG_ENTITY_NAME = "ConfigValue";
  public static final String CONFIG_DISPLAY_NAME = "Configuration Value";
  public static final Map<String, BaseManager.FieldType> CONFIG_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("appCode", BaseManager.FieldType.STRING)
          .put("name", BaseManager.FieldType.STRING)
          .put("description", BaseManager.FieldType.STRING)
          .put("value", BaseManager.FieldType.STRING)
          .put("text", BaseManager.FieldType.TEXT)
          .put("image", BaseManager.FieldType.STRING)
          .build();

  public ConfigManager() {
    super(CONFIG_STRUCTURE, CONFIG_ENTITY_NAME, CONFIG_DISPLAY_NAME);
  }

  public ReturnMessage insertConfigValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the config value information from the request
    String valueApp = params.get("appCode");
    String valueName = params.get("name");
    String valueDescription = params.get("description");
    String valueValue = params.get("value");
    //***This will either be URL encoded or Base64.
    String valueText = params.get("text");
    String valueImageId = params.get("imageid");
    String userEmail = params.get("user");

    //*** TODO(mgordon): Add required validation.
    //*** Do transformations of the app and name
    valueApp = valueApp.toUpperCase();
    valueName = valueName.toUpperCase();

    Key dsKey = KeyFactory.createKey("ConfigValue", valueApp + valueName);
    Entity newValue = new Entity(dsKey);

    newValue.setProperty("appCode", valueApp);
    newValue.setProperty("name", valueName);
    newValue.setUnindexedProperty("description", valueDescription);
    newValue.setUnindexedProperty("value", valueValue);
    newValue.setProperty("text", new Text(valueText));
    newValue.setUnindexedProperty("valueImageId", valueImageId);
    newValue.setProperty("createdBy", userEmail);
    newValue.setProperty("createDate", new Date());
    newValue.setProperty("modifiedBy", userEmail);
    newValue.setProperty("modifyDate", new Date());

    LOGGER.info("Creating config value " + dsKey.getName());
    Key newConfigKey = ds.put(newValue);
    JSONObject obj = this.createJSONFromKey(newConfigKey);

    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response = builder.status(insertValueStatus).message(message).value(obj).build();
    return response;
  }

  public ReturnMessage updateConfigValue(Map<String, String> params, String configValueKey) {
    String configUpdateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
 
    Entity updateValue = null;

    if (configValueKey != null) {
      dsKey = KeyFactory.stringToKey(configValueKey);
      try {
        updateValue = ds.get(dsKey);
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("Config value identified by " + configValueKey + " does not exist.");
        message = "Config value not found!";
        configUpdateStatus = "FAILURE";
      }
    }

    //***Set the updated values.
    if (configUpdateStatus.equals("SUCCESS")) {
      //***Read the config value information from the request
      String valueName = params.get("name");
      String valueDescription = params.get("description");
      String valueValue = params.get("value");
      //***This will either be URL encoded or Base64.
      String valueText = params.get("text");
      String valueImageId = params.get("imageid");
      String userEmail = params.get("user");

      if (valueName != null) {
        updateValue.setProperty("name", valueName);
      }
      if (valueValue != null) {
        updateValue.setUnindexedProperty("value", valueValue);
      }
      if (valueText != null) {
        updateValue.setProperty("text", new Text(valueText));
      }
      if (valueImageId != null) {
        updateValue.setUnindexedProperty("valueImageId", valueImageId);
      }
      updateValue.setProperty("modifiedBy", userEmail);
      updateValue.setProperty("modifyDate", new Date());

      ds.put(updateValue);
      String configValueApp = (String)updateValue.getProperty("appCode");
      String configValueName = (String)updateValue.getProperty("name");
      message = "Config value for app " +
                configValueApp +
                " with name " +
                configValueName +
                " updated.";
      LOGGER.info(message);
    }

    ReturnMessage.Builder builder = new ReturnMessage.Builder();
    ReturnMessage response = builder.status(configUpdateStatus).message(message).build();
    return response;
  }

  public ReturnMessage readConfigValues(Map<String, String> params, String itemKey) {
    if (params.containsKey("appCode")) {
      q = new Query(CONFIG_ENTITY_NAME);
      Query.Filter appFilter =
          new Query.FilterPredicate("appCode", Query.FilterOperator.EQUAL, params.get("appCode"));
      q.setFilter(appFilter);
    }
    return this.doRead(params, itemKey);
  }

  public ReturnMessage deleteConfigValue(String valueKey) {
    return this.doDelete(valueKey, "name");
  }

  /**
   * Gets a single Config value from the Datastore, using the appCode and configCode.
   * 
   * @param application The application to which the config value belongs.
   * @param configCode The code of the config value.
   * @return 
   */
  public ReturnMessage getConfigValueFromCode(String application, String configCode){
    String configGetStatus = "SUCCESS";
    String message = "";
    Entity value = null;
    List<Map> entityList = new ArrayList<Map>();
 
    if (configCode != null) {
      //*** Prepare the query with two search parameters.
      Query q = new Query(entityName);
      Query.Filter appFilter =
            new Query.FilterPredicate("appCode", Query.FilterOperator.EQUAL, application);
      Query.Filter codeFilter =
            new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, configCode);
      Filter configFilter = CompositeFilterOperator.and(appFilter, codeFilter);
      q.setFilter(appFilter).setFilter(configFilter);
      PreparedQuery pq = ds.prepare(q);
      value = pq.asSingleEntity();
      if (value != null) {
        message = "Returned 1 config value: " + value.getProperty("name");
        entityList.add(value.getProperties());
      } else {
        message = "Config value not found!";
        configGetStatus = "FAILURE";
      }
    }

    LOGGER.info(message);
    ReturnMessage.Builder builder = new ReturnMessage.Builder()
        .message(message)
        .status(configGetStatus)
        .valueList(entityList);
    ReturnMessage response = builder.build();
    return response;
  }
  
  /*
   * Gets the config values for the specified application.
   * 
   * @param application The application to search.
   * @return A message indicating success or failure and the values.
   */
  public List<Map> getConfigValuesForApp(String application) {
    String message = "";
    List<Entity> values;
    List<Map> entityValueList = new ArrayList<Map>();
 
    if (application != null) {
      //*** Prepare the query with two search parameters.
      Query q = new Query(entityName);
      Query.Filter appFilter =
            new Query.FilterPredicate("appCode", Query.FilterOperator.EQUAL, application);
      q.setFilter(appFilter);
      PreparedQuery pq = ds.prepare(q);
      int returnSize = 0;
      for (Entity value : pq.asIterable()) {
        entityValueList.add(value.getProperties());
        returnSize++;
      }
      if (returnSize > 0) {
        message =
            "Returned " + Integer.toString(returnSize) + " config values for app " + application;
      } else {
        message = "Config values not found for app " + application;
      }
    }

    LOGGER.info(message);
    return entityValueList;
  }

  /**
   * Wrapper to get a Config Value and return a simple Java class instead of a ReturnMessage.
   * @param application The application to which the config value belongs.
   * @param configCode The code of the config value.
   * @return The populated config value.
   */
  public SimpleConfigValue getSimpleConfigValue(String application, String configCode) {
    ReturnMessage message = this.getConfigValueFromCode(application, configCode);
    SimpleConfigValue configValue = new SimpleConfigValue();
    
    if (!message.getValueList().isEmpty()) {
      Map <String, Object> valueMap = message.getValueList().get(0);
      configValue.setAppCode((String)valueMap.get("appCode"));
      configValue.setConfigName((String)valueMap.get("name"));
      configValue.setConfigDescription((String)valueMap.get("description"));
      configValue.setConfigValue((String)valueMap.get("value"));
      Text valueText = (Text)valueMap.get("text");
      if (valueText != null) {
        configValue.setConfigText(valueText.getValue());
      }
    } else {
      configValue = null;
    }
    return configValue;
  }
  
  /*
   * Get a list of {@link SimpleConfigValue} config values for an application.
   */
  public List<SimpleConfigValue> getSimpleConfigValues(String application) {
    List<Map> values = this.getConfigValuesForApp(application);
    List<SimpleConfigValue> returnValues = new ArrayList();
    SimpleConfigValue configValue;
    for (Map <String, Object> cv : values) {
      configValue = new SimpleConfigValue();
      configValue.setAppCode((String)cv.get("appCode"));
      configValue.setConfigName((String)cv.get("name"));
      configValue.setConfigDescription((String)cv.get("description"));
      configValue.setConfigValue((String)cv.get("value"));
      Text valueText = (Text)cv.get("text");
      if (valueText != null) {
        configValue.setConfigText(valueText.getValue());
      }
      returnValues.add(configValue);
    }

    return returnValues;
  }

  /**
   * Inserts a configuration value given a {@link SimpleConfigValue}.
   * @param value The value to insert.
   * @return true/false, depending on whether the insert was successful.
   */
  public boolean insertSimpleConfigValue(SimpleConfigValue value) {
    value.setAppCode(value.getAppCode().toUpperCase());
    value.setConfigName(value.getConfigName().toUpperCase());

    Key dsKey = KeyFactory.createKey(CONFIG_ENTITY_NAME, value.getAppCode() + value.getConfigName());
    Entity newValue = new Entity(dsKey);

    newValue.setProperty("appCode", value.getAppCode());
    newValue.setProperty("name", value.getConfigName());
    newValue.setUnindexedProperty("description", value.getConfigDescription());
    //*** Config value might not have been set.
    if (value.getConfigValue() != null) {
      newValue.setUnindexedProperty("value", value.getConfigValue());
    }
    //*** Config Text might not have been set.
    if (value.getConfigText() != null) {
      newValue.setProperty("text", new Text(value.getConfigText()));
    }
    //*** TODO(mgordon): Implement images.
    newValue.setUnindexedProperty("valueImageId", "");
    newValue.setProperty("createdBy", value.getConfigUser());
    newValue.setProperty("createDate", new Date());
    newValue.setProperty("modifiedBy", value.getConfigUser());
    newValue.setProperty("modifyDate", new Date());

    try {
      Key newConfigKey = ds.put(newValue);
    } catch (Exception ex) {
      LOGGER.severe("Could not store config value: " + ex.toString());
      return false;
    }
    return true;
  }
}
