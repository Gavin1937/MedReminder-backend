package cs3337.MedReminderbackend.Util;


public class Types
{
    public enum LogicalOperators
    {
        EQ, NE, GT, GTE, LT, LTE
    }
    
    public enum Roles
    {
        NOROLE, ADMIN, DOCTOR, PATIENT
    }
    
    public static Roles strToRoles(String role)
    {
        Roles output = Roles.NOROLE;
        if (role.toLowerCase().equals("norole"))
            output = Roles.NOROLE;
        else if (role.toLowerCase().equals("admin"))
            output = Roles.ADMIN;
        else if (role.toLowerCase().equals("doctor"))
            output = Roles.DOCTOR;
        else if (role.toLowerCase().equals("patient"))
            output = Roles.PATIENT;
        return output;
    }
    
    public static String roleToStr(Roles role)
    {
        switch (role)
        {
        case NOROLE:
            return "norole";
        case ADMIN:
            return "admin";
        case DOCTOR:
            return "doctor";
        case PATIENT:
            return "patient";
        default:
            return "norole";
        }
    }
    
    public enum Operations
    {
        ADMIN_READ, ADMIN_WRITE,
        DOCTOR_READ, DOCTOR_WRITE,
        PATIENT_READ, PATIENT_WRITE,
        NOT_DEFINED
    }
    
}
