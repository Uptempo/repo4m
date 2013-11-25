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
            <div class="span3 persons-list">
              <h3>
                <div class="control-group">
                  <div class="controls">
                    <select class="input-large m-wrap" tabindex="1" id="appt-doctors-list" name="appt-doctors-list" onchange="javascript:uptempo.officePortal.appointments.getDoctorAllAppointments($(this).val());">
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
            <div class="span9 person-details">
              <div class="buttons-container">
                <button class="btn btn-danger" type="button" id="delete-selected-appt" onclick="javascript:uptempo.officePortal.appointments.deleteSelected();" disabled>Delete Selected</button>
                <button class="btn btn-primary" type="button" onclick="javascript:uptempo.officePortal.appointments.addApptForm();">Add Appointment</button>
                <button class="btn btn-primary" type="button" onclick="javascript:uptempo.officePortal.appointments.addMultiApptForm();">Add Batch Appointments</button>                        
              </div>
              <div class="widget">
                <div class="widget-title">
                  <h4>Appointments Table</h4>
                </div>
                <input name="appt-office-tz" id="appt-office-tz" type="hidden" value="" />
                <div class="widget-body" id="appointments-table-widget">
                  <!-- BEGIN EXAMPLE TABLE widget-->
                  <table class="table table-striped table-bordered" id="appointments-table">
                    <thead>
                        <tr>
                            <th style="width:8px;"><input type="checkbox" class="group-checkable" data-set="#appointments-table .checkboxes" /></th>
                            <th>Appointment Time</th>
                            <th>Doctor</th>
                            <th>Status</th>
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
                    <td>Source:</td>
                    <td><input type="text" id="appt-source"></td>
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
                    <td><input type="text" id="appt-date"></td> <!-- QUI METTERE DATEPICKER-->
                  </tr>
                  <tr>
                  <td colspan="2">
                  <div class="row-fluid">
						<div class="span4">Start Hour:
							<select name="appt-start-hour" id="appt-start-hour">
					          <option value="0">0</option>							
					          <option value="1">1</option>
					          <option value="2">2</option>
					          <option value="3">3</option>
					          <option value="4">4</option>
					          <option value="5">5</option>
					          <option value="6">6</option>
					          <option value="7" selected>7</option>
					          <option value="8">8</option>
					          <option value="9">9</option>
					          <option value="10">10</option>
					          <option value="11">11</option>
					          <option value="12">12</option>
					        </select>
	                    </div>
						<div class="span4">Start Minute:
							<select name="appt-start-min" id="appt-start-min">
					          <option value="00">:00</option>
					          <option value="05">:05</option>
					          <option value="10">:10</option>
					          <option value="15">:15</option>
					          <option value="20">:20</option>
					          <option value="25">:25</option>
					          <option value="30">:30</option>
					          <option value="35">:35</option>
					          <option value="40">:40</option>
					          <option value="45">:45</option>
					          <option value="50">:50</option>
					          <option value="55">:55</option>
					        </select>
					   </div>
					   <div class="span2">
					   AM/PM
					        <select name="appt-start-ap" id="appt-start-ap">
					          <option value="AM" selected>AM</option>
					          <option value="PM">PM</option>
					        </select>
					   </div>
				  </div>
                  </td>
                  </tr>
                  <tr>
                  <td colspan="2">
                  <div class="row-fluid">
						<div class="span4">End Hour:
					        <select name="appt-end-hour" id="appt-end-hour">
					          <option value="0">0</option>
					          <option value="1">1</option>
					          <option value="2">2</option>
					          <option value="3">3</option>
					          <option value="4">4</option>
					          <option value="5">5</option>
					          <option value="6">6</option>
					          <option value="7" selected>7</option>
					          <option value="8">8</option>
					          <option value="9">9</option>
					          <option value="10">10</option>
					          <option value="11">11</option>
					          <option value="12">12</option>
							        </select>

	                    </div>
						<div class="span4">End Minute:
					        <select name="appt-end-min" id="appt-end-min">
					          <option value="00">:00</option>
					          <option value="05">:05</option>
					          <option value="10">:10</option>
					          <option value="15">:15</option>
					          <option value="20">:20</option>
					          <option value="25">:25</option>
					          <option value="30">:30</option>
					          <option value="35">:35</option>
					          <option value="40">:40</option>
					          <option value="45">:45</option>
					          <option value="50">:50</option>
					          <option value="55">:55</option>
					        </select>

					   </div>
					   <div class="span2">
					   AM/PM
				        <select name="appt-end-ap" id="appt-end-ap">
				          <option value="AM" selected>AM</option>
				          <option value="PM">PM</option>
				        </select>
					   </div>
					       <input type="hidden" name="appt-start-hr" id="appt-start-hr" />
						   <input type="hidden" name="appt-end-hr" id="appt-end-hr" />
				  </div>
                  </td>
                  </tr>
                </tbody>
              </table>
      </div>
	  <div class="modal-footer">
    	<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	    <button class="btn btn-primary" data-dismiss="modal" aria-hidden="true" id="update-confirmed">Update</button>
		</div>
    </div> <!-- End of modal appointment details-->
   



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
              <button class="btn btn-danger" data-dismiss="modal" aria-hidden="true" id="delete-appt-confirmed">Delete</button>
          </div>
    </div> <!-- End of modal warning message-->
    
    
    <div class="modal hide fade" id="modal-appt-multiple">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>Appointment Details</h3>
      </div>
      <div class="modal-body" id="appt-details" >
          <input type="hidden" id="appt-multi-doctor">
          <input type="hidden" id="appt-multi-office-select">
          <table class="table table-borderless">
                <tbody>
                  <tr>
                    <td>Status:</td>
                    <td>
                        <select name="appt-multi-status" id="appt-multi-status">
                          <option value="HELD">Reserved</option>
                          <option value="RESERVED">Scheduled</option>
                          <option value="AVAILABLE">Available</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                      </td>
                  </tr>
                  <tr>
                    <td>Description:</td>
                    <td><input type="text" id="appt-multi-description"></td>
                  </tr>
                  <tr>
                    <td>Appointment Block Start Date:</td> 
                    <td><input type="text" id="appt-multi-date"></td> <!-- QUI METTERE DATEPICKER-->
                  </tr>
                  <tr>
                  <td colspan="2">
                  <div class="row-fluid">
						<div class="span4">Start Hour:
							<select name="appt-multi-start-hr" id="appt-multi-start-hr">
					          <option value="0">0</option>							
					          <option value="1">1</option>
					          <option value="2">2</option>
					          <option value="3">3</option>
					          <option value="4">4</option>
					          <option value="5">5</option>
					          <option value="6">6</option>
					          <option value="7" selected>7</option>
					          <option value="8">8</option>
					          <option value="9">9</option>
					          <option value="10">10</option>
					          <option value="11">11</option>
					          <option value="12">12</option>
					        </select>
	                    </div>
						<div class="span4">Start Minute:
							<select name="appt-multi-start-min" id="appt-multi-start-min">
					          <option value="00">:00</option>
					          <option value="05">:05</option>
					          <option value="10">:10</option>
					          <option value="15">:15</option>
					          <option value="20">:20</option>
					          <option value="25">:25</option>
					          <option value="30">:30</option>
					          <option value="35">:35</option>
					          <option value="40">:40</option>
					          <option value="45">:45</option>
					          <option value="50">:50</option>
					          <option value="55">:55</option>
					        </select>
					   </div>
					   <div class="span2">
					        AM/PM
						    <select name="appt-multi-start-ap" id="appt-multi-start-ap">
						       <option value="AM" selected>AM</option>
						       <option value="PM">PM</option>
						    </select>
					   </div>
				  </div>
                  </td>
                  </tr>
                  <tr>
                  	<td colspan="2">
                  <div class="row-fluid">
						<div class="span6">Appointment Length:
					        <select name="appt-multi-length" id="appt-multi-length">
					          <option value="15">15 min</option>
					          <option value="20">20 min</option>
					          <option value="30">30 min</option>
					          <option value="45">45 min</option>
					          <option value="60">1 hour</option>
					          <option value="75">1 hour 15 min</option>
					          <option value="90">1 hour 30 min</option>
					        </select>

	                    </div>
						<div class="span6">Minutes Spacing:
					        <select name="appt-multi-spacing" id="appt-multi-spacing">
					          <option value="0">0</option>
					          <option value="5">5 min</option>
					          <option value="10">10 min</option>
					          <option value="15">15 min</option>
					          <option value="20">20 min</option>
					          <option value="25">25 min</option>
					          <option value="30">30 min</option>
					          <option value="35">35 min</option>
					          <option value="40">40 min</option>
					          <option value="45">45 min</option>
					          <option value="50">50 min</option>
					          <option value="55">55 min</option>
					          <option value="60">60 min</option>
					        </select>

					   </div>
				  </div>
                  </td>
                  </tr>
                  <tr>
                  <td colspan="2">
                  <div class="row-fluid">
						<div class="span4">End Hour:
					        <select name="appt-multi-end-hr" id="appt-multi-end-hr">
					          <option value="0">0</option>
					          <option value="1">1</option>
					          <option value="2">2</option>
					          <option value="3">3</option>
					          <option value="4">4</option>
					          <option value="5">5</option>
					          <option value="6">6</option>
					          <option value="7" selected>7</option>
					          <option value="8">8</option>
					          <option value="9">9</option>
					          <option value="10">10</option>
					          <option value="11">11</option>
					          <option value="12">12</option>
							        </select>

	                    </div>
						<div class="span4">End Minute:
					        <select name="appt-multi-end-min" id="appt-multi-end-min">
					          <option value="00">:00</option>
					          <option value="05">:05</option>
					          <option value="10">:10</option>
					          <option value="15">:15</option>
					          <option value="20">:20</option>
					          <option value="25">:25</option>
					          <option value="30">:30</option>
					          <option value="35">:35</option>
					          <option value="40">:40</option>
					          <option value="45">:45</option>
					          <option value="50">:50</option>
					          <option value="55">:55</option>
					        </select>

					   </div>
   					   <div class="span2">
					        AM/PM
						    <select name="appt-multi-end-ap" id="appt-multi-end-ap">
						       <option value="AM" selected>AM</option>
						       <option value="PM">PM</option>
						    </select>
					   </div>

				  </div>
                  </td>
                  <tr>
                  <td colspan="2">
                  <div class="span6">
                  <label for="appt-multi-days">Number of days to schedule</label>
                  <select name="appt-multi-days" id="appt-multi-days">
			          <option value="1">1</option>
			          <option value="2">2</option>
			          <option value="3">3</option>
			          <option value="4">4</option>
			          <option value="5">5</option>
			          <option value="6">6</option>
			          <option value="7">7</option>
			          <option value="8">8</option>
			          <option value="9">9</option>
			          <option value="10">10</option>
			          <option value="11">11</option>
			          <option value="12">12</option>
			          <option value="13">13</option>
			          <option value="14">14</option>
			          <option value="15">15</option>
			          <option value="16">16</option>
			          <option value="17">17</option>
			          <option value="18">18</option>
			          <option value="19">19</option>
			          <option value="20">20</option>
			          <option value="21">21</option>
			          <option value="22">22</option>
			          <option value="23">23</option>
			          <option value="24">24</option>
			          <option value="25">25</option>
			          <option value="26">26</option>
			          <option value="27">27</option>
			          <option value="28">28</option>
			        </select>
                  </div>  
                  <div class="span6">
                  <div class="span6">
                  <label for="appt-multi-weekdays">Schedule on weekdays</label>
                  <input type="checkbox" name="appt-multi-weekdays" id="appt-multi-weekdays" checked />
                  </div>
                  <div class="span6"> 
                  <label for="appt-multi-weekends">Schedule on weekends</label>
				  <input type="checkbox" name="appt-multi-weekends" id="appt-multi-weekends" />
                  </div>
                  </div>
                  </td>
                  </tr>
                  </tr>
                </tbody>
              </table>
      </div>
	  <div class="modal-footer">
    	<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	    <button class="btn btn-primary" data-dismiss="modal" aria-hidden="true" id="add-multiple" onclick="javascript:uptempo.officePortal.appointments.addMultiAppt();">Create Appointments</button>
		</div>
    </div> <!-- End of modal appointment multiple-->