package com.example.quitnow.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import java.math.RoundingMode
import java.util.concurrent.TimeUnit


inline val String.Companion.empty: String
    get() = ""

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Int.toPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

fun Int.toDp(context: Context): Int = (this / context.resources.displayMetrics.density).toInt()

fun Float.toPx(context: Context): Float = (this * context.resources.displayMetrics.density)

fun Float.toDp(context: Context): Float = (this / context.resources.displayMetrics.density)

object Epoch {
    fun now(): Long {
        return System.currentTimeMillis()
    }

    fun calcPercentage(start: Long, end: Long): Float {
        val limit = end - start
        val current = now() - start
        val percent = (current.toFloat() / limit.toFloat())
        val progress = percent * 100

        return when (progress >= 100) {
            true -> 100f
            false -> progress
        }
    }

    fun calcSmoked(years: Int, perDay: Int): Int {
        val days = years * 365
        return days * perDay
    }

    fun calcNotSmoked(start: Long, perDay: Int): Int {
        val days = differenceBetweenTimestampsInDays(maxTime = now(), minTime = start)
        return days * perDay
    }

    fun calcMoney(days: Int, perDay: Int, inPack: Int, price: Float): Float {
        val moneyPerDay = (perDay.toFloat() / inPack.toFloat()) * price

        return (days * moneyPerDay).toBigDecimal().setScale(2, RoundingMode.HALF_UP).toFloat()
    }

    fun calcLifeLost(smoked: Int): String {
        val days = smoked * 11 / 60 / 24
        return DateConverters.daysToDuration(days)
    }

    fun calcLifeRegained(notSmoked: Int): String {
        val days = notSmoked * 11 / 60 / 24
        return DateConverters.daysToDuration(days)
    }

    /**
     * Calculates difference from now to past date in days
     * @param maxTime [Long]
     * @param minTime [Long]
     * @return days [Int]
     */
    fun differenceBetweenTimestampsInDays(maxTime: Long, minTime: Long): Int {
        if (maxTime < minTime) throw IllegalArgumentException("Time-traveling is not allowed.")

        return TimeUnit.MILLISECONDS.toDays(maxTime - minTime).toInt()
    }

    fun calcPassedTime(minTime: Long, maxTime: Long = now()): String {
        var diff = maxTime - minTime

        val mYear = diff / 31556926000 // year in milliseconds
        if (mYear > 0) diff -= mYear * 31556926000

        val mMonth = diff / 2629743000  // month in milliseconds
        if (mMonth > 0) diff -= mMonth * 2629743000

        val mDay = diff / 86400000 // day in milliseconds
        if (mDay > 0) diff -= mDay * 86400000

        val mHours = diff / 3600000 // hour in milliseconds
        if (mHours > 0) diff -= mHours * 3600000

        val mMinutes = diff / 60000 // minute in milliseconds
        if (mMinutes > 0) diff -= mMinutes * 60000

        val mSeconds = diff / 1000 // second in milliseconds

        return when {
            mYear > 0 -> "${mYear}y ${mMonth}m ${mDay}d ${mHours}h ${mMinutes}min ${mSeconds}s"
            mMonth > 0 -> "${mMonth}m ${mDay}d ${mHours}h ${mMinutes}min ${mSeconds}s"
            mDay > 0 -> "${mDay}d ${mHours}h ${mMinutes}min ${mSeconds}s"
            mHours > 0 -> "${mHours}h ${mMinutes}min ${mSeconds}s"
            mMinutes > 0 -> "${mMinutes}min ${mSeconds}s"
            else -> "${mSeconds}s"
        }
    }

}

//TODO add lifecycle component to add listener onResume and remove it onPause
inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

fun hideKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}
