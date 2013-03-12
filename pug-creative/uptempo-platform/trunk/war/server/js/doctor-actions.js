/* This file contains all admin doctor definition actions */
msAdmin.doctor = {};
msAdmin.doctor.tableHeadings = [
  {"sTitle": "Office", "aTargets": [0]},
  {"sTitle": "First name", "aTargets": [1]},
  {"sTitle": "Last name", "aTargets": [2]},
  {"sTitle": "email", "aTargets": [3]},
  {"sTitle": "Titles", "aTargets": [4], "mData" : null},
  {"sTitle": "Speciality", "aTargets": [5], "mData" : null},
  {"sTitle": "Education", "aTargets": [6]},
  {"sTitle": "Photo", "aTargets": [7], "mData" : null},
  {"sTitle": "Public description", "aTargets": [8], "mData" : null},
  {"sTitle": "Notes", "aTargets": [9], "mData" : null},
  {"sTitle": "Action", "aTargets": [10], "mData" : null},

];

//*** Field mapping for validation and naming.
msAdmin.doctor.validFields = [
  {name: "Office", inputId: "#doctor-billingOffice", formVal: "billingOffice", required: true},
  {name: "First name", inputId: "#doctor-firstName", formVal: "firstName", required: true},
  {name: "Last name", inputId: "#doctor-lastName", formVal: "lastName", required: true},
  {name: "Email", inputId: "#doctor-email", formVal: "email", required: true},
  {name: "Education", inputId: "#doctor-education", formVal: "education", required: true},
  {name: "Public description", inputId: "#doctor-publicDescription", formVal: "publicDescription", required: false},
  {name: "Notes", inputId: "#doctor-notes", formVal: "notes", required: false},
];

msAdmin.doctor.resetValidFields = function(){
  msAdmin.doctor.validFields = new Array();
  msAdmin.doctor.validFields.push({name: "Office", inputId: "#doctor-billingOffice", formVal: "billingOffice", required: true});
  msAdmin.doctor.validFields.push({name: "First name", inputId: "#doctor-firstName", formVal: "firstName", required: true});
  msAdmin.doctor.validFields.push({name: "Last name", inputId: "#doctor-lastName", formVal: "lastName", required: true});
  msAdmin.doctor.validFields.push({name: "Email", inputId: "#doctor-email", formVal: "email", required: true});
  msAdmin.doctor.validFields.push({name: "Education", inputId: "#doctor-education", formVal: "education", required: true});
  msAdmin.doctor.validFields.push({name: "Public description", inputId: "#doctor-publicDescription", formVal: "publicDescription", required: false});
  msAdmin.doctor.validFields.push({name: "Notes", inputId: "#doctor-notes", formVal: "notes", required: false});
}

msAdmin.doctor.addClearValues = function(){
  var element = { name: "doctor-clear-title-values-holder", inputId: "#doctor-clear-title-values-holder", formVal: "clearTitles", required: false };
  msAdmin.doctor.validFields.push(element);
  element = { name: "doctor-clear-speciality-values-holder", inputId: "#doctor-clear-speciality-values-holder", formVal: "clearSpecialities", required: false };
  msAdmin.doctor.validFields.push(element);
}

msAdmin.doctor.addDynamicValidFields = function() {
  if (msAdmin.doctor.titleTotal == 1) {
    $("#doctor-clear-title-values-holder").val("false");
  } else {
    $("#doctor-clear-title-values-holder").val("true");
    for(var i = 1; i < msAdmin.doctor.titleTotal; i++) {
      elementId = "#doctor-title-element" + i;
      elementFormValue = "title" + i;
      element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
      msAdmin.doctor.validFields.push(element); 
    }
  }
  if (msAdmin.doctor.specialityTotal == 1) {
    $("#doctor-clear-speciality-values-holder").val("false");
  } else {
    $("#doctor-clear-speciality-values-holder").val("true");
    for(var i = 1; i < msAdmin.doctor.specialityTotal; i++) {
      elementId = "#doctor-speciality-element" + i;
      elementFormValue = "speciality" + i;
      element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
      msAdmin.doctor.validFields.push(element); 
    }
  }
}

//*** Formats the doctor table.
msAdmin.doctor.tableFormatter = function(nRow, aData, DisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"msAdmin.doctor.showUpdate('" + aData[17] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"msAdmin.doctor.showDeleteConfirm('" + aData[17] + "');\">del</a>";
  var showTitles = "<a href='#' onclick=\"msAdmin.util.showList('" + "Title', 'doctor', '" + aData[17] + "');\">show</a>&nbsp;&nbsp;";
  var showPhoto = "<a href='#' onclick=\"msAdmin.doctor.Photo('" + aData[17] + "');\">upload/view</a>&nbsp;&nbsp;";
  var showSpecialities = "<a href='#' onclick=\"msAdmin.util.showList('" + "Speciality', 'doctor', '" + aData[17] + "');\">show</a>&nbsp;&nbsp;";
  var showEducation = "<a href='#' onclick=\"msAdmin.util.showList('" + "Education', 'doctor', '" + aData[17] + "');\">show</a>&nbsp;&nbsp;";
  var showPublicDescription = "<a href='#' onclick=\"msAdmin.util.showList('" + "PublicDescription', 'doctor', '" + aData[17] + "');\">show</a>&nbsp;&nbsp;";
  var showNotes = "<a href='#' onclick=\"msAdmin.util.showList('" + "Notes', 'doctor', '" + aData[17] + "');\">show</a>&nbsp;&nbsp;";

  msAdmin.doctor.getOfficeNameBy(aData[0], $("td:eq(0)", nRow));
  
  if (aData[4] != null && aData[4].length > 0){
    $("td:eq(4)", nRow).html(showTitles);
  }
  else{
    $("td:eq(4)", nRow).html('');
  }
  if (aData[5] != null && aData[5].length > 0){
    $("td:eq(5)", nRow).html(showSpecialities);
  }
  else{
    $("td:eq(5)", nRow).html('');
  }
  
  $("td:eq(7)", nRow).html(showPhoto);
  
  if (aData[8] != null && aData[8].length > 0){
    $("td:eq(8)", nRow).html(showPublicDescription);
  }
  else{
    $("td:eq(8)", nRow).html('');
  }
  if (aData[9] != null && aData[9].length > 0){
    $("td:eq(9)", nRow).html(showNotes);
  }
  else{
    $("td:eq(9)", nRow).html('');
  }
  $("td:eq(10)", nRow).html(editLink + delLink);
};

msAdmin.doctor.listTitles = [];
msAdmin.doctor.listSpecialities = [];
msAdmin.doctor.titleValues = [];
msAdmin.doctor.specialityValues = [];

msAdmin.doctor.showNew = function () {
  msAdmin.doctor.clearDoctorForm();
  uptempo.office.fillDropdownWithOffices("doctor-billingOffice");

  msAdmin.doctor.markAsUnchecked("#doctor-titles");
  msAdmin.doctor.markAsUnchecked("#doctor-specialities");
  
  //*** Setup the form.
  $("#doctor-form-title").html("New Doctor");
  $("#doctor-form-submit").changeButtonText("Create this Doctor");
  $("#doctor-form-submit").off("click");
  $("#doctor-form-submit").on("click", msAdmin.doctor.submitNew);
  $("#doctor-form-errors").html("");
  //*** Show the form.
  $("#doctor-form").popup("open");
}

msAdmin.doctor.search = function () {

  
  msAdmin.loader.show("Getting Doctor data.");
  var appDataArray = ["No Doctor data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/doctor',
    data: "q="+$("#doctor-search").val()+"&format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get Doctor records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#doctor-table').html('<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="doctor-table-data"></table>');
      //*** Make this table the active one for row events.
      msAdmin.activeTable = $('#doctor-table-data').dataTable({
        "aoColumnDefs": msAdmin.doctor.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": msAdmin.doctor.tableFormatter,
        "bProcessing": true
      });
    },
    complete: msAdmin.loader.hide()
  });

}

msAdmin.doctor.createCheckboxList = function(container, values, elementPrefix) {
  $(container).empty();
  for (var i=0; i<values.length; i++) {
    var checkboxHtml = '<input type="checkbox" name="' + elementPrefix + 'Check' + (i + 1) + '" id="' + elementPrefix + 'Check' + (i + 1) + 
                        '" class="custom" value="' + values[i] + '" data-theme="a"/>' + 
                        '<label for="' + elementPrefix + 'Check' + (i + 1) + '">' + values[i] + '</label>';
    $(container).append(checkboxHtml);  
  }
  $(container).find('input').checkboxradio();
}

msAdmin.doctor.getListDataForDoctors = function (forWhat) {
  var successFn = function(listData) {
    if (forWhat == "TITLES") {
      var titleArray = new Array();
      $.each(listData, function(index, item) {
        titleArray[index] = item['listValue'];        
      });
      msAdmin.doctor.titleValues = titleArray[0];
      msAdmin.doctor.createCheckboxList("#doctor-titles", msAdmin.doctor.titleValues, "title");
     } else if (forWhat == "SPECIALTIES") {
       var specialityArray = new Array();
       $.each(listData, function(index, item) {
        specialityArray[index] = item['listValue'];        
       });
       msAdmin.doctor.specialityValues = specialityArray[0];
       msAdmin.doctor.createCheckboxList("#doctor-specialities", msAdmin.doctor.specialityValues, "speciality");
     }
  }
  msAdmin.ajax.getStaticList("COMMON", forWhat, successFn);
}

msAdmin.doctor.getOfficeNameBy = function (key, setElement) {
  //*** Get the office data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billingoffice/'+key,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        if (response.message == ""){
          setElement.text("null").html();
        }
        else{
          setElement.text(response.data[ "officeName" ]).html();
        }
      } else {
        $(".status-bar").html("Failed to get billingoffice record! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
}

msAdmin.doctor.prepareCheckedTitlesAndSpecialities = function() {
  var titleIndex = 1;
  var specialityIndex = 1;
  $("#doctor-lists").empty();
  $("#doctor-titles").find('input[type="checkbox"]').each(function() {
    if($(this).is(':checked')) {
      var titleValue = $(this).val();
      var hiddenTitle = $('<input/>',{type:'hidden',id:"doctor-title-element" + titleIndex, value: titleValue});
      hiddenTitle.appendTo("#doctor-lists");
      titleIndex++;
    }
  });
  msAdmin.doctor.titleTotal = titleIndex;
  $("#doctor-specialities").find('input[type="checkbox"]').each(function() {
    if($(this).is(':checked')) {
      var specialtyValue = $(this).val();
      var hiddenSpecialty = $('<input/>',{type:'hidden',id:"doctor-speciality-element" + specialityIndex, value: specialtyValue});
      hiddenSpecialty.appendTo("#doctor-lists");
      specialityIndex++;
    }
  });
  msAdmin.doctor.specialityTotal = specialityIndex;
}

msAdmin.doctor.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#doctor-firstName").val();

  //*** On success, close the submission window and reload the table.
  var doctorSuccessFn = function() {
    $("#doctor-form").popup("close");
    msAdmin.doctor.clearDoctorForm();
    msAdmin.doctor.getDoctorData();
  };

  msAdmin.doctor.resetValidFields();
  console.log(msAdmin.doctor.validFields.length);
  
  // add hidden fields for checked title or specialty
  msAdmin.doctor.prepareCheckedTitlesAndSpecialities();

  msAdmin.doctor.addDynamicValidFields();
  
  msAdmin.ajax.submitNew("Doctor",
                         "/service/doctor",
                         msAdmin.doctor.validFields,
                         "doctor-firstName",
                         key,
                         doctorSuccessFn);
}

//*** Show the update application popup.
msAdmin.doctor.showUpdate = function (valueKey) {
  msAdmin.doctor.clearDoctorForm();
  $("#doctor-form-title").html("Update Doctor");
  $("#doctor-form-submit").changeButtonText("Update this Doctor");  
  $("#doctor-form-errors").html("");
  $("#doctor-key").val(valueKey);

  msAdmin.doctor.clearDoctorForm();
  uptempo.office.fillDropdownWithOffices("doctor-billingOffice");

  msAdmin.doctor.markAsUnchecked("#doctor-titles");
  msAdmin.doctor.markAsUnchecked("#doctor-specialities");
  
  //*** Submit the XHR request.
  $.ajax({
    type: 'GET',
    url: '/service/doctor/' + valueKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
        
          var billingOffice = response.data.billingOffice;
          var firstName = response.data.firstName;
          var lastName = response.data.lastName;
          var email = response.data.email;
          msAdmin.doctor.listTitles = response.data.title;
          if (msAdmin.doctor.listTitles == null){
            msAdmin.doctor.listTitles = [];
          }
          msAdmin.doctor.listSpecialities = response.data.speciality;
          if (msAdmin.doctor.listSpecialities == null){
            msAdmin.doctor.listSpecialities = [];
          }
          var education = response.data.education;
          var publicDescription = response.data.publicDescription;
          var notes = response.data.notes;

          $("#doctor-billingOffice").val(billingOffice);
          $("#doctor-firstName").val(firstName);
          $("#doctor-lastName").val(lastName);
          $("#doctor-email").val(email);
          $("#doctor-education").val(education);
          $("#doctor-publicDescription").val(publicDescription);
          $("#doctor-notes").val(notes);

          msAdmin.doctor.markAsChecked(msAdmin.doctor.listTitles, "#doctor-titles");
          msAdmin.doctor.markAsChecked(msAdmin.doctor.listSpecialities, "#doctor-specialities");
          
        } else {
          alert(response.message);
        }
      }
    });

  $("#doctor-form-submit").off("click");
  $("#doctor-form-submit").on("click", msAdmin.doctor.submitUpdate);
  //*** Show the form.
  $("#doctor-form").popup("open");
}

msAdmin.doctor.markAsChecked = function(itemList, el) {
  var index = 1;
  
  $(el).find('input[type="checkbox"]').each(function() {
    for (i in itemList) {
      if ($(this).val() == itemList[i]) {
        $(this).attr('checked', true);
        index++;
        break;
      }      
    }    
  });
  
  if (el == "#doctor-titles") {
    msAdmin.doctor.titleTotal = index;
  } else {
    msAdmin.doctor.specialityTotal = index;
  }
  $(el).find('input[type="checkbox"]').checkboxradio("refresh");  
} 

msAdmin.doctor.markAsUnchecked = function(el) { 
  $(el).find('input[type="checkbox"]').each(function() {
    $(this).attr('checked', false);   
  });
  if (el == "#doctor-titles") {
    msAdmin.doctor.titleTotal = 0;
  } else {
    msAdmin.doctor.specialityTotal = 0;
  }
  $(el).find('input[type="checkbox"]').checkboxradio("refresh");  
} 

msAdmin.doctor.submitUpdate = function() {
  //*** Set the key for submission.
  var doctorKey = $("#doctor-key").val();

  msAdmin.doctor.resetValidFields();

  // add hidden fields for checked title or specialty
  msAdmin.doctor.prepareCheckedTitlesAndSpecialities();

  msAdmin.doctor.addDynamicValidFields();
  msAdmin.doctor.addClearValues();
  
  //*** On success, close the submission window and reload the table.
  var doctorUpdsuccessFn = function() {
    $("#doctor-form").popup("close");
    msAdmin.doctor.clearDoctorForm();
    msAdmin.doctor.getDoctorData();
  };

  $("#doctor-form").serialize();
  msAdmin.ajax.submitUpdate("Doctor",
                            "/service/doctor/" + doctorKey,
                            msAdmin.doctor.validFields,
                            "doctor-firstName",
                            doctorUpdsuccessFn);
}

msAdmin.doctor.clearDoctorForm = function() {
  $("#doctor-billingOffice").val("");
  $("#doctor-firstName").val("");
  $("#doctor-lastName").val("");
  $("#doctor-email").val("");
  $("#doctor-education").val("");
  $("#doctor-publicDescription").val("");
  $("#doctor-notes").val("");

  $('#doctor-table-title-values').empty();
  $('#doctor-table-speciality-values').empty();
  $('#doctor-div-clear-titles').remove();
  $('#doctor-div-clear-specialities').remove();

}

msAdmin.doctor.getDoctorData = function () {

  
  msAdmin.loader.show("Getting Doctor data.");
  var appDataArray = ["No Doctor data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/doctor',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get Doctor records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#doctor-table').html('<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="doctor-table-data"></table>');
      //*** Make this table the active one for row events.
      msAdmin.activeTable = $('#doctor-table-data').dataTable({
        "aoColumnDefs": msAdmin.doctor.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": msAdmin.doctor.tableFormatter,
        "bProcessing": true
      });
    },
    complete: msAdmin.loader.hide()
  });

}

msAdmin.doctor.loadLists = function() {
  msAdmin.doctor.getListDataForDoctors("TITLES");
  msAdmin.doctor.getListDataForDoctors("SPECIALTIES");
}

msAdmin.doctor.showDeleteConfirm = function(doctorKey) {

  //*** Get the application code/name.
  $.ajax({
    type: 'GET',
    url: '/service/doctor/' + doctorKey,
    success: function(response) {
      if (response.status == "SUCCESS") {          
        var firstName = response.data.firstName;
        var lastName = response.data.lastName;
        var email = response.data.email;
      }
      $("#doctor-Name-delete").val(firstName+" "+lastName+" "+email);
      $("#doctor-confirm-popup-body")
          .html("Are you sure you want to delete Doctor with name " + firstName+" "+lastName+" "+email + " ?");
    }
  });

  //*** Set the title and body.
  $("#doctor-confirm-popup-heading").html("Delete Doctor name ?");
  $("#doctor-confirm-popup-action").html("Delete Doctor name");
  $("#doctor-key-delete").val(doctorKey);
  $("#doctor-confirm-popup-delete").on("click", msAdmin.doctor.deleteApp);

  //*** Show the form.
  $("#doctor-confirm-popup").popup("open");
}

msAdmin.doctor.deleteApp = function() {
  var doctorKey = $("#doctor-key-delete").val();
  var doctorName = "(" + $("#doctor-Name-delete").val() + ")";
  var doctorMessage = doctorName;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#doctor-confirm-popup").popup("close");
    msAdmin.doctor.getDoctorData();
  };
  msAdmin.ajax.submitDelete(doctorKey, "/service/doctor/", "Doctor", doctorMessage, audDelSuccessFn);
}

// when iframe is used on the page this init is needed
// see JQuery mobile docs:
// http://jquerymobile.com/demos/1.2.0/docs/pages/popup/popup-iframes.html 
msAdmin.doctor.initUploadPopup = function() {
  $("#doctor-image-form iframe")
        .attr("width", 0)
        .attr("height", 0);
      
  $("#doctor-image-form" ).on({
    popupbeforeposition: function() {
      var w = "100%";
      var h = "100%";
      $("#doctor-image-form iframe")
          .attr("width", w)
          .attr("height", h);
    },
    popupafterclose: function() {
      $("#doctor-image-form iframe")
          .attr("width", 0)
          .attr("height", 0)   
          .attr("src", ""); 
    }
  }); 
}

//***When the user goes to this page, show the data table on load.
$("#doctor").live('pageshow', msAdmin.doctor.getDoctorData);
$("#doctor").live('pageshow', msAdmin.util.pageTransition);
$("#doctor").live('pageshow', msAdmin.doctor.loadLists);

$("#doctor").live('pageinit', msAdmin.doctor.initUploadPopup);

msAdmin.doctor.Photo = function(doctorKey) {
  //*** Submit the XHR request.
  $.ajax({
    type: 'GET',
    url: '/service/doctor/' + doctorKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var oldImage = "";
        if (response.data.photo !== undefined) {
            oldImage = "&img=" +  response.data.photo;     
        }
        $("#doctor-image-form iframe").attr("src", 
              "/server/include/doctor-image-upload.jsp?doc=" + doctorKey + oldImage);
      } else {
        alert(response.message);
      }
    }
  });
  
  $("#doctor-image-form").popup("open");
}
