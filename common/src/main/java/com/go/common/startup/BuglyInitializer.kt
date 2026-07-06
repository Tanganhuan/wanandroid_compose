package com.go.common.startup

import android.content.Context
import android.os.Build
import androidx.startup.Initializer
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.go.common.BuildConfig
import com.tencent.bugly.crashreport.CrashReport

class BuglyInitializer : Initializer<Unit>  {

    companion object {
        const val BUGLY_APP_ID = "5d67762814"
        var isInited = false
    }

    override fun create(context: Context) {

        LogUtils.d(BaseApplicationInitializer.TAG,"==BuglyInitializer isInited:$isInited\tgetAppID:${CrashReport.getAppID()}\tgetUserId:${CrashReport.getUserId()}")
        if(isInited) {
            return
        }

        LogUtils.d(BaseApplicationInitializer.TAG,"BuglyInitializer")

        val userId = "${DeviceUtils.getModel()}_${DeviceUtils.getUniqueDeviceId()}".lowercase()
        LogUtils.d(
            BaseApplicationInitializer.TAG,
            "BUGLY_APP_ID:$BUGLY_APP_ID\tDEBUG:${BuildConfig.DEBUG}\tuserId:${userId}"
        )

        val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        LogUtils.d(BaseApplicationInitializer.TAG, "defaultUncaughtExceptionHandler1:$defaultUncaughtExceptionHandler")
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            LogUtils.e(BaseApplicationInitializer.TAG, "setDefaultUncaughtExceptionHandler AppUtils.relaunchApp() thread:$thread\tthrowable:$throwable")
            AppUtils.relaunchApp(true)
        }

        LogUtils.d(BaseApplicationInitializer.TAG, "defaultUncaughtExceptionHandler2:$defaultUncaughtExceptionHandler")
        val crashDirPath = context.getExternalFilesDir("crash")?.absolutePath
        CrashUtils.init(crashDirPath) { crashInfo ->
            LogUtils.e(BaseApplicationInitializer.TAG, "crashInfo:$crashInfo")
        }

        LogUtils.d(
            BaseApplicationInitializer.TAG,
            "defaultUncaughtExceptionHandler3:${Thread.getDefaultUncaughtExceptionHandler()}")

        CrashReport.setUserId(userId)
        CrashReport.setDeviceId(context,userId)
        CrashReport.setDeviceModel(context, Build.MODEL)
        CrashReport.initCrashReport(context, BUGLY_APP_ID, BuildConfig.DEBUG)
        LogUtils.d(
            BaseApplicationInitializer.TAG,
            "defaultUncaughtExceptionHandler4:${Thread.getDefaultUncaughtExceptionHandler()}"
        )
        isInited = true

    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return mutableListOf( LogInitializer::class.java )
    }
}