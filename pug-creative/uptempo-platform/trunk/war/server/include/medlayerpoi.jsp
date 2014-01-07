<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.medlayerpoi.showNew();">
        Create a MedLayer POI
      </a>
    </div>
    
  </div>
  <div style="clear:both;"></div>
  <div id="medlayerpoi-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="medlayerpoi-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3>
      <span id="medlayerpoi-form-title">Create a new medlayer poi</span>
    </h3>
    <div id="medlayerpoi-form-errors" class="form-errors"></div>
    <label for="medlayer-poi-dataset">Dataset</label>
    <select id="medlayer-poi-dataset" name="medlayer-poi-dataset">
    </select>
    <label for="medlayerpoi-title">Title</label>
    <input type="text" size="40" name="medlayerpoi-title" id="medlayerpoi-title" value="" placeholder="Title" data-theme="a" />
    <label for="medlayerpoi-description">Description</label>
    <input type="text" size="40" name="medlayerpoi-description" id="medlayerpoi-description" value="" placeholder="description" data-theme="a" />
    <label for="medlayerpoi-address">Address</label>
    <input type="text" size="40" name="medlayerpoi-address" id="medlayerpoi-address" value="" placeholder="Use this field to obtain Lat-Lon" data-theme="a" />
    <input type="button" onclick="uptempo.medlayerpoi.getLocationCoord('medlayerpoi-address');" value="Get Coordinates" data-mini="true"/>
    <label for="medlayerpoi-lat">Latitude</label>
    <input type="text" size="40" name="medlayerpoi-lat" id="medlayerpoi-lat" value="" placeholder="latitude" data-theme="a" />
    <label for="medlayerpoi-lon">Longitude</label>
    <input type="text" size="40" name="medlayerpoi-lon" id="medlayerpoi-lon" value="" placeholder="longitude" data-theme="a" />
    <label for="medlayerpoi-alt">Altitude</label>
    <input type="text" size="40" name="medlayerpoi-alt" id="medlayerpoi-alt" value="" placeholder="altitude" data-theme="a" />
    <label for="medlayerpoi-normalimg">Normal IMG</label>
    <input type="text" size="40" name="medlayerpoi-normalimg" id="medlayerpoi-normalimg" value="" placeholder="normalimg path" data-theme="a" />
    <label for="medlayerpoi-directionimg">Direction IMG</label>
    <input type="text" size="40" name="medlayerpoi-directionimg" id="medlayerpoi-directionimg" value="" placeholder="directionimg path" data-theme="a" />
    <label for="medlayerpoi-selectedimg">Selected IMG</label>
    <input type="text" size="40" name="medlayerpoi-selectedimg" id="medlayerpoi-selectedimg" value="" placeholder="selectedimg path" data-theme="a" />
    <label for="medlayerpoi-detailhtml">Detailed HTML</label>
    <input type="text" size="40" name="medlayerpoi-detailhtml" id="medlayerpoi-detailhtml" value="" placeholder="detailhtml" data-theme="a" />
    <input type="hidden" name="medlayerpoi-key" id="medlayerpoi-key" />
    <input id="medlayerpoi-form-submit" type="submit" data-theme="b" />
  </div>
</div>

<!-- Delete App Popup -->
<div data-role="popup" id="medlayerpoi-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="medlayerpoi-confirm-popup-heading">Delete poi?</span></h3><br />
    <div id="medlayerpoi-del-form-errors" class="form-errors"></div>
    <span id="medlayerpoi-confirm-popup-body">Are you sure you want to delete this poi?</span><br />
    <input type="hidden" name="medlayerpoi-key-delete" id="medlayerpoi-key-delete" />
    <input type="hidden" name="medlayerpoi-code-delete" id="medlayerpoi-code-delete" />
    <input type="hidden" name="medlayerpoi-name-delete" id="medlayerpoi-name-delete" />
    <button type="submit" data-theme="b" id="medlayerpoi-confirm-popup-delete">Delete poi</button>
  </div>
</div>

<!-- Show Key Popup -->
<div data-role="popup" id="medlayerpoi-key-display-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="medlayerpoi-confirm-popup-heading">poi Access Key</span></h3><br />
    <div id="medlayerpoi-del-form-errors" class="form-errors"></div>
    poi Key: <span id="medlayerpoi-key-display"></span><br />
  </div>
</div>

<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBMxWwCJrz_47cncPDPlDV0aSR8422JBvg&sensor=false"></script>
