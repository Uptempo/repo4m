/* This file contains all admin Billing office definition actions */
uptempo.billingOffices = {};
uptempo.billingOffices.tableHeadings = [
  {"sTitle": "Office group", "aTargets": [0]},
  {"sTitle": "Office name", "aTargets": [1]},
  {"sTitle": "Address", "aTargets": [2]},
  {"aTargets": [3], "bVisible": false},
  {"sTitle": "City", "aTargets": [4]},
  {"sTitle": "State", "aTargets": [5]},
  {"sTitle": "Postal code", "aTargets": [6]},
  {"sTitle": "Phones", "aTargets": [7], "mData" : null},
  {"sTitle": "Faxes", "aTargets": [8], "mData" : null},
  {"sTitle": "Email", "aTargets": [9]},
  {"sTitle": "Notes", "aTargets": [10], "mData" : null},
  {"sTitle": "Hours", "aTargets": [11], "mData" : null},
  {"sTitle": "created by", "aTargets": [12]},
  {"sTitle": "Action", "aTargets": [13], "mData" : null},
];

//*** Field mapping for validation and naming.
uptempo.billingOffices.validFields =
[
  {name: "Office group", inputId: "#billingoffices-officeGroup", formVal: "officeGroup", required: true},
  {name: "Office name", inputId: "#billingoffices-officeName", formVal: "officeName", required: true},
  {name: "Office address 1", inputId: "#billingoffices-officeAddress1", formVal: "officeAddress1", required: true},
  {name: "Office address 2", inputId: "#billingoffices-officeAddress2", formVal: "officeAddress2", required: false},
  {name: "Office city", inputId: "#billingoffices-officeCity", formVal: "officeCity", required: true},
  {name: "Office state", inputId: "#billingoffices-officeState", formVal: "officeState", required: true},
  {name: "Office postal code", inputId: "#billingoffices-officePostalCode", formVal: "officePostalCode", required: true},
  {name: "Office time zone", inputId: "#billingoffices-officeTimeZone", formVal: "officeTimeZone", required: true},  
  {name: "Office email", inputId: "#billingoffices-officeEmail", formVal: "officeEmail", required: true},
  {name: "Office notes", inputId: "#billingoffices-officeNotes", formVal: "officeNotes", required: false},
  {name: "Office hours", inputId: "#billingoffices-officeHours", formVal: "officeHours", required: false},
  {name: "Office logo URL", inputId: "#billingoffices-officeLogoURL", formVal: "officeLogoURL", required: false},
  {name: "Office website URL", inputId: "#billingoffices-officeSiteURL", formVal: "officeSiteURL", required: false},
  {name: "Office Facebook Page URL", inputId: "#billingoffices-officeFBURL", formVal: "officeFBURL", required: false},
  {name: "Office Google Analytics UA", inputId: "#billingoffices-officeAnalyticsUA", formVal: "officeAnalyticsUA", required: false}
];

uptempo.billingOffices.resetValidFields = function(validFields){
  validFields.splice(0, validFields.length);
  validFields.push({name: "Office group", inputId: "#billingoffices-officeGroup", formVal: "officeGroup", required: true});
  validFields.push({name: "Office name", inputId: "#billingoffices-officeName", formVal: "officeName", required: true});
  validFields.push({name: "Office address 1", inputId: "#billingoffices-officeAddress1", formVal: "officeAddress1", required: true});
  validFields.push({name: "Office address 2", inputId: "#billingoffices-officeAddress2", formVal: "officeAddress2", required: false});
  validFields.push({name: "Office city", inputId: "#billingoffices-officeCity", formVal: "officeCity", required: true});
  validFields.push({name: "Office state", inputId: "#billingoffices-officeState", formVal: "officeState", required: true});
  validFields.push({name: "Office postal code", inputId: "#billingoffices-officePostalCode", formVal: "officePostalCode", required: true}); 
  validFields.push({name: "Office time zone", inputId: "#billingoffices-officeTimeZone", formVal: "officeTimeZone", required: true}); 
  validFields.push({name: "Office email", inputId: "#billingoffices-officeEmail", formVal: "officeEmail", required: true});
  validFields.push({name: "Office notes", inputId: "#billingoffices-officeNotes", formVal: "officeNotes", required: false});
  validFields.push({name: "Office hours", inputId: "#billingoffices-officeHours", formVal: "officeHours", required: false});
  validFields.push({name: "Office logo URL", inputId: "#billingoffices-officeLogoURL", formVal: "officeLogoURL", required: false});
  validFields.push({name: "Office website URL", inputId: "#billingoffices-officeSiteURL", formVal: "officeSiteURL", required: false});
  validFields.push({name: "Office Facebook Page URL", inputId: "#billingoffices-officeFBURL", formVal: "officeFBURL", required: false});
  validFields.push({name: "Office Google Analytics UA", inputId: "#billingoffices-officeAnalyticsUA", formVal: "officeAnalyticsUA", required: false});
}

uptempo.billingOffices.addClearValues = function(validFields){
  var element = {name: "billingoffices-clear-phone-values-holder", inputId: "#billingoffices-clear-phone-values-holder", formVal: "clearPhone", required: true};
  validFields.push(element);
  element = { name: "billingoffices-clear-fax-values-holder", inputId: "#billingoffices-clear-fax-values-holder", formVal: "clearFax", required: false };
  validFields.push(element);
}

uptempo.billingOffices.addDynamicValidFields = function(validFields){
  var startPhones = 1;
  var startFaxs = 1;
  if ($('input[name=billingoffices-radio-clear-phones]:checked').val() == 'true'){
    startPhones = 1;
  }
  else if ($('input[name=billingoffices-radio-clear-phones]:checked').val() == 'false'){
    startPhones = uptempo.billingOffices.listPhones.length + 1;
  }
  if ($('input[name=billingoffices-radio-clear-faxes]:checked').val() == 'true'){
    startFaxs = 1;
  }
  else if ($('input[name=billingoffices-radio-clear-faxes]:checked').val() == 'false'){
    startFaxs = uptempo.billingOffices.listFaxs.length + 1;
  }
  for(var i = startPhones; i <= uptempo.billingOffices.getPhonesCounter(); i ++){
    elementId = "#billingoffices-phone-element" + i;
    elementFormValue = "officePhone" + i;
    element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push(element); 
  }
  for(var i = startFaxs; i <= uptempo.billingOffices.getFaxsCounter(); i ++){
    elementId = "#billingoffices-fax-element" + i;
    elementFormValue = "officeFax" + i;
    element = { name: "Dynamic list text", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push(element); 
  }
}

//*** Formats the billingoffice table.
uptempo.billingOffices.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var officeKeyPos = aData.length - 1;
  var editLink = "<a href='#' onclick=\"uptempo.billingOffices.showUpdate('" + aData[officeKeyPos] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.billingOffices.showDeleteConfirm('" + aData[officeKeyPos] + "');\">del</a>";
  var showPhones = "<a href='#' onclick=\"uptempo.util.showList('" + "Phone', 'billingoffice', '" + aData[officeKeyPos] + "');\">show</a>&nbsp;&nbsp;";
  var showFaxs = "<a href='#' onclick=\"uptempo.util.showList('" + "Fax', 'billingoffice', '" + aData[officeKeyPos] + "');\">show</a>&nbsp;&nbsp;";
  var showNotes = "<a href='#' onclick=\"uptempo.util.showList('" + "Note', 'billingoffice', '" + aData[officeKeyPos] + "');\">show</a>&nbsp;&nbsp;";
  var showHours = "<a href='#' onclick=\"uptempo.util.showList('" + "Hour', 'billingoffice', '" + aData[officeKeyPos] + "');\">show</a>&nbsp;&nbsp;";
  
  uptempo.billingOffices.getGroupNameBy(aData[0], $("td:eq(0)", nRow));

  var address = aData[2];
  if (aData[3] != null && aData[3].length > 0) {
    address += "<br />" + aData[3];
  }

  $("td:eq(2)", nRow).html(address);

  if (aData[9] != null && aData[9].length > 0){
    $("td:eq(6)", nRow).html(showPhones);
  } else {
    $("td:eq(6)", nRow).html('');
  }
  if (aData[10] != null && aData[10].length > 0){
    $("td:eq(7)", nRow).html(showFaxs);
  } else {
    $("td:eq(7)", nRow).html('');
  }

  if (aData[11] != null && aData[11].length > 0){
    $("td:eq(8)", nRow).html(aData[10]);
  }

  if (aData[12] != null && aData[12].length > 0){
    $("td:eq(9)", nRow).html(showNotes);
  } else {
    $("td:eq(9)", nRow).html('');
  }
  if (aData[13] != null && aData[13].length > 0){
    $("td:eq(10)", nRow).html(showHours);
  } else {
    $("td:eq(10)", nRow).html('');
  }

  if (aData[14] != null && aData[14].length > 0){
    $("td:eq(11)", nRow).html(aData[14]);
  }

  $("td:eq(12)", nRow).html(editLink + delLink);
};

uptempo.billingOffices.listPhonesCounter = 0;
uptempo.billingOffices.listFaxsCounter = 0;
uptempo.billingOffices.listPhones = [];
uptempo.billingOffices.listFaxs = [];

uptempo.billingOffices.setPhonesCounter = function(value){
  uptempo.billingOffices.listPhonesCounter = value
}

uptempo.billingOffices.setFaxsCounter = function(value){
  uptempo.billingOffices.listFaxsCounter = value
}

uptempo.billingOffices.getPhonesCounter = function(){
  return uptempo.billingOffices.listPhonesCounter;
}

uptempo.billingOffices.getFaxsCounter = function(){
  return uptempo.billingOffices.listFaxsCounter;
}

uptempo.billingOffices.addTableRow = function(value, tableName, rowCounter){
  var item = '<tr><td> id="' + tableName + rowCounter + '"' + value + '</td></tr>';
  $('#'+tableName).append(item);
}

uptempo.billingOffices.addTextareaField = function(itemValue, domElementId, readonly){
  item = '<textarea rows="10" cols="80" id="text-element" placeholder="" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset">'+itemValue+'</textarea>'
  elementToInsert = '<tr><td>' + item + '</td></tr>'
  $(domElementId).append(elementToInsert);
}

uptempo.billingOffices.addTextFieldAndIncreaseForOneValueCounter = function(itemValue, domElementId, readonly){
    var id = 0;
    var placeholderValue = '';
    var idName = ''
    if (domElementId == "#billingoffices-table-phone-values"){
      id = uptempo.billingOffices.getPhonesCounter() + 1;
      uptempo.billingOffices.setPhonesCounter(id);
      placeholderValue = 'Phone value';
      idName = 'billingoffices-phone-element';
    }
    else if (domElementId == "#billingoffices-table-fax-values"){
      id = uptempo.billingOffices.getFaxsCounter() + 1;
      uptempo.billingOffices.setFaxsCounter(id);
      placeholderValue = 'Fax value';
      idName = 'billingoffices-fax-element';
    }
    item = '<input type="text" size="60" id="'+idName+id+'" value="'+itemValue+'" placeholder="'+placeholderValue+'" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset" />'
    elementToInsert = '<tr><td>' + item + '</td></tr>'
    $(domElementId).append(elementToInsert);
}
uptempo.billingOffices.showNew = function () {
  uptempo.billingOffices.clearBillingofficesForm();
  uptempo.billingOffices.setPhonesCounter(0);
  uptempo.billingOffices.setFaxsCounter(0);
  uptempo.billingGroups.fillDropdownWithGroups("billingoffices-officeGroup");
  //*** Setup the form.
  $("#billingoffices-form-title").html("New Billing Office");
  $("#billingoffices-form-submit").changeButtonText("Create this Billing Office");
  $("#billingoffices-form-submit").off("click");
  $("#billingoffices-form-submit").on("click", uptempo.billingOffices.submitNew);
  $("#billingoffices-form-errors").html("");
  //*** Show the form.
  $("#billingoffices-form").popup("open");
}

uptempo.billingOffices.getGroupDataForOffices = function () {
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var groupDataArray = response.data.values;
        uptempo.billingOffices.loadGroupNameOptions(groupDataArray);
      } else {
        $(".status-bar").html("Failed to get billing group records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
}

uptempo.billingOffices.getGroupNameBy = function (key, setElement) {
  //*** Get the group data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup/'+key,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        if (response.message == ""){
          setElement.text("null").html();
        }
        else{
          setElement.text(response.data["groupName"]).html();
        }
      } else {
        $(".status-bar").html("Failed to get application records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
}

uptempo.billingOffices.loadGroupNameOptions = function(groupDataArray){
  $("#billingoffices-officeGroup").empty();
  var key = null;
  var value = null;
  for (group in groupDataArray) {
    value = groupDataArray[group][0];
    key = groupDataArray[group][18];
    $("#billingoffices-officeGroup")
          .append($('<option>', {value : key})
          .text(value));
  }
  $("#billingoffices-officeGroup").selectmenu('refresh');
}

uptempo.billingOffices.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#billingoffices-officeName").val();

  //*** On success, close the submission window and reload the table.
  var billingofficesSuccessFn = function() {
    $("#billingoffices-form").popup("close");
    uptempo.billingOffices.clearBillingofficesForm();
    uptempo.billingOffices.getBillingofficesData();
    uptempo.billingOffices.setPhonesCounter(0);
    uptempo.billingOffices.setFaxsCounter(0);
  };
  uptempo.billingOffices.resetValidFields(uptempo.billingOffices.validFields);
  uptempo.billingOffices.addDynamicValidFields(uptempo.billingOffices.validFields);
  $("#billingoffices-form").serialize();
  uptempo.ajax.submitNew("Billing Office",
                         "/service/billingoffice",
                         uptempo.billingOffices.validFields,
                         "billingoffices-officeName",
                         key,
                         billingofficesSuccessFn);
}

//*** Show the update application popup.
uptempo.billingOffices.showUpdate = function (valueKey) {
  uptempo.billingOffices.clearBillingofficesForm();
  uptempo.billingOffices.setPhonesCounter(0);
  uptempo.billingOffices.setFaxsCounter(0);
  $("#billingoffices-form-title").html("Update Billing Office");
  $("#billingoffices-form-submit").changeButtonText("Update this Billing Office");  
  $("#billingoffices-form-errors").html("");
  $("#billingoffices-key").val(valueKey);
  radioDivPhones = '<div id="billingoffices-div-clear-phones" style="display:none; data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="billingoffices-radio-clear-phones"><input type="radio" name="billingoffices-radio-clear-phones" value="false" onclick="uptempo.billingOffices.handleRadioButtonClick("phones");">Add phone numbers</label><label for="billingoffices-radio-clear-phones" style="margin:20px;"><input type="radio" name="billingoffices-radio-clear-phones" value="true" checked="checked" onclick="uptempo.billingOffices.handleRadioButtonClick("phones");">Replace phone numbers</label></div>';
  radioDivFaxs = '<div id="billingoffices-div-clear-faxes" style="display:none; data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="billingoffices-radio-clear-faxes"><input type="radio" name="billingoffices-radio-clear-faxes" value="false" onclick="uptempo.billingOffices.handleRadioButtonClick("faxes");">Add fax numbers</label><label for="billingoffices-radio-clear-faxes" style="margin:20px;"><input type="radio" name="billingoffices-radio-clear-faxes" value="true" checked="checked" onclick="uptempo.billingOffices.handleRadioButtonClick("faxes");">Replace faxes numbers</label></div>'; 

  $("#billingoffices-officePostalCode").after(radioDivPhones);
  $("#billingoffices-div-clear-phones").after(radioDivFaxs);
  //uptempo.billingOffices.getGroupDataForOffices();
  uptempo.billingGroups.fillDropdownWithGroups("billingoffices-officeGroup");
  //*** Submit the XHR request.
  $.ajax({
    type: 'GET',
    url: '/service/billingoffice/' + valueKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
        
          var officeName = response.data.officeName;
          var officeAddress1 = response.data.officeAddress1;
          var officeAddress2 = response.data.officeAddress2;
          var officeCity = response.data.officeCity;
          var officeState = response.data.officeState;
          var officePostalCode = response.data.officePostalCode;         
          uptempo.billingOffices.listPhones = response.data.officePhone;
          if (uptempo.billingOffices.listPhones == null){
            uptempo.billingOffices.listPhones = [];
          }
          uptempo.billingOffices.listFaxs = response.data.officeFax;
          if (uptempo.billingOffices.listFaxs == null){
            uptempo.billingOffices.listFaxs = [];
          }
          var officeEmail = response.data.officeEmail;
          var officeNotes = response.data.officeNotes;
          var officeHours = response.data.officeHours;

          $("#billingoffices-officeName").val(officeName);
          $("#billingoffices-officeAddress1").val(officeAddress1);
          $("#billingoffices-officeAddress2").val(officeAddress2);
          $("#billingoffices-officeState").val(officeState);
          $("#billingoffices-officePostalCode").val(officePostalCode);          
          $("#billingoffices-officeCity").val(officeCity);
          $("#billingoffices-officeNotes").val(officeNotes);
          $("#billingoffices-officeHours").val(officeHours);
          $("#billingoffices-officeLogoURL").val(response.data.officeLogoURL);
          $("#billingoffices-officeSiteURL").val(response.data.officeSiteURL);
          $("#billingoffices-officeFBURL").val(response.data.officeFBURL);
          $("#billingoffices-officeAnalyticsUA").val(response.data.officeAnalyticsUA);

          uptempo.billingOffices.addToFormListsFromResponse(uptempo.billingOffices.listPhones, uptempo.billingOffices.addTextFieldAndIncreaseForOneValueCounter, '#billingoffices-table-phone-values', '');
          uptempo.billingOffices.addToFormListsFromResponse(uptempo.billingOffices.listFaxs, uptempo.billingOffices.addTextFieldAndIncreaseForOneValueCounter, '#billingoffices-table-fax-values', '');
          
          $("#billingoffices-officeEmail").val(officeEmail);

        } else {
          alert(response.message);
        }
      }
    });

  $("#billingoffices-form-submit").off("click");
  $("#billingoffices-form-submit").on("click", uptempo.billingOffices.submitUpdate);
  //*** Show the form.
  $("#billingoffices-form").popup("open");
}

uptempo.billingOffices.addToFormListsFromResponse = function(responseList, whereToAdd, domElementId, readonly) {
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

uptempo.billingOffices.handleRadioButtonClick = function(where) {
  if (where == 'phones'){
    uptempo.billingOffices.setPhonesCounter(0);
    $('#billingoffices-table-phone-values').empty();
    if ($('input[name=billingoffices-radio-clear-phones]:checked').val() == 'true'){
      uptempo.billingOffices.addToFormListsFromResponse(uptempo.billingOffices.listPhones, uptempo.billingOffices.addTextFieldAndIncreaseForOneValueCounter , '#billingoffices-table-phone-values', '');
    }
    else if ($('input[name=billingoffices-radio-clear-phones]:checked').val() == 'false'){
      uptempo.billingOffices.addToFormListsFromResponse(uptempo.billingOffices.listPhones, uptempo.billingOffices.addTextFieldAndIncreaseForOneValueCounter, '#billingoffices-table-phone-values', 'readonly');
    }
  }
  else if (where == 'faxes'){
    uptempo.billingOffices.setFaxsCounter(0);
    $('#billingoffices-table-fax-values').empty();
    if ($('input[name=billingoffices-radio-clear-faxes]:checked').val() == 'true'){
      uptempo.billingOffices.addToFormListsFromResponse(uptempo.billingOffices.listFaxs, uptempo.billingOffices.addTextFieldAndIncreaseForOneValueCounter , '#billingoffices-table-fax-values', '');    }
    else if ($('input[name=billingoffices-radio-clear-faxes]:checked').val() == 'false'){
      uptempo.billingOffices.addToFormListsFromResponse(uptempo.billingOffices.listFaxs, uptempo.billingOffices.addTextFieldAndIncreaseForOneValueCounter, '#billingoffices-table-fax-values', 'readonly');
    }
  }
  return false;
}

uptempo.billingOffices.submitUpdate = function() {
  //*** Set the key for submission.
  $('#billingoffices-clear-phone-values-holder').val($('input[name=billingoffices-radio-clear-phones]:checked').val());
  $('#billingoffices-clear-fax-values-holder').val($('input[name=billingoffices-radio-clear-faxes]:checked').val());

  var billingofficesKey = $("#billingoffices-key").val();
  uptempo.billingOffices.resetValidFields(uptempo.billingOffices.validFields);
  uptempo.billingOffices.addDynamicValidFields(uptempo.billingOffices.validFields);
  uptempo.billingOffices.addClearValues(uptempo.billingOffices.validFields);
  
  //*** On success, close the submission window and reload the table.
  var billingofficesUpdsuccessFn = function() {

    $("#billingoffices-form").popup("close");
    uptempo.billingOffices.clearBillingofficesForm();
    uptempo.billingOffices.getBillingofficesData();
    uptempo.billingOffices.setPhonesCounter(0);
    uptempo.billingOffices.setFaxsCounter(0);
  };
  $("#billingoffices-form").serialize();
  uptempo.ajax.submitUpdate("Billing Office",
                            "/service/billingoffice/" + billingofficesKey,
                            uptempo.billingOffices.validFields,
                            "billingoffices-officeName",
                            billingofficesUpdsuccessFn);
}

uptempo.billingOffices.clearBillingofficesForm = function() {
  $("#billingoffices-officeGroup").val("");
  $("#billingoffices-officeName").val("");
  $("#billingoffices-officeAddress1").val("");
  $("#billingoffices-officeAddress2").val("");
  $("#billingoffices-officeState").val("");
  $("#billingoffices-officePostalCode").val("");  
  $("#billingoffices-officeCity").val("");
  $("#billingoffices-officeNotes").val("");
  $("#billingoffices-officeHours").val("");
  $("#billingoffices-officeEmail").val("");
  $('#billingoffices-table-phone-values').empty();
  $('#billingoffices-table-fax-values').empty();
  $('#billingoffices-div-clear-phones').remove();
  $('#billingoffices-div-clear-faxes').remove();
  $("#billingoffices-officeLogoURL").val("");
  $("#billingoffices-officeSiteURL").val("");
  $("#billingoffices-officeFBURL").val("");
  $("#billingoffices-officeAnalyticsUA").val("");
}

uptempo.billingOffices.getBillingofficesData = function () {
  uptempo.loader.show("Getting Billing Office data.");
  var appDataArray = ["No Billing Office data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billingoffice',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get Billing Office records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#billingoffices-table')
          .html('<table cellpadding="0" cellspacing="0" border="0" width="100%" class="entity-table" id="billingoffices-table-data"></table>');
      
      //*** Make this table the active one for row events.
      uptempo.activeTable = $('#billingoffices-table-data').dataTable({
        "aoColumnDefs": uptempo.billingOffices.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.billingOffices.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });

}


uptempo.billingOffices.showDeleteConfirm = function(billingofficesKey) {

  //*** Get the application code/name.
  $.ajax({
    type: 'GET',
    url: '/service/billingoffice/' + billingofficesKey,
    success: function(response) {
      if (response.status == "SUCCESS") {          
        officeName = response.data.officeName;
      }
      $("#billingoffices-officeName-delete").val(officeName);
      $("#billingoffices-confirm-popup-body")
          .html("Are you sure you want to delete Billing Office with name " + officeName + " ?");
    }
  });

  //*** Set the title and body.
  $("#billingoffices-confirm-popup-heading").html("Delete Billing Office name ?");
  $("#billingoffices-confirm-popup-action").html("Delete Billing Office name");
  $("#billingoffices-key-delete").val(billingofficesKey);
  $("#billingoffices-confirm-popup-delete").on("click", uptempo.billingOffices.deleteApp);

  //*** Show the form.
  $("#billingoffices-confirm-popup").popup("open");
}

uptempo.billingOffices.deleteApp = function() {
  var billingofficesKey = $("#billingoffices-key-delete").val();
  var officeName = "(" + $("#billingoffices-officeName-delete").val() + ")";
  var billingofficesMessage = officeName;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#billingoffices-confirm-popup").popup("close");
    uptempo.billingOffices.getBillingofficesData();
  };
  uptempo.ajax.submitDelete(billingofficesKey, "/service/billingoffice/", "Billingoffices", billingofficesMessage, audDelSuccessFn);
}

uptempo.office.fillDropdownWithOffices = function (dropdownId, callbackFn) {
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billingoffice',
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var officeDataArray = response.data.values;
        var officeValueId = $("#" + dropdownId);
        officeValueId.empty();
        if (officeDataArray.length == 0) {
          officeValueId.append("<option value=''>***No Offices Found!***</option>");
        }
        $.each(officeDataArray, function(index, office) {
          officeValueId.append(
              "<option value='" + office['key'] + "'>" + office['officeName'] +
              "(" + office['officeAddress1'] + ")</option>");
        })
        officeValueId.selectmenu("refresh");
        if (callbackFn != null) {
          callbackFn();
        }
      } else {
        var officeValues = "<select>" +
                      "<option value='DEFAULT'> Could not get offices, defaulting to DEFAULT</option>" +
                      "</select>";
        officeValueId.replaceWith(officeValues)
      }
    }
  });
};

//***When the user goes to this page, show the data table on load.
$("#billingoffices").live('pageshow', uptempo.billingOffices.getBillingofficesData);
$("#billingoffices").live('pageshow', uptempo.util.pageTransition);
