package cs3337.MedReminderbackend.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import static cs3337.MedReminderbackend.Util.Types.strToLogicalOperators;
import static cs3337.MedReminderbackend.Util.Types.strToSortOrder;

import cs3337.MedReminderbackend.DB.MedReminderDB;
import cs3337.MedReminderbackend.Model.Medication;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.Util.Utilities;
import cs3337.MedReminderbackend.Util.Types.LogicalOperators;
import cs3337.MedReminderbackend.Util.Types.Operations;
import cs3337.MedReminderbackend.Util.Types.SortOrder;
import cs3337.MedReminderbackend.Exception.*;


@RestController
@RequestMapping("/api/medication")
public class MedicationApiController
{
    
    public MedicationApiController()
    {
        MyLogger.info("Construct MedicationApiController.");
    }
    
    
    // api
    
    /** <p><code>GET /api/medication/{id}</code></p>
     * 
     * Get medication info by id.
     * 
     * <pre>
     * Operation Type:
     * DOCTOR_READ or PATIENT_READ
     * </pre>
     * 
     * @param
     *  username string username in request header
     * 
     * @param
     *  secret string user secret in request header
     * 
     * @param
     *  id [Path Parameter] Integer id of Medication
     * 
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": {
     *     "id": int,
     *     "name": str,
     *     "description": str,
     *     "frequency": int,
     *     "early_time": int,
     *     "late_time": int
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @GetMapping(value="/{id}")
    public ResponseEntity<Object> getMedication(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @PathVariable("id") Integer id
    )
    {
        // validate user operation
        Operations[] ops = {Operations.DOCTOR_READ, Operations.PATIENT_READ};
        boolean valid = mrdb.validateOperationsOr(
            username, secret,
            ops
        );
        if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        
        Medication medication = mrdb.getMedication(id);
        if (medication == null)
            throw new MyBadRequestException("Cannot find medication with specified id.");
        
        JSONObject output = medication.toJson();
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>POST /api/medication</code></p>
     * 
     * Add new medication to system
     * 
     * <p><strong>Content-Type: application/json</strong></p>
     * 
     * <pre>
     * Operation Type:
     * DOCTOR_WRITE
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
     *   "name": str,
     *   "description": str,
     *   "frequency": int,
     *   "early_time": int,
     *   "late_time": int
     * }
     * </pre>
     * 
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": {
     *     "id": int
     *   },
     *   "ok": true,
     *   "status": 200
     * }
     * </pre>
     */
    @PostMapping(consumes="application/json")
    public ResponseEntity<Object> addMedication(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @RequestBody String data
    )
    {
        // validate user operation
        boolean valid = mrdb.validateOperationSingle(
            username, secret,
            Operations.DOCTOR_WRITE
        );
        if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        
        JSONObject json = null;
        Integer id = -1;
        try
        {
            json = new JSONObject(data);
            id = mrdb.addMedication(
                json.getString("name"), json.getString("description"),
                json.getInt("frequency"),
                json.getInt("early_time"), json.getInt("late_time")
            );
            if (id <= 0)
                throw new MyBadRequestException(
                    "Cannot add medication, this may because input medication info already in database."
                );
        }
        catch (Exception e)
        {
            throw new MyBadRequestException(
                "Invalid request parameter. Missing json key? Wrong parameter?"
            );
        }
        
        JSONObject output = new JSONObject();
        output.put("id", id);
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>GET /api/medication/find</code></p>
     * 
     * Find medication info by supplied parameters.
     * 
     * <pre>
     * Operation Type:
     * DOCTOR_READ
     * </pre>
     * 
     * @param
     *  username string username in request header
     * 
     * @param
     *  secret string user secret in request header
     * 
     * @param
     *  name [Request Query] String medication name
     * 
     * @param
     *  frequency [Request Query] Integer medication frequency
     * 
     * @param
     *  early_time [Request Query] Integer medication early time
     * 
     * @param
     *  late_time [Request Query] Integer medication late time
     * 
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": {
     *     "id": int,
     *     "name": str,
     *     "description": str,
     *     "frequency": int,
     *     "early_time": int,
     *     "late_time": int
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @GetMapping(value="/find")
    public ResponseEntity<Object> findMedication(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @RequestParam(value="name") String name,
        @RequestParam(value="frequency") Integer frequency,
        @RequestParam(value="early_time") Integer early_time,
        @RequestParam(value="late_time") Integer late_time
    )
    {
        // validate user operation
        boolean valid = mrdb.validateOperationSingle(
            username, secret,
            Operations.DOCTOR_READ
        );
        if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        
        Medication medication = null;
        try
        {
            medication = mrdb.searchMedication(
                name, frequency,
                early_time, late_time
            );
            if (medication == null)
                throw new MyBadRequestException("Cannot find medication with supplied parameter.");
        }
        catch (MyBadRequestException be)
        {
            throw be;
        }
        catch (Exception e)
        {
            throw new MyBadRequestException(
                "Invalid request parameter. Missing json key? Wrong parameter?"
            );
        }
        
        JSONObject output = medication.toJson();
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>GET /api/medication/history</code></p>
     * 
     * Find user's medication history by supplied parameters.
     * 
     * <pre>
     * Operation Type:
     * DOCTOR_READ or PATIENT_READ
     * User can only check his own history or other users who have lower role.
     * </pre>
     * 
     * <pre>
     * Logical Operators (Case Insensitive):
     * | Operation                 | String Operator |
     * |---------------------------|-----------------|
     * | Equal                     | =, ==, eq       |
     * | Not Equal                 | !=, ne          |
     * | Greater Than              | >, gt           |
     * | Greater Than and Equal To | >=, gte         |
     * | Less Than                 | <, lt           |
     * | Less Than and Equal To    | <=, lte         |
     * </pre>
     * 
     * 
     * @param
     *  username string username in request header
     * 
     * @param
     *  secret string user secret in request header
     * 
     * @param
     *   user_id [Request Query] Integer user id
     * 
     * @param
     *   med_id [Request Query] Integer medication id
     * 
     * @param
     *   med_id_opt [Request Query] String logical comparison operators to use with med_id
     * 
     * @param
     *   time [Optional][Request Query] Integer unix timestamp (default now)
     * 
     * @param
     *   time_opt [Optional (MUST come with "time")][Request Query] String logical comparison operators to use with time (default lte)
     * 
     * @param
     *   sort_order [Optional][Request Query] String "asc" or "desc" (default "asc")
     * 
     * @param
     *   limit [Optional][Request Query] Integer >= 0 int limit of result array size, -1 => query all (default 50)
     * 
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": [
     *     {
     *       "id": int,
     *       "user_id": int,
     *       "med_id": int,
     *       "med_time": int
     *     },
     *     ...
     *   ],
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @GetMapping(value="/history")
    public ResponseEntity<Object> getMedHistory(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @RequestParam(value="user_id", required=true) Integer user_id,
        @RequestParam(value="med_id", required=true) Integer med_id,
        @RequestParam(value="med_id_opt", required=true) String med_id_opt,
        @RequestParam(value="time", required=false) Integer time,
        @RequestParam(value="time_opt", required=false) String time_opt,
        @RequestParam(value="sort_order", required=false) String sort_order,
        @RequestParam(value="limit", required=false) Integer limit
    )
    {
        // validate user operation
        Operations[] ops = {Operations.DOCTOR_READ, Operations.PATIENT_READ};
        boolean valid = mrdb.validateOperationsOr(
            username, secret,
            user_id, "alleq",
            ops
        );
        if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        
        JSONArray output = null;
        try
        {
            // determine special case
            boolean hasTime = (time != null);
            boolean hasTimeOpt = (time_opt != null);
            if (hasTime != hasTimeOpt) // only have one param "time" or "time_opt"
                throw new MyBadRequestException("Request Query \"time\" MUST come with \"time_opt\".");
            
            // set default value
            if (hasTime == false && hasTimeOpt == false)
            {
                time = Utilities.getUnixTimestampNow();
                time_opt = "lte";
            }
            if (sort_order == null)
            {
                sort_order = "asc";
            }
            if (limit == null)
            {
                limit = 50;
            }
            
            // query medication history
            LogicalOperators medIdOpt = strToLogicalOperators(med_id_opt);
            LogicalOperators timeOpt = strToLogicalOperators(time_opt);
            SortOrder sort = strToSortOrder(sort_order);
            output = mrdb.queryMedHistory(
                user_id,
                med_id, medIdOpt,
                time, timeOpt,
                sort, limit
            );
        }
        catch (MyBadRequestException be)
        {
            throw be;
        }
        catch (Exception e)
        {
            throw new MyBadRequestException(
                "Invalid request parameter. Missing json key? Wrong parameter?"
            );
        }
        
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>PUT /api/medication/history</code></p>
     * 
     * Update user's medication history by supplied parameters.
     * 
     * <p><strong>Content-Type: application/json</strong></p>
     * 
     * <pre>
     * Operation Type:
     * PATIENT_WRITE
     * User can only update his own history.
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
     *     "user_id": int,
     *     "med_id": int,
     *     "time": int
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @PutMapping(value="/history", consumes="application/json")
    public ResponseEntity<Object> updateMedHistory(
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
            Operations.PATIENT_WRITE
        );
        if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        
        try
        {
            boolean result = mrdb.updateMedHistory(
                json.getInt("user_id"),
                json.getInt("med_id")
            );
            if (result == false)
                throw new MyBadRequestException("Cannot update user's medication history.");
        }
        catch (Exception e)
        {
            throw new MyBadRequestException(
                "Invalid request parameter. Missing json key? Wrong parameter?"
            );
        }
        
        JSONObject output = new JSONObject();
        output.put("user_id", json.getInt("user_id"));
        output.put("med_id", json.getInt("med_id"));
        output.put("time", Utilities.getUnixTimestampNow());
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    
    // private members
    private static MedReminderDB mrdb = MedReminderDB.getInstance();
    
}
