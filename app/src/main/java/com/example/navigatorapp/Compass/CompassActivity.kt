package com.example.navigatorapp.Compass

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.navigatorapp.R
import android.widget.TextView

import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.widget.ImageView
import com.example.navigatorapp.databinding.ActivityCompassBinding
import android.view.animation.Animation

import android.view.animation.RotateAnimation
import android.widget.Toast
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.locationRepositry.LocationRepository
import com.example.navigatorapp.locationRepositry.MyLocationInterface
import java.text.DecimalFormat
import java.util.*


class CompassActivity : AppCompatActivity(), SensorEventListener {
    lateinit var binding: ActivityCompassBinding
    lateinit var locationRepository:LocationRepository

    private var currentDegree = 0f
    private var mSensorManager: SensorManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getCurrentLocation()

        mSensorManager =  getSystemService(SENSOR_SERVICE) as SensorManager

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

    private fun getCurrentLocation() {
        locationRepository = LocationRepository(this, object : MyLocationInterface {
            override fun onLocationChange(location: Location) {
                val cLat = location.latitude
                val cLong = location.longitude
                val f_lat= DecimalFormat("#.###").format(cLat)
                val f_long= DecimalFormat("#.###").format(cLong)
                binding.txtSetlat.text= f_lat.toString()
                binding.txtSetlong.text= f_long.toString()
                locationRepository.stopLocation()

                val geocoder = Geocoder(
                    this@CompassActivity,
                    Locale.getDefault()
                )
                val addreses = geocoder.getFromLocation(
                    location.latitude, location.longitude,
                    1
                ).toString()
                val adders = addreses.split(":").toTypedArray()
                val add1 = adders[1]
                val addres2 = add1.split("]").toTypedArray()
                val finaladdres = addres2[0]
                binding.txtaddress.setText(finaladdres)
                binding.txtaddress.isSelected=true
            }
        })
    }


    override fun onSensorChanged(p0: SensorEvent?) {
        val degree = Math.round(p0!!.values.get(0)).toFloat()
        binding.tvHeading.text =  java.lang.Float.toString(degree)+"°"

        val ra = RotateAnimation(
            currentDegree,
            -degree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        ra.duration = 210
        ra.fillAfter = true
        binding.imageViewCompass.startAnimation(ra)
        currentDegree = -degree

    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onResume() {
        super.onResume()

        // for the system's orientation sensor registered listeners
        if (mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){

            mSensorManager!!.registerListener(
                this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME
            )
        }else {
        Toast.makeText(this,"Sensor not available",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        // to stop the listener and save battery
        mSensorManager!!.unregisterListener(this);
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}