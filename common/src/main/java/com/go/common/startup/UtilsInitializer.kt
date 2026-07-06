package com.go.common.startup

import android.content.Context
import androidx.startup.Initializer
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.go.common.BaseApplication

class UtilsInitializer  : Initializer<Unit> {

    override fun create(context: Context) {
        LogUtils.d(BaseApplicationInitializer.TAG,"==UtilsInitializer create context:$context\thashCode:${hashCode()}")
        Utils.init(BaseApplication.Instance)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}