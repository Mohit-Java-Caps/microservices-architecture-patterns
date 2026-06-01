package saga;

import java.util.*;
import java.util.function.Consumer;

/**
 * SagaPattern — Choreography-based Saga for distributed transactions.
 *
 * PRODUCTION SCENARIO:
 * ─────────────────────────────────────────────────────────────────────
 * Customer places an order. Your system must:
 *   1. Create the order
 *   2. Reserve inventory
 *   3. Charge payment
 *   4. Send confirmation email
 *
 * Problem: These span 4 separate microservices.
 * If payment fails after inventory is reserved — inventory stays locked.
 * You can't use a DB transaction across services.
 * You need a SAGA.
 *
 * SAGA PATTERN:
 * ─────────────────────────────────────────────────────────────────────
 * A saga is a sequence of local transactions.
 * Each step publishes an event → triggers the next step.
 * If any step fails → compensating transactions undo prior steps.
 *
 * TWO TYPES:
 * ─────────────────────────────────────────────────────────────────────
 *  CHOREOGRAPHY  → Services react to events (no central coordinator)
 *                  Pros: loose coupling, simple
 *                  Cons: hard to track overall flow, cyclic dependencies
 *
 *  ORCHESTRATION → A central Saga Orchestrator tells each service what to do
 *                  Pros: clear flow, easier to debug, single source of truth
 *                  Cons: orchestrator becomes a bottleneck if poorly designed
 *
 * This demo shows CHOREOGRAPHY — each service listens for events and
 * either proceeds or triggers a compensation on failure.
 *
 * Author: Mohit Kumar — github.com/Mohit-Java-Caps
 */
public class SagaPattern {

    // ── Event bus (in production: Apache Kafka) ───────────────────────────
    static class EventBus {
        private final Map<String, List<Consumer<SagaEvent>>> subscribers = new HashMap<>();

        void subscribe(String eventType, Consumer<SagaEvent> handler) {
            subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
        }

        void publish(SagaEvent event) {
            System.out.printf("  [EVENT BUS] Published: %-35s | OrderId: %s%n",
                event.type(), event.orderId());
            List<Consumer<SagaEvent>> handlers = subscribers.getOrDefault(event.type(), List.of());
            handlers.forEach(h -> h.accept(event));
        }
    }

    record SagaEvent(String type, String orderId, Map<String, Object> data) {}

    // ── Step 1: Order Service ─────────────────────────────────────────────
    static class OrderService {
        private final EventBus bus;
        private final Map<String, String> orders = new HashMap<>();

        OrderService(EventBus bus) {
            this.bus = bus;
            // Listen for compensation — cancel order if payment failed
            bus.subscribe("PAYMENT_FAILED", this::cancelOrder);
            bus.subscribe("INVENTORY_FAILED", this::cancelOrder);
        }

        void createOrder(String orderId, String product, int qty) {
            System.out.println("\n[ORDER SERVICE] Creating order " + orderId);
            orders.put(orderId, "PENDING");
            bus.publish(new SagaEvent("ORDER_CREATED", orderId,
                Map.of("product", product, "qty", qty, "price", 99.99)));
        }

        private void cancelOrder(SagaEvent event) {
            orders.put(event.orderId(), "CANCELLED");
            System.out.println("[ORDER SERVICE] COMPENSATING — Order " + event.orderId() + " cancelled");
        }

        String getStatus(String orderId) { return orders.getOrDefault(orderId, "NOT_FOUND"); }
    }

    // ── Step 2: Inventory Service ─────────────────────────────────────────
    static class InventoryService {
        private final EventBus bus;
        private int stock = 5; // only 5 items in stock

        InventoryService(EventBus bus) {
            this.bus = bus;
            bus.subscribe("ORDER_CREATED", this::reserveInventory);
            // Compensate if payment fails
            bus.subscribe("PAYMENT_FAILED", this::releaseInventory);
        }

        private void reserveInventory(SagaEvent event) {
            int qty = (int) event.data().get("qty");
            System.out.println("[INVENTORY SERVICE] Reserving " + qty + " units (stock: " + stock + ")");

            if (stock >= qty) {
                stock -= qty;
                System.out.println("[INVENTORY SERVICE] Reserved. Remaining stock: " + stock);
                bus.publish(new SagaEvent("INVENTORY_RESERVED", event.orderId(), event.data()));
            } else {
                System.out.println("[INVENTORY SERVICE] FAILED — Insufficient stock!");
                bus.publish(new SagaEvent("INVENTORY_FAILED", event.orderId(), event.data()));
            }
        }

        private void releaseInventory(SagaEvent event) {
            int qty = (int) event.data().get("qty");
            stock += qty;
            System.out.println("[INVENTORY SERVICE] COMPENSATING — Released " + qty + " units back to stock: " + stock);
        }
    }

    // ── Step 3: Payment Service ───────────────────────────────────────────
    static class PaymentService {
        private final EventBus bus;
        private final boolean shouldFail; // inject failure for demo

        PaymentService(EventBus bus, boolean shouldFail) {
            this.bus = bus;
            this.shouldFail = shouldFail;
            bus.subscribe("INVENTORY_RESERVED", this::chargePayment);
        }

        private void chargePayment(SagaEvent event) {
            double price = (double) event.data().get("price");
            System.out.println("[PAYMENT SERVICE] Charging $" + price);

            if (!shouldFail) {
                System.out.println("[PAYMENT SERVICE] Payment successful!");
                bus.publish(new SagaEvent("PAYMENT_COMPLETED", event.orderId(), event.data()));
            } else {
                System.out.println("[PAYMENT SERVICE] FAILED — Card declined!");
                bus.publish(new SagaEvent("PAYMENT_FAILED", event.orderId(), event.data()));
            }
        }
    }

    // ── Step 4: Notification Service ──────────────────────────────────────
    static class NotificationService {
        NotificationService(EventBus bus) {
            bus.subscribe("PAYMENT_COMPLETED", e ->
                System.out.println("[NOTIFICATION SERVICE] Confirmation email sent for order " + e.orderId()));
            bus.subscribe("PAYMENT_FAILED", e ->
                System.out.println("[NOTIFICATION SERVICE] Failure email sent for order " + e.orderId()));
            bus.subscribe("INVENTORY_FAILED", e ->
                System.out.println("[NOTIFICATION SERVICE] Out-of-stock email sent for order " + e.orderId()));
        }
    }

    // ── Main ──────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== SAGA Pattern — Choreography Demo ===");
        System.out.println("Each service listens for events and reacts independently.");
        System.out.println("No central coordinator — pure event-driven choreography.\n");

        // Scenario 1: Happy path
        System.out.println("━━━ Scenario 1: SUCCESS — All steps complete ━━━");
        EventBus bus1 = new EventBus();
        OrderService orderSvc1 = new OrderService(bus1);
        new InventoryService(bus1);
        new PaymentService(bus1, false); // payment succeeds
        new NotificationService(bus1);
        orderSvc1.createOrder("ORD-001", "Laptop", 1);
        System.out.println("\nFinal order status: " + orderSvc1.getStatus("ORD-001"));

        System.out.println("\n━━━ Scenario 2: FAILURE + COMPENSATION — Payment fails ━━━");
        EventBus bus2 = new EventBus();
        OrderService orderSvc2 = new OrderService(bus2);
        new InventoryService(bus2);
        new PaymentService(bus2, true); // payment FAILS
        new NotificationService(bus2);
        orderSvc2.createOrder("ORD-002", "Laptop", 1);
        System.out.println("\nFinal order status: " + orderSvc2.getStatus("ORD-002"));
        System.out.println("(Inventory was reserved then released — no data inconsistency)");

        System.out.println("\n━━━ Scenario 3: FAILURE — Insufficient stock ━━━");
        EventBus bus3 = new EventBus();
        OrderService orderSvc3 = new OrderService(bus3);
        new InventoryService(bus3);
        new PaymentService(bus3, false);
        new NotificationService(bus3);
        orderSvc3.createOrder("ORD-003", "Laptop", 99); // too many items
        System.out.println("\nFinal order status: " + orderSvc3.getStatus("ORD-003"));

        System.out.println("""

            === Key Interview Points ===
            Saga solves distributed transactions WITHOUT 2PC (two-phase commit).
            Each step is atomic locally. Failures trigger compensating transactions.
            Choreography = event-driven, decoupled. Orchestration = central controller.
            Production tools: Axon Framework, Temporal.io, AWS Step Functions.
            """);
    }
}
