package co.edu.unal.reto3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BoardView extends View {

    public static final int GRID_WIDTH = 6;
    public TicTacToeGame mGame;

    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;
    private Paint mPaint;

    public void initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cross);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public BoardView(Context context) {
        super(context);
        initialize();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int boardWidth = getWidth();
        int boardHeight = getHeight();

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(GRID_WIDTH);

        int cellWidth = boardWidth / 3;
        canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
        canvas.drawLine(cellWidth*2, 0, cellWidth*2, boardHeight, mPaint);

        canvas.drawLine(0, cellWidth, boardWidth, cellWidth, mPaint);
        canvas.drawLine(0, cellWidth*2, boardWidth, cellWidth*2, mPaint);

        // Draw all the X and O images
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;
            // Define the boundaries of a destination rectangle for the image
            int left = col * cellWidth;
            int top = row * cellWidth;
            int right = left + cellWidth;
            int bottom = top + cellWidth;

            if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,
                        null, // src
                        new Rect(left, top, right, bottom), // dest
                        null);

            }
            else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,
                        null, // src
                        new Rect(left, top, right, bottom), // dest
                        null);
            }
        }
    }

    public void setGame(TicTacToeGame game) {
        this.mGame = game;
    }

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }

    public int getBoardCellHeight() {
        return getHeight() / 3;
    }
}
