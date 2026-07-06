package com.go.common.startup

import android.content.Context
import androidx.startup.Initializer
import com.blankj.utilcode.util.LogUtils
import com.go.common.BuildConfig


class BaseApplicationInitializer : Initializer<Unit> {

    companion object {
        const val TAG = "InitializerTAG"
    }

    override fun create(context: Context) {
        LogUtils.d(TAG,"========BaseApplicationInitializer===")
        LogUtils.d(TAG, "BaseApplicationInitializer context:$context\thashCode:${this.hashCode()}\tDEBUG:${BuildConfig.DEBUG}")
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(
            LogInitializer::class.java,
            BuglyInitializer::class.java,
            UtilsInitializer::class.java,
            StrictModeInitializer::class.java,
            ANRWatchDogInitializer::class.java,
            GcWatcherInitializer::class.java
        )
    }
}