# Contact API Spec

## Create Contact

Endpoint : POST /api/contacts

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "firstName": "hendi",
  "lastName": "wicaksono",
  "email": "hendi@test.com",
  "phone": "08143132345"
}
```

Response Body (Success) :

```json
{
    "status_code": 201,
    "message": "The item exist",
    "data": {
        "id": 1,
        "firstName": "hendi",
        "lastName": "wicaksono",
        "email": "hendi@test.com",
        "phone": "08143132345"
    }
}
```

Response Body (Failed) :

```json
{
  "status_code": 400,
  "message": "Validation errors in your request",
  "errors": "email: must be a well-formed email address"
}
```

## Update Contact

Endpoint : PUT /api/contacts/{contactId}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "firstName": "hendi update",
  "lastName": "wicaksono update",
  "email": "hendi@test.com",
  "phone": "08143132345"
}
```

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "The item was updated successfully",
  "data": {
    "id": 1,
    "firstName": "hendi update",
    "lastName": "wicaksono update",
    "email": "hendi@test.com",
    "phone": "08143132345"
  }
}
```

Response Body (Failed) :

```json
{
  "status_code": 400,
  "message": "Validation errors in your request",
  "errors": "firstName: must not be blank, email: must be a well-formed email address"
}
```

## Get Detail Contact

Endpoint : GET /api/contacts/{contactId}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "The item exist",
  "data": {
    "id": 1,
    "firstName": "hendi update",
    "lastName": "wicaksono update",
    "email": "hendi@test.com",
    "phone": "08143132345"
  }
}
```

Response Body (Failed, 404) :

```json
{
  "status_code": 404,
  "message": "The request was not valid",
  "errors": "Contact not found"
}
```

## Remove Contact

Endpoint : DELETE /api/contacts/{contactId}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "The item was deleted successfully",
  "data": ""
}
```

Response Body (Failed) :

```json
{
  "status_code": 404,
  "message": "The request was not valid",
  "errors": "Contact not found"
}
```

## Get List Contact

Endpoint : GET /api/contacts

Request Header :

X-API-TOKEN : Token (Mandatory)

Query Param :

- firstName : String, firstName users using like query, optional
- lastName : String, lastName users, using like query, optional
- email : String, email users, using like query, optional
- phone : String, phone users, using like query, optional
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
      "id": 1,
      "firstName": "hendi update",
      "lastName": "wicaksono update",
      "email": "hendi@test.com",
      "phone": "08143132345"
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