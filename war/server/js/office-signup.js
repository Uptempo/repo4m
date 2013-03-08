uptempo = {};
uptempo.office = {};
uptempo.office.google = {};
uptempo.office.google.clientId = msAdmin.ajax.getConfigValue(msAdmin.appConfig.GOOGLE_CLIENT_ID);
uptempo.office.google.redirectURI =
    msAdmin.ajax.getConfigValue(msAdmin.appConfig.GOOGLE_CAL_OFFICE_REDIRECT_URI);
uptempo.office.google.authURI = "https://accounts.google.com/o/oauth2/auth"
uptempo.office.google.calendarParams = {
  "response_type": "code",
  "client_id": uptempo.office.google.clientId,
  "redirect_uri": uptempo.office.google.redirectURI,
  "scope": "https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/userinfo.email",
  "access_type": "offline",
  "state": "officeCalendar",
  "approval_prompt": "force" //*** Remove this when done debugging.
}

uptempo.office.googleLogin = "";
uptempo.office.signUp = function() {
  //*** Get the signup values.
  //*** Submit the signup.
  uptempo.office.signUpSuccess();
  return false;
}

//*** Field mapping for validation and naming.
uptempo.office.validFields =
[
  {name: "Office group", inputId: "#office-group", formVal: "officeGroup", required: true},
  {name: "Office name", inputId: "#office-name", formVal: "officeName", required: true},
  {name: "Office address 1", inputId: "#office-address-1", formVal: "officeAddress1", required: true},
  {name: "Office address 2", inputId: "#office-address-2", formVal: "officeAddress2", required: false},
  {name: "Office city", inputId: "#office-city", formVal: "officeCity", required: true},
  {name: "Office state", inputId: "#office-state", formVal: "officeState", required: true},
  {name: "Office postal code", inputId: "#office-zip", formVal: "officePostalCode", required: true},
  {name: "Office phone", inputId: "#office-phone", formVal: "officePhone1", required: true},
  {name: "Office fax", inputId: "#office-fax", formVal: "officeFax1", required: false},
  {name: "Office email", inputId: "#office-email", formVal: "officeEmail", required: true},
  {name: "Office calendar email", inputId: "#office-cal-email", formVal: "officeCalEmail", required: false},
  {name: "Office notes", inputId: "#office-notes", formVal: "officeNotes", required: false},
  {name: "Office hours", inputId: "#office-hours", formVal: "officeHours", required: false}
 ];

uptempo.office.submitNew = function () {
  var signupFunction = function () {
    var url = uptempo.office.google.redirectURI + "?googlepair=false";
    window.location = url;
  }
  //*** Figure out of Google calendar will be used to manage this office.
  if ($("#office-google-cal")[0].checked) {
    signupFunction = uptempo.office.signUpSuccess;
  }
  
  //*** Set the key for submission.
  var officeKey = $("#office-name").val();
  msAdmin.ajax.submitNew("Office",
                         "/service/billingoffice",
                         uptempo.office.validFields,
                         "office-name",
                         officeKey,
                         signupFunction,
                         null);
}

/**
 * Callback function upon signup success, activates redirect to Google for calendar auth.
 */
uptempo.office.signUpSuccess = function(newOfficeKey) {
  var url = uptempo.office.google.authURI + "?";
  var delimiter = "";
  uptempo.office.google.calendarParams['state'] = newOfficeKey;
  $.each(uptempo.office.google.calendarParams, function(key, value) {
    var urlString = delimiter + key + "=" + encodeURIComponent(value);
    url += urlString;
    delimiter = "&";
  });
  window.location = url;
};

uptempo.office.getGroupData = function (dropdownId) {
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup',
    data: "format=obj",
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var groupDataArray = response.data.values;
        var groupValueId = $("#" + dropdownId);
        groupValueId.empty();
        groupValueId.append("<option value='Select an Office Group'>--Select an Office Group--</option>");
        $.each(groupDataArray, function(index, group) {
          groupValueId.append("<option value='" + group[18] + "'>" + group[0] + "(" + group[3] + "," + group[4] + ")</option>");
        })
        groupValueId.val("Select an Office Group");
        groupValueId.selectmenu("refresh");
      } else {
        var groupValues = "<select>" +
                      "<option value='DEFAULT'> Could not get office groups, defaulting to DEFAULT</option>" +
                      "</select>";
        groupValueId.replaceWith(groupValues)
      }
    }
  });
};

uptempo.office.initSignup = function () {
  uptempo.office.getGroupData("office-group");
};

//***When the user goes to this page, show the office groups on load.
$("#office-signup").live('pageshow', uptempo.office.initSignup);
