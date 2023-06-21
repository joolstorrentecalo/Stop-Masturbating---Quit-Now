package com.example.quitnow.util

import android.content.Context
import com.example.quitnow.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.ROOT


object DateConverters {

    enum class Duration {
        MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS, ERA
    }

    private fun formatDateTime(context: Context): SimpleDateFormat {
        return SimpleDateFormat("${context.getString(R.string.common_date_time_formatting_date_day_month_year)} ${context.getString(R.string.common_date_time_formatting_time_hour_minute)}", ROOT)
    }

    fun toDateTime(context: Context, epoch: Long): String {
        return formatDateTime(context).format(epoch)
    }

    fun getGoalTimestamp(position: Int, start: Long): Long {
        return when (position) {
            0 -> getEndTimestamp(start, 2, Duration.DAYS)
            1 -> getEndTimestamp(start, 3, Duration.DAYS)
            2 -> getEndTimestamp(start, 4, Duration.DAYS)
            3 -> getEndTimestamp(start, 5, Duration.DAYS)
            4 -> getEndTimestamp(start, 6, Duration.DAYS)
            5 -> getEndTimestamp(start, 1, Duration.WEEKS)
            6 -> getEndTimestamp(start, 10, Duration.DAYS)
            7 -> getEndTimestamp(start, 2, Duration.WEEKS)
            8 -> getEndTimestamp(start, 3, Duration.WEEKS)
            9 -> getEndTimestamp(start, 1, Duration.MONTHS)
            10 -> getEndTimestamp(start, 3, Duration.MONTHS)
            11 -> getEndTimestamp(start, 6, Duration.MONTHS)
            12 -> getEndTimestamp(start, 1, Duration.YEARS)
            13 -> getEndTimestamp(start, 5, Duration.YEARS)
            15 -> getEndTimestamp(start, 10, Duration.YEARS)
            else -> 0L
        }
    }

    fun getGoalIndex(start: Long, goal: Long): Int {
        return when (goal) {
            getEndTimestamp(start, 2, Duration.DAYS) -> 0
            getEndTimestamp(start, 3, Duration.DAYS) -> 1
            getEndTimestamp(start, 4, Duration.DAYS) -> 2
            getEndTimestamp(start, 5, Duration.DAYS) -> 3
            getEndTimestamp(start, 6, Duration.DAYS) -> 4
            getEndTimestamp(start, 1, Duration.WEEKS) -> 5
            getEndTimestamp(start, 10, Duration.DAYS) -> 6
            getEndTimestamp(start, 2, Duration.WEEKS) -> 7
            getEndTimestamp(start, 3, Duration.WEEKS) -> 8
            getEndTimestamp(start, 1, Duration.MONTHS) -> 9
            getEndTimestamp(start, 3, Duration.MONTHS) -> 10
            getEndTimestamp(start, 6, Duration.MONTHS) -> 11
            getEndTimestamp(start, 1, Duration.YEARS) -> 12
            getEndTimestamp(start, 5, Duration.YEARS) -> 13
            getEndTimestamp(start, 10, Duration.YEARS) -> 15
            else -> 0
        }
    }

    fun getGoalValue(context: Context, start: Long, goal: Long): String {
        return when (goal) {
            getEndTimestamp(start, 2, Duration.DAYS) -> context.resources.getString(R.string.goal_two_days)
            getEndTimestamp(start, 3, Duration.DAYS) -> context.resources.getString(R.string.goal_three_days)
            getEndTimestamp(start, 4, Duration.DAYS) -> context.resources.getString(R.string.goal_four_days)
            getEndTimestamp(start, 5, Duration.DAYS) -> context.resources.getString(R.string.goal_five_days)
            getEndTimestamp(start, 6, Duration.DAYS) -> context.resources.getString(R.string.goal_six_days)
            getEndTimestamp(start, 1, Duration.WEEKS) -> context.resources.getString(R.string.goal_one_week)
            getEndTimestamp(start, 10, Duration.DAYS) -> context.resources.getString(R.string.goal_ten_days)
            getEndTimestamp(start, 2, Duration.WEEKS) -> context.resources.getString(R.string.goal_two_weeks)
            getEndTimestamp(start, 3, Duration.WEEKS) -> context.resources.getString(R.string.goal_three_weeks)
            getEndTimestamp(start, 1, Duration.MONTHS) -> context.resources.getString(R.string.goal_one_month)
            getEndTimestamp(start, 3, Duration.MONTHS) -> context.resources.getString(R.string.goal_three_months)
            getEndTimestamp(start, 6, Duration.MONTHS) -> context.resources.getString(R.string.goal_six_months)
            getEndTimestamp(start, 1, Duration.YEARS) -> context.resources.getString(R.string.goal_one_year)
            getEndTimestamp(start, 5, Duration.YEARS) -> context.resources.getString(R.string.goal_five_years)
            getEndTimestamp(start, 10, Duration.YEARS) -> context.resources.getString(R.string.goal_ten_years)
            else -> String.empty
        }
    }

    fun daysToDuration(days: Int): String {
        val mYears = days / 365000000
        val mMonths = days / 300000000

        return when {
            mYears > 0 -> {
                val mDays = days / 12 / 30
                "${mYears}y ${mMonths}m ${mDays} days"
            }
            mMonths > 0 -> {
                val mDays = days / 30
                "${mMonths}m ${mDays} days"
            }
            else -> "${days} days"
        }
    }

    fun getEndTimestamp(start: Long, duration: Int, type: Duration): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = start

        return when (type) {
            Duration.MINUTES -> {
                c.add(Calendar.MINUTE, duration)
                c.timeInMillis
            }
            Duration.HOURS -> {
                c.add(Calendar.HOUR, duration)
                c.timeInMillis
            }
            Duration.DAYS -> {
                c.add(Calendar.DATE, duration)
                c.timeInMillis
            }
            Duration.WEEKS -> {
                c.add(Calendar.WEEK_OF_YEAR, duration)
                c.timeInMillis
            }
            Duration.MONTHS -> {
                c.add(Calendar.MONTH, duration)
                c.timeInMillis
            }
            Duration.YEARS -> {
                c.add(Calendar.YEAR, duration)
                c.timeInMillis
            }
            Duration.ERA -> {
                c.add(Calendar.ERA, duration)
                c.timeInMillis
        }
    }
}}