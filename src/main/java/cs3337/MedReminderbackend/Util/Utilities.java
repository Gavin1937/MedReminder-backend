package cs3337.MedReminderbackend.Util;

import java.time.Instant;
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
    
    public static int getUnixTimestampNow()
    {
        return (int)(Instant.now().toEpochMilli()/1000);
    }
    
    public static ResponseEntity<Object> genJsonResponse(JSONObject response, HttpStatus status)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new ResponseEntity<Object>(response.toString(), headers, status);
    }
    
    public static ResponseEntity<Object> genJsonResponse(JSONArray response, HttpStatus status)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new ResponseEntity<Object>(response.toString(), headers, status);
    }
    
    public static ResponseEntity<Object> genStrResponse(String response, HttpStatus status)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/plain");
        return new ResponseEntity<Object>(response, headers, status);
    }
    
}
