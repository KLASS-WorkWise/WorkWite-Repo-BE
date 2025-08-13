# 1. Auth

| Method | Endpoint             | Name           | Status    | Body mẫu |
| ------ | -------------------- | -------------- | --------- | -------- |
| GET    | `/api/users`         | Get all users  | ✅ Done   |          |
| PATCH  | `/api/users/{id}`    | Update user    | ✅ Done   | `{ "fullName": "New Name", ... }` |
| DELETE | `/api/users/{id}`    | Xóa user    | ✅ Done   |          |

# 3. Roles

| Method | Endpoint                                    | Name                | Status    | Body mẫu |
| ------ | ------------------------------------------- | ------------------- | --------- | -------- |
| GET    | `/api/roles`                                | Get all roles       | ✅ Done   |          |
| POST   | `/api/roles/assign/{userId}/{roleId}`       | Gán role cho user   | ✅ Done   |          |
| POST   | `/api/roles/remove/{userId}/{roleId}`       | Xóa role khỏi user  | ✅ Done   |          |
| POST   | `/api/roles/change/{userId}/{roleId}`       | Thay đổi role của user | ✅ Done |          |

# 4. Employers

| Method | Endpoint                  | Name               | Status    | Body mẫu |
| ------ | ------------------------- | ------------------ | --------- | -------- |
| GET    | `/api/employers`           | Get all employers  | ✅ Done   |          |
| GET   | `/api/employers/{id}`           | Lấy employer theo id    | ✅ Done   |  |
| PATCH  | `/api/employers/{id}`      | Update employer    | ✅ Done   | `{ "companyInformation": {...}, ... }` |        |

# 5. CompanyInformation API

| Method | Endpoint                                                          | Chức năng                                      | Status    | Body mẫu |
| ------ | ------------------------------------------------------------------ | ----------------------------------------------- | --------- | -------- |
| GET    | `/api/company`                                                   | Lấy danh sách công ty (phân trang)              | ✅ Done   | params: page, size |
| GET    | `/api/company/{id}`                                              | Lấy thông tin công ty theo id                   | ✅ Done   |          |
| POST   | `/api/company/{employerId}/company-info`                                      | Tạo thông tin công ty cho employer              | ✅ Done   | `{ "employee": 120, "companyName": "Công ty TNHH Công Nghệ AI", "logoUrl": "https://example.com/logo.png", "bannerUrl": "https://example.com/banner.png", "email": "contact@company.com", "phone": "0123456789", "description": "Công ty chuyên về phát triển trí tuệ nhân tạo và giải pháp phần mềm.", "lastPosted": "2025-08-12T15:30:00", "address": "123 Đường ABC, Quận 1, TP. Hồ Chí Minh", "location": "TP. Hồ Chí Minh", "website": "https://company.com", "industry": "Công nghệ thông tin" }` |
| PATCH  | `/api/companies/{employerId}/update-company-info`                                      | Cập nhật thông tin công ty                      | ✅ Done   | `{ "companyName": "......", ... }` |
| DELETE | `/api/company/{id}`                                              | Xóa thông tin công ty                           | ✅ Done   |          |
| GET    | `/api/company/search?name={name}&page={page}&size={size}`        | Tìm kiếm công ty theo tên (phân trang)          | ✅ Done   |          |


