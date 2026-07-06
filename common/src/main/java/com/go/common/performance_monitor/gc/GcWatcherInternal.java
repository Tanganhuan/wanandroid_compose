package com.go.common.performance_monitor.gc;

import android.os.SystemClock;

import com.go.common.BuildConfig;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/** 通过对象回收时会调用 finalize 实现gc监听。
 * gc 监听器
 * 参考自com.android.internal.os.BinderInternal
 */
public class GcWatcherInternal {

    //SoftReference 是内存溢出前才会释放,WeakReference是每次GC回收都会触发。
    private static Reference<GcWatcher> sGcWatcher;

    private static final ArrayList<Runnable> sGcWatchers = new ArrayList<>();
    private static final Object lock=new Object();
    private static long sLastGcTime;
    private static long gcCount = 0;

    private static final class GcWatcher {

        //对象被回收前会调用 finalize 方法
        @Override
        protected void finalize() throws Throwable {
            gcCount++;
            if(System.currentTimeMillis() - sLastGcTime < 1000L) {
                return;
            }

            sLastGcTime = SystemClock.uptimeMillis();
            ArrayList<Runnable> sTmpWatchers;
            synchronized (lock) {
                sTmpWatchers = sGcWatchers;
                try{
                    for (int i = 0; i < sTmpWatchers.size(); i++) {
                        if (sTmpWatchers.get(i) != null) {
                            sTmpWatchers.get(i).run();
                        }
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                }
                createReference();
            }

        }
    }

    private static void createReference() {
        if(BuildConfig.DEBUG) {
            sGcWatcher = new WeakReference<>(new GcWatcher());
        } else {
            sGcWatcher = new SoftReference<>(new GcWatcher());
        }
    }

    public static long getGcCount() {
        return gcCount;
    }

    public static void addGcWatcher(Runnable watcher) {
        synchronized (lock) {
            sGcWatchers.add(watcher);
            if(sGcWatcher==null)
                createReference();
        }
    }

    public static void removeGcWatcher(Runnable watcher) {
        synchronized (lock) {
            sGcWatchers.remove(watcher);
            if(sGcWatchers.isEmpty())
                sGcWatcher=null;
        }
    }
}