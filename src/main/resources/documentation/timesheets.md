## 9. **Timesheets**

This section of the API allows for managing timesheets, which record the working hours associated with specific job cards.

### Get all timesheets

Path: **GET** : _{{baseUrl}}/api/v1/timesheets/all_ - Returns all timesheets in the database.

**Response Format:**

```json lines
[
  {
    "id": "uuid",
    "sheetTitle": "String",
    "report": "String",
    "clockInDateAndTime": "LocalDateTime",
    "clockOutDateAndTime": "LocalDateTime",
    "jobCardId": "uuid",
    "jobCardName": "String",
    "technicianId": "uuid",
    "technicianName": "String"
  }, ...
]
```

### Get timesheet by job card ID

Path: **GET** : _{{baseUrl}}/api/v1/timesheets/jobCard/{id}_  
Gets all timesheets associated with the job card that matches the `id` parameter.

**Response Format:**

```json lines
[
  {
    "id": "uuid",
    "sheetTitle": "String",
    "report": "String",
    "clockInDateAndTime": "LocalDateTime",
    "clockOutDateAndTime": "LocalDateTime",
    "jobCardId": "uuid",
    "jobCardName": "String",
    "technicianId": "uuid",
    "technicianName": "String"
  }
]
```

### Get timesheet by ID

Path: **GET** : _{{baseUrl}}/api/v1/timesheets/get/{id}_  
Gets a timesheet entity with the ID that matches the `id` parameter.

**Response Format:**

```json lines
{
  "id": "uuid",
  "sheetTitle": "String",
  "report": "String",
  "clockInDateAndTime": "LocalDateTime",
  "clockOutDateAndTime": "LocalDateTime",
  "jobCardId": "uuid",
  "jobCardName": "String",
  "technicianId": "uuid",
  "technicianName": "String"
}
```

### Add a new timesheet

Path: **POST** : _{{baseUrl}}/api/v1/timesheets/add_  
Creates a new timesheet and returns the created timesheet entity.

**Request Body:**

```json lines
{
  "jobCardId": "uuid",
  "employeeId": "uuid",
  "clockInDateAndTime": "LocalDateTime",
  "clockOutDateAndTime": "LocalDateTime",
  "sheetTitle": "String",
  "report": "String"
}
```

**Response Format:**

```json lines
{
  "id": "uuid",
  "sheetTitle": "String",
  "report": "String",
  "clockInDateAndTime": "LocalDateTime",
  "clockOutDateAndTime": "LocalDateTime",
  "jobCardId": "uuid",
  "jobCardName": "String",
  "technicianId": "uuid",
  "technicianName": "String"
}
```

### Update a timesheet

Path: **PUT** : _{{baseUrl}}/api/v1/timesheets/update/{id}_  
Updates an existing timesheet identified by the `id` parameter.

**Request Body:**

```json lines
{
  "jobCardId": "uuid",
  "employeeId": "uuid",
  "clockInDateAndTime": "LocalDateTime",
  "clockOutDateAndTime": "LocalDateTime",
  "sheetTitle": "String",
  "report": "String"
}
```

**Response Format:**

```json lines
{
  "id": "uuid",
  "sheetTitle": "String",
  "report": "String",
  "clockInDateAndTime": "LocalDateTime",
  "clockOutDateAndTime": "LocalDateTime",
  "jobCardId": "uuid",
  "jobCardName": "String",
  "technicianId": "uuid",
  "technicianName": "String"
}
```

### Delete a timesheet

Path: **DELETE** : _{{baseUrl}}/api/v1/timesheets/delete/{id}_  
Deletes the timesheet entity that matches the `id` parameter.

**Response:**

```json
{
  "message": "Timesheet deleted successfully."
}
```

---

This draft aligns with the format used in your documentation. Adjustments can be made as needed.