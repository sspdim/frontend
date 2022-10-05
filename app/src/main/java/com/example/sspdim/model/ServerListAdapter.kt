package com.example.sspdim.model

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.databinding.ServerListItemBinding
import com.example.sspdim.network.Server
import kotlin.properties.Delegates

class ServerListAdapter: ListAdapter<Server, ServerListAdapter.ServerViewHolder>(DiffCallback) {

//    var selectedPosition by Delegates.observable(-1) { _, oldPos, newPos ->
//        notifyItemChanged(oldPos)
//        notifyItemChanged(newPos)
//    }

    companion object DiffCallback : DiffUtil.ItemCallback<Server>() {

        private var selectedPosition: Int = -1

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
            }
//            if (selectedPosition == -1 && position == 0) {
//                binding.serverNameRadiobutton.isChecked = true
//            }
//            else {
//                if (selectedPosition == position) {
//                    binding.serverNameRadiobutton.isChecked = true
//                }
//                else {
//                    binding.serverNameRadiobutton.isChecked = false
//                    selectedPosition = adapterPosition
//                    notifyDataSetChanged()
//                }
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerListAdapter.ServerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ServerListAdapter().ServerViewHolder(
            ServerListItemBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ServerListAdapter.ServerViewHolder, position: Int) {
        val server = getItem(position)
        holder.bind(server, position)
    }
}