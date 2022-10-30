package cs3337.MedReminderbackend.DB;

import java.util.*;
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
	
	
}
