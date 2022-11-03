
// global variables
let USERNAME = '';
let SECRET = '';

// helper functions
function setUsername(username='')
{
    USERNAME = username;
}

function setSecret(secret='')
{
    SECRET = secret;
}

function doGet(url='', param={})
{
    fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'username': USERNAME,
            'secret': SECRET
        }
    })
    .then((response) => response.json())
    .then((data) => console.log(data));
    return output;
}

function doGetStr(url='', param={})
{
    fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'username': USERNAME,
            'secret': SECRET
        }
    })
    .then((response) => response.text())
    .then((data) => console.log(data));
}

function doPost(url='', param={})
{
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'username': USERNAME,
            'secret': SECRET
        },
        body: JSON.stringify(param)
    })
    .then((response) => response.json())
    .then((data) => console.log(data));
}

function doPut(url='', param={})
{
    fetch(url, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'username': USERNAME,
            'secret': SECRET
        },
        body: JSON.stringify(param)
    })
    .then((response) => response.json())
    .then((data) => console.log(data));
}

function doDelete(url='', param={})
{
    fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'username': USERNAME,
            'secret': SECRET
        },
        body: JSON.stringify(param)
    })
    .then((response) => response.json())
    .then((data) => console.log(data));
}


// demo of api usage
// it rely on specific database setting to execute successfully
// so don't try to run this function directly
function demo()
{
    
    // GET /api/hello
    doGetStr('/api/hello');
    
    // GET /api/except
    doGetStr('/api/except');
    
    // POST /api/auth
    doPost('/api/auth', {username:'tdoctor1', auth_hash:'368e09984e72aadecbf982d05be26913'});
    
    // set user credential
    setUsername('tdoctor1');
    setSecret(prompt("What's user secret?"));
    
    // GET /api/user/doctor/3
    doGet('/api/user/doctor/2');
    
    // GET /api/user/patient/3
    doGet('/api/user/patient/3');
    
    // POST /api/user
    doPost('/api/user', {
        hospital_id: 100,
        med_id: 1,
        password: '1234',
        role: 'patient'
    });
    
    // GET /api/medication/1
    doGet('/api/medication/1');
    
    // POST /api/medication
    doPost('/api/medication', {
        name:'med 01',
        description:'med 01 des',
        frequency:2,
        early_time:1200,
        late_time:1400
    });
    
    // POST /api/medication/find
    doPost('/api/medication/find', {
        name: 'med 01',
        frequency: 2,
        early_time:1200,
        late_time:1400
    });
    
    // POST /api/medication/history
    doPost('/api/medication/history', {
        user_id: 3,
        med_id: 1,
        med_id_opt: '=',
        time: Math.round((new Date()).getTime()/1000),
        time_opt: '<=',
        sort_order: 'desc',
        limit: 5
    });
    
    // PUT /api/medication/history
    doPut('/api/medication/history', {
        user_id: 3,
        med_id: 1
    });
    
    // POST /api/notification
    doPost('/api/notification', {
        user_id: 3,
        med_id: 1
    });
    
}


