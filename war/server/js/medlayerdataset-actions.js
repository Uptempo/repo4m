/* This file contains all admin application definition actions */
uptempo.medlayerdataset = {};
uptempo.medlayerdataset.tableHeadings = [
  {"sTitle": "Tab Name", "aTargets": [0]},
  {"sTitle": "Tab Icon", "aTargets": [1]},
  {"sTitle": "Medlayer App", "aTargets": [2]},
  {"sTitle": "Created By", "bSearchable": false, "bVisible": false, "aTargets": [3]},
  {"sTitle": "Create Date", "bSearchable": false, "bVisible": false, "aTargets": [5]},
  {"sTitle": "Modified By", "bSearchable": false, "bVisible": false, "aTargets": [4]},
  {"sTitle": "Modify Date" , "bSearchable": false, "bVisible": false, "bSearchable": false, "aTargets": [6]},
  {"sTitle": "Access Key", "bSearchable": false, "aTargets": [7]},
  {"sTitle": "Action", "aTargets": [8], "mData" : null},
];

//*** Field mapping for validation and naming.
uptempo.medlayerdataset.validFields =
    [{name: "Tab Name", inputId: "#medlayerdataset-tabName", formVal: "tabName", required: true},
    {name: "Tab Icon", inputId: "#medlayerdataset-tabIcon", formVal: "tabIcon", required: true},
    {name: "MedLayer App", inputId: "#medlayer-app-dataset", formVal: "medlayerApp", required: true}
    ];

//*** Formats the app table.
uptempo.medlayerdataset.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"uptempo.medlayerdataset.showUpdate('" + aData[7] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.medlayerdataset.showDeleteConfirm('" + aData[7] + "');\">del</a>";
  var showAkLink = "<a href='#' onclick=\"uptempo.medlayerdataset.showAccessKey('" + aData[7] + "');\">view</a>&nbsp;&nbsp;";
  $("td:eq(4)", nRow).html(showAkLink + editLink + delLink);
};

uptempo.medlayerdataset.showNew = function () {
  //*** Setup the form.
  uptempo.medlayerdataset.clearAppForm();    
  uptempo.medlayerdataset.fillDropdownWithApps("medlayer-app-dataset");
  $("#medlayerdataset-form-title").html("New Dataset");
  $("#medlayerdataset-form-submit").changeButtonText("Create this Dataset");
  $("#medlayerdataset-form-submit").off("click");
  $("#medlayerdataset-form-submit").on("click", uptempo.medlayerdataset.submitNew);
  $("#medlayerdataset-form-errors").html("");
  //*** Show the form.
  $("#medlayerdataset-form").popup("open");
}

uptempo.medlayerdataset.submitNew = function () {
  //*** Set the key for submission.
  var appKey = $("#medlayerdataset-tabName").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#medlayerdataset-form").popup("close");
    uptempo.medlayerdataset.clearAppForm();
    uptempo.medlayerdataset.getDatasetData();
  };

  uptempo.ajax.submitNew("Application",
                         "/medlayer/dataset",
                         uptempo.medlayerdataset.validFields,
                         "medlayerdataset-tabName",
                         appKey,
                         successFn);
}

//*** Show the update application popup.
uptempo.medlayerdataset.showUpdate = function (valueKey) {
  uptempo.medlayerdataset.clearAppForm();    
  uptempo.medlayerdataset.fillDropdownWithApps("medlayer-app-dataset");    
  $("#medlayerdataset-form-title").html("Update an Application");
  $("#medlayerdataset-form-submit").changeButtonText("Update this application");
  $("#medlayerdataset-code").attr("disabled", "true");
  $("#medlayerdataset-form-errors").html("");
  $("#medlayerdataset-key").val(valueKey);
  //*** Get the data for this application.

    //*** Submit the XHR request.
    $.ajax({
      type: 'GET',
      url: '/medlayer/dataset/' + valueKey,
      success: function(response) {
        //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
          var tabName = response.data.tabName;
          var tabIcon = response.data.tabIcon;
          var app = response.data.medlayerApp;
          $("#medlayerdataset-tabName").val(tabName);
          $("#medlayerdataset-tabIcon").val(tabIcon);
          $("#medlayer-app-dataset").val(app);
          $("#medlayer-app-dataset").selectmenu("refresh");    
        } else {
          alert(response.message);
        }
      }
    });

  $("#medlayerdataset-form-submit").off("click");
  $("#medlayerdataset-form-submit").on("click", uptempo.medlayerdataset.submitUpdate);
  //*** Show the form.
  $("#medlayerdataset-form").popup("open");
}

uptempo.medlayerdataset.submitUpdate = function() {
  //*** Set the key for submission.
  var appKey = $("#medlayerdataset-key").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#medlayerdataset-form").popup("close");
    uptempo.medlayerdataset.clearAppForm();
    uptempo.medlayerdataset.getDatasetData();
  };

  uptempo.ajax.submitUpdate("Application",
                            "/medlayer/dataset/" + appKey,
                            uptempo.medlayerdataset.validFields,
                            "app-code",
                            successFn);
}

uptempo.medlayerdataset.clearAppForm = function() {
  $('#medlayerdataset-key').val("");
  $('#medlayerdataset-tabName').val("");
  $('#medlayerdataset-tabIcon').val("");
}

uptempo.medlayerdataset.getDatasetData = function () {
  uptempo.loader.show("Getting dataset data.");
  var appDataArray = ["No dataset data"];
  
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/medlayer/dataset',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get application records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#medlayerdataset-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="dataset-table-data"></table>' );
      //*** Make this table the active one for row events.
      uptempo.activeTable = $('#dataset-table-data').dataTable( {
        "aoColumnDefs": uptempo.medlayerdataset.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.medlayerdataset.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });
};

uptempo.medlayerdataset.showAccessKey = function (accessKey) {
  $("#medlayerdataset-key-display-popup").popup("open");
  $("#medlayerdataset-key-display").html(accessKey);
};

uptempo.medlayerdataset.showDeleteConfirm = function(datasetKey) {
  var tabName = "Could not get Dataset Name";
  
  //*** Get the application code/name.
  $.ajax({
    type: 'GET',
    url: '/medlayer/dataset/' + datasetKey,
    success: function(response) {
      if (response.status == "SUCCESS") {  
        tabName = response.data.tabName;
      }
      $("#medlayerdataset-name-delete").val(tabName);
      $("#medlayerdataset-confirm-popup-body")
          .html("Are you sure you want to delete " + tabName + "?");
    }
  });

  //*** Set the title and body.
  $("#medlayerdataset-confirm-popup-heading").html("Delete dataset?");
  $("#medlayerdataset-confirm-popup-action").html("Delete dataset");
  $("#medlayerdataset-key-delete").val(datasetKey);
  $("#medlayerdataset-confirm-popup-delete").on("click", uptempo.medlayerdataset.deleteDataset);

  //*** Show the form.
  $("#medlayerdataset-confirm-popup").popup("open");
}

uptempo.medlayerdataset.deleteDataset = function() {
  var datasetKey = $("#medlayerdataset-key-delete").val();
  var tabName = "(" + $("#medlayerdataset-name-delete").val() + ")";
  var tabMessage = tabName;

  //*** Define a success function.
  var successFn = function() {
    $("#medlayerdataset-confirm-popup").popup("close");
    uptempo.medlayerdataset.getDatasetData();
  };
  uptempo.ajax.submitDelete(datasetKey, "/medlayer/dataset/", "Dataset", tabMessage, successFn);
}

uptempo.medlayerdataset.fillDropdownWithApps = function (dropdownId, callbackFn) {
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/medlayer/app',
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var appDataArray = response.data.values;
        var appDropDownId = $("#" + dropdownId);
        appDropDownId.empty();
        if (appDataArray.length == 0) {
          appDropDownId.append("<option value=''>***No Groups Found!***</option>");
        }
        $.each(appDataArray, function(index, group) {
          appDropDownId.append(
              "<option value='" + group.key + "'>" + group.appName +
              "</option>");
        })
        appDropDownId.selectmenu("refresh");
        if (callbackFn != null) {
          callbackFn();
        }
      } else {
        var appValues = "<select>" +
                      "<option value='DEFAULT'> Could not get groups, defaulting to DEFAULT</option>" +
                      "</select>";
        appDropDownId.replaceWith(appValues)
      }
    }
  });
};

//***When the user goes to this page, show the data table on load.
$("#medlayerdataset").live('pageshow', uptempo.medlayerdataset.getDatasetData);
$("#medlayerdataset").live('pageshow', uptempo.util.pageTransition);
