<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.auditLog.showNew();">
        Create an audit Event
      </a>
    </div>
    <div style="width:13%;float:left;margin: 0.5em;">
      <a href="/service/auditlog/export?authKey=<%=request.getAttribute("uptempo-authkey") %>"
         data-role="button"
         data-ajax="false">
        Export data 
      </a>
    </div>
    <div style="width:13%;float:left;margin: 0.5em;">
      <a href="/service/auditlog/export?authKey=<%=request.getAttribute("uptempo-authkey") %>&extension=csv&delimiter=,"
         data-role="button"
         data-ajax="false">
        Export to CSV 
      </a>
    </div>
    <div style="width:26%;float:left;margin: 0.5em;'">
      <div style="float:left;margin: 1em 0.5em 0 0;">Number of days to show:</div>
      
      <select name="set-log-days" id="set-log-days">
        <option value="7">7 days</option>
        <option value="15" selected>15 days</option>
        <option value="30">30 days</option>
        <option value="60">60 days</option>
        <option value="180">180 days</option>
      </select>
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

