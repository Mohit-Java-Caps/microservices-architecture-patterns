
# API Gateway Pattern – Single Entry Point for Microservices

The **API Gateway** pattern is usually the **first and most important design pattern**
introduced in a microservices architecture.

Without an API Gateway, even well‑designed microservices can become
**hard to use, insecure, and difficult to maintain**.

---

## What Is an API Gateway?

> **An API Gateway is a single entry point that sits between clients and microservices and routes requests to the appropriate services.**

Clients never communicate with microservices directly.
All requests go through the API Gateway.

```

Client → API Gateway → Microservices

```

---

## The Problem Without an API Gateway

Imagine a frontend application calling microservices directly:

```

Client → User Service
Client → Order Service
Client → Payment Service
Client → Inventory Service

```

### Problems This Creates

❌ Client must know all service URLs  
❌ Client must handle service failures  
❌ Authentication logic duplicated everywhere  
❌ Too many network calls  
❌ Hard to change backend without breaking clients  

This tightly couples the client to internal system details.

---

## How API Gateway Solves This

With an API Gateway:

```

Client → API Gateway → User Service
→ Order Service
→ Payment Service

```

The client:
✅ Talks to **one endpoint**  
✅ Knows nothing about internal services  
✅ Receives a stable API even if backend changes  

---

## Core Responsibilities of an API Gateway

An API Gateway is **not just a router**.
In real systems, it handles multiple responsibilities.

---

### 1️⃣ Request Routing

The gateway routes requests to the correct microservice.

Example:
```

/api/users     → User Service
/api/orders    → Order Service
/api/payments  → Payment Service

```

✅ Centralized routing  
✅ Easy to modify backend topology  

---

### 2️⃣ Authentication and Authorization

Security should not be duplicated across services.

API Gateway typically:
- Validates JWT tokens
- Handles OAuth authentication
- Rejects unauthorized requests early

✅ Security enforced in one place  
✅ Reduced duplication  

---

### 3️⃣ Request Aggregation

Client screens often need data from **multiple services**.

Instead of:
```

Client → User Service
Client → Order Service
Client → Recommendation Service

```

API Gateway:
✅ Calls all required services  
✅ Aggregates responses  
✅ Returns a single payload  

---

### Real‑World Example (Very Interview‑Friendly)

Mobile app “Home Screen” needs:
- User details
- Recent orders
- Offers

API Gateway:
1. Calls User Service
2. Calls Order Service
3. Calls Offer Service
4. Combines data
5. Sends one response to client

✅ Fewer network calls  
✅ Better performance  
✅ Simpler client code  

---

### 4️⃣ Rate Limiting & Throttling

API Gateway can:
- Limit requests per user
- Prevent abuse
- Protect backend services

Example:
```

User → Max 100 requests/minute

```

✅ Prevents system overload  
✅ Improves reliability  

---

### 5️⃣ Logging and Monitoring

Centralized logging at the gateway level:
- Request tracing
- Performance metrics
- Error monitoring

✅ Easier debugging  
✅ Better observability  

---

## API Gateway vs Load Balancer (Common Interview Question)

| API Gateway | Load Balancer |
|-----------|---------------|
| Application layer | Transport layer |
| Routing logic | Traffic distribution |
| Handles auth, rate limiting | No business logic |
| Client‑facing | Infrastructure‑focused |

Interview tip:
> “A load balancer distributes traffic,  
> an API Gateway **manages APIs**.”

---

## Types of API Gateways

### 1️⃣ Fine‑Grained Gateway
- One gateway per client type
- Separate gateway for mobile and web

✅ Optimized responses  
❌ More gateways to manage  

---

### 2️⃣ Unified Gateway
- Single gateway for all clients

✅ Simple architecture  
✅ Easier management  
❌ More complex routing logic  

Both models are valid — choice depends on requirements.

---

## Important Trade‑Offs (Very Important for Interviews)

API Gateway is powerful, but introduces risks:

❌ Single point of failure (if not designed well)  
❌ Adds latency (extra network hop)  
❌ Becomes complex if overused  

Best practice:
✅ Make API Gateway lightweight  
✅ Avoid heavy business logic  
✅ Ensure high availability  

---

## When API Gateway Should NOT Be Used

❌ Very small systems  
❌ Internal service‑to‑service calls  
❌ Simple monolithic apps  

Golden rule:
> API Gateway is for **client‑to‑service communication**,  
> not service‑to‑service communication.

---

## API Gateway in Real Systems

Almost every large microservices system uses an API Gateway:

- E‑commerce platforms
- Streaming services
- Banking systems
- Cloud‑native applications

Framework examples (just for context, not mandatory):
- Spring Cloud Gateway
- Kong
- NGINX
- AWS API Gateway

---

## How to Explain API Gateway in an Interview (Perfect Answer)

Use this structure:

> “API Gateway acts as a single entry point for clients.  
It handles routing, authentication, aggregation, and rate limiting,  
so that microservices remain simple and independent.”

✅ Short  
✅ Clear  
✅ Senior‑level  

---

## Key Takeaways

✅ API Gateway simplifies client communication  
✅ Centralizes cross‑cutting concerns  
✅ Protects microservices from clients  
✅ Essential starting point for microservices  

> **API Gateway keeps microservices clean  
by pushing cross‑cutting concerns to the edge.**
