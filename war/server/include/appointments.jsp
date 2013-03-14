<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>
  <div id="appt-office-selector">
    <label for="appt-office-select">Select an Office</label>
    <select name="appt-office-select" id="appt-office-select">
      <option value="test">--Office Here--</option>
    </select>
    <div>
      <br />
      <strong>Office key: </strong>
      <span id="appt-office-id"></span>
    </div>
  </div>
  <br />
  <div id="appt-left-container">
    <div id="appt-cal-container">
      Choose a Day:
      <input type="text" name="appt-cal-date" id="appt-cal-date" />
    </div>
    <div id="appt-action-container">
      <a href="#"
         data-role="button"
         onclick="uptempo.appointment.showNew();">
        Add an appointment on this date
      </a> <br />
      <a href="#"
         data-role="button"
         onclick="uptempo.appointment.showMultiNew();">
        Batch create appointments
      </a> <br />
    </div>
  </div>
  <div id="appt-right-container">
    <table id="appt-day-table">
      <tr>
        <th>Time</th>
        <th>Appointments</th>
      </tr>
    </table>
  </div>
  <div style="clear:both;"></div>
</div>

<div data-role="popup" id="appt-form-single" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div class="admin-popup-form">
    <h3>
      <span id="app-form-title">Add a new appointment</span>
    </h3>
    <div id="appt-form-errors" class="form-errors"></div>
    <label for="appt-patient-user">Patient E-mail</label>
    <input type="text" size="40" name="appt-patient-user" id="appt-patient-user" value="" placeholder="Patient E-mail" data-theme="a" />
    <div class="ui-grid-a">
      <div class="ui-block-a">
        <label for="app-patient-fname">Patient First Name</label>
        <input type="text" size="20" name="appt-patient-fname" id="appt-patient-fname" value="" placeholder="Patient First Name" data-theme="a" />
      </div>
      <div class="ui-block-b">
        <label for="app-patient-lname">Patient Last Name</label>
        <input type="text" size="20" name="appt-patient-lname" id="appt-patient-lname" value="" placeholder="Patient Last Name" data-theme="a" />
      </div>
    </div>
    <label for="appt-patient-phone">Patient Cell # (for text messaging)</label>
    <input type="text" size="20" name="appt-patient-phone" id="appt-patient-phone" value="" placeholder="Patient Cell #" data-theme="a" />
    <label for="appt-doctor">Appointment Doctor</label>
    <select name="appt-doctor" id="appt-doctor">
      <option value="NONE">--Select a Doctor--</option>
    </select>
    <label for="appt-status">Appointment Status</label>
    <select name="appt-status" id="appt-status">
      <option value="AVAILABLE">Available</option>
      <option value="RESERVED">Reserved</option>
      <option value="SCHEDULED">Scheduled</option>
      <option value="CANCELLED">Cancelled</option>
    </select>
    <label for="appt-description">Description</label>
    <input type="text" size="40" name="appt-description" id="appt-description" value="" placeholder="Description" data-theme="a" />
    <label for="appt-notes">Notes</label>
    <input type="text" size="60" maxlength="499" name="appt-notes" id="appt-notes" value="" placeholder="Notes" data-theme="a" />
    <label for="appt-date">Appointment Date</label>
    <input type="text" size="40" name="appt-date" id="appt-date" value="" placeholder="Appointment Date" data-theme="a" />
    <div class="ui-grid-b">
      <div class="ui-block-a">
        <label for="appt-start-hour">Start Hour</label>
        <select name="appt-start-hour" id="appt-start-hour">
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
      <div class="ui-block-b">
        <label for="appt-start-min">Start Minute</label>
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
      <div class="ui-block-c">
        <label for="appt-start-ap">AM/PM</label>
        <select name="appt-start-ap" id="appt-start-ap">
          <option value="AM" selected>AM</option>
          <option value="PM">PM</option>
        </select>
      </div>
    </div>
    <div class="ui-grid-b">
      <div class="ui-block-a">
        <label for="appt-end-hour">End Hour</label>
        <select name="appt-end-hour" id="appt-end-hour">
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
      <div class="ui-block-b">
        <label for="appt-end-min">End Minute</label>
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
      <div class="ui-block-c">
        <label for="appt-end-ap">AM/PM</label>
        <select name="appt-end-ap" id="appt-end-ap">
          <option value="AM" selected>AM</option>
          <option value="PM">PM</option>
        </select>
      </div>
    </div>
    <input type="hidden" name="appt-start-time" id="appt-start-time" />
    <input type="hidden" name="appt-end-time" id="appt-end-time" />
    <input type="hidden" name="appt-key" id="appt-key" />
    <input id="appt-form-submit" type="submit" data-theme="b" />
  </div>
</div>

<!-- Add Multiple Appointments -->
<div data-role="popup" id="appt-form-multi" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div class="admin-popup-form">
    <h3>Batch Add Appointments</h3>
    <div id="appt-multi-errors" class="form-errors"></div>
    <label for="appt-multi-doctor">Appointment Doctor</label>
    <select name="appt-multi-doctor" id="appt-multi-doctor">
      <option value="NONE">--Select a Doctor--</option>
    </select>
    <label for="appt-multi-status">Appointment Status</label>
    <select name="appt-multi-status" id="appt-multi-status">
      <option value="AVAILABLE">Available</option>
      <option value="RESERVED">Reserved</option>
      <option value="SCHEDULED">Scheduled</option>
      <option value="CANCELLED">Cancelled</option>
    </select>
    <label for="appt-multi-description">Description</label>
    <input type="text" size="40" name="appt-multi-description" id="appt-multi-description" value="" placeholder="Description" data-theme="a" />
    <label for="appt-multi-date">Appointment Date</label>
    <input type="text" size="40" name="appt-multi-date" id="appt-multi-date" value="" placeholder="Appointment Date" data-theme="a" />
    <div class="ui-grid-b">
      <div class="ui-block-a">
        <label for="appt-multi-start-hour">Start Hour</label>
        <select name="appt-multi-start-hour" id="appt-multi-start-hour">
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
      <div class="ui-block-b">
        <label for="appt-multi-start-min">Start Minute</label>
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
      <div class="ui-block-c">
        <label for="appt-multi-start-ap">AM/PM</label>
        <select name="appt-multi-start-ap" id="appt-multi-start-ap">
          <option value="AM" selected>AM</option>
          <option value="PM">PM</option>
        </select>
      </div>
    </div>
    <div class="ui-grid-a">
      <div class="ui-block-a">
        <label for="appt-multi-length">Appointment Length</label>
        <select name="appt-multi-length" id="appt-multi-length">
          <option value="15">15 min</option>
          <option value="30">30 min</option>
          <option value="45">45 min</option>
          <option value="60">1 hour</option>
          <option value="75">1 hour 15 min</option>
          <option value="90">1 hour 30 min</option>
        </select>
      </div>
      <div class="ui-block-b">
        <label for="appt-multi-spacing">Minutes spacing</label>
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
    <div class="ui-grid-b">
      <div class="ui-block-a">
        <label for="appt-multi-end-hour">End Hour</label>
        <select name="appt-multi-end-hour" id="appt-multi-end-hour">
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
      <div class="ui-block-b">
        <label for="appt-multi-end-min">End Minute</label>
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
      <div class="ui-block-c">
        <label for="appt-multi-end-ap">AM/PM</label>
        <select name="appt-multi-end-ap" id="appt-multi-end-ap">
          <option value="AM" selected>AM</option>
          <option value="PM">PM</option>
        </select>
      </div>
    </div>
    <input type="hidden" name="appt-multi-start-time" id="appt-multi-start-time" />
    <input type="hidden" name="appt-multi-end-time" id="appt-multi-end-time" />
    <input id="appt-multi-form-submit" type="submit" data-theme="b" value="Create Appointments" />
  </div>
</div>

<!-- Delete Appointment Popup -->
<div data-role="popup" id="appt-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="appt-confirm-popup-heading">Delete Appointment?</span></h3><br />
    <div id="del-form-errors" class="form-errors"></div>
    <span id="appt-confirm-popup-body">Are you sure you want to delete this appointment?</span><br />
    <input type="hidden" name="appt-key-delete" id="appt-key-delete" />
    <input type="hidden" name="appt-doctor-delete" id="appt-doctor-delete" />
    <input type="hidden" name="appt-patient-delete" id="appt-patient-delete" />
    <button type="submit" data-theme="b" id="appt-confirm-popup-delete">Delete Appointment</button>
  </div>
</div>
