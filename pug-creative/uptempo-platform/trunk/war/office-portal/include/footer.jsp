
<!-- BEGIN JAVASCRIPTS -->    
  <!-- Load javascripts at bottom, this will reduce page load time -->
  <script src="js/jquery-1.8.3.min.js"></script>
  <script src="assets/jquery-ui/jquery-ui-1.10.1.custom.min.js" type="text/javascript"></script>
  <script src="assets/bootstrap/js/bootstrap.min.js"></script>
  <script src="js/jquery.blockui.js"></script>
  <!-- ie8 fixes -->
  <!--[if lt IE 9]>
  <script src="js/excanvas.js"></script>
  <script src="js/respond.js"></script>
  <![endif]-->
  <script type="text/javascript" src="assets/chosen-bootstrap/chosen/chosen.jquery.min.js"></script>
  <script type="text/javascript" src="assets/uniform/jquery.uniform.min.js"></script>
  <script src="js/scripts.js"></script>
  <script src="js/ui-jqueryui.js"></script>
  <script type="text/javascript" src="assets/data-tables/jquery.dataTables.js"></script>
  <script type="text/javascript" src="assets/data-tables/DT_bootstrap.js"></script>
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