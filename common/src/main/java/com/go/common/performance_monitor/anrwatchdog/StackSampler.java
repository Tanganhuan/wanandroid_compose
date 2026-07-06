package com.go.common.performance_monitor.anrwatchdog;

import android.content.Context;
import android.os.Looper;


/**
 * Dumps thread stack.
 */
public class StackSampler extends AbstractSampler {

    private static final int DEFAULT_MAX_ENTRY_COUNT = 100;

    private final Thread mCurrentThread;

    private StringBuilder stringBuilder;
    public StackSampler() {
        this(Looper.getMainLooper().getThread(),0);
    }

    public StackSampler(Thread thread, long sampleIntervalMillis) {
        this(thread, DEFAULT_MAX_ENTRY_COUNT, sampleIntervalMillis);
    }

    public StackSampler(Thread thread, int maxEntryCount, long sampleIntervalMillis) {
        super(sampleIntervalMillis);
        mCurrentThread = thread;
    }


    @Override
    public String getSampleInfo() {
       if(stringBuilder == null) {
           return "";
       }
       return stringBuilder.toString();
    }

    @Override
    public void clearRecord() {
        stringBuilder = null;
    }

    @Override
    public StackSampler doSample(Context context) {
        if(stringBuilder==null) {
            stringBuilder = new StringBuilder("\r\n=============调用栈跟踪采集开始==============\r\n");
        } else {
            stringBuilder.append("\r\n=============调用栈跟踪采集开始==============\r\n");
        }


        for (StackTraceElement stackTraceElement : mCurrentThread.getStackTrace()) {
            stringBuilder
                    .append(stackTraceElement.toString())
                    .append("\r\n");
        }

        stringBuilder.append("\r\n=============调用栈跟踪采集结束==============\r\n");
        return this;
    }
}