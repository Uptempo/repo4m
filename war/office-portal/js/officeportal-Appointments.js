uptempo.officePortal.util = {};
uptempo.officePortal.util.alert = function (message, title) {
	title = title || "Warning";
	$("#alert-title").html(title);
    $('#uptempo-alert #message p').html(message);
    $('#uptempo-alert').modal('show');
};

uptempo.officePortal.appointments = {};
uptempo.officePortal.appointments.createOfficesList = function (groupKey) {
    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/billingoffice',
        data: 'officeGroup=' + groupKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if (typeof response.data !== 'undefined') {
                    $("#appt-offices-list").empty();
                    for (var office in response.data.values) {
                        $("#appt-offices-list").append('<li data-id="' + response.data.values[office]['key'] + '" class=""><a href="#" onclick="javascript:uptempo.officePortal.appointments.getDoctorsList(\'' + response.data.values[office]['key'] + '\',\''+ response.data.values[office]['officeTimeZone'] + '\');">Office ' + response.data.values[office]['officeName'] + '</a></li>');
                    }
                    $("#appt-offices-list").find('a').each(function () {
                        if ($(this).parent().data('id') === response.data.values[0]['key']) {
                            $(this).parent().addClass('active');
                            uptempo.officePortal.appointments.getDoctorsList(response.data.values[0]['key']);
                        }
                    });

                } else {
                    uptempo.officePortal.util.alert('There are no offices for the group required');
                }

            } else {

            }

        }
    });
}

uptempo.officePortal.appointments.getDoctorsList = function (officeKey, officeTimeZone) {
    uptempo.officePortal.appointments.officeKey = officeKey;
    uptempo.officePortal.appointments.officeTimeZone = officeTimeZone;
    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/doctor',
        data: 'billingOffice=' + officeKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if ((typeof response.data.values !== 'undefined') && (response.data.values.length > 0)) {
                    $("#appt-doctors-list").empty();
                    for (var doc in response.data.values) {
                        $("#appt-doctors-list").append('<option value="' + response.data.values[doc]['key'] + '">' + response.data.values[doc]['title'] + ' ' + response.data.values[doc]['firstName'] + ' ' + response.data.values[doc]['lastName'] + '</option>');
                    }
                    uptempo.officePortal.appointments.getDoctorAllAppointments(response.data.values[0]['key'])
                    $("#appt-offices-list").find('li').removeClass('active');
                    $("#appt-offices-list").find('a').each(function () {
                        if ($(this).parent().data('id') === officeKey) {
                            $(this).parent().addClass('active');
                        }
                    });

                } else {
                    uptempo.officePortal.util.alert('There are no doctors for the group required');
                }

            } else {

            }

        }
    });
}

uptempo.officePortal.appointments.getDoctorAllAppointments = function (doctorKey) {
    $('#loading-indicator').fadeIn();
    uptempo.officePortal.appointments.doctorKey = doctorKey;
    var events = {};
    var endDate = new Date();
    endDate.setDate(endDate.getDate() + 60);
    var dd = endDate.getDate();
    //var dd = ('0' + endDate.getDate()).slice(-2);
	var mm = endDate.getMonth() + 1;
    //var mm = ('0' + endDate.getMonth()+1).slice(-2);
    var y = endDate.getFullYear();

    $("#appointments_date_picker").datepicker({
        numberOfMonths: 2,
        onSelect: function (date) {
            uptempo.officePortal.appointments.getDoctorAppointments(uptempo.officePortal.appointments.doctorKey)
        }
    });

    var formattedEndDate = mm + '/' + dd + '/' + y;
    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/appointment',
        data: 'apptDoctor=' + doctorKey + '&apptEndDay=' + formattedEndDate,
        cache: false,
        success: function (response) {
            if (response.status == "SUCCESS") {
            $("#appointments_date_picker").find(".active-day").each(function(){
	            $(this).removeClass("active-day");
            })
                if ((typeof response.data.values !== 'undefined') && (response.data.values.length > 0)) {
                    for (var day in response.data.values) {
                        events[new Date(response.data.values[day]['apptDate'])] = new Event(response.data.values[day]['key']);
                    }
                    $("#appointments_date_picker").datepicker( "option", "beforeShowDay", function (date) {
                            var event = events[date];
                            if (event) {
                                return [true, 'active-day', event.text];
                            } else {
                                return [true, '', ''];
                            }
                        });
                    uptempo.officePortal.appointments.getDoctorAppointments(uptempo.officePortal.appointments.doctorKey);
                } else {
                    $('#loading-indicator').fadeOut();
                    $("#appointments-table tbody").empty();
                    uptempo.officePortal.util.alert('There are no appointments for this doctor');
                    $("#appointments_date_picker").datepicker( "option", "beforeShowDay", function (date) {
                            var event = events[date];
                            if (event) {
                                return [true, 'active-day', event.text];
                            } else {
                                return [true, '', ''];
                            }
                        });
                }
            }
        },
        error: function (e) {
            $('#loading-indicator').fadeOut();
            uptempo.appointments.util.alert(e, "Error!");
        }
    });
}


uptempo.officePortal.appointments.getDoctorAppointments = function (doctorKey) {
    $('html, body').animate({scrollTop:0});
    $('#loading-indicator').fadeIn();
                                   
    uptempo.officePortal.appointments.doctorKey = doctorKey;
    var date = $("#appointments_date_picker").datepicker("getDate");
    //var date2 = $("#appointments_date_picker").datepicker("getDate", "+1d");
    //date2.setDate(date2.getDate() + 1);
    if (date !== null) {
        date = $.datepicker.formatDate('m/d/yy', date);
        //date2 = $.datepicker.formatDate('mm/dd/yy', date2);
    }
        $.ajax({
        type: 'GET',
        url: '/service/appointment',
        data: 'apptDoctor=' + doctorKey + '&apptStartDay=' + date + '&apptEndDay=' + date+"&showPatients=TRUE",
        cache: false,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if ((typeof response.data.values !== 'undefined') && (response.data.values.length > 0)) {
                    $("#appointments-table tbody").empty();
                    for (var appt in response.data.values) {
	                    var apptStartDate = uptempo.util.getDateDisplay(response.data.values[appt]['apptStartHr'], response.data.values[appt]['apptStartMin']);
			            var apptEndDate = uptempo.util.getDateDisplay(response.data.values[appt]['apptEndHr'], response.data.values[appt]['apptEndMin']);
                    
                        var patient = '';
                        if (typeof response.data.values[appt]['patientFName'] !== 'undefined') {
                            patient = response.data.values[appt]['patientFName'] + ' ' + response.data.values[appt]['patientLName'];
                        }
                        var phone = '';
                        if (typeof response.data.values[appt]['patientPhone'] !== 'undefined') {
                            phone = response.data.values[appt]['patientPhone'];
                        }
                        var email = '';
                        if (typeof response.data.values[appt]['patientUser'] !== 'undefined') {
                            email = response.data.values[appt]['patientUser'];
                        }
                        var source = '';
                        if (typeof response.data.values[appt]['source'] !== 'undefined') {
                            source = response.data.values[appt]['source'];
                        }
						$("#appointments-table").append('<tr class="odd gradeX"><td><input type="checkbox" class="checkboxes" data-id="'+response.data.values[appt]['key']+'" /></td><td class="center hidden-phone">' + response.data.values[appt]['apptDate'] + ' ' + apptStartDate +' - ' + apptEndDate + '</td><td>' + response.data.values[appt]['apptDoctor'] + '</td><td>'+response.data.values[appt]['status']+'</td><td>' + patient + '</td><td>' + phone + '</td><td>' + email + '</td><td>' + source + '</td><td><button class="btn btn-small btn-primary" onclick="javascript:uptempo.officePortal.appointments.showDetails(\'' + response.data.values[appt]['key'] + '\');"><i class="icon-pencil icon-white"></i> Edit</button><button class="btn btn-small btn-danger" onclick="javascript:uptempo.officePortal.appointments.delete(\'' + response.data.values[appt]['key'] + '\');"><i class="icon-remove icon-white"></i> Delete</button></td></tr>');
                        //<button class="btn btn-small btn-info"><i class="icon-ban-circle icon-white"></i> Cancel</button>
                    }
                    $('#loading-indicator').fadeOut();

                } else {
                    $("#appointments-table tbody").empty();
                    $('#loading-indicator').fadeOut();
                }

            }
        },
        error: function (e) {
            uptempo.officePortal.util.alert(e)
        }
    });
}

uptempo.officePortal.appointments.showDetails = function (apptKey) {
    $('#modal-appt-details').modal('show');
    $.ajax({
        type: 'GET',
        url: '/service/appointment/' + apptKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if (typeof response.data !== 'undefined') {
                    for (var i in uptempo.appointment.validFields) {
                        $(uptempo.appointment.validFields[i].inputId).val(response.data[uptempo.appointment.validFields[i].formVal]);
                    }
                    var apptStartHr = response.data['apptStartHr'];
			        var apptEndHr = response.data['apptEndHr'];
			        var apptStartHours =
			            apptStartHr < 13 ? apptStartHr : (apptStartHr - 12);
			        var apptEndHours =
			            apptEndHr < 13 ? apptEndHr : (apptEndHr - 12);
			        $("#appt-start-hour").val(apptStartHours);
			        $("#appt-start-ap").val(uptempo.util.getAmPmFromHours(apptStartHr));
			        $("#appt-end-hour").val(apptEndHours);
			        $("#appt-end-ap").val(uptempo.util.getAmPmFromHours(apptEndHr));
                    
                    $("#appt-office-select").val(response.data['ancestor']);
					$("#update-confirmed").html('Update Appointment');
					$("#update-confirmed").off('click');
                    $("#update-confirmed").click(function () {
                        uptempo.officePortal.appointments.update(apptKey)
                    });

                } else {
                    uptempo.officePortal.util.alert('Some problem occured');
                }
            }
        },
        error: function (e) {
            uptempo.appointments.util.alert(e);
        }
    });
}

uptempo.officePortal.appointments.update = function (apptKey) {
    var validationResult = uptempo.ajax.validateInput(uptempo.appointment.validFields)

    if (validationResult.isValid) {
        uptempo.appointment.assembleTimeFn();
        var formData = uptempo.ajax.consructPostString(uptempo.appointment.validFields);
        if(typeof $("#appt-source").val() !== 'undefined'){
            formData += "&source="+$("#appt-source").val();
        }
        //*** Submit the XHR request
        $.ajax({
            type: 'PUT',
            url: "/service/appointment/" + apptKey,
            data: formData,
            success: function (response) {
                //*** If the response was sucessful, show the success indicator.
                if (response.status === "SUCCESS") {
                    uptempo.officePortal.appointments.getDoctorAppointments(uptempo.officePortal.appointments.doctorKey);
                } else {
                    console.log("Failed to add " +
                        response.message);
                }
            }
        });
        //close the modal
    } else {
        var message = "";
        if (validationResult.errorMessage != "") {
            message = validationResult.errorMessage;
            uptempo.officePortal.util.alert(message)
        }
    }
}

uptempo.officePortal.appointments.delete = function (apptKey) {
    $('#modal-appt-delete').modal('show');
    $("#delete-appt-confirmed").click(function () {
        //*** Get the data from the server.
        $.ajax({
            type: 'DELETE',
            url: '/service/appointment/' + apptKey,
            success: function (response) {
                if (response.status == "SUCCESS") {
                    uptempo.officePortal.util.alert('Appointment Deleted');
                    uptempo.officePortal.appointments.getDoctorAppointments(uptempo.officePortal.appointments.doctorKey);
                } else {
                    uptempo.officePortal.util.alert('Some problem occured while deleting the appointment');
                }
            },
            error: function (e) {
                uptempo.appointments.util.alert(e);
            }
        });
    })
}

uptempo.officePortal.appointments.addAppt = function () {
    $('#loading-indicator').fadeIn();
    var validationResult = uptempo.ajax.validateInput(uptempo.appointment.validFields)
    if (validationResult.isValid){
        uptempo.appointment.assembleTimeFn();
        var formData = uptempo.ajax.consructPostString(uptempo.appointment.validFields);
        //console.log(formData)
        if((typeof $("#appt-source").val() !== 'undefined')&&($("#appt-source").val() !== '')){
            formData += "&source="+$("#appt-source").val();
        }
       
		    $.ajax({
		        type: 'POST',
		        url: '/service/appointment',
		        data: formData,
		        success: function (response) {
		            if (response.status == "SUCCESS") {
		                //console.log(response.data)
                        console.log(uptempo.officePortal.appointments.doctorKey);
                        uptempo.officePortal.appointments.getDoctorAllAppointments(uptempo.officePortal.appointments.doctorKey);
                        $('#loading-indicator').fadeOut();

		            }
		        },
		        error: function (e) {
		            uptempo.appointments.util.alert(e);
		        }
		    });

        } else {
        var message = "";
        if (validationResult.errorMessage != "") {
            message = validationResult.errorMessage;
            uptempo.officePortal.util.alert(message);
            $('#uptempo-alert').on('hidden', function() {
			    $('#modal-appt-details').modal('show');
			    $('#uptempo-alert').off('hidden');
			});
        }
    }
}

uptempo.officePortal.appointments.addApptForm = function () {
	uptempo.officePortal.appointments.clearApptDetails();

    var date = $("#appointments_date_picker").datepicker("getDate");
    if (date !== null) {
        //date = $.datepicker.formatDate('mm/dd/yy', date);
        date = $.datepicker.formatDate('m/d/yy', date);
    } 	
    $("#appt-date").val(date);
	$("#appt-office-select").val(uptempo.officePortal.appointments.officeKey);
    $("#appt-doctor").val(uptempo.officePortal.appointments.doctorKey);

    $('#modal-appt-details').modal('show');
	$("#update-confirmed").html('Add Appointment');
	$("#update-confirmed").off('click');
    $("#update-confirmed").click(function () {
        uptempo.officePortal.appointments.addAppt()
    });
}

uptempo.officePortal.appointments.clearApptDetails = function () {
    $('#modal-appt-details').find("input[type=text], textarea").val("");
    $('#modal-appt-details').find("#appt-start-hr").val(0);
    $('#modal-appt-details').find("#appt-end-hr").val(0);
    $('#modal-appt-details').find("#appt-start-min").val(0);
    $('#modal-appt-details').find("#appt-end-min").val(0);            
}

uptempo.officePortal.appointments.addMultiApptForm = function () {
    uptempo.officePortal.appointments.clearMultiApptDetails();
    var date = $("#appointments_date_picker").datepicker("getDate");
    if (date !== null) {
        //date = $.datepicker.formatDate('mm/dd/yy', date);
        date = $.datepicker.formatDate('m/d/yy', date);
    } 	
    $("#appt-multi-date").val(date);
	$("#appt-multi-office-select").val(uptempo.officePortal.appointments.officeKey);
    $("#appt-multi-doctor").val(uptempo.officePortal.appointments.doctorKey);

    $('#modal-appt-multiple').modal('show');
}

uptempo.officePortal.appointments.clearMultiApptDetails = function () {
    $('#modal-appt-multiple').find("input[type=text], textarea").val("");	
    $('#modal-appt-multiple').find("#appt-multi-status").val(0);
    $('#modal-appt-multiple').find("#appt-multi-start-hr").val(8);
    $('#modal-appt-multiple').find("#appt-multi-end-hr").val(9);
    $('#modal-appt-multiple').find("#appt-multi-start-min").val(0);
    $('#modal-appt-multiple').find("#appt-multi-end-min").val(0);            
    $('#modal-appt-multiple').find("#appt-multi-status").val('AVAILABLE');
    $('#modal-appt-multiple').find("#appt-multi-days").val(1);      
    $('#modal-appt-multiple').find("#appt-multi-length").val(15);      
    $('#modal-appt-multiple').find("#appt-multi-spacing").val(0);              

}

uptempo.officePortal.appointments.addMultiAppt = function () {
//*** Get the number of days in a row to schedule.
  $('#loading-indicator').fadeIn();
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
    var startHours = parseInt($("#appt-multi-start-hr").val()) +
        uptempo.util.getAmPmHours($("#appt-multi-start-ap").val());
    if (startHours == 24) {startHours = 12;}
    startDate.setHours(startHours, parseInt($("#appt-multi-start-min").val()), 0);
    var endDate = new Date(batchStartDate.getTime());
    var endHours = parseInt($("#appt-multi-end-hr").val()) +
      uptempo.util.getAmPmHours($("#appt-multi-end-ap").val());
    if (endHours == 24) {endHours = 12;}
    endDate.setHours(endHours, parseInt($("#appt-multi-end-min").val()), 0);

    //*** Calculate the total number of appointments to be submitted.
    var cStartDate = startDate;
    var cEndDate = endDate;
    var errors = 0;
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
                     "&apptOffice=" + $("#appt-multi-office-select").val();
      //*** Submit this appointment
      $.ajax({
        type: 'POST',
        url: '/service/appointment',
        cache: false,
        data: apptData,
        success: function(response) {
          if (response.status == "SUCCESS") {
			  
          } else {
            	errors++;         
          }
        },
        error: function(e){
        	uptempo.officePortal.util.alert("Some error occured while creating appointments" + e);                                 
        }
      });
      //*** Increment by the spacing between appointments.
      startDate.setMinutes(startDate.getMinutes() + apptSpacing);
    }
    currentDay++;
    //*** Add a day.
    batchStartDate.setTime(batchStartDate.getTime() + 86400000);
  } //*** End number of days loop.
  if(errors == 0){
      //uptempo.officePortal.util.alert("Appointments successfully created", "Success!");
      uptempo.officePortal.appointments.getDoctorAllAppointments(uptempo.officePortal.appointments.doctorKey);
  } else {
	  uptempo.officePortal.util.alert("There was some problem creating " + errors + " appointments of the batch");
      uptempo.officePortal.appointments.getDoctorAllAppointments(uptempo.officePortal.appointments.doctorKey);
  }
  

}

uptempo.officePortal.appointments.deleteSelected = function(){
    $("#appointments-table .checkboxes").each(function () {
        if ($(this).is(":checked")) {
            uptempo.officePortal.appointments.delete($(this).data("id"));
             //console.log($(this).data("id") + "is checked"); 
        } 
    });
    $("#appointments-table .group-checkable").removeAttr("checked");
}

uptempo.officePortal.appointments.anyApptChecked = function(){
	var set = $("#appointments-table .group-checkable").attr("data-set");
	var anySelected = false;
    jQuery(set).each(function () {
        if ($(this).is(":checked")) {
            anySelected = true;
        }
    });
    if (anySelected) { 
    	$("#delete-selected-appt").removeAttr("disabled");            
    	$("#delete-selected-appt").show();            
    } else {
    	$('#delete-selected-appt').attr("disabled","disabled"); 
    	$('#delete-selected-appt').hide(); 
    }

}
