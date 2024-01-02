package org.shop.chat.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import org.shop.chat.Key.Companion.DB_USERS
import org.shop.chat.LoginActivity
import org.shop.chat.databinding.FragmentMyPageBinding
import org.shop.chat.userlist.UserItem

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = Firebase.auth.currentUser?.uid ?: ""
        val currentUserDB = Firebase.database.reference.child(DB_USERS).child(currentUser)

        currentUserDB.get().addOnSuccessListener {
            val currentUserItem = it.getValue(UserItem::class.java) ?: return@addOnSuccessListener

            binding.usernameEditText.setText(currentUserItem.username)
            binding.descriptionEditText.setText(currentUserItem.description)
        }

        binding.applyButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(context, "유저이름은 빈 값이 될 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = mutableMapOf<String, Any>()
            user["username"] = username
            user["description"] = description
            currentUserDB.updateChildren(user)
            // TODO 파이어 베이스 realtime database update
        }

        binding.signOutButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
    }
}