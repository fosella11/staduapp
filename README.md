# StaduApp - Control de Acceso en Tiempo Real

Solución Android de alto rendimiento para la gestión de aforo y asignación dinámica de asientos en estadios, basada en procesamiento de eventos vía WebSockets.

## 📱 Vista General del Proyecto

Esta aplicación ha sido diseñada para cumplir con los requisitos del [Reto Kotlin- para corregir.pdf](Reto%20Kotlin-%20para%20corregir.pdf), implementando un sistema reactivo que visualiza la ocupación de un estadio en tiempo real.

### 🎨 Diseño y UI
La interfaz sigue fielmente el diseño propuesto en [STAUAPP.png](STAUAPP.png), destacando:
- **Dashboard Interactivo**: Mapa del estadio con bloques que crecen visualmente según su ocupación.
- **Feedback Visual**: Colores dinámicos (Púrpura/Naranja/Rojo) y alertas de bloqueo al alcanzar el 70% de capacidad.
- **Logs Detallados**: Feed de eventos con chips de colores, puertas de acceso y estado de asignación.

> [!TIP]
> Puedes ver una demostración del funcionamiento en el video [PRUEBA.mp4](PRUEBA.mp4).

---

## 🏗️ Arquitectura y Tecnologías

La aplicación sigue los principios de **Clean Architecture** con un enfoque **Feature-First**, garantizando escalabilidad y facilidad de testeo.

### 🛠️ Stack Tecnológico
- **Lenguaje**: Kotlin 2.0 (Kotlin DSL).
- **UI**: Jetpack Compose con Material 3 y animaciones personalizadas.
- **Concurrencia**: Kotlin Coroutines & Flow (StateFlow/SharedFlow).
- **Red**: OkHttp WebSockets para comunicación bidireccional en tiempo real.
- **Serialización**: Kotlinx Serialization.
- **DI**: Patrón Service Locator para una gestión de dependencias ligera y eficiente.

### 📂 Documentación en la Raíz
Para entender a fondo la implementación, consulta los siguientes archivos:
- 📘 [ARCHITECTURE_ES.md](ARCHITECTURE_ES.md): Guía detallada de la estructura de paquetes, flujo de datos y capas (Domain, Data, Presentation).
- 🧠 [DECISIONS.md](DECISIONS.md): Registro de decisiones técnicas, trade-offs y justificación del uso de `Mutex`, `StateFlow` y estrategias de asignación.

---

## 🚀 Guía de Inicio Rápido

### 1. Preparar el Servidor
La aplicación requiere un servidor WebSocket emitiendo eventos.
- Si tienes el JAR del servidor: `java -jar server.jar --port=8765`
- La app está configurada por defecto para conectarse a la IP de red local detectada en el desarrollo.

### 2. Configuración de Red
- **Emulador**: Usa `10.0.2.2` para referenciar al localhost de tu máquina.
- **Dispositivo Físico**: Actualiza la IP en `ServiceLocator.kt` para que coincida con tu PC.
- **Cleartext**: Habilitado en `AndroidManifest.xml` para permitir conexiones `ws://` locales.

### 3. Ejecución
1. Abre el proyecto en Android Studio.
2. Sincroniza Gradle.
3. Ejecuta: `Shift + F10`.

---

## 🛡️ Reglas de Negocio Implementadas
1. **Multicolor**: Acceso denegado automáticamente.
2. **Azul**: Prioridad en Sector Norte con fallback dinámico.
3. **Estándar**: Asignación por cercanía a la puerta de entrada (Bloque C > B > A).
4. **Auto-Bloqueo**: Cierre automático de bloques al 70% de capacidad, redirigiendo a otros bloques disponibles.

## 🧪 Pruebas Unitarias
Se han incluido tests para validar la lógica de asignación y el estado del motor del estadio:
```bash
./gradlew test
```

---

## 🔧 Configuración de Conexión y Resolución de Problemas

De acuerdo con las especificaciones del reto (**2026-02-09**), el servidor de prueba escucha en `0.0.0.0:8765` y el endpoint es **`ws://localhost:8765` (sin `/events`)**. Los nombres de las puertas (`gate`) se envían en español (**NORTE|SUR|ESTE|OESTE**).

Si experimentas problemas de conexión o procesamiento ("no conecta / no procesa"), verifica estos 3 puntos críticos:

1.  **URL Correcta**: Asegúrate de usar la raíz `ws://192.168.0.170:8765`. El servidor no define el path `/events`.
2.  **Mapeo de Gates**: El parser está configurado para soportar tanto inglés como español. Si el servidor envía `NORTE`, la app lo mapeará correctamente a `SectorName.NORTH`.
3.  **Tráfico Cleartext**: Android bloquea por defecto el tráfico `ws://` (no cifrado). La app ya tiene `android:usesCleartextTraffic="true"` en el manifiesto para permitirlo.

### 🛠️ Guía Rápida para Conectar
1.  **Arregla la URL**: En `ServiceLocator.kt`, usa `ws://<TU_IP_LAN>:8765`.
2.  **Permisos**: Verifica que `INTERNET` y `usesCleartextTraffic` estén en el `AndroidManifest.xml`.
3.  **Registro**: Asegúrate de que `StaduApp` esté registrado en el manifiesto bajo la propiedad `android:name`.

### 🧪 Validación del Entorno (Mac)
*   **Levantar Server**: `python3 websocket_server.py`
*   **Verificar Puerto**: `lsof -nP -iTCP:8765 -sTCP:LISTEN`
*   **Probar Salida**: `websocat ws://localhost:8765`

---

## 🤖 Apoyo y Asistencia de IA
Para acelerar tareas repetitivas (boilerplate, documentación y refactors), utilicé un asistente de IA (**Antigravity/Gemini**) como apoyo. 

Las decisiones de arquitectura (Clean/MVVM, modelo de concurrencia, reglas de negocio), la implementación final y la validación mediante pruebas y ejecución local fueron realizadas y revisadas por mí.

---
*Desarrollado como parte de un desafío técnico de ingeniería Android Senior.*
