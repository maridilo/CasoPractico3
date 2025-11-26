https://github.com/maridilo/casopractico3.git
# Caso Práctico 3 – Sistema Concurrente de Procesamiento de Pedidos

## Integrantes

- María Díaz - Heredero López 152337
- Nombre Apellido 2 (ID)
- Nombre Apellido 3 (ID, opcional)

## Descripción

Aplicación **Spring Boot** que simula el procesamiento concurrente de pedidos en una tienda online.  
Cada pedido se procesa en un hilo independiente y se utiliza **Programación Orientada a Aspectos (AOP)** para:

- Registrar auditoría (inicio y fin de cada pedido).
- Medir el tiempo de ejecución de cada proceso.
- Capturar y registrar errores simulados (pago rechazado, error de stock).

## Estructura de paquetes

- `com.example.casopractico3.annotations`  
  - `Auditable.java`: anotación personalizada para marcar métodos que deben ser auditados y cronometrados.

- `com.example.casopractico3.aspects`  
  - `AuditAspect.java`: aspecto AOP que intercepta los métodos anotados con `@Auditable` y:
    - Muestra mensajes de auditoría de inicio y fin.
    - Calcula el tiempo de ejecución de cada pedido con `@Around`.
    - Captura excepciones con `@AfterThrowing` y muestra mensajes de error.

- `com.example.casopractico3.orders`  
  - `Order.java`: clase de dominio que representa un pedido (id, total, nombre del cliente).

- `com.example.casopractico3.service`  
  - `OrderProcessingService.java`: contiene la lógica de negocio para procesar un pedido.  
    Simula distintas fases (stock, pago, envío) mediante `Thread.sleep()` con tiempos aleatorios y genera errores simulados.
  - `OrderSimulationRunner.java`: implementa `CommandLineRunner`.  
    Al iniciar la aplicación crea 10 pedidos y los procesa concurrentemente usando un `ExecutorService` con un pool de hilos.  
    Cuenta cuántos pedidos han tenido éxito y cuántos han fallado y muestra el resumen final.

## Funcionamiento

1. Al arrancar la aplicación Spring Boot se ejecuta `OrderSimulationRunner`.
2. Se crean 10 pedidos de ejemplo con distintos clientes.
3. Cada pedido se envía a procesar en un hilo del `ExecutorService`.
4. El método `processOrder` está anotado con `@Auditable`, por lo que:
   - El aspecto `AuditAspect` muestra:
     - `--- Auditoría: Inicio de proceso para Pedido X ---`
     - `--- Auditoría: Fin de proceso para Pedido X ---`
   - Se registra el tiempo:
     - `[PERFORMANCE] Pedido X procesado en N ms`
     - o `[PERFORMANCE] Pedido X falló tras N ms`
   - Si se lanza una excepción simulada, se muestra:
     - `[ERROR] Pedido X falló: <mensaje de error>`
5. Al terminar todos los hilos se muestra:
   - Pedidos completados exitosamente.
   - Pedidos con error.
   - Tiempo total de simulación.

```bash
mvn spring-boot:run
