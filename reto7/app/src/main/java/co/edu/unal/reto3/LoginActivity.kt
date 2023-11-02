package co.edu.unal.reto3

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Calendar

class LoginActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    lateinit var mUsernameInput : EditText
    lateinit var mLoginBtn : Button
    lateinit var mErrorText : TextView

    private lateinit var mPrefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE)

        val savedUsername = mPrefs.getString("username", "")
        if (savedUsername != ""){
            db.collection("users").get().addOnSuccessListener { collection ->
                for (doc in collection) {
                    if (doc.id == savedUsername){
                        val intent = Intent(this, LobbyActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        mUsernameInput = findViewById(R.id.username_input)

        mLoginBtn = findViewById(R.id.login_btn)
        mLoginBtn.setOnTouchListener(mTouchListener)

        mErrorText = findViewById(R.id.error_text)
    }

    private val mTouchListener = OnTouchListener { v, event ->
        v.performClick()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                var error = false
                if (mUsernameInput.text.isEmpty()){
                    mErrorText.text = "You must insert a valid username"
                } else {
                    db.collection("users").get().addOnSuccessListener { result ->
                        for (document in result) {
                            if (document.id == mUsernameInput.text.toString()){
                                mErrorText.text = "The username is already taken"
                                error = true
                                break
                            }
                        }

                        if (!error){
                            val username = mUsernameInput.text.toString()
                            val user = hashMapOf(
                                "name" to username
                            )

                            db.collection("users").document(username).set(user).addOnSuccessListener { doc ->
                                Log.d("lol", "Document added with ID: ${username}")
                                val ed = mPrefs.edit()
                                ed.putString("username", username)
                                ed.commit()

                                val intent = Intent(this, LobbyActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }

        false
    }
}