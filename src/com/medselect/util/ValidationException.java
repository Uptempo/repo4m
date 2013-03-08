/*
 * Copyright 2012 Rentolution.com
 */

package com.medselect.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mike Gordon
 */
public class ValidationException extends Exception {
  private List<String> messages;

  public ValidationException() {
    messages = new ArrayList<String>();
  }

  public void addMessage(String message) {
    messages.add(message);
  }

  public List<String> getMessageList() {
    return messages;
  }
}
