<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
  BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<!DOCTYPE HTML>
<html style="min-height: 300px;">
  <!-- Page HEAD include -->
  <%@include file="head.jsp" %>
  <body>  	
  	<div data-role="page" id="config-import-data" data-theme="a">
    <h3>
      <span id="config-import-data-form-title">Import config data from file</span>
    </h3>
     
    <% if (request.getParameter("res") != null)  { %>
      <% if (request.getParameter("res").equals("success")) { %>
        <div id="config-import-data-form-notice" class="form-notice">
          Successfully imported config data!
        </div>
        <% } else if (request.getParameter("res").equals("failed")) { %>
        <div id="config-import-data-form-errors" class="form-errors">
          Failed to import config data. Please try again.
        </div>
      <% } %>  
    <% } %>      
		<form action="<%= blobstoreService.createUploadUrl("/import-config-data") %>" method="post" enctype="multipart/form-data" data-ajax="false">		  
		  <input type="file" data-theme="b" name="myFile">
		  <input type="submit" data-theme="b" value="Submit">
		</form>
	</div>
  </body>
</html>