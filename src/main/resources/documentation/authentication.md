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