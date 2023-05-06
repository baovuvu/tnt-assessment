# Start up

### Run the back-end services (on port 8088):
- docker pull xyzassessment/backend-services
- docker run --publish 8088:8080 xyzassessment/backend-services:latest

### Run the AssessmentApplication.
By default, the application is running on port 8080: http://localhost:8080/aggregation
(some http requests are provided in the test directory)

