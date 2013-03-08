//***Page Document Ready
jQuery(document).ready(function(event){
  MedSelect.Scribble.getDimensions();
  //*** Disable the landscape stylesheet.
  if(window.orientation == 0) {
    document.styleSheets[4].disabled = true;
  } else {
    document.styleSheets[4].disabled = false;
    $("#splash-image").attr("src", "img/splash-landscape.png");
    //*** Set the drawing offset.
    MedSelect.Scribble.dimensions.xOffset = 128;
  }
  
  //*** If the user refreshed, only redirect to the first screen
  if (location.href.indexOf('#') > 0) {
    location.href="/";
  }

  MedSelect.Scribble.canvas = jQuery("#sketchpad");
  MedSelect.Scribble.context = MedSelect.Scribble.canvas[0].getContext('2d');

  // prevent elastic scrolling
  document.body.addEventListener('touchmove',function(event){
      event.preventDefault();
  },false);	// end body.onTouchMove

  $('.error').hide();
  /* attach a submit handler to the form */
  $('#loginForm').submit(MedSelect.Scribble.doLogin);

  //***Settings Screen Events
  $("#notify-email").on("change", MedSelect.Scribble.userEmailSettingChange);

  $("#notify-txt-message").on("change", MedSelect.Scribble.userTextSettingChange);

  $("#notify-unsubscribe").on("change", MedSelect.Scribble.userAddFooter);
});
//*** End page document ready

//$(document).bind('mobileinit', function(){
//    $.mobile.metaViewportContent = 'width=device-width';
//});

jQuery(window).bind('orientationchange', function(e){
    MedSelect.Scribble.getDimensions();
    if ($.event.special.orientationchange.orientation() == "landscape") {
      //alert("landscape");
      $("#splash-image").attr("src", "img/splash-landscape.png");
      document.styleSheets[4].disabled = false;
      MedSelect.Scribble.dimensions.xOffset = 128;

    } else if ($.event.special.orientationchange.orientation() == "portrait") {
      //alert("portrait");
      $("#splash-image").attr("src", "img/splash-portrait.png");
      document.styleSheets[4].disabled = true;
      MedSelect.Scribble.dimensions.xOffset = 0;
      // Change to portrait messes up slider on canvas page.  Redraw it.
      //*** Remove the progress bar and create it again
      if (MedSelect.Scribble.sizeArray > 0) {
        $("#image-slider").progressbar({min: 0, max: MedSelect.Scribble.sizeArray - 1, value:MedSelect.Scribble.currentDrawImage});
      }
    }
});

//*** Transition to page 1: Splash
$("#one").live('pageshow', MedSelect.Scribble.showPageSplash);

//*** Transition to page 2: Login
$("#two").live('pageshow', MedSelect.Scribble.showPageLogin);

//*** Transition to page 3: Image Carousel
$("#three").live('pageshow', MedSelect.Scribble.showPageCarousel);

//*** Transition to page 4: Canvas
$("#four").live('pageshow', MedSelect.Scribble.showPageCanvas);

//***Transition to Page 5: Settings
$("#five").live('pageshow', MedSelect.Scribble.showPageSettings);

//***Transition to Page 6: Registration
$("#six").live('pageshow', MedSelect.Scribble.showPageRegistration);

//***Transition to Page 7: Info
$("#seven").live('pageshow', MedSelect.Scribble.showPageInfo);

//***Drawing Screen Events
jQuery("#menu-button").live('vmousedown', MedSelect.Scribble.drawMenuOn);

jQuery("#menu-active-button").live('vmousedown', MedSelect.Scribble.drawMenuOff);

// Color chooser.
jQuery(".ColorBlotch").live('vmousedown', MedSelect.Scribble.chooseColor);

// Line width chooser.
jQuery(".lineWidth").live('vmousedown', MedSelect.Scribble.chooseLineWidth);

// Back to ImageFlow.
jQuery(".prev").live('vmousedown', MedSelect.Scribble.backToImages);

// Erase drawing.
jQuery("#del").live('vmousedown', MedSelect.Scribble.clearDrawing);

// Activate eraser.
jQuery("#eraser").live('vmousedown', MedSelect.Scribble.activateEraser);
