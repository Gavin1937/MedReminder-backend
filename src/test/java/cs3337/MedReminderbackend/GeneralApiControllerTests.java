package cs3337.MedReminderbackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.http.HttpRequest;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import cs3337.MedReminderbackend.Controller.GeneralApiController;
import cs3337.MedReminderbackend.DB.HospitalDB;
import cs3337.MedReminderbackend.DB.MedReminderDB;
import cs3337.MedReminderbackend.Util.ConfigManager;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.Util.Utilities;


@ExtendWith(SpringExtension.class)
@WebMvcTest(value=GeneralApiController.class)
class GeneralApiControllerTests
{
    
    private static ConfigManager config = ConfigManager.getInstance();
    private static HospitalDB hdb = HospitalDB.getInstance();
    private static MedReminderDB mrdb = MedReminderDB.getInstance();
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeAll
    public static void setup()
        throws Exception
    {
        config.loadConfig("./data/test_config.json");
        MyLogger.init(config.getLogFilePath(), config.getLoggingLevel());
        hdb.init(
            config.getDBIp(),
            config.getHospitalDbName(),
            config.getDBUsername(),
            config.getDBPwd()
        );
        mrdb.init(
            config.getDBIp(),
            config.getMedReminderDbName(),
            config.getDBUsername(),
            config.getDBPwd()
        );
        MyLogger.info("In GeneralApiControllerTests");
    }
    
    @Test
    void helloTest()
        throws Exception
    {
        RequestBuilder requestBuilder = 
            MockMvcRequestBuilders.get("/api/hello")
        ;
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        
        MockHttpServletResponse response = result.getResponse();
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertNotEquals(response.getContentType(), null);
        assertEquals(response.getContentType(), "text/plain");
        assertTrue(response.getContentAsString().contains("Hello: "));
    }
    
    @Test
    void exceptTest()
        throws Exception
    {
        RequestBuilder requestBuilder = 
            MockMvcRequestBuilders.get("/api/except")
        ;
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        
        MockHttpServletResponse response = result.getResponse();
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
        assertNotEquals(response.getContentType(), null);
        assertEquals(response.getContentType(), "application/json");
        JSONObject obj = new JSONObject(response.getContentAsString());
        assertFalse(obj.getBoolean("ok"));
        assertEquals(obj.getString("error"), "This Is A Bad Request Exception");
        assertEquals(obj.getInt("status"), HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    void doAuthTest()
        throws Exception
    {
        JSONObject postData = new JSONObject();
        postData.put("username", "gguo1");
        postData.put("auth_hash", "e62ca17bbe7d9c712a3f17b971db3301");
        RequestBuilder requestBuilder = 
            MockMvcRequestBuilders.post("/api/auth")
            .contentType("application/json")
            .content(postData.toString())
        ;
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        
        MockHttpServletResponse response = result.getResponse();
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertNotEquals(response.getContentType(), null);
        assertEquals(response.getContentType(), "application/json");
        JSONObject obj = new JSONObject(response.getContentAsString());
        assertTrue(obj.getBoolean("ok"));
        assertEquals(obj.getInt("status"), HttpStatus.OK.value());
        assertEquals(obj.getInt("user_id"), 1);
        assertTrue(obj.getInt("expire") > Utilities.getUnixTimestampNow());
        assertEquals(obj.getString("secret").length(), 32);
    }
    
}
