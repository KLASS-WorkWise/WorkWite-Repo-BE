# 1. Auth

| Method | Endpoint                          | Name                | Status    | Body |
| ------ | --------------------------------- | ------------------- | --------- | ---- |
| POST   | `/auth/login`                     | login               | ✅ Done   | `{"username": "", "password": ""}` |
| POST   | `/auth/register`                  | Register            | ✅ Done   |    |
| POST   | `/auth/reset-password/{email}`    | send reset pass     |     None   |      |
| POST   | `/auth/update-password`           | update-password     |     None   |      |

# 2. Users

| Method | Endpoint           | Name       | Status    | Body |
| ------ | ------------------ | ---------- | --------- | ---- |
| GET    | `/api/users`       | getAlluser | ✅ Done   |      |
| PATCH  | `/api/users/{id}`  | getByid    | ✅ Done   |      |


## Employers Api :


## Candidate Api :


## Job Posting Api : 
- Create a new job posting  
  POST : http://localhost:8080/api/job-postings  
  Body (JSON):  
  ```json
  {
    "employerId": 1,
    "title": "Java Developer",
    "description": "Develop backend Java applications.",
    "location": "Hanoi",
    "salaryRange": "20-30 million",
    "jobType": "Full-time",
    "category": "IT",
    "requiredSkills": "Java, Spring Boot, SQL",
    "minExperience": 2,
    "requiredDegree": "Bachelor",
    "createdAt": "2025-08-11T09:00:00",
    "endAt": "2025-09-01T23:59:59",
    "status": "active"
  }
  ```

- Get job posting details  
  GET : http://localhost:8080/api/job-postings/{id}

- Update a job posting  
  PUT : http://localhost:8080/api/job-postings/{id}  
  Body (JSON): same as create

- Delete a job posting  
  DELETE : http://localhost:8080/api/job-postings/{id}

- Search, filter, and paginate job postings  
  GET : http://localhost:8080/api/job-postings?category=IT&location=Hanoi  
  Query params: category, location, salaryRange, jobType, requiredSkills, requiredDegree, minExperience, page, size