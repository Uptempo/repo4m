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

msAdmin.doctor.resetValidFields = function(validFields){
  validFields.splice(0, validFields.length);
  validFields.push({name: "Office", inputId: "#doctor-billingOffice", formVal: "billingOffice", required: true});
  validFields.push({name: "First name", inputId: "#doctor-firstName", formVal: "firstName", required: true});
  validFields.push({name: "Last name", inputId: "#doctor-lastName", formVal: "lastName", required: true});
  validFields.push({name: "Email", inputId: "#doctor-email", formVal: "email", required: true});
  validFields.push({name: "Education", inputId: "#doctor-education", formVal: "education", required: true});
  validFields.push({name: "Public description", inputId: "#doctor-publicDescription", formVal: "publicDescription", required: false});
  validFields.push({name: "Notes", inputId: "#doctor-notes", formVal: "notes", required: false});
}

msAdmin.doctor.addClearValues = function(validFields){
  var element = { name: "doctor-clear-title-values-holder", inputId: "#doctor-clear-title-values-holder", formVal: "clearTitles", required: false };
  validFields.push(element);
  element = { name: "doctor-clear-speciality-values-holder", inputId: "#doctor-clear-speciality-values-holder", formVal: "clearSpecialities", required: false };
  validFields.push(element);
}

msAdmin.doctor.addDynamicValidFields = function(validFields){
  var startTitles = 1;
  var startSpecialities = 1;
  if ($('input[name=doctors-radio-clear-titles]:checked').val() == 'true'){
    startTitles = 1;
  }
  else if ($('input[name=doctors-radio-clear-titles]:checked').val() == 'false'){
    startTitles = msAdmin.doctor.listTitles.length + 1;
  }
  if ($('input[name=doctors-radio-clear-specialities]:checked').val() == 'true'){
    startSpecialities = 1;
  }
  else if ($('input[name=doctors-radio-clear-specialities]:checked').val() == 'false'){
    startSpecialities = msAdmin.doctor.listSpecialities.length + 1;
  }
  var elementId = "#doctor-title-element" + i;
  var elementFormValue = "title" + i;
  var element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
  for(var i = startTitles; i <= msAdmin.doctor.getTitlesCounter(); i ++){
    elementId = "#doctor-title-element" + i;
    elementFormValue = "title" + i;
    element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push(element); 
  }
  for(var i = startSpecialities; i <= msAdmin.doctor.getSpecialitiesCounter(); i ++){
    elementId = "#doctor-speciality-element" + i;
    elementFormValue = "speciality" + i;
    element = { name: "Dynamic list text", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push(element); 
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
  $("td:eq(1)", nRow).text(aData[1]).html();
  $("td:eq(2)", nRow).text(aData[2]).html();
  $("td:eq(3)", nRow).text(aData[3]).html();
  $("td:eq(6)", nRow).text(aData[6]).html();
  $("td:eq(7)", nRow).html(showPhoto);

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

msAdmin.doctor.listTitlesCounter = 0;
msAdmin.doctor.listSpecialitiesCounter = 0;
msAdmin.doctor.listTitles = [];
msAdmin.doctor.listSpecialities = [];
msAdmin.doctor.titleValues = [];
msAdmin.doctor.specialityValues = [];

msAdmin.doctor.setTitlesCounter = function(value){
  msAdmin.doctor.listTitlesCounter = value
}

msAdmin.doctor.setSpecialitiesCounter = function(value){
  msAdmin.doctor.listSpecialitiesCounter = value
}

msAdmin.doctor.getTitlesCounter = function(){
  return msAdmin.doctor.listTitlesCounter;
}

msAdmin.doctor.getSpecialitiesCounter = function(){
  return msAdmin.doctor.listSpecialitiesCounter;
}

msAdmin.doctor.addTextFieldAndIncreaseForOneValueCounter = function(itemValue, domElementId, readonly){
    var id = 0;
    var placeholderValue = '';
    var idName = ''
    var data = [];
    if (domElementId == "#doctor-table-title-values"){
      id = msAdmin.doctor.getTitlesCounter() + 1;
      msAdmin.doctor.setTitlesCounter(id);
      placeholderValue = 'Title value';
      idName = 'doctor-title-element';
      data = msAdmin.doctor.titleValues;
    }
    else if (domElementId == "#doctor-table-speciality-values"){
      id = msAdmin.doctor.getSpecialitiesCounter() + 1;
      msAdmin.doctor.setSpecialitiesCounter(id);
      placeholderValue = 'Speciality value';
      idName = 'doctor-speciality-element';
      data = msAdmin.doctor.specialityValues;
    }
    var item = '<select id="'+idName+id+'" placeholder="'+placeholderValue+'" data-theme="a" style="font-size:20px;"></select>'
    var elementToInsert = '<tr><td>' + item + '</td></tr>'
    $(domElementId).append(elementToInsert);
    msAdmin.doctor.loadOptions(data, idName+id, itemValue);
}
msAdmin.doctor.showNew = function () {
  msAdmin.doctor.clearDoctorForm();
  msAdmin.doctor.setTitlesCounter(0);
  msAdmin.doctor.setSpecialitiesCounter(0);
  uptempo.office.fillDropdownWithOffices("doctor-billingOffice");
  msAdmin.doctor.getListDataForDoctors("TITLES");
  msAdmin.doctor.getListDataForDoctors("SPECIALTIES");
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

msAdmin.doctor.getListDataForDoctors = function (forWhat) {
  var successFn = function(listData) {
    if (forWhat == "TITLES") {
      var titleArray = new Array();
      $.each(listData, function(index, item) {
        titleArray[index] = item['listValue'];        
      });
      msAdmin.doctor.titleValues = titleArray;
     } else if (forWhat == "SPECIALTIES") {
       var titleArray = new Array();
       $.each(listData, function(index, item) {
        titleArray[index] = item['listValue'];        
       });
       msAdmin.doctor.specialityValues = titleArray;
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

msAdmin.doctor.loadOptions = function(what, where){
  $("#"+where).empty();
  for (field in what) {
    $("#"+where)
          .append($('<option>', { value : what[field] })
          .text(what[field]));
  }
  $("#"+where).selectmenu();
  $("#"+where).selectmenu('refresh', true);
}

msAdmin.doctor.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#doctor-firstName").val();

  //*** On success, close the submission window and reload the table.
  var doctorSuccessFn = function() {
    $("#doctor-form").popup("close");
    msAdmin.doctor.clearDoctorForm();
    msAdmin.doctor.getDoctorData();
    msAdmin.doctor.setTitlesCounter(0);
    msAdmin.doctor.setSpecialitiesCounter(0);
  };
  msAdmin.doctor.resetValidFields(msAdmin.doctor.validFields);
  msAdmin.doctor.addDynamicValidFields(msAdmin.doctor.validFields);
  $("#doctor-form").serialize();
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
  msAdmin.doctor.setTitlesCounter(0);
  msAdmin.doctor.setSpecialitiesCounter(0);
  $("#doctor-form-title").html("Update Doctor");
  $("#doctor-form-submit").changeButtonText("Update this Doctor");  
  $("#doctor-form-errors").html("");
  $("#doctor-key").val(valueKey);

  var radioDivTitles = '<div id="doctors-div-clear-titles" style="display:none;" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="doctors-radio-clear-titles"><input type="radio" name="doctors-radio-clear-titles" value="false" onclick="msAdmin.doctor.handleRadioButtonClick("titles");">Add title</label><label for="doctors-radio-clear-titles" style="margin:20px;"><input type="radio" name="doctors-radio-clear-titles" value="true" checked="checked" onclick="msAdmin.doctor.handleRadioButtonClick("titles");">Replace title</label></div>';
  var radioDivSpecialities = '<div id="doctors-div-clear-specialities" style="display:none;" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="doctors-radio-clear-specialities"><input type="radio" name="doctors-radio-clear-specialities" value="false" onclick="msAdmin.doctor.handleRadioButtonClick("specialities");">Add speciality</label><label for="doctors-radio-clear-specialities" style="margin:20px;"><input type="radio" name="doctors-radio-clear-specialities" value="true" checked="checked" onclick="msAdmin.doctor.handleRadioButtonClick("specialities");">Replace speciality</label></div>'; 

  $("#doctor-publicDescription").after(radioDivTitles);
  $("#doctors-div-clear-titles").after(radioDivSpecialities);
  msAdmin.doctor.clearDoctorForm();
  uptempo.office.fillDropdownWithOffices("doctor-billingOffice");
  msAdmin.doctor.getListDataForDoctors("TITLES");
  msAdmin.doctor.getListDataForDoctors("SPECIALTIES");

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

          msAdmin.doctor.addToFormListsFromResponse(msAdmin.doctor.listTitles, msAdmin.doctor.addTextFieldAndIncreaseForOneValueCounter, '#doctor-table-title-values', '');
          msAdmin.doctor.addToFormListsFromResponse(msAdmin.doctor.listSpecialities, msAdmin.doctor.addTextFieldAndIncreaseForOneValueCounter, '#doctor-table-speciality-values', '');
          
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

msAdmin.doctor.addToFormListsFromResponse = function(responseList, whereToAdd, domElementId, readonly) {
  var len = 0;
  if (responseList != null){
    len = responseList.length;
  }
  for (var i=0; i<len; ++i) {
    if (i in responseList) {
      var item = responseList[ i ];
      whereToAdd(item, domElementId, readonly);
    }
  }
}

msAdmin.doctor.handleRadioButtonClick = function(where) {
  if (where == 'titles'){
    msAdmin.doctor.setTitlesCounter(0);
    $('#doctor-table-title-values').empty();
    if ($('input[name=doctors-radio-clear-titles]:checked').val() == 'true'){
      msAdmin.doctor.addToFormListsFromResponse(msAdmin.doctor.listTitles, msAdmin.doctor.addTextFieldAndIncreaseForOneValueCounter , '#doctor-table-title-values', '');
    }
    else if ($('input[name=doctors-radio-clear-titles]:checked').val() == 'false'){
      msAdmin.doctor.addToFormListsFromResponse(msAdmin.doctor.listTitles, msAdmin.doctor.addTextFieldAndIncreaseForOneValueCounter, '#doctor-table-title-values', 'readonly');
    }
  }
  else if (where == 'specialities'){
    msAdmin.doctor.setSpecialitiesCounter(0);
    $('#doctor-table-speciality-values').empty();
    if ($('input[name=doctors-radio-clear-specialities]:checked').val() == 'true'){
      msAdmin.doctor.addToFormListsFromResponse(msAdmin.doctor.listSpecialities, msAdmin.doctor.addTextFieldAndIncreaseForOneValueCounter , '#doctor-table-speciality-values', '');    }
    else if ($('input[name=doctors-radio-clear-specialities]:checked').val() == 'false'){
      msAdmin.doctor.addToFormListsFromResponse(msAdmin.doctor.listSpecialities, msAdmin.doctor.addTextFieldAndIncreaseForOneValueCounter, '#doctor-table-specialitiy-values', 'readonly');
    }
  }
  return false;
}

msAdmin.doctor.submitUpdate = function() {
  //*** Set the key for submission.
  $('#doctor-clear-title-values-holder').val($('input[name=doctors-radio-clear-titles]:checked').val());
  $('#doctor-clear-speciality-values-holder').val($('input[name=doctors-radio-clear-specialities]:checked').val());

  var doctorKey = $("#doctor-key").val();
  msAdmin.doctor.resetValidFields(msAdmin.doctor.validFields);
  msAdmin.doctor.addDynamicValidFields(msAdmin.doctor.validFields);
  msAdmin.doctor.addClearValues(msAdmin.doctor.validFields);
  
  //*** On success, close the submission window and reload the table.
  var doctorUpdsuccessFn = function() {

    $("#doctor-form").popup("close");
    msAdmin.doctor.clearDoctorForm();
    msAdmin.doctor.getDoctorData();
    msAdmin.doctor.setTitlesCounter(0);
    msAdmin.doctor.setSpecialitiesCounter(0);
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
