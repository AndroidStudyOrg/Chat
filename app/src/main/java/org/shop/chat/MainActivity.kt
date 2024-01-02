package org.shop.chat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.shop.chat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            // 로그인이 안되어 있음
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}