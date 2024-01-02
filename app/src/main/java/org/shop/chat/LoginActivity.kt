package org.shop.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import org.shop.chat.Key.Companion.DB_URL
import org.shop.chat.Key.Companion.DB_USERS
import org.shop.chat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // TODO 정규식을 이용해서 예외처리. 일단은 empty가 아니면 허용하는 걸로
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 회원가입 성공
                        Toast.makeText(this, "회원가입에 성공했습니다. 로그인해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        // 회원가입 실패
                        Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    val currentUser = Firebase.auth.currentUser
                    if (task.isSuccessful && currentUser != null) {
                        // 로그인 성공

                        val userId = currentUser.uid
                        val user = mutableMapOf<String, Any>()
                        user["userId"] = userId
                        user["username"] = email
                        // DB가 미국이 아닌 싱가포르 등 다른 나라에 있을 경우 아래의 코드를 추가해주어야 함.
//                        Firebase.database("https://simplechat-e8a47-default-rtdb.firebaseio.com/")
                        Firebase.database(DB_URL).reference.child(DB_USERS).child(userId)
                            .updateChildren(user)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // 로그인 실패
                        Log.e("LoginActivity", task.exception.toString())
                        Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}