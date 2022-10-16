package cs3337.MedReminderbackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

import cs3337.MedReminderbackend.Util.ConfigManager;
import cs3337.MedReminderbackend.Util.MyLogger;


@SpringBootTest(classes=MedReminderBackendApplication.class)
// @SpringBootTest
class MedReminderBackendApplicationTests
{
    
    private static ConfigManager config = ConfigManager.getInstance();
    
    @BeforeAll
    public static void setup()
        throws Exception
    {
        config.loadConfig("./data/test_config.json");
        MyLogger.init(config.getLogFilePath(), config.getLoggingLevel());
    }
    
    @Test
    void contextLoads()
    {
    }
    
}
