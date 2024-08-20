## **Users**

This section of the API manages operations related to users, including creating, updating, retrieving, and deleting user records.

### Get all users

Path: **GET** : _{{baseUrl}}/api/v1/users/all_  
Returns all users stored in the database.

**Response Format:**

```json lines
[
  {
    "id": "uuid",
    "username": "String",
    "email": "String",
    "userRole": "String",
    "employeeId": "uuid",
    "employeeName": "String",
    "employeeSurname": "String"
  }
]
```

### Get user by ID

Path: **GET** : _{{baseUrl}}/api/v1/users/get/{id}_  
Retrieves a specific user based on the `id` parameter provided.

**Response Format:**

```json lines
{
  "id": "uuid",
  "username": "String",
  "email": "String",
  "userRole": "String",
  "employeeId": "uuid",
  "employeeName": "String",
  "employeeSurname": "String"
}
```

### Find user by username

Path: **GET** : _{{baseUrl}}/api/v1/users/find/{username}_  
Retrieves a user based on the `username` parameter provided.

**Response Format:**

```json lines
{
  "id": "uuid",
  "username": "String",
  "email": "String",
  "userRole": "String",
  "employeeId": "uuid",
  "employeeName": "String",
  "employeeSurname": "String"
}
```

### Find user by email

Path: **GET** : _{{baseUrl}}/api/v1/users/find/{email}_  
Retrieves a user based on the `email` parameter provided.

**Response Format:**

```json lines
{
  "id": "uuid",
  "username": "String",
  "email": "String",
  "userRole": "String",
  "employeeId": "uuid",
  "employeeName": "String",
  "employeeSurname": "String"
}
```

### Find user by employee ID

Path: **GET** : _{{baseUrl}}/api/v1/users/find/{employeeId}_  
Retrieves a user based on the `employeeId` parameter provided.

**Response Format:**

```json lines
{
  "id": "uuid",
  "username": "String",
  "email": "String",
  "userRole": "String",
  "employeeId": "uuid",
  "employeeName": "String",
  "employeeSurname": "String"
}
```

### Create a new user

Path: **POST** : _{{baseUrl}}/api/v1/users/new_  
Creates a new user and returns the newly created user entity.

**Request Body:**

```json lines
{
  "username": "String",
  "password": "String",
  "email": "String",
  "userRole": "String",
  "employeeId": "uuid"
}
```

**Response Format:**

```json lines
{
  "id": "uuid",
  "username": "String",
  "email": "String",
  "userRole": "String",
  "employeeId": "uuid",
  "employeeName": "String",
  "employeeSurname": "String"
}
```

### Update an existing user

Path: **PUT** : _{{baseUrl}}/api/v1/users/update/{id}_  
Updates an existing user identified by the `id` parameter.

**Request Body:**

```json lines
{
  "username": "String",
  "password": "String",
  "email": "String",
  "userRole": "String",
  "employeeId": "uuid"
}
```

**Response Format:**

```json lines
{
  "id": "uuid",
  "username": "String",
  "email": "String",
  "userRole": "String",
  "employeeId": "uuid",
  "employeeName": "String",
  "employeeSurname": "String"
}
```

### Delete a user

Path: **DELETE** : _{{baseUrl}}/api/v1/users/delete/{id}_  
Deletes the user that matches the `id` parameter.

**Response:**

```json
{
  "message": "User deleted successfully."
}
```

---
