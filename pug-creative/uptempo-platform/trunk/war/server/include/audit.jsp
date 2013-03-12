<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.audit.showNew();">
        Create an audit Event Type
      </a>
    </div>
  </div>
  <div style="clear:both;"></div>
  <div id="audit-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="audit-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3>
      <span id="audit-form-title">Create a new Audit Event Type</span>
    </h3>
    <div id="audit-form-errors" class="form-errors"></div>
    
    <label for="apps-code">Application Code</label>
    <select id="apps-code" name="apps-code" placeholder="App Code">
    </select>
    
    <label for="event-code">Event Code</label>
    <input type="text" size="40" name="event-code" id="event-code" value="" placeholder="Event Code" data-theme="a" />
    
    <label for="description">Description</label>              
    <input type="text" size="60" maxlength="499" name="description" id="description" value="" placeholder="Description" data-theme="a" />
    
    <label for="severity">severity</label>
    <select id="severity" name="severity" placeholder="Severity">
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
    </select>
    
    <label for="alert-threshold">Alert Threshold</label>
    <select id="alert-threshold" name="alert-threshold" placeholder="Alert Threshold">
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
    </select>
    
    <label for="alert-type">Alert Type</label>
    <select id="alert-type" name="alert-type" placeholder="Alert Type">
      <option value="email">E-mail</option>
      <option value="text">Text</option>
      <option value="both">E-mail and Text</option>
    </select>
    
    <label for="alert-email">Alert Email</label>
    <input type="text" size="40" name="alert-email" id="alert-email" value="" placeholder="Alert Email" data-theme="a" />
    
    <label for="alert-phone">Alert Phone</label>
    <input type="text" size="40" name="alert-phone" id="alert-phone" value="" placeholder="Alert Phone" data-theme="a" />
    <input type="hidden" name="audit-key" id="audit-key" />
    
    <input id="audit-form-submit" type="submit" data-theme="b" />
  </div>
</div>


<!-- Delete Audit Popup -->
<div data-role="popup" id="audit-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="audit-confirm-popup-heading">Delete Audit Event Type?</span></h3><br />
    <span id="audit-confirm-popup-body">Are you sure you want to delete this audit event Type?</span><br />
    <input type="hidden" name="audit-key-delete" id="audit-key-delete" />
    <input type="hidden" name="app-code-delete" id="app-code-delete" />
    <input type="hidden" name="event-code-delete" id="event-code-delete" />
    <button type="submit" data-theme="b" id="audit-confirm-popup-delete">Delete Audit Event Type</button>
  </div>
</div>
