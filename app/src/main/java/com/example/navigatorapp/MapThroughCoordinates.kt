package com.example.navigatorapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.example.navigatorapp.databinding.ActivityMapThroughCoordinatesBinding
import com.example.navigatorapp.databinding.ActivityPoiRouteBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class MapThroughCoordinates : AppCompatActivity() {

    private lateinit var Latlong:String
    private var Long: Double = 0.0
    private lateinit var binding: ActivityMapThroughCoordinatesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapThroughCoordinatesBinding.inflate(layoutInflater)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(binding.root)

        //getlatlong from CoordinatesActivity
        Latlong = intent.getStringExtra("latlng").toString()
        Log.e("TAG", "onCreate: latlong $Latlong", )
        val splitstring=Latlong.split(",")
        val lat=splitstring[0].toDouble()
        val long=splitstring[1].toDouble()

        intializeMap(lat,long)
        Log.e("TAG", "onCreate:12367i $lat $long", )
    }

    private fun intializeMap(lat:Double,lng:Double){
        binding.mapthroughcoordinates.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.mapthroughcoordinates.setBuiltInZoomControls(true)
        binding.mapthroughcoordinates.setMultiTouchControls(true)
        val mRotationGestureOverlay = RotationGestureOverlay(this, binding.mapthroughcoordinates)
        mRotationGestureOverlay.isEnabled = true
        binding.mapthroughcoordinates.overlays.add(mRotationGestureOverlay)
        val mapController: IMapController = binding.mapthroughcoordinates.controller
        mapController.setZoom(10 )
        val startPoint = GeoPoint(lat, lng)
        mapController.setCenter(startPoint)
        //marker
        val marker = Marker(binding.mapthroughcoordinates)
        marker.title = ("Current Location")
        marker.textLabelFontSize = 11
        //must set the icon to null last
        marker.icon = null
        marker.position = startPoint
        val icon = BitmapFactory.decodeResource(resources, R.drawable.orangemarker)
        setMarkerIconAsPhoto(marker, icon!!)
        binding.mapthroughcoordinates.overlays.add(marker)

    }

    fun setMarkerIconAsPhoto(marker: Marker, thumbnail: Bitmap) {
        var thumbnail = thumbnail
        val borderSize = 2
        thumbnail = Bitmap.createScaledBitmap(thumbnail, 90, 90, true)
        val withBorder = Bitmap.createBitmap(
            thumbnail.width + borderSize * 2,
            thumbnail.height + borderSize * 2,
            thumbnail.config
        )
        val canvas = Canvas(withBorder)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawBitmap(thumbnail, borderSize.toFloat(), borderSize.toFloat(), null)
        val icon = BitmapDrawable(resources, withBorder)
        marker.icon = icon
    }
}