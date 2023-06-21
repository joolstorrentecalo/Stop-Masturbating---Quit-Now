package com.example.quitnow

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quitnow.data.AppRepo
import com.example.quitnow.data.broadcasts.GoalBroadcastReceiver
import com.example.quitnow.data.db.store.StoreItemEntity
import com.example.quitnow.data.db.user.UserEntity
import com.example.quitnow.data.db.user.UserWithStoreItems
import com.example.quitnow.di.DependencyProvider
import com.example.quitnow.util.DateConverters.getGoalIndex
import com.example.quitnow.util.DateConverters.getGoalTimestamp
import com.example.quitnow.util.DateConverters.toDateTime
import com.example.quitnow.util.Epoch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var repository: AppRepo

    val user: LiveData<UserEntity>
    val userWithStoreItems: LiveData<UserWithStoreItems>
    val store: LiveData<List<StoreItemEntity>>

    private var startEpoch: Long? = null

    init {
        DependencyProvider.appComponent.inject(this)

        user = repository.observeUser()
        userWithStoreItems = repository.observeUserWithStoreItems()
        store = repository.observeStore()
    }

    fun setStartEpoch(epoch: Long?) {
        startEpoch = epoch
    }

    fun getStartEpoch(): Long {
        return startEpoch ?: user.value?.start ?: 0L
    }

    fun setUserData(user: UserEntity, context: Context) {
        viewModelScope.launch {
            repository.setUser(user)
            setGoal(0)
            setGoalNotification(user.goal, context)
        }
    }

    fun getUser(): UserEntity? {
        return user.value
    }

    fun setGoal(position: Int) {
        viewModelScope.launch {
            val start = user.value?.start ?: return@launch
            val timestamp = getGoalTimestamp(position, start)
            repository.updateGoal(timestamp)
        }
    }

    fun getGoal(): Int {
        val start = user.value?.start ?: return 0
        val goal = user.value?.goal ?: return 0

        return getGoalIndex(start, goal)
    }

    fun calculateNotSmoked(): Int {
        return user.value?.let {
            val days = Epoch.differenceBetweenTimestampsInDays(Epoch.now(), it.start)

            (days.toFloat() * it.cigPerDay.toFloat()).toInt()
        } ?: 0
    }

    fun getSmokeFreeDays(): Int {
        return user.value?.let {
            Epoch.differenceBetweenTimestampsInDays(Epoch.now(), it.start)
        } ?: 0
    }

    fun getRegainedDays(): Int {
        val notSmoked = calculateNotSmoked()
        return calculateLifeRegained(notSmoked)
    }

    private fun calculateLifeRegained(notSmoked: Int): Int {
        return notSmoked * 11 / 60 / 24
    }

    fun addStoreItem(item: StoreItemEntity) {
        viewModelScope.launch {
            repository.addStoreItem(item)
        }
    }

    fun removeStoreItem(id: Int) {
        viewModelScope.launch {
            repository.removeStoreItem(id)
        }
    }

    fun buyStoreItem(id: Int) {
        viewModelScope.launch {
            repository.buyStoreItem(id)
        }
    }

    fun setGoalNotification(epoch: Long, context: Context) {
        if (epoch <= Epoch.now()) return

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, GoalBroadcastReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, GoalBroadcastReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        Timber.i("Trigger notification at ${toDateTime(context, epoch)}")
        alarmMgr.setExact(AlarmManager.RTC, epoch, alarmIntent)
    }
}