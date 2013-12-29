/* This file contains all admin audit definition actions */
uptempo.audit = {};
uptempo.audit.tableHeadings = [
  {"sTitle": "App Code", "aTargets": [0]},
  {"sTitle": "Event Code", "aTargets": [1]},
  {"sTitle": "Description", "aTargets": [2]},
  {"sTitle": "Severity", "aTargets": [3]},
  {"sTitle": "Alert Threshold", "aTargets": [4]},
  {"sTitle": "Alert Type", "aTargets": [5]},
  {"sTitle": "Alert Email", "bVisible": false, "aTargets": [6]},
  {"sTitle": "Alert Phone", "bVisible": false, "aTargets": [7]},  
  {"sTitle": "Modified By", "bVisible": false, "aTargets": [8]},
  {"sTitle": "Modify Date", "bVisible": false, "aTargets": [9]},
  {"sTitle": "Action", "bVisible": false, "aTargets": [10]},
];

//*** Field mapping for validation and naming.
uptempo.audit.validFields =
    [
     {name: "Event Code", inputId: "#event-code", formVal: "eventCode", required: true},     
     {name: "App Code", inputId: "#apps-code", formVal: "appCode", required: true},
     {name: "Description", inputId: "#description", formVal: "description", required: true},
     {name: "Severity", inputId: "#severity", formVal: "severity", required: true},
     {name: "Alert Threshold", inputId: "#alert-threshold", formVal: "alertThreshold", required: false},
     {name: "Alert Type", inputId: "#alert-type", formVal: "alertType", required: false},
     {name: "Alert Email", inputId: "#alert-email", formVal: "alertEmail", required: false},
     {name: "Alert Phone", inputId: "#alert-phone", formVal: "alertPhone", required: false}
     ];

//*** Formats the audit table.
uptempo.audit.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"uptempo.audit.showUpdate('" + aData[10] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.audit.showDeleteConfirm('" + aData[10] + "');\">del</a>";
  $("td:eq(9)", nRow).html(editLink + delLink);
  //*** Format the date
  var logDate = new Date(aData[10]);
  $("td:eq(8)", nRow).html(logDate.toISOString());
};

loadAppCodeOptions = function(appDataArray){
	
	for (field in appDataArray) {
		var key = appDataArray[field][0];
		var value = appDataArray[field][1];
		$("#apps-code")
          .append($('<option>', { value : key })
          .text(value)); 
	}
	
}

uptempo.audit.showNew = function () {
	myArray = uptempo.audit.getAppDataForAudit();

	//*** Setup the form.	
  $("#audit-form-title").html("New Audit Event Type");
  $("#audit-form-submit").changeButtonText("Create this audit event");
  $("#audit-form-submit").off("click");
  $("#audit-form-submit").on("click", uptempo.audit.submitNew);
  $("#audit-form-errors").html("");
  //*** Show the form.
  $("#audit-form").popup("open");
}

uptempo.audit.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#event-code").val();

  //*** On success, close the submission window and reload the table.
  var auditSuccessFn = function() {
    $("#audit-form").popup("close");
    uptempo.audit.clearAuditForm();
    uptempo.audit.getAuditData();
  };

  uptempo.ajax.submitNew("Audit",
                         "/service/audit",
                         uptempo.audit.validFields,
                         "event-code",
                         key,
                         auditSuccessFn);
}

//*** Show the update application popup.
uptempo.audit.showUpdate = function (valueKey) {
	uptempo.audit.clearAuditForm();
  $("#audit-form-title").html("Update an Application");
  $("#audit-form-submit").changeButtonText("Update this application");  
  $("#audit-form-errors").html("");
  $("#audit-key").val(valueKey);
  uptempo.audit.getAppDataForAudit();
  //*** Get the data for this application.

    //*** Submit the XHR request.
    $.ajax({
      type: 'GET',
      url: '/service/audit/' + valueKey,
      success: function(response) {
        //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
        	
          var eventCode = response.data.eventCode;
          var appCode = response.data.appCode;
          var description = response.data.description;
          var severity = response.data.severity;
          var alertThreshold = response.data.alertThreshold;
          var alertType = response.data.alertType;
          var alertEmail = response.data.alertEmail;
          var alertPhone = response.data.alertPhone;
          alert(eventCode + ', ' + appCode + ', ' + description + ', ' + severity + ', ' + alertThreshold + ', ' + alertType + ', ' + alertEmail + ', ' + alertPhone );
          $("#apps-code").val(appCode);
          $("#event-code").val(eventCode);
          $("#description").val(description);
          $("#severity").val(severity);
          $("#alert-threshold").val(alertThreshold);
          $("#alert-type").val(alertType);
          $("#alert-email").val(alertEmail);
          $("#alert-phone").val(alertPhone);
        } else {
          alert(response.message);
        }
      }
    });

  $("#audit-form-submit").off("click");
  $("#audit-form-submit").on("click", uptempo.audit.submitUpdate);
  //*** Show the form.
  $("#audit-form").popup("open");
}

uptempo.audit.submitUpdate = function() {
  //*** Set the key for submission.
  var auditKey = $("#audit-key").val();
  
  //*** On success, close the submission window and reload the table.
  var auditUpdsuccessFn = function() {

    $("#audit-form").popup("close");
    uptempo.audit.clearAuditForm();
    uptempo.audit.getAuditData();
  };

  uptempo.ajax.submitUpdate("Audit",
                            "/service/audit/" + auditKey,
                            uptempo.audit.validFields,
                            "event-code",
                            auditUpdsuccessFn);
}

uptempo.audit.clearAuditForm = function() {
	
  $('#apps-code').val("");  
  $('#event-code').val("");
  $('#description').val("");
  $('#severity').val("");
  $('#alert-threshold').val("");
  $('#alert-type').val("");
  $('#alert-email').val("");
  $('#alert-phone').val("");
};

uptempo.audit.getAppDataForAudit = function () {
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/app',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
		loadAppCodeOptions(appDataArray);
      } else {
        $(".status-bar").html("Failed to get application records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
};

uptempo.audit.getAuditData = function () {
  uptempo.loader.show("Getting audit data.");
  var appDataArray = ["No audit data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/audit',
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
      $('#audit-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="audit-table-data"></table>' );
      //*** Make this table the active one for row events.
      uptempo.activeTable = $('#audit-table-data').dataTable( {
        "aoColumnDefs": uptempo.audit.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.audit.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });
};


uptempo.audit.showDeleteConfirm = function(auditKey) {
  var eventCode = "Could not get Audit event Code";
  var appCode = "AUDIT";

  //*** Get the application code/name.
  $.ajax({
    type: 'GET',
    url: '/service/audit/' + auditKey,
    success: function(response) {
      if (response.status == "SUCCESS") {          
        appCode = response.data.appCode;
		eventCode = response.data.eventCode;
      }
      $("#app-code-delete").val(appCode);
      $("#event-code-delete").val(eventCode);
      $("#audit-confirm-popup-body")
          .html("Are you sure you want to delete audit event type " + eventCode + "?");
    }
  });

  //*** Set the title and body.
  $("#audit-confirm-popup-heading").html("Delete Audit Event Type?");
  $("#audit-confirm-popup-action").html("Delete Audit Event Type");
  $("#audit-key-delete").val(auditKey);
  $("#audit-confirm-popup-delete").on("click", uptempo.audit.deleteApp);

  //*** Show the form.
  $("#audit-confirm-popup").popup("open");
}


uptempo.audit.deleteApp = function() {
  var auditKey = $("#audit-key-delete").val();
  var appCode = $("#app-code-delete").val();
  var eventCode = "(" + $("#event-code-delete").val() + ")";
  var auditMessage = appCode + eventCode;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#audit-confirm-popup").popup("close");
    uptempo.audit.getAuditData();
  };
  uptempo.ajax.submitDelete(auditKey, "/service/audit/", "Audit", auditMessage, audDelSuccessFn);
}


//***When the user goes to this page, show the data table on load.
$("#auditing").live('pageshow', uptempo.audit.getAuditData);
$("#auditing").live('pageshow', uptempo.util.pageTransition);
