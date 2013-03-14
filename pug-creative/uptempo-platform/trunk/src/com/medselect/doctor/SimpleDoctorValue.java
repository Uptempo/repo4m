/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.doctor;

import java.util.List;

/**
 *
 * @author Mike Gordon
 */
public class SimpleDoctorValue {
  private List<String> titles;
  private String firstName;
  private String lastName;
  private String email;
  private String education;
  private String notes;
  private String publicDescription;
  private List<String> specialties;

  /**
   * @return the titles
   */
  public List<String> getTitles() {
    return titles;
  }

  /**
   * @param titles the titles to set
   */
  public void setTitles(List<String> titles) {
    this.titles = titles;
  }

  /**
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @param firstName the firstName to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * @param lastName the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return the education
   */
  public String getEducation() {
    return education;
  }

  /**
   * @param education the education to set
   */
  public void setEducation(String education) {
    this.education = education;
  }

  /**
   * @return the notes
   */
  public String getNotes() {
    return notes;
  }

  /**
   * @param notes the notes to set
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * @return the publicDescription
   */
  public String getPublicDescription() {
    return publicDescription;
  }

  /**
   * @param publicDescription the publicDescription to set
   */
  public void setPublicDescription(String publicDescription) {
    this.publicDescription = publicDescription;
  }

  /**
   * @return the specialties
   */
  public List<String> getSpecialties() {
    return specialties;
  }

  /**
   * @param specialties the specialties to set
   */
  public void setSpecialties(List<String> specialties) {
    this.specialties = specialties;
  }
}
