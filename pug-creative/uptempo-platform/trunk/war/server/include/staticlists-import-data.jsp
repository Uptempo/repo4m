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
  	<div data-role="page" id="staticlists-import-data" data-theme="a">
    <h3>
      <span id="staticlists-import-data-form-title">Import static list data from file</span>
    </h3>
     
    <% if (request.getParameter("res") != null)  { %>
      <% if (request.getParameter("res").equals("success")) { %>
        <div id="staticlists-import-data-form-notice" class="form-notice">
          Successfully imported static list data!
        </div>
        <% } else if (request.getParameter("res").equals("failed")) { %>
        <div id="staticčists-import-data-form-errors" class="form-errors">
          Failed to import static list data. Please try again.
        </div>
      <% } %>  
    <% } %>      
		<form action="<%= blobstoreService.createUploadUrl("/import-staticlist-data") %>" method="post" enctype="multipart/form-data" data-ajax="false">		  
		  <input type="file" data-theme="b" name="myFile">
		  <input type="submit" data-theme="b" value="Submit">
		</form>
	</div>
  </body>
</html>