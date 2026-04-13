
# Saga Pattern – Managing Distributed Transactions in Microservices

In a monolithic application, maintaining data consistency is easy.
A single database transaction either **commits** or **rolls back**.

In microservices, this is **no longer possible**.

The **Saga Pattern** exists to solve this problem:
> **How do we maintain data consistency when a business operation spans multiple microservices?**

---

## The Core Problem Saga Solves

In microservices:
- Each service has its own database
- Traditional ACID transactions do not work across services
- A failure in one service can leave the system in an inconsistent state

---

### Real‑World Problem (Very Important for Interviews)

E‑commerce order flow:
1. Order Service → Create order ✅
2. Payment Service → Process payment ✅
3. Inventory Service → Reduce stock ❌ (fails)

Now what?

Without Saga:
❌ Order is created  
❌ Payment is completed  
❌ Inventory not reserved  

The system is **inconsistent**.

---

## What Is the Saga Pattern?

> **Saga is a pattern that manages a long‑running business transaction by breaking it into a series of local transactions, each with a corresponding compensating action.**

Instead of one big transaction:
✅ Multiple small transactions  
✅ Each service commits locally  
✅ Failures are handled via compensation  

---

## Simple Mental Model

> **Saga = “Do steps one by one, and if something fails, undo what was already done.”**

---

## How Saga Works (High Level Flow)

```

Service A → Service B → Service C
✅          ✅          ❌

```

If Service C fails:
- Undo Service B
- Undo Service A

✅ System returns to consistent state

---

## Key Concept: Compensating Transactions

Each step in a saga has:
- **Forward action** (normal operation)
- **Compensating action** (undo operation)

Example:
- Create order → Cancel order
- Charge payment → Refund payment
- Reserve stock → Release stock

---

## Types of Saga Patterns (Very Common Interview Question)

There are **two ways** to implement Saga.

---

## 1️⃣ Choreography‑Based Saga

### How It Works

- No central coordinator
- Services communicate using events
- Each service reacts to events and triggers the next step

---

### Example Flow

```

Order Service → OrderCreated Event
Payment Service → PaymentCompleted Event
Inventory Service → InventoryReserved Event

```

If any service fails:
- It publishes a failure event
- Previous services trigger compensation

---

### Advantages
✅ No central point of failure  
✅ Loose coupling  
✅ Scales well  

### Disadvantages
❌ Complex event flow  
❌ Harder to debug  
❌ Event chain can become confusing  

---

### When to Use Choreography

✅ Simple workflows  
✅ Event‑driven systems  
✅ Teams comfortable with eventing  

---

## 2️⃣ Orchestration‑Based Saga

### How It Works

- A **central Saga Orchestrator** controls the flow
- Orchestrator tells services what to do next
- Orchestrator handles failures and rollback

---

### Example Flow

```

Saga Orchestrator
→ Order Service
→ Payment Service
→ Inventory Service

```

If any step fails:
✅ Orchestrator triggers compensation steps

---

### Advantages
✅ Clear control flow  
✅ Easier to understand  
✅ Easier error handling  

### Disadvantages
❌ Orchestrator becomes central component  
❌ Needs careful design  

---

### When to Use Orchestration

✅ Complex workflows  
✅ Strong consistency requirements  
✅ Easier monitoring & debugging  

---

## Choreography vs Orchestration (Interview Table)

| Aspect | Choreography | Orchestration |
|----|----|----|
| Control | Distributed | Centralized |
| Complexity | High in large flows | Easier to manage |
| Debugging | Difficult | Easier |
| Scalability | High | Depends on implementation |

---

## Real‑World Saga Example (Interview‑Friendly)

**Order Placement Saga**

Steps:
1. Create Order
2. Process Payment
3. Reserve Inventory
4. Confirm Order

If Step 2 fails:
✅ Cancel Order

If Step 3 fails:
✅ Refund Payment  
✅ Cancel Order  

This ensures **eventual consistency**.

---

## Saga Pattern vs Two‑Phase Commit (2PC)

| Saga | 2PC |
|----|----|
| Eventual consistency | Strong consistency |
| Designed for microservices | Poor for microservices |
| No locking | Heavy locking |
| Scalable | Not scalable |

👉 **Microservices avoid 2PC and use Saga**.

---

## Challenges with Saga Pattern

❌ Eventual consistency (not immediate)  
❌ Complex compensating logic  
❌ Careful failure handling required  

But:
✅ It scales  
✅ It works in real‑world systems  
✅ It’s cloud‑friendly  

---

## When Saga Pattern Is Needed

✅ Business workflows across services  
✅ Multiple databases involved  
✅ Distributed transactions  
✅ Microservices architecture  

---

## Interview‑Ready Explanation (Use This)

> “Saga pattern manages distributed transactions by breaking them into a sequence of local transactions with compensating actions. It provides eventual consistency without locking resources.”

Short. Clear. Senior‑level ✅

---

## Key Takeaways

✅ Microservices cannot use traditional transactions  
✅ Saga ensures data consistency  
✅ Uses compensation instead of rollback  
✅ Essential for real‑world microservices  

> **In distributed systems, consistency is managed — not guaranteed instantly.**

---

## What’s Next

Now that we’ve covered:
✅ API Gateway  
✅ Circuit Breaker  
✅ Service Discovery  
✅ Saga Pattern  

The next critical topic is **Observability & Fault Tolerance** —
how we monitor and debug microservices in production.
