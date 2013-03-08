<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Image Category</title>
<link
	href="//ajax.aspnetcdn.com/ajax/jquery.ui/1.9.0/themes/flick/jquery-ui.css"
	type="text/css" rel="stylesheet" />
<link href="/css/imageservice.css" type="text/css" rel="stylesheet" />

<script type="text/javascript"
	src="//ajax.aspnetcdn.com/ajax/jQuery/jquery-1.8.2.js"></script>
<script src="//ajax.aspnetcdn.com/ajax/jquery.validate/1.10.0/jquery.validate.js"
	type="text/javascript"></script>
<script type="text/javascript"
	src="//ajax.aspnetcdn.com/ajax/jquery.ui/1.9.0/jquery-ui.js"></script>
<script type="text/javascript" src="/js/imageservice.js"></script>
</head>
<body>
	<div id="page-wrap">
		<div id="tabs">
			<ul>
				<li><a href="#tabs-1">Add Image Category</a></li>
				<li><a href="#tabs-2">View Image Categories</a></li>
				<li><a href="#tabs-3" class="imageUploadTab">Image Upload</a></li>
				<li><a href="#tabs-4">List Uploaded Images</a></li>
			</ul>
			<div id="tabs-1">
				<form method="POST" action="/service/imagecategory" id="saveCategoryForm">
					<div class="clear">
						<span class="formLabel">Category Name</span>
						<div class="clear"></div>
						<input type="text" name="categoryName" id="categoryName" class="textfield" />
						<div class="clear"></div>
						<span id="error"></span>
					</div>
					<div class="clear" style="height: 10px;"></div>
					<div class="clear">
						<span class="formLabel">Category Description</span>
						<div class="clear"></div>
						<textarea name="categoryDescription" id="categoryDescription"
							class="textarea"></textarea>
						<div class="clear"></div>
						<span id="error"></span>
					</div>
					<div class="clear">
						<input type="button" class="btn" id="saveCategory" value="Save Category" />
						<input type="reset" class="btn" id="resetCategory" value="Reset" />
					</div>
				</form>
			</div>
			<!-- ----------------------------------------------------------------- -->
			<div id="tabs-2">
				<button class="loadCategories btn">Load All Categories</button>
				<div class="clear"></div>
				<table cellspacing="0" id="table2">
					<thead>
						<tr>
							<th>Category Name</th>
							<th>Description</th>
							<th>Created By</th>
							<th>Create Date</th>
							<th>Modified By</th>
							<th>Modified Date</th>
							<th>Edit</th>
							<th>Delete</th>
						</tr>
					</thead>
					<tbody class="appendCategoriesHere"></tbody>
				</table>
				<div class="categoryDialog clear">
					<form action="/service/imagecategory" id="updateCategoryForm">
						<input type="hidden" name="hiddenID" id="hiddenID" />
						<div class="clear">
							<span class="formLabel">Updated Category Name</span>
							<div class="clear"></div>
							<input type="text" name="updatedCategoryName" id="updatedCategoryName" class="textfield" />
							<div class="clear"></div>
							<span id="error"></span>
						</div>
						<div class="clear" style="height: 10px;"></div>
						<div class="clear">
							<span class="formLabel">Updated Category Description</span>
							<div class="clear"></div>
							<textarea name="updatedCategoryDescription" id="updatedCategoryDescription"
								class="textarea"></textarea>
							<div class="clear"></div>
							<span id="error"></span>
						</div>
						<div class="clear">
							<input type="button" class="btn" id="updateCategory" value="Update Category" />
						</div>
					</form>
				</div>
			</div>
			<!-- --------------------------------------------------------------- -->
			<div id="tabs-3">
			<%
  			  BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			%>
				<form method="POST" action="<%= blobstoreService.createUploadUrl("/upload") %>" id="uploadImageForm" enctype="multipart/form-data">
					<div class="clear" id="formRow">
						<span class="formLabel">Choose Image</span>
						<div class="clear"></div>
						<input type="file" name="imageFile" id="imageFile" class="imageFile" />
						<div class="clear"></div>
						<span id="error"></span>
					</div>
					<div class="clear" style="height: 5px;"></div>
					<div class="clear" id="previewImage"></div>
					<div class="clear" style="height: 5px;"></div>
					<div class="clear" id="formRow">
						<span class="formLabel">Image Category</span>
						<div class="clear"></div>
						<select name="chooseImageCategory" id="chooseImageCategory"></select>
						<div class="clear"></div>
						<span id="error"></span>
					</div>
					<div class="clear" style="height: 6px;"></div>
					<div class="clear" id="formRow">
						<span class="formLabel">Image Description / Caption</span>
						<div class="clear"></div>
						<input type="text" name="imageCaption" id="imageCaption" class="textfield" />
						<div class="clear"></div>
						<span id="error"></span>
					</div>
					<div class="clear">
						<input type="submit" class="btn" id="saveImage" value="Upload Image" />
						<input type="reset" class="btn" id="resetImage" value="Reset" />
					</div>
				</form>
				<img class="originalImagePreview" />
			</div>
			<!-- ---------------------------------------------------------------------- -->
			<div id="tabs-4">
				<button class="loadImages btn">Load All Images</button>
				<div class="clear"></div>
				<table cellspacing="0" id="table2">
					<thead>
						<tr>
							<th>File Name</th>
							<th>Caption</th>
							<th>Category</th>
							<th>Created By</th>
							<th>Create Date</th>
							<th>Modified By</th>
							<th>Modified Date</th>
							<th>Preview</th>
							<th>Delete</th>
						</tr>
					</thead>
					<tbody class="appendImagesHere"></tbody>
				</table>
				<div class="clear" id="imagePreviewDiv">
					<img class="changeImagePreview" />
					<div class="imgDescPrev">
						<span id="imageCaptionPreview"></span>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>