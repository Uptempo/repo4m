<!-- BEGIN PAGE -->  
<div class="main-content">
  <!-- BEGIN PAGE CONTAINER-->
  <div class="container-fluid">
    <!-- BEGIN PAGE HEADER-->   
    <div class="row-fluid">
      <div class="span12">
        <!-- BEGIN PAGE TITLE & BREADCRUMB-->
        <h3 class="page-title">Office Info <small>Page</small></h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
      </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BEGIN PAGE CONTENT-->
    <div class="row-fluid">
      <div class="span12">
          <ul class="nav nav-pills"  id="offices-list">
  
          </ul>
          
        <div class="widget">
          <div class="widget-title">
            <h4><i class="icon-user"></i>Office Info</h4>                  
          </div>

		  <div class="widget-body">
<!--            <div class="span3">
              <div class="text-center profile-pic">
                <img src="img/profile-pic.jpg" alt="">
              </div>
              <button type="button" class="btn btn-primary" style="margin-left: 20px;">Change Photo</button>
            </div>
-->
            <div class="span9">
              <h4><span id="billingoffices-officeName" class="editable"></span></h4>
                <span id="billingoffices-officeGroup" class="editable" style="display:none;"></span>
              <table class="table table-borderless">
                <tbody>
                  <tr>
                    <td class="span2">Address 1:</td>
                    <td><span id="billingoffices-officeAddress1" class="editable"></span></td>
                  </tr>
                  <tr>
                    <td class="span2">Address 2:</td>
                    <td><span id="billingoffices-officeAddress2" class="editable"></span></td>
                  </tr>
                  <tr>
                    <td class="span2">City: </td>
                    <td><span id="billingoffices-officeCity" class="editable"></span></td>
                  </tr>
                  <tr>
                    <td class="span2">Country: </td>
                    <td><span id="billingoffices-officeCountry" class="editable"></span></td>
                  </tr>
                  <tr>
                    <td class="span2">State: </td>
                    <td><span id="billingoffices-officeState" class="editable"></span></td>
                  </tr>
                  <tr>
                    <td class="span2">Postal Code: </td>
                    <td>
                      <span id="billingoffices-officePostalCode" class="editable"></span>
                    </td>
                  </tr>
                  <tr>
                    <td class="span2"> Email :</td>
                    <td><span id="billingoffices-officeEmail" class="editable"></span></td>
                  </tr>
<!--
                  <tr>
                    <td class="span2"> Mobile :</td>
                    <td>12345677</td>
                  </tr>
-->
                </tbody>
              </table>
              <h4>Notes</h4>
              <p class="push"><span id="billingoffices-officeNotes" class="editable"></span></p>
            </div>
            <div class="span3">
              <button type="button" class="btn btn-primary" style="float:right;" onclick="javascript:uptempo.officePortal.billingOffices.makePageEditable('#billingoffice-page', $(this))" id="edit-profile">Edit Profile</button>
            </div>
            <div class="space5"></div>
          </div>
        </div>
      </div>
    </div>
    <!-- END PAGE CONTENT-->         
   </div>
   <!-- END PAGE CONTAINER-->
</div>
<!-- END PAGE --> 