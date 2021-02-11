package com.minkiapps.workmanagertester

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.WorkManager
import com.minkiapps.workmanagertester.di.persistenceModule
import com.minkiapps.workmanagertester.worker.TestWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(persistenceModule)
        }

        createNotificationChannels()
        TestWorker.enqueueTestWorker(WorkManager.getInstance(this))
    }

    private fun createNotificationChannels() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelInfectionMessage = NotificationChannel(
                WORKER_NOTIFICATION_CHANNEL_ID,
                "Background Worker",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channelInfectionMessage)
        }
    }

    companion object {
        const val WORKER_NOTIFICATION_CHANNEL_ID = "channel_background_worker"
    }
}