package cs3337.MedReminderbackend.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
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
import cs3337.MedReminderbackend.Exception.MyBadRequestException;


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
     *  id Integer id of Medication
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
            throw new MyBadRequestException(
                "This user cannot perform current operation or authentication secret incorrect."
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
     * <p><strong>content-type: application/json</strong></p>
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
            throw new MyBadRequestException(
                "This user cannot perform current operation or authentication secret incorrect."
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
            throw new MyBadRequestException("Invalid request parameter. Missing json key?");
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
     * <p><strong>content-type: application/json</strong></p>
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
     *  json get request body
     * <pre>
     * {
     *   "name": str,
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
    @GetMapping(value="/find", consumes="application/json")
    public ResponseEntity<Object> findMedication(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @RequestBody String data
    )
    {
        // validate user operation
        boolean valid = mrdb.validateOperationSingle(
            username, secret,
            Operations.DOCTOR_READ
        );
        if (valid == false)
            throw new MyBadRequestException(
                "This user cannot perform current operation or authentication secret incorrect."
            );
        
        
        Medication medication = null;
        try
        {
            JSONObject json = new JSONObject(data);
            medication = mrdb.findMedication(
                json.getString("name"), json.getInt("frequency"),
                json.getInt("early_time"), json.getInt("late_time")
            );
            if (medication == null)
                throw new MyBadRequestException("Cannot find medication with supplied parameter.");
        }
        catch (Exception e)
        {
            throw new MyBadRequestException("Invalid request parameter. Missing json key?");
        }
        
        JSONObject output = medication.toJson();
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>GET /api/medication/history</code></p>
     * 
     * Find user's medication history by supplied parameters.
     * 
     * <p><strong>content-type: application/json</strong></p>
     * 
     * <pre>
     * Operation Type:
     * DOCTOR_READ or PATIENT_READ
     * User can only check his own history or other users who have lower role.
     * </pre>
     * 
     * @param
     *  username string username in request header
     * 
     * @param
     *  secret string user secret in request header
     * 
     * @param
     *  json get request body
     * <pre>
     * {
     *   "user_id": int,
     *   "med_id": int,
     *   "med_id_opt": str, // logical comparison operators (=,!=,>,>=,<,<=)
     *   "time": int, // unix timestamp
     *   "time_opt": str, // logical comparison operators (=,!=,>,>=,<,<=)
     *   "sort_order": str, // "asc" or "desc", default "asc"
     *   "limit": int // >= 0 int limit of result array size, -1 => query all
     * }
     * </pre>
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
    @GetMapping(value="/history", consumes="application/json")
    public ResponseEntity<Object> getMedHistory(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @RequestBody String data
    )
    {
        // validate user operation
        JSONObject json = new JSONObject(data);
        Operations[] ops = {Operations.DOCTOR_READ, Operations.PATIENT_READ};
        boolean valid = mrdb.validateOperationsOr(
            username, secret,
            json.getInt("user_id"), "alleq",
            ops
        );
        if (valid == false)
            throw new MyBadRequestException(
                "This user cannot perform current operation or authentication secret incorrect."
            );
        
        
        JSONArray output = null;
        try
        {
            LogicalOperators medIdOpt = strToLogicalOperators(json.getString("med_id_opt"));
            LogicalOperators timeOpt = strToLogicalOperators(json.getString("time_opt"));
            SortOrder order = strToSortOrder(json.getString("sort_order"));
            output = mrdb.queryMedHistory(
                json.getInt("user_id"),
                json.getInt("med_id"), medIdOpt,
                json.getInt("time"), timeOpt,
                order,
                json.getInt("limit")
            );
        }
        catch (Exception e)
        {
            throw new MyBadRequestException("Invalid request parameter. Missing json key?");
        }
        
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>PUT /api/medication/history</code></p>
     * 
     * Update user's medication history by supplied parameters.
     * 
     * <p><strong>content-type: application/json</strong></p>
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
            throw new MyBadRequestException(
                "This user cannot perform current operation or authentication secret incorrect."
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
            throw new MyBadRequestException("Invalid request parameter. Missing json key?");
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
