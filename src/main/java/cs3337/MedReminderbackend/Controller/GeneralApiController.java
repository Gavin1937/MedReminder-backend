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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    
    
    // testing api
    
    /** <p><code>GET /api/hello</code></p>
     * 
     * This is a testing endpoint.
     * 
     * @return
     *  This endpoint will return a single string: "Hello: " + current time
     */
    @GetMapping(value="/hello")
    public ResponseEntity<Object> hello()
    {
        Date today = new Date();
        return Utilities.genStrResponse("Hello: " + today.toString(), HttpStatus.OK);
    }
    
    /** <p><code>GET /api/except</code></p>
     * 
     * This is a testing endpoint.
     * 
     * @return
     *  This endpoint will return error message json
     * <pre>
     * {
     *   "ok":false,
     *   "error":"This Is A Bad Request Exception",
     *   "status":400
     * }
     * </pre>
     */
    @GetMapping(value="/except")
    public ResponseEntity<Object> except()
        throws Exception
    {
        throw new MyBadRequestException("This Is A Bad Request Exception");
    }
    
    
    // api
    
    /** <p><code>POST /api/auth</code></p>
     * 
     * Authenticate an user & generate a session for him
     * 
     * <p><strong>content-type: application/json</strong></p>
     * 
     * @param
     *  json post request body
     * <pre>
     * {
     *   "username": str,
     *   "auth_hash": str
     * }
     * </pre>
     * 
     * @return
     *  If success:
     * <pre>
     * {
     *   "payload": {
     *     "user_id": int,
     *     "expire": int unix timestamp,
     *     "secret": str
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     * 
     *  If failed
     * <pre>
     * {
     *   "ok": false,
     *   "error": str error message,
     *   "status": 400
     * }
     * </pre>
     */
    @PostMapping(value="/auth", consumes="application/json")
    public ResponseEntity<Object> doAuth(
        HttpServletRequest request, HttpServletResponse response,
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
        
        Utilities.logReqResp("info", request, authResult);
        return Utilities.genOkRespnse(authResult);
    }
    
    
    // private members
    private static MedReminderDB mrdb = MedReminderDB.getInstance();
    
}
