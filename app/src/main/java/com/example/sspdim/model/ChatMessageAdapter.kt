package com.example.sspdim.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.R
import com.example.sspdim.database.ChatMessage

class ChatMessageAdapter: ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {

        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.friendUsername == newItem.friendUsername && oldItem.messageId == newItem.messageId
                    && oldItem.sender == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return  oldItem.messageContent == newItem.messageContent
        }
    }

    abstract class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: ChatMessage)
    }

    class MyChatMessageViewHolder(val view: View) : ChatMessageViewHolder(view) {
        private val messageContent = view.findViewById<TextView>(R.id.message)

        override fun bind(item: ChatMessage) {
            messageContent.text = item.messageContent
        }
    }
    class FriendChatMessageViewHolder(val view: View) : ChatMessageViewHolder(view) {
        private val messageContent = view.findViewById<TextView>(R.id.message)

        override fun bind(item: ChatMessage) {
            messageContent.text = item.messageContent
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val context = parent.context
        return when (viewType) {
            Message.TYPE_MY_MESSAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.my_message_item, parent, false)
                MyChatMessageViewHolder(view)
            }
            Message.TYPE_FRIEND_MESSAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.friend_message_item, parent, false)
                FriendChatMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).sender
    }
}