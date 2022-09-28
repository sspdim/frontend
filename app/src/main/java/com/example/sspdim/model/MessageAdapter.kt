package com.example.sspdim.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.R
import com.example.sspdim.model.Message.Companion.TYPE_FRIEND_MESSAGE
import com.example.sspdim.model.Message.Companion.TYPE_MY_MESSAGE

class MessageAdapter(
    private val context: Context,
    private val datasource: DataSource
    ) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    abstract class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Message)
    }

    class MyMessageViewHolder(val view: View) : MessageViewHolder(view) {
        private val messageContent = view.findViewById<TextView>(R.id.message)

        override fun bind(item: Message) {
            messageContent.text = item.content
        }
    }
    class FriendMessageViewHolder(val view: View) : MessageViewHolder(view) {
        private val messageContent = view.findViewById<TextView>(R.id.message)

        override fun bind(item: Message) {
            messageContent.text = item.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val context = parent.context
        return when (viewType) {
            TYPE_MY_MESSAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.my_message_item, parent, false)
                MyMessageViewHolder(view)
            }
            TYPE_FRIEND_MESSAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.friend_message_item, parent, false)
                FriendMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = datasource.getMessages()[position]
        holder.bind(item)
    }
    override fun getItemCount(): Int {
        return datasource.getMessages().size
    }

    override fun getItemViewType(position: Int): Int {
        return datasource.getMessages()[position].userId
    }
}