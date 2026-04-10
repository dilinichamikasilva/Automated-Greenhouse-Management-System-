<div align="center">

### Automated Greenhouse Management System

*A cloud-native microservice platform — live IoT data, automated rules, zero manual monitoring.*

<br/>

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![MySQL](https://img.shields.io/badge/MySQL-Zone%20%26%20Automation-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![MongoDB](https://img.shields.io/badge/MongoDB-Crop%20Service-47A248?style=flat-square&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![H2](https://img.shields.io/badge/H2-Sensor%20Service-blue?style=flat-square)](https://h2database.com/)
[![JWT](https://img.shields.io/badge/JWT-Secured-black?style=flat-square&logo=jsonwebtokens)](https://jwt.io/)

<br/>

**[🏗️ Architecture](#️-architecture) · [🚀 Quick Start](#-quick-start) · [🔐 Authentication](#-authentication) · [📋 API Reference](#-api-reference) · [🌱 Crop Lifecycle](#-crop-lifecycle)**

<br/>

> 🔗 **Repository:** [github.com/dilinichamikasilva/Automated-Greenhouse-Management-System-](https://github.com/dilinichamikasilva/Automated-Greenhouse-Management-System-)

</div>

---

## 🌍 Overview

AGMS is a distributed Spring Cloud microservice platform for precision greenhouse management. It connects to a **live external IoT API** (`http://104.211.95.241:8080/api`) acting as a remote thermo-hygrometer, fetches real-time temperature and humidity telemetry every **10 seconds**, and runs an automated rule engine that logs `TURN_FAN_ON`, `TURN_HEATER_ON`, or `STATUS_NORMAL` for every zone — all secured behind a JWT-validated API Gateway.

---

## 🏗️ Architecture

```
                    ┌─────────────────────────────────────────┐
                    │         External IoT API                │
                    │    http://104.211.95.241:8080/api       │
                    │  (Bearer JWT · WebFlux · REST/JSON)     │
                    └──────────────────┬──────────────────────┘
                                       │ telemetry every 10s
                                       ▼
┌──────────┐    ┌──────────────────────────────────────────────────────┐
│  Client  │    │                                                      │
│ (Postman)│───▶│  🛡️  API GATEWAY  :8080  (lk.ijse.apigateway)       │
└──────────┘    │       JWTAuthFilter · GatewayConfig routes           │
                │  /api/zones/** · /api/sensors/**                     │
                │  /api/automation/** · /api/crops/**                  │
                └────┬──────────────┬──────────────┬───────────┬───────┘
                     │              │              │           │
              ┌──────▼──┐  ┌────────▼──┐  ┌───────▼──┐  ┌────▼─────┐
              │  🌿 Zone │  │ 📡 Sensor │  │ ⚙️ Auto  │  │ 🌱 Crop  │
              │  :8081   │  │   :8082   │  │  :8083   │  │  :8084   │
              │  MySQL   │  │    H2     │  │  MySQL   │  │ MongoDB  │
              └──────────┘  └─────┬─────┘  └────┬─────┘  └──────────┘
                    ▲             │  OpenFeign   │ OpenFeign
                    └─────────────┘──────────────┘

              ┌────────────────────┐   ┌──────────────────────────────────┐
              │  🔍 Eureka  :8761  │   │  ⚙️  Config Server  :8888        │
              └────────────────────┘   │  Git source: this repository     │
                                       │  search-paths: config-repo/      │
                                       └──────────────────────────────────┘
```

---

## 📦 Service Map

| Service | Port | Package | Database | Role |
|---------|------|---------|----------|------|
| 🔍 **Eureka Server** | `8761` | `lk.paymedia.eurekaserver` | — | Service registry |
| ⚙️ **Config Server** | `8888` | `lk.ijse.configserver` | Git (`config-repo/`) | Centralised config |
| 🛡️ **API Gateway** | `8080` | `lk.ijse.apigateway` | — | JWT auth + routing |
| 🌿 **Zone Service** | `8081` | `lk.ijse.zoneservice` | MySQL `agms_zone_db` | Zone CRUD + IoT device registration |
| 📡 **Sensor Service** | `8082` | `lk.ijse.sensorservice` | H2 in-memory | Telemetry scheduler (10s) |
| ⚙️ **Automation Service** | `8083` | `lk.ijse.automationservice` | MySQL `agms_automation_db` | Rule engine + action logs |
| 🌱 **Crop Service** | `8084` | `lk.ijse.cropservice` | MongoDB `agms_crop_db` | Crop lifecycle management |

---

## ⚙️ Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| MongoDB | 6.0+ |

**Create the MySQL databases before starting:**

```sql
CREATE DATABASE agms_zone_db;
CREATE DATABASE agms_automation_db;
```

MongoDB `agms_crop_db` is created automatically on first write.

---

## 🚀 Quick Start

> ⚠️ **Start services in this exact order.** Infrastructure must be fully up before domain services.

### 1️⃣ Eureka Server

```bash
cd infrastructure/eureka-server
./mvnw spring-boot:run
```

✅ Dashboard → **http://localhost:8761**

---

### 2️⃣ Config Server

```bash
cd infrastructure/config-server
./mvnw spring-boot:run
```

Fetches all service properties from **`config-repo/`** in this repository via Git:

```
config-repo/
├── application.yml          ← shared: Eureka URL + JWT secret
├── zone-service.yml         ← MySQL, port 8081
├── sensor-service.yml       ← H2, IoT API credentials, port 8082
├── automation-service.yml   ← MySQL, port 8083
└── crop-service.yml         ← MongoDB, port 8084
```

✅ Health → **http://localhost:8888/actuator/health**

---

### 3️⃣ API Gateway

```bash
cd infrastructure/api-gateway
./mvnw spring-boot:run
```

✅ All external traffic enters at → **http://localhost:8080**

---

### 4️⃣ Domain Services

```bash
# Terminal 1 — Zone Management
cd services/zone-service && ./mvnw spring-boot:run

# Terminal 2 — Sensor Telemetry
cd services/sensor-service && ./mvnw spring-boot:run

# Terminal 3 — Automation & Control
cd services/automation-service && ./mvnw spring-boot:run

# Terminal 4 — Crop Inventory
cd services/crop-service && ./mvnw spring-boot:run
```

---

### 5️⃣ Verify in Eureka

Open **http://localhost:8761** — all services should appear as **UP**:

```
✅  ZONE-SERVICE          UP
✅  SENSOR-SERVICE        UP
✅  AUTOMATION-SERVICE    UP
✅  CROP-SERVICE          UP
✅  CONFIG-SERVER         UP
```


---

## 🔐 Authentication

The Gateway's `JWTAuthFilter` intercepts every request. Paths `/auth/login`, `/auth/register`, and `/eureka` are whitelisted — everything else requires a valid Bearer token.

### Get a Token

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "student123",
  "password": "password123"
}
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Token is valid for **10 hours**. Use it as:
```
Authorization: Bearer <token>
```

### Gateway Filter Flow

```
Request arrives
      │
      ▼
Path whitelisted? (/auth/login · /auth/register · /eureka)
     YES ──▶ Pass through
      │
      NO
      ▼
Authorization header missing?  ──▶ 401 Unauthorized
      │
      ▼
jwtUtil.validateToken() fails?  ──▶ 401 Unauthorized
      │
      ▼
Forward to downstream service ✅
```

---

## 🔄 End-to-End Data Flow

```
① Farmer calls POST /api/zones/create
   ZoneServiceImpl: validates minTemp < maxTemp
   IoTClient (OpenFeign): POST http://104.211.95.241:8080/api/devices
   Stores returned deviceId on Zone entity → saved to MySQL agms_zone_db

② Every 10 seconds — TelemetryScheduler fires (@Scheduled fixedDelay=10000)
   ZoneClient (OpenFeign): GET /api/zones → all zones + deviceIds
   TokenManager: provides Bearer token (auto-refreshes on 401)
   WebClient: GET /devices/telemetry/{deviceId} from IoT API
   AutomationClient (OpenFeign): POST /api/automation/process

③ AutomationServiceImpl processes telemetry
   ZoneClient (OpenFeign): GET /api/zones/{id} → fetches minTemp/maxTemp
   Rule Engine:
     currentTemp > maxTemp  →  TURN_FAN_ON
     currentTemp < minTemp  →  TURN_HEATER_ON
     else                   →  STATUS_NORMAL
   AutomationLog saved → MySQL agms_automation_db

④ Farmer reviews GET /api/automation/logs
   Full chronological action history
```

---

## 🌐 API Reference

> Base URL: `http://localhost:8080` | Header: `Authorization: Bearer <token>`

### 🌿 Zone Service `/api/zones`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/zones/create` | Create zone + register IoT device |
| `GET` | `/api/zones/get-all` | List all zones |
| `GET` | `/api/zones/{id}` | Get zone by ID |
| `PUT` | `/api/zones/{id}` | Update zone |
| `DELETE` | `/api/zones/{id}` | Delete zone |

**Request body:**
```json
{
  "name": "Tomato Zone A",
  "minTemp": 18.0,
  "maxTemp": 30.0
}
```

> ⚠️ `minTemp >= maxTemp` → `400 Bad Request` (enforced in `ZoneServiceImpl`)

**Response includes `deviceId`** registered with the External IoT API:
```json
{
  "id": 1,
  "name": "Tomato Zone A",
  "minTemp": 18.0,
  "maxTemp": 30.0,
  "deviceId": "b751b8c9-644a-484c-ba3f-be63f9b27ad0",
  "createdAt": "2024-05-01T09:30:00"
}
```

---

### 📡 Sensor Service

No REST endpoints — operates entirely via its internal scheduler.

`TelemetryScheduler` runs every **10 seconds**:
1. Fetches all zones from Zone Service via `ZoneClient` (OpenFeign)
2. Calls IoT API for each device's telemetry using `TokenManager`'s access token
3. Pushes telemetry payload to Automation Service via `AutomationClient` (OpenFeign)

**H2 Console** (development): http://localhost:8082/h2-console  
Username: `shamodha` · Password: `123456`

---

### ⚙️ Automation Service `/api/automation`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/automation/process` | Internal — receives telemetry from Sensor Service |
| `GET` | `/api/automation/logs` | All action logs ordered by `triggeredAt DESC` |

**Log entry:**
```json
{
  "id": 42,
  "zoneId": 1,
  "temperature": 33.5,
  "action": "TURN_FAN_ON",
  "triggeredAt": "2024-05-01T09:40:12"
}
```

---

### 🌱 Crop Service `/api/crops`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/crops` | Register batch (auto `SEEDLING`, auto `plantedDate`) |
| `GET` | `/api/crops` | Full inventory |
| `GET` | `/api/crops/{id}` | Get by MongoDB ID |
| `PUT` | `/api/crops/{id}/status?status=VEGETATIVE` | Advance lifecycle |

**Register request:**
```json
{
  "batchId": "BATCH-001",
  "cropName": "Cherry Tomato",
  "category": "Vegetable",
  "zoneId": 1
}
```

---

## 🌱 Crop Lifecycle

State machine enforced in `CropServiceImpl.isValidTransition()`:

```
  ┌───────────┐         ┌─────────────┐         ┌───────────┐
  │ SEEDLING  │────────▶│  VEGETATIVE │────────▶│ HARVESTED │
  └───────────┘         └─────────────┘         └───────────┘

  ✅  SEEDLING   → VEGETATIVE    valid
  ✅  VEGETATIVE → HARVESTED     valid
  ❌  Any other transition       400 Bad Request
```

---

## 🤖 Rule Engine

Evaluated in `AutomationServiceImpl.processTelemetry()` — zone thresholds fetched live per reading:

| Condition | Action |
|-----------|--------|
| `temp > zone.maxTemp` | `TURN_FAN_ON` |
| `temp < zone.minTemp` | `TURN_HEATER_ON` |
| Within range | `STATUS_NORMAL` |

---

## 🗄️ Databases

| Service | Type | Connection | Schema |
|---------|------|-----------|--------|
| Zone | MySQL | `localhost:3306/agms_zone_db` | `zones` table |
| Automation | MySQL | `localhost:3306/agms_automation_db` | `automation_logs` table |
| Sensor | H2 in-memory | `jdbc:h2:mem:sensor_db` | — |
| Crop | MongoDB | `localhost:27017/agms_crop_db` | `crops` collection |

> SQL schema available at `docs/query.sql`

---

## 📁 Project Structure

```
AGMS/
├── config-repo/                      ← Config Server Git source
│   ├── application.yml               ← Shared: Eureka + JWT secret
│   ├── zone-service.yml
│   ├── sensor-service.yml
│   ├── automation-service.yml
│   └── crop-service.yml
│
├── infrastructure/
│   ├── eureka-server/                ← lk.paymedia.eurekaserver
│   ├── config-server/                ← lk.ijse.configserver
│   └── api-gateway/                  ← lk.ijse.apigateway
│       ├── filter/JWTAuthFilter.java
│       ├── config/GatewayConfig.java ← 4 lb:// routes
│       ├── controller/AuthController ← POST /auth/login
│       └── util/JWTUtil.java
│
├── services/
│   ├── zone-service/                 ← lk.ijse.zoneservice · MySQL
│   │   ├── model/Zone.java
│   │   ├── client/IoTClient.java     ← OpenFeign → IoT API
│   │   └── service/impl/ZoneServiceImpl.java
│   │
│   ├── sensor-service/               ← lk.ijse.sensorservice · H2
│   │   ├── manager/TokenManager.java ← IoT API token lifecycle
│   │   ├── service/TelemetryScheduler.java
│   │   ├── client/ZoneClient.java    ← OpenFeign → zone-service
│   │   └── client/AutomationClient.java ← OpenFeign → automation-service
│   │
│   ├── automation-service/           ← lk.ijse.automationservice · MySQL
│   │   ├── model/AutomationLog.java
│   │   ├── client/ZoneClient.java    ← OpenFeign → zone-service
│   │   └── service/Impl/AutomationServiceImpl.java
│   │
│   └── crop-service/                 ← lk.ijse.cropservice · MongoDB
│       ├── model/Crop.java           ← @Document(collection = "crops")
│       └── service/impl/CropServiceImpl.java
│
└── docs/
    ├── eureka-status.png
    ├── query.sql
    └── postman/collections/
        └── New Collection.postman_collection.json
```

---

## 🧪 Postman Testing

Import `docs/postman/collections/New Collection.postman_collection.json`.

```
1.  POST  /auth/login                           → copy token
2.  POST  /api/zones/create                     → note returned id
3.  GET   /api/zones/get-all                    → verify zone
4.  Wait 10–15 seconds for scheduler to run
5.  GET   /api/automation/logs                  → see rule engine decisions
6.  POST  /api/crops                            → register batch (SEEDLING)
7.  PUT   /api/crops/{id}/status?status=VEGETATIVE
8.  PUT   /api/crops/{id}/status?status=HARVESTED
9.  GET   /api/crops                            → full inventory
10. Try invalid transition again                → expect 400
11. Call any endpoint without token             → expect 401
```

---

## 📚 Assignment Coverage

| Requirement | Implementation |
|-------------|----------------|
| Service Discovery (Eureka) | `infrastructure/eureka-server` |
| Centralised Config | `infrastructure/config-server` + `config-repo/` via Git |
| API Gateway Routing | `GatewayConfig` — 4 routes via `lb://` Eureka |
| JWT Security at Gateway | `JWTAuthFilter` + `JWTUtil` + `AuthController` |
| Zone Management Service | `ZoneServiceImpl` — CRUD, minTemp < maxTemp validation |
| IoT Device Registration | `IoTClient` (OpenFeign) on `POST /api/zones/create` |
| Sensor Telemetry (10s) | `TelemetryScheduler` — `@Scheduled(fixedDelay=10000)` |
| IoT Token Management | `TokenManager` — `@PostConstruct` + `refresh()` on 401 |
| Automation Rule Engine | `AutomationServiceImpl` — TURN_FAN_ON / TURN_HEATER_ON |
| Inter-service Communication | `ZoneClient`, `AutomationClient` — OpenFeign |
| Crop Lifecycle State Machine | `CropServiceImpl.isValidTransition()` |
| Polyglot Persistence | MySQL (Zone, Automation) · H2 (Sensor) · MongoDB (Crop) |
| Postman Collection | `docs/postman/collections/New Collection.postman_collection.json` |
| Eureka Screenshot | `docs/eureka-status.png` |

---

<div align="center">

*ITS 2018 — Software Architectures & Design Patterns II*

*Graduate Diploma in Software Engineering · IJSE*

```
"The greenhouse never sleeps. Neither does AGMS."
```

🌱 → 🌿 → 🌾

</div>