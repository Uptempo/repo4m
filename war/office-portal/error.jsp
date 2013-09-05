<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html>
  <!-- Page HEAD include -->
	<%@include file="include/head.jsp" %>

	<!-- BEGIN BODY -->
  <body class="fixed-top">
    <div id="main-responder">
      <%@include file="include/page-header.jsp" %>
      <!-- BEGIN PAGE -->  
      <div id="main-content">
        <!-- BEGIN PAGE CONTAINER-->
        <div class="container-fluid">
          <!-- BEGIN PAGE HEADER-->   
          <div class="row-fluid">
            <div class="span12">
              <h1 class="page-title">Error</h1>
              You have reached this page because of an error:
              <%=request.getAttribute("error-message") %>
            </div>
          </div>
        </div>
      </div>
      <%@include file="include/page-footer.jsp" %>
    </div>
    <!-- Page FOOTER include -->
  	<%@include file="include/footer.jsp" %>
  </body>
</html>
