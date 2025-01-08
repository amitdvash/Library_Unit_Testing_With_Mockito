# Library Unit Testing with Mockito

## Overview
This project focuses on implementing comprehensive **unit tests** for a Library Management System. The main objective is to ensure the robustness and reliability of the `Library` class and its associated components using JUnit5 and Mockito.

## Features
- **Unit Testing**: Exhaustive test coverage for the `Library` class.
- **Mocking and Isolation**: Usage of Mockito for isolating the `Library` class during testing.
- **Validation Tests**: Testing input validations for books, users, and operations.
- **Exception Handling**: Ensuring proper exception handling in edge cases.

## Key Components
- **Library**: Central class managing books and users.
- **Book & User**: Represent the library's entities.
- **Services**:
  - `DatabaseService`: Manages interactions with the database.
  - `NotificationService`: Sends notifications to users.
  - `ReviewService`: Retrieves book reviews.

## Exceptions
Custom exceptions were created to handle specific errors in the system:
- `BookAlreadyBorrowedException`
- `BookNotBorrowedException`
- `BookNotFoundException`
- `NoReviewsFoundException`
- `NotificationException`
- `ReviewException`
- `ReviewServiceUnavailableException`
- `UserNotRegisteredException`

## Unit Testing Highlights
- Written with **JUnit5** for structured and organized testing.
- Utilized **Mockito** for mocking dependencies like `DatabaseService` and `NotificationService`.
- Achieved comprehensive test and mutation coverage to ensure reliability.

## Main Deliverable
The main work completed is encapsulated in the `TestLibrary.java` file, which contains:
- Tests for adding books, registering users, borrowing and returning books.
- Coverage for edge cases, invalid inputs, and exception scenarios.
- Use of parameterized tests for validating multiple cases efficiently.

## Tools and Dependencies
- **JUnit5**: Test framework.
- **Mockito**: Mocking library for Java.
- **Maven**: Dependency and build management tool.

---

## How to Run
To run the tests, follow these steps:

1. **Navigate to the directory containing the `pom.xml` file**:
   Make sure you are in the correct directory where the `pom.xml` file is located.
2. **execute the following Maven command**:
  ```bash
mvn clean test
  ```
## About
This project was developed as part of the **Software Quality Engineering** course, focusing on testing practices and ensuring high-quality code through unit testing.
