<div data-role="content" style="min-height:50%">
  <div class="status-bar"></div>

  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <div style="width:30%;float:left;margin: 0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.imageCategories.showNew();">
        Create an image category
      </a>
    </div>   
  </div>
  <div class="action-bar" style="margin-top: 0.5em; width:90%;">
    <input style="width:20%;float:left;margin-top: 1.3em;" type="text" size="20" name="imagecategories-search-name" id="imagecategories-search-name" value="" placeholder="Category name" data-theme="a" />
    <input style="width:20%;float:left;margin-top: 1.3em;" type="text" size="20" name="imagecategories-search-description" id="imagecategories-search-description" value="" placeholder="Category description" data-theme="a" />
    <div style="width:15%;float:left;margin:0.5em;">
      <a href="#"
         data-role="button"
         onclick="uptempo.imageCategories.search();">
        Search
      </a>
    </div>
  </div>
  <div style="clear:both;"></div>
  <div id="imagecategories-table" style="margin-top:0.5em;border: 1px solid #000;">
  </div>
</div>

<!-- Create or update image category popup -->
<div data-role="popup" id="imagecategories-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div class="admin-popup-form">
    <h3>
      <span id="imagecategories-form-title">Create a new image category</span>
    </h3>
    <div id="imagecategories-form-errors" class="form-errors"></div>
    
    <label for="imagecategories-name">Category name</label>
    <input type="text" size="40" name="imagecategories-name" id="imagecategories-name" value="" placeholder="Category name" data-theme="a" />
    
    <label for="imagecategories-description">Category description</label>
    <input type="text" size="40" name="imagecategories-description" id="imagecategories-description" value="" placeholder="Category description" data-theme="a" />

    <label for="imagecategories-app">Application</label>
    <select id="imagecategories-app" name="imagecategories-app" placeholder="App title">
    </select>

    <br/>
    
    <input type="hidden" name="imagecategories-key" id="imagecategories-key" />
    <input id="imagecategories-form-submit" type="submit" data-theme="b" />    
  </div>
</div>

<!-- Delete image category popup -->
<div data-role="popup" id="imagecategories-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="imagecategories-confirm-popup-heading">Delete image category?</span></h3><br />
    <span id="imagecategories-confirm-popup-body">Are you sure you want to delete this image category?</span><br />
    <input type="hidden" name="imagecategories-key-delete" id="imagecategories-key-delete" />   
    <input type="hidden" name="imagecategories-name-delete" id="imagecategories-name-delete" value=""/>
    <button type="submit" data-theme="b" id="imagecategories-confirm-popup-delete">Delete image category</button>
  </div>
</div>

<!-- Show image category key popup -->
<div data-role="popup" id="imagecategories-key-display-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="imagecategories-key-confirm-popup-heading">Image category key</span></h3><br />
    Image category key: <span id="imagecategories-key-display"></span><br />
  </div>
</div>