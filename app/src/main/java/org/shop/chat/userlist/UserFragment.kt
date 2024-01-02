package org.shop.chat.userlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import org.shop.chat.Key.Companion.DB_CHAT_ROOMS
import org.shop.chat.Key.Companion.DB_USERS
import org.shop.chat.chatdetail.ChatActivity
import org.shop.chat.chatdetail.ChatActivity.Companion.EXTRA_CHAT_ROOM_ID
import org.shop.chat.chatdetail.ChatActivity.Companion.EXTRA_OTHER_USER_ID
import org.shop.chat.chatlist.ChatRoomItem
import org.shop.chat.databinding.FragmentUserBinding
import java.util.UUID

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAdapter = UserAdapter { otherUser ->
            val myUserId = Firebase.auth.currentUser?.uid ?: ""
            val chatRoomDB = Firebase.database.reference.child(DB_CHAT_ROOMS).child(myUserId)
                .child(otherUser.userId ?: "")

            chatRoomDB.get().addOnSuccessListener {
                var chatRoomId = ""
                if (it.value != null) {
                    // 데이터가 보존
                    val chatRoom = it.getValue(ChatRoomItem::class.java)
                    chatRoomId = chatRoom?.chatRoomId ?: ""
                } else {
                    chatRoomId = UUID.randomUUID().toString()
                    val newChatRoom = ChatRoomItem(
                        chatRoomId = chatRoomId,
                        otherUserName = otherUser.username,
                        otherUserId = otherUser.userId
                    )
                    chatRoomDB.setValue(newChatRoom)
                }

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(EXTRA_OTHER_USER_ID, otherUser.userId)
                intent.putExtra(EXTRA_CHAT_ROOM_ID, chatRoomId)
                startActivity(intent)
            }
        }

        binding.userListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        val currentUserId = Firebase.auth.currentUser?.uid ?: ""

        Firebase.database.reference.child(DB_USERS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userItemList = mutableListOf<UserItem>()

                    snapshot.children.forEach {
                        val user = it.getValue(UserItem::class.java)
                        user ?: return

                        if (user.userId != currentUserId) {
                            userItemList.add(user)
                        }
                    }

                    userAdapter.submitList(userItemList)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}