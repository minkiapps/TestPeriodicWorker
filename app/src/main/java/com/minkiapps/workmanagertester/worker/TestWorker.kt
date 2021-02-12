package com.minkiapps.workmanagertester.worker

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.work.*
import com.minkiapps.workmanagertester.App.Companion.WORKER_NOTIFICATION_CHANNEL_ID
import com.minkiapps.workmanagertester.R
import com.minkiapps.workmanagertester.di.workerQualifier
import com.minkiapps.workmanagertester.util.isBatteryOptimizationIgnored
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(KoinApiExtension::class)
class TestWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params), KoinComponent {

    private val prefs: SharedPreferences by inject(workerQualifier)

    override suspend fun doWork(): Result {

        val text = "Periodic work just did some work on ${Date()}, BA is off: ${applicationContext.isBatteryOptimizationIgnored()}"

        val date = Date()
        prefs.edit {
            putString(date.time.toString(), text)
        }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, WORKER_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Periodic Worker")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        return Result.success()
    }

    companion object {
        private const val TAG = "TestWorker"

        fun enqueueTestWorker(workManager: WorkManager) {
            val request = PeriodicWorkRequestBuilder<TestWorker>(1, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .addTag(TAG)
                .build()

            workManager.enqueueUniquePeriodicWork(
                TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
    }
}