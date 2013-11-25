$(document).ready(function(){
$("#login-error-message").html("");	
$("#user-menu").hide();			
	$('form[name=officeportal-login]').submit(function(e) {
		e.preventDefault();
		var username = $('#username').val();
		var password = $('#password').val();
		//var officeKey = $('#officeKey').val();
		var officeKey = "";
		var officeGroupKey = "";
	    var formData = "email="+escape(username)+"&password="+escape(password);
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
						$("#login-error-message").html(response.message);
					}
	        }, 
	        error: function (response) {
				console.log("Error");
				$("#login-error-message").html("Sorry, there was some problem with the login");				
	        } 
	    });
	});	
});
