package com.example.navigatorapp.Dialouge

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.example.navigatorapp.AltitudeMeter.AltitudemeterActivity
import com.example.navigatorapp.Compass.CompassActivity
import com.example.navigatorapp.Speedometer.SpeedometerActivity
import com.example.navigatorapp.Weather.WeatherActivity
import com.example.navigatorapp.databinding.AdvanceToolDialogBinding

class AdvancedToolDialog (private val mContext: Context): Dialog(mContext){
    private lateinit var binding:AdvanceToolDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setCancelable(true)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = AdvanceToolDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.constraintSpeedometer.setOnClickListener {
         mContext.startActivity(Intent(mContext,SpeedometerActivity::class.java))
        }
        binding.constraintAltitudemter.setOnClickListener {
            mContext.startActivity(Intent(mContext,AltitudemeterActivity::class.java))
        }
        binding.constraintWeather.setOnClickListener {
            mContext.startActivity(Intent(mContext,WeatherActivity::class.java))
        }
        binding.constraintCompaass.setOnClickListener {
            mContext.startActivity(Intent(mContext,CompassActivity::class.java))
        }
    }

}