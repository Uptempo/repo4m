/*
 * Copyright 2012 Uptempo Group Inc.
 */

package com.medselect.drawing;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Composite.Anchor;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.images.Transform;
import com.medselect.server.BaseServerResource;
import com.medselect.util.MailUtils;
import com.medselect.util.SystemUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

/**
 *
 * @author Mike Gordon
 */
public class DrawingServerResource extends BaseServerResource {

  String UPLOAD_FILE_TYPE = "image/png";
  //*** Determines if the drawing should be e-mailed from the server or not.
  String DRAWING_EMAIL_FROM_SERVER = "FALSE";
  
  public DrawingServerResource() {
    super();
  }

  @Get
  public JsonRepresentation getDrawings(Representation drawingFilterParams) {
    Form dForm = new Form(drawingFilterParams);
    String imageKeyStr = dForm.getFirstValue("id");
    String imageFileKeyStr = dForm.getFirstValue("fileid");
    String drawingReadStatus = "SUCCESS";
    String message = "";
    
    BlobKey fileKey = new BlobKey(imageFileKeyStr);
    ImagesService services = ImagesServiceFactory.getImagesService();
    ServingUrlOptions serve = ServingUrlOptions.Builder.withBlobKey(fileKey);
    String url = services.getServingUrl(serve);

    JsonRepresentation a = this.getJsonRepresentation(drawingReadStatus, message, null);

    return a;
  }

  /**
   * Inserts and sends a drawing.
   *
   */
  @Post
  public JsonRepresentation insertDrawing(Representation drawingValue) {
    LOGGER.info("Uploading drawing.");
    Form dForm = new Form(drawingValue);
    JSONObject imageURLObj = null;
    String drawingDescription = dForm.getFirstValue("description");
    String drawingUser = dForm.getFirstValue("user");
    String drawingImageBase64 = dForm.getFirstValue("img");
    String backgroundPath = dForm.getFirstValue("bg");
    String drawingInsertStatus = "SUCCESS";  
    String message = "";
    boolean lock = true; //*** Lock the file after writing.
    byte[] finalImage = null; //***Final combined image.

    //*** Validate the inputs
    if (drawingUser == null || drawingImageBase64 == null) {
      drawingInsertStatus = "FAILURE";
      message = "ERROR: Could not save and send drawing: parameters missing.";
      LOGGER.severe(message);
    }
    
    //*** Convert the image from Base64 to a byte array.
    String pattern = "data:image/png;base64,";
    drawingImageBase64 = drawingImageBase64.substring(22);
    byte[] decodedImage = Base64.decodeBase64(drawingImageBase64.getBytes());

    //*** Open the blobstore, save the file, then get the file handle
    FileService fileService = FileServiceFactory.getFileService();

    // Create a new Blob file with mime-type "text/plain"
    AppEngineFile file = null;
    try {
      // Create file object.
      File fileToRead = new File(backgroundPath);
   
      // Create FileInputStream object.
      FileInputStream fin = new FileInputStream(fileToRead);
     
      //***Create a byte array the size of the file.
      byte underlayImage[] = new byte[(int)fileToRead.length()];

      //***Read the image content in.
      fin.read(underlayImage);
      finalImage = this.combineImages(underlayImage, decodedImage);

      //***Write the finished image to the blobstore.
      file = fileService.createNewBlobFile(UPLOAD_FILE_TYPE);
      FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
      writeChannel.write(ByteBuffer.wrap(finalImage));
      writeChannel.closeFinally();

    } catch (IOException ex) {
      LOGGER.severe("Image upload failed: " + ex.toString());
      message += "Image upload failed: " + ex.toString();
      drawingInsertStatus = "FAILURE";
    } catch (Exception ex) {
      LOGGER.severe("Image upload failed: " + ex.toString());
      message += "Image upload failed: " + ex.toString();
      drawingInsertStatus = "FAILURE";
    }

    if (!drawingInsertStatus.equals("FAILURE")) {
      String fileName = file.getFullPath();
      //***Build a URL to return to the front end to serve immediately.
      BlobKey blobKey = fileService.getBlobKey(file);
      ServingUrlOptions serve = ServingUrlOptions.Builder.withBlobKey(blobKey);
      ImagesService imageService = ImagesServiceFactory.getImagesService();
      String url = imageService.getServingUrl(serve);
      imageURLObj = new JSONObject();
      try {
        imageURLObj.put("url", url);
      } catch (JSONException ex) {
        message += "Error converting image URL to JSON: " + ex.toString();
      }
      
      Entity newDrawing = new Entity("Drawing");
      newDrawing.setProperty("description", drawingDescription);
      newDrawing.setProperty("userEmail", drawingUser);
      newDrawing.setProperty("drawingPath", fileName);
      ds.put(newDrawing);

      if (DRAWING_EMAIL_FROM_SERVER.equals("TRUE")) {
        String drawingEmailTo = dForm.getFirstValue("emailto");
        boolean drawingSendResult = sendDrawingImage(drawingUser, drawingEmailTo, finalImage);
        if (drawingSendResult) {
          message = "Image upload and send to " + drawingEmailTo + " successful.";
        } else {
          message = "Image upload and send to " + drawingEmailTo + " failed.";
          drawingInsertStatus = "FAILURE";
        }
      } else {
          message = "Image upload from user " + drawingUser + " successful.";
      }
    }
  
    JsonRepresentation a = this.getJsonRepresentation(drawingInsertStatus, message, imageURLObj);

    return a;
  }

  /**
   * Combine two images in byte arrays.  The resolution of the second (overlay)
   * image determines final image resolution.
   * @param imageBottom The underlay image of the drawing.
   * @param imageTop The overlay image of the drawing.
   * @return
   */
  private byte[] combineImages (byte[] imageBottom, byte[] imageTop) {
    
    //***Get the images into picture objects.
    Image picture1 = ImagesServiceFactory.makeImage(imageBottom);
    Image picture2 = ImagesServiceFactory.makeImage(imageTop);
    
    //***Get the size of the overlay image.
    int overlayWidth = picture2.getWidth();
    
    //***Make a resize transform to resize the underlay image to match the overlay.
    Transform bgResize = ImagesServiceFactory.makeResize(overlayWidth, 0);
    ImagesService painter = ImagesServiceFactory.getImagesService();
    picture1 = painter.applyTransform(bgResize, picture1);
    String logMessage = "Compositing images.  Background: " + picture1.getWidth() +
                        "x" + picture1.getHeight()  + ", Foreground: " + picture2.getWidth() +
                        "x" + picture2.getHeight();
    LOGGER.info(logMessage);
    
    //***Crete canvas elements for compositing the image.
    Composite canvasBottom = ImagesServiceFactory.makeComposite(picture1, 0, 0, 1, Anchor.TOP_LEFT);
    Composite canvasTop = ImagesServiceFactory.makeComposite(picture2, 0, 0, 1, Anchor.TOP_LEFT);
    
    Collection <Composite> compositeArray = Arrays.asList(canvasBottom, canvasTop);
    Image finalImg = painter.composite(compositeArray,
                                       picture1.getWidth(),
                                       picture1.getHeight(),
                                       0xFFFFFF);

    return finalImg.getImageData();
  }

  /**
   * Send a drawing image.
   * @param drawingUser  The user e-mail to send the drawing from (cc).
   * @param drawingEmailTo  The e-mail to send the drawing to.
   * @param finalImage  The composed image bytes.
   * @return boolean indicating whether image send was successful or not.
   */
  private boolean sendDrawingImage(String drawingUser, String drawingEmailTo, byte[] finalImage) {
    boolean sendResult = true;

    //*** Send an e-mail with the image to the recipient and user.
    //*** Get the user information
    Key dsKey = KeyFactory.createKey("Users", drawingUser.toLowerCase());
    String subject = " has prepared a Rheumatology Image for you!";
    String fromName = "Rheumatology User";
    try {
      Entity userEntity = ds.get(dsKey);
      String userFName = (String)userEntity.getProperty("firstName");
      String userLName = (String)userEntity.getProperty("lastName");
      String userTitle = (String)userEntity.getProperty("title");
      fromName = userTitle + " " + userFName + " " + userLName;
      subject = fromName + subject;
    } catch (EntityNotFoundException ex) {
      LOGGER.severe("Image upload failed: " + ex.toString());
      sendResult = false;
    }
    String htmlMessage = "<strong>Here's the image I went over with you." +
                         " If you have any questions, please give me a call." +
                         "</strong><br />";

    htmlMessage += "<br />";
    htmlMessage += "<a href='http://www.enbrel.com/request-information.jspx'>";
    htmlMessage += "<img src='" + SystemUtils.getAppURLNonSSL();
    htmlMessage += "/img/email-footer-image.png' />";
    htmlMessage += "</a>";
    if (sendResult) {
      try {
        MailUtils mailSender = new MailUtils();
        MimeBodyPart imageBinary = new MimeBodyPart();
        //prepare attachment using a bytearraydatasource
        DataSource src = new ByteArrayDataSource(finalImage, "image/png");
        imageBinary.setFileName("drawing.png");
        imageBinary.setDataHandler(new DataHandler(src));

        mailSender.sendMultiPartMail(MailUtils.EMAIL_FROM,
                                     MailUtils.EMAIL_FROM_DISPLAY,
                                     drawingEmailTo,
                                     drawingEmailTo,
                                     drawingUser,
                                     subject,
                                     htmlMessage,
                                     htmlMessage,
                                     Arrays.asList(imageBinary));

      } catch (MessagingException ex) {
        LOGGER.severe("Image upload failed: " + ex.toString());
        sendResult = false;
      }
    }

    return sendResult;
  }

  /**
   * Allows updating the metadata of a drawing, but not the actual drawing itself.
   * @param configValue
   * @return
   */
  @Put
  public JsonRepresentation UpdateDrawing(Representation drawingValue) {
    Form dForm = new Form(drawingValue);
    String drawingUpdateStatus = "SUCCESS";
    String message = "";


    JsonRepresentation a = this.getJsonRepresentation(drawingUpdateStatus, message, null);

    return a;
  }

  @Delete
  public JsonRepresentation DeleteDrawing(Representation drawingValue) {
    Form dForm = new Form(drawingValue);
    String drawingDeleteStatus = "SUCCESS";
    String message = "";


    JsonRepresentation a = this.getJsonRepresentation(drawingDeleteStatus, message, null);

    return a;
  }
}
