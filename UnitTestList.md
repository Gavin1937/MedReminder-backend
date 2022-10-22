
# Unit Test List

This file will list all the unit test

**All tests relating to http response will also test their status and content-type**

| Suite                     | Test                                                                                     | Expect                                       |
|---------------------------|------------------------------------------------------------------------------------------|----------------------------------------------|
| GeneralApiControllerTests | [helloTest](./src/test/java/cs3337/MedReminderbackend/GeneralApiControllerTests.java)    | str: "Hello: " + current date time           |
| UtilitiesTests            | [getUnixTimestampNowTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTests.java) | int unix timstamp                            |
| UtilitiesTests            | [genJsonResponseTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTests.java)     | ResponseEntity w/ json object & array        |
| UtilitiesTests            | [genStrResponseTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTests.java)      | ResponseEntity w/ str                        |
| UtilitiesTests            | [genSecretTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTests.java)           | gen 3 secrets & test their length & charters |
| MedReminderDBTests        | [init](./src/test/java/cs3337/MedReminderbackend/MedReminderDBTests.java)                | initialize db connection                     |
