/*
 * Copyright 2013 Uptempo Group Inc.
 */

package com.medselect.config;

/**
 * POJO class to hold a simple representation of a Config Value.  Used by other services that need
 * to access config values internally.
 * @author Mike Gordon (mgordon)
 */
public class SimpleConfigValue {
  private String appCode;
  private String configDescription;
  private String configName;
  private String configValue;
  private String configText;
  private String configUser;

  /**
   * @return the appCode
   */
  public String getAppCode() {
    return appCode;
  }

  /**
   * @param appCode the appCode to set
   */
  public void setAppCode(String appCode) {
    this.appCode = appCode;
  }

  /**
   * @return the configDescription
   */
  public String getConfigDescription() {
    return configDescription;
  }

  /**
   * @param configDescription the configDescription to set
   */
  public void setConfigDescription(String configDescription) {
    this.configDescription = configDescription;
  }

  /**
   * @return the configName
   */
  public String getConfigName() {
    return configName;
  }

  /**
   * @param configName the configName to set
   */
  public void setConfigName(String configName) {
    this.configName = configName;
  }

  /**
   * @return the configValue
   */
  public String getConfigValue() {
    return configValue;
  }

  /**
   * @param configValue the configValue to set
   */
  public void setConfigValue(String configValue) {
    this.configValue = configValue;
  }

  /**
   * @return the configText
   */
  public String getConfigText() {
    return configText;
  }

  /**
   * @param configText the configText to set
   */
  public void setConfigText(String configText) {
    this.configText = configText;
  }

  /**
   * @return the configUser
   */
  public String getConfigUser() {
    return configUser;
  }

  /**
   * @param configUser the configUser to set
   */
  public void setConfigUser(String configUser) {
    this.configUser = configUser;
  }
}
