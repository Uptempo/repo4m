/* This file contains all admin billinggroup definition actions */
msAdmin.billingGroups = {};
msAdmin.billingGroups.tableHeadings = [
  {"sTitle": "Group name", "aTargets": [0]},
  {"sTitle": "Address 1", "aTargets": [1]},
  {"sTitle": "Address 2", "aTargets": [2]},
  {"sTitle": "City", "aTargets": [3]},
  {"sTitle": "State", "aTargets": [4]},
  {"sTitle": "Postal code", "aTargets": [5]},
  {"sTitle": "Phones", "aTargets": [6], "mData" : null},
  {"sTitle": "Faxs", "aTargets": [7], "mData" : null},
  {"sTitle": "Email", "aTargets": [8]},
  {"sTitle": "Notes", "aTargets": [9], "mData" : null},
  {"sTitle": "Hours", "aTargets": [10], "mData" : null},
  {"sTitle": "created by", "aTargets": [11]},
  {"sTitle": "Action", "aTargets": [12], "mData" : null},

];

//*** Field mapping for validation and naming.
msAdmin.billingGroups.validFields =
    [
     {name: "Group name", inputId: "#billinggroups-groupName", formVal: "groupName", required: true},
     {name: "Group address 1", inputId: "#billinggroups-groupAddress1", formVal: "groupAddress1", required: true},
     {name: "Group address 2", inputId: "#billinggroups-groupAddress2", formVal: "groupAddress2", required: false},
     {name: "Group city", inputId: "#billinggroups-groupCity", formVal: "groupCity", required: true},
     {name: "Group state", inputId: "#billinggroups-groupState", formVal: "groupState", required: true},
     {name: "Group postal code", inputId: "#billinggroups-groupPostalCode", formVal: "groupPostalCode", required: true},
     {name: "Group country", inputId: "#billinggroups-groupCountry", formVal: "groupCountry", required: true},
     {name: "Group email", inputId: "#billinggroups-groupEmail", formVal: "groupEmail", required: false},
     {name: "Group notes", inputId: "#billinggroups-groupNotes", formVal: "groupNotes", required: false},
     {name: "Group hours", inputId: "#billinggroups-groupHours", formVal: "groupHours", required: false},

     ];

msAdmin.billingGroups.resetValidFields = function( validFields ){
  validFields.splice( 0, validFields.length );
  validFields.push( {name: "Group name", inputId: "#billinggroups-groupName", formVal: "groupName", required: true} );
  validFields.push( {name: "Group address 1", inputId: "#billinggroups-groupAddress1", formVal: "groupAddress1", required: true} );
  validFields.push( {name: "Group address 2", inputId: "#billinggroups-groupAddress2", formVal: "groupAddress2", required: false} );
  validFields.push( {name: "Group city", inputId: "#billinggroups-groupCity", formVal: "groupCity", required: true} );
  validFields.push( {name: "Group state", inputId: "#billinggroups-groupState", formVal: "groupState", required: true} );
  validFields.push( {name: "Group postal code", inputId: "#billinggroups-groupPostalCode", formVal: "groupPostalCode", required: true} );
  validFields.push( {name: "Group country", inputId: "#billinggroups-groupCountry", formVal: "groupCountry", required: true} );
  validFields.push( {name: "Group email", inputId: "#billinggroups-groupEmail", formVal: "groupEmail", required: false} );
  validFields.push( {name: "Group notes", inputId: "#billinggroups-groupNotes", formVal: "groupNotes", required: false} );
  validFields.push( {name: "Group hours", inputId: "#billinggroups-groupHours", formVal: "groupHours", required: false} );
}

msAdmin.billingGroups.addClearValues = function( validFields ){
  element = { name: "billinggroups-clear-phone-values-holder", inputId: "#billinggroups-clear-phone-values-holder", formVal: "clearPhone", required: false };
  validFields.push( element );
  element = { name: "billinggroups-clear-fax-values-holder", inputId: "#billinggroups-clear-fax-values-holder", formVal: "clearFax", required: false };
  validFields.push( element );
}

msAdmin.billingGroups.addDynamicValidFields = function( validFields ){
  var startPhones = 1;
  var startFaxs = 1;
  if ( $('input[name=billinggroups-radio-clear-phones]:checked').val() == 'true' ){
    startPhones = 1;
  }
  else if ( $('input[name=billinggroups-radio-clear-phones]:checked').val() == 'false' ){
    startPhones = msAdmin.billingGroups.listPhones.length + 1;
  }
  if ( $('input[name=billinggroups-radio-clear-faxes]:checked').val() == 'true' ){
    startFaxs = 1;
  }
  else if ( $('input[name=billinggroups-radio-clear-faxes]:checked').val() == 'false' ){
    startFaxs = msAdmin.billingGroups.listFaxs.length + 1;
  }
  for( var i = startPhones; i <= msAdmin.billingGroups.getPhonesCounter(); i ++ ){
    elementId = "#billinggroups-phone-element" + i;
    elementFormValue = "groupPhone" + i;
    element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push( element ); 
  }
  for( var i = startFaxs; i <= msAdmin.billingGroups.getFaxsCounter(); i ++ ){
    elementId = "#billinggroups-fax-element" + i;
    elementFormValue = "groupFax" + i;
    element = { name: "Dynamic list text", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push( element ); 
  }
}

//*** Formats the billinggroup table.
msAdmin.billingGroups.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"msAdmin.billingGroups.showUpdate('" + aData[16] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"msAdmin.billingGroups.showDeleteConfirm('" + aData[16] + "');\">del</a>";
  var showPhones = "<a href='#' onclick=\"msAdmin.util.showList('Phone', 'billinggroup', '" + aData[16] + "');\">show</a>&nbsp;&nbsp;";
  var showFaxs = "<a href='#' onclick=\"msAdmin.util.showList('Fax', 'billinggroup', '" + aData[16] + "');\">show</a>&nbsp;&nbsp;";
  var showNotes = "<a href='#' onclick=\"msAdmin.util.showList('Note', 'billinggroup', '" + aData[16] + "');\">show</a>&nbsp;&nbsp;";
  var showHours = "<a href='#' onclick=\"msAdmin.util.showList('Hour', 'billinggroup', '" + aData[16] + "');\">show</a>&nbsp;&nbsp;";

  $("td:eq(0)", nRow).text( aData[0] ).html();
  $("td:eq(1)", nRow).text( aData[1] ).html();
  $("td:eq(2)", nRow).text( aData[2] ).html();
  $("td:eq(3)", nRow).text( aData[3] ).html();
  $("td:eq(4)", nRow).text( aData[4] ).html();
  $("td:eq(5)", nRow).text( aData[5] ).html();
  $("td:eq(8)", nRow).text( aData[9] ).html();
  $("td:eq(11)", nRow).text( aData[15] ).html();

  if ( aData[7] != null && aData[7].length > 0 ){
    $("td:eq(6)", nRow).html( showPhones );
  }
  else{
    $("td:eq(6)", nRow).html( '' );
  }
  if ( aData[8] != null && aData[8].length > 0 ){
    $("td:eq(7)", nRow).html( showFaxs );
  }
  else{
    $("td:eq(7)", nRow).html( '' );
  }
  if ( aData[9] != null && aData[9].length > 0 ){
    $("td:eq(9)", nRow).html( showNotes );
  }
  else{
    $("td:eq(9)", nRow).html( '' );
  }
  if ( aData[10] != null && aData[10].length > 0 ){
    $("td:eq(10)", nRow).html( showHours );
  }
  else{
    $("td:eq(10)", nRow).html( '' );
  }
  $("td:eq(12)", nRow).html(editLink + delLink);
};

msAdmin.billingGroups.listPhonesCounter = 0;
msAdmin.billingGroups.listFaxsCounter = 0;
msAdmin.billingGroups.listPhones = [];
msAdmin.billingGroups.listFaxs = [];

msAdmin.billingGroups.setPhonesCounter = function( value ){
  msAdmin.billingGroups.listPhonesCounter = value
}

msAdmin.billingGroups.setFaxsCounter = function( value ){
  msAdmin.billingGroups.listFaxsCounter = value
}

msAdmin.billingGroups.getPhonesCounter = function(){
  return msAdmin.billingGroups.listPhonesCounter;
}

msAdmin.billingGroups.getFaxsCounter = function(){
  return msAdmin.billingGroups.listFaxsCounter;
}

msAdmin.billingGroups.addToFormListsFromResponse = function( responseList, whereToAdd, domElementId, readonly ) {
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

msAdmin.billingGroups.addTextFieldAndIncreaseForOneValueCounter = function( itemValue, domElementId, readonly ){
    var id = 0;
    var placeholderValue = '';
    var idName = ''
    if ( domElementId == "#billinggroups-table-phone-values" ){
      id = msAdmin.billingGroups.getPhonesCounter() + 1;
      msAdmin.billingGroups.setPhonesCounter( id );
      placeholderValue = 'Phone value';
      idName = 'billinggroups-phone-element';
    }
    else if ( domElementId == "#billinggroups-table-fax-values" ){
      id = msAdmin.billingGroups.getFaxsCounter() + 1;
      msAdmin.billingGroups.setFaxsCounter( id );
      placeholderValue = 'Fax value';
      idName = 'billinggroups-fax-element';
    }
    item = '<input type="text" size="60" id="'+idName+id+'" value="'+itemValue+'" placeholder="'+placeholderValue+'" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset" />'
    elementToInsert = '<tr><td>' + item + '</td></tr>'
    $(domElementId).append(elementToInsert);
}

msAdmin.billingGroups.addTextareaField = function( itemValue, domElementId, readonly ){
    item = '<textarea rows="10" cols="80" id="text-element" placeholder="" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset">'+itemValue+'</textarea>'
        elementToInsert = '<tr><td>' + item + '</td></tr>'
          $(domElementId).append(elementToInsert);
}

msAdmin.billingGroups.showNew = function () {
  msAdmin.billingGroups.clearBillinggroupsForm();
  msAdmin.billingGroups.setPhonesCounter( 0 );
  msAdmin.billingGroups.setFaxsCounter( 0 );

  //*** Setup the form.
  $("#billinggroups-form-title").html("New Billing Group");
  $("#billinggroups-form-submit").changeButtonText("Create this Billing Group");
  $("#billinggroups-form-submit").off("click");
  $("#billinggroups-form-submit").on("click", msAdmin.billingGroups.submitNew);
  $("#billinggroups-form-errors").html("");
  //*** Show the form.
  $("#billinggroups-form").popup("open");
}

msAdmin.billingGroups.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#billinggroups-groupName").val();

  //*** On success, close the submission window and reload the table.
  var billinggroupsSuccessFn = function() {
    $("#billinggroups-form").popup("close");
    msAdmin.billingGroups.clearBillinggroupsForm();
    msAdmin.billingGroups.getBillinggroupsData();
    msAdmin.billingGroups.setPhonesCounter( 0 );
    msAdmin.billingGroups.setFaxsCounter( 0 );
  };
  msAdmin.billingGroups.resetValidFields( msAdmin.billingGroups.validFields );
  msAdmin.billingGroups.addDynamicValidFields( msAdmin.billingGroups.validFields );
  $("#billinggroups-form").serialize();
  msAdmin.ajax.submitNew("Billing Group",
                         "/service/billinggroup",
                         msAdmin.billingGroups.validFields,
                         "billinggroups-groupName",
                         key,
                         billinggroupsSuccessFn);
}

//*** Show the update application popup.
msAdmin.billingGroups.showUpdate = function (valueKey) {
  msAdmin.billingGroups.clearBillinggroupsForm();
  msAdmin.billingGroups.setPhonesCounter( 0 );
  msAdmin.billingGroups.setFaxsCounter( 0 );
  $("#billinggroups-form-title").html("Update Billing Group");
  $("#billinggroups-form-submit").changeButtonText("Update this Billing Group");  
  $("#billinggroups-form-errors").html("");
  $("#billinggroups-key").val(valueKey);
  radioDivPhones = '<div id="billinggroups-div-clear-phones" style="display:none; data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="billinggroups-radio-clear-phones"><input type="radio" name="billinggroups-radio-clear-phones" value="false" onclick="msAdmin.billingGroups.handleRadioButtonClick( "phones" );">Add phone numbers</label><label for="billinggroups-radio-clear-phones" style="margin:20px;"><input type="radio" name="billinggroups-radio-clear-phones" value="true" checked="checked" onclick="msAdmin.billingGroups.handleRadioButtonClick( "phones" );">Replace phone numbers</label></div>';
  radioDivFaxs = '<div id="billinggroups-div-clear-faxes" style="display:none; data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="billinggroups-radio-clear-faxes"><input type="radio" name="billinggroups-radio-clear-faxes" value="false" onclick="msAdmin.billingGroups.handleRadioButtonClick( "faxes" );">Add fax numbers</label><label for="billinggroups-radio-clear-faxes" style="margin:20px;"><input type="radio" name="billinggroups-radio-clear-faxes" value="true" checked="checked" onclick="msAdmin.billingGroups.handleRadioButtonClick( "faxes" );">Replace faxes numbers</label></div>'; 

  $("#billinggroups-groupCountry").after( radioDivPhones );
  $("#billinggroups-div-clear-phones").after( radioDivFaxs );
  //*** Submit the XHR request.
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup/' + valueKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
        
          var groupName = response.data.groupName;
          var groupAddress1 = response.data.groupAddress1;
          var groupAddress2 = response.data.groupAddress2;
          var groupCity = response.data.groupCity;
          var groupState = response.data.groupState;
          var groupPostalCode = response.data.groupPostalCode;
          var groupCountry = response.data.groupCountry;
          msAdmin.billingGroups.listPhones = response.data.groupPhone;
          if ( msAdmin.billingGroups.listPhones == null ){
            msAdmin.billingGroups.listPhones = [];
          }
          msAdmin.billingGroups.listFaxs = response.data.groupFax;
          if ( msAdmin.billingGroups.listFaxs == null ){
            msAdmin.billingGroups.listFaxs = [];
          }
          var groupEmail = response.data.groupEmail;
          var groupNotes = response.data.groupNotes;
          var groupHours = response.data.groupHours;

          $("#billinggroups-groupName").val(groupName);
          $("#billinggroups-groupAddress1").val(groupAddress1);
          $("#billinggroups-groupAddress2").val(groupAddress2);
          $("#billinggroups-groupState").val(groupState);
          $("#billinggroups-groupPostalCode").val(groupPostalCode);
          $("#billinggroups-groupCountry").val(groupCountry);
          $("#billinggroups-groupCity").val(groupCity);
          $("#billinggroups-groupNotes").val(groupNotes);
          $("#billinggroups-groupHours").val(groupHours);


          msAdmin.billingGroups.addToFormListsFromResponse( msAdmin.billingGroups.listPhones, msAdmin.billingGroups.addTextFieldAndIncreaseForOneValueCounter, '#billinggroups-table-phone-values', '' );
          msAdmin.billingGroups.addToFormListsFromResponse( msAdmin.billingGroups.listFaxs, msAdmin.billingGroups.addTextFieldAndIncreaseForOneValueCounter, '#billinggroups-table-fax-values', '' );
          
          $("#billinggroups-groupEmail").val(groupEmail);

        } else {
          alert(response.message);
        }
      }
    });

  $("#billinggroups-form-submit").off("click");
  $("#billinggroups-form-submit").on("click", msAdmin.billingGroups.submitUpdate);
  //*** Show the form.
  $("#billinggroups-form").popup("open");
}



msAdmin.billingGroups.handleRadioButtonClick = function( where ) {
  if ( where == 'phones' ){
    msAdmin.billingGroups.setPhonesCounter( 0 );
    $('#billinggroups-table-phone-values').empty();
    if ( $('input[name=billinggroups-radio-clear-phones]:checked').val() == 'true' ){
      msAdmin.billingGroups.addToFormListsFromResponse( msAdmin.billingGroups.listPhones, msAdmin.billingGroups.addTextFieldAndIncreaseForOneValueCounter , '#billinggroups-table-phone-values', '' );
    }
    else if ( $('input[name=billinggroups-radio-clear-phones]:checked').val() == 'false' ){
      msAdmin.billingGroups.addToFormListsFromResponse( msAdmin.billingGroups.listPhones, msAdmin.billingGroups.addTextFieldAndIncreaseForOneValueCounter, '#billinggroups-table-phone-values', 'readonly' );
    }
  }
  else if ( where == 'faxes' ){
    msAdmin.billingGroups.setFaxsCounter( 0 );
    $('#billinggroups-table-fax-values').empty();
    if ( $('input[name=billinggroups-radio-clear-faxes]:checked').val() == 'true' ){
      msAdmin.billingGroups.addToFormListsFromResponse( msAdmin.billingGroups.listFaxs, msAdmin.billingGroups.addTextFieldAndIncreaseForOneValueCounter , '#billinggroups-table-fax-values', '' );    }
    else if ( $('input[name=billinggroups-radio-clear-faxes]:checked').val() == 'false' ){
      msAdmin.billingGroups.addToFormListsFromResponse( msAdmin.billingGroups.listFaxs, msAdmin.billingGroups.addTextFieldAndIncreaseForOneValueCounter, '#billinggroups-table-fax-values', 'readonly' );
    }
  }
  return false;
}

msAdmin.billingGroups.submitUpdate = function() {
  //*** Set the key for submission.
  $('#billinggroups-clear-phone-values-holder').val( $('input[name=billinggroups-radio-clear-phones]:checked').val() );
  $('#billinggroups-clear-fax-values-holder').val( $('input[name=billinggroups-radio-clear-faxes]:checked').val() );

  var billinggroupsKey = $("#billinggroups-key").val();
  msAdmin.billingGroups.resetValidFields( msAdmin.billingGroups.validFields );
  msAdmin.billingGroups.addDynamicValidFields( msAdmin.billingGroups.validFields );
  msAdmin.billingGroups.addClearValues( msAdmin.billingGroups.validFields );
  
  //*** On success, close the submission window and reload the table.
  var billinggroupsUpdsuccessFn = function() {

    $("#billinggroups-form").popup("close");
    msAdmin.billingGroups.clearBillinggroupsForm();
    msAdmin.billingGroups.getBillinggroupsData();
    msAdmin.billingGroups.setPhonesCounter( 0 );
    msAdmin.billingGroups.setFaxsCounter( 0 );
  };
  $("#billinggroups-form").serialize();
  msAdmin.ajax.submitUpdate("Billing Group",
                            "/service/billinggroup/" + billinggroupsKey,
                            msAdmin.billingGroups.validFields,
                            "billinggroups-groupName",
                            billinggroupsUpdsuccessFn);
}

msAdmin.billingGroups.clearBillinggroupsForm = function() {
  $("#billinggroups-groupName").val("");
  $("#billinggroups-groupAddress1").val("");
  $("#billinggroups-groupAddress2").val("");
  $("#billinggroups-groupState").val("");
  $("#billinggroups-groupPostalCode").val("");
  $("#billinggroups-groupCountry").val("");
  $("#billinggroups-groupCity").val("");
  $("#billinggroups-groupNotes").val("");
  $("#billinggroups-groupHours").val("");
  $("#billinggroups-groupEmail").val("");


  $('#billinggroups-table-phone-values').empty();
  $('#billinggroups-table-fax-values').empty();
  $('#billinggroups-div-clear-phones').remove();
  $('#billinggroups-div-clear-faxes').remove();

}

msAdmin.billingGroups.getBillinggroupsData = function () {

  
  msAdmin.loader.show("Getting Billing Group data.");
  var appDataArray = ["No Billing Group data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get Billing Group records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#billinggroups-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="billinggroups-table-data"></table>' );
      //*** Make this table the active one for row events.
      msAdmin.activeTable = $('#billinggroups-table-data').dataTable( {
        "aoColumnDefs": msAdmin.billingGroups.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": msAdmin.billingGroups.tableFormatter,
        "bProcessing": true
      });
    },
    complete: msAdmin.loader.hide()
  });

}


msAdmin.billingGroups.showDeleteConfirm = function( billinggroupsKey ) {

  //*** Get the application code/name.
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup/' + billinggroupsKey,
    success: function(response) {
      if (response.status == "SUCCESS") {          
        groupName = response.data.groupName;
      }
      $("#billinggroups-groupName-delete").val(groupName);
      $("#billinggroups-confirm-popup-body")
          .html("Are you sure you want to delete Billing Group with name " + groupName + " ?");
    }
  });

  //*** Set the title and body.
  $("#billinggroups-confirm-popup-heading").html("Delete Billing Group name ?");
  $("#billinggroups-confirm-popup-action").html("Delete Billing Group name");
  $("#billinggroups-key-delete").val(billinggroupsKey);
  $("#billinggroups-confirm-popup-delete").on( "click", msAdmin.billingGroups.deleteApp );

  //*** Show the form.
  $("#billinggroups-confirm-popup").popup("open");
}

msAdmin.billingGroups.deleteApp = function() {
  var billinggroupsKey = $("#billinggroups-key-delete").val();
  var groupName = "(" + $("#billinggroups-groupName-delete").val() + ")";
  var billinggroupsMessage = groupName;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#billinggroups-confirm-popup").popup("close");
    msAdmin.billingGroups.getBillinggroupsData();
  };
  msAdmin.ajax.submitDelete(billinggroupsKey, "/service/billinggroup/", "Billinggroups", billinggroupsMessage, audDelSuccessFn);
}

msAdmin.billingGroups.fillDropdownWithGroups = function (dropdownId, callbackFn) {
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup',
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var groupDataArray = response.data.values;
        var groupValueId = $("#" + dropdownId);
        groupValueId.empty();
        if (groupDataArray.length == 0) {
          groupValueId.append("<option value=''>***No Groups Found!***</option>");
        }
        $.each(groupDataArray, function(index, group) {
          groupValueId.append(
              "<option value='" + group['key'] + "'>" + group['groupName'] +
              "(" + group['groupAddress1'] + ")</option>");
        })
        groupValueId.selectmenu("refresh");
        if (callbackFn != null) {
          callbackFn();
        }
      } else {
        var groupValues = "<select>" +
                      "<option value='DEFAULT'> Could not get groups, defaulting to DEFAULT</option>" +
                      "</select>";
        groupValueId.replaceWith(groupValues)
      }
    }
  });
};

//***When the user goes to this page, show the data table on load.
$("#billinggroups").live('pageshow', msAdmin.billingGroups.getBillinggroupsData);
$("#billinggroups").live('pageshow', msAdmin.util.pageTransition);
