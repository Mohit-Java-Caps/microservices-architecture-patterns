
# Security in Microservices – Protecting a Distributed System

Security in microservices is **very different** from security in monolithic applications.

In a monolith:
- One application
- One security boundary

In microservices:
- Many services
- Many entry points
- Many network calls

Security must be **designed**, not added later.

---

## Why Security Is Harder in Microservices

Microservices introduce new attack surfaces:

- Many APIs exposed
- Services talk over the network
- Distributed deployments
- Dynamic service locations

Without a proper security strategy:
❌ Unauthorized access becomes easy  
❌ Tokens get leaked  
❌ Internal services get compromised  
❌ A single breach spreads quickly  

---

## Core Security Principles in Microservices

Before patterns, understand these **fundamental principles**.

✅ **Zero Trust** – Never trust the network  
✅ **Defense in Depth** – Multiple layers of security  
✅ **Least Privilege** – Grant minimum access needed  
✅ **Secure by Default** – Deny unless explicitly allowed  

> **In microservices, every service must protect itself.**

---

## Authentication vs Authorization (Interview Classic)

| Concept | Meaning |
|-----|-----|
| Authentication | Who are you? |
| Authorization | What can you do? |

Both are required in every serious system.

---

## Pattern 1: API Gateway as Security Gateway

### Why API Gateway Is Central to Security

API Gateway is usually the **first security layer**.

It handles:
- Authentication (JWT / OAuth)
- Authorization
- Rate limiting
- Request validation

✅ Invalid requests are blocked early  
✅ Microservices stay focused on business logic  

---

### Real‑World Example

Client sends request → API Gateway:
1. Validates token
2. Checks permissions
3. Routes request to service

If token is invalid:
❌ Request never reaches backend

---

### Interview Tip 🎯
> “API Gateway centralizes security concerns and protects microservices from external clients.”

---

## Pattern 2: Token‑Based Authentication (JWT)

### Why Tokens Are Used

Microservices are **stateless**.
Session‑based auth does not scale well.

Tokens solve this by:
- Carrying user identity
- Being self‑contained
- Avoiding server‑side sessions

---

### JWT Basics (High Level)

A JWT contains:
- User identity
- Roles / permissions
- Expiry time
- Digital signature

✅ Can be verified without DB calls  
✅ Scales across services  

---

### Important Security Rule

❌ Do not trust JWT payload blindly  
✅ Always verify signature  
✅ Always check expiration  

---

## Pattern 3: Service‑to‑Service Security

Client‑to‑service security is not enough.

In microservices:
```

Service A → Service B

```

This communication **must also be secured**.

---

### Why Internal Calls Need Security

Common misconception:
> “Internal services are safe.”

Reality:
❌ Network can be compromised  
❌ One service breach spreads quickly  

---

### Service‑to‑Service Auth Techniques

✅ Mutual TLS (mTLS)  
✅ Internal tokens  
✅ Service identities  

Conceptually:
> **Every service must prove who it is.**

---

### Interview Tip 🎯
> “Zero Trust means even internal services must authenticate.”

---

## Pattern 4: Network‑Level Security

Security is not just code‑level.

Network‑level protections include:
- Firewall rules
- Network policies
- Segmentation
- Private communication channels

Goal:
✅ Limit who can talk to whom  

---

## Pattern 5: Secrets Management

### What Are Secrets?

- API keys
- Database passwords
- Token signing keys
- Certificates

---

### What NOT to Do

❌ Hard‑code secrets  
❌ Store secrets in Git  
❌ Expose secrets in logs  

---

### Correct Approach

✅ Externalize secrets  
✅ Rotate secrets regularly  
✅ Restrict access  

Secrets must be **managed securely and centrally**.

---

## Pattern 6: Rate Limiting & Throttling

Protect services from:
- Abuse
- DDoS attacks
- Misbehaving clients

Rate limiting is usually enforced at:
✅ API Gateway  
✅ Edge layer  

Example:
```

100 requests per minute per user

```

---

## Pattern 7: Secure Communication (HTTPS Everywhere)

All communication must be:
✅ Encrypted in transit  

This includes:
- Client → Gateway
- Gateway → Service
- Service → Service

Unencrypted traffic = easy data leaks.

---

## Observability + Security (Often Overlooked)

Security incidents are detected via:
- Logs
- Metrics
- Alerts

Examples:
- Too many failed logins
- Unusual request patterns
- Access denial spikes

Security without visibility is **blind**.

---

## Common Security Mistakes (Interview Gold)

❌ Assuming internal network is safe  
❌ Putting all security only at gateway  
❌ No service‑to‑service authentication  
❌ Hard‑coded secrets  
❌ No rate limiting  

---

## Interview‑Ready Explanation (Use This)

> “Microservices security is based on zero‑trust principles.  
Security is enforced at multiple layers using API Gateway, token‑based authentication, service‑to‑service authentication, secure communication, and proper secrets management.”

✅ Structured  
✅ Confident  
✅ Senior‑level  

---

## Key Takeaways

✅ Microservices expand the attack surface  
✅ Security must be layered  
✅ API Gateway is the first line of defense  
✅ Internal services must authenticate  
✅ Zero Trust is essential  

> **In microservices, trust nothing—verify everything.**

---

## What’s Next (Optional)

You have now covered:
✅ Architecture fundamentals  
✅ Core design patterns  
✅ Resilience patterns  
✅ Observability  
✅ Security  
