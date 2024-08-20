## 7. **JobCard Status**

### Get all JobCard Status Entries

Path: **GET**: _{{baseUrl}}/job-card-status/all_ - returns all JobCard Status Entries in the Response JobCard Status
Entries

```json lines
//Response Body
[
  //list of Response JobCard Status Entities
  {
    "statusId": "uuid",
    "jobId": "uuid",
    "status": "String",
    "createdAt": "LocalDateTime"
  },
  ...
]
```

### Get JobCard Status Entries

Path: **GET**: _{{baseUrl}}/job-card-status/get/:id_ - returns a list of JobCard Status Entries for a the JobCard with
the id that matches the id parameter provided on the request url

```json lines
//Response Body
[
  //list of Response JobCard Status Entities
  {
    "statusId": "uuid",
    "jobId": "uuid",
    "status": "String",
    "createdAt": "LocalDateTime"
  },
  ...
]
```

### Add new status entry

Path: **POST** : _/job-card-status/newJobCardStatus_ - add a new status entry to the JobCard Status Entries

```json lines
//Request Body
{
  "jobCardId": "uuid",
  "status": "String",
  "createdAt": "LocalDate"
  //optional, default value is the current time
}
//Response Body
{
  "statusId": "uuid",
  "jobId": "uuid",
  "status": "String",
  "createdAt": "LocalDateTime"
}
```