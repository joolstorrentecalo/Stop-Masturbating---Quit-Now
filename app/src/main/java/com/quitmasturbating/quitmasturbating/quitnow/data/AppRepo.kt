package com.example.quitnow.data

import androidx.lifecycle.LiveData
import com.example.quitnow.data.db.store.StoreItemDao
import com.example.quitnow.data.db.store.StoreItemEntity
import com.example.quitnow.data.db.user.UserDao
import com.example.quitnow.data.db.user.UserEntity
import com.example.quitnow.data.db.user.UserWithStoreItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AppRepo @Inject constructor(
    private val userDao: UserDao,
    private val storeItemDao: StoreItemDao
) {

    suspend fun setUser(userEntity: UserEntity) = withContext(Dispatchers.IO) {
        userDao.setUser(userEntity)
    }

    suspend fun updateGoal(timestamp: Long) = withContext(Dispatchers.IO) {
        userDao.updateGoal(timestamp)
    }

    fun observeUser(): LiveData<UserEntity> {
        return userDao.observeUser()
    }

    fun observeUserWithStoreItems(): LiveData<UserWithStoreItems> {
        return userDao.observeUserWithStoreItems()
    }

    fun observeStore(): LiveData<List<StoreItemEntity>> {
        return storeItemDao.observeStore()
    }

    suspend fun addStoreItem(item: StoreItemEntity) = withContext(Dispatchers.IO) {
        storeItemDao.insert(item)
    }

    suspend fun removeStoreItem(id: Int) = withContext(Dispatchers.IO) {
        storeItemDao.delete(id)
    }

    suspend fun buyStoreItem(id: Int) = withContext(Dispatchers.IO) {
        storeItemDao.buyItem(id)
    }
}