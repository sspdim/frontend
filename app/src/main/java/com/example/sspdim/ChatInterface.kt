package com.example.sspdim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sspdim.adapter.ItemAdapter
import com.example.sspdim.data.DataSource

class ChatInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val datasource = DataSource()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val itemAdapter = ItemAdapter(this, datasource)
        recyclerView.adapter = itemAdapter
        recyclerView.setHasFixedSize(true)
        val sendButton: Button = findViewById(R.id.send_button)
        sendButton.setOnClickListener {
            val textInput = findViewById<TextView>(R.id.enter_message_text_input)
            val msg = textInput.text.toString()
            if(msg.isNotEmpty()) {
                datasource.addMessage(msg, 0)
                itemAdapter.notifyDataSetChanged()
                textInput.text = ""

            }
        }
    }
}