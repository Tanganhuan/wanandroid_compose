package com.go.common

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Looper
import androidx.multidex.MultiDex
import androidx.startup.AppInitializer
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.go.common.http.okHttpClient
import com.go.common.startup.BaseApplicationInitializer
import kotlin.system.measureTimeMillis

open class BaseApplication : Application(), SingletonImageLoader.Factory  {

    companion object {
        private const val TAG = "BaseApplication"
        lateinit var Instance:BaseApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Instance = this
//        initOnIdle()
        initializeStartup()
        LogUtils.d(TAG,"onCreate _application:$Instance")
        //工具类初始化
    }

    private fun initializeStartup() {
        val measureTimeMillis = measureTimeMillis {
            AppInitializer.getInstance(this)
                .initializeComponent(BaseApplicationInitializer::class.java)
        }
        // addIdleHandler measureTimeMillis:62
        LogUtils.d(TAG, "addIdleHandler measureTimeMillis:$measureTimeMillis")
    }

    private fun initOnIdle() {
        val delay = measureTimeMillis {
            LogUtils.d(TAG,"")
        }
        //delay measureTimeMillis:7
        LogUtils.d(TAG,"delay measureTimeMillis:$delay")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LogUtils.d(TAG,"ThreadUtils.getMainHandler().looper.queue.addIdleHandler")
            ThreadUtils.getMainHandler().looper.queue.addIdleHandler {
                initializeStartup()
                false
            }
        } else {
            LogUtils.d(TAG,"Looper.myQueue().addIdleHandler")
            ThreadUtils.getMainHandler().post {
                Looper.myQueue().addIdleHandler {
                    initializeStartup()
                    false
                }
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this);
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        LogUtils.d(TAG, "onConfigurationChanged:${GsonUtils.toJson(newConfig)}")
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        LogUtils.d(TAG, "onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        LogUtils.d(TAG, "onTrimMemory level:$level")
    }

    override fun onTerminate() {
        super.onTerminate()
        LogUtils.d(TAG, "onTerminate")
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components { 
                add(OkHttpNetworkFetcherFactory(callFactory = okHttpClient))
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                val imageCache = context.getExternalFilesDir("files")?.resolve("image_cache")
                    ?:context.cacheDir.resolve("image_cache")
                DiskCache.Builder()
                    .directory(imageCache)
                    .maxSizePercent(0.02)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}