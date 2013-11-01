$(document).ready(function(){
	$('form[name=officeportal-login]').submit(function(e) {
		e.preventDefault();
		var username = $('#username').val();
		var password = $('#password').val();
		var officeKey = $('#officeKey').val();
	    var formData = "email="+username+"&password="+password;
	    $.ajax({
	        type: 'POST',
	        url: '/service/userauth',
	        data: formData,
	        success: function (response) {
				console.log("chiamata riuscita");

					if(response.status=="SUCCESS"){
						uptempo.globals.loginKey = response.data.loginKey;
						var authData = "userAuthKey="+uptempo.globals.loginKey+"&officeKey="+officeKey+"&username="+username;
						window.location = "/officeportal?"+authData;

					} else {
						console.log(response)
					}
	        }, 
	        error: function (response) {
				console.log("ERRRRRRORE");
				
	        } 
	    });
	});	
});
