package com.example.navigatorapp.Splaash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Slider.SliderActivity
import com.example.navigatorapp.MainActivity

class SplaashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.navigatorapp.R.layout.activity_splaash)

        val sp = getSharedPreferences("SharedPreferences", android.content.Context.MODE_PRIVATE)
        val check = sp.getBoolean("stopSlider",  false)
//        Toast.makeText(this, "$check", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            if (check){
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this,SliderActivity::class.java))
                finish()
            }

        }, 3000)

    }


}