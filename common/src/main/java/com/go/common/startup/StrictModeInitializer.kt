package com.go.common.startup

import android.content.Context
import android.os.StrictMode
import androidx.startup.Initializer
import com.blankj.utilcode.util.LogUtils

class StrictModeInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        LogUtils.d(BaseApplicationInitializer.TAG,"==StrictModeInitializer create context:$context\thashCode:${hashCode()}")
        val threadPolicyBuilder =  StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
//        if(BuildConfig.DEBUG) {
//            threadPolicyBuilder.penaltyDeath()
//        }

        StrictMode.setThreadPolicy(
            threadPolicyBuilder.build()
        )

        val vmPolicyBuilder = StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()


//        if(BuildConfig.DEBUG) {
//            vmPolicyBuilder.penaltyDeath()
//        }

        StrictMode.setVmPolicy(
            vmPolicyBuilder.build()
        )
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}