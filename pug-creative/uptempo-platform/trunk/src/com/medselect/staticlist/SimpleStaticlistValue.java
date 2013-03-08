/*
 * Copyright 2013 Uptempo Group Inc.
 */

package com.medselect.staticlist;

/**
 * POJO class to hold a simple representation of a Staticlist Value.  Used by other services that need
 * to access staticlist values internally.
 * @author karlo.smid@gmail.com
 */
public class SimpleStaticlistValue {
  private String listApp;
  private String listCode;
  private String listKey;
  private String listValues;
  private String listTexts;
  private String user;

  /**
   * @return the listApp
   */
  public String getListApp() {
    return listApp;
  }

  /**
   * @param listApp the listApp to set
   */
  public void setListApp(String listApp) {
    this.listApp = listApp;
  }

  /**
   * @return the listCode
   */
  public String getListCode() {
    return listCode;
  }

  /**
   * @param listCode the listCode to set
   */
  public void setListCode(String listCode) {
    this.listCode = listCode;
  }

  /**
   * @return the listKey
   */
  public String getListKey() {
    return listKey;
  }

  /**
   * @param listKey the listKey to set
   */
  public void setListKey(String listKey) {
    this.listKey = listKey;
  }

  /**
   * @return the listValues
   */
  public String getListValues() {
    return listValues;
  }

  /**
   * @param listValues the listValues to set
   */
  public void setListValues(String listValues) {
    this.listValues = listValues;
  }

  /**
   * @return the listTexts
   */
  public String getListTexts() {
    return listTexts;
  }

  /**
   * @param listTexts the listTexts to set
   */
  public void setListTexts(String listTexts) {
    this.listTexts = listTexts;
  }
  
  /**
   * @return the user
   */
  public String getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(String user) {
    this.user = user;
  }

}
