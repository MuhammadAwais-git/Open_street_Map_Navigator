package com.example.navigatorapp.Dialouge

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.example.navigatorapp.FavouritePlacesRoomDataBase.DataModel
import com.example.navigatorapp.FavouritePlacesRoomDataBase.ItemClickInterface
import com.example.navigatorapp.databinding.FavouriteItemclickDialogBinding

class FavouriteItemClickDialog(mContext: Context,private val model: DataModel,val item: ItemClickInterface): Dialog(mContext){

        private lateinit var binding: FavouriteItemclickDialogBinding


        override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)
            setCancelable(true)
            window!!.requestFeature(Window.FEATURE_NO_TITLE)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding = FavouriteItemclickDialogBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.txtPlaceaddress.text = model.placeNameAddress
            binding.txtPlacename.text = model.placeNameId
            binding.dialogbtnnavigate.setOnClickListener {
                item.
                onNavigateClick(model)
                dismiss()
            }
            binding.dilogbtnshowonmap.setOnClickListener {
                item.itemclick(model)

                dismiss()

            }
        }
}