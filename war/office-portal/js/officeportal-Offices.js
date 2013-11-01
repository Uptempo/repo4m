uptempo.officePortal.billingOffices = {};

uptempo.officePortal.billingOffices.getBillingOfficesInfo = function (groupKey) {
    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/billingoffice',
        data: 'officeGroup='+groupKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if(typeof response.data !== 'undefined'){
                    $("#offices-list").empty();
                    for (var office in response.data.values){
                        $("#offices-list").append('<li data-id="'+response.data.values[office]['key']+'" class=""><a href="#" onclick="javascript:uptempo.officePortal.billingOffices.getBillingOfficeInfo(\''+response.data.values[office]['key']+'\');">Office '+response.data.values[office]['officeName']+'</a></li>');
                    }
                    $("#edit-profile").data('office-key', response.data.values[0]['key']);
                    for (var i in uptempo.billingOffices.validFields) {
                            $(uptempo.billingOffices.validFields[i].inputId).html(response.data.values[0][uptempo.billingOffices.validFields[i].formVal]);
                    }
                    $("#offices-list").find('a').each(function(){
                        if ($(this).parent().data('id')===response.data.values[0]['key']){
                            $(this).parent().addClass('active');
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

uptempo.officePortal.billingOffices.getBillingOfficeInfo = function(officeKey){
    
    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/billingoffice/' +officeKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if(typeof response.data !== 'undefined'){
                    $("#edit-profile").data('office-key', response.data['key']);
                    for (var i in uptempo.billingOffices.validFields) {
                        $(uptempo.billingOffices.validFields[i].inputId).html(response.data[uptempo.billingOffices.validFields[i].formVal]);
                    }
                    $("#offices-list").find('li').removeClass('active');
                    $("#offices-list").find('a').each(function(){
                        if ($(this).parent().data('id')===response.data['key']){
                            $(this).parent().addClass('active');
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

uptempo.officePortal.billingOffices.makePageEditable = function (pageSelector, buttonSelector) {
    
    $(pageSelector).find('.editable').each(function () {
        $(this).html('<input type="text"  name="' + $(this).attr('id') + '" id="' + $(this).attr('id') + '" value="' + $(this).html() + '"/>');
        $(this).removeAttr('id');
    });

    $(buttonSelector).html('Save Changes');
    $(buttonSelector).attr('onclick', 'javascript:uptempo.officePortal.billingOffices.submitUpdate("'+$(buttonSelector).data('office-key')+'");');
    $(buttonSelector).attr('id', 'save-changes');

}

uptempo.officePortal.billingOffices.makePageNotEditable = function (pageSelector, buttonSelector) {
    $(pageSelector).find('.editable').each(function () {
        var id = $(this).children('input').attr('id');
        var value = $(this).children('input').val();
        $(this).empty();
        $(this).attr('id', id);
        $(this).html(value);
    });

    $(buttonSelector).html('Edit Profile');
    $(buttonSelector).attr('onclick', 'uptempo.officePortal.billingOffices.makePageEditable("#billingoffice-page", $(this))');
    $(buttonSelector).attr('id', 'edit-profile');
}

uptempo.officePortal.billingOffices.submitUpdate = function (billingOfficeKey) {
    var validationResult = uptempo.ajax.validateInput(uptempo.billingOffices.validFields)

    if (validationResult.isValid) {
        var formData = uptempo.ajax.consructPostString(uptempo.billingOffices.validFields);
        //*** Submit the XHR request
        
        console.log(formData)        
        $.ajax({
            type: 'PUT',
            url: "/service/billingoffice/" + billingOfficeKey,
            data: formData,
            success: function (response) {
                //*** If the response was sucessful, show the success indicator.
                if (response.status === "SUCCESS") {
                    uptempo.officePortal.billingOffices.makePageNotEditable("#billingoffice-page", "#save-changes");
                    console.log('success')
                } else {
                    console.log("Failed to add " +
                        response.message);
                }
            }, 
            error: function(e){
                uptempo.officePortal.util.alert("Sorry, something went wrong");
            }
        });
        //uptempo.officePortal.billingGroup.makePageNotEditable("#billingoffice-page", "#save-changes"); //this should be done only after the ajax call succeed
    } else {
        var message = "";
        if (validationResult.errorMessage != "") {
            message = validationResult.errorMessage;
        uptempo.officePortal.util.alert(message)
        }        
    }
}
