/*
 * Copyright 2012 Rentolution.com
 */

package com.medselect.util;

/**
 *
 * @author Mike Gordon
 *
 * Base filter class for GET requests.
 */
public class BaseFilter {

  //***Defines the direction of the filter for the db query
  public static enum FilterDirection {ASC, DESC}

  //***FilterDirection for this filter
  private FilterDirection direction;

  //***Number of item to start with in the query
  private int start;

  //***How many items to get with this query
  private int length = 1000;

  public static FilterDirection getDirectionValue(String dir) {
    if (dir != null && dir.toUpperCase().equals("DESC")) {
      return FilterDirection.DESC;
    }

    //***Default to Ascending, no matter the value
    return FilterDirection.ASC;
  }
  
  /**
   * @return the direction
   */
  public FilterDirection getDirection() {
    return direction;
  }

  /**
   * @param direction the direction to set
   */
  public void setDirection(FilterDirection direction) {
    this.direction = direction;
  }

  /**
   * @return the start
   */
  public int getStart() {
    return start;
  }

  /**
   * @param start the start to set
   */
  public void setStart(int start) {
    this.start = start;
  }

  /**
   * @return the length
   */
  public int getLength() {
    return length;
  }

  /**
   * @param length the length to set
   */
  public void setLength(int length) {
    this.length = length;
    if (this.length == 0) {
      this.length = 1000;
    }
  }
  
}
