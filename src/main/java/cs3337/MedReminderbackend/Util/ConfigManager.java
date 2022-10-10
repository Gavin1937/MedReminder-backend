package cs3337.MedReminderbackend.Util;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import ch.qos.logback.classic.Level;


public class ConfigManager
{
    
    public static ConfigManager getInstance()
    {
        if (__instance == null)
            __instance = new ConfigManager();
        return __instance;
    }
    
    public void readConfig(String config_path)
        throws Exception
    {
        String content = FileUtils.readFileToString(new File(config_path), "UTF-8");
        __config = new JSONObject(content);
        
        // checking config content
        String key = "";
        try
        {
            key = "dbinfo";
            __config.get(key);
            key = "db_ip";
            __config.getJSONObject("dbinfo").get(key);
            key = "db_name";
            __config.getJSONObject("dbinfo").get(key);
            key = "db_username";
            __config.getJSONObject("dbinfo").get(key);
            key = "db_pwd";
            __config.getJSONObject("dbinfo").get(key);
            key = "log_filepath";
            __config.get(key);
            key = "logging_level";
            __config.get(key);
            key = "server_port";
            __config.get(key);
        }
        catch (Exception err)
        {
            throw new Exception("Configuration File Error, key \"" + key + "\" does not exist in config.json.");
        }
    }
    
    public JSONObject getConfig()
    {
        return __config;
    }
    
    public String getDBIp()
    {
        return __config.getJSONObject("dbinfo").getString("db_ip");
    }
    
    public String getDBName()
    {
        return __config.getJSONObject("dbinfo").getString("db_name");
    }
    
    public String getDBUsername()
    {
        return __config.getJSONObject("dbinfo").getString("db_username");
    }
    
    public String getDBPwd()
    {
        return __config.getJSONObject("dbinfo").getString("db_pwd");
    }
    
    public String getLogFilePath()
    {
        return __config.getString("log_filepath");
    }
    
    public Integer getServerPort()
    {
        Integer port = -1;
        try
        {
            port = (Integer)__config.getInt("server_port");
        }
        catch (Exception e)
        {
            return 8080;
        }
        
        if (port > 0)
            return port;
        return 8080;
    }
    
    public Level getLoggingLevel()
    {
        String level = __config.getString("logging_level").toUpperCase();
        switch (level)
        {
        case "TRACE":
            return Level.TRACE;
        case "DEBUG":
            return Level.DEBUG;
        case "INFO":
            return Level.INFO;
        case "WARN":
            return Level.WARN;
        case "ERROR":
            return Level.ERROR;
        default:
            return Level.INFO;
        }
    }
    
    public String getLoggingLevelStr()
    {
        return __config.getString("logging_level").toUpperCase();
    }
    
    
    // private constructor for singleton
    private ConfigManager() {}
    
    
    // private members 
    private static ConfigManager __instance = null;
    private JSONObject __config = new JSONObject();
    
}