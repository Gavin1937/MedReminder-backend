
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



