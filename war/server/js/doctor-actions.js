/* This file contains all admin doctor definition actions */
uptempo.doctor = {};
uptempo.doctor.tableHeadings = [
  {"sTitle": "Office", "mData": null},
  {"sTitle": "First name", "mData" : "firstName"},
  {"sTitle": "Last name", "mData" : "lastName"},
  {"sTitle": "email", "mData" : "email"},
  {"sTitle": "Titles", "mData" : null},
  {"sTitle": "Speciality", "mData" : null},
  {"sTitle": "Education", "mData" : null},
  {"sTitle": "Photo", "mData" : null},
  {"sTitle": "Public description", "mData" : null},
  {"sTitle": "Notes", "mData" : null},
  {"sTitle": "Action", "mData" : null}
];

//*** Field mapping for validation and naming.
uptempo.doctor.validFields = [
  {name: "Office", inputId: "#doctor-billingOffice", formVal: "billingOffice", required: true},
  {name: "First name", inputId: "#doctor-firstName", formVal: "firstName", required: true},
  {name: "Last name", inputId: "#doctor-lastName", formVal: "lastName", required: true},
  {name: "Email", inputId: "#doctor-email", formVal: "email", required: true},
  {name: "Education", inputId: "#doctor-education", formVal: "education", required: true},
  {name: "Public description", inputId: "#doctor-publicDescription", formVal: "publicDescription", required: false},
  {name: "Notes", inputId: "#doctor-notes", formVal: "notes", required: false},
];

uptempo.doctor.resetValidFields = function(){
  uptempo.doctor.validFields = new Array();
  uptempo.doctor.validFields.push({name: "Office", inputId: "#doctor-billingOffice", formVal: "billingOffice", required: true});
  uptempo.doctor.validFields.push({name: "First name", inputId: "#doctor-firstName", formVal: "firstName", required: true});
  uptempo.doctor.validFields.push({name: "Last name", inputId: "#doctor-lastName", formVal: "lastName", required: true});
  uptempo.doctor.validFields.push({name: "Email", inputId: "#doctor-email", formVal: "email", required: true});
  uptempo.doctor.validFields.push({name: "Education", inputId: "#doctor-education", formVal: "education", required: true});
  uptempo.doctor.validFields.push({name: "Public description", inputId: "#doctor-publicDescription", formVal: "publicDescription", required: false});
  uptempo.doctor.validFields.push({name: "Notes", inputId: "#doctor-notes", formVal: "notes", required: false});
}

uptempo.doctor.addClearValues = function(){
  var element = { name: "doctor-clear-title-values-holder", inputId: "#doctor-clear-title-values-holder", formVal: "clearTitles", required: false };
  uptempo.doctor.validFields.push(element);
  element = { name: "doctor-clear-specialty-values-holder", inputId: "#doctor-clear-specialty-values-holder", formVal: "clearSpecialities", required: false };
  uptempo.doctor.validFields.push(element);
}

uptempo.doctor.addDynamicValidFields = function() {
  if (uptempo.doctor.titleTotal == 1) {
    $("#doctor-clear-title-values-holder").val("false");
  } else {
    $("#doctor-clear-title-values-holder").val("true");
    for(var i = 1; i < uptempo.doctor.titleTotal; i++) {
      elementId = "#doctor-title-element" + i;
      elementFormValue = "title" + i;
      element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
      uptempo.doctor.validFields.push(element); 
    }
  }
  if (uptempo.doctor.specialtyTotal == 1) {
    $("#doctor-clear-specialty-values-holder").val("false");
  } else {
    $("#doctor-clear-specialty-values-holder").val("true");
    for(var i = 1; i < uptempo.doctor.specialtyTotal; i++) {
      elementId = "#doctor-specialty-element" + i;
      elementFormValue = "specialty" + i;
      element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
      uptempo.doctor.validFields.push(element); 
    }
  }
}

//*** Formats the doctor table.
uptempo.doctor.tableFormatter = function(nRow, aData, DisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"uptempo.doctor.showUpdate('" + aData["key"] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.doctor.showDeleteConfirm('" + aData["key"] + "');\">del</a>";
  var showTitles = "<a href='#' onclick=\"uptempo.util.showList('" + "Title', 'doctor', '" + aData["key"] + "');\">show</a>&nbsp;&nbsp;";
  var showPhoto = "<a href='#' onclick=\"uptempo.doctor.Photo('" + aData["key"] + "');\">upload/view</a>&nbsp;&nbsp;";
  var showSpecialities = "<a href='#' onclick=\"uptempo.util.showList('" + "Speciality', 'doctor', '" + aData["key"] + "');\">show</a>&nbsp;&nbsp;";
  var showEducation = "<a href='#' onclick=\"uptempo.util.showList('" + "Education', 'doctor', '" + aData["key"] + "');\">show</a>&nbsp;&nbsp;";
  var showPublicDescription = "<a href='#' onclick=\"uptempo.util.showList('" + "PublicDescription', 'doctor', '" + aData["key"] + "');\">show</a>&nbsp;&nbsp;";
  var showNotes = "<a href='#' onclick=\"uptempo.util.showList('" + "Notes', 'doctor', '" + aData["key"] + "');\">show</a>&nbsp;&nbsp;";

  uptempo.doctor.getOfficeNameBy(aData["billingOffice"], $("td:eq(0)", nRow));
  
  if (aData["title"] != null && aData["title"].length > 0){
    $("td:eq(4)", nRow).html(showTitles);
  } else {
    $("td:eq(4)", nRow).html('');
  }
  if (aData["specialty"] != null && aData["specialty"].length > 0){
    $("td:eq(5)", nRow).html(showSpecialities);
  } else {
    $("td:eq(5)", nRow).html('');
  }

  if (aData["education"] != null && aData["education"].length > 0){
    $("td:eq(6)", nRow).html(showEducation);
  } else {
    $("td:eq(6)", nRow).html('');
  }
  
  $("td:eq(7)", nRow).html(showPhoto);
  
  if (aData["publicDescription"] != null && aData["publicDescription"].length > 0){
    $("td:eq(8)", nRow).html(showPublicDescription);
  } else {
    $("td:eq(8)", nRow).html('');
  }

  if (aData["notes"] != null && aData["notes"].length > 0){
    $("td:eq(9)", nRow).html(showNotes);
  } else {
    $("td:eq(9)", nRow).html('');
  }
  $("td:eq(10)", nRow).html(editLink + delLink);
};

uptempo.doctor.listTitles = [];
uptempo.doctor.listSpecialities = [];
uptempo.doctor.titleValues = [];
uptempo.doctor.specialtyValues = [];

uptempo.doctor.showNew = function () {
  uptempo.doctor.clearDoctorForm();
  uptempo.ajax.fillDropdownWithOffices("doctor-billingOffice");

  uptempo.doctor.markAsUnchecked("#doctor-titles");
  uptempo.doctor.markAsUnchecked("#doctor-specialties");
  
  //*** Setup the form.
  $("#doctor-form-title").html("New Doctor");
  $("#doctor-form-submit").changeButtonText("Create this Doctor");
  $("#doctor-form-submit").off("click");
  $("#doctor-form-submit").on("click", uptempo.doctor.submitNew);
  $("#doctor-form-errors").html("");
  //*** Show the form.
  $("#doctor-form").popup("open");
}

uptempo.doctor.search = function () {

  
  uptempo.loader.show("Getting Doctor data.");
  var appDataArray = ["No Doctor data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/doctor',
    data: "q="+$("#doctor-search").val(),
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
      uptempo.activeTable = $('#doctor-table-data').dataTable({
        "aoColumns": uptempo.doctor.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.doctor.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });

}

uptempo.doctor.createCheckboxList = function(container, values, elementPrefix) {
  if (values) {
    $(container).empty();
    for (var i=0; i<values.length; i++) {
      var checkboxHtml = '<input type="checkbox" name="' + elementPrefix + 'Check' + (i + 1) + '" id="' + elementPrefix + 'Check' + (i + 1) + 
                          '" class="custom" value="' + values[i] + '" data-theme="a"/>' + 
                          '<label for="' + elementPrefix + 'Check' + (i + 1) + '">' + values[i] + '</label>';
      $(container).append(checkboxHtml);  
    }
    $(container).find('input').checkboxradio();
  }
}

uptempo.doctor.getListDataForDoctors = function (forWhat) {
  var successFn = function(listData) {
    if (forWhat == "TITLES") {
      var titleArray = new Array();
      $.each(listData, function(index, item) {
        titleArray[index] = item['listValue'];        
      });
      uptempo.doctor.titleValues = titleArray[0];
      uptempo.doctor.createCheckboxList("#doctor-titles", uptempo.doctor.titleValues, "title");
     } else if (forWhat == "SPECIALTIES") {
       var specialtyArray = new Array();
       $.each(listData, function(index, item) {
        specialtyArray[index] = item['listValue'];        
       });
       uptempo.doctor.specialtyValues = specialtyArray[0];
       uptempo.doctor.createCheckboxList("#doctor-specialties", uptempo.doctor.specialtyValues, "specialty");
     }
  }
  uptempo.ajax.getStaticList("COMMON", forWhat, successFn);
}

uptempo.doctor.getOfficeNameBy = function (key, setElement) {
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

uptempo.doctor.prepareCheckedTitlesAndSpecialities = function() {
  var titleIndex = 1;
  var specialtyIndex = 1;
  $("#doctor-lists").empty();
  $("#doctor-titles").find('input[type="checkbox"]').each(function() {
    if($(this).is(':checked')) {
      var titleValue = $(this).val();
      var hiddenTitle = $('<input/>',{type:'hidden',id:"doctor-title-element" + titleIndex, value: titleValue});
      hiddenTitle.appendTo("#doctor-lists");
      titleIndex++;
    }
  });
  uptempo.doctor.titleTotal = titleIndex;
  $("#doctor-specialties").find('input[type="checkbox"]').each(function() {
    if($(this).is(':checked')) {
      var specialtyValue = $(this).val();
      var hiddenSpecialty = $('<input/>',{type:'hidden',id:"doctor-specialty-element" + specialtyIndex, value: specialtyValue});
      hiddenSpecialty.appendTo("#doctor-lists");
      specialtyIndex++;
    }
  });
  uptempo.doctor.specialtyTotal = specialtyIndex;
};

uptempo.doctor.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#doctor-firstName").val();

  //*** On success, close the submission window and reload the table.
  var doctorSuccessFn = function() {
    $("#doctor-form").popup("close");
    uptempo.doctor.clearDoctorForm();
    uptempo.doctor.getDoctorData();
  };

  uptempo.doctor.resetValidFields();
  console.log(uptempo.doctor.validFields.length);
  
  // add hidden fields for checked title or specialty
  uptempo.doctor.prepareCheckedTitlesAndSpecialities();

  uptempo.doctor.addDynamicValidFields();
  
  uptempo.ajax.submitNew("Doctor",
                         "/service/doctor",
                         uptempo.doctor.validFields,
                         "doctor-firstName",
                         key,
                         doctorSuccessFn);
}

//*** Show the update application popup.
uptempo.doctor.showUpdate = function (valueKey) {
  uptempo.doctor.clearDoctorForm();
  $("#doctor-form-title").html("Update Doctor");
  $("#doctor-form-submit").changeButtonText("Update this Doctor");  
  $("#doctor-form-errors").html("");
  $("#doctor-key").val(valueKey);

  uptempo.doctor.clearDoctorForm();
  uptempo.ajax.fillDropdownWithOffices("doctor-billingOffice");

  uptempo.doctor.markAsUnchecked("#doctor-titles");
  uptempo.doctor.markAsUnchecked("#doctor-specialties");
  
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
          uptempo.doctor.listTitles = response.data.title;
          if (uptempo.doctor.listTitles == null){
            uptempo.doctor.listTitles = [];
          }
          uptempo.doctor.listSpecialities = response.data.specialty;
          if (uptempo.doctor.listSpecialities == null){
            uptempo.doctor.listSpecialities = [];
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

          uptempo.doctor.markAsChecked(uptempo.doctor.listTitles, "#doctor-titles");
          uptempo.doctor.markAsChecked(uptempo.doctor.listSpecialities, "#doctor-specialties");
          
        } else {
          alert(response.message);
        }
      }
    });

  $("#doctor-form-submit").off("click");
  $("#doctor-form-submit").on("click", uptempo.doctor.submitUpdate);
  //*** Show the form.
  $("#doctor-form").popup("open");
}

uptempo.doctor.markAsChecked = function(itemList, el) {
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
    uptempo.doctor.titleTotal = index;
  } else {
    uptempo.doctor.specialtyTotal = index;
  }
  $(el).find('input[type="checkbox"]').checkboxradio("refresh");  
} 

uptempo.doctor.markAsUnchecked = function(el) { 
  $(el).find('input[type="checkbox"]').each(function() {
    $(this).attr('checked', false);   
  });
  if (el == "#doctor-titles") {
    uptempo.doctor.titleTotal = 0;
  } else {
    uptempo.doctor.specialtyTotal = 0;
  }
  $(el).find('input[type="checkbox"]').checkboxradio("refresh");  
} 

uptempo.doctor.submitUpdate = function() {
  //*** Set the key for submission.
  var doctorKey = $("#doctor-key").val();

  uptempo.doctor.resetValidFields();

  // add hidden fields for checked title or specialty
  uptempo.doctor.prepareCheckedTitlesAndSpecialities();

  uptempo.doctor.addDynamicValidFields();
  uptempo.doctor.addClearValues();
  
  //*** On success, close the submission window and reload the table.
  var doctorUpdsuccessFn = function() {
    $("#doctor-form").popup("close");
    uptempo.doctor.clearDoctorForm();
    uptempo.doctor.getDoctorData();
  };

  $("#doctor-form").serialize();
  uptempo.ajax.submitUpdate("Doctor",
                            "/service/doctor/" + doctorKey,
                            uptempo.doctor.validFields,
                            "doctor-firstName",
                            doctorUpdsuccessFn);
}

uptempo.doctor.clearDoctorForm = function() {
  $("#doctor-billingOffice").val("");
  $("#doctor-firstName").val("");
  $("#doctor-lastName").val("");
  $("#doctor-email").val("");
  $("#doctor-education").val("");
  $("#doctor-publicDescription").val("");
  $("#doctor-notes").val("");

  $('#doctor-table-title-values').empty();
  $('#doctor-table-specialty-values').empty();
  $('#doctor-div-clear-titles').remove();
  $('#doctor-div-clear-specialties').remove();

}

uptempo.doctor.getDoctorData = function () {

  
  uptempo.loader.show("Getting Doctor data.");
  var appDataArray = ["No Doctor data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/doctor',
    data: "", //"format=obj",
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
      uptempo.activeTable = $('#doctor-table-data').dataTable({
        "aoColumns": uptempo.doctor.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.doctor.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });

}

uptempo.doctor.loadLists = function() {
  uptempo.doctor.getListDataForDoctors("TITLES");
  uptempo.doctor.getListDataForDoctors("SPECIALTIES");
}

uptempo.doctor.showDeleteConfirm = function(doctorKey) {

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
  $("#doctor-confirm-popup-delete").on("click", uptempo.doctor.deleteApp);

  //*** Show the form.
  $("#doctor-confirm-popup").popup("open");
}

uptempo.doctor.deleteApp = function() {
  var doctorKey = $("#doctor-key-delete").val();
  var doctorName = "(" + $("#doctor-Name-delete").val() + ")";
  var doctorMessage = doctorName;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#doctor-confirm-popup").popup("close");
    uptempo.doctor.getDoctorData();
  };
  uptempo.ajax.submitDelete(doctorKey, "/service/doctor/", "Doctor", doctorMessage, audDelSuccessFn);
}

// when iframe is used on the page this init is needed
// see JQuery mobile docs:
// http://jquerymobile.com/demos/1.2.0/docs/pages/popup/popup-iframes.html 
uptempo.doctor.initUploadPopup = function() {
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
$("#doctor").live('pageshow', uptempo.doctor.getDoctorData);
$("#doctor").live('pageshow', uptempo.util.pageTransition);
$("#doctor").live('pageshow', uptempo.doctor.loadLists);

$("#doctor").live('pageinit', uptempo.doctor.initUploadPopup);

uptempo.doctor.Photo = function(doctorKey) {
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

/**
 * Fills a dropdown with a list of doctors, with the value equal to the doctor key.
 */
uptempo.doctor.fillDropdownWithDoctors = function(dropdownId, officeKey) {
  var params = "";
  if (officeKey != null && officeKey != "") {
    params = "billingOffice=" + officeKey;
  }
  
  $.ajax({
    type: 'GET',
    url: '/service/doctor',
    data: params,
    success: function(response) {
      var drValueId = $("#" + dropdownId);
      drValueId.empty();
      //*** If the response was successful, show apps, otherwise show appropriate message.
      if (response.status == "SUCCESS") {
        var drData = response.data.values;
        $.each(drData, function(index, dr) {
          var drDisplay = dr['title'][0] + " " + dr['firstName'] + " " + dr['lastName'];
          drValueId.append("<option value='" + dr['key'] + "'>" + drDisplay + "</option>");
        });
        drValueId.selectmenu("refresh");
      } else {
        var drValues = "<select>" +
                    "<option value='NONE'> Could not get doctors, defaulting to NONE</option>" +
                    "</select>";
        drValueId.replaceWith(drValues)
      }
    }
  });
}

