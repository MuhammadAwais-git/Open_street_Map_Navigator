package com.example.navigatorapp.FavouritePlacesRoomDataBase

import android.app.Application
import androidx.lifecycle.LiveData


class DataRepository(application: Application) {

    private var dataDao: DataDAo
    private var allData: LiveData<List<DataModel>>

    private val database = DataBaseClass.getInstance(application)

    init {
        dataDao = database.Dao()
        allData = dataDao.getAll()
    }

    fun insert(data: DataModel) {
        dataDao.insert(data)

    }
    fun delete(data: DataModel) {
        dataDao.delete(data)

    }


    fun getAllData(): LiveData<List<DataModel>> {
        return allData
    }



}