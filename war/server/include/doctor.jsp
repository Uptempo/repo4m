<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="msAdmin.doctor.showNew();">
        Create a Doctor 
      </a>
    </div>
  </div>
  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <input style="width:47%;float:left;margin-top: 1.3em;" type="text" size="40" name="doctor-search" id="doctor-search" value="" placeholder="Doctor first, last name or first speciality" data-theme="a" />
    <div style="width:25%;float:right;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="msAdmin.doctor.search();">
        Search
      </a>
    </div>
  </div>
  <div style="clear:both;"></div>
  <div id="doctor-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

  

<div data-role="popup" id="doctor-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;overflow-y:scroll;height: 600px">
    <h3>
      <span id="doctor-form-title">Create a new Doctor</span>
    </h3>
    <div id="doctor-form-errors" class="form-errors"></div>
    
    <label for="doctor-billingOffice">Office</label>
    <select id="doctor-billingOffice" name="doctor-billingOffice">
    </select>
    <label for="doctor-FirstName">First Name</label>
    <input type="text" size="40" name="doctor-firstName" id="doctor-firstName" value="" placeholder="First name" data-theme="a" />

    <label for="doctor-LastName">Last Name</label>
    <input type="text" size="40" name="doctor-lastName" id="doctor-lastName" value="" placeholder="Last name" data-theme="a" />
    
    <label for="doctor-email">Email</label>
    <input type="text" size="40" name="doctor-email" id="doctor-email" value="" placeholder="Email address" data-theme="a" />
    
    <label for="doctor-education">Education</label>
    <input type="text" size="40" name="doctor-education" id="doctor-education" value="" placeholder="Education" data-theme="a" />
    
    <label for="doctor-notes">Notes</label>
    <textarea rows="10" cols="80" id="doctor-notes" placeholder="Notes" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"></textarea>

    <label for="doctor-publicDescription">Public description</label>
    <input type="text" size="40" name="doctor-publicDescription" id="doctor-publicDescription" value="" placeholder="Public description" data-theme="a" />

    <div class="action-bar" style="margin-top: 0.5em; width:90%;">
      <div style="width:55%;float:left;margin: 0.5em;">
        <a href="#" data-role="button"
          onclick="msAdmin.doctor.addTextFieldAndIncreaseForOneValueCounter('', '#doctor-table-title-values', '');">
        Add title field
        </a>
      </div>
      <div style="float:left;" data-role="fieldcontain">
        <table id="doctor-table-title-values" data-theme="a" >
        </table>
      </div>
    </div>
    <div class="action-bar" data-role="fieldcontain" style="float:left; margin-top: 0.5em; width:90%;">
      <div style="width:55%;float:left;margin: 0.5em;">
        <a href="#" data-role="button"
          onclick="msAdmin.doctor.addTextFieldAndIncreaseForOneValueCounter('', '#doctor-table-speciality-values', '');">
        Add speciality field
        </a>
      </div>
      <div style="float:left;" data-role="fieldcontain">
        <table id="doctor-table-speciality-values" data-theme="a">
        </table>
      </div>
    </div>
    <div style="float:left;">
      <input type="hidden" name="doctor-key" id="doctor-key" />
      <input type="hidden" name="doctor-image-key" id="doctor-image-key" />
      <input type="hidden" name="doctor-clear-title-values-holder" id="doctor-clear-title-values-holder" />
      <input type="hidden" name="doctor-clear-speciality-values-holder" id="doctor-clear-speciality-values-holder" />

      <input id="doctor-form-submit" type="submit" data-theme="b" />
    </div>
  </div>
</div>

<div data-role="popup" id="doctors-show-textarea-form" data-theme="a" class="ui-corner-all" style="padding:10px 20px;height: 600px;width:600px;position:absolute;top: 50%;left: 50%;margin-left:-300px;margin-top:-300px;">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;overflow-y:scroll;height: 580px;">
    <h3>
      <span id="doctors-textarea-form-title"></span>
    </h3>
    <div style="float:left;">
      <table id="doctors-table-textarea" data-theme="a" >
      </table>
    </div>
  </div>
</div>

<div data-role="popup" id="doctor-image-form" data-theme="a" data-history="false" class="ui-corner-all" style="padding:10px 20px;height: 350px;width:250px;position:absolute;top: 50%;left: 50%;margin-left:-300px;margin-top:-300px;">
  <a href="#" data-rel="back" id="close-doctor-image-form" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
 <iframe src="" width="100%" height="100%" frameBorder="0" seamless></iframe>
</div>

<div data-role="popup" id="doctor-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="doctor-confirm-popup-heading">Delete Doctor?</span></h3><br />
    <span id="doctor-confirm-popup-body">Are you sure you want to delete this Doctor?</span><br />
    <input type="hidden" name="doctor-key-delete" id="doctor-key-delete" />
    <input type="hidden" name="doctor-Name-delete" id="doctor-Name-delete" />
    <button type="submit" data-theme="b" id="doctor-confirm-popup-delete">Delete Doctor</button>
  </div>
</div>
