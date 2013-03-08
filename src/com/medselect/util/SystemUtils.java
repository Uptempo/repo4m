package com.medselect.util;

/**
 *
 * @author Mike Gordon (mgordon)
 */
public class SystemUtils {
  public static String getAppURL() {
    String hostUrl;
    String environment = System.getProperty("com.google.appengine.runtime.environment");
    if (environment.equals("Production")) {
        String applicationId = System.getProperty("com.google.appengine.application.id");
        String version = System.getProperty("com.google.appengine.application.version");
        hostUrl = "https://" + applicationId + ".appspot.com/";
    } else {
        hostUrl = "http://localhost:8888";
    }

    return hostUrl;
  }

  public static String getAppURLNonSSL() {
    String hostUrl;
    String environment = System.getProperty("com.google.appengine.runtime.environment");
    if (environment.equals("Production")) {
        String applicationId = System.getProperty("com.google.appengine.application.id");
        String version = System.getProperty("com.google.appengine.application.version");
        hostUrl = "http://" + applicationId + ".appspot.com/";
    } else {
        hostUrl = "http://localhost:8888";
    }

    return hostUrl;
  }
}
