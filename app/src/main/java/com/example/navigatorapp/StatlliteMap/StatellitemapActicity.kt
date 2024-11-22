package com.example.navigatorapp.StatlliteMap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Util
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.R
import com.example.navigatorapp.databinding.ActivityStatellitemapActicityBinding
import com.example.navigatorapp.locationRepositry.GetLatLongfromAddress
import com.example.navigatorapp.locationRepositry.LocationRepository
import com.example.navigatorapp.locationRepositry.MyLocationInterface
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class StatellitemapActicity : AppCompatActivity() {

    private lateinit var binding: ActivityStatellitemapActicityBinding
    private lateinit var startPoint: GeoPoint
    private lateinit var destinationPoint: GeoPoint
    private lateinit var locationRepository: LocationRepository
    private lateinit var locationfromaddress: GetLatLongfromAddress
    private var cLat: Double = 0.0
    private var cLong: Double = 0.0
    private var dLat: Double = 0.0
    private var dLong: Double = 0.0
    private var chk = false
    var MAPBOXSATELLITELABELLED: OnlineTileSourceBase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatellitemapActicityBinding.inflate(layoutInflater)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(binding.root)




        getCurrentLocation()
        clicklistener()
    }

    private fun initialzemapDestination(lat: Double, lng: Double) {
        BingMapTileSource.setBingKey(Util.OsmHelper.bingMapApiKey)
        val bing = BingMapTileSource(null)
        bing.style = BingMapTileSource.IMAGERYSET_AERIALWITHLABELS
        bing.initMetaData()
        binding.statellitemap.setTileSource(bing)

        binding.statellitemap.setBuiltInZoomControls(true)
        binding.statellitemap.setMultiTouchControls(true)
//        val mRotationGestureOverlay = RotationGestureOverlay(this, binding.statellitemap)
//        mRotationGestureOverlay.isEnabled = true
//        binding.statellitemap.overlays.add(mRotationGestureOverlay)
        val mapController: IMapController = binding.statellitemap.controller
        mapController.setZoom(19)
        destinationPoint = GeoPoint(lat, lng)
        binding.statellitemap.controller.animateTo(destinationPoint)

    }

    private fun initializeMap(lat: Double, lng: Double) {

        BingMapTileSource.setBingKey(Util.OsmHelper.bingMapApiKey)
        val bing = BingMapTileSource(null)
        bing.style = BingMapTileSource.IMAGERYSET_AERIALWITHLABELS
        bing.initMetaData()
        binding.statellitemap.setTileSource(bing)

//        binding.statellitemap.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.statellitemap.setBuiltInZoomControls(true)
        binding.statellitemap.setMultiTouchControls(true)
        val mRotationGestureOverlay = RotationGestureOverlay(this, binding.statellitemap)
        mRotationGestureOverlay.isEnabled = true
        binding.statellitemap.overlays.add(mRotationGestureOverlay)
        val mapController: IMapController = binding.statellitemap.controller
        mapController.setZoom(6)
        startPoint = GeoPoint(lat, lng)

//      val mapshowpoint = GeoPoint(51.5072, 0.1276)
        mapController.setCenter(startPoint)

        //marker
        val marker = Marker(binding.statellitemap)
        marker.title = ("Current Location")
        marker.textLabelFontSize = 11
        //must set the icon to null last
        marker.icon = null
        marker.position = startPoint
        val icon = BitmapFactory.decodeResource(resources, R.drawable.orangemarker)
        setMarkerIconAsPhoto(marker, icon!!)
        binding.statellitemap.overlays.add(marker)
        binding.statellitemap.invalidate()
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

    private fun clicklistener() {
        binding.imgsearch.setOnClickListener {
            if (binding.txtSearch.text.isEmpty()) {
                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
            } else {
                gettext()
            }
        }

        binding.Cardcurrentlocation.setOnClickListener {
            Util.OsmHelper.zoomWithAnimate(binding.statellitemap.controller, startPoint, 18)

        }

        binding.cardoftypes.setOnClickListener {
            if (!chk) {
                binding.typesimages.setImageResource(R.drawable.cross)
                binding.constrainttypes.visibility = View.VISIBLE
                chk = true
            } else {
                binding.typesimages.setImageResource(R.drawable.map_styles)
                binding.constrainttypes.visibility = View.GONE
                chk = false
            }
        }
        binding.cardmapbox.setOnClickListener {
            binding.cardmapbox.strokeColor = Color.parseColor("#2D2F30")
            binding.cardbeingaerial.strokeColor = Color.parseColor("#4D78EC")
            mapBoxLayer()
        }
        binding.cardbeingaerial.setOnClickListener {
            binding.cardbeingaerial.strokeColor = Color.parseColor("#2D2F30")
            binding.cardmapbox.strokeColor = Color.parseColor("#4D78EC")
            BingMapTileSource.setBingKey(Util.OsmHelper.bingMapApiKey)
            val bing = BingMapTileSource(null)
            bing.style = BingMapTileSource.IMAGERYSET_AERIAL
            bing.initMetaData()
            binding.statellitemap.setTileSource(bing)
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun mapBoxLayer() {

        val mapBoxTileSourceFixed = MapBoxTileSourceFixed("MapBoxSatelliteLabelled", 1, 19, 256)
        this.MAPBOXSATELLITELABELLED = mapBoxTileSourceFixed
        mapBoxTileSourceFixed.retrieveAccessToken(this)
        (MAPBOXSATELLITELABELLED as MapBoxTileSource?)!!.retrieveMapBoxMapId(this)
        binding.statellitemap.setTileSource(this.MAPBOXSATELLITELABELLED)
    }

    class MapBoxTileSourceFixed(str: String?, i: Int, i2: Int, i3: Int) :
        MapBoxTileSource(str, i, i2, i3, "") {
        override fun getTileURLString(j: Long): String {

            //origional token 1
//            val Access_token =
//              "pk.eyJ1IjoiYXBwbGU1NSIsImEiOiJjbGJ4azU1bzQwM3BiM3BtbjB6MnkwcWM1In0.gx3dmo5WXLSnGWlbh5zZEw"
            Util.OsmHelper.mapbox_token = Util.OsmHelper.getRandomKeyMapBox()
            Log.d(
                "MapBoxLinkTAG",
                "getTileURLString: " + "https://api.mapbox.com/styles/v1/mapbox/" + "satellite-streets-v11" + "/tiles/" + MapTileIndex.getZoom(
                    j
                ) + "/" + MapTileIndex.getX(j) + "/" + MapTileIndex.getY(j) + "?access_token=" + Util.OsmHelper.mapbox_token
            )
            return "https://api.mapbox.com/styles/v1/mapbox/" + "satellite-streets-v11" + "/tiles/" + MapTileIndex.getZoom(
                j
            ) + "/" + MapTileIndex.getX(j) + "/" + MapTileIndex.getY(j) + "?access_token=" + Util.OsmHelper.mapbox_token
        }
    }

    private fun gettext() {
        locationfromaddress =
            GetLatLongfromAddress(this, binding.txtSearch.text.toString(), object :
                GetLatLongfromAddress.GeoTaskCallback {
                override fun onSuccessLocationFetched(fetchedLatLngs: GeoPoint?) {
                    dLat = fetchedLatLngs!!.latitude
                    dLong = fetchedLatLngs.longitude
                    Log.e("TAG", "onSuccessLocationFetched: dlat ${dLat} dlong ${dLong}")
                    initialzemapDestination(dLat, dLong)
                }

                override fun onFailedLocationFetched() {
                    Log.e("TAG", "onFailedLocationFetched: Failedfrtch lat long")
                }

            })

        locationfromaddress.execute()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}