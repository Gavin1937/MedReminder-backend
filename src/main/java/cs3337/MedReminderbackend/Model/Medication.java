package cs3337.MedReminderbackend.Model;

import java.util.ArrayList;

import org.json.JSONObject;

public class Medication {
    private int id;
    private String name;
    private String description;
    private int frequency;
    private int earlyTime;
    private int lateTime;

    public Medication(int id, String name, String description, int frequency, int earlyTime, int lateTime){
        this.id=id;
        this.name=name;
        this.description=description;
        this.frequency=frequency;
        this.earlyTime=earlyTime;
        this.lateTime=lateTime;
    }

    public JSONObject toJson(Medication obj){
        JSONObject newObj = new JSONObject();
        newObj.put("id", obj.getId());
        newObj.put("name", obj.getName());
        newObj.put("description", obj.getDescription());
        newObj.put("frequency", obj.getFrequency());
        newObj.put("early_time", obj.getEarlyTime());
        newObj.put("late_time", obj.getLateTime());
        return newObj;
    }

    public static Medication fromJson(JSONObject json){
        Medication med = new Medication
        (json.getInt("id"), json.getString("name"), json.getString("description"),
        json.getInt("frequency"), json.getInt("early_time"), json.getInt("late_time"));
        return med;
    }

    public ArrayList<Integer> getTimeInfo(){
        ArrayList<Integer> timeInfo = new ArrayList<Integer>();
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

    public int getId() {
        return id;
    }
    public void setId(int id) {
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
    public int getFrequency() {
        return frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public int getEarlyTime() {
        return earlyTime;
    }
    public void setEarlyTime(int earlyTime) {
        this.earlyTime = earlyTime;
    }
    public int getLateTime() {
        return lateTime;
    }
    public void setLateTime(int lateTime) {
        this.lateTime = lateTime;
    }

}
