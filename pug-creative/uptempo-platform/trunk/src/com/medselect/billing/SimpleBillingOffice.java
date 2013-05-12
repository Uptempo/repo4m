/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.billing;

import java.util.List;

/**
 *
 * @author Mike Gordon (mgordon)
 */
public class SimpleBillingOffice {
  private String officeGroupKey;
  private String officeName;
  private String officeAddress1;
  private String officeAddress2;
  private String officeCity;
  private String officeState;
  private String officePostalCode;
  private int officeTimeZoneOffset;
  private String officeCountry;
  private List <String> officePhones;
  private List <String> officeFaxes;
  private String officeEmail;
  private String officeNotes;
  private String officeHours;
  private boolean daylightSavingsTime = true;

  /**
   * @return the officeGroupKey
   */
  public String getOfficeGroupKey() {
    return officeGroupKey;
  }

  /**
   * @param officeGroupKey the officeGroupKey to set
   */
  public void setOfficeGroupKey(String officeGroupKey) {
    this.officeGroupKey = officeGroupKey;
  }

  /**
   * @return the officeName
   */
  public String getOfficeName() {
    return officeName;
  }

  /**
   * @param officeName the officeName to set
   */
  public void setOfficeName(String officeName) {
    this.officeName = officeName;
  }

  /**
   * @return the officeAddress1
   */
  public String getOfficeAddress1() {
    return officeAddress1;
  }

  /**
   * @param officeAddress1 the officeAddress1 to set
   */
  public void setOfficeAddress1(String officeAddress1) {
    this.officeAddress1 = officeAddress1;
  }

  /**
   * @return the officeAddress2
   */
  public String getOfficeAddress2() {
    return officeAddress2;
  }

  /**
   * @param officeAddress2 the officeAddress2 to set
   */
  public void setOfficeAddress2(String officeAddress2) {
    this.officeAddress2 = officeAddress2;
  }

  /**
   * @return the officeCity
   */
  public String getOfficeCity() {
    return officeCity;
  }

  /**
   * @param officeCity the officeCity to set
   */
  public void setOfficeCity(String officeCity) {
    this.officeCity = officeCity;
  }

  /**
   * @return the officeState
   */
  public String getOfficeState() {
    return officeState;
  }

  /**
   * @param officeState the officeState to set
   */
  public void setOfficeState(String officeState) {
    this.officeState = officeState;
  }

  /**
   * @return the officePostalCode
   */
  public String getOfficePostalCode() {
    return officePostalCode;
  }

  /**
   * @param officePostalCode the officePostalCode to set
   */
  public void setOfficePostalCode(String officePostalCode) {
    this.officePostalCode = officePostalCode;
  }

  /**
   * @return the officeTimeZoneOffset
   */
  public int getOfficeTimeZoneOffset() {
    return officeTimeZoneOffset;
  }

  /**
   * @param officeTimeZoneOffset the officeTimeZoneOffset to set
   */
  public void setOfficeTimeZoneOffset(int officeTimeZoneOffset) {
    this.officeTimeZoneOffset = officeTimeZoneOffset;
  }

  /**
   * @return the officeCountry
   */
  public String getOfficeCountry() {
    return officeCountry;
  }

  /**
   * @param officeCountry the officeCountry to set
   */
  public void setOfficeCountry(String officeCountry) {
    this.officeCountry = officeCountry;
  }

  /**
   * @return the officePhones
   */
  public List <String> getOfficePhones() {
    return officePhones;
  }

  /**
   * @param officePhones the officePhones to set
   */
  public void setOfficePhones(List <String> officePhones) {
    this.officePhones = officePhones;
  }

  /**
   * @return the officeFaxes
   */
  public List <String> getOfficeFaxes() {
    return officeFaxes;
  }

  /**
   * @param officeFaxes the officeFaxes to set
   */
  public void setOfficeFaxes(List <String> officeFaxes) {
    this.officeFaxes = officeFaxes;
  }

  /**
   * @return the officeEmail
   */
  public String getOfficeEmail() {
    return officeEmail;
  }

  /**
   * @param officeEmail the officeEmail to set
   */
  public void setOfficeEmail(String officeEmail) {
    this.officeEmail = officeEmail;
  }

  /**
   * @return the officeNotes
   */
  public String getOfficeNotes() {
    return officeNotes;
  }

  /**
   * @param officeNotes the officeNotes to set
   */
  public void setOfficeNotes(String officeNotes) {
    this.officeNotes = officeNotes;
  }

  /**
   * @return the officeHours
   */
  public String getOfficeHours() {
    return officeHours;
  }

  /**
   * @param officeHours the officeHours to set
   */
  public void setOfficeHours(String officeHours) {
    this.officeHours = officeHours;
  }

  /**
   * @return the daylightSavingsTime subscription indicator.
   */
  public boolean subscibesToDaylightSavingsTime() {
    return daylightSavingsTime;
  }

  /**
   * @param daylightSavingsTime the daylightSavingsTime to set
   */
  public void setDaylightSavingsTime(boolean daylightSavingsTime) {
    this.daylightSavingsTime = daylightSavingsTime;
  }
}
