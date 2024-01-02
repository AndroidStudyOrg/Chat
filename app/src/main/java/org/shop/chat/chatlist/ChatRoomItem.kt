package org.shop.chat.chatlist

data class ChatRoomItem(
    val chatRoomId: String,
    val otherUserName: String,
    val lastMessage: String
)
