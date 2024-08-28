## 1. **Auth**

The API uses JWT Token Authentication to give access to all endpoints except the `auth` , This is the only end point that can be
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

The access token contains the user role of the user, expiration of the token which happens after every 24
hours: the user