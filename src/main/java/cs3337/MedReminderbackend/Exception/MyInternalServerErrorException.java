package cs3337.MedReminderbackend.Exception;


/**
 * HTTP Status 500
 */
public class MyInternalServerErrorException extends RuntimeException
{
    
    public MyInternalServerErrorException()
    {
        super();
    }
    
    public MyInternalServerErrorException(String message)
    {
        super(message);
    }
    
}
