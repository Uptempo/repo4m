<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.app.showNew();">
        Create an application
      </a>
    </div>
  </div>
  <div style="clear:both;"></div>
  <div id="app-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="app-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3>
      <span id="app-form-title">Create a new application</span>
    </h3>
    <div id="app-form-errors" class="form-errors"></div>
    <label for="app-code">Application Code</label>
    <input type="text" size="40" name="app-code" id="app-code" value="" placeholder="App Code" data-theme="a" />
    <label for="app-name">Application Name</label>
    <input type="text" size="40" name="app-name" id="app-name" value="" placeholder="App Name" data-theme="a" />
    <label for="app-description">Application Description</label>
    <input type="text" size="60" maxlength="499" name="app-description" id="app-description" value="" placeholder="Description" data-theme="a" />
    <label for="app-url">Application URL</label>
    <input type="text" size="40" name="app-url" id="app-url" value="" placeholder="URL" data-theme="a" />
    <input type="hidden" name="app-key" id="app-key" />
    <input id="app-form-submit" type="submit" data-theme="b" />
  </div>
</div>

<!-- Delete App Popup -->
<div data-role="popup" id="app-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="app-confirm-popup-heading">Delete Application?</span></h3><br />
    <div id="del-form-errors" class="form-errors"></div>
    <span id="app-confirm-popup-body">Are you sure you want to delete this application?</span><br />
    <input type="hidden" name="app-key-delete" id="app-key-delete" />
    <input type="hidden" name="app-code-delete" id="app-code-delete" />
    <input type="hidden" name="app-name-delete" id="app-name-delete" />
    <button type="submit" data-theme="b" id="app-confirm-popup-delete">Delete Application</button>
  </div>
</div>

<!-- Change Key Popup -->
<div data-role="popup" id="app-key-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="app-confirm-popup-heading">Change Access Key?</span></h3><br />
    <div id="del-form-errors" class="form-errors"></div>
    <span id="app-key-reset-confirm">Are you sure you want to reset this application access key?</span><br />
    <input type="hidden" name="app-key-reset" id="app-key-reset" />
    <button type="submit" data-theme="b" id="app-confirm-popup-reset">Reset Access Key!</button>
  </div>
</div>

<!-- Show Key Popup -->
<div data-role="popup" id="app-key-display-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="app-confirm-popup-heading">Application Access Key</span></h3><br />
    <div id="del-form-errors" class="form-errors"></div>
    Application Key: <span id="app-key-display"></span><br />
  </div>
</div>
