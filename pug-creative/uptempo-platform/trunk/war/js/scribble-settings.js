//***Scribble Object Declaration
var MedSelect = {};
MedSelect.Scribble = {};
MedSelect.Scribble.canvas = {};
MedSelect.Scribble.context = {};
MedSelect.Scribble.imageFlows = new Array(
  {div: "brImageFlow",
   dropdown: "Rheumatoid Arthritis",
   instance: null,
   html: [
     //'<img src="img/libraries/Rheumatology/image1.jpg" longdesc="image1" />',
     '<img src="img/libraries/Rheumatology/image2.jpg" longdesc="image2" />',
     '<img src="img/libraries/Rheumatology/image3.jpg" longdesc="image3" />',
     '<img src="img/libraries/Rheumatology/image4.jpg" longdesc="image4" />',
     '<img src="img/libraries/Rheumatology/image5.jpg" longdesc="image5" />',
     //'<img src="img/libraries/Rheumatology/image6.jpg" longdesc="image6" />',
     '<img src="img/libraries/Rheumatology/image7.jpg" longdesc="image7" />',
     '<img src="img/libraries/Rheumatology/image8.jpg" longdesc="image8" />',
     '<img src="img/libraries/Rheumatology/image9.jpg" longdesc="image9" />',
     '<img src="img/libraries/Rheumatology/image10.jpg" longdesc="image10" />',
     '<img src="img/libraries/Rheumatology/image11.jpg" longdesc="image11" />'
     //'<img src="img/libraries/Rheumatology/image12.jpg" longdesc="image12" />'
   ]
  }
);

MedSelect.Scribble.currentDrawImage = 0;
MedSelect.Scribble.currentPage = 1;
MedSelect.Scribble.dimensions = {};
MedSelect.Scribble.dimensions.yOffset = 50;
MedSelect.Scribble.dimensions.xOffset = 0;
MedSelect.Scribble.drawOn = false;
MedSelect.Scribble.arrayImage = new Array();
MedSelect.Scribble.statusSlider = null;
MedSelect.Scribble.sketchPadSwipe = false;
MedSelect.Scribble.color = null;
MedSelect.Scribble.width = 0;
MedSelect.Scribble.cookieExpiration = 365;
MedSelect.Scribble.cookieSSL = false;
MedSelect.Scribble.serverDrawSend = false;
MedSelect.Scribble.sizeArray = 0;
MedSelect.Scribble.userFields =
    ["title", "firstname", "lastname",
     "email", "address1", "address2",
     "city", "state", "cell",
     "notifyemail", "notifytext", "addfooter"];