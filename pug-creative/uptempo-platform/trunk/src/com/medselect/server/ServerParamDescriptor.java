/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.server;

import com.medselect.common.BaseManager;

/**
 * Descriptor class for server parameters.  Used to provide more than just data
 * type for each parameter.
 * 
 * @author Mike Gordon
 */
public class ServerParamDescriptor {
  private BaseManager.FieldType fieldType;
  private String description;
  private boolean isRequired;
  private boolean isIndexed = true;

  /**
   * @return the fieldType
   */
  public BaseManager.FieldType getFieldType() {
    return fieldType;
  }

  /**
   * @param fieldType the fieldType to set
   */
  private void setFieldType(BaseManager.FieldType fieldType) {
    this.fieldType = fieldType;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  private void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the isRequired
   */
  public boolean isRequired() {
    return isRequired;
  }

  /**
   * @param isRequired the isRequired to set
   */
  private void setRequired(boolean isRequired) {
    this.isRequired = isRequired;
  }

  /**
   * @return the isIndexed
   */
  public boolean isIndexed() {
    return isIndexed;
  }

  /**
   * @param isIndexed the isIndexed to set
   */
  private void setIndexed(boolean isIndexed) {
    this.isIndexed = isIndexed;
  }
  
  /**
   * Builder class for the ServerParamDescriptor.
   */
  public static class Builder {
    private BaseManager.FieldType bFieldType;
    private String bDescription;
    private boolean bRequired;
    private boolean bIndexed;

    /**
     * Constructor for a Builder, a shortcut to building by method.
     * @param ft Type of field that's described by the {@link ServerParamDescriptor}
     * @param description Description of field.
     * @param required Whether the field is required.
     * @param indexed Whether the field is indexed by App Engine Datastore.
     */
    public Builder(
        BaseManager.FieldType ft,
        String description,
        boolean required,
        boolean indexed) {
      bFieldType = ft;
      bDescription = description;
      bRequired = required;
      bIndexed = indexed;
    }
    /**
     * Builds an immutable ServerParamDescriptor from the configured params.
     * @return The finished {@link ServerParamDescriptor}.
     */
    public ServerParamDescriptor build() {
      ServerParamDescriptor descriptor = new ServerParamDescriptor();
      descriptor.setFieldType(bFieldType);
      descriptor.setDescription(bDescription);
      descriptor.setRequired(bRequired);
      descriptor.setIndexed(bIndexed);
      return descriptor;
    }
    
    public Builder description(String value) {
      this.bDescription = value;
      return this;
    }
    
    public Builder fieldType(BaseManager.FieldType value) {
      this.bFieldType = value;
      return this;
    }

    public Builder required(boolean value) {
      this.bRequired = value;
      return this;
    }

    public Builder indexed(boolean value) {
      this.bIndexed = value;
      return this;
    }
  }
}
