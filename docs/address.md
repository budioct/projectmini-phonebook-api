# Address API Spec

## Create Address

Endpoint : POST /api/contacts/{contactId}/address

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "street" : "jl. cendrawasi",
  "city" : "jakarta",
  "province" : "jawa barat",
  "country" : "indonesia",
  "postalCode" : "15157"
}
```

Response Body (Success) :

```json
{
  "status_code": 201,
  "message": "The item was created successfully",
  "data": {
    "id": 1,
    "street": "jl. cendrawasi",
    "city": "jakarta",
    "province": "jawa barat",
    "country": "indonesia",
    "postalCode": "15157"
  }
}
```

Response Body (Failed) :

```json
{
  "status_code": 404,
  "message": "The request was not valid",
  "errors": "Contact is not found"
}
```

## Update Address

Endpoint : PUT /api/contacts/{contactId}/address/{addressId}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "street": "jl. cendrawasi update",
  "city": "jakarta update",
  "province": "jawa barat",
  "country": "indonesia",
  "postalCode": "15157"
}
```

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "The item was updated successfully",
  "data": {
    "id": 1,
    "street": "jl. cendrawasi update",
    "city": "jakarta update",
    "province": "jawa barat",
    "country": "indonesia",
    "postalCode": "15157"
  }
}
```

Response Body (Failed) :

```json
{
  "status_code": 404,
  "message": "The request was not valid",
  "errors": "Address is not found"
}
```

## Get Detail Address

Endpoint : GET /api/contacts/{contactId}/address/{addressId}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "The item exist",
  "data": {
    "id": 1,
    "street": "jl. cendrawasi update",
    "city": "jakarta update",
    "province": "jawa barat",
    "country": "indonesia",
    "postalCode": "15157"
  }
}
```

Response Body (Failed) :

```json
{
  "status_code": 404,
  "message": "The request was not valid",
  "errors": "Address is not found"
}
```

## Remove Address

Endpoint : DELETE /api/contacts/{contactId}/address/{addressId}

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
  "errors": "Address is not found"
}
```

## Get List Address

Endpoint : GET /api/addresses

Request Header :

X-API-TOKEN : Token (Mandatory)

Query Param :

- street : String, street users using like query, optional
- city : String, city users, using like query, optional
- province : String, province users, using like query, optional
- country : String, country users, using like query, optional
- postalCode : String, postalCode users, using like query, optional
- sort: String, sort key list object data, using orderBy, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Response Body (Success) :

```json
{
  "status_code": 200,
  "message": "The item exist",
  "count_data": 2,
  "data": [
    {
      "id": 2,
      "street": "jl. bahkti 3",
      "city": "jakarta",
      "province": "jawa barat",
      "country": "indonesia",
      "postalCode": "15153"
    },
    {
      "id": 1,
      "street": "jl. cendrawasi update",
      "city": "jakarta update",
      "province": "jawa barat",
      "country": "indonesia",
      "postalCode": "15157"
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