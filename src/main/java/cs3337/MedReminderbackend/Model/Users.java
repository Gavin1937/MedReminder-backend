package cs3337.MedReminderbackend.Model;


import org.json.JSONObject;

import cs3337.MedReminderbackend.Util.Types.Roles;
import static cs3337.MedReminderbackend.Util.Types.roleToStr;
import static cs3337.MedReminderbackend.Util.Types.strToRoles;


public class Users
{
    
    public Users(
        Integer id,
        Doctors doc, Patients pat,
        Integer medId,
        String username, String authHash,
        Roles role
    )
    {
        this.id = id;
        this.docInfo = doc;
        this.patInfo = pat;
        this.medId = medId;
        this.username = username;
        this.authHash = authHash;
        this.role = role;
    }
    
    
    // getters & setters
    public Integer getId()
    {
        return id;
    }
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public Doctors getDoctors()
    {
        return docInfo;
    }
    public void setDoctors(Doctors docInfo)
    {
        this.docInfo = docInfo;
    }
    
    public Patients getPatients()
    {
        return patInfo;
    }
    public void setPatients(Patients patInfo)
    {
        this.patInfo = patInfo;
    }
    
    public Integer getMedId()
    {
        return medId;
    }
    public void setMedId(Integer medId)
    {
        this.medId = medId;
    }
    
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getAuthHash()
    {
        return authHash;
    }
    public void setAuthHash(String authHash)
    {
        this.authHash = authHash;
    }
    
    public Roles getRole()
    {
        return role;
    }
    public void setRole(Roles role)
    {
        this.role = role;
    }
    
    public JSONObject toJson()
    {
        String id_str = "\"id\":";
        String doc_info_str = "\"doc_info\":";
        String pat_info_str = "\"pat_info\":";
        String med_id_str = "\"med_id\":";
        String username_str = "\"username\":";
        String auth_hash_str = "\"auth_hash\":";
        String role_str = "\"role\":";
        
        
        id_str += ""+id.toString()+",";
        if (docInfo == null)
            doc_info_str += "null,";
        else
            doc_info_str += docInfo.toJson().toString()+",";
        if (patInfo == null)
            pat_info_str += "null,";
        else
            pat_info_str += patInfo.toJson().toString()+",";
        med_id_str += medId.toString()+",";
        username_str += "\""+username+"\",";
        auth_hash_str += "\""+authHash+"\",";
        role_str += "\""+roleToStr(role)+"\"";
        
        return new JSONObject(
            "{" +
            id_str +
            doc_info_str + pat_info_str +
            med_id_str +
            username_str + auth_hash_str +
            role_str + "}"
        );
    }
    public static Users fromJson(JSONObject obj)
    {
        JSONObject docinfo = null;
        JSONObject patinfo = null;
        try
        {
            docinfo = obj.getJSONObject("doc_info");
        }
        catch (Exception e)
        {
            docinfo = null;
        }
        try
        {
            patinfo = obj.getJSONObject("pat_info");
        }
        catch (Exception e)
        {
            patinfo = null;
        }
        
        return new Users(
            obj.getInt("id"),
            Doctors.fromJson(docinfo), Patients.fromJson(patinfo),
            obj.getInt("med_id"),
            obj.getString("username"), obj.getString("auth_hash"),
            strToRoles(obj.getString("role"))
        );
    }
    
    
    // private members
    private Integer id = -1;
    private Doctors docInfo = null;
    private Patients patInfo = null;
    private Integer medId = -1;
    private String username = null;
    private String authHash = null;
    private Roles role = Roles.NOROLE;
}
