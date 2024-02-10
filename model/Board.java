package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;

import model.playerModels.PlayerBase;

public class Board implements Cloneable, Serializable
{
    // 1. variable declarations -------------------------------------------------------------------

    public final int Size = 3;
    private int[][] __board;  
    private int __whoseMove;

    // types of fields
    public static final int Empty = 0;
    public static final int Player1 = 1;
    public static final int Player2 = 2;

    private LinkedList<Board> __nextMoves;

    /** returns 1 if player 1 makes next move, 2 otherwise */
    public int getWhoseMove()
    {
        return this.__whoseMove;
    }

    // 2. constructors ----------------------------------------------------------------------------

    public Board()
    {
        this.__board = new int[this.Size][this.Size];
        for (int row = 0; row < this.Size; row++) 
        {
            this.__board[row] = new int[this.Size];
        }
        for (int col = 0; col < this.Size; col++)
        {
            this.__board[0][col] = Board.Player1;
            this.__board[this.Size - 1][col] = Board.Player2;
        }
        this.__whoseMove = Board.Player1;
        this.__nextMoves = this.__findAllNextMoves();
    }

    /** copy constructor for boards with not yet determined next moves */
    private Board(int Size, int whoseMove, int[][] board)
    {
        this.__whoseMove = whoseMove;
        this.__board = board;
        this.__nextMoves = this.__findAllNextMoves();
    }

    // 3. checking move legality ------------------------------------------------------------------

    /**
     * Checks whether (row, col) is existing field on the board
     * @param row row of board
     * @param col column of board
     */
    private boolean __fieldExists(int row, int col)
    {
        return  row >= 0 && row < this.Size &&
                col >= 0 && col < this.Size;
    }

    /** Checks whether in this position current player has any legal move */
    private boolean __anyMovePossible()
    {
        return !this.__nextMoves.isEmpty();
    }

    /** returns amount of possible moves from this state */
    public int possibleMovesCount()
    {
        return this.__nextMoves.size();
    }

    /**
     * Checks whether move (fromRow, fromCol) -> (toRow, toCol) is legal
     * @param fromRow initial row of piece
     * @param fromCol initial column of piece
     * @param toRow target row of piece
     * @param toCol target column of piece
     * @throws InvalidAttributeValueException if __whoseMove is different than 1 or 2
     */
    public boolean isLegalMove(int fromRow, int fromCol, int toRow, int toCol) throws InvalidAttributeValueException
    {
        if (this.__whoseMove == Board.Player1)
            return this.__isLegalMove1(fromRow, fromCol, toRow, toCol);
        else if (this.__whoseMove == Board.Player2)
            return this.__isLegalMove2(fromRow, fromCol, toRow, toCol);
        else
            throw new InvalidAttributeValueException("Unable to determine whose move!");
    }

    /** version for player 1 (white) who is supposed to start row 0 and go up */
    private boolean __isLegalMove1(int fromRow, int fromCol, int toRow, int toCol)
    {
        int toPosPlayer = this.__board[toRow][toCol]; // 0 - empty, 1 - this player, 2 - opponent
        int colChange = toCol - fromCol; // 1 = go right, -1 = go left
        return  toRow - fromRow == 1 && // only 1 step at the time
                Math.abs(colChange) <= 1 && // at max 1 step to the side
                this.__fieldExists(fromRow, fromCol) &&
                this.__fieldExists(toRow, toCol) &&
                this.__board[fromRow][fromCol] == Board.Player1 &&
                ((toPosPlayer == Board.Player2 && colChange != 0) || // capturing pawn
                (toPosPlayer == Board.Empty && colChange == 0)); // normal pawn move
    }

    /** version for player 2 (black) who is supposed to start last row and go down */
    private boolean __isLegalMove2(int fromRow, int fromCol, int toRow, int toCol)
    {
        int toPosPlayer = this.__board[toRow][toCol]; // 0 - empty, 1 - opponent, 2 - this player
        int colChange = toCol - fromCol; // 1 = go left, -1 = go right
        return  toRow - fromRow == -1 && // only 1 step at the time
                Math.abs(colChange) <= 1 && // at max 1 step to the side
                this.__fieldExists(fromRow, fromCol) &&
                this.__fieldExists(toRow, toCol) &&
                this.__board[fromRow][fromCol] == Board.Player2 &&
                ((toPosPlayer == Board.Player1 && colChange != 0) || // capturing pawn
                (toPosPlayer == Board.Empty && colChange == 0)); // normal pawn move
    }

    // 4. board state operations ------------------------------------------------------------------

    /**
     * retrieve which pawn (or none) is at board position (row, col)
     * @param row row of board you want to check
     * @param col column of board you want to check
     * @return 0 if no pawn is there, 1 if white, 2 if black
     */
    public int getBoardAt(int row, int col)
    {
        return this.__board[row][col];
    }

    /**
     * creates and returns board after making move (fromRow, fromCol) -> (toRow, toCol) 
     * @param fromRow initial row of piece
     * @param fromCol initial column of piece
     * @param toRow target row of piece
     * @param toCol target column of piece
     */
    public Board createNewState(int fromRow, int fromCol, int toRow, int toCol)
    {
        int[][] copy = this.__cloneArray();
        copy[toRow][toCol] = copy[fromRow][fromCol];
        copy[fromRow][fromCol] = Board.Empty;
        int whoseMove = 3 - this.__whoseMove; // change player

        return new Board(this.Size, whoseMove, copy);
    }

    /**
     * return index i such that __nextMoves[i] = newBoard
     * @param newBoard state achievable within 1 move
     * @return index of newBoard in list of next moves
     * @throws AttributeNotFoundException when newBoard is not achievable from current state within 1 move
     */
    public int getNextStatesIndex(Board newBoard) throws AttributeNotFoundException
    {
        int index = this.__nextMoves.indexOf(newBoard);
        if (index == -1)
            throw new AttributeNotFoundException("Can't find given move!");
        return index;
    }

    /**
     * return board such that __nextMoves[index] = board
     * @param index index of that board in next moves list
     * @throws IndexOutOfBoundsException
     */
    public Board NextState(int index) throws IndexOutOfBoundsException
    {
        return this.__nextMoves.get(index);
    }

    /**
     * checks whether any player won
     * @param player1 model of player 1 (human or bot)
     * @param player2 model of player 2 (human or bot)
     * @param round number of moves made so far
     * @return 0 if nobody won yet, 1 if player 1 won, 2 if player 2 won
     */
    public int getWinner(PlayerBase player1, PlayerBase player2, int round)
    {
        boolean player1_canMove = player1.getCurrentBoard().__anyMovePossible();
        boolean player2_canMove = player2.getCurrentBoard().__anyMovePossible();

        if (!player1_canMove)
        {
            if (player2_canMove) // only player 2 can move so he wins  
                return Board.Player2;
            return 2 - (round % 2); // both cant move, wins player who made last move
        }
        else if (!player2_canMove) // only player 1 can move so he wins
            return Board.Player1;
        for (int col = 0; col < this.Size; col++)
        {
            if (this.__board[this.Size - 1][col] == Board.Player1)
                return Board.Player1; // player 1 reached final row and won
            else if (this.__board[0][col] == Board.Player2)
                return Board.Player2; // player2 reached final row and won
        }
        return Board.Empty; // nobody won yet
    }

    /**
     * removes given state from list of possible moves
     * @param child state to be removed
     */
    public void removeChild(Board child)
    {
        this.__nextMoves.remove(child);
    }

    // 5. creating decision tree of boards --------------------------------------------------------

    /** returns number of possible distinct games */
    /* Not used
    public int treeSize()
    {
        if (this.__nextMoves.isEmpty())
            return 1;
        int size = 0;
        for (Board move : this.__nextMoves) 
        {
            size += move.treeSize();
        }
        return size;
    } */

    /** finds list of all achieveable Board positions within 1 move */
    private LinkedList<Board> __findAllNextMoves()
    {
        LinkedList<Board> possibleMoves = new LinkedList<>();

        for (int row = 0; row < this.Size; row++) 
        {
            // check if its last row for current player
            if (row == this.Size - 1 && this.__whoseMove == Board.Player1)
                return possibleMoves;
            else if (row == 0 && this.__whoseMove == Board.Player2)
                continue;

            for (int col = 0; col < this.Size; col++) 
            {
                // found pawn of currently moving player
                if (this.__board[row][col] == this.__whoseMove)
                {
                    if (this.__whoseMove == Board.Player1) // starts row 0, goes up
                    {
                        for (int newCol = 0; newCol < this.Size; newCol++)
                        {
                            if (this.__isLegalMove1(row, col, row+1, newCol))
                                possibleMoves.add(this.createNewState(row, col, row+1, newCol));
                        }
                    }
                    else // starts last row, goes down
                    {
                        for (int newCol = 0; newCol < this.Size; newCol++)
                        {
                            if (this.__isLegalMove2(row, col, row-1, newCol))
                                possibleMoves.add(this.createNewState(row, col, row-1, newCol));
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    // 6. method overrides ------------------------------------------------------------------------

    /** compares ONLY current board state NOT entire decision trees */
    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Board)) // not a game board
            return false;
        Board temp = (Board)other;
        if (this.Size != temp.Size) // different sizes
            return false;
        else if (this.__whoseMove != temp.__whoseMove) // different players to make next move
            return false;
        for (int row = 0; row < this.Size; row++) 
        {
            for (int col = 0; col < this.Size; col++) 
            {
                if (this.__board[row][col] != temp.__board[row][col])
                    return false;
            }
        }
        return true;
    }

    /** deep copy of ALREADY CREATED board */
    @Override
    public Board clone() 
    {
        try 
        {
            Board copy = (Board) super.clone();
            copy.__board = new int[Size][Size];
            for (int i = 0; i < Size; i++) 
            {
                copy.__board[i] = Arrays.copyOf(this.__board[i], this.Size);
            }
            copy.__whoseMove = this.__whoseMove;
            copy.__nextMoves = new LinkedList<>();
            for(Board b : this.__nextMoves)
            {
                copy.__nextMoves.add(b.clone());
            }
            return copy;
        } 
        catch (CloneNotSupportedException e) 
        {
            e.printStackTrace();
            return null;
        }
    }

    private int[][] __cloneArray()
    {
        int[][] copy = new int[this.Size][this.Size];  
        for (int row = 0; row < this.Size; row++) 
        {
            copy[row] = new int[this.Size];
            copy[row] = Arrays.copyOf(this.__board[row], this.Size);
        }
        return copy;
    }
}