
# Unit Test List

This file will list all the unit test

**All tests relating to http response will also test their status and Content-Type**

## [UtilitiesTests](./src/test/java/cs3337/MedReminderbackend/UtilitiesTests.java)

| Test                    | Expect                                       |
|-------------------------|----------------------------------------------|
| getUnixTimestampNowTest | int unix timstamp                            |
| genJsonResponseTest     | ResponseEntity w/ json object & array        |
| genStrResponseTest      | ResponseEntity w/ str                        |
| genSecretTest           | gen 3 secrets & test their length & charters |
| getMD5Test              | gen md5 for all encoding string & raw bytes  |


## [ModelTests](./src/test/java/cs3337/MedReminderbackend/ModelTests.java)

| Test                | Expect                                   |
|---------------------|------------------------------------------|
| modelDoctorsTest    | getters & json func                      |
| modelPatientsTest   | getters & json func                      |
| modelUsersTest      | getters, setters, & json func            |
| modelMedicationTest | getters, setters, time info, & json func |


## [MedReminderDBTests](./src/test/java/cs3337/MedReminderbackend/MedReminderDBTests.java)

| Test     | Expect                   |
|----------|--------------------------|
| initTest | initialize db connection |


## [HospitalDBTests](./src/test/java/cs3337/MedReminderbackend/HospitalDBTests.java)

| Test     | Expect                   |
|----------|--------------------------|
| initTest | initialize db connection |


## [GeneralApiControllerTests](./src/test/java/cs3337/MedReminderbackend/GeneralApiControllerTests.java)

| Test       | Expect                                                                      |
|------------|-----------------------------------------------------------------------------|
| helloTest  | str: "Hello: " + current date time                                          |
| exceptTest | json bad request (400) w/ msg: "This Is A Bad Request Exception"            |
| doAuthTest | auth user "gguo1", pwd "1234", auth_hash "e62ca17bbe7d9c712a3f17b971db3301" |

