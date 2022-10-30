package cs3337.MedReminderbackend.Exception;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cs3337.MedReminderbackend.Util.Utilities;



@RestControllerAdvice
public class ExceptionHandlers
{
    
    // 400
    @ExceptionHandler(MyBadRequestException.class)
    public ResponseEntity<Object> handleMyBadRequestException(
        Exception e
    )
    {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", e.getMessage());
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    // 404
    @ExceptionHandler(MyNotFoundException.class)
    public ResponseEntity<Object> handleMyNotFoundException(
        Exception e
    )
    {
        HttpStatus status = HttpStatus.NOT_FOUND;
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", e.getMessage());
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
    // 500
    @ExceptionHandler(MyInternalServerErrorException.class)
    public ResponseEntity<Object> handleMyInternalServerErrorException(
        Exception e
    )
    {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        JSONObject response = new JSONObject();
        response.put("ok", false);
        response.put("error", e.getMessage());
        response.put("status", status.value());
        
        return Utilities.genJsonResponse(response, status);
    }
    
}
