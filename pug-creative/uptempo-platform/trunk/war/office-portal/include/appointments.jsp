<!-- BEGIN PAGE -->  
<div class="main-content">
  <!-- BEGIN PAGE CONTAINER-->
  <div class="container-fluid">
    <!-- BEGIN PAGE HEADER-->   
    <div class="row-fluid">
      <div class="span12">
        <!-- BEGIN PAGE TITLE & BREADCRUMB-->
        <h3 class="page-title">Appointments <small>Page</small></h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
      </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BEGIN PAGE CONTENT-->
    <div class="row-fluid">
      <div class="span12">
          <ul class="nav nav-pills"  id="appt-offices-list">
  
          </ul>
        <div class="widget">
          <div class="widget-body">
            <div class="span4 persons-list">
              <h3>
                <div class="control-group">
                  <div class="controls">
                    <select class="input-large m-wrap" tabindex="1" id="doctors-list" name="doctors-list" onchange="javascript:uptempo.officePortal.appointments.getDoctorAllAppointments($(this).val());">
                    </select>
                  </div>
                </div>                
              </h3>
              <form action="#" class="form-horizontal">
                <div class="control-group">
                  <div id="appointments_date_picker"></div>
                </div>
              </form>
            </div>
            <div class="span8 person-details">
              <div class="buttons-container">
                <button class="btn btn-primary" type="button">Add Appointment</button>
                <button class="btn btn-primary" type="button">Add Batch Appointments</button>                        
              </div>
              <div class="widget">
                <div class="widget-title">
                  <h4>Appointments Table</h4>
                </div>
                <div class="widget-body">
                  <!-- BEGIN EXAMPLE TABLE widget-->
                  <table class="table table-striped table-bordered" id="appointments-table">
                    <thead>
                        <tr>
                            <th style="width:8px;"><input type="checkbox" class="group-checkable" data-set="#sample_1 .checkboxes" /></th>
                            <th>Appointment Time</th>
                            <th>Doctor</th>
                            <th>Patient</th>
                            <th>Phone</th>
                            <th>Email</th>
                            <th>Source</th>
                            <th>Actions</th>
                        </tr>
                      </thead>
                    <tbody>
                      
                    </tbody>
                  </table>
                  <!-- END EXAMPLE TABLE widget-->                                
                </div>
              </div>
            </div>
            <div class="space5"></div>
          </div>
        </div>
      </div>
    </div>
    <!-- END PAGE CONTENT-->           
  </div>
  <!-- END PAGE CONTAINER-->
</div>
<!-- END PAGE -->  

<div class="modal hide fade" id="modal-appt-details">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Appointment Details</h3>
      </div>
      <div class="modal-body" id="appt-details" >
          <input type="hidden" id="appt-doctor">
          <input type="hidden" id="appt-office-select">
          <table class="table table-borderless">
                <tbody>
                  <tr>
                    <td>Patient E-mail:</td>
                    <td><input  type="text" id="appt-patient-user"></td>
                  </tr>
                  <tr>
                    <td>Patient First Name:</td>
                    <td><input  type="text" id="appt-patient-fname"></td>
                  </tr>
                  <tr>
                    <td>Patient Last Name:</td>
                    <td><input type="text" id="appt-patient-lname"></td>
                  </tr>
                  <tr>
                    <td>Patient Phone Number:</td>
                    <td><input type="text" id="appt-patient-phone"></td>
                  </tr>
                  <tr>
                    <td>Status:</td>
                    <td>
                        <select name="appt-status" id="appt-status">
                          <option value="HELD">Reserved</option>
                          <option value="RESERVED">Scheduled</option>
                          <option value="AVAILABLE">Available</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                      </td>
                  </tr>
                  <tr>
                    <td>Description:</td>
                    <td><input type="text" id="appt-description"></td>
                  </tr>
                  <tr>
                    <td>Notes:</td>
                    <td><input type="text" id="appt-notes"></td>
                  </tr>
                  <tr>
                    <td>Appointment Date:</td>
                    <td><input type="text" id="appt-date"></td>
                  </tr>
                  <tr>
                    <td>Start Hour:</td>
                    <td><input type="text" id="appt-start-hr"></td>
                  </tr>
                  <tr>
                    <td>Start Minute:</td>
                    <td><input type="text" id="appt-start-min"></td>
                  </tr>
                  <tr>
                    <td>End Hour:</td>
                    <td><input type="text" id="appt-end-hr"></td>
                  </tr>
                  <tr>
                    <td>End Minute:</td>
                    <td><input type="text" id="appt-end-min"></td>
                  </tr>
                </tbody>
              </table>
      </div>
    <div class="modal-footer">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
    <button class="btn btn-primary" data-dismiss="modal" aria-hidden="true" id="update-confirmed">Update</button>
  </div>
    </div>



<div class="modal hide fade" id="modal-appt-delete">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Warning</h3>
      </div>
      <div class="modal-body" id="message">
        <p>Are you sure?</p>
      </div>
          <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Do not delete</button>
              <button class="btn btn-danger" data-dismiss="modal" aria-hidden="true" id="delete-confirmed">Delete</button>
          </div>
    </div>