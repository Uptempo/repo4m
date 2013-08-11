/* This file contains all admin application definition actions */
uptempo.app = {};
uptempo.app.tableHeadings = [
  {"sTitle": "App Code", "aTargets": [0]},
  {"sTitle": "App Name", "aTargets": [1]},
  {"sTitle": "Description", "aTargets": [2]},
  {"sTitle": "Prod URL", "bVisible": false, "aTargets": [3]},
  {"sTitle": "Created By", "bSearchable": false, "bVisible": false, "aTargets": [4]},
  {"sTitle": "Create Date", "bSearchable": false, "bVisible": false, "aTargets": [5]},
  {"sTitle": "Modified By", "aTargets": [6]},
  {"sTitle": "Modify Date" , "bSearchable": false, "aTargets": [7]},
  {"sTitle": "Access Key", "bSearchable": false, "aTargets": [8]},
  {"sTitle": "Action", "aTargets": [9], "mData" : null},
];

//*** Field mapping for validation and naming.
uptempo.app.validFields =
    [{name: "App Code", inputId: "#app-code", formVal: "appCode", required: true},
     {name: "App Name", inputId: "#app-name", formVal: "appName", required: true},
     {name: "Description", inputId: "#app-description", formVal: "appDescription", required: true},
     {name: "URL", inputId: "#app-url", formVal: "url", required: false}];

//*** Formats the app table.
uptempo.app.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"uptempo.app.showUpdate('" + aData[9] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.app.showDeleteConfirm('" + aData[9] + "');\">del</a>";
  $("td:eq(6)", nRow).html(editLink + delLink);
  //*** Show the access key actions.
  var showAkLink = "<a href='#' onclick=\"uptempo.app.showAccessKey('" + aData[8] + "');\">view</a>&nbsp;&nbsp;&nbsp;&nbsp;";
  var changeAkLink = "<a href='#' onclick=\"uptempo.app.showAppKeyForm('" + aData[0] + "','" + aData[9] + "');\">reset</a>";
  $("td:eq(5)", nRow).html(showAkLink + changeAkLink);
};

uptempo.app.showNew = function () {
  //*** Setup the form.
  $("#app-form-title").html("New Application");
  $("#app-form-submit").changeButtonText("Create this application");
  $("#app-code").removeAttr("disabled");
  $("#app-form-submit").off("click");
  $("#app-form-submit").on("click", uptempo.app.submitNew);
  $("#app-form-errors").html("");
  //*** Show the form.
  $("#app-form").popup("open");
}

uptempo.app.submitNew = function () {
  //*** Set the key for submission.
  var appKey = $("#app-code").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#app-form").popup("close");
    uptempo.app.clearAppForm();
    uptempo.app.getAppData();
  };

  uptempo.ajax.submitNew("Application",
                         "/service/app",
                         uptempo.app.validFields,
                         "app-code",
                         appKey,
                         successFn);
}

//*** Show the update application popup.
uptempo.app.showUpdate = function (valueKey) {
  $("#app-form-title").html("Update an Application");
  $("#app-form-submit").changeButtonText("Update this application");
  $("#app-code").attr("disabled", "true");
  $("#app-form-errors").html("");
  $("#app-key").val(valueKey);
  //*** Get the data for this application.

    //*** Submit the XHR request.
    $.ajax({
      type: 'GET',
      url: '/service/app/' + valueKey,
      success: function(response) {
        //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
          var appCode = response.data.appCode;
          var appName = response.data.appName;
          var appDescription = response.data.appDescription;
          var url = response.data.url;
          $("#app-code").val(appCode);
          $("#app-name").val(appName);
          $("#app-description").val(appDescription);
          $("#app-url").val(url);
        } else {
          alert(response.message);
        }
      }
    });

  $("#app-form-submit").off("click");
  $("#app-form-submit").on("click", uptempo.app.submitUpdate);
  //*** Show the form.
  $("#app-form").popup("open");
}

uptempo.app.submitUpdate = function() {
  //*** Set the key for submission.
  var appKey = $("#app-key").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#app-form").popup("close");
    uptempo.app.clearAppForm();
    uptempo.app.getAppData();
  };

  uptempo.ajax.submitUpdate("Application",
                            "/service/app/" + appKey,
                            uptempo.app.validFields,
                            "app-code",
                            successFn);
}

uptempo.app.clearAppForm = function() {
  $('#app-code').val("");
  $('#app-name').val("");
  $('#app-description').val("");
  $('#app-url').val("");
}

uptempo.app.getAppData = function () {
  uptempo.loader.show("Getting application data.");
  var appDataArray = ["No application data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/app',
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
      $('#app-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="app-table-data"></table>' );
      //*** Make this table the active one for row events.
      uptempo.activeTable = $('#app-table-data').dataTable( {
        "aoColumnDefs": uptempo.app.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.app.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });
};

uptempo.app.showAccessKey = function (accessKey) {
  $("#app-key-display-popup").popup("open");
  $("#app-key-display").html(accessKey);
};

uptempo.app.showAppKeyForm = function (appCode, appKey) {
  //*** Show the form.
  $("#app-key-popup").popup("open");
  //*** Fill in the appCode field with the application code.
  $("#app-key-reset-confirm").html("Are you sure you want to reset the key for " + appCode + "?");
  $("#app-key-reset").val(appKey);
  $("#app-confirm-popup-reset").on("click", uptempo.app.resetKey);
}

uptempo.app.resetKey = function() {
  var appKey = $("#app-key-reset").val();
  uptempo.loader.show("Resetting application key.");
  $.ajax({
    type: 'POST',
    url: '/service/appkey/' + appKey,
    success: function(response) {
      $(".status-bar").html(response.message);
      $(".status-bar").css("display", "block");
      $("#app-key-popup").popup("close");      
    },
    complete: uptempo.loader.hide()
  });
}

uptempo.app.showDeleteConfirm = function(appKey) {
  var appName = "Could not get Application Name";
  var appCode = "APPLICATION";

  //*** Get the application code/name.
  $.ajax({
    type: 'GET',
    url: '/service/app/' + appKey,
    success: function(response) {
      if (response.status == "SUCCESS") {  
        appName = response.data.appName;
        appCode = response.data.appCode;
      }
      $("#app-code-delete").val(appCode);
      $("#app-name-delete").val(appName);
      $("#app-confirm-popup-body")
          .html("Are you sure you want to delete application " + appCode + "?");
    }
  });

  //*** Set the title and body.
  $("#app-confirm-popup-heading").html("Delete Application?");
  $("#app-confirm-popup-action").html("Delete Application");
  $("#app-key-delete").val(appKey);
  $("#app-confirm-popup-delete").on("click", uptempo.app.deleteApp);

  //*** Show the form.
  $("#app-confirm-popup").popup("open");
}

uptempo.app.deleteApp = function() {
  var appKey = $("#app-key-delete").val();
  var appCode = $("#app-code-delete").val();
  var appName = "(" + $("#app-name-delete").val() + ")";
  var appMessage = appCode + appName;

  //*** Define a success function.
  var successFn = function() {
    $("#app-confirm-popup").popup("close");
    uptempo.app.getAppData();
  };
  uptempo.ajax.submitDelete(appKey, "/service/app/", "Application", appMessage, successFn);
}

//***When the user goes to this page, show the data table on load.
$("#applications").live('pageshow', uptempo.app.getAppData);
$("#applications").live('pageshow', uptempo.util.pageTransition);
