package cs3337.MedReminderbackend.DB;

import java.util.*;

import cs3337.MedReminderbackend.Model.Doctors;
import cs3337.MedReminderbackend.Model.Patients;

import java.sql.*;

public class HospitalDB {
	private static HospitalDB instance = new HospitalDB();
	private static Connection conn;
	
	
	private HospitalDB() {
		
	}
	
	public static HospitalDB getInstance() {
		return instance;
	}
	
	public boolean init(String ip, String username, String password) {
        try {
        String connectStr = "jdbc:mysql://" + ip + "/";
        conn = DriverManager.getConnection(connectStr, username, password);
        }catch(SQLException e) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    return false;
                }
            }
            return false;
        }
        return true;
    }
	
	public Doctors getDoctors(Integer id) {
		Doctors d = null;
		try {
			String sql = "select * from doctors where id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				d = new Doctors(rs.getInt("id"), rs.getString("fname"),rs.getString("lname"),rs.getString("phone"),rs.getString("email"));
			}
			pstmt.close();
		}catch(SQLException e) {
			return null;
		}
		return d;
	}
	
	public Patients getPatients(Integer id) {
		Patients p = null;
		try {
			String sql = "select * from patients where id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				p = new Patients(rs.getInt("id"), rs.getString("fname"),rs.getString("lname"),rs.getString("phone"),rs.getString("email"), rs.getInt("primaryDoc"));
			}
			pstmt.close();
		}catch(SQLException e) {
			return null;
		}
		return p;
	}
	
	
}
