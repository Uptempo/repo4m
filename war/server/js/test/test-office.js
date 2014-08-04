//*** Office Group Test Data.
utTest.office = {};

//*** Tests require a delay due to datastore persistence.
utTest.office.testDelay = 500;
utTest.office.testData = {
  data: [
    {"officeGroup": "AutomatedTestingGroupFakeKey",
     "officeName": "Automated Testing Office",
     "officeAddress1": "124 Hillsdale Ln.",
     "officeAddress2": "",
     "officeCity": "New York",
     "officeState": "NY",
     "officePostalCode": "10086",
     "officeTimeZone": -7,
     "officeEmail": "admin@testingoffice.com",
     "officePhone": "520-222-1111",
     "officeNotes": "Test Automated Notes",
     "officeHours": "Mon 0800-1700 \n Tue 0800-1700",
     "officeLogoURL": "http://www.uptempo.biz/wp-content/uploads/2014/06/UPT1001_UPTEMPO_Logo-300x162.png",
     "officeSiteURL": "http://www.uptempo.biz/",
     "officeFBURL": "http://www.facebook.com",
     "officeEmailTemplate": "This is an e-mail %officeName%",
     "officeUserEmailTemplate": "This is a user e-mail %officeName%",
     "officeBufferHours": 6,
     "user": "automated-test@uptempo.biz"
   }
  ]};

/**
 * Add data shortcut.
 */
utTest.office.addData = function() {
  //*** Call the office API.
  $.ajax({
    contentType: 'application/json',
    url: utTest.urlPrefix + "/service/billingoffice",
    data: utTest.office.testData.data[0],
    dataType: 'json',
    type: 'POST',
    success: function(response) {
      //*** Save the office key.
      utTest.office.currentKey = response.data.key;
    }
  });
};

/**
 * Delete data shortcut.
 */
utTest.office.deleteData = function() {
  if (utTest.office.currentKey) {
    //*** Call the delete office API.
    $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/billingoffice/" + utTest.office.currentKey,
      dataType: 'json',
      type: 'DELETE',
      success: function() {
        //*** Do nothing.
      }
    });
  }
};

module("Billing Office tests", {
  setup: utTest.office.addData
});

//*** Get application list.
asyncTest( "Get office list.", function() {
  var runList = function() {
    //*** Call the application API.
    $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/billingoffice",
      dataType: 'json',
      type: 'GET',
      cache: false,
      success: function(response) {
        equal("SUCCESS", response.status, "Check list office status OK");
        ok(response.data.values.length > 0, "List size OK");
        utTest.office.deleteData();
        QUnit.start();
      }
    });
  };

  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runList, utTest.office.testDelay);
});

//*** Get single application by key.
asyncTest("Retrieve testing office.", function() {
  var runGet = function() {
    //*** Call the application API.
    $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/billingoffice/" + utTest.office.currentKey,
      dataType: 'json',
      type: 'GET',
      cache: false,
      success: function(response) {
        equal("SUCCESS", response.status, "Check get office status OK");
        ok(response.data, "GET for single office returned a value");
        utTest.office.deleteData();
        QUnit.start();
      }
    });
  };
  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runGet, utTest.office.testDelay);
});

//*** Update the application.
asyncTest("Update the test office.", function() {
  var newData = {"officeAddress2": "Suite 106", "user": "automated-test@uptempo.biz"};
  
  var runUpdate = function() {
   $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/billingoffice/" + utTest.office.currentKey,
      data: newData,
      dataType: 'json',
      type: 'PUT',
      success: function(response) {
        equal("SUCCESS", response.status, "Check update office status OK");
        utTest.office.deleteData();
        QUnit.start();
      }
    });
  };

  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runUpdate, utTest.office.testDelay);
});
