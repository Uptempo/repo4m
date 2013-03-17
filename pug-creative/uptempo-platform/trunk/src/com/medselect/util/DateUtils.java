package com.medselect.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Date utilities for the services layer.
 * @author Mike Gordon (mgordon)
 */
public class DateUtils {
  /**
   * Gets a date from a String date in the format mm/dd/yyyy.
   * @param dateString The date to parse.
   * @return a {@link Date} representing the date provided.
   */
  public static Calendar getDateFromDateString(String dateString) {
    String[] dateValArray = dateString.split("/");
    int month = Integer.parseInt(dateValArray[0]);
    int day = Integer.parseInt(dateValArray[1]);
    int year = Integer.parseInt(dateValArray[2]);

    Calendar returnCal = Calendar.getInstance();
    returnCal.set(year, month - 1, day);
    return returnCal;
  }

  public static String makeDateStringFromDate(Calendar cal) {
    return Integer.toString(cal.get(Calendar.MONTH) + 1) + "/" +
           Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "/" +
           Integer.toString(cal.get(Calendar.YEAR));
  }
  
  /**
   * Determines if two blocks of time, represented by the ms since epoch, overlap.
   * @param block1Start Start time of block 1.
   * @param block1End End time of block 1.
   * @param block2Start Start time of block 2.
   * @param block2End End time of block 2.
   * @return True if the blocks overlap, false if they don't.
   */
  public static boolean doDateBlocksOverlap(
      long block1Start,
      long block1End,
      long block2Start,
      long block2End) {
    //*** If block 1 completely contains block 2.
    if (block1Start < block2Start && block1End > block2End) {
      return true;
    }
    
    //*** If block 1 start falls within block 2.
    if (block1Start > block2Start && block1Start < block2End) {
      return true;
    }
    
    //*** If block 1 end falls within block 2.
    if (block2Start < block1End && block2End > block1End) {
      return true;
    }
    
    return false;
  }

  public static String getRfcTime(Calendar date) {
    return DateUtils.getRfcTime(date, DateTimeZone.UTC);
  }
  
  public static String getRfcTime(Calendar date, DateTimeZone tz) {
    int year = date.get(Calendar.YEAR);
    int month = date.get(Calendar.MONTH) + 1;
    int day = date.get(Calendar.DATE);
    int hour = date.get(Calendar.HOUR_OF_DAY);
    int minute = date.get(Calendar.MINUTE);
    int second = date.get(Calendar.SECOND);
    DateTime dt = new DateTime(year, month, day, hour, minute, second, tz);
    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    return fmt.print(dt);
  }
  
  /**
   * Wrapper function for returning readable time, if no time zone offset is specified.
   * @param date The date to convert to a readable String.
   * @return The readable date.
   */
  public static String getReadableTime(Calendar date) {
    return getReadableTime(date, 0);
  }

  /**
   * Returns a readable time value, given a date in a <@link Calendar>.
   * @param date The date to convert.
   * @param offset The time zone offset from UTC (for example, PST is normally -7).
   * @return The readable date.
   */
  public static String getReadableTime(Calendar date, int offset) {
    DateTime dt = new DateTime(date.getTimeInMillis(), DateTimeZone.forOffsetHours(offset));
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");
    return fmt.print(dt);
  }
}
