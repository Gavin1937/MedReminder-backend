package cs3337.MedReminderbackend.DB;

import java.sql.*;


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
		String ip,
		String dbName, String username,
		String password
	) throws SQLException
	{
		String connectStr = "jdbc:mysql://" + ip + "/" + dbName + "?user="+username +"&password=" + password;
        conn = DriverManager.getConnection(connectStr);
    }
    
    public void finalize() throws SQLException
	{
        if (conn != null)
		{
            conn.close();
        }
    }
    
    // private and empty for singleton class
    private MedReminderDB() {}
    
    private static MedReminderDB instance = null;
    private static Connection conn;
	
}
