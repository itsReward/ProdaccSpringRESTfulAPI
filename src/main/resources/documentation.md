# Prodacc RestFul API

This is a RESTFUL API database crud operations. The first version with the bare-bones functionality, does not
include the database synchronisation functionality

**Database Entities**

* Users
* Employees
* Clients
* Vehicles
* JobCards
* JobCardReports
* JobCardStatus
* JobCardTechnicians
* Timesheet
* VehicleControlChecklist
* VehicleServiceChecklist
* VehicleStateChecklist

### **REQUESTS**

the first request is the authentication endpoint.

## 1. **Auth**

The API uses JWT Token Authentication to give access to various endpoints, This is the only end point that can be
accessed without authorisation, or without the JWT Token.

Path:  **POST**: _{{baseUrl}}/auth_

``` json
  //Request Body
  {
     "username" : "username",
     "password" : "password"
  }

  //Response
  {
     "accessToken": "accessToken"
  }
   
```

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

## 5. **JobCards**

### Get a single jobcard:

Path: **GET**: _{{baseUrl}}/get/:id_ - gets a job card whose job card id matches the id on the parameter query returns a
Response JobCard

``` json lines
//Responce JobCard Format
{
        "id": "UUID",
        "jobCardName": "STRING",
        "jobCardNumber": STRING,
        "vehicleId": "UUID",
        "vehicleName": "STRING",
        "clientId": "UUID",
        "clientName": "STRING",
        "serviceAdvisorId": "UUID",
        "serviceAdvisorName": "STRING",
        "supervisorId": "UUID",
        "supervisorName": "STRING",
        "dateAndTimeIn": "LOCALDATETIME",
        "estimatedTimeOfCompletion": LOCALDATETIME,
        "dateAndTimeFrozen": LOCALDATETIME,
        "dateAndTimeClosed": LOCALDATETIME,
        "priority": BOOLEAN,
        "jobCardDeadline": LOCALDATETIME,
        "timesheets": [TIMESHEET OBJECT],
        "stateChecklistId": "UUID",
        "serviceChecklistId": "UUID",
        "controlChecklistId": "UUID"
}
```

### Get all jobcard:

Path: **GET**: _{{baseUrl}}/all_ - gets all job card entities and returns a list of job card entities in the Response
jobcard format

``` json lines
//Responce JobCard Format
{
        "id": "UUID",
        "jobCardName": "STRING",
        "jobCardNumber": STRING,
        "vehicleId": "UUID",
        "vehicleName": "STRING",
        "clientId": "UUID",
        "clientName": "STRING",
        "serviceAdvisorId": "UUID",
        "serviceAdvisorName": "STRING",
        "supervisorId": "UUID",
        "supervisorName": "STRING",
        "dateAndTimeIn": "LOCALDATETIME",
        "estimatedTimeOfCompletion": LOCALDATETIME,
        "dateAndTimeFrozen": LOCALDATETIME,
        "dateAndTimeClosed": LOCALDATETIME,
        "priority": BOOLEAN,
        "jobCardDeadline": LOCALDATETIME,
        "timesheets": [TIMESHEET OBJECT],
        "stateChecklistId": "UUID",
        "serviceChecklistId": "UUID",
        "controlChecklistId": "UUID"
}
```

### Get a new jobcard:

Path: **POST**: _{{baseUrl}}/jobCards/new_ - creates a job cards and returns the new jobCard entity in the above
Response jobcard format

```json lines
//these are the non-nullable values that have to be passed to create a new jobcard
{
  //mandatory values 
  "vehicleId": "uuid",
  "serviceAdvisorId": "uuid",
  "supervisorId": "uuid",
  "dateAndTimeIn": "LOCALDATETIME",
  //optional
  "jobCardStatus": String,
  "estimatedTimeOfCompletion": LocalDateTime,
  "dateAndTimeFrozen": LocalDateTime,
  "dateAndTimeClosed": LocalDateTime,
  "priority": Boolean,
  "jobCardDeadline": LocalDateTime
}
```

### Update Job Card

Path: **PUT**: _{{baseUrl}}/jobCards/update/:id_ - updates an existing job card with the id that matches the query id
returns an error if the jobCard with a matching id does not exist

```json lines
{
  //all values are optional 
  "vehicleId": "uuid",
  "serviceAdvisorId": "uuid",
  "supervisorId": "uuid",
  "dateAndTimeIn": "LocalDateTime",
  "jobCardStatus": "String",
  "estimatedTimeOfCompletion": "LocalDateTime",
  "dateAndTimeFrozen": "LocalDateTime",
  "dateAndTimeClosed": "LocalDateTime",
  "priority": Boolean,
  "jobCardDeadline": "LocalDateTime"
}
```

returns a Response JobCard Entity _(view get request)_

### Delete Job Card

Path: **DELETE**: _{{baseUrl}}/jobCards/delete/:id_ - delete job card with the jobcard id that matches the provided id

## 6. **JobCard Technicians**

### Get all job card technician entries

Path : **GET** : _{{baseUrl}}/job-card-technicians/allEntities_ - gets all jobcard - technician entries and returns a
list of JobCard Technician entries

```json lines
[
    {
        "jobCardId": "uuid",
        "technicianId": "uuid"
    },
]
```
### Get Technicians allocated to a certain job card

Path : **GET** : _{{baseUrl}}/job-card-technicians/:id_ - gets all technicians assigned to a jobCard with a job id o



