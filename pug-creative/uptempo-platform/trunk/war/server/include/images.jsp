<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:25%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.images.showNew();">
        Create an Image
      </a>
    </div>   
  </div>
  <div style="clear:both;"></div>
  <div id="images-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<div data-role="popup" id="images-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div class="admin-popup-form">
    <h3>
      <span id="images-form-title">Create a new Image</span>
    </h3>
    <div id="images-form-errors" class="form-errors"></div>
    
    <label for="images-name">Image name</label>
    <input type="text" size="40" name="images-name" id="images-name" value="" placeholder="Image name" data-theme="a" />
    
    <label for="images-description">Image description</label>
    <input type="text" size="40" name="images-description" id="images-description" value="" placeholder="Image description" data-theme="a" />
    
    <input type="hidden" name="images-key" id="images-key" />
    <input id="images-form-submit" type="submit" data-theme="b" />
  </div>
</div>

<div data-role="popup" id="images-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="images-confirm-popup-heading">Delete image?</span></h3><br />
    <span id="images-confirm-popup-body">Are you sure you want to delete this image?</span><br />
    <input type="hidden" name="images-key-delete" id="images-key-delete" />   
    <button type="submit" data-theme="b" id="images-confirm-popup-delete">Delete image</button>
  </div>
</div>