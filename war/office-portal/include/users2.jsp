      <!-- BEGIN PAGE -->  
      <div id="main-content">
       <!-- BEGIN PAGE CONTAINER-->
       <div class="container-fluid">
        <!-- BEGIN PAGE HEADER-->   
        <div class="row-fluid">
         <div class="span12">
          <!-- BEGIN PAGE TITLE & BREADCRUMB-->
          <h3 class="page-title">
            Users
            <small>Page</small>
          </h3>
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
              <h3>Users List</h3>
              <ul class="">
                <li>
                  <a href="" class="user-name btn-primary-gray">User 1</a>
                  <a class="delete-button" href="#">X</a>
                </li>
                <li>
                  <a href="" class="user-name btn-primary-gray">User 2</a>
                  <a class="delete-button" href="#">X</a>
                </li>
                <li>
                  <a href="" class="user-name btn-primary-gray">User 3</a>
                  <a class="delete-button" href="#">X</a>
                </li>
                <li>
                  <a href="" class="user-name btn-primary-gray">User 4</a>
                  <a class="delete-button" href="#">X</a>
                </li>
                <li>
                  <a href="" class="user-name btn-primary">Dr. Perjan Duro</a>
                  <a class="delete-button" href="#">X</a>
                </li>
              </ul>
              <a href="#myModal1" class="btn btn-large btn-block btn-success" data-toggle="modal" type="button" onclick="uptempo.user.showNewOP();">Add New User</a>
            </div>
            <div class="span8 person-details">
              <h3>Perjan Duro Detailed Profile <small><a href="#">Edit Profile</a></small>

              </h3>
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

<div id="myModal1" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel1" aria-hidden="false" style="display: block;">
  <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
      <h3 id="myModalLabel1">Modal Header</h3>
  </div>
  <div class="modal-body">
      <div style="padding:10px 20px;">
    <h3>
      <span id="user-form-title">Create a new user</span>
    </h3>
    <div id="user-form-errors" class="form-errors"></div>
    <input type="text" size="40" name="user-email" id="user-email" value="" placeholder="Username/E-mail" data-theme="a" />

    <select id="user-title" name="user-title" placeholder="Title">
      <option value="DEFAULT">Select Salutation</option>
      <option value="Dr.">Dr.</option>
      <option value="Mr.">Mr.</option>
      <option value="Miss">Miss</option>
      <option value="Mrs.">Mrs.</option>
      <option value="Ms.">Ms.</option>
    </select>

    <input type="text" size="20" name="user-fname" id="user-fname" value="" placeholder="First Name" data-theme="a" />

    <input type="text" size="20" name="user-lname" id="user-lname" value="" placeholder="Last Name" data-theme="a" />

    <input type="text" size="40" name="user-address1" id="user-address1" value="" placeholder="Address 1" data-theme="a" />
    
    <input type="text" size="40" name="user-address2" id="user-address2" value="" placeholder="Address 2" data-theme="a" />

    <input type="text" size="20" name="user-city" id="user-city" value="" placeholder="City" data-theme="a" />
    
    <select id="user-state" name="user-state" placeholder="State">
      <option value="AL">Alabama</option>
      <option value="AK">Alaska</option>
      <option value="AZ">Arizona</option>
      <option value="AR">Arkansas</option>
      <option value="CA">California</option>
      <option value="CO">Colorado</option>
      <option value="CT">Connecticut</option>
      <option value="DE">Delaware</option>
      <option value="DC">District of Columbia</option>
      <option value="FL">Florida</option>
      <option value="GA">Georgia</option>
      <option value="HI">Hawaii</option>
      <option value="ID">Idaho</option>
      <option value="IL">Illinois</option>
      <option value="IN">Indiana</option>
      <option value="IA">Iowa</option>
      <option value="KS">Kansas</option>
      <option value="KY">Kentucky</option>
      <option value="LA">Louisiana</option>
      <option value="ME">Maine</option>
      <option value="MD">Maryland</option>
      <option value="MA">Massachusetts</option>
      <option value="MI">Michigan</option>
      <option value="MN">Minnesota</option>
      <option value="MS">Mississippi</option>
      <option value="MO">Missouri</option>
      <option value="MT">Montana</option>
      <option value="NE">Nebraska</option>
      <option value="NV">Nevada</option>
      <option value="NH">New Hampshire</option>
      <option value="NJ">New Jersey</option>
      <option value="NM">New Mexico</option>
      <option value="NY">New York</option>
      <option value="NC">North Carolina</option>
      <option value="ND">North Dakota</option>
      <option value="OH">Ohio</option>
      <option value="OK">Oklahoma</option>
      <option value="OR">Oregon</option>
      <option value="PA">Pennsylvania</option>
      <option value="RI">Rhode Island</option>
      <option value="SC">South Carolina</option>
      <option value="SD">South Dakota</option>
      <option value="TN">Tennessee</option>
      <option value="TX">Texas</option>
      <option value="UT">Utah</option>
      <option value="VT">Vermont</option>
      <option value="VA">Virginia</option>
      <option value="WA">Washington</option>
      <option value="WV">West Virginia</option>
      <option value="WI">Wisconsin</option>
      <option value="WY">Wyoming</option>
    </select>

    <input type="text" size="20" name="user-cell" id="user-cell" value="" placeholder="Cell Phone" data-theme="a" />

    <input type="password" name="user-pwd" id="user-pwd" value="" placeholder="password" data-theme="a" />

    <input type="hidden" name="user-key" id="user-key" />

    <input id="user-form-submit" type="submit" data-theme="b" />
  </div>  </div>
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



