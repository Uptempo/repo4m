uptempo.officePortal.util = {};
uptempo.officePortal.util.alert = function (message) {
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
                        $("#appt-offices-list").append('<li data-id="' + response.data.values[office]['key'] + '" class=""><a href="#" onclick="javascript:uptempo.officePortal.appointments.getDoctorsList(\'' + response.data.values[office]['key'] + '\');">Office ' + response.data.values[office]['officeName'] + '</a></li>');
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

uptempo.officePortal.appointments.getDoctorsList = function (officeKey) {
    uptempo.officePortal.appointments.officeKey = officeKey;
    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/doctor',
        data: 'billingOffice=' + officeKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if ((typeof response.data.values !== 'undefined') && (response.data.values.length > 0)) {
                    $("#doctors-list").empty();
                    for (var doc in response.data.values) {
                        $("#doctors-list").append('<option value="' + response.data.values[doc]['key'] + '">' + response.data.values[doc]['title'] + ' ' + response.data.values[doc]['firstName'] + ' ' + response.data.values[doc]['lastName'] + '</option>');
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
    uptempo.officePortal.appointments.doctorKey = doctorKey;
    var events = {};
    var endDate = new Date();
    endDate.setDate(endDate.getDate() + 60);
    var dd = endDate.getDate();
    var mm = endDate.getMonth() + 1;
    var y = endDate.getFullYear();

    var formattedEndDate = mm + '/' + dd + '/' + y;
    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/appointment',
        data: 'apptDoctor=' + doctorKey + '&apptEndDay=' + formattedEndDate,
        cache: false,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if ((typeof response.data.values !== 'undefined') && (response.data.values.length > 0)) {
                    for (var day in response.data.values) {
                        events[new Date(response.data.values[day]['apptDate'])] = new Event(response.data.values[day]['key'], 'active-day');
                    }
                    $("#appointments_date_picker").datepicker({
                        numberOfMonths: 2,
                        onSelect: function (date) {
                            uptempo.officePortal.appointments.getDoctorAppointments(uptempo.officePortal.appointments.doctorKey)
                        },
                        beforeShowDay: function (date) {
                            var event = events[date];
                            if (event) {

                                return [true, 'active-day', event.text];
                            } else {
                                return [true, '', ''];
                            }
                        }
                    });
                    uptempo.officePortal.appointments.getDoctorAppointments(uptempo.officePortal.appointments.doctorKey);


                } else {
                    $("#appointments-table tbody").empty();
                    uptempo.officePortal.util.alert('There are no appointments for this doctor');
                }
            }
        },
        error: function (e) {
            uptempo.appointments.util.alert(e);
        }
    });
}


uptempo.officePortal.appointments.getDoctorAppointments = function (doctorKey) {
    uptempo.officePortal.appointments.doctorKey = doctorKey;

    var date = $("#appointments_date_picker").datepicker("getDate");
    var date2 = $("#appointments_date_picker").datepicker("getDate", "+1d");
    date2.setDate(date2.getDate() + 1);
    if (date !== null) {
        date = $.datepicker.formatDate('mm/dd/yy', date);
        date2 = $.datepicker.formatDate('mm/dd/yy', date2);
    }

    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/appointment',
        data: 'apptDoctor=' + doctorKey + '&apptStartDay=' + date + '&apptEndDay=' + date2,
        cache: false,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if ((typeof response.data.values !== 'undefined') && (response.data.values.length > 0)) {
                    $("#appointments-table tbody").empty();
                    for (var appt in response.data.values) {
                        var minutes = response.data.values[appt]['apptStartMin'];
                        if (minutes === 0) minutes = '00';
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

                        $("#appointments-table").append('<tr class="odd gradeX"><td><input type="checkbox" class="checkboxes" value="1" /></td><td class="center hidden-phone">' + response.data.values[appt]['apptDate'] + ' ' + response.data.values[appt]['apptStartHr'] + ':' + minutes + '</td><td>' + response.data.values[appt]['apptDoctor'] + '</td><td>' + patient + '</td><td>' + phone + '</td><td>' + email + '</td><td>' + source + '</td><td><button class="btn btn-small btn-primary" onclick="javascript:uptempo.officePortal.appointments.showDetails(\'' + response.data.values[appt]['key'] + '\');"><i class="icon-pencil icon-white"></i> Edit</button><button class="btn btn-small btn-danger" onclick="javascript:uptempo.officePortal.appointments.delete(\'' + response.data.values[appt]['key'] + '\');"><i class="icon-remove icon-white"></i> Delete</button></td></tr>');
                        //<button class="btn btn-small btn-info"><i class="icon-ban-circle icon-white"></i> Cancel</button>
                    }

                } else {
                    $("#appointments-table tbody").empty();
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
                console.log(response.data)
                if (typeof response.data !== 'undefined') {
                    for (var i in uptempo.appointment.validFields) {
                        console.log(uptempo.appointment.validFields[i].inputId)
                        console.log(response.data[uptempo.appointment.validFields[i].formVal]);
                        $(uptempo.appointment.validFields[i].inputId).val(response.data[uptempo.appointment.validFields[i].formVal]);

                    }
                    $("#appt-office-select").val(response.data['ancestor']);

                    $("#update-confirmed").click(function () {
                        uptempo.officePortal.appointments.update(apptKey)
                    })

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
        var formData = uptempo.ajax.consructPostString(uptempo.appointment.validFields);
        console.log(formData)
        //*** Submit the XHR request
        $.ajax({
            type: 'PUT',
            url: "/service/appointment/" + apptKey,
            data: formData,
            success: function (response) {
                //*** If the response was sucessful, show the success indicator.
                if (response.status === "SUCCESS") {
                    console.log('success');
                    console.log(JSON.stringify(response));
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
    $("#delete-confirmed").click(function () {
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