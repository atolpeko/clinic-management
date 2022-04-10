# Clinic Management Service sample application 

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The Clinic Management Service allows you to manage departments, medical facilities, employees, clients and services; make registrations and store test results.

**Technology stack:** Java SE, Spring Core, Spring Boot, Spring MVC, Spring Data, Spring Security, Spring Cloud, Apache Tomcat, Jackson, Maven, JUnit, Mockito, MySQL, Hibernate, Docker.

## Run locally

The first step is to build the project with Maven. Maven will build a jar for each microservice.
```
mvn clean verify 
```

Then, give the run_all shell script the right to execute and run it.
The script will create a `logs` directory to store the logs.

```
chmod +x run_all.sh 
./run_all.sh 
```

> Note: in order to run services locally without Docker, you must have a MySQL database running at port 3306.

## Run locally with Docker

The first step is to build the project with Maven. Maven will build a Docker image for each microservice and push it to your local Docker repository.

```
mvn clean install 
```

Next, run images with Docker compose. It will mount the `db-volume` volume to hold all the data.

```
docker compose config
docker compose up  
```

## Using Clinic Management Service

The Clinic Management Service serves clients through the API Gateway on port 8080, providing information dynamically through HATEOAS.

It exposes the following endpoints:
 * http://localhost:8080/clients
 * http://localhost:8080/doctors
 * http://localhost:8080/team-managers
 * http://localhost:8080/top-managers
 * http://localhost:8080/departments
 * http://localhost:8080/facilities
 * http://localhost:8080/services
 * http://localhost:8080/registrations
 * http://localhost:8080/results

All endpoints follow the REST API Resource naming conventions - e.g., hitting the `http://localhost:8080/clients/{id}` endpoint during a GET request will return the client with the specified `id`, hitting the `http://localhost:8080/clients?email={email}` during a GET request will return the client with the specified `email`, hitting the `http://localhost:8080/clients/` during a POST request will save the new client, and so on.

Clinic Management Service declares 3 user roles: Top managers, Team managers, Doctors and Users. Each role has its own privileges - e.g., Top managers can edit departments and facilities, Team managers can manage their teams, Doctors have access to client registrations and test results. To authenticate and obtain an access token, hit the `oauth/token` endpoint with the following parameters:

```
grant_type: password
username: ...
password: ...
client_id: web-frontend
client_secret: 11front_45_jt590
scope: read write
```

## Clinic Management Service Internals

Clinic management is made up of 8 microservices: <br>
* Client Service - port 5980
* Clinic Service - port 6120
* Registration Service - port 6340
* Results Service - port 6450
* Discovery Server - port 8761
* API Gateway - port 8080
* Configuration Server - port 8888
* Authentication server - port 9000

**You can see the architecture on the chart:**

![Clinic Management System](https://user-images.githubusercontent.com/83589564/162607788-5c3f8b49-2561-4aa3-b13f-0285e2bd9ba6.png)
