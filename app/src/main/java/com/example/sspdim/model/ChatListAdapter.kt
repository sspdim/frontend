package com.example.sspdim.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.database.Friend
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_PENDING
import com.example.sspdim.databinding.ChatListItemBinding

class ChatListAdapter(
    private val onItemClicked: (Friend) -> Unit,
    private val onAccept: (Friend) -> Unit,
    private val onDecline: (Friend) -> Unit
): ListAdapter<Friend, ChatListAdapter.ChatViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Friend>() {

        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.username == newItem.username && oldItem.status == newItem.status
        }
    }

    inner class ChatViewHolder(private var binding: ChatListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            binding.chat = friend
            if (friend.status == FRIEND_REQUEST_PENDING) {
                binding.chatItemAcceptButton.visibility = View.VISIBLE
                binding.chatItemDeclineButton.visibility = View.VISIBLE
                binding.chatItemAcceptButton.setOnClickListener {
                    onAccept(friend)
                }
                binding.chatItemDeclineButton.setOnClickListener {
                    onDecline(friend)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChatViewHolder(
            ChatListItemBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val friend = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(friend)
        }
        holder.bind(friend)
    }
}