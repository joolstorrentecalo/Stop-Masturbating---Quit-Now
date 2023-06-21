package com.example.quitnow.data.db.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey
    var uid: Int = 0,
    var start: Long = 0L,
    var cigPerDay: Int = 0,
    var inPack: Int = 0,
    var years: Int = 0,
    var price: Float = 0f,
    var goal: Long = 0L
)