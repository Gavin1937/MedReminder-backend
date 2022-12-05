package cs3337.MedReminderbackend.DB;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

import static cs3337.MedReminderbackend.Util.Types.logicalOperatorsToStr;
import static cs3337.MedReminderbackend.Util.Types.sortOrderToStr;
import static cs3337.MedReminderbackend.Util.Types.roleToStr;
import static cs3337.MedReminderbackend.Util.Types.strToRoles;

import cs3337.MedReminderbackend.Util.ConfigManager;
import cs3337.MedReminderbackend.Util.MyLogger;
import cs3337.MedReminderbackend.Util.Utilities;
import cs3337.MedReminderbackend.Util.Types.LogicalOperators;
import cs3337.MedReminderbackend.Util.Types.SortOrder;
import cs3337.MedReminderbackend.Util.Types.Roles;
import cs3337.MedReminderbackend.Util.Types.Operations;
import cs3337.MedReminderbackend.Model.Patients;
import cs3337.MedReminderbackend.Model.Doctors;
import cs3337.MedReminderbackend.Model.Medication;
import cs3337.MedReminderbackend.Model.Users;


public class MedReminderDB
{
    
    public static MedReminderDB getInstance()
    {
        if (instance == null)
            instance = new MedReminderDB();
        
        return instance;
    }
    
    public void init(
        String ip, String dbName,
        String username, String password
    ) throws SQLException
    {
        // set username & password via getConnection,
        // so JDBC can handle not urlencoded characters inside
        String connectStr = "jdbc:mysql://" + ip + "/" + dbName;
        conn = DriverManager.getConnection(connectStr, username, password);
        
        hdb = HospitalDB.getInstance();
    }
    
    public void finalize() throws SQLException
    {
        if (conn != null)
        {
            conn.close();
        }
        hdb = null;
    }
    
    public Users getUser(Integer id)
    {
        Users output = null;
        Patients pat = null;
        Doctors doc = null;
        try
        {
            String sql = "SELECT * FROM users WHERE id = ?;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setInt(1, id);
            ResultSet rs = select.executeQuery();
            if (rs.next())
            {
                Integer hospital_id = rs.getInt(2);
                Roles role = strToRoles(rs.getString(6));
                switch (role)
                {
                case DOCTOR: case ADMIN:
                    doc = hdb.getDoctors(hospital_id);
                    break;
                case PATIENT:
                    pat = hdb.getPatients(hospital_id);
                    break;
                case NOROLE:
                    throw new SQLException("Invalid role");
                }
                if (doc == null && pat == null)
                    throw new SQLException("Invalid id");
                output = new Users(
                    rs.getInt(1),
                    doc, pat,
                    rs.getInt(3),
                    rs.getString(4), rs.getString(5),
                    role
                );
            }
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        return output;
    }
    
    public Roles getUserRole(Integer id)
    {
        Users user = getUser(id);
        if (user == null)
            return Roles.NOROLE;
        return user.getRole();
    }
    
    public Roles getUserRole(String secret)
    {
        Roles output = Roles.NOROLE;
        try
        {
            String sql = "SELECT user_id FROM authed_user WHERE secret = ?;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, secret);
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                output = getUser(rs.getInt(1)).getRole();
            }
            select.close();
        }
        catch (Exception e)
        {
            return Roles.NOROLE;
        }
        return output;
    }
    
    public Users getPrimaryDocForUser(Integer patientId)
    {
        Patients patient = hdb.getPatients(patientId);
        Users output = null;
        try
        {
            String sql = "SELECT * FROM users WHERE hospital_id = ? AND (role = 'doctor' OR role = 'admin')";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setInt(1, patient.getPrimaryDoc());
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                Integer hospital_id = rs.getInt(2);
                Roles role = strToRoles(rs.getString(6));
                Doctors doc = hdb.getDoctors(hospital_id);
                if (doc == null)
                    throw new SQLException("Invalid id");
                output = new Users(
                    rs.getInt(1),
                    doc, null,
                    rs.getInt(3),
                    rs.getString(4), rs.getString(5),
                    role
                );
            }
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        
        return output;
    }
    
    public Integer addUser(Integer hospitalId, Integer medId, String password, Roles role)
    {
        // preprocessing
        if (password.length() > 40)
            password = password.substring(0, 40);
        
        String fname = null;
        String lname = null;
        String username = "";
        String authHash = "";
        
        // generate username
        try
        {
            switch (role)
            {
            case DOCTOR: case ADMIN:
                Doctors doc = hdb.getDoctors(hospitalId);
                fname = doc.getFname();
                lname = doc.getLname();
                break;
            case PATIENT:
                Patients pat = hdb.getPatients(hospitalId);
                fname = pat.getFname();
                lname = pat.getLname();
                break;
            case NOROLE:
                throw new SQLException("Invalid role");
            }
            if (fname == null || lname == null)
                throw new SQLException("Invalid hospitalId");
            fname = fname.toLowerCase();
            username += fname.charAt(0);
            username += lname.toLowerCase();
            username = findLastValidUsername(username);
        }
        catch (SQLException e)
        {
            return -1;
        }
        
        // generate authHash
        authHash = Utilities.getMD5(username+password);
        if (authHash == null)
            return -1;
        
        // insert
        Integer newId = -1;
        try
        {
            String sql = "INSERT INTO users (hospital_id, med_id, username, auth_hash, role) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement insert = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            insert.setInt(1, hospitalId);
            insert.setInt(2, medId);
            insert.setString(3, username);
            insert.setString(4, authHash);
            insert.setString(5, roleToStr(role));
            Integer affectedRows = insert.executeUpdate();
            
            if (affectedRows.equals(0))
            {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            ResultSet generatedKeys = insert.getGeneratedKeys();
            if (generatedKeys.next())
                newId = generatedKeys.getInt(1);
            else
                throw new SQLException("Creating user failed, no ID obtained.");
            insert.close();
        }
        catch (SQLException e)
        {
            return -1;
        }
        
        return newId;
    }
    
    public Users getUserBySecret(String secret)
    {
        Users output = null;
        Patients pat = null;
        Doctors doc = null;
        try
        {
            String sql = "SELECT u.* FROM authed_user a JOIN users u ON a.user_id = u.id WHERE a.secret = ?;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, secret);
            ResultSet rs = select.executeQuery();
            if (rs.next())
            {
                Integer hospital_id = rs.getInt(2);
                Roles role = strToRoles(rs.getString(6));
                switch (role)
                {
                case ADMIN:
                    doc = hdb.getDoctors(hospital_id);
                    break;
                case DOCTOR:
                    doc = hdb.getDoctors(hospital_id);
                    break;
                case PATIENT:
                    pat = hdb.getPatients(hospital_id);
                    break;
                case NOROLE:
                    return null;
                }
                if (doc == null && pat == null)
                    throw new SQLException("Invalid auth_hash");
                
                output = new Users(
                    rs.getInt(1),
                    doc, pat,
                    rs.getInt(3),
                    rs.getString(4), rs.getString(5),
                    role
                );
            }
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        return output;
    }
    
    public Users getUserByAuthHash(String authHash)
    {
        Users output = null;
        Patients pat = null;
        Doctors doc = null;
        try
        {
            String sql = "SELECT * FROM users WHERE auth_hash = ?;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, authHash);
            ResultSet rs = select.executeQuery();
            if (rs.next())
            {
                Integer hospital_id = rs.getInt(2);
                Roles role = strToRoles(rs.getString(6));
                switch (role)
                {
                case ADMIN:
                    doc = hdb.getDoctors(hospital_id);
                    break;
                case DOCTOR:
                    doc = hdb.getDoctors(hospital_id);
                    break;
                case PATIENT:
                    pat = hdb.getPatients(hospital_id);
                    break;
                case NOROLE:
                    return null;
                }
                if (doc == null && pat == null)
                    throw new SQLException("Invalid auth_hash");
                
                output = new Users(
                    rs.getInt(1),
                    doc, pat,
                    rs.getInt(3),
                    rs.getString(4), rs.getString(5),
                    role
                );
            }
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        return output;
    }
    
    public Users getDoctorByDocId(Integer docId)
    {
        Users output = null;
        try
        {
            String sql = "SELECT * FROM users WHERE (role = ? OR role = ?) AND hospital_id = ? LIMIT 1;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, "admin");
            select.setString(2, "doctor");
            select.setInt(3, docId);
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                output = new Users(
                    rs.getInt(1),
                    hdb.getDoctors(rs.getInt(2)), null,
                    rs.getInt(3),
                    rs.getString(4), rs.getString(5),
                    strToRoles(rs.getString(6))
                );
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return output;
    }
    
    public Users getDoctorByObj(Doctors doc)
    {
        Users output = null;
        try
        {
            String sql = "SELECT * FROM users WHERE (role = ? OR role = ?) AND hospital_id = ? LIMIT 1;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, "admin");
            select.setString(2, "doctor");
            select.setInt(3, doc.getId());
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                output = new Users(
                    rs.getInt(1),
                    doc, null,
                    rs.getInt(3),
                    rs.getString(4), rs.getString(5),
                    strToRoles(rs.getString(6))
                );
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return output;
    }
    
    public Users getPatientByPatId(Integer patId)
    {
        Users output = null;
        try
        {
            String sql = "SELECT * FROM users WHERE role = ? AND hospital_id = ? LIMIT 1;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, "patient");
            select.setInt(2, patId);
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                output = new Users(
                    rs.getInt(1),
                    null, hdb.getPatients(rs.getInt(2)),
                    rs.getInt(3),
                    rs.getString(4), rs.getString(5),
                    strToRoles(rs.getString(6))
                );
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return output;
    }
    
    public Users getPatientByObj(Patients pat)
    {
        Users output = null;
        try
        {
            String sql = "SELECT * FROM users WHERE role = ? AND hospital_id = ? LIMIT 1;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, "patient");
            select.setInt(2, pat.getId());
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                output = new Users(
                    rs.getInt(1),
                    null, pat,
                    rs.getInt(3),
                    rs.getString(4), rs.getString(5),
                    strToRoles(rs.getString(6))
                );
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return output;
    }
    
    public JSONArray getAllUsersOfDoc(Integer docId, Integer limit, Integer offset)
    {
        JSONArray output = new JSONArray();
        ArrayList<Patients> allPatients = hdb.getPatientsOfDoc(docId, limit, offset);
        if (allPatients == null)
            return output;
        
        for (Patients p : allPatients)
        {
            Users u = getPatientByObj(p);
            if (u == null)
            {
                u = new Users();
                u.setPatients(p);
            }
            output.put(u.toJson());
        }
        
        return output;
    }
    
    public JSONArray searchMyPatients(Integer docId, Integer offset, String ...searchArgs)
    {
        JSONArray output = new JSONArray();
        try
        {
            ArrayList<Patients> patients = hdb.findMyPatient(docId, offset, searchArgs);
            for (Patients p : patients)
            {
                Users u = getPatientByObj(p);
                if (u == null)
                {
                    u = new Users();
                    u.setPatients(p);
                }
                output.put(u.toJson());
            }
        }
        catch (Exception e)
        {
            return new JSONArray();
        }
        
        return output;
    }
    
    public Medication getMedication(Integer id) {
        Medication med = null;
        try
        {
            String sql = "SELECT * FROM medication WHERE id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                med = new Medication(
                    rs.getInt("id"),
                    rs.getString("name"), rs.getString("description"),
                    rs.getInt("frequency"), rs.getInt("early_time"),
                    rs.getInt("late_time")
                );
            }
            pstmt.close();
        }
        catch(SQLException e)
        {
            return null;
        }
        return med;
    }
    
    public Medication searchMedication(String name, Integer frequency, Integer earlyTime, Integer lateTime) {
        Medication med = null;
        try
        {
            String sql = "SELECT * FROM medication WHERE name = ? AND frequency = ? AND early_time = ? AND late_time = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, frequency);
            pstmt.setInt(3, earlyTime);
            pstmt.setInt(4, lateTime);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                med = new Medication(
                    rs.getInt("id"),
                    rs.getString("name"), rs.getString("description"),
                    rs.getInt("frequency"),
                    rs.getInt("early_time"), rs.getInt("late_time")
                );
            }
            pstmt.close();
        }
        catch(SQLException e)
        {
            return null;
        }
        return med;
    }
    
    public Integer addMedication(
        String name, String description,
        Integer frequency,
        Integer earlyTime, Integer lateTime
    )
    {
        Integer newId = -1;
        
        try
        {
            Medication searchMed = searchMedication(name, frequency, earlyTime, lateTime);
            
            // no duplicate in db, insert a new medication
            if(searchMed == null)
            {
                String sql = "INSERT INTO medication (name, description, frequency, early_time, late_time) VALUES (?, ?, ?, ?, ?);";
                PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setInt(3, frequency);
                pstmt.setInt(4, earlyTime);
                pstmt.setInt(5, lateTime);
                Integer affectedRows = pstmt.executeUpdate();
                
                // get new id from inserted row
                if (affectedRows.equals(0))
                    throw new SQLException("Cannot insert new Medication");
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next())
                    newId = rs.getInt(1);
                else
                    throw new SQLException("Cannot insert new Medication");
                
                pstmt.close();
            }
            // duplication in db, return found medication id
            else
            {
                return searchMed.getId();
            }
        }
        catch(SQLException e)
        {
            return -1;
        }
        return newId;
    }
    
    public JSONArray queryMedHistory(
        Integer userId,
        Integer medId, LogicalOperators medIdOpt,
        Integer time, LogicalOperators timeOpt,
        SortOrder order, Integer limit
    )
    {
        // build sql
        ArrayList<Integer> arglist = new ArrayList<Integer>();
        String sqljson = "JSON_OBJECT('id',id, 'user_id',user_id, 'med_id',med_id, 'med_time',med_time)";
        String sql = "SELECT " + sqljson + " FROM med_history WHERE user_id = ? ";
        arglist.add(userId);
        sql += "AND med_id " + logicalOperatorsToStr(medIdOpt) + " ? ";
        arglist.add(medId);
        sql += "AND med_time " + logicalOperatorsToStr(timeOpt) + " ? ";
        sql += "ORDER BY id " + sortOrderToStr(order) + " ";
        arglist.add(time);
        if (limit >= 0)
        {
            sql += "LIMIT ?";
            arglist.add(limit);
        }
        sql += ";";
        
        // query from db
        JSONArray output = new JSONArray();
        try
        {
            PreparedStatement query = conn.prepareStatement(sql);
            for (Integer i = 0; i < arglist.size(); ++i)
                query.setInt(i+1, arglist.get(i));
            ResultSet rs = query.executeQuery();
            
            while (rs.next())
            {
                output.put(new JSONObject(rs.getString(1)));
            }
            query.close();
        }
        catch (SQLException e)
        {
            return new JSONArray();
        }
        return output;
    }
    
    public JSONObject queryNotiInfo(Integer userId, Integer medId)
    {
        // fetch latest notification info
        JSONArray arr = queryMedHistory(
            userId,
            medId, LogicalOperators.EQ,
            Utilities.getUnixTimestampNow(), LogicalOperators.LTE,
            SortOrder.DESC, 1
        );
        
        // pack everything into json
        Integer lastMedTime = null;
        if (arr.isEmpty())
            lastMedTime = -1;
        else
            lastMedTime = arr.getJSONObject(0).getInt("med_time");
        Medication med = getMedication(medId);
        JSONObject output = new JSONObject();
        output.put("last_medication_time", lastMedTime);
        output.put("frequency", med.getFrequency());
        output.put("early_time", med.getEarlyTime());
        output.put("late_time", med.getLateTime());
        
        return output;
    }
    
    public boolean updateMedHistory(Integer userId, Integer medId)
    {
        Integer currentTime = Utilities.getUnixTimestampNow();
        boolean output = false;
        
        // update
        String sql = "INSERT INTO med_history (user_id, med_id, med_time) VALUES (?, ?, ?);";
        try
        {
            PreparedStatement insert = conn.prepareStatement(sql);
            insert.setInt(1, userId);
            insert.setInt(2, medId);
            insert.setInt(3, currentTime);
            Integer affectedRows = insert.executeUpdate();
            output = true;
            
            // no update, insert fail
            if (affectedRows.equals(0))
                output = false;
            insert.close();
        }
        catch (SQLException e)
        {
            output = false;
        }
        
        return output;
    }
    
    public JSONObject authUser(String username, String authHash)
    {
        Users searchUser = getUserByAuthHash(authHash);
        MyLogger.debug("authUser(): username = {}", username);
        MyLogger.debug("authUser(): authHash = {}", authHash);
        
        // validation
        if (searchUser == null)
            return null;
        
        if (searchUser.getUsername().equals(username) == false)
            return null;
        
        // try to authenticate user
        Integer maxTry = 5;
        boolean passed = false;
        String secret = null;
        Integer expire = Utilities.getUnixTimestampNow() + config.getMaxSessionAge();
        while (passed == false && maxTry > 0)
        {
            try
            {
                secret = Utilities.genSecret();
                String sql = "REPLACE INTO authed_user VALUES (?, ?, ?);";
                PreparedStatement replace = conn.prepareStatement(sql);
                replace.setInt(1, searchUser.getId());
                replace.setString(2, secret);
                replace.setInt(3, expire);
                Integer affectedRows = replace.executeUpdate();
                
                // no update caused by other reason
                if (affectedRows.equals(0))
                    return null;
                else if (affectedRows > 0)
                    passed = true;
                replace.close();
            }
            // unique constraint failed / primary key failed
            catch (SQLIntegrityConstraintViolationException e)
            {
                // is primary key failure
                if (e.getMessage().toLowerCase().contains("primary"))
                    return null;
                continue;
            }
            catch (SQLException e)
            {
                return null;
            }
            maxTry--;
        }
        
        // output auth info
        JSONObject output = new JSONObject();
        output.put("user_id", searchUser.getId());
        output.put("secret", secret);
        output.put("expire", expire);
        MyLogger.debug("authUser(): output = {}", output.toString());
        return output;
    }
    
    // validate a single operation
    public boolean validateOperationSingle(
        String username, String secret,
        Operations operations
    )
    {
        // validate authentication
        Roles userRole = validateAuth(username, secret);
        if (userRole == null)
            return false;
        
        // check role & operations
        switch (userRole)
        {
        case NOROLE:
            return false;
        case ADMIN:
            return true;
        case DOCTOR:
            return (
                operations.equals(Operations.DOCTOR_READ) ||
                operations.equals(Operations.DOCTOR_WRITE) ||
                operations.equals(Operations.PATIENT_READ) ||
                operations.equals(Operations.PATIENT_WRITE)
            );
        case PATIENT:
            return (
                operations.equals(Operations.PATIENT_READ) ||
                operations.equals(Operations.PATIENT_WRITE)
            );
        }
        
        return false;
    }
    
    // validate a single operation with self checking
    public boolean validateOperationSingle(
        String username, String secret,
        Integer targetUserId, String validateFunc,
        Operations operations
    )
    {
        // validate authentication
        Roles userRole = null;
        if (validateFunc.equals("alleq"))
            userRole = validateAuthWithTargetAllEq(username, secret, targetUserId);
        else if (validateFunc.equals("eqrole"))
            userRole = validateAuthWithTargetEqRole(username, secret, targetUserId);
        else
            return false;
        if (userRole == null)
            return false;
        
        // check role & operations
        switch (userRole)
        {
        case NOROLE:
            return false;
        case ADMIN:
            return true;
        case DOCTOR:
            return (
                operations.equals(Operations.DOCTOR_READ) ||
                operations.equals(Operations.DOCTOR_WRITE) ||
                operations.equals(Operations.PATIENT_READ) ||
                operations.equals(Operations.PATIENT_WRITE)
            );
        case PATIENT:
            return (
                operations.equals(Operations.PATIENT_READ) ||
                operations.equals(Operations.PATIENT_WRITE)
            );
        }
        
        return false;
    }
    
    // use "and" logic to compare operations
    public boolean validateOperationsAnd(
        String username, String secret,
        Operations[] operations
    )
    {
        // validate authentication
        Roles userRole = validateAuth(username, secret);
        if (userRole == null)
            return false;
        
        // check role & operations
        boolean andCompare = true;
        for (Operations opt : operations)
        {
            boolean compResult = false;
            switch (userRole)
            {
            case NOROLE:
                return false;
            case ADMIN:
                return true;
            case DOCTOR:
                compResult = (
                    opt.equals(Operations.DOCTOR_READ) ||
                    opt.equals(Operations.DOCTOR_WRITE) ||
                    opt.equals(Operations.PATIENT_READ) ||
                    opt.equals(Operations.PATIENT_WRITE)
                );
                break;
            case PATIENT:
                compResult = (
                    opt.equals(Operations.PATIENT_READ) ||
                    opt.equals(Operations.PATIENT_WRITE)
                );
                break;
            }
            andCompare = andCompare && compResult;
        }
        
        return andCompare;
    }
    
    // use "and" logic to compare operations
    public boolean validateOperationsAnd(
        String username, String secret,
        Integer targetUserId, String validateFunc,
        Operations[] operations
    )
    {
        // validate authentication
        Roles userRole = null;
        if (validateFunc.equals("alleq"))
            userRole = validateAuthWithTargetAllEq(username, secret, targetUserId);
        else if (validateFunc.equals("eqrole"))
            userRole = validateAuthWithTargetEqRole(username, secret, targetUserId);
        else
            return false;
        if (userRole == null)
            return false;
        
        // check role & operations
        boolean andCompare = true;
        for (Operations opt : operations)
        {
            boolean compResult = false;
            switch (userRole)
            {
            case NOROLE:
                return false;
            case ADMIN:
                return true;
            case DOCTOR:
                compResult = (
                    opt.equals(Operations.DOCTOR_READ) ||
                    opt.equals(Operations.DOCTOR_WRITE) ||
                    opt.equals(Operations.PATIENT_READ) ||
                    opt.equals(Operations.PATIENT_WRITE)
                );
                break;
            case PATIENT:
                compResult = (
                    opt.equals(Operations.PATIENT_READ) ||
                    opt.equals(Operations.PATIENT_WRITE)
                );
                break;
            }
            andCompare = andCompare && compResult;
        }
        
        return andCompare;
    }
    
    // use "or" logic to compare operations
    public boolean validateOperationsOr(
        String username, String secret,
        Operations[] operations
    )
    {
        // validate authentication
        Roles userRole = validateAuth(username, secret);
        if (userRole == null)
            return false;
        
        // check role & operations
        boolean orCompare = false;
        for (Operations opt : operations)
        {
            boolean compResult = false;
            switch (userRole)
            {
            case NOROLE:
                return false;
            case ADMIN:
                return true;
            case DOCTOR:
                compResult = (
                    opt.equals(Operations.DOCTOR_READ) ||
                    opt.equals(Operations.DOCTOR_WRITE) ||
                    opt.equals(Operations.PATIENT_READ) ||
                    opt.equals(Operations.PATIENT_WRITE)
                );
                break;
            case PATIENT:
                compResult = (
                    opt.equals(Operations.PATIENT_READ) ||
                    opt.equals(Operations.PATIENT_WRITE)
                );
                break;
            }
            orCompare = orCompare || compResult;
        }
        
        return orCompare;
    }
    
    // use "or" logic to compare operations
    public boolean validateOperationsOr(
        String username, String secret,
        Integer targetUserId, String validateFunc,
        Operations[] operations
    )
    {
        // validate authentication
        Roles userRole = null;
        if (validateFunc.equals("alleq"))
            userRole = validateAuthWithTargetAllEq(username, secret, targetUserId);
        else if (validateFunc.equals("eqrole"))
            userRole = validateAuthWithTargetEqRole(username, secret, targetUserId);
        else
            return false;
        if (userRole == null)
            return false;
        
        // check role & operations
        boolean orCompare = false;
        for (Operations opt : operations)
        {
            boolean compResult = false;
            switch (userRole)
            {
            case NOROLE:
                return false;
            case ADMIN:
                return true;
            case DOCTOR:
                compResult = (
                    opt.equals(Operations.DOCTOR_READ) ||
                    opt.equals(Operations.DOCTOR_WRITE) ||
                    opt.equals(Operations.PATIENT_READ) ||
                    opt.equals(Operations.PATIENT_WRITE)
                );
                break;
            case PATIENT:
                compResult = (
                    opt.equals(Operations.PATIENT_READ) ||
                    opt.equals(Operations.PATIENT_WRITE)
                );
                break;
            }
            orCompare = orCompare || compResult;
        }
        
        return orCompare;
    }
    
    
    // private helper functions
    private String findLastValidUsername(String username)
    {
        String output = null;
        try
        {
            String sql = "SELECT username FROM users WHERE username REGEXP ? ORDER BY id DESC LIMIT 1;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, username+"\\d+");
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
                output = rs.getString(1);
            else
                throw new SQLException("Cannot find supplied username.");
            select.close();
        }
        catch (SQLException e)
        {
            return username + "1";
        }
        Pattern p = Pattern.compile("[a-z]+(\\d+)$");
        Matcher m = p.matcher(output);
        if (m.find())
            output = username + Integer.toString(Integer.parseInt(m.group(1))+1);
        else
            output = username + "1";
        return output;
    }
    
    /**
     * validate user 1 (username, secret) has valid user info
     * 
     * @param username String username in db
     * @param secret String user secret in db
     * @return
     *  If success, return user1 Roles. Otherwise, return null
     */
    private Roles validateAuth(String username, String secret)
    {
        // fetching data from db
        Integer _id = null;
        String _username = null;
        String _role = null;
        String _secret = null;
        Integer _expire = null;
        
        String sql =
            "SELECT u.id, u.username, u.role, a.secret, a.expire " +
            "FROM authed_user a JOIN users u " +
            "ON a.user_id = u.id " +
            "WHERE u.username = ? AND a.secret = ? " +
            "LIMIT 1;"
        ;
        try
        {
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, username);
            select.setString(2, secret);
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                _id = rs.getInt(1);
                _username = rs.getString(2);
                _role = rs.getString(3);
                _secret = rs.getString(4);
                _expire = rs.getInt(5);
            }
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        
        // validations
        if (_expire == null || _expire <= Utilities.getUnixTimestampNow())
            return null;
        if (username.equals(_username) == false)
            return null;
        if (secret.equals(_secret) == false)
            return null;
        
        return strToRoles(_role);
    }
    
    /**
     * <pre>
     * validate user 1 w/ (username, secret) and user 2 w/ (targetUserId)
     * If user 1 role > user 2 role, return user 1 role
     * If user 1 role < user 2 role, return null
     * If user 1 role = user 2 role, check their id, username, secrete, ...
     * </pre>
     * 
     * @param
     *  username str for user 1
     * 
     * @param
     *  secret str for user 1
     * 
     * @param
     *  targetUserId int for user 2
     * 
     * @return
     *  If success, return user1 Roles. Otherwise, return null
     */
    private Roles validateAuthWithTargetAllEq(
        String username, String secret,
        Integer targetUserId
    )
    {
        // fetching data from db
        Integer _id = null;
        String _username = null;
        String _role = null;
        String _secret = null;
        Integer _expire = null;
        
        String sql =
            "SELECT u.id, u.username, u.role, a.secret, a.expire " +
            "FROM authed_user a JOIN users u " +
            "ON a.user_id = u.id " +
            "WHERE u.username = ? AND a.secret = ? " +
            "LIMIT 1;"
        ;
        try
        {
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, username);
            select.setString(2, secret);
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                _id = rs.getInt(1);
                _username = rs.getString(2);
                _role = rs.getString(3);
                _secret = rs.getString(4);
                _expire = rs.getInt(5);
            }
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        
        Users targetUser = getUser(targetUserId);
        Roles selfRole = strToRoles(_role);
        try
        {
            // validations
            if (_expire == null || _expire <= Utilities.getUnixTimestampNow())
                return null;
            
            // user 1 can manipulate user 2
            if (selfRole.isHigherThan(targetUser.getRole()))
                return selfRole;
            // user 1 cannot manipulate user 2
            else if (selfRole.isLowerThan(targetUser.getRole()))
                return null;
            
            // user 1 & user 2 have same role, check if they're equal
            if (_id != targetUserId)
                return null;
            if (username.equals(_username) == false)
                return null;
            if (secret.equals(_secret) == false)
                return null;
        }
        catch (Exception e)
        {
            return null;
        }
        
        return selfRole;
    }
    
    /**
     * <pre>
     * validate user 1 w/ (username, secret) and user 2 w/ (targetUserId)
     * If user 1 role > user 2 role, return user 1 role
     * If user 1 role < user 2 role, return null
     * If user 1 role = user 2 role, return user 1 role
     * </pre>
     * 
     * @param
     *  username str for user 1
     * 
     * @param
     *  secret str for user 1
     * 
     * @param
     *  targetUserId int for user 2
     * 
     * @return
     *  If success, return user1 Roles. Otherwise, return null
     */
    private Roles validateAuthWithTargetEqRole(
        String username, String secret,
        Integer targetUserId
    )
    {
        // fetching data from db
        Integer _id = null;
        String _username = null;
        String _role = null;
        String _secret = null;
        Integer _expire = null;
        
        String sql =
            "SELECT u.id, u.username, u.role, a.secret, a.expire " +
            "FROM authed_user a JOIN users u " +
            "ON a.user_id = u.id " +
            "WHERE u.username = ? AND a.secret = ? " +
            "LIMIT 1;"
        ;
        try
        {
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, username);
            select.setString(2, secret);
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                _id = rs.getInt(1);
                _username = rs.getString(2);
                _role = rs.getString(3);
                _secret = rs.getString(4);
                _expire = rs.getInt(5);
            }
            else
                throw new SQLException("Cannot fetch user login info from db.");
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        
        Users targetUser = getUser(targetUserId);
        Roles selfRole = strToRoles(_role);
        try
        {
            // validations
            if (_expire <= Utilities.getUnixTimestampNow())
                return null;
            
            // user 1 can manipulate user 2
            if (selfRole.isHigherThan(targetUser.getRole()))
                return selfRole;
            // user 1 cannot manipulate user 2
            else if (selfRole.isLowerThan(targetUser.getRole()))
                return null;
            // user 1 & user 2 have same role
            else
                return selfRole;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    
    // private and empty for singleton class
    private MedReminderDB() {}
    
    private static MedReminderDB instance = null;
    private static HospitalDB hdb = null;
    private static Connection conn;
    private static ConfigManager config = ConfigManager.getInstance();
    
}
