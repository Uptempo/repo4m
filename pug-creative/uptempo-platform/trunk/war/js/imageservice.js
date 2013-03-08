$(document).ready(function() {
	$( "#tabs" ).tabs();
	
	
	// For saving the categories of images.
	$("#saveCategory").live("click",function(event){
		event.preventDefault();
		var formData = $("#saveCategoryForm").serialize();
		var actionURL = $("#saveCategoryForm").attr("action");
		$.ajax({
			url : actionURL,
			type : 'POST',
			data : formData,
			success : function(data, status, xhr){
				alert("Success : "+data.message);
				$("#saveCategoryForm").find("#resetCategory").trigger('click');
			},
			error : function(data, statusText){
				alert("Error : "+statusText);
			}
		});
	});
	
	// For getting the categories of images from DB / Datastore.
	$(".loadCategories").live("click",function(event){
		event.preventDefault();
		$.ajax({
			url : '/service/imagecategory',
			type : 'GET',
			success : function(data, status, xhr){
				var catergoriesArray = data.data.imageCategories;
				//alert("Success : \n"+catergoriesArray);
				appendRows(catergoriesArray);
			},
			error : function(data, status, xhr){
				alert("Error : "+status);
			}
		});
	});
	
	function appendRows(catergoriesArray){
		var categoryJSONArray = catergoriesArray;
		$(".categoryRow").remove();

		for(var i=0; i<categoryJSONArray.length; i++){
			var appendCategories = '';
			var appendCategories = '<tr id="'+categoryJSONArray[i].id+'" class="categoryRow">'+
									'<td><span id="categoryNameHere">'+categoryJSONArray[i].categoryName+'</span></td>'+
									'<td><span id="categoryDescriptionHere">'+categoryJSONArray[i].categoryDescription+'</span></td>'+
									'<td><span id="createdByHere">'+categoryJSONArray[i].createdBy+'</span></td>'+
									'<td><span id="createdDateHere">'+categoryJSONArray[i].createdDate+'</span></td>'+
									'<td><span id="modifiedByHere">'+categoryJSONArray[i].modifiedBy+'</span></td>'+
									'<td><span id="modifiedDateHere">'+categoryJSONArray[i].modifiedDate+'</span></td>'+
									'<td><input type="hidden" name="accessCode" class="accessCode" value="'+categoryJSONArray[i].accessCode+'" /><button class="editCategoryBtn btn2">Edit</button></td>'+
									'<td><form id="deleteCategoryForm"><input type="hidden" name="categoryName" value="'+categoryJSONArray[i].categoryName+'" /><input type="button" class="btn2 deleteCategory" value="Delete" /></form></td>'+
									'</tr>';
			$(".appendCategoriesHere").append(appendCategories);
		}
	}
	
	$(".categoryDialog").dialog({
		autoOpen : false,
		width : "650",
		height: "350",
		modal : true
	});
	
	$(".editCategoryBtn").live("click",function(event){
		event.preventDefault();
		//var accessCode = $(this).parents(".categoryRow").find(".accessCode").val();
		var hiddenID = $(this).parents(".categoryRow").attr("id");
		var categoryNameHere = $(this).parents(".categoryRow").find("#categoryNameHere").text();
		var categoryDescriptionHere = $(this).parents(".categoryRow").find("#categoryDescriptionHere").text();
		
		$("#hiddenID").val(hiddenID);
		$("#updatedCategoryName").val(categoryNameHere);
		$("#updatedCategoryDescription").val(categoryDescriptionHere);
		$(".categoryDialog").dialog("open");
	});
	
	$("#updateCategory").live("click",function(event){
		event.preventDefault();
		var formData = $("#updateCategoryForm").serialize();
		var actionURL = $("#updateCategoryForm").attr("action");
		$.ajax({
			url : actionURL,
			type : 'PUT',
			data : formData,
			success : function(data, status, xhr){
				alert("Success : "+data.message);
				$("#saveCategoryForm").find("#resetCategory").trigger('click');
			},
			error : function(data, statusText){
				alert("Error : "+statusText);
			}
		});
	});
	
	$(".deleteCategory").live("click",function(event){
		event.preventDefault();

		var formData = $(this).parents(".categoryRow").find("#deleteCategoryForm").serialize();
		$.ajax({
			url : '/service/imagecategory',
			type : 'DELETE',
			data : formData,
			success : function(data, status, xhr){
				var message = data.message;
				alert("Success : "+message);
			},
			error: function(data, status, xhr){
				alert("Error : "+data);
			}
		});
	});
	
/***************************************************************************************************/
	// Load image categories on the drop down on clicking on image upload tab.
	$(".imageUploadTab").live("click",function(){
		$.ajax({
			url : '/service/imagecategory',
			type : 'GET',
			success : function(data, status, xhr){
				var catergoriesArray = data.data.imageCategories;
				//alert("Success : \n"+catergoriesArray);
				appendCategories(catergoriesArray);
			},
			error : function(data, status, xhr){
				alert("Error : "+status);
			}
		});
	});
	
	function appendCategories(catergoriesArray){
		var categoryJSONArray = catergoriesArray;
		$(".categoriesInDropDown").remove();
		$("#chooseImageCategory").append('<option class="categoriesInDropDown">Choose Category</option>');
		for(var i=0;i<categoryJSONArray.length;i++){
			$("#chooseImageCategory").append('<option class="categoriesInDropDown">'+categoryJSONArray[i].categoryName+'</option>');
		}
	}
	
	// Display the preview of image on the UI
	$(".imageFile").live("change",function(evt){
		var files = evt.target.files; // FileList object
		
	    // Loop through the FileList and render image files as thumbnails.
	    for (var i = 0, f; f = files[i]; i++) {
	      // Only process image files.
	      if (!f.type.match('image.*')) {
	        continue;
	      }
	      var reader = new FileReader();
	      // Closure to capture the file information.
	      reader.onload = (function(theFile) {
	        return function(e) {
	        	$(".imageThumbnailPreview").remove();
	        	var appendImage = '<img src="'+e.target.result+'" title="'+theFile.name+'" class="imageThumbnailPreview" />';  
	        	$("#previewImage").append(appendImage);
	        	$(".originalImagePreview").attr("src",e.target.result);
	        };
	      })(f);

	      // Read in the image file as a data URL.
	      reader.readAsDataURL(f);
	    }
	});
	
	$.validator.addMethod("aspectRatio", function(value, element) {
			var width = $(".originalImagePreview").width();
			var height = $(".originalImagePreview").height();
			if(width / height == 3/4){
				return true;
			}else if(width / height == 4/3){
				return true;
			}
			return false;
	}, "Please upload file of 4:3 or 3:4 aspect ratio");
	
	$("#uploadImageForm").validate({
		rules : {
			imageFile : {
				required : true,
				aspectRatio : true,
				accept: "jpg|png|gif"
			},
			imageCaption : {
				required : true
			}
		},
		messages : {
			imageFile : "Please upload only (.jgp),(.png),(.gif) images of aspect ratio 4:3 or 3:4 (e.g 1024 X 768 , 800 X 600)",
			imageCaption : "Please enter some description for image"
		},
		errorPlacement: function(error, element) {
		     error.appendTo(element.parents("#formRow").find("#error"));
		}
	});
	
//	$("#saveImage").live("click",function(event){
//		event.preventDefault();
//		var formData = $("#uploadImageForm").serialize();
//		var actionURL = $("#uploadImageForm").attr("action");
//		var contentType = $("#uploadImageForm").attr("enctype");
//		$.ajax({
//			url : actionURL,
//			type : 'POST',
//			contentType : contentType,
//			data : formData,
//			success : function(data, status, xhr){
//				alert("Success : "+data.message);
//				$("#uploadImageForm").find("#resetImage").trigger('click');
//			},
//			error : function(data, statusText){
//				alert("Error : "+statusText);
//			}
//		});
//	});
	
	$("#resetImage").live("click",function(){
		$("#imageFile").val('');
		$("#imageCaption").val('');
		$("#chooseImageCategory").val('Choose Category');
		$(".imageThumbnailPreview").remove();
	});
	
/***********************************************************************************************************/
	$(".loadImages").live("click",function(event){
		event.preventDefault();
		$.ajax({
			url : '/service/image',
			type : 'GET',
			success : function(data, status, xhr){
				var imagesArray = data.data.imagesList;
				//alert("Success : \n"+catergoriesArray);
				appendImageRows(imagesArray);
			},
			error : function(data, status, xhr){
				alert("Error : "+status);
			}
		});
	});
	
	function appendImageRows(imagesArray){
		var imagesJSONArray = imagesArray;
		$(".imageRow").remove();

		for(var i=0; i<imagesJSONArray.length; i++){
			var appendCategories = '';
			var appendCategories = '<tr id="'+imagesJSONArray[i].id+'" class="imageRow">'+
									'<td><span id="imageNameHere">'+imagesJSONArray[i].fileName+'</span></td>'+
									'<td><span id="imageCaptionHere">'+imagesJSONArray[i].imageCaption+'</span></td>'+
									'<td><span id="imageCategoryNameHere">'+imagesJSONArray[i].imageCategory+'</span></td>'+
									'<td><span id="createdByHere">'+imagesJSONArray[i].createdBy+'</span></td>'+
									'<td><span id="createdDateHere">'+imagesJSONArray[i].createdDate+'</span></td>'+
									'<td><span id="modifiedByHere">'+imagesJSONArray[i].modifiedBy+'</span></td>'+
									'<td><span id="modifiedDateHere">'+imagesJSONArray[i].modifiedDate+'</span></td>'+
									'<td><input type="hidden" name="imageURL" class="imageURL" value="'+imagesJSONArray[i].imageURL+'" /><button id="previewImage" class="btn2">P1</button><button id="previewImage" class="btn2">P2</button><button id="previewImage" class="btn2">P3</button></td>'+
									'<td><form id="deleteImageForm"><input type="hidden" name="blobKey" class="blobKey" value="'+imagesJSONArray[i].blobKey+'" /><input type="hidden" name="imageID" class="imageID" value="'+imagesJSONArray[i].id+'" /><button class="btn2 deleteImage">Delete</button></form></td>'+
									'</tr>';
			$(".appendImagesHere").append(appendCategories);
		}
	}
	
	$(".deleteImage").live("click",function(event){
		event.preventDefault();
		//var imageId = $(this).parents(".imageRow").attr("id");
		var blobKey = $(this).parents(".imageRow").find(".blobKey").val();
		var formData = $(this).parents(".imageRow").find("#deleteImageForm").serialize();
		//var formData = 'blobKey='+blobKey;
		$.ajax({
			url : '/service/image',
			type : 'Delete',
			data : formData,
			success : function(data, status, xhr){
				var message = data.message;
				alert("Success : "+message);
			},
			error: function(data, status, xhr){
				alert("Error : "+data);
			}
		});
	});
	
	/*********************************************************************************************/
	$("#previewImage").live("click",function(event){
		event.preventDefault();
		var imageURL = $(this).parents(".imageRow").find(".imageURL").val();
		var btnText = $(this).text();
		if(btnText === "P1"){
			$(".changeImagePreview").attr("src",imageURL+"=s1024");
		}
		if(btnText === "P2"){
			$(".changeImagePreview").attr("src",imageURL+"=s400");
		}
		if(btnText === "P3"){
			$(".changeImagePreview").attr("src",imageURL+"=s150");
		}
		var imageCaption = $(this).parents(".imageRow").find("#imageCaptionHere").text();
		$("#imageCaptionPreview").text(imageCaption);
		//window.open("/imagerender?blobKey="+blobKey,null,false);
		
	});
});