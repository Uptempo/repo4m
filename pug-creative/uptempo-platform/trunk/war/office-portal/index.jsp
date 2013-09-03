<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html>
  <!-- Page HEAD include -->
	<%@include file="include/head.jsp" %>

	<!-- BEGIN BODY -->
  <body class="fixed-top">
    <!-- Begin Users Page -->
    <div id="main-responder">
      <%@include file="include/page-header.jsp" %>
      <%@include file="include/page-sidebar.jsp" %>
        <div id="users-page" data-role="template-page">
          <%@include file="include/users.jsp" %>      
        </div>
        <div id="doctors-page" data-role="template-page">
          <%@include file="include/doctors.jsp" %>      
        </div>
        <div id="appointments-page" data-role="template-page">
          <%@include file="include/appointments.jsp" %>      
        </div>
        <div id="billinggroup-page" data-role="template-page">
          <%@include file="include/billinggroup.jsp" %>      
        </div>  
        <div id="billingoffice-page" data-role="template-page">
          <%@include file="include/billingoffice.jsp" %>      
        </div>  
      <%@include file="include/page-footer.jsp" %>
    </div>
    <!-- End Users Page -->
    <!-- Page FOOTER include -->
  	<%@include file="include/footer.jsp" %>


          <script>
      $(window).bind("load", function() {

        //*** Initial Page to display
        $("#users-page").css('display', 'block');

        //*** Hide all other pages and keep only the initial page
        $("#doctors-page").css('display', 'none');
        $("#appointments-page").css('display', 'none');

        //*** This binds the menu to the page selected
        console.log("page fully loaded");
        $("#doctors-link").click(function() {
          /* Act on the event */
          console.log("clicked on the menu");
          $(".sidebar-menu li").removeClass('active');
          $("#doctors-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#doctors-page").css('display', 'block');

        });

        $("#users-link").click(function() {
          /* Act on the event */
          console.log("clicked on the menu");
          $(".sidebar-menu li").removeClass('active');
          $("#users-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#users-page").css('display', 'block');
        });

        $("#appointments-link").click(function() {
          /* Act on the event */
          console.log("clicked on the menu");
          $(".sidebar-menu li").removeClass('active');
          $("#appointments-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#appointments-page").css('display', 'block');
        });

        $("#billinggroup-link").click(function() {
          /* Act on the event */
          console.log("clicked on the menu");
          $(".sidebar-menu li").removeClass('active');
          $("#billinggroup-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#billinggroup-page").css('display', 'block');
        });

        $("#billingoffice-link").click(function() {
          /* Act on the event */
          console.log("clicked on the menu");
          $(".sidebar-menu li").removeClass('active');
          $("#billingoffice-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#billingoffice-page").css('display', 'block');
        });


      });
    </script>
  </body>
</html>
