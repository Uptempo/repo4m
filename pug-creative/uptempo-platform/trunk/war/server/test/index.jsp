<!DOCTYPE HTML>
<html>
  <head>
    <title>UpTempo API Testing</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
    <script src="//code.jquery.com/qunit/qunit-1.12.0.js"></script>
    <link rel="icon" type="image/png" href="/img/favicon.png" />
    <link type="text/css" rel="stylesheet" href="//code.jquery.com/qunit/qunit-1.12.0.css" />
  </head>
  <body>
    <!-- Setup the authkey in the HTTP header. -->
    <script src="/server/js/test/test-setup.js"></script>
    <div id="uptempo-header">
      <div class="uptempo-content">
        <div id="uptempo-header-logo">UpTempo Platform Tests</div>
        <div id="uptempo-header-title"> <h1>UpTempo Platform Testing Page</h1> </div>
      </div>
    </div>
    <div class="uptempo-clear"></div>
    <div id="qunit"></div>
    <div id="qunit-fixture"></div>
    <!-- Office Group tests -->
    <script src="/server/js/test/test-office-group.js"></script>
    <!-- Office tests -->
    <script src="/server/js/test/test-office.js"></script>
    <!-- Doctor tests -->
    <script src="/server/js/test/test-doctor.js"></script>
    <!-- Appointment tests -->
    <script src="/server/js/test/test-appointment.js"></script>
    <div class="uptempo-clear"></div>
    <div id="uptempo-footer" class="uptempo-content">
      <div id="uptempo-footer-content">
        &copy;UpTempo Group 2014. v1.0
      </div>
    </div>
  </body>
</html>
