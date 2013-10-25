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
        <div id="email-page" data-role="template-page">
          <%@include file="include/email.jsp" %>      
        </div>          
      <%@include file="include/page-footer.jsp" %>
    </div>
    <!-- End Loading Page -->
    <!-- Page FOOTER include -->
  	<%@include file="include/footer.jsp" %>
        
    <div class="modal hide fade" id="uptempo-alert">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3 id="alert-title">Warning</h3>
      </div>
      <div class="modal-body" id="message">
        <p></p>
      </div>
          <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
          </div>
    </div>
    
    <script>
      $(window).bind("load", function() {
		$("#appt-date").datepicker();
		$("#appt-multi-date").datepicker();        
        //*** Hide all other pages and keep only the initial page
        $('div[data-role="template-page"]').css('display', 'none');
        
		$('#appointments-table .group-checkable').change(function () {
            var set = jQuery(this).attr("data-set");
            var checked = jQuery(this).is(":checked");
            jQuery(set).each(function () {
                if (checked) {
                    $(this).attr("checked", true);
                } else {
                    $(this).attr("checked", false);
                }
            });
  			uptempo.officePortal.appointments.anyApptChecked();
        });
        
        $(document).on('change','#appointments-table .checkboxes', function () {
			uptempo.officePortal.appointments.anyApptChecked();	        
        });
        
        //*** Initial Page to display
        uptempo.officePortal.billingGroup.getBillingGroupInfo(uptempo.officePortal.billingGroup.groupKey);
        $("#billinggroup-page").css('display', 'block');

        //*** This binds the menu to the page selected
        $("#doctors-link").click(function() {
          /* Act on the event */
          uptempo.officePortal.doctors.createOfficesList(uptempo.officePortal.billingGroup.groupKey);
          $(".sidebar-menu li").removeClass('active');
          $("#doctors-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#doctors-page").css('display', 'block');

        });

        $("#users-link").click(function() {
          /* Act on the event */
          $(".sidebar-menu li").removeClass('active');
          $("#users-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#users-page").css('display', 'block');
        });
          

          
        $("#appointments-link").click(function() {
          /* Act on the event */
          uptempo.officePortal.appointments.createOfficesList(uptempo.officePortal.billingGroup.groupKey);
          $(".sidebar-menu li").removeClass('active');
          $("#appointments-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#appointments-page").css('display', 'block');
        });

        $("#billinggroup-link").click(function() {
          /* Act on the event */
            uptempo.officePortal.billingGroup.getBillingGroupInfo(uptempo.officePortal.billingGroup.groupKey);
          $(".sidebar-menu li").removeClass('active');
          $("#billinggroup-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#billinggroup-page").css('display', 'block');
        });

        $("#billingoffice-link").click(function() {
          /* Act on the event */
          uptempo.officePortal.billingOffices.getBillingOfficesInfo(uptempo.officePortal.billingGroup.groupKey);
          $(".sidebar-menu li").removeClass('active');
          $("#billingoffice-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#billingoffice-page").css('display', 'block');
        });

        $("#email-link").click(function() {
          /* Act on the event */
          $(".sidebar-menu li").removeClass('active');
          $("#email-link").parent().addClass('active');
          $('div[data-role="template-page"]').css('display', 'none');
          $("#email-page").css('display', 'block');
        });

      });
        
    </script>
  </body>
</html>
