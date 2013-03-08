/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.imageservice;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.medselect.server.BaseServerResource;

public class ImageServerResource extends BaseServerResource {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	public ImageServerResource() {
          super();
	}

	@Get
	public JsonRepresentation readImagesInfo(Representation image)
			throws JSONException {
		// Listing all the image categories.
		Query q = new Query("ImageInfo");
		PreparedQuery pq = ds.prepare(q);
		int totalImages = 0;
		JSONArray imageCategoryArray = new JSONArray();
		for (Entity result : pq.asIterable()) {
			Key key = result.getKey();
			String keyString = key.getName();
			Map<String, Object> mappedObject = new HashMap<String, Object>();
			mappedObject.put("id", keyString);
			mappedObject.putAll(result.getProperties());
			JSONObject imageCategoryJson = new JSONObject(mappedObject);
			imageCategoryArray.put(imageCategoryJson);
			totalImages++;
		}

		String message = "Returned " + totalImages + " images.";
		JSONObject obj = new JSONObject();
		try {
			obj.put("imagesList", imageCategoryArray);
		} catch (JSONException ex) {
			message = "Error converting error list to JSON: " + ex.toString();
		}

		LOGGER.info(message);
		JsonRepresentation a = this.getJsonRepresentation("SUCCESS", message,
				obj);
		return a;
	}

	@Delete
	public JsonRepresentation deleteImage(Representation image) {
		Form imageForm = new Form(image);
		String imageKeyString = imageForm.getFirstValue("imageID");
		String blobKeyString = imageForm.getFirstValue("blobKey");

		Key dataStoreKey = KeyFactory.stringToKey(imageKeyString);
		// Key imageKey = KeyFactory.stringToKey(blobKeyString);
		BlobKey blobKey = new BlobKey(blobKeyString);
		if (dataStoreKey != null) {
			blobstoreService.delete(blobKey);
			ds.delete(dataStoreKey);
		}
		JsonRepresentation a = this.getJsonRepresentation("SUCCESS",
				"Image deleted.", null);
		return a;
	}

	@Put
	public JsonRepresentation updateUser(Representation image) {
		Form uForm = new Form(image);
		String updateImageStatus = "SUCCESS";
		JSONArray errorList = new JSONArray();
		String message = "";

		// ***Read the user information from the request
		String userEmail = uForm.getFirstValue("email");
		Key dsKey = KeyFactory.createKey("Users", userEmail);
		LOGGER.info("Updating user " + dsKey.getName());
		Entity updateUser = null;
		try {
			updateUser = ds.get(dsKey);
		} catch (EntityNotFoundException ex) {
			LOGGER.warning("User " + userEmail + " does not exist.");
			message = "Update failed. The user did not exist.";
		}

		String title = uForm.getFirstValue("title");
		String firstName = uForm.getFirstValue("firstname");
		String lastName = uForm.getFirstValue("lastname");
		String address = uForm.getFirstValue("address");
		String city = uForm.getFirstValue("city");
		String state = uForm.getFirstValue("state");
		String cell = uForm.getFirstValue("cell");
		String currentPwd = uForm.getFirstValue("currentpwd");
		String newPwd = uForm.getFirstValue("newpwd");

		// *** Make the update a sparse update by detecting filled in fields.
		if (title != null) {
			updateUser.setUnindexedProperty("title", title);
		}
		if (firstName != null) {
			updateUser.setUnindexedProperty("firstName", firstName);
		}
		if (lastName != null) {
			updateUser.setProperty("lastName", lastName);
		}
		if (address != null) {
			updateUser.setProperty("address", address);
		}
		if (city != null) {
			updateUser.setProperty("city", city);
		}
		if (state != null) {
			updateUser.setProperty("state", state);
		}
		if (cell != null) {
			updateUser.setUnindexedProperty("cell", cell);
		}

		JsonRepresentation a = this.getJsonRepresentation(updateImageStatus,message, null);
		return new JsonRepresentation("User Updated");
	}

	// @Post
	// public JsonRepresentation createImage(Representation image,
	// HttpServletRequest req) throws JSONException{
	// String imageUploadStatus = "";
	// String message = "";
	// JSONObject obj = new JSONObject();
	//
	// Form imageForm = new Form(image);
	// Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
	// List<BlobKey> blobKey = blobs.get("imageFile");
	// if(blobKey == null){
	// System.out.println("No Files Uploaded..!!");
	// imageUploadStatus = "FAILURE";
	// message = "Image upload Failed.";
	// }else{
	// for(BlobKey blobKeyToSave : blobKey){
	// String blobKeyString = blobKeyToSave.getKeyString();
	// BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
	// BlobInfo blobInfo = null;
	// blobInfo = blobInfoFactory.loadBlobInfo(blobKeyToSave);
	// String fileName = blobInfo.getFilename();
	// imageUploadStatus = "SUCCESS";
	//
	// String imageCaption = imageForm.getFirstValue("imageCaption");
	// String imageCategory = imageForm.getFirstValue("chooseImageCategory");
	// String createdBy = "test@example.com";
	// Date createdDate = new Date();
	//
	// String imageKey = KeyFactory.createKeyString("imageKey", blobKeyString);
	// Entity newImage = new Entity("Image",imageKey);
	// newImage.setProperty("fileName", fileName);
	// newImage.setProperty("imageCaption", imageCaption);
	// newImage.setProperty("createdBy", createdBy);
	// newImage.setProperty("createdDate", createdDate);
	// newImage.setProperty("imageCategory", imageCategory);
	//
	// message = "Successfully inserted image category " + imageKey;
	// ds.put(newImage);
	// obj.put("imageKey", imageKey);
	// }
	// }
	// JsonRepresentation a = this.getJsonRepresentation(imageUploadStatus,
	// message, obj);
	// return a;
	// }
}
