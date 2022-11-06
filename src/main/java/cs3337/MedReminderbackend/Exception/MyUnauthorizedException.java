package cs3337.MedReminderbackend.Exception;


/**
 * HTTP Status 401
 */
public class MyUnauthorizedException extends RuntimeException
{
    
    public MyUnauthorizedException()
    {
        super();
    }
    
    public MyUnauthorizedException(String message)
    {
        super(message);
    }
    
}
