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

Path : **GET** : _{{baseUrl}}/job-card-technicians/:id_ - gets all technicians assigned to a jobCard with a job id that
matches the id provided in the id parameter, trows an error if jobCard cannot be found

```json lines
[
  //list of technician uuid
  "uuid",
  ...
]
```

### Get JobCards associated with a certain technician

Path: **GET**:_{{baseUrl}}/job-card-technicians/getJobCardsByTechnician/:id_- get JobCards assigned to a technician via
technician id
provided as the parameter id, throws an error if technician with the id that matches the id does not exist

```json lines
[
  //list of jobcard uuids
  "uuid",
  ...
]
```

### Add Technician to a jobcard

Path: **POST**: _{{baseUrl}}/job-card-technicians/add-technician_ - adds a technician to JobCard Technicians, returns
a JobCard Technicians entity in the form of a Response JobCard Technicians Entity

```json lines
//Request Body
{
  "jobCardId": "uuid",
  "technicianId": "uuid"
}
//Response Body
{
  "jobCardId": "uuid",
  "technicianId": "uuid"
}
```

### Remove Technician from JobCard

Path: **DELETE**: _{{baseUrl}}/job-card-technicians/remove-technician_ - Removes a Technician from a JobCard by deleting
the record in the JobCard Technicians that matches the Request Body, returns a string if successful and throws an error
if delete not successful or there is an error somehow

```json lines
//RequestBody
{
  "technicianId": "uuid",
  "jobCardId": "uuid"
}
```