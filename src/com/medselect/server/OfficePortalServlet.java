/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.server;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
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
    } else {
      String officeKey = request.getParameter("officeKey");
      if (officeKey == null || officeKey.isEmpty()) {
        request.setAttribute("error-message", "You must provide an office key to use this portal.");
        request.getRequestDispatcher("/office-portal/error.jsp").forward(request, response);
      } else {
        //*** Forward to portal here.
        request.setAttribute("office-key", request.getParameter("officeKey"));
        request.setAttribute("uptempo-authkey", System.getProperty("com.uptempo.appAuthKey"));
        request.setAttribute("user-name", "User Name Here");
        request.getRequestDispatcher("/office-portal/index.jsp").forward(request, response);
      }
    }
  }
}
