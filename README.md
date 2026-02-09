# StaduApp - Control de Acceso en Tiempo Real

Solución Android para el reto técnico de gestión de aforo y asignación dinámica de asientos basada en reglas de negocio.

## 🚀 Guía de Inicio Rápido

### 1. Preparar el Servidor
La aplicación requiere un servidor WebSocket emitiendo eventos.
- Si tienes el JAR del servidor: `java -jar server.jar --port=8765`
- La app está configurada por defecto para `ws://192.168.0.170:8765`.

### 2. Configuración de Red (Importante)
- **Emulador**: Usa `10.0.2.2` para referenciar al localhost de tu máquina.
- **Dispositivo Físico**: Asegúrate de que el móvil y el PC estén en la misma red WiFi y actualiza la IP en `ServiceLocator.kt`.
- **Cleartext**: La app tiene habilitado `android:usesCleartextTraffic="true"` en el `AndroidManifest.xml` para permitir conexiones `ws://` no cifradas.

### 3. Ejecución
1. Abre el proyecto en Android Studio (Ladybug o superior).
2. Sincroniza Gradle.
3. Ejecuta en tu dispositivo: `Shift + F10`.

## 🏗️ Resumen de Arquitectura
La app utiliza **Clean Architecture** dividida por **features**:
- **Domain**: Reglas de negocio puras (Multicolor, Blue, Standard).
- **Data**: Gestión de WebSockets con OkHttp y flujos reactivos.
- **Presentation**: UI moderna con Jetpack Compose y MVVM.

Consulte [ARCHITECTURE_ES.md](ARCHITECTURE_ES.md) para un detalle profundo.

## 🛡️ Reglas de Asignación
1. **Multicolor**: Acceso denegado inmediatamente.
2. **Azul**: Prioridad Sector Norte -> Fallback Bloque C otros sectores.
3. **Estándar**: Asignación según la puerta de entrada al bloque más cercano (C > B > A).
4. **Bloqueo 70%**: Los bloques se cierran automáticamente al llegar al 70% de su capacidad.

## 🧪 Verificación
Ejecute los tests unitarios para validar las reglas de negocio:
```bash
./gradlew test
```

## 🛠️ Troubleshooting
- **No conecta**: Verifica que el puerto 8765 esté abierto en el firewall de tu PC.
- **Eventos no aparecen**: Revisa el Logcat filtrando por "StadiumRemote".
- **Error de compilación**: Asegúrate de usar Java 17 para el proceso de build de Gradle.
