package co.edu.unal.reto3

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.isEmpty

class MainActivity : ComponentActivity() {

    val mGame: TicTacToeGame = TicTacToeGame()

    lateinit var mBoardButtons : Array<Button>
    lateinit var mInfoTextView : TextView
    lateinit var mBtnNewGame : Button
    var mGameOver : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        mInfoTextView = findViewById<TextView>(R.id.information)

        mBoardButtons = arrayOf<Button>(
            findViewById<Button>(R.id.one),
            findViewById<Button>(R.id.two),
            findViewById<Button>(R.id.three),
            findViewById<Button>(R.id.four),
            findViewById<Button>(R.id.five),
            findViewById<Button>(R.id.six),
            findViewById<Button>(R.id.seven),
            findViewById<Button>(R.id.eight),
            findViewById<Button>(R.id.nine)
        )

        mBtnNewGame = findViewById<Button>(R.id.btnNewGame)
        mBtnNewGame.setOnClickListener {
            startNewGame()
        }

        startNewGame()
    }

    private fun startNewGame() {
        mGame.clearBoard()
        mGameOver = false
        for (i in mBoardButtons.indices) {
            mBoardButtons[i].text = ""
            mBoardButtons[i].isEnabled = true
            mBoardButtons[i].setOnClickListener {
                if (mBoardButtons[i].isEnabled){
                    setMove(TicTacToeGame.HUMAN_PLAYER, i)

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
        mInfoTextView.setText(R.string.first_human)
    }

    private fun setMove(player: Char, location : Int) {
        if (mGameOver)
            return
        mGame.setMove(player, location)
        mBoardButtons[location].isEnabled = false;
        mBoardButtons[location].text = player.toString()
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0,200,0))
        else
            mBoardButtons[location].setTextColor(Color.rgb(200,0,0))
    }

    fun main() {
        startNewGame()
    }
}