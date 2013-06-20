package org.haodev.textbin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import javax.xml.bind.DatatypeConverter;

/**
 * Retrieves and Decodes string from TextBin
 *
 * @author yuhao93@gmail.com
 */
public class TextBin{
  private TextBin(){ }

  /**
   * Encrypts a string with key
   *
   * @param str cleartext to encrypt
   * @param password key to use to encrypt
   * @return encrypted string
   */
  public static String encryptString(String str, String password){
    try{
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(hash(password).getBytes("UTF-8"), "AES");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      return str(cipher.doFinal(str.getBytes("UTF-8")));
    }catch(Exception e){
      e.printStackTrace();
    }
    
    return null;
  }
  
  /**
   * Upload encrypted text to textbin
   *
   * @param password key to encrypt
   * @param key key to bin
   * @param text cleartext to encrypt and then push
   * @return true if push succeeded, false otherwise
   */
  public static boolean pushAndEncryptString(String password, String key, String text){
    String str = encryptString(text, password);
    
    try{
      // Construct data
      String data = "";
      data += String.format("%s=%s", URLEncoder.encode("txt", "UTF-8"), URLEncoder.encode(str, "UTF-8"));
      data += String.format("&%s=%s", URLEncoder.encode("key", "UTF-8"), URLEncoder.encode(key, "UTF-8"));
 
      // Send data
      URL url = new URL("http://textbin.herokuapp.com/send");
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(data);
      wr.flush();
 
      // Get the response
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String res = "";
      String line;
      while ((line = rd.readLine()) != null) {
        res += line;
      }
      wr.close();
      rd.close();
      if(res.equals("done")){
        return true;
      }
    }catch(Exception e){
      e.printStackTrace();
    }
    
    return false;
  }
  
  /**
   * Grab the text from TextBin and decode it 
   *
   * @param password the key to decrypt text
   * @param key The key to the correct bin
   * @return Decrypted string grabbed from TextBin
   */
  public static String getAndDecryptString(String password, String key) throws MalformedURLException{
    try{
      InputStream input = new URL("http://textbin.herokuapp.com/get/" + key).openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(input));
      String line = br.readLine();
      String res = "";
      while(line != null) {
        res += line;
        line = br.readLine();
      }
      br.close();
    
      return decryptString(res, password);
    }catch(IOException e){
      e.printStackTrace();
    }
    
    return null;
  }

  /**
   * Decode the encrypted string
   *
   * @param str The encrypted string
   * @param password The key to decode the encrypted string
   * @return the decrypted string
   */
  public static String decryptString(String str, String password){
    try{
      byte[] b = destr(str);
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(hash(password).getBytes("UTF-8"), "AES");
      cipher.init(Cipher.DECRYPT_MODE, key);
      return new String(cipher.doFinal(b),"UTF-8");
    }catch(Exception e){
      e.printStackTrace();
    }
    
    return null;
  }
  
  // Generate a 16 byte string hash of password
  private static String hash(String str){
    try{
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(str.getBytes());
      
      String s = "";
      for (int i = 0; i < bytes.length; i++) {
        s += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
      }
      
      return s.substring(0, 16);
    }catch(NoSuchAlgorithmException e){
    }
    return null;
  }
  
  // Encode data into base64 string
  private static String str(byte[] b){
    return DatatypeConverter.printBase64Binary(b);
  }
  
  // Decode data into bytes
  private static byte[] destr(String str){
    return DatatypeConverter.parseBase64Binary(str);
  }
}
