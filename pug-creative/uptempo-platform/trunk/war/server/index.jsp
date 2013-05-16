<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html>
  <!-- Page HEAD include -->
  <%@include file="include/head.jsp" %>
  <body>
      <!-- Begin Users Page -->
      <div data-role="page" id="users">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/users.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End Users Page -->

      <!-- Begin Config Value Page -->
      <div data-role="page" id="config">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/config.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End Config Value Page -->

      <!-- Begin Application Page -->
      <div data-role="page" id="applications">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/applications.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End Application Page -->

      <!-- Begin Audit Page -->
      <div data-role="page" id="auditing">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/audit.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End Audit Page -->

      <!-- Begin Audit Log Page -->
      <div data-role="page" id="auditlog">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/audit-log.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End Audit Page -->
      
      <!-- Begin Appointment Page -->
      <div data-role="page" id="appointment">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/appointments.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End Appointment Page -->
      
      <!-- Begin Staticlist Page -->
      <div data-role="page" id="staticlists">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/staticlists.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End Staticlist Page -->
      
      <!-- Begin billing groups Page -->
      <div data-role="page" id="billinggroups">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/billinggroup.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End billing groups Page -->
      
      <!-- Begin billing office Page -->
      <div data-role="page" id="billingoffices">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/billingoffice.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End billing office Page -->
      
      <!-- Begin doctor Page -->
      <div data-role="page" id="doctor">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/doctor.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End doctor Page -->

      <!-- Begin image categories Page -->
      <div data-role="page" id="imagecategories">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/imagecategories.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End image category Page -->

      <!-- Begin images Page -->
      <div data-role="page" id="images">
        <%@include file="include/page-header.jsp" %>
        <br />
        <%@include file="include/images.jsp" %>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End images Page -->     

  </body>
</html>
