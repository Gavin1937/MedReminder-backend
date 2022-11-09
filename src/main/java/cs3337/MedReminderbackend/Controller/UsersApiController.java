package cs3337.MedReminderbackend.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import static cs3337.MedReminderbackend.Util.Types.strToRoles;

import cs3337.MedReminderbackend.DB.MedReminderDB;
import cs3337.MedReminderbackend.Model.Users;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.Util.Utilities;
import cs3337.MedReminderbackend.Util.Types.Operations;
import cs3337.MedReminderbackend.Util.Types.Roles;
import cs3337.MedReminderbackend.Exception.*;


@RestController
@RequestMapping("/api/user")
public class UsersApiController
{
    
    public UsersApiController()
    {
        MyLogger.info("Construct UsersApiController.");
    }
    
    
    // api
    
    /** <p><code>GET /api/user/{id}</code></p>
     * 
     * Get user info by id.
     * 
     * <pre>
     * Operation Type:
     * ADMIN_READ
     * User can only check himself or other users who have lower role.
     * </pre>
     * 
     * @param
     *  username string username in request header
     * 
     * @param
     *  secret string user secret in request header
     * 
     * @param
     *  id [Path Parameter] Integer id of User
     * 
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": {
     *     "doc_info": { // can be null
     *       "fname": str,
     *       "lname": str,
     *       "phone": str,
     *       "id": int,
     *       "email": str
     *     },
     *     "med_id": int,
     *     "role": str,
     *     "pat_info": { // can be null
     *       "fname": str,
     *       "lname": str,
     *       "phone": str,
     *       "id": int,
     *       "email": str
     *     },
     *     "auth_hash": str,
     *     "id": int,
     *     "username": str
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @GetMapping(value="/{id}")
    public ResponseEntity<Object> getUser(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @PathVariable("id") Integer id
    )
    {
        // validate user operation
        boolean valid = mrdb.validateOperationSingle(
            username, secret,
            id, "eqrole",
            Operations.ADMIN_READ
        );
        if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        
        Users user = mrdb.getUser(id);
        if (user == null)
            throw new MyBadRequestException("Cannot find user with specified id.");
        
        JSONObject output = user.toJson();
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>GET /api/user/me</code></p>
     * 
     * Get user info of current user.
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
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": {
     *     "doc_info": { // can be null
     *       "id": int,
     *       "fname": str,
     *       "lname": str,
     *       "phone": str,
     *       "email": str
     *     },
     *     "med_id": int,
     *     "role": str,
     *     "pat_info": { // can be null
     *       "id": int,
     *       "fname": str,
     *       "lname": str,
     *       "phone": str,
     *       "email": str,
     *       "primary_doc": int
     *     },
     *     "auth_hash": str,
     *     "id": int,
     *     "username": str
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @GetMapping(value="/me")
    public ResponseEntity<Object> getUserMe(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret
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
        
        Users user = mrdb.getUserBySecret(secret);
        if (user == null)
            throw new MyBadRequestException("Cannot find current user information.");
        
        JSONObject output = user.toJson();
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>GET /api/user/doctor/{id}</code></p>
     * 
     * Get doctor user info by id.
     * 
     * <pre>
     * Operation Type:
     * DOCTOR_READ
     * Doctor User can only check himself and other doctors.
     * Also handle special case: patient user find his primary doctor
     * </pre>
     * 
     * @param
     *  username string username in request header
     * 
     * @param
     *  secret string user secret in request header
     * 
     * @param
     *  id [Path Parameter] Integer id of User
     * 
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": {
     *     "doc_info": {
     *       "id": int,
     *       "fname": str,
     *       "lname": str,
     *       "phone": str,
     *       "email": str
     *     },
     *     "med_id": int,
     *     "role": str,
     *     "pat_info": null,
     *     "auth_hash": str,
     *     "id": int,
     *     "username": str
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @GetMapping(value="/doctor/{id}")
    public ResponseEntity<Object> getDoctorUser(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @PathVariable("id") Integer id
    )
    {
        // validate user operation
        Roles userRole = mrdb.getUserRole(secret);
        if (
            mrdb.getUserRole(id).equals(Roles.DOCTOR) != true &&
            mrdb.getUserRole(id).equals(Roles.ADMIN) != true
        )
            throw new MyBadRequestException("Input id isn't a Doctor Id.");
        
        Operations[] ops = {Operations.DOCTOR_READ};
        boolean valid = mrdb.validateOperationsOr(
            username, secret,
            id, "eqrole",
            ops
        );
        // special case, patient find his primary doc
        if (valid == false && userRole.equals(Roles.PATIENT))
        {
            Users primaryDoc = mrdb.getPrimaryDocForUser(id);
            if (primaryDoc == null || primaryDoc.getId() != id)
                throw new MyUnauthorizedException(
                    "This user cannot perform current operation or authentication failed."
                );
            
            JSONObject output = primaryDoc.toJson();
            Utilities.logReqResp("info", request, output);
            return Utilities.genOkRespnse(output);
        }
        else if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        Users user = mrdb.getUser(id);
        if (user == null)
            throw new MyBadRequestException("Cannot find user with specified id.");
        
        JSONObject output = user.toJson();
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>GET /api/user/patient/{id}</code></p>
     * 
     * Get patient user info by id.
     * 
     * <pre>
     * Operation Type:
     * DOCTOR_READ or PATIENT_READ
     * Patient User can only check himself.
     * Doctor User can check all his patients.
     * </pre>
     * 
     * @param
     *  username string username in request header
     * 
     * @param
     *  secret string user secret in request header
     * 
     * @param
     *  id [Path Parameter] Integer id of User
     * 
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": {
     *     "doc_info": null,
     *     "med_id": int,
     *     "role": str,
     *     "pat_info": {
     *       "id": int,
     *       "fname": str,
     *       "lname": str,
     *       "phone": str,
     *       "email": str,
     *       "primary_doc": int
     *     },
     *     "auth_hash": str,
     *     "id": int,
     *     "username": str
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @GetMapping(value="/patient/{id}")
    public ResponseEntity<Object> getPatientUser(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @PathVariable("id") Integer id
    )
    {
        // validate user operation
        if (mrdb.getUserRole(id).equals(Roles.PATIENT) != true)
            throw new MyBadRequestException("Input id isn't a Patient Id.");
        
        Operations[] opt = {Operations.DOCTOR_READ, Operations.PATIENT_READ};
        boolean valid = mrdb.validateOperationsOr(
            username, secret,
            id, "alleq",
            opt
        );
        if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        Users user = mrdb.getUser(id);
        if (user == null)
            throw new MyBadRequestException("Cannot find user with specified id.");
        
        JSONObject output = user.toJson();
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>GET /api/user/mypatients/{page}</code></p>
     * 
     * Get list of patient users belong to current doctor user in users table
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
     *  page [Path Parameter] Integer page of User list (>= 1), each page contains 50 users
     * 
     * @return
     *  If success
     * <pre>
     * {
     *   "payload": {
     *     "patients": [
     *       {
     *         "doc_info": null,
     *         "med_id": int,
     *         "role": str,
     *         "pat_info": {
     *           "id": int,
     *           "fname": str,
     *           "lname": str,
     *           "phone": str,
     *           "email": str,
     *           "primary_doc": int
     *         },
     *         "auth_hash": str,
     *         "id": int,
     *         "username": str
     *       },
     *       ...
     *     ],
     *     "this_page": int,
     *     "next_page": int
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @GetMapping(value="/mypatients/{page}")
    public ResponseEntity<Object> getAllPatientUserOfDoc(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret,
        @PathVariable("page") Integer page
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
        if (page < 1)
            throw new MyBadRequestException(
                "Path parameter \"page\" must greater than or equal to 1."
            );
        
        Integer pageLimit = 50;
        Integer offset = (page-1)*pageLimit;
        Users currentDoc = mrdb.getUserBySecret(secret);
        JSONArray result = mrdb.getAllUsersOfDoc(currentDoc.getId(), pageLimit, offset);
        
        JSONObject output = new JSONObject();
        output.put("patients", result);
        output.put("this_page", page);
        output.put("next_page", page+1);
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>GET /api/user/mydoctor</code></p>
     * 
     * Get primary doctor info of current user (patient).
     * 
     * <pre>
     * Operation Type:
     * PATIENT_READ
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
     *     "doc_info": {
     *       "id": int,
     *       "fname": str,
     *       "lname": str,
     *       "phone": str,
     *       "email": str
     *     },
     *     "med_id": int,
     *     "role": str,
     *     "pat_info": null,
     *     "auth_hash": str,
     *     "id": int,
     *     "username": str
     *   },
     *   "ok": bool,
     *   "status": 200
     * }
     * </pre>
     */
    @GetMapping(value="/mydoctor")
    public ResponseEntity<Object> getMyPrimaryDoc(
        HttpServletRequest request, HttpServletResponse response,
        @RequestHeader("username") String username,
        @RequestHeader("secret") String secret
    )
    {
        // validate user operation
        boolean valid = mrdb.validateOperationSingle(
            username, secret,
            Operations.PATIENT_READ
        );
        if (valid == false)
            throw new MyUnauthorizedException(
                "This user cannot perform current operation or authentication failed."
            );
        
        Users user = mrdb.getUserBySecret(secret);
        if (user == null)
            throw new MyBadRequestException("Cannot find current user information.");
        Users doc = mrdb.getPrimaryDocForUser(user.getPatients().getId());
        if (doc == null)
            throw new MyBadRequestException("Cannot find primary doctor for current user.");
        
        JSONObject output = doc.toJson();
        Utilities.logReqResp("info", request, output);
        return Utilities.genOkRespnse(output);
    }
    
    /** <p><code>POST /api/user</code></p>
     * 
     * Add new user to system
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
     *   "hospital_id": int,
     *   "med_id": int,
     *   "password": str,
     *   "role": str
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
    public ResponseEntity<Object> addUser(
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
        Integer id = null;
        try
        {
            json = new JSONObject(data);
            id = mrdb.addUser(
                json.getInt("hospital_id"),
                json.getInt("med_id"),
                json.getString("password"),
                strToRoles(json.getString("role"))
            );
            if (id <= 0)
                throw new MyBadRequestException(
                    "Cannot add user, this may because input user info already in database."
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
    
    
    // private members
    private static MedReminderDB mrdb = MedReminderDB.getInstance();
    
}
