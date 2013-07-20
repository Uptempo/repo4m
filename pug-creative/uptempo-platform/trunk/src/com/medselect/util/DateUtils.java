package com.medselect.util;

import com.medselect.billing.SimpleBillingOffice;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
    String monthString, dayString;
    int month = cal.get(Calendar.MONTH) + 1;
    monthString = Integer.toString(month);
    int day = cal.get(Calendar.DAY_OF_MONTH);
    dayString = Integer.toString(day);
    
    return monthString + "/" +
           dayString + "/" +
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
   * Returns a readable time value, given a date, hour, and minute.
   * @param date The date in the format mm/dd/yyyy
   * @param hour The hour of the time.
   * @param min The minute of the time.
   * @param offset The offset from GMT, in hours.
   * @return The readable date.
   */
  public static String getReadableTime(String date, int hour, int min, int offset) {
    String amPm = "AM";
    if (hour >= 12) {
      amPm = "PM";
    }
    if (hour >= 13) {
      hour = hour - 12;
    }
    String hrValue = String.valueOf(hour);
    String minValue = String.valueOf(min);
    if (min < 10) {
      minValue = "0" + minValue;
    }
    if (hour < 10) {
      hrValue = "0" + hrValue;
    }
    
    try {
      // Change the date to readable time
      Date dateObject = new SimpleDateFormat("MM/dd/yyyy").parse(date);

      SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");

      // Return the values
      return dateFormat.format(dateObject) + " " +
             hrValue + ":" +
             minValue + " " +
             amPm + "(GMT" + String.valueOf(offset) + ")";
    } catch (ParseException ex) {
      return "DATE NOT PARSED";
    }
  }

  /**
   * Given a date, hour, minute, and offset, returns a date object.
   * @param date The date, in mm/dd/yyyy format.
   * @param hour The hour, in 24 hour time.
   * @param min The minute.
   * @param offset The time zone offset from UTC (for example, PST is normally -7).
   * @return The date in mm/dd/yyyy format.
   */
  public static Date getDateFromValues(String date, int hour, int min, int offset) {
    DateTimeZone zone = DateTimeZone.forOffsetHours(offset);
    String [] dateSplit = date.split("/");
    int month = Integer.parseInt(dateSplit[0]);
    int day = Integer.parseInt(dateSplit[1]);
    int year = Integer.parseInt(dateSplit[2]);
    DateTime dt = new DateTime(year, month, day, hour, min, zone);
    return dt.toDate();
  }
  
  /**
   * Converts an existing to daylight savings time, given the config manager and the office.  Uses
   * the office settings to figure out if the office subscribes to daylight savings time.
   * @param offset The current offset.
   * @param cManager A {@link ConfigManager} instance to get the DST config value.
   * @param office The office information, including whether it subscribes to DST.
   * @return 
   */
  public static int convertOffsetForDst(
      int offset, ConfigManager cManager, SimpleBillingOffice office) {
    SimpleConfigValue config =
        cManager.getSimpleConfigValue(Constants.APPOINTMENT_APP, Constants.DAYLIGHT_SAVINGS_TIME_ON);
    if (config != null && config.getConfigValue().equalsIgnoreCase("TRUE")) {
      if (office.subscibesToDaylightSavingsTime()) {
        offset++;
      }
    }
    return offset;
  }
}
