package com.example.Slider


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.example.navigatorapp.R
import com.google.android.material.button.MaterialButton

class SliderAdapter(val context: Context, val sliderList: ArrayList<SliderData>) : PagerAdapter() {

    override fun getCount(): Int {
        return sliderList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.slider_item, container, false)

        val imageView: ImageView = view.findViewById(R.id.idIVSlider)
        val sliderHeadingTV: TextView = view.findViewById(R.id.txt_sliderTitle)


        val sliderData: SliderData = sliderList.get(position)


        sliderHeadingTV.text = sliderData.slideTitle

        imageView.setImageResource(sliderData.slideImage)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }


}