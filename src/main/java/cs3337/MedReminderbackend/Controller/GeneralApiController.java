package cs3337.MedReminderbackend.Controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import cs3337.MedReminderbackend.DB.MedReminderDB;
import cs3337.MedReminderbackend.Exception.MyBadRequestException;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.Util.Utilities;


@RestController
@RequestMapping("/api")
public class GeneralApiController
{
    
    public GeneralApiController()
    {
        MyLogger.info("Construct GeneralApiController.");
    }
    
    
    // api
    
    /** <p>GET /api/hello</p>
     * 
     * This is a testing endpoint.
     * 
     * @return
     *  This endpoint will return a single string: "Hello: " + current time
     */
    @GetMapping("/hello")
    public ResponseEntity<Object> hello()
    {
        Date today = new Date();
        return Utilities.genStrResponse("Hello: " + today.toString(), HttpStatus.OK);
    }
    
    @GetMapping("/except")
    public ResponseEntity<Object> except()
        throws Exception
    {
        throw new MyBadRequestException("This Is A Bad Request Exception");
    }
    
    
    @PostMapping(value="/auth", consumes="application/json")
    public ResponseEntity<Object> doAuth(
        @RequestBody String data
    )
    {
        MyLogger.debug("doAuth(): data = {}", data);
        JSONObject authResult = null;
        try
        {
            JSONObject json = new JSONObject(data);
            if ((json.has("username") && json.has("auth_hash")) == false)
                throw new MyBadRequestException("Missing json key in request body.");
            
            authResult = mrdb.authUser(json.getString("username"), json.getString("auth_hash"));
            if (authResult == null)
                throw new MyBadRequestException("Authentication Fail.");
        }
        catch (MyBadRequestException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            MyLogger.debug("doAuth(): Exception e = {}", e.getMessage());
            throw new MyBadRequestException("Authentication Fail.");
        }
        
        authResult.put("ok", true);
        authResult.put("status", 200);
        MyLogger.info("doAuth(): authResult = {}", authResult);
        return Utilities.genJsonResponse(authResult, HttpStatus.OK);
    }
    
    
    // private members
    private static MedReminderDB mrdb = MedReminderDB.getInstance();
    
}
