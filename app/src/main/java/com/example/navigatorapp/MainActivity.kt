package com.example.navigatorapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.example.Util
import com.example.navigatorapp.Dialouge.AdvancedToolDialog
import com.example.navigatorapp.EarthMap.EarthmapActivity
import com.example.navigatorapp.Favourite.FavouritePlacesActivity
import com.example.navigatorapp.RouteNavigation.POI_Route_Activity
import com.example.navigatorapp.StatlliteMap.StatellitemapActicity
import com.example.navigatorapp.databinding.ActivityMainBinding
import com.example.navigatorapp.locationRepositry.LocationRepository
import com.example.navigatorapp.locationRepositry.MobileGPSdialogBox
import com.example.navigatorapp.locationRepositry.MyLocationInterface
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationRepository: LocationRepository
    private lateinit var startPoint: GeoPoint

    private var Destinationlat: Double = 0.0
    private var Destinationlong: Double = 0.0
    var status = false

    companion object {
        var cLat: Double = 0.0
        var cLong: Double = 0.0
    }

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var finaladdres: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        location()
        DrawerListener()

        binding.Cardcurrentlocation.setOnClickListener {
            Util.OsmHelper.zoomWithAnimate(binding.mainmap.controller, startPoint, 18)
        }
        binding.txtMainsearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.txtMainsearch.text!!.isEmpty()) {
                    Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
                } else {
                    val view = this.currentFocus
                    if (view != null) {
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    gettext()

                    val intent = Intent(this@MainActivity, POI_Route_Activity::class.java)
                    intent.putExtra("dlat", Destinationlat)
                    intent.putExtra("dlong", Destinationlong)
                    val destxt = binding.txtMainsearch.text.toString()
                    intent.putExtra("dtext", destxt)

                    intent.putExtra("clat", cLat)
                    intent.putExtra("clong", cLong)
                    // val ctxt=binding.txtMainsearch.text.toString()
                    intent.putExtra("ctext", finaladdres)
                    startActivity(intent)
                }

                return@OnEditorActionListener true
            }
            false
        })

//        binding.txtMainsearch.setOnClickListener {
//            if (binding.txtMainsearch.text!!.isEmpty()) {
//                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
//            } else {
//                gettext()
//
//                val intent = Intent(this@MainActivity, POI_Route_Activity::class.java)
//                intent.putExtra("dlat", Destinationlat)
//                intent.putExtra("dlong", Destinationlong)
//                val destxt = binding.txtMainsearch.text.toString()
//                intent.putExtra("dtext", destxt)
//
//                intent.putExtra("clat", cLat)
//                intent.putExtra("clong", cLong)
//                // val ctxt=binding.txtMainsearch.text.toString()
//                intent.putExtra("ctext", finaladdres)
//                startActivity(intent)
//            }
//        }

    }

    private fun gettext() {

        val geocoder = Geocoder(this)
        val addressList: List<Address>

        try {

            addressList = geocoder.getFromLocationName(binding.txtMainsearch.text.toString(), 1)
            if (addressList.isEmpty()) {
                Toast.makeText(this, "Please Enter valid address", Toast.LENGTH_SHORT).show()
//                binding.txtDestination.text = "Latitude: $Lat | Longitude: $Long"
            } else {
                Destinationlat = addressList[0].latitude
                Destinationlong = addressList[0].longitude
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun DrawerListener() {
        binding.imageMainDrawer.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        binding.apply {
            toggle = ActionBarDrawerToggle(
                this@MainActivity, drawerLayout,
                R.string.open,
                R.string.close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

//            navView.findViewById<TextView>(R.id.txt_removeadds).setOnClickListener {
//                Toast.makeText(this@MainActivity, "Smjo Remove ho gye ", Toast.LENGTH_SHORT)
//                    .show()
//            }
            navView.findViewById<TextView>(R.id.txt_moreapps).setOnClickListener {
                moreOurApp(this@MainActivity)
            }
            navView.findViewById<TextView>(R.id.txt_rateus).setOnClickListener {
                rateUsApp(this@MainActivity)
            }
            navView.findViewById<TextView>(R.id.txt_privacypolicy).setOnClickListener {
                appPrivacyPolicy(this@MainActivity)
            }
            navView.findViewById<TextView>(R.id.txt_share).setOnClickListener {
                shareUsApp(this@MainActivity)
            }
            navView.findViewById<TextView>(R.id.txt_Feedback).setOnClickListener {
                feedBack("sonikstudio001@gmail.com")
            }
            navView.findViewById<ConstraintLayout>(R.id.constraint_earthmap).setOnClickListener {
                startActivity(Intent(this@MainActivity, EarthmapActivity::class.java))
            }
            navView.findViewById<ConstraintLayout>(R.id.constraint_statellitemap)
                .setOnClickListener {
                    startActivity(Intent(this@MainActivity, StatellitemapActicity::class.java))

                }
            navView.findViewById<ConstraintLayout>(R.id.constraint_favourite).setOnClickListener {
                startActivity(Intent(this@MainActivity, FavouritePlacesActivity::class.java))

            }
            navView.findViewById<ConstraintLayout>(R.id.constraint_Tools).setOnClickListener {
                val dialogBox = AdvancedToolDialog(this@MainActivity)
                dialogBox.show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeMap(lat: Double, lng: Double) {

        binding.mainmap.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.mainmap.setBuiltInZoomControls(true)
        binding.mainmap.setMultiTouchControls(true)
        val mRotationGestureOverlay = RotationGestureOverlay(this, binding.mainmap)
        mRotationGestureOverlay.isEnabled = true
        binding.mainmap.overlays.add(mRotationGestureOverlay)
        val mapController: IMapController = binding.mainmap.controller
        startPoint = GeoPoint(lat, lng)
        mapController.setCenter(startPoint)
        mapController.setZoom(10)
        //marker
        val marker = Marker(binding.mainmap)
        marker.title = ("Current Location")
        marker.textLabelFontSize = 11
        //must set the icon to null last
        marker.icon = null
        marker.position = startPoint
        val icon = BitmapFactory.decodeResource(resources, R.drawable.orangemarker)
        setMarkerIconAsPhoto(marker, icon!!)
        binding.mainmap.overlays.add(marker)

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

                val geocoder = Geocoder(
                    this@MainActivity,
                    Locale.getDefault()
                )

                try {
                    val addreses = geocoder.getFromLocation(
                        location.latitude, location.longitude,
                        1
                    ).toString()

                    val adders = addreses.split(":").toTypedArray()
                    val add1 = adders[1]
                    val addres2 = add1.split("]").toTypedArray()
                    finaladdres = addres2[0]

                    if (!status) {

                        initializeMap(cLat, cLong)
                        status = true
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "onLocationChange: ${e.message}")
                }
            }
        })

    }

    fun location() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Location Permission Needed")
                .setMessage("This app needs the Location permission, please accept to use location functionality")
                .setPositiveButton("OK") { _, _ ->
                    //Prompt the user once explanation has been shown
                    requestLocationPermission()
                }
                .create()
                .show()
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        } else {
//            getCurrentLocation()

            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getCurrentLocation()
            } else {
                val dialog = MobileGPSdialogBox(this)
                dialog.show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED) {

            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getCurrentLocation()
            } else {
                val dialog = MobileGPSdialogBox(this)
                dialog.show()
            }

        } else {
            // permission was not granted

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                location()
            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri =
                    Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }

        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            101
        )
    }

    fun rateUsApp(context: Context) {
        try {
            val intentRateApp = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(context.resources.getString(R.string.rate_app))
            )
            context.startActivity(intentRateApp)
        } catch (e: Exception) {
        }
    }

    fun shareUsApp(context: Context) {
        try {
            val shareAppIntent = Intent(Intent.ACTION_SEND)
            shareAppIntent.type = "text/plain"
            val shareSub = "Check out this application on play store!"
            val shareBody: String = context.resources.getString(R.string.share_app_link)
            shareAppIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            shareAppIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            context.startActivity(Intent.createChooser(shareAppIntent, "Share App using..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun moreOurApp(context: Context) {
        try {
            val intentRateApp = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(context.resources.getString(R.string.more_app))
            )
            context.startActivity(intentRateApp)
        } catch (e: Exception) {
        }
    }

    fun appPrivacyPolicy(context: Context) {
        try {
            val intentPrivacyApps =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(context.resources.getString(R.string.privacy_policy_link))
                )
            context.startActivity(intentPrivacyApps)
        } catch (e: Exception) {
        }
    }

    private fun feedBack(addresses: String) {
        val email = Intent(Intent.ACTION_SEND)
        email.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(addresses))
        //need this to prompts email client only
        //need this to prompts email client only
        email.type = "message/rfc822"
        startActivity(Intent.createChooser(email, ""))
    }

    override fun onResume() {
        super.onResume()
        getCurrentLocation()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}