## 4. **Employees**

#### Get all employee entities in the database

Path : **GET** : _{{baseUrl}}/employees/all_ - returns all employee entities in the database in the format below i.e
Response Employee Format

```json lines
//Response Employee Format
{
  "id": "uuid",
  "employeeName": "String",
  "employeeSurname": "String",
  "rating": float,
  "employeeRole": "String",
  "employeeDepartment": "String",
  "phoneNumber": "String",
  "homeAddress": "String",
  "jobCards": [
    //list of jobCards associated with the employee returned in this format
    {
      "id": "uuid",
      "name": "String"
    }
  ]
}
```

### Get employee in the with id

Path : **GET** : _{{baseUrl}}/employees/get/:id_ - gets a employee entity with the id that matches the id parameter
provided and returns an error if not found

returns a Response Employee Entity, (refer to get all employees request to see structure of response)

### Add new employees in the database

Path: **POST** : _{{baseUrl}}/employees/new_ - creates a new employee entity and returns a Response Employee Entity of
the new created entity

```json lines
//Request Body
{
  "employeeName": "String",
  "employeeSurname": "String",
  "employeeRole": "String",
  "employeeDepartment": "String",
  "phoneNumber": "String",
  "homeAddress": "String"
}

//Response Body
{
  "id": "uuid",
  "employeeName": "String",
  "employeeSurname": "String",
  "rating": float,
  "employeeRole": "String",
  "employeeDepartment": "String",
  "phoneNumber": "String",
  "homeAddress": "String",
  "jobCards": []
}
```

### Update employee in the database

Path: **POST** : _{{baseUrl}}/employees/update/:id_ - updates an existing employee entity in the database and returns an
error if the employee entity that matches the id was not found

```json lines
//Request Body
//all parameters are optional, only the parameters you provided are updated
{
  "employeeName": "String",
  "employeeSurname": "String",
  "employeeRole": "String",
  "employeeDepartment": "String",
  "phoneNumber": "String",
  "homeAddress": "String"
}

//Response Body
{
  "id": "uuid",
  "employeeName": "String",
  "employeeSurname": "String",
  "rating": float,
  "employeeRole": "String",
  "employeeDepartment": "String",
  "phoneNumber": "String",
  "homeAddress": "String",
  "jobCards": [
    //list of jobCards associated with the employee returned in this format
    {
      "id": "uuid",
      "name": "String"
    }
  ]
}
```

### Delete Employee Entity

Path : **DELETE** : _{{baseUrl}}/employees/delete/:id_ - Deletes an employee entity in the database and returns a string
throws an error when the employee with the id parameter is not found