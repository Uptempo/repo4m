/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.uptempo.google;

/**
 * Exception to mark failed Google authentication.
 * 
 * @author Mike Gordon (mgordon)
 */
public class GoogleAuthException extends Exception {
  public GoogleAuthException(String message) {
    super(message);
  }
}
