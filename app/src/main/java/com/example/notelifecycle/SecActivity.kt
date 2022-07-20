package com.example.notelifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notelifecycle.observer.MyObserver

class SecActivity : AppCompatActivity() {
    private  val myObserver: MyObserver by lazy {
        MyObserver()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sec)
        lifecycle.addObserver(myObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(myObserver)
    }
}