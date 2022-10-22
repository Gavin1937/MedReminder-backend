package cs3337.MedReminderbackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cs3337.MedReminderbackend.Util.Utilities;


@ExtendWith(SpringExtension.class)
public class UtilitiesTests
{
    
    @Test
    void getUnixTimestampNowTest()
        throws Exception
    {
        Integer time = Utilities.getUnixTimestampNow();
        Integer now = (int)(Instant.now().toEpochMilli()/1000);
        assertTrue(now >= time);
    }
    
    @Test
    void genJsonResponseTest()
        throws Exception
    {
        JSONObject jobj = new JSONObject();
        jobj.put("key", "data");
        JSONArray jarr = new JSONArray();
        jarr.put(jobj);
        
        ResponseEntity<Object> response1 = Utilities.genJsonResponse(jobj, HttpStatus.OK);
        ResponseEntity<Object> response2 = Utilities.genJsonResponse(jarr, HttpStatus.BAD_REQUEST);
        
        String body1 = (String)response1.getBody();
        String body2 = (String)response2.getBody();
        assertTrue(body1.equals("{\"key\":\"data\"}"));
        assertTrue(body2.equals("[{\"key\":\"data\"}]"));
        
        MediaType type1 = response1.getHeaders().getContentType();
        MediaType type2 = response2.getHeaders().getContentType();
        assertTrue(type1.equals(MediaType.APPLICATION_JSON));
        assertTrue(type2.equals(MediaType.APPLICATION_JSON));
        
        HttpStatus status1 = response1.getStatusCode();
        HttpStatus status2 = response2.getStatusCode();
        assertTrue(status1.equals(HttpStatus.OK));
        assertTrue(status2.equals(HttpStatus.BAD_REQUEST));
    }
    
    @Test
    void genStrResponseTest()
        throws Exception
    {
        ResponseEntity<Object> response = Utilities.genStrResponse("hello", HttpStatus.BAD_GATEWAY);
        
        String body = (String)response.getBody();
        assertTrue(body.equals("hello"));
        
        MediaType type = response.getHeaders().getContentType();
        assertTrue(type.equals(MediaType.TEXT_PLAIN));
        
        HttpStatus status = response.getStatusCode();
        assertTrue(status.equals(HttpStatus.BAD_GATEWAY));
    }
    
}
