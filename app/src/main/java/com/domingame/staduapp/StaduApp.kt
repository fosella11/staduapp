package com.domingame.staduapp

import android.app.Application

/**
 * Application class for StaduApp.
 * Dependency Injection is handled by ServiceLocator in core/di.
 */
class StaduApp : Application() {
    // Empty for now - DI handled by ServiceLocator
    // Future: Could add global configuration, crash reporting, etc.
}
