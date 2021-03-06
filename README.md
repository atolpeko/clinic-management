# Clinic Management Service sample application 

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The Clinic Management Service allows you to manage departments, medical facilities, employees, clients and services; make registrations and store test results.

**Technology stack:** Java SE, Spring Core, Spring Boot, Spring MVC, Spring Data, Spring Security, Spring Cloud, Apache Tomcat, Jackson, Maven, JUnit, Mockito, MySQL, H2, Hibernate, Docker.

## Run locally

The first step is to build the project with Maven. Maven will build a jar for each microservice.
```
mvn clean verify 
```

Then, give the `run_all` shell script the right to execute and run it.
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

The Clinic Management Service declares 5 user roles: Admins, Top managers, Team managers, Doctors and Users. Each role has its own privileges - e.g., Top managers can edit departments and facilities, Team managers can manage their teams, Doctors have access to client registrations and test results. To authenticate and obtain an access token, hit the `oauth/token` endpoint with the following parameters:

```
grant_type: password
username: ...
password: ...
client_id: web-frontend
client_secret: 11front_45_jt590
scope: read write
```

The Clinic Management Service provides 1 pre-assigned administrator. His credentials are:

```
username: admin
password: admin_3377
```

## Monitoring Clinic Management Service
Spring Boot Admin provides statistics about all active microservices. Hit the `http://localhost:8080/admin`
endpoint to get it.

<img width="1243" alt="Screenshot 2022-04-14 at 18 59 31" src="https://user-images.githubusercontent.com/83589564/163431304-3807423c-60ba-42b0-8ebb-d8be16856f52.png">

> Note: You must have the administrator rights to access the admin server.

## Clinic Management Service Internals

Clinic management is made up of 10 microservices: <br>
* Client Service - port 5980
* Clinic Service - port 6120
* Employee Service - port 5960
* Registration Service - port 6340
* Results Service - port 6450
* Discovery Server - port 8761
* API Gateway - port 8080
* Configuration Server - port 8888
* Authentication server - port 9000
* Admin server - port 9090

You can see the architecture on the chart:

![Clinic Management System](https://user-images.githubusercontent.com/83589564/164301584-5b60fa7f-ef1e-4e1c-8097-1d282f6125c0.png)

### Configuration
At startup, each microservice contacts the Configuration server for configurations. All configurations are under version control in the [configuration repository](https://github.com/atolpeko/clinic-config).

### Security
Each microservice acts as a resource server, protecting sensitive data. 
The Auth-server implements OAuth2 authorization protocol, issueing signed JSON Web Tokens.

For instance, you can see the flow of getting metrics on the chart:

![oauth2](https://user-images.githubusercontent.com/83589564/163437551-3defc807-864f-4029-af4b-f792229dccad.png)

### Sustainability
The microservice architecture enables the rapid, frequent and reliable delivery of applications. Each microservice is delivered as a small, self-contained, easily deployable container so that it can be easily restarted in the event of a failure.

Each microservice uses a circuit breaker for remote call so that it's protected against failing or busy-waiting calls.
