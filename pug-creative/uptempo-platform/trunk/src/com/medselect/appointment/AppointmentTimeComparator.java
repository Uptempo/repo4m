package com.medselect.appointment;

import com.google.appengine.api.datastore.Entity;
import java.util.Comparator;

/**
 * Comparator to compare two appointments by start time, for sorting.
 * @author Mike Gordon
 */
public class AppointmentTimeComparator implements Comparator<Entity> {
  @Override
  public int compare(Entity appointment1, Entity appointment2) {
    long appointment1Time = (long)appointment1.getProperty("apptStartLong");
    long appointment2Time = (long)appointment2.getProperty("apptStartLong");
    if (appointment1Time < appointment2Time) return -1;
    if (appointment1Time > appointment2Time) return 1;
    return 0;
  }
}
