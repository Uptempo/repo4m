<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.medlayerdataset.showNew();">
        Create a MedLayer dataset
      </a>
    </div>
    
  </div>
  <div style="clear:both;"></div>
  <div id="medlayerdataset-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="medlayerdataset-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3>
      <span id="medlayerdataset-form-title">Create a new medlayer dataset</span>
    </h3>
    <div id="medlayerdataset-form-errors" class="form-errors"></div>
    <label for="medlayer-app">Office group</label>
    <select id="medlayer-app-dataset" name="medlayer-app-dataset">
    </select>
    <label for="medlayerdataset-tabName">Tab Name</label>
    <input type="text" size="40" name="medlayerdataset-tabName" id="medlayerdataset-tabName" value="" placeholder="Tab Name" data-theme="a" />
    <label for="medlayerdataset-tabIcon">Tab Icon</label>
    <input type="text" size="40" name="medlayerdataset-tabIcon" id="medlayerdataset-tabIcon" value="" placeholder="Tab Icon URL" data-theme="a" />
    <input type="hidden" name="medlayerdataset-key" id="medlayerdataset-key" />
    <input id="medlayerdataset-form-submit" type="submit" data-theme="b" />
  </div>
</div>

<!-- Delete App Popup -->
<div data-role="popup" id="medlayerdataset-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="medlayerdataset-confirm-popup-heading">Delete Dataset?</span></h3><br />
    <div id="medlayerdataset-del-form-errors" class="form-errors"></div>
    <span id="medlayerdataset-confirm-popup-body">Are you sure you want to delete this dataset?</span><br />
    <input type="hidden" name="medlayerdataset-key-delete" id="medlayerdataset-key-delete" />
    <input type="hidden" name="medlayerdataset-code-delete" id="medlayerdataset-code-delete" />
    <input type="hidden" name="medlayerdataset-name-delete" id="medlayerdataset-name-delete" />
    <button type="submit" data-theme="b" id="medlayerdataset-confirm-popup-delete">Delete dataset</button>
  </div>
</div>

<!-- Show Key Popup -->
<div data-role="popup" id="medlayerdataset-key-display-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="medlayerdataset-confirm-popup-heading">Dataset Access Key</span></h3><br />
    <div id="medlayerdataset-del-form-errors" class="form-errors"></div>
    Dataset Key: <span id="medlayerdataset-key-display"></span><br />
  </div>
</div>
