/*
 * Copyright 2012 Pug Creative Inc.
 */

package com.medselect.user;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for user functions.
 * @author Mike Gordon
 */
public class UserUtils {
  private DatastoreService ds;

  public UserUtils() {
    ds = DatastoreServiceFactory.getDatastoreService();
  }
  
  public static byte[] encryptPassword(String cipherKey, String password) throws Exception {
    try {
      byte[] key = cipherKey.getBytes();
      //*** Could generate NoSuchAlgorithmException.
      Cipher cipher = Cipher.getInstance("AES");
      SecretKeySpec k = new SecretKeySpec(key, "AES");
      //*** Could generate InvalidKeyException.
      cipher.init(Cipher.ENCRYPT_MODE, k);
      byte[] encryptedPwd = cipher.doFinal(password.getBytes());
      return encryptedPwd;
    } catch (Exception ex) {
      throw new Exception(ex);
    }
  }

  public boolean authenticateUser(Key userKey, String cipherKey, String password) {
    return true;
  }
}
