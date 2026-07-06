package com.go.common.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.go.common.performance_monitor.anrwatchdog.ANRWatchDog

private var anrWatchDogThread:Thread? = null
private var notRelaunchAppThread:Thread? = null

class ANRWatchDogInitializer  : Initializer<Unit> {

    override fun create(context: Context) {
        LogUtils.d(BaseApplicationInitializer.TAG,"==ANRWatchDogStarup create anrWatchDogThread:$anrWatchDogThread\tstate:${anrWatchDogThread?.state}")

        if(anrWatchDogThread==null || anrWatchDogThread?.isAlive == false) {
            anrWatchDogThread?.interrupt()
            (context.applicationContext as? Application)?.let { app ->
                anrWatchDogThread = ANRWatchDog(app, 10 * 1000)
                    .setANRListener { error, stackInfo ->
                        LogUtils.d(BaseApplicationInitializer.TAG,"ANRWatchDog error:$error\tstackInfo:$stackInfo")
                        AppUtils.relaunchApp()
                    }
                anrWatchDogThread?.start()
            }
        }

        if(notRelaunchAppThread==null || notRelaunchAppThread?.isAlive == false) {
            notRelaunchAppThread?.interrupt()
            (context.applicationContext as? Application)?.let { app ->
                notRelaunchAppThread = ANRWatchDog(app,3*1000)
                    .setANRListener { error, stackInfo ->
                        LogUtils.d(BaseApplicationInitializer.TAG,"ANRWatchDog error:$error\tstackInfo:$stackInfo")
                    }
                notRelaunchAppThread?.start()
            }
        }


    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}