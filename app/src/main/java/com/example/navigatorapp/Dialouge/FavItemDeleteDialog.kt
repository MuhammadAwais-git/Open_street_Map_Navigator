package com.example.navigatorapp.Dialouge

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.example.navigatorapp.Favourite.FavouritePlacesActivity
import com.example.navigatorapp.FavouritePlacesRoomDataBase.DeleteItemInterface
import com.example.navigatorapp.FavouritePlacesRoomDataBase.InterfacePlacename
import com.example.navigatorapp.databinding.FavItemDeleteDialogBinding


class FavItemDeleteDialog (private val mContext: Context, private val deleteiteminterface: DeleteItemInterface): Dialog(mContext) {

    private lateinit var binding: FavItemDeleteDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setCancelable(true)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = FavItemDeleteDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDismiss.setOnClickListener {
            dismiss()
        }
        binding.btnsave.setOnClickListener {
            deleteiteminterface.deleteItem()
            dismiss()

        }
    }
}