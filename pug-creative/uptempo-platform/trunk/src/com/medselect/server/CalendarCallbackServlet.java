/*
 * Copyright 2013 Uptempo Group Inc.
 */
package com.medselect.server;

import com.google.common.collect.ImmutableMap;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import com.medselect.util.Constants;
import com.medselect.util.URLRequestUtil;
import com.uptempo.google.GoogleAuthException;
import com.uptempo.google.GoogleAuthProxy;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Mike Gordon(mgordon)
 */
public class CalendarCallbackServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger(CalendarCallbackServlet.class.getName());
  public void doGet(
      HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    String message;
    //*** TODO(mgordon): Get the office key so that the follow on screen can continue to show the
    //*** office sign-up confirmation.
    //*** Get the access code.
    String accessToken = request.getParameter("code");
    //*** Get the error, if returned.
    String errorMessage = request.getParameter("error");
    
    if (errorMessage == null) {
      try {
        message = GoogleAuthProxy.doGoogleAuth(accessToken);
        request.setAttribute("message", message);
      } catch (GoogleAuthException ex) {
        request.setAttribute("auth-error", "Auth token request from Google failed: " + ex.getMessage());
      }
    } else {
      request.setAttribute("auth-error", "Auth token request from Google failed: " + errorMessage);
    }

    request.getRequestDispatcher("/server/office-confirm.jsp").forward(request, response);
  }
}
