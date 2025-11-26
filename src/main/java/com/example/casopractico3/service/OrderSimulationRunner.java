package com.example.casopractico3.service;

import com.example.casopractico3.orders.Order;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrderSimulationRunner implements CommandLineRunner {

    private final OrderProcessingService orderProcessingService;

    public OrderSimulationRunner(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== INICIO DE SIMULACIÓN DE PEDIDOS ===");

        List<Order> orders = createSampleOrders();

        int numThreads = 5; // número de hilos en el pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Future<?>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        long simulationStart = System.currentTimeMillis();

        for (Order order : orders) {
            Future<?> future = executor.submit(() -> {
                try {
                    orderProcessingService.processOrder(order);
                    successCount.incrementAndGet();
                } catch (Exception ex) {
                    // el aspecto ya ha registrado el error; aquí sólo contamos
                    errorCount.incrementAndGet();
                }
            });
            futures.add(future);
        }

        // Esperar a que terminen todos
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                // ya contamos el error en el submit
            }
        }

        executor.shutdown();

        long totalTime = System.currentTimeMillis() - simulationStart;

        System.out.println();
        System.out.println("=== PROCESAMIENTO FINALIZADO ===");
        System.out.printf("Pedidos completados exitosamente: %d%n", successCount.get());
        System.out.printf("Pedidos con error: %d%n", errorCount.get());
        System.out.printf("Tiempo total de simulación: %d ms%n", totalTime);
    }

    private List<Order> createSampleOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, 120.50, "Ana López"));
        orders.add(new Order(2, 75.30, "Carlos Gómez"));
        orders.add(new Order(3, 210.99, "Marta Ruiz"));
        orders.add(new Order(4, 59.99, "Diego Torres"));
        orders.add(new Order(5, 310.10, "Laura Fernández"));
        orders.add(new Order(6, 89.49, "Pedro Ramírez"));
        orders.add(new Order(7, 145.00, "Sofía Medina"));
        orders.add(new Order(8, 42.80, "Juan Pérez"));
        orders.add(new Order(9, 99.90, "Lucía Vargas"));
        orders.add(new Order(10, 180.75, "Jorge Castillo"));
        return orders;
    }
}
