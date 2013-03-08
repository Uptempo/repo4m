/* This file contains all admin audit definition actions */
msAdmin.audit = {};
msAdmin.audit.tableHeadings = [
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
msAdmin.audit.validFields =
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
msAdmin.audit.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"msAdmin.audit.showUpdate('" + aData[10] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"msAdmin.audit.showDeleteConfirm('" + aData[10] + "');\">del</a>";
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

msAdmin.audit.showNew = function () {
	myArray = msAdmin.audit.getAppDataForAudit();

	//*** Setup the form.	
  $("#audit-form-title").html("New Audit Event Type");
  $("#audit-form-submit").changeButtonText("Create this audit event");
  $("#audit-form-submit").off("click");
  $("#audit-form-submit").on("click", msAdmin.audit.submitNew);
  $("#audit-form-errors").html("");
  //*** Show the form.
  $("#audit-form").popup("open");
}

msAdmin.audit.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#event-code").val();

  //*** On success, close the submission window and reload the table.
  var auditSuccessFn = function() {
    $("#audit-form").popup("close");
    msAdmin.audit.clearAuditForm();
    msAdmin.audit.getAuditData();
  };

  msAdmin.ajax.submitNew("Audit",
                         "/service/audit",
                         msAdmin.audit.validFields,
                         "event-code",
                         key,
                         auditSuccessFn);
}

//*** Show the update application popup.
msAdmin.audit.showUpdate = function (valueKey) {
	msAdmin.audit.clearAuditForm();
  $("#audit-form-title").html("Update an Application");
  $("#audit-form-submit").changeButtonText("Update this application");  
  $("#audit-form-errors").html("");
  $("#audit-key").val(valueKey);
  msAdmin.audit.getAppDataForAudit();
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
  $("#audit-form-submit").on("click", msAdmin.audit.submitUpdate);
  //*** Show the form.
  $("#audit-form").popup("open");
}

msAdmin.audit.submitUpdate = function() {
  //*** Set the key for submission.
  var auditKey = $("#audit-key").val();
  
  //*** On success, close the submission window and reload the table.
  var auditUpdsuccessFn = function() {

    $("#audit-form").popup("close");
    msAdmin.audit.clearAuditForm();
    msAdmin.audit.getAuditData();
  };

  msAdmin.ajax.submitUpdate("Audit",
                            "/service/audit/" + auditKey,
                            msAdmin.audit.validFields,
                            "event-code",
                            auditUpdsuccessFn);
}

msAdmin.audit.clearAuditForm = function() {
	
  $('#apps-code').val("");  
  $('#event-code').val("");
  $('#description').val("");
  $('#severity').val("");
  $('#alert-threshold').val("");
  $('#alert-type').val("");
  $('#alert-email').val("");
  $('#alert-phone').val("");
}

msAdmin.audit.getAppDataForAudit = function () {
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
}


msAdmin.audit.getAuditData = function () {
	
  msAdmin.loader.show("Getting audit data.");
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
      msAdmin.activeTable = $('#audit-table-data').dataTable( {
        "aoColumnDefs": msAdmin.audit.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": msAdmin.audit.tableFormatter,
        "bProcessing": true
      });
    },
    complete: msAdmin.loader.hide()
  });

}


msAdmin.audit.showDeleteConfirm = function(auditKey) {
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
  $("#audit-confirm-popup-delete").on("click", msAdmin.audit.deleteApp);

  //*** Show the form.
  $("#audit-confirm-popup").popup("open");
}


msAdmin.audit.deleteApp = function() {
  var auditKey = $("#audit-key-delete").val();
  var appCode = $("#app-code-delete").val();
  var eventCode = "(" + $("#event-code-delete").val() + ")";
  var auditMessage = appCode + eventCode;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#audit-confirm-popup").popup("close");
    msAdmin.audit.getAuditData();
  };
  msAdmin.ajax.submitDelete(auditKey, "/service/audit/", "Audit", auditMessage, audDelSuccessFn);
}


//***When the user goes to this page, show the data table on load.
$("#auditing").live('pageshow', msAdmin.audit.getAuditData);
$("#auditing").live('pageshow', msAdmin.util.pageTransition);
