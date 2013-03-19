uptempo.doctorImageUpload = {};

uptempo.doctorImageUpload.init = function() {
	// fix to style input file element in Jquery Mobile 1.2.0
	// TODO: remove for JQuery Mobile 1.3.0
	$('input[type="file"]').textinput({theme: 'b'});

	$("#doctor-image-upload-form").submit(function() {
		if ($('input[type="file"]').val() === "") {
			$("#doctor-image-upload-form-msg").html('<span class="form-errors">Error! Please choose file to upload.</span>');
			return false;
		}		
		return true;
	});
}

$("#doctor-image-upload").live('pageinit', uptempo.doctorImageUpload.init);