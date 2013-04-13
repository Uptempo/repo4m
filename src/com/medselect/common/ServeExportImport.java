/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.common;

import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileWriteChannel;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.logging.Logger;
import org.json.JSONException;
import java.io.IOException;
import java.io.FileNotFoundException;

import com.google.appengine.api.blobstore.BlobKey;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import com.medselect.util.Constants;
import com.medselect.application.ApplicationManager;


/**
 * Base class to serve export.
 * @author karlo.smid@gmail.com
 */
public class ServeExportImport {
  protected static final Logger LOGGER = Logger.getLogger(ServeExportImport.class.getName());
  protected static final String AUTH_KEY_PROP = "com.uptempo.appAuthKey";


  /**
   * Check application key is valid 
   * @return true/false 
   */
  public boolean isApplicationKeyValid(String clientKey) {
  //*** Check for proper service authentication.
  //*** Config manager shared to subclasses.
  if (clientKey == null) {
    clientKey = "";
  }
  ConfigManager cManager = new ConfigManager();
  SimpleConfigValue keyFlag =
      cManager.getSimpleConfigValue(Constants.COMMON_APP, Constants.API_SECURITY_FLAG);
    if (keyFlag != null && keyFlag.getConfigValue().equalsIgnoreCase("TRUE")) {
      String authKey = System.getProperty(AUTH_KEY_PROP);
      if (!authKey.equals(clientKey)) {
        //*** The master key didn't match, so check the stored application keys.
        ApplicationManager appManager = new ApplicationManager();
        if (!appManager.isValidKey(clientKey)) {
          return false;
        }
      }
    }
  return true;
  }

  /**
   * Get export file blob key from the database on demand filled with export data.
   * @param exportData String data that will be exported
   * @return fileBlobKey BlobKey export file blob key
   */
  public BlobKey doGetBlobKey(String exportData) throws IOException, FileNotFoundException, JSONException {
    FileService fileService = FileServiceFactory.getFileService();
    AppEngineFile file = fileService.createNewBlobFile("text/html", "export.json");
    boolean lock = true;
    FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
    PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
    out.println(exportData);
    out.close();
    writeChannel.closeFinally();
    return fileService.getBlobKey(file);
  }
}
