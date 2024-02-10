package model.playerModels;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import model.Board;
import model.ClickModel;
import model.Paths;

public class BruteforceLearningBot extends BotBase 
{
    private Random __random;

    protected BruteforceLearningBot(String datapath) 
    {
        super(datapath, BotType.BruteFroceLearning);
        this.__random = new Random();
    }

    @Override
    public Board makeMove(ClickModel __u__)
    {
        try 
        {
            TimeUnit.MILLISECONDS.sleep(500);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
        int movesCount = this._currentMovesTree.possibleMovesCount();
        int index = this.__random.nextInt(movesCount);
        Board newBoard = this._currentMovesTree.NextState(index);
        this._currentMovesTree = newBoard;
        this._movesHistory.add(index);
        return this._currentMovesTree;
    }

    @Override
    public void lost()
    {
        if (this._movesHistory.isEmpty())
            return;
        // we are always player2 and we never make last move if we lost
        this._currentMovesTree = this._fullMovesTree;
        Board grandparent = null;
        Board parent = null;
        for (int index : this._movesHistory) 
        {
            grandparent = parent;
            parent = this._currentMovesTree;
            this._currentMovesTree = this._currentMovesTree.NextState(index);
        }
        if (grandparent.possibleMovesCount() > 1)
        {
            grandparent.removeChild(parent);
        }
        String path = Paths.getTreePath(false);
        Paths.setBotTree(this._fullMovesTree, path);
        this._currentMovesTree = this._fullMovesTree;
    }
}
