/* This file contains all admin images definition actions */
uptempo.images = {
  selectedCategory: "",
  imageDataArray: [],
  thumbnailSize: "s150",
  startPage: 0,
  limitPerPage: 10,
  numToShow: 0,
  numOfPages: 0
};

//*** Field mapping for validation and naming.
uptempo.images.validFields = [
  {name: "Image caption", inputId: "#images-caption", formVal: "caption", required: true},
  {name: "Category name", inputId: "#images-category", formVal: "categoryId", required: true},
  {name: "Image file", inputId: "#images-file", formVal: "", required: true}
];

uptempo.images.updateValidFields = [
  {name: "Image caption", inputId: "#images-caption", formVal: "caption", required: true},
  {name: "Category name", inputId: "#images-category", formVal: "categoryId", required: true}
];

uptempo.images.showNew = function () {
  uptempo.images.clearImageForm();

  uptempo.images.fillDropdownWithCategories("images-category");

  //*** Setup the form.
  $("#images-form-title").html("New Image");
  $("#images-form-submit").changeButtonText("Create this image");
  $("#images-form-submit").off("click");
  $("#images-form-submit").on("click", uptempo.images.submitNew);
  $("#images-form-errors").html("");
  //*** Show the form.
  $("#images-form").popup("open");
}

uptempo.images.submitNew = function () {
  //*** Set the key for submission.
  var key = "image";

  //*** On success, close the submission window and reload the table.
  var imagesSuccessFn = function(imageKey) {
    $(".status-bar").css("display", "none");
    uptempo.images.uploadImage(imageKey);
  }; 
  
  uptempo.ajax.submitNew("image",
                         "/service/imagerender",
                         uptempo.images.validFields,
                         "images-caption",
                         key,
                         imagesSuccessFn);
}

uptempo.images.uploadImage = function(imageKey) {
  $("#images-key").val(imageKey);

  var uploadUrl = $("#images-upload-form").attr("action");
  var baseUrlIndex = uploadUrl.indexOf("/_ah"); 
  if (baseUrlIndex != -1) {
    uploadUrl = uploadUrl.substring(baseUrlIndex);
  }
  
  var fd = new FormData($("#images-upload-form")[0]);    

  $.ajax({
    url: uploadUrl,
    data: fd,
    processData: false,
    contentType: false,
    type: 'POST',
    success: function(data) {
      $("#images-form").popup("close");  
      
      if (data.status == "FAILURE") { 
        $(".status-bar").html("Failed to create image " + $("#images-caption") + ". Reason: " + data.message); 
        //TODO - also delete created image if upload fails   
      } 
      
      $(".status-bar").css("display", "block");

      uptempo.images.clearImageForm();
      uptempo.images.getImageData();
    }
  });
}

//*** Show the update image popup.
uptempo.images.showUpdate = function (valueKey) {
  uptempo.images.clearImageForm();

  // it is not possible to upload another image
  // when updating image so hide input file element 
  $("#images-file-label").hide();
  $("#images-file").hide();

  //*** Setup the form. 
  $("#images-form-title").html("Update Image");
  $("#images-form-submit").changeButtonText("Update this image"); 
  $("#images-form-errors").html("");
  $("#images-key").val(valueKey);

  uptempo.images.fillDropdownWithCategories("images-category", "key", function() {
    //*** Get the data for this image.

    //*** Submit the XHR request.
    $.ajax({
      type: 'GET',
      url: '/service/imagerender/' + valueKey,
      success: function(response) {
        //*** If the response was sucessful, showw data for update
        if (response.status == "SUCCESS") {              
          $("#images-caption").val(response.data.caption);                  
          $("#images-category option[value="+response.data.ancestor+"]").attr('selected', 'selected');
          $("#images-category").selectmenu("refresh", true);
        } else {
          alert(response.message);
        }

        $("#images-form-submit").off("click");
        $("#images-form-submit").on("click", uptempo.images.submitUpdate); 
        //*** Show the form.
        $("#images-form").popup("open");
      }
    }); 
  });
 

}

uptempo.images.submitUpdate = function() {
  //*** Set the key for submission.
  var imageKey = $("#images-key").val();

  //*** On success, close the submission window and reload the table.
  var imagesUpdateSuccessFn = function() {
    $("#images-form").popup("close");

    uptempo.images.clearImageForm();

    // restore hidden input file element 
    $("#images-file-label").show();
    $("#images-file").show();

    uptempo.images.getImageData();    
  }; 
  
  uptempo.ajax.submitUpdate("image",
                         "/service/imagerender/" + imageKey,
                         uptempo.images.updateValidFields,
                         "images-caption",
                         imagesUpdateSuccessFn); 
}

/**
 * Gets a list of category objects and fills a dropdown with those objects.
 * If present, valueIndex determines value for option element
 * Callback function is called if defined
 */
uptempo.images.fillDropdownWithCategories = function(dropdownId, valueKey, callbackFn) {
  var valueKey = valueKey || "key"; 
  $.ajax({
    type: 'GET',
    url: '/service/imagecategory',
    data: "",
    success: function(response) {
      var categoryValueId = $("#" + dropdownId);
      categoryValueId.empty();
      //*** If the response was successful, show apps, otherwise show appropriate message.
      if (response.status == "SUCCESS") {
        var categoryData = response.data.values;
        categoryValueId.append("<option value=''>--Select a Category--</option>");
        $.each(categoryData, function(index, category) {
          categoryValueId.append("<option value='" + category[valueKey] + "'>" + category["name"] + "</option>");
        })
        categoryValueId.val("Select a Category");
        categoryValueId.selectmenu("refresh");
        if (callbackFn) {
          callbackFn();
        }
      } else {
        categoryValues = "<select>" +
                    "<option value='DEFAULT'> Could not get cetegories, defaulting to DEFAULT</option>" +
                    "</select>";
        categoryValueId.replaceWith(categoryValues);
      }
    }
  });
}

uptempo.images.createUploadUrl = function() {
  $.ajax({
    type: 'GET',
    url: '/service/imagerender/upload',
    success: function(response) {
      //*** If the response was sucessful, attache upload url to form's actions
        if (response.status == "SUCCESS") {
          $("#images-upload-form").attr("action", response.uploadUrl);            
        } else {
          alert("Failed to create upload URL");
        }
      }
    });  
}

uptempo.images.clearImageForm = function() {
  $("#images-caption").val("");
  $("#images-category").val("");  
  $("#images-file").replaceWith($("#images-file").clone(true));
  // fix to style input file element in Jquery Mobile 1.2.0
  // TODO: remove for JQuery Mobile 1.3.0
  $('#images-file').textinput({theme: 'b'});
  uptempo.images.createUploadUrl();
}

uptempo.images.showPrevPage = function() {
  if (uptempo.images.startPage > 0) {
    uptempo.images.startPage--;   
    uptempo.images.drawGrid();
    uptempo.images.togglePagination();
  }
}

uptempo.images.showNextPage = function() {
  if (uptempo.images.startPage < uptempo.images.numOfPages - 1 ) {
    uptempo.images.startPage++;    
    uptempo.images.drawGrid();
    uptempo.images.togglePagination();
  }
}

uptempo.images.togglePagination = function() {
  $("#images-display-previous").toggleClass("paginate_enabled_previous", !(uptempo.images.startPage == 0));
  $("#images-display-next").toggleClass("paginate_enabled_next", 
                            !(uptempo.images.startPage == uptempo.images.numOfPages - 1));  
}

uptempo.images.displayAsPagingGrid = function() {  
  uptempo.images.startPage = 0;
  uptempo.images.numOfPages = Math.ceil(uptempo.images.imageDataArray.length / uptempo.images.limitPerPage);
 
  if (uptempo.images.imageDataArray.length < uptempo.images.limitPerPage) {
    uptempo.images.numToShow = uptempo.images.imageDataArray.length;   
    $("#images-display-next").removeClass("paginate_enabled_next");    
  } else {
    uptempo.images.numToShow = uptempo.images.limitPerPage;
    $("#images-display-next").addClass("paginate_enabled_next");
  }

  uptempo.images.drawGrid();
}

uptempo.images.drawGrid = function() {
  var startIndex = uptempo.images.startPage * uptempo.images.limitPerPage;
  var endIndex = startIndex + uptempo.images.numToShow;
  if (endIndex > uptempo.images.imageDataArray.length) {
    endIndex = uptempo.images.imageDataArray.length;
  }

  // display number of images and total
  $("#images-number-total").html("Showing " + (startIndex + 1) + " to " + 
                                 endIndex + " of " + uptempo.images.imageDataArray.length); 

  // draw grid based on thumbnail size
  var grid = $("#images-table");
  grid.html("");
  for (var i = startIndex; i < endIndex; i++) {
    var img = uptempo.images.imageDataArray[i];
    if (img.url) {
      var thumbnailUrl = img.url + "=" + uptempo.images.thumbnailSize;
      var imageDiv = "<div class='image-thumbnail-view'>" +
                     "<a href='#' onclick=\"uptempo.images.showUpdate('"+img.key+"')\">" + 
                     "<img src='" + thumbnailUrl + "'/>" +
                     "</a><br/>" + img.caption + "&nbsp;&nbsp;" +
                     "<a href='#' onclick=\"uptempo.images.showDeleteConfirm('"+img.key+"')\">Delete</a>" + 
                     "</div>";
      grid.append(imageDiv);
    }
  }
  grid.append("<div style='clear:both'></div>");  
}

uptempo.images.getImageData = function () {
  var categoryId = "";
  if (uptempo.images.selectedCategory != "") {
    categoryId = "categoryId=" + uptempo.images.selectedCategory;
  }
  uptempo.loader.show("Getting Image data.");
  var imageDataArray = ["No Image data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/imagerender',
    data: "" + categoryId,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        uptempo.images.imageDataArray = response.data.values;
        uptempo.images.displayAsPagingGrid();
      } else {
        $(".status-bar").html("Failed to get image records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    },
    complete: uptempo.loader.hide()
  });

}


uptempo.images.showDeleteConfirm = function(imageKey) {
  var imageCaption = "Could not get image caption";
  
  //*** Get the image caption.
  $.ajax({
    type: 'GET',
    url: '/service/imagerender/' + imageKey,
    success: function(response) {
      if (response.status == "SUCCESS") {  
        imageCaption = response.data.caption;        
      }
      $("#images-caption-delete").val(imageCaption);
      $("#images-confirm-popup-body").html(
        "Are you sure you want to delete image: " + imageCaption + "?");
    }
  });

  //*** Set the title and body.
  $("#images-confirm-popup-heading").html("Delete image?");
  $("#images-confirm-popup-action").html("Delete image");
  $("#images-key-delete").val(imageKey);
  $("#images-confirm-popup-delete").on("click", uptempo.images.deleteImage);

  //*** Show the delete form.
  $("#images-confirm-popup").popup("open");
}

uptempo.images.deleteImage = function() {
  var imageKey = $("#images-key-delete").val();
  var imageCaption = $("#images-caption-delete").val();  

  //*** Define a delete success function.
  var imagesDelSuccessFn = function() {
    $("#images-confirm-popup").popup("close");
    uptempo.images.getImageData();
  };

  uptempo.ajax.submitDelete(imageKey, 
                            "/service/imagerender/", 
                            "image", 
                            imageCaption, 
                            imagesDelSuccessFn);
}

uptempo.images.initImageAdmin = function() {
  uptempo.images.fillDropdownWithCategories("images-display-category", "key", function() {
    $("#images-display-category").on("change", function() {
      if ($(this).val() == "") {
        $("#images-table").html("");
      } else {
        uptempo.images.selectedCategory = $(this).val();
        uptempo.images.getImageData();
      }    
    });
  });  

}


//***When the user goes to this page, show the data table on load.
$("#images").live('pageshow', uptempo.images.initImageAdmin);
$("#images").live('pageshow', uptempo.util.pageTransition);