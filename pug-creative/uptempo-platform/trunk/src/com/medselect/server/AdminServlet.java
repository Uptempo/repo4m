/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.server;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class AdminServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String requestPath = request.getRequestURI();
    if (requestPath.contains("loadconfig")) {
      String applications = request.getParameter("a");
      String [] appCodes = applications.split(",");
      ConfigManager cm = new ConfigManager();
      String responseText = "";
      for (String app : appCodes) {
        List<SimpleConfigValue> values = cm.getSimpleConfigValues(app.toUpperCase());
        for (SimpleConfigValue value : values) {
          responseText += "msAdmin.appConfig.configValues['" + value.getConfigName() + "']";
          responseText += "={'value':'" + value.getConfigValue() + "',";
          if (value.getConfigText() != null) {
            responseText += "'text':'" + value.getConfigText().replace("'", "\'") + "'};\n";
          } else {
            responseText += "'text':''};\n";
          }
        }
      }
      response.setContentType("application/x-javascript");
      PrintWriter out = response.getWriter();
      out.print(responseText);
      out.flush();
    } else {
      BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      String blobURL = blobstoreService.createUploadUrl("/upload-attachment");
      UserService userService = UserServiceFactory.getUserService();
      request.setAttribute("app-environment", SystemProperty.environment.get());
      request.setAttribute("app-id", SystemProperty.applicationId.get());
      request.setAttribute("app-version", SystemProperty.applicationVersion.get());
      request.setAttribute("uptempo-authkey", System.getProperty("com.uptempo.appAuthKey"));
      request.setAttribute("user-name", userService.getCurrentUser().getEmail());
      request.setAttribute("attachment-upload-url", blobURL);
      request.getRequestDispatcher("/server/index.jsp").forward(request, response);
    }
  }
}
