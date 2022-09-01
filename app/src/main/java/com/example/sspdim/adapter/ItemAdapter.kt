package com.example.sspdim.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.R
import com.example.sspdim.data.DataSource

class ItemAdapter(
    private val context: Context,
    private val datasource: DataSource
    ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

        class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            val textview: TextView =view.findViewById(R.id.item_content)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = datasource.getMessages()[position]
        holder.textview.text = item.message
    }

    override fun getItemCount(): Int {
        return datasource.getMessages().size
    }
}