/* This file contains all admin Billing office definition actions */
msAdmin.billingOffices = {};
msAdmin.billingOffices.tableHeadings = [
  {"sTitle": "Office group", "aTargets": [0]},
  {"sTitle": "Office name", "aTargets": [1]},
  {"sTitle": "Address 1", "aTargets": [2]},
  {"sTitle": "Address 2", "aTargets": [3]},
  {"sTitle": "City", "aTargets": [4]},
  {"sTitle": "State", "aTargets": [5]},
  {"sTitle": "Postal code", "aTargets": [6]},
  {"sTitle": "Phones", "aTargets": [7], "mData" : null},
  {"sTitle": "Email", "aTargets": [8]},
  {"sTitle": "Notes", "aTargets": [9], "mData" : null},
  {"sTitle": "Hours", "aTargets": [10], "mData" : null},
  {"sTitle": "created by", "aTargets": [11]},
  {"sTitle": "Action", "aTargets": [12], "mData" : null},
];

//*** Field mapping for validation and naming.
msAdmin.billingOffices.validFields =
[
  {name: "Office group", inputId: "#billingoffices-officeGroup", formVal: "officeGroup", required: true},
  {name: "Office name", inputId: "#billingoffices-officeName", formVal: "officeName", required: true},
  {name: "Office address 1", inputId: "#billingoffices-officeAddress1", formVal: "officeAddress1", required: true},
  {name: "Office address 2", inputId: "#billingoffices-officeAddress2", formVal: "officeAddress2", required: false},
  {name: "Office city", inputId: "#billingoffices-officeCity", formVal: "officeCity", required: true},
  {name: "Office state", inputId: "#billingoffices-officeState", formVal: "officeState", required: true},
  {name: "Office postal code", inputId: "#billingoffices-officePostalCode", formVal: "officePostalCode", required: true},    
  {name: "Office email", inputId: "#billingoffices-officeEmail", formVal: "officeEmail", required: false},
  {name: "Office notes", inputId: "#billingoffices-officeNotes", formVal: "officeNotes", required: false},
  {name: "Office hours", inputId: "#billingoffices-officeHours", formVal: "officeHours", required: false}
];

msAdmin.billingOffices.resetValidFields = function( validFields ){
  validFields.splice( 0, validFields.length );
  validFields.push( {name: "Office group", inputId: "#billingoffices-officeGroup", formVal: "officeGroup", required: true} );
  validFields.push( {name: "Office name", inputId: "#billingoffices-officeName", formVal: "officeName", required: true} );
  validFields.push( {name: "Office address 1", inputId: "#billingoffices-officeAddress1", formVal: "officeAddress1", required: true} );
  validFields.push( {name: "Office address 2", inputId: "#billingoffices-officeAddress2", formVal: "officeAddress2", required: false} );
  validFields.push( {name: "Office city", inputId: "#billingoffices-officeCity", formVal: "officeCity", required: true} );
  validFields.push( {name: "Office state", inputId: "#billingoffices-officeState", formVal: "officeState", required: true} );
  validFields.push( {name: "Office postal code", inputId: "#billingoffices-officePostalCode", formVal: "officePostalCode", required: true} );  
  validFields.push( {name: "Office email", inputId: "#billingoffices-officeEmail", formVal: "officeEmail", required: false} );
  validFields.push( {name: "Office notes", inputId: "#billingoffices-officeNotes", formVal: "officeNotes", required: false} );
  validFields.push( {name: "Office hours", inputId: "#billingoffices-officeHours", formVal: "officeHours", required: false} );
}

msAdmin.billingOffices.addClearValues = function( validFields ){
  var element = { name: "billingoffices-clear-phone-values-holder", inputId: "#billingoffices-clear-phone-values-holder", formVal: "clearPhone", required: false };
  validFields.push( element );
  element = { name: "billingoffices-clear-fax-values-holder", inputId: "#billingoffices-clear-fax-values-holder", formVal: "clearFax", required: false };
  validFields.push( element );
}

msAdmin.billingOffices.addDynamicValidFields = function( validFields ){
  var startPhones = 1;
  var startFaxs = 1;
  if ( $('input[name=billingoffices-radio-clear-phones]:checked').val() == 'true' ){
    startPhones = 1;
  }
  else if ( $('input[name=billingoffices-radio-clear-phones]:checked').val() == 'false' ){
    startPhones = msAdmin.billingOffices.listPhones.length + 1;
  }
  if ( $('input[name=billingoffices-radio-clear-faxes]:checked').val() == 'true' ){
    startFaxs = 1;
  }
  else if ( $('input[name=billingoffices-radio-clear-faxes]:checked').val() == 'false' ){
    startFaxs = msAdmin.billingOffices.listFaxs.length + 1;
  }
  for( var i = startPhones; i <= msAdmin.billingOffices.getPhonesCounter(); i ++ ){
    elementId = "#billingoffices-phone-element" + i;
    elementFormValue = "officePhone" + i;
    element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push( element ); 
  }
  for( var i = startFaxs; i <= msAdmin.billingOffices.getFaxsCounter(); i ++ ){
    elementId = "#billingoffices-fax-element" + i;
    elementFormValue = "officeFax" + i;
    element = { name: "Dynamic list text", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push( element ); 
  }
}

//*** Formats the billingoffice table.
msAdmin.billingOffices.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"msAdmin.billingOffices.showUpdate('" + aData[17] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"msAdmin.billingOffices.showDeleteConfirm('" + aData[17] + "');\">del</a>";
  var showPhones = "<a href='#' onclick=\"msAdmin.util.showList('" + "Phone', 'billingoffice', '" + aData[17] + "');\">show</a>&nbsp;&nbsp;";
  var showFaxs = "<a href='#' onclick=\"msAdmin.util.showList('" + "Fax', 'billingoffice', '" + aData[17] + "');\">show</a>&nbsp;&nbsp;";
  var showNotes = "<a href='#' onclick=\"msAdmin.util.showList('" + "Note', 'billingoffice', '" + aData[17] + "');\">show</a>&nbsp;&nbsp;";
  var showHours = "<a href='#' onclick=\"msAdmin.util.showList('" + "Hour', 'billingoffice', '" + aData[17] + "');\">show</a>&nbsp;&nbsp;";
  
  msAdmin.billingOffices.getGroupNameBy( aData[0], $("td:eq(0)", nRow) );

  if ( aData[8] != null && aData[8].length > 0 ){
    $("td:eq(7)", nRow).html( showPhones );
  }
  else{
    $("td:eq(7)", nRow).html( '' );
  }
  if ( aData[10] != null && aData[10].length > 0 ){
    $("td:eq(9)", nRow).html( showNotes );
  }
  else{
    $("td:eq(9)", nRow).html( '' );
  }
  if ( aData[11] != null && aData[11].length > 0 ){
    $("td:eq(10)", nRow).html( showHours );
  }
  else{
    $("td:eq(10)", nRow).html( '' );
  }
  $("td:eq(12)", nRow).html(editLink + delLink);
};

msAdmin.billingOffices.listPhonesCounter = 0;
msAdmin.billingOffices.listFaxsCounter = 0;
msAdmin.billingOffices.listPhones = [];
msAdmin.billingOffices.listFaxs = [];

msAdmin.billingOffices.setPhonesCounter = function( value ){
  msAdmin.billingOffices.listPhonesCounter = value
}

msAdmin.billingOffices.setFaxsCounter = function( value ){
  msAdmin.billingOffices.listFaxsCounter = value
}

msAdmin.billingOffices.getPhonesCounter = function(){
  return msAdmin.billingOffices.listPhonesCounter;
}

msAdmin.billingOffices.getFaxsCounter = function(){
  return msAdmin.billingOffices.listFaxsCounter;
}

msAdmin.billingOffices.addTableRow = function( value, tableName, rowCounter ){
  var item = '<tr><td> id="' + tableName + rowCounter + '"' + value + '</td></tr>';
  $('#'+tableName).append( item );
}

msAdmin.billingOffices.addTextareaField = function( itemValue, domElementId, readonly ){
  item = '<textarea rows="10" cols="80" id="text-element" placeholder="" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset">'+itemValue+'</textarea>'
  elementToInsert = '<tr><td>' + item + '</td></tr>'
  $(domElementId).append(elementToInsert);
}

msAdmin.billingOffices.addTextFieldAndIncreaseForOneValueCounter = function( itemValue, domElementId, readonly ){
    var id = 0;
    var placeholderValue = '';
    var idName = ''
    if ( domElementId == "#billingoffices-table-phone-values" ){
      id = msAdmin.billingOffices.getPhonesCounter() + 1;
      msAdmin.billingOffices.setPhonesCounter( id );
      placeholderValue = 'Phone value';
      idName = 'billingoffices-phone-element';
    }
    else if ( domElementId == "#billingoffices-table-fax-values" ){
      id = msAdmin.billingOffices.getFaxsCounter() + 1;
      msAdmin.billingOffices.setFaxsCounter( id );
      placeholderValue = 'Fax value';
      idName = 'billingoffices-fax-element';
    }
    item = '<input type="text" size="60" id="'+idName+id+'" value="'+itemValue+'" placeholder="'+placeholderValue+'" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset" />'
    elementToInsert = '<tr><td>' + item + '</td></tr>'
    $(domElementId).append(elementToInsert);
}
msAdmin.billingOffices.showNew = function () {
  msAdmin.billingOffices.clearBillingofficesForm();
  msAdmin.billingOffices.setPhonesCounter( 0 );
  msAdmin.billingOffices.setFaxsCounter( 0 );
  msAdmin.billingGroups.fillDropdownWithGroups("billingoffices-officeGroup");
  //*** Setup the form.
  $("#billingoffices-form-title").html("New Billing Office");
  $("#billingoffices-form-submit").changeButtonText("Create this Billing Office");
  $("#billingoffices-form-submit").off("click");
  $("#billingoffices-form-submit").on("click", msAdmin.billingOffices.submitNew);
  $("#billingoffices-form-errors").html("");
  //*** Show the form.
  $("#billingoffices-form").popup("open");
}

msAdmin.billingOffices.getGroupDataForOffices = function () {
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var groupDataArray = response.data.values;
        msAdmin.billingOffices.loadGroupNameOptions(groupDataArray);
      } else {
        $(".status-bar").html("Failed to get billing group records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
}

msAdmin.billingOffices.getGroupNameBy = function ( key, setElement ) {
  //*** Get the group data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup/'+key,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        if ( response.message == "" ){
          setElement.text( "null" ).html();
        }
        else{
          setElement.text( response.data[ "groupName" ] ).html();
        }
      } else {
        $(".status-bar").html("Failed to get application records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
}

msAdmin.billingOffices.loadGroupNameOptions = function(groupDataArray){
  $("#billingoffices-officeGroup").empty();
  var key = null;
  var value = null;
  for (group in groupDataArray) {
    value = groupDataArray[group][0];
    key = groupDataArray[group][18];
    $("#billingoffices-officeGroup")
          .append($('<option>', { value : key })
          .text(value));
  }
  $("#billingoffices-officeGroup").selectmenu('refresh');
}

msAdmin.billingOffices.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#billingoffices-officeName").val();

  //*** On success, close the submission window and reload the table.
  var billingofficesSuccessFn = function() {
    $("#billingoffices-form").popup("close");
    msAdmin.billingOffices.clearBillingofficesForm();
    msAdmin.billingOffices.getBillingofficesData();
    msAdmin.billingOffices.setPhonesCounter( 0 );
    msAdmin.billingOffices.setFaxsCounter( 0 );
  };
  msAdmin.billingOffices.resetValidFields( msAdmin.billingOffices.validFields );
  msAdmin.billingOffices.addDynamicValidFields( msAdmin.billingOffices.validFields );
  $("#billingoffices-form").serialize();
  msAdmin.ajax.submitNew("Billing Office",
                         "/service/billingoffice",
                         msAdmin.billingOffices.validFields,
                         "billingoffices-officeName",
                         key,
                         billingofficesSuccessFn);
}

//*** Show the update application popup.
msAdmin.billingOffices.showUpdate = function (valueKey) {
  msAdmin.billingOffices.clearBillingofficesForm();
  msAdmin.billingOffices.setPhonesCounter( 0 );
  msAdmin.billingOffices.setFaxsCounter( 0 );
  $("#billingoffices-form-title").html("Update Billing Office");
  $("#billingoffices-form-submit").changeButtonText("Update this Billing Office");  
  $("#billingoffices-form-errors").html("");
  $("#billingoffices-key").val(valueKey);
  radioDivPhones = '<div id="billingoffices-div-clear-phones" style="display:none; data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="billingoffices-radio-clear-phones"><input type="radio" name="billingoffices-radio-clear-phones" value="false" onclick="msAdmin.billingOffices.handleRadioButtonClick( "phones" );">Add phone numbers</label><label for="billingoffices-radio-clear-phones" style="margin:20px;"><input type="radio" name="billingoffices-radio-clear-phones" value="true" checked="checked" onclick="msAdmin.billingOffices.handleRadioButtonClick( "phones" );">Replace phone numbers</label></div>';
  radioDivFaxs = '<div id="billingoffices-div-clear-faxes" style="display:none; data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="billingoffices-radio-clear-faxes"><input type="radio" name="billingoffices-radio-clear-faxes" value="false" onclick="msAdmin.billingOffices.handleRadioButtonClick( "faxes" );">Add fax numbers</label><label for="billingoffices-radio-clear-faxes" style="margin:20px;"><input type="radio" name="billingoffices-radio-clear-faxes" value="true" checked="checked" onclick="msAdmin.billingOffices.handleRadioButtonClick( "faxes" );">Replace faxes numbers</label></div>'; 

  $("#billingoffices-officePostalCode").after( radioDivPhones );
  $("#billingoffices-div-clear-phones").after( radioDivFaxs );
  //msAdmin.billingOffices.getGroupDataForOffices();
  msAdmin.billingGroups.fillDropdownWithGroups("billingoffices-officeGroup");
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
          msAdmin.billingOffices.listPhones = response.data.officePhone;
          if ( msAdmin.billingOffices.listPhones == null ){
            msAdmin.billingOffices.listPhones = [];
          }
          msAdmin.billingOffices.listFaxs = response.data.officeFax;
          if ( msAdmin.billingOffices.listFaxs == null ){
            msAdmin.billingOffices.listFaxs = [];
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


          msAdmin.billingOffices.addToFormListsFromResponse( msAdmin.billingOffices.listPhones, msAdmin.billingOffices.addTextFieldAndIncreaseForOneValueCounter, '#billingoffices-table-phone-values', '' );
          msAdmin.billingOffices.addToFormListsFromResponse( msAdmin.billingOffices.listFaxs, msAdmin.billingOffices.addTextFieldAndIncreaseForOneValueCounter, '#billingoffices-table-fax-values', '' );
          
          $("#billingoffices-officeEmail").val(officeEmail);

        } else {
          alert(response.message);
        }
      }
    });

  $("#billingoffices-form-submit").off("click");
  $("#billingoffices-form-submit").on("click", msAdmin.billingOffices.submitUpdate);
  //*** Show the form.
  $("#billingoffices-form").popup("open");
}

msAdmin.billingOffices.addToFormListsFromResponse = function( responseList, whereToAdd, domElementId, readonly ) {
  var len = 0;
  if ( responseList != null ){
    len = responseList.length;
  }
  for ( var i=0; i<len; ++i ) {
    if ( i in responseList ) {
      var item = responseList[ i ];
      whereToAdd( item, domElementId, readonly );
    }
  }
}

msAdmin.billingOffices.handleRadioButtonClick = function( where ) {
  if ( where == 'phones' ){
    msAdmin.billingOffices.setPhonesCounter( 0 );
    $('#billingoffices-table-phone-values').empty();
    if ( $('input[name=billingoffices-radio-clear-phones]:checked').val() == 'true' ){
      msAdmin.billingOffices.addToFormListsFromResponse( msAdmin.billingOffices.listPhones, msAdmin.billingOffices.addTextFieldAndIncreaseForOneValueCounter , '#billingoffices-table-phone-values', '' );
    }
    else if ( $('input[name=billingoffices-radio-clear-phones]:checked').val() == 'false' ){
      msAdmin.billingOffices.addToFormListsFromResponse( msAdmin.billingOffices.listPhones, msAdmin.billingOffices.addTextFieldAndIncreaseForOneValueCounter, '#billingoffices-table-phone-values', 'readonly' );
    }
  }
  else if ( where == 'faxes' ){
    msAdmin.billingOffices.setFaxsCounter( 0 );
    $('#billingoffices-table-fax-values').empty();
    if ( $('input[name=billingoffices-radio-clear-faxes]:checked').val() == 'true' ){
      msAdmin.billingOffices.addToFormListsFromResponse( msAdmin.billingOffices.listFaxs, msAdmin.billingOffices.addTextFieldAndIncreaseForOneValueCounter , '#billingoffices-table-fax-values', '' );    }
    else if ( $('input[name=billingoffices-radio-clear-faxes]:checked').val() == 'false' ){
      msAdmin.billingOffices.addToFormListsFromResponse( msAdmin.billingOffices.listFaxs, msAdmin.billingOffices.addTextFieldAndIncreaseForOneValueCounter, '#billingoffices-table-fax-values', 'readonly' );
    }
  }
  return false;
}

msAdmin.billingOffices.submitUpdate = function() {
  //*** Set the key for submission.
  $('#billingoffices-clear-phone-values-holder').val( $('input[name=billingoffices-radio-clear-phones]:checked').val() );
  $('#billingoffices-clear-fax-values-holder').val( $('input[name=billingoffices-radio-clear-faxes]:checked').val() );

  var billingofficesKey = $("#billingoffices-key").val();
  msAdmin.billingOffices.resetValidFields( msAdmin.billingOffices.validFields );
  msAdmin.billingOffices.addDynamicValidFields( msAdmin.billingOffices.validFields );
  msAdmin.billingOffices.addClearValues( msAdmin.billingOffices.validFields );
  
  //*** On success, close the submission window and reload the table.
  var billingofficesUpdsuccessFn = function() {

    $("#billingoffices-form").popup("close");
    msAdmin.billingOffices.clearBillingofficesForm();
    msAdmin.billingOffices.getBillingofficesData();
    msAdmin.billingOffices.setPhonesCounter( 0 );
    msAdmin.billingOffices.setFaxsCounter( 0 );
  };
  $("#billingoffices-form").serialize();
  msAdmin.ajax.submitUpdate("Billing Office",
                            "/service/billingoffice/" + billingofficesKey,
                            msAdmin.billingOffices.validFields,
                            "billingoffices-officeName",
                            billingofficesUpdsuccessFn);
}

msAdmin.billingOffices.clearBillingofficesForm = function() {
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
}

msAdmin.billingOffices.getBillingofficesData = function () {
  msAdmin.loader.show("Getting Billing Office data.");
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
      $('#billingoffices-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="billingoffices-table-data"></table>' );
      //*** Make this table the active one for row events.
      msAdmin.activeTable = $('#billingoffices-table-data').dataTable( {
        "aoColumnDefs": msAdmin.billingOffices.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": msAdmin.billingOffices.tableFormatter,
        "bProcessing": true
      });
    },
    complete: msAdmin.loader.hide()
  });

}


msAdmin.billingOffices.showDeleteConfirm = function( billingofficesKey ) {

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
  $("#billingoffices-confirm-popup-delete").on( "click", msAdmin.billingOffices.deleteApp );

  //*** Show the form.
  $("#billingoffices-confirm-popup").popup("open");
}

msAdmin.billingOffices.deleteApp = function() {
  var billingofficesKey = $("#billingoffices-key-delete").val();
  var officeName = "(" + $("#billingoffices-officeName-delete").val() + ")";
  var billingofficesMessage = officeName;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#billingoffices-confirm-popup").popup("close");
    msAdmin.billingOffices.getBillingofficesData();
  };
  msAdmin.ajax.submitDelete(billingofficesKey, "/service/billingoffice/", "Billingoffices", billingofficesMessage, audDelSuccessFn);
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
$("#billingoffices").live('pageshow', msAdmin.billingOffices.getBillingofficesData);
$("#billingoffices").live('pageshow', msAdmin.util.pageTransition);
