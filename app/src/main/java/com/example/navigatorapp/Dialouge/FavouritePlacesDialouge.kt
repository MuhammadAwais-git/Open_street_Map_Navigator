package com.example.navigatorapp.Dialouge

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat.startActivity
import com.example.navigatorapp.Compass.CompassActivity
import com.example.navigatorapp.Favourite.FavouritePlacesActivity
import com.example.navigatorapp.FavouritePlacesRoomDataBase.InterfacePlacename
import com.example.navigatorapp.databinding.FavouritePlacesDialogBinding

class FavouritePlacesDialouge(private val mContext: Context, private val dialogInterface: InterfacePlacename): Dialog(mContext) {

    private lateinit var binding: FavouritePlacesDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setCancelable(true)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = FavouritePlacesDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDismiss.setOnClickListener {
            dismiss()
        }
        binding.btnsave.setOnClickListener {
            val placeName = binding.txtplacename.text.trim().toString()
            dialogInterface.getplacename(placeName)
            dismiss()
            mContext.startActivity(Intent(mContext, FavouritePlacesActivity::class.java))
        }
    }
}