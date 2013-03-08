<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="msAdmin.staticLists.showNew();">
        Create a Static List Value
      </a>
    </div>
  </div>
  <div style="clear:both;"></div>
  <div id="staticlists-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="staticlists-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;overflow-y:scroll;height: 600px">
    <h3>
      <span id="staticlists-form-title">Create a new Static List Value</span>
    </h3>
    <div id="staticlists-form-errors" class="form-errors"></div>
    
    <label for="staticlists-apps-code">Application Code</label>
    <select id="staticlists-apps-code" name="apps-code" placeholder="App Code">
    </select>
    
    <label for="list-code">List Code</label>
    <input type="text" size="40" name="list-code" id="list-code" value="" placeholder="List Code" data-theme="a" />
    
    <label for="list-key">List Key</label>
    <input type="text" size="40" name="list-key" id="list-key" value="" placeholder="List Key" data-theme="a" />
    
    <div class="action-bar" style="margin-top: 0.5em; width:90%;">
      <div style="width:55%;float:left;margin: 0.5em;">
        <a href="#" data-role="button"
          onclick="msAdmin.staticLists.addListValueAndIncreaseForOneValueCounter('', '#table-list-values', '');">
        Add list value field
        </a>
      </div>
      <div style="float:left;">
        <table id="table-list-values" data-theme="a" >
        </table>
      </div>
    </div>
    <div class="action-bar" style="margin-top: 0.5em; width:90%;">
      <div style="width:55%;float:left;margin: 0.5em;">
        <a href="#" data-role="button"
          onclick="msAdmin.staticLists.addListTextAndIncreaseForOneTextCounter('', '#table-list-texts', '');">
        Add list text field
        </a>
      </div>
      <div style="float:left;" data-role="fieldcontain">
        <table id="table-list-texts" data-theme="a" >
        </table>
      </div>
    </div>
    <div style="float:left;">
      <input type="hidden" name="staticlists-key" id="staticlists-key" />
      <input type="hidden" name="clear-values-holder" id="clear-values-holder" />
      <input id="staticlists-form-submit" type="submit" data-theme="b" />
    </div>
  </div>
</div>

<div data-role="popup" id="staticlists-show-textarea-form" data-theme="a" class="ui-corner-all" style="padding:10px 20px;height: 600px;width:600px;position:absolute;top: 50%;left: 50%;margin-left:-300px;margin-top:-300px;">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;overflow-y:scroll;height: 580px;">
    <h3>
      <span id="staticlists-textarea-form-title"></span>
    </h3>
    <div style="float:left;">
      <table id="staticlists-table-textarea" data-theme="a" >
      </table>
    </div>
  </div>
</div>

<div data-role="popup" id="staticlists-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="staticlists-confirm-popup-heading">Delete Static List?</span></h3><br />
    <span id="staticlists-confirm-popup-body">Are you sure you want to delete this Static List?</span><br />
    <input type="hidden" name="staticlists-key-delete" id="staticlists-key-delete" />
    <input type="hidden" name="app-code-delete" id="app-code-delete" />
    <input type="hidden" name="list-code-delete" id="list-code-delete" />
    <button type="submit" data-theme="b" id="staticlists-confirm-popup-delete">Delete Static List</button>
  </div>
</div>

