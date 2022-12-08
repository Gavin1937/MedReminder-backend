package cs3337.MedReminderbackend.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import cs3337.MedReminderbackend.DB.MedReminderDB;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.Util.Utilities;
import cs3337.MedReminderbackend.Util.Types.Operations;
import cs3337.MedReminderbackend.Exception.*;
import cs3337.MedReminderbackend.Model.Users;


@RestController
@RequestMapping("/api/notification")
public class NotificationApiController
{
    
    public NotificationApiController()
    {
        MyLogger.info("Construct NotificationApiController.");
    }
    
    
    // api
    
    /** <p><code>GET /api/notification</code></p>
     * 
     * Get user's notification information.
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
    @GetMapping()
    public ResponseEntity<Object> getNotiInfo(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret
    )
    {
        // validate user operation
        Users user = mrdb.getUserBySecret(secret);
        boolean valid = mrdb.validateOperationSingle(
            username, secret,
            user.getId(), "alleq",
            Operations.PATIENT_READ
        );
        if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        
        JSONObject output = mrdb.queryNotiInfo(
            user.getId(), user.getMedId()
        );
        if (output == null)
            throw new MyBadRequestException("Cannot find any notification information.");
        
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    
    // private members
    private static MedReminderDB mrdb = MedReminderDB.getInstance();
    
}
