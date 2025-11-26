package com.example.casopractico3.aspects;

import com.example.casopractico3.orders.Order;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    @Around("@annotation(com.example.casopractico3.annotations.Auditable)")
    public Object aroundAuditableMethods(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Order order = extractOrderFromArgs(pjp.getArgs());

        if (order != null) {
            System.out.printf("--- Auditoría: Inicio de proceso para Pedido %d ---%n",
                    order.getId());
        }

        try {
            Object result = pjp.proceed();  // ejecuta el método real
            long duration = System.currentTimeMillis() - start;

            if (order != null) {
                System.out.printf("[PERFORMANCE] Pedido %d procesado en %d ms%n",
                        order.getId(), duration);
                System.out.printf("--- Auditoría: Fin de proceso para Pedido %d ---%n",
                        order.getId());
            }

            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            if (order != null) {
                System.out.printf("[PERFORMANCE] Pedido %d falló tras %d ms%n",
                        order.getId(), duration);
            }
            // volvemos a lanzar la excepción para que la capture @AfterThrowing
            throw ex;
        }
    }

    @AfterThrowing(
            pointcut = "@annotation(com.example.casopractico3.annotations.Auditable)",
            throwing = "ex")
    public void logErrors(JoinPoint joinPoint, Throwable ex) {
        Order order = extractOrderFromArgs(joinPoint.getArgs());
        if (order != null) {
            System.out.printf("[ERROR] Pedido %d falló: %s%n",
                    order.getId(), ex.getMessage());
        } else {
            System.out.printf("[ERROR] Método %s falló: %s%n",
                    joinPoint.getSignature().getName(), ex.getMessage());
        }
    }

    private Order extractOrderFromArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg instanceof Order)
                .map(Order.class::cast)
                .findFirst()
                .orElse(null);
    }
}
