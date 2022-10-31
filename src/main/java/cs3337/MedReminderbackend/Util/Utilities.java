package cs3337.MedReminderbackend.Util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * Collection of Utilities as static functions
 */
public class Utilities
{
    
    /**
     * Get Current Unix Timestamp
     * 
     * @return
     *  Integer value of current unix timestamp (precision: seconds)
     */
    public static Integer getUnixTimestampNow()
    {
        return (int)(Instant.now().toEpochMilli()/1000);
    }
    
    /**
     * Generate JSON ResponseEntity
     * 
     * @param
     *  response JSONObject contains all the data of ResponseEntity
     * 
     * @param
     *  status HttpStatus of this ResponseEntity
     * 
     * @return
     *  A ResponseEntity<Object>
     */
    public static ResponseEntity<Object> genJsonResponse(JSONObject response, HttpStatus status)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new ResponseEntity<Object>(response.toString(), headers, status);
    }
    
    /**
     * Generate JSON ResponseEntity
     * 
     * @param
     *  response JSONArray contains all the data of ResponseEntity
     * 
     * @param
     *  status HttpStatus of this ResponseEntity
     * 
     * @return
     *  A ResponseEntity<Object>
     */
    public static ResponseEntity<Object> genJsonResponse(JSONArray response, HttpStatus status)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new ResponseEntity<Object>(response.toString(), headers, status);
    }
    
    /**
     * Generate JSON ResponseEntity
     * 
     * @param
     *  response String contains all the data of ResponseEntity
     * 
     * @param
     *  status HttpStatus of this ResponseEntity
     * 
     * @return
     *  A ResponseEntity<Object>
     */
    public static ResponseEntity<Object> genStrResponse(String response, HttpStatus status)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/plain");
        return new ResponseEntity<Object>(response, headers, status);
    }
    
    /**
     * Generate a random String with length defined by parameter
     * 
     * @param
     *  length Integer String length
     * 
     * @return
     *  A Random String length defined by parameter
     */
    public static String genSecret(Integer length)
    {
        Random generator = new Random();
        Integer upperBound = 26+26+10;
        String returnstr = "";
        
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String possibleCharacters = lowerCase+upperCase+numbers;
        
        for (Integer counter = 0; counter<length; counter++)
        {
            Integer random = generator.nextInt(upperBound);
            char character = possibleCharacters.charAt(random);
            returnstr+=character;
        }
        
        return returnstr;
    }
    
    /**
     * Generate a random String with length = 32
     * 
     * @return
     *  A Random String length = 32
     */
    public static String genSecret()
    {
        int length = 32;
        Random generator = new Random();
        Integer upperBound = 26+26+10;
        String returnstr = "";
        
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String possibleCharacters = lowerCase+upperCase+numbers;
        
        for (int counter = 0; counter<length; counter++)
        {
            int random = generator.nextInt(upperBound);
            char character = possibleCharacters.charAt(random);
            returnstr+=character;
        }
        
        return returnstr;
    }
    
    /**
     * Get MD5 hash of input data
     * 
     * @param
     *  data byte[] to hash
     * 
     * @return
     *  if success, return hashed md5 String, otherwise return null 
     */
    public static String getMD5(byte[] data)
    {
        String output = null;
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] arr = md5.digest(data);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < arr.length; ++i)
            {
                sb.append(
                    Integer.toHexString((arr[i] & 0xFF) | 0x100)
                    .substring(1,3)
                );
            }
            output = sb.toString();
        }
        catch (Exception e)
        {
            return null;
        }
        return output;
    }
    
    /**
     * Get MD5 hash of input data
     * 
     * @param
     *  data String to hash
     * 
     * @param
     *  encoding String encoding of data
     * 
     * @return
     *  if success, return hashed md5 String, otherwise return null 
     */
    public static String getMD5(String data, String encoding)
    {
        return getMD5(data.getBytes(Charset.forName(encoding)));
    }
    
    /**
     * Get MD5 hash of input data
     * 
     * @param
     *  data String to hash, default encoding is ASCII
     * 
     * @return
     *  if success, return hashed md5 String, otherwise return null 
     */
    public static String getMD5(String data)
    {
        return getMD5(data.getBytes(Charset.forName("ASCII")));
    }
    
}
