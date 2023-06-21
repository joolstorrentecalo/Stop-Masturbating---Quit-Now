package com.example.quitnow

import android.app.Application
import com.example.quitnow.di.AppModule
import com.example.quitnow.di.DaggerAppComponent
import com.example.quitnow.di.DependencyProvider
import timber.log.Timber

class MainApplication : Application() {

    companion object {
        const val GOAL_NOTIFICATION_ID = 0
        const val GOAL_CHANNEL_ID = "GOAL_CHANNEL_ID"
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        val component = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()

        DependencyProvider.init(component)
    }
}