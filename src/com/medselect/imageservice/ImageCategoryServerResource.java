/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.imageservice;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.medselect.server.BaseServerResource;

public class ImageCategoryServerResource extends BaseServerResource {

  public ImageCategoryServerResource() {
    super();
  }

  @Get
  public JsonRepresentation readImageCategories(Representation imageCategory) throws JSONException {
    // Listing all the image categories.
    Query q = new Query("ImageCategories");
    PreparedQuery pq = ds.prepare(q);
    int totalCategories = 0;
    JSONArray imageCategoryArray = new JSONArray();
    for (Entity result : pq.asIterable()) {
      Key key = result.getKey();
      String keyString = key.getName();
      long keyLong = key.getId();
      Map<String, Object> mappedObject = new HashMap<String, Object>();
      mappedObject.put("id", keyString);
      mappedObject.put("longKey", keyLong);
      mappedObject.putAll(result.getProperties());
      JSONObject imageCategoryJson = new JSONObject(mappedObject);
      imageCategoryArray.put(imageCategoryJson);
      totalCategories++;
    }

    String message = "Returned " + totalCategories + " image categories.";
    JSONObject obj = new JSONObject();
    try {
      obj.put("imageCategories", imageCategoryArray);
    } catch (JSONException ex) {
      message = "Error converting error list to JSON: " + ex.toString();
    }

    LOGGER.info(message);
    JsonRepresentation a = this.getJsonRepresentation("SUCCESS", message, obj);
    return a;
  }

  @Post
  public JsonRepresentation createImageCategory(Representation imageCategory)
                  throws JSONException {
          Form imageCategoryForm = new Form(imageCategory);
          String creatingImageCategoryStatus = "SUCCESS";

          String categoryName = imageCategoryForm.getFirstValue("categoryName");
          String categoryDescription = imageCategoryForm
                          .getFirstValue("categoryDescription");
          String createdBy = "test@example.com";
          Date createdDate = new Date();
          String accessCode = generateRandomWords(10);

          Key imageCategoryKey = KeyFactory.createKey("ImageCategories",accessCode);
          String keyString = KeyFactory.keyToString(imageCategoryKey);
          System.out.println(keyString);

          Entity newImageCategory = new Entity("ImageCategories",keyString);
          newImageCategory.setProperty("categoryName", categoryName);
          newImageCategory.setProperty("categoryDescription", categoryDescription);
          newImageCategory.setProperty("createdBy", createdBy);
          newImageCategory.setProperty("createdDate", createdDate);
          newImageCategory.setProperty("modifiedBy", "NA");
          newImageCategory.setProperty("modifiedDate", "NA");
          newImageCategory.setProperty("accessCode", accessCode);

          String message = "Successfully inserted image category ";
          JSONObject obj = new JSONObject();

          if (categoryDescription != null && !categoryDescription.equals("")) {
                  ds.put(newImageCategory);
                  //obj.put("categoryKey", imageCategoryKey);
          } else {
                  obj.put("Error", "No New Category Added.");
          }
          JsonRepresentation a = this.getJsonRepresentation(
                          creatingImageCategoryStatus, message, obj);
          return a;
  }

  @Put
  public JsonRepresentation updateCategory(Representation imageCategory) throws UnsupportedEncodingException {
          Form uForm = new Form(imageCategory);
          String updateImageStatus = "SUCCESS";
          String message = "";

          // ***Read the category information from the request
          String hiddenID = uForm.getFirstValue("hiddenID");
          String updatedCategoryName = uForm.getFirstValue("updatedCategoryName");
          String updatedCategoryDescription = uForm.getFirstValue("updatedCategoryDescription");

          Key dsKey = KeyFactory.stringToKey(hiddenID);

          LOGGER.info("Updating image category " + dsKey.getName());
          Entity updatedImageCategory = null;
          try {
                  updatedImageCategory = ds.get(dsKey);
                  message = "Image Category Updated Successfully.";
          } catch (EntityNotFoundException ex) {
                  LOGGER.warning("Category Name : " + updatedCategoryName + " does not exist.");
                  message = "Update failed. Image Category did not exist.";
          }

          // *** Make the update a sparse update by detecting filled in fields.
          if (updatedCategoryName != null) {
                  updatedImageCategory.setProperty("categoryName", updatedCategoryName);
                  updatedImageCategory.setProperty("categoryDescription", updatedCategoryDescription);
          }

          JsonRepresentation a = this.getJsonRepresentation(updateImageStatus,message, null);
          return a;
  }

  @Delete
  public JsonRepresentation deleteImage(Representation imageCategory) {
          Form imageForm = new Form(imageCategory);
          String categoryName = imageForm.getFirstValue("categoryName");

          Key dataStoreCategoryKey = KeyFactory.stringToKey(categoryName);
          // Key imageKey = KeyFactory.stringToKey(blobKeyString);
          if (dataStoreCategoryKey != null) {
                  ds.delete(dataStoreCategoryKey);
          }
          JsonRepresentation a = this.getJsonRepresentation("SUCCESS",
                          "Image Category deleted.", null);
          return a;
  }

  public String generateRandomWords(int wordLength) {
          String randomString = "";
          Random random = new Random();
          char[] word = new char[wordLength]; // length of words.
          for (int j = 0; j < word.length; j++) {
                  word[j] = (char) ('a' + random.nextInt(26));
          }
          randomString = new String(word);
          return randomString.toUpperCase();
  }
}
