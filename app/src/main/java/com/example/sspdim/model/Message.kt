package com.example.sspdim.model

class Message(val content: String, val userId: Int) {
    companion object {
        const val TYPE_MY_MESSAGE = 0
        const val TYPE_FRIEND_MESSAGE = 1
    }
}