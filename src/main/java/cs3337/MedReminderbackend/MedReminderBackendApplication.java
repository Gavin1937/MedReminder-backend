package cs3337.MedReminderbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cs3337.MedReminderbackend.Util.ConfigManager;


@SpringBootApplication
public class MedReminderBackendApplication
{
    
    public static void main(String[] args)
    {
        
        // try to init config & logger
        try
        {
            if (args.length >= 1)
            {
                config.readConfig(args[0]);
            }
            else // try to read config from current directory
            {
                config.readConfig("./config.json");
            }
        }
        catch (Exception e)
        {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
        }
        
        
        // init spring boot application
        SpringApplication.run(MedReminderBackendApplication.class, args);
        
    }
    
    private static ConfigManager config = ConfigManager.getInstance();
}
