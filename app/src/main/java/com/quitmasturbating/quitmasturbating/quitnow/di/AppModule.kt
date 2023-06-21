package com.example.quitnow.di

import android.content.Context
import androidx.room.Room
import com.example.quitnow.data.db.AppDatabase
import com.example.quitnow.data.db.store.StoreItemDao
import com.example.quitnow.data.db.user.UserDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val applicationContext: Context) {

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app.db").build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideStoreDao(appDatabase: AppDatabase): StoreItemDao {
        return appDatabase.storeItemDao()
    }
}