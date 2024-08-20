## 8. **JobCard Reports**

### Get a Report by report id

Path: **GET**: _{{baseUrl}}/getReportById/:id_ - gets a job card report whose report id matches the id on the parameter
query returns a Response JobCard Report

```json lines
{
  "reportId": "uuid",
  "jobCardId": "uuid",
  "employeeId": "uuid",
  "reportType": "String",
  "jobReport": "String"
}
```

### Get a Report by job card id

Path: **GET**: _{{baseUrl}}/getJobCardReports/:id_ - gets a job card report whose job card id matches the id on the
parameter query returns a Response JobCard Report

```json lines
{
  "reportId": "uuid",
  "jobCardId": "uuid",
  "employeeId": "uuid",
  "reportType": "String",
  "jobReport": "String"
}
```

### Get all jobcard reports:

Path: **GET**: _{{baseUrl}}/getAllJobCardReports/:id_ - gets all reports and returns a list of Response JobCard Report

```json lines
[
  {
    "reportId": "uuid",
    "jobCardId": "uuid",
    "employeeId": "uuid",
    "reportType": "String",
    "jobReport": "String"
  },
  ...
]
```

### Create a new report:

Path: **POST**: _{{baseUrl}}/job-card-reports/new_ - creates a report and returns the new report entity in the above
Response Report format

```json lines
//Request Body
{
  "jobCardId": "uuid",
  "employeeId": "uuid",
  "reportType": "String",
  "jobReport": "String"
}
//Response Body
{
  "reportId": "uuid",
  "jobCardId": "uuid",
  "employeeId": "uuid",
  "reportType": "String",
  "jobReport": "String"
}
```

### Update Report

Path: **PUT**: _{{baseUrl}}/job-cards-report/update/:id_ - updates an existing report with the id that matches the query
id
returns an error if the report with a matching report id does not exist

```json lines
//Request Body
//all parameters are optional
{
  "jobCardId": "uuid",
  "employeeId": "uuid",
  "reportType": "String",
  "jobReport": "String"
}
//Response Body
{
  "reportId": "uuid",
  "jobCardId": "uuid",
  "employeeId": "uuid",
  "reportType": "String",
  "jobReport": "String"
}
```

### Delete report

Path: **DELETE**: _{{baseUrl}}/job-cards-report/delete/:id_ - delete report with the report id that matches the provided
id






