/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.server;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.medselect.util.Constants;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class OfficePortalServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    //*** Set the common attributes.
    request.setAttribute("app-environment", SystemProperty.environment.get());
    request.setAttribute("app-id", SystemProperty.applicationId.get());
    request.setAttribute("app-version", SystemProperty.applicationVersion.get());
    String requestPath = request.getRequestURI();
    if (requestPath.contains("login")) {
      //*** Do login here.
    	request.getRequestDispatcher("/office-portal/login.jsp").forward(request, response);
    } else {
      //*** Check the user authentication key against the available keys.
      String userAuthKey = request.getParameter("userAuthKey");
      boolean userAuthStatus = checkUserAuth(userAuthKey);
      //*** Check for the existence of an office key.
      String officeKey = request.getParameter("officeKey");
      String officeGroupKey = request.getParameter("officeGroupKey");
      if ((officeKey == null || officeKey.isEmpty() || !userAuthStatus) &&
          (officeGroupKey == null || officeGroupKey.isEmpty()) || !userAuthStatus){
        if (!userAuthStatus) {
          //*** Redirect to the login.
        	request.getRequestDispatcher("/office-portal/login.jsp").forward(request, response);
        } else {
          //*** Provide error message about missing office key.
          request.setAttribute("error-message",
              "You must provide an office key to use this portal.");
        }
        request.getRequestDispatcher("/office-portal/error.jsp").forward(request, response);
      } else {
        //*** Forward to portal here.
    	request.setAttribute("office-key", request.getParameter("officeKey"));
    	request.setAttribute("office-group-key", request.getParameter("officeGroupKey"));
    	request.setAttribute("uptempo-authkey", System.getProperty("com.uptempo.appAuthKey"));
        request.setAttribute("username", request.getParameter("username"));
        request.getRequestDispatcher("/office-portal/index.jsp").forward(request, response);
      }
    }
  }
  
  /**
   * Compares the given user authentication key with the keys stored in memcache, to check whether
   * the key is valid and this user has recently logged in with a valid login.
   * @param userAuthKey The key provided by the request to check.
   * @return true if the key is found, false if it's not.
   */
  private boolean checkUserAuth(String userAuthKey) {
    boolean userAuthStatus = false;
    if (userAuthKey != null && !userAuthKey.isEmpty()) {
      //*** Get the auth key from memcache.
      MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
      syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
      //*** Get the cache session, which contains the user e-mail and login key.
      String cachedKey = (String)syncCache.get(
        Constants.MEMCACHE_LOGIN_KEY + "." + userAuthKey);
      if (cachedKey != null && !cachedKey.isEmpty()) {
        userAuthStatus = true;
      }
    }
    return userAuthStatus || Constants.ALWAYS_ALLOW_AUTH;
  }
}
