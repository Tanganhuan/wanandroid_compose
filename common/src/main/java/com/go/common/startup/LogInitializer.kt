package com.go.common.startup

import android.content.Context
import androidx.startup.Initializer
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.go.common.BuildConfig

class LogInitializer : Initializer<Unit>  {

    override fun create(context: Context) {
        initLogAndCrashReport(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    private fun initLogAndCrashReport(context: Context) {
        LogUtils.d(BaseApplicationInitializer.TAG,"==initLogAndCrashReport context:$context\thashCode:${hashCode()}")

        val logDirPath = context.getExternalFilesDir("logs")?.absolutePath
        LogUtils.getConfig().run {
            stackDeep = if(BuildConfig.DEBUG) 5 else 7
            globalTag = context.packageName
            saveDays = 7
            isLog2FileSwitch = true
            filePrefix = AppUtils.getAppPackageName()
            // 当自定义路径为空时，写入应用的/cache/log/目录中
            dir = logDirPath
        }

        LogUtils.d(BaseApplicationInitializer.TAG, LogUtils.getConfig())

    }
}