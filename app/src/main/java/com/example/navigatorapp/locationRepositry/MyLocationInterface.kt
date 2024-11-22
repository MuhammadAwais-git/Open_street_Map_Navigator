package com.example.navigatorapp.locationRepositry

import android.location.Location

interface MyLocationInterface {
    fun onLocationChange(location: Location)
}