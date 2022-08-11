package com.example.notelifecycle.application

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Create by SunnyDay /07/20 22:08:38
 */
class AppObserver : LifecycleObserver {
    companion object {
        const val TAG = "AppObserver"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun activityCreate() {
        Log.i(TAG, "onCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activityStart() {
        Log.i(TAG, "onStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun activityResume() {
        Log.i(TAG, "onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun activityStop() {
        Log.i(TAG, "onStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun activityDestroy() {
        Log.i(TAG, "onDestroy")
    }

}