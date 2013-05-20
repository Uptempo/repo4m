/* This file contains all imagecategories definition actions */
uptempo.imageCategories = {};

uptempo.imageCategories.tableHeadings = [
  {"sTitle": "Category name", "mData": "name" },
  {"sTitle": "Category description", "mData": "description" },
  {"sTitle": "Category key", "mData": null},
  {"sTitle": "Application", "mData": null },
  {"sTitle": "Created By", "mData": "createdBy" },
  {"sTitle": "Modified By", "bVisible": false, "mData": null },
  {"sTitle": "Create Date", "bVisible": false, "mData": null },  
  {"sTitle": "Modify Date", "bVisible": false, "mData": null },
  {"sTitle": "Access Key", "bVisible": false, "mData": null },
  {"sTitle": "Action", "mData" : null},
];

//*** Field mapping for validation and naming.
uptempo.imageCategories.validFields = [
  {name: "Category name", inputId: "#imagecategories-name", formVal: "name", required: true},
  {name: "Category description", inputId: "#imagecategories-description", formVal: "description", required: true},
  {name: "Application", inputId: "#imagecategories-app", formVal: "applicationId", required: true}
];

//*** Formats the imagecategories table.
uptempo.imageCategories.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append an edit and delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"uptempo.imageCategories.showUpdate('" + aData["key"] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.imageCategories.showDeleteConfirm('" + aData["key"] + "', '" + aData["name"] + "');\">del</a>";

  var viewKeyLink = "<a href='#' onclick=\"uptempo.imageCategories.viewKey('" + aData["key"] + "');\">view</a>";
  $("td:eq(2)", nRow).html(viewKeyLink);

  uptempo.imageCategories.getAppNameByKey(aData["ancestor"], $("td:eq(3)", nRow));

  $("td:eq(5)", nRow).html(editLink + delLink);
}

uptempo.imageCategories.getAppNameByKey = function (key, setElement) {
  //*** Get the group data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/app/'+key,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        if (response.message == ""){
          setElement.text("null").html();
        }
        else{
          setElement.text(response.data["appName"]).html();
        }
      } else {
        $(".status-bar").html("Failed to get application records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
}

uptempo.imageCategories.showNew = function () {
  uptempo.imageCategories.clearImageCategoriesForm();
  uptempo.ajax.fillDropdownWithApps("imagecategories-app", 9); 

  //*** Setup the form.
  $("#imagecategories-form-title").html("New image category");
  $("#imagecategories-form-submit").changeButtonText("Create this image category");
  $("#imagecategories-form-submit").off("click");
  $("#imagecategories-form-submit").on("click", uptempo.imageCategories.submitNew);
  $("#imagecategories-form-errors").html("");
  //*** Show the form.
  $("#imagecategories-form").popup("open");
}

uptempo.imageCategories.submitNew = function () {
  //*** Set the key for submission.
  var key = "imageCategory";

  //*** On success, close the submission window and reload the table.
  var imageCategoriesSuccessFn = function() {
    $("#imagecategories-form").popup("close");  
    uptempo.imageCategories.clearImageCategoriesForm();
    uptempo.imageCategories.getImageCategoriesData();
  }; 
  
  uptempo.ajax.submitNew("image category",
                         "/service/imagecategory",
                         uptempo.imageCategories.validFields,
                         "imagecategories-name",
                         key,
                         imageCategoriesSuccessFn);
  
}

//*** Show the update application popup.
uptempo.imageCategories.showUpdate = function (valueKey) {
  uptempo.imageCategories.clearImageCategoriesForm();

  $("#imagecategories-form-title").html("Update image category");
  $("#imagecategories-form-submit").changeButtonText("Update this image category");  
  $("#imagecategories-form-errors").html("");
  $("#imagecategories-key").val(valueKey);

  uptempo.ajax.fillDropdownWithApps("imagecategories-app", 9, function() {
    //*** Get the data for this application.
    //*** Submit the XHR request.
    $.ajax({
      type: 'GET',
      url: '/service/imagecategory/' + valueKey,
      success: function(response) {
        //*** If the response was sucessful, showw data for update
          if (response.status == "SUCCESS") {              
            $("#imagecategories-name").val(response.data.name);
            $("#imagecategories-description").val(response.data.description);
            //$("#imagecategories-app").val(response.data.applicationId);
            $("#imagecategories-app option[value="+response.data.ancestor+"]").attr('selected', 'selected');
            $("#imagecategories-app").selectmenu("refresh", true);
          } else {
            alert(response.message);
          }
        }
    });

    $("#imagecategories-form-submit").off("click");
    $("#imagecategories-form-submit").on("click", uptempo.imageCategories.submitUpdate);
    //*** Show the form.
    $("#imagecategories-form").popup("open");  
  });  
  
}

uptempo.imageCategories.submitUpdate = function() {
  //*** Set the key for submission.
  var imageCategoriesKey = $("#imagecategories-key").val();
  
  //*** On success, close the submission window and reload the table.
  var imageCategoriesUpdsuccessFn = function() {
    $("#imagecategories-form").popup("close");
    uptempo.imageCategories.clearImageCategoriesForm();
    uptempo.imageCategories.getImageCategoriesData();    
  };

  uptempo.ajax.submitUpdate("image category",
                            "/service/imagecategory/" + imageCategoriesKey,
                            uptempo.imageCategories.validFields,
                            "imagecategories-name",
                            imageCategoriesUpdsuccessFn);
}

uptempo.imageCategories.clearImageCategoriesForm = function() {
  $("#imagecategories-app").val("");  
  $("#imagecategories-name").val("");
  $("#imagecategories-description").val("");  
}

uptempo.imageCategories.getImageCategoriesData = function (searchCriteria) {  
  var search = (typeof searchCriteria  == "object") ? "" : searchCriteria;
  uptempo.loader.show("Getting image category data.");
  var appDataArray = ["No image category data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/imagecategory',
    data: "" + search,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get image category records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#imagecategories-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="imagecategories-table-data"></table>' );
      //*** Make this table the active one for row events.
      uptempo.activeTable = $('#imagecategories-table-data').dataTable( {
        "aoColumns": uptempo.imageCategories.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.imageCategories.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });

}

uptempo.imageCategories.search = function() {
  var name = $("#imagecategories-search-name").val();
  var description = $("#imagecategories-search-description").val();
  var searchCriteria = "";
  if (name != "")  {
    searchCriteria += "name=" + name + "&";
  }
  if (description != "") {
    searchCriteria += "description=" + description;
  }
  uptempo.imageCategories.getImageCategoriesData(searchCriteria);
}


uptempo.imageCategories.viewKey = function (imageCategoryKey) {
  $("#imagecategories-key-display-popup").popup("open");
  $("#imagecategories-key-display").html(imageCategoryKey);
}

uptempo.imageCategories.showDeleteConfirm = function(imageCategoryKey) {
  var imageCategoryName = "Could not get image category Name";
  
  //*** Get the application code/name.
  $.ajax({
    type: 'GET',
    url: '/service/imagecategory/' + imageCategoryKey,
    success: function(response) {
      if (response.status == "SUCCESS") {  
        imageCategoryName = response.data.name;        
      }
      $("#imagecategories-name-delete").val(imageCategoryName);
      $("#imagecategories-confirm-popup-body").html(
        "Are you sure you want to delete image category: " + imageCategoryName + "?");
    }
  });

  //*** Set the title and body.
  $("#imagecategories-confirm-popup-heading").html("Delete image category?");
  $("#imagecategories-confirm-popup-action").html("Delete image category");
  $("#imagecategories-key-delete").val(imageCategoryKey);
  $("#imagecategories-confirm-popup-delete").on( "click", uptempo.imageCategories.deleteCategory );

  //*** Show the delete form.
  $("#imagecategories-confirm-popup").popup("open");
}

uptempo.imageCategories.deleteCategory = function() {
  var imageCategoryKey = $("#imagecategories-key-delete").val();
  var imageCategoryMessage = $("#imagecategories-name-delete").val();

  //*** Define a delete success function.
  var imageCategoriesDelSuccessFn = function() {
    $("#imagecategories-confirm-popup").popup("close");
    uptempo.imageCategories.getImageCategoriesData();
  };

  uptempo.ajax.submitDelete(imageCategoryKey, 
                            "/service/imagecategory/", 
                            "image category", 
                            imageCategoryMessage, 
                            imageCategoriesDelSuccessFn);
  
}

uptempo.imageCategories.cleanFormErrorsAfterPopup = function() {
  $("#imagecategories-form").bind({
    popupafterclose: function(event, ui) {
      $(".form-errors").html("");
    }
  });
}


//***When the user goes to this page, show the data table on load.
$("#imagecategories").live('pageshow', uptempo.imageCategories.getImageCategoriesData);
$("#imagecategories").live('pageshow', uptempo.util.pageTransition);
$("#imagecategories").live('pageinit', uptempo.imageCategories.cleanFormErrorsAfterPopup);
