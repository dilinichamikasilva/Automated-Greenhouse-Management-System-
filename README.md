# AGMS - Automated Greenhouse Management System

A cloud-native microservices platform built with Spring Boot and Spring Cloud.

## Architecture

- **Eureka Server** (8761): Service Discovery
- **Config Server** (8888): Centralized configuration management
- **API Gateway** (8080): Single entry point with JWT security
- **Zone Service** (8081): Manages greenhouse zones and IoT devices
- **Sensor Service** (8082): Fetches telemetry data from IoT API
- **Automation Service** (8083): Rule engine for fan/heater control
- **Crop Service** (8084): Manages crop inventory and lifecycle

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL (running on localhost:3306 with `root/password`)
- Create databases: `zone_db`, `automation_db`, `crop_db`

## Startup Order

1.  **Eureka Server**
2.  **Config Server** (connects to `config-repo`)
3.  **API Gateway**
4.  **Zone Service**
5.  **Automation Service**
6.  **Sensor Service**
7.  **Crop Service**

## Security

All endpoints except `/auth/login` are secured via JWT.
- **Login**: POST `http://localhost:8080/auth/login`
- **Credentials**: `student123` / `password123`
- **Header**: `Authorization: Bearer <token>`

## IoT Integration

The Sensor service polls the external IoT API at `http://104.211.95.241:8080/api` every 10 seconds for each registered zone.

## GitHub Workflow & Anti-Plagiarism

This project follows the phased implementation plan with structured commits for each module.
