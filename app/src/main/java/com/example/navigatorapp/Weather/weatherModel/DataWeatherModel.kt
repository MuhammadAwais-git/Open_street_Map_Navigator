package com.example.navigatorapp.Weather.weatherModel


import com.google.gson.annotations.SerializedName

data class DataWeatherModel(
    @SerializedName("cod"     ) var cod     : String?         = null,
    @SerializedName("message" ) var message : Int?            = null,
    @SerializedName("cnt"     ) var cnt     : Int?            = null,
    @SerializedName("list"    ) var list    : ArrayList<WeatherList> = ArrayList(),
    @SerializedName("city"    ) var city    : City?           = City()

)