
# Unit Test List

This file will list all the unit test

**All tests relating to http response will also test their status and content-type**

| Suite                     | Test                                                                                     | Expect                                                           |
|---------------------------|------------------------------------------------------------------------------------------|------------------------------------------------------------------|
| UtilitiesTests            | [getUnixTimestampNowTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTests.java) | int unix timstamp                                                |
| UtilitiesTests            | [genJsonResponseTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTests.java)     | ResponseEntity w/ json object & array                            |
| UtilitiesTests            | [genStrResponseTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTests.java)      | ResponseEntity w/ str                                            |
| UtilitiesTests            | [genSecretTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTests.java)           | gen 3 secrets & test their length & charters                     |
| ModelTests                | [modelDoctorsTest](./src/test/java/cs3337/MedReminderbackend/ModelTests.java)            | getters & json func                                              |
| ModelTests                | [modelPatientsTest](./src/test/java/cs3337/MedReminderbackend/ModelTests.java)           | getters & json func                                              |
| ModelTests                | [modelUsersTest](./src/test/java/cs3337/MedReminderbackend/ModelTests.java)              | getters, setters, & json func                                    |
| ModelTests                | [modelMedicationTest](./src/test/java/cs3337/MedReminderbackend/ModelTests.java)         | getters, setters, time info, & json func                         |
| MedReminderDBTests        | [initTest](./src/test/java/cs3337/MedReminderbackend/MedReminderDBTests.java)            | initialize db connection                                         |
| GeneralApiControllerTests | [helloTest](./src/test/java/cs3337/MedReminderbackend/GeneralApiControllerTests.java)    | str: "Hello: " + current date time                               |
| GeneralApiControllerTests | [exceptTest](./src/test/java/cs3337/MedReminderbackend/GeneralApiControllerTests.java)   | json bad request (400) w/ msg: "This Is A Bad Request Exception" |
