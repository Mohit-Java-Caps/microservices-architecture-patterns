package servicediscovery;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ServiceDiscovery — Client-side service discovery with load balancing.
 *
 * PRODUCTION SCENARIO:
 * ─────────────────────────────────────────────────────────────────────
 * In a microservices system, services scale up and down dynamically.
 * You can't hardcode IP addresses — pods come and go every few seconds.
 *
 * Service Discovery solves: "How does Service A find Service B?"
 *
 * TWO PATTERNS:
 * ─────────────────────────────────────────────────────────────────────
 *  CLIENT-SIDE (shown here)
 *    → Client queries the registry, picks an instance, calls it directly
 *    → Example: Netflix Eureka + Ribbon (Spring Cloud)
 *    → Pro: no extra hop. Con: client needs discovery logic
 *
 *  SERVER-SIDE
 *    → Load balancer queries registry, routes for the client
 *    → Example: AWS ALB + ECS Service Discovery, Kubernetes Service
 *    → Pro: client stays simple. Con: extra network hop
 *
 * HEALTH CHECKING:
 * ─────────────────────────────────────────────────────────────────────
 *  Registry periodically pings each instance.
 *  Dead instances are automatically deregistered.
 *  Client never sees a stale/dead endpoint.
 *
 * Author: Mohit Kumar — github.com/Mohit-Java-Caps
 */
public class ServiceDiscovery {

    // ── Service Registry (like Eureka, Consul, or Kubernetes etcd) ────────
    static class ServiceRegistry {
        // serviceName → list of registered instances
        private final Map<String, List<ServiceInstance>> registry = new ConcurrentHashMap<>();

        void register(ServiceInstance instance) {
            registry.computeIfAbsent(instance.serviceName(), k -> new ArrayList<>()).add(instance);
            System.out.printf("[REGISTRY] Registered: %-20s at %s:%d (zone: %s)%n",
                instance.serviceName(), instance.host(), instance.port(), instance.zone());
        }

        void deregister(ServiceInstance instance) {
            List<ServiceInstance> instances = registry.get(instance.serviceName());
            if (instances != null) {
                instances.remove(instance);
                System.out.printf("[REGISTRY] Deregistered: %s at %s:%d%n",
                    instance.serviceName(), instance.host(), instance.port());
            }
        }

        List<ServiceInstance> getHealthyInstances(String serviceName) {
            return registry.getOrDefault(serviceName, List.of())
                .stream()
                .filter(ServiceInstance::isHealthy)
                .toList();
        }

        void markUnhealthy(ServiceInstance instance) {
            instance.markUnhealthy();
            System.out.printf("[REGISTRY] Health check FAILED: %s:%d — marked UNHEALTHY%n",
                instance.host(), instance.port());
        }

        void printRegistry() {
            System.out.println("\n[REGISTRY] Current state:");
            registry.forEach((name, instances) -> {
                System.out.println("  " + name + ":");
                instances.forEach(i -> System.out.printf("    %s:%d [%s] zone=%s%n",
                    i.host(), i.port(), i.isHealthy() ? "HEALTHY" : "UNHEALTHY", i.zone()));
            });
        }
    }

    static class ServiceInstance {
        private final String serviceName;
        private final String host;
        private final int port;
        private final String zone;
        private boolean healthy = true;

        ServiceInstance(String serviceName, String host, int port, String zone) {
            this.serviceName = serviceName;
            this.host = host;
            this.port = port;
            this.zone = zone;
        }

        void markUnhealthy()  { this.healthy = false; }
        boolean isHealthy()   { return healthy; }
        String serviceName()  { return serviceName; }
        String host()         { return host; }
        int port()            { return port; }
        String zone()         { return zone; }

        @Override public String toString() {
            return host + ":" + port + "(" + zone + ")";
        }
    }

    // ── Load Balancer strategies ───────────────────────────────────────────
    interface LoadBalancer {
        ServiceInstance choose(List<ServiceInstance> instances);
        String strategyName();
    }

    /** Round-robin: distribute evenly across all instances */
    static class RoundRobinLoadBalancer implements LoadBalancer {
        private final AtomicInteger counter = new AtomicInteger(0);

        public ServiceInstance choose(List<ServiceInstance> instances) {
            if (instances.isEmpty()) return null;
            int idx = Math.abs(counter.getAndIncrement() % instances.size());
            return instances.get(idx);
        }

        public String strategyName() { return "Round-Robin"; }
    }

    /** Random: simple, good for homogeneous instances */
    static class RandomLoadBalancer implements LoadBalancer {
        private final Random random = new Random();

        public ServiceInstance choose(List<ServiceInstance> instances) {
            if (instances.isEmpty()) return null;
            return instances.get(random.nextInt(instances.size()));
        }

        public String strategyName() { return "Random"; }
    }

    // ── Discovery Client (used by the calling service) ─────────────────────
    static class DiscoveryClient {
        private final ServiceRegistry registry;
        private final LoadBalancer loadBalancer;

        DiscoveryClient(ServiceRegistry registry, LoadBalancer loadBalancer) {
            this.registry = registry;
            this.loadBalancer = loadBalancer;
        }

        /**
         * Discover and call a service by name.
         * In production: this would make an actual HTTP call to the resolved endpoint.
         */
        String call(String serviceName, String endpoint) {
            List<ServiceInstance> instances = registry.getHealthyInstances(serviceName);

            if (instances.isEmpty()) {
                return "ERROR: No healthy instances of '" + serviceName + "' available!";
            }

            ServiceInstance chosen = loadBalancer.choose(instances);
            String url = "http://" + chosen.host() + ":" + chosen.port() + endpoint;
            return "[" + loadBalancer.strategyName() + "] Routed → " + url;
        }
    }

    // ── Main ──────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== Service Discovery + Load Balancing Demo ===\n");

        ServiceRegistry registry = new ServiceRegistry();

        // Register 3 instances of payment-service (simulating 3 pods in K8s)
        ServiceInstance p1 = new ServiceInstance("payment-service", "10.0.1.10", 8080, "us-east-1a");
        ServiceInstance p2 = new ServiceInstance("payment-service", "10.0.1.11", 8080, "us-east-1b");
        ServiceInstance p3 = new ServiceInstance("payment-service", "10.0.1.12", 8080, "us-east-1c");

        // Register 2 instances of order-service
        ServiceInstance o1 = new ServiceInstance("order-service", "10.0.2.10", 9090, "us-east-1a");
        ServiceInstance o2 = new ServiceInstance("order-service", "10.0.2.11", 9090, "us-east-1b");

        registry.register(p1); registry.register(p2); registry.register(p3);
        registry.register(o1); registry.register(o2);
        registry.printRegistry();

        // Client with round-robin
        DiscoveryClient client = new DiscoveryClient(registry, new RoundRobinLoadBalancer());

        System.out.println("\n--- 6 requests to payment-service (Round-Robin) ---");
        for (int i = 1; i <= 6; i++) {
            System.out.println("  Request " + i + ": " + client.call("payment-service", "/api/pay"));
        }

        // Simulate a pod going down
        System.out.println("\n--- Simulating pod failure: payment-service 10.0.1.11:8080 goes down ---");
        registry.markUnhealthy(p2);

        System.out.println("\n--- 6 more requests — traffic avoids the unhealthy instance ---");
        for (int i = 1; i <= 6; i++) {
            System.out.println("  Request " + i + ": " + client.call("payment-service", "/api/pay"));
        }

        // All instances down
        System.out.println("\n--- Simulating all instances down ---");
        registry.markUnhealthy(p1);
        registry.markUnhealthy(p3);
        System.out.println("  Request: " + client.call("payment-service", "/api/pay"));

        System.out.println("""

            === Key Interview Points ===
            Service registry = source of truth for service locations.
            Health checks ensure stale/dead instances are never routed to.
            Spring Cloud: @EnableEurekaServer + @EnableDiscoveryClient
            Kubernetes: built-in via kube-dns + Service resources (no extra library needed).
            Production: prefer server-side discovery (K8s Service) — simpler client code.
            """);
    }
}
