package com.example.sspdim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.adapter.MessageAdapter
import com.example.sspdim.data.DataSource
import com.example.sspdim.model.Message.Companion.TYPE_FRIEND_MESSAGE
import com.example.sspdim.model.Message.Companion.TYPE_MY_MESSAGE

class ChatInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val datasource = DataSource()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val messageAdapter = MessageAdapter(this, datasource)

        recyclerView.adapter = messageAdapter
        recyclerView.setHasFixedSize(true)

        val sendButton: Button = findViewById(R.id.send_button)
        sendButton.setOnClickListener {
            val textInput = findViewById<TextView>(R.id.enter_message_text_input)
            val msg = textInput.text.toString()
            if(msg.isNotEmpty()) {
                datasource.addMessage("You: $msg", TYPE_MY_MESSAGE)
                datasource.addMessage("Friend: $msg", TYPE_FRIEND_MESSAGE)
                messageAdapter.notifyDataSetChanged()
                textInput.text = ""
            }
        }
    }
}