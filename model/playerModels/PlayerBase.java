package model.playerModels;

import java.util.LinkedList;

import javax.management.AttributeNotFoundException;

import model.Board;
import model.ClickModel;
import model.Paths;

public abstract class PlayerBase 
{
    // 1. variable declarations -------------------------------------------------------------------
    
    protected Board _fullMovesTree;
    protected Board _currentMovesTree;
    protected LinkedList<Integer> _movesHistory;

    public int getRound()
    {
        return this._movesHistory.size();
    }

    public Board getCurrentBoard()
    {
        return this._currentMovesTree;
    }

    // 2. constructor and abstract methods --------------------------------------------------------

    public PlayerBase(String treeDatapath)
    {
        Board tree = Paths.getBotTree(treeDatapath);
        this._fullMovesTree = tree;
        this._currentMovesTree = tree;
        this._movesHistory = new LinkedList<>();
    }

    /**
     * Update your decision tree after opponent made a move
     * @param newBoard board state that opponent created after making move
     * @throws AttributeNotFoundException when newBoard is not achievable from current state within 1 move
     */
    public void receiveMove(Board newBoard) throws AttributeNotFoundException
    {
        int index = this._currentMovesTree.getNextStatesIndex(newBoard);
        Board current = this._currentMovesTree.NextState(index);
        this._currentMovesTree = current;
        this._movesHistory.add(index);
    }

    /** From current board state, pick a move and return new board state 
     * @throws AttributeNotFoundException */
    public abstract Board makeMove(ClickModel clickModel) throws AttributeNotFoundException;
}
