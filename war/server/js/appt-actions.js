/* This file contains all appointment actions */
uptempo.appointment = {};

uptempo.appointment.init = function() {
  $("#appt-cal-date").glDatePicker(
      {zIndex: 99, selectedDate: 0, onChange: uptempo.appointment.dateChangeCallback});
  var today = new Date();
  $("#appt-cal-date").val(uptempo.util.getDateString(today));
  uptempo.office.fillDropdownWithOffices("appt-office-select", uptempo.appointment.getApptsForToday);
  //*** Bind the change event for selecting offices.
  $("#appt-office-select").on("change", function(event){
    uptempo.appointment.getApptsForDay(today);
  });
}

uptempo.appointment.dateChangeCallback = function (target, newDate) {
  var newDateString = uptempo.util.getDateString(newDate);
  target.val(newDateString);
  $(".status-bar").css("display", "none");
  uptempo.appointment.getApptsForDay(newDate);
}

//*** Field mapping for validation and naming.
uptempo.appointment.validFields =
    [{name: "Patient E-mail", inputId: "#appt-patient-user", formVal: "patientUser", required: false},
     {name: "Patient First Name", inputId: "#appt-patient-fname", formVal: "patientFName", required: false},
     {name: "Patient Last Name", inputId: "#appt-patient-lname", formVal: "patientLName", required: false},
     {name: "Patient Phone Number", inputId: "#appt-patient-phone", formVal: "patientPhone", required: false},
     {name: "Doctor E-mail", inputId: "#appt-doctor", formVal: "apptDoctor", required: true},
     {name: "Appointment Status", inputId: "#appt-status", formVal: "status", required: true},
     {name: "Description", inputId: "#appt-description", formVal: "description", required: false},
     {name: "Notes", inputId: "#appt-notes", formVal: "notes", required: false},
     {name: "Appointment Date", inputId: "#appt-date", formVal: "apptDate", required: true},
     {name: "Start Time", inputId: "#appt-start-time", formVal: "apptStart", required: true},
     {name: "End Time", inputId: "#appt-end-time", formVal: "apptEnd", required: true},
     {name: "Office Parent", inputId: "#appt-office-select", formVal: "apptOffice", required: true}];
 
uptempo.appointment.validMultiFields =
    [{name: "Doctor E-mail", inputId: "#appt-mutli-doctor", formVal: "apptDoctor", required: true},
     {name: "Appointment Status", inputId: "#appt-mutli-status", formVal: "status", required: true},
     {name: "Description", inputId: "#appt-mutli-description", formVal: "description", required: false},
     {name: "Appointment Date", inputId: "#appt-mutli-date", formVal: "apptDate", required: true},
     {name: "Start Time", inputId: "#appt-mutli-start-time", formVal: "apptStart", required: true},
     {name: "End Time", inputId: "#appt-mutli-end-time", formVal: "apptEnd", required: true},
     {name: "Duration", inputId: "#appt-multi-length", formVal: "duration", required: true},
     {name: "Spacing", inputId: "#appt-multi-spacing", formVal: "spacing", required: true},
     {name: "Office Parent", inputId: "#appt-office-select", formVal: "apptOffice", required: true}];

uptempo.appointment.apptStatusCallback = function(list) {
  if (list.length) {
    $("#appt-multi-status").empty();
    $("#appt-status").empty();
    $.each(list, function(index, listItem) {
      $("#appt-multi-status")
          .append("<option value='" + listItem.listKey + "'>" + listItem.listValue[0] + "</option>");
      $("#appt-status")
          .append("<option value='" + listItem.listKey + "'>" + listItem.listValue[0] + "</option>");
    })
    $("#appt-multi-status").selectmenu("refresh");
    $("#appt-status").selectmenu("refresh");
  }
}

uptempo.appointment.showNew = function () {
  //*** Populate the appointment status list.
  uptempo.ajax.getStaticList(
      uptempo.commonAppCode,
      uptempo.lists.apptType,
      uptempo.appointment.apptStatusCallback);
  
  //*** Setup the form.
  $("#appt-form-title").html("New Appointment");
  $("#appt-form-submit").changeButtonText("Create this appointment");
  $("#appt-form-submit").off("click");
  $("#appt-form-submit").on("click", uptempo.appointment.submitNew);
  $("#appt-form-errors").html("");
  //*** Set the current date.
  $("#appt-date").val($("#appt-cal-date").val());
  //*** Show the form.
  $("#appt-form-single").popup("open");
}

uptempo.appointment.showMultiNew = function () {
  //*** Populate the appointment status list.
  uptempo.ajax.getStaticList(
      uptempo.commonAppCode,
      uptempo.lists.apptType,
      uptempo.appointment.apptStatusCallback);

  //*** Setup the form.
  $("#appt-multi-form-submit").off("click");
  $("#appt-multi-form-submit").on("click", uptempo.appointment.submitMulti);
  $("#appt-multi-form-errors").html("");
  //*** Set the current date.
  $("#appt-multi-date").val($("#appt-cal-date").val());
  //*** Show the form.
  $("#appt-form-multi").popup("open");
}

uptempo.appointment.clearSingleForm = function() {
  $("#appt-patient-user").val("");
  $("#appt-doctor").val("");
  $("#appt-description").val("");
  $("#appt-notes").val("");
  $("#appt-date").val("");

  $("#appt-start-hour").val("1");
  $("#appt-start-hour").selectmenu("refresh");
  $("#appt-start-min").val("00");
  $("#appt-start-min").selectmenu("refresh");
  $("#appt-start-ap").val("AM");
  $("#appt-start-ap").selectmenu("refresh");

  $("#appt-end-hour").val("1");
  $("#appt-end-hour").selectmenu("refresh");
  $("#appt-end-min").val("00");
  $("#appt-end-min").selectmenu("refresh");
  $("#appt-end-ap").val("AM");
  $("#appt-end-ap").selectmenu("refresh");
  $("#appt-start-time").val("");
  $("#appt-end-time").val("");
}

uptempo.appointment.clearMultiForm = function() {
  $("#appt-multi-doctor").val("");
  $("#appt-multi-description").val("");
  $("#appt-multi-date").val("");

  $("#appt-multi-start-hour").val("1");
  $("#appt-multi-start-hour").selectmenu("refresh");
  $("#appt-multi-start-min").val("00");
  $("#appt-multi-start-min").selectmenu("refresh");
  $("#appt-multi-start-ap").val("AM");
  $("#appt-multi-start-ap").selectmenu("refresh");

  $("#appt-multi-end-hour").val("1");
  $("#appt-multi-end-hour").selectmenu("refresh");
  $("#appt-multi-end-min").val("00");
  $("#appt-multi-end-min").selectmenu("refresh");
  $("#appt-multi-end-ap").val("AM");
  $("#appt-multi-end-ap").selectmenu("refresh");
  
  $("#appt-multi-length").val("15");
  $("#appt-multi-length").selectmenu("refresh");
  $("#appt-multi-spacing").val("0");
  $("#appt-multi-spacing").selectmenu("refresh");

  $("#appt-multi-start-time").val("");
  $("#appt-multi-end-time").val("");
}

/**
 * Callback function, used to assemble the time values when calling the generic add/update
 * functions.
 * @return true indicating assembly is successful.
 */
uptempo.appointment.assembleTimeFn = function() {
  var startDate = uptempo.util.getDateFromString($("#appt-date").val());
  var startHours = parseInt($("#appt-start-hour").val()) +
        uptempo.util.getAmPmHours($("#appt-start-ap").val());
  startDate.setHours(startHours, parseInt($("#appt-start-min").val()), 0);

  var endDate = uptempo.util.getDateFromString($("#appt-date").val());
  var endHours = parseInt($("#appt-end-hour").val()) +
        uptempo.util.getAmPmHours($("#appt-end-ap").val());
  endDate.setHours(endHours, parseInt($("#appt-end-min").val()), 0);

  $("#appt-start-time").val(startDate.getTime());
  $("#appt-end-time").val(endDate.getTime());
  return true;
}

uptempo.appointment.submitNew = function () {
  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#appt-form-single").popup("close");
    uptempo.appointment.clearSingleForm();
    var dateToShow = uptempo.util.getDateFromString($("#appt-cal-date").val());
    uptempo.appointment.getApptsForDay(dateToShow);
  };

  uptempo.ajax.submitNew("Appointment",
                         "/service/appointment",
                         uptempo.appointment.validFields,
                         "appt-date",
                         null,
                         successFn,
                         uptempo.appointment.assembleTimeFn);
}

uptempo.appointment.submitMulti = function () {
  $(".status-bar").html("");
  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#appt-form-multi").popup("close");
    uptempo.appointment.clearMultiForm();
    var dateToShow = uptempo.util.getDateFromString($("#appt-cal-date").val());
    uptempo.appointment.getApptsForDay(dateToShow);
  };
  
  //*** Assembles the start/end time appropriately.
  var timeFn = function() {    
    var startDate = uptempo.util.getDateFromString($("#appt-multi-date").val());
    var startHours = parseInt($("#appt-multi-start-hour").val()) +
          uptempo.util.getAmPmHours($("#appt-multi-start-ap").val());
    startDate.setHours(startHours, parseInt($("#appt-multi-start-min").val()), 0);
    
    var endDate = uptempo.util.getDateFromString($("#appt-multi-date").val());
    var endHours = parseInt($("#appt-multi-end-hour").val()) +
          uptempo.util.getAmPmHours($("#appt-multi-end-ap").val());
    endDate.setHours(endHours, parseInt($("#appt-multi-end-min").val()), 0);
    
    $("#appt-multi-start-time").val(startDate.getTime());
    $("#appt-multi-end-time").val(endDate.getTime());
    return true;
  }
  
  var apptLength = parseInt($("#appt-multi-length").val());
  var apptSpacing = parseInt($("#appt-multi-spacing").val());
  var startDate = uptempo.util.getDateFromString($("#appt-multi-date").val());
  var startHours = parseInt($("#appt-multi-start-hour").val()) +
      uptempo.util.getAmPmHours($("#appt-multi-start-ap").val());
  startDate.setHours(startHours, parseInt($("#appt-multi-start-min").val()), 0);
  var endDate = uptempo.util.getDateFromString($("#appt-multi-date").val());
  var endHours = parseInt($("#appt-multi-end-hour").val()) +
    uptempo.util.getAmPmHours($("#appt-multi-end-ap").val());
  endDate.setHours(endHours, parseInt($("#appt-multi-end-min").val()), 0);
  
  while (startDate < endDate) {
    var minutesAdd = apptLength;
    var currentEndDate = new Date(startDate.getTime());
    currentEndDate.setMinutes(currentEndDate.getMinutes() + minutesAdd);
    
    var apptData = "apptDoctor=" + $("#appt-multi-doctor").val() +
                   "&status=" + $("#appt-multi-status").val() +
                   "&description=" + $("#appt-multi-description").val() +
                   "&apptDate=" + $("#appt-multi-date").val() +
                   "&apptStart=" + startDate.getTime() +
                   "&apptEnd=" + currentEndDate.getTime() +
                   "&patientUser=" +
                   "&patientFName=" +
                   "&patientLName=" +
                   "&user=" + uptempo.globals.user +
                   "&apptOffice=" + $("#appt-office-select").val();
    
    //*** Submit this appointment
    $.ajax({
      type: 'POST',
      url: '/service/appointment',
      data: apptData,
      success: function(response) {
        if (response.status == "SUCCESS") {
          $(".status-bar").append("<span>Appointment insert successful.</span> <br />");
        } else {
          $(".status-bar").append("<span>Appointment insert failed.</span> <br />");
        }
      }
    });
    //*** Increment by the spacing between appointments.
    startDate = new Date(currentEndDate.getTime());
    startDate.setMinutes(startDate.getMinutes() + apptSpacing)
  }
  
  successFn();
  $(".status-bar").css("display", "block");
}

uptempo.appointment.showDeleteConfirm = function(apptKey) {
  var apptDoctor = "Doctor Unknown";
  var apptPatient = "Patient Unknown";
  
  //*** Get the appointment information.
  $.ajax({
    type: 'GET',
    url: '/service/appointment/' + apptKey,
    success: function(response) {
      if (response.status == "SUCCESS") {  
        apptDoctor = response.data.apptDoctor;
        apptPatient = response.data.patientFName + " " + response.data.patientLName;
      }
      $("#appt-doctor-delete").val(apptDoctor);
      $("#appt-patient-delete").val(apptPatient);
      $("#appt-confirm-popup-body")
          .html(
          "Are you sure you want to delete appointment for " +
          apptDoctor + " with " + apptPatient + "?");
    }
  });

  //*** Set the title and body.
  $("#appt-confirm-popup-heading").html("Delete Appointment?");
  $("#appt-confirm-popup-action").html("Delete Appointment");
  $("#appt-key-delete").val(apptKey);
  $("#appt-confirm-popup-delete").on("click", uptempo.appointment.deleteAppt);

  //*** Show the form.
  $("#appt-confirm-popup").popup("open");
}

uptempo.appointment.deleteAppt = function() {
  var apptKey = $("#appt-key-delete").val();
  var apptDoctor = $("#appt-doctor-delete").val();
  var apptPatient = $("#appt-patient-delete").val();
  var apptMessage = apptDoctor + "(patient:" + apptPatient + ")";

  //*** Define a success function.
  var successFn = function() {
    $("#appt-confirm-popup").popup("close");
    var dateToShow = uptempo.util.getDateFromString($("#appt-cal-date").val());
    uptempo.appointment.getApptsForDay(dateToShow);
  };
  uptempo.ajax.submitDelete(apptKey, "/service/appointment/", "Appointment", apptMessage, successFn); 
}

uptempo.appointment.showUpdate = function(apptKey) {
  $("#appt-form-title").html("Update Appointment");

  //*** Populate the appointment status list.
  uptempo.ajax.getStaticList(
      uptempo.commonAppCode,
      uptempo.lists.apptType,
      uptempo.appointment.apptStatusCallback);
  
  //*** Submit the XHR request
  $.ajax({
    type: 'GET',
    url: '/service/appointment/' + apptKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        $("#appt-patient-user").val(response.data.patientUser);
        $("#appt-patient-fname").val(response.data.patientFName);
        $("#appt-patient-lname").val(response.data.patientLName);
        $("#appt-patient-phone").val(response.data.patientPhone);
        $("#appt-doctor").val(response.data.apptDoctor);
        $("#appt-status").val(response.data.status);
        $("#appt-status").selectmenu("refresh");
        $("#appt-description").val(response.data.description);
        $("#appt-notes").val(response.data.apptNotes);
        $("#appt-date").val(response.data.apptDate);
        var apptStart = new Date(response.data.apptStart);
        var apptEnd = new Date(response.data.apptEnd);
        var apptStartHours =
            apptStart.getHours() < 13 ? apptStart.getHours() : (apptStart.getHours() - 12);
        var apptEndHours =
            apptEnd.getHours() < 13 ? apptEnd.getHours() : (apptEnd.getHours() - 12);
        $("#appt-start-hour").val(apptStartHours);
        $("#appt-start-hour").selectmenu("refresh");
        $("#appt-start-min").val(apptStart.getMinutes());
        $("#appt-start-min").selectmenu("refresh");
        $("#appt-start-ap").val(uptempo.util.getAmPmFromHours(apptStart.getHours()));
        $("#appt-start-ap").selectmenu("refresh");
        $("#appt-end-hour").val(apptEndHours);
        $("#appt-end-hour").selectmenu("refresh");
        $("#appt-end-min").val(apptEnd.getMinutes());
        $("#appt-end-min").selectmenu("refresh");
        $("#appt-end-ap").val(uptempo.util.getAmPmFromHours(apptEnd.getHours()));
        $("#appt-end-ap").selectmenu("refresh");
        $("#appt-key").val(apptKey);
      } else {
        alert(response.message);
      }
      uptempo.loader.hide();
    }
  });
  
  //*** Setup the form.
  $("#appt-form-title").html("Update Appointment");
  $("#appt-form-submit").changeButtonText("Update this appointment");
  $("#appt-form-submit").off("click");
  $("#appt-form-submit").on("click", uptempo.appointment.submitUpdate);
  $("#appt-form-errors").html("");
  //*** Show the form.
  $("#appt-form-single").popup("open");
}

uptempo.appointment.submitUpdate = function() {
  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#appt-form-single").popup("close");
    uptempo.appointment.clearSingleForm();
    var dateToShow = uptempo.util.getDateFromString($("#appt-cal-date").val());
    uptempo.appointment.getApptsForDay(dateToShow);
  };

  //*** Set the key for submission.
  var apptKey = $("#appt-key").val();
  uptempo.ajax.submitUpdate("Appointment",
                            "/service/appointment/" + apptKey,
                            uptempo.appointment.validFields,
                            "appt-date",
                            successFn,
                            uptempo.appointment.assembleTimeFn);
}

uptempo.appointment.getApptsForToday = function() {
  var today = new Date();
  uptempo.appointment.getApptsForDay(today);
}

uptempo.appointment.getApptsForDay = function(day) {
  var officeKey = $("#appt-office-select").val();
  $("#appt-office-id").html(officeKey);
  
  var dateString = uptempo.util.getDateString(day);
  var submitParams = "?apptStartDay=" + dateString +
                     "&apptEndDay=" + dateString +
                     "&apptOffice=" + officeKey;
  uptempo.loader.show("Loading appointments for " + dateString);
  $("#appt-day-table").html("");
  $("#appt-day-table").append("<tr><th>Time</th><th>Appointments</th></tr>\n");
  //$("#appt-day-table").find("tr:gt(0)").remove();
  $.ajax({
      type: 'GET',
      url: '/service/appointment' + submitParams,
      success: function(response) {
        if (response.status == "SUCCESS") {
          //*** Loop through the returned appointments.
          //*** And draw the table for them.
          var appointments = response.data.values;
          for(var appt in appointments) {
            var tableRow = "<tr>"
            var apptStartDate = new Date(appointments[appt].apptStart);
            var apptEndDate = new Date(appointments[appt].apptEnd);
            tableRow += 
                "<td>" +
                apptStartDate.toLocaleTimeString() +
                " - " +
                apptEndDate.toLocaleTimeString() +
                "</td>";
            var patientDisplay = "Open";
            if (appointments[appt].patientFName &&
                appointments[appt].patientFName) {
                patientDisplay = 
                    appointments[appt].patientFName + " " +
                    appointments[appt].patientLName;
            }
            var apptDisplay = appointments[appt].apptDoctor + "(" + patientDisplay +  ")";
            if (appointments[appt].googleApptId != null && appointments[appt].googleApptId != "") {
              apptDisplay += "<span class='appt-gcal'> -GCAL- </span>"
            }
            var apptActions =
                "<a href='#' onclick=\"uptempo.appointment.showUpdate('" +
                appointments[appt].key +
                "')\">update</a> &nbsp;" +
                "<a href='#'>copy</a> &nbsp;" +
                "<a href='#' onclick=\"uptempo.appointment.showDeleteConfirm('" +
                appointments[appt].key +
                "')\">delete</a>";
            var apptDisplayClass;
            switch(appointments[appt].status) {
              case "AVAILABLE":
                apptDisplayClass = "appt-av";
                break;
              case "RESERVED":
                apptDisplayClass = "appt-re";
                break;
              case "SCHEDULED":
                apptDisplayClass = "appt-sc";
                break;
              case "CANCELLED":
                apptDisplayClass = "appt-ca";
                break;
            }

            tableRow += "<td class='" + apptDisplayClass + "'>" +
              apptDisplay + "&nbsp;" + apptActions + "</td></tr>";
            $('#appt-day-table > tbody:last').append(tableRow);
          }
        } else {
          alert(response.message);
        }
        uptempo.loader.hide();
      }
    });
}

//***When the user goes to this page, show the data table on load.
$("#appointment").live('pageshow', uptempo.appointment.init);

