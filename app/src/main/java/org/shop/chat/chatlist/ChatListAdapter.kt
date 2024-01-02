package org.shop.chat.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.shop.chat.databinding.ItemChatroomBinding

class ChatListAdapter : ListAdapter<ChatRoomItem, ChatListAdapter.ChatViewHolder>(diffUtil) {
    inner class ChatViewHolder(private val binding: ItemChatroomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatRoomItem) {
            binding.nicknameTextView.text = item.otherUserName
            binding.lastMessageTextView.text = item.lastMessage
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ItemChatroomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatRoomItem>() {
            override fun areItemsTheSame(oldItem: ChatRoomItem, newItem: ChatRoomItem): Boolean {
                return oldItem.chatRoomId == newItem.chatRoomId
            }

            override fun areContentsTheSame(oldItem: ChatRoomItem, newItem: ChatRoomItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}