package cs3337.MedReminderbackend.DB;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

import cs3337.MedReminderbackend.Model.Doctors;
import cs3337.MedReminderbackend.Model.Patients;


public class HospitalDB
{
    
    public static HospitalDB getInstance()
    {
        if (instance == null)
            instance = new HospitalDB();
        
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
    
    public Doctors getDoctors(Integer id)
    {
        Doctors d = null;
        try
        {
            String sql = "SELECT * FROM doctors WHERE id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next())
            {
                d = new Doctors(
                    rs.getInt("id"),
                    rs.getString("fname"), rs.getString("lname"),
                    rs.getString("phone"), rs.getString("email")
                );
            }
            pstmt.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        return d;
    }
    
    public Patients getPatients(Integer id)
    {
        Patients p = null;
        try
        {
            String sql = "SELECT * FROM patients WHERE id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next())
            {
                p = new Patients(
                    rs.getInt("id"),
                    rs.getString("fname"), rs.getString("lname"),
                    rs.getString("phone"), rs.getString("email"),
                    rs.getInt("primaryDoc")
                );
            }
            pstmt.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        return p;
    }
    
    public ArrayList<Patients> getPatientsOfDoc(Integer docId, Integer limit)
    {
        ArrayList<Patients> patients = new ArrayList<Patients>();
        ArrayList<Integer> paramList = new ArrayList<Integer>();
        try
        {
            String sql = "SELECT * FROM patients WHERE primary_doc = ?";
            paramList.add(docId);
            if (limit == -1)
            {
                sql += " LIMIT ?";
                paramList.add(limit);
            }
            sql += ";";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < paramList.size(); i++)
            {
                pstmt.setInt(i + 1, paramList.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next())
            {
                patients.add(new Patients(
                    rs.getInt("id"),
                    rs.getString("fname"), rs.getString("lname"),
                    rs.getString("phone"), rs.getString("email"),
                    rs.getInt("primary_doc")
                ));
            }
        }
        catch (SQLException e)
        {
            return null;
        }
        return patients;
    }
    
    public ArrayList<Patients> getPatientsOfDoc(Integer docId)
    {
        return getPatientsOfDoc(docId, -1);
    }
    
    public ArrayList<Patients> findPatient(String ...args)
    {
        String fname = "";
        String lname = "";
        String phone = "";
        String email = "";
        
        // parse args
        Pattern p = Pattern.compile("([a-z]+):([a-z]+)");
        for (String arg : args)
        {
            Matcher m = p.matcher(arg);
            String left = m.group(1);
            String right = m.group(2);
            
            if (left.equals("fname"))
                fname = right;
            else if (left.equals("lname"))
                lname = right;
            else if (left.equals("phone"))
                phone = right;
            else if (left.equals("email"))
                email = right;
        }
        
        // build sql
        String sql = "SELECT * FROM patients WHERE 1=1 ";
        ArrayList<String> arglist = new ArrayList<String>();
        if (fname.isEmpty() == false)
        {
            sql += "AND fname LIKE ? ";
            arglist.add(fname);
        }
        if (lname.isEmpty() == false)
        {
            sql += "AND lname LIKE ? ";
            arglist.add(lname);
        }
        if (phone.isEmpty() == false)
        {
            sql += "AND phone LIKE ? ";
            arglist.add(phone);
        }
        if (email.isEmpty() == false)
        {
            sql += "AND email LIKE ? ";
            arglist.add(email);
        }
        sql += ";";
        
        // run sql
        ArrayList<Patients> output = new ArrayList<Patients>();
        try
        {
            PreparedStatement select = conn.prepareStatement(sql);
            for (Integer i = 1; i <= arglist.size(); ++i)
            {
                select.setString(i, arglist.get(i-1));
            }
            ResultSet rs = select.executeQuery();
            while (rs.next())
            {
                output.add(new Patients(
                    rs.getInt(1),
                    rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5),
                    rs.getInt(6)
                ));
            }
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        
        if (output.isEmpty())
            return null;
        return output;
    }
    
    public ArrayList<Doctors> findDoctor(String ...args)
    {
        String fname = "";
        String lname = "";
        String phone = "";
        String email = "";
        
        // parse args
        Pattern p = Pattern.compile("([a-z]+):([a-z]+)");
        for (String arg : args)
        {
            Matcher m = p.matcher(arg);
            String left = m.group(1);
            String right = m.group(2);
            
            if (left.equals("fname"))
                fname = right;
            else if (left.equals("lname"))
                lname = right;
            else if (left.equals("phone"))
                phone = right;
            else if (left.equals("email"))
                email = right;
        }
        
        // build sql
        String sql = "SELECT * FROM doctors WHERE 1=1 ";
        ArrayList<String> arglist = new ArrayList<String>();
        if (fname.isEmpty() == false)
        {
            sql += "AND fname LIKE ? ";
            arglist.add(fname);
        }
        if (lname.isEmpty() == false)
        {
            sql += "AND lname LIKE ? ";
            arglist.add(lname);
        }
        if (phone.isEmpty() == false)
        {
            sql += "AND phone LIKE ? ";
            arglist.add(phone);
        }
        if (email.isEmpty() == false)
        {
            sql += "AND email LIKE ? ";
            arglist.add(email);
        }
        sql += ";";
        
        // run sql
        ArrayList<Doctors> output = new ArrayList<Doctors>();
        try
        {
            PreparedStatement select = conn.prepareStatement(sql);
            for (Integer i = 1; i <= arglist.size(); ++i)
            {
                select.setString(i, arglist.get(i-1));
            }
            ResultSet rs = select.executeQuery();
            if (rs.next())
            {
                output.add(new Doctors(
                    rs.getInt(1),
                    rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5)
                ));
            }
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        
        if (output.isEmpty())
            return null;
        return output;
    }
    
    public ArrayList<Patients> findPatientOfDoc(Integer docId, String ...args)
    {
        String fname = "";
        String lname = "";
        String phone = "";
        String email = "";
        
        // parse args
        Pattern p = Pattern.compile("([a-z]+):([a-z]+)");
        for (String arg : args)
        {
            Matcher m = p.matcher(arg);
            String left = m.group(1);
            String right = m.group(2);
            
            if (left.equals("fname"))
                fname = right;
            else if (left.equals("lname"))
                lname = right;
            else if (left.equals("phone"))
                phone = right;
            else if (left.equals("email"))
                email = right;
        }
        
        // build sql
        String sql = "SELECT * FROM patients WHERE 1=1 ";
        ArrayList<String> arglist = new ArrayList<String>();
        if (fname.isEmpty() == false)
        {
            sql += "AND fname LIKE ? ";
            arglist.add(fname);
        }
        if (lname.isEmpty() == false)
        {
            sql += "AND lname LIKE ? ";
            arglist.add(lname);
        }
        if (phone.isEmpty() == false)
        {
            sql += "AND phone LIKE ? ";
            arglist.add(phone);
        }
        if (email.isEmpty() == false)
        {
            sql += "AND email LIKE ? ";
            arglist.add(email);
        }
        sql += "AND id = ?;";
        
        // run sql
        ArrayList<Patients> output = new ArrayList<Patients>();
        try
        {
            PreparedStatement select = conn.prepareStatement(sql);
            for (Integer i = 1; i <= arglist.size(); ++i)
            {
                select.setString(i, arglist.get(i-1));
            }
            ResultSet rs = select.executeQuery();
            while (rs.next())
            {
                output.add(new Patients(
                    rs.getInt(1),
                    rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5),
                    rs.getInt(6)
                ));
            }
            select.close();
        }
        catch (SQLException e)
        {
            return null;
        }
        
        if (output.isEmpty())
            return null;
        return output;
    }
    
    
    // private constructor for singleton
    private HospitalDB() {}
    
    // private members
    private static HospitalDB instance = null;
    private static Connection conn;
    
}
