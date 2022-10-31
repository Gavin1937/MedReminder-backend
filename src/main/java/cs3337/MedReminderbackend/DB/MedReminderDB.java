package cs3337.MedReminderbackend.DB;

import java.sql.*;
import cs3337.MedReminderbackend.Model.*;


public class MedReminderDB
{
    
    public static MedReminderDB getInstance()
	{
        if (instance == null)
		{
            instance = new MedReminderDB();
        }
        
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
    
    public Medication getMedication(Integer id) {
		Medication med = null;
		try {
			String sql = "select * from medication where id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
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
	
	public Medication findMedication(String name, Integer frequency, Integer earlyTime, Integer lateTime) {
		Medication med = null;
		try {
			String sql = "select * from medication where name = ? AND frequency = ? AND early_time = ? AND late_time = ?";
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
		Medication searchMed = findMedication(name, frequency, earlyTime, lateTime);
		try {
			if(searchMed == null) {
				String sql = "insert into medication (name, description, frequency, early_time, late_time) values (?, ?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, name);
				pstmt.setString(2, description);
				pstmt.setInt(3, frequency);
				pstmt.setInt(4, earlyTime);
				pstmt.setInt(5, lateTime);
				ResultSet rs = pstmt.executeQuery();
				searchMed = new Medication(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getInt("frequency"), rs.getInt("early_time"), rs.getInt("late_time"));
				pstmt.close();
				return searchMed.getId();
			}
			else {
				return searchMed.getId();
			}
		}catch(SQLException e) {
			return -1;
		}
	}
    // private and empty for singleton class
    private MedReminderDB() {}
    
    private static MedReminderDB instance = null;
    private static Connection conn;
	
}
