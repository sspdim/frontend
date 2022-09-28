package com.example.sspdim.model

import com.example.sspdim.model.Message

class DataSource {
    private val messages = mutableListOf<Message>()
    fun getMessages(): MutableList<Message> {
        return messages;
    }
    fun addMessage(message: String, userId: Int) {
        messages.add(Message(message, userId))
    }
}