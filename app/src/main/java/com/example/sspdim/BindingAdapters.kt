package com.example.sspdim

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.database.Friend
import com.example.sspdim.model.ChatListAdapter
import com.example.sspdim.model.ServerListAdapter
import com.example.sspdim.network.Server

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Server>?) {
    val adapter = recyclerView.adapter as ServerListAdapter
    adapter.submitList(data)
}

@BindingAdapter("chatListData")
fun bindChatRecyclerView(recyclerView: RecyclerView, data: List<Friend>?) {
    val adapter = recyclerView.adapter as ChatListAdapter
    adapter.submitList(data)
}