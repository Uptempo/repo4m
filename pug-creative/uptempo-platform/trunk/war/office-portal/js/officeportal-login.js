$(document).ready(function(){
$("#login-error-message").html("");				
	$('form[name=officeportal-login]').submit(function(e) {
		e.preventDefault();
		var username = $('#username').val();
		var password = $('#password').val();
		//var officeKey = $('#officeKey').val();
		var officeKey = "";
		var officeGroupKey = "";
	    var formData = "email="+username+"&password="+password;
	    $.ajax({
	        type: 'POST',
	        url: '/service/userauth',
	        data: formData,
	        success: function (response) {	
					if(response.status=="SUCCESS"){
						uptempo.globals.loginKey = response.data.loginKey;
						officeKey = response.data.officeKey;
						officeGroupKey = response.data.officeGroupKey;
						if(typeof officeKey !== "undefined"){
							var authData = "userAuthKey="+uptempo.globals.loginKey+"&officeKey="+officeKey+"&username="+username;	
						} else if(typeof officeGroupKey !== "undefined"){
							var authData = "userAuthKey="+uptempo.globals.loginKey+"&officeGroupKey="+officeGroupKey+"&username="+username;	
						}
						window.location = "/officeportal?"+authData;
					} else {
						console.log(response)
						$("#login-error-message").html("Incorrect username or password");
					}
	        }, 
	        error: function (response) {
				console.log("Error");
				$("#login-error-message").html("Sorry, there was some problem with the login");				
	        } 
	    });
	});	
});
