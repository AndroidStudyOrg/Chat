package org.shop.chat.chatlist

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
import org.shop.chat.chatdetail.ChatActivity
import org.shop.chat.chatdetail.ChatActivity.Companion.EXTRA_CHAT_ROOM_ID
import org.shop.chat.chatdetail.ChatActivity.Companion.EXTRA_OTHER_USER_ID
import org.shop.chat.databinding.FragmentChatListBinding

class ChatListFragment : Fragment() {
    private lateinit var binding: FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatListAdapter = ChatListAdapter { chatRoomItem ->
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(EXTRA_OTHER_USER_ID, chatRoomItem.otherUserId)
            intent.putExtra(EXTRA_CHAT_ROOM_ID, chatRoomItem.chatRoomId)
            startActivity(intent)
        }

        binding.chatListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatListAdapter
        }

        val currentUserId = Firebase.auth.currentUser?.uid ?: return
        val chatRoomsDB = Firebase.database.reference.child(DB_CHAT_ROOMS).child(currentUserId)

        // 내부 정보가 바뀔 때마다 수신
        chatRoomsDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRoomlist = snapshot.children.map {
                    it.getValue(ChatRoomItem::class.java)
                }
                chatListAdapter.submitList(chatRoomlist)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}