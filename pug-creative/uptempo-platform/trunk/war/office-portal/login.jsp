<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html>
  <!-- Page HEAD include -->
	<%@include file="include/head.jsp" %>

	<!-- BEGIN BODY -->
  <body class="fixed-top">
    <!-- Begin Loading Pages -->
    <div id="main-responder">
      <%@include file="include/page-header.jsp" %>

<!-- BEGIN CONTAINER -->
<div id="container" class="row-fluid">
  <!-- BEGIN SIDEBAR -->
<div data-role="template-page" id="login-page">
<!-- BEGIN PAGE -->  
<div class="main-content">
  <!-- BEGIN PAGE CONTAINER-->
  <div class="container-fluid">
    <!-- BEGIN PAGE HEADER-->   
    <div class="row-fluid">
      <div class="span12">
        <!-- BEGIN PAGE TITLE & BREADCRUMB-->
        <h3 class="page-title">Uptempo Office Portal Login</h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
      </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BEGIN PAGE CONTENT-->
    <div class="row-fluid">
      <div class="span12">
          
        <div class="widget">
          <div class="widget-title">
            <h4><i class="icon-user"></i>Welcome to the Uptempo Office Portal</h4>                  
          </div>

		  <div class="widget-body">
		  <div class="row">
		  <div class="span4 offset4">Please enter your username and password here:</div>
		  <div class="span4"></div>		  
		  <div class="span2 offset4">
		  <form action="index.jsp" name="officeportal-login" method="post">
		  <input type="hidden" name="officeKey" id="officeKey" value="<%=request.getParameter("officeKey") %>" />
		  <label for="username">Username:</label><input type="text" id="username" name="username" value=""/>
		  <label for="password">Password:</label><input type="password" id="password" name="password" value=""/></div>
		  <input type="submit" class="btn btn-primary" value="Login" />
		  </form>
		  <div class="span4"></div>
		  </div>
		  	
		  	
          </div>
        </div>
      </div>
    </div>
    <!-- END PAGE CONTENT-->         
   </div>
   <!-- END PAGE CONTAINER-->
</div>
<!-- END PAGE --> 
        </div>
      <%@include file="include/page-footer.jsp" %>
    </div>
    <!-- End Loading Page -->
    <!-- Page FOOTER include -->
  	<%@include file="include/footer.jsp" %>
        
    
  </body>
</html>
