package cs3337.MedReminderbackend.Exception;

import org.json.JSONObject;
import ch.qos.logback.classic.Level;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.beans.TypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import cs3337.MedReminderbackend.Util.ConfigManager;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.Util.Utilities;


@RestControllerAdvice
public class RestApiExceptionHandlers
{
    
    // 400 Parameter type mismatch
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Object>
    handleTypeMismatchException(
        Exception e
    )
    {
        MyLogger.debug("Input parameter type mismatch.");
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", "Input parameter type mismatch.");
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    // 400 Custom Throwable
    @ExceptionHandler(MyBadRequestException.class)
    public ResponseEntity<Object>
    handleMyBadRequestException(
        Exception e
    )
    {
        MyLogger.debug(e.getMessage());
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", e.getMessage());
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    // 404 Not Found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object>
    handleNotFound(
        Exception e
    )
    {
        MyLogger.debug(e.getMessage());
        
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = chooseExceptionMsg(
            "Path Not Found: " + request.getServletPath(),
            "Path Not Found: " + e.getMessage()
        );
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", message);
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    // 404 Custom Throwable
    @ExceptionHandler(MyNotFoundException.class)
    public ResponseEntity<Object>
    handleMyNotFoundException(
        Exception e
    )
    {
        MyLogger.debug(e.getMessage());
        
        HttpStatus status = HttpStatus.NOT_FOUND;
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", e.getMessage());
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    // 405 Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object>
    handleNotFound(
        HttpRequestMethodNotSupportedException e
    )
    {
        MyLogger.debug("{} is not one of the supported Http Methods.", e.getMethod());
        
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", e.getMethod() + " is not one of the supported Http Methods.");
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    // 415 Content-Type mismatch
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object>
    handleHttpMediaTypeNotSupportedException(
        Exception e
    )
    {
        MyLogger.debug("Unsupported Content-Type. Expecting: application/json");
        
        HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", "Unsupported Content-Type. Expecting: application/json");
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    // 500 NullPointerException
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object>
    handleNullPointerException(
        NullPointerException e
    )
    {
        MyLogger.warn(e.getMessage());
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = chooseExceptionMsg(
            "Internal Server Error.",
            e.getMessage()
        );
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", message);
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    // 500 Custom Throwable
    @ExceptionHandler(MyInternalServerErrorException.class)
    public ResponseEntity<Object>
    handleMyInternalServerErrorException(
        Exception e
    )
    {
        MyLogger.warn(e.getMessage());
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", e.getMessage());
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    
    // private helper function
    public String chooseExceptionMsg(String infoMsg, String debugMsg)
    {
        if (logLevel.isGreaterOrEqual(Level.INFO))
            return infoMsg;
        else
            return debugMsg;
    }
    
    
    // private member
    private Level logLevel = ConfigManager.getInstance().getLoggingLevel();
    @Autowired HttpServletRequest request;
    
}
