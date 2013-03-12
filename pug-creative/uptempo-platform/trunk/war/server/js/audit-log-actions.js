/* This file contains all admin audit definition actions */
uptempo.auditLog = {};
uptempo.auditLog.tableHeadings = [
  {"sTitle": "App Code", "aTargets": [0]},
  {"sTitle": "Event Code", "aTargets": [1]},
  {"sTitle": "Event Description", "aTargets": [2]},
  {"sTitle": "Remote IP", "aTargets": [3]},
  {"sTitle": "Remote User", "aTargets": [4]},
  {"sTitle": "Event Time", "aTargets": [5]},
  {"sTitle": "Action", "aTargets": [6], "mData" : null},
];

//*** Field mapping for validation and naming.
uptempo.auditLog.validFields = [
  {name: "Event Code", inputId: "#audevent-code", formVal: "eventCode", required: true},
  {name: "App Code", inputId: "#audapp-code", formVal: "appCode", required: true},
  {name: "Description", inputId: "#event-description", formVal: "eventDescription", required: true}
];

//*** Formats the audit table.
uptempo.auditLog.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row. 
  var delLink = "<a href='#' onclick=\"uptempo.audit.showDeleteConfirm('" + aData[1] + "');\">del</a>";
  $("td:eq(6)", nRow).html(delLink);
  //*** Format the date
  var logDate = new Date(aData[5]);
  $("td:eq(5)", nRow).html(logDate.toISOString());
};

uptempo.auditLog.showNew = function () {
  //*** Setup the form.	
  $("#auditLog-form-title").html("New Audit Event");
  $("#auditLog-form-submit").changeButtonText("Create this audit event");
  $("#auditLog-form-submit").off("click");
  $("#auditLog-form-submit").on("click", uptempo.auditLog.submitNew);
  $("#auditLog-form-errors").html("");
  //*** Show the form.
  $("#auditLog-form").popup("open");
}

uptempo.auditLog.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#audevent-code").val();
  //*** On success, close the submission window and reload the table.
  var auditLogSuccessFn = function() {
    $("#auditLog-form").popup("close");
    uptempo.auditLog.clearAuditLogForm();
    uptempo.auditLog.getAuditLogData();
  };

  uptempo.ajax.submitNew("AuditLog",
                         "/service/auditlog",
                         uptempo.auditLog.validFields,
                         "audevent-code",
                         key,
                         auditLogSuccessFn);
}



uptempo.auditLog.clearAuditLogForm = function() {
  $('#audapp-code').val("");  
  $('#audevent-code').val("");
  $('#event-description').val("");
}

uptempo.auditLog.getAuditLogData = function () {
  uptempo.loader.show("Getting audit log data.");
  var auditLogDataArray = ["No audit log data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/auditlog',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
    	  auditLogDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get application records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#auditLog-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="auditLog-table-data"></table>' );
      //*** Make this table the active one for row events.
      uptempo.activeTable = $('#auditLog-table-data').dataTable( {
        "aoColumnDefs": uptempo.auditLog.tableHeadings,
        "aaData" : auditLogDataArray,
        "fnRowCallback": uptempo.auditLog.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });

}

//***When the user goes to this page, show the data table on load.
$("#auditlog").live('pageshow', uptempo.auditLog.getAuditLogData);
$("#auditlog").live('pageshow', uptempo.util.pageTransition);
