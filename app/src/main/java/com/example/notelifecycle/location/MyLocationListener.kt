package com.example.notelifecycle.location

import android.content.Context

/**
 * Create by SunnyDay /07/20 21:29:31
 */
class MyLocationListener(private val context: Context,private val callback:(Location)->Unit) {

    fun start() {
        // connect to system location service
        // get current Location
        // feedback
        callback.invoke(Location(50F,50F))
    }

    fun stop() {
        // disconnect from system location service
    }

   data class Location(val x:Float,val y:Float)
}