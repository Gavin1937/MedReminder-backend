package cs3337.MedReminderbackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import cs3337.MedReminderbackend.Util.ConfigManager;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.DB.HospitalDB;


@ExtendWith(SpringExtension.class)
public class HospitalDBTests
{
    
    private static ConfigManager config = ConfigManager.getInstance();
    private HospitalDB hdb = HospitalDB.getInstance();
    
    @BeforeAll
    public static void setup()
        throws Exception
    {
        config.loadConfig("./data/test_config.json");
        MyLogger.init(config.getLogFilePath(), config.getLoggingLevel());
        MyLogger.info("In HospitalDBTests");
    }
    
    @Test
    @Order(1)
    void initTest()
        throws Exception
    {
        hdb.init(
            config.getDBIp(),
            config.getHospitalDbName(),
            config.getDBUsername(),
            config.getDBPwd()
        );
    }
    
}
