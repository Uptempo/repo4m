<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>
  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.billingOffices.showNew();">
        Create a Billing Office
      </a>
    </div>
    <div style="width:13%;float:left;margin: 0.5em;">
      <a href="/service/billingoffice/export?authKey=<%=request.getAttribute("uptempo-authkey") %>"
         data-role="button"
         data-ajax="false">
        Export data 
      </a>
    </div>
  </div>
  <div style="clear:both;"></div>
  <div id="billingoffices-table" width="100%" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="billingoffices-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;overflow-y:scroll;height: 600px">
    <h3>
      <span id="billingoffices-form-title">Create a new Billing Office</span>
    </h3>
    <div id="billingoffices-form-errors" class="form-errors"></div>
    
    <label for="billingoffices-officeGroup">Office group</label>
    <select id="billingoffices-officeGroup" name="billingoffices-officeGroup">
    </select>
    <label for="billingoffices-officeName" class="form-required">Office name</label>
    <input type="text" size="40" name="billingoffices-officeName" id="billingoffices-officeName" value="" placeholder="Office name" data-theme="a" />
    
    <label for="billingoffices-officeEmail" class="form-required">Office email</label>
    <input type="text" size="40" name="billingoffices-officeEmail" id="billingoffices-officeEmail" value="" placeholder="Office email" data-theme="a" />

    <label for="billingoffices-officeAddress1" class="form-required">Office address 1</label>
    <input type="text" size="40" name="billingoffices-officeAddress1" id="billingoffices-officeAddress1" value="" placeholder="Office address 1" data-theme="a" />
    
    <label for="billingoffices-officeAddress2">Office address 2</label>
    <input type="text" size="40" name="billingoffices-officeAddress2" id="billingoffices-officeAddress2" value="" placeholder="Office address 2" data-theme="a" />
    
    <label for="billingoffices-officeCity" class="form-required">Office city</label>
    <input type="text" size="40" name="billingoffices-officeCity" id="billingoffices-officeCity" value="" placeholder="Office city" data-theme="a" />
    
    <label for="billingoffices-officeState" class="form-required">Office state</label>
    <input type="text" size="40" name="billingoffices-officeState" id="billingoffices-officeState" value="" placeholder="Office state" data-theme="a" />
    
    <label for="billingoffices-officePostalCode" class="form-required">Office postal code</label>
    <input type="text" size="40" name="billingoffices-officePostalCode" id="billingoffices-officePostalCode" value="" placeholder="Office postal code" data-theme="a" />  
    
    <label for="billingoffices-officeTimeZone" class="form-required">Office time zone</label>
    <select name="billingoffices-officeTimeZone" id="billingoffices-officeTimeZone">
      <option value="-5">U.S. Eastern Standard Time (UTC-5, DST-4)</option>
      <option value="-6">U.S. Central Standard Time (UTC-6, DST-5)</option>
      <option value="-7">U.S. Mountain Standard Time (UTC-7, DST-6)</option>
      <option value="-8">U.S. Pacific Standard Time (UTC-8, DST-7)</option>
    </select>

    <div class="action-bar" style="margin-top: 0.5em; width:90%;">
      <div style="width:55%;float:left;margin: 0.5em;">
        <a href="#" data-role="button"
          onclick="uptempo.billingOffices.addTextFieldAndIncreaseForOneValueCounter('', '#billingoffices-table-phone-values', '');">
        Add phone field
        </a>
      </div>
      <div style="float:left;">
        <table id="billingoffices-table-phone-values" data-theme="a" >
        </table>
      </div>
    </div>
    <div class="action-bar" style="margin-top: 0.5em; width:90%;">
      <div style="width:55%;float:left;margin: 0.5em;">
        <a href="#" data-role="button"
          onclick="uptempo.billingOffices.addTextFieldAndIncreaseForOneValueCounter('', '#billingoffices-table-fax-values', '');">
        Add fax field
        </a>
      </div>
      <div style="float:left;" data-role="fieldcontain">
        <table id="billingoffices-table-fax-values" data-theme="a">
        </table>
      </div>
    </div>
    <div data-role="fieldcontain">
      <label for="billingoffices-officeNotes">Office notes</label>
      <textarea rows="10" cols="80" id="billingoffices-officeNotes" placeholder="Office notes" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"></textarea>

      <label for="billingoffices-officeHours">Office hours</label>
      <textarea rows="10" cols="80" id="billingoffices-officeHours" placeholder="Office hours" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"></textarea>
    </div>
    <label for="billingoffices-officeLogoURL" class="form-required">Office logo URL</label>
    <input type="text" size="50" name="billingoffices-officeLogoURL" id="billingoffices-officeLogoURL" value="" placeholder="Office logo URL" data-theme="a" />
    <label for="billingoffices-officeSiteURL" class="form-required">Office site URL</label>
    <input type="text" size="50" name="billingoffices-officeSiteURL" id="billingoffices-officeSiteURL" value="" placeholder="Office site URL" data-theme="a" />
    <label for="billingoffices-officeFBURL" class="form-required">Office FB page URL</label>
    <input type="text" size="50" name="billingoffices-officeFBURL" id="billingoffices-officeFBURL" value="" placeholder="Office FB page URL" data-theme="a" />
    <label for="billingoffices-officeAnalyticsUA" class="form-required">Office Google Analytics UA</label>
    <input type="text" size="50" name="billingoffices-officeAnalyticsUA" id="billingoffices-officeAnalyticsUA" value="" placeholder="Office Google Analytics UA" data-theme="a" />
    <label for="billingoffices-officeEmailTemplate">Office e-mail template</label>
    <textarea rows="10" cols="80" id="billingoffices-officeEmailTemplate" placeholder="Office e-mail template" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"></textarea>
    <label for="billingoffices-officeUserEmailTemplate">Office user e-mail template</label>
    <textarea rows="10" cols="80" id="billingoffices-officeUserEmailTemplate" placeholder="Office user e-mail template" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"></textarea>
    <div style="float:left;">
      <input type="hidden" name="billingoffices-key" id="billingoffices-key" />
      <input type="hidden" name="billingoffices-clear-phone-values-holder" id="billingoffices-clear-phone-values-holder" />
      <input type="hidden" name="billingoffices-clear-fax-values-holder" id="billingoffices-clear-fax-values-holder" />

      <input id="billingoffices-form-submit" type="submit" data-theme="b" />
    </div>
  </div>
</div>

<div data-role="popup" id="billingoffices-show-textarea-form" data-theme="a" class="ui-corner-all admin-detail-popup" style="">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;overflow-y:scroll;height: auto;">
    <h3>
      <span id="billingoffices-textarea-form-title"></span>
    </h3>
    <div style="float:left;">
      <table id="billingoffices-table-textarea" data-theme="a">
      </table>
    </div>
  </div>
</div>

<div data-role="popup" id="billingoffices-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="billingoffices-confirm-popup-heading">Delete Billing Office?</span></h3><br />
    <span id="billingoffices-confirm-popup-body">Are you sure you want to delete this Billing Office?</span><br />
    <input type="hidden" name="billingoffices-key-delete" id="billingoffices-key-delete" />
    <input type="hidden" name="billingoffices-officeName-delete" id="billingoffices-officeName-delete" />
    <button type="submit" data-theme="b" id="billingoffices-confirm-popup-delete">Delete Billing Office</button>
  </div>
</div>

<div data-role="popup" id="office-banner-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div class="admin-popup-form">
    <h3>
      <span id="images-form-title">
        Upload a banner for office <span id="office-banner-upload-officename"></span>
      </span>
    </h3>
    <div id="banner-form-errors" class="form-errors"></div>
    
    <form id="banner-upload-form" action="">
      <input type="hidden" id="office-banner-key" name="entityKey" />
      <input type="hidden" id="office-banner-category" name="category" value="Banners"/>
      <label id="office-banner-file-label" for="office-banner-file">
        Choose image for upload.  It should be 400px x 200px or an aspect ratio of 2:1.
      </label><br>
      <input type="file" id="office-banner-file" name="attachmentFile" data-theme="b" />      
      <span id="office-banner-file-name"></span>
    </form>
      
    <input id="office-banner-form-submit" type="submit" value="Submit Banner" data-theme="b" />
  </div>
</div>
