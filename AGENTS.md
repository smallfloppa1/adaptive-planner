# Adaptive Planner

## Project Description

The Adaptive Planner is a Spring Boot application that provides a personal planning and scheduling service through a Telegram bot. It aims to help users create and maintain an adaptive schedule that can be adjusted based on their progress and changing priorities.

The application uses a hexagonal architecture, with a clear separation between the application's core logic, its infrastructure (like the database and Telegram integration), and its presentation layer (the Telegram bot).

## Project Structure

The project is organized into the following main packages:

*   `src/main/java/com/floppahost/adaptiveplanner`: The root package for the application.
    *   `AdaptivePlannerApplication.java`: The main entry point for the Spring Boot application.
    *   `planner`: The main module for the planner application.
        *   `application`: Contains the application services and use cases.
            *   `port`: Defines the inbound and outbound ports for the application.
                *   `inbound`: Defines the use cases that can be invoked by the outside world (e.g., handling a Telegram update).
                *   `outbound`: Defines the interfaces for services that the application depends on (e.g., repositories for accessing the database).
            *   `usecase`: Implements the inbound ports (use cases).
        *   `domain`: Contains the core domain logic of the application.
            *   `dto`: Data Transfer Objects for the domain layer.
            *   `exception`: Custom exceptions for the domain layer.
            *   `model`: The core domain models (e.g., `User`, `Task`, `DayPlan`).
            *   `service`: Contains the domain services that implement the core business logic.
            *   `value`: Value objects used in the domain models.
        *   `infrastructure`: Contains the implementation of the outbound ports.
            *   `config`: Configuration for the infrastructure layer.
            *   `inbound`: Inbound adapters (e.g., REST controllers, message listeners). Currently empty.
            *   `outbound`: Outbound adapters for services that the application depends on.
                *   `persistence`: Implementation of the repositories using Spring Data JPA.
                    *   `common`: Common persistence-related classes.
                    *   `telegram`: Persistence for Telegram user data.
                    *   `user`: Persistence for application user data.
                    *   `userprofile`: Persistence for user profile data.
        *   `presentation`: Contains the presentation layer of the application.
            *   `telegrambot`: The Telegram bot implementation.
                *   `config`: Configuration for the Telegram bot.
                *   `mapper`: Mappers for converting between Telegram API objects and application DTOs.
*   `src/main/resources`: Contains the application resources.
    *   `application.yaml`: The main configuration file for the application.
    *   `db/migration`: Flyway database migration scripts.
*   `src/test`: Contains the tests for the application.

## Unit Tests Naming Conventions

Unit test methods adhere to the following conventions:

*   **Method Naming:** Test method names generally follow the pattern `should[Behavior]`, clearly describing the specific behavior or functionality being tested (e.g., `shouldAllocateStudyAndBreakBlocks`).
*   **Display Name Annotation:** Each test method is annotated with `@DisplayName`, providing a more human-readable and descriptive name for the test.
*   **Class Display Name:** Test classes themselves are also annotated with `@DisplayName`, giving a clear, descriptive title to the entire test suite for that class (e.g., `@DisplayName("BlockAllocationService Tests")`).
