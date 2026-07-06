
package com.go.common.performance_monitor.anrwatchdog;

import android.content.Context;

public abstract class AbstractSampler {

    protected long mSampleInterval;
    private static final int DEFAULT_SAMPLE_INTERVAL = 300;

    public AbstractSampler() {
        this(0);
    }

    public AbstractSampler(long sampleInterval) {
        if (0 == sampleInterval) {
            sampleInterval = DEFAULT_SAMPLE_INTERVAL;
        }
        mSampleInterval = sampleInterval;
    }

    abstract public AbstractSampler doSample(Context context);

    abstract String getSampleInfo();

    abstract void clearRecord();
}
