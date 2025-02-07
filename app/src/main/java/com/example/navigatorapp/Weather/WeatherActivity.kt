package com.example.navigatorapp.Weather

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.Util
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.databinding.ActivityWeatherBinding
import com.example.navigatorapp.locationRepositry.LocationRepository
import com.example.navigatorapp.locationRepositry.MyLocationInterface
import java.io.IOException


class WeatherActivity : AppCompatActivity() {
    lateinit var binding:ActivityWeatherBinding
    private lateinit var viewModel: DataViewModel
    private lateinit var movieAdapter : WeatherAdapter
    lateinit var locationRepository:LocationRepository
    private var lat:Double=0.0
    private var long:Double=0.0
    private var dLat:Double=0.0
    private var dLong:Double=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.imgsearch.setOnClickListener {
            if (binding.txtSearch.text.isEmpty()) {

                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
            } else {
                val view = this.currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                gettext()
            }
        }

        getCurrentLocation()
        binding.progressbar.visibility=View.VISIBLE
        prepareRecyclerView()
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        viewModel.observeLiveData().observe(this, Observer { mList ->
            if (mList.list.isNotEmpty()) {
                movieAdapter.setdataList(mList.list)
                binding.progressbar.visibility=View.INVISIBLE
                val city=mList.city?.name
                binding.txtCountry.text= city
                val temp= mList.list[0].main?.temp
                val celsius =(temp!!-273.0).toInt()
                binding.txtDegree.text= "$celsius°"
                binding.txtStatus.text=mList.list[0].weather[0].description
//                Glide.with(this)
//                    .load("https://openweathermap.org/img/wn/"+mList.list[0].weather[0].icon+"@2x.png")
//                    .into(binding.imgWeathericon)

               val iconcode=mList.list[0].weather[0].icon
                Glide.with(this)
                    .load(Util.OsmHelper.getIcon(iconcode!!))
                    .into(binding.imgWeathericon)
                binding.txtDate.text=Util.getWeatherDate(mList.list[0].dt!!.toLong(),1)
                binding.txtSethumidity.text=mList.list[0].main?.humidity.toString()+"%"
                binding.txtSetwind.text=mList.list[0].wind?.speed.toString()+"Km/h"
                binding.txtSetfeelike.text= (mList.list[0].main?.feelsLike!!- 273.15).toInt().toString()+"%"
                binding.txtSetpressure.text=(mList.list[0].main?.pressure!!*0.1).toInt().toString()+"%"
            }
        })
    }

    private fun prepareRecyclerView() {
        movieAdapter = WeatherAdapter()
        binding.weatherRecycler.apply {
            layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL,false)
            adapter = movieAdapter

        }
    }
    private fun getCurrentLocation() {
        locationRepository = LocationRepository(this, object : MyLocationInterface {
            override fun onLocationChange(location: Location) {
                lat = location.latitude
                long = location.longitude
                locationRepository.stopLocation()
                viewModel.getalldata(lat,long)
            }
        })

    }
    private fun gettext() {
        val geocoder = Geocoder(this)
        val addressList: List<Address>
        try {
            addressList = geocoder.getFromLocationName(binding.txtSearch.text.toString(), 1)
            if (addressList.isEmpty() )
            {
                Toast.makeText(this,"No data found", Toast.LENGTH_SHORT).show()
            }
            else{
                dLat = addressList[0].latitude
                dLong = addressList[0].longitude
                Log.d("TAG", "gettext: $dLat $dLong")
                viewModel.getalldata(dLat,dLong)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}