# Start up

### Run the back-end services (on port 8088):
- docker pull xyzassessment/backend-services
- docker run --publish 8088:8080 xyzassessment/backend-services:latest

### Run the AssessmentApplication.
By default, the application is running on port 8080: http://localhost:8080/aggregation

(some http requests are provided in the test directory)

# Decision log
- Implement story 2 first because this is the bottleneck
- Use CompletableFuture to queue requests: seems like a workable option
- Use abstract classes instead of interfaces to simplify code: i find it easier to work (testing, debugging) with abstracts
- When the cap is met, send all, instead of only 5, requests in the queue to the back-end service: this better meet our goal (not overloading the API) and makes the code a lot simpler

