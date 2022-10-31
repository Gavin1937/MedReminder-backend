package cs3337.MedReminderbackend.Model;

import org.json.JSONObject;

public class Doctors
{
    
    public Doctors(
        Integer id,
        String fname, String lname,
        String phone, String email
    )
    {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.email = email;
    }
    
    
    // getters
    public Integer getId()
    {
        return id;
    }
    public String getFname()
    {
        return fname;
    }
    public String getLname()
    {
        return lname;
    }
    public String getPhone()
    {
        return phone;
    }
    public String getEmail()
    {
        return email;
    }
    
    // other functions
    public JSONObject toJson()
    {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("fname", fname);
        obj.put("lname", lname);
        obj.put("phone", phone);
        obj.put("email", email);
        return obj;
    }
    public static Doctors fromJson(JSONObject obj)
    {
        return new Doctors(
            obj.getInt("id"),
            obj.getString("fname"), obj.getString("lname"),
            obj.getString("phone"), obj.getString("email")
        );
    }
    
    
    // private members
    private Integer id;
    private String fname;
    private String lname;
    private String phone;
    private String email;
    
}
