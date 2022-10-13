package com.example.sspdim.model

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.databinding.ServerListItemBinding
import com.example.sspdim.network.Server

class ServerListAdapter(
    private val onClick: (Server) -> Unit
): ListAdapter<Server, ServerListAdapter.ServerViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Server>() {

        var selectedPosition: Int = -1

        override fun areItemsTheSame(oldItem: Server, newItem: Server): Boolean {
            return oldItem.ipAddress == newItem.ipAddress
        }

        override fun areContentsTheSame(oldItem: Server, newItem: Server): Boolean {
            return oldItem.ipAddress == newItem.ipAddress && oldItem.domainName == newItem.domainName
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ServerViewHolder(private var binding: ServerListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(server: Server, position: Int) {
            binding.server = server
            binding.serverNameRadiobutton.isChecked = (position == selectedPosition)
            binding.executePendingBindings()

            binding.serverNameRadiobutton.setOnClickListener {
                Log.d("ServerListAdapter", "$selectedPosition $position")
                val temp = selectedPosition
                selectedPosition = position
                notifyItemChanged(temp)
                notifyItemChanged(selectedPosition)
                onClick(server)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ServerViewHolder(
            ServerListItemBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        val server = getItem(position)
        holder.bind(server, position)
    }
}