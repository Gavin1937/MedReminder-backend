package cs3337.MedReminderbackend.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import cs3337.MedReminderbackend.DB.MedReminderDB;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.Util.Utilities;
import cs3337.MedReminderbackend.Util.Types.Operations;
import cs3337.MedReminderbackend.Exception.MyBadRequestException;


@RestController
@RequestMapping("/api/notification")
public class NotificationApiController
{
    
    public NotificationApiController()
    {
        MyLogger.info("Construct NotificationApiController.");
    }
    
    
    // api
    
    /** <p><code>POST /api/notification</code></p>
     * 
     * Get user's notification information.
     * 
     * <p><strong>Content-Type: application/json</strong></p>
     * 
     * <pre>
     * Operation Type:
     * PATIENT_READ
     * User can only get his own notification.
     * </pre>
     * 
     * @param
     *  username string username in request header
     * 
     * @param
     *  secret string user secret in request header
     * 
     * @param
     *  json post request body
     * <pre>
     * {
     *   "user_id": int,
     *   "med_id": int
     * }
     * </pre>
     * 
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": {
     *     "last_medication_time": int,
     *     "frequency": int,
     *     "earyly_time": int,
     *     "late_time": int
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @PostMapping(consumes="application/json")
    public ResponseEntity<Object> getNotiInfo(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @RequestBody String data
    )
    {
        // validate user operation
        JSONObject json = new JSONObject(data);
        boolean valid = mrdb.validateOperationSingle(
            username, secret,
            json.getInt("user_id"), "alleq",
            Operations.PATIENT_READ
        );
        if (valid == false)
            throw new MyBadRequestException(
                "This user cannot perform current operation or authentication secret incorrect."
            );
        
        
        JSONObject output = mrdb.queryNotiInfo(
            json.getInt("user_id"),
            json.getInt("med_id")
        );
        if (output == null)
            throw new MyBadRequestException("Cannot find any notification information.");
        
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    
    // private members
    private static MedReminderDB mrdb = MedReminderDB.getInstance();
    
}
