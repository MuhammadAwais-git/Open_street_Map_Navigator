package com.example.navigatorapp.Weather

import com.example.navigatorapp.Weather.weatherModel.DataWeatherModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("forecast?")
    fun getAllData(@Query("lat") lat : String,@Query("lon") lon : String,@Query("appid") appid : String) : Call<DataWeatherModel>
}