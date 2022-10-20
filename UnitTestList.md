
# Unit Test List

This file will list all the unit test

**All tests relating to http response will also test their status and content-type**

| Suite                     | Test                                                                                    | Expect                                       |
|---------------------------|-----------------------------------------------------------------------------------------|----------------------------------------------|
| GeneralApiControllerTests | [helloTest](./src/test/java/cs3337/MedReminderbackend/GeneralApiControllerTests.java)   | return str: "Hello: " + current date time    |
| UtilitiesTest             | [getUnixTimestampNowTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTest.java) | return int unix timstamp                     |
| UtilitiesTest             | [genJsonResponseTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTest.java)     | return ResponseEntity w/ json object & array |
| UtilitiesTest             | [genStrResponseTest](./src/test/java/cs3337/MedReminderbackend/UtilitiesTest.java)      | return ResponseEntity w/ str                 |
