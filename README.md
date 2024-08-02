# Phonebook Application Restful API

This project is a RESTful API for an Phonebook application developed using Spring Boot, MySQL. The application allows users to manage contacts, and provides functionalities such as user authentication native, and performance testing.

## Features

### User Management
- Create users
- Update users
- Retrieve a specific user by header
- Retrieve all users with optional pagination
- User register
- User authentication

### Contacts Management
- Create contacts entries
- Update contacts entries
- Delete contacts entries
- Retrieve a specific contacts entry by ID
- Retrieve all contacts entries with optional pagination

### Addresses Management
- Create addresses entries
- Update addresses entries
- Delete addresses entries
- Retrieve a specific addresses entry by ID
- Retrieve all addresses entries with optional pagination

### Performance Testing
- Performance unit-test for all endpoint

### Error Handling
- Handle errors gracefully with custom exception handling
- Return data in a structured JSON format

### Prerequisites

- Java 21
- MySQL
- Maven
- Spring Boot
- (For Dummy Data)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/budioct/projectmini-phonebook-api.git
   cd projectmini-phonebook-api/
   ```

2. Build .jar using Maven
   ```bash
   mvn clean compile package
   ```

3. Build and run spring boot application
   ```bash
   mvn spring-boot:run
   ```

The application will run on http://localhost:8080

For API documentation on directory docs/
- user.md
- contact.md
- address.md

To make requests with Postman 
- To make requests with Postman, visit here: https://documenter.getpostman.com/view/7284698/2sA3kdBxyP
- on directory docs/ MSIG Test Candidate.postman_collection.json
