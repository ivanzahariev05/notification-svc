# Notification Service

## Description

This microservice handles user notifications for the Online Supplement Store. It sends:

- Welcome notifications upon user registration.
- Periodic promotional emails via a scheduler.

## Requirements

- Java 17+
- Maven/Gradle
- Configuration for email sending (SMTP settings) in `application.properties`

## Running the Service

Start this service **before** or **together** with the main application to enable notification features.

## Notes

- The main application can run without this service, but notification features will be disabled.
