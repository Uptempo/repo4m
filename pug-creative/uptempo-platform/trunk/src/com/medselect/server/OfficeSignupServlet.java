/*
 * Copyright 2013 Uptempo Group Inc.
 */

package com.medselect.server;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class OfficeSignupServlet extends HttpServlet {
  @Override
  public void doGet(
      HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    request.setAttribute("app-environment", SystemProperty.environment.get());
    request.setAttribute("app-id", SystemProperty.applicationId.get());
    request.setAttribute("app-version", SystemProperty.applicationVersion.get());
    request.getRequestDispatcher("/server/office-signup.jsp").forward(request, response);
  }

  @Override
  public void doPost(
      HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    request.setAttribute("app-environment", SystemProperty.environment.get());
    request.setAttribute("app-id", SystemProperty.applicationId.get());
    request.setAttribute("app-version", SystemProperty.applicationVersion.get());
    //*** Get the office data.
    //*** Create the new office.
    //*** Dispatch the request to the confirmation page.
    request.getRequestDispatcher("/server/office-confirm.jsp").forward(request, response);
  }
}
