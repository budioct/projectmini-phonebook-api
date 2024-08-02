# User API Spec
## Register User

Endpoint : POST /api/users

Request Body :

```json
{
  "username": "budioct",
  "password": "rahasia",
  "name": "budhi octaviansyah"
}
```

Response Body (Success) :

```json
{
  "status_code": 201,
  "message": "The item was created successfully",
  "data": ""
}
```

Response Body (Failed) :

```json
{
  "status_code": 400,
  "message": "Validation errors in your request",
  "errors": "username: must not be blank"
}
```

Response Body (Username Duplicate) :

```json
{
  "status_code": 400,
  "message": "The request was not valid",
  "errors": "Username is already in use"
}
```

## Get Detail User

Endpoint : GET /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "The item exist",
  "data": {
    "username": "budioct",
    "name": "budhi octaviansyah",
    "createdAt": "2024-08-02T07:34:00.59532",
    "updatedAt": "2024-08-02T07:44:01.259917"
  }
}
```

## Get List User

Endpoint : GET /api/users

Request Header :

X-API-TOKEN : Token (Mandatory)

Query Param :

- username : String, username users using like query, optional
- name : String, name users, using like query, optional
- createdAt : String, createdAt users, using like query, optional
- updatedAt : String, updatedAt users, using like query, optional
- sort: String, sort key list object data, using orderBy, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "The item exist",
  "count_data": 1,
  "data": [
    {
      "username": "budioct",
      "name": "budhi octaviansyah update",
      "createdAt": "2024-08-02T07:34:00.59532",
      "updatedAt": "2024-08-02T07:52:17.512646"
    }
  ],
  "paging": {
    "currentPage": 0,
    "totalPage": 1,
    "sizePage": 10
  }
}
```

Response Body (Failed, 401) :

```json
{
  "status_code": 401,
  "message": "The request was not valid",
  "errors": "Unauthorized"
}
```

## Update User

Endpoint : PATCH /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "name": "budhi octaviansyah update" // put if only want to update name
  "password": "new password" // put if only want to update password
}
```

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "The item was updated successfully",
  "data": {
    "username": "budioct",
    "name": "budhi octaviansyah update",
    "createdAt": "2024-08-02T07:34:00.59532",
    "updatedAt": "2024-08-02T07:44:01.259917"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "status_code": 401,
  "message": "The request was not valid",
  "errors": "Unauthorized"
}
```

## Login User

Endpoint : POST /api/auth/login

Request Body :

```json
{
  "username" : "budioct",
  "password" : "rahasia"
}
```

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "Successfully logged in",
  "data": {
    "token": "640f5b83-816b-404b-892e-b85bf3ff2bcb",
    "expiredAt": 1722559997076
  }
}
```

Response Body (Failed, 401) :

```json
{
  "status_code": 401,
  "message": "The request was not valid",
  "errors": "Username or password wrong"
}
```

## Logout User

Endpoint : DELETE /api/auth/logout

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "Successfully logged out",
  "data": ""
}
```

Response Body (Failed, 401) :

```json
{
  "status_code": 401,
  "message": "The request was not valid",
  "errors": "Unauthorized"
}
```