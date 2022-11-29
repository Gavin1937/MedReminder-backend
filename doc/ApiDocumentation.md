
# API Documentation

This document will explain behavior, parameter, and return value for all the API Endpoints in MedReminder-backend.

<details>
<summary><b><u><h2>How to write this API Documentation</h2></u></b></summary>

**All the API Endpoints must be add to this document**

And all the explanations should follow this format:

### Title

* Starts with a level 3 heading with http method and endpoint path closed by `backtick`
* You can use "variables" to represent path parameter or request query
* Example 01:

```
### HTTP_METHOD `/api/path`
```

* Example 02:

```
### GET `/api/getUser/{id}?filter=f`

"{id}" is a path parameter
"filter" is a request query
```

### Explanation

* After two new lines, follows a brief explanation of the endpoint
* Example:

```
### GET `/api/getUser/{id}?filter=f`

Get user information for a specific user.
```

### Parameter

* In a new level 1 bullet point, put bolded **Parameters** to start a parameter section
* In new level 2 bullet point under **Parameters**, explain all the parameters
  * Use square bracket to tell whether this parameter is [optional] or not
  * Parameter name and [optional] text should be **bold** as well
  * **Also explain whether this is a path parameter or request query**
  * If you want to further explain this parameter, you can use higher level bullet points
* Explain the behavior of this endpoint in more level 2 bullet point as well
* Example

```
* **Parameters**:
  * **id**: Path Parameter, integer value that represents a user in the database.
  * **[optional] filter**: Request Query, url encoded sub string in user's first name or last name.
    * Application will only query user that does not contain filter sub string in their first name or last name.
    * Even if we found a user by his id, we will still filter him by the filter sub string. 
```

### Returns

* In a new level 1 bullet point, put bolded **Returns** to start a return section
* In new level 2 bullet point user **Returns**, explain all the return values and behavior
  * Feel free to use higher level bullet point to fully explain the return behaviors
* Example (remove "\\" before the backtick in example):

```
* **Returns**
  * Return a json contains all the user information
  * Example:

\```json
{
    "id": integer user id,
    "hospital_id": integer id represents user's id in hospital database,
    "med_id": integer id represents user's medication,
    "role": string role name of user
}
\```

```

### Use of Examples

You should use examples to explain complex behaviors.

And also give some examples for **Parameters** and **Returns** section since those sections often contain json parameter/return.

When your example contains any kind of code or plain text, use [markdown code block](https://www.markdownguide.org/extended-syntax/#fenced-code-blocks)

A json Example is (checkout the plain text version of this section):

```json
{
    "id": integer user id,
    "hospital_id": integer id represents user's id in hospital database,
    "med_id": integer id represents user's medication,
    "role": string role name of user
}
```

* Note that I use `json` text beside three backticks to indicate this is json syntax so it can be color coded.
* Also note that I put plain text after each key to explain the key
  * Those plain text value make the whole thing an invalid json
  * but you can use them to explain each key's meaning
  * or you can provide a valid json and use bullet points below it to explain it.

### A Complete Example:

> ### GET `/api/getUser/{id}?filter=f`
> 
> Get user information for a specific user.
> 
> * **Parameters**:
>   * **id**: Path Parameter, integer value that represents a user in the database.
>   * **[optional] filter**: Request Query, url encoded sub string in user's first name or last name.
>     * Application will only query user that does not contain filter sub string in their first name or last name.
>     * Even if we found a user by his id, we will still filter him by the filter sub string. 
> 
> * **Returns**
>   * Return a json contains all the user information
>   * Example:
> 
> ```json
> {
>     "id": integer user id,
>     "hospital_id": integer id represents user's id in hospital database,
>     "med_id": integer id represents user's medication,
>     "role": string role name of user
> }
> ```

### Feel free to use other API Endpoints as your template 

</details>

## General Stuff

* All API Endpoints will start with `/api/...`
* This document will write each endpoint as: `HTTP_METHOD /api/path`
* Most of the return json will follow this structure:

```json
// if success
{
  "payload": {}, // can be a list
  "ok": true,
  "status": 200
}
```

```json
// if failed
{
  "ok": true,
  "error": str,
  "status": int http status code
}
```

* In debug mode, API return json will contain more information

* **Note that some of the return json does not follow above structure.**
  * They will follow spring boot rest controller error json message

```json
{
{
  "timestamp": str,
  "status": int http status code,
  "error": str,
  "path": str
}
}
```

* **Content-Type**
  * Most of the API requires **Content-Type: application/json**
  * You should set this Content-Type in header

* **User Sessions**
  * Most of the API requires username and secret
  * username is a string made up with lower case letters follow by a number at the end
    * the lower case letters are made up with user's first name and last name
    * If a user's first name is `Steve` and last name is `Jobs`, then his username will be `sjobs1`
    * This username is made up with the initial + last name
    * The `1` at the end will avoid duplication
    * If there are another `sjobs`, we will set his number to be `2`, so his username will be `sjobs2`
  * secret is a 32 character long random unique string made up with lower case letter, upper case letter, and numbers.
    * It will be automatically generated by backend when a user requesting [`/api/auth`](#post-apiauth)
  * **Both username and secret made up of a user session**
  * **You need to supply those two string values in your request header in order to request most of the API**
  * Example request header

```json
{
  "username": "sjobs1",
  "secret": "8Jdbmkwva53QfbtPpZ8DYXysBDBzJYbg"
}
```

* **API Operations**
  * Different type of user can perform different API Operations we provide
  * Users are divide into different **Roles**
  * And most of APIs have their **Operation Type**
  * Which **Roles** can perform which **Operation Type**

| Roles   |
|---------|
| ADMIN   |
| DOCTOR  |
| PATIENT |
| NOROLE  |


| Operations    |
|---------------|
| ADMIN_READ    |
| ADMIN_WRITE   |
| DOCTOR_READ   |
| DOCTOR_WRITE  |
| PATIENT_READ  |
| PATIENT_WRITE |
| NOT_DEFINED   |

| **Roles** | **Avaliable Operations**                               |
|-----------|--------------------------------------------------------|
| ADMIN     | All                                                    |
| DOCTOR    | DOCTOR_READ, DOCTOR_WRITE, PATIENT_READ, PATIENT_WRITE |
| PATIENT   | PATIENT_READ, PATIENT_WRITE                            |
| NOROLE    | N/A                                                    |

* Checkout this [ApiDemo.js](./ApiDemo.js) as a reference.


## API

### GET `/api/hello`

This is a testing endpoint.

* **Parameters**:
  * This endpoint does not take any parameter.
* **Returns**:
  * This endpoint will return a single string: "Hello: " + current time


### GET `/api/except`

This is a testing endpoint.

* **Parameters**:
  * This endpoint does not take any parameter.
* **Returns**:
  * This endpoint will respond with http status: `400`
  * This endpoint will return a json string:

```json
{
  "ok": false,
  "error": "This Is A Bad Request Exception",
  "status": 400
}
```

### POST `/api/auth`

Authenticate an user & generate a session for him

**Content-Type: application/json**

* **Parameters**:
  * json post request body

```json
{
  "username": str,
  "auth_hash": str
}
```

* **Returns**:

```json
// If success
{
  "payload": {
    "user_id": int,
    "expire": int unix timestamp,
    "secret": str
  },
  "ok": true,
  "status": 200
}
```

```json
// If failed
{
  "ok": false,
  "error": str error message,
  "status": 400
}
```

### GET `/api/user/{id}`

Get user info by id.

* Operation Type:
  * **ADMIN_READ**
  * User can only check himself or other users who have lower role.

* **Parameters**:
* **username** string username in request header
* **secret** string user secret in request header
* **id** [Path Parameter] Integer id of User

* **Returns**:

```json
// If success
{
  "payload": {
    "doc_info": { // can be null
      "id": int,
      "fname": str,
      "lname": str,
      "phone": str,
      "email": str
    },
    "med_id": int,
    "role": str,
    "pat_info": { // can be null
      "id": int,
      "fname": str,
      "lname": str,
      "phone": str,
      "email": str,
      "primary_doc": int
    },
    "auth_hash": str,
    "id": int,
    "username": str
  },
  "ok": bool,
  "status": 200
}
```

### GET `/api/user/me`

Get user info of current user.

* Operation Type:
  * **DOCTOR_READ** or **PATIENT_READ**

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header

* **Returns**:

```json
// If success
{
  "payload": {
    "doc_info": { // can be null
      "id": int,
      "fname": str,
      "lname": str,
      "phone": str,
      "email": str
    },
    "med_id": int,
    "role": str,
    "pat_info": { // can be null
      "id": int,
      "fname": str,
      "lname": str,
      "phone": str,
      "email": str,
      "primary_doc": int
    },
    "auth_hash": str,
    "id": int,
    "username": str
  },
  "ok": bool,
  "status": 200
}
```

### GET `/api/user/doctor/{id}`

Get doctor user info by id.

* **Operation Type:**
  * **DOCTOR_READ**
  * Doctor User can only check himself and other doctors.
  * Also handle special case: patient user find his primary doctor

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * **id** [Path Parameter] Integer id of User

* **Returns**:

```json
// If success
{
  "payload": {
    "doc_info": {
      "id": int,
      "fname": str,
      "lname": str,
      "phone": str,
      "email": str
    },
    "med_id": int,
    "role": str,
    "pat_info": null,
    "auth_hash": str,
    "id": int,
    "username": str
  },
  "ok": bool,
  "status": 200
}
```

### GET `/api/user/patient/{id}`

Get patient user info by id.

* **Operation Type:**
  * **DOCTOR_READ** or **PATIENT_READ**
  * Patient User can only check himself.
  * Doctor User can check all his patients.

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * **id** [Path Parameter] Integer id of User

* **Returns**:

```json
// If success
{
  "payload": {
    "doc_info": null,
    "med_id": int,
    "role": str,
    "pat_info": {
      "id": int,
      "fname": str,
      "lname": str,
      "phone": str,
      "email": str,
      "primary_doc": int
    },
    "auth_hash": str,
    "id": int,
    "username": str
  },
  "ok": bool,
  "status": 200
}
```

### GET `/api/user/mypatients/{page}`

Get list of patient users belong to current doctor user in users table

* Operation Type:
  * **DOCTOR_READ**

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * **page** [Path Parameter] Integer page of User list (>= 1), each page contains 50 users

* **Returns**:

```json
// If success
{
  "payload": {
    "patients": [
      {
        "doc_info": null,
        "med_id": int,
        "role": str,
        "pat_info": {
          "id": int,
          "fname": str,
          "lname": str,
          "phone": str,
          "email": str,
          "primary_doc": int
        },
        "auth_hash": str,
        "id": int,
        "username": str
      },
      ...
    ],
    "this_page": int,
    "next_page": int
  },
  "ok": bool,
  "status": 200
}
```

### GET `/api/user/mypatients/find/{page}`

Find list of patient users belong to current doctor user in users table by query parameters

* Operation Type:
  * **DOCTOR_READ**

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * **page** [Path Parameter] Integer page of User list (>= 1), each page contains 50 users
  * **fname** [Request Query] string user first name
  * **lname** [Request Query] string user last name
  * **phone** [Request Query] string user phone number in format (123) 456-789
  * **email** [Request Query] string user email

* **Returns**:

```json
// If success
{
  "payload": {
    "patients": [
      {
        "doc_info": null,
        "med_id": int,
        "role": str,
        "pat_info": {
          "id": int,
          "fname": str,
          "lname": str,
          "phone": str,
          "email": str,
          "primary_doc": int
        },
        "auth_hash": str,
        "id": int,
        "username": str
      },
      ...
    ],
    "this_page": int,
    "next_page": int
  },
  "ok": bool,
  "status": 200
}
```

### GET `/api/user/mydoctor`

Get primary doctor info of current user (patient).

* Operation Type:
  * **PATIENT_READ**

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header

* **Returns**:
```json
// If success
{
  "payload": {
    "doc_info": {
      "id": int,
      "fname": str,
      "lname": str,
      "phone": str,
      "email": str
    },
    "med_id": int,
    "role": str,
    "pat_info": null,
    "auth_hash": str,
    "id": int,
    "username": str
  },
  "ok": bool,
  "status": 200
}
```

### POST `/api/user`

Add new user to system

**Content-Type: application/json**

* **Operation Type:**
  * **DOCTOR_WRITE**

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * json post request body

```json
{
  "hospital_id": int,
  "med_id": int,
  "password": str,
  "role": str
}
```

* **Returns**

```json
// If success
{
  "payload": {
    "id": int
  },
  "ok": true,
  "status": 200
}
```

### GET `/api/medication/{id}`

Get medication info by id.

* **Operation Type:**
  * **DOCTOR_READ** or **PATIENT_READ**

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * **id** [Path Parameter] Integer id of Medication

* **Returns**:

```json
// If success
{
  "payload": {
    "id": int,
    "name": str,
    "description": str,
    "frequency": int,
    "early_time": int,
    "late_time": int
  },
  "ok": bool,
  "status": 200
}
```

### POST `/api/medication`

Add new medication to system

**Content-Type: application/json**

* **Operation Type:**
  * **DOCTOR_WRITE**

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * json post request body

```json
{
  "name": str,
  "description": str,
  "frequency": int,
  "early_time": int,
  "late_time": int
}
```

* **Returns**:

```json
// If success
{
  "payload": {
    "id": int
  },
  "ok": true,
  "status": 200
}
```

### GET `/api/medication/find`

Find medication info by supplied parameters.

* **Operation Type:**
  * **DOCTOR_READ**

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * **name** [Request Query] String medication name
  * **frequency** [Request Query] Integer medication frequency
  * **early_time** [Request Query] Integer medication early time
  * **late_time** [Request Query] Integer medication late time

* **Returns**:

```json
// If success
{
  "payload": {
    "id": int,
    "name": str,
    "description": str,
    "frequency": int,
    "early_time": int,
    "late_time": int
  },
  "ok": bool,
  "status": 200
}
```

### GET `/api/medication/history`

Find user's medication history by supplied parameters.

* **Operation Type:**
  * **DOCTOR_READ** or **PATIENT_READ**
  * User can only check his own history or other users who have lower role.

* **Logical Operators (Case Insensitive)**:

| Operation                 | String Operator |
|---------------------------|-----------------|
| Equal                     | =, ==, eq       |
| Not Equal                 | !=, ne          |
| Greater Than              | >, gt           |
| Greater Than and Equal To | >=, gte         |
| Less Than                 | <, lt           |
| Less Than and Equal To    | <=, lte         |

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * **user_id** [Request Query] Integer user id
  * **med_id** [Request Query] Integer medication id
  * **med_id_opt** [Request Query] String logical comparison operators to use with med_id
  * **time** [Optional][Request Query] Integer unix timestamp (default now)
  * **time_opt** [Optional (MUST come with "time")][Request Query] String logical comparison operators to use with time (default lte)with time
  * **sort_order** [Optional][Request Query] String "asc" or "desc" (default "asc")
  * **limit** [Optional][Request Query] Integer >= 0 int limit of result array size, -1 => query all (default 50)

* **Returns**:

```json
// If success
{
  "payload": [
    {
      "id": int,
      "user_id": int,
      "med_id": int,
      "med_time": int
    },
    ...
  ],
  "ok": bool,
  "status": 200
}
```

### PUT `/api/medication/history`

Update user's medication history by supplied parameters.

**Content-Type: application/json**

* **Operation Type:**
  * **PATIENT_WRITE**
  * User can only update his own history.

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * json post request body

```json
{
  "user_id": int,
  "med_id": int
}
```

* **Returns**:

```json
// If success
{
  "payload": {
    "user_id": int,
    "med_id": int,
    "time": int
  },
  "ok": bool,
  "status": 200
}
```

### POST `/api/notification`

Get user's notification information.

**Content-Type: application/json**

* **Operation Type:**
  * **PATIENT_READ**
  * User can only get his own notification.

* **Parameters**:
  * **username** string username in request header
  * **secret** string user secret in request header
  * json post request body

```json
{
  "user_id": int,
  "med_id": int
}
```

* **Returns**:

```json
// If success
{
  "payload": {
    "last_medication_time": int,
    "frequency": int,
    "earyly_time": int,
    "late_time": int
  },
  "ok": bool,
  "status": 200
}
```


