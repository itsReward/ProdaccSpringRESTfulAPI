## 11. **Vehicle Service Checklist**

This section of the API manages operations related to the Vehicle Service Checklist, which documents the service tasks performed on a vehicle.

### Get all service checklists

Path: **GET** : _{{baseUrl}}/api/v1/service-checklist/all_  
Returns all vehicle service checklists stored in the database.

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

### Get service checklist by ID

Path: **GET** : _{{baseUrl}}/api/v1/service-checklist/get/{id}_  
Retrieves a specific vehicle service checklist based on the `id` parameter provided.

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

### Get service checklist by job card ID

Path: **GET** : _{{baseUrl}}/api/v1/service-checklist/get/jobCard/{id}_  
Retrieves the vehicle service checklist associated with the job card that matches the `id` parameter.

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

### Create a new service checklist

Path: **POST** : _{{baseUrl}}/api/v1/service-checklist/new_  
Creates a new vehicle service checklist and returns the newly created checklist entity.

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

### Update a service checklist

Path: **PUT** : _{{baseUrl}}/api/v1/service-checklist/update/{id}_  
Updates an existing vehicle service checklist identified by the `id` parameter.

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

### Delete a service checklist

Path: **DELETE** : _{{baseUrl}}/api/v1/service-checklist/delete/{id}_  
Deletes the vehicle service checklist that matches the `id` parameter.

**Response:**

```json
{
  "message": "Service checklist deleted successfully."
}
```

---

