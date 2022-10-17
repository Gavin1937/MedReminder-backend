package cs3337.MedReminderbackend.Util;

import java.time.Instant;


/**
 * Collection of Utilities as static functions
 */
public class Utilities
{
    
    public static int getUnixTimestampNow()
    {
        return (int)(Instant.now().toEpochMilli()/1000);
    }
    
}
