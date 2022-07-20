package com.example.notelifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notelifecycle.location.MyLocationListener

class MainActivity : AppCompatActivity() {
    private lateinit var myLocationListener: MyLocationListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init and register callback
        myLocationListener = MyLocationListener(this){
            // todo update ui
            it.x
            it.y
        }
    }

    override fun onStart() {
        super.onStart()
        myLocationListener.start()
    }

    override fun onStop() {
        super.onStop()
        myLocationListener.stop()
    }
}