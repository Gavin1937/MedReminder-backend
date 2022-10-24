package cs3337.MedReminderbackend.Exception;


/**
 * HTTP Status 404
 */
public class MyNotFoundException extends RuntimeException
{
    
    public MyNotFoundException()
    {
        super();
    }
    
    public MyNotFoundException(String message)
    {
        super(message);
    }
    
}
