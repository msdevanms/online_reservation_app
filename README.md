# Online Reservation App

## Overview
The **Online Reservation App** is a microservices-based application designed to efficiently handle reservations. Each service is dedicated to a specific functionality, ensuring scalability, flexibility, and maintainability.

## Service List

- **Admin Service**  
  Manages administrative tasks like user roles, permissions, and system settings.

- **Auth Service**  
  Handles authentication and authorization, including login, token generation, and session management.

- **Booking Service**  
  Manages the reservation process, including availability checks, booking creation, updates, and cancellations.

- **Compositor Service**  
  Aggregates data from multiple services to provide a unified view for clients.

- **Config Server**  
  Provides centralized configuration management for all services using Spring Cloud Config.

- **Eureka Server**  
  Implements service discovery for dynamic registration and discovery of microservices.

- **Gateway Server**  
  Acts as a single entry point for clients, providing API routing, load balancing, and security.

- **Inventory Service**  
  Tracks and manages the availability of resources for reservations.

- **Message Service**  
  Facilitates asynchronous communication between services using a messaging queue.

- **Profile Service**  
  Manages user profiles, including personal details, preferences, and reservation history.

## Tools Used

- **Zookeeper**: Distributed coordination for synchronization and fault tolerance.  
- **Kafka Server**: Message queuing for asynchronous and real-time communication between services.  
- **Zipkin**: Distributed tracing for monitoring and debugging microservices.  
- **Grafana**: Advanced data visualization and monitoring.  
- **Loki**: Log aggregation for querying logs seamlessly with Grafana.  
- **Redis**: High-performance in-memory data store for caching and session management.

## How to Run

1. **Start the Infrastructure**  
   - Launch Zookeeper, Kafka Server, and Redis.  
   - Set up monitoring tools (Zipkin, Grafana, Loki).  

2. **Run Services**  
   - Start `configserverboot3` to load configurations.  
   - Start `eurekaserverboot3` for service discovery.  
   - Launch the remaining services in the required order.  

3. **Access the Application**  
   - Use the Gateway Server (`gatewayserverboot3`) as the entry point.  
   - API endpoints and documentation are available through the gateway.

## Monitoring and Debugging

- Use **Grafana** for performance monitoring and dashboards.  
- Query logs through **Loki** for troubleshooting.  
- Trace requests across services with **Zipkin**.

---
