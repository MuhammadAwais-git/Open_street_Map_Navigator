package com.example.navigatorapp.FavouritePlacesRoomDataBase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = DataRepository(app)
    private val allNotes = repository.getAllData()

    fun insert(data: DataModel) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(data)
        }
    }
    fun delete(data: DataModel) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.delete(data)
        }
    }

    fun getAllNotes(): LiveData<List<DataModel>> {
        return allNotes
    }


}
