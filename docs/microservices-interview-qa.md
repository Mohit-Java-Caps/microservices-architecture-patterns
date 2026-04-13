# Microservices Interview Questions & Answers (System‑Design Focus)

This document contains **frequently asked microservices interview questions**
with **clear, real‑world explanations**.

These answers are structured to show:
- System‑level thinking
- Practical experience
- Pattern‑driven design knowledge

---

## 1. What is Microservices Architecture?

Microservices architecture is an approach where an application is built as a
collection of **small, independent services**, each responsible for a single
business capability.

Each service:
- Is independently deployable
- Owns its own data
- Communicates over the network
- Can scale independently

---

## 2. Why Microservices over Monolithic Architecture?

Monoliths become difficult to scale and maintain as systems grow.

Microservices solve:
✅ Independent scaling  
✅ Faster deployments  
✅ Fault isolation  
✅ Team autonomy  

However, they introduce **distributed system complexity**.

---

## 3. What Are the Main Challenges in Microservices?

Key challenges include:
- Network latency
- Partial failures
- Data consistency
- Service discovery
- Monitoring and debugging
- Security

This is why **design patterns are mandatory**.

---

## 4. What Is API Gateway and Why Is It Needed?

API Gateway is a **single entry point** for clients.

It handles:
- Authentication & authorization
- Request routing
- Rate limiting
- Request aggregation

Interview answer:
> “API Gateway protects microservices and simplifies client interaction.”

---

## 5. Difference Between API Gateway and Load Balancer?

| API Gateway | Load Balancer |
|-----------|--------------|
| Application‑level | Infra‑level |
| Handles security & routing | Distributes traffic |
| Client‑facing | Service‑facing |

---

## 6. What Is Circuit Breaker Pattern?

Circuit Breaker prevents **cascading failures** by stopping calls to unhealthy services.

It has three states:
- Closed
- Open
- Half‑Open

Fail fast is better than slow failure.

---

## 7. What Problem Does Circuit Breaker Solve?

Without circuit breaker:
❌ Threads get blocked  
❌ System resources exhaust  
❌ Entire service fails  

With circuit breaker:
✅ Failures are contained  
✅ System remains responsive  

---

## 8. What Is Service Discovery?

Service Discovery allows services to **find each other dynamically** instead of
using hard‑coded URLs.

It enables:
✅ Auto‑scaling  
✅ Cloud‑native behavior  
✅ Zero‑downtime deployments  

---

## 9. Client‑Side vs Server‑Side Service Discovery?

| Client‑Side | Server‑Side |
|-----------|-------------|
| Client queries registry | Load balancer queries registry |
| Client chooses instance | Infra chooses instance |
| More control | Simpler clients |

---

## 10. What Is the Saga Pattern?

Saga manages **distributed transactions** by breaking them into
a sequence of **local transactions with compensating actions**.

Used when:
- Multiple services
- Multiple databases
- Eventual consistency required

---

## 11. Choreography vs Orchestration Saga?

| Choreography | Orchestration |
|------------|---------------|
| Event‑driven | Central coordinator |
| Complex debugging | Clear control flow |
| Loose coupling | Easier monitoring |

---

## 12. What Is Eventual Consistency?

Eventual consistency means:
> The system may be temporarily inconsistent, but will become consistent over time.

This is expected in distributed systems.

---

## 13. What Is Observability?

Observability answers:
> “What is happening inside the system right now?”

It is built using:
✅ Logs  
✅ Metrics  
✅ Distributed Tracing  

---

## 14. Logs vs Metrics vs Traces?

| Logs | Metrics | Traces |
|----|----|----|
| What happened | How often | Where it happened |
| Text | Numbers | Request path |

All three are required.

---

## 15. What Is Retry Pattern?

Retry allows a request to be attempted again after failure,
assuming the failure is **temporary**.

Must be combined with:
✅ Timeout  
✅ Backoff  
✅ Circuit Breaker  

---

## 16. Why Retry Alone Is Dangerous?

Blind retries:
❌ Increase load  
❌ Worsen outages  
❌ Cause retry storms  

Golden rule:
> “Never retry without timeout and circuit breaker.”

---

## 17. What Is Bulkhead Pattern?

Bulkhead isolates resources so failure in one component
does not affect others.

Inspired by ship compartments.

---

## 18. Bulkhead vs Circuit Breaker?

| Bulkhead | Circuit Breaker |
|-------|----------------|
| Isolates resources | Stops calling |
| Prevents resource exhaustion | Prevents cascading failures |

Used together for resilience.

---

## 19. How Is Security Handled in Microservices?

Using **Zero Trust** principle:
- API Gateway + JWT
- Service‑to‑service authentication
- Secure communication (HTTPS)
- Secrets management
- Rate limiting

---

## 20. When Are Microservices a Bad Idea?

❌ Small teams  
❌ Simple CRUD apps  
❌ No DevOps maturity  

Golden quote:
> “Start with a monolith. Move to microservices when scale demands it.”

---

## Interview Tip (Very Important)

Interviewers care less about tools and more about:
✅ Clear thinking  
✅ Trade‑offs  
✅ Failure handling  

> **Microservices is about managing failure, not avoiding it.**
