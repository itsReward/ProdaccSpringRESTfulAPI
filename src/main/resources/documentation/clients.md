## 2. **Clients**

#### Get all client entities in the database

Path : **GET** : _{{baseUrl}}/clients/all_ - returns all client entities in the database in the format below i.e
Response Client Format

```json lines
//Response Client Format
{
  "id": "uuid",
  "clientName": "String",
  "clientSurname": "String",
  "gender": "String",
  "jobTitle": "String",
  "company": "String",
  "phone": "String",
  "email": "String",
  "address": "String",
  "vehicles": [
    //list of vehicles owned by the client returned in this format
    {
      "id": "uuid",
      "model": "String",
      "make": "String"
    }
  ]
}
```

### Get client in the with id

Path : **GET** : _{{baseUrl}}/clients/get/:id_ - gets a client entity with the id that matches the id parameter provided
and returns an error if not found

returns a Response Client Entity, (refer to get all clients request to see structure of response)

### Add new client in the database

Path: **POST** : _{{baseUrl}}/clients/new_ - creates a new client entity and returns a Response Client Entity of the new
created entity

```json lines
//Request Body
{
  "clientName": "String",
  "clientSurname": "String",
  "gender": "String",
  "jobTitle": "String",
  "company": "String",
  "phone": "String",
  "email": "String",
  "address": "String"
}

//Response Body
{
  "id": "uuid",
  "clientName": "String",
  "clientSurname": "String",
  "gender": "String",
  "jobTitle": "String",
  "company": "String",
  "phone": "String",
  "email": "String",
  "address": "String",
  "vehicles": []
}
```

### Update client in the database

Path: **POST** : _{{baseUrl}}/clients/new_ - updates an existing client entity in the database and returns an error if
the vehicle entity that matches the id was not found

```json lines
//Request Body
{
  "clientName": "String",
  "clientSurname": "String",
  "gender": "String",
  "jobTitle": "String",
  "company": "String",
  "phone": "String",
  "email": "String",
  "address": "String"
}

//Response Body
{
  "id": "uuid",
  "clientName": "String",
  "clientSurname": "String",
  "gender": "String",
  "jobTitle": "String",
  "company": "String",
  "phone": "String",
  "email": "String",
  "address": "String",
  "vehicles": []
}
```

### Delete Client Entity

Path : **DELETE** : _{{baseUrl}}/clients/delete/:id_ - Deletes a clients entity in the database and returns a string
throws an error when the client with the id parameter is not found