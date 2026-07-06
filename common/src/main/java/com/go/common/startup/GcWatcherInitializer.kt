package com.go.common.startup

import android.content.Context
import androidx.startup.Initializer
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.go.common.BuildConfig
import com.go.common.performance_monitor.gc.GcWatcherInternal
import java.util.*

class GcWatcherInitializer : Initializer<Unit> {

    companion object {
        const val TAG = "GcWatcherInitTAG"
    }

    override fun create(context: Context) {
        LogUtils.d(BaseApplicationInitializer.TAG,"==GcWatcherInitializer create context:$context\thashCode:${hashCode()}")
        GcWatcherInternal.addGcWatcher {
            val msg = "${if(!BuildConfig.DEBUG) "内存不足，请检查并优化内存占用" else "回存回收"}\tgcCount:${GcWatcherInternal.getGcCount()}\t发生了GC事件:${TimeUtils.date2String(Date(),"yyyy-MM-dd HH:mm:ss:SSS")}"
            LogUtils.d(TAG, msg)
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}