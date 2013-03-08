//***Scribble Functions
MedSelect.Scribble.getDimensions = function() {
  MedSelect.Scribble.dimensions.x = $(window).width();
  MedSelect.Scribble.dimensions.y = $(window).height();
};

MedSelect.Scribble.changeImageFlow = function() {
  //*** Get value of dropdown
  var imageFlowVal = $("#select-choice-1").val();

  for (item in MedSelect.Scribble.imageFlows) {
    if (imageFlowVal == MedSelect.Scribble.imageFlows[item].dropdown) {
      //***Try to loop through all of the other image flows and turn them off first here
      var itemId = MedSelect.Scribble.imageFlows[item].div;
      $("#" + itemId).css("display", "block");
      $("#" + itemId).empty();
      MedSelect.Scribble.constructImg(itemId, MedSelect.Scribble.imageFlows[item].html)
      MedSelect.Scribble.imageFlows[item].instance = new ImageFlow();
      
      MedSelect.Scribble.imageFlows[item].instance.init({ImageFlowID:itemId});
    }
    else {
      //*** If this imageflow is not active, clear it
      $("#" + MedSelect.Scribble.imageFlows[item].div).css("display", "none");
      MedSelect.Scribble.imageFlows[item].instance = null;
    }
  }
};

//*** Helper function to null out the image flow fo the current group
MedSelect.Scribble.removeCurrentFlow = function() {
  for (item in MedSelect.Scribble.imageFlows) {
    if (MedSelect.Scribble.imageFlows[item].instance != null) {
      $("#" + MedSelect.Scribble.imageFlows[item].div).empty();
      MedSelect.Scribble.imageFlows[item].instance = null;
    }
  }
};

MedSelect.Scribble.constructImg = function(divName, htmlArray) {
  for (text in htmlArray) {
    $("#" + divName).append(htmlArray[text]);
  }
};

MedSelect.Scribble.getImgDivFromSelect = function() {
  var imageFlowVal = $("#select-choice-1").val();
  for (item in MedSelect.Scribble.imageFlows) {
    if (imageFlowVal == MedSelect.Scribble.imageFlows[item].dropdown) {
      return MedSelect.Scribble.imageFlows[item].div;
    }
  }
};

// create a drawing manager containing the draw functions.
MedSelect.Scribble.drawingManager = {
  isDrawing: false,
  vmousedown: function(coors){
    MedSelect.Scribble.context.beginPath();
    MedSelect.Scribble.context.moveTo(coors.x, coors.y);
    this.isDrawing = true;
  },
  vmousemove: function(coors){
    if (this.isDrawing) {
      MedSelect.Scribble.context.lineTo(coors.x, coors.y);
      MedSelect.Scribble.context.stroke();
    }
  },
  vmouseup: function(coors){
    if (this.isDrawing) {
      coors.y = coors.y;
      this.isDrawing = false;
    }
  }
};
    
// create a function to pass touch events and coordinates to drawer
function draw(event){
  // get canvas position
  var coors = {
    x: event.pageX - MedSelect.Scribble.dimensions.xOffset,
    y: event.pageY - MedSelect.Scribble.dimensions.yOffset

  };

  //$('#draw-coords').html("&nbsp;&nbsp;x:" + coors.x + ", y:" + coors.y + "&nbsp;");
  // pass the coordinates to the appropriate handler
  MedSelect.Scribble.drawingManager[event.type](coors);
}
	
// clear canvas
MedSelect.Scribble.clearCanvasWidth = function(){
  var s = document.getElementById("sketchpad");
  var w = s.width;
  var h = s.height;
  MedSelect.Scribble.context.clearRect(0, 0, w, h);
}

//*** Upload the canvas contents to a server side script to save and e-mail
//*** the file
MedSelect.Scribble.uploadCanvas = function() {
  var canvas = document.getElementById("sketchpad");
  var canvasDataURI = encodeURIComponent(canvas.toDataURL("image/png"));
  var user = MedSelect.Scribble.getCookie("medSelectEmail");
  var title = MedSelect.Scribble.getCookie("medSelectTitle");
  var fname = MedSelect.Scribble.getCookie("medSelectFName");
  var lname = MedSelect.Scribble.getCookie("medSelectLName");
  var emailTo = $('#draw-email').val();
  var drawTitle = "Drawing by user " + user;
  if (!emailTo && MedSelect.Scribble.serverDrawSend) {
    alert("You must enter an e-mail recipient!");
    $("#draw-popup").popup("close");
    return false;   
  } else {
    $.ajax({
      type: 'POST',
      url: '/service/drawing',
      data: 'user=' + user +
        '&emailto=' + emailTo +
        '&description=' + drawTitle +
        '&bg=' + MedSelect.Scribble.arrayImage[MedSelect.Scribble.currentDrawImage] +
        '&img=' + canvasDataURI,
      success: function(response) {
        var imageURL = response.data.url;
        var emailSubject = "A Rheumatology image was prepared for you!";
        if (title != "" && fname != "" && lname != "") {
          emailSubject = title + " " + fname + " " + lname + " has prepared a Rheumatology image for you!";
        }
        var privacyPolicy = "<b>Dear Patient, </b><br /><br />" +
        "<b>Your doctor has sent you this information in connection with matters " +
        "discussed during your recent visit. " +
        "Please note that while Amgen and Pfizer have made this material " +
        "available to your doctor for use with patients, neither Amgen " +
        "nor Pfizer receive any information regarding the content shared " +
        "with individual patients.  If you no longer wish to receive " +
        "this information from your doctor, please notify him/her directly.</b><br /><br />";

        var link = "mailto:" + emailTo +
                   "?subject=" + emailSubject +
                   "&body=<b>Here's the image I went over with you." +
                   " If you have any questions please give me a call.</b><br />" +
                   "<img style='display:block;width:800px;height:500px' title='Patient Education Image' alt='Patient Education Image' src='" + imageURL + "' />" +
                   "<br />" + privacyPolicy +
                   "<a href='http://www.enbrel.com/request-information.jspx'>" +
                   "<img src='http://medselect-dev.appspot.com/img/email-footer-image.png' />" +
                   "</a>";
        window.location.href = link;
      }
    });
    $("#draw-popup").popup("close");
    return false;
  }
}

MedSelect.Scribble.toggleSketchPadSwipe = function() {
  //*** If the sketchpad swipe is on, turn it off and vice-versa
  if (MedSelect.Scribble.sketchPadSwipe) {
    //*** Turn swipe events off when drawing
    $("#sketchpad").off('swiperight');
    $("#sketchpad").off('swipeleft');
    MedSelect.Scribble.sketchPadSwipe = false;
  }
  else {
    $("#sketchpad").on('swipeleft',function(){
      if (!MedSelect.Scribble.drawOn) {
        if (MedSelect.Scribble.currentDrawImage < MedSelect.Scribble.arrayImage.length - 1){
          MedSelect.Scribble.currentDrawImage++;
          $("#image-slider").progressbar("option", "value", MedSelect.Scribble.currentDrawImage);
          $("#drawable-image").html('<img src="'+MedSelect.Scribble.arrayImage[MedSelect.Scribble.currentDrawImage] +'" width="768px" id="image-dra">');
          MedSelect.Scribble.clearCanvasWidth();
        }
      }
    });
    $("#sketchpad").on('swiperight',function(){
      if (!MedSelect.Scribble.drawOn) {
        if(MedSelect.Scribble.currentDrawImage > 0){
          MedSelect.Scribble.currentDrawImage--;
          $("#image-slider").progressbar("option", "value", MedSelect.Scribble.currentDrawImage);
          $("#drawable-image").html('<img src="'+MedSelect.Scribble.arrayImage[MedSelect.Scribble.currentDrawImage] +'" width="768px" id="image-dra">');
          MedSelect.Scribble.clearCanvasWidth();
        }
      }
    });
    MedSelect.Scribble.sketchPadSwipe = true;
  }
}

MedSelect.Scribble.clearDrawing = function() {
  MedSelect.Scribble.clearCanvasWidth();
  MedSelect.Scribble.context.strokeStyle = MedSelect.Scribble.color;
  MedSelect.Scribble.context.lineWidth = MedSelect.Scribble.width;
};

MedSelect.Scribble.activateEraser = function() {
  MedSelect.Scribble.context.globalCompositeOperation = 'destination-out';
  MedSelect.Scribble.context.lineWidth = "15";
};