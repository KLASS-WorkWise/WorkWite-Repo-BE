# API WORKWISE

## Auth :
- Register : POST  : http://localhost:8080/api/auth/register               Done
- Login : POST  : http://localhost:8080/api/auth/loginDoneDone

## Users :  + access_token :   
### username : hoangle191205@gmail.com
### password : 123456789
- GetAllUser : GET  :   http://localhost:8080/api/users                    Done
- UpdateUser : PATCH :  http://localhost:8080/api/users/{id}               Done
- DeleteUser : DELETE : DELETE http://localhost:8080/api/users/{id}


## Role Api :
- GetAllRole :   GET     http://localhost:8080/api/roles                   Done
- UpdateRole :   PATCH   http://localhost:8080/api/roles/{id}              Done
- AddRoleForUser POST    http://localhost:8080/api/roles/assign?userId={userId}&roleId={roleId}
- RemoveRoleUser : POST  http://localhost:8080/api/roles/remove?userId={userId}&roleId={roleId}