/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.imageservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

@SuppressWarnings("serial")
public class ImageUploaderServlet extends HttpServlet {
	private DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String imageUploadStatus = "";
		String message = "";

		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKey = blobs.get("imageFile");
		if (blobKey == null) {
			System.out.println("No Files Uploaded..!!");
			imageUploadStatus = "FAILURE";
			message = "Image upload Failed.";
		} else {
			for (BlobKey blobKeyToSave : blobKey) {
				String blobKeyString = blobKeyToSave.getKeyString();
				BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
				BlobInfo blobInfo = null;
				blobInfo = blobInfoFactory.loadBlobInfo(blobKeyToSave);
				String fileName = blobInfo.getFilename();
				Date createdDate = blobInfo.getCreation();
				imageUploadStatus = "SUCCESS";

				String imageCaption = req.getParameter("imageCaption");
				String chooseImageCategory = req.getParameter("chooseImageCategory");
				String createdBy = "test@example.com";
				String imageCategory = "NA";
				if (chooseImageCategory != null
						&& !chooseImageCategory.equals("Choose Category")) {
					imageCategory = chooseImageCategory;
				}

				ImagesService services = ImagesServiceFactory.getImagesService();
				ServingUrlOptions serve = ServingUrlOptions.Builder.withBlobKey(blobKeyToSave);
				String url = services.getServingUrl(serve);

				String imageDatastoreKey = KeyFactory.createKeyString("imageKey", blobKeyString);
				Entity newImage = new Entity("ImageInfo", imageDatastoreKey);
				newImage.setProperty("blobKey", blobKeyString);
				newImage.setProperty("fileName", fileName);
				newImage.setProperty("imageCaption", imageCaption);
				newImage.setProperty("createdBy", createdBy);
				newImage.setProperty("createdDate", createdDate);
				newImage.setProperty("modifiedBy", "NA");
				newImage.setProperty("modifiedDate", "NA");
				newImage.setProperty("imageCategory", imageCategory);
				newImage.setProperty("imageURL", url);

				ds.put(newImage);
				message = "Successfully inserted image : " + fileName;
			}
		}
		PrintWriter out = resp.getWriter();
		String outputMessage = "<html><head>"
				+ "<script type='text/javascript'>"
				+ "setInterval(function(){"
				+ "window.location = '/imagecategory.jsp'"
				+ "},2500);</script>"
				+ "</head>"
				+ "<body><div style='width=1000px;margin:auto;text-align:center;font-size:16px;color:green;'>"
				+ imageUploadStatus + " : " + message + "</div></body>"
				+ "</html>";
		out.println(outputMessage);
	}
}
