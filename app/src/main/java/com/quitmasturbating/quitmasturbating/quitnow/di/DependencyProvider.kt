package com.example.quitnow.di

internal object DependencyProvider {

    lateinit var appComponent: AppComponent

    fun init(appComponent: AppComponent) {
        this.appComponent = appComponent
    }
}