# Decisiones Técnicas y Trade-offs

Este documento detalla las decisiones arquitectónicas tomadas durante el desarrollo de StaduApp.

## 1. Concurrencia y Serialización
**Decisión**: Uso de `Mutex` en `StadiumEngine`.
- **Razón**: Los eventos de entrada pueden llegar a ráfagas muy altas (high frequency). Para mantener la consistencia del estado del estadio (que es único y compartido), necesitamos asegurar que solo un evento sea procesado a la vez.
- **Trade-off**: El procesamiento es secuencial. Sin embargo, dado que la lógica de negocio es puramente computacional y en memoria, el tiempo de procesamiento es despreciable frente a la latencia de red.

## 2. Gestión de Estado Inmutable
**Decisión**: Uso de `data classes` con `StateFlow`.
- **Razón**: La inmutabilidad garantiza que no haya "race conditions" accidentales donde la UI lea un estado parcialmente modificado.
- **Trade-off**: Mayor consumo de memoria para asignar nuevas copias del estado, pero en Android moderno esto está altamente optimizado y es preferible por seguridad de hilos.

## 3. Límites en el Historial (Log)
**Decisión**: Límite de 500 entradas en el log de la UI.
- **Razón**: Dejar que el log crezca indefinidamente saturaría la memoria del dispositivo y degradaría el rendimiento de las listas en Compose (`LazyColumn`).
- **Solución**: Usamos `.take(500)` en el ViewModel al recibir nuevos eventos.

## 4. Estrategia de Reconexión
**Decisión**: Implementación de Backoff Exponencial en el Repositorio.
- **Razón**: Si el servidor cae, saturarlo con intentos de conexión constantes es contraproducente.
- **Implementación**: Empezamos en 1s y duplicamos hasta un máximo de 30s.

## 5. Inyección de Dependencias Manual (ServiceLocator)
**Decisión**: No usar Dagger/Hilt en esta fase inicial.
- **Razón**: El alcance del reto no justifica la complejidad de configuración de Hilt (KSP, anotaciones). Un `ServiceLocator` sencillo en el paquete `core` es suficiente para desacoplar componentes y facilitar tests.

## 6. Métricas en la UI
**Decisión**: Cálculo de métricas por sector delegado temporalmente en la capa de presentación (Gap P1).
- **Razón**: Prioridad en terminar la visualización funcional. 
- **Plan de mejora**: Mover este cálculo al `StadiumEngine` del dominio para asegurar que la UI sea puramente "dump" (tonta).
