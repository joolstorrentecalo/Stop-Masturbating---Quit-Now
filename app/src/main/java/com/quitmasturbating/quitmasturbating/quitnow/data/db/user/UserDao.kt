package com.example.quitnow.data.db.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for the user table.
 */
@Dao
interface UserDao {

    /**
     * Observes user data.
     *
     * @return all user fields.
     */
    @Query("SELECT * FROM User WHERE uid = 0")
    fun observeUser(): LiveData<UserEntity>

    @Query("SELECT * FROM User WHERE uid = 0")
    fun observeUserWithStoreItems(): LiveData<UserWithStoreItems>

    @Query("SELECT * FROM User WHERE uid = 0")
    suspend fun get(): UserEntity

    /**
     * Sets user data.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUser(userEntity: UserEntity)

    @Query("DELETE FROM User WHERE uid = 0")
    suspend fun delete()

    @Query("UPDATE User SET goal = :timestamp WHERE uid = 0")
    suspend fun updateGoal(timestamp: Long)
}