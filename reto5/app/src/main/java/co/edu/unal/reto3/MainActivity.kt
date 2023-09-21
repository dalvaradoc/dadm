package co.edu.unal.reto3

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment


class MainActivity : AppCompatActivity() {
    val mGame: TicTacToeGame = TicTacToeGame()

    private lateinit var mInfoTextView : TextView
    private lateinit var mBoardView : BoardView

    private var mGameOver : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        mBoardView = findViewById(R.id.board)
        mBoardView.setGame(mGame)
        mBoardView.setOnTouchListener(mTouchListener)

        supportActionBar?.title = "Reto4"

        mInfoTextView = findViewById<TextView>(R.id.information)

        startNewGame()
    }

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

    class QuitDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                var builder = AlertDialog.Builder(it)
                builder.setTitle(R.string.quit_question)
                    .setPositiveButton(R.string.yes, DialogInterface.OnClickListener() { dialogInterface, i ->
                        activity?.finish()
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
            R.id.quit -> {
                val quitDialogFragment = QuitDialogFragment()
                quitDialogFragment.show(supportFragmentManager, "dialog")
                return true
            }
        }
        return false
    }

    private val mTouchListener = OnTouchListener {v, event ->
        v.performClick()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val col = (event.x / mBoardView.getBoardCellWidth()).toInt();
                val row = (event.y / mBoardView.getBoardCellHeight()).toInt();
                val pos = row * 3 + col;

                println(pos)

                if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                    var winner = mGame.checkForWinner()
                    if (winner == 0){
                        mInfoTextView.setText(R.string.turn_computer)
                        val move = mGame.computerMove
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move)
                        winner = mGame.checkForWinner()
                    }

                    if (winner != 0)
                        mGameOver = true

                    when (winner) {
                        0 -> mInfoTextView.setText(R.string.turn_human)
                        1 -> mInfoTextView.setText(R.string.result_tie)
                        2 -> mInfoTextView.setText(R.string.result_human_wins)
                        3 -> mInfoTextView.setText(R.string.result_computer_wins)
                        else -> mInfoTextView.text = "Error D:"
                    }
                }
            }
        }

        false
    }

    fun main() {
        startNewGame()
    }
}