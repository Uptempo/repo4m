/* This file contains all admin user actions */
msAdmin.user = {};
msAdmin.user.tableHeadings = [
  {"sTitle": "E-mail", "aTargets": [0]},
  {"sTitle": "Last Name", "aTargets": [1]},
  {"sTitle": "First Name", "aTargets": [2]},
  {"sTitle": "Title" , "bSearchable": false, "aTargets": [3]},
  {"sTitle": "City", "aTargets": [4]},
  {"sTitle": "State", "aTargets": [5]},
  {"sTitle": "Cell", "bSearchable": false, "aTargets": [6]},
  {"sTitle": "Action", "aTargets": [7], "mData" : null},
];

//*** Field mapping for validation and naming.
msAdmin.user.validFields =
    [{name: "Title", inputId: "#user-title", formVal: "title", required: false},
     {name: "First Name", inputId: "#user-fname", formVal: "firstName", required: false},
     {name: "Last Name", inputId: "#user-lname", formVal: "lastName", required: false},
     {name: "E-Mail", inputId: "#user-email", formVal: "email", required: true},
     {name: "Password", inputId: "#user-pwd", formVal: "password", required: false},
     {name: "Address 1", inputId: "#user-address1", formVal: "address1", required: false},
     {name: "Address 2", inputId: "#user-address2", formVal: "address2", required: false},
     {name: "City", inputId: "#user-city", formVal: "city", required: false},
     {name: "State", inputId: "#user-state", formVal: "state", required: false},
     {name: "Cell Phone", inputId: "#user-cell", formVal: "cell", required: false}];

//*** Formats the user table.
msAdmin.user.tableFormatter = function(nRow, aData, iDisplayIndex) {
  //*** Append a delete link to the end of the row.
  var editLink = "<a href='#' onclick=\"msAdmin.user.showUpdate('" + aData[0] + "');\">edit</a>&nbsp;&nbsp;";
  var pwdLink = "<a href='#' onclick=\"msAdmin.user.showPwdForm('" + aData[0] + "');\">pwd</a>&nbsp;&nbsp;";
  var delLink = "<a href='#' onclick=\"msAdmin.user.showUserDeleteConfirm('" + aData[0] + "');\">del</a>";
  $("td:eq(7)", nRow).html(editLink + pwdLink + delLink);
};

msAdmin.user.validateInput = function() {
  var isValid = true;
  var errorMessage = "";
  for (field in msAdmin.user.validFields) {
    if ($(msAdmin.user.validFields[field].inputId).val() == "" && msAdmin.user.validFields[field].required) {
      errorMessage = errorMessage + msAdmin.user.validFields[field].name + " is required. <br />";
      isValid = false;
    }
  }
  return {isValid: isValid, errorMessage: errorMessage};
}

msAdmin.user.consructPostString = function(validationArray) {
  //***Setup the form data.
  var formData = "";
  var separator = "";
  for (field in validationArray) {
    formData += separator +
                validationArray[field].formVal +
                "=" + escape($(validationArray[field].inputId).val());
    separator = "&";
  }
  return formData;
}

msAdmin.user.showNew = function () {
  //*** Setup the form.
  $("#user-form-title").html("New user");
  $("#user-form-submit").changeButtonText("Create this user");
  $("#user-email").removeAttr("disabled");
  $("#user-form-submit").off("click");
  $("#user-form-submit").on("click", msAdmin.user.submitNew);
  $("#user-form-errors").html("");
  //*** Show the form.
  $("#user-form").popup("open");
}

msAdmin.user.submitNew = function () {
  //*** Set the key for submission.
  var userKey = $("#user-email").val();

  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#user-form").popup("close");
    msAdmin.user.clearUserForm();
    msAdmin.user.getUserData();
  };

  //*** Special user validation.
  var userValidFn = function() {
    //*** Check to make sure the password is set on insert.
    if ($("#user-pwd").val() == "") {
      return false;
    }
    return true;
  }
  
  msAdmin.ajax.submitNew("User",
                         "/service/user",
                         msAdmin.user.validFields,
                         "user-email",
                         userKey,
                         successFn,
                         userValidFn);
}

//*** Show the update config value popup.
msAdmin.user.showUpdate = function (valueKey) {
  $("#user-form-title").html("Update a user");
  $("#user-form-submit").changeButtonText("Update this user");
  $("#user-email").attr("disabled", "true");
  $("#user-form-errors").html("");
  //*** Get the data for this user

  //*** Submit the XHR request
  $.ajax({
    type: 'GET',
    url: '/service/user/' + valueKey,
    success: function(response) {
      if (response.status == "SUCCESS") {
        var userTitle = response.data.title;
        var userFName = response.data.firstName;
        var userLName = response.data.lastName;
        var userAddress1 = response.data.address1;
        var userAddress2 = response.data.address2;
        var userCity = response.data.city;
        var userState = response.data.state;
        $("#user-title").val(userTitle);
        $("#user-title").selectmenu("refresh");
        $("#user-email").val(valueKey);
        $("#user-fname").val(userFName);
        $("#user-lname").val(userLName);
        $("#user-address1").val(userAddress1);
        $("#user-address2").val(userAddress2);
        $("#user-city").val(userCity);
        $("#user-state").val(userState);
      } else {
        alert(response.message);
      }
    }
  });

  $("#user-form-submit").off("click");
  $("#user-form-submit").on("click", msAdmin.user.submitUpdate);
  //*** Show the form.
  $("#user-form").popup("open");
}

msAdmin.user.submitUpdateNewImpl = function() {
  //*** On success, close the submission window and reload the table.
  var successFn = function() {
    $("#user-form").popup("close");
    msAdmin.user.clearUserForm();
    msAdmin.user.getUserData();
  };

  //*** Set the key for submission.
  var userKey = $("#user-key").val();
  msAdmin.ajax.submitUpdate("User",
                            "/service/user/" + userKey,
                            msAdmin.user.validFields,
                            "user-email",
                            successFn,
                            null);
}

msAdmin.user.submitUpdate = function() {
  msAdmin.loader.show("Updating User " + $(msAdmin.user.validFields[3].inputId).val());

  var validationResult = msAdmin.user.validateInput();

  if (validationResult.isValid) {
    var formData = msAdmin.user.consructPostString(msAdmin.user.validFields);

    //*** Submit the XHR request
    $.ajax({
      type: 'PUT',
      url: '/service/user',
      data: formData,
      success: function(response) {
        //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
          var userEmail = $('#user-email').val();
          $(".status-bar").html("Successfully updated user " + userEmail);
          $(".status-bar").css("display", "block");
          msAdmin.user.clearUserForm();
          msAdmin.user.getUserData();
        } else {
          $(".status-bar").html("Failed to update user " + userEmail);
          $(".status-bar").css("display", "block");
        }
        $("#user-form").popup("close");
      },
      complete: msAdmin.loader.hide()
    });
  } else {
    $("#user-form-errors").html(validationResult.errorMessage);
    msAdmin.loader.hide();
    return false;
  }
  return false;
}

msAdmin.user.clearUserForm = function() {
  $('#user-email').val("");
  $('#user-title').val("DEFAULT");
  $('#user-fname').val("");
  $('#user-lname').val("");
  $('#user-pwd').val("");
  $('#user-address').val("");
  $('#user-city').val("");
  $('#user-state').val("");
  $('#user-cell').val("");
}

msAdmin.user.getUserData = function () {
  msAdmin.loader.show("Getting user data.");
  var userDataArray = ["No user data"];
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/user',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        userDataArray = response.data.users;
      } else {
        $(".status-bar").html("Failed to get user records! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
      //*** Format the data/datatable, regardless of response.
      $('#user-table').html( '<table cellpadding="0" cellspacing="0" border="0" class="entity-table" id="user-table-data"></table>' );
      //*** Make this table the active one for row events.
      msAdmin.activeTable = $('#user-table-data').dataTable( {
        "aoColumnDefs": msAdmin.user.tableHeadings,
        "aaData" : userDataArray,
        "fnRowCallback": msAdmin.user.tableFormatter,
        "bProcessing": true
      });
    },
    complete: msAdmin.loader.hide()
  });

}

msAdmin.user.showPwdForm = function (userEmail) {
  //*** Show the form.
  $("#user-pwd-form").popup("open");
  //*** Fill in the e-mail field with the user's e-mail.
  $("#user-pwd-email-display").html("Setting password for " + userEmail);
  $("#user-pwd-email").val(userEmail);
}

msAdmin.user.changePwd = function() {
  //*** Hide the error div.
  $(".form-errors").css("display", "none");

  //*** Get the form info.
  var userEmail = $('#user-pwd-email').val();
  var userPwd = $('#user-pwd-change').val();
  if (userEmail == "" || userPwd == "") {
    $(".form-errors").html("You must fill in the password for this user!");
    $(".form-errors").css("display", "block");
  } else {
    msAdmin.loader.show("Changing user password");
    $.ajax({
      type: 'PUT',
      url: '/service/user',
      data: "email=" + userEmail + "&newpwd=" + userPwd,
      success: function(response) {
        //*** If the response was sucessful, close the popup form.
        if (response.status != "SUCCESS") {
          $(".form-errors").html(response.message);
          $(".form-errors").attr("display", "block");
          alert(response.message);
        } else {
          alert(response.message);
          $("#user-pwd-form").popup("close");
        }
      },
      complete: msAdmin.loader.hide()
    });
  }
}

msAdmin.user.showUserDeleteConfirm = function(userEmail) {
  //*** Set the title and body.
  $("#user-confirm-popup-heading").html("Delete User?");
  $("#user-confirm-popup-body").html("Are you sure you want to delete user " + userEmail + "?");
  $("#user-confirm-popup-action").html("Delete User");
  $("#user-email-delete").val(userEmail);
  $("#user-confirm-popup-delete").on("click", msAdmin.user.deleteUser);

  //*** Show the form.
  $("#user-confirm-popup").popup("open");
}

msAdmin.user.deleteUser = function() {
  var userEmail = $("#user-email-delete").val();
  $.ajax({
      type: 'DELETE',
      url: '/service/user/' + userEmail,
      success: function(response) {
        //*** If the response was sucessful, close the popup form.
        if (response.status != "SUCCESS") {
          $(".form-errors").html(response.message);
          $(".form-errors").css("display", "block");
          alert(response.message);
          $("#user-confirm-popup").popup("close");
        } else {
          $(".form-errors").html("User " + userEmail + " successfully deleted.");
          $(".form-errors").css("display", "block");
          $("#user-confirm-popup").popup("close");
          msAdmin.user.getUserData();
        }
      },
      complete: msAdmin.loader.hide()
    });
}

//***When the user goes to this page, show the data table on load.
$("#users").live('pageshow', msAdmin.user.getUserData);
$("#users").live('pageshow', msAdmin.util.pageTransition);

