package cs3337.MedReminderbackend.DB;

import java.util.*;
import java.sql.*;


public class MedReminderDB {

	private static MedReminderDB instance = null;

	// private and empty for singleton class
	private MedReminderDB() {

	}

	public static MedReminderDB getInstance() {
		if (instance == null) {
			instance = new MedReminderDB();
		}
		
		return instance;
	}
	
	public void init(String ip, String dbName, String username, String password) throws SQLException {
		c = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + dbName + "?user="+username +"&password=" + password);
		
	}
	
	public void finalize() throws SQLException {
		if(c != null) {
			c.close();
		}
	}
	
	
	private static Connection c;
}
