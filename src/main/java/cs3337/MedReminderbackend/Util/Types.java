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
    
    public static LogicalOperators strToLogicalOperators(String opt)
    {
        LogicalOperators output = LogicalOperators.NOTSTAT;
        if (opt.equals("=") || opt.equals("=="))
            output = LogicalOperators.EQ;
        else if (opt.equals("!="))
            output = LogicalOperators.NE;
        else if (opt.equals(">"))
            output = LogicalOperators.GT;
        else if (opt.equals(">="))
            output = LogicalOperators.GTE;
        else if (opt.equals("<"))
            output = LogicalOperators.LT;
        else if (opt.equals("<="))
            output = LogicalOperators.LTE;
        else
            output = LogicalOperators.NOTSTAT;
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
    
    public static SortOrder strToSortOrder(String order)
    {
        SortOrder output = SortOrder.ASC;
        order = order.toUpperCase();
        if (order.equals("ASC"))
            output = SortOrder.ASC;
        else if (order.equals("DESC"))
            output = SortOrder.DESC;
        return output;
    }
    
    public enum Roles
    {
        ADMIN(3), DOCTOR(2), PATIENT(1), NOROLE(0);
        
        Roles(Integer roleLevel)
        {
            this.roleLevel = roleLevel;
        }
        
        // comparison functions
        public boolean isEqualTo(Roles role)
        {
            return this.roleLevel == role.roleLevel;
        }
        public boolean isHigherThan(Roles role)
        {
            return this.roleLevel > role.roleLevel;
        }
        public boolean isHigherEqual(Roles role)
        {
            return this.roleLevel >= role.roleLevel;
        }
        public boolean isLowerThan(Roles role)
        {
            return this.roleLevel < role.roleLevel;
        }
        public boolean isLowerEqual(Roles role)
        {
            return this.roleLevel <= role.roleLevel;
        }
        
        private Integer roleLevel;
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
        ADMIN_READ(5), ADMIN_WRITE(6),
        DOCTOR_READ(3), DOCTOR_WRITE(4),
        PATIENT_READ(1), PATIENT_WRITE(2),
        NOT_DEFINED(0);
        
        Operations(Integer optLevel)
        {
            this.optLevel = optLevel;
        }
        
        // comparison functions
        public boolean isEqualTo(Operations opt)
        {
            return this.optLevel == opt.optLevel;
        }
        public boolean isHigherThan(Operations opt)
        {
            return this.optLevel > opt.optLevel;
        }
        public boolean isHigherEqual(Operations opt)
        {
            return this.optLevel >= opt.optLevel;
        }
        public boolean isLowerThan(Operations opt)
        {
            return this.optLevel < opt.optLevel;
        }
        public boolean isLowerEqual(Operations opt)
        {
            return this.optLevel <= opt.optLevel;
        }
        
        private Integer optLevel;
    }
    
}
