package cs3337.MedReminderbackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import cs3337.MedReminderbackend.Util.ConfigManager;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.DB.MedReminderDB;


@ExtendWith(SpringExtension.class)
public class MedReminderDBTests
{
    
    private static ConfigManager config = ConfigManager.getInstance();
    private MedReminderDB mrdb = MedReminderDB.getInstance();
    
    @BeforeAll
    public static void setup()
        throws Exception
    {
        config.loadConfig("./data/test_config.json");
        MyLogger.init(config.getLogFilePath(), config.getLoggingLevel());
        MyLogger.info("In MedReminderDBTests");
    }
    
    @Test
    @Order(1)
    void initTest()
        throws Exception
    {
        mrdb.init(
            config.getDBIp(),
            config.getMedReminderDbName(),
            config.getDBUsername(),
            config.getDBPwd()
        );
    }
    
}
