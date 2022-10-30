package cs3337.MedReminderbackend.Exception;


/**
 * HTTP Status 400
 */
public class MyBadRequestException extends RuntimeException
{
    
    public MyBadRequestException()
    {
        super();
    }
    
    public MyBadRequestException(String message)
    {
        super(message);
    }
    
}
