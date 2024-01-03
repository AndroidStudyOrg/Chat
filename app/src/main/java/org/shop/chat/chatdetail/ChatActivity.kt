package org.shop.chat.chatdetail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import org.shop.chat.Key.Companion.DB_CHATS
import org.shop.chat.Key.Companion.DB_CHAT_ROOMS
import org.shop.chat.Key.Companion.DB_USERS
import org.shop.chat.databinding.ActivityChatBinding
import org.shop.chat.userlist.UserItem

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter

    // chatRoomId, otherUserId
    private var chatRoomId: String = ""
    private var myUserId: String = ""
    private var otherUserId: String = ""
    private var myUserName: String = ""

    private val chatItemList = mutableListOf<ChatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        chatRoomId = intent.getStringExtra(EXTRA_CHAT_ROOM_ID) ?: return
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID) ?: return
        myUserId = Firebase.auth.currentUser?.uid ?: ""

        Firebase.database.reference.child(DB_USERS).child(myUserId).get().addOnSuccessListener {
            val myUserItem = it.getValue(UserItem::class.java)
            myUserName = myUserItem?.username ?: ""
        }

        Firebase.database.reference.child(DB_USERS).child(otherUserId).get().addOnSuccessListener {
            val otherUserItem = it.getValue(UserItem::class.java)
            chatAdapter.otherUserItem = otherUserItem
        }

        Firebase.database.reference.child(DB_CHATS).child(chatRoomId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatItem = snapshot.getValue(ChatItem::class.java)
                    chatItem ?: return

                    chatItemList.add(chatItem)
                    chatAdapter.submitList(chatItemList.toMutableList())
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            })

        chatAdapter = ChatAdapter()
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(this, "빈 메시지는 전송할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newChatItem = ChatItem(
                message = message,
                userId = myUserId
            )

            Firebase.database.reference.child(DB_CHATS).child(chatRoomId).push().apply {
                newChatItem.chatId = key
                setValue(newChatItem)
            }

            val updates: MutableMap<String, Any> = hashMapOf(
                "${DB_CHAT_ROOMS}/$myUserId/$otherUserId/lastMessage" to message,
                "${DB_CHAT_ROOMS}/$otherUserId/$myUserId/lastMessage" to message,
                "${DB_CHAT_ROOMS}/$otherUserId/$myUserId/chatRoomId" to chatRoomId,
                "${DB_CHAT_ROOMS}/$otherUserId/$myUserId/otherUserId" to myUserId,
                "${DB_CHAT_ROOMS}/$otherUserId/$myUserId/otherUserName" to myUserName,
            )
            Firebase.database.reference.updateChildren(updates)

            binding.messageEditText.text.clear()
        }
    }

    companion object {
        const val EXTRA_CHAT_ROOM_ID = "chatRoomId"
        const val EXTRA_OTHER_USER_ID = "otherUserId"
    }
}