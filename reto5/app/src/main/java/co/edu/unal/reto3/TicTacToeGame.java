package co.edu.unal.reto3;

/* TicTacToeConsole.java
 * By Frank McCown (Harding University)
 *
 * This is a tic-tac-toe game that runs in the console window.  The human
 * is X and the computer is O.
 */

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class TicTacToeGame {
    public final int BOARD_SIZE = 9;
    private char mBoard[] = {' ',' ',' ',' ',' ',' ',' ',' ',' '};
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';
    // The computer's difficulty levels
    public enum DifficultyLevel {Easy, Harder, Expert};
    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    private Random mRand;


    public TicTacToeGame() {

        // Seed the random number generator
        mRand = new Random();
    }

    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public int getComputerMove()
    {
        System.out.println("Generating move in difficulty " + mDifficultyLevel);
        int move;

        // Check if difficulty is not easy (so is expert or hard)
        // First see if there's a move O can make to win
        if (mDifficultyLevel != DifficultyLevel.Easy) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                    mBoard[i] = COMPUTER_PLAYER;
                    if (checkForWinner() == 3) {
                        return i;
                    }
                    mBoard[i] = OPEN_SPOT;
                }
            }
        }

        // Check if difficulty is expert
        // See if there's a move O can make to block X from winning
        if (mDifficultyLevel == DifficultyLevel.Expert) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                    mBoard[i] = HUMAN_PLAYER;
                    if (checkForWinner() == 2) {
                        return i;
                    }
                    mBoard[i] = OPEN_SPOT;
                }
            }
        }

        // Any difficulty can end up in a random move
        // Generate random move
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);

        return move;
    }

    public void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; ++i){
            mBoard[i] = OPEN_SPOT;
        }
    }

    public void setMove(char player, int location){
        if (player != HUMAN_PLAYER && player != COMPUTER_PLAYER){
            System.out.println("Error, player not valid.");
            return;
        }

        if (location < 0 || location > 8 || mBoard[location] != OPEN_SPOT) {
            System.out.println("Error, location no valid");
        }

        mBoard[location] = player;
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }
    public void setDifficultyLevel(int difficultyLevel) {
        if (difficultyLevel < 0 || difficultyLevel > 2){
            return;
        }
        System.out.println("New dificulty lvl is: " + DifficultyLevel.values()[difficultyLevel]);
        mDifficultyLevel = DifficultyLevel.values()[difficultyLevel];
    }

    public int getDifficultyLevelInt () {
        return mDifficultyLevel.ordinal();
    }
}