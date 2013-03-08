/* This file contains all admin user actions */
msAdmin.config = {};
msAdmin.config.tableHeadings = [
  {"sTitle": "Application", "aTargets": [0]},
  {"sTitle": "Config Name", "aTargets": [1]},
  {"sTitle": "Description", "aTargets": [2]},
  {"sTitle": "Value", "aTargets": [3]},
  {"sTitle": "Text Field", "aTargets": [4]},
  {"sTitle": "Image", "aTargets": [5]},
  {"sTitle": "Modified By", "aTargets": [6]},
  {"sTitle": "Action", "aTargets": [7], "mData" : null},
];

//*** Field mapping for validation and naming.
msAdmin.config.validFields =
    [{name: "App Code", inputId: "#config-app", formVal: "appCode", required: true},
     {name: "Config Value Code", inputId: "#config-name", formVal: "name", required: true},
     {name: "Description", inputId: "#config-description", formVal: "description", required: false},
     {name: "Value", inputId: "#config-value", formVal: "value", required: false},
     {name: "Long Text Value", inputId: "#config-text", formVal: "text", required: false}];

//*** Show the new config value popup.
msAdmin.config.showNew = function () {
  //*** Show the form.
  $("#config-form").popup("open");
  $("#config-form-title").html("Create a new config value");

  $("#config-name").val("");
  $("#config-description").val("");
  $("#config-value").val("");
  $("#config-text").val("");
  $("#config-form-submit").changeButtonText("Create config value");
  $("#config-form-submit").off("click");
  $("#config-form-submit").on("click", msAdmin.config.submitNew);
  msAdmin.ajax.fillDropdownWithApps("config-app");
}

//*** Show the update config value popup.
msAdmin.config.showUpdate = function (valueKey) {
  $("#config-form-title").html("Update a config value");
  $("#config-form-submit").html("Update this value");
  msAdmin.ajax.fillDropdownWithApps("config-app");

  //*** Submit the XHR request, adding a slight delay so the app code can be populated.
  setTimeout(
    function() {
    $.ajax({
      type: 'GET',
      url: '/service/config/' + valueKey,
      success: function(response) {
        //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
          $("#config-app").val(response.data.appCode);
          $("#config-app").selectmenu("refresh");
          $("#config-name").val(response.data.name);
          $("#config-description").val(response.data.description);
          $("#config-value").val(response.data.value);
          $("#config-text").val(response.data.text);
          $("#config-key").val(valueKey);
        } else {
          alert(response.message);
        }
        msAdmin.loader.hide();
      }
    })}, 120);

  $("#config-form-submit").changeButtonText("Update this config value");
  $("#config-form-submit").off("click");
  $("#config-form-submit").on("click", msAdmin.config.submitUpdate);
  //*** Show the form.
  $("#config-form").popup("open");
  
}

msAdmin.config.submitUpdate = function() {
  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#config-form").popup("close");
    msAdmin.config.clearConfigForm();
    msAdmin.config.getConfigData();
  };

  //*** Set the key for submission.
  var configKey = $("#config-key").val();
  msAdmin.ajax.submitUpdate("Config Value",
                            "/service/config/" + configKey,
                            msAdmin.config.validFields,
                            "config-name",
                            successFn,
                            null);
}

msAdmin.config.submitNew = function () {
  //*** Set the key for submission.
  var configKey = $("#config-app").val() + $("#config-name").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#config-form").popup("close");
    msAdmin.config.clearConfigForm();
    msAdmin.config.getConfigData();
  };

  //*** Checks if either the config value or text was filled in.
  var configValidFn = function() {
    var configValue = $("#config-value").val();
    var configText = $("#config-text").val();
    var configApp = $("#config-app").val();
    if (configApp == "Select an Application") {
      return false;
    }
    if (configValue == null && configText == null) {
      return false;
    } else {
      return true;
    }
  }
  
  msAdmin.ajax.submitNew("Configuration Value",
                         "/service/config",
                         msAdmin.config.validFields,
                         "config-name",
                         configKey,
                         successFn,
                         configValidFn);
}

msAdmin.config.clearConfigForm = function() {
  $('#config-app').val("DEFAULT");
  $('#config-name').val("");
  $('#config-description').val("");
  $('#config-value').val("");
  $('#config-text').val("");
}

msAdmin.config.getConfigData = function () {
  var configDataArray = ["No config data."];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/config',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, put the config data in the table.
      if (response.status == "SUCCESS") {
        configDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get config values! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#config-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="config-table-data"></table>' );
      $('#config-table-data').dataTable( {
        "aoColumnDefs": msAdmin.config.tableHeadings,
        "aaData" : configDataArray,
        "fnRowCallback": msAdmin.config.tableFormatter,
        "bProcessing": true
      });
    }
  });
}

//*** Formats the user table.
msAdmin.config.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"msAdmin.config.showUpdate('" + aData[11] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"msAdmin.config.showDeleteConfirm('" + aData[11] + "');\">del</a>";
  $("td:eq(7)", nRow).html(editLink + delLink);
  
  //*** Replace HTML in the config text so it's visible
  var configTextLink = "";
  if (aData[4]) {
    configTextLink = "<a href='#'>View Config Text</a>"
  }
  $("td:eq(4)", nRow).html(configTextLink);
};

msAdmin.config.tableAddRow = function() {
  $('#config-table-data').dataTable().fnAddData(
    [application,
     configName,
     description,
     value,
     text,
     "Image Not Implemented",
     user,
     id]
  )
}

msAdmin.config.showDeleteConfirm = function(configKey) {
  //*** Set the title and body.
  $("#config-confirm-popup-heading").html("Delete Config Value?");
  $("#config-confirm-popup-body").html("Are you sure you want to delete config value " + configKey + "?");
  $("#config-confirm-popup-action").html("Delete Config Value");
  $("#config-key-delete").val(configKey);
  $("#config-confirm-popup-delete").on("click", msAdmin.config.deleteConfig);

  //*** Show the form.
  $("#config-confirm-popup").popup("open");
}

msAdmin.config.deleteConfig = function() {
  var configKey = $("#config-key-delete").val();
  $.ajax({
      type: 'DELETE',
      url: '/service/config/' + configKey,
      success: function(response) {
        //*** If the response was sucessful, close the popup form.
        if (response.status != "SUCCESS") {
          $(".form-errors").html(response.message);
          $(".form-errors").css("display", "block");
          alert(response.message);
          $("#config-confirm-popup").popup("close");
        } else {
          $(".form-errors").html("Config Value " + configKey + " successfully deleted.");
          $(".form-errors").css("display", "block");
          $("#config-confirm-popup").popup("close");
          msAdmin.config.getConfigData();
        }
      },
      complete: msAdmin.loader.hide()
    });
}

//***When the user goes to this page, show the data table on load.
$("#config").live('pageshow', msAdmin.config.getConfigData);
$("#config").live('pageshow', msAdmin.util.pageTransition);
