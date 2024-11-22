package com.example.navigatorapp.FavouritePlacesRoomDataBase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigatorapp.R
import com.google.android.material.card.MaterialCardView

class DataAdapter(
    val context: Context,
    private val list: ArrayList<DataModel>,
    private var mlistener: onItemClickListener
) :
    RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recyler_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DataAdapter.ViewHolder, position: Int) {
        val data = list[position]
        holder.placename.text = data.placeNameId
        holder.delte.setOnClickListener {
            mlistener.ondeleteitemclick(position)
        }
        holder.placename.setOnClickListener {
            mlistener.onitemclick(data)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var placename: TextView = view.findViewById(R.id.txt_placename)
        var delte: MaterialCardView = view.findViewById(R.id.imgdelete)
    }

    interface onItemClickListener {

        fun ondeleteitemclick(position: Int)
        fun onitemclick(model: DataModel)
    }
}