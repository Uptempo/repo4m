<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8">
  <title>Rheumatology</title>

  <link rel="apple-touch-icon" href="img/MS_Icon.png">
  <link rel="icon" href="img/MS_Icon.png" />
  <link rel="shortcut icon" href="img/MS_Icon.png" />
  <link rel="apple-touch-icon-precomposed" href="img/MS_Icon.png"/>
  <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta name="apple-mobile-web-app-status-bar-style" content="black" />
  <meta content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0, initial-scale=1.0" name="viewport" />
  <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css"  type="text/css"/>
  <link rel="stylesheet" href="css/jquery.mobile-1.2.0.min.css" />
  <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
  <script type="text/javascript" src="js/jquery.mobile-1.2.0.min.js"></script>
  <link rel="stylesheet" href="css/style_ipad2.css" />
  <script type="text/javascript" src="js/scribble-settings.js"></script>
  <script type="text/javascript" src="js/scribble-user.js"></script>
  <script type="text/javascript" src="js/scribble-draw.js"></script>
  <script type="text/javascript" src="js/scribble-nav.js"></script>
  <script type="text/javascript" src="js/scribble-pageload.js"></script>
      
  <!-- CoverFlow -->
  <link rel="stylesheet" href="css/imageflow.css" type="text/css" />
  <script type="text/javascript" src="js/imageflow.js"></script>

  <link rel="stylesheet" href="css/home-landscape.css" />

</head>

<body>

<!-- Start of FIRST page: #one --- HOME PAGE -->
<div data-role="page" id="one" data-url="one" tabindex="0" class="ui-page ui-body-c ui-page-active" style="min-height: 344px;">
  <div data-role="content" class="ui-content" role="main">
    <div id="container">
      <div id="start-container">
        <a href="#" onclick="MedSelect.Scribble.checkLogin()" class="ui-link">
          <img id="splash-image" src="img/splash-portrait.png" alt="home-screen" width="100%" />
        </a>
      </div>
    </div>
  </div>
</div>
<!-- /page one -->


<!-- Start of TWO page: #two --- LOGIN -->
<div data-role="page" id="two">
  <div data-role="content">
    <div id="login-top-bar">
      <div class="logo"><img src="img/pfizer-amgen.png" alt="Pfizer Amgen" width="200px"></div>
      <div id="login-message-title">
        <h2>Rheumatology Patient Education Tool</h2>
      </div>
    </div>
    <div id="login-message-container">
      <strong>Welcome to the Amgen Pfizer Rheumatology Patient Education Tool.</strong>
      Here you have a library of images to refer to while you are consulting with your patients.
      Using this tool, you can draw an image to help illustrate various conditions to individual patients.
      <br /><strong>Please log in.</strong>
    </div>
    <div id="login">
      <form action="#three" id="loginForm" >
        <input type="text" name="email" id="email" value="" placeholder="Email"/>
        <label class="error" for="email" id="email_error">Your e-mail/username is required.</label>
        <input type="password" name="pwd" id="pwd" value ="" placeholder="Password"/>
        <div id="login-forgot"><a href="mailto:help@patienteducation-rheumatology.com?subject=Please reset my password for the Rheumatology Patient Education Tool">forgot password?</a></div>
        <label class="error" for="pwd" id="pwd_error">Your password is required.</label>
        <div class="ui-block-a" id="login-submit"><input type="submit" value="Log In" data-theme="b" /></div>
        <div class="ui-block-a" id="login-register"><input type="button" onclick="location.href='#six'" value="Register" data-theme="b"></div> <br />
      </form>
      <div id="login-loader">
        <img src="img/ajax-loader.gif" alt="Logging in...">
      </div>
      <div class="disclaimer">
        For information and illustrative purposes only.  Not for diagnostic use.
      </div>
    </div>
  </div>
</div><!-- /page two -->


<!-- Start of THREE page: #three --- IMAGEFLOW -->
<div data-role="page" id="three">
  <div data-role="content">
    <div class="logo"><img src="img/pfizer-amgen.png" alt="Pfizer Amgen" width="200px"></div>
    <div id="three-greet"></div>
      <div style="width:50%; margin: 8% auto 0 auto; text-align:center;">
        Choose an image library to use:
        <!-- drop down menu -->
        <form method="post" action="#three" name="area">
          <select name="select-choice-1" id="select-choice-1" onChange="MedSelect.Scribble.changeImageFlow();">
          <option value="Rheumatoid Arthritis">Rheumatoid Arthritis</option>
          <!--<option value="Joints">Joints</option>
          <option value="Neuro">CNS</option>-->
          </select>
        </form>
      </div><!-- /grid-a -->
      <!-- ImageFlow -->
      <div id="brImageFlow" class="imageflow"></div>
      <div id="joImageFlow" class="imageflow"></div>
      <div id="neImageFlow" class="imageflow"></div>
        <!-- Help, settings, info -->
      <div id="menu-bar-bottom"></div>
      
      <div id="menu-settings">
        <a href="#nine"><img src="img/button_help.png" /></a>
        <a href="#five"><img src="img/button_settings.png" alt="Settings" /></a>
        <a href="#seven"><img src="img/button_info.png" alt="Instructions"/></a>
        <a href="#" onclick="MedSelect.Scribble.logout()">Logout</a>
      </div><!-- /button_footer -->
      <div class="disclaimer">
        For information and illustrative purposes only.  Not for diagnostic use.
      </div>
  </div>
</div><!-- /page three -->

<!-- Start of FOUR page: #four --- IMAGE SELECTED -->
<div data-role="page" id="four">
  <div data-role="content">
    <div id="container">
      <div class="logo" id="drawing-logo" ><img src="img/pfizer-amgen.png" alt="Pfizer Amgen" width="200px" /></div>
      <!-- menu top -->
      <div id="menu-button"></div>
      <div id="menu-active-button"></div>
      <div id="menu-bar"></div>
      <div id="menu-ColorPicker">
        <span class="ColorBlotch" style="background-color: rgb(0, 0, 0); border: 2px solid #F1F5F9;" title="#000000">&nbsp;</span>
        <span class="ColorBlotch" style="background-color: rgb(0, 0, 255);" title="#0000FF">&nbsp;</span>
        <span class="ColorBlotch" style="background-color: rgb(144, 0, 255);" title="#9000FF">&nbsp;</span>
        <span class="ColorBlotch" style="background-color: rgb(0, 170, 0);" title="#00AA00">&nbsp;</span>
        <span class="ColorBlotch" style="background-color: rgb(255, 0, 0);" title="#FF0000">&nbsp;</span>
        <span class="ColorBlotch" style="background-color: rgb(255, 108, 0);" title="#FF6C00" >&nbsp;</span>
        <span class="ColorBlotch" style="background-color: rgb(255, 255, 0);" title="#FFFF00">&nbsp;</span>
      </div>
      <div id="menu-lineWidth">
        <span class="lineWidth" id="thin" title="1" style="border: 2px solid #F1F5F9;">&nbsp;</span>
        <span class="lineWidth" id="medium" title="3">&nbsp;</span>
        <span class="lineWidth" id="large" title="5">&nbsp;</span>
        <span class="lineWidth" id="eraser" title="6">&nbsp;</span>
      </div>

      <!-- button help, setting e info -->
      <div id="menu-bar-bottom"></div>
      <div id="image-status-number">
      <div id="image-slider"></div>
      </div>
      <div id="menu-settings">
        <a href="#nine"><img src="img/button_help.png" /></a>
        <a href="#five"><img src="img/button_settings.png" alt="Settings" /></a>
        <a href="#seven"><img src="img/button_info.png" alt="Instructions"/></a>
        <div id="progressbar"></div>
        <div id="img-r">
          <span id="del"><img src="img/button_delete.png" alt="Delete Image" /></span>
          <span class="prev"><img src="img/button_previus.png" alt="Back to Image Selector" /></span>
          <a href="#" onclick="MedSelect.Scribble.showDrawPopup()"><img src="img/button_share.png" alt="Forward or Share the Drawing" /></a>
        </div>
      </div><!-- /button_footer -->
      <canvas id="sketchpad" width="768" height="886">
              Sorry, your browser is not supported.
      </canvas>
      <!-- image -->
      <div id="drawable-image"></div>
      <!-- Send Image Popup -->
      <div data-role="popup" id="draw-popup" data-theme="b" class="ui-corner-all">
        <div style="padding:10px 20px;">
          <a href="#" data-rel="back" data-role="button" data-theme="a" data-icon="delete" data-iconpos="notext" class="ui-btn-right">Cancel</a><h3>E-mail this drawing</h3>

          <label for="un" class="ui-hidden-accessible">Enter Drawing E-mail Recipient:</label>
          <input type="text" name="draw-email" id="draw-email" value="" placeholder="e-mail" data-theme="b" />

          <button type="submit" data-theme="b" onclick="MedSelect.Scribble.uploadCanvas();">Send Drawing</button>
        </div>
      </div>
      <!-- End Send Image Popup -->
    </div> <!-- end container -->
    <div class="disclaimer">
      For information and illustrative purposes only.  Not for diagnostic use.
    </div>
  </div>
</div><!-- /page three -->

<!-- Start of page FIVE --- SETTINGS -->
<div data-role="page" id="five">
  <div class="info-head">
    <div class="logo" style="width:15%"><img src="img/pfizer-amgen.png" alt="Pfizer Amgen" width="200px"></div>
    <div id="done-button-div">
      <a href="#" onClick="javascript:history.back();" data-role="button" data-icon="back" data-theme="b" data-mini="true">Done</a>
    </div>
  </div>
  <div class="info-title">
      <h2>Rheumatology Patient Education Tool</h2>
      <h3>Application Settings</h3>
  </div>
  <div style="height:100%;" data-role="content" data-theme="c">
    <div id="container">
      <div class="info-body">
        <div data-role="collapsible-set" data-theme="c" data-content-theme="d">
          <div data-role="collapsible" data-collapsed="false">
            <h2>E-mail and alert settings</h2>
            <ul data-role="listview">
              <li data-icon="false" class="settings-switch">
                <div data-role="fieldcontain">
                    <label for="notify-email">Notify me of image updates via e-mail:</label>
                    <select name="notify-email" id="notify-email" data-role="slider">
                        <option value="off">OFF</option>
                        <option value="on" selected>ON</option>
                    </select>
                </div>     
              </li>
              <li data-icon="false" class="settings-switch">
                <div data-role="fieldcontain">
                   <label for="notify-txt-message">Notify me of image updates via text message:</label>
                   <select name="notify-txt-message" id="notify-txt-message" data-role="slider">
                        <option value="off">OFF</option>
                        <option value="on">ON</option>
                   </select>
                 </div>
              </li>
              <li data-icon="false" class="settings-switch">
                <div data-role="fieldcontain">
                   <label for="notify-unsubscribe">Unsubscribe for additional links:</label>
                   <select name="notify-unsibscribe" id="notify-unsubscribe" data-role="slider">
                        <option value="off">OFF</option>
                        <option value="on">ON</option>
                   </select>
                 </div>
              </li>
            </ul>
          </div>
        </div> <!-- End collapsible set -->
      </div>
    </div> <!-- End container -->
  </div>
</div>
<!-- End of page FIVE --- SETTINGS -->

<!-- Start of SIX page: #six --- REGISTRATION -->
<div data-role="page" id="six">
    <div class="reg-head">
        <div class="logo" style="width:15%">
            <img src="img/pfizer-amgen.png" alt="Pfizer Amgen" width="200px">
       </div>
    </div>
    <div class="reg-title">
        <h2>Rheumatology Patient Education Tool</h2>
    </div>
    <div data-role="content">
        <div id="container">
            <div class="instructions">
                <p>
                  <strong>Welcome to the Amgen Pfizer Rheumatology Patient Education Tool Registration Page. </strong>
                  Here you can register for the Patient Education Tool. Please fill in all fields.
                  <a href="#">Click to see the Amgen Pfizer Privacy Policy</a>
                </p>
            </div>
            <div id="form-reg-loader">
                <img src="img/ajax-loader.gif" alt="Logging in...">
            </div>
            <div id="form-reg">
                <!-- form register -->
                <form action="POST" onsubmit="MedSelect.Scribble.submitRegistration();" enctype="multipart/form-data" id="contact-form" name="contact-form">
                    <fieldset class="registration">  
                        <div data-role="fieldcontain" class="fieldalign">
                            <label for="title">Title:</label>
                            <input type="text" id="c-title" name="title" value="" />
                        </div>
                        <div data-role="fieldcontain" class="fieldalign">
                            <label for="firstname">First Name: </label>
                            <input type="text" id="c-firstname" name="firstname" value="" />
                        </div>
                        <div data-role="fieldcontain" class="fieldalign">
                            <label for="lasttname">Last Name: </label>
                            <input type="text" id="c-lastname" name="lastname" value="" />
                        </div>
                        <div data-role="fieldcontain" class="fieldalign">
                            <label for="address1">Address1: </label>
                            <input type="text" id="c-address1" name="address1" value="" />
                        </div>
                       <div data-role="fieldcontain" class="fieldalign">
                            <label for="address2">Address2: </label>
                            <input type="text" id="c-address2" name="address2" value="" />
                        </div>
                        
                        <div class="ui-grid-a">
                            <div class="ui-block-a">
                                <div data-role="fieldcontain" class="fieldalign">
                                    <label for="city">City: </label>
                                    <input type="text" id="c-city" name="city" value="" />
                                </div>
                            </div>
                            <div class="ui-block-b">
                                <div data-role="fieldcontain" class="city-state-fieldalign">
                                    <label for="state1" class="statealign">State: </label>
                                    <select id="c-state" name="state"  />
                                        <option value="NULL"></option>
                                        <option value="AL">AL</option>
                                        <option value="AK">AK</option>
                                        <option value="AZ">AZ</option>
                                        <option value="AR">AR</option>
                                        <option value="CA">CA</option>
                                        <option value="CO">CO</option>
                                        <option value="CT">CT</option>
                                        <option value="DE">DE</option>
                                        <option value="DC">DC</option>
                                        <option value="FL">FL</option>
                                        <option value="GA">GA</option>
                                        <option value="HI">HI</option>
                                        <option value="ID">ID</option>
                                        <option value="IL">IL</option>
                                        <option value="IN">IN</option>
                                        <option value="IA">IA</option>
                                        <option value="KS">KS</option>
                                        <option value="KY">KY</option>
                                        <option value="LA">LA</option>
                                        <option value="ME">ME</option>
                                        <option value="MD">MD</option>
                                        <option value="MA">MA</option>
                                        <option value="MI">MI</option>
                                        <option value="MN">MI</option>
                                        <option value="MS">MS</option>
                                        <option value="MO">MO</option>
                                        <option value="MT">MT</option>
                                        <option value="NE">NE</option>
                                        <option value="NV">NV</option>
                                        <option value="NH">NH</option>
                                        <option value="NJ">NJ</option>
                                        <option value="NM">NM</option>
                                        <option value="NY">NY</option>
                                        <option value="NC">NC</option>
                                        <option value="ND">ND</option>
                                        <option value="OH">OH</option>
                                        <option value="OK">OK</option>
                                        <option value="OR">OR</option>
                                        <option value="PA">PA</option>
                                        <option value="RI">RI</option>
                                        <option value="SC">SC</option>
                                        <option value="SD">SD</option>
                                        <option value="TN">TN</option>
                                        <option value="TX">TX</option>
                                        <option value="UT">UT</option>
                                        <option value="VT">VT</option>
                                        <option value="VA">VA</option>
                                        <option value="WA">WA</option>
                                        <option value="WV">WV</option>
                                        <option value="WI">WI</option>
                                        <option value="WY">WY</option>        
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div data-role="fieldcontain" class="fieldalign">
                            <label for="cell">Cell: </label>
                            <input type="text" id="c-cell" name="cell" value="" />
                        </div>
                        <div data-role="fieldcontain" class="fieldalign">   
                            <label for="emai1">Email/Username: </label>
                            <input type="text" id="c-email" name="email" value="" />
                        </div>
                        <div data-role="fieldcontain" class="fieldalign">
                            <label for="password">Password: </label>
                            <input type="password" id="c-password" name="password" value="" />
                        </div>
                        <div class="ui-grid-b">
                            <div class="ui-block-a"></div>
                            <div class="ui-block-b"><input type="button" onclick="MedSelect.Scribble.submitRegistration();" value="Register" data-theme="b"></div>
                            <div class="ui-block-c"></div>
                        </div>     
                    </fieldset>
                </form>
            </div><!-- End form-reg -->
        </div>
      <div class="disclaimer reposition">
          <p>For information and illustrative purposes only.  Not for diagnostic use.</p>
      </div>
    </div>
</div>

<!-- Start of page Seven - Info-->
<div data-role="page" id="seven">
  <div class="info-head">
    <div class="logo" style="width:15%"><img src="img/pfizer-amgen.png" alt="Pfizer Amgen" width="200px"></div>
    <div id="done-button-div">
      <a href="#" onClick="javascript:history.back();" data-role="button" data-icon="back" data-theme="b" data-mini="true">Done</a>
    </div>
  </div>
  <div class="info-title">
      <h2>Rheumatology Patient Education Tool</h2>
      <h3>Information</h3>
  </div>
  <div style="height:100%;" data-role="content" data-theme="b">
    <div id="container">
      <div class="info-body">
        <ul data-role="listview" data-inset="true" data-theme="c">
          <li><a href="/download/AmgenPfizerEmailPrivacy.pdf" target="_blank">Amgen/Pfizer Privacy Statement</a></li>
          <li><a href="/download/AmgenPfizerEmailPrivacy.pdf" target="_blank">E-mail Privacy Policy</a></li>
          <li>
            <a href="mailto:help@patienteducation-rheumatology.com?subject=Rheumatology Patient Education Tool Help">
              Contact Patient Education Tool Help Desk
            </a>
          </li>
          <li><a href="/download/ElsevierLicense.pdf" target="_blank">Elsevier Image License</a></li>
        </ul>
      </div>
      <!-- logo -->
      <div id="logo">
        <img src="img/MS_Icon.png" style="float:left; margin: 0 30px;"><br>
        <h3>Patient Education Tool for iPad <br> Version 2.0.1 <br> &copy;2012 McCallan Health LLC. </h3><br><br>
      </div>
    </div>
  </div>
  <!--<div data-role="footer">
    <h4>Footer content</h4>
  </div>--><!-- /footer -->
</div>
<!-- End of page Seven - Info -->

<!-- Start of page NINE --- HELP -->
<div data-role="page" id="nine">
  <div class="info-head">
    <div class="logo" style="width:15%"><img src="img/pfizer-amgen.png" alt="Pfizer Amgen" width="200px"></div>
    <div id="done-button-div">
      <a href="#" onClick="javascript:history.back();" data-role="button" data-icon="back" data-theme="b" data-mini="true">Done</a>
    </div>
  </div>
  <div class="info-title">
      <h2>Rheumatology Patient Education Tool</h2>
      <h3>Help</h3>
  </div>
  <div style="height:100%;" data-role="content" data-theme="c">
    <div id="container">
      <div class="info-body">
        <ul data-role="listview" data-inset="true">
          <li><a href="/download/AmgenPfizerInstructions.pdf" target="_blank" data-ajax="false">Instructions</a></li>
          <li><a href="mailto:help@patienteducation-rheumatology.com?subject=Rheumatology Patient Education Tool Help">Contact Patient Eduction Tool Help Desk</a></li>
        </ul>
      </div>
    </div>
  </div>
</div>
<!-- End of page NINE --- HELP -->
</body>
</html>
