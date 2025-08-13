# 1. Auth

| Method | Endpoint                          | Name                | Status    | Body |
| ------ | --------------------------------- | ------------------- | --------- | ---- |
| POST   | `/auth/login`                     | login               | ✅ Done   | `{"username": "", "password": ""}` |
| POST   | `/auth/google-login`              | google login(test)  | ✅ Done   |      |
| POST   | `/auth/register`                  | Register            | ✅ Done   |      |
| POST   | `/auth/reset-password/{email}`    | send reset pass     | ✅ Done   |      |
| POST   | `/auth/verify-reset-code`         | verify-reset-code   | ✅ Done   |      |
| POST   | `/auth/update-password`           | update-password     | ✅ Done   |      |

# 2. Users

| Method | Endpoint           | Name       | Status    | Body |
| ------ | ------------------ | ---------- | --------- | ---- |
| GET    | `/api/users`       | getAlluser | ✅ Done   |      |
| PATCH  | `/api/users/{id}`  | getByid    | ✅ Done   |      |
