package com.example.navigatorapp.FavouritePlacesRoomDataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface DataDAo {
    @Query("SELECT * FROM Data_Location")
    fun getAll(): LiveData<List<DataModel>>

    @Insert
    fun insert(PName: DataModel)

    @Delete
    fun delete(PName: DataModel)

}