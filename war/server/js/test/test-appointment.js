//*** Global test data.
var rlApplicationTest = {};
//*** Tests require a delay due to datastore persistence.
rlApplicationTest.testDelay = 1000;
rlApplicationTest.testData = {
  data: [
    {"name": "TestingAppName",
     "description": "App Description",
     "url": "http://testmyapp.appspot.com"}
  ]};

/**
 * Add data shortcut.
 */
rlApplicationTest.addData = function() {
  //*** Call the application API.
  $.ajax({
    contentType: 'application/json',
    url: "/api/application",
    data: rlApplicationTest.testData.data[0],
    dataType: 'json',
    type: 'POST',
    success: function(response) {
      //*** Save the application key.
      rlApplicationTest.currentKey = response.data.key;
    }
  });
};

/**
 * Delete data shortcut.
 */
rlApplicationTest.deleteData = function() {
  //*** Call the application API.
  $.ajax({
    contentType: 'application/json',
    url: "/api/application/" + rlApplicationTest.currentKey,
    dataType: 'json',
    type: 'DELETE',
    success: function() {
      //*** Do nothing.
    }
  });
};

module("Application tests", {
  setup: rlApplicationTest.addData
});

//*** Get application list.
asyncTest( "Get application list.", function() {
  var runList = function() {
    //*** Call the application API.
    $.ajax({
      contentType: 'application/json',
      url: "/api/application",
      dataType: 'json',
      type: 'GET',
      cache: false,
      success: function(response) {
        equal("SUCCESS", response.status, "Check list status OK");
        ok(response.data.length > 0, "List size OK");
        rlApplicationTest.deleteData();
        QUnit.start();
      }
    });
  };

  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runList, rlApplicationTest.testDelay);
});

//*** Get single application by key.
asyncTest("Retrieve added application.", function() {
  var runGet = function() {
    //*** Call the application API.
    $.ajax({
      contentType: 'application/json',
      url: "/api/application/" + rlApplicationTest.currentKey,
      dataType: 'json',
      type: 'GET',
      cache: false,
      success: function(response) {
        equal("SUCCESS", response.status, "Check get application status OK");
        equal(1, response.data.length, "Get application length");
        rlApplicationTest.deleteData();
        QUnit.start();
      }
    });
  };
  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runGet, rlApplicationTest.testDelay);
});

//*** Update the application.
asyncTest("Update the application.", function() {
  var newData = {"description": "Updated test application."};
  
  var runUpdate = function() {
   $.ajax({
      contentType: 'application/json',
      url: "/api/application/"  + rlApplicationTest.currentKey,
      data: newData,
      dataType: 'json',
      type: 'PUT',
      success: function(response) {
        equal("SUCCESS", response.status, "Check update application status OK");
        rlApplicationTest.deleteData();
        QUnit.start();
      }
    });
  };

  //*** Introduce a delay for datastore save to take effect.
  setTimeout(runUpdate, rlApplicationTest.testDelay);
});
