uptempo.staticListsImportData = {};

uptempo.staticListsImportData.init = function() {
	// fix to style input file element in Jquery Mobile 1.2.0
	// TODO: remove for JQuery Mobile 1.3.0
	$('input[type="file"]').textinput({theme: 'b'});

	$("#staticlists-import-data-form").submit(function() {
		if ($('input[type="file"]').val() === "") {
			$("#staticlists-import-data-form-msg").html('<span class="form-errors">Error! Please choose file to import.</span>');
			return false;
		}		
		return true;
	});
}

$("#staticlists-import-data").live('pageinit', uptempo.staticListsImportData.init);