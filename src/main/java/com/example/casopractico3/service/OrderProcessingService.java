package com.example.casopractico3.service;

import com.example.casopractico3.annotations.Auditable;
import com.example.casopractico3.orders.Order;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OrderProcessingService {

    private final Random random = new Random();

    @Auditable
    public void processOrder(Order order) {
        System.out.printf("[INFO] Pedido %d recibido para el cliente: %s%n",
                order.getId(), order.getCustomerName());

        // Simulamos varias fases: stock, pago, envío
        simulateStep("Verificando stock", 500, 1500);
        simulateStep("Procesando pago", 800, 2000);
        simulateStep("Preparando envío", 700, 1800);
    }

    private void simulateStep(String stepName, int minMs, int maxMs) {
        try {
            int delay = random.nextInt(maxMs - minMs + 1) + minMs;
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Hilo interrumpido durante " + stepName);
        }

        // Simulación de fallo aleatorio (≈25% de probabilidad total)
        double p = random.nextDouble();
        if (p < 0.15) {
            throw new RuntimeException("Pago rechazado (Error simulado)");
        } else if (p >= 0.15 && p < 0.25) {
            throw new RuntimeException("Error al verificar stock (Error simulado)");
        }
    }
}
