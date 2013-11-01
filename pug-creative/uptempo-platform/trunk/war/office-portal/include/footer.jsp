
<!-- BEGIN JAVASCRIPTS -->    
  <!-- Load javascripts at bottom, this will reduce page load time -->
  <!-- <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script> -->
  <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>

  <script src="/office-portal/assets/bootstrap/js/bootstrap.min.js"></script>
  <script src="/office-portal/js/jquery.blockui.js"></script>
  <!-- ie8 fixes -->
  <!--[if lt IE 9]>
  <script src="js/excanvas.js"></script>
  <script src="js/respond.js"></script>
  <![endif]-->
  <script type="text/javascript" src="/office-portal/assets/chosen-bootstrap/chosen/chosen.jquery.min.js"></script>
  <script type="text/javascript" src="/office-portal/assets/uniform/jquery.uniform.min.js"></script>
  <script src="/office-portal/js/scripts.js"></script>
  <script src="/office-portal/js/ui-jqueryui.js"></script>
  <script type="text/javascript" src="/office-portal/assets/data-tables/jquery.dataTables.js"></script>
  <script type="text/javascript" src="/office-portal/assets/data-tables/DT_bootstrap.js"></script>
  <script>
    jQuery(document).ready(function() {       
      // initiate layout and plugins
      App.init();
      // Init calendar elements and jQuery UI
      UIJQueryUI.init();

      // Remove default jQuery UI classes from the calendar
      $('#ui_date_picker_inline').find('.ui-datepicker-inline').css('width','100%').removeClass('ui-datepicker-multi-2 ui-datepicker-multi');
    });
  </script>
<!-- END JAVASCRIPTS -->