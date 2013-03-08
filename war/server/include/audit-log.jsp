<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="msAdmin.auditLog.showNew();">
        Create an audit Event
      </a>
    </div>
  </div>
  <div style="clear:both;"></div>
  <div id="auditLog-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="auditLog-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3>
      <span id="auditLog-form-title">Create a new Audit Event</span>
    </h3>
    <div id="auditLog-form-errors" class="form-errors"></div>
    <input type="text" size="40" name="audapp-code" id="apps-code" value="" placeholder="App Code" data-theme="a" />
    <input type="text" size="40" name="audevent-code" id="event-code" value="" placeholder="Event Code" data-theme="a" />    
    <input type="text" size="60" maxlength="499" name="event-description" id="event-description" value="" placeholder="Event Description" data-theme="a" />
    <input id="auditLog-form-submit" type="submit" data-theme="b" />
  </div>
</div>

