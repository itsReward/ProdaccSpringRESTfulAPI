## 10. **Vehicle State Checklist**

This section of the API handles operations related to the Vehicle State Checklist, which is used to track the state of a vehicle before and after a job is performed.

### Get all state checklists

Path: **GET** : _{{baseUrl}}/api/v1/state-checklist/all_  
Returns all vehicle state checklists stored in the database.

**Response Format:**

```json lines
[
  {
    "id": "uuid",
    "jobCardId": "uuid",
    "jobCardName": "String",
    "millageIn": "String",
    "millageOut": "String",
    "fuelLevelIn": "String",
    "fuelLevelOut": "String",
    "created": "LocalDateTime",
    "checklist": {
      "item1": "value1",
      "item2": "value2"
      // additional items
    }
  }
]
```

### Get state checklist by ID

Path: **GET** : _{{baseUrl}}/api/v1/state-checklist/get-by-id/{id}_  
Retrieves a specific vehicle state checklist based on the `id` parameter provided.

**Response Format:**

```json lines
{
  "id": "uuid",
  "jobCardId": "uuid",
  "jobCardName": "String",
  "millageIn": "String",
  "millageOut": "String",
  "fuelLevelIn": "String",
  "fuelLevelOut": "String",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

### Get state checklist by job card ID

Path: **GET** : _{{baseUrl}}/api/v1/state-checklist/get-by-jobCard/{id}_  
Retrieves the vehicle state checklist associated with the job card that matches the `id` parameter.

**Response Format:**

```json lines
{
  "id": "uuid",
  "jobCardId": "uuid",
  "jobCardName": "String",
  "millageIn": "String",
  "millageOut": "String",
  "fuelLevelIn": "String",
  "fuelLevelOut": "String",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

### Create a new state checklist

Path: **POST** : _{{baseUrl}}/api/v1/state-checklist/new_  
Creates a new vehicle state checklist and returns the newly created checklist entity.

**Request Body:**

```json lines
{
  "jobCardId": "uuid",
  "millageIn": "String",
  "millageOut": "String",
  "fuelLevelIn": "String",
  "fuelLevelOut": "String",
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
  "millageIn": "String",
  "millageOut": "String",
  "fuelLevelIn": "String",
  "fuelLevelOut": "String",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

### Update a state checklist

Path: **PUT** : _{{baseUrl}}/api/v1/state-checklist/update/{id}_  
Updates an existing vehicle state checklist identified by the `id` parameter.

**Request Body:**

```json lines
{
  "jobCardId": "uuid",
  "millageIn": "String",
  "millageOut": "String",
  "fuelLevelIn": "String",
  "fuelLevelOut": "String",
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
  "millageIn": "String",
  "millageOut": "String",
  "fuelLevelIn": "String",
  "fuelLevelOut": "String",
  "created": "LocalDateTime",
  "checklist": {
    "item1": "value1",
    "item2": "value2"
    // additional items
  }
}
```

### Delete a state checklist

Path: **DELETE** : _{{baseUrl}}/api/v1/state-checklist/delete/{id}_  
Deletes the vehicle state checklist that matches the `id` parameter.

**Response:**

```json
{
  "message": "State checklist deleted successfully."
}
```

---