# Project details
### Name of the project
ordermanager
### Prerequisites
* jdk 1.8
* docker and docker-compose
* connection to internet or gradle locally installed and IDE configured properly
* **NOTE**: I have built this project on a Windows machine. It is not tested on an Unix environment.
### Api documentation
API documentation was made with Swagger tool using OpenAPI Specification v2
You can find it here: swagger/ordermanager-swagger
In order to use open https://inspector.swagger.io/ and load this file
### Quick start guide
1. Go to ./scripts and run `run.bat` for windows and `run.sh` for linux (might need execution rights)
2. Run `docker ps -a` and check status of the 3 containers (should be UP)
3. Run a command towards the API on http://localhost:8080
4. Check the MongoDB through mongo-express web interface on http://localhost:8081
5. **OPTIONAL**: To stop the services run `stop.bat` for windows and `stop.sh` for linux (might need execution rights) 
6. **NOTE**: When running API calls, in order to get the id of an object saved in the MongoDB you have 2 options:
* Check Location Header value from POST response with HttpStatus.Created
* Go to http://localhost:8081/db/ordermanager/products or http://localhost:8081/db/ordermanager/orders to check directly the MongoDB saved objects 

# Solution description
The application uses Spring Web starter and Spring Data Mongo starter projects.
For testing purposes I used Junit 5 and Mockito.
The application has following Spring profiles:
* test - used when running tests
* local - used when starting the application with ./gradle bootRun
* **NOTE**: in order to use local profile you should first start docker-compose services
* dev - used when starting the application as a service in docker-compose

# Docker images used
* ordermanager-dev - use the Dockerfile from the root of the project to build ordermanager image
* mongo-dev - use the Dockerfile from ./scripts/mongo to build this image (uses official MongoDB base image https://hub.docker.com/_/mongo)
* mongo-express - it is the web-based MongoDB admin interface; using the official image https://hub.docker.com/_/mongo-express

# How to guides
### How to build the application and run unit tests
From the root project run `gradlew build` or from IDE run `build` gradle task
### How to start application as a service in docker-compose
From the root project navigate to scripts directory and run `docker-compose up -d`
After docker-compose finishes building ordermanager and mongo-dev Docker images, 
make sure that your docker instances have started properly, basically run `docker ps -a` 
and check that Status column is UP for all of 3 containers
### How to access docker-compose services
* ordermanager-dev listens on **http://localhost:8080**
* mongo-dev exposes database port on **localhost:27018**
* **NOTE**: localhost:27018 connection is used only by the local Spring profile 
* mongo-express exposes a web interface on **http://localhost:8081**

# Choices
I used MongoDB because I find it easier to work with it than classical non-relational databases. Of course, a relational database could be used here.

# Assumptions
1. This is a backend service accessed by server side code only and so providing data for the API (e.g. an array of products) is easy to be done programmatically.
2. There can be duplicate products, meaning there is no check when adding a new product.
I could have constrained the name of the product to be unique, but this would have prevented from inserting, for example, same product from multiple marketplaces.  
3. There can be duplicate orders. Basically same buyer can order multiple times same thing.
4. Adding products to an order is done using their ObjectId. The id is used to make sure that those products are also stored in the DB. 

# Improvements
1. In order to use less system resources (e.g. threads) and to make it asynchronous I would have used reactive programming and Spring WebFlux project.
2. Spring HATEOS could be an improvement depending on how the clients interact with this service.
### Answers to assignment considerations
1. The authentication can be done using two concepts/protocols. First one, which is mandatory, is HTTPS. 
It is meant to prevent any intruder from listening to the communication between clients and ordermanger service.
The next concept can be one of the following depending on the use cases ordermanager is involved in:
* Basic authentication if the API is going to be used only internally and only by the servers I know and can control.
* Token-based authentication (preferably JWT) over OAuth2 if this service is exposed to external servers.
2. In order to make the service redundant I need to be able to scale it horizontally. This can be done with the current service because it is stateless.
There is another discussion about the MongoDB which should be deployed as a highly-available cluster distributed geographically.
A good approach would be to deploy this service as a POD into Kubernetes which can take care on replicating the service multiple times and restarting the instances that went down.
