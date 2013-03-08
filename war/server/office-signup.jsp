<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html>
  <!-- Page HEAD include -->
  <%@include file="include/office-signup-head.jsp" %>
  <body>
      <!-- Begin Office Signup Page -->
      <div data-role="page" id="office-signup">
        <div data-role="header" data-theme="c">
          <img height="75" alt="Uptempo Appointment" src="server/img/uptempo-logo-large.jpg" />
          <h1>Uptempo Appointment Office Signup</h1>
        </div>
        <div data-role="content" data-theme="c">
          <h2>Sign your office up for the Uptempo Appointment System</h2>
          <br />
          <div class="form-errors"></div>
          <div class="ui-grid-a">
            <div class="ui-block-a" style="width:25%;margin-right:25%;">
              <label for="office-group" class="form-required">Office Group</label>
              <select name="office-group" id="office-group" data-mini="true">
                <option value="none">--No Office Group--</option>
                <option value="key-here">Dr. Zhivago office group</option>
              </select>
            </div>
            <div class="ui-block-b">
              <label for="office-name" class="form-required">Office Name</label>
              <input type="text" size="40" style="width:75%;" name="office-name" id="office-name" value="" placeholder="Office Name" />
            </div>
            <div class="ui-block-a">
              <label for="office-address-1" class="form-required">Office Address 1</label>
              <input type="text" size="40" style="width:75%;" name="office-address-1" id="office-address-1" value="" placeholder="Address 1" />
            </div>
            <div class="ui-block-b">
              <label for="office-address-2">Office Address 2</label>
              <input type="text" size="40" style="width:75%;" name="office-address-2" id="office-address-2" value="" placeholder="Address 2" />
            </div>
            <div class="ui-block-a">
              <label for="office-city" class="form-required">City</label>
              <input type="text" size="40" style="width:75%;" name="office-city" id="office-city" value="" placeholder="City" />
            </div>
            <div class="ui-block-b">
              <label for="office-state" class="form-required">State</label>
              <input type="text" size="5" style="width:25%;" name="office-state" id="office-state" value="" placeholder="State" />
            </div>
            <div class="ui-block-a">
              <label for="office-zip" class="form-required">Zip Code</label>
              <input type="text" size="40" style="width:75%;" name="office-zip" id="office-zip" value="" placeholder="Zip Code" />
            </div>
            <div class="ui-block-b">
              <label for="office-phone" class="form-required">Office Phone</label>
              <input type="text" size="40" style="width:75%;" name="office-phone" id="office-phone" value="" placeholder="Phone" />
            </div>
            <div class="ui-block-a">
              <label for="office-fax">Office Fax</label>
              <input type="text" size="40" style="width:75%;" name="office-fax" id="office-fax" value="" placeholder="Office Fax" />
            </div>
            <div class="ui-block-b">
              <label for="office-email" class="form-required">Office E-mail</label>
              <input type="text" size="40" style="width:75%;" name="office-email" id="office-email" value="" placeholder="Office E-mail" />
            </div>
            <div class="ui-block-b">
              <label for="office-google-cal">
                Use Google Calendar to display your office appointments?
                If you select this option, you will be prompted to login with the Google account that
                you wish to use to display appointments.
              </label>
              <input type="checkbox" name="office-google-cal" id="office-google-cal" value="manage" />
            </div>
          </div>
          <br />
          <label for="office-notes">Office notes</label>
          <textarea rows="10" cols="40" id="office-notes" placeholder="Office Notes"></textarea>
          <br />
          <label for="office-notes">Office hours</label>
          <textarea rows="10" cols="40" id="office-hours" placeholder="Office Hours"></textarea>
          <br />
          <input id="office-submit" type="submit" data-theme="b" value="Submit" onclick="uptempo.office.submitNew();" />
          By submitting this form, you agree to the terms of service.
        </div>
        <%@include file="include/page-footer.jsp" %>
      </div>
      <!-- End Office Signup Page -->
  </body>
</html>
