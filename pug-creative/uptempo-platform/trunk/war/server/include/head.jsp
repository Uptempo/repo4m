<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Uptempo Application Administration</title>
  <link rel="stylesheet" href="/css/jquery.mobile-1.2.0.min.css" />
  <link rel="stylesheet" type="text/css" href="/server/css/datepick-default.css" />
  <link rel="stylesheet" type="text/css" href="/server/css/style.css" />
  <link rel="stylesheet" type="text/css" href="/server/css/jquery.dataTables.css" />
  <script type="text/javascript" src="/js/jquery-1.8.2.min.js"></script>
  <script type="text/javascript" src="/js/jquery.mobile-1.2.0.min.js"></script>
  <script type="text/javascript" src="/server/js/glDatePicker.min.js"></script>
  <script type="text/javascript" src="/server/js/jquery.dataTables.min.js"></script>
  <script type="text/javascript" src="/server/js/constants.js"></script>
  <script type="text/javascript" src="/server/js/web-app-js.js"></script>
  <script type="text/javascript" src="/server/js/user-actions.js"></script>
  <script type="text/javascript" src="/server/js/config-actions.js"></script>
  <script type="text/javascript" src="/server/js/config-import-data.js"></script>
  <script type="text/javascript" src="/server/js/app-actions.js"></script>
  <script type="text/javascript" src="/server/js/audit-actions.js"></script>
  <script type="text/javascript" src="/server/js/audit-log-actions.js"></script>
  <script type="text/javascript" src="/server/js/appt-actions.js"></script>
  <script type="text/javascript" src="/server/js/staticlists-actions.js"></script>
  <script type="text/javascript" src="/server/js/staticlists-import-data.js"></script>
  <script type="text/javascript" src="/server/js/billinggroup-actions.js"></script>
  <script type="text/javascript" src="/server/js/billingoffice-actions.js"></script>
  <script type="text/javascript" src="/server/js/doctor-actions.js"></script>
  <script type="text/javascript" src="/server/js/doctorimageupload-actions.js"></script>

  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
  <meta name="apple-movile-web-app-capable" content="yes" />
  <meta name="apple-movile-web-app-status-bar-style" content="black" />
  <link rel="apple-touch-icon-precomposed" type="text/css" href="/server/img/icon.png?v=1" />
  <link rel="apple-startup-image"
        type="text/css"
        href="server/img/startup_landscape.jpg?v=1"
        media="screen and (min-device-width:481px) and (max-device-width:1024px) and (orientation:landscape)" />
  <link rel="apple-startup-image"
        type="text/css"
        href="server/img/startup_portrait.jpg?v=1"
        media="screen and (min-device-width:481px) and (max-device-width:1024px) and (orientation:portrait)" />
  <script type="text/javascript">
    uptempo.globals = {}
    uptempo.globals.user = '<%=request.getAttribute("user-name") %>';
    $.ajaxSetup({
      headers: {"uptempokey": '<%=request.getAttribute("uptempo-authkey") %>'}
    });
    //***Application startup load.
    uptempo.ajax.populateConfigValues();
  </script>
</head>
