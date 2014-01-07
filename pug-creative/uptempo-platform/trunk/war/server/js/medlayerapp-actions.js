/* This file contains all admin application definition actions */
uptempo.medlayerapp = {};
uptempo.medlayerapp.tableHeadings = [
  {"sTitle": "App Name", "aTargets": [0]},
  {"sTitle": "Created By", "bSearchable": false, "bVisible": false, "aTargets": [3]},
  {"sTitle": "Create Date", "bSearchable": false, "bVisible": false, "aTargets": [1]},
  {"sTitle": "Modified By", "aTargets": [4]},
  {"sTitle": "Modify Date" , "bSearchable": false, "aTargets": [2]},
  {"sTitle": "Access Key", "bSearchable": false, "aTargets": [5]},
  {"sTitle": "Action", "aTargets": [6], "mData" : null},
];

//*** Field mapping for validation and naming.
uptempo.medlayerapp.validFields =
    [{name: "App Name", inputId: "#medlayerapp-name", formVal: "appName", required: true}
     ];

//*** Formats the app table.
uptempo.medlayerapp.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"uptempo.medlayerapp.showUpdate('" + aData[5] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.medlayerapp.showDeleteConfirm('" + aData[5] + "');\">del</a>";
  var showAkLink = "<a href='#' onclick=\"uptempo.medlayerapp.showAccessKey('" + aData[5] + "');\">view</a>&nbsp;&nbsp;";
  $("td:eq(4)", nRow).html(showAkLink + editLink + delLink);
};

uptempo.medlayerapp.showNew = function () {
  //*** Setup the form.
  $("#medlayerapp-form-title").html("New Application");
  $("#medlayerapp-form-submit").changeButtonText("Create this application");
  $("#medlayerapp-form-submit").off("click");
  $("#medlayerapp-form-submit").on("click", uptempo.medlayerapp.submitNew);
  $("#medlayerapp-form-errors").html("");
  //*** Show the form.
  $("#medlayerapp-form").popup("open");
}

uptempo.medlayerapp.submitNew = function () {
  //*** Set the key for submission.
  var appKey = $("#medlayerapp-name").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#medlayerapp-form").popup("close");
    uptempo.medlayerapp.clearAppForm();
    uptempo.medlayerapp.getAppData();
  };

  uptempo.ajax.submitNew("Application",
                         "/medlayer/app",
                         uptempo.medlayerapp.validFields,
                         "medlayerapp-name",
                         appKey,
                         successFn);
}

//*** Show the update application popup.
uptempo.medlayerapp.showUpdate = function (valueKey) {
  $("#medlayerapp-form-title").html("Update an Application");
  $("#medlayerapp-form-submit").changeButtonText("Update this application");
  $("#medlayerapp-code").attr("disabled", "true");
  $("#medlayerapp-form-errors").html("");
  $("#medlayerapp-key").val(valueKey);
  //*** Get the data for this application.

    //*** Submit the XHR request.
    $.ajax({
      type: 'GET',
      url: '/medlayer/app/' + valueKey,
      success: function(response) {
        //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
          var appName = response.data.appName;
          $("#medlayerapp-name").val(appName);
        } else {
          alert(response.message);
        }
      }
    });

  $("#medlayerapp-form-submit").off("click");
  $("#medlayerapp-form-submit").on("click", uptempo.medlayerapp.submitUpdate);
  //*** Show the form.
  $("#medlayerapp-form").popup("open");
}

uptempo.medlayerapp.submitUpdate = function() {
  //*** Set the key for submission.
  var appKey = $("#medlayerapp-key").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#medlayerapp-form").popup("close");
    uptempo.medlayerapp.clearAppForm();
    uptempo.medlayerapp.getAppData();
  };

  uptempo.ajax.submitUpdate("Application",
                            "/medlayer/app/" + appKey,
                            uptempo.medlayerapp.validFields,
                            "app-code",
                            successFn);
}

uptempo.medlayerapp.clearAppForm = function() {
  $('#medlayerapp-key').val("");
  $('#medlayerapp-name').val("");
}

uptempo.medlayerapp.getAppData = function () {
  uptempo.loader.show("Getting application data.");
  var appDataArray = ["No application data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/medlayer/app',
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
      $('#medlayerapp-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="app-table-data"></table>' );
      //*** Make this table the active one for row events.
      uptempo.activeTable = $('#app-table-data').dataTable( {
        "aoColumnDefs": uptempo.medlayerapp.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.medlayerapp.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });
};

uptempo.medlayerapp.showAccessKey = function (accessKey) {
  $("#medlayerapp-key-display-popup").popup("open");
  $("#medlayerapp-key-display").html(accessKey);
};

uptempo.medlayerapp.showAppKeyForm = function (appCode, appKey) {
  //*** Show the form.
  $("#medlayerapp-key-popup").popup("open");
  //*** Fill in the appCode field with the application code.
  $("#medlayerapp-key-reset-confirm").html("Are you sure you want to reset the key for " + appCode + "?");
  $("#medlayerapp-key-reset").val(appKey);
  $("#medlayerapp-confirm-popup-reset").on("click", uptempo.medlayerapp.resetKey);
}

uptempo.medlayerapp.showDeleteConfirm = function(appKey) {
  var appName = "Could not get Application Name";
  
  //*** Get the application code/name.
  $.ajax({
    type: 'GET',
    url: '/medlayer/app/' + appKey,
    success: function(response) {
      if (response.status == "SUCCESS") {  
        appName = response.data.appName;
      }
      $("#medlayerapp-name-delete").val(appName);
      $("#medlayerapp-confirm-popup-body")
          .html("Are you sure you want to delete application " + appName + "?");
    }
  });

  //*** Set the title and body.
  $("#medlayerapp-confirm-popup-heading").html("Delete Application?");
  $("#medlayerapp-confirm-popup-action").html("Delete Application");
  $("#medlayerapp-key-delete").val(appKey);
  $("#medlayerapp-confirm-popup-delete").on("click", uptempo.medlayerapp.deleteApp);

  //*** Show the form.
  $("#medlayerapp-confirm-popup").popup("open");
}

uptempo.medlayerapp.deleteApp = function() {
  var appKey = $("#medlayerapp-key-delete").val();
  var appName = "(" + $("#medlayerapp-name-delete").val() + ")";
  var appMessage = appName;

  //*** Define a success function.
  var successFn = function() {
    $("#medlayerapp-confirm-popup").popup("close");
    uptempo.medlayerapp.getAppData();
  };
  uptempo.ajax.submitDelete(appKey, "/medlayer/app/", "Application", appMessage, successFn);
}

//***When the user goes to this page, show the data table on load.
$("#medlayerapp").live('pageshow', uptempo.medlayerapp.getAppData);
$("#medlayerapp").live('pageshow', uptempo.util.pageTransition);
