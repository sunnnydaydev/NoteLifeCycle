package com.example.notelifecycle.location

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Create by SunnyDay /07/21 21:01:08
 */
class MyLocationListenerWithLifecycle(
    private val context: Context,
    private val callback:(Location)->Unit
):LifecycleObserver {
    companion object{
      const val tag = "Lifecycle"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        Log.d(tag,"onStart")
        // connect to system location service
        // get current Location
        // feedback
        callback.invoke(Location(50F,50F))
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        Log.d(tag,"onStop")
        // disconnect from system location service
    }

    data class Location(val x:Float,val y:Float)
}