package com.example.navigatorapp.FavouritePlacesRoomDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [DataModel::class], version = 3)
abstract class DataBaseClass : RoomDatabase() {

    abstract fun Dao(): DataDAo


    companion object {

        private var instance: DataBaseClass? = null

        @Synchronized
        fun getInstance(ctx: Context): DataBaseClass {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    ctx.applicationContext, DataBaseClass::class.java,
                    "Data_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }
            return instance!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        }

        private fun populateDatabase(db: DataBaseClass) {
            val noteDao = db.Dao()

        }
    }
}


//
//    @Database(entities = [HistoryModel::class,MyFavouriteModel::class,HomeWorkModel::class], version = 3, exportSchema = false)
//    abstract class AppDatabase : RoomDatabase() {

//        abstract fun historyDao(): HistoryDao

//    companion object {
//        @Volatile
//        var instance: DataBaseClass? = null
//        const val NAME_ROOM = "SpeedClock.db"
//
//        private val LOCK = Any()
//
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
//            instance ?: buildDatabase(context).also {
//                instance = it
//            }
//        }
//
//        private fun buildDatabase(context: Context) = Room.databaseBuilder(
//            context.applicationContext,
//            DataBaseClass::class.java,
//            NAME_ROOM
//
//        ).fallbackToDestructiveMigration()
//            .build()
//    }
//}


