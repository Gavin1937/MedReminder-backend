package cs3337.MedReminderbackend;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
    cs3337.MedReminderbackend.MedReminderBackendApplicationTests.class,
    cs3337.MedReminderbackend.UtilitiesTests.class,
    cs3337.MedReminderbackend.ModelTests.class,
    cs3337.MedReminderbackend.HospitalDBTests.class,
    cs3337.MedReminderbackend.MedReminderDBTests.class,
    cs3337.MedReminderbackend.GeneralApiControllerTests.class
})
public class TestSuite 
{
}
