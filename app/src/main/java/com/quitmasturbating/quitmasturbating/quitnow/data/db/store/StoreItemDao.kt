package com.example.quitnow.data.db.store

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for the store table.
 */
@Dao
interface StoreItemDao {

    /**
     * Observes store data.
     *
     * @return all store items.
     */
    @Query("SELECT * FROM StoreItem")
    fun observeStore(): LiveData<List<StoreItemEntity>>

    /**
     * Sets store item data.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: StoreItemEntity)

    /**
     * Deletes store item data.
     */
    @Query("DELETE FROM StoreItem WHERE id = :id")
    suspend fun delete(id: Int)

    /**
     * Buys store item
     */
    @Query("UPDATE StoreItem SET bought = 1 WHERE id = :id")
    suspend fun buyItem(id: Int)
}