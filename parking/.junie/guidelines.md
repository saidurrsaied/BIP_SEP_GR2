# [cite_start]Digital Parking Management System [cite: 1]
## [cite_start]Architectural Context Document [cite: 2]
### [cite_start]Spring Modulith Backend — Implementation Specification [cite: 3]

| Attribute | Details |
| :--- | :--- |
| **Project** | [cite_start]Digital Parking Management [cite: 4] |
| **Architecture** | [cite_start]Spring Modulith — Modular Monolith [cite: 4] |
| **Framework** | [cite_start]Spring Boot 3.x + Spring Modulith 1.3.x [cite: 4] |
| **Purpose** | [cite_start]AI Agent Implementation Reference [cite: 4] |
| **Institution** | [cite_start]Fachhochschule Dortmund [cite: 4] |
| **Version** | [cite_start]1.0 — Initial Architecture [cite: 4] |

## [cite_start]1. Document Purpose and Agent Instructions [cite: 5]
[cite_start]This document is the single authoritative reference for implementing the Digital Parking Management System backend. [cite: 6] [cite_start]It is structured for consumption by AI coding agents and human developers alike. [cite: 7] [cite_start]Every implementation decision should trace back to a specification in this document. [cite: 8]

**Instructions for AI Agents**
1. [cite_start]Read this entire document before generating any code. [cite: 9]
2. [cite_start]Implement one module at a time in the order defined in Section 5. [cite: 9]
3. [cite_start]Never allow a module to import from another module's internal/ package. [cite: 9]
4. [cite_start]All cross-module communication must use only the patterns defined in Section 7. [cite: 9]
5. [cite_start]Each module must have its own repository — never query another module's database table. [cite: 9]
6. [cite_start]Run ApplicationModules.verify() after completing each module. [cite: 9]
7. [cite_start]Reference Section 8 for all domain events — do not invent new events without justification. [cite: 9]
8. [cite_start]When in doubt about a boundary, consult Section 6 (Module Contracts). [cite: 9]

## [cite_start]2. System Overview [cite: 10]

### [cite_start]2.1 Scenario [cite: 11]
[cite_start]The city of Dortmund operates multiple parking zones equipped with electric vehicle charging points. [cite: 12] Citizens use the system to search for, reserve, and occupy parking spaces. [cite_start]City administrators manage zones, spaces, and pricing policies. [cite: 13] [cite_start]The backend must handle real-time space state transitions, billing for both parking duration and EV charging usage, and outbound notifications to citizens. [cite: 14]

### [cite_start]2.2 System Actors [cite: 15]

| Actor | Type | Primary Interactions |
| :--- | :--- | :--- |
| **Citizen** | End User | [cite_start]Search spaces, make reservations, view billing history [cite: 16] |
| **City Administrator** | Internal User | [cite_start]Manage zones, spaces, pricing, monitor occupancy [cite: 16] |
| **Payment Provider** | External System | [cite_start]Processes payment transactions via REST API [cite: 16] |
| **Mapping Service** | External System | [cite_start]Provides map tile and coordinate data via REST API [cite: 16] |
| **Notification Service** | External System | [cite_start]Delivers email and SMS alerts to citizens [cite: 16] |

### [cite_start]2.3 Technology Stack [cite: 17]

| Layer | Technology | Notes |
| :--- | :--- | :--- |
| **Language** | Java 21 | [cite_start]LTS release, record types encouraged [cite: 18] |
| **Core Framework** | Spring Boot 3.x | [cite_start]Auto-configuration, embedded Tomcat [cite: 18] |
| **Modulith** | Spring Modulith 1.3.x | [cite_start]Module verification, event support [cite: 18] |
| **Persistence** | Spring Data JPA + Hibernate | [cite_start]Shared database, per-module repositories [cite: 18] |
| **Database** | PostgreSQL (prod) / H2 (test) | [cite_start]Single schema, module-prefixed tables [cite: 18] |
| **Security** | Spring Security + JWT | [cite_start]Stateless auth, role-based access [cite: 18] |
| **API** | REST / JSON | [cite_start]OpenAPI 3.0 documentation [cite: 18] |
| **Build Tool** | [cite_start]Maven | spring-modulith-bom for dependency management [cite: 18] |
| **Frontend** | React SPA | [cite_start]Citizen portal and Admin portal [cite: 18] |

## [cite_start]3. Architecture Principles [cite: 19]

### [cite_start]3.1 Modular Monolith Pattern [cite: 20]
[cite_start]The system is deployed as a single Spring Boot application JAR. [cite: 21] [cite_start]Internally, it is structured as a set of independently maintainable modules with enforced boundaries. [cite: 22] [cite_start]This is the modular monolith (Modulith) pattern — combining the operational simplicity of a monolith with the internal discipline of microservices. [cite: 23]

### [cite_start]3.2 Non-Negotiable Rules [cite: 24]

| Architectural Invariants — These Rules Must Never Be Violated |
| :--- |
| [cite_start]RULE 1: No module may import any class from another module's internal/ package. [cite: 25] |
| [cite_start]RULE 2: No module may directly query or write to another module's database tables. [cite: 25] |
| [cite_start]RULE 3: When modules must coordinate, they do so via public service APIs or domain events only. [cite: 25] |
| [cite_start]RULE 4: Entities are never passed across module boundaries — only primitive IDs (String, UUID). [cite: 25] |
| [cite_start]RULE 5: External API clients (payment, map, notification) live inside their module's internal/ package. [cite: 25] |
| [cite_start]RULE 6: The ApplicationModules.verify() test must pass at all times. [cite: 25] |

### [cite_start]3.3 Package Convention [cite: 26]
Modulith detects module boundaries entirely from the Java package structure. [cite_start]The root package is com.parking. [cite: 27] Every direct sub-package of com.parking is one module. [cite_start]Classes inside a module's internal sub-package are private to that module and inaccessible from outside. [cite: 28]

| Package Path | Visibility | Description |
| :--- | :--- | :--- |
| `com.parking.{module}/SomeService.java` | PUBLIC | [cite_start]Accessible by other modules [cite: 29] |
| `com.parking.{module}/SomeEvent.java` | PUBLIC | [cite_start]Domain events are always public [cite: 29] |
| `com.parking.{module}/internal/Anything.java` | PRIVATE | [cite_start]Blocked by Modulith verify() [cite: 29] |

### [cite_start]3.4 DDD Alignment [cite: 30]
The module structure directly maps to Domain-Driven Design bounded contexts. [cite_start]Each module owns its domain model, business logic, and persistence. [cite: 31] The ubiquitous language of each context is reflected in class and method names. [cite_start]Aggregates enforce consistency boundaries within a module. [cite: 32] [cite_start]Domain events carry facts across module boundaries without creating direct dependencies. [cite: 33]

## [cite_start]4. C4 Architecture Summary [cite: 34]

### [cite_start]4.1 Level 1 — System Context [cite: 35]
[cite_start]The Parking System sits at the center, interacting with two human actor types (Citizen, Admin) and three external software systems (Payment Provider, Mapping Service, Notification Service). [cite: 36] [cite_start]All external communication is outbound from the backend — no external system calls into the backend unprompted. [cite: 37]

### [cite_start]4.2 Level 2 — Containers [cite: 38]

| Container | Technology | Responsibility |
| :--- | :--- | :--- |
| **User Frontend** | React SPA | [cite_start]Citizen-facing UI: search, reserve, billing view [cite: 39] |
| **Admin Portal** | React SPA | [cite_start]Admin-facing UI: zone management, occupancy monitoring [cite: 39] |
| **Backend API** | Spring Modulith | [cite_start]All business logic, module orchestration, REST API [cite: 39] |
| **Database** | PostgreSQL | [cite_start]Shared relational database, single schema [cite: 39] |

### [cite_start]4.3 Level 3 — Components (Backend Modules) [cite: 40]
[cite_start]The Backend API container is decomposed into the following Spring Modulith modules. [cite: 41] [cite_start]Each row represents one module — a bounded context with its own public API surface and internal implementation. [cite: 42]

| Module | Responsibility | Actor-Facing | Type |
| :--- | :--- | :--- | :--- |
| **usermanagement** | User registration, authentication, role management | Both | [cite_start]Domain [cite: 43] |
| **zonemanagement** | Zones, spaces, occupancy tracking, map overlay | Admin + Citizen | [cite_start]Domain (Core) [cite: 43] |
| **reservation** | Reservation lifecycle, space locking, status transitions | Citizen | [cite_start]Domain [cite: 43] |
| **billing** | Invoice generation, pricing calculation, payment initiation | Citizen | [cite_start]Domain [cite: 43] |
| **notification** | Outbound alerts via email/SMS to citizens | Citizen (passive) | [cite_start]Supporting [cite: 43] |

## [cite_start]5. Module Specifications [cite: 44]
Modules must be implemented in the order listed below. [cite_start]Each module's dependencies are on modules already completed before it. [cite: 45] [cite_start]This ordering allows incremental verify() testing throughout development. [cite: 46]

[cite_start]**Implementation Order:** `usermanagement` → `zonemanagement` → `reservation` → `billing` → `notification` [cite: 47]

### [cite_start]5.1 Module: usermanagement [cite: 48]

| Element | Details |
| :--- | :--- |
| **Bounded Context:** User Identity & Access Management | This is the foundational module. It owns all concepts related to user identity, credentials, and roles. No other module is allowed to store or manage user credentials. [cite_start]Other modules reference users by their userId string only. [cite: 49] |
| **Aggregate Root:** User | [cite_start]Fields: userId (String/UUID), email (String, unique), hashedPassword (String), role (UserRole), createdAt (Instant) [cite: 49] |
| **Enumerations** | [cite_start]UserRole: CITIZEN, ADMIN [cite: 49] |
| **Public API — UserService** | [cite_start]`registerUser(email, password, role) → User`<br>`authenticateUser(email, password) → JWT token String`<br>`findUserById(userId) → User`<br>`getUserRole(userId) → UserRole` [cite: 49] |
| **Domain Events Published** | [cite_start]`UserRegisteredEvent { userId, email, role, occurredAt }` [cite: 49] |
| **Internal Package Contents** | [cite_start]UserRepository — JPA repository for User aggregate<br>PasswordHasher — BCrypt hashing utility [cite: 49] |
| **REST Endpoints** | [cite_start]POST /api/auth/register — public, registers new user<br>POST /api/auth/login — public, returns JWT token<br>GET /api/users/{userId} — protected, ADMIN only [cite: 49] |
| [cite_start]**Module Dependencies:** NONE | this module has no imports from other modules. [cite: 49] |

### [cite_start]5.2 Module: zonemanagement [cite: 50]

| Element | Details |
| :--- | :--- |
| **Bounded Context:** Parking Infrastructure, Occupancy & Map | This is the core domain module. It owns the authoritative definition of what parking zones and spaces exist, their physical locations, their real-time status, and whether they have EV charging. Occupation tracking is implemented as a sub-area within this module. [cite_start]Map overlay aggregation is also handled here as a read-only query service. [cite: 51] |
| **Aggregate Root:** ParkingZone | Fields: zoneId, name, city, address, latitude, longitude, List<ParkingSpace> spaces, pricingPolicy (Value Object)<br>ParkingSpace is owned by ParkingZone. [cite_start]It is never accessed directly from outside — always through the aggregate root. [cite: 51] |
| **Entity:** ParkingSpace | (inside ParkingZone aggregate)[cite_start]<br>Fields: spaceId, zoneId, status (SpaceStatus), hasChargingPoint (HasChargingPoint), level, spaceNumber [cite: 51] |
| **Enumerations** | [cite_start]SpaceStatus: FREE, RESERVED, OCCUPIED<br>HasChargingPoint: TRUE, FALSE [cite: 51] |
| **Value Object:** PricingPolicy | (internal)<br>Fields: hourlyRateCents (int), chargingRatePerKwhCents (int), currency (String). Owned by ParkingZone. [cite_start]Pricing logic is encapsulated here and read by the billing module via the ZoneService public API. [cite: 51] |
| **Public API — ZoneService** | [cite_start]`createZone(name, address, lat, lng) → ParkingZone`<br>`updateZone(zoneId, ...) → ParkingZone`<br>`deleteZone(zoneId) → void`<br>`addSpaceToZone(zoneId, hasChargingPoint) → ParkingSpace`<br>`updateSpaceStatus(spaceId, SpaceStatus) → void [used by reservation and occupation sub-area]`<br>`getAvailableSpaces(zoneId) → List<ParkingSpace>`<br>`getSpaceById(spaceId) → ParkingSpace`<br>`getPricingPolicy(zoneId) → PricingPolicy [called by billing module]` [cite: 51] |
| **Public API — OccupationService** | (sub-area of zonemanagement)[cite_start]<br>`markSpaceOccupied(spaceId, userId?) → void [userId is nullable for walk-ins without reservation]`<br>`markSpaceVacated(spaceId) → OccupationRecord [returns closed record with duration]`<br>`getCurrentOccupancy(spaceId) → OccupationRecord?` [cite: 51] |
| **Public API — MapOverlayService** | (sub-area of zonemanagement)[cite_start]<br>`getEnrichedMapData(zoneId) → MapData [zone coordinates + per-space status overlay]`<br>`getAllZonesMapData() → List<MapData> [full city overview for citizen map view]` [cite: 51] |
| **Domain Events Published** | [cite_start]`ZoneCreatedEvent { zoneId, name, occurredAt }`<br>`SpaceStatusChangedEvent { spaceId, zoneId, oldStatus, newStatus, occurredAt }`<br>`SpaceOccupiedEvent { spaceId, zoneId, userId?, hasChargingPoint, occurredAt }`<br>`SpaceVacatedEvent { spaceId, zoneId, userId?, hasChargingPoint, occupationDurationMinutes, occurredAt }` [cite: 51] |
| **Internal Package Contents** | [cite_start]ZoneRepository — JPA repository for ParkingZone aggregate<br>SpaceRepository — JPA repository for ParkingSpace entities<br>OccupationRepository — JPA repository for OccupationRecord<br>OccupationRecord — internal entity: recordId, spaceId, userId?, startTime, endTime?, hasChargingPoint<br>PricingPolicy — value object embedded in ParkingZone<br>MapIntegrationClient — HTTP client wrapping external Mapping Service REST API [cite: 51] |
| [cite_start]**Module Dependencies:** NONE | this module has no imports from other modules. [cite: 51] |

### [cite_start]5.3 Module: reservation [cite: 52]

| Element | Details |
| :--- | :--- |
| **Bounded Context:** Reservation Lifecycle Management | Owns the complete lifecycle of a parking reservation from creation to completion or cancellation. Coordinates with zonemanagement to lock and release spaces. [cite_start]Emits domain events that drive downstream billing and notification processes. [cite: 53] |
| **Aggregate Root:** Reservation | Fields: reservationId, userId (String — ID only, no User object), spaceId (String — ID only), zoneId (String — ID only), status (ReservationStatus), reservedFrom (Instant), reservedUntil (Instant), createdAt (Instant)<br>IMPORTANT: Reservation stores only IDs referencing entities in other modules. It never holds a User object or ParkingSpace object. [cite_start]This is the primary boundary enforcement mechanism. [cite: 53] |
| **Enumerations** | [cite_start]ReservationStatus: PENDING, CONFIRMED, CANCELLED, COMPLETED, EXPIRED [cite: 53] |
| **Public API — ReservationService** | [cite_start]`placeReservation(userId, spaceId, from, until) → Reservation`<br>→ validates space is FREE via ZoneService.getSpaceById()<br>→ updates space status to RESERVED via ZoneService.updateSpaceStatus()<br>→ publishes ReservationPlacedEvent<br>`cancelReservation(reservationId) → void`<br>→ sets status to CANCELLED, releases space to FREE via ZoneService<br>→ publishes ReservationCancelledEvent<br>`completeReservation(reservationId) → void`<br>→ sets status to COMPLETED, publishes ReservationCompletedEvent<br>`getReservation(reservationId) → Reservation`<br>`getReservationsForUser(userId) → List<Reservation>` [cite: 53] |
| **Domain Events Published** | [cite_start]`ReservationPlacedEvent { reservationId, userId, spaceId, zoneId, from, until, occurredAt }`<br>`ReservationCancelledEvent { reservationId, userId, spaceId, occurredAt }`<br>`ReservationCompletedEvent { reservationId, userId, spaceId, zoneId, durationMinutes, occurredAt }` [cite: 53] |
| **Internal Package Contents** | [cite_start]ReservationRepository — JPA repository for Reservation aggregate<br>ReservationValidator — validates time window, user eligibility, no conflicting reservations [cite: 53] |
| **Module Dependencies:** ZoneService | (public API only) [cite_start]from zonemanagement module. [cite: 53] |

### [cite_start]5.4 Module: billing [cite: 54]

| Element | Details |
| :--- | :--- |
| **Bounded Context:** Pricing, Invoicing & Payment | Responsible for all financial operations. Reacts purely to domain events — it is never called directly by other modules. [cite_start]Creates invoices, calculates costs based on pricing policy from zonemanagement, and delegates actual payment processing to an internal payment gateway client. [cite: 55] |
| **Aggregate Root:** Invoice | [cite_start]Fields: invoiceId, userId (String — ID only), reservationId (String — ID only), items (List<BillingItem>), status (InvoiceStatus), totalAmountCents (long), currency (String), createdAt (Instant), paidAt (Instant?) [cite: 55] |
| **Entity:** BillingItem | (owned by Invoice aggregate)[cite_start]<br>Fields: itemId, type (BillingItemType), description (String), amountCents (long), durationMinutes (int?) [cite: 55] |
| **Enumerations** | [cite_start]InvoiceStatus: PENDING, PAID, FAILED, REFUNDED<br>BillingItemType: PARKING, EV_CHARGING [cite: 55] |
| **Event Listeners** | (entry points into billing module)[cite_start]<br>onReservationCompleted(ReservationCompletedEvent):<br>→ fetches pricing via ZoneService.getPricingPolicy(zoneId)<br>→ calculates parking cost from durationMinutes * hourlyRate<br>→ creates Invoice with PARKING BillingItem<br>→ initiates payment via PaymentGatewayClient<br>→ publishes PaymentProcessedEvent on success<br>onSpaceVacated(SpaceVacatedEvent):<br>→ if hasChargingPoint == TRUE: adds EV_CHARGING BillingItem to existing invoice for this userId [cite: 55] |
| **Public API — BillingService** | [cite_start]`getInvoice(invoiceId) → Invoice`<br>`getUserBillingHistory(userId) → List<Invoice>` [cite: 55] |
| **Domain Events Published** | [cite_start]`PaymentProcessedEvent { invoiceId, userId, reservationId, totalAmountCents, occurredAt }` [cite: 55] |
| **Internal Package Contents** | [cite_start]InvoiceRepository — JPA repository for Invoice aggregate<br>PriceCalculator — computes cost from duration and PricingPolicy<br>PaymentGatewayClient — HTTP client wrapping external Payment Provider REST API [cite: 55] |
| **Module Dependencies:** ZoneService | (public API only) [cite_start]from zonemanagement — to fetch pricing policy. [cite: 55] |

### [cite_start]5.5 Module: notification [cite: 56]

| Element | Details |
| :--- | :--- |
| **Bounded Context:** Outbound User Notifications | A pure supporting module. It has no domain logic of its own. It translates domain events from other modules into outbound messages sent to citizens via the external Notification Service. [cite_start]It never calls other modules directly and no other module calls it directly. [cite: 57] |
| **Event Listeners** | (all entry points are event-driven)[cite_start]<br>onReservationPlaced(ReservationPlacedEvent) → send reservation confirmation to citizen<br>onReservationCancelled(ReservationCancelledEvent) → send cancellation notice to citizen<br>onPaymentProcessed(PaymentProcessedEvent) → send payment receipt to citizen<br>onSpaceOccupied(SpaceOccupiedEvent) → optional: alert users about changing availability [cite: 57] |
| **Internal Package Contents** | [cite_start]NotificationRepository — stores notification history (event, userId, sentAt, channel, status)<br>NotificationProviderClient — HTTP client wrapping external Notification Service (email/SMS) REST API [cite: 57] |
| [cite_start]**Module Dependencies:** NONE | fully event-driven, no direct module calls. [cite: 57] |

## [cite_start]6. Module Contracts & Dependency Map [cite: 58]

### [cite_start]6.1 Allowed Dependencies [cite: 59]
The table below defines every permitted cross-module dependency. [cite_start]Any dependency not listed here is forbidden and will be caught by ApplicationModules.verify(). [cite: 60]

| Calling Module | Called Module | Mechanism | Purpose |
| :--- | :--- | :--- | :--- |
| reservation | zonemanagement | Direct — ZoneService | [cite_start]Check/update space status [cite: 61] |
| billing | zonemanagement | Direct — ZoneService | [cite_start]Fetch pricing policy [cite: 61] |
| billing | reservation (via event) | ReservationCompletedEvent | [cite_start]Trigger invoice creation [cite: 61] |
| billing | zonemanagement (via event) | SpaceVacatedEvent | [cite_start]Add EV charging cost [cite: 61] |
| notification | reservation (via event) | ReservationPlacedEvent | [cite_start]Send confirmation [cite: 61] |
| notification | reservation (via event) | ReservationCancelledEvent | [cite_start]Send cancellation [cite: 61] |
| notification | billing (via event) | PaymentProcessedEvent | [cite_start]Send receipt [cite: 61] |
| notification | zonemanagement (via event) | SpaceOccupiedEvent | [cite_start]Availability alert [cite: 61] |

### [cite_start]6.2 Dependency Direction [cite: 62]

| Dependency Rules |
| :--- |
[cite_start]| usermanagement → no outgoing dependencies [cite: 63] |
[cite_start]| zonemanagement → no outgoing dependencies [cite: 63] |
[cite_start]| reservation → zonemanagement (ZoneService only) [cite: 63] |
[cite_start]| billing → zonemanagement (ZoneService only) + event listeners [cite: 63] |
[cite_start]| notification → no outgoing dependencies (event listeners only) [cite: 63] |
| **There are NO circular dependencies. [cite_start]The graph is a directed acyclic graph (DAG).** [cite: 63] |

### [cite_start]6.3 Cross-Module ID Reference Rule [cite: 64]
[cite_start]When a module needs to reference an entity owned by another module, it stores only the String ID — never the entity object itself. [cite: 65] [cite_start]The owning module's public service is called at runtime to resolve the entity when needed. [cite: 66]

| Module | Referenced Entity | How it is stored |
| :--- | :--- | :--- |
| reservation | [cite_start]User (usermanagement) | userId: String — resolved via UserService.findUserById() when needed [cite: 67] |
| reservation | [cite_start]ParkingSpace (zonemanagement) | spaceId: String — verified at reservation time via ZoneService [cite: 67] |
| billing | [cite_start]User (usermanagement) | userId: String — stored in Invoice, not resolved to User object [cite: 67] |
| billing | [cite_start]Reservation (reservation) | reservationId: String — stored in Invoice for reference only [cite: 67] |

## [cite_start]7. Domain Event Catalog [cite: 68]
All domain events are Java records with immutable fields. Events are published via Spring's ApplicationEventPublisher. [cite_start]Listeners use `@ApplicationModuleListener`. [cite: 69] [cite_start]Events are the only mechanism for cross-module communication that does not involve a direct public API call. [cite: 70]

| Event | Published By | Consumed By | Payload Fields |
| :--- | :--- | :--- | :--- |
| `UserRegisteredEvent` | usermanagement | (future use) [cite_start]| userId, email, role, occurredAt [cite: 71] |
| `ZoneCreatedEvent` | zonemanagement | (future use) [cite_start]| zoneId, name, occurredAt [cite: 71] |
| `SpaceStatusChangedEvent` | zonemanagement | (future use) [cite_start]| spaceId, zoneId, oldStatus, newStatus, occurredAt [cite: 71] |
| [cite_start]`SpaceOccupiedEvent` | zonemanagement | notification | spaceId, zoneId, userId?, hasChargingPoint, occurredAt [cite: 71] |
| [cite_start]`SpaceVacatedEvent` | zonemanagement | billing | spaceId, zoneId, userId?, hasChargingPoint, durationMinutes, occurredAt [cite: 71] |
| [cite_start]`ReservationPlacedEvent` | reservation | notification | reservationId, userId, spaceId, zoneId, from, until, occurredAt [cite: 71] |
| [cite_start]`ReservationCancelledEvent` | reservation | notification | reservationId, userId, spaceId, occurredAt [cite: 71] |
| [cite_start]`ReservationCompletedEvent` | reservation | billing | reservationId, userId, spaceId, zoneId, durationMinutes, occurredAt [cite: 71] |
| [cite_start]`PaymentProcessedEvent` | billing | notification | invoiceId, userId, reservationId, totalAmountCents, occurredAt [cite: 71] |

### [cite_start]7.1 Event Flow Narrative [cite: 72]

[cite_start]**Flow A — Citizen Makes a Reservation [cite: 73]**
Citizen calls POST /api/reservations. [cite_start]ReservationService validates the space is FREE via ZoneService, updates status to RESERVED, saves the Reservation, and publishes ReservationPlacedEvent. [cite: 74] [cite_start]The notification module listens and sends a confirmation message to the citizen. [cite: 75]

[cite_start]**Flow B — Citizen Arrives and Parks [cite: 76]**
[cite_start]An external trigger (sensor, app check-in, or number plate recognition) calls OccupationService.markSpaceOccupied(). [cite: 77] The space status is updated to OCCUPIED via ZoneService. [cite_start]SpaceOccupiedEvent is published. [cite: 78] [cite_start]The notification module may optionally use this to alert users about space availability changes in the zone. [cite: 79]

[cite_start]**Flow C — Citizen Leaves the Space [cite: 80]**
OccupationService.markSpaceVacated() is called. The occupation record is closed with an end time. [cite_start]The space is updated to FREE. [cite: 81] [cite_start]SpaceVacatedEvent is published carrying the duration and charging point status. [cite: 82] [cite_start]If hasChargingPoint is TRUE, the billing module appends an EV_CHARGING item to the pending invoice. [cite: 83]

[cite_start]**Flow D — Reservation Completed and Billed [cite: 84]**
[cite_start]ReservationService.completeReservation() transitions the reservation to COMPLETED and publishes ReservationCompletedEvent with the duration. [cite: 85] [cite_start]The billing module receives this event, fetches the pricing policy from ZoneService, calculates total cost, creates an Invoice, and calls PaymentGatewayClient. [cite: 86] [cite_start]On success it publishes PaymentProcessedEvent, which the notification module receives to send a receipt to the citizen. [cite: 87]

## [cite_start]8. Database Conventions [cite: 88]

### [cite_start]8.1 Shared Database, Module-Isolated Tables [cite: 89]
All modules share a single PostgreSQL database instance. [cite_start]However, each module is the exclusive owner of its tables. [cite: 90] [cite_start]No module writes to or queries from another module's tables. [cite: 91] [cite_start]This is enforced by convention and by placing all repository access inside each module's internal/ package. [cite: 92]

### [cite_start]8.2 Table Naming Convention [cite: 93]
[cite_start]All tables are prefixed with the module name to make ownership visually obvious and to prevent naming conflicts. [cite: 94]

| Module | Table Prefix | Example Tables |
| :--- | :--- | :--- |
[cite_start]| usermanagement | um_ | um_users [cite: 95] |
[cite_start]| zonemanagement | zm_ | zm_zones, zm_spaces, zm_occupation_records [cite: 95] |
[cite_start]| reservation | res_ | res_reservations [cite: 95] |
[cite_start]| billing | bil_ | bil_invoices, bil_billing_items [cite: 95] |
[cite_start]| notification | notif_ | notif_notification_history [cite: 95] |

### [cite_start]8.3 ID Strategy [cite: 96]
[cite_start]All primary keys are UUID strings generated by the application layer (`UUID.randomUUID()`), not database auto-increment sequences. [cite: 97] [cite_start]This ensures IDs are globally unique, portable across modules, and safe to reference across boundaries as plain String values. [cite: 98]

### [cite_start]8.4 Cross-Module References [cite: 99]
[cite_start]Foreign key constraints between tables owned by different modules are NOT enforced at the database level. [cite: 100] [cite_start]Referential integrity across modules is maintained at the application level. [cite: 101] [cite_start]For example, `res_reservations` stores userId and spaceId as plain VARCHAR columns — there are no FK constraints to `um_users` or `zm_spaces`. [cite: 102] [cite_start]This preserves module independence. [cite: 103]

## [cite_start]9. REST API Overview [cite: 104]
All endpoints are served under `/api`. [cite_start]Authentication uses JWT Bearer tokens. [cite: 105] [cite_start]ADMIN role endpoints reject CITIZEN tokens with HTTP 403. Public endpoints require no token. [cite: 106]

| Method | Path | Access | Description |
| :--- | :--- | :--- | :--- |
| POST | `/api/auth/register` | Public | [cite_start]Register new user [cite: 107] |
| POST | `/api/auth/login` | Public | [cite_start]Authenticate, receive JWT [cite: 107] |
| GET | `/api/zones` | Public | [cite_start]List all parking zones [cite: 107] |
| GET | `/api/zones/{zoneId}/spaces` | Public | [cite_start]List spaces in zone with status [cite: 107] |
| POST | `/api/zones` | ADMIN | [cite_start]Create new parking zone [cite: 107] |
| PUT | `/api/zones/{zoneId}` | ADMIN | [cite_start]Update zone details or pricing [cite: 107] |
| DELETE | `/api/zones/{zoneId}` | ADMIN | [cite_start]Delete parking zone [cite: 107] |
| POST | `/api/zones/{zoneId}/spaces` | ADMIN | [cite_start]Add space to zone [cite: 107] |
| GET | `/api/map/zones` | Public | [cite_start]All zones enriched with map + status data [cite: 107] |
| GET | `/api/map/zones/{zoneId}` | Public | [cite_start]Single zone map overlay [cite: 107] |
| POST | `/api/reservations` | CITIZEN | [cite_start]Place a reservation [cite: 107] |
| GET | `/api/reservations/{id}` | CITIZEN | [cite_start]Get reservation details [cite: 107] |
| DELETE | `/api/reservations/{id}` | CITIZEN | [cite_start]Cancel reservation [cite: 107] |
| POST | `/api/reservations/{id}/complete` | CITIZEN | [cite_start]Mark reservation as completed [cite: 107] |
| POST | `/api/occupation/{spaceId}/occupy` | ADMIN | [cite_start]Mark space as physically occupied [cite: 107] |
| POST | `/api/occupation/{spaceId}/vacate` | ADMIN | [cite_start]Mark space as vacated [cite: 107] |
| GET | `/api/billing/invoices/{id}` | CITIZEN | [cite_start]Get invoice [cite: 107] |
| GET | `/api/billing/history` | CITIZEN | [cite_start]Get billing history for authenticated user [cite: 107] |

## [cite_start]10. Testing Strategy [cite: 108]

### [cite_start]10.1 Module Verification Test [cite: 109]
This is the most important test in the project. [cite_start]It must be created first and must pass continuously throughout development. [cite: 110]

```java
[cite_start]// Required Test — ModularityVerificationTest.java 
@Test
void verifyModularity() {
    ApplicationModules.of(ParkingApplication.class).verify();
}

This test fails immediately if any module imports from another module's internal/ package.  Run this on every build. A failing verify() test indicates an architectural violation. 10.2 Module Integration Tests Spring Modulith provides @ApplicationModuleTest to bootstrap only a single module and its dependencies. Use this to test each module in isolation before testing cross-module flows. 10.3 Event Flow Tests Test complete event-driven flows using Spring's test event publication utilities. Verify that publishing ReservationCompletedEvent results in an Invoice being created in the billing module, and that PaymentProcessedEvent results in a notification record being created. 10.4 Diagram Generation Use Spring Modulith's Documenter class to auto-generate architecture diagrams from the running application context. These diagrams serve as living documentation and can be included in the project report. Java// Diagram Generation Code 
ApplicationModules modules = ApplicationModules.of(ParkingApplication.class);
new Documenter(modules).writeDocumentation();
Output is generated in target/modulith-docs/ and includes PlantUML diagrams showing module dependencies and event flows. 11. Implementation Checklist for AI Agents Use this checklist to track implementation progress. Complete items in order. Do not proceed to the next module until verify() passes for the current state. Phase 1 — Project Setup [ ] Create Spring Boot project via start.spring.io with Web, JPA, Security, PostgreSQL dependencies [ ] Add spring-modulith-bom, spring-modulith-core, spring-modulith-test to pom.xml [ ] Configure application.yaml with database connection, JWT secret, and external API base URLs [ ] Create ParkingApplication.java at com.parking root package [ ] Write ModularityVerificationTest.java — confirm it compiles and passes with empty modules Phase 2 — Module Implementation [ ] Implement usermanagement module — User, UserRole, UserService, UserRepository, PasswordHasher [ ] Run verify() — must pass [ ] Implement zonemanagement module — ParkingZone, ParkingSpace, SpaceStatus, HasChargingPoint, ZoneService [ ] Implement OccupationService and internal OccupationRecord within zonemanagement [ ] Implement MapOverlayService and internal MapIntegrationClient within zonemanagement [ ] Run verify() — must pass [ ] Implement reservation module — Reservation, ReservationStatus, ReservationService [ ] Verify reservation module only accesses zonemanagement via ZoneService (never internal/) [ ] Run verify() — must pass [ ] Implement billing module — Invoice, BillingItem, BillingItemType, InvoiceStatus, BillingService [ ] Implement internal PriceCalculator and PaymentGatewayClient [ ] Wire event listeners: onReservationCompleted and onSpaceVacated [ ] Run verify() — must pass [ ] Implement notification module — NotificationService, internal NotificationProviderClient [ ] Wire all four event listeners in notification module [ ] Run verify() — must pass Phase 3 — Integration and Testing [ ] Write @ApplicationModuleTest for each module [ ] Write full event flow test: reservation placed → notification sent [ ] Write full event flow test: reservation completed → invoice created → payment → receipt sent [ ] Generate module documentation diagrams via Documenter [ ] Load representative seed data into database [ ] Connect React frontend to all REST endpoints [ ] Final verify() run — all tests green 