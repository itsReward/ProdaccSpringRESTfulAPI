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

## 2. **JobCards**

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

## 3. Clients

#### Get all client entities in the database

Path : **GET** : _{{baseUrl}}/clients/all_ - returns all client entities in the database in the format below i.e
Response Client Format
```json lines

```











