package cs3337.MedReminderbackend.Model;

import org.json.JSONObject;

public class Patients
{
    
    public Patients(
        Integer id,
        String fname, String lname,
        String phone, String email,
        Integer primaryDoc
    )
    {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.email = email;
        this.primaryDoc = primaryDoc;
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
    public Integer getPrimaryDoc()
    {
        return primaryDoc;
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
        obj.put("primary_doc", primaryDoc);
        return obj;
    }
    public static Patients fromJson(JSONObject obj)
    {
        return new Patients(
            obj.getInt("id"),
            obj.getString("fname"), obj.getString("lname"),
            obj.getString("phone"), obj.getString("email"),
            obj.getInt("primary_doc")
        );
    }
    
    
    // private members
    private Integer id;
    private String fname;
    private String lname;
    private String phone;
    private String email;
    private Integer primaryDoc;
    
}
