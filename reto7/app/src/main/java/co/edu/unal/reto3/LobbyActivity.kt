package co.edu.unal.reto3

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import co.edu.unal.reto3.databinding.ActivityLobbyBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LobbyActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLobbyBinding
    private lateinit var mPrefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE)

        db.collection("boards").get().addOnSuccessListener { collection ->
            for (doc in collection){
                val boardButton = Button(this)
                boardButton.text = doc.id
                boardButton.setOnClickListener {
                    db.collection("boards").document(doc.id)
                        .update("player2", mPrefs.getString("username", "error"))
                        .addOnSuccessListener {
                            print("entering game")
                            val ed = mPrefs.edit()
                            ed.putString("currentBoard", doc.id)
                            ed.commit()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                }
                binding.boardsList.addView(boardButton)
            }
        }

        binding.fab.setOnClickListener { view ->
            val board = hashMapOf(
                "player1" to mPrefs.getString("username", "error"),
                "player2" to "",
                "turn" to mPrefs.getString("username", "error"),
                "board" to " , , , , , , , , ",
                "status" to "searching"
            )

            db.collection("boards").document(mPrefs.getString("username","error").toString()).set(board).addOnSuccessListener { doc ->
                val ed = mPrefs.edit()
                ed.putString("currentBoard", mPrefs.getString("username", "error"))
                ed.commit()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}