package co.edu.unal.reto3

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.lang.Error


class MainActivity : AppCompatActivity() {
    val mGame: TicTacToeGame = TicTacToeGame()

    private lateinit var mInfoTextView : TextView
//    private lateinit var mHumanWinsTextView : TextView
//    private lateinit var mComputerWinsTextView : TextView
//    private lateinit var mTiesTextView : TextView
    private lateinit var mBoardView : BoardView
    private lateinit var mHumanMediaPlayer : MediaPlayer
    private lateinit var mComputerMediaPlayer : MediaPlayer
    private lateinit var mPrefs : SharedPreferences

    private lateinit var mUsername : String
    private lateinit var mBoardName : String
    private lateinit var mBoard : List<String>
    private lateinit var mPlayer1 : String
    private lateinit var mPlayer2 : String

    private var mWaitingPlayers : Boolean = true
    private var mGameOver : Boolean = false

    private var mPlayerTurn : String = ""

//    private var mHumanWins : Int = 0
//    private var mComputerWins : Int = 0
//    private var mTies : Int = 0

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_layout)

        mBoardView = findViewById(R.id.board)
        mBoardView.setGame(mGame)
        mBoardView.setOnTouchListener(mTouchListener)

        supportActionBar?.title = "Reto 6"

        mInfoTextView = findViewById<TextView>(R.id.information)
//        mHumanWinsTextView = findViewById(R.id.human_wins)
//        mTiesTextView = findViewById(R.id.ties)
//        mComputerWinsTextView = findViewById(R.id.computer_wins)

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE)

        setSavedScores()
        mGame.setDifficultyLevel(mPrefs.getInt("mDifficultyLevel", 2))

        mUsername = mPrefs.getString("username", "error").toString()
        mBoardName = mPrefs.getString("currentBoard", "error").toString()

        print(mBoardName)

        mInfoTextView.text = "waiting players..."

        val docRef = db.collection("boards").document(mBoardName)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("lol", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                if (snapshot.get("player2") != "") {
                    mBoardView.invalidate()
                    mWaitingPlayers = false
                    mPlayer1 = snapshot.id
                    mPlayer2 = snapshot.get("player2").toString()
                    mPlayerTurn = snapshot.get("turn").toString()
                    mBoard = snapshot.get("board").toString().split(",")
                    for (i in mBoard.indices){
                        mGame.mBoard[i] = mBoard[i].toCharArray()[0]
                    }
                    if (snapshot.get("status") == "finished") {
                        mGameOver = true
                        mInfoTextView.text = "It's a tie!"
                        for (tile in mGame.mBoard){
                            if (tile == ' '){
                                mInfoTextView.text = "The winner is " + mPlayerTurn
                            }
                        }
                    } else {
                        mInfoTextView.text = "It's turn of " + mPlayerTurn
                    }
                }
            } else {
                Log.d("lol", "Current data: null")
            }
        }

    }

    override fun onResume() {
        super.onResume()

        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.human_move)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.computer_move)
    }

    override fun onPause() {
        super.onPause()

        mHumanMediaPlayer.release()
        mComputerMediaPlayer.release()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        outState.putCharArray("board", mGame.boardState)
//        outState.putBoolean("mGameOver", mGameOver)
//        outState.putCharSequence("info", mInfoTextView.text)
////        outState.putInt("mHumanWins", mHumanWins)
////        outState.putInt("mComputerWins", mComputerWins)
////        outState.putInt("mTies", mTies)
//        outState.putChar("mPlayerTurn", mPlayerTurn)
//    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//
//        mGame.boardState = savedInstanceState.getCharArray("board");
//        mGameOver = savedInstanceState.getBoolean("mGameOver");
//        mInfoTextView.text = savedInstanceState.getCharSequence("info");
////        mHumanWins = savedInstanceState.getInt("mHumanWins");
////        mComputerWins = savedInstanceState.getInt("mComputerWins");
////        mTies = savedInstanceState.getInt("mTies");
//        mPlayerTurn = savedInstanceState.getChar("mPlayerTurn");
//
//        displayScores()
//
//        if (mPlayerTurn == TicTacToeGame.COMPUTER_PLAYER && !mGameOver){
//            Handler(Looper.getMainLooper()).postDelayed({
//                val move = mGame.computerMove
//                setMove(TicTacToeGame.COMPUTER_PLAYER, move)
//                mComputerMediaPlayer.start()
//                mPlayerTurn = TicTacToeGame.HUMAN_PLAYER
//                val winner = mGame.checkForWinner()
//
//                if (winner != 0)
//                    mGameOver = true
//
//                when (winner) {
//                    0 -> mInfoTextView.setText(R.string.turn_human)
//                    1 -> {
//                        mInfoTextView.setText(R.string.result_tie)
////                        ++mTies
//                    }
//                    2 -> {
////                        ++mHumanWins
//                    }
//                    3 -> {
//                        mInfoTextView.setText(R.string.result_computer_wins)
////                        ++mComputerWins
//                    }
//                    else -> mInfoTextView.text = "Error D:"
//                }
//                displayScores()
//            }, 500)
//        }
//    }

//    override fun onStop() {
//        super.onStop()
//
//        val ed : SharedPreferences.Editor = mPrefs.edit()
////        ed.putInt("mHumanWins", mHumanWins)
////        ed.putInt("mTies", mTies)
////        ed.putInt("mComputerWins", mComputerWins)
//        ed.putInt("mDifficultyLevel", mGame.difficultyLevelInt)
//        ed.commit()
//
//    }

    private fun startNewGame() {
        mGame.clearBoard()
        mGameOver = false
        mBoardView.invalidate()
        mInfoTextView.setText(R.string.first_human)
    }

    private fun setMove(player: Char, location : Int) : Boolean {
        if (mGame.setMove(player, location)){
            mBoardView.invalidate()
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        var inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        return true
    }

    class DifficultyDialogFragment : DialogFragment() {

        private var mainActivity : MainActivity? = null

        fun setMainActivity(activity: MainActivity) {
            mainActivity = activity
        }
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                var currentLevel = mainActivity?.mGame?.difficultyLevelInt
                if (currentLevel == null)
                    currentLevel = 2
                val builder = AlertDialog.Builder(it)
                // Set the dialog title
                builder.setTitle(R.string.difficulty_choose)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setSingleChoiceItems(R.array.difficulties, currentLevel,
                        DialogInterface.OnClickListener() { dialog, which ->
                            mainActivity?.mGame?.setDifficultyLevel(which)
                        })

                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }

    private fun setDifficultyLevel(level : Int) {
        mGame.setDifficultyLevel(level)
        val ed = mPrefs.edit()
        ed.putInt("mDifficultyLevel", level)
        ed.commit()
    }

//    class QuitDialogFragment : DialogFragment() {
//        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//            return activity?.let {
//                var builder = AlertDialog.Builder(it)
//                builder.setTitle(R.string.quit_question)
//                    .setPositiveButton(R.string.yes, DialogInterface.OnClickListener() { dialogInterface, i ->
//                        activity?.finish()
//                    })
//                    .setNegativeButton(R.string.no, null)
//                builder.create()
//            } ?: throw IllegalStateException("Activity cannot be null")
//        }
//    }

    class ResetScoresDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                var builder = AlertDialog.Builder(it)
                builder.setTitle(R.string.reset_scores_questions)
                    .setPositiveButton(R.string.yes, DialogInterface.OnClickListener() { dialogInterface, i ->
                        val prefs = activity?.getSharedPreferences("ttt_prefs", MODE_PRIVATE)
                        if (prefs != null) {
                            val ed : SharedPreferences.Editor = prefs.edit()
                            ed.putInt("mHumanWins", 0)
                            ed.putInt("mTies", 0)
                            ed.putInt("mComputerWins", 0)
                            ed.commit()
                            val intent = activity?.intent
                            activity?.finish()
                            if (intent != null)
                                startActivity(intent)
                            println("LMAO?")
                        } else {
                            println("WTF???")
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.new_game -> {
                startNewGame()
                return true
            }
            R.id.ai_difficulty -> {
                val difficultyDialog = DifficultyDialogFragment()
                difficultyDialog.setMainActivity(this)
                difficultyDialog.show(supportFragmentManager, "dialog")
                return true
            }
            R.id.reset_scores -> {
                val resetDialogFragment = ResetScoresDialogFragment()
                resetDialogFragment.show(supportFragmentManager, "dialog")
                return true
            }
        }
        return false
    }

    private val mTouchListener = OnTouchListener {v, event ->
        v.performClick()

        val col = (event.x / mBoardView.getBoardCellWidth()).toInt();
        val row = (event.y / mBoardView.getBoardCellHeight()).toInt();
        val pos = row * 3 + col;

        println(pos)
        if (!mWaitingPlayers && !mGameOver && mPlayerTurn == mUsername) {
            if (mPlayerTurn == mPlayer1){
                mHumanMediaPlayer.start()
                mGame.setMove(TicTacToeGame.HUMAN_PLAYER, pos)
                mPlayerTurn = mPlayer2
            } else {
                mComputerMediaPlayer.start()
                mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, pos)
                mPlayerTurn = mPlayer1
            }

            var dbBoard = ""
            for (i in mGame.mBoard.indices){
                dbBoard += mGame.mBoard[i]
                if (i < 8){
                    dbBoard += ","
                }
            }
            print(dbBoard)

            db.collection("boards").document(mBoardName).update("board", dbBoard)

            var winner = mGame.checkForWinner()
            if (winner == 0){
                db.collection("boards").document(mBoardName).update("turn", mPlayerTurn)
            }

            if (winner != 0){
                mGameOver = true
                db.collection("boards").document(mBoardName).update("status", "finished")
            }


            when (winner) {
                0 -> mInfoTextView.setText("Its turn of " + mPlayerTurn)
                1 -> {
                    mInfoTextView.setText(R.string.result_tie)
//                        ++mTies
                }
                2 -> {
                    mInfoTextView.setText(mPlayer1 +" won!")
//                        ++mHumanWins
                }
                3 -> {
                    mInfoTextView.setText(mPlayer2 +" won!")
//                        ++mComputerWins
                }
                else -> mInfoTextView.text = "Error D:"
            }

            displayScores()
        }

        false
    }

    private fun setSavedScores() {
//        mHumanWins = mPrefs.getInt("mHumanWins", 0);
//        mComputerWins = mPrefs.getInt("mComputerWins", 0);
//        mTies = mPrefs.getInt("mTies", 0);
        displayScores()
    }

    private fun displayScores() {
//        mHumanWinsTextView.text = getString(R.string.human_wins, mHumanWins)
//        mTiesTextView.text = getString(R.string.ties, mTies)
//        mComputerWinsTextView.text = getString(R.string.computer_wins, mComputerWins)
    }

    fun main() {
//        startNewGame()
    }
}