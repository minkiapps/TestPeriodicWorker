package com.minkiapps.workmanagertester.util

import android.content.Context
import android.os.PowerManager

fun Context.isBatteryOptimizationIgnored(): Boolean {
    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(packageName)
}