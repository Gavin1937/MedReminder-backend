package cs3337.MedReminderbackend.Util;


public class Types
{
    public enum LogicalOperators
    {
        NOTSTAT, EQ, NE, GT, GTE, LT, LTE
    }
    
    public static String logicalOperatorsToStr(LogicalOperators opt)
    {
        String output = "";
        switch (opt)
        {
        case EQ:
            output = "=";
            break;
        case NE:
            output = "!=";
            break;
        case GT:
            output = ">";
            break;
        case GTE:
            output = ">=";
            break;
        case LT:
            output = "<";
            break;
        case LTE:
            output = "<=";
            break;
        case NOTSTAT:
            output = "";
            break;
        }
        return output;
    }
    
    public enum SortOrder
    {
        ASC,  // ascendent
        DESC  // descendent
    }
    
    public static String sortOrderToStr(SortOrder order)
    {
        String output = "ASC";
        switch (order)
        {
        case ASC:
            output = "ASC";
            break;
        case DESC:
            output = "DESC";
            break;
        }
        return output;
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
