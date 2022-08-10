package com.example.notelifecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notelifecycle.service.LocationService

class ServiceActivity : AppCompatActivity() {
   private val mIntent by lazy {
       Intent(this,LocationService::class.java)
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        startService(mIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        stopService(mIntent)
    }
}