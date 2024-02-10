package model;

import javax.management.InvalidAttributeValueException;

public class ClickModel 
{
    // 1. variable declarations -------------------------------------------------------------------

    private int __fromRow = -1;
    private int __fromCol = -1;
    private int __toRow = -1;
    private int __toCol = -1;
    private boolean __validClick1 = false;
    private boolean __validClick2 = false;
    
    // 2. getters ---------------------------------------------------------------------------------

    /** get index of first clicked board row */
    public int getFromRow()
    {
        return this.__fromRow;
    }

    /** get index of first clicked board column */
    public int getFromCol()
    {
        return this.__fromCol;
    }

    /** get index of second clicked board row */
    public int getToRow()
    {
        return this.__toRow;
    }

    /** get index of second clicked board column */
    public int getToCol()
    {
        return this.__toCol;
    }

    /** check if last 2 board clicks form a valid move of current player */
    public boolean isCorrectMove()
    {
        return this.__validClick2 && this.__validClick1;
    }

    // 3. Click operations ------------------------------------------------------------------------

    /**
     * Call every time there is a board click to have easier click handling
     * @param board current state of board
     * @param playerNumber 1 if player 1 makes next move, otherwise 2
     * @param row last clicked board row
     * @param col last clicked board column
     * @throws InvalidAttributeValueException if board.__whoseMove is different than 1 or 2
     */
    public void AddClick(Board board, int row, int col) 
        throws InvalidAttributeValueException
    {
        this.__fromRow = this.__toRow;
        this.__fromCol = this.__toCol;
        this.__toRow = row;
        this.__toCol = col;

        // first click was valid, check second one
        if (this.__validClick1 && !this.__validClick2)
        {
            boolean okMove = board.isLegalMove(
                this.__fromRow, this.__fromCol, this.__toRow, this.__toCol);
            if (okMove) // valid both clicks
            {    
                this.__validClick2 = true;
                return;
            }
            // invalid second click, but it could still be valid as first click
        }
        // this is first click
        boolean okClick = board.getBoardAt(row, col) == board.getWhoseMove();
        if (okClick) // good first click
            this.__validClick1 = true;
        else // bad first click
        {
            this.__validClick1 = false;
            this.__validClick2 = false;
        }
    }

    /** call after making player move to make sure 
     * his second click isn't counted as opponent's first in next move */
    public void reset()
    {
        this.__validClick1 = false;
        this.__validClick2 = false;
        this.__fromRow = -1;
        this.__fromCol = -1;
        this.__toRow = -1;
        this.__toCol = -1;
    }
}
