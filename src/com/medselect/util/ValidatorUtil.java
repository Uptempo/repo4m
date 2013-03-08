package com.medselect.util;

/**
 *
 * @author karlo.smid@gmail.com
 */
public class ValidatorUtil {
/**
* Is this valid US ZIP code.
* @param input String string that we want to test.
* @return boolean true if input is valid US ZIP code.
*/
  public boolean isUSZIPcode( String input ){
    return input.matches("^\\d{5}(-\\d{4})?$");
  }
  
/**
 * Is this valid email.
 * @param input String string that we want to test.
 * @return boolean true if input is valid email.
*/
  public boolean isEmail( String input ){
    return input.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$");
  }

/**
* Is this valid phone number.
* @param input String string that we want to test.
* @return boolean true if input is valid phone number.
*/
  public boolean isPhoneNumber( String input ){
    if ( input.isEmpty() ){
      return true;
    }
    return input.matches("^[\\D*\\d]{10,}$");
  }

}
