package com.example.navigatorapp.Dialouge

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.example.navigatorapp.RouteNavigation.poiInterface

import com.example.navigatorapp.databinding.PoiDialogBinding
import org.osmdroid.util.GeoPoint

class PoiDialog(mContext: Context,private val mlocation:GeoPoint?, val name:String? , val description:String?,val item: poiInterface): Dialog(mContext){

    private lateinit var binding: PoiDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = PoiDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtPlacename.text=name
        binding.txtPlaceaddress.text=description
        binding.dialogbtnnavigate.setOnClickListener {
            item.poinavigate(mlocation)
            dismiss()
        }

    }
}