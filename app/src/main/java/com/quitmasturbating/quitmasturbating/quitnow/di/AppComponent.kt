package com.example.quitnow.di

import com.example.quitnow.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
internal interface AppComponent {
    fun inject(obj: MainViewModel)
}