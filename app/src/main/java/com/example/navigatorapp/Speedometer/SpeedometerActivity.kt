package com.example.navigatorapp.Speedometer

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.R
import com.example.navigatorapp.databinding.ActivitySpeedometerBinding
import com.example.navigatorapp.locationRepositry.LocationRepository
import com.example.navigatorapp.locationRepositry.MyLocationInterface
import java.util.*

class SpeedometerActivity : AppCompatActivity() {
    lateinit var locationRepository: LocationRepository
    lateinit var binding: ActivitySpeedometerBinding
    private var speed: Float? = null
    private var seconds = 0
    private var timeString =""
    private var running = false
    var count = 0
    var lat: Double = 0.0
    var lng: Double = 0.0
    var handler: Handler? = null
    var latDest: Double = 0.0
    var lngDest: Double = 0.0
    var nCurrentSpeed = 0
    var totalSpeed = 0.0f
    var distance = 0.0f
    private var maxspeed: Int = 0



        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySpeedometerBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        getspeed()
        clicklistener()
    }

    private fun getspeed() {
        locationRepository = LocationRepository(this, object : MyLocationInterface {
            override fun onLocationChange(location: Location) {
                speed = location.speed
                binding.speedMeter.speedTo(speed!!)
                Log.e("TAG", "onLocationChange: speedd $speed", )
                binding.txtSetspeed.text=speed.toString()+"\nMPH"


                if (count==0 && location!=null){
                    lat = location.latitude
                    lng = location.longitude
                }
                latDest = location.latitude
                lngDest = location.longitude
                nCurrentSpeed = location.speed.toInt()
                count++
                totalSpeed += location.speed

                binding.txtSetavg.text = String.format("%.2f", totalSpeed/ count );
                binding.speedMeter.speedTo(nCurrentSpeed.toFloat())
                distance = calculateDistance(lat, lng, latDest, lngDest)
               var distance1= (distance * 0.001).toFloat()
                Log.e("TAG", "onLocationChange: $distance", )
                Log.e("TAG", "onLocationChange: $distance1", )

                binding.txtSetdistance.text = String.format("%.2f", distance )


                val currentspeed = location.speed.toInt()
                Log.d("TAG", "onLocationChange:cc $currentspeed")
                try {

                    if (currentspeed >= maxspeed) {
                        Log.d("TAG", "onLocationChange:00 $maxspeed")
                        maxspeed = currentspeed
                        Log.d("TAG", "onLocationChange:11 $maxspeed")
                        binding.txtSetmaximum.text = maxspeed.toString()
                    }
                }catch (e:NullPointerException)
                {

                }
            }
        })

    }
    private fun clicklistener(){
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.btnStart.setOnClickListener {
            if (!running) {
                running = true
                binding.txtSetduration.visibility = View.VISIBLE
                binding.btnStart.setBackgroundResource(R.drawable.round_with_black)
                binding.btnStop.setBackgroundResource(R.drawable.round_with_blue)

               getspeed()
                startTimer()
        }
            binding.btnStop.setOnClickListener {
                handler!!.removeCallbacksAndMessages(null);
                running = false
                binding.btnStop.setBackgroundResource(R.drawable.round_with_black)
                binding.btnStart.setBackgroundResource(R.drawable.round_with_blue)

                locationRepository.stopLocation()
                binding.txtSetduration.text = "00:00"
                binding.txtSetdistance.text = "00:00"
                binding.txtSetavg.text = "00:00"
                seconds=0
                count = 0
                totalSpeed = 0.0f
                distance = 0.0f

            }

    }
    }

    private fun startTimer() {
        handler = Handler()

        handler!!.post(object : Runnable {
            override fun run() {
                val hours: Int = seconds / 3600
                val minutes: Int = seconds % 3600 / 60
                val secs: Int = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time: String = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                Log.d("TAG", "run: --------------------$time--")
                timeString = time

                binding.txtSetduration.text = time

                // If running is true, increment the
                // seconds variable.
                if (running)
                {
                    seconds++
                } else {

                }
                // Post the code again
                // with a delay of 1 second.
                handler!!.postDelayed(this, 1000)

            }
        })
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double):Float{
        val locationA = Location("point A")
        locationA.latitude = lat1
        locationA.longitude = lon1
        val locationB = Location("point B")
        locationB.latitude = lat2
        locationB.longitude = lon2
        val distance = locationA.distanceTo(locationB)

        Log.d("TAG", "calculateDistance:     $distance")
        return distance
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}