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
  <div class="action-bar" style="width:50%">
    <label for="images-display-category">Display images in category</label>
    <select id="images-display-category" name="images-display-category" placeholder="Category name">
    </select>
  </div>
  <div id="images-table" style="margin-top:0.5em;border: 1px solid #000; padding: 0 0 5px 5px">
  </div>
  <div id="images-number-total" class="images-info"></div>
  <div class="images-pagination">
    <a id="images-display-previous" href="#" onclick="uptempo.images.showPrevPage();" class="paginate_disabled_previous">Previous</a>
    <a id="images-display-next" href="#" onclick="uptempo.images.showNextPage();" class="paginate_disabled_next">Next</a>
  </div>
</div>

<div data-role="popup" id="images-form" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div class="admin-popup-form">
    <h3>
      <span id="images-form-title">Create a new Image</span>
    </h3>
    <div id="images-form-errors" class="form-errors"></div>
    
    <label for="images-caption">Image caption</label>
    <input type="text" size="40" name="images-caption" id="images-caption" value="" placeholder="Caption" data-theme="a" />
    
    <label for="images-category">Category</label>
    <select id="images-category" name="images-category" placeholder="Category name">
    </select>


    <form id="images-upload-form" action="">
      <input type="hidden" id="images-key" name="imageKey" />
      <label id="images-file-label" for="images-file">Choose image for upload</label>
      <input type="file" id="images-file" name="myFile" data-theme="b" />       
    </form>
      
    <input id="images-form-submit" type="submit" data-theme="b" />
  </div>
</div>

<div data-role="popup" id="images-confirm-popup" data-theme="a" class="ui-corner-all">
  <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Close</a>
  <div style="padding:10px 20px;">
    <h3><span id="images-confirm-popup-heading">Delete image?</span></h3><br />
    <span id="images-confirm-popup-body">Are you sure you want to delete this image?</span><br />
    <input type="hidden" name="images-key-delete" id="images-key-delete" />   
    <input type="hidden" name="images-caption-delete" id="imagecategories-caption-delete" value=""/>
    <button type="submit" data-theme="b" id="images-confirm-popup-delete">Delete image</button>
  </div>
</div>