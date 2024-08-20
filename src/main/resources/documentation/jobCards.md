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