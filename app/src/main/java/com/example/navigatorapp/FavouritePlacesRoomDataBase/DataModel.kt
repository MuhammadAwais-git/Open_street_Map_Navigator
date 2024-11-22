package com.example.navigatorapp.FavouritePlacesRoomDataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Data_Location")
data class DataModel(
    @PrimaryKey(autoGenerate = true) val id: Int? =null,
    @ColumnInfo(name = "placeNameId")
    val placeNameId: String,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "longLocation")
    val longLocation:Double?,
    @ColumnInfo(name = "placeNameAddress")
    val placeNameAddress: String

)
