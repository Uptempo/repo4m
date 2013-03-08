/* This file contains all admin staticlists definition actions */
msAdmin.staticLists = {};

msAdmin.staticLists.listValueCounter = 0;
msAdmin.staticLists.listTextCounter = 0;
msAdmin.staticLists.listValues = [];
msAdmin.staticLists.listTexts = [];

msAdmin.staticLists.setListValueCounter = function( value ){
  msAdmin.staticLists.listValueCounter = value
}

msAdmin.staticLists.setListTextCounter = function( value ){
  msAdmin.staticLists.listTextCounter = value
}

msAdmin.staticLists.getListValueCounter = function(){
  return msAdmin.staticLists.listValueCounter;
}

msAdmin.staticLists.getListTextCounter = function(){
  return msAdmin.staticLists.listTextCounter;
}

msAdmin.staticLists.tableHeadings = [
  {"sTitle": "App Code", "aTargets": [0]},
  {"sTitle": "List Code", "aTargets": [1]},
  {"sTitle": "List Key", "aTargets": [2]},
  {"sTitle": "List Values", "aTargets": [3], "mData" : null},
  {"sTitle": "List Text",  "aTargets": [4], "mData" : null},
  {"sTitle": "Created By", "aTargets": [5]},
  {"sTitle": "Modified By", "bVisible": false, "aTargets": [6]},
  {"sTitle": "Create Date", "bVisible": false, "aTargets": [7]},  
  {"sTitle": "Modify Date", "bVisible": false, "aTargets": [8]},
  {"sTitle": "Acess Key", "bVisible": false, "aTargets": [9]},
  {"sTitle": "Action", "aTargets": [10], "mData" : null},
];

//*** Field mapping for validation and naming.
msAdmin.staticLists.validFields =
    [
     {name: "List Code", inputId: "#list-code", formVal: "listCode", required: true},     
     {name: "App Code", inputId: "#staticlists-apps-code", formVal: "listApp", required: true},
     {name: "List Key", inputId: "#list-key", formVal: "listKey", required: true},

     ];

msAdmin.staticLists.resetValidFields = function( validFields ){
  validFields.splice( 0, validFields.length );
  validFields.push( {name: "List Code", inputId: "#list-code", formVal: "listCode", required: true} );
  validFields.push( {name: "App Code", inputId: "#staticlists-apps-code", formVal: "listApp", required: true} );
  validFields.push( {name: "List Key", inputId: "#list-key", formVal: "listKey", required: true} );
}

msAdmin.staticLists.addClearValues = function( validFields ){
  element = { name: "ClearValues", inputId: "#clear-values-holder", formVal: "clearValues", required: false };
  validFields.push( element );
}

msAdmin.staticLists.addDynamicValidFields = function( validFields ){
  var startList = 1;
  var startText = 1;
  if ( $('input[name=clear-values]:checked').val() == 'true' ){
    startList = 1;
    startText = 1;
  }
  else if ( $('input[name=clear-values]:checked').val() == 'false' ){
    startList = msAdmin.staticLists.listValues.length + 1;
    startText = msAdmin.staticLists.listTexts.length + 1;
  }
  for( var i = startList; i <= msAdmin.staticLists.getListValueCounter(); i ++ ){
    elementId = "#list-value-element" + i;
    elementFormValue = "listValue" + i;
    element = { name: "Dynamic list value", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push( element ); 
  }
  for( var i = startText; i <= msAdmin.staticLists.getListTextCounter(); i ++ ){
    elementId = "#list-text-element" + i;
    elementFormValue = "listText" + i;
    element = { name: "Dynamic list text", inputId: elementId, formVal: elementFormValue, required: false }; 
    validFields.push( element ); 
  }
}

//*** Formats the staticlist table.
msAdmin.staticLists.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"msAdmin.staticLists.showUpdate('" + aData[9] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"msAdmin.staticLists.showDeleteConfirm('" + aData[9] + "');\">del</a>";
  var showListValues = "<a href='#' onclick=\"msAdmin.util.showList('Value', 'staticlist', '" + aData[9] + "');\">show</a>&nbsp;&nbsp;";
  var showListTexts = "<a href='#' onclick=\"msAdmin.util.showList('Text', 'staticlist', '" + aData[9] + "');\">show</a>&nbsp;&nbsp;";

  $("td:eq(0)", nRow).text( aData[0] ).html();
  $("td:eq(1)", nRow).text( aData[1] ).html();
  $("td:eq(2)", nRow).text( aData[2] ).html();
  $("td:eq(5)", nRow).text( aData[5] ).html();
  if ( aData[3] != null && aData[3].length > 0 ){
    $("td:eq(3)", nRow).html( showListValues );
  }
  else{
    $("td:eq(3)", nRow).html( '' );
  }
  if ( aData[4] != null && aData[4].length > 0 ){
    $("td:eq(4)", nRow).html( showListTexts );
  }
  else{
    $("td:eq(4)", nRow).html( '' );
  } 
  $("td:eq(6)", nRow).html(editLink + delLink);
};

loadAppCodeOptions = function(appDataArray){
  $("#staticlists-apps-code").empty();
  var key = null;
  var value = null;
  for (field in appDataArray) {
    key = appDataArray[field][0];
    value = appDataArray[field][1];
    $("#staticlists-apps-code")
          .append($('<option>', { value : key })
          .text(value));
  }
  $("#staticlists-apps-code").selectmenu('refresh');
}

msAdmin.staticLists.addListValueAndIncreaseForOneValueCounter = function( itemValue, domElementId, readonly ){
  id = msAdmin.staticLists.getListValueCounter() + 1;
  msAdmin.staticLists.setListValueCounter( id );
  item = '<input type="text" size="60" id="list-value-element'+id+'" value="'+itemValue+'" placeholder="List Value" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset" />'
  elementToInsert = '<tr><td>' + item + '</td></tr>'
  $(domElementId).append(elementToInsert);
}

msAdmin.staticLists.addListTextAndIncreaseForOneTextCounter = function( itemValue, domElementId, readonly ){
  id = msAdmin.staticLists.getListTextCounter() + 1;
  msAdmin.staticLists.setListTextCounter( id );
  item = '<textarea rows="10" cols="80" id="list-text-element'+id+'" placeholder="List Text" '+readonly+' data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset">'+itemValue+'</textarea>'
  elementToInsert = '<tr><td>' + item + '</td></tr>'
  $(domElementId).append(elementToInsert);
}

msAdmin.staticLists.showNew = function () {
  msAdmin.staticLists.clearStaticlistsForm();
  msAdmin.staticLists.setListValueCounter( 0 );
  msAdmin.staticLists.setListTextCounter( 0 );
  myArray = msAdmin.staticLists.getAppDataForStaticlists();

  //*** Setup the form.
  $("#staticlists-form-title").html("New Staticlists");
  $("#staticlists-form-submit").changeButtonText("Create this staticlists");
  $("#staticlists-form-submit").off("click");
  $("#staticlists-form-submit").on("click", msAdmin.staticLists.submitNew);
  $("#staticlists-form-errors").html("");
  //*** Show the form.
  $("#staticlists-form").popup("open");
}

msAdmin.staticLists.submitNew = function () {
  //*** Set the key for submission.
  var key = $("#list-code").val();

  //*** On success, close the submission window and reload the table.
  var staticlistsSuccessFn = function() {
    $("#staticlists-form").popup("close");
    msAdmin.staticLists.clearStaticlistsForm();
    msAdmin.staticLists.getStaticlistsData();
    msAdmin.staticLists.setListValueCounter( 0 );
    msAdmin.staticLists.setListTextCounter( 0 );
  };
  msAdmin.staticLists.resetValidFields( msAdmin.staticLists.validFields );
  msAdmin.staticLists.addDynamicValidFields( msAdmin.staticLists.validFields );
  $("#staticlists-form").serialize();
  msAdmin.ajax.submitNew("Staticlists",
                         "/service/staticlist",
                         msAdmin.staticLists.validFields,
                         "list-code",
                         key,
                         staticlistsSuccessFn);
}

//*** Show the update application popup.
msAdmin.staticLists.showUpdate = function (valueKey) {
  msAdmin.staticLists.clearStaticlistsForm();
  msAdmin.staticLists.setListValueCounter( 0 );
  msAdmin.staticLists.setListTextCounter( 0 );
  $("#staticlists-form-title").html("Update Staticlist");
  $("#staticlists-form-submit").changeButtonText("Update this staticlist");  
  $("#staticlists-form-errors").html("");
  $("#staticlists-key").val(valueKey);
  radioDiv = '<div id="div-clear-values" data-theme="a" class="ui-input-text ui-body-a ui-corner-all ui-shadow-inset"><label for="clear-values"><input type="radio" name="clear-values" value="false" onclick="msAdmin.staticLists.handleRadioButtonClick();">Add list elements</label><label for="clear-values" style="margin:20px;"><input type="radio" name="clear-values" value="true" checked="checked" onclick="msAdmin.staticLists.handleRadioButtonClick();">Replace list elements</label></div>'; 
  $("#list-key").after( radioDiv );
  msAdmin.staticLists.getAppDataForStaticlists();
  //*** Get the data for this application.

  //*** Submit the XHR request.
  $.ajax({
    type: 'GET',
    url: '/service/staticlist/' + valueKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
        
          var listCode = response.data.listCode;
          var listApp = response.data.listApp;
          var listKey = response.data.listKey;
          msAdmin.staticLists.listValues = response.data.listValue;
          if ( msAdmin.staticLists.listValues == null ){
            msAdmin.staticLists.listValues = [];
          }
          msAdmin.staticLists.listTexts = response.data.listText;
          if ( msAdmin.staticLists.listTexts == null ){
            msAdmin.staticLists.listTexts = [];
          }
          $("#staticlists-apps-code").val(listApp);
          $("#list-code").val(listCode);
          $("#list-key").val(listKey);
          msAdmin.staticLists.addToFormListsFromResponse( msAdmin.staticLists.listValues, msAdmin.staticLists.addListValueAndIncreaseForOneValueCounter, '#table-list-values', '' );
          msAdmin.staticLists.addToFormListsFromResponse( msAdmin.staticLists.listTexts, msAdmin.staticLists.addListTextAndIncreaseForOneTextCounter, '#table-list-texts', '' );
        } else {
          alert(response.message);
        }
      }
    });

  $("#staticlists-form-submit").off("click");
  $("#staticlists-form-submit").on("click", msAdmin.staticLists.submitUpdate);
  //*** Show the form.
  $("#staticlists-form").popup("open");
}

msAdmin.staticLists.addToFormListsFromResponse = function( responseList, whereToAdd, domElementId, readonly ) {
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

msAdmin.staticLists.handleRadioButtonClick = function() {
  msAdmin.staticLists.setListValueCounter( 0 );
  msAdmin.staticLists.setListTextCounter( 0 );
  $('#table-list-values').empty();
  $('#table-list-texts').empty();
  if ( $('input[name=clear-values]:checked').val() == 'true' ){
    msAdmin.staticLists.addToFormListsFromResponse( msAdmin.staticLists.listValues, msAdmin.staticLists.addListValueAndIncreaseForOneValueCounter, '#table-list-values', '' );
    msAdmin.staticLists.addToFormListsFromResponse( msAdmin.staticLists.listTexts, msAdmin.staticLists.addListTextAndIncreaseForOneTextCounter, '#table-list-texts', '' );
  }
  else if ( $('input[name=clear-values]:checked').val() == 'false' ){
    msAdmin.staticLists.addToFormListsFromResponse( msAdmin.staticLists.listValues, msAdmin.staticLists.addListValueAndIncreaseForOneValueCounter, '#table-list-values', 'readonly' );
    msAdmin.staticLists.addToFormListsFromResponse( msAdmin.staticLists.listTexts, msAdmin.staticLists.addListTextAndIncreaseForOneTextCounter, '#table-list-texts', 'readonly' );
  }
  return false;
}

msAdmin.staticLists.submitUpdate = function() {
  //*** Set the key for submission.
  $('#clear-values-holder').val( $('input[name=clear-values]:checked').val() );
  var staticlistsKey = $("#staticlists-key").val();
  msAdmin.staticLists.resetValidFields( msAdmin.staticLists.validFields );
  msAdmin.staticLists.addDynamicValidFields( msAdmin.staticLists.validFields );
  msAdmin.staticLists.addClearValues( msAdmin.staticLists.validFields );
  
  //*** On success, close the submission window and reload the table.
  var staticlistsUpdsuccessFn = function() {

    $("#staticlists-form").popup("close");
    msAdmin.staticLists.clearStaticlistsForm();
    msAdmin.staticLists.getStaticlistsData();
    msAdmin.staticLists.setListValueCounter( 0 );
    msAdmin.staticLists.setListTextCounter( 0 );
  };
  $("#staticlists-form").serialize();
  msAdmin.ajax.submitUpdate("Staticlists",
                            "/service/staticlist/" + staticlistsKey,
                            msAdmin.staticLists.validFields,
                            "list-code",
                            staticlistsUpdsuccessFn);
}

msAdmin.staticLists.clearStaticlistsForm = function() {
  
  $('#staticlists-apps-code').val("");  
  $('#list-code').val("");
  $('#list-key').val("");
  $('#table-list-values').empty();
  $('#table-list-texts').empty();
  $('#div-clear-values').remove();
}

msAdmin.staticLists.getAppDataForStaticlists = function () {
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/app',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
        loadAppCodeOptions(appDataArray);
      } else {
        $(".status-bar").html("Failed to get application records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
}


msAdmin.staticLists.getStaticlistsData = function () {

  
  msAdmin.loader.show("Getting Static List data.");
  var appDataArray = ["No Static List data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/staticlist',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get staticlists records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#staticlists-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="staticlists-table-data"></table>' );
      //*** Make this table the active one for row events.
      msAdmin.activeTable = $('#staticlists-table-data').dataTable( {
        "aoColumnDefs": msAdmin.staticLists.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": msAdmin.staticLists.tableFormatter,
        "bProcessing": true
      });
    },
    complete: msAdmin.loader.hide()
  });

}


msAdmin.staticLists.showDeleteConfirm = function( staticlistsKey ) {
  var listCode = "Could not get Static List";
  var listApp = "STATICLISTS";

  //*** Get the application code/name.
  $.ajax({
    type: 'GET',
    url: '/service/staticlist/' + staticlistsKey,
    success: function(response) {
      if (response.status == "SUCCESS") {          
        listApp = response.data.listApp;
        listCode = response.data.listCode;
      }
      $("#app-code-delete").val(listApp);
      $("#list-code-delete").val(listCode);
      $("#staticlists-confirm-popup-body")
          .html("Are you sure you want to delete staticlists with code " + listCode + "?");
    }
  });

  //*** Set the title and body.
  $("#staticlists-confirm-popup-heading").html("Delete Staticlists code?");
  $("#staticlists-confirm-popup-action").html("Delete Staticlists code");
  $("#staticlists-key-delete").val(staticlistsKey);
  $("#staticlists-confirm-popup-delete").on( "click", msAdmin.staticLists.deleteApp );

  //*** Show the form.
  $("#staticlists-confirm-popup").popup("open");
}

msAdmin.staticLists.deleteApp = function() {
  var staticlistsKey = $("#staticlists-key-delete").val();
  var appCode = $("#app-code-delete").val();
  var listCode = "(" + $("#list-code-delete").val() + ")";
  var staticlistsMessage = appCode + listCode;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#staticlists-confirm-popup").popup("close");
    msAdmin.staticLists.getStaticlistsData();
  };
  msAdmin.ajax.submitDelete(staticlistsKey, "/service/staticlist/", "Staticlists", staticlistsMessage, audDelSuccessFn);
}


//***When the user goes to this page, show the data table on load.
$("#staticlists").live('pageshow', msAdmin.staticLists.getStaticlistsData);
$("#staticlists").live('pageshow', msAdmin.util.pageTransition);
