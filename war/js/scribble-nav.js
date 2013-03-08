//*** Scribble General Event Handlers
//*** Depends on: scribble-drawing.js, scribble-user.js

MedSelect.Scribble.showPageSplash = function() {
  $("#one").attr("display", "block");
  $("#button-overlay").attr("display", "block");
  MedSelect.Scribble.currentPage = 1;
};

MedSelect.Scribble.showPageLogin = function() {
  $("#button-overlay").attr("display", "none");
  MedSelect.Scribble.currentPage = 2;
};

MedSelect.Scribble.showPageCarousel = function() {
  var userCookie = MedSelect.Scribble.getCookieUser();
  if (userCookie != null) {
    var greeting = "<h2>Welcome back " + userCookie.email + "</h2>";
    //*** Check if this user is registered with a name.
    if (userCookie.title != "EMPTY" &&
        userCookie.fname != "EMPTY" &&
        userCookie.lname != "EMPTY") {
        greeting = "<h2>Welcome back " + userCookie.title
        + " " + userCookie.fname + " " + userCookie.lname + "</h2>";
    }
  }
  $("#three-greet").html(greeting);
  $("#three").attr("display", "block");

  //*** Make sure the sketchpad is in initialized state
  $("#sketchpad").off('swiperight');
  $("#sketchpad").off('swipeleft');
  MedSelect.Scribble.sketchPadSwipe = false;

  MedSelect.Scribble.changeImageFlow();
  MedSelect.Scribble.currentPage = 3;
};

MedSelect.Scribble.showPageCanvas = function() {
  $("#four").attr("display", "block");

  //*** Get the active selected image category.
  var imageDiv = MedSelect.Scribble.getImgDivFromSelect();

  //*** Only reset the array of images if this transition is from
  //*** the carousel screen or a screen that didn't erase the images.
  if ($("#" + imageDiv + "_images img").length > 0) {
    MedSelect.Scribble.arrayImage = new Array();
    // Check which set of images is being used

    $("#" + imageDiv + "_images img").each(function () {
      MedSelect.Scribble.sizeArray = MedSelect.Scribble.arrayImage.push($(this).attr("src"));
    });

    //*** Remove the current image flow so a transition back to
    //*** page 3 is stateless
    MedSelect.Scribble.removeCurrentFlow();
    
    //*** Toggle the sketchpad on.
    MedSelect.Scribble.toggleSketchPadSwipe();
  }

  //*** Focus on the current img in the carousel
  var nameImage = $("#image-dra").attr("src");
  for(var i = 0;i < MedSelect.Scribble.sizeArray;i++){
    if(MedSelect.Scribble.arrayImage[i] == nameImage){
      MedSelect.Scribble.currentDrawImage = i
    }
  }

  //*** Create the progress bar again.
  $("#image-slider").progressbar({min: 0, max:MedSelect.Scribble.sizeArray - 1, value:MedSelect.Scribble.currentDrawImage});
  MedSelect.Scribble.currentPage = 4;
};

MedSelect.Scribble.showPageSettings = function() {
  $("#five").attr("display", "block");
  MedSelect.Scribble.currentPage = 5;
};

MedSelect.Scribble.showPageRegistration = function() {
  $("#six").attr("display", "block");
  MedSelect.Scribble.currentPage = 6;
};

MedSelect.Scribble.showPageInfo = function() {
  $("#seven").attr("display", "block");
  MedSelect.Scribble.currentPage = 7;
};

//***Send Image Handlers
MedSelect.Scribble.showDrawPopup = function () {
  if (MedSelect.Scribble.serverDrawSend) {
    $("#draw-popup").popup("open");
  } else {
    MedSelect.Scribble.uploadCanvas();
  }
}

//***Drawing Menu Event Handlers
MedSelect.Scribble.drawMenuOn = function() {
  $("#menu-bar").toggle();
  $("#menu-active-button").toggle();
  $("#menu-button").toggle();
  $("#menu-ColorPicker").toggle();
  $("#menu-lineWidth").toggle();
  $("#draw-left-image").toggle();
  $("#draw-right-image").toggle();
  $("#menu-bar-bottom").show();
  $("#menu-settings").show();
  $("#drawing-logo").hide();
  MedSelect.Scribble.canvas.bind('vmousedown vmousemove vmouseup',draw);
  MedSelect.Scribble.drawOn = true;
};

MedSelect.Scribble.drawMenuOff = function() {
  $("#menu-bar").toggle();
  $("#menu-active-button").toggle();
  $("#menu-button").toggle();
  $("#menu-ColorPicker").toggle();
  $("#menu-lineWidth").toggle();
  $("#draw-left-image").toggle();
  $("#draw-right-image").toggle();
  $("#menu-bar-bottom").show();
  $("#menu-settings").show();
  $("#drawing-logo").show();
  MedSelect.Scribble.canvas.unbind('vmousedown vmousemove vmouseup',draw);
  MedSelect.Scribble.drawOn = false;
};

//***Drawing action event handlers.
MedSelect.Scribble.chooseColor = function() {
  MedSelect.Scribble.color = $(this).css("background-color");
  MedSelect.Scribble.context.strokeStyle = MedSelect.Scribble.color;
  $(".ColorBlotch").each(function (i) {
      $(this).css("border", "2px solid #14609D");
  });
  $(this).css("border", "2px solid #F1F5F9");
};

MedSelect.Scribble.chooseLineWidth = function() {
  MedSelect.Scribble.width=$(this).attr('title');
  MedSelect.Scribble.context.lineWidth = $(this).attr('title');
  $(".lineWidth").each(function (i) {
      $(this).css("border", "none");
  });
  $(this).css("border","2px solid #F1F5F9");
  MedSelect.Scribble.context.globalCompositeOperation = 'source-over';
};

MedSelect.Scribble.backToImages = function() {
  MedSelect.Scribble.clearCanvasWidth();
  $("#menu-bar").hide();
  $("#menu-active-button").hide();
  $("#menu-button").show();
  $("#menu-ColorPicker").hide();
  $("#menu-lineWidth").hide();
  $("#draw-left-image").hide();
  $("#draw-right-image").hide();
  MedSelect.Scribble.canvas.unbind('vmousedown vmousemove vmouseup', draw);
  $.mobile.changePage("#three", {transition: "flip"});
};