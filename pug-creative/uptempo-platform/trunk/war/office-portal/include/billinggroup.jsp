<!-- BEGIN PAGE -->  
<div class="main-content">
  <!-- BEGIN PAGE CONTAINER-->
  <div class="container-fluid">
    <!-- BEGIN PAGE HEADER-->   
    <div class="row-fluid">
      <div class="span12">
        <!-- BEGIN PAGE TITLE & BREADCRUMB-->
        <h3 class="page-title">Group Info <small>Page</small></h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
      </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BEGIN PAGE CONTENT-->
    <div class="row-fluid">
      <div class="span12">
        <div class="widget">
          <div class="widget-title">
            <h4><i class="icon-user"></i>Group Info</h4>                  
          </div>
          <div class="widget-body">
<!--
            <div class="span3">
              <div class="text-center profile-pic">
                <img src="img/profile-pic.jpg" alt="">
              </div>
              <button type="button" class="btn btn-primary" style="margin-left: 20px;">Change Photo</button>
            </div>
-->
            <div class="span9">
              <h4><span id="billinggroups-groupName" class="editable"></span></h4>
              <table class="table table-borderless">
                <tbody>
                  <tr>
                    <td class="span2">Address 1:</td>
                    <td>
                      <span id="billinggroups-groupAddress1" class="editable"></span>
                    </td>
                  </tr>
                  <tr>
                    <td class="span2">Address 2:</td>
                    <td>
                      <span id="billinggroups-groupAddress2" class="editable"></span>
                    </td>
                  </tr>
                  <tr>
                    <td class="span2">City:</td>
                    <td>
                      <span id="billinggroups-groupCity" class="editable"></span>
                    </td>
                  </tr>    
                  <tr>
                    <td class="span2">Country: </td>
                    <td>
                      <span id="billinggroups-groupCountry" class="editable"></span>
                    </td>
                  </tr>
                  <tr>
                    <td class="span2">State: </td>
                    <td>
                      <span  id="billinggroups-groupState" class="editable"></span>
                    </td>
                  </tr>
                  <tr>
                    <td class="span2">Postal Code: </td>
                    <td>
                      <span  id="billinggroups-groupPostalCode" class="editable"></span>
                    </td>
                  </tr>
                  <tr>
                    <td class="span2"> Email :</td>
                    <td>
                      <span  id="billinggroups-groupEmail" class="editable"></span>
                    </td>
                  </tr>
<!--
                  <tr>
                    <td class="span2"> Mobile :</td>
                    <td id="billinggroup-groupCountry">
                      12345677
                    </td>
                  </tr>
-->
                </tbody>
              </table>
              <h4>Notes</h4>

              <p class="push"><span  id="billinggroups-groupNotes" class="editable"></span></p>
            </div>
            <div class="span3">
              <button type="button" class="btn btn-primary" style="float:right;" onclick="javascript:uptempo.officePortal.billingGroup.makePageEditable('#billinggroup-page', $(this))">Edit Profile</button>
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