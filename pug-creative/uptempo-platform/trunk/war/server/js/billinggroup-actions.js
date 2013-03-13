/* This file contains all admin billinggroup definition actions */
uptempo.billingGroups = {};
uptempo.billingGroups.tableHeadings = [
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
  {"sTitle": "Action", "aTargets": [12], "mData" : null}
];

//*** Field mapping for validation and naming.
uptempo.billingGroups.validFields =
[
  {name: "Group name", inputId: "#billinggroups-groupName", formVal: "groupName", required: true},
  {name: "Group address 1", inputId: "#billinggroups-groupAddress1", formVal: "groupAddress1", required: true},
  {name: "Group address 2", inputId: "#billinggroups-groupAddress2", formVal: "groupAddress2", required: false},
  {name: "Group city", inputId: "#billinggroups-groupCity", formVal: "groupCity", required: true},
  {name: "Group state", inputId: "#billinggroups-groupState", formVal: "groupState", required: true},
  {name: "Group postal code", inputId: "#billinggroups-groupPostalCode", formVal: "groupPostalCode", required: true},
  {name: "Group email", inputId: "#billinggroups-groupEmail", formVal: "groupEmail", required: false},
  {name: "Group notes", inputId: "#billinggroups-groupNotes", formVal: "groupNotes", required: false},
  {name: "Group hours", inputId: "#billinggroups-groupHours", formVal: "groupHours", required: false}
];

uptempo.billingGroups.resetValidFields = function( validFields ){
  validFields.splice( 0, validFields.length );
  validFields.push( {name: "Group name", inputId: "#billinggroups-groupName", formVal: "groupName", required: true} );
  validFields.push( {name: "Group address 1", inputId: "#billinggroups-groupAddress1", formVal: "groupAddress1", required: true} );
  validFields.push( {name: "Group address 2", inputId: "#billinggroups-groupAddress2", formVal: "groupAddress2", required: false} );
  validFields.push( {name: "Group city", inputId: "#billinggroups-groupCity", formVal: "groupCity", required: true} );
  validFields.push( {name: "Group state", inputId: "#billinggroups-groupState", formVal: "groupState", required: true} );
  validFields.push( {name: "Group postal code", inputId: "#billinggroups-groupPostalCode", formVal: "groupPostalCode", required: true} );
  validFields.push( {name: "Group email", inputId: "#billinggroups-groupEmail", formVal: "groupEmail", required: false} );
  validFields.push( {name: "Group notes", inputId: "#billinggroups-groupNotes", formVal: "groupNotes", required: false} );
  validFields.push( {name: "Group hours", inputId: "#billinggroups-groupHours", formVal: "groupHours", required: false} );
}

uptempo.billingGroups.addClearValues = function( validFields ){
  element = { name: "billinggroups-clear-phone-values-holder", inputId: "#billinggroups-clear-phone-values-holder", formVal: "clearPhone", required: false };
  validFields.push( element );
  element = { name: "billinggroups-clear-fax-values-holder", inputId: "#billinggroups-clear-fax-values-holder", formVal: "clearFax", required: false };
  validFields.push( element );
}

uptempo.billingGroups.addDynamicValidFields = function( validFields ){
  var startPhones = 1;
  var startFaxs = 1;
  if ( $('input[name=billinggroups-radio-clear-phones]:checked').val() == 'true' ){
    startPhones = 1;
  }
  else if ( $('input[name=billinggroups-radio-clear-phones]:checked').val() == 'false' ){
    startPhones = uptempo.billingGroups.listPhones.length + 1;
  }
  if ( $('input[name=billinggroups-radio-clear-faxes]:checked').val() == 'true' ){
    startFaxs = 1;
  }
  else if ( $('input[name=billinggroups-radio-clear-faxes]:checked').val() == 'false' ){
    startFaxs = uptempo.billingGroups.listFaxs.length + 1;
  }
  for( var i = startPhones; i <= uptempo.billingGroups.getPhonesCounter(); i ++ ){
    elementId = "#billinggroups-phone-element" + i;
    elementFormValue = "groupPhone" + i;
    element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push( element ); 
  }
  for( var i = startFaxs; i <= uptempo.billingGroups.getFaxsCounter(); i ++ ){
    elementId = "#billinggroups-fax-element" + i;
    elementFormValue = "groupFax" + i;
    element = { name: "Dynamic list text", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push( element ); 
  }
}

//*** Formats the billinggroup table.
uptempo.billingGroups.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"uptempo.billingGroups.showUpdate('" + aData[16] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.billingGroups.showDeleteConfirm('" + aData[16] + "');\">del</a>";
  var showPhones = "<a href='#' onclick=\"uptempo.util.showList('Phone', 'billinggroup', '" + aData[16] + "');\">show</a>&nbsp;&nbsp;";
  var showFaxs = "<a href='#' onclick=\"uptempo.util.showList('Fax', 'billinggroup', '" + aData[16] + "');\">show</a>&nbsp;&nbsp;";
  var showNotes = "<a href='#' onclick=\"uptempo.util.showList('Note', 'billinggroup', '" + aData[16] + "');\">show</a>&nbsp;&nbsp;";
  var showHours = "<a href='#' onclick=\"uptempo.util.showList('Hour', 'billinggroup', '" + aData[16] + "');\">show</a>&nbsp;&nbsp;";

  if ( aData[7] != null && aData[7].length > 0 ){
    $("td:eq(6)", nRow).html( showPhones );
  } else {
    $("td:eq(6)", nRow).html( '' );
  }

  if ( aData[8] != null && aData[8].length > 0 ){
    $("td:eq(7)", nRow).html( showFaxs );
  } else {
    $("td:eq(7)", nRow).html( '' );
  }

  if ( aData[9] != null && aData[9].length > 0 ){
    $("td:eq(8)", nRow).html(aData[9]);
  }

  if ( aData[10] != null && aData[10].length > 0 ){
    $("td:eq(9)", nRow).html( showNotes );
  } else {
    $("td:eq(9)", nRow).html( '' );
  }

  if ( aData[11] != null && aData[11].length > 0 ){
    $("td:eq(10)", nRow).html( showHours );
  } else {
    $("td:eq(10)", nRow).html( '' );
  }

  if ( aData[12] != null && aData[12].length > 0 ){
    $("td:eq(11)", nRow).html(aData[12]);
  }

  $("td:eq(12)", nRow).html(editLink + delLink);
};

uptempo.billingGroups.listPhonesCounter = 0;
uptempo.billingGroups.listFaxsCounter = 0;
uptempo.billingGroups.listPhones = [];
uptempo.billingGroups.listFaxs = [];

uptempo.billingGroups.setPhonesCounter = function( value ){
  uptempo.billingGroups.listPhonesCounter = value
}

uptempo.billingGroups.setFaxsCounter = function( value ){
  uptempo.billingGroups.listFaxsCounter = value
}

uptempo.billingGroups.getPhonesCounter = function(){
  return uptempo.billingGroups.listPhonesCounter;
}

uptempo.billingGroups.getFaxsCounter = function(){
  return uptempo.billingGroups.listFaxsCounter;
}

uptempo.billingGroups.addToFormListsFromResponse = function( responseList, whereToAdd, domElementId, readonly ) {
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

uptempo.billingGroups.addTextFieldAndIncreaseForOneValueCounter = function( itemValue, domElementId, readonly ){
    var id = 0;
    var placeholderValue = '';
    var idName = ''
    if ( domElementId == "#billinggroups-table-phone-values" ){
      id = uptempo.billingGroups.getPhonesCounter() + 1;
      uptempo.billingGroups.setPhonesCounter( id );
      placeholderValue = 'Phone value';
      idName = 'billinggroups-phone-element';
    }
    else if ( domElementId == "#billinggroups-table-fax-values" ){
      id = uptempo.billingGroups.getFaxsCounter() + 1;
      uptempo.billingGroups.setFaxsCounter( id );
      placeholderValue = 'Fax value';
      idName = 'billinggroups-fax-element';
    }
    item = '<input type="text" size="60" id="'+idName+id+'" value="'+itemValue+'" placeholder="'+placeholderValue+'" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset" />'
    elementToInsert = '<tr><td>' + item + '</td></tr>'
    $(domElementId).append(elementToInsert);
}

uptempo.billingGroups.addTextareaField = function( itemValue, domElementId, readonly ){
    item = '<textarea rows="10" cols="80" id="text-element" placeholder="" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset">'+itemValue+'</textarea>'
        elementToInsert = '<tr><td>' + item + '</td></tr>'
          $(domElementId).append(elementToInsert);
}

uptempo.billingGroups.showNew = function () {
  uptempo.billingGroups.clearBillinggroupsForm();
  uptempo.billingGroups.setPhonesCounter( 0 );
  uptempo.billingGroups.setFaxsCounter( 0 );

  //*** Setup the form.
  $("#billinggroups-form-title").html("New Billing Group");
  $("#billinggroups-form-submit").changeButtonText("Create this Billing Group");
  $("#billinggroups-form-submit").off("click");
  $("#billinggroups-form-submit").on("click", uptempo.billingGroups.submitNew);
  $("#billinggroups-form-errors").html("");
  //*** Show the form.
  $("#billinggroups-form").popup("open");
}

uptempo.billingGroups.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#billinggroups-groupName").val();

  //*** On success, close the submission window and reload the table.
  var billinggroupsSuccessFn = function() {
    $("#billinggroups-form").popup("close");
    uptempo.billingGroups.clearBillinggroupsForm();
    uptempo.billingGroups.getBillinggroupsData();
    uptempo.billingGroups.setPhonesCounter( 0 );
    uptempo.billingGroups.setFaxsCounter( 0 );
  };
  uptempo.billingGroups.resetValidFields( uptempo.billingGroups.validFields );
  uptempo.billingGroups.addDynamicValidFields( uptempo.billingGroups.validFields );
  $("#billinggroups-form").serialize();
  uptempo.ajax.submitNew("Billing Group",
                         "/service/billinggroup",
                         uptempo.billingGroups.validFields,
                         "billinggroups-groupName",
                         key,
                         billinggroupsSuccessFn);
}

//*** Show the update application popup.
uptempo.billingGroups.showUpdate = function (valueKey) {
  uptempo.billingGroups.clearBillinggroupsForm();
  uptempo.billingGroups.setPhonesCounter( 0 );
  uptempo.billingGroups.setFaxsCounter( 0 );
  $("#billinggroups-form-title").html("Update Billing Group");
  $("#billinggroups-form-submit").changeButtonText("Update this Billing Group");  
  $("#billinggroups-form-errors").html("");
  $("#billinggroups-key").val(valueKey);
  radioDivPhones = '<div id="billinggroups-div-clear-phones" style="display:none; data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="billinggroups-radio-clear-phones"><input type="radio" name="billinggroups-radio-clear-phones" value="false" onclick="uptempo.billingGroups.handleRadioButtonClick( "phones" );">Add phone numbers</label><label for="billinggroups-radio-clear-phones" style="margin:20px;"><input type="radio" name="billinggroups-radio-clear-phones" value="true" checked="checked" onclick="uptempo.billingGroups.handleRadioButtonClick( "phones" );">Replace phone numbers</label></div>';
  radioDivFaxs = '<div id="billinggroups-div-clear-faxes" style="display:none; data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="billinggroups-radio-clear-faxes"><input type="radio" name="billinggroups-radio-clear-faxes" value="false" onclick="uptempo.billingGroups.handleRadioButtonClick( "faxes" );">Add fax numbers</label><label for="billinggroups-radio-clear-faxes" style="margin:20px;"><input type="radio" name="billinggroups-radio-clear-faxes" value="true" checked="checked" onclick="uptempo.billingGroups.handleRadioButtonClick( "faxes" );">Replace faxes numbers</label></div>'; 

  $("#billinggroups-groupPostalCode").after( radioDivPhones );
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

          uptempo.billingGroups.listPhones = response.data.groupPhone;
          if ( uptempo.billingGroups.listPhones == null ){
            uptempo.billingGroups.listPhones = [];
          }
          uptempo.billingGroups.listFaxs = response.data.groupFax;
          if ( uptempo.billingGroups.listFaxs == null ){
            uptempo.billingGroups.listFaxs = [];
          }
          var groupEmail = response.data.groupEmail;
          var groupNotes = response.data.groupNotes;
          var groupHours = response.data.groupHours;

          $("#billinggroups-groupName").val(groupName);
          $("#billinggroups-groupAddress1").val(groupAddress1);
          $("#billinggroups-groupAddress2").val(groupAddress2);
          $("#billinggroups-groupState").val(groupState);
          $("#billinggroups-groupPostalCode").val(groupPostalCode);         
          $("#billinggroups-groupCity").val(groupCity);
          $("#billinggroups-groupNotes").val(groupNotes);
          $("#billinggroups-groupHours").val(groupHours);


          uptempo.billingGroups.addToFormListsFromResponse( uptempo.billingGroups.listPhones, uptempo.billingGroups.addTextFieldAndIncreaseForOneValueCounter, '#billinggroups-table-phone-values', '' );
          uptempo.billingGroups.addToFormListsFromResponse( uptempo.billingGroups.listFaxs, uptempo.billingGroups.addTextFieldAndIncreaseForOneValueCounter, '#billinggroups-table-fax-values', '' );
          
          $("#billinggroups-groupEmail").val(groupEmail);

        } else {
          alert(response.message);
        }
      }
    });

  $("#billinggroups-form-submit").off("click");
  $("#billinggroups-form-submit").on("click", uptempo.billingGroups.submitUpdate);
  //*** Show the form.
  $("#billinggroups-form").popup("open");
}



uptempo.billingGroups.handleRadioButtonClick = function( where ) {
  if ( where == 'phones' ){
    uptempo.billingGroups.setPhonesCounter( 0 );
    $('#billinggroups-table-phone-values').empty();
    if ( $('input[name=billinggroups-radio-clear-phones]:checked').val() == 'true' ){
      uptempo.billingGroups.addToFormListsFromResponse( uptempo.billingGroups.listPhones, uptempo.billingGroups.addTextFieldAndIncreaseForOneValueCounter , '#billinggroups-table-phone-values', '' );
    }
    else if ( $('input[name=billinggroups-radio-clear-phones]:checked').val() == 'false' ){
      uptempo.billingGroups.addToFormListsFromResponse( uptempo.billingGroups.listPhones, uptempo.billingGroups.addTextFieldAndIncreaseForOneValueCounter, '#billinggroups-table-phone-values', 'readonly' );
    }
  }
  else if ( where == 'faxes' ){
    uptempo.billingGroups.setFaxsCounter( 0 );
    $('#billinggroups-table-fax-values').empty();
    if ( $('input[name=billinggroups-radio-clear-faxes]:checked').val() == 'true' ){
      uptempo.billingGroups.addToFormListsFromResponse( uptempo.billingGroups.listFaxs, uptempo.billingGroups.addTextFieldAndIncreaseForOneValueCounter , '#billinggroups-table-fax-values', '' );    }
    else if ( $('input[name=billinggroups-radio-clear-faxes]:checked').val() == 'false' ){
      uptempo.billingGroups.addToFormListsFromResponse( uptempo.billingGroups.listFaxs, uptempo.billingGroups.addTextFieldAndIncreaseForOneValueCounter, '#billinggroups-table-fax-values', 'readonly' );
    }
  }
  return false;
}

uptempo.billingGroups.submitUpdate = function() {
  //*** Set the key for submission.
  $('#billinggroups-clear-phone-values-holder').val( $('input[name=billinggroups-radio-clear-phones]:checked').val() );
  $('#billinggroups-clear-fax-values-holder').val( $('input[name=billinggroups-radio-clear-faxes]:checked').val() );

  var billinggroupsKey = $("#billinggroups-key").val();
  uptempo.billingGroups.resetValidFields( uptempo.billingGroups.validFields );
  uptempo.billingGroups.addDynamicValidFields( uptempo.billingGroups.validFields );
  uptempo.billingGroups.addClearValues( uptempo.billingGroups.validFields );
  
  //*** On success, close the submission window and reload the table.
  var billinggroupsUpdsuccessFn = function() {

    $("#billinggroups-form").popup("close");
    uptempo.billingGroups.clearBillinggroupsForm();
    uptempo.billingGroups.getBillinggroupsData();
    uptempo.billingGroups.setPhonesCounter( 0 );
    uptempo.billingGroups.setFaxsCounter( 0 );
  };
  $("#billinggroups-form").serialize();
  uptempo.ajax.submitUpdate("Billing Group",
                            "/service/billinggroup/" + billinggroupsKey,
                            uptempo.billingGroups.validFields,
                            "billinggroups-groupName",
                            billinggroupsUpdsuccessFn);
}

uptempo.billingGroups.clearBillinggroupsForm = function() {
  $("#billinggroups-groupName").val("");
  $("#billinggroups-groupAddress1").val("");
  $("#billinggroups-groupAddress2").val("");
  $("#billinggroups-groupState").val("");
  $("#billinggroups-groupPostalCode").val("");  
  $("#billinggroups-groupCity").val("");
  $("#billinggroups-groupNotes").val("");
  $("#billinggroups-groupHours").val("");
  $("#billinggroups-groupEmail").val("");


  $('#billinggroups-table-phone-values').empty();
  $('#billinggroups-table-fax-values').empty();
  $('#billinggroups-div-clear-phones').remove();
  $('#billinggroups-div-clear-faxes').remove();

}

uptempo.billingGroups.getBillinggroupsData = function () {

  
  uptempo.loader.show("Getting Billing Group data.");
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
      uptempo.activeTable = $('#billinggroups-table-data').dataTable( {
        "aoColumnDefs": uptempo.billingGroups.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.billingGroups.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });

}


uptempo.billingGroups.showDeleteConfirm = function( billinggroupsKey ) {

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
  $("#billinggroups-confirm-popup-delete").on( "click", uptempo.billingGroups.deleteApp );

  //*** Show the form.
  $("#billinggroups-confirm-popup").popup("open");
}

uptempo.billingGroups.deleteApp = function() {
  var billinggroupsKey = $("#billinggroups-key-delete").val();
  var groupName = "(" + $("#billinggroups-groupName-delete").val() + ")";
  var billinggroupsMessage = groupName;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#billinggroups-confirm-popup").popup("close");
    uptempo.billingGroups.getBillinggroupsData();
  };
  uptempo.ajax.submitDelete(billinggroupsKey, "/service/billinggroup/", "Billinggroups", billinggroupsMessage, audDelSuccessFn);
}

uptempo.billingGroups.fillDropdownWithGroups = function (dropdownId, callbackFn) {
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
$("#billinggroups").live('pageshow', uptempo.billingGroups.getBillinggroupsData);
$("#billinggroups").live('pageshow', uptempo.util.pageTransition);
