package com.example.Slider

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView


class SliderActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter
    lateinit var txtskip: TextView
    lateinit var sliderList: ArrayList<SliderData>
    lateinit var indicatorSlideOneTV: TextView
    lateinit var indicatorSlideTwoTV: TextView
    lateinit var indicatorSlideThreeTV: TextView
    lateinit var firstCardView: MaterialCardView
    lateinit var secondCardView: MaterialCardView
    lateinit var thirdCardView: MaterialCardView
    lateinit var btnstart: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider)

        viewPager = findViewById(R.id.idViewPager)
        indicatorSlideOneTV = findViewById(R.id.idTVSlideOne)
        indicatorSlideTwoTV = findViewById(R.id.idTVSlideTwo)
        indicatorSlideThreeTV = findViewById(R.id.idTVSlideThree)
        firstCardView = findViewById(R.id.fcarview)
        secondCardView = findViewById(R.id.secondcarview)
        thirdCardView = findViewById(R.id.thirdcarview)
        btnstart = findViewById(R.id.btn_sliderstart)
        txtskip = findViewById(R.id.txt_skip)

            val sp = getSharedPreferences("SharedPreferences", android.content.Context.MODE_PRIVATE)
            val edit: SharedPreferences.Editor = sp.edit()

        txtskip.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            edit.putBoolean("stopSlider", true)
            edit.apply()
            startActivity(intent)
            finish()

        }
        btnstart.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            edit.putBoolean("stopSlider", true)
            edit.apply()
            startActivity(intent)
            finish()

        }

        sliderList = ArrayList()


        sliderList.add(SliderData("Get The Fastest Route",  R.drawable.fitst_sliderimage))

        sliderList.add(SliderData("Choose Your Destination", R.drawable.second_sliderimage))

        sliderList.add(SliderData("Explore The Navigation",  R.drawable.third_sliderimage))

        sliderAdapter = SliderAdapter(this, sliderList)

        viewPager.adapter = sliderAdapter

        viewPager.addOnPageChangeListener(viewListener)

    }
    // creating a method for view pager for on page change listener.
    var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {

            if (position == 0) {
                firstCardView.setCardBackgroundColor(resources.getColor(R.color.blue))
                secondCardView.setCardBackgroundColor(resources.getColor(R.color.silver))
                thirdCardView.setCardBackgroundColor(resources.getColor(R.color.silver))
                btnstart.visibility=View.GONE
                txtskip.visibility=View.VISIBLE

            } else if (position == 1) {
                firstCardView.setCardBackgroundColor(resources.getColor(R.color.silver))
                secondCardView.setCardBackgroundColor(resources.getColor(R.color.blue))
                thirdCardView.setCardBackgroundColor(resources.getColor(R.color.silver))
                btnstart.visibility=View.GONE
                txtskip.visibility=View.VISIBLE

            } else {
                firstCardView.setCardBackgroundColor(resources.getColor(R.color.silver))
                secondCardView.setCardBackgroundColor(resources.getColor(R.color.silver))
                thirdCardView.setCardBackgroundColor(resources.getColor(R.color.blue))
                btnstart.visibility=View.VISIBLE
                txtskip.visibility=View.GONE
            }
        }

        // below method is use to check scroll state.
        override fun onPageScrollStateChanged(state: Int) {}
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}