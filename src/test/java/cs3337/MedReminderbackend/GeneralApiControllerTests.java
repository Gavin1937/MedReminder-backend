package cs3337.MedReminderbackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;

import cs3337.MedReminderbackend.Controller.GeneralApiController;
import cs3337.MedReminderbackend.Util.ConfigManager;
import cs3337.MedReminderbackend.Util.MyLogger;


@ExtendWith(SpringExtension.class)
@WebMvcTest(value=GeneralApiController.class)
class GeneralApiControllerTests
{
    
    private static ConfigManager config = ConfigManager.getInstance();
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeAll
    public static void setup()
        throws Exception
    {
        config.loadConfig("./data/test_config.json");
        MyLogger.init(config.getLogFilePath(), config.getLoggingLevel());
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
        assertTrue(response.getStatus() == 200);
        assertTrue(response.getContentType() != null);
        assertTrue(response.getContentType().equals("text/plain"));
        assertTrue(response.getContentAsString().contains("Hello: "));
    }
    
}
