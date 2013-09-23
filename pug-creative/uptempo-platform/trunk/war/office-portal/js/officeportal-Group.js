uptempo.officePortal = {};
uptempo.officePortal.billingGroup = {};

uptempo.officePortal.billingGroup.getBillingGroupInfo = function (groupKey) {
    //*** Get the data from the server.
    $.ajax({
        type: 'GET',
        url: '/service/billinggroup/' + groupKey,
        success: function (response) {
            if (response.status == "SUCCESS") {
                if(typeof response.data !== 'undefined'){
                    for (var i in uptempo.billingGroups.validFields) {
                        $(uptempo.billingGroups.validFields[i].inputId).html(response.data[uptempo.billingGroups.validFields[i].formVal]);
                    }    
                } else {
                    uptempo.officePortal.util.alert('The Group required does not exist');
                }
                
            } else {

            }

        }
    });
}

uptempo.officePortal.billingGroup.makePageEditable = function (pageSelector, buttonSelector) {
    $(pageSelector).find('.editable').each(function () {
        $(this).html('<input type="text"  name="' + $(this).attr('id') + '" id="' + $(this).attr('id') + '" value="' + $(this).html() + '"/>');
        $(this).removeAttr('id');
    });

    $(buttonSelector).html('Save Changhes');
    $(buttonSelector).attr('onclick', 'javascript:uptempo.officePortal.billingGroup.submitUpdate();');
    $(buttonSelector).attr('id', 'save-changes');

}

uptempo.officePortal.billingGroup.makePageNotEditable = function (pageSelector, buttonSelector) {
    $(pageSelector).find('.editable').each(function () {
        var id = $(this).children('input').attr('id');
        var value = $(this).children('input').val();
        $(this).empty();
        $(this).attr('id', id);
        $(this).html(value);
    });

    $(buttonSelector).html('Edit Profile');
    $(buttonSelector).attr('onclick', 'uptempo.officePortal.billingGroup.makePageEditable("#billinggroup-page", $(this))');
    $(buttonSelector).attr('id', 'edit-profile');
}

uptempo.officePortal.billingGroup.submitUpdate = function () {
    var billinggroupsKey = uptempo.officePortal.billingGroup.groupKey;
    var validationResult = uptempo.ajax.validateInput(uptempo.billingGroups.validFields)

    if (validationResult.isValid) {
        var formData = uptempo.ajax.consructPostString(uptempo.billingGroups.validFields);
        //*** Submit the XHR request
        $.ajax({
            type: 'PUT',
            url: "/service/billinggroup/" + billinggroupsKey,
            data: formData,
            success: function (response) {
                //*** If the response was sucessful, show the success indicator.
                if (response.status === "SUCCESS") {
                    console.log('success');
                    console.log(JSON.stringify(response));
                    uptempo.officePortal.billingGroup.makePageNotEditable("#billinggroup-page", "#save-changes");
                } else {
                    console.log("Failed to add " +
                        response.message);
                }
            }
        });
    } else {
        var message = "";
        if (validationResult.errorMessage != "") {
            message = validationResult.errorMessage;
        }
        uptempo.officePortal.util.alert(message)
    }
}