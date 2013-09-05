<!-- BEGIN PAGE -->  
<div id="main-content">
  <!-- BEGIN PAGE CONTAINER-->
  <div class="container-fluid">
    <!-- BEGIN PAGE HEADER-->   
    <div class="row-fluid">
      <div class="span12">
        <!-- BEGIN PAGE TITLE & BREADCRUMB-->
        <h3 class="page-title">Doctors <small>Page</small></h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
      </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BEGIN PAGE CONTENT-->
    <div class="row-fluid">
      <div class="span12">
        <div class="widget">
          <div class="widget-body">
            <div class="span4 persons-list">
              <h3>Doctors List</h3>
              <ul class="">
                <li>
                  <a href="" class="user-name btn-primary-gray">Doctor 1</a>
                  <a class="delete-button" href="#">X</a>
                </li>
                <li>
                  <a href="" class="user-name btn-primary-gray">Doctor 2</a>
                  <a class="delete-button" href="#">X</a>
                </li>
                <li>
                  <a href="" class="user-name btn-primary-gray">Doctor 3</a>
                  <a class="delete-button" href="#">X</a>
                </li>
                <li>
                  <a href="" class="user-name btn-primary-gray">Doctor 4</a>
                  <a class="delete-button" href="#">X</a>
                </li>
                <li>
                  <a href="" class="user-name btn-primary">Dr. Perjan Duro</a>
                  <a class="delete-button" href="#">X</a>
                </li>
              </ul>
              <a href="#modal-new-doctor" class="btn btn-large btn-block btn-success" data-toggle="modal" type="button">Add New Doctor</a>
            </div>
            <div class="span8 person-details">
              <h3>Dr. Perjan Duro Detailed Profile <small><a href="#">Edit Profile</a></small></h3>
              <div class="row-fluid">
                <div class="span8">
                  <table class="table table-borderless">
                    <tbody>
                      <tr>
                        <td class="span2 field-description">First Name</td>
                        <td>
                          Perjan
                        </td>
                      </tr>
                      <tr>
                        <td class="span2 field-description">Last Name</td>
                        <td>
                          Duro
                        </td>
                      </tr>
                      <tr>
                        <td class="span2 field-description">Titles</td>
                        <td>
                          Dr. MD
                        </td>
                      </tr>
                      <tr>
                        <td class="span2 field-description">Specialities</td>
                        <td>
                          Cardio
                        </td>
                      </tr>
                      <tr>
                        <td class="span2 field-description">Education</td>
                        <td>
                          M.D USC 2001
                        </td>
                      </tr>
                      <tr>
                        <td class="span2 field-description">Public Description</td>
                        <td>
                          This doctor is amazing.
                        </td>
                      </tr>
                      <tr>
                        <td class="span2 field-description">Internal Notes</td>
                        <td>
                          This doctors plays a lot of PS4. 
                        </td>
                      </tr>
                    </tbody>
                  </table>                                  
                </div>
                <div class="span4">
                  <div class="text-center profile-pic">
                    <img src="img/profile-pic.jpg" alt="">
                  </div>
                  <button type="button" class="btn btn-primary" style="float:right; margin-right:10px;">Change Photo</button>               
                </div>                                  
              </div>
              <h4>Notes</h4>
              <p class="push">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas ultrices, justo vel imperdiet gravida, urna ligula hendrerit nibh, ac cursus nibh sapien in purus. Mauris tincidunt tincidunt turpis in porta. Integer fermentum tincidunt auctor. Vestibulum ullamcorper, odio sed rhoncus imperdiet, enim elit sollicitudin orci, eget dictum leo mi nec lectus. Nam commodo turpis id lectus scelerisque vulputate. Integer sed dolor erat. Fusce erat ipsum, varius vel euismod sed, tristique et lectus? Etiam egestas fringilla enim, id convallis lectus laoreet at. Fusce purus nisi, gravida sed consectetur ut, interdum quis nisi. Quisque egestas nisl id lectus facilisis scelerisque? Proin rhoncus dui at ligula vestibulum ut facilisis ante sodales! Suspendisse potenti. Aliquam tincidunt sollicitudin sem nec ultrices. Sed at mi velit. Ut egestas tempor est, in cursus enim venenatis eget! Nulla quis ligula ipsum. Donec vitae ultrices dolor?</p>
            </div>
            <div class="space5"></div>
          </div>
        </div>
      </div>
    </div>

    <div id="modal-new-doctor" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="user-form-title" aria-hidden="false" style="display: block;">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
        <h3 id="user-form-title">Create a new Doctor</h3>
      </div>
      <div class="modal-body">
        <form action="#" class="form-horizontal">
          <div id="user-form-errors" class="form-errors"></div>
          <div class="control-group">
            <label class="control-label">First Name</label>
            <div class="controls">
              <input type="text" placeholder="First Name" class="input-xlarge" id="user-fname" >
            </div>
          </div>
          <div class="control-group">
            <label class="control-label">Last Name</label>
            <div class="controls">
              <input type="text" placeholder="Last Name" class="input-xlarge" id="user-lname">
            </div>
          </div>
          <div class="control-group">
            <label class="control-label">Title</label>
            <div class="controls">
              <select id="user-title" name="user-title" placeholder="Title">
                <option value="DEFAULT">Select Salutation</option>
                <option value="Dr.">Dr.</option>
                <option value="Mr.">Mr.</option>
                <option value="Miss">Miss</option>
                <option value="Mrs.">Mrs.</option>
                <option value="Ms.">Ms.</option>
              </select>
            </div>
          </div>
          <div class="control-group">
            <label class="control-label">Education</label>
            <div class="controls">
              <input type="text" placeholder="Education" class="input-xlarge" id="user-education">
            </div>
          </div> 
          <div class="control-group">
            <label class="control-label">Public Description</label>
            <div class="controls">
              <textarea class="input-xlarge" rows="3"></textarea>
            </div>
          </div>
          <div class="control-group">
            <label class="control-label">Internal Notes</label>
            <div class="controls">
              <textarea class="input-xlarge" rows="3"></textarea>
            </div>
          </div>
          <input type="hidden" name="user-key" id="user-key" />
        </form>
      </div>
      <div class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
        <input id="user-form-submit" type="submit" class="btn btn-primary" name="Submit"/>
      </div>
    </div>    
    <!-- END PAGE CONTENT-->         
  </div>
  <!-- END PAGE CONTAINER-->
</div>
<!-- END PAGE -->  



