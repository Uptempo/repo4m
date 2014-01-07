/* This file contains all admin POI definition actions */
uptempo.medlayerpoi = {};
uptempo.medlayerpoi.tableHeadings = [
  {"sTitle": "title", "aTargets": [0]},
  {"sTitle": "lat", "aTargets": [1],"bVisible": false},
  {"sTitle": "lon", "aTargets": [2],"bVisible": false},
  {"sTitle": "alt", "aTargets": [3],"bVisible": false},
  {"sTitle": "Description", "aTargets": [4]},
  {"sTitle": "Details", "aTargets": [5], "bVisible": false},
  {"sTitle": "IMG normal", "aTargets": [6],"bVisible": false},
  {"sTitle": "IMG selected", "aTargets": [7],"bVisible": false},
  {"sTitle": "IMG direction", "aTargets": [8],"bVisible": false},
  {"sTitle": "MedLayer Dataset", "bSearchable": false, "bVisible": false, "aTargets": [9]},
  {"sTitle": "Created By", "bSearchable": false, "bVisible": false, "aTargets": [10]},
  {"sTitle": "Modified By", "bSearchable": false, "bVisible": false, "aTargets": [11]},
  {"sTitle": "Create Date", "bSearchable": false, "bVisible": false, "aTargets": [12]},
  {"sTitle": "Modify Date" , "bSearchable": false, "bVisible": false, "bSearchable": false, "aTargets": [13]},
  {"sTitle": "HTML", "aTargets": [14],"bVisible": false},
  {"sTitle": "Access Key", "bSearchable": false, "bVisible": false, "aTargets": [15]},
  {"sTitle": "Action", "aTargets": [16], "mData" : null},
];

//*** Field mapping for validation and naming.
uptempo.medlayerpoi.validFields =
    [{name: "title", inputId: "#medlayerpoi-title", formVal: "title", required: true},
    {name: "description", inputId: "#medlayerpoi-description", formVal: "description", required: true},
    {name: "longitude", inputId: "#medlayerpoi-lon", formVal: "lon", required: true},
    {name: "latitude", inputId: "#medlayerpoi-lat", formVal: "lat", required: true},
    {name: "altitude", inputId: "#medlayerpoi-alt", formVal: "alt", required: true},
    {name: "normalimg", inputId: "#medlayerpoi-normalimg", formVal: "normalImg", required: true},
    {name: "directionimg", inputId: "#medlayerpoi-directionimg", formVal: "directionImg", required: true},
    {name: "selectedimg", inputId: "#medlayerpoi-selectedimg", formVal: "selectedImg", required: true},
    {name: "detailHTML", inputId: "#medlayerpoi-detailhtml", formVal: "DetailHTML", required: true},
    {name: "MedLayer Dataset", inputId: "#medlayer-poi-dataset", formVal: "medlayerDataset", required: true}
    ];

//*** Formats the app table.
uptempo.medlayerpoi.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"uptempo.medlayerpoi.showUpdate('" + aData[15] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.medlayerpoi.showDeleteConfirm('" + aData[15] + "');\">del</a>";
  var showAkLink = "<a href='#' onclick=\"uptempo.medlayerpoi.showAccessKey('" + aData[15] + "');\">view</a>&nbsp;&nbsp;";
  $("td:eq(2)", nRow).html(showAkLink + editLink + delLink);
};

uptempo.medlayerpoi.showNew = function () {
  //*** Setup the form.
  uptempo.medlayerpoi.clearAppForm();    
  uptempo.medlayerpoi.fillDropdownWithDatasets("medlayer-poi-dataset");
  $("#medlayerpoi-form-title").html("New poi");
  $("#medlayerpoi-form-submit").changeButtonText("Create this poi");
  $("#medlayerpoi-form-submit").off("click");
  $("#medlayerpoi-form-submit").on("click", uptempo.medlayerpoi.submitNew);
  $("#medlayerpoi-form-errors").html("");
  //*** Show the form.
  $("#medlayerpoi-form").popup("open");
}

uptempo.medlayerpoi.submitNew = function () {
  //*** Set the key for submission.
  var appKey = $("#medlayerpoi-title").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#medlayerpoi-form").popup("close");
    uptempo.medlayerpoi.clearAppForm();
    uptempo.medlayerpoi.getpoiData();
  };

  uptempo.ajax.submitNew("POI",
                         "/medlayer/poi",
                         uptempo.medlayerpoi.validFields,
                         "medlayerpoi-title",
                         appKey,
                         successFn);
}

//*** Show the update POI popup.
uptempo.medlayerpoi.showUpdate = function (valueKey) {
  uptempo.medlayerpoi.clearAppForm();    
  uptempo.medlayerpoi.fillDropdownWithDatasets("medlayer-poi-dataset");    
  $("#medlayerpoi-form-title").html("Update POI");
  $("#medlayerpoi-form-submit").changeButtonText("Update this POI");
  $("#medlayerpoi-code").attr("disabled", "true");
  $("#medlayerpoi-form-errors").html("");
  $("#medlayerpoi-key").val(valueKey);
  //*** Get the data for this POI.

    //*** Submit the XHR request.
    $.ajax({
      type: 'GET',
      url: '/medlayer/poi/' + valueKey,
      data: "format=obj",
      success: function(response) {
        //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
            $("#medlayerpoi-title").val(response.data.title);
            $("#medlayerpoi-description").val(response.data.description);
            $("#medlayerpoi-lon").val(response.data.lon);
            $("#medlayerpoi-lat").val(response.data.lat);
            $("#medlayerpoi-alt").val(response.data.alt);
            $("#medlayerpoi-normalimg").val(response.data.normalImg);
            $("#medlayerpoi-directionimg").val(response.data.directionImg );
            $("#medlayerpoi-selectedimg").val(response.data.selectedImg);
            $("#medlayerpoi-detailhtml").val(response.data.DetailHTML);
            $("#medlayer-poi-dataset").val(response.data.medlayerDataset);
            $("#medlayer-poi-dataset").selectmenu("refresh");    
        } else {
          alert(response.message);
        }
      }
    });

  $("#medlayerpoi-form-submit").off("click");
  $("#medlayerpoi-form-submit").on("click", uptempo.medlayerpoi.submitUpdate);
  //*** Show the form.
  $("#medlayerpoi-form").popup("open");
}

uptempo.medlayerpoi.submitUpdate = function() {
  //*** Set the key for submission.
  var appKey = $("#medlayerpoi-key").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#medlayerpoi-form").popup("close");
    uptempo.medlayerpoi.clearAppForm();
    uptempo.medlayerpoi.getpoiData();
  };

  uptempo.ajax.submitUpdate("POI",
                            "/medlayer/poi/" + appKey,
                            uptempo.medlayerpoi.validFields,
                            "medlayerpoi-title",
                            successFn);
}

uptempo.medlayerpoi.clearAppForm = function() {
  $('#medlayerpoi-key').val("");
  $('#medlayerpoi-tabName').val("");
  $('#medlayerpoi-tabIcon').val("");
}

uptempo.medlayerpoi.getpoiData = function () {
  uptempo.loader.show("Getting poi data.");
  var appDataArray = ["No poi data"];
  
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/medlayer/poi',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get POI records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#medlayerpoi-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="poi-table-data"></table>' );
      //*** Make this table the active one for row events.
      uptempo.activeTable = $('#poi-table-data').dataTable( {
        "aoColumnDefs": uptempo.medlayerpoi.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.medlayerpoi.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });
};

uptempo.medlayerpoi.showAccessKey = function (accessKey) {
  $("#medlayerpoi-key-display-popup").popup("open");
  $("#medlayerpoi-key-display").html(accessKey);
};

uptempo.medlayerpoi.showDeleteConfirm = function(poiKey) {
  var tabName = "Could not get poi Name";
  
  //*** Get the POI code/name.
  $.ajax({
    type: 'GET',
    url: '/medlayer/poi/' + poiKey,
    success: function(response) {
      if (response.status == "SUCCESS") {  
        tabName = response.data.tabName;
      }
      $("#medlayerpoi-name-delete").val(tabName);
      $("#medlayerpoi-confirm-popup-body")
          .html("Are you sure you want to delete " + tabName + "?");
    }
  });

  //*** Set the title and body.
  $("#medlayerpoi-confirm-popup-heading").html("Delete poi?");
  $("#medlayerpoi-confirm-popup-action").html("Delete poi");
  $("#medlayerpoi-key-delete").val(poiKey);
  $("#medlayerpoi-confirm-popup-delete").on("click", uptempo.medlayerpoi.deletepoi);

  //*** Show the form.
  $("#medlayerpoi-confirm-popup").popup("open");
}

uptempo.medlayerpoi.deletepoi = function() {
  var poiKey = $("#medlayerpoi-key-delete").val();
  var tabName = "(" + $("#medlayerpoi-name-delete").val() + ")";
  var tabMessage = tabName;

  //*** Define a success function.
  var successFn = function() {
    $("#medlayerpoi-confirm-popup").popup("close");
    uptempo.medlayerpoi.getpoiData();
  };
  uptempo.ajax.submitDelete(poiKey, "/medlayer/poi/", "poi", tabMessage, successFn);
}

uptempo.medlayerpoi.fillDropdownWithDatasets = function (dropdownId, callbackFn) {
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/medlayer/dataset',
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var appDataArray = response.data.values;
        var appDropDownId = $("#" + dropdownId);
        appDropDownId.empty();
        if (appDataArray.length == 0) {
          appDropDownId.append("<option value=''>***No Datasets Found!***</option>");
        }
        $.each(appDataArray, function(index, group) {
          appDropDownId.append(
              "<option value='" + group.key + "'>" + group.tabName +
              "</option>");
        })
        appDropDownId.selectmenu("refresh");
        if (callbackFn != null) {
          callbackFn();
        }
      } else {
        var appValues = "<select>" +
                      "<option value='DEFAULT'> Could not get datasets, defaulting to DEFAULT</option>" +
                      "</select>";
        appDropDownId.replaceWith(appValues)
      }
    }
  });
};

uptempo.medlayerpoi.getLocationCoord = function (addressFieldID) {
            var geocoder = new google.maps.Geocoder();
            var address = $("#"+addressFieldID).val();
            geocoder.geocode({ 'address': address }, function (results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
                    var latitude = results[0].geometry.location.lat();
                    var longitude = results[0].geometry.location.lng();
                    $("#medlayerpoi-lat").val(latitude);
                    $("#medlayerpoi-lon").val(longitude);
                } else {
                    alert("Request failed.")
                }
            });
        };

//***When the user goes to this page, show the data table on load.
$("#medlayerpoi").live('pageshow', uptempo.medlayerpoi.getpoiData);
$("#medlayerpoi").live('pageshow', uptempo.util.pageTransition);
