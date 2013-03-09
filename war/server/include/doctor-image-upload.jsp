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
  	<% 
  		String imageUrl = "";
  		String imageKey = "";
  		String doctorKey = "";
  		if (request.getParameter("img") != null && request.getParameter("img") != "") {
  			imageUrl = "/serve-doctor-image?blob-key=" + request.getParameter("img");
  			imageKey = "oldImage="+request.getParameter("img")+"=oldImage";  		
  		}

      if (request.getParameter("doc") != null) {
        doctorKey = "doctorKey="+request.getParameter("doc")+"=doctorKey";
      }
    
  	%>
  	<div data-role="page" id="doctor-image-upload" data-theme="a">
    <h3>
      <span id="doctor-image-upload-form-title">Upload doctor's image</span>
    </h3>
     
    <% if (request.getParameter("res") != null)  { %>
      <% if (request.getParameter("res").equals("success")) { %>
        <div id="doctor-image-upload-form-notice" class="form-notice">
          Successfully uploaded new photo!
        </div>
        <% } else if (request.getParameter("res").equals("failed")) { %>
        <div id="doctor-image-upload-form-errors" class="form-errors">
          Failed to upload photo. Please try again.
        </div>
      <% } %>  
    <% } %>  
    <% if (imageUrl != "") { %>
		  <img src="<%= imageUrl %>" style="display: block; margin: 2px auto 2px auto" alt="Doctor photo" id="doctor-photo-src" height="150" width="180"/>
    <% } %>    
		<form action="<%= blobstoreService.createUploadUrl("/upload-doctor-image") %>" method="post" enctype="multipart/form-data" data-ajax="false">
		  <input type="hidden" name="foo" id="text-for-key" value="<%= doctorKey %>">
		  <input type="hidden" name="bar" id="old_image" value="<%= imageKey %>">
		  <input type="file" data-theme="b" name="myFile">
		  <input type="submit" data-theme="b" value="Submit">
		</form>
	</div>
  </body>
</html>