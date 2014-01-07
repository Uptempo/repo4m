<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.medlayerapp.showNew();">
        Create a MedLayer application
      </a>
    </div>
    
  </div>
  <div style="clear:both;"></div>
  <div id="medlayerapp-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="medlayerapp-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3>
      <span id="medlayerapp-form-title">Create a new medlayer application</span>
    </h3>
    <div id="medlayerapp-form-errors" class="form-errors"></div>
    <label for="medlayerapp-name">Application Name</label>
    <input type="text" size="40" name="medlayerapp-name" id="medlayerapp-name" value="" placeholder="App Name" data-theme="a" />
    <input type="hidden" name="medlayerapp-key" id="medlayerapp-key" />
    <input id="medlayerapp-form-submit" type="submit" data-theme="b" />
  </div>
</div>

<!-- Delete App Popup -->
<div data-role="popup" id="medlayerapp-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="medlayerapp-confirm-popup-heading">Delete Application?</span></h3><br />
    <div id="medlayerapp-del-form-errors" class="form-errors"></div>
    <span id="medlayerapp-confirm-popup-body">Are you sure you want to delete this application?</span><br />
    <input type="hidden" name="medlayerapp-key-delete" id="medlayerapp-key-delete" />
    <input type="hidden" name="medlayerapp-code-delete" id="medlayerapp-code-delete" />
    <input type="hidden" name="medlayerapp-name-delete" id="medlayerapp-name-delete" />
    <button type="submit" data-theme="b" id="medlayerapp-confirm-popup-delete">Delete Application</button>
  </div>
</div>

<!-- Show Key Popup -->
<div data-role="popup" id="medlayerapp-key-display-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="medlayerapp-confirm-popup-heading">Application Access Key</span></h3><br />
    <div id="medlayerapp-del-form-errors" class="form-errors"></div>
    Application Key: <span id="medlayerapp-key-display"></span><br />
  </div>
</div>
