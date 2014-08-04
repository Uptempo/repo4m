/* Main JS file for MedSelect Admin */
//*** Util functions
/**
 * Returns a date string in the format mm/dd/yyyy from a date object.
 * 
 * @param {!Date}date The date to convert to string.
 * @return {String} A string containing the date in mm/dd/yyyy format.
 */
uptempo.appConfig = {};
uptempo.util = {};
uptempo.util.getDateString = function(date) {
  var dateString = 
      String(date.getMonth() + 1) + "/" +
      String(date.getDate()) + "/" +
      String(date.getFullYear());
  return dateString;
};

uptempo.util.getDateFromString = function(dateString) {
  var apptSplit = dateString.split("/");
  var month = apptSplit[0];
  var day = apptSplit[1];
  var year = apptSplit[2];
  var date = new Date();
  date.setMonth(month - 1, day);
  date.setYear(year);
  return date;
};

uptempo.util.getAmPmFromHours = function(value) {
  if (value == 24) {
    return "AM";
  } else if (value > 11) {
    return "PM";
  } else {
    return "AM";
  }
};

uptempo.util.getDateDisplay = function(hour, min) {
  var amPm = uptempo.util.getAmPmFromHours(hour);
  if (hour > 12) {
    hour = hour - 12;
  }
  if (hour < 10) {
    hour = "0" + hour;
  }
  if (min < 10) {
    min = "0" + min;
  }
  return hour + ":" + min + " " + amPm;
};

uptempo.util.getAmPmHours = function(value) {
  if (value.indexOf("PM") >= 0) {
    return 12;
  } else {
    return 0;
  }
};

uptempo.util.isEmptyString = function(value) {
  return !value || !value.length;
};

/**
 * Executes page transitions.
 * 
 * @param {!function} fnTransition The function to execute upon transition.
 */
uptempo.util.pageTransition = function() {
  $(".status-bar").html("");
  $(".status-bar").css("display", "none");
};

//*** End util functions

uptempo.activeTable = {};
uptempo.loader = {};
uptempo.loader.show = function(textValue) {
  $.mobile.loading('show', {
    text: textValue,
    textVisible: true,
    theme: 'a',
    html: ""
  });
};

uptempo.loader.hide = function() {
  $.mobile.loading('hide', {});
}

$(document).ready(function() {
  //*** Table Actions
  //*** Click handler for table row selection
  $("#user-table-data tbody tr").click(function(e) {
    if ($(this).hasClass('row-selected')) {
      $(this).removeClass('row-selected');
    }
    else {
      uptempo.activeTable.$('tr.row-selected').removeClass('row-selected');
      $(this).addClass('row-selected');
    }
  });
});

//*** Provide a method to change button text
(function($) {
  /*
   * Changes the displayed text for a jquery mobile button.
   * Encapsulates the idiosyncracies of how jquery re-arranges the DOM
   * to display a button for either an <a> link or <input type="button">
   */
  $.fn.changeButtonText = function(newText) {
    return this.each(function() {
      $this = $(this);
      if( $this.is('a') ) {
        $('span.ui-btn-text',$this).text(newText);
        return;
      }
      if( $this.is('input') ) {
        $this.val(newText);
        // go up the tree
        var ctx = $this.closest('.ui-btn');
        $('span.ui-btn-text',ctx).text(newText);
        return;
      }
    });
  };
})(jQuery);

//*** Convenience routines for AJAX requests.
uptempo.ajax = {};

/**
 * Takes an object of the structure:
 *   name - name of the entity.
 *   inputId - ID of the form input containing the entity's input.
 *   formVal - The POST/PUT form value for this field.
 *   required - {boolean} indicating whether the field is required.
 * and validates the input for this entity.
 *
 * @param {!object <name, inputId, formVal, required>} validationMapArray The metadata of the
 *    entity to validate.
 * @return {object <isValid, errorMessage>} Indicator whether field validation passed and the
 *    list of error messages related to failed validation, if applicable.
 */
uptempo.ajax.validateInput = function(validationMapArray) {
  var isValid = true;
  var errorMessage = "";
  for (field in validationMapArray) {
    if ($(validationMapArray[field].inputId).val() == "" && validationMapArray[field].required) {
      errorMessage = errorMessage + validationMapArray[field].name + " is required. <br />";
      isValid = false;
    }
  }
  return {isValid: isValid, errorMessage: errorMessage};
};

/**
 * Construct the POST/PUT string to submit input via AJAX.
 *
 * @param validationMapArray An array of objects, each in the structure:
 *     name - name of the entity.
 *     inputId - ID of the form input containing the entity's input.
 *     formVal - The POST/PUT form value for this field.
 *     required - {boolean} indicating whether the field is required.
 * @param key A {string} containing the unique identifier key for this entity.
 * @return A {string} containing the form data.
 */
uptempo.ajax.consructPostString = function(validationMapArray, key) {
  var separator = "";
  //***Setup the form data.
  var formData = "";
  if (key) {
    formData = "key=" + key;
    separator = "&";
  }
  for (field in validationMapArray) {
    if ((validationMapArray[field].formVal != "")&&(typeof $(validationMapArray[field].inputId).val() !== 'undefined' )) {
      formData += separator +
                validationMapArray[field].formVal +
                "=" + escape($(validationMapArray[field].inputId).val());
      separator = "&";
    }
  }
  //***Add the user to each POST.
  formData += "&user=" + uptempo.globals.user;
  return formData;
};

/**
 * Submit a new entity using HTTP POST.
 *
 * @param entityName A {!string} containing the entity name.
 * @param url A {!string} containing the URL to POST to.
 * @param validationMapArray An {!object} that contains validation information for the entity to
 *    be submitted.
 * @param confirmFieldId A {string} containing the form field ID value to show in the confirmation
 *    messge.  For example, the message created with this ID would be:
 *    'User ' + $('#' + confirmFieldId).val() + 'added'
 * @param key A {string} containing the unique key used to generate this entity's identifier.
 * @param successFn A {function} that is executed when the request is successful.
 * @param validationFn Used to add unique validation for the entity.  A {function} that returns an
 *    {object} in the form:
 *    isValid - {boolean} indicating whether the validation passed.
 *    errorMessage - {string} containing the message(s) related to validation failure.
 *
 */
uptempo.ajax.submitNew = 
    function(entityName, url, validationMapArray, confirmFieldId, key, successFn, validationFn) {
  var attemptItemName = $("#" + confirmFieldId).val();
  uptempo.loader.show("Inserting " + entityName + " " + attemptItemName);
  $(".form-errors").html("");
  $(".form-errors").css("display", "none");

  var specialValidationResult = true;
  if (validationFn) {
    specialValidationResult = validationFn();
  }
  var validationResult = uptempo.ajax.validateInput(validationMapArray);
  //*** Execute special validation here.
  
  if (validationResult.isValid && specialValidationResult) {
    var formData = uptempo.ajax.consructPostString(validationMapArray, key);   
    //*** Submit the XHR request
    $.ajax({
      type: 'POST',
      url: url,
      data: formData,
      success: function(response) {
        //*** If the response was sucessful, show the success indicator.
        if (response.status == "SUCCESS") {
          var statusMessage = 
              "Successfully added " + entityName + " " +
              attemptItemName + "<br />" + response.message;
          $(".status-bar").html(statusMessage);
          $(".status-bar").css("display", "block");
          var resp = response.data ? response.data.key : "";
          successFn(resp);
        } else {
          $(".form-errors").html("Failed to add " +
                                 entityName + " " +
                                 attemptItemName + ": " +
                                 response.message);
          $(".form-errors").css("display", "block");
        }
      },
      complete: uptempo.loader.hide()
    });
  } else {
    uptempo.loader.hide();
    var message = "";
    if (validationResult.errorMessage != "") {
      message = validationResult.errorMessage;
    }
    if (!specialValidationResult) {
      message += "<br />Special Validation Failed!";
    }
    $(".form-errors").html($(".form-errors").html() + message);
    $(".form-errors").css("display", "block");
    return false;
  }
  return false;
};

/**
 * Submit an update to an entity using HTTP PUT.  This function is separate from POST due to the
 * possiblity that each would need different logic.
 *
 * @param entityName A {!string} containing the entity name.
 * @param url A {!string} containing the URL to PUT to.
 * @param validationMapArray An {!object} that contains validation information for the entity to
 *    be submitted.
 * @param confirmFieldId A {string} containing the form field ID value to show in the confirmation
 *    messge.  For example, the message created with this ID would be:
 *    'User ' + $('#' + confirmFieldId).val() + 'added'
 * @param successFn A {function} that is executed when the request is successful.
 * @param validationFn Used to add unique validation for the entity.  A {function} that returns an
 *    {object} in the form:
 *    isValid - {boolean} indicating whether the validation passed.
 *    errorMessage - {string} containing the message(s) related to validation failure.
 */
uptempo.ajax.submitUpdate =
    function(entityTypeName,
             url,
             validationMapArray,
             confirmFieldId,
             successFn,
             validationFn) {
  var attemptItemName = $("#" + confirmFieldId).val();
  uptempo.loader.show("Updating " + entityTypeName + " " + attemptItemName);

  //*** Execute special validation here.
  var specialValidationResult = true;
  if (validationFn) {
    specialValidationResult = validationFn();
  }
  var validationResult = uptempo.ajax.validateInput(validationMapArray);
  
  if (validationResult.isValid && specialValidationResult) {
    var formData = uptempo.ajax.consructPostString(validationMapArray);

    //*** Submit the XHR request
    $.ajax({
      type: 'PUT',
      url: url,
      data: formData,
      success: function(response) {
        //*** If the response was sucessful, show the success indicator.
        if (response.status == "SUCCESS") {
          $(".status-bar").html("Successfully updated " + entityTypeName + " " + attemptItemName);
          $(".status-bar").css("display", "block");
          successFn();
        } else {
          $(".form-errors").html(
              "Failed to update " +
              entityTypeName + " " +
              attemptItemName + ".<br />" +
              response.message);
          $(".form-errors").css("display", "block");
        }
      },
      complete: uptempo.loader.hide()
    });
  } else {
    uptempo.loader.hide();
    var message = "";
    if (validationResult.errorMessage != "") {
      message = validationResult.errorMessage;
    }
    if (!specialValidationResult) {
      message += "<br />Special Validation Failed!";
    }
    $(".form-errors").html(message);
    $(".form-errors").css("display", "block");
    return false;
  }
  return false;
}

/**
 * Submit an entity using HTTP DELETE.
 *
 * @param key The item key.
 * @param url The URL to use to submit the delete operation.
 * @param entityTypeName The name of the entity type for status messages.
 * @param entityName The human readable name of the object to delete.
 * @param successFn A {function} that is executed when the request is successful.
 */
uptempo.ajax.submitDelete = function(key, url, entityTypeName, entityName, successFn) {
  uptempo.loader.show("Deleting " + entityTypeName + " " + entityName);
  $.ajax({
      type: 'DELETE',
      url: url + key,
      success: function(response) {
        //*** If the response was sucessful, execute the provided success function.
        if (response.status == "SUCCESS") {
          $(".status-bar")
              .html("Deleted " + entityTypeName + " " + entityName + ":" + response.message);
          $(".status-bar").css("display", "block");
          successFn();
        } else {
          $(".form-errors")
              .html(entityTypeName + " " + entityName + " deletion failed: " + response.message);
          $(".form-errors").css("display", "block");
        }
      },
      complete: uptempo.loader.hide()
    });
};

/**
 * Gets a list of application objects and fills a dropdown with those objects.
 * If present, valueIndex determines value for option element
 * Callback function is called if defined
 */
uptempo.ajax.fillDropdownWithApps = function(dropdownId, valueIndex, callbackFn) {
  var valueIndex = valueIndex || 0; 
  $.ajax({
    type: 'GET',
    url: '/service/app',
    data: "format=obj",
    success: function(response) {
      var appValueId = $("#" + dropdownId);
      appValueId.empty();
      //*** If the response was successful, show apps, otherwise show appropriate message.
      if (response.status == "SUCCESS") {
        var appData = response.data.values;
        appValueId.append("<option value=''>--Select an Application--</option>");
        $.each(appData, function(index, app) {
          appValueId.append("<option value='" + app[valueIndex] + "'>" + app[1] + "(" + app[0] + ")</option>");
        });
        appValueId.val("Select an Application");
        appValueId.selectmenu("refresh");
        if (callbackFn) {
          callbackFn();
        }
      } else {
        var appValues = "<select>" +
            "<option value='DEFAULT'> Could not get apps, defaulting to DEFAULT</option>" +
            "</select>";
        appValueId.replaceWith(appValues);
      }
    }
  });
};

/**
 * Fills a dropdown with a list of Offices.
 * @param {!string} dropdownId The ID of the dropdown to fill.
 * @param {function} callbackFn A callback function to execute after the dropdown is filled.
 */
uptempo.ajax.fillDropdownWithOffices = function(dropdownId, callbackFn) {
  $.ajax({
    type: 'GET',
    url: '/service/billingoffice',
    success: function(response) {
      var oValueId = $("#" + dropdownId);
      oValueId.empty();
      //*** If the response was successful, show apps, otherwise show appropriate message.
      if (response.status == "SUCCESS") {
        var officeData = response.data.values;
        oValueId.append("<option value=''>--No Office selected--</option>");
        $.each(officeData, function(index, office) {
          oValueId.append("<option value='" + office.key + "'>" + office.officeName + "</option>");
        });
        oValueId.val("Select an Office");
        oValueId.selectmenu("refresh");
        if (callbackFn) {
          callbackFn();
        }
      } else {
        var oValues = "<select><option value='DEFAULT'> No Offices Available!</option></select>";
        oValueId.replaceWith(oValues);
      }
    }
  });
};

/**
 * Fills a dropdown with a list of Office groups.
 * @param {!string} dropdownId The ID of the dropdown to fill.
 * @param {function} callbackFn A callback function to execute after the dropdown is filled.
 */
uptempo.ajax.fillDropdownWithOfficeGroups = function(dropdownId, callbackFn) {
  $.ajax({
    type: 'GET',
    url: '/service/billinggroup',
    success: function(response) {
      var gValueId = $("#" + dropdownId);
      gValueId.empty();
      //*** If the response was successful, show apps, otherwise show appropriate message.
      if (response.status == "SUCCESS") {
        var groupData = response.data.values;
        gValueId.append("<option value=''>--No Office Group selected--</option>");
        $.each(groupData, function(index, group) {
          gValueId.append("<option value='" + group.key + "'>" + group.groupName + "</option>");
        });
        gValueId.val("Select an Office Group");
        gValueId.selectmenu("refresh");
        if (callbackFn) {
          callbackFn();
        }
      } else {
        var gValues = "<select><option value='DEFAULT'> No Offices Available!</option></select>";
        gValueId.replaceWith(gValues);
      }
    }
  });
};

/**
 * Fills a dropdown with a list of Medlayer App.
 * @param {!string} dropdownId The ID of the dropdown to fill.
 * @param {function} callbackFn A callback function to execute after the dropdown is filled.
 */
uptempo.ajax.fillDropdownWithMedlayerApps = function(dropdownId, callbackFn) {
  $.ajax({
    type: 'GET',
    url: '/medlayer/app',
    success: function(response) {
      var aValueId = $("#" + dropdownId);
      aValueId.empty();
      //*** If the response was successful, show apps, otherwise show appropriate message.
      if (response.status == "SUCCESS") {
        var appData = response.data.values;
        aValueId.append("<option value=''>--No Medlayer App selected--</option>");
        $.each(appData, function(index, app) {
          aValueId.append("<option value='" + app.key + "'>" + app.appName + "</option>");
        });
        aValueId.val("Select an Office Group");
        aValueId.selectmenu("refresh");
        if (callbackFn) {
          callbackFn();
        }
      } else {
        var aValues = "<select><option value='DEFAULT'> No Medlayer apps Available!</option></select>";
        aValueId.replaceWith(aValues);
      }
    }
  });
};

/**
 * Gets a static list given an App Code and Static List Key.
 * @param {!string} appCode Application code.
 * @param {!string} listKey Static list key to get.
 * @param {!function} successFn Success callback function to execute once list is retrieved.
 * @return List of static list objects in this format:
 *   listKey: List Key
 *   listValue: List Value Array
 *   listText: List Text Array
 */
uptempo.ajax.getStaticList = function(appCode, listCode, successFn) {
  var params = "listApp=" + appCode + "&listCode=" + listCode;
  $.ajax({
    type: 'GET',
    url: '/service/staticlist?' + params,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var listArray = response.data.values;
        successFn(listArray);
      } else {
        $(".status-bar").html("Failed to get static list:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
}

/**
 * Fills a dropdown with a static list.
 * @param {!string} appCode Application code.
 * @param {!string} listKey Static list key to get.
 * @param {!string} selectId ID of the dropdown.
 */
uptempo.ajax.fillDropdownWithList = function(appCode, listCode, selectId) {
  var listValueId = $("#" + selectId);
  var successFn = function(listValues) {
    $.each(appData, function(index, item) {
      listValueId.append("<option value='" + item['listKey'] + "'>" + item['listValue'][0] + "</option>");
    });
    listValueId.selectmenu("refresh");
  };

  uptempo.ajax.getStaticList(appCode, listCode, successFn);
};

/*
 * Gets a config value, given the name, checking the local cache first.
 * @param valueName The name of the config value to get.
 * @return A string with the value.
 */
uptempo.ajax.getConfigValue = function(valueName) {
  return uptempo.appConfig.values[valueName];
};

uptempo.ajax.populateConfigValues = function() {
  uptempo.appConfig.values = {};
  //*** Get the data from the server.
  $.ajax({
    type: 'GET',
    url: '/service/config',
    success: function(response) {
      if (response.status == "SUCCESS") {
        var configDataArray = response.data.values;
        for (var i in configDataArray) {
          uptempo.appConfig.values[configDataArray[i].name] =
              {text: configDataArray[i].text, value: configDataArray[i].value};
        }
      } else {
        $(".status-bar").html("Failed to get config values! Error:" + response.message);
        $(".status-bar").css("display", "block");
      }
    }
  });
};

/**
 * 
 * @param {String} key The attachment key.
 * @param {Function} fnSuccess The function to execute upon success, with the attachment URL as
 *     the first parameter to the function.
 */
uptempo.ajax.FillAttachmentUrlFromKey = function(key, fnSuccess) {
  $.ajax({
    type: 'GET',
    url: '/service/attachment/url/' + key,
    success: function(response) {
      if (response.status == "SUCCESS") {          
        fnSuccess(response.data.url);
      } else {
        alert("Could not get URL for attachment " + key);
      }
    }
  });
};

uptempo.util.showList = function ( what, serviceName, valueKey ) {
  var prefix = '';
  var name = '';
  var phones = null;
  var faxs = null;
  var notes = '';
  var hours = '';
  var values = '';
  var texts = '';
  var titles = '';
  var specialties = '';
  var education = '';
  var publicDescription = '';
  var notes = '';
  
  var application = '';
  var value = '';
  
  $("#"+serviceName+"s-table-textarea").empty();
  //*** Submit the XHR request.
  $.ajax({
    type: 'GET',
    url: '/service/'+serviceName+'/' + valueKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
      
        if ( serviceName.indexOf('group') != -1 ){
          prefix = 'group';
          name = response.data.groupName;
          phones = response.data.groupPhone;
          faxs = response.data.groupFax;
          notes = response.data.groupNotes;
          hours = response.data.groupHours;
        }
        else if ( serviceName.indexOf('office') != -1 ){
          prefix = 'office';
          name = response.data.officeName;
          phones = response.data.officePhone;
          faxs = response.data.officeFax;
          notes = response.data.officeNotes;
          hours = response.data.officeHours;
        }
        else if ( serviceName.indexOf('staticlist') != -1 ){
          prefix = 'list';
          name = response.data.listCode;
          values = response.data.listValue;
          texts = response.data.listText;
        }
        else if ( serviceName.indexOf('doctor') != -1 ){
          prefix = 'doctor';
          name = response.data.firstName+' '+response.data.lastName;
          titles = response.data.title;
          specialties = response.data.specialty;
          education = response.data.education;
          publicDescription = response.data.publicDescription;
          notes = response.data.notes;
        }
		else if ( serviceName.indexOf('config') != -1 ){
			prefix = 'config';
			name = response.data.name;
			publicDescription = response.data.description;
			value = response.data.value;
			application = response.data.appCode;
			texts = response.data.text;
		}
        if ( what == 'Phone' ){
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' values for '+prefix+' name {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( phones, serviceName+'s-table-textarea' );
        }
		else if ( what == 'ConfigText' ){
		  var list = new Array();
          var textCheck = texts || "";
          list[0] = '<p>' + textCheck.replace(/(\r\n|\n|\r)/gm,"<br>"); + '</p>';
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' for '+prefix+' {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( list, serviceName+'s-table-textarea' );
		}
		else if ( what == 'ConfigValue' ){
		  var list = new Array();
          var valueCheck = value || "";
          list[0] = '<p>' + valueCheck.replace(/(\r\n|\n|\r)/gm,"<br>"); + '</p>';
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' for '+prefix+' {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( list, serviceName+'s-table-textarea' );
		}
        else if ( what == 'Fax' ){
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' values for '+prefix+' name {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( faxs, serviceName+'s-table-textarea' );
        }
        else if ( what == 'Title' ){
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' values for '+prefix+' name {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( titles, serviceName+'s-table-textarea' );
        }
        else if ( what == 'Speciality' ){
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' values for '+prefix+' name {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( specialties, serviceName+'s-table-textarea' );
        }
        else if ( what == 'Note' ){
          var list = new Array();
          var checkNotes = notes || "";
          list[0] = '<p>' + checkNotes.replace(/(\r\n|\n|\r)/gm,"<br>"); + '</p>';
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' for '+prefix+' {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( list, serviceName+'s-table-textarea' );
        }
        else if ( what == 'Notes' ){
          var list = new Array();
          var checkNotes = notes || "";
          list[0] = '<p>' + checkNotes.replace(/(\r\n|\n|\r)/gm,"<br>"); + '</p>';
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' for '+prefix+' {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( list, serviceName+'s-table-textarea' );
        }
        else if ( what == 'Education' ){
          var list = new Array();
          var educationCheck = education || "";
          list[0] = '<p>' + education.replace(/(\r\n|\n|\r)/gm,"<br>"); + '</p>';
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' for '+prefix+' {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( list, serviceName+'s-table-textarea' );
        }
        else if ( what == 'PublicDescription' ){
          var list = new Array();
          var publicDescriptionCheck = publicDescription || "";
          list[0] = '<p>' + publicDescription.replace(/(\r\n|\n|\r)/gm,"<br>"); + '</p>';
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' for '+prefix+' {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( list, serviceName+'s-table-textarea' );
        }
        else if ( what == 'Hour' ){
          var list = new Array();
          list[0] = '<p>' + hours.replace(/(\r\n|\n|\r)/gm,"<br>"); + '</p>';
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' for '+prefix+' name {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( list, serviceName+'s-table-textarea' );
        }
        else if ( what == 'Value' ){
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' values for '+prefix+' name {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( values, serviceName+'s-table-textarea' );
        }
        else if ( what == 'Text' ){
          $( '#'+serviceName+'s-textarea-form-title' ).html( what+' values for '+prefix+' name {'+ name +'}' );
          uptempo.util.addToReadOnlyFromResponse( texts, serviceName+'s-table-textarea' );
        }
      }
      else {
        alert(response.message);
      }
    }
  });  
  
  $('#'+serviceName+'s-show-textarea-form').popup("open");
}

uptempo.util.addToReadOnlyFromResponse = function(responseList, domElementName) {
  var len = 0;
  if (responseList != null){
    len = responseList.length;
  }
  for (var i = 0; i < len; ++i) {
    if (i in responseList) {
      var item = responseList[i];
      uptempo.util.addTableRow(item.replace(/(\r\n|\n|\r)/gm,"<br>"), domElementName, i);
    }
  }
};

uptempo.util.addTableRow = function( value, tableName, rowCounter ){
  var item = '<tr> id="' + tableName + rowCounter + '"<td>'  + value + '</td></tr>';
  $('#'+tableName).append( item );
};
