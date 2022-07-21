package com.example.notelifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notelifecycle.location.MyLocationListenerWithLifecycle
import com.example.notelifecycle.observer.MyObserver

class SecActivity : AppCompatActivity() {
    private  val myObserver: MyObserver by lazy {
        MyObserver()
    }

    private  val mLocationListenerWithLifecycle: MyLocationListenerWithLifecycle by lazy {
        MyLocationListenerWithLifecycle(this){
            // todo update ui
           it.x
           it.y
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sec)

        lifecycle.addObserver(myObserver)
        lifecycle.addObserver(mLocationListenerWithLifecycle)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(myObserver)
        lifecycle.removeObserver(mLocationListenerWithLifecycle)
    }
}