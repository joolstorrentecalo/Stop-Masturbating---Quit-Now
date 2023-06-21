package com.example.quitnow.ui.trophies

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class Trophy {
    data class Data(@DrawableRes val imgRes: Int, val achieved: Boolean) : Trophy()
    data class Separator(@StringRes val titleRes: Int) : Trophy()
}
