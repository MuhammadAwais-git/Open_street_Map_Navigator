package com.example.navigatorapp.Weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.navigatorapp.Weather.weatherModel.DataWeatherModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataViewModel : ViewModel() {
    private var movieLiveData = MutableLiveData<DataWeatherModel>()
    private var appid="3626c1b87e7e6d9a7ceb6b30fadd28c2"

    fun getalldata(lat:Double,lng:Double) {
        Log.d("observeLiveData", "observeLiveData:latlng $lat $lng ")
        RetrofitInstance.api.getAllData(lat.toString(),lng.toString(),appid).enqueue(object  :
            Callback<DataWeatherModel> {
            override fun onResponse(call: Call<DataWeatherModel>, response: Response<DataWeatherModel>) {
                if (response.body()!=null){
                    Log.d("observeLiveData", "observeLiveData:postvalue ${response} ")
                    movieLiveData.postValue(response.body()!!)
//                    Log.d("observeLiveData", "observeLiveData:postvalue ${response.body()} ")
                }
                else{
                    return
                }
            }
            override fun onFailure(call: Call<DataWeatherModel>, t: Throwable) {
                Log.d("TAG","failed: "+t.message.toString())
            }
        })
    }
    fun observeLiveData() : LiveData<DataWeatherModel> {
        Log.d("observeLiveData", "observeLiveData: ")
        return movieLiveData
    }
}