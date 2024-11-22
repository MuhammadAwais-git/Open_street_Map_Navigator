package com.example.navigatorapp.RouteNavigation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
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
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Util
import com.example.navigatorapp.Dialouge.PoiDialog
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.Navigation.TestNavigation
import com.example.navigatorapp.R
import com.example.navigatorapp.databinding.ActivityPoiRouteBinding
import com.example.navigatorapp.locationRepositry.LocationRepository
import com.example.navigatorapp.locationRepositry.MyLocationInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.location.POI
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.IOException
import java.text.DecimalFormat
import java.util.*


class POI_Route_Activity : AppCompatActivity() {

    lateinit var binding: ActivityPoiRouteBinding

    private lateinit var locationRepository: LocationRepository
    private lateinit var finaladdres: String
    private var DLat: Double = 0.0
    private var DLong: Double = 0.0
    private var dLat0: Double = 0.0
    private var dLong0: Double = 0.0
    private var NewdistinationLat: Double = 0.0
    private var NewdistinationLong: Double = 0.0
    private var curentLocationAddress: String = ""
    private lateinit var poiMarker: Marker
    private var road: Road? = null
    private var result: Double = 0.0
    private var obtainTypedArray: TypedArray? = null
    private lateinit var startPoint1: GeoPoint
    private lateinit var endPoint: GeoPoint
    private var waypoints = ArrayList<GeoPoint>()
    private var poiList = ArrayList<POI>()
    private var destinationText: String = ""


    companion object {
        const val carRoute = OSRMRoadManager.MEAN_BY_CAR
        const val bikeRoute = OSRMRoadManager.MEAN_BY_BIKE
        const val footRoute = OSRMRoadManager.MEAN_BY_FOOT

        var cLat: Double = 0.0
        var cLong: Double = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPoiRouteBinding.inflate(layoutInflater)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(binding.root)


        binding.Cardcurrentlocation.setOnClickListener {
            Util.OsmHelper.zoomWithAnimate(
                binding.navigationMaponpoiroute.controller,
                startPoint1,
                18
            )
        }

        obtainTypedArray = resources.obtainTypedArray(R.array.direction_icons)

        //getlatlong from MainActivity
        dLat0 = intent.getDoubleExtra("dlat", DLat)
        dLong0 = intent.getDoubleExtra("dlong", DLong)
        destinationText = intent.getStringExtra("dtext").toString()
        binding.txtDestination.setText(destinationText, TextView.BufferType.EDITABLE)
        val cText: String? = intent.getStringExtra("ctext")

        BottombarClickListener()
        bygoClicklistener()
        getCurrentLocation()


        binding.btnNavigation.setOnClickListener {
            val intent = Intent(this@POI_Route_Activity, TestNavigation::class.java)
            intent.putExtra("clat", cLat)
            intent.putExtra("clong", cLong)
            intent.putExtra("dlat", dLat0)
            intent.putExtra("dlong", dLong0)
            startActivity(intent)
        }
        binding.txtDestination.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //unfocus keyboard
                val view = this.currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                getLocationFromAdress()
                return@OnEditorActionListener true
            }
            false
        })
        binding.txtCurrentlocation.setOnClickListener {
            getLocationFromAdress()
        }
    }

    private fun getCurrentLocation() {

        locationRepository = LocationRepository(this, object : MyLocationInterface {
            override fun onLocationChange(location: Location) {
                cLat = location.latitude
                cLong = location.longitude

                Log.d("loc", "onLocationChange: " + cLat)
                Log.d("loc", "onLocationChange: " + cLong)
                locationRepository.stopLocation()

                val geocoder = Geocoder(
                    this@POI_Route_Activity,
                    Locale.getDefault()
                )
                val addreses = geocoder.getFromLocation(
                    location.latitude, location.longitude,
                    1
                ).toString()

                val adders = addreses.split(":").toTypedArray()
                val add1 = adders[1]
                val addres2 = add1.split("]").toTypedArray()
                finaladdres = addres2[0]

                curentLocationAddress = finaladdres
                binding.txtCurrentlocation.setText(finaladdres)
                binding.txtCurrentlocation.isSelected = true

                initializeMap(cLat, cLong)
            }
        })
    }

    private fun initializeMap(lat: Double, lng: Double) {
        binding.navigationMaponpoiroute.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.navigationMaponpoiroute.setBuiltInZoomControls(true)
        binding.navigationMaponpoiroute.setMultiTouchControls(true)
        val mapController: IMapController = binding.navigationMaponpoiroute.controller
        mapController.setZoom(12)
        val startPoint = GeoPoint(lat, lng)
        mapController.setCenter(startPoint)
        binding.navigationMaponpoiroute.setBuiltInZoomControls(false)

        GlobalScope.launch(Dispatchers.IO) {
//        val SDK_INT = Build.VERSION.SDK_INT
//        if (SDK_INT > 8) {
//            val policy = ThreadPolicy.Builder()
//                .permitAll().build()
//            StrictMode.setThreadPolicy(policy)
            showroute(carRoute)
        }

    }

    private suspend fun showroute(type: String) {
        try {
            binding.navigationMaponpoiroute.overlays.clear()
            val mRotationGestureOverlay =
                RotationGestureOverlay(this, binding.navigationMaponpoiroute)
            mRotationGestureOverlay.isEnabled = true
            binding.navigationMaponpoiroute.overlays.add(mRotationGestureOverlay)
            val MY_USERAGENT = "com.beview.mygeoapp"
//        val roadManager: RoadManager = OSRMRoadManager(this, "MY_USER_AGENT")
            val roadManager: RoadManager = OSRMRoadManager(this, MY_USERAGENT)
            (roadManager as OSRMRoadManager).setMean(type)
            waypoints = ArrayList<GeoPoint>()
            startPoint1 = GeoPoint(cLat, cLong)
            waypoints.add(startPoint1)
            endPoint = GeoPoint(dLat0, dLong0)
            waypoints.add(endPoint)

            //marker
            val marker = Marker(binding.navigationMaponpoiroute)
            marker.title = ("Current Location")
            marker.textLabelFontSize = 11
            //must set the icon to null last
            marker.icon = null
            marker.position = startPoint1
            val icon = BitmapFactory.decodeResource(resources, R.drawable.orangemarker)
            setMarkerIconAsPhoto(marker, icon!!)
            binding.navigationMaponpoiroute.overlays.add(marker)

            //marker
            val markerend = Marker(binding.navigationMaponpoiroute)
            markerend.title = ("Current Location")
            marker.textLabelFontSize = 11
            markerend.icon = null
            markerend.position = endPoint
            val iconend = BitmapFactory.decodeResource(resources, R.drawable.orangemarker)
            setMarkerIconAsPhoto(markerend, iconend!!)
            binding.navigationMaponpoiroute.overlays.add(markerend)
            var road: Road? = null
            road = roadManager.getRoad(waypoints)


            CalculateDistnce(roadManager, waypoints)
            withContext(Dispatchers.Main)
            {
                val line: Polyline = RoadManager.buildRoadOverlay(road)
                line.color = Color.BLUE
                line.width = 16.0F
                line.outlinePaint.colorFilter
                binding.navigationMaponpoiroute.overlays.add(line)
                binding.navigationMaponpoiroute.invalidate()

            }

        }catch (E:NullPointerException){

        }
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

    private suspend fun CalculateDistnce(
        roadManager: RoadManager,
        waypoints: ArrayList<GeoPoint>
    ) {
        road = roadManager.getRoad(waypoints)
        if (road!!.mLength >= 100.0) {
            result = road!!.mLength
        } else if (road!!.mLength >= 1.0) {
            result = Math.round(road!!.mLength * 10) / 10.0
        } else {
            result = road!!.mLength * 1000
        }
        withContext(Dispatchers.Main) {
            val res = DecimalFormat("#.#").format(result)
            binding.txtDistance.text = res.toString()

            val duration = road!!.mDuration
            if(duration==0.0) {
                Toast.makeText(applicationContext, "No Route found", Toast.LENGTH_SHORT).show()
            }else{
                binding.txtTime.text = calculateTime(duration.toInt())
            }
        }
    }

    private fun calculateTime(n: Int): String {
        var timeString = ""
        var d = n
        val day = d / (24 * 3600)
        d %= (24 * 3600)
        val hour = d / 3600
        d %= 3600
        val minutes = d / 60
        d %= 60
        val seconds = n
        if (day == 0) {
            Log.d("TAG", "convertSecondDay  poi:-----546------------- $minutes")
            timeString = "$hour hrs $minutes minutes"

            if (hour == 0) {
                Log.d("TAG", "convertSecondDay poi1:-----549------------- $minutes")

                timeString = "$minutes minutes"
            }
        } else {
            Log.d("TAG", "convertSecondDay poi2:-----554------------- $minutes")

            timeString = "$day d $hour hrs $minutes minutes"
        }
        return timeString
    }

    private fun NearbyCurrentlocation() {
        binding.navigationMaponpoiroute.overlays.clear()
        GlobalScope.launch(Dispatchers.IO) {
            showroute(carRoute)
            binding.navigationMaponpoiroute.invalidate()
            val poiProvider = NominatimPOIProvider("OSMBonusPackTutoUserAgent")
            try {


                poiList = poiProvider.getPOICloseTo(startPoint1, "Fuel", 10, 0.1)
                val poiMarkers = FolderOverlay(applicationContext)
                binding.navigationMaponpoiroute.overlays.add(poiMarkers)
//            val poiIcon = resources.getDrawable(R.drawable.poi_marker)

                for (poi in poiList) {
                    addNearMarkersPlace(poi.mLocation, poi.mType, poi.mDescription)
                }
            }catch (e:NullPointerException){

            }
        }
    }

    private fun NearbyDestination() {
        binding.navigationMaponpoiroute.overlays.clear()

        GlobalScope.launch(Dispatchers.IO) {
            showroute(carRoute)
            binding.navigationMaponpoiroute.invalidate()
            val poiProvider = NominatimPOIProvider("OSMBonusPackTutoUserAgent")
            try {
            val pois = poiProvider.getPOICloseTo(endPoint, "Fuel", 10, 0.1)

            val poiMarkers = FolderOverlay(applicationContext)
            binding.navigationMaponpoiroute.overlays.add(poiMarkers)


                for (poi in pois) {
                    addNearMarkersPlace(poi.mLocation, poi.mType, poi.mDescription)
                }
            }catch (e:NullPointerException){

            }
        }
    }

    private fun addNearMarkersPlace(mLocation: GeoPoint?, mType: String?, mdescription: String?) {
        val mOtherMarker = Marker(binding.navigationMaponpoiroute)
        mOtherMarker.position = mLocation
        mOtherMarker.title = mType
        mOtherMarker.snippet = mdescription
        val icon = BitmapFactory.decodeResource(resources, R.drawable.p_o_i_marker)
        mOtherMarker.icon = null
        setMarkerIconAsPhoto(mOtherMarker, icon!!)
        binding.navigationMaponpoiroute.overlays.add(mOtherMarker)
        binding.navigationMaponpoiroute.invalidate()

        mOtherMarker.setOnMarkerClickListener(object : Marker.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {
                val dialog = PoiDialog(this@POI_Route_Activity, mLocation, mType, mdescription,
                    object : poiInterface {
                        override fun poinavigate(mLocation: GeoPoint?) {
                            val intent = Intent(this@POI_Route_Activity, TestNavigation::class.java)
                            intent.putExtra("clat", MainActivity.cLat)
                            intent.putExtra("clong", MainActivity.cLong)
                            intent.putExtra("dlat", mLocation!!.latitude)
                            intent.putExtra("dlong", mLocation.longitude)
                            startActivity(intent)
                        }
                    })
                dialog.show()
                return true
            }
        })
    }

    private fun BottombarClickListener() {

        binding.constraintPoi.setOnClickListener {
            binding.imgPoi.setColorFilter(Color.parseColor("#4B7AFC"))
            binding.txtPoi.setTextColor(Color.parseColor("#4B7AFC"))
            binding.imgCoordinatess.setColorFilter(Color.parseColor("#000000"))
            binding.txtCoordinatess.setTextColor(Color.parseColor("#000000"))
            binding.constraintCurrentnearby.visibility = View.VISIBLE
            binding.constraintDestinationnearby.visibility = View.VISIBLE
        }
        binding.constraintCoordinates.setOnClickListener {
            binding.imgCoordinatess.setColorFilter(Color.parseColor("#4B7AFC"))
            binding.txtCoordinatess.setTextColor(Color.parseColor("#4B7AFC"))
            startActivity(Intent(this, CoordinatesActivity::class.java))
        }
        binding.constraintCurrentnearby.setOnClickListener {
            binding.constraintCurrentnearby.setBackgroundResource(R.drawable.round_with_black)
            binding.constraintDestinationnearby.setBackgroundResource(R.drawable.round_with_blue)
            NearbyCurrentlocation()
            binding.navigationMaponpoiroute.controller.animateTo(startPoint1)
            binding.navigationMaponpoiroute.controller.setZoom(12)

        }
        binding.constraintDestinationnearby.setOnClickListener {
            binding.constraintDestinationnearby.setBackgroundResource(R.drawable.round_with_black)
            binding.constraintCurrentnearby.setBackgroundResource(R.drawable.round_with_blue)
            NearbyDestination()
            binding.navigationMaponpoiroute.controller.animateTo(endPoint)
            binding.navigationMaponpoiroute.controller.setZoom(11)
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun bygoClicklistener() {
        binding.constraintCar.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                showroute(carRoute)
            }
            binding.constraintCar.setBackgroundResource(R.drawable.round_with_blue)
            binding.txtCar.visibility = View.VISIBLE
            binding.imgCar.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.txtCar.setTextColor(Color.parseColor("#FFFFFF"))
            binding.txtBike.visibility = View.GONE
            binding.txtFoot.visibility = View.GONE
            binding.imgBike.setColorFilter(Color.parseColor("#000000"))
            binding.imgFoot.setColorFilter(Color.parseColor("#000000"))
            binding.constraintBike.setBackgroundResource(R.color.white)
            binding.constraintFoot.setBackgroundResource(R.color.white)


        }

        binding.constraintBike.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                showroute(bikeRoute)
            }
            binding.constraintBike.setBackgroundResource(R.drawable.round_with_blue)
            binding.txtBike.visibility = View.VISIBLE
            binding.imgBike.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.txtBike.setTextColor(Color.parseColor("#FFFFFF"))

            binding.txtCar.visibility = View.GONE
            binding.txtFoot.visibility = View.GONE

            binding.imgCar.setColorFilter(Color.parseColor("#000000"))
            binding.imgFoot.setColorFilter(Color.parseColor("#000000"))

            binding.constraintCar.setBackgroundResource(R.color.white)
            binding.constraintFoot.setBackgroundResource(R.color.white)

        }
        binding.constraintFoot.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {
                showroute(footRoute)
            }
            binding.constraintFoot.setBackgroundResource(R.drawable.round_with_blue)
            binding.txtFoot.visibility = View.VISIBLE
            binding.imgFoot.setColorFilter(Color.parseColor("#FFFFFF"))
            binding.txtFoot.setTextColor(Color.parseColor("#FFFFFF"))

            binding.txtBike.visibility = View.GONE
            binding.txtCar.visibility = View.GONE

            binding.imgBike.setColorFilter(Color.parseColor("#000000"))
            binding.imgCar.setColorFilter(Color.parseColor("#000000"))

            binding.constraintBike.setBackgroundResource(R.color.white)
            binding.constraintCar.setBackgroundResource(R.color.white)

        }

    }

    //getlatlngfromaddress
    private fun getLocationFromAdress() {
        binding.navigationMaponpoiroute.overlays.clear()
        val geocoder = Geocoder(this)
        val addressList: List<Address>
        try {
//            current
            if (!curentLocationAddress.equals(binding.txtCurrentlocation.text.toString())) {
                val addressListCurrent: List<Address> =
                    geocoder.getFromLocationName(binding.txtCurrentlocation.text.toString(), 1)
                cLat = addressListCurrent[0].latitude
                cLong = addressListCurrent[0].longitude

            }
            //destination
            if (!destinationText.equals(binding.txtDestination.text.toString())) {
                addressList =
                    geocoder.getFromLocationName(binding.txtDestination.text.toString(), 1)
                if (addressList.isEmpty()) {
                    Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show()
                } else {
                    dLat0 = addressList[0].latitude
                    dLong0 = addressList[0].longitude
                    Log.d("TAG", "gettext: lat${NewdistinationLat}")
                    Log.d("TAG", "gettext: long${NewdistinationLong}")
                }
            }
            GlobalScope.launch(Dispatchers.IO) {
                showroute(carRoute)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        binding.txtCoordinatess.setTextColor(Color.parseColor("#737373"))
        binding.imgCoordinatess.setColorFilter(Color.parseColor("#737373"))
    }
    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}


