package com.slateblua.meent.core.services

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.util.Log
import android.view.accessibility.AccessibilityEvent

@SuppressLint("AccessibilityPolicy")
class AppBlockerService : AccessibilityService() {

    companion object {
        // This is a temporary in-memory store for blocked apps.
        // Room or DataStore should be used later
        val blockedApps = mutableSetOf<String>()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()

            if (packageName != null && blockedApps.contains(packageName)) {
                Log.i("AppBlockerService", "Blocking app: $packageName")

                // Navigate to the home screen to block the app usage
                // Add block layer screen later
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
        }
    }

    override fun onInterrupt() {
        Log.w("AppBlockerService", "Service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i("AppBlockerService", "Service connected")
    }
}
