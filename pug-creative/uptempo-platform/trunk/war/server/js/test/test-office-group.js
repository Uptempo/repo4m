//*** Office Group Test Data.
utTest.group = {};

//*** Tests require a delay due to datastore persistence.
utTest.group.testDelay = 500;
utTest.group.testData = {
  data: [
    {"groupName": "Automated Testing Group",
     "groupAddress1": "123 Hillsdale Ln.",
     "groupAddress2": "",
     "groupCity": "New York",
     "groupState": "NY",
     "groupPostalCode": "10086",
     "groupEmail": "admin@testinggroup.com",
     "groupPhone": "520-222-1111",
     "groupNotes": "Test Automated Notes",
     "groupHours": "Mon 0800-1700 \n Tue 0800-1700",
     "user": "automated-test@uptempo.biz"}
  ]};

/**
 * Add data shortcut.
 */
utTest.group.addData = function() {
  //*** Call the application API.
  $.ajax({
    contentType: 'application/json',
    url: utTest.urlPrefix + "/service/billinggroup",
    data: utTest.group.testData.data[0],
    dataType: 'json',
    type: 'POST',
    success: function(response) {
      //*** Save the group key.
      utTest.group.currentKey = response.data.key;
    }
  });
};

/**
 * Delete data shortcut.
 */
utTest.group.deleteData = function() {
  //*** Call the application API.
  $.ajax({
    contentType: 'application/json',
    url: utTest.urlPrefix + "/service/billinggroup/" + utTest.group.currentKey,
    dataType: 'json',
    type: 'DELETE',
    success: function() {
      //*** Do nothing.
    }
  });
};

module("Billing Group tests", {
  setup: utTest.group.addData
});

//*** Get application list.
asyncTest( "Get group list.", function() {
  var runList = function() {
    //*** Call the application API.
    $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/billinggroup",
      dataType: 'json',
      type: 'GET',
      cache: false,
      success: function(response) {
        equal("SUCCESS", response.status, "Check list status OK");
        ok(response.data.values.length > 0, "List size OK");
        utTest.group.deleteData();
        QUnit.start();
      }
    });
  };

  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runList, utTest.group.testDelay);
});

//*** Get single application by key.
asyncTest("Retrieve testing group.", function() {
  var runGet = function() {
    //*** Call the application API.
    $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/billinggroup/" + utTest.group.currentKey,
      dataType: 'json',
      type: 'GET',
      cache: false,
      success: function(response) {
        equal("SUCCESS", response.status, "Check get group status OK");
        ok(response.data, "GET for single group returned a value");
        utTest.group.deleteData();
        QUnit.start();
      }
    });
  };
  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runGet, utTest.group.testDelay);
});

//*** Update the application.
asyncTest("Update the test group.", function() {
  var newData = {"groupAddress2": "Suite 106"};
  
  var runUpdate = function() {
   $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/billinggroup/" + utTest.group.currentKey,
      data: newData,
      dataType: 'json',
      type: 'PUT',
      success: function(response) {
        equal("SUCCESS", response.status, "Check update group status OK");
        utTest.group.deleteData();
        QUnit.start();
      }
    });
  };

  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runUpdate, utTest.group.testDelay);
});
