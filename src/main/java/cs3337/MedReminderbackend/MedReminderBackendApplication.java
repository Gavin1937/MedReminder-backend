package cs3337.MedReminderbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.PostConstruct;
import java.util.Collections;

import cs3337.MedReminderbackend.Util.ConfigManager;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.DB.*;


@SpringBootApplication(scanBasePackages="cs3337.MedReminderbackend")
public class MedReminderBackendApplication
{
    
    public static void main(String[] args)
    {
        try
        {
            // try to init config
            if (args.length >= 1)
            {
                config.loadConfig(args[0]);
            }
            else // try to read config from current directory
            {
                config.loadConfig("./config.json");
            }
            
            // try to init logger
            MyLogger.init(config.getLogFilePath(), config.getLoggingLevel());
            
            // TODO: try to init hospital db connection
            
            // try to init med_reminder db connection
            mrdb.init(
                config.getDBIp(),
                config.getMedReminderTableName(),
                config.getDBUsername(),
                config.getDBPwd()
            );
            
        }
        catch (Exception e)
        {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
        }
        
        
        // init spring boot application
        MyLogger.info("Finish configuration, starting spring application...");
        MyLogger.info("Logging Level: [{}]", config.getLoggingLevelStr());
        MyLogger.info("Start Server On Port: [{}]", config.getServerPort());
        
        SpringApplication app =
        new SpringApplication(MedReminderBackendApplication.class);
        app.setDefaultProperties(
            Collections.singletonMap(
                "server.port", config.getServerPort().toString()
            )
        );
        app.run(args);
    }
    
    @PostConstruct
    public void postInit()
    {
        MyLogger.init(config.getLogFilePath(), config.getLoggingLevel());
    }
    
    
    private static ConfigManager config = ConfigManager.getInstance();
    private static MedReminderDB mrdb = MedReminderDB.getInstance();
    
}
