uptempo.doctorImageUpload = {};

uptempo.doctorImageUpload.init = function() {
	// fix to style input file element in Jquery Mobile 1.2.0
	// TODO: remove for JQuery Mobile 1.3.0
	$('input[type="file"]').textinput({theme: 'b'});
}

$("#doctor-image-upload").live('pageinit', uptempo.doctorImageUpload.init);