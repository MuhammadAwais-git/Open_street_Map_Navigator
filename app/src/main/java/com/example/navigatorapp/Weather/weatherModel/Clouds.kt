package com.example.navigatorapp.Weather.weatherModel


import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all" ) var all : Int? = null

)