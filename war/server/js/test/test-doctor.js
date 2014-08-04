//*** Office Group Test Data.
utTest.doctor = {};

//*** Tests require a delay due to datastore persistence.
utTest.doctor.testDelay = 500;
utTest.doctor.testData = {
  data: [
    {"firstName": "Mike",
     "lastName": "McCarthy",
     "email": "mike@testoffice.com",
     "education": "1988 NYU, 1994 Johns Hopkins Medical School",
     "publicDescription": "This is a description of Dr. McCarthy",
     "notes": "Number one provider of exotic medicines.",
     "specialty1": "Cardiology",
     "title1": "Dr.",
     "user": "automated-test@uptempo.biz"
   }
  ]};

/**
 * Add data shortcut.
 */
utTest.doctor.addData = function() {
  //*** Chain the office API and doctor API calls due to the dependency between the two.
  $.ajax({
    contentType: 'application/json',
    url: utTest.urlPrefix + "/service/billingoffice",
    data: utTest.office.testData.data[0],
    dataType: 'json',
    type: 'POST',
    success: function(response) {
      //*** Save the office key.
      utTest.office.currentKey = response.data.key;
      utTest.doctor.testData.data[0].billingOffice = response.data.key;
      $.ajax({
        contentType: 'application/json',
        url: utTest.urlPrefix + "/service/doctor",
        data: utTest.doctor.testData.data[0],
        dataType: 'json',
        type: 'POST',
        success: function(response) {
          //*** Save the application key.
          utTest.doctor.currentKey = response.data.key;
          equal("SUCCESS", response.status, "Add doctor OK");
        }
      });
    }
  });
};

/**
 * Delete data shortcut.
 */
utTest.doctor.deleteData = function() {
  //*** Call the application API.
  if (utTest.doctor.currentKey) {
    $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/doctor/" + utTest.doctor.currentKey,
      dataType: 'json',
      type: 'DELETE',
      success: function() {
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
    });
  }
};

module("Doctor Profile tests", {
  setup: utTest.doctor.addData
});

//*** Get application list.
asyncTest( "Get doctor.", function() {
  var runList = function() {
    //*** Call the application API.
    $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/doctor",
      dataType: 'json',
      type: 'GET',
      cache: false,
      success: function(response) {
        equal("SUCCESS", response.status, "Check list doctor status OK");
        ok(response.data.values.length > 0, "List size OK");
        utTest.doctor.deleteData();
        QUnit.start();
      }
    });
  };

  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runList, utTest.doctor.testDelay);
});

//*** Get single application by key.
asyncTest("Retrieve testing doctor.", function() {
  var runGet = function() {
    //*** Call the application API.
    $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/doctor/" + utTest.doctor.currentKey,
      dataType: 'json',
      type: 'GET',
      cache: false,
      success: function(response) {
        equal("SUCCESS", response.status, "Check get doctor status OK");
        ok(response.data, "GET for single doctor returned a value");
        utTest.doctor.deleteData();
        QUnit.start();
      }
    });
  };
  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runGet, utTest.doctor.testDelay);
});

//*** Update the application.
asyncTest("Update the test doctor.", function() {
  var newData = utTest.doctor.testData.data[0];
  newData.publicDescription = "Updated description of Dr. McCarthy";
  newData.user = "automated-test@uptempo.biz";
  
  var runUpdate = function() {
   $.ajax({
      contentType: 'application/json',
      url: utTest.urlPrefix + "/service/doctor/" + utTest.doctor.currentKey,
      data: newData,
      dataType: 'json',
      type: 'PUT',
      success: function(response) {
        equal("SUCCESS", response.status, "Check update doctor status OK");
        utTest.doctor.deleteData();
        QUnit.start();
      }
    });
  };

  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runUpdate, utTest.doctor.testDelay);
});
