<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html>
  <!-- Page HEAD include -->
  <%@include file="include/office-signup-head.jsp" %>
  <body>
      <!-- Begin Users Page -->
      <div data-role="page" id="office-confirm">
        <div data-role="header" data-theme="c">
          <img height="75" alt="Uptempo Appointment" src="server/img/uptempo-logo-large.jpg" />
          <h1>Uptempo Appointment Office Signup</h1>
        </div>
        <div data-role="content" data-theme="c">
          <h2>Office Signup Complete!</h2>
          Thank you for signing your office up for the Uptempo appointment system!
          <br /><br />
          <%=request.getAttribute("message") %>
        </div>
        <%@include file="include/page-footer.jsp" %>
      </div>
  </body>
</html>
