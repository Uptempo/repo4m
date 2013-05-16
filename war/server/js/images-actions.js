/* This file contains all admin images definition actions */
uptempo.images = {};

uptempo.images.tableHeadings = [
  {"sTitle": "Category name", "aTargets": [0]},
  {"sTitle": "Created By", "aTargets": [1]},
  {"sTitle": "Modified By", "bVisible": false, "aTargets": [2]},
  {"sTitle": "Create Date", "bVisible": false, "aTargets": [3]},  
  {"sTitle": "Modify Date", "bVisible": false, "aTargets": [4]},
  {"sTitle": "Access Key", "bVisible": false, "aTargets": [5]},
  {"sTitle": "Action", "aTargets": [6], "mData" : null},
];

//*** Field mapping for validation and naming.
uptempo.images.validFields =
    [
     {name: "Image name", inputId: "#images-name", formVal: "", required: true}
     ];

uptempo.images.resetValidFields = function( validFields ){
  validFields.splice( 0, validFields.length );
  validFields.push( {name: "Image name", inputId: "#images-name", formVal: "", required: true} );
}

//*** Formats the staticlist table.
uptempo.images.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"uptempo.staticLists.showUpdate('" + aData[9] + "');\">edit</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"uptempo.staticLists.showDeleteConfirm('" + aData[9] + "');\">del</a>";
  var showListValues = "<a href='#' onclick=\"uptempo.util.showList('Value', 'staticlist', '" + aData[9] + "');\">show</a>&nbsp;&nbsp;";
  var showListTexts = "<a href='#' onclick=\"uptempo.util.showList('Text', 'staticlist', '" + aData[9] + "');\">show</a>&nbsp;&nbsp;";

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

uptempo.images.showNew = function () {
  //*** Setup the form.
  $("#images-form-title").html("New Image");
  $("#images-form-submit").changeButtonText("Create this image");
  $("#images-form-submit").off("click");
  $("#images-form-submit").on("click", uptempo.images.submitNew);
  $("#images-form-errors").html("");
  //*** Show the form.
  $("#images-form").popup("open");
}

uptempo.images.submitNew = function () {
  //*** Set the key for submission.
  /*var key = $("#list-code").val();

  //*** On success, close the submission window and reload the table.
  var staticlistsSuccessFn = function() {
    $("#staticlists-form").popup("close");
    uptempo.staticLists.clearStaticlistsForm();
    uptempo.staticLists.getStaticlistsData();
    uptempo.staticLists.setListValueCounter( 0 );
    uptempo.staticLists.setListTextCounter( 0 );
  };
  uptempo.staticLists.resetValidFields( uptempo.staticLists.validFields );
  uptempo.staticLists.addDynamicValidFields( uptempo.staticLists.validFields );
  $("#staticlists-form").serialize();
  uptempo.ajax.submitNew("Staticlists",
                         "/service/staticlist",
                         uptempo.staticLists.validFields,
                         "list-code",
                         key,
                         staticlistsSuccessFn);
  */
}

//*** Show the update application popup.
uptempo.images.showUpdate = function (valueKey) {
  $("#images-form-title").html("Update Image");
  $("#images-form-submit").changeButtonText("Update this image");  
  $("#images-form-errors").html("");
  $("#images-key").val(valueKey);
  //*** Get the data for this application.

  //*** Submit the XHR request.
  /*$.ajax({
    type: 'GET',
    url: '/service/staticlist/' + valueKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
        
          var listCode = response.data.listCode;
          var listApp = response.data.listApp;
          var listKey = response.data.listKey;
          uptempo.staticLists.listValues = response.data.listValue;
          if ( uptempo.staticLists.listValues == null ){
            uptempo.staticLists.listValues = [];
          }
          uptempo.staticLists.listTexts = response.data.listText;
          if ( uptempo.staticLists.listTexts == null ){
            uptempo.staticLists.listTexts = [];
          }
          $("#staticlists-apps-code").val(listApp);
          $("#list-code").val(listCode);
          $("#list-key").val(listKey);
          uptempo.staticLists.addToFormListsFromResponse( uptempo.staticLists.listValues, uptempo.staticLists.addListValueAndIncreaseForOneValueCounter, '#table-list-values', '' );
          uptempo.staticLists.addToFormListsFromResponse( uptempo.staticLists.listTexts, uptempo.staticLists.addListTextAndIncreaseForOneTextCounter, '#table-list-texts', '' );
        } else {
          alert(response.message);
        }
      }
    });

  $("#staticlists-form-submit").off("click");
  $("#staticlists-form-submit").on("click", uptempo.staticLists.submitUpdate);
  //*** Show the form.
  $("#staticlists-form").popup("open");
  */
}

uptempo.images.submitUpdate = function() {
  //*** Set the key for submission.
  var imagesKey = $("#images-key").val();
  uptempo.images.resetValidFields( uptempo.images.validFields );
  
  //*** On success, close the submission window and reload the table.
  /*var staticlistsUpdsuccessFn = function() {

    $("#staticlists-form").popup("close");
    uptempo.staticLists.clearStaticlistsForm();
    uptempo.staticLists.getStaticlistsData();
    uptempo.staticLists.setListValueCounter( 0 );
    uptempo.staticLists.setListTextCounter( 0 );
  };
  $("#staticlists-form").serialize();
  uptempo.ajax.submitUpdate("Staticlists",
                            "/service/staticlist/" + staticlistsKey,
                            uptempo.staticLists.validFields,
                            "list-code",
                            staticlistsUpdsuccessFn);
  */
}

uptempo.images.getImagesData = function () {
  uptempo.loader.show("Getting Image data.");
  var appDataArray = ["No Image data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/image',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        appDataArray = response.data.values;
      } else {
        $(".status-bar").html("Failed to get image records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#images-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="images-table-data"></table>' );
      //*** Make this table the active one for row events.
      uptempo.activeTable = $('#images-table-data').dataTable( {
        "aoColumnDefs": uptempo.images.tableHeadings,
        "aaData" : appDataArray,
        "fnRowCallback": uptempo.images.tableFormatter,
        "bProcessing": true
      });
    },
    complete: uptempo.loader.hide()
  });

}


uptempo.images.showDeleteConfirm = function( imagesKey ) {
  /*var listCode = "Could not get Static List";
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
  $("#staticlists-confirm-popup-delete").on( "click", uptempo.staticLists.deleteApp );

  //*** Show the form.
  $("#staticlists-confirm-popup").popup("open");
  */
}

uptempo.images.deleteApp = function() {
  /*var staticlistsKey = $("#staticlists-key-delete").val();
  var appCode = $("#app-code-delete").val();
  var listCode = "(" + $("#list-code-delete").val() + ")";
  var staticlistsMessage = appCode + listCode;

  //*** Define a success function.
  var audDelSuccessFn = function() {
    $("#staticlists-confirm-popup").popup("close");
    uptempo.staticLists.getStaticlistsData();
  };
  uptempo.ajax.submitDelete(staticlistsKey, "/service/staticlist/", "Staticlists", staticlistsMessage, audDelSuccessFn);
  */
}


//***When the user goes to this page, show the data table on load.
$("#images").live('pageshow', uptempo.images.getImagesData);
$("#images").live('pageshow', uptempo.util.pageTransition);
