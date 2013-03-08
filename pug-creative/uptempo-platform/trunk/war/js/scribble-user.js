//*** Scribble User Functions
//*** All functions in this file are related to user login/management/cookies.
//*** Depends on: scribble-settings.js
MedSelect.Scribble.setRegistrationCookies = function (regUser) {
  for (var key in regUser) {
    if (regUser[key] == null || regUser[key] == "") {
      regUser[key] = "EMPTY";
    }
  }

  MedSelect.Scribble.setCookie("medSelectEmail", regUser.email, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectTitle", regUser.title, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectFName", regUser.fname, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectLName", regUser.lname, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectAddress", regUser.address, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectCity", regUser.city, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectState", regUser.state, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectCell", regUser.cell, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectNotifyEmail", regUser.notifyemail, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectNotifyText", regUser.notifytext, MedSelect.Scribble.cookieExpiration);
  MedSelect.Scribble.setCookie("medSelectNotifyUnsubscribe", regUser.notifyunsubscribe, MedSelect.Scribble.cookieExpiration);
}

MedSelect.Scribble.setCookie = function(c_name, value, exdays) {
  var exdate=new Date();
  exdate.setDate(exdate.getDate() + exdays);
  var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
  document.cookie=c_name + "=" + c_value;
}

MedSelect.Scribble.getCookie = function(c_name) {
  var i,x,y,ARRcookies=document.cookie.split(";");
  for (i=0;i<ARRcookies.length;i++)
  {
    x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
    y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
    x=x.replace(/^\s+|\s+$/g,"");
    if (x==c_name) {
      return unescape(y);
    }
   }
}

MedSelect.Scribble.getCookieUser = function() {
  var userObj = {};
  userObj.email = MedSelect.Scribble.getCookie("medSelectEmail");
  userObj.title = MedSelect.Scribble.getCookie("medSelectTitle");
  userObj.fname = MedSelect.Scribble.getCookie("medSelectFName");
  userObj.lname = MedSelect.Scribble.getCookie("medSelectLName");
  userObj.city = MedSelect.Scribble.getCookie("medSelectCity");
  userObj.state = MedSelect.Scribble.getCookie("medSelectState");
  userObj.cell = MedSelect.Scribble.getCookie("medSelectCell");
  userObj.notifyemail = MedSelect.Scribble.getCookie("medSelectNotifyEmail");
  userObj.notifytext = MedSelect.Scribble.getCookie("medSelectNotifyText");
  userObj.notifyunsubscribe = MedSelect.Scribble.getCookie("medSelectNotifyUnsubscribe");
  if (userObj.email == "0" || !userObj.email || !userObj.title || !userObj.fname || !userObj.lname) {
    return null;
  } else {
    return userObj;
  }
}

//***Settings screen event handlers.
MedSelect.Scribble.showUserUpdatePopup = function () {
  //*** Get the user info, set form values.
  userObj = MedSelect.Scribble.getCookieUser();
  $("#setting-user-address").val(userObj.address);
  $("#setting-user-city").val(userObj.city);
  $("#setting-user-state").val(userObj.state);
  $("#setting-user-cell").val(userObj.cell);
  $("#settings-info-popup").popup("open");
}

MedSelect.Scribble.userEmailSettingChange = function () {
  var currentUser = MedSelect.Scribble.getCookieUser();
  var updateUser = {};
  updateUser.email = currentUser.email;
  updateUser.notifyemail = $("#notify-email").val();
  MedSelect.Scribble.sendUserUpdate(updateUser);
  MedSelect.Scribble.setCookie(
      "medSelectNotifyEmail",
      updateUser.notifyemail,
      MedSelect.Scribble.cookieExpiration);
}

MedSelect.Scribble.userTextSettingChange = function () {
  var currentUser = MedSelect.Scribble.getCookieUser();
  var updateUser = {};
  updateUser.email = currentUser.email;
  updateUser.notifytext = $("#notify-txt-message").val();
  MedSelect.Scribble.sendUserUpdate(updateUser);
  MedSelect.Scribble.setCookie(
      "medSelectNotifyText",
      updateUser.notifytext,
      MedSelect.Scribble.cookieExpiration);
}

MedSelect.Scribble.userAddFooter = function () {
  var currentUser = MedSelect.Scribble.getCookieUser();
  var updateUser = {};
  updateUser.email = currentUser.email;
  updateUser.addfooter = $("#notify-unsubscribe").val();
  MedSelect.Scribble.sendUserUpdate(updateUser);
  MedSelect.Scribble.setCookie(
      "medSelectNotifyUnsubscribe",
      updateUser.addfooter,
      MedSelect.Scribble.cookieExpiration);
}

MedSelect.Scribble.sendUserUpdate = function(user) {
  var formData = "";
  var fields = MedSelect.Scribble.userFields;
  for (field in fields) {
    if (user[fields[field]] != null) {
      formData += fields[field] + "=" + escape(user[fields[field]]) + "&";
    }
  }
  $.ajax({
    type: 'PUT',
    url: '/service/user',
    data: formData,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        //*** Do nothing.
      } else {
        alert("ERROR:" + response.message);
      }
    }
  });
}

//***Login/Registration Event Handlers
MedSelect.Scribble.doLogin = function() {
  var validEntries = true;
  var mail = $("input#email").val();
  $("label#email_error").hide();
  $("label#pwd_error").hide();
  if (mail == "") {
    $("label#email_error").show();
    validEntries = false;
  }
  var pwd = $("input#pwd").val();
  if (pwd == "") {
    $("label#pwd_error").show();
    validEntries = false;
  }

  if (validEntries == true) {
    /*
    if ((mail.toLowerCase() == 'test') && (pwd.toLowerCase()=='test')){
      var userObj = {}
      userObj.title = "Sr.";
      userObj.fname = "Test";
      userObj.lname = "User";
      userObj.email = "test@test.com";
      MedSelect.Scribble.setRegistrationCookies(userObj);
      $.mobile.changePage("#three", {transition: "flip"});
      return false;
    } else {
    */
    //*** Check the login against the server
    var formData = "email=" + escape(mail) + "&password=" + pwd;

    $("login-loader").attr("display", "block");
    //*** Submit the XHR request
    $.ajax({
      type: 'POST',
      url: '/service/userauth',
      data: formData,
      success: function(response) {
        $("login-loader").attr("display", "none");
        //*** Parse the response JSON.
        if (response.status == "SUCCESS") {
          //*** Set the login cookie.
          var userObj = response.data;
          MedSelect.Scribble.setRegistrationCookies(userObj);
          MedSelect.Scribble.setSettings(userObj);
          $.mobile.changePage("#three");
        } else {
          $("input#pwd").val("");
          alert(response.message);
        }
      }
    });
    return false;
  }
  return false;
};

MedSelect.Scribble.setSettings = function(user) {
  //*** If notifyemail is set, it will be 'off' or 'on'.
  if (user.notifyemail) {
    $("#notify-email").val(user.notifyemail);
  }

  if (user.notifytext) {
    $("#notify-txt-message").val(user.notifytext);
  }

  if (user.addfooter) {
    $("#notify-unsubscribe").val(user.notifyunsubscribe);
  }
}

MedSelect.Scribble.logout = function() {
  blankUser = {};
  blankUser.email = 0;
  MedSelect.Scribble.setRegistrationCookies(blankUser);
  alert("Logged Out");
}

MedSelect.Scribble.submitRegistration = function() {
  var validFields = new Array();
  var isValid = false;
  var errorMessage = "";

  validFields[0] = {name: "E-Mail", val: $('#c-email').val()};
  //validFields[0] = {name: "Title", val: $('#c-title').val()};
  //validFields[1] = {name: "First Name", val: $('#c-firstname').val()};
  //validFields[2] = {name: "Last Name", val: $('#c-lastname').val()};
  //validFields[3] = {name: "E-Mail", val: $('#c-email').val()};
  //validFields[4] = {name: "Password", val: $('#c-password').val()};
  //validFields[5] = {name: "Address", val: $('#c-address').val()};
  //validFields[6] = {name: "City", val: $('#c-city').val()};
  //validFields[7] = {name: "State", val: $('#c-state').val()};
  validFields[8] = {name: "Cell Phone", val: $('#c-cell').val()};

  for (field in validFields) {
    if (validFields[field].val != "") {
      isValid = true;
    }
  }

  if (isValid == true) {
    var formData = "";
    formData += "title=" + escape($('#c-title').val());
    formData += "&firstname=" + escape($('#c-firstname').val());
    formData += "&lastname=" + escape($('#c-lastname').val());
    formData += "&email=" + escape($('#c-email').val());
    formData += "&password=" + escape($('#c-password').val());
    formData += "&address1=" + escape($('#c-address1').val());
    formData += "&address2=" + escape($('#c-address2').val());
    formData += "&city=" + escape($('#c-city').val());
    formData += "&state=" + escape($('#c-state').val());
    formData += "&cell=" + escape($('#c-cell').val());

    $("form-reg-loader").attr("display", "block");
    //*** Submit the XHR request
    $.ajax({
      type: 'POST',
      url: '/service/user',
      data: formData,
      success: function(response) {
        $("form-reg-loader").attr("display", "none");
        //*** If the response was sucessful, save the user info in cookies.
        if (response.status == "SUCCESS") {
          alert("Thank you for registering for the Rheumatology Patient Education App.");
          //*** Redirect to the login screen.
          location.href="/#two";
        } else {
          alert(response.message);
        }
      }
    });
  } else {
    errorMessage = "In order to receive this application, " +
                   "you must include either your e-mail address or cell phone number.";
    alert(errorMessage);
  }
}

MedSelect.Scribble.checkLogin = function() {
  //*** If the user cookie is set, skip login.
  var userCookie = MedSelect.Scribble.getCookieUser();
  var isLoggedIn = true;
  if (userCookie != null && userCookie.email == "EMPTY") {
    isLoggedIn = false;
  }
  if (userCookie == null || !isLoggedIn) {
    $.mobile.changePage("#two");
    $("#two").attr("display", "block");
    $("#button-overlay").attr("display", "none");
    MedSelect.Scribble.currentPage = 2;
  } else {
    //*** Fill the settings in.
    MedSelect.Scribble.setSettings(userCookie)
    $.mobile.changePage("#three");
  }
};