package cs3337.MedReminderbackend.Model;

import java.util.ArrayList;

import org.json.JSONObject;

public class Medication
{
    
    public Medication(Integer id, String name, String description, Integer frequency, Integer earlyTime, Integer lateTime){
        this.id=id;
        this.name=name;
        this.description=description;
        this.frequency=frequency;
        this.earlyTime=earlyTime;
        this.lateTime=lateTime;
    }
    
    public JSONObject toJson(){
        JSONObject newObj = new JSONObject();
        newObj.put("id", this.getId());
        newObj.put("name", this.getName());
        newObj.put("description", this.getDescription());
        newObj.put("frequency", this.getFrequency());
        newObj.put("early_time", this.getEarlyTime());
        newObj.put("late_time", this.getLateTime());
        return newObj;
    }

    public static Medication fromJson(JSONObject json){
        Medication med = new Medication
        (json.getInteger("id"), json.getString("name"), json.getString("description"),
        json.getInteger("frequency"), json.getInteger("early_time"), json.getInteger("late_time"));
        return med;
    }

    public ArrayList<Integereger> getTimeInfo(){
        ArrayList<Integereger> timeInfo = new ArrayList<Integereger>();
        timeInfo.add(this.getFrequency());
        timeInfo.add(this.getEarlyTime());
        timeInfo.add(this.getLateTime());
        return timeInfo;
    }

    public JSONObject getJsonTimeInfo(){
        JSONObject obj = new JSONObject();
        obj.put("frequency", this.frequency);
        obj.put("early_time", this.earlyTime);
        obj.put("late_time", this.lateTime);
        return obj;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getFrequency() {
        return frequency;
    }
    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
    public Integer getEarlyTime() {
        return earlyTime;
    }
    public void setEarlyTime(Integer earlyTime) {
        this.earlyTime = earlyTime;
    }
    public Integer getLateTime() {
        return lateTime;
    }
    public void setLateTime(Integer lateTime) {
        this.lateTime = lateTime;
    }

    // private members
    private Integer id = -1;
    private String name = null;
    private String description = null;
    private Integer frequency = -1;
    private Integer earlyTime = -1;
    private Integer lateTime = -1;
    
}
