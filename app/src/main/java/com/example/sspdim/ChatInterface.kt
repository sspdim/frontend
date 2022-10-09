package com.example.sspdim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.model.MessageAdapter
import com.example.sspdim.model.DataSource
import com.example.sspdim.model.Message.Companion.TYPE_FRIEND_MESSAGE
import com.example.sspdim.model.Message.Companion.TYPE_MY_MESSAGE
import com.example.sspdim.network.SendMessageRequest
import com.example.sspdim.network.SspdimApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChatInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chat)

        val datasource = DataSource()
        val recyclerView = findViewById<RecyclerView>(R.id.chat_message_recycler_view)
        val messageAdapter = MessageAdapter(this, datasource)

        recyclerView.adapter = messageAdapter
        recyclerView.setHasFixedSize(true)

        /*val sendButton: Button = findViewById(R.id.send_button)
        sendButton.setOnClickListener {
            val textInput = findViewById<TextView>(R.id.enter_message_text_input)
            val msg = textInput.text.toString()
            if(msg.isNotEmpty()) {
                datasource.addMessage(msg, TYPE_MY_MESSAGE)
                datasource.addMessage(msg, TYPE_FRIEND_MESSAGE)
                messageAdapter.notifyDataSetChanged()
                textInput.text = ""
                val request = SendMessageRequest("ploij1", "tusr", "Hello")
                runBlocking {
                    launch {
                        val response = SspdimApi.retrofitService.sendMessage(request)
                        Log.d("NewMessage", "[${response.status}]")
                    }
                }
            }
        }*/
    }
}