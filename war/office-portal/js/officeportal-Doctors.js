uptempo.officePortal.doctors = {};
uptempo.officePortal.doctors.staticLists = {};
uptempo.officePortal.doctors.getStaticLists = function(){
	uptempo.officePortal.doctors.staticLists.specialties = [];
	uptempo.officePortal.doctors.staticLists.titles = [];
	
	$.ajax({
        type: 'GET',
        url: '/service/staticlist',
        success: function (response) {
            if (response.status == "SUCCESS") {
                if (typeof response.data !== 'undefined') {
                	for(var list in response.data.values){
						if(response.data.values[list].listKey == "SPECIALTIES"){
							uptempo.officePortal.doctors.staticLists.specialties = response.data.values[list].listValue;		
						} else if(response.data.values[list].listKey == "TITLES"){
							uptempo.officePortal.doctors.staticLists.titles = response.data.values[list].listValue;		
						}
	                	
                	}
                } else {
                    uptempo.officePortal.util.alert('No SPECIALTIES defined', 'Error');
                }

            } else {
				uptempo.officePortal.util.alert('No SPECIALTIES defined', 'Error');
            }

        }
    });
}

uptempo.officePortal.doctors.createOfficesList = function (groupKey) {
//Populate the static lists
uptempo.officePortal.doctors.getStaticLists();
	    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/billingoffice',
        data: 'officeGroup=' + groupKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if (typeof response.data !== 'undefined') {
                    $("#docs-offices-list").empty();
                    for (var office in response.data.values) {
                        $("#docs-offices-list").append('<li data-id="' + response.data.values[office]['key'] + '" class=""><a href="#" onclick="javascript:uptempo.officePortal.doctors.getDoctorsList(\'' + response.data.values[office]['key'] + '\');">Office ' + response.data.values[office]['officeName'] + '</a></li>');
                    }
                    $("#docs-offices-list").find('a').each(function () {
                        if ($(this).parent().data('id') === response.data.values[0]['key']) {
                            $(this).parent().addClass('active');
                            uptempo.officePortal.doctors.getDoctorsList(response.data.values[0]['key']);
                        }
                    });

                } else {
                    uptempo.officePortal.util.alert('There are no offices for the group required');
                }

            } else {

            }

        }
    });
}

uptempo.officePortal.doctors.getDoctorsList = function (officeKey) {
	uptempo.officePortal.doctors.getStaticLists();
    uptempo.officePortal.doctors.officeKey = officeKey;
    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/doctor',
        data: 'billingOffice=' + officeKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if ((typeof response.data.values !== 'undefined') && (response.data.values.length > 0)) {
                    $("#docs-doctors-list").empty();

                    for (var doc in response.data.values) {
                    	var docFullName = response.data.values[doc]['title'] + ' ' + response.data.values[doc]['firstName'] + ' ' + response.data.values[doc]['lastName'];
                        $("#docs-doctors-list").append('<li data-id="'+response.data.values[doc]['key'] + '"><a href="#" class="user-name btn-primary-gray"  onclick="javascript:uptempo.officePortal.doctors.getDoctorDetails(\''+response.data.values[doc]['key']+'\');">' + docFullName + '</a><a class="delete-button" href="#" onclick="javascript:uptempo.officePortal.doctors.deleteDoctor(\''+response.data.values[doc]['key']+'\', \'' + docFullName + '\');">X</a></li>');
                    }

                     uptempo.officePortal.doctors.getDoctorDetails(response.data.values[0]['key']) 
                    $("#docs-offices-list").find('li').removeClass('active');
                    $("#docs-offices-list").find('a').each(function () {
                        if ($(this).parent().data('id') === officeKey) {
                            $(this).parent().addClass('active');
                        }
                    });

                } else {
                    uptempo.officePortal.util.alert('There are no doctors for the office required');
                    $("#docs-doctors-list").empty();
                    uptempo.officePortal.doctors.clearDocDetails();
                    $("#docs-offices-list").find('li').removeClass('active');
                    $("#docs-offices-list").find('a').each(function () {
                        if ($(this).parent().data('id') === officeKey) {
                            $(this).parent().addClass('active');
                        }
                    });

                }

            } else {

            }

        }
    });
}

uptempo.officePortal.doctors.getDoctorDetails = function(doctorKey){
uptempo.officePortal.doctors.makePageNotEditable("#doctors-page", "#save-doctor-changes");
	uptempo.officePortal.doctors.doctorKey = doctorKey;
	var titles = '';
	var specialties = '';
	    $.ajax({
        type: 'GET',
        url: '/service/doctor/'+doctorKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if (typeof response.data !== 'undefined') {

	                if ((typeof response.data['title'] !== 'undefined')&&(response.data['title'].length>0)){
		               for (var s in response.data['title']){
			           		titles += response.data['title'][s] + ",";
		               }
		               titles= titles.substr(0, titles.length- 1);
	               
					}

	                if ((typeof response.data['specialty'] !== 'undefined')&&(response.data['specialty'].length>0)){
		               for (var s in response.data['specialty']){
			           		specialties += response.data['specialty'][s] + ",";    
		               }
					   specialties= specialties.substr(0, specialties.length- 1);
					}
					var docFullName = titles + " " + response.data['firstName'] + " "+ response.data['lastName'];

                	$("#doctor-fullName").html(docFullName);
                	$("#doctor-titles").html(titles);
                	$("#doctor-specialties").html(specialties);
					for (var i in uptempo.doctor.validFields) {
                        $(uptempo.doctor.validFields[i].inputId).html(response.data[uptempo.doctor.validFields[i].formVal]);
                    }					
                } else {
                    uptempo.officePortal.util.alert('There are no doctors for this office');
                }


            } else {

            }

        }
    });
    uptempo.officePortal.doctors.Photo(uptempo.officePortal.doctors.doctorKey);
}

uptempo.officePortal.doctors.deleteDoctor = function(doctorKey, docFullName){
    $("#delete-doc-name").html(docFullName);
	$('#modal-doc-delete').modal('show');
	$("#delete-doc-confirmed").click(function () {
        
//*** Get the data from the server.
        $.ajax({
            type: 'DELETE',
            url: '/service/doctor/' + doctorKey,
            success: function (response) {
                if (response.status == "SUCCESS") {
                    uptempo.officePortal.util.alert('Doctor Deleted', 'Success');
                    uptempo.officePortal.doctors.getDoctorsList(uptempo.officePortal.doctors.officeKey);
                } else {
                    uptempo.officePortal.util.alert('Some problem occured while deleting this doctor');
                }
            },
            error: function (e) {
                uptempo.appointments.util.alert(e);
            }
        });

    });
}

uptempo.officePortal.doctors.clearDocDetails = function(){
		for (var i in uptempo.doctor.validFields) {
            $(uptempo.doctor.validFields[i].inputId).html("");
        }					
      	$("#doctor-fullName").html("Full Name Goes Here");
       	$("#doctor-titles").html("");
     	$("#doctor-specialties").html("");
     	$("#doctor-image").attr("src","");

}

uptempo.officePortal.doctors.makePageEditable = function (pageSelector, buttonSelector) {
    $(pageSelector).find('.editable').each(function () {
    if($(this).attr('id')=="doctor-titles") {
    	var currentTitles = $(this).html();
    	var titlesArray = currentTitles.split(",");
	   	var titles = '';
	   	for (var i in uptempo.officePortal.doctors.staticLists.titles){
		   	var checked = '';
	    	if(titlesArray.indexOf(uptempo.officePortal.doctors.staticLists.titles[i])>-1)  { 
		   		checked = 'checked'; 
		   	}
			    titles += '<input type="checkbox" name="doctor-titles" class="checkboxes doctor-titles" value="'+uptempo.officePortal.doctors.staticLists.titles[i]+'" '+checked+'/><label for="'+uptempo.officePortal.doctors.staticLists.titles[i]+'" class="doctor-titles-label">'+uptempo.officePortal.doctors.staticLists.titles[i]+'</label>';
		    }
	    $(this).html(titles);
 
    } else if($(this).attr('id')=="doctor-specialties"){
    	var currentSpecs = $(this).html();
    	console.log("CurrentSpecs" +currentSpecs);
    	var specsArray = currentSpecs.split(",");
    	console.log("specsArray" +specsArray);
	   	var specialties = '';
	    for (var i in uptempo.officePortal.doctors.staticLists.specialties){
	    	var checked = '';
	    	if(specsArray.indexOf(uptempo.officePortal.doctors.staticLists.specialties[i])>-1)  { 
	    		checked = 'checked'; 
	    		}
		    specialties += '<input type="checkbox" name="doctor-specialties" class="checkboxes doctor-specialties" value="'+uptempo.officePortal.doctors.staticLists.specialties[i]+'" '+checked+'/><label for="'+uptempo.officePortal.doctors.staticLists.specialties[i]+'" class="doctor-specialties-label">'+uptempo.officePortal.doctors.staticLists.specialties[i]+'</label>';
	    }
	    $(this).html(specialties);
    } else {
	    $(this).html('<input type="text"  name="' + $(this).attr('id') + '" id="' + $(this).attr('id') + '" value="' + $(this).html() + '"/>');
	    $(this).removeAttr('id');
    }
        
    });

    $(buttonSelector).html('Save Changes');
    $(buttonSelector).attr('onclick', 'javascript:uptempo.officePortal.doctors.submitUpdate("'+uptempo.officePortal.doctors.doctorKey+'");');
    $(buttonSelector).attr('id', 'save-doctor-changes');

}

uptempo.officePortal.doctors.makePageNotEditable = function (pageSelector, buttonSelector) {
    $(pageSelector).find('.editable').each(function () {
        var id = $(this).children('input').attr('id');
        var value = $(this).children('input').val();
        $(this).empty();
        $(this).attr('id', id);
        $(this).html(value);
    });

    $(buttonSelector).html('Edit Profile');
    $(buttonSelector).attr('onclick', 'uptempo.officePortal.doctors.makePageEditable("#doctors-page", $(this))');
    $(buttonSelector).attr('id', 'edit-doctor-changes');
}

uptempo.officePortal.doctors.submitUpdate = function(doctorKey){
    var validationResult = uptempo.ajax.validateInput(uptempo.doctor.validFields)

    if (validationResult.isValid) {
        var formData = uptempo.ajax.consructPostString(uptempo.doctor.validFields);
        //*** Submit the XHR request
        formData += "&billingOffice="+uptempo.officePortal.doctors.officeKey;
        
		var titles = '';
		var i = 1;
	      $("#doctor-titles").find('input[type="checkbox"]').each(function() {
		    if($(this).is(':checked')) {
		    	titles += "&title"+i+"="+$(this).val();
		    	i++;
		    }
		  });
		
		var specialties = '';
		var i = 1;
	      $("#doctor-specialties").find('input[type="checkbox"]').each(function() {
		    if($(this).is(':checked')) {
		    	specialties += "&specialty"+i+"="+$(this).val();
		    	i++;
		    }
		  });  
		
/*
		var photo = $("#doctor-image").attr("src");
		console.log("photo" + photo);
		if(photo !== "img/profile-pic.jpg"){
			formData+="&photo="+photo;
		}
*/
		  
        formData += titles+specialties+"&clearTitles=true&clearSpecialities=true";
        console.log(formData)
        //gestire title e specialties    
        $.ajax({
            type: 'PUT',
            url: "/service/doctor/" + doctorKey,
            data: formData,
            success: function (response) {
                //*** If the response was sucessful, show the success indicator.
                if (response.status === "SUCCESS") {
/*                     uptempo.officePortal.doctors.makePageNotEditable("#doctors-page", "#save-doctor-changes"); */
                    uptempo.officePortal.doctors.getDoctorDetails(doctorKey);
                } else {
                    uptempo.officePortal.util.alert(response.message, "Error");
                }
            }, 
            error: function(e){
                uptempo.officePortal.util.alert("Sorry, something went wrong");
            }
        });
        
    } else {
        var message = "";
        if (validationResult.errorMessage != "") {
            message = validationResult.errorMessage;
			uptempo.officePortal.util.alert(message, 'Error')
        }        
    }

	
}

uptempo.officePortal.doctors.makeNewForm = function (pageSelector, buttonSelector) {
	$("#doctor-fullName").html("New Doctor");
	$(pageSelector).find('.editable').each(function () {
    if($(this).attr('id')=="doctor-titles") {
    var titles = '';
	   	for (var i in uptempo.officePortal.doctors.staticLists.titles){
		   	var checked = '';
			    titles += '<input type="checkbox" name="doctor-titles" class="checkboxes doctor-titles" value="'+uptempo.officePortal.doctors.staticLists.titles[i]+'" /><label for="'+uptempo.officePortal.doctors.staticLists.titles[i]+'" class="doctor-titles-label">'+uptempo.officePortal.doctors.staticLists.titles[i]+'</label>';
		    }
	    $(this).html(titles);
 
    } else if($(this).attr('id')=="doctor-specialties"){
	   	var specialties = '';
	    for (var i in uptempo.officePortal.doctors.staticLists.specialties){
		    specialties += '<input type="checkbox" name="doctor-specialties" class="checkboxes doctor-specialties" value="'+uptempo.officePortal.doctors.staticLists.specialties[i]+'" /><label for="'+uptempo.officePortal.doctors.staticLists.specialties[i]+'" class="doctor-specialties-label">'+uptempo.officePortal.doctors.staticLists.specialties[i]+'</label>';
	    }
	    $(this).html(specialties);
    } else {
	    $(this).html('<input type="text"  name="' + $(this).attr('id') + '" id="' + $(this).attr('id') + '" value=""/>');
	    $(this).removeAttr('id');
    }
       	$("#doctor-image").attr("src","/office-portal/img/profile-pic.jpg");
    });

    $(buttonSelector).html('Save Doctor');
    $(buttonSelector).attr('onclick', 'javascript:uptempo.officePortal.doctors.saveNew();');
    $(buttonSelector).attr('id', 'save-doctor-changes');

}

uptempo.officePortal.doctors.saveNew = function(){
    var validationResult = uptempo.ajax.validateInput(uptempo.doctor.validFields)

    if (validationResult.isValid) {
        var formData = uptempo.ajax.consructPostString(uptempo.doctor.validFields);
        //*** Submit the XHR request
        formData += "&billingOffice="+uptempo.officePortal.doctors.officeKey;
        
		var titles = '';
		var i = 1;
	      $("#doctor-titles").find('input[type="checkbox"]').each(function() {
		    if($(this).is(':checked')) {
		    	titles += "&title"+i+"="+$(this).val();
		    	i++;
		    }
		  });
		
		var specialties = '';
		var i = 1;
	      $("#doctor-specialties").find('input[type="checkbox"]').each(function() {
		    if($(this).is(':checked')) {
		    	specialties += "&specialty"+i+"="+$(this).val();
		    	i++;
		    }
		  });  
		  
        formData += titles+specialties;
        $.ajax({
            type: 'POST',
            url: "/service/doctor",
            data: formData,
            success: function (response) {
                //*** If the response was sucessful, show the success indicator.
                if (response.status === "SUCCESS") {
                    uptempo.officePortal.doctors.makePageNotEditable("#doctors-page", "#save-doctor-changes");
                    uptempo.officePortal.doctors.getDoctorsList(uptempo.officePortal.doctors.officeKey);
                } else {
                    uptempo.officePortal.util.alert(response.message, "Error");
                }
            }, 
            error: function(e){
                uptempo.officePortal.util.alert("Sorry, something went wrong");
            }
        });
        
    } else {
        var message = "";
        if (validationResult.errorMessage != "") {
            message = validationResult.errorMessage;
			uptempo.officePortal.util.alert(message, 'Error')
        }        
    }
	
}

uptempo.officePortal.doctors.Photo = function(doctorKey) {
  //*** Submit the XHR request.
  $.ajax({
    type: 'GET',
    url: '/service/doctor/' + doctorKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var oldImage = "";
        if (typeof response.data.photo !== 'undefined') {
	        $("#doctor-image").attr("src", 
              "/serve-doctor-image?blob-key=" + response.data.photo);
        } else {
	        $("#doctor-image").attr("src", "/office-portal/img/profile-pic.jpg");
        }
        
      } else {
        uptempo.officePortal.util.alert("Sorry, something went wrong getting the doctor's image");
      }
    }
  });
}

uptempo.officePortal.doctors.uploadPicForm = function(){
  //*** Submit the XHR request.
  $.ajax({
    type: 'GET',
    url: '/service/doctor/' + uptempo.officePortal.doctors.doctorKey,
    success: function(response) {
      //*** If the response was sucessful, save the user info in cookies.
      if (response.status == "SUCCESS") {
        var oldImage = "";
        if (response.data.photo !== undefined) {
            oldImage = "&img=" +  response.data.photo;     
        }
        $("#doctor-image-upload iframe").attr("src", 
              "/office-portal/include/doctor-image-upload.jsp?source=office-portal&doc=" + uptempo.officePortal.doctors.doctorKey + oldImage);
      } else {
        alert(response.message);
      }
    }
  });
  
  $('#modal-doc-photo').modal('show');
  
  $('#modal-doc-photo').on('hidden', function () {
    uptempo.officePortal.doctors.getDoctorDetails(uptempo.officePortal.doctors.doctorKey);
  });
}

uptempo.officePortal.doctors.readImage = function (input) {

	if (typeof(window.FileReader) == 'undefined') 
	    {
	        uptempo.officePortal.util.alert('Browser does not support HTML5 file uploads!','Error');
	    } else {
			var image64 = "/office-portal/img/profile-pic.jpg";
		    if ( input.files && input.files[0] ) {
		        var FR= new FileReader();
		
		        FR.onload = function(e) {
					image64 = e.target.result;
					$("#save-doc-photo").on("click", function(){
						$("#doctor-image").attr("src", image64);
					});
					$("#save-doc-photo").removeAttr("disabled");
		        };       
		
		        FR.readAsDataURL( input.files[0] );
		    }		    
	    }

}
