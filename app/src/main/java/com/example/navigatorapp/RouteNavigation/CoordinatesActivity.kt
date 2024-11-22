package com.example.navigatorapp.RouteNavigation

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.navigatorapp.MapThroughCoordinates
import com.example.navigatorapp.Navigation.TestNavigation
import com.example.navigatorapp.R
import com.example.navigatorapp.databinding.ActivityCoordinatesBinding
import com.example.navigatorapp.databinding.ActivityMapThroughCoordinatesBinding
import org.osmdroid.config.Configuration
import java.lang.IndexOutOfBoundsException

class CoordinatesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoordinatesBinding
    private lateinit var latlng:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordinatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.constraintMapview.setOnClickListener {
            try {
                if (binding.txtCoordiateOnCoordinateActivity.text.isEmpty()) {
                    Toast.makeText(this, "Enter Coordinates", Toast.LENGTH_SHORT).show()
                } else {

                    latlng = binding.txtCoordiateOnCoordinateActivity.text.toString()
                    Log.d("TAG", "onCreate: textfrom $latlng")
                    val intent = Intent(this, MapThroughCoordinates::class.java)
                    intent.putExtra("latlng", latlng)
                    startActivity(intent)
                }
            }catch (e:IndexOutOfBoundsException){

            }
        }
        binding.constraintNaigationOnCoordinates.setOnClickListener {
            if (binding.txtCoordiateOnCoordinateActivity.text.isEmpty()) {
                Toast.makeText(this,"Enter Coordinates",Toast.LENGTH_SHORT).show()
            }else{
                latlng = binding.txtCoordiateOnCoordinateActivity.text.toString()
                Log.d("TAG", "onCreate: textfrom nav $latlng")
                val intent = Intent(this, TestNavigation::class.java)
                intent.putExtra("latlng", latlng)
                intent.putExtra("clat", POI_Route_Activity.cLat)
                intent.putExtra("clong", POI_Route_Activity.cLong)

                val splitstring = latlng.split(",")
                val dLat0 = splitstring[0].toDouble()
                val dLong0 = splitstring[1].toDouble()

                intent.putExtra("dlat", dLat0)
                intent.putExtra("dlong", dLong0)
                startActivity(intent)
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}