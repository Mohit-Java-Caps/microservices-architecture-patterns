
# Circuit Breaker Pattern – Preventing Cascading Failures

In a microservices system, **failures are not an exception — they are normal**.

The **Circuit Breaker pattern** exists to make sure that:
> *One failing service does NOT bring down the entire system.*

This pattern is a **must‑know topic for system design interviews**.

---

## What Is the Circuit Breaker Pattern?

> **The Circuit Breaker pattern prevents an application from repeatedly trying to execute an operation that is likely to fail.**

Instead of waiting for timeouts and blocking resources,  
the system **fails fast and recovers gracefully**.

---

## The Problem Circuit Breaker Solves

In microservices, services depend on each other:

```

Order Service → Payment Service → Bank API

```

Now imagine:
- Payment Service is slow or down
- Order Service keeps calling it

### Without Circuit Breaker

❌ Threads block waiting for response  
❌ Requests pile up  
❌ CPU and memory get exhausted  
❌ Other services start failing  

This is called a **cascading failure**.

---

## Real‑World Example (Easy to Explain in Interview)

E‑commerce system:
- User places an order
- Order Service calls Payment Service
- Payment Service is down

Without circuit breaker:
❌ Order Service waits for timeout  
❌ User experience degrades  
❌ System becomes unstable  

With circuit breaker:
✅ Order Service fails fast  
✅ Returns a fallback message  
✅ System stays responsive  

---

## How Circuit Breaker Works (Core Concept)

A circuit breaker acts **like an electrical circuit breaker**.

It has **three states**.

---

## Circuit Breaker States

### 1️⃣ Closed State (Normal Operation)

- All requests flow normally
- Failures are tracked

```

Order Service → Payment Service ✅

```

✅ System behaving normally

---

### 2️⃣ Open State (Service Is Considered Down)

- Requests are blocked immediately
- No remote calls are made
- Fallback logic is executed

```

Order Service ✋ Payment Service

````

✅ No waiting  
✅ Resources protected  

---

### 3️⃣ Half‑Open State (Recovery Check)

- A few test requests are allowed
- If successful → circuit closes
- If failures continue → circuit opens again

✅ Automatic recovery  
✅ No manual intervention needed  

---

## Why Circuit Breaker Is Better Than Just Timeout

Timeout alone:
- Still consumes threads
- Still waits
- Still wastes resources

Circuit breaker:
✅ Stops calls early  
✅ Protects system capacity  
✅ Improves resilience  

---

## Fallback Mechanism (Very Important)

When circuit is open, the system usually:
- Returns cached data
- Returns default response
- Returns friendly error message

### Example
If Payment is down:
```text
"Payment service is currently unavailable. Please try later."
````

Better than:

```text
Request timeout after 30 seconds
```

***

## Circuit Breaker in Real Systems

Circuit breakers are used in:

*   Payment systems
*   Third‑party API calls
*   Notification services
*   Recommendation engines

Any service that:
✅ Is slow  
✅ Is unreliable  
✅ Is external

***

## Common Circuit Breaker Configuration Concepts

You don’t need exact numbers in interviews, but understand the ideas:

*   Failure threshold (e.g., 50% failures)
*   Timeout duration
*   Retry window
*   Recovery interval

Interviewers care about:

> **Why**, not exact configuration values.

***

## Circuit Breaker vs Retry (Common Interview Question)

| Retry               | Circuit Breaker            |
| ------------------- | -------------------------- |
| Tries request again | Stops request completely   |
| Can increase load   | Reduces load               |
| May worsen failure  | Prevents cascading failure |

✅ **Best practice:** Use retry *with* circuit breaker.

***

## When Circuit Breaker Should Be Used

✅ Remote service calls  
✅ Third‑party APIs  
✅ Network I/O  
✅ Any failure‑prone interaction

***

## When Circuit Breaker May Not Be Needed

❌ In‑process method calls  
❌ Local database access  
❌ Simple monolithic systems

***

## Interview‑Ready Explanation (Use This)

> “Circuit Breaker prevents cascading failures by stopping calls to failing services.  
> It fails fast, protects system resources, and allows automatic recovery once the service becomes healthy.”

✅ Crisp  
✅ Professional  
✅ Senior‑level

***

## Key Takeaways

✅ Failures are normal in microservices  
✅ Circuit breaker prevents system collapse  
✅ Protects threads, memory, and latency  
✅ Mandatory for resilient systems

> **In distributed systems,  
> failing fast is better than failing slowly.**

***

## What’s Next

Now that failures are handled,  
the next challenge is **how services discover each other dynamically**.
