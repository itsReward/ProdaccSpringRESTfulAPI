## 3. **Vehicles**

### Get All Vehicles

Path: **GET** : _{{baseUrl}}/vehicles/all_ - returns a list of all vehicle entities in the database in the Response
Vehicle Entity format

```json lines
//Response Vehicle Entity 
{
  "id": "uuid",
  "model": "String",
  "regNumber": "String",
  "make": "String",
  "color": "String",
  "chassisNumber": "String",
  "clientId": "uuid",
  "clientName": "String",
  "clientSurname": "String"
}
```

### Get a vehicle entity by id

Path: **GET** : _{{baseUrl}}/vehicles/get/:id_ - gets a vehicle entity with the id that matches the id parameter
provided and returns an error if not found

returns a Response Vehicle Entity, _(refer to get all vehicles request to see structure of response)_

### Add a new Vehicle

Path: **POST** : _{{baseUrl}}/vehicles/new_ - creates a new vehicle entity and returns the created vehicle entity if
there was no error encountered

```json lines
//Request Body
{
  "model": "String",
  "regNumber": "String",
  "make": "String",
  "color": "String",
  "chassisNumber": "String",
  "clientId": "uuid"
}

//Response Body
{
  "id": "uuid",
  "model": "String",
  "regNumber": "String",
  "make": "String",
  "color": "String",
  "chassisNumber": "String",
  "clientId": "uuid",
  "clientName": "String",
  "clientSurname": "String"
}

```

### Update Vehicle Entity

Path: **PUT** : _{{baseUrl}}/vehicles/update/:id_ - updates an existing vehicle entity in the database and returns an
error if the vehicle entity that matches the id was not found

```json lines
//Request Body
//All parameters optional except the id
{
  "model": "String",
  "regNumber": "String",
  "make": "String",
  "color": "String",
  "chassisNumber": "String",
  "clientId": "uuid"
}
//Response Body
{
  "id": "uuid",
  "model": "String",
  "regNumber": "String",
  "make": "String",
  "color": "String",
  "chassisNumber": "String",
  "clientId": "uuid",
  "clientName": "String",
  "clientSurname": "String"
}
```

### Delete Vehicle Entity

Path : **DELETE** : _{{baseUrl}}/vehicles/delete/:id_ - Deletes a vehicle entity in the database and returns a string
throws an error when the vehicle with the id is not found