package cs3337.MedReminderbackend;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.ArrayList;
import org.json.JSONObject;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cs3337.MedReminderbackend.Util.Types.Roles;
import cs3337.MedReminderbackend.Model.*;


@ExtendWith(SpringExtension.class)
public class ModelTests
{
    
    @Test
    @Order(1)
    void modelDoctorsTest()
        throws Exception
    {
        Doctors tar1 = new Doctors(
            1,
            "fname", "lname",
            "phone", "email"
        );
        
        assertEquals(tar1.getId(), 1);
        assertEquals(tar1.getFname(), "fname");
        assertEquals(tar1.getLname(), "lname");
        assertEquals(tar1.getPhone(), "phone");
        assertEquals(tar1.getEmail(), "email");
        
        JSONObject obj1 = tar1.toJson();
        JSONObject obj2 = new JSONObject();
        obj2.put("id", 1);
        obj2.put("fname", "fname");
        obj2.put("lname", "lname");
        obj2.put("phone", "phone");
        obj2.put("email", "email");
        
        assertEquals(obj1.toString(), obj2.toString());
        
        Doctors tar2 = Doctors.fromJson(obj2);
        
        assertEquals(tar1.getId(), tar2.getId());
        assertEquals(tar1.getFname(), tar2.getFname());
        assertEquals(tar1.getLname(), tar2.getLname());
        assertEquals(tar1.getPhone(), tar2.getPhone());
        assertEquals(tar1.getEmail(), tar2.getEmail());
    }
    
    @Test
    @Order(2)
    void modelPatientsTest()
        throws Exception
    {
        Patients tar1 = new Patients(
            1,
            "fname", "lname",
            "phone", "email",
            1
        );
        
        assertEquals(tar1.getId(), 1);
        assertEquals(tar1.getFname(), "fname");
        assertEquals(tar1.getLname(), "lname");
        assertEquals(tar1.getPhone(), "phone");
        assertEquals(tar1.getEmail(), "email");
        assertEquals(tar1.getPrimaryDoc(), 1);
        
        JSONObject obj1 = tar1.toJson();
        JSONObject obj2 = new JSONObject();
        obj2.put("id", 1);
        obj2.put("fname", "fname");
        obj2.put("lname", "lname");
        obj2.put("phone", "phone");
        obj2.put("email", "email");
        obj2.put("primary_doc", 1);
        
        assertEquals(obj1.toString(), obj2.toString());
        
        Patients tar2 = Patients.fromJson(obj2);
        
        assertEquals(tar1.getId(), tar2.getId());
        assertEquals(tar1.getFname(), tar2.getFname());
        assertEquals(tar1.getLname(), tar2.getLname());
        assertEquals(tar1.getPhone(), tar2.getPhone());
        assertEquals(tar1.getEmail(), tar2.getEmail());
        assertEquals(tar1.getPrimaryDoc(), tar2.getPrimaryDoc());
    }
    
    @Test
    @Order(3)
    void modelUsersTest()
        throws Exception
    {
        Users tar1 = new Users(
            1,
            null, null,
            1,
            "username", "Rr8x1WCQn2R7wysJsn8zTMKsamOLQxOE",
            Roles.NOROLE
        );
        
        assertEquals(tar1.getId(), 1);
        assertEquals(tar1.getDoctors(), null);
        assertEquals(tar1.getPatients(), null);
        assertEquals(tar1.getMedId(), 1);
        assertEquals(tar1.getUsername(), "username");
        assertEquals(tar1.getAuthHash(), "Rr8x1WCQn2R7wysJsn8zTMKsamOLQxOE");
        assertEquals(tar1.getRole(), Roles.NOROLE);
        
        Doctors d1 = new Doctors(
            1,
            "doc_fname", "doc_lname",
            "doc_phone", "doc_email"
        );
        Patients p1 = new Patients(
            1,
            "pat_fname", "pat_lname",
            "pat_phone", "pat_email",
            1
        );
        tar1.setId(2);
        tar1.setDoctors(d1);
        tar1.setPatients(p1);
        tar1.setMedId(2);
        tar1.setUsername("username_2");
        tar1.setAuthHash("authhash");
        tar1.setRole(Roles.ADMIN);
        
        JSONObject obj1 = tar1.toJson();
        JSONObject obj2 = new JSONObject();
        obj2.put("id", 2);
        obj2.put("doc_info", new JSONObject(
            "{\"id\":1,\"fname\":\"doc_fname\",\"lname\":\"doc_lname\",\"phone\":\"doc_phone\",\"email\":\"doc_email\"}"
        ));
        obj2.put("pat_info", new JSONObject(
            "{\"id\":1,\"fname\":\"pat_fname\",\"lname\":\"pat_lname\",\"phone\":\"pat_phone\",\"email\":\"pat_email\",\"primary_doc\":1}"
        ));
        obj2.put("med_id", 2);
        obj2.put("username", "username_2");
        obj2.put("auth_hash", "authhash");
        obj2.put("role", "admin");
        
        assertEquals(obj1.toString(), obj2.toString());
        
        Users tar2 = Users.fromJson(obj2);
        
        assertEquals(tar1.getId(), tar2.getId());
        
        assertEquals(tar1.getDoctors().getId(), tar2.getDoctors().getId());
        assertEquals(tar1.getDoctors().getFname(), tar2.getDoctors().getFname());
        assertEquals(tar1.getDoctors().getLname(), tar2.getDoctors().getLname());
        assertEquals(tar1.getDoctors().getPhone(), tar2.getDoctors().getPhone());
        assertEquals(tar1.getDoctors().getEmail(), tar2.getDoctors().getEmail());
        
        assertEquals(tar1.getPatients().getId(), tar2.getPatients().getId());
        assertEquals(tar1.getPatients().getFname(), tar2.getPatients().getFname());
        assertEquals(tar1.getPatients().getLname(), tar2.getPatients().getLname());
        assertEquals(tar1.getPatients().getPhone(), tar2.getPatients().getPhone());
        assertEquals(tar1.getPatients().getEmail(), tar2.getPatients().getEmail());
        
        assertEquals(tar1.getMedId(), tar2.getMedId());
        assertEquals(tar1.getUsername(), tar2.getUsername());
        assertEquals(tar1.getAuthHash(), tar2.getAuthHash());
        assertEquals(tar1.getRole(), tar2.getRole());
    }
    
    @Test
    @Order(4)
    void modelMedicationTest()
        throws Exception
    {
        Medication tar1 = new Medication(
            1,
            "name", "description",
            1, 1010, 2020
        );
        
        assertEquals(tar1.getId(), 1);
        assertEquals(tar1.getName(), "name");
        assertEquals(tar1.getDescription(), "description");
        assertEquals(tar1.getFrequency(), 1);
        assertEquals(tar1.getEarlyTime(), 1010);
        assertEquals(tar1.getLateTime(), 2020);
        
        ArrayList<Integer> ti = tar1.getTimeInfo();
        assertEquals(ti.get(0), 1);
        assertEquals(ti.get(1), 1010);
        assertEquals(ti.get(2), 2020);
        
        JSONObject tijson = new JSONObject();
        tijson.put("frequency", 1);
        tijson.put("early_time", 1010);
        tijson.put("late_time", 2020);
        assertEquals(tar1.getJsonTimeInfo().toString(), tijson.toString());
        
        tar1.setId(2);
        tar1.setName("name_2");
        tar1.setDescription("description_2");
        tar1.setFrequency(2);
        tar1.setEarlyTime(3030);
        tar1.setLateTime(4040);
        
        JSONObject obj1 = tar1.toJson();
        JSONObject obj2 = new JSONObject();
        obj2.put("id", 2);
        obj2.put("name", "name_2");
        obj2.put("description", "description_2");
        obj2.put("frequency", 2);
        obj2.put("early_time", 3030);
        obj2.put("late_time", 4040);
        
        assertEquals(obj1.toString(), obj2.toString());
        
        Medication tar2 = Medication.fromJson(obj2);
        
        assertEquals(tar1.getId(), tar2.getId());
        assertEquals(tar1.getName(), tar2.getName());
        assertEquals(tar1.getDescription(), tar2.getDescription());
        assertEquals(tar1.getFrequency(), tar2.getFrequency());
        assertEquals(tar1.getEarlyTime(), tar2.getEarlyTime());
        assertEquals(tar1.getLateTime(), tar2.getLateTime());
    }
    
}
