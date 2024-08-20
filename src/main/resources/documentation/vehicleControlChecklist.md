## 12. **Vehicle Control Checklist**

This section of the API manages operations related to the Vehicle Control Checklist, which tracks control procedures performed on a vehicle.

### Get all control checklists

Path: **GET** : _{{baseUrl}}/api/v1/control-checklist/all_  
Returns all vehicle control checklists stored in the database.

**Response Format:**

```json lines
[
  {
    "id": "uuid",
    "jobCardId": "uuid",
    "jobCardName": "String",
    "technicianId": "uuid",
    "technicianName": "String",
    "created": "LocalDateTime",
    "checklist": {
      "item1": "value1",
      "item2": "value2"
      // additional items
    }
  }
]
```

### Get control checklist by job card ID

Path: **GET** : _{{baseUrl}}/api/v1/control-checklist/get-by-jobCard/{id}_  
Retrieves the vehicle control checklist associated with the job card that matches the `id` parameter.

**Response Format:**

```json lines
{
  "id": "uuid",
  "jobCardId": "uuid",
  "jobCardName": "String",
  "technicianId": "uuid",
  "technicianName": "String",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

### Get control checklist by ID

Path: **GET** : _{{baseUrl}}/api/v1/control-checklist/get-by-id/{id}_  
Retrieves a specific vehicle control checklist based on the `id` parameter provided.

**Response Format:**

```json lines
{
  "id": "uuid",
  "jobCardId": "uuid",
  "jobCardName": "String",
  "technicianId": "uuid",
  "technicianName": "String",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

### Create a new control checklist

Path: **POST** : _{{baseUrl}}/api/v1/control-checklist/new_  
Creates a new vehicle control checklist and returns the newly created checklist entity.

**Request Body:**

```json lines
{
  "jobCardId": "uuid",
  "technicianId": "uuid",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

**Response Format:**

```json lines
{
  "id": "uuid",
  "jobCardId": "uuid",
  "jobCardName": "String",
  "technicianId": "uuid",
  "technicianName": "String",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

### Update a control checklist

Path: **PUT** : _{{baseUrl}}/api/v1/control-checklist/update/{id}_  
Updates an existing vehicle control checklist identified by the `id` parameter.

**Request Body:**

```json lines
{
  "jobCardId": "uuid",
  "technicianId": "uuid",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

**Response Format:**

```json lines
{
  "id": "uuid",
  "jobCardId": "uuid",
  "jobCardName": "String",
  "technicianId": "uuid",
  "technicianName": "String",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

### Delete a control checklist

Path: **DELETE** : _{{baseUrl}}/api/v1/control-checklist/delete/{id}_  
Deletes the vehicle control checklist that matches the `id` parameter.

**Response:**

```json
{
  "message": "Control checklist deleted successfully."
}
```

---



Here’s the drafted section for the "Users" portion of your API documentation:

---