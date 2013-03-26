uptempo.configImportData = {};

uptempo.configImportData.init = function() {
	// fix to style input file element in Jquery Mobile 1.2.0
	// TODO: remove for JQuery Mobile 1.3.0
	$('input[type="file"]').textinput({theme: 'b'});

	$("#config-import-data-form").submit(function() {
		if ($('input[type="file"]').val() === "") {
			$("#config-import-data-form-msg").html('<span class="form-errors">Error! Please choose file to import.</span>');
			return false;
		}		
		return true;
	});
}

$("#config-import-data").live('pageinit', uptempo.configImportData.init);