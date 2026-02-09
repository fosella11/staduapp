# Stadium Access Control Android App

This is a Senior Android Engineer technical challenge implementation for real-time stadium access control using WebSockets.

## 🚀 How to Run

### 1. **Start the WebSocket Server**
(Based on PDF instructions)
1. Ensure Java 11+ is installed.
2. Run the server JAR (or command provided in PDF):
   ```bash
   java -jar server.jar --port=8080
   ```
   *(Adjust command based on actual PDF content if different)*

### 2. **Configure the App**
The WebSocket URL is configured in `StaduApp.kt`.
If using the Android Emulator, use `ws://10.0.2.2:8080/events`.
If using a physical device, update with your machine's local IP (e.g., `ws://192.168.1.50:8080/events`).

### 3. **Run the Android App**
Open the project in Android Studio Iguana+ or run via command line:
```bash
./gradlew installDebug
```
The app will launch and automatically attempt to connect.

## 🏗️ Architecture

The app follows Clean Architecture principles with Modularization by Layer (in a single module for simplicity):

- **Domain Layer (`com.domingame.staduapp.domain`)**: 
  - Pure Kotlin logic.
  - **Models**: `StadiumState`, `BlockState`, `EntryEvent`.
  - **Engine**: `StadiumEngine` handles the state machine, concurrency (Mutex), and business rules.
  - **Strategy**: `AssignmentStrategy` implements the core algorithms (Standard, Blue, Multicolor).
  
- **Data Layer (`com.domingame.staduapp.data`)**: 
  - **WebSocketManager**: Handles OkHttp connection, reconnection, and flow emission.
  - **Repository**: Maps JSON events to Domain objects and exposes connection state.

- **Presentation Layer (`com.domingame.staduapp.ui`)**: 
  - **ViewModel**: `MainViewModel` exposes `StateFlow` streams for UI.
  - **Compose**: Modern UI using Jetpack Compose, Material3, and Navigation.
  - **Screens**: Dashboard (Map), Metrics (Analytics), Log (History).

## ✅ Implemented Features

- [x] **Real-time WebSocket Connection**: Auto-connect with retry/backoff.
- [x] **Concurrent Event Processing**: Thread-safe state updates using internal Mutex.
- [x] **Assignment Algorithms**:
  - **Standard**: Closest block (C->B->A) in designated Sector. Fallback to Adjacent Sectors.
  - **Blue Shirt**: Forces North Sector allocation. Fallback to Block C in East/West/South.
  - **Multicolor**: Blocks access and logs as "Blocked".
- [x] **Capacity Logic**: Block locks at 70% occupancy (14/20).
- [x] **Metrics**:
  - Total Admitted / Refused / Blocked.
  - Global Average Distance.
  - Sector-level Average Distances.
- [x] **UI**:
  - **Dashboard**: Visual grid of Sectors/Blocks with occupancy indicators.
  - **Log**: Scrollable list of events with status icons.
  
## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material3)
- **Concurrency**: Coroutines, Flow, Mutex, StateFlow
- **Network**: OkHttp (WebSocket)
- **Serialization**: Kotlinx Serialization
- **DI**: Manual Dependency Injection via `StaduApp`

## 🧪 Testing

Unit tests for the domain logic can be found in `src/test/java/.../domain/engine/AssignmentStrategyTest.kt`.
Run tests via:
```bash
./gradlew testDebugUnitTest
```
