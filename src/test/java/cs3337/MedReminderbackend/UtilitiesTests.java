package cs3337.MedReminderbackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    
    @Test
    void genSecretTest()
        throws Exception
    {
        Random rng = new Random();
        Integer len2 = rng.nextInt(1, 51);
        
        String s1 = Utilities.genSecret();
        String s2 = Utilities.genSecret(len2);
        String s3 = Utilities.genSecret(32);
        
        // regex checker
        Pattern p1 = Pattern.compile("[a-zA-Z0-9]{32}");
        Pattern p2 = Pattern.compile("[a-zA-Z0-9]{"+Integer.toString(len2)+"}");
        Matcher m1 = p1.matcher(s1);
        Matcher m2 = p2.matcher(s2);
        Matcher m3 = p1.matcher(s3);
        
        assertTrue(m1.matches());
        assertTrue(m2.matches());
        assertTrue(m3.matches());
        assertTrue(s1.length() == s3.length());
    }
    
    @Test
    void getMD5Test()
        throws Exception
    {
        String ascii_data1 = "1234567890";
        String ascii_hash1 = "e807f1fcf82d132f9bb018ca6738a19f";
        String utf8_data2 = "EAJaijfIJfiJJEF@&$Y>fOI!IJIJFO, 中文文字，日本語。";
        String utf8_hash2 = "e37cbe9d2bd93186facc8eb4bb3eec68";
        byte[] raw_data3 = HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d");
        String raw_hash3 = "cf1581bccf6ac60a1e5a2fac57eff66d";
        
        assertEquals(Utilities.getMD5(ascii_data1), ascii_hash1);
        assertEquals(Utilities.getMD5(utf8_data2, "UTF-8"), utf8_hash2);
        assertEquals(Utilities.getMD5(raw_data3), raw_hash3);
    }
    
}
