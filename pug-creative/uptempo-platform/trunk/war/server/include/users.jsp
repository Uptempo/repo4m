<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.user.showNew();">
        Create a user
      </a>
    </div>
    <div style="width:13%;float:left;margin: 0.5em;">
      <a href="/service/user/export"
         data-role="button"
         data-ajax="false">
        Export data 
      </a>
    </div> 
  </div>
  <div style="clear:both;"></div>
  <div id="user-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="user-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3>
      <span id="user-form-title">Create a new user</span>
    </h3>
    <div id="user-form-errors" class="form-errors"></div>
    <input type="text" size="40" name="user-email" id="user-email" value="" placeholder="Username/E-mail" data-theme="a" />

    <select id="user-title" name="user-title" placeholder="Title">
      <option value="DEFAULT">Select Salutation</option>
      <option value="Dr.">Dr.</option>
      <option value="Mr.">Mr.</option>
      <option value="Miss">Miss</option>
      <option value="Mrs.">Mrs.</option>
      <option value="Ms.">Ms.</option>
    </select>

    <input type="text" size="20" name="user-fname" id="user-fname" value="" placeholder="First Name" data-theme="a" />

    <input type="text" size="20" name="user-lname" id="user-lname" value="" placeholder="Last Name" data-theme="a" />

    <input type="text" size="40" name="user-address1" id="user-address1" value="" placeholder="Address 1" data-theme="a" />
    
    <input type="text" size="40" name="user-address2" id="user-address2" value="" placeholder="Address 2" data-theme="a" />

    <input type="text" size="20" name="user-city" id="user-city" value="" placeholder="City" data-theme="a" />
    
    <select id="user-state" name="user-state" placeholder="State">
      <option value="AL">Alabama</option>
      <option value="AK">Alaska</option>
      <option value="AZ">Arizona</option>
      <option value="AR">Arkansas</option>
      <option value="CA">California</option>
      <option value="CO">Colorado</option>
      <option value="CT">Connecticut</option>
      <option value="DE">Delaware</option>
      <option value="DC">District of Columbia</option>
      <option value="FL">Florida</option>
      <option value="GA">Georgia</option>
      <option value="HI">Hawaii</option>
      <option value="ID">Idaho</option>
      <option value="IL">Illinois</option>
      <option value="IN">Indiana</option>
      <option value="IA">Iowa</option>
      <option value="KS">Kansas</option>
      <option value="KY">Kentucky</option>
      <option value="LA">Louisiana</option>
      <option value="ME">Maine</option>
      <option value="MD">Maryland</option>
      <option value="MA">Massachusetts</option>
      <option value="MI">Michigan</option>
      <option value="MN">Minnesota</option>
      <option value="MS">Mississippi</option>
      <option value="MO">Missouri</option>
      <option value="MT">Montana</option>
      <option value="NE">Nebraska</option>
      <option value="NV">Nevada</option>
      <option value="NH">New Hampshire</option>
      <option value="NJ">New Jersey</option>
      <option value="NM">New Mexico</option>
      <option value="NY">New York</option>
      <option value="NC">North Carolina</option>
      <option value="ND">North Dakota</option>
      <option value="OH">Ohio</option>
      <option value="OK">Oklahoma</option>
      <option value="OR">Oregon</option>
      <option value="PA">Pennsylvania</option>
      <option value="RI">Rhode Island</option>
      <option value="SC">South Carolina</option>
      <option value="SD">South Dakota</option>
      <option value="TN">Tennessee</option>
      <option value="TX">Texas</option>
      <option value="UT">Utah</option>
      <option value="VT">Vermont</option>
      <option value="VA">Virginia</option>
      <option value="WA">Washington</option>
      <option value="WV">West Virginia</option>
      <option value="WI">Wisconsin</option>
      <option value="WY">Wyoming</option>
    </select>

    <input type="text" size="20" name="user-cell" id="user-cell" value="" placeholder="Cell Phone" data-theme="a" />

    <input type="password" name="user-pwd" id="user-pwd" value="" placeholder="password" data-theme="a" />

    <input type="hidden" name="user-key" id="user-key" />

    <input id="user-form-submit" type="submit" data-theme="b" />
  </div>
</div>

<div data-role="popup" id="user-pwd-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>  
  <div style="padding:10px 20px;">
    <h3>Change User's Password</h3><br />
    <div id="user-pwd-form-errors" class="form-errors"></div>
    <span id="user-pwd-email-display"></span><br /><br />
    <input type="password" size="25" name="user-pwd-change" id="user-pwd-change" value="" placeholder="Enter New Password" data-theme="a" />
    <input type="hidden" name="user-pwd-email" id="user-pwd-email" />
    <br />
    Pressing the submit button will change the user's<br />
    password and e-mail the user with the new password.<br />
    You will not be given a chance to reverse this action.
    <button type="submit" data-theme="b" onclick="uptempo.user.changePwd();">Change user's password</button>
  </div>
</div>

<!-- Delete User Popup -->
<div data-role="popup" id="user-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="user-confirm-popup-heading">Delete User?</span></h3><br />
    <span id="user-confirm-popup-body">Are you sure you want to delete this user?</span><br />
    <input type="hidden" name="user-email-delete" id="user-email-delete" />
    <button type="submit" data-theme="b" id="user-confirm-popup-delete">Delete User</button>
  </div>
</div>
