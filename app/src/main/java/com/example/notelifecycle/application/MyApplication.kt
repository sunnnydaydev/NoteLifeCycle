package com.example.notelifecycle.application

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * Create by SunnyDay /08/11 21:06:31
 */
class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppObserver())
    }
}