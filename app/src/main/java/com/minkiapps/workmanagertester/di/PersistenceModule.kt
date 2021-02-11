package com.minkiapps.workmanagertester.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module

private const val PREFS_WORKER_RECORDS = "PREFS_WORKER_RECORDS"
val workerQualifier = StringQualifier(PREFS_WORKER_RECORDS)

val persistenceModule = module {

    single<SharedPreferences>(qualifier = workerQualifier) {
        val ctx = androidApplication()
        ctx.getSharedPreferences(PREFS_WORKER_RECORDS, Context.MODE_PRIVATE)
    }
}