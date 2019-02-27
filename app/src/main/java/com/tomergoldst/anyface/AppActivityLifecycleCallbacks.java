package com.tomergoldst.anyface;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.UUID;

public class AppActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private String mSessionId;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable checkRunnable;
    private boolean mIsForeground = false;
    private boolean mPaused = true;
    private static final int CHECK_DELAY = 200;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mPaused = false;
        boolean wasBackground = !mIsForeground;
        mIsForeground = true;

        if (checkRunnable != null) {
            mHandler.removeCallbacks(checkRunnable);
        }

        if (wasBackground) {
            startSession(activity);
        }

    }

    @Override
    public void onActivityPaused(final Activity activity) {
        mPaused = true;

        if (checkRunnable != null) {
            mHandler.removeCallbacks(checkRunnable);
        }

        mHandler.postDelayed(checkRunnable = new Runnable() {
            @Override
            public void run() {
                if (mIsForeground && mPaused) {
                    endSession(activity.getApplicationContext());
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public String getSessionId() {
        return mSessionId;
    }

    private void startSession(Context context) {
        mSessionId = UUID.randomUUID().toString();
    }

    private void endSession(Context context) {
        mSessionId = null;
        mIsForeground = false;
    }

    public boolean isAppInForeground() {
        return mIsForeground;
    }
}
