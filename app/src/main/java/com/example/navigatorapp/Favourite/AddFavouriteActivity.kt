package com.example.navigatorapp.Favourite

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.Util
import com.example.navigatorapp.Dialouge.FavouritePlacesDialouge
import com.example.navigatorapp.FavouritePlacesRoomDataBase.DataModel
import com.example.navigatorapp.FavouritePlacesRoomDataBase.DataViewModel
import com.example.navigatorapp.FavouritePlacesRoomDataBase.InterfacePlacename
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.R
import com.example.navigatorapp.databinding.ActivityAddFavouriteBinding
import com.example.navigatorapp.locationRepositry.LocationRepository
import com.example.navigatorapp.locationRepositry.MyLocationInterface
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.IOException
import java.util.*

class AddFavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFavouriteBinding
    lateinit var locationRepository: LocationRepository
    private var cLat: Double = 0.0
    private var cLong: Double = 0.0
    private var dLat: Double = 0.0
    private var dLong: Double = 0.0
    private var value: Double = 0.0
    private var numvalue: Double = 0.0
    private var markerlatitude: Double = 0.0
    private var markerlongitude: Double = 0.0
    private var finaladdres = ""
    private var latfromfavplaces: Double = 0.0
    private var longfromfavplaces: Double = 0.0
    lateinit var startPoint: GeoPoint
    private lateinit var destinationPoint: GeoPoint
    private lateinit var item_point: GeoPoint
    var marker: Marker? = null
    private lateinit var vm: DataViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        binding = ActivityAddFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mRotationGestureOverlay = RotationGestureOverlay(this, binding.MapFavrtPlaces)
        mRotationGestureOverlay.isEnabled = true
        binding.MapFavrtPlaces.overlays.add(mRotationGestureOverlay)

        getCurrentLocation()
        clicklistener()
        marker = Marker(binding.MapFavrtPlaces)
        // Adding to DATABASE
        vm = ViewModelProvider(this)[DataViewModel::class.java]
        binding.btnsaveplaces.setOnClickListener {
            val dialog = FavouritePlacesDialouge(this, object : InterfacePlacename {
                override fun getplacename(placename: String) {
                    vm.insert(
                        DataModel(
                            null,
                            placename,
                            markerlatitude,
                            markerlongitude,
                            finaladdres
                        )
                    )
                }

            })
            dialog.show()
        }

        latfromfavplaces = intent.getDoubleExtra("lat", latfromfavplaces)
        longfromfavplaces = intent.getDoubleExtra("long", longfromfavplaces)
        numvalue = intent.getDoubleExtra("value", value)

        if (latfromfavplaces == 0.0 && longfromfavplaces == 0.0) {
            initializeMap(33.5651, 73.0169)
        } else {
            itemshowmap(latfromfavplaces, longfromfavplaces)
            binding.btnsaveplaces.visibility = View.GONE

        }
    }

    private fun initializeMap(lat: Double, lng: Double) {
        binding.MapFavrtPlaces.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean {
                markerlatitude = point!!.latitude
                markerlongitude = point.longitude
                marker!!.title = ("Favourite place")
                marker!!.textLabelFontSize = 11
                marker!!.icon = null
                marker!!.position = point
                val icon = BitmapFactory.decodeResource(resources, R.drawable.p_o_i_marker)
                setMarkerIconAsPhoto(marker!!, icon!!)
                binding.MapFavrtPlaces.overlays.add(marker)
                binding.MapFavrtPlaces.invalidate()
                val geocoder = Geocoder(
                    this@AddFavouriteActivity,
                    Locale.getDefault()
                )

                val addreses = geocoder.getFromLocation(
                    point.latitude, point.longitude,
                    1
                ).toString()
                val adders = addreses.split(":").toTypedArray()
                val add1 = adders[1]
                val addres2 = add1.split("]").toTypedArray()
                finaladdres = addres2[0]

                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                Toast.makeText(
                    this@AddFavouriteActivity,
                    "press Tap to add Favourite place",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
        }))

        binding.MapFavrtPlaces.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.MapFavrtPlaces.setMultiTouchControls(true)
        binding.MapFavrtPlaces.setBuiltInZoomControls(false)
        val mapController: IMapController = binding.MapFavrtPlaces.controller
        mapController.setZoom(17)
        startPoint = GeoPoint(lat, lng)
        mapController.setCenter(startPoint)
    }

    private fun initialzemapDestination(dLat: Double, dLong: Double) {

        binding.MapFavrtPlaces.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean {
                markerlatitude = point!!.latitude
                markerlongitude = point.longitude

                val marker = Marker(binding.MapFavrtPlaces)
                marker.title = ("Favourite place")
                marker.textLabelFontSize = 11
                //must set the icon to null last
                marker.icon = null
                marker.position = point
                val icon = BitmapFactory.decodeResource(resources, R.drawable.p_o_i_marker)
                setMarkerIconAsPhoto(marker, icon!!)
                binding.MapFavrtPlaces.overlays.add(marker)

                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                Toast.makeText(
                    this@AddFavouriteActivity,
                    "press Tap to add Favourite place",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
        }))
        binding.MapFavrtPlaces.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.MapFavrtPlaces.setBuiltInZoomControls(true)
        val mapController: IMapController = binding.MapFavrtPlaces.controller
        mapController.setZoom(18)
        destinationPoint = GeoPoint(dLat, dLong)
        binding.MapFavrtPlaces.controller.animateTo(destinationPoint)

    }

    private fun itemshowmap(lat: Double, Long: Double) {
        binding.MapFavrtPlaces.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        val mapController: IMapController = binding.MapFavrtPlaces.controller
        mapController.setZoom(18)
        item_point = GeoPoint(lat, Long)
        binding.MapFavrtPlaces.controller.animateTo(item_point)
        //marker
        val marker = Marker(binding.MapFavrtPlaces)
        marker.title = ("Favourite place")
        marker.textLabelFontSize = 11
        marker.icon = null
        marker.position = item_point
        var icon = BitmapFactory.decodeResource(resources, R.drawable.p_o_i_marker)
        setMarkerIconAsPhoto(marker, icon!!)
        binding.MapFavrtPlaces.overlays.add(marker)
        binding.MapFavrtPlaces.invalidate()

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
                locationRepository.stopLocation()
                initializeMap(cLat, cLong)

            }
        })
    }

    private fun gettext() {
        val geocoder = Geocoder(this)
        val addressList: List<Address>
        try {
            addressList = geocoder.getFromLocationName(binding.txtSearch.text.toString(), 1)
            if (addressList.isEmpty()) {
                Toast.makeText(this, "Please Enter Valid address", Toast.LENGTH_SHORT).show()
            } else {
                dLat = addressList[0].latitude
                dLong = addressList[0].longitude
                initialzemapDestination(dLat, dLong)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun clicklistener() {

        binding.Cardcurrentlocation.setOnClickListener {
            Util.OsmHelper.zoomWithAnimate(binding.MapFavrtPlaces.controller, startPoint, 17)

        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.imgsearch.setOnClickListener {
            if (binding.txtSearch.text.isEmpty()) {
                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
            } else {
                gettext()
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}