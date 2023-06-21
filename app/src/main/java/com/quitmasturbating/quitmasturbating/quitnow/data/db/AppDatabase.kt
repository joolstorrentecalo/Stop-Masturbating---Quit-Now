package com.example.quitnow.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quitnow.data.db.store.StoreItemDao
import com.example.quitnow.data.db.store.StoreItemEntity
import com.example.quitnow.data.db.user.UserDao
import com.example.quitnow.data.db.user.UserEntity


@Database(entities = [
    UserEntity::class,
    StoreItemEntity::class
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun storeItemDao(): StoreItemDao
}