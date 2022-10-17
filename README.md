
# MedReminder-backend

## Backend Application for MedReminder Project for CS 3337

[![MedReminder-backend Unit Test](https://github.com/Gavin1937/MedReminder-backend/actions/workflows/backend-unit-test.yml/badge.svg)](https://github.com/Gavin1937/MedReminder-backend/actions/workflows/backend-unit-test.yml)

## Before you start to Contribute to our project, please read this [Contribution Document](./doc/Contribution.md)

## [Api Documentation](./doc/ApiDocumentation.md)


## How to configure this application:

Before launching this backend application, you need to have following things.

* Java JRE 17, or Java JDK 17 for developers
* A running MySQL Server with valid account and password
* A **config.json** configuration file

Example **config.json**:

```json
{
    "dbinfo": {
        "db_ip": "db_ip",
        "db_username": "db_username",
        "db_pwd": "db_pwd",
        "hospital": "hospital_db_name",
        "med_reminder": "med_reminder_db_name"
    },
    "log_filepath": "/path/to/MedReminder-backend.log",
    "logging_level": "debug",
    "max_session_age": 0,
    "server_port": 8080
}
```

* **dbinfo**
  * **db_ip**: ip address of MySQL database
  * **db_username**: username for a valid account in the database
  * **db_pwd**: password for your database account
  * **hospital**: name for hospital database
  * **med_reminder**: name for med_reminder database
* **log_filepath**: path to application log file
  * if this field is null or empty, application will try to write to **"./MedReminder-backend.log"** file under current directory
* **logging_level**: Log level for the application
  * It can be any one of the following levels:
    * TRACE
    * DEBUG
    * INFO
    * WARN
    * ERROR
  * default log level is **INFO**
  * case does not matter
  * [Learn more about Log Level](https://logback.qos.ch/manual/architecture.html#basic_selection)
* **max_session_age**: int of maximum session age (in seconds)
  * this age should be positive integer
  * if supplied negative number, that means this session is already expire / you want to force this session to expire.
  * **for our own configuration, we will set the default session age to 3 days (3 * 24 * 3600 = 259200)**
* **server_port**: which port does this backend server listen to
  * default port is 8080
  * **it must be a numerical value greater than 0**
  * otherwise it will be set to default port

**You can use [example_config.json](./data/example_config.json) as reference.**

Once you finish all the configuration,

Make sure You see a **"target"** folder appear in the project root contains a **"MedReminder-backend.jar"** file

You can launch this application with following command

```sh
# For all os
java -jar target/MedReminder-backend.jar /path/to/your/config.json
```
