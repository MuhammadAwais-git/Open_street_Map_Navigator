package com.example.navigatorapp.Favourite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigatorapp.Dialouge.FavItemDeleteDialog
import com.example.navigatorapp.Dialouge.FavouriteItemClickDialog
import com.example.navigatorapp.FavouritePlacesRoomDataBase.*
import com.example.navigatorapp.MainActivity
import com.example.navigatorapp.Navigation.TestNavigation
import com.example.navigatorapp.databinding.ActivityFavouritePlacesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouritePlacesActivity : AppCompatActivity() {
    lateinit var binding:ActivityFavouritePlacesBinding
    private lateinit var vm: DataViewModel
    private lateinit var adapter: DataAdapter
    private var list = ArrayList<DataModel>()
    private var position: Int = 0
    private var lat:Double=0.0
    private var long:Double=0.0
    private var placeName=""
    private var placeNameAddress=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFavouritePlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this)[DataViewModel::class.java]

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.btnAddfvrtonmap.setOnClickListener {
            startActivity(Intent(this, AddFavouriteActivity::class.java))
        }

            CoroutineScope(Dispatchers.Main).launch {
                getAllData()
            }

    }

        private suspend fun getAllData() {

            vm.getAllNotes().observe(this, Observer {
                list = it as ArrayList<DataModel>
                if(list.isEmpty()){
                    binding.txtChkdata.visibility=View.VISIBLE
                }else {
                    setUpRecyclerView(list)
                }
            })
        }
    private fun setUpRecyclerView(mList: ArrayList<DataModel>) {

        binding.recyclerFavrtPlaces.layoutManager = LinearLayoutManager(this)
        binding.recyclerFavrtPlaces.setHasFixedSize(true)

        adapter = DataAdapter(this, mList, object : DataAdapter.onItemClickListener {
            override fun ondeleteitemclick(position: Int) {
                val dialog=FavItemDeleteDialog(this@FavouritePlacesActivity,object :DeleteItemInterface{
                    override fun deleteItem() {
                        vm.delete(list[position])
                        list.removeAt(position)
                        adapter.notifyItemChanged(position)
                        Toast.makeText(
                            this@FavouritePlacesActivity,
                            "Removed Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
                dialog.show()

            }

            override fun onitemclick(model: DataModel) {
                this@FavouritePlacesActivity.position =position
                val dialogBox = FavouriteItemClickDialog(this@FavouritePlacesActivity, model,object :ItemClickInterface{
                    override fun itemclick(model: DataModel) {
                        val intent = Intent(this@FavouritePlacesActivity, AddFavouriteActivity::class.java)
                        intent.putExtra("lat", model.lat)
                        intent.putExtra("long", model.longLocation)
                        intent.putExtra("value", 0.56)
                        startActivity(intent)
                    }

                    override fun onNavigateClick(model: DataModel) {
                        val intent = Intent(this@FavouritePlacesActivity, TestNavigation::class.java)
                        intent.putExtra("clat", MainActivity.cLat)
                        intent.putExtra("clong", MainActivity.cLong)
                        intent.putExtra("dlat", model.lat)
                        intent.putExtra("dlong", model.longLocation)
                        intent.putExtra("value", 0.56)
                        startActivity(intent)
                    }

                })
                dialogBox.show()

            }
        })
        binding.recyclerFavrtPlaces.adapter = adapter

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}