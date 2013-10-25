<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
  BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<!DOCTYPE HTML>
<html>
  <!-- Page HEAD include -->
  <%@include file="head.jsp" %>
  <body style="background-color: #fff;">
  	<% 
  		String imageUrl = "";
  		String imageKey = "";
  		String doctorKey = "";
  		String source = "";  		
  		if (request.getParameter("img") != null && request.getParameter("img") != "" && request.getParameter("source") != "") {
  			imageUrl = "/serve-doctor-image?blob-key=" + request.getParameter("img");
  			imageKey = request.getParameter("img");  		
  			source = request.getParameter("source");  		
  		}

      if (request.getParameter("doc") != null) {
        doctorKey = request.getParameter("doc");
      }
    
  	%>
         <script type="text/javascript">
	          $(document).ready(function(){
		      	$("#upload-doctor-photo").show();
		      	$("#doctor-image-file").on("change", function(){
			    	$("#upload-doctor-photo").show();  	
		      	})
	          })
          </script>

      <% if (request.getParameter("res") != null)  { %>
        <% if (request.getParameter("res").equals("success")) { %>
          <span class="text-success">
            Successfully uploaded photo!
          </span>
          <script type="text/javascript">
	          $(document).ready(function(){
		      	$("#upload-doctor-photo").hide();    
	          })
          </script>
        <% } else if (request.getParameter("res").equals("failed")) { %>
          <span class="text-error">
            Failed to upload photo. Please try again.
          </span>
        <% } %>  
      <% } %> 

    <% if (imageUrl != "") { %>
		  <img src="<%= imageUrl %>" style="display: block; margin: 2px auto 2px auto" alt="Doctor photo" id="doctor-photo-src" height="150" width="180"/>
    <% } %> 
    <div class="span12">   
		<form id="doctor-image-upload-form" action="<%= blobstoreService.createUploadUrl("/upload-doctor-image") %>" method="post" enctype="multipart/form-data" data-ajax="false">
		  <input type="hidden" name="doctorKey" value="<%= doctorKey %>">
		  <input type="hidden" name="oldImageKey" value="<%= imageKey %>">
		  <input type="hidden" name="source" value="<%= source %>">
		  <input type="file" data-theme="b" name="myFile" id="doctor-image-file">
		  <input type="submit" data-theme="b" value="Submit" class="btn btn-success" id="upload-doctor-photo" onclick="javascript:$(this).hide();" style="float:right; margin-right:10px;">
		</form>
    </div>
  </body>
</html>