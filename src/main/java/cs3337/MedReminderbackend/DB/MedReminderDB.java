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

import org.apache.catalina.webresources.Cache;
import org.apache.taglibs.standard.tag.common.core.CatchTag;
import org.json.JSONArray;
import org.json.JSONObject;

import static cs3337.MedReminderbackend.Util.Types.logicalOperatorsToStr;
import static cs3337.MedReminderbackend.Util.Types.sortOrderToStr;
import static cs3337.MedReminderbackend.Util.Types.roleToStr;
import static cs3337.MedReminderbackend.Util.Types.strToRoles;

import cs3337.MedReminderbackend.Util.ConfigManager;
import cs3337.MedReminderbackend.Util.Utilities;
import cs3337.MedReminderbackend.Util.Types.LogicalOperators;
import cs3337.MedReminderbackend.Util.Types.SortOrder;
import cs3337.MedReminderbackend.Util.Types.Roles;
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
    }
    
    public void finalize() throws SQLException
    {
        if (conn != null)
        {
            conn.close();
        }
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
                    doc = HospitalDB.getInstance().getDoctors(hospital_id);
                    break;
                case PATIENT:
                    pat = HospitalDB.getInstance().getPatients(hospital_id);
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
                Doctors doc = HospitalDB.getInstance().getDoctors(hospitalId);
                fname = doc.getFname();
                lname = doc.getLname();
                break;
            case PATIENT:
                Patients pat = HospitalDB.getInstance().getPatients(hospitalId);
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
            PreparedStatement insert = conn.prepareStatement(sql);
            insert.setInt(1, hospitalId);
            insert.setInt(2, medId);
            insert.setString(3, username);
            insert.setString(4, authHash);
            insert.setString(5, roleToStr(role));
            Integer affectedRows = insert.executeUpdate();
            
            if (affectedRows == 0)
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
                case DOCTOR: case ADMIN:
                    doc = HospitalDB.getInstance().getDoctors(hospital_id);
                    break;
                case PATIENT:
                    pat = HospitalDB.getInstance().getPatients(hospital_id);
                    break;
                case NOROLE:
                    throw new SQLException("Invalid role");
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
    
    public Medication findMedication(String name, Integer frequency, Integer earlyTime, Integer lateTime) {
        Medication med = null;
        try {
            String sql = "SELECT * FROM medication WHERE name = ? AND frequency = ? AND early_time = ? AND late_time = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, frequency);
            pstmt.setInt(3, earlyTime);
            pstmt.setInt(4, lateTime);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                med = new Medication(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getInt("frequency"), rs.getInt("early_time"), rs.getInt("late_time"));
                med.setId(rs.getInt("id"));
                med.setName(rs.getString("name"));
                med.setDescription(rs.getString("description"));
                med.setFrequency(rs.getInt("frequency"));
                med.setEarlyTime(rs.getInt("early_time"));
                med.setLateTime(rs.getInt("late_time"));
            }
            pstmt.close();
        }catch(SQLException e) {
            return null;
        }
        return med;
    }
    
    public Integer addMedication(String name, String description, Integer frequency, Integer earlyTime, Integer lateTime) {
        Integer newId = -1;
        
        try
        {
            Medication searchMed = findMedication(name, frequency, earlyTime, lateTime);
            
            // no duplicate in db, insert a new medication
            if(searchMed == null)
            {
                String sql = "INSERT INTO medication (name, description, frequency, early_time, late_time) VALUES (?, ?, ?, ?, ?);";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setInt(3, frequency);
                pstmt.setInt(4, earlyTime);
                pstmt.setInt(5, lateTime);
                Integer affectedRows = pstmt.executeUpdate();
                
                // get new id from inserted row
                if (affectedRows == 0)
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
        String sqljson = "JSON_ARRAYAGG(JSON_OBJECT('id',id, 'user_id',user_id, 'med_id',med_id, 'med_time',med_time))";
        String sql = "SELECT " + sqljson + " FROM med_history WHERE user_id = ? ";
        arglist.add(userId);
        sql += "AND med_id " + logicalOperatorsToStr(medIdOpt) + " ? ";
        arglist.add(medId);
        sql += "AND med_time " + logicalOperatorsToStr(timeOpt) + " ? ";
        sql += "ORDER BY " + sortOrderToStr(order) + " ";
        arglist.add(time);
        if (limit > 0)
        {
            sql += "LIMIT ?";
            arglist.add(limit);
        }
        sql += ";";
        
        // query from db
        JSONArray output = new JSONArray("[]");
        try
        {
            PreparedStatement query = conn.prepareStatement(sql);
            for (Integer i = 0; i < arglist.size(); ++i)
                query.setInt(i+1, arglist.get(i));
            ResultSet rs = query.executeQuery();
            
            if (rs.next())
            {
                output = new JSONArray(rs.getString(1));
            }
            query.close();
        }
        catch (SQLException e)
        {
            return new JSONArray("[]");
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
        Integer lastNotiTime = arr.getJSONObject(0).getInt("med_time");
        Medication med = getMedication(medId);
        JSONObject output = new JSONObject();
        output.put("last_noti_time", lastNotiTime);
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
            if (affectedRows == 0)
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
        
        // validation
        if (searchUser == null)
            return null;
        
        if (searchUser.getUsername().equals(username) == false)
            return null;
        
        // try to authenticate user
        boolean passed = false;
        String secret = null;
        Integer expire = Utilities.getUnixTimestampNow() + config.getMaxSessionAge();
        while (passed == false)
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
                if (affectedRows == 0)
                    return null;
                else if (affectedRows == 1)
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
        }
        
        // output auth info
        JSONObject output = new JSONObject();
        output.put("user_id", searchUser.getId());
        output.put("secret", secret);
        output.put("expire", expire);
        return output;
    }
    
    
    // private helper functions
    private String findLastValidUsername(String username)
    {
        String output = null;
        try
        {
            String sql = "SELECT username FROM users WHERE username LIKE ? ORDER BY id DESC LIMIT 1;";
            PreparedStatement select = conn.prepareStatement(sql);
            select.setString(1, username+"%");
            ResultSet rs = select.executeQuery();
            
            if (rs.next())
            {
                output = rs.getString(1);
            }
            select.close();
        }
        catch (SQLException e)
        {
            return username + "1";
        }
        Pattern p = Pattern.compile("[a-z]+(\\d+)$");
        Matcher m = p.matcher(output);
        output = username + Integer.toString(Integer.parseInt(m.group(1))+1);
        return output;
    }
    
    
    // private and empty for singleton class
    private MedReminderDB() {}
    
    private static MedReminderDB instance = null;
    private static Connection conn;
    private static ConfigManager config = ConfigManager.getInstance();
    
}
