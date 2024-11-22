package com.example.navigatorapp.EarthMap


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.Util
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.R
import com.example.navigatorapp.databinding.ActivityEarthmapBinding
import com.example.navigatorapp.locationRepositry.LocationRepository
import com.example.navigatorapp.locationRepositry.MyLocationInterface
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.Marker
import java.io.IOException

class EarthmapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarthmapBinding
    private lateinit var locationRepository:LocationRepository
    private lateinit var startPoint: GeoPoint
    private lateinit var destinationPoint:GeoPoint
    private var cLat:Double=0.0
    private var cLong:Double=0.0
    private var dLat:Double=0.0
    private var dLong:Double=0.0
    var chk=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarthmapBinding.inflate(layoutInflater)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(binding.root)

        getCurrentLocation()
        clicklistener()

    }

    private fun initializeMap(lat: Double, lng: Double) {

        binding.Earthmap.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.Earthmap.setBuiltInZoomControls(true)
        binding.Earthmap.setMultiTouchControls(true)
        val mRotationGestureOverlay = RotationGestureOverlay(this, binding.Earthmap)
        mRotationGestureOverlay.isEnabled = true
        binding.Earthmap.overlays.add(mRotationGestureOverlay)
        val mapController: IMapController = binding.Earthmap.controller
        mapController.setZoom(6)
         startPoint = GeoPoint(lat, lng)
        mapController.setCenter(startPoint)

        // val mapshowpoint = GeoPoint(51.5072, 0.1276)
        //marker
        val marker = Marker(binding.Earthmap)
        marker.title = ("Current Location")
        marker.textLabelFontSize = 11
        //must set the icon to null last
        marker.icon = null
        marker.position = startPoint
        val icon = BitmapFactory.decodeResource(resources, R.drawable.orangemarker)
        setMarkerIconAsPhoto(marker, icon!!)
        binding.Earthmap.overlays.add(marker)
        binding.Earthmap.invalidate()
    }
    private fun initialzemapDestination(lat: Double, lng: Double) {
//        BingMapTileSource.setBingKey("AoexEyqVex1qAdw1WdPm-gAot8bO_-Tf6B-5ZfH5jWGaOc7Q_0GSgy6ZilW2HPWn")
//        val bing = BingMapTileSource(null)
//        bing.style = BingMapTileSource.IMAGERYSET_AERIALWITHLABELS
//        bing.initMetaData()
//        binding.Earthmap.setTileSource(bing)

        binding.Earthmap.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.Earthmap.setBuiltInZoomControls(true)
        binding.Earthmap.setMultiTouchControls(true)
//        val mRotationGestureOverlay = RotationGestureOverlay(this, binding.Earthmap)
//        mRotationGestureOverlay.isEnabled = true
//        binding.Earthmap.overlays.add(mRotationGestureOverlay)
        binding.Earthmap.setBuiltInZoomControls(true)
        val mapController: IMapController = binding.Earthmap.controller
        mapController.setZoom(19)
        destinationPoint = GeoPoint(lat, lng)
        binding.Earthmap.controller.animateTo(destinationPoint)

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

    private fun getCurrentLocation() {

        locationRepository = LocationRepository(this, object : MyLocationInterface {
            override fun onLocationChange(location: Location) {
                cLat = location.latitude
                cLong = location.longitude

                Log.d("loc", "onLocationChange: " + cLat)
                Log.d("loc", "onLocationChange: " + cLong)
                locationRepository.stopLocation()

                initializeMap(cLat,cLong)

            }
        })
    }
    private fun clicklistener()
    {

        binding.Cardcurrentlocation.setOnClickListener {
            Util.OsmHelper.zoomWithAnimate(binding.Earthmap.controller, startPoint, 18)

        }
        binding.imgsearch.setOnClickListener {
            if (binding.txtSearch.text.isEmpty()) {
                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
            } else {
                gettext()
            }
        }

        binding.cardoftypes.setOnClickListener {
            if(!chk) {
                binding.typesimages.setImageResource(R.drawable.cross)
                binding.constrainttypes.visibility = View.VISIBLE
                chk = true
            }else{
                binding.typesimages.setImageResource(R.drawable.map_styles)
                binding.constrainttypes.visibility = View.GONE
                chk=false
            }
        }


        binding.cardopentopo.setOnClickListener {
            binding.cardopentopo.strokeColor = Color.parseColor("#2D2F30")
            binding.cardmapnik.strokeColor = Color.parseColor("#4D78EC")
            binding.Earthmap.setTileSource(TileSourceFactory.OpenTopo)
            binding.Earthmap.controller.setZoom(14.0)

        }
        binding.cardmapnik.setOnClickListener {
            binding.cardmapnik.strokeColor = Color.parseColor("#2D2F30")
            binding.cardopentopo.strokeColor = Color.parseColor("#4D78EC")
            binding.Earthmap.setTileSource(TileSourceFactory.MAPNIK)
            binding.Earthmap.controller.setZoom(14.0)

        }
//        binding.cardstatllite.setOnClickListener {
//            binding.Earthmap.setTileSource(TileSourceFactory.USGS_SAT)
//            binding.Earthmap.controller.setZoom(14.0)
//
//        }

        binding.btnBack.setOnClickListener {
         startActivity(Intent(this,MainActivity::class.java))

        }

    }

    private fun gettext() {

        val geocoder = Geocoder(this)
        val addressList: List<Address>

        try {

            addressList = geocoder.getFromLocationName(binding.txtSearch.text.toString(), 1)
            if (addressList.isEmpty() )
            {
                Toast.makeText(this,"Empty DATA", Toast.LENGTH_SHORT).show()
//                binding.txtDestination.text = "Latitude: $Lat | Longitude: $Long"
            }
            else{
                dLat = addressList[0].latitude
                dLong = addressList[0].longitude
                initialzemapDestination(dLat,dLong)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
