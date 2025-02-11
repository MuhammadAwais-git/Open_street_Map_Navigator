package com.example.navigatorapp.Weather

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.Util
import com.example.navigatorapp.Weather.weatherModel.DataWeatherModel
import com.example.navigatorapp.Weather.weatherModel.WeatherList
import com.example.navigatorapp.databinding.WeatherRecItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    private var dataList = ArrayList<WeatherList>()
    fun setdataList(mList : List<WeatherList>){
        this.dataList = mList as ArrayList<WeatherList>
        notifyDataSetChanged()
    }
    class ViewHolder(val binding : WeatherRecItemBinding) : RecyclerView.ViewHolder(binding.root)  {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            WeatherRecItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = dataList[position]
        holder.binding.txtTime.text =Util.getWeatherDate(model.dt!!.toLong(),2)

        val iconcode=model.weather[0].icon
                Glide.with(holder.itemView)
            .load(Util.OsmHelper.getIcon(iconcode!!))
            .into(holder.binding.imgWeathericon)

        val temp= dataList[position].main?.temp.toString()
        val celsius =(temp.toDouble()-273.0).toInt()
        holder.binding.txtDegree.text= "$celsius°"
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
}