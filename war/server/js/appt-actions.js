/* This file contains all appointment actions */
uptempo.appointment = {};
uptempo.appointment.displaySize = 0;

uptempo.appointment.init = function() {
  $("#appt-cal-date").glDatePicker(
      {zIndex: 99, selectedDate: 0, onChange: uptempo.appointment.dateChangeCallback});
  var today = new Date();
  $("#appt-cal-date").val(uptempo.util.getDateString(today));
  uptempo.office.fillDropdownWithOffices("appt-office-select", uptempo.appointment.setOffice);
  //*** Bind the change event for selecting offices.
  $("#appt-office-select").on("change", function(event){
    uptempo.appointment.setOffice();
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
     {name: "Doctor E-mail", inputId: "#appt-doctor", formVal: "apptDoctorKey", required: true},
     {name: "Appointment Status", inputId: "#appt-status", formVal: "status", required: true},
     {name: "Description", inputId: "#appt-description", formVal: "description", required: false},
     {name: "Notes", inputId: "#appt-notes", formVal: "notes", required: false},
     {name: "Appointment Date", inputId: "#appt-date", formVal: "apptDate", required: true},
     {name: "Start Hour", inputId: "#appt-start-hr", formVal: "apptStartHr", required: true},
     {name: "Start Minute", inputId: "#appt-start-min", formVal: "apptStartMin", required: true},
     {name: "End Hour", inputId: "#appt-end-hr", formVal: "apptEndHr", required: true},
     {name: "End Min", inputId: "#appt-end-min", formVal: "apptEndMin", required: true},
     {name: "Office Parent", inputId: "#appt-office-select", formVal: "apptOffice", required: true}];
 
uptempo.appointment.validMultiFields =
    [{name: "Doctor E-mail", inputId: "#appt-mutli-doctor", formVal: "apptDoctorKey", required: true},
     {name: "Appointment Status", inputId: "#appt-mutli-status", formVal: "status", required: true},
     {name: "Description", inputId: "#appt-mutli-description", formVal: "description", required: false},
     {name: "Appointment Date", inputId: "#appt-mutli-date", formVal: "apptDate", required: true},
     {name: "Start Hour", inputId: "#appt-mutli-start-hr", formVal: "apptStartHr", required: true},
     {name: "Start Min", inputId: "#appt-mutli-start-min", formVal: "apptStartMin", required: true},
     {name: "End Hour", inputId: "#appt-mutli-end-hr", formVal: "apptEndHr", required: true},
     {name: "End Min", inputId: "#appt-mutli-end-min", formVal: "apptEndMin", required: true},
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

  //*** Populate the doctor list.
  uptempo.doctor.fillDropdownWithDoctors("appt-doctor", $("#appt-office-select").val());

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

  //*** Populate the doctor list.
  uptempo.doctor.fillDropdownWithDoctors("appt-multi-doctor", $("#appt-office-select").val());

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

  $("#appt-start-hour").val("7");
  $("#appt-start-hour").selectmenu("refresh");
  $("#appt-start-min").val("00");
  $("#appt-start-min").selectmenu("refresh");
  $("#appt-start-ap").val("AM");
  $("#appt-start-ap").selectmenu("refresh");

  $("#appt-end-hour").val("7");
  $("#appt-end-hour").selectmenu("refresh");
  $("#appt-end-min").val("00");
  $("#appt-end-min").selectmenu("refresh");
  $("#appt-end-ap").val("AM");
  $("#appt-end-ap").selectmenu("refresh");
  $("#appt-start-hr").val("");
  $("#appt-end-hr").val("");
}

uptempo.appointment.clearMultiForm = function() {
  $("#appt-multi-doctor").val("");
  $("#appt-multi-description").val("");
  $("#appt-multi-date").val("");

  $("#appt-multi-start-hour").val("7");
  $("#appt-multi-start-hour").selectmenu("refresh");
  $("#appt-multi-start-min").val("00");
  $("#appt-multi-start-min").selectmenu("refresh");
  $("#appt-multi-start-ap").val("AM");
  $("#appt-multi-start-ap").selectmenu("refresh");

  $("#appt-multi-end-hour").val("7");
  $("#appt-multi-end-hour").selectmenu("refresh");
  $("#appt-multi-end-min").val("00");
  $("#appt-multi-end-min").selectmenu("refresh");
  $("#appt-multi-end-ap").val("AM");
  $("#appt-multi-end-ap").selectmenu("refresh");
  
  $("#appt-multi-length").val("15");
  $("#appt-multi-length").selectmenu("refresh");
  $("#appt-multi-spacing").val("0");
  $("#appt-multi-spacing").selectmenu("refresh");
}

uptempo.appointment.selectAll = function() {

}

/**
 * Callback function, used to assemble the time values when calling the generic add/update
 * functions.
 * @return true indicating assembly is successful.
 */
uptempo.appointment.assembleTimeFn = function() {
  var startHours = parseInt($("#appt-start-hour").val()) +
        uptempo.util.getAmPmHours($("#appt-start-ap").val());

  var endHours = parseInt($("#appt-end-hour").val()) +
        uptempo.util.getAmPmHours($("#appt-end-ap").val());

  $("#appt-start-hr").val(startHours);
  $("#appt-end-hr").val(endHours);
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

uptempo.appointment.batchDelete = function () {
  //*** Calculate the batch delete size for display.
  uptempo.appointment.batchDeleteSize = 0;
  uptempo.appointment.batchDeleted = 0;
  for (var i = 0; i < uptempo.appointment.displaySize; i++) {
    if ($("#appt-sel-" + i).is(":checked")) {
      uptempo.appointment.batchDeleteSize++;
    }
  }
  for (var i = 0; i < uptempo.appointment.displaySize; i++) {
    if ($("#appt-sel-" + i).is(":checked")) {
      var apptKey = $("#appt-sel-v-" + i).val();

      var successFn = function() {
        uptempo.appointment.batchDeleted++;
        $(".status-bar").html("Deleted " +
                              uptempo.appointment.batchDeleted + " out of " +
                              uptempo.appointment.batchDeleteSize + " apppointments.");
        if (uptempo.appointment.batchDeleted == uptempo.appointment.batchDeleteSize) {
          uptempo.appointment.getApptsForToday();
        }
      };
      uptempo.ajax.submitDelete(apptKey, "/service/appointment/", "Appointment", "", successFn);
    }
  } 
}

uptempo.appointment.setOffice = function() {
  $.ajax({
    type: 'GET',
    url: '/service/billingoffice/' + $("#appt-office-select").val(),
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var officeTimeZone = response.data.officeTimeZone;
        $("#appt-office-tz").val(officeTimeZone);
        uptempo.appointment.getApptsForToday();
      } else {
        $(".status-bar").html("Failed to get Billing Office records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
   });
}

uptempo.appointment.submitMulti = function () {
  $(".status-bar").html("");
  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#appt-form-multi").popup("close");
    uptempo.appointment.clearMultiForm();
  };

  //*** Get the number of days in a row to schedule.
  var numberOfDays = parseInt($("#appt-multi-days").val());
  var utcWeekdays = [0, 1, 1, 1, 1, 1, 0];
  var utcWeekends = [1, 0, 0, 0, 0, 0, 1];
  var useWeekends = $("#appt-multi-weekends").is(":checked");
  var useWeekdays = $("#appt-multi-weekdays").is(":checked");
  var batchStartDate = uptempo.util.getDateFromString($("#appt-multi-date").val());
  var currentDay = 1;
  var apptLength = parseInt($("#appt-multi-length").val());
  var apptSpacing = parseInt($("#appt-multi-spacing").val());
  var totalApptCount = 0;
  while (currentDay <= numberOfDays) {
    //*** Do the weekday/weekend checks first.
    var dayOfWeek = batchStartDate.getDay();
    var weekendMatch = utcWeekends[dayOfWeek] && useWeekends;
    var weekdayMatch = utcWeekdays[dayOfWeek] && useWeekdays;
    //*** Increment by a day until the weekend or weekday match is encountered.
    while (!weekendMatch && !weekdayMatch) {
      currentDay++;
      batchStartDate.setTime(batchStartDate.getTime() + 86400000);
      dayOfWeek = batchStartDate.getDay();
      weekendMatch = utcWeekends[dayOfWeek] && useWeekends;
      weekdayMatch = utcWeekdays[dayOfWeek] && useWeekdays;
    }
    
    var startDate = new Date(batchStartDate.getTime());
    var startHours = parseInt($("#appt-multi-start-hour").val()) +
        uptempo.util.getAmPmHours($("#appt-multi-start-ap").val());
    if (startHours == 24) {startHours = 12;}
    startDate.setHours(startHours, parseInt($("#appt-multi-start-min").val()), 0);
    var endDate = new Date(batchStartDate.getTime());
    var endHours = parseInt($("#appt-multi-end-hour").val()) +
      uptempo.util.getAmPmHours($("#appt-multi-end-ap").val());
    if (endHours == 24) {endHours = 12;}
    endDate.setHours(endHours, parseInt($("#appt-multi-end-min").val()), 0);

    //*** Use the office offset to adjust the start date/end date.
    var officeTz = $("#appt-office-tz").val();

    //*** Calculate the total number of appointments to be submitted.
    var cStartDate = startDate;
    var cEndDate = endDate;
    uptempo.appointment.batchCount = 0;
    uptempo.appointment.batchCreated = 0;
    uptempo.appointment.currentDay = currentDay;
    while (cStartDate < cEndDate) {
      var minutesAdd = apptLength;
      var currentEndDate = new Date(cStartDate.getTime());
      currentEndDate.setMinutes(currentEndDate.getMinutes() + minutesAdd);
      uptempo.appointment.batchCount++;
      totalApptCount++;
      cStartDate = new Date(currentEndDate.getTime());
      cStartDate.setMinutes(cStartDate.getMinutes() + apptSpacing);
    }

    while (startDate < endDate) {
      var minutesAdd = apptLength;
      var currentEndDate = new Date(startDate.getTime());
      var startHr = startDate.getHours();
      var startMin = startDate.getMinutes();
      startDate.setMinutes(currentEndDate.getMinutes() + minutesAdd);
      var endHr = startDate.getHours();
      var endMin = startDate.getMinutes();

      var apptData = "apptDoctorKey=" + $("#appt-multi-doctor").val() +
                     "&status=" + $("#appt-multi-status").val() +
                     "&description=" + $("#appt-multi-description").val() +
                     "&apptDate=" + uptempo.util.getDateString(startDate) +
                     "&apptStartHr=" + startHr +
                     "&apptEndHr=" + endHr +
                     "&apptStartMin=" + startMin +
                     "&apptEndMin=" + endMin +
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
            uptempo.appointment.batchCreated++;
            var apptNumDisplay = "Day " + uptempo.appointment.currentDay + " out of " +
                                 numberOfDays + ". " + uptempo.appointment.batchCreated +
                                 " appointments created out of " + totalApptCount;
            $(".status-bar")
                .html("<span>Appointment insert successful. " + apptNumDisplay + " </span> <br />");
            if (uptempo.appointment.batchCreated == uptempo.appointment.batchCount) {
              var dateToShow = uptempo.util.getDateFromString($("#appt-cal-date").val());
              uptempo.appointment.getApptsForDay(dateToShow);
            }
          } else {
            var apptNumDisplay = "Day " + uptempo.appointment.currentDay + " out of " +
                                 numberOfDays + ". " + uptempo.appointment.batchCreated +
                                 " appointments created.";
            $(".status-bar")
                .html("<span>Appointment insert failed. " + apptNumDisplay + " </span> <br />");
          }
        }
      });
      //*** Increment by the spacing between appointments.
      startDate.setMinutes(startDate.getMinutes() + apptSpacing);
    }
    currentDay++;
    //*** Add a day.
    batchStartDate.setTime(batchStartDate.getTime() + 86400000);
  } //*** End number of days loop.

  successFn();
  $(".status-bar").css("display", "block");
}

uptempo.appointment.showDeleteConfirm = function(apptKey) {
  var apptDoctor = "Doctor Unknown";
  var apptPatient = "Patient Unknown";
  
  //*** Get the appointment information.
  $.ajax({
    type: 'GET',
    url: '/service/appointment/' + apptKey + "?l=TRUE",
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
  
  //*** Populate the doctor list.
  uptempo.doctor.fillDropdownWithDoctors("appt-doctor", $("#appt-office-select").val());

  //*** Submit the XHR request
  $.ajax({
    type: 'GET',
    url: '/service/appointment/' + apptKey + "?l=true",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        $("#appt-patient-user").val(response.data.patientUser);
        $("#appt-patient-fname").val(response.data.patientFName);
        $("#appt-patient-lname").val(response.data.patientLName);
        $("#appt-patient-phone").val(response.data.patientPhone);
        $("#appt-doctor").val(response.data.apptDoctorKey);
        $("#appt-status").val(response.data.status);
        $("#appt-status").selectmenu("refresh");
        $("#appt-description").val(response.data.description);
        $("#appt-notes").val(response.data.apptNotes);
        $("#appt-date").val(response.data.apptDate);
        var apptStartHr = response.data.apptStartHr;
        var apptEndHr = response.data.apptEndHr;
        var apptStartHours =
            apptStartHr < 13 ? apptStartHr : (apptStartHr - 12);
        var apptEndHours =
            apptEndHr < 13 ? apptEndHr : (apptEndHr - 12);
        $("#appt-start-hour").val(apptStartHours);
        $("#appt-start-hour").selectmenu("refresh");
        $("#appt-start-min").val(response.data.apptStartMin);
        $("#appt-start-min").selectmenu("refresh");
        $("#appt-start-ap").val(uptempo.util.getAmPmFromHours(apptStartHr));
        $("#appt-start-ap").selectmenu("refresh");
        $("#appt-end-hour").val(apptEndHours);
        $("#appt-end-hour").selectmenu("refresh");
        $("#appt-end-min").val(response.data.apptEndMin);
        $("#appt-end-min").selectmenu("refresh");
        $("#appt-end-ap").val(uptempo.util.getAmPmFromHours(apptEndHr));
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

  //*** Set the start/end hours.
  var startHr = $("#appt-start-hour").val() + uptempo.util.getAmPmHours($("#appt-start-ap").val());
  var endHr = $("#appt-end-hour").val() + uptempo.util.getAmPmHours($("#appt-end-ap").val());
  $("#appt-start-hr").val(startHr);
  $("#appt-end-hr").val(endHr);

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
                     "&apptOffice=" + officeKey +
                     "&showPatients=TRUE";
  uptempo.loader.show("Loading appointments for " + dateString);
  $("#appt-day-table").html("");
  $("#appt-day-table").append("<tr><th><input type='checkbox' id='appt-check-all' /></th>" +
                              "<th>Time</th>" +
                              "<th>Appointments</th>" +
                              "</tr>\n");
  $.ajax({
      type: 'GET',
      url: '/service/appointment' + submitParams,
      success: function(response) {
        if (response.status == "SUCCESS") {
          //*** Get the timezone offset differentce from the office.
          var officeTz = $("#appt-office-tz").val();
          //*** Loop through the returned appointments.
          //*** And draw the table for them.
          var appointments = response.data.values;
          uptempo.appointment.displaySize = appointments.length;
          for (var appt in appointments) {
            var tableRow = "<tr>"
            var apptStartDate = uptempo.util.getDateDisplay(
                appointments[appt].apptStartHr, appointments[appt].apptStartMin);
            var apptEndDate = uptempo.util.getDateDisplay(
                appointments[appt].apptEndHr, appointments[appt].apptEndMin);
            var officeTz = $("#appt-office-tz").val();
            tableRow += "<td><input type='checkbox' id='appt-sel-" + appt + "' />" +
                        "<input type='hidden' id='appt-sel-v-" + appt + "' value='" +
                        appointments[appt].key + "'></td>"
            tableRow += 
                "<td>" +
                apptStartDate +
                " - " +
                apptEndDate +
                "(GMT" + officeTz + ")</td>";
            var patientDisplay = "Open";
            if (appointments[appt].status == "HELD") {
              patientDisplay = "HELD";
            }        
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
                "<a href='#' onclick=\"uptempo.appointment.showDeleteConfirm('" +
                appointments[appt].key +
                "')\">delete</a>";
            var apptDisplayClass;
            switch(appointments[appt].status) {
              case "AVAILABLE":
                apptDisplayClass = "appt-av";
                break;
              case "HELD":  //*** Previously: RESERVED.
                apptDisplayClass = "appt-re";
                break;
              case "RESERVED":  //*** Previously: SCHEDULED.
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
    $("#appt-check-all").on("change", function(event) {
      for (var i = 0; i < uptempo.appointment.displaySize; i++) {
        $("#appt-sel-" + i).attr("checked", $("#appt-check-all").is(":checked"));
      }
    })
}

//***When the user goes to this page, show the data table on load.
$("#appointment").live('pageshow', uptempo.appointment.init);

