<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.billingGroups.showNew();">
        Create a Billing Group
      </a>
    </div>
    <div style="width:13%;float:left;margin: 0.5em;">
      <a href="/service/billinggroup/export"
         data-role="button"
         data-ajax="false">
        Export data 
      </a>
    </div>
  </div>
  <div style="clear:both;"></div>
  <div id="billinggroups-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="billinggroups-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;overflow-y:scroll;height: 600px;">
    <h3>
      <span id="billinggroups-form-title">Create a new Billing Group</span>
    </h3>
    <div id="billinggroups-form-errors" class="form-errors"></div>
    
    <label for="billinggroups-groupName">Group name</label>
    <input type="text" size="40" name="billinggroups-groupName" id="billinggroups-groupName" value="" placeholder="Group name" data-theme="a" />

    <label for="billinggroups-groupAddress1">Group address 1</label>
    <input type="text" size="40" name="billinggroups-groupAddress1" id="billinggroups-groupAddress1" value="" placeholder="Group address 1" data-theme="a" />
    
    <label for="billinggroups-groupAddress2">Group address 2</label>
    <input type="text" size="40" name="billinggroups-groupAddress2" id="billinggroups-groupAddress2" value="" placeholder="Group address 2" data-theme="a" />
    
    <label for="billinggroups-groupCity">Group city</label>
    <input type="text" size="40" name="billinggroups-groupCity" id="billinggroups-groupCity" value="" placeholder="Group city" data-theme="a" />
    
    <label for="billinggroups-groupState">Group state</label>
    <input type="text" size="40" name="billinggroups-groupState" id="billinggroups-groupState" value="" placeholder="Group state" data-theme="a" />
    
    <label for="billinggroups-groupPostalCode">Group postal code</label>
    <input type="text" size="40" name="billinggroups-groupPostalCode" id="billinggroups-groupPostalCode" value="" placeholder="Group postal code" data-theme="a" />
    
    <div class="action-bar" style="margin-top: 0.5em; width:90%;">
      <div style="width:55%;float:left;margin: 0.5em;">
        <a href="#" data-role="button"
          onclick="uptempo.billingGroups.addTextFieldAndIncreaseForOneValueCounter('', '#billinggroups-table-phone-values', '');">
        Add phone field
        </a>
      </div>
      <div style="float:left;">
        <table id="billinggroups-table-phone-values" data-theme="a" >
        </table>
      </div>
    </div>
    <div class="action-bar" style="margin-top: 0.5em; width:90%;">
      <div style="width:55%;float:left;margin: 0.5em;">
        <a href="#" data-role="button"
          onclick="uptempo.billingGroups.addTextFieldAndIncreaseForOneValueCounter('', '#billinggroups-table-fax-values', '');">
        Add fax field
        </a>
      </div>
      <div style="float:left;" data-role="fieldcontain">
        <table id="billinggroups-table-fax-values" data-theme="a" >
        </table>
      </div>
    </div>
    <div data-role="fieldcontain">
      <label for="billinggroups-groupEmail">Group email</label>
      <input type="text" size="40" name="billinggroups-groupEmail" id="billinggroups-groupEmail" value="" placeholder="Group email" data-theme="a" />
    </div>
    <label for="billinggroups-groupNotes">Group notes</label>
    <textarea rows="10" cols="80" id="billinggroups-groupNotes" placeholder="Group notes" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"></textarea>
    
    <label for="billinggroups-groupHours">Group hours</label>
    <textarea rows="10" cols="80" id="billinggroups-groupHours" placeholder="Group hours" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"></textarea>

    <div style="float:left;">
      <input type="hidden" name="billinggroups-key" id="billinggroups-key" />
      <input type="hidden" name="billinggroups-clear-phone-values-holder" id="billinggroups-clear-phone-values-holder" />
      <input type="hidden" name="billinggroups-clear-fax-values-holder" id="billinggroups-clear-fax-values-holder" />

      <input id="billinggroups-form-submit" type="submit" data-theme="b" />
    </div>
  </div>
</div>

<div data-role="popup" id="billinggroups-show-textarea-form" data-theme="a" class="ui-corner-all" style="padding:10px 20px;height: 600px;width:600px;position:absolute;top: 50%;left: 50%;margin-left:-300px;margin-top:-300px;">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;overflow-y:scroll;height: 580px;">
    <h3>
      <span id="billinggroups-textarea-form-title"></span>
    </h3>
    <div style="float:left;">
      <table id="billinggroups-table-textarea" data-theme="a" >
      </table>
    </div>
  </div>
</div>

<div data-role="popup" id="billinggroups-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="billinggroups-confirm-popup-heading">Delete Billing Group?</span></h3><br />
    <span id="billinggroups-confirm-popup-body">Are you sure you want to delete this Billing Group?</span><br />
    <input type="hidden" name="billinggroups-key-delete" id="billinggroups-key-delete" />
    <input type="hidden" name="billinggroups-groupName-delete" id="billinggroups-groupName-delete" />
    <button type="submit" data-theme="b" id="billinggroups-confirm-popup-delete">Delete Billing Group</button>
  </div>
</div>
