package com.example.navigatorapp.AltitudeMeter

import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.R
import com.example.navigatorapp.RouteNavigation.POI_Route_Activity
import com.example.navigatorapp.databinding.ActivityAltitudemeterBinding
import com.example.navigatorapp.locationRepositry.LocationRepository
import com.example.navigatorapp.locationRepositry.MyLocationInterface
import java.io.IOException
import java.text.DecimalFormat
import java.text.FieldPosition
import java.util.*

class AltitudemeterActivity : AppCompatActivity() {
    lateinit var locationRepository:LocationRepository
    lateinit var binding:ActivityAltitudemeterBinding

    private var altitude: Double? = null
    var lat:Double=0.0
    var long:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAltitudemeterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getaltitude(1)
        clicklistener()


    }

    private fun getaltitude(position: Int) {
        locationRepository = LocationRepository(this, object : MyLocationInterface {
            override fun onLocationChange(location: Location) {
                altitude = location.altitude

                if (position==1){
                    latitudeInMeter(location)
                }else{
                    latitudeInFeet(location)
                }
                val cLat = location.latitude
                val cLong = location.longitude
                val f_lat= DecimalFormat("#.###").format(cLat)
                val f_long= DecimalFormat("#.###").format(cLong)
                binding.txtSetlat.text= f_lat.toString()
                binding.txtSetlong.text= f_long.toString()
                locationRepository.stopLocation()

                val geocoder = Geocoder(
                    this@AltitudemeterActivity,
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
    private fun latitudeInMeter(location: Location){
        val alt = location.altitude.toFloat()
        binding.altitudeview.maxSpeed=1000F
        binding.altitudeview.unit = "Meter"
        binding.altitudeview.speedTo(alt)

    }
    private fun latitudeInFeet(location: Location){
        val alt = location.altitude.toFloat()
        binding.altitudeview.maxSpeed=1000F
        binding.altitudeview.unit = "Ft"
        val alt1 = (alt*3.281).toFloat()
        binding.altitudeview.speedTo(alt1)
    }
    private fun clicklistener(){

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

     binding.btnMeter.setOnClickListener {
         binding.btnMeter.setCardBackgroundColor(Color.parseColor("#4B7AFC"))
         binding.txtmeter.setTextColor(Color.parseColor("#FFFFFF"))
         binding.btnfeet.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
         binding.txtfeet.setTextColor(Color.parseColor("#212121"))

      getaltitude(1)

     }
        binding.btnfeet.setOnClickListener {
            binding.btnfeet.setCardBackgroundColor(Color.parseColor("#4B7AFC"))
            binding.txtfeet.setTextColor(Color.parseColor("#FFFFFF"))
            binding.btnMeter.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.txtmeter.setTextColor(Color.parseColor("#212121"))
            getaltitude(2)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}