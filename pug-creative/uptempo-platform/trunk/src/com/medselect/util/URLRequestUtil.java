/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.util;

import com.medselect.common.BaseManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Mike Gordon (mgordon)
 */
public class URLRequestUtil {
  
  /**
   * Executes a POST request, with default HTTP header parameters.
   * 
   * @param URI The URI to request.
   * @param parameters The parameters for the request.
   * @return 
   */
  public static String doPost(
      String URI,
      Map<String, String> parameters) {
    return doPost(URI, parameters, null, null);
  }
  
  /**
   * Executes a POST request, with default HTTP header parameters.
   * 
   * @param URI The URI to request.
   * @param parameters The parameters for the request.
   * @param header The header properties for the request.
   * @param body The request body.  Can be null.  If not null, this replaces the parameters.
   * @return A String containing the response.
   */
  public static String doPost(
      String URI,
      Map<String, String> parameters,
      Map<String, String> header,
      String body) {
    return URLRequestUtil.fetchURL(URI, "POST", parameters, header, body);
  }
  
  /**
   * Executes a PUT request, with default HTTP header parameters.
   * 
   * @param URI The URI to request.
   * @param parameters The parameters for the request.
   * @param header The header properties for the request.
   * @param body The request body.  Can be null.  If not null, this replaces the parameters.
   * @return A String containing the response.
   */
  public static String doPut(
      String URI,
      Map<String, String> parameters,
      Map<String, String> header,
      String body) {
    return URLRequestUtil.fetchURL(URI, "PUT", parameters, header, body);
  }
  
  /**
   * Executes a GET request, with default HTTP header parameters.
   * @param URI The URI to request.
   * @param parameters The parameters for the request.
   * @return  A String containing the response.
   */
  public static String doGet(String URI, Map<String, String> parameters) {
    return URLRequestUtil.fetchURL(URI, "GET", parameters, null, null);
  }
  
  /**
   * Executes a GET request, with custom HTTP header parameters.
   * @param URI The URI to request.
   * @param parameters The parameters for the request.
   * @param header The header parameters.
   * @return  A String containing the response.
   */
  public static String doGet(
      String URI,
      Map<String, String> parameters,
      Map<String, String> header) {
    return URLRequestUtil.fetchURL(URI, "GET", parameters, header, null);
  }

  /**
   * Helper function to execute a server-side URL fetch.
   * @param URI The URI to request.
   * @param method The request method: GET|PUT|POST|DELETE
   * @param parameters The parameters for the request.
   * @param header The request header parameters.  Can be null.  If null, not used.
   * @param body A request body.  Can be null.  If not null, this replaces parameters.  If null,
   *    then not used.
   * @return A String containing the response.
   */
  public static String fetchURL(
      String URI, 
      String method,
      Map<String, String> parameters,
      Map<String, String> header,
      String body) {
    Logger LOGGER = Logger.getLogger(BaseManager.class.getName());
    String responseString = "";
    String responseJSONFailedString  = "{\"status\":\"FAILED\"}";
    URL serviceURL = null;
    HttpURLConnection serviceConn = null;
    Boolean httpErrorFlag = false;
    StringBuffer responseBuf = new StringBuffer();
    StringBuffer parameterBuf = new StringBuffer();

    try {
      serviceURL = new URL(URI);
      /*** 
       * Removed code since HttpsURLConnection causes error with the google appengine
       * check if the request is https
      if (URI.toLowerCase().contains("https")) {
        serviceConn = (HttpsURLConnection) serviceURL.openConnection();
      } else { //***Default to non ssl connection
        serviceConn = (HttpURLConnection) serviceURL.openConnection();
      }
      *****/
      serviceConn = (HttpURLConnection) serviceURL.openConnection();
      serviceConn.setRequestMethod(method);

      //*** Use the parameters instead of the body.
      if (body == null) {
        String paramDelimiter = "";
        if (!parameters.isEmpty()) {
          for (String key : parameters.keySet()) {
            parameterBuf.append(paramDelimiter);
            parameterBuf.append(key);
            parameterBuf.append("=");
            if (method.equals("GET")) {
              parameterBuf.append(URLEncoder.encode(parameters.get(key), "UTF-8"));
            } else {
              parameterBuf.append(parameters.get(key));
            }
            paramDelimiter = "&";
          }
        }
      } else {
        parameterBuf.append(body);
      }

      //*** Fill in the HTTP header parameters.
      if (header == null) {
        serviceConn
            .setRequestProperty("Content-Length", "" + Integer.toString(parameterBuf.length()));
        serviceConn.setRequestProperty("Content-Language", "en-US");
        serviceConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      } else {
        for (String key : header.keySet()) {
          serviceConn.setRequestProperty(key, header.get(key));
        }
      }

      serviceConn.setConnectTimeout(60000);
      serviceConn.setReadTimeout(60000);
      serviceConn.setAllowUserInteraction(false);  
      serviceConn.setUseCaches(false);
      serviceConn.setDoInput(true);
      serviceConn.setDoOutput(true);


      if (parameterBuf.length() > 0) {
        

        //***Do Request
        DataOutputStream wr = new DataOutputStream(serviceConn.getOutputStream());
        wr.writeBytes(parameterBuf.toString());
        wr.flush();
        wr.close();
      } else {
        serviceConn.connect();
      }

      //***Check response 
      if (serviceConn.getResponseCode() == HttpURLConnection.HTTP_OK ||
          serviceConn.getResponseCode() == HttpURLConnection.HTTP_CREATED ||
          serviceConn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
        //***Get response from the service
        BufferedReader br = new BufferedReader(new InputStreamReader(serviceConn.getInputStream()));

        while (br.ready()) {
          responseBuf.append(br.readLine());
        }

        br.close();
      } else if (serviceConn.getResponseCode() == HttpURLConnection.HTTP_CONFLICT) {
        LOGGER.severe("Error: HTTP Conflict Returned:" + serviceConn.getResponseCode());
        LOGGER.severe("Error: Parameters:" + parameterBuf.toString());
        responseString = "Error: HTTP Conflict Returned:" + serviceConn.getResponseCode();
        httpErrorFlag = true;
      } else {
        responseString = Integer.toString(serviceConn.getResponseCode());
        LOGGER.severe("Error: HTTP Code other than '200' returned:" + responseString);
        LOGGER.severe("Parameters:" + parameterBuf.toString());
        httpErrorFlag = true;

      }
    } catch (Exception ex) {
      if (httpErrorFlag == false) {
        //**Non-HTTP response connection error.
        LOGGER.severe("Error: Problem opening connection: " + ex.toString());
      }
    } finally {
      if (serviceConn != null) {
        serviceConn.disconnect();
         if (httpErrorFlag == true) {
          return responseJSONFailedString;
         }
      }
    }
    
    return responseBuf.toString();
  }
}
