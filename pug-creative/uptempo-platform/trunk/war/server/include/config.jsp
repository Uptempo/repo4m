<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%">
      <a href="#" id="new-config-value" data-role="button" onclick="msAdmin.config.showNew();">
        Create a config value
      </a>
    </div>
  </div>

  <div id="config-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="config-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div id="config-form-errors" class="form-errors"></div>
  <div class="admin-popup-form">
    <h3><span id="config-form-title">Create a new config value</span></h3>
    <label for="config-app">Application</label>
    <select id="config-app" name="config-app">
      <option value="DEFAULT">Default Application (DEFAULT)</option>
      <option value="AMGEN-PFIZER">Amgen-Pfizer Client App (AMGEN-PFIZER)</option>
      <option value="CLIENT-APP">Client App Global (CLIENT-APP)</option>
      <option value="ADMIN">Admin Application (ADMIN)</option>
    </select>

    <label for="config-name">Config Code</label>
    <input type="text" size="40" name="config-name" id="config-name" value="" data-theme="a" />

    <label for="config-name">Description</label>
    <input type="text" size="40" name="config-description" id="config-description" value="" data-theme="a" />

    <label for="config-value">Value</label>
    <input type="text" size="40" name="config-value" id="config-value" value="" data-theme="a" />

    <label for="config-text">Config Text</label>
    <textarea name="config-text" id="config-text">
    </textarea>
    <input type="hidden" name="config-key" id="config-key" />

    <input id="config-form-submit" type="submit" data-theme="b" />
  </div>
</div>

<!-- Delete Config Value Popup -->
<div data-role="popup" id="config-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div class="admin-popup-form">
    <h3><span id="config-confirm-popup-heading">Delete Config Value?</span></h3><br />
    <span id="config-confirm-popup-body">Are you sure you want to delete this config value?</span><br />
    <input type="hidden" name="config-key-delete" id="config-key-delete" />
    <input type="hidden" name="config-name-delete" id="config-key-delete" />
    <input type="hidden" name="config-app-delete" id="config-key-delete" />
    <button type="submit" data-theme="b" id="config-confirm-popup-delete">Delete Config Value</button>
  </div>
</div>
