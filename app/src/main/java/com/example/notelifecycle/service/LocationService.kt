package com.example.notelifecycle.service


import android.util.Log
import androidx.lifecycle.LifecycleService
import com.example.notelifecycle.location.MyLocationListenerWithLifecycle

/***
 * Activity的父类ComponentActivity实现了LifecycleOwner接口，service默认没实现，所以我们应该添加依赖库继承LifecycleService
 */
class LocationService  : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService","onCreate")
        lifecycle.addObserver(MyLocationListenerWithLifecycle(this){
            it.x
            it.y
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LocationService","onDestroy")
    }
}