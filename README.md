# taco-loco
A Spring Boot application for REST API service that calculates prices for orders for an imaginary food truck service, Taco Loco.

## Required Installation
* Java SDK 8 and up
* Maven 3.6.3
* Clone repo: '''git clone https://github.com/howardwu1/taco-loco.git'''

## Running the Spring Boot REST service
You can either run '''mvn spring-boot:run'''
OR
'''mvn clean package''' then when that completes, run '''java -jar ./target/pricingCalculator-0.0.1-SNAPSHOT.jar'''
When that boots up you can access the page by going to http://localhost:8080/ and call commands. Check out the Swagger Doc for more details and to try out the service (under POST /total)

## Accessing the API Documentation
Access the Swagger Doc using this link when the service is running:
http://localhost:8080/swagger-ui.html
